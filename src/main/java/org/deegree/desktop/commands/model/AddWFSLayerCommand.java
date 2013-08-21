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

package org.deegree.desktop.commands.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.sf.ehcache.Cache;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.config.Util;
import org.deegree.desktop.dataadapter.DataAccessException;
import org.deegree.desktop.mapmodel.AuthenticationInformation;
import org.deegree.desktop.mapmodel.Datasource;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.LayerGroup;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.mapmodel.NamedStyle;
import org.deegree.desktop.mapmodel.WFSDatasource;
import org.deegree.desktop.settings.Settings;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.framework.util.HttpUtils;
import org.deegree.graphics.sld.UserStyle;
import org.deegree.desktop.config.DirectStyleType;
import org.deegree.desktop.config.OnlineResourceType;
import org.deegree.desktop.config.WFSDatasourceType;
import org.deegree.desktop.config.ServiceDatasourceType.CapabilitiesURL;
import org.deegree.desktop.config.WFSDatasourceType.GetFeatureRequest;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.Command;
import org.deegree.model.Identifier;
import org.deegree.model.filterencoding.Filter;
import org.deegree.ogcwebservices.OWSUtils;
import org.deegree.ogcwebservices.getcapabilities.GetCapabilities;
import org.deegree.ogcwebservices.getcapabilities.MetadataURL;
import org.deegree.ogcwebservices.wfs.XMLFactory;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSFeatureType;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.deegree.ogcwebservices.wfs.operation.Query;

