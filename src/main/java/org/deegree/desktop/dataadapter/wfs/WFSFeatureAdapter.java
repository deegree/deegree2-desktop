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

package org.deegree.desktop.dataadapter.wfs;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.dataadapter.DataAccessException;
import org.deegree.desktop.dataadapter.FeatureAdapter;
import org.deegree.desktop.dataadapter.FileFeatureAdapter;
import org.deegree.desktop.dataadapter.OWSURLUtils;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.Datasource;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.mapmodel.WFSDatasource;
import org.deegree.desktop.settings.Settings;
import org.deegree.desktop.settings.WFSFeatureAdapterSettings;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.HttpUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.schema.GMLSchema;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.deegree.ogcwebservices.wfs.operation.Query;
import org.xml.sax.SAXException;

/**
 * Implementation of {@link FeatureAdapter} for accessing data from a WFS
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class WFSFeatureAdapter extends FeatureAdapter {

    private static final ILogger LOG = LoggerFactory.getLogger( FileFeatureAdapter.class );

    private boolean isLazyLoading;

    private URL describeFTURL;

    private URL getFeatureURL;

    private URL transactionURL;

    private String version;

    private WFSDataLoader loader;

    private WFSFeatureAdapterSettings wfsAda;

    private Envelope lastEnv;

    private static Map<String, XMLFragment> capabilitiesCache;

    private URL baseURL;

    static {
        if ( capabilitiesCache == null ) {
            capabilitiesCache = new HashMap<String, XMLFragment>();
        }
    }

    /**
     * 
     * @param datasource
     * @param layer
     * @param mapModel
     * @param baseURL
     * @param isLazyLoading
     */
    public WFSFeatureAdapter( Datasource datasource, Layer layer, MapModel mapModel, URL baseURL, boolean isLazyLoading ) {
        super( datasource, layer, mapModel );
        this.baseURL = baseURL;

        this.isLazyLoading = isLazyLoading;

        Settings settings = mapModel.getApplicationContainer().getSettings();
        wfsAda = settings.getWFSFeatureAdapter();

        // first try using baseURL for performing requests
        useBaseURL( baseURL );

        instantiateLoader();

        refresh();
    }

    /**
     * 
     * @param baseURL
     */
    private void useBaseURL( URL baseURL ) {
        try {
            URL url = OWSURLUtils.normalizeOWSURL( baseURL );
            transactionURL = url;
            getFeatureURL = url;
            describeFTURL = url;
        } catch ( MalformedURLException e ) {
            LOG.logError( e );
        }
    }

    /**
     * loads the GML application schema assigned with the feature types of a WFS datasource
     * 
     */
    private void loadSchema() {
        GetFeature gf = ( (WFSDatasource) this.datasource ).getGetFeature();
        Query query = gf.getQuery()[0];
        if ( schemas.get( datasource.getName() ) == null ) {
            try {
                // read GML application schema if not already has been loaded
                QualifiedName[] qn = query.getTypeNames();
                GMLSchema schema = loader.readGMLApplicationSchema( describeFTURL, layer, qn );
                schemas.put( datasource.getName(), schema.getFeatureType( qn[0] ) );
            } catch ( Exception e ) {
                if ( describeFTURL == null ) {
                    // read target URLs (and WFS version) from WFS capabilities
                    readURLs( baseURL );
                    loadSchema();
                } else {
                    throw new DataAccessException( e );
                }
            }
        }
    }

    /**
     * reads destination URLs for GetFeature, DescribeFeatureType and Transaction requests from WFS capabilities
     * document
     * 
     * @param baseURL
     * @return
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
                InputStream is = HttpUtils.performHttpGet( capabilitiesUrl, null, wfsAda.getTimeout(),
                                                           appCont.getUser(), appCont.getPassword(), null ).getResponseBodyAsStream();
                xml = new XMLFragment();
                xml.load( is, baseURL.toExternalForm() );
                capabilitiesCache.put( capabilitiesUrl, xml );
            }
        } catch ( SAXException e ) {
            LOG.logError( e.getMessage(), e );
            String s = StringTools.stackTraceToString( e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10013", capabilitiesUrl )
                                           + "\n" + s, e );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            String s = StringTools.stackTraceToString( e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10012", capabilitiesUrl )
                                           + "\n" + s );
        }
        try {
            version = XMLTools.getRequiredAttrValue( "version", null, xml.getRootElement() );
        } catch ( XMLParsingException e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10014", capabilitiesUrl,
                                                                xml.getAsPrettyString() ) );
        }

        String className = wfsAda.getCapabilitiesEvaluator( version );
        Class<?> clzz = null;
        try {
            clzz = Class.forName( className );
        } catch ( ClassNotFoundException e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10017", className ) );
        }

        WFSCapabilitiesEvaluator evaluator = null;
        try {
            evaluator = (WFSCapabilitiesEvaluator) clzz.newInstance();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10018", className ) );
        }
        evaluator.setCapabilities( xml );
        try {
            getFeatureURL = evaluator.getGetFeatureURL();
            describeFTURL = evaluator.getDescribeFeatureTypeURL();
            transactionURL = evaluator.getTransactionURL();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( e.getMessage(), e );
        }

    }

    /**
     * loads features described by the adapted WFS datasource
     * 
     */
    private void loadData() {
        fireStartLoadingEvent();
        GetFeature gf = ( (WFSDatasource) datasource ).getGetFeature();
        Query[] queries = gf.getQuery();
        QualifiedName geomProperty = ( (WFSDatasource) datasource ).getGeometryProperty();

        Envelope currentEnv = null;
        if ( this.isLazyLoading ) {
            currentEnv = mapModel.getEnvelope();
        } else {
            currentEnv = mapModel.getMaxExtent();
        }
        // transform current envelope into native CRS of the data source if required
        if ( !mapModel.getCoordinateSystem().equals( datasource.getNativeCoordinateSystem() ) ) {
            GeoTransformer gt = new GeoTransformer( datasource.getNativeCoordinateSystem() );
            try {
                currentEnv = gt.transform( currentEnv, mapModel.getCoordinateSystem().getPrefixedName(), true );
            } catch ( Exception e ) {
                LOG.logError( e );
                throw new DataAccessException( e.getMessage() );
            }
        }
        Envelope env = null;
        if ( featureCollections.get( datasource.getName() ) != null ) {
            // if a fc is already available take its BBOX
            try {
                env = featureCollections.get( datasource.getName() ).getBoundedBy();
            } catch ( GeometryException e ) {
                fireLoadingExceptionEvent();
                throw new DataAccessException( e );
            }
        }

        for ( Query query : queries ) {
            // load data from WFS
            FeatureCollection fc = loader.readFeatureCollection( getFeatureURL, geomProperty, currentEnv, query, layer );
            fc = transformToMapModelCrs( fc );
            try {
                if ( env == null ) {
                    env = fc.getBoundedBy();
                } else if ( fc.getBoundedBy() != null ) {
                    env = env.merge( fc.getBoundedBy() );
                }
            } catch ( Exception e ) {
                fireLoadingExceptionEvent();
                throw new DataAccessException( e );
            }
            if ( featureCollections.get( datasource.getName() ) == null ) {
                featureCollections.put( datasource.getName(), fc );
            } else {
                featureCollections.get( datasource.getName() ).addAllUncontained( fc );
            }
        }

        // set envelope of the datasource
        if ( env == null ) {
            env = layer.getOwner().getEnvelope();
        }
        datasource.setExtent( env );
        fireLoadingFinishedEvent();
    }

    /**
     * initializes the loader class used with a WFSFeatureAdapter. Because the concrete loader depends on version of
     * connected WFS, class name will be read from deegree configuration and instantiated using reflection API
     * 
     */
    private void instantiateLoader() {

        String className = wfsAda.getDataLoader( version );

        Class<?> clzz = null;
        try {
            clzz = Class.forName( className );
        } catch ( ClassNotFoundException e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10026", className ) );
        }

        try {
            this.loader = (WFSDataLoader) clzz.newInstance();
            this.loader.setTimeout( wfsAda.getTimeout() );
            this.loader.setMaxFeatures( wfsAda.getMaxFeatures() );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10027", className ) );
        }
    }

    /**
     * initializes the write class used with a WFSFeatureAdapter. Because the concrete loader depends on version of
     * connected WFS, class name will be read from deegree configuration and instantiated using reflection API
     * 
     */
    private WFSDataWriter instantiateWriter() {

        String className = wfsAda.getDataWriter( version );

        Class<?> clzz = null;
        try {
            clzz = Class.forName( className );
        } catch ( ClassNotFoundException e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10026", className ) );
        }

        WFSDataWriter writer;
        try {
            writer = (WFSDataWriter) clzz.newInstance();
            writer.setTimeout( wfsAda.getTimeout() );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10027", className ) );
        }
        return writer;
    }

    @Override
    public FeatureCollection getFeatureCollection() {
        if ( this.isLazyLoading ) {
            synchronized ( datasource ) {
                double min = datasource.getMinScaleDenominator();
                double max = datasource.getMaxScaleDenominator();
                if ( mapModel.getScaleDenominator() >= min && mapModel.getScaleDenominator() < max ) {
                    Envelope currentEnv = mapModel.getEnvelope();
                    if ( lastEnv == null || ( !currentEnv.equals( lastEnv ) && !lastEnv.contains( currentEnv ) ) ) {
                        refreshData();
                        lastEnv = mapModel.getEnvelope();
                        // perform all inserts, updates, deletes that has been performed on this
                        // data source adapter on the feature collection read from the adapted data source
                        updateFeatureCollection();
                    }
                }
            }
        }
        return featureCollections.get( datasource.getName() );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.IMapModelAdapter#refresh()
     */
    public void refresh() {
        if ( featureCollections.get( datasource.getName() ) == null & !this.isLazyLoading ) {
            // if feature collection has already been loaded for a not lazy loading data source
            // it don't have to loaded again.
            refreshData();
        }
        loadSchema();
    }

    private void refreshData() {
        try {
            loadData();
        } catch ( Exception e ) {
            if ( getFeatureURL == null ) {
                // read target URLs (and WFS version) from WFS capabilities
                readURLs( baseURL );
                loadData();
            } else {
                throw new DataAccessException( e );
            }
        }
    }

    @Override
    public void refresh( boolean forceReload ) {
        if ( forceReload ) {
            refreshData();
        } else {
            refresh();
        }
    }

    @Override
    public void commitChanges()
                            throws IOException {

        if ( transactionURL != null ) {
            try {
                WFSDataWriter writer = instantiateWriter();
                List<FeatureAdapter.Changes> changeList = changes.get( datasource.getName() );
                if ( changeList != null ) {
                    FeatureCollection fc = getInsertCollection( changeList );
                    List<String> newIDs = writer.insertFeatures( transactionURL, fc, layer );
                    // set IDs generated by the WFS when inserting new features to ensure consistent feature
                    // IDs on server (WFS) and client
                    Feature[] features = fc.toArray();
                    for ( int i = 0; i < features.length; i++ ) {
                        features[i].setId( newIDs.get( i ) );
                    }

                    fc = getUpdateCollection( changeList );
                    writer.updateFeatures( transactionURL, fc, layer );

                    fc = getDeleteCollection( changeList );
                    writer.deleteFeatures( transactionURL, fc, layer );
                }
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                throw new IOException( e.getMessage() );
            } finally {
                changes.get( datasource.getName() ).clear();
            }
        } else {
            throw new IOException( Messages.getMessage( Locale.getDefault(), "$DG10085", layer.getTitle() ) );
        }
    }
}
