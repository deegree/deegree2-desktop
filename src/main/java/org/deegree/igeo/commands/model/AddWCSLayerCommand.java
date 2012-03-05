/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2007 by:
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

package org.deegree.igeo.commands.model;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import net.sf.ehcache.Cache;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.HttpUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.config.OnlineResourceType;
import org.deegree.igeo.config.QualifiedNameType;
import org.deegree.igeo.config.WCSDatasourceType;
import org.deegree.igeo.config.ServiceDatasourceType.CapabilitiesURL;
import org.deegree.igeo.dataadapter.DataAccessException;
import org.deegree.igeo.dataadapter.wcs.WCSCapabilitiesEvaluator;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.AuthenticationInformation;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.LayerGroup;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.WCSDatasource;
import org.deegree.igeo.settings.WCSGridCoverageAdapterSettings;
import org.deegree.kernel.AbstractCommand;
import org.deegree.model.Identifier;
import org.deegree.ogcwebservices.wcs.CoverageOfferingBrief;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageDescriptionDocument;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageOffering;
import org.deegree.ogcwebservices.wcs.getcapabilities.WCSCapabilities;
import org.xml.sax.SAXException;

/**
 * <code>AddWCSLayerCommand</code> adds a WCS as a new layer to the map model
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class AddWCSLayerCommand extends AbstractCommand {

    private static final ILogger LOG = LoggerFactory.getLogger( AddWCSLayerCommand.class );

    public static final QualifiedName commandName = new QualifiedName( "add layer from WCS command" );

    private MapModel mapModel;

    private String layerName = UUID.randomUUID().toString();

    private String layerTitle = "";

    private String layerAbstract = "";

    private String nameDS = "";

    private double minScaleDenominator = 0;

    private double maxScaleDenominator = 9E99;

    private URL capabilitiesURL;

    private WCSCapabilities wcsCapabilities;

    private List<org.deegree.igeo.config.LayerType.MetadataURL> metadataURLs = new ArrayList<org.deegree.igeo.config.LayerType.MetadataURL>();

    private org.deegree.igeo.mapmodel.Layer newLayer;

    private boolean performed = false;

    private CoverageOfferingBrief coverageOffering;

    private String crs;

    private String format;

    private String timestamp;

    /**
     * @param mapModel
     *            the mapModel to add the new layer
     * @param capabilitiesURL
     *            the capabilities URL of the WCS to add as a new layer
     * @param wcsCapabilities
     * @param layerTitle
     *            the title of the layer
     */
    private AddWCSLayerCommand( MapModel mapModel, URL capabilitiesURL, WCSCapabilities wcsCapabilities,
                                String layerTitle ) {
        setMetaInformation( wcsCapabilities );
        this.mapModel = mapModel;
        this.capabilitiesURL = capabilitiesURL;
        this.layerTitle = layerTitle;
    }

    /**
     * @param mapModel
     *            the mapModel to add the new layer
     * @param capabilitiesURL
     *            the capabilities URL of the WCS to add as a new layer
     * @param wcsCapabilities
     *            the capabilities document of the WCS to add as new layer
     * @param coverageOffering
     *            the selected coverage
     * @param format
     */
    public AddWCSLayerCommand( MapModel mapModel, URL capabilitiesURL, WCSCapabilities wcsCapabilities,
                               CoverageOffering coverageOffering, String format, String timestamp ) {
        this( mapModel, capabilitiesURL, wcsCapabilities, coverageOffering.getLabel() );
        setMetadataURLs( coverageOffering );
        this.coverageOffering = coverageOffering;
        if ( coverageOffering.getSupportedCRSs().getNativeSRSs() != null
             && coverageOffering.getSupportedCRSs().getNativeSRSs().length > 0 ) {
            this.crs = coverageOffering.getSupportedCRSs().getNativeSRSs()[0].getCodes()[0];
        } else {
            this.crs = coverageOffering.getSupportedCRSs().getRequestResponseSRSs()[0].getCodes()[0];
        }
        this.format = format;
        this.timestamp = timestamp;
    }

    /**
     * @param mapModel
     *            the mapModel to add the new layer
     * @param capabilitiesURL
     *            the capabilities URL of the WCS to add as a new layer
     * @param wcsCapabilities
     *            the capabilities document of the WCS to add as new layer
     * @param coverageOffering
     *            the selected coverage
     */
    public AddWCSLayerCommand( MapModel mapModel, URL capabilitiesURL, WCSCapabilities wcsCapabilities,
                               CoverageOfferingBrief coverageOffering ) {
        this( mapModel, capabilitiesURL, wcsCapabilities, coverageOffering.getLabel() );
        setMetadataURLs( coverageOffering );
        this.coverageOffering = coverageOffering;
        readCRSandFormat();
    }

    /**
     * 
     * @param mapModel
     * @param capabilitiesURL
     * @param wcsCaps
     * @param layerName
     * @param layerTitle
     * @param layerAbstract
     * @param nameDS
     * @param minScale
     * @param maxScale
     * @param coverageOffering
     * @param format
     * @param timestamp
     */
    public AddWCSLayerCommand( MapModel mapModel, URL capabilitiesURL, WCSCapabilities wcsCaps, String layerName,
                               String layerTitle, String layerAbstract, String nameDS, double minScale,
                               double maxScale, CoverageOffering coverageOffering, String format, String timestamp ) {
        this( mapModel, capabilitiesURL, wcsCaps, layerTitle );
        this.layerName = layerName;
        this.layerAbstract = layerAbstract;
        this.minScaleDenominator = minScale;
        this.maxScaleDenominator = maxScale;
        this.nameDS = nameDS;
        this.coverageOffering = coverageOffering;
        if ( coverageOffering.getSupportedCRSs().getNativeSRSs() != null
             && coverageOffering.getSupportedCRSs().getNativeSRSs().length > 0 ) {
            this.crs = coverageOffering.getSupportedCRSs().getNativeSRSs()[0].getCodes()[0];
        } else {
            this.crs = coverageOffering.getSupportedCRSs().getRequestResponseSRSs()[0].getCodes()[0];
        }
        this.format = format;
        this.timestamp = timestamp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {

        AuthenticationInformation authenticationInformation = null;
        Cache cache = null;

        List<Datasource> datasources = new ArrayList<Datasource>( 1 );

        // create the wcs datasource
        WCSDatasourceType dsType = new WCSDatasourceType();
        dsType.setName( nameDS );
        QualifiedNameType qnt = new QualifiedNameType();
        qnt.setLocalName( coverageOffering.getName() );
        dsType.setCoverage( qnt );
        dsType.setMinScaleDenominator( minScaleDenominator );
        dsType.setMaxScaleDenominator( maxScaleDenominator );
        dsType.setEditable( false );
        dsType.setQueryable( true );
        dsType.setLazyLoading( true );
        dsType.setSupportToolTips( false );
        dsType.setNativeCRS( crs );
        dsType.setFormat( format );
        dsType.setTime( timestamp );
        CapabilitiesURL cu = new CapabilitiesURL();
        OnlineResourceType ort = new OnlineResourceType();
        ort.setHref( this.capabilitiesURL.toExternalForm() );
        cu.setOnlineResource( ort );
        dsType.setCapabilitiesURL( cu );

        datasources.add( new WCSDatasource( dsType, authenticationInformation, cache ) );
        // avoid double layer name/id
        Identifier id = new Identifier( layerName );
        int i = 0;
        String tmp = layerTitle;
        while ( mapModel.exists( id ) ) {
            tmp = layerTitle + "_" + i;
            id = new Identifier( layerName + "_" + i++ );
        }
        // create a new layer
        newLayer = new org.deegree.igeo.mapmodel.Layer( mapModel, id, tmp, this.layerAbstract, datasources,
                                                        this.metadataURLs );
        newLayer.setMinScaleDenominator( minScaleDenominator );
        newLayer.setMaxScaleDenominator( maxScaleDenominator );
        newLayer.setVisible( true );
        newLayer.setEditable( false );
        // and add the layer to the mapModel
        if ( mapModel.getLayerGroups().size() == 0 ) {
            LayerGroup layerGroup = new LayerGroup( mapModel, new Identifier(), "LayerGroup", "" );
            mapModel.insert( layerGroup, null, null, false );
        }
        mapModel.insert( newLayer, mapModel.getLayerGroups().get( 0 ), null, false );
        performed = true;
        fireCommandProcessedEvent();
    }

    /**
     * @return
     */
    private void readCRSandFormat() {

        CoverageDescriptionDocument doc = new CoverageDescriptionDocument();
        String descURL = null;
        try {
            ApplicationContainer<?> appCont = mapModel.getApplicationContainer();
            WCSGridCoverageAdapterSettings wcsSet = appCont.getSettings().getWCSGridCoveragesAdapter();
            descURL = readDescribeCoverageURL().toURI().toASCIIString();
            String tmp = HttpUtils.normalizeURL( descURL );
            descURL = HttpUtils.addAuthenticationForKVP( descURL, appCont.getUser(), appCont.getPassword(),
                                                         appCont.getCertificate( tmp ) );
            URL url = new URL( descURL );
            String req = "VERSION=" + wcsCapabilities.getVersion() + "&SERVICE=WCS&COVERAGE="
                         + coverageOffering.getName() + "&request=DescribeCoverage";
            InputStream is = HttpUtils.performHttpGet( url.toURI().toASCIIString(), req, wcsSet.getTimeout(),
                                                       appCont.getUser(), appCont.getPassword(), null ).getResponseBodyAsStream();
            doc.load( is, descURL );
            CoverageOffering co = doc.getCoverageOfferings()[0];
            if ( co.getSupportedCRSs().getNativeSRSs() != null && co.getSupportedCRSs().getNativeSRSs().length > 0 ) {
                crs = co.getSupportedCRSs().getNativeSRSs()[0].getCodes()[0];
            } else {
                crs = co.getSupportedCRSs().getRequestResponseSRSs()[0].getCodes()[0];
            }
            format = co.getSupportedFormats().getFormats()[0].getCodes()[0];
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            String s = StringTools.stackTraceToString( e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10113", descURL ) + s );
        }

    }

    private URL readDescribeCoverageURL() {

        XMLFragment xml;
        String capabilitiesUrl = null;
        ApplicationContainer<?> appCont = mapModel.getApplicationContainer();
        WCSGridCoverageAdapterSettings wcsSet = appCont.getSettings().getWCSGridCoveragesAdapter();
        try {
            capabilitiesUrl = this.capabilitiesURL.toURI().toASCIIString();
            String tmp = HttpUtils.normalizeURL( capabilitiesUrl );
            capabilitiesUrl = HttpUtils.addAuthenticationForKVP( capabilitiesUrl, appCont.getUser(),
                                                                 appCont.getPassword(), appCont.getCertificate( tmp ) );
            InputStream is = HttpUtils.performHttpGet( capabilitiesUrl, null, wcsSet.getTimeout(), appCont.getUser(),
                                                       appCont.getPassword(), null ).getResponseBodyAsStream();
            xml = new XMLFragment();
            xml.load( is, capabilitiesUrl );

        } catch ( SAXException e ) {
            LOG.logError( e.getMessage(), e );
            String s = StringTools.stackTraceToString( e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10106", capabilitiesUrl ) + s );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            String s = StringTools.stackTraceToString( e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$$DG10107", capabilitiesUrl ) + s );
        }
        String version = null;
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
            return evaluator.getDescribeCoverageHTTPGetURL();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( e.getMessage(), e );
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getName()
     */
    public QualifiedName getName() {
        return commandName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getResult()
     */
    public Object getResult() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#isUndoSupported()
     */
    public boolean isUndoSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#undo()
     */
    public void undo()
                            throws Exception {
        if ( performed ) {
            mapModel.remove( newLayer );
            performed = false;
        }
    }

    /**
     * extract the meta information out of the WCS capabilities document
     * 
     * @param wcsCapabilities
     *            the capabilities of the WCS
     */
    private void setMetaInformation( WCSCapabilities wcsCapabilities ) {
        this.wcsCapabilities = wcsCapabilities;
        this.nameDS = wcsCapabilities.getService().getLabel();
    }

    /**
     * @param layers
     *            the layer to extract the metadataURLs
     */
    private void setMetadataURLs( CoverageOfferingBrief coverage ) {
        layerAbstract = coverage.getDescription();

        if ( coverage.getMetadataLink() != null ) {
            URL url = coverage.getMetadataLink().getReference();
            org.deegree.igeo.config.LayerType.MetadataURL mu = new org.deegree.igeo.config.LayerType.MetadataURL();
            OnlineResourceType ort = new OnlineResourceType();
            ort.setHref( url.toExternalForm() );
            mu.setOnlineResource( ort );
            this.metadataURLs.add( mu );
        }
    }

}
