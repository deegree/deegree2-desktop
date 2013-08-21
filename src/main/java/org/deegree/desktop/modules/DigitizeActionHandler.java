//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.desktop.modules;

import java.awt.Container;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.commands.UnselectFeaturesCommand;
import org.deegree.desktop.commands.digitize.CuttingPolygonHoleCommand;
import org.deegree.desktop.commands.digitize.GroupFeatureCommand;
import org.deegree.desktop.commands.digitize.InsertFeatureCommand;
import org.deegree.desktop.commands.digitize.MoveFeatureCommand;
import org.deegree.desktop.commands.digitize.UngroupFeatureCommand;
import org.deegree.desktop.commands.digitize.UniteGeometriesCommand;
import org.deegree.desktop.dataadapter.DataAccessAdapter;
import org.deegree.desktop.dataadapter.FeatureAdapter;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.state.mapstate.EditState;
import org.deegree.desktop.state.mapstate.MapTool;
import org.deegree.desktop.state.mapstate.ToolState;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.GeometryUtils;
import org.deegree.framework.util.Pair;
import org.deegree.kernel.Command;
import org.deegree.kernel.CommandList;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.GeometryPropertyType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiPoint;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Surface;

/**
 * Class containing methods for handling actions triggerd by {@link DigitizerModule}
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class DigitizeActionHandler {

    private static ILogger LOG = LoggerFactory.getLogger( DigitizeActionHandler.class );

    private ApplicationContainer<Container> appContainer;

    private DigitizerModule<Container> owner;

    /**
     * 
     * @param owner
     */
    DigitizeActionHandler( DigitizerModule<Container> owner ) {
        this.appContainer = owner.appContainer;
        this.owner = owner;
    }

    /**
     * 
     * @param selection
     *            action layer(s) are selected for
     * @return {@link Layer}
     */
    private Layer getSelectedLayer( String selection ) {
        MapModel mm = appContainer.getMapModel( null );
        List<Layer> layList = mm.getLayersSelectedForAction( selection );
        if ( layList.size() > 0 ) {
            return layList.get( 0 );
        }
        return null;
    }

    /**
     * deletes selected features
     * 
     */
    protected void deleteFeature() {
        Layer layer = getSelectedLayer( MapModel.SELECTION_EDITING );
        List<DataAccessAdapter> dataAccess = layer.getDataAccess();

        DefaultMapModule<?> mapModule = owner.getAssignedMapModule();
        ToolState ts = mapModule.getMapTool().getState();
        setDeleteFeatureState();
        // In this case a feature or a number of features has been changed. Because all
        // changes will be done to a copy of a feature to underlying data access layer
        // must be updated explicitly.
        EditState es = (EditState) mapModule.getMapTool().getState();
        try {
            Command command = es.createCommand( dataAccess.get( 0 ), layer.getSelectedFeatures() );
            appContainer.getCommandProcessor().executeSychronously( command, true );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null,
                                             Messages.getMessage( Locale.getDefault(), "$MD10289", e.getMessage() ),
                                             Messages.getMessage( Locale.getDefault(), "$MD10290" ) );
        }
        // reset previous state
        mapModule.getMapTool().setState( ts );
        layer.unselectAllFeatures();
        owner.getDigitizerPanel().resetAll();
        owner.getEditFeature().setFeature( layer, FeatureFactory.createFeatureCollection( "ID_", 1 ) );
    }

    /**
     * 
     * @param parameter
     *            values contains IDs of features to be deleted
     */
    protected void deleteFeature( Map<String, Object> parameter ) {
        Layer layer = getSelectedLayer( MapModel.SELECTION_EDITING );
        List<DataAccessAdapter> dataAccess = layer.getDataAccess();

        FeatureCollection fc = FeatureFactory.createFeatureCollection( "UUID_" + UUID.randomUUID().toString(),
                                                                       parameter.size() );
        Iterator<Object> iter = parameter.values().iterator();
        while ( iter.hasNext() ) {
            String id = (String) iter.next();
            Feature feature = ( (FeatureAdapter) dataAccess.get( 0 ) ).getFeatureCollection().getFeature( id );
            fc.add( feature );
        }
        

        DefaultMapModule<?> mapModule = owner.getAssignedMapModule();
        ToolState ts = mapModule.getMapTool().getState();
        setDeleteFeatureState();
        // In this case a feature or a number of features has been changed. Because all
        // changes will be done to a copy of a feature to underlying data access layer
        // must be updated explicitly.
        EditState es = (EditState) mapModule.getMapTool().getState();
        try {
            Command command = es.createCommand( dataAccess.get( 0 ), fc );
            appContainer.getCommandProcessor().executeSychronously( command, true );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null,
                                             Messages.getMessage( Locale.getDefault(), "$MD10289", e.getMessage() ),
                                             Messages.getMessage( Locale.getDefault(), "$MD10290" ) );
        }
        // reset previous state
        mapModule.getMapTool().setState( ts );        
    }

    /**
     * Puts the mapModule associated into the DeleteFeatureState.
     * 
     * @returns the CreateFeatureState object
     */
    private void setDeleteFeatureState() {
        DefaultMapModule<?> mapModule = owner.getAssignedMapModule();
        MapTool<?> mapTool = mapModule.getMapTool();
        mapTool.setEditState();
        EditState editState = (EditState) mapTool.getState();
        editState.setDeleteFeatureState();
    }

    /**
     * groups selected polygons into a MultiSurface
     * 
     */
    protected void groupPolygons() {
        groupFeatures( Surface.class, MultiSurface.class );
    }

    /**
     * groups selected lines into a MultiCurve
     * 
     */
    protected void groupLines() {
        groupFeatures( Curve.class, MultiCurve.class );
    }

    /**
     * groups selected points into a MultiPoint
     * 
     */
    protected void groupPoints() {
        groupFeatures( Point.class, MultiPoint.class );
    }

    private void groupFeatures( Class<?>... classes ) {
        MapModel mm = appContainer.getMapModel( null );
        List<Layer> layList = mm.getLayersSelectedForAction( MapModel.SELECTION_EDITING );
        Layer layer = layList.get( 0 );

        String fid = owner.getEditFeature().getCurrentFeature().getId();
        List<DataAccessAdapter> dataAccess = layer.getDataAccess();

        FeatureCollection tmp = layer.getSelectedFeatures();
        if ( tmp.size() < 2 ) {
            DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null,
                                             Messages.getMessage( Locale.getDefault(), "$MD10286" ),
                                             Messages.getMessage( Locale.getDefault(), "$MD10285" ) );
            return;
        }

        for ( DataAccessAdapter adapter : dataAccess ) {
            Command command = new GroupFeatureCommand( adapter, tmp, null, fid, classes );
            try {
                appContainer.getCommandProcessor().executeSychronously( command, true );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null, e.getMessage(),
                                                 Messages.getMessage( Locale.getDefault(), "$MD10285" ) );
            }
            FeatureCollection fc = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(), 1 );
            fc.add( (Feature) command.getResult() );
            owner.getEditFeature().setFeature( layer, fc );
            owner.getDigitizerPanel().setSelectedFeatures( fc );
            owner.getDigitizerPanel().invalidate();
            owner.getDigitizerPanel().repaint();
        }
    }

    /**
     * ungroups a multi polygon; for each polygon one feature will be created
     * 
     */
    protected void ungroupMultiPolygon() {
        ungroupFeatures( );
    }

    /**
     * ungroups a multi curve/linestring; for each curve/linestring one feature will be created
     * 
     */
    protected void ungroupMultiCurve() {
        ungroupFeatures( );
    }

    /**
     * ungroups a multi point; for each point one feature will be created
     * 
     */
    protected void ungroupMultiPoint() {
        ungroupFeatures( );
    }

    private void ungroupFeatures() {
        MapModel mm = appContainer.getMapModel( null );
        List<Layer> layList = mm.getLayersSelectedForAction( MapModel.SELECTION_EDITING );
        Layer layer = layList.get( 0 );

        List<DataAccessAdapter> dataAccess = layer.getDataAccess();

        FeatureCollection tmp = layer.getSelectedFeatures();
        if ( tmp.size() != 1 ) {
            DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null,
                                             Messages.getMessage( Locale.getDefault(), "$MD10288" ),
                                             Messages.getMessage( Locale.getDefault(), "$MD10287" ) );
            return;
        }

        for ( DataAccessAdapter adapter : dataAccess ) {
            Command command = new UngroupFeatureCommand( adapter, tmp.getFeature( 0 ), null );
            try {
                appContainer.getCommandProcessor().executeSychronously( command, true );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null, e.getMessage(),
                                                 Messages.getMessage( Locale.getDefault(), "$MD10285" ) );
            }
            FeatureCollection fc = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(), 1 );
            fc = (FeatureCollection) command.getResult();
            owner.getEditFeature().setFeature( layer, fc );
            owner.getDigitizerPanel().setSelectedFeatures( fc );
            owner.getDigitizerPanel().invalidate();
            owner.getDigitizerPanel().repaint();
        }
    }

    /**
     * unites two or more polygons into one. The result is one feature having one polygon as geometry. This is the
     * difference groupXXX where the result will be a MultiGeometry
     */
    protected void uniteGeometries() {
        MapModel mm = appContainer.getMapModel( null );
        List<Layer> layList = mm.getLayersSelectedForAction( MapModel.SELECTION_EDITING );
        Layer layer = layList.get( 0 );

        String fid = owner.getEditFeature().getCurrentFeature().getId();
        List<DataAccessAdapter> dataAccess = layer.getDataAccess();

        FeatureCollection tmp = layer.getSelectedFeatures();
        if ( tmp.size() < 2 ) {
            DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null,
                                             Messages.getMessage( Locale.getDefault(), "$MD10535" ),
                                             Messages.getMessage( Locale.getDefault(), "$MD10536" ) );
            return;
        }

        for ( DataAccessAdapter adapter : dataAccess ) {
            Command command1 = new UniteGeometriesCommand( adapter, tmp, null, fid );
            Command command2 = new UnselectFeaturesCommand( layer, false );
            CommandList command = new CommandList();
            command.add( command1 );
            command.add( command2 );

            try {
                appContainer.getCommandProcessor().executeSychronously( command, true );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null, e.getMessage(),
                                                 Messages.getMessage( Locale.getDefault(), "$MD10537" ) );
            }

            FeatureCollection fc = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(), 1 );
            fc.add( (Feature) command1.getResult() );
            owner.getEditFeature().setFeature( layer, fc );
            owner.getDigitizerPanel().invalidate();
            owner.getDigitizerPanel().repaint();
        }
    }

    /**
     * 
     * @param parameter
     */
    protected void createParallel( Map<String, Object> parameter ) {
        double distance = ( (Number) parameter.get( "distance" ) ).doubleValue();
        Layer layer = appContainer.getMapModel( null ).getLayersSelectedForAction( MapModel.SELECTION_EDITING ).get( 0 );
        FeatureCollection fc = layer.getSelectedFeatures();
        List<Feature> list = new ArrayList<Feature>( fc.size() * 2 );
        Iterator<Feature> iter = fc.iterator();
        while ( iter.hasNext() ) {
            Feature feature = (Feature) iter.next();
            if ( (Boolean) parameter.get( "right" ) ) {
                Feature tmp = createParallelFeature( distance, feature );
                if ( tmp != null ) {
                    list.add( tmp );
                }
            }
            if ( (Boolean) parameter.get( "left" ) ) {
                Feature tmp = createParallelFeature( -1 * distance, feature );
                if ( tmp != null ) {
                    list.add( tmp );
                }
            }
        }
        if ( list.size() > 0 ) {
            Feature[] features = list.toArray( new Feature[list.size()] );
            InsertFeatureCommand ifc = new InsertFeatureCommand( layer.getDataAccess().get( 0 ), features );
            try {
                appContainer.getCommandProcessor().executeSychronously( ifc, true );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11236" ),
                                               Messages.get( "$MD11237" ), e );
            }
        }

    }

    private Feature createParallelFeature( double distance, Feature feature ) {
        try {
            feature = (Feature) feature.cloneDeep();
            FeatureProperty[] geomProps = collectGeometryProperties( feature );
            for ( int i = 0; i < geomProps.length; i++ ) {
                if ( geomProps[i].getValue() instanceof Curve ) {
                    Curve curve = (Curve) geomProps[i].getValue();
                    geomProps[i].setValue( GeometryUtils.createCurveParallel( distance, curve ) );
                    feature.setProperty( geomProps[i], i );
                } else if ( geomProps[i].getValue() instanceof MultiCurve ) {
                    Curve[] curves = ( (MultiCurve) geomProps[i].getValue() ).getAllCurves();
                    for ( int j = 0; j < curves.length; j++ ) {
                        curves[j] = GeometryUtils.createCurveParallel( distance, curves[j] );
                        geomProps[i].setValue( GeometryFactory.createMultiCurve( curves ) );
                        feature.setProperty( geomProps[i], i );
                    }
                } else {
                    LOG.logWarning( "just for curves and multi curves parallels can be computed" );
                }
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            feature = null;
            DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11262" ),
                                           Messages.get( "$MD11263" ), e );
        }
        return feature;
    }

    protected FeatureProperty[] collectGeometryProperties( Feature feature ) {
        List<FeatureProperty> list = new ArrayList<FeatureProperty>( 10 );
        PropertyType[] pt = feature.getFeatureType().getProperties();
        for ( PropertyType type : pt ) {
            if ( type instanceof GeometryPropertyType ) {
                FeatureProperty[] tmp = feature.getProperties( type.getName() );
                for ( FeatureProperty property : tmp ) {
                    list.add( property );
                }
            }
        }
        return list.toArray( new FeatureProperty[list.size()] );
    }

    /**
     * move selected objects by defined distance in x- and y-direction
     * 
     * @param parameter
     */
    @SuppressWarnings("unchecked")
    public void moveByDistance( Map<String, Object> parameter ) {
        Pair<Double, Double> pair = (Pair<Double, Double>) parameter.get( "distance" );
        double[] d = new double[] { pair.first, pair.second };
        Layer layer = appContainer.getMapModel( null ).getLayersSelectedForAction( MapModel.SELECTION_EDITING ).get( 0 );
        FeatureCollection fc = layer.getSelectedFeatures();
        MoveFeatureCommand command = new MoveFeatureCommand( fc, null, d );
        try {
            appContainer.getCommandProcessor().executeSychronously( command, true );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11260" ),
                                           Messages.get( "$MD11261", pair.first, pair.second ), e );
        }
    }

    /**
     * performs a command for cutting holes into a surface by selecting overlapping surfaces
     */
    public void cutPolygonHole() {
        Layer layer = getSelectedLayer( MapModel.SELECTION_EDITING );
        List<DataAccessAdapter> dataAccess = layer.getDataAccess();
        DefaultMapModule<?> mapModule = owner.getAssignedMapModule();
        MapTool<?> mapTool = mapModule.getMapTool();
        ToolState ts = mapTool.getState();
        mapTool.setEditState();
        EditState editState = (EditState) mapTool.getState();
        editState.setCuttingPolygonHoleState();
        EditState es = (EditState) mapModule.getMapTool().getState();
        CuttingPolygonHoleCommand command = null;
        try {
            command = (CuttingPolygonHoleCommand) es.createCommand( dataAccess.get( 0 ), null );
            appContainer.getCommandProcessor().executeSychronously( command, true );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null,
                                             Messages.getMessage( Locale.getDefault(), "$MD11606", e.getMessage() ),
                                             Messages.getMessage( Locale.getDefault(), "$MD11607" ) );
            return;
        }
        // reset previous state
        mapModule.getMapTool().setState( ts );
        layer.unselectAllFeatures();
        owner.getDigitizerPanel().resetAll();
        FeatureCollection fc = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(),
                                                                       new Feature[] { (Feature) command.getResult() } );
        owner.getEditFeature().setFeature( layer, fc );
    }

}
