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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.ehcache.Cache;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.values.TypedLiteral;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.KVP2Map;
import org.deegree.framework.util.StringTools;
import org.deegree.igeo.config.OnlineResourceType;
import org.deegree.igeo.config.WMSDatasourceType;
import org.deegree.igeo.config.ServiceDatasourceType.CapabilitiesURL;
import org.deegree.igeo.dataadapter.DataAccessException;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.AuthenticationInformation;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.LayerGroup;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.WMSDatasource;
import org.deegree.kernel.AbstractCommand;
import org.deegree.model.Identifier;
import org.deegree.ogcwebservices.OWSUtils;
import org.deegree.ogcwebservices.getcapabilities.GetCapabilities;
import org.deegree.ogcwebservices.getcapabilities.MetadataURL;
import org.deegree.ogcwebservices.wms.capabilities.Layer;
import org.deegree.ogcwebservices.wms.capabilities.LegendURL;
import org.deegree.ogcwebservices.wms.capabilities.Style;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.owscommon_new.DomainType;
import org.deegree.owscommon_new.HTTP;
import org.deegree.owscommon_new.Operation;
import org.deegree.owscommon_new.OperationsMetadata;
import org.deegree.owscommon_new.ServiceIdentification;

/**
 * <code>AddWMSLayerCommand</code> adds a WMS as a new layer to the map model
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class AddWMSLayerCommand extends AbstractCommand {

    private static final ILogger LOG = LoggerFactory.getLogger( AddWMSLayerCommand.class );

    public static final QualifiedName commandName = new QualifiedName( "add layer from WMS command" );

    private MapModel mapModel;

    private String layerName = UUID.randomUUID().toString();

    private String layerTitle = "";

    private String layerAbstract = "";

    private String nameDS = "";

    private double minScaleDenominator = 0;

    private double maxScaleDenominator = 9E99;

    private URL capabilitiesURL;

    private WMSCapabilities wmsCapabilities;

    private String baseRequest;

    private List<org.deegree.igeo.config.LayerType.MetadataURL> metadataURLs = new ArrayList<org.deegree.igeo.config.LayerType.MetadataURL>();

    private org.deegree.igeo.mapmodel.Layer newLayer;

    private boolean performed = false;

    private boolean allowSwapAxis;

    /**
     * @param mapModel
     *            the mapModel to add the new layer
     * @param wmsCapabilities
     * @param layerTitle
     *            the title of the layer
     * @param allowSwapAxsisOrder
     *            if <code>true</code> axis order of bounding box will be swapped if a WMS 1.3 GetMap request shall be
     *            created
     */
    private AddWMSLayerCommand( MapModel mapModel, WMSCapabilities wmsCapabilities, String layerTitle,
                                boolean allowSwapAxis ) {
        setMetaInformation( wmsCapabilities );
        this.allowSwapAxis = allowSwapAxis;
        this.mapModel = mapModel;
        try {
            String s = OWSUtils.getHTTPGetOperationURL( wmsCapabilities, GetCapabilities.class ).toExternalForm();
            s = OWSUtils.validateHTTPGetBaseURL( s );
            this.capabilitiesURL = new URL( s + "version=" + wmsCapabilities.getVersion()
                                            + "&service=WMS&request=GetCapabilities" );
        } catch ( MalformedURLException e ) {
            throw new DataAccessException( e );
        }
        this.layerTitle = layerTitle;
    }

    /**
     * @param mapModel
     *            the mapModel to add the new layer
     * @param wmsCapabilities
     * @param layerName
     *            the name of the new layer
     * @param layerTitle
     *            the title of the new layer
     * @param layerAbstract
     *            abstract of the new layer
     * @param nameDS
     *            the name of the datasource
     * @param minScaleDenominator
     *            the minScaleDenominator of the new datasource
     * @param maxScaleDenominator
     *            the maxScaleDenominator of the new datasource
     * @param selectedStyleLayers
     *            the selected layers with style information
     * @param baseRequest
     *            baseRequest of the datasource
     * @param allowSwapAxsisOrder
     *            if <code>true</code> axis order of bounding box will be swapped if a WMS 1.3 GetMap request shall be
     *            created
     */
    public AddWMSLayerCommand( MapModel mapModel, WMSCapabilities wmsCapabilities, String layerName, String layerTitle,
                               String layerAbstract, String nameDS, double minScaleDenominator,
                               double maxScaleDenominator, Map<Layer, String> selectedStyleLayers, String baseRequest,
                               boolean allowSwapAxis ) {
        this( mapModel, wmsCapabilities, layerTitle, allowSwapAxis );
        this.layerName = layerName;
        this.layerAbstract = layerAbstract;
        this.nameDS = nameDS;
        this.minScaleDenominator = minScaleDenominator;
        this.maxScaleDenominator = maxScaleDenominator;
        setMetadataURLs( selectedStyleLayers );
        this.baseRequest = baseRequest;
    }

    /**
     * @param mapModel
     *            the mapModel to add the new layer
     * @param wmsCapabilities
     *            the capabilities document of the WMS to add as new layer
     * @param selectedLayers
     *            the selected layers
     * @param layerTitle
     *            the title of the layer
     * @param allowSwapAxsisOrder
     *            if <code>true</code> axis order of bounding box will be swapped if a WMS 1.3 GetMap request shall be
     *            created
     */
    public AddWMSLayerCommand( MapModel mapModel, WMSCapabilities wmsCapabilities, List<Layer> selectedLayers,
                               String layerTitle, boolean allowSwapAxis ) {
        this( mapModel, wmsCapabilities, layerTitle, allowSwapAxis );
        setMetadataURLs( selectedLayers );

        String layers = null;
        for ( Layer layer : selectedLayers ) {
            if ( layers != null && layers.length() > 0 ) {
                layers = StringTools.concat( 300, layers, ',', layer.getName() );
            } else {
                layers = layer.getName();
            }
        }
        this.baseRequest = StringTools.concat( 500, "REQUEST=GetMap&TRANSPARENT=TRUE", "&FORMAT=",
                                               getFormat( wmsCapabilities ), "&LAYERS=", layers );
    }

    /**
     * @param mapModel
     *            the mapModel to add the new layer
     * @param wmsCapabilities
     *            the capabilities document of the WMS to add as new layer
     * @param selectedLayerStyles
     *            the selected layers with style information
     * @param layerTitle
     *            the title of the layer
     * @param allowSwapAxsisOrder
     *            if <code>true</code> axis order of bounding box will be swapped if a WMS 1.3 GetMap request shall be
     *            created
     */
    public AddWMSLayerCommand( MapModel mapModel, WMSCapabilities wmsCapabilities,
                               Map<Layer, String> selectedLayerStyles, String layerTitle, boolean allowSwapAxis ) {
        this( mapModel, wmsCapabilities, layerTitle, allowSwapAxis );
        setMetadataURLs( selectedLayerStyles );

        this.baseRequest = StringTools.concat( 500, "REQUEST=GetMap&TRANSPARENT=TRUE", "&FORMAT=",
                                               getFormat( wmsCapabilities ), getLayerStyleSection( selectedLayerStyles ) );
    }

    /**
     * @param mapModel
     *            the mapModel to add the new layer
     * @param wmsCapabilities
     *            the capabilities document of the WMS to add as new layer
     * @param selectedLayerStyles
     *            the selected layers with style information
     * @param layerTitle
     *            the title of the layer
     * @param format
     *            the desired format of the map
     * @param transparency
     *            indicates, if the background should be transparent
     * @param allowSwapAxsisOrder
     *            if <code>true</code> axis order of bounding box will be swapped if a WMS 1.3 GetMap request shall be
     *            created
     */
    public AddWMSLayerCommand( MapModel mapModel, WMSCapabilities wmsCapabilities,
                               Map<Layer, String> selectedLayerStyles, String layerTitle, String format,
                               boolean transparency, boolean allowSwapAxis ) {
        this( mapModel, wmsCapabilities, layerTitle, allowSwapAxis );
        setMetadataURLs( selectedLayerStyles );
        this.baseRequest = StringTools.concat( 500, "REQUEST=GetMap&", "&FORMAT=", format, "&TRANSPARENT=",
                                               ( "" + transparency ).toUpperCase(),
                                               getLayerStyleSection( selectedLayerStyles ) );

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

        String crs = findCommonCRS();
        if ( crs == null ) {
            throw new DataAccessException( Messages.get( "$DG10089", this.baseRequest ) );
        }

        List<Datasource> datasources = new ArrayList<Datasource>( 1 );

        // create the wms datasource
        WMSDatasourceType dsType = new WMSDatasourceType();
        dsType.setName( nameDS );
        dsType.setMinScaleDenominator( minScaleDenominator );
        dsType.setMaxScaleDenominator( maxScaleDenominator );
        dsType.setEditable( false );
        dsType.setQueryable( true );
        dsType.setLazyLoading( true );
        dsType.setSupportToolTips( false );
        dsType.setBaseRequest( this.baseRequest );
        dsType.setNativeCRS( crs );
        dsType.setServiceVersion( wmsCapabilities.getVersion() );
        dsType.setAllowSwapAxis( allowSwapAxis );
        CapabilitiesURL cu = new CapabilitiesURL();
        OnlineResourceType ort = new OnlineResourceType();
        ort.setHref( this.capabilitiesURL.toExternalForm() );
        cu.setOnlineResource( ort );
        dsType.setCapabilitiesURL( cu );

        datasources.add( new WMSDatasource( dsType, authenticationInformation, cache ) );
        Identifier id = new Identifier( this.layerName );
        int i = 0;
        String tmp = this.layerTitle;
        while ( mapModel.exists( id ) ) {
            tmp = this.layerTitle + "_" + i;
            id = new Identifier( this.layerName + "_" + i++ );
        }
        // create a new layer
        newLayer = new org.deegree.igeo.mapmodel.Layer( mapModel, id, tmp, this.layerAbstract, datasources,
                                                        this.metadataURLs );
        newLayer.setMinScaleDenominator( minScaleDenominator );
        newLayer.setMaxScaleDenominator( maxScaleDenominator );
        newLayer.setVisible( true );
        newLayer.setEditable( false );
        URL legendURL = getLegendURL();
        LOG.logDebug( "legend URL: ", legendURL );
        try {
            newLayer.getCurrentStyle().setLegendURL( legendURL );
        } catch ( Exception e ) {
            LOG.logWarning( "could not set legend URL", e );
        }
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
    private String findCommonCRS() {
        String crs = mapModel.getCoordinateSystem().getPrefixedName();
        Map<String, String> map = KVP2Map.toMap( this.baseRequest );
        String[] layers = StringTools.toArray( map.get( "LAYERS" ), ",", true );
        List<String[]> layersSRS = new ArrayList<String[]>();
        boolean supportsMapSRS = true;
        for ( String layerName : layers ) {
            Layer layer = wmsCapabilities.getLayer( layerName );
            String[] srs = layer.getSrs();
            boolean tmp = false;
            for ( String string : srs ) {
                if ( string.equalsIgnoreCase( crs ) ) {
                    tmp = true;
                    break;
                }
            }
            if ( !tmp ) {
                supportsMapSRS = false;
            }
            layersSRS.add( srs );
        }
        if ( !supportsMapSRS ) {
            // find common crs because map model CRS is not supported by all layers
            crs = null;
            String[] tmp1 = layersSRS.get( 0 );
            for ( int i = 0; i < tmp1.length; i++ ) {
                int k = 0;
                for ( int j = 1; j < layersSRS.size(); j++ ) {
                    String[] tmp2 = layersSRS.get( j );
                    for ( int l = 0; l < tmp2.length; l++ ) {
                        if ( tmp2[l].equalsIgnoreCase( tmp1[i] ) ) {
                            k++;
                            break;
                        }
                    }
                }
                // if each layer supports srs tmp1[i] k must be equal to (layersSRS.size() - 1)
                if ( k == layersSRS.size() - 1 ) {
                    crs = tmp1[i];
                    break;
                }
            }
        }
        return crs;
    }

    private URL getLegendURL() {
        Map<String, String> map = KVP2Map.toMap( baseRequest );
        String layerName = map.get( "LAYERS" );
        LOG.logDebug( "layer: ", layerName );

        URL legendURL = null;
        // a layer name contains a ',' it is a layer combined from more than one layer, so it will
        // not be
        // possible to read a legend URL from WMS capabilities
        if ( layerName != null && !layerName.contains( "," ) ) {
            Layer layer = wmsCapabilities.getLayer( layerName );
            if ( layer != null ) {
                String s = map.get( "STYLES" );
                if ( s == null || s.trim().length() == 0 ) {
                    s = "default";
                }
                Style style = layer.getStyleResource( s );
                if ( style != null ) {
                    LegendURL[] legendURLs = style.getLegendURL();
                    if ( legendURLs != null && legendURLs.length > 0 ) {
                        legendURL = legendURLs[0].getOnlineResource();
                    }
                }
            }
            if ( legendURL == null ) {
                QualifiedName getLegendGraphic = new QualifiedName( "GetLegendGraphic" );
                Operation operation = wmsCapabilities.getOperationMetadata().getOperation( getLegendGraphic );
                if ( operation != null ) {
                    legendURL = ( (HTTP) operation.getDCP().get( 0 ) ).getGetOnlineResources().get( 0 );
                    try {
                        String s = legendURL.toURI().toASCIIString();
                        if ( !s.endsWith( "?" ) ) {
                            s = s + "?";
                        }
                        legendURL = new URL( s
                                             + "request=GetLegendGraphic&format=image/png&width=40&height=40&version="
                                             + wmsCapabilities.getVersion() + "&layer=" + layerName + "&style="
                                             + map.get( "STYLES" ) );
                    } catch ( Exception e ) {
                        LOG.logWarning( "could not create legend URL", e );
                    }
                }
            }
        }
        return legendURL;
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
     * extract the metainformation out of the wms capabilities document
     * 
     * @param wmsCapabilities
     *            the capabilities of the WMS
     */
    private void setMetaInformation( WMSCapabilities wmsCapabilities ) {
        this.wmsCapabilities = wmsCapabilities;
        ServiceIdentification service = wmsCapabilities.getServiceIdentification();
        if ( service != null ) {
            if ( this.layerTitle == null || !( this.layerTitle.length() > 0 ) ) {
                this.layerTitle = service.getTitle();
            }
            this.nameDS = service.getTitle();
        }
    }

    /**
     * @param wmsCapabilities
     *            the capabilities of the WMS
     * @return the format declared at first position
     */
    private String getFormat( WMSCapabilities wmsCapabilities ) {
        OperationsMetadata om = wmsCapabilities.getOperationMetadata();
        Operation op = om.getOperation( new QualifiedName( "GetMap" ) );
        if ( op == null ) {
            om.getOperation( new QualifiedName( "map" ) );
        }
        DomainType parameter = (DomainType) op.getParameter( new QualifiedName( "Format" ) );
        List<TypedLiteral> values = parameter.getValues();
        return values.get( 0 ).getValue();
    }

    /**
     * @param selectedLayerStyles
     *            the map containing all selected layers with assigned Styles
     * @return the kvp encoded layer/style section for the GetMap request
     */
    private String getLayerStyleSection( Map<Layer, String> selectedLayerStyles ) {
        String layers = null;
        String styles = null;
        for ( Layer layer : selectedLayerStyles.keySet() ) {
            String styleName = selectedLayerStyles.get( layer );
            if ( layers != null && layers.length() > 0 ) {
                layers = StringTools.concat( 300, layers, ',', layer.getName() );
                styles = StringTools.concat( 300, styles, ',', styleName );
            } else {
                layers = layer.getName();
                styles = styleName;
            }
        }
        return StringTools.concat( 500, "&LAYERS=", layers, "&STYLES=", styles );
    }

    /**
     * @param layers
     *            the layer to extract the metadataURLs
     */
    private void setMetadataURLs( List<Layer> layers ) {
        for ( Layer layer : layers ) {
            MetadataURL[] urls = layer.getMetadataURL();
            for ( int i = 0; i < urls.length; i++ ) {
                org.deegree.igeo.config.LayerType.MetadataURL mu = new org.deegree.igeo.config.LayerType.MetadataURL();
                OnlineResourceType ort = new OnlineResourceType();
                ort.setHref( urls[i].getOnlineResource().toExternalForm() );
                mu.setOnlineResource( ort );
                this.metadataURLs.add( mu );
            }
        }
    }

    /**
     * @param selectedLayerStyles
     */
    private void setMetadataURLs( Map<Layer, String> selectedLayerStyles ) {
        for ( Layer layer : selectedLayerStyles.keySet() ) {
            MetadataURL[] urls = layer.getMetadataURL();
            for ( int i = 0; i < urls.length; i++ ) {
                org.deegree.igeo.config.LayerType.MetadataURL mu = new org.deegree.igeo.config.LayerType.MetadataURL();
                OnlineResourceType ort = new OnlineResourceType();
                ort.setHref( urls[i].getOnlineResource().toExternalForm() );
                mu.setOnlineResource( ort );
                this.metadataURLs.add( mu );
            }
        }
    }

}
