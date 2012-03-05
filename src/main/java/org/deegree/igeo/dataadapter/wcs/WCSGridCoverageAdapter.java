//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2008 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/

package org.deegree.igeo.dataadapter.wcs;

import static org.deegree.framework.util.MapUtils.DEFAULT_PIXEL_SIZE;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.HttpUtils;
import org.deegree.framework.util.ImageUtils;
import org.deegree.framework.util.MapUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.dataadapter.DataAccessException;
import org.deegree.igeo.dataadapter.GridCoverageAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.WCSDatasource;
import org.deegree.igeo.settings.WCSGridCoverageAdapterSettings;
import org.deegree.model.Identifier;
import org.deegree.model.coverage.grid.GridCoverage;
import org.deegree.model.coverage.grid.ImageGridCoverage;
import org.deegree.model.crs.CRSTransformationException;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcwebservices.OGCWebServiceException;
import org.deegree.ogcwebservices.wcs.getcoverage.GetCoverage;
import org.xml.sax.SAXException;

/**
 * Concrete implementation of {@link GridCoverageAdapter} for reading raster data from a WCS
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class WCSGridCoverageAdapter extends GridCoverageAdapter {

    private static final ILogger LOG = LoggerFactory.getLogger( WCSGridCoverageAdapter.class );

    private int timeout = 25000;

    private URL getCoverageHttpGetURL;

    private WCSGridCoverageAdapterSettings wcsSet;

    private String version;

    private static Map<String, XMLFragment> capabilitiesCache;
    static {
        if ( capabilitiesCache == null ) {
            capabilitiesCache = new HashMap<String, XMLFragment>();
        }
    }

    /**
     * @param cmmDatasource
     * @param cmmLayer
     * @param cmmMapModel
     */
    public WCSGridCoverageAdapter( Datasource cmmDatasource, Layer cmmLayer, MapModel cmmMapModel, URL baseURL ) {
        super( cmmDatasource, cmmLayer, cmmMapModel );
        Envelope bbox = mapModel.getEnvelope();
        datasource.setExtent( bbox );
        wcsSet = mapModel.getApplicationContainer().getSettings().getWCSGridCoveragesAdapter();

        // wcsSet = mapModel.getApplicationContainer().getSettings().getWC
        // read request timeout from deegree configuration
        timeout = wcsSet.getTimeout();

        // read target URLs (and WCS version) from WCS capabilities
        readURLs( baseURL );
    }

    /**
     * @param baseURL
     */
    private void readURLs( URL baseURL ) {

        XMLFragment xml;
        String capabilitiesUrl = null;
        try {
            ApplicationContainer<?> appCont = mapModel.getApplicationContainer();
            capabilitiesUrl = baseURL.toURI().toASCIIString();
            String tmp = HttpUtils.normalizeURL( capabilitiesUrl );
            capabilitiesUrl = HttpUtils.addAuthenticationForKVP( capabilitiesUrl, appCont.getUser(),
                                                                 appCont.getPassword(), appCont.getCertificate( tmp ) );

            if ( capabilitiesCache.containsKey( capabilitiesUrl ) ) {
                xml = capabilitiesCache.get( capabilitiesUrl );
                LOG.logInfo( "read capabilities from cache: ", capabilitiesUrl );
            } else {
                InputStream is = HttpUtils.performHttpGet( capabilitiesUrl, null, timeout, appCont.getUser(),
                                                           appCont.getPassword(), null ).getResponseBodyAsStream();
                xml = new XMLFragment();
                xml.load( is, capabilitiesUrl );
                capabilitiesCache.put( capabilitiesUrl, xml );
            }
        } catch ( SAXException e ) {
            LOG.logError( e.getMessage(), e );
            String s = StringTools.stackTraceToString( e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10106", capabilitiesUrl ) + s );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            String s = StringTools.stackTraceToString( e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$$DG10107", capabilitiesUrl ) + s );
        }
        try {
            version = XMLTools.getRequiredAttrValue( "version", null, xml.getRootElement() );
        } catch ( XMLParsingException e ) {
            LOG.logError( e.getMessage(), e );
            LOG.logError( xml.getAsPrettyString() );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10108", capabilitiesUrl,
                                                                xml.getAsPrettyString() ) );
        }

        String className = wcsSet.getCapabilitiesEvaluator( version );
        Class<?> clzz = null;
        try {
            clzz = Class.forName( className );
        } catch ( ClassNotFoundException e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10109", className ) );
        }

        WCSCapabilitiesEvaluator evaluator = null;
        try {
            evaluator = (WCSCapabilitiesEvaluator) clzz.newInstance();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10110", className ) );
        }
        evaluator.setCapabilities( xml );
        try {
            getCoverageHttpGetURL = evaluator.getGetCoverageHTTPGetURL();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( e.getMessage(), e );
        }

    }

    @Override
    public GridCoverage getCoverage() {
        readCoverageImage();
        return this.coverage;
    }

    /**
     * 
     */
    private void readCoverageImage() {
        fireStartLoadingEvent();
        // update GetCoverage request with current state of the map model
        Envelope bbox = mapModel.getEnvelope();

        int width = mapModel.getTargetDevice().getPixelWidth();
        int height = mapModel.getTargetDevice().getPixelHeight();
        String srs = mapModel.getCoordinateSystem().getPrefixedName();
        GeoTransformer transformBack = null;
        if ( !datasource.getNativeCoordinateSystem().equals( mapModel.getCoordinateSystem() ) ) {
            // perform CRS transformation of the request bounding box and resizing of the requested image
            // to enable transformation and cutting the result of a GetCoverage request back to the CRS of
            // the map model without lost of data at the image boundaries
            srs = datasource.getNativeCoordinateSystem().getPrefixedName();
            GeoTransformer transformer = new GeoTransformer( datasource.getNativeCoordinateSystem() );
            transformBack = new GeoTransformer( mapModel.getCoordinateSystem() );
            try {
                bbox = transformer.transform( bbox, mapModel.getCoordinateSystem(), true );
            } catch ( CRSTransformationException e ) {
                LOG.logError( e.getMessage(), e );
                throw new DataAccessException( e.getMessage() );
            }

            double scale = MapUtils.calcScale( width, height, mapModel.getEnvelope(), mapModel.getCoordinateSystem(),
                                               DEFAULT_PIXEL_SIZE );
            double newScale = MapUtils.calcScale( width, height, bbox, datasource.getNativeCoordinateSystem(),
                                                  DEFAULT_PIXEL_SIZE );
            double ratio = scale / newScale;
            width = (int) Math.round( width * ratio );
            height = (int) Math.round( height * ratio );
        }

        Map<String, String> map = new HashMap<String, String>();
        // ensure that GetCoverage request will be created for version read from WCS capabilities
        map.put( "ID", new Identifier().getAsQualifiedString() );
        map.put( "VERSION", version );
        map.put( "COVERAGE", ( (WCSDatasource) datasource ).getCoverage().getLocalName() );
        map.put( "WIDTH", Integer.toString( width ) );
        map.put( "HEIGHT", Integer.toString( height ) );
        map.put( "FORMAT", ( (WCSDatasource) datasource ).getFormat() );
        map.put( "CRS", srs );
        map.put( "BBOX", bbox.getMin().getX() + "," + bbox.getMin().getY() + ',' + bbox.getMax().getX() + ','
                         + bbox.getMax().getY() );
        if ( ( (WCSDatasource) datasource ).getTime() != null ) {
            map.put( "TIME", ( (WCSDatasource) datasource ).getTime() );
        }

        GetCoverage baseRequest;
        try {
            baseRequest = GetCoverage.create( map );
        } catch ( Exception e ) {
            LOG.logError( e );
            throw new DataAccessException( e );
        }

        InputStream is = null;
        URL baseURL = null;
        try {
            ApplicationContainer<?> appCont = mapModel.getApplicationContainer();
            String getCoveragUrl = getCoverageHttpGetURL.toURI().toASCIIString();
            String param = HttpUtils.addAuthenticationForKVP( baseRequest.getRequestParameter(), appCont.getUser(),
                                                              appCont.getPassword(),
                                                              appCont.getCertificate( getCoveragUrl ) );

            LOG.logDebug( "base WCS URL ", getCoveragUrl );
            LOG.logDebug( "WCS GetCoverage parameter ", param );
            is = HttpUtils.performHttpGet( getCoveragUrl, param, timeout, appCont.getUser(), appCont.getPassword(),
                                           null ).getResponseBodyAsStream();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            fireLoadingExceptionEvent();
            try {
                throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10111", baseURL,
                                                                    baseRequest.getRequestParameter() ) );
            } catch ( OGCWebServiceException e1 ) {
                e1.printStackTrace();
            }
        }

        // read data from WCS into a byte array to enable debugging and detailed error messages
        // if result can not be parsed as image
        ByteArrayOutputStream bos = new ByteArrayOutputStream( 100000 );
        byte[] b = new byte[100000];
        try {
            int c = is.read( b );
            do {
                bos.write( b, 0, c );
                c = is.read( b );
            } while ( c > 0 );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            try {
                throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10112", baseURL,
                                                                    baseRequest.getRequestParameter(), e.getMessage() ) );
            } catch ( OGCWebServiceException e1 ) {
                e1.printStackTrace();
            }
        }

        BufferedImage image = null;
        try {
            image = ImageUtils.loadImage( new ByteArrayInputStream( bos.toByteArray() ) );
            bos.close();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            try {
                throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10112", baseURL,
                                                                    baseRequest.getRequestParameter(),
                                                                    new String( bos.toByteArray() ) ) );
            } catch ( OGCWebServiceException e1 ) {
                e1.printStackTrace();
            }
        }
        if ( !datasource.getNativeCoordinateSystem().equals( mapModel.getCoordinateSystem() ) ) {
            // perform transformation and cutting of the result of a GetCoverage request if map model CRS and
            // CRS supported by requested layer(s) are not the same
            try {
                image = transformBack.transform( image, bbox, mapModel.getEnvelope(),
                                                 mapModel.getTargetDevice().getPixelWidth(),
                                                 mapModel.getTargetDevice().getPixelHeight(), 25, 3, null );
            } catch ( CRSTransformationException e ) {
                LOG.logError( e.getMessage(), e );
                throw new DataAccessException( e.getMessage() );
            }
        }
        this.coverage = new ImageGridCoverage( null, mapModel.getEnvelope(), image );
        fireLoadingFinishedEvent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.IMapModelAdapter#refresh()
     */
    public void refresh() {
        // TODO Auto-generated method stub

    }

    @Override
    public void commitChanges()
                            throws IOException {
        // TODO Auto-generated method stub

    }

}