/**
 * {@link Command} implementation for adding a layer based on a WFS feature type to a {@link MapModel}
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class AddWFSLayerCommand extends AbstractCommand {

    public static final QualifiedName commandName = new QualifiedName( "add layer from WFS command" );

    private MapModel mapModel;

    private WFSCapabilities wfsCapabilities;

    private URL capabilitiesURL;

    private WFSFeatureType featureType;

    private QualifiedName geometryProp;

    private String serviceName;

    private String serviceTitle;

    private String serviceAbstract;

    private List<org.deegree.desktop.config.LayerType.MetadataURL> metadataURLs = new ArrayList<org.deegree.desktop.config.LayerType.MetadataURL>();

    private String dsName;

    private double minScaleDenominator = 0;

    private double maxScaleDenominator = 9E99;

    private Filter filter;

    private Layer newLayer;

    private boolean performed = false;

    private boolean lazyLoading = false;

    /**
     * 
     * @param mapModel
     *            the mapModel to add the new layer
     * @param wfsCapabilities
     *            the capabilities of the new data source
     * @param featureType
     *            the featureType to add as new layer
     * @param geometryProp
     *            the geometry of the featureType to add as new layer
     * @param filter
     *            the filter
     */
    public AddWFSLayerCommand( MapModel mapModel, WFSCapabilities wfsCapabilities, WFSFeatureType featureType,
                               QualifiedName geometryProp, Filter filter ) {
        this.mapModel = mapModel;
        try {
            String s = OWSUtils.getHTTPGetOperationURL( wfsCapabilities, GetCapabilities.class ).toExternalForm();
            s = OWSUtils.validateHTTPGetBaseURL( s );
            this.capabilitiesURL = new URL( s + "version=" + wfsCapabilities.getVersion()
                                            + "&service=WFS&request=GetCapabilities" );
        } catch ( MalformedURLException e ) {
            throw new DataAccessException( e );
        }
        this.wfsCapabilities = wfsCapabilities;
        this.featureType = featureType;
        this.geometryProp = geometryProp;

        this.serviceName = UUID.randomUUID().toString();
        String title;
        if ( featureType.getTitle() != null && featureType.getTitle().length() > 0 ) {
            title = featureType.getTitle();
        } else {
            title = featureType.getName().getLocalName();
        }

        this.serviceTitle = title;
        this.serviceAbstract = featureType.getAbstract();

        String dsName = featureType.getName().getLocalName();
        if ( wfsCapabilities.getServiceIdentification() != null
             && wfsCapabilities.getServiceIdentification().getName() != null ) {
            dsName = dsName + " " + wfsCapabilities.getServiceIdentification().getName();
        }
        this.dsName = dsName;
        this.filter = filter;
    }

    /**
     * 
     * @param mapModel
     *            the mapModel to add the new layer
     * @param wfsCapabilities
     *            the capabilities of the new datasource
     * @param featureType
     *            the featureType to add as new layer
     * @param geometryProp
     *            the geometry of the featureType to add as new layer
     * @param filter
     *            the filter
     * @param serviceName
     *            the name of the new layer
     * @param serviceTitle
     *            the title of the new layer
     * @param serviceAbstract
     *            the abstract of the new layer
     * @param dsName
     *            the name of the new data source
     * @param minScaleDenominator
     *            the minScaleDenominator of the new data source
     * @param maxScaleDenominator
     *            the maxScaleDenominator of the new data source
     */
    public AddWFSLayerCommand( MapModel mapModel, WFSCapabilities wfsCapabilities, WFSFeatureType featureType,
                               QualifiedName geometryProp, Filter filter, String serviceName, String serviceTitle,
                               String serviceAbstract, String dsName, double minScaleDenominator,
                               double maxScaleDenominator, boolean lazyLoading ) {
        this.mapModel = mapModel;
        try {
            String s = HttpUtils.normalizeURL( OWSUtils.getHTTPGetOperationURL( wfsCapabilities, GetCapabilities.class ) );
            s = OWSUtils.validateHTTPGetBaseURL( s );
            this.capabilitiesURL = new URL( s + "version=" + wfsCapabilities.getVersion()
                                            + "&service=WFS&request=GetCapabilities" );
        } catch ( MalformedURLException e ) {
            throw new DataAccessException( e );
        }
        this.wfsCapabilities = wfsCapabilities;
        this.featureType = featureType;
        this.geometryProp = geometryProp;

        this.serviceName = serviceName;
        this.serviceTitle = serviceTitle;
        this.serviceAbstract = serviceAbstract;
        this.dsName = dsName;

        // read meatadataURLs out of the featureType
        MetadataURL[] metadataUrls = this.featureType.getMetadataUrls();
        for ( int i = 0; i < metadataUrls.length; i++ ) {
            org.deegree.desktop.config.LayerType.MetadataURL mu = new org.deegree.desktop.config.LayerType.MetadataURL();
            OnlineResourceType ort = new OnlineResourceType();
            ort.setHref( metadataUrls[i].getOnlineResource().toExternalForm() );
            mu.setOnlineResource( ort );
            this.metadataURLs.add( mu );
        }
        this.minScaleDenominator = minScaleDenominator;
        this.maxScaleDenominator = maxScaleDenominator;
        this.filter = filter;
        this.lazyLoading = lazyLoading;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {
        try {
            List<Datasource> datasources = new ArrayList<Datasource>();

            AuthenticationInformation authenticationInformation = null;

            Cache cache = null;

            QualifiedName qn = featureType.getName();
            Query query = Query.create( null, null, null, null, null, new QualifiedName[] { qn }, null, null,
                                        this.filter, -1, 1, GetFeature.RESULT_TYPE.RESULTS );

            GetFeature getFeature = GetFeature.create( this.wfsCapabilities.getVersion(), "addLayer",
                                                       GetFeature.RESULT_TYPE.RESULTS, GetFeature.FORMAT_GML3, "", -1,
                                                       0, -1, -1, new Query[] { query } );

            WFSDatasourceType dsType = new WFSDatasourceType();
            dsType.setName( this.dsName );
            dsType.setMinScaleDenominator( minScaleDenominator );
            dsType.setMaxScaleDenominator( maxScaleDenominator );
            dsType.setEditable( true );
            dsType.setQueryable( true );
            dsType.setLazyLoading( lazyLoading );
            dsType.setSupportToolTips( true );
            dsType.setGeometryProperty( Util.convertQualifiedName( this.geometryProp ) );

            dsType.setNativeCRS( featureType.getDefaultSRS().toASCIIString() );

            CapabilitiesURL cu = new CapabilitiesURL();
            OnlineResourceType ort = new OnlineResourceType();
            ort.setHref( this.capabilitiesURL.toExternalForm() );
            cu.setOnlineResource( ort );
            dsType.setCapabilitiesURL( cu );
            GetFeatureRequest value = new GetFeatureRequest();
            value.setValue( XMLFactory.export( getFeature ).getAsString() );
            value.setVersion( getFeature.getVersion() );
            dsType.setGetFeatureRequest( value );

            datasources.add( new WFSDatasource( dsType, authenticationInformation, cache ) );

            // avoid double layer name/id
            Identifier id = new Identifier( this.serviceName );
            int i = 0;
            String tmp = this.serviceTitle;
            while ( mapModel.exists( id ) ) {
                tmp = this.serviceTitle + "_" + i;
                id = new Identifier( this.serviceName + "_" + i++ );
            }
            newLayer = new Layer( mapModel, id, tmp, this.serviceAbstract, datasources, this.metadataURLs );
            List<NamedStyle> styles = new ArrayList<NamedStyle>();
            Settings settings = mapModel.getApplicationContainer().getSettings();
            DirectStyleType dst = new DirectStyleType();
            dst.setCurrent( true );
            UserStyle us = settings.getWFSDefaultStyle().getDefaultStyle();
            dst.setName( us.getName() );
            dst.setTitle( us.getTitle() );
            dst.setAbstract( us.getAbstract() );
            dst.setCurrent( true );
            // styles.add( new DirectStyle( dst, us, newLayer ) );
            styles.add( new NamedStyle( dst, newLayer ) );
            newLayer.setStyles( styles );
            newLayer.setMinScaleDenominator( minScaleDenominator );
            newLayer.setMaxScaleDenominator( maxScaleDenominator );
            newLayer.setEditable( true );

            newLayer.setVisible( true );
            if ( mapModel.getLayerGroups().size() == 0 ) {
                LayerGroup layerGroup = new LayerGroup( mapModel, new Identifier(), "LayerGroup", "" );
                mapModel.insert( layerGroup, null, null, false );
            }
            mapModel.insert( newLayer, mapModel.getLayerGroups().get( 0 ), null, false );
        } catch ( Exception e ) {
            if ( processMonitor != null ) {
                processMonitor.cancel();
            }
            String s = mapModel.getApplicationContainer().getViewPlatform();
            DialogFactory.openErrorDialog( s, null, "LOAD WFS Layer", "can not load data from WFS", e );
            throw e;
        } finally {
            fireCommandProcessedEvent();
        }
        performed = true;
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
        return newLayer;
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

}
