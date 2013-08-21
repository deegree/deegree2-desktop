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

package org.deegree.desktop.modules;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.ActiveMapModelChanged;
import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.commands.UnselectFeaturesCommand;
import org.deegree.desktop.dataadapter.DataAccessAdapter;
import org.deegree.desktop.dataadapter.FeatureAdapter;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.modules.ActionDescription.ACTIONTYPE;
import org.deegree.desktop.state.StateException;
import org.deegree.desktop.state.mapstate.EditState;
import org.deegree.desktop.state.mapstate.MapTool;
import org.deegree.desktop.state.mapstate.SelectState;
import org.deegree.desktop.state.mapstate.ToolState;
import org.deegree.desktop.state.mapstate.EditState.DrawPolygonHoleState;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.desktop.views.DigitizerFunctionSelect;
import org.deegree.desktop.views.swing.CursorRegistry;
import org.deegree.desktop.views.swing.digitize.DigitizerFunctionSelectPanel;
import org.deegree.desktop.views.swing.digitize.DigitizerPanel;
import org.deegree.desktop.views.swing.map.DefaultMapComponent;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.Pair;
import org.deegree.desktop.config.ModuleType;
import org.deegree.desktop.config._ComponentPositionType;
import org.deegree.kernel.Command;
import org.deegree.kernel.CommandProcessor;
import org.deegree.model.Identifier;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.spatialschema.Geometry;

/**
 * Module class providing digitizing and editing functionalities. Because this is one of the most complex modules of
 * iGeoDesktop assigned classes are packed in their own package. Access to available functionalities are provided by
 * {@link DigitizerFunctionSelectPanel}. Beside the usual options it provides access to validation utilities and options
 * that are important for digitizing actions.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class DigitizerModule<T> extends DefaultModule<T> {

    private static final ILogger LOG = LoggerFactory.getLogger( DigitizerModule.class );

    /**
     * key name for storing angle between two segments to be used for digitizing polygons and lines in
     * {@link ApplicationContainer} instance settings
     */
    public static final String ANGLE = "digitizer.segment.angle";

    /**
     * key name for storing length of segments to be used for digitizing polygons and lines in
     * {@link ApplicationContainer} instance settings
     */
    public static final String LENGTH = "digitizer.segment.length";

    private DefaultMapModule<Container> mapModule;

    private DigitizerPanel digitizerPanel;

    private DigitizerFunctionSelect dfs;

    private EditFeature eff;

    private String currentAction;

    private DigitizeActionHandler actionHandler;

    private Container jco;

    private Pair<Object, String> onActionFinished;

    // list of digitized/edited/selected points. If e.g. a multi surface or a surface
    // with holes has been edited, each ring will be available as a list of points
    private List<Geometry> geometries = new ArrayList<Geometry>();

    static {
        ActionDescription actionDescription = new ActionDescription(
                                                                     "open",
                                                                     "opens a dialog enabling selection of digitizing functions",
                                                                     null, "open digitize dialog",
                                                                     ACTIONTYPE.PushButton, null, null );
        moduleCapabilities = new ModuleCapabilities( actionDescription );
    }

    @SuppressWarnings("unchecked")
    @Override
    public void init( ModuleType moduleType, _ComponentPositionType componentPosition, ApplicationContainer<T> appCont,
                      IModule<T> parent, Map<String, String> initParams ) {
        super.init( moduleType, componentPosition, appCont, parent, initParams );

        mapModule = getAssignedMapModule();
        actionHandler = new DigitizeActionHandler( (DigitizerModule<Container>) this );
        if ( mapModule == null ) {
            LOG.logInfo( "no map module assigned to DigitizerModule" );
            DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null,
                                             Messages.getMessage( Locale.getDefault(), "$MD10197" ),
                                             Messages.getMessage( Locale.getDefault(), "$MD10198" ) );
            return;
        }
    }

    /**
     * opens the modules view
     * 
     */
    public void open() {
        List<Layer> list = appContainer.getMapModel( null ).getLayersSelectedForAction( MapModel.SELECTION_EDITING );
        if ( list.size() == 0 ) {
            DialogFactory.openErrorDialog( "application", null, Messages.get( "$MD11189" ), Messages.get( "$MD11190" ) );
            return;
        }
        appContainer.setInstanceSetting( DigitizerModule.LENGTH, -1 );
        appContainer.setInstanceSetting( DigitizerModule.ANGLE, -1 );
        mapModule = getAssignedMapModule();
        addDigitizerPanel();
        this.componentStateAdapter.setClosed( false );
        createIView();
    }

    /**
     * removes all drawing panes from the module
     */
    public void clear() {
        super.clear();
        // reset all allocated resources
        getEditFeatureState().resetState();
        mapModule.getMapTool().resetState();
        MapModel mm = appContainer.getMapModel( null );
        List<Layer> list = mm.getLayersSelectedForAction( MapModel.SELECTION_EDITING );
        for ( Layer layer : list ) {
            layer.unselectAllFeatures();
        }
        if ( geometries != null ) {
            geometries.clear();
        }
        removeDigitizerPanel();
        if ( eff != null ) {
            eff.dispose();
        }
        currentAction = null;
        digitizerPanel = null;
        // actionHandler = null;
        eff = null;
        dfs = null;
        jco = null;

    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        super.actionPerformed( e );
        if ( e instanceof ActiveMapModelChanged ) {
            try {
                if ( dfs != null ) {
                    Method m = dfs.getClass().getMethod( "dispose", new Class<?>[0] );
                    if ( m != null ) {
                        m.invoke( dfs, new Object[0] );
                    }
                }
            } catch ( Exception e1 ) {
                e1.printStackTrace();
            }
            clear();
        }
    }

    /**
     * 
     * @return current JPanel used for drawing
     */
    DigitizerPanel getDigitizerPanel() {
        return digitizerPanel;
    }

    /**
     * 
     * @return map module used for digitizing
     */
    public DefaultMapModule<?> getMapModule() {
        return mapModule;
    }

    /**
     * 
     * @return
     */
    EditFeature getEditFeature() {
        return eff;
    }

    /**
     * 
     * @return list of geometries resulting from last digitizing action
     */
    public List<Geometry> getGeometries() {
        return geometries;
    }

    /**
     * activates the module for performing the passed action
     * 
     * @param action
     */
    public void setDigitizingAction( String action ) {
        MapModel mm = appContainer.getMapModel( null );

        DefaultMapModule<?> mapMod = appContainer.getActiveMapModule();
        if ( mapMod != null ) {
            Component cmp = (Component) mapMod.getGUIContainer();
            cmp.requestFocus();
        }

        List<Layer> list = mm.getLayersSelectedForAction( MapModel.SELECTION_EDITING );

        if ( list.size() == 0 ) {
            this.currentAction = null;
            // a layer/featuretype must be selected if a digitizing/editing/selection
            // function has been selected
            dfs.unselectAll();
            DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null,
                                             Messages.getMessage( Locale.getDefault(), "$MD10193" ),
                                             Messages.getMessage( Locale.getDefault(), "$MD10194" ) );
        } else {
            if ( "select2ForEdit".equals( action ) || "selectForInsert".equals( action ) ) {
                removeDigitizerPanel();
                addDigitizerPanel();
                unselectFeatures();
                digitizerPanel.resetAll();
                setSelectByRectangleState();
            } else if ( "drawPolygon".equals( action ) ) {
                resetDigitizerPane();
                setCreatePolygonState();
            } else if ( "drawLineString".equals( action ) ) {
                resetDigitizerPane();
                setCreateLineStringState();
            } else if ( "drawPoint".equals( action ) ) {
                resetDigitizerPane();
                setCreatePointState();
            } else if ( "drawRectangle".equals( action ) ) {
                resetDigitizerPane();
                setCreateRectangleState();
            } else if ( "drawSizedEllipse".equals( action ) ) {
                resetDigitizerPane();
                setCreateSizedEllipseState();
            } else if ( "drawSizedRectangle".equals( action ) ) {
                resetDigitizerPane();
                setCreateSizedRectangleState();
            } else if ( "drawCircle".equals( action ) ) {
                resetDigitizerPane();
                setCreateCircleState();
            } else if ( "drawPolygonHole".equals( action ) ) {
                try {
                    setDrawPolygonHoleState();
                } catch ( Exception e ) {
                    return;
                }
                digitizerPanel.resetDrawingPane();
                digitizerPanel.resetGeometries();
            } else if ( "update:moveFeature".equals( action ) ) {
                digitizerPanel.resetDrawingPane();
                setMoveFeatureState();
            } else if ( "update:moveVertex".equals( action ) ) {
                digitizerPanel.resetDrawingPane();
                setMoveVertexState();
            } else if ( "update:deleteVertex".equals( action ) ) {
                digitizerPanel.resetDrawingPane();
                setDeleteVertexState();
            } else if ( "update:mergeVertices".equals( action ) ) {
                digitizerPanel.resetDrawingPane();
                setMergeVerticesState();
            } else if ( "update:insertVertex".equals( action ) ) {
                digitizerPanel.resetDrawingPane();
                setInsertVertexState();
            } else if ( "splitPolygon".equals( action ) || "splitLine".equals( action ) ) {
                digitizerPanel.resetDrawingPane();
                setSplitFeatureState();
            } else if ( "drawPolygonByFillingHole".equals( action ) ) {
                digitizerPanel.resetDrawingPane();
                setPolygonByFillingHoleState();
            } else if ( "joinCurves".equals( action ) ) {
                resetDigitizerPane();
                setJoinCurvesState();
            } else if ( "drawArc".equals( action ) ) {
                resetDigitizerPane();
                setDrawArcState();
            } else {
                LOG.logWarning( "action not suppored yet: " + action );
            }
            // just set current action if setting state has worked
            this.currentAction = action;
            LOG.logInfo( "activated digitizing action: ", action );
        }
    }

    @SuppressWarnings("unchecked")
    private void unselectFeatures() {
        ApplicationContainer<Container> appCont = (ApplicationContainer<Container>) getApplicationContainer();
        CommandProcessor cp = appCont.getCommandProcessor();
        MapModel mm = appContainer.getMapModel( null );
        Command cmd = new UnselectFeaturesCommand(
                                                   mm.getLayersSelectedForAction( MapModel.SELECTION_EDITING ).get( 0 ),
                                                   false );
        try {
            cp.executeSychronously( cmd, true );
        } catch ( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * performs the passed action. It is assumed that for each action to be performed a method with the identical name
     * exists.
     * 
     * @param actionName
     *            name of the action to be performed
     */
    public void performDigitizingAction( String actionName ) {
        if ( actionName != null && actionName.length() > 0 ) {
            try {
                LOG.logDebug( "invoked class: ", this.getClass().getName() );
                LOG.logDebug( "invoked method: ", actionName );
                Method action = actionHandler.getClass().getDeclaredMethod( actionName, (Class[]) null );
                action.invoke( actionHandler, (Object[]) null );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
            }
        }
        digitizerPanel.resetGeometries();
    }

    /**
     * same as {@link #performDigitizingAction(String)} but additional parameters are passed as {@link HashMap}
     * 
     * @param actionName
     *            name of the action to be performed
     * @param parameter
     * 
     */
    public void performDigitizingAction( String actionName, Map<String, Object> parameter ) {
        if ( actionName != null && actionName.length() > 0 ) {
            try {
                LOG.logDebug( "invoked class: ", this.getClass().getName() );
                LOG.logDebug( "invoked method: ", actionName );
                Object[] params = new Object[] { parameter };
                Class<?>[] clzz = new Class[] { Map.class };
                Method action = actionHandler.getClass().getDeclaredMethod( actionName, clzz );
                action.invoke( actionHandler, params );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
            }
        }
        digitizerPanel.resetGeometries();
    }

    /**
     * call back method that should be invoke by a DigitizerPanel if action has been finished
     * 
     * @param geometries
     */
    public void mouseActionFinished( List<Geometry> geometries, int keyModifiers ) {
        LOG.logInfo( "digitizing action finished: ", currentAction );
        this.geometries.clear();
        for ( Geometry geometry : geometries ) {
            this.geometries.add( geometry );
        }

        Layer layer = getSelectedLayer( MapModel.SELECTION_EDITING );
        ToolState state = mapModule.getMapTool().getState();
        if ( layer != null ) {
            FeatureCollection fc = layer.getSelectedFeatures();
            if ( state instanceof SelectState ) {
                SelectState selSt = (SelectState) mapModule.getMapTool().getState();
                fc = selSt.handle( appContainer, geometries, layer, keyModifiers );
                openFeaturePropertiesPanel( fc );
                invokeOnActionFinished();
            } else if ( state instanceof EditState.CreateArcFeatureState || state instanceof EditState.JoinCurvesState ) {
                invokeOnActionFinished();
            } else if ( state instanceof EditState.CreateFeatureState && !( state instanceof DrawPolygonHoleState ) ) {
                // openFeaturePropertiesPanel();
                FeatureAdapter dataAccessAdapter = (FeatureAdapter) layer.getDataAccess().get( 0 );
                FeatureType ft = dataAccessAdapter.getSchema();
                LOG.logDebug( ft.getName().getFormattedString() );
                // if current state is n instance of CreateFeatureState the according command must
                // be created and executed. The execution of the command will be performed
                // synchronously because it does not takes a lot of time and so we can
                // avoid overhead resulting from asychronously processing
                EditState es = (EditState) mapModule.getMapTool().getState();
                try {
                    Feature feature = (Feature) dataAccessAdapter.getDefaultFeature( ft.getName() ).clone();
                    QualifiedName geomProperty = ft.getGeometryProperties()[0].getName();
                    // feature must be cloned to avoid inserting the same feature instance
                    // into a feature collection (FeatureAdapter) several times
                    Command command = es.createCommand( dataAccessAdapter, feature, geomProperty, geometries );
                    appContainer.getCommandProcessor().executeSychronously( command, true );
                } catch ( Exception e ) {
                    LOG.logError( e.getMessage(), e );
                    DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null,
                                                     Messages.getMessage( Locale.getDefault(), "$MD10265",
                                                                          e.getMessage() ),
                                                     Messages.getMessage( Locale.getDefault(), "$MD10266",
                                                                          currentAction ) );
                }
                layer.unselectAllFeatures();
                digitizerPanel.resetAll();
            } else if ( state instanceof EditState ) {
                EditState es = (EditState) mapModule.getMapTool().getState();
                fc = es.handle( appContainer, geometries, layer, keyModifiers );
                invokeOnActionFinished();
            }
            digitizerPanel.resetAll();
            digitizerPanel.setSelectedFeatures( fc );
            digitizerPanel.invalidate();
            digitizerPanel.repaint();

        }
        digitizerPanel.setCursor( CursorRegistry.DEFAULT_CURSOR );
    }

    private void invokeOnActionFinished() {
        if ( onActionFinished != null ) {
            Class<?> cl = onActionFinished.first.getClass();
            try {
                Method m = cl.getMethod( onActionFinished.second );
                m.invoke( onActionFinished.first );
            } catch ( Exception e ) {
                LOG.logError( e );
            }
            // onActionFinished = null;
        }
    }

    /**
     * call back method that should be invoke by an {@link EditFeature} instance if editing of a features properties has
     * been finished or be canceled
     * 
     * @param geomProperty
     * @param cancel
     * @param close
     */
    public void propertyEditingFinished( boolean cancel, boolean close ) {
        if ( !cancel ) {
            Layer layer = getSelectedLayer( MapModel.SELECTION_EDITING );
            DataAccessAdapter dataAccessAdapter = layer.getDataAccess().get( 0 );
            // retrieve edited feature instance
            FeatureCollection featureCollection = null;
            try {
                // will be thrown if a user enters invalid value for one of the
                // feature properties
                featureCollection = eff.getFeatureCollection();
            } catch ( Exception e ) {
                LOG.logError( e );
                return;
            }

            setUpdateAlphaNumericPropertiesState();

            ToolState ts = mapModule.getMapTool().getState();

            // Because it is possible to update a lot of features simultaneously the command
            // will be performed asynchronously
            EditState es = (EditState) ts;
            try {
                Command command = es.createCommand( dataAccessAdapter, featureCollection );
                appContainer.getCommandProcessor().executeSychronously( command, true );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                DialogFactory.openWarningDialog( appContainer.getViewPlatform(),
                                                 null,
                                                 Messages.getMessage( Locale.getDefault(), "$MD10289", e.getMessage() ),
                                                 Messages.getMessage( Locale.getDefault(), "$MD10290" ) );
            }
            digitizerPanel.setSelectedFeatures( featureCollection );

            if ( close ) {
                eff.dispose();
                digitizerPanel.resetAll();
                eff = null;
            }
        } else {
            digitizerPanel.resetAll();
            eff.dispose();
            eff = null;
        }
        digitizerPanel.setCursor( CursorRegistry.DEFAULT_CURSOR );
    }

    /**
     * 
     * @param onActionFinished
     *            first element is an instance of a class; second is the name of a method of this class that should be
     *            invoked mouse action or property editing finished
     */
    public void setOnActionFinished( Pair<Object, String> onActionFinished ) {
        this.onActionFinished = onActionFinished;
    }

    private void openFeaturePropertiesPanel( FeatureCollection featureCollection ) {

        if ( eff == null ) {
            Layer layer = getSelectedLayer( MapModel.SELECTION_EDITING );
            String vp = appContainer.getViewPlatform();
            eff = EditFeatureFormFactory.create( layer, featureCollection, vp, mapModule );
            eff.setDigitizerModule( this );
        } else {
            Layer layer = getSelectedLayer( MapModel.SELECTION_EDITING );
            eff.setFeature( layer, featureCollection );
        }
    }

    /**
     * Fetches and returns the assigned map module
     * 
     * @return the assigned map module
     */
    @SuppressWarnings("unchecked")
    DefaultMapModule<Container> getAssignedMapModule() {

        List<IModule<T>> list = this.appContainer.findModuleByName( "MapModule" );
        if ( list.size() == 0 ) {
            Identifier mapModuleID = new Identifier( getInitParameter( "mapModule" ) );
            return (DefaultMapModule<Container>) this.appContainer.findModuleByIdentifier( mapModuleID );
        } else if ( list.size() == 0 ) {
            return (DefaultMapModule<Container>) list.get( 0 );
        } else {
            for ( IModule<T> iModule : list ) {
                String t = iModule.getInitParameter( "assignedMapModel" );
                if ( t != null && t.equals( appContainer.getMapModel( null ).getIdentifier().getValue() ) ) {
                    return (DefaultMapModule<Container>) iModule;
                }
            }
            return (DefaultMapModule<Container>) list.get( 0 );
        }
    }

    /**
     * Creates a new digitizer panel
     */
    @SuppressWarnings("unchecked")
    private void createDigitizerPanel( IModule<Container> mapModule ) {
        MapModel mm = appContainer.getMapModel( null );
        digitizerPanel = new DigitizerPanel( (DigitizerModule<Container>) this, mapModule, mm );
    }

    /**
     * Creates the digitizer panel if it hasn't been created yet and adds it to the DefaultMapModule
     */
    private void addDigitizerPanel() {
        if ( digitizerPanel == null ) {
            createDigitizerPanel( mapModule );
            jco = mapModule.getMapContainer();
            jco.add( digitizerPanel, 0 );
            jco.addMouseListener( digitizerPanel.getMouseListener() );
            jco.addMouseMotionListener( digitizerPanel.getMouseMotionListener() );
            jco.addKeyListener( digitizerPanel.getKeyListener() );
        }
    }

    /**
     * reset all objects assigned to digitizing function
     */
    public void resetDigitizerPane() {
        if ( digitizerPanel == null ) {
            createDigitizerPanel( mapModule );
        } else {
            digitizerPanel.resetAll();
        }
        if ( dfs == null ) {
            dfs = (DigitizerFunctionSelect) getViewForm();
            if ( dfs != null ) {
                dfs.registerDigitizerModule( this );
            }
        }
        removeDigitizerPanel();
        addDigitizerPanel();
    }

    /**
     * resets the digitizer function select panel
     */
    public void resetFunctionSelect() {
        if ( dfs != null ) {
            dfs.unselectAll();
        }
    }

    /**
     * Creates the digitizer panel if it hasn't been created yet and adds it to the DefaultMapModule
     */
    private void removeDigitizerPanel() {

        if ( jco != null ) {
            MouseListener[] ml = jco.getMouseListeners();
            for ( MouseListener mouseListener : ml ) {
                if ( !( mouseListener instanceof DefaultMapComponent.DMCMouseListener ) ) {
                    // keep mouse motion listener of map component
                    jco.removeMouseListener( mouseListener );
                }
            }
            MouseMotionListener[] mml = jco.getMouseMotionListeners();
            for ( MouseMotionListener mouseMotionListener : mml ) {
                if ( !( mouseMotionListener instanceof DefaultMapComponent.DMCMouseMotionListener ) ) {
                    // keep mouse motion listener of map component
                    jco.removeMouseMotionListener( mouseMotionListener );
                }
            }
            Component[] comps = jco.getComponents();
            for ( int i = 0; i < comps.length; i++ ) {
                ml = comps[i].getMouseListeners();
                for ( MouseListener mouseListener : ml ) {
                    if ( !( mouseListener instanceof DefaultMapComponent.DMCMouseListener ) ) {
                        // keep mouse motion listener of map component
                        comps[i].removeMouseListener( mouseListener );
                    }
                }
                mml = comps[i].getMouseMotionListeners();
                for ( MouseMotionListener mouseMotionListener : mml ) {
                    if ( !( mouseMotionListener instanceof DefaultMapComponent.DMCMouseMotionListener ) ) {
                        // keep mouse motion listener of map component
                        comps[i].removeMouseMotionListener( mouseMotionListener );
                    }
                }
                if ( comps[i] instanceof DigitizerPanel ) {
                    jco.remove( comps[i] );
                    digitizerPanel = (DigitizerPanel) comps[i];
                    digitizerPanel.resetGeometries();

                    KeyListener[] kl = jco.getKeyListeners();
                    for ( KeyListener listener : kl ) {
                        if ( listener instanceof DigitizerPanel.DPKeyListener ) {
                            jco.removeKeyListener( listener );
                        }
                    }
                }
            }
        }
        digitizerPanel = null;
        mapModule.clear();
    }

    /**
     * Puts the mapModule associated into the CreateFeatureState.
     * 
     * @returns the CreateFeatureState object
     */
    protected EditState.CreateFeatureState getCreateFeatureState() {
        MapTool<T> mapTool = getEditFeatureState();
        EditState editState = (EditState) mapTool.getState();
        editState.setCreateFeatureState();
        return (EditState.CreateFeatureState) editState.getSubstate();
    }

    /**
     * Puts the mapModule associated into the UpdateFeatureState.
     * 
     * @returns the CreateFeatureState object
     */
    protected EditState.UpdateFeatureState getUpdateFeatureState() {
        MapTool<T> mapTool = getEditFeatureState();
        EditState editState = (EditState) mapTool.getState();
        editState.setUpdateFeatureState();
        return (EditState.UpdateFeatureState) editState.getSubstate();
    }

    /**
     * Puts the mapModule associated into the EditFeatureState.
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    private MapTool<T> getEditFeatureState() {
        MapTool<T> mapTool = (MapTool<T>) mapModule.getMapTool();
        mapTool.setEditState();
        return mapTool;
    }

    /**
     * Puts the mapModule associated with this DigitizerModule into the SelectState.
     * 
     * @returns the CreateFeatureState object
     */
    protected SelectState getSelectState() {
        MapTool<?> mapTool = mapModule.getMapTool();
        mapTool.setSelectState();
        return (SelectState) mapTool.getState();
    }

    /**
     * Lets the user create a point feature
     */
    private void setCreatePointState() {
        EditState.CreateFeatureState cfs = getCreateFeatureState();
        if ( cfs != null ) {
            cfs.setCreatePointFeatureState();
        }
        digitizerPanel.setCursor( CursorRegistry.DRAW_CURSOR );
    }

    /**
     * Lets the user create a line feature
     */
    private void setCreateLineStringState() {
        EditState.CreateFeatureState cfs = getCreateFeatureState();
        if ( cfs != null ) {
            cfs.setCreateCurveFeatureState();
        }
        digitizerPanel.setCursor( CursorRegistry.DRAW_CURSOR );
    }

    /**
     * Lets the user join two curves
     */
    private void setJoinCurvesState() {
        EditState.UpdateFeatureState ufs = getUpdateFeatureState();
        if ( ufs != null ) {
            ufs.setJoinCurveState();
        }
        digitizerPanel.setCursor( CursorRegistry.DRAW_CURSOR );
    }

    /**
     * Lets the user create a curve feature by drawing an arc
     */
    private void setDrawArcState() {
        EditState.UpdateFeatureState ufs = getUpdateFeatureState();
        if ( ufs != null ) {
            ufs.setCreateArcFeatureState();
        }
        digitizerPanel.setCursor( CursorRegistry.DRAW_CURSOR );
    }

    /**
     * Lets the user create a polygon feature
     */
    private void setCreatePolygonState() {
        EditState.CreateFeatureState cfs = getCreateFeatureState();
        if ( cfs != null ) {
            cfs.setCreatePolygonFeatureState();
        }
        digitizerPanel.setCursor( CursorRegistry.DRAW_CURSOR );
    }

    /**
     * Lets the user create a rectangle feature
     */
    private void setCreateRectangleState() {
        EditState.CreateFeatureState cfs = getCreateFeatureState();
        if ( cfs != null ) {
            cfs.setCreateRectangleFeatureState();
        }
        digitizerPanel.setCursor( CursorRegistry.DRAW_CURSOR );
    }

    /**
     * Lets the user create a sized rectangle feature
     */
    private void setCreateSizedRectangleState() {
        EditState.CreateFeatureState cfs = getCreateFeatureState();
        if ( cfs != null ) {
            cfs.setCreateSizedRectangleFeatureState();
        }
        digitizerPanel.setCursor( CursorRegistry.DRAW_CURSOR );
    }

    /**
     * Lets the user create a sized ellipse feature
     */
    private void setCreateSizedEllipseState() {
        EditState.CreateFeatureState cfs = getCreateFeatureState();
        if ( cfs != null ) {
            cfs.setCreateSizedEllipseFeatureState();
        }
        digitizerPanel.setCursor( CursorRegistry.DRAW_CURSOR );
    }

    /**
     * Lets the user create a circle feature
     */
    private void setCreateCircleState() {
        EditState.CreateFeatureState cfs = getCreateFeatureState();
        if ( cfs != null ) {
            cfs.setCreateCircleFeatureState();
        }
        digitizerPanel.setCursor( CursorRegistry.DRAW_CURSOR );
    }

    /**
     * 
     */
    private void setPolygonByFillingHoleState() {
        EditState.CreateFeatureState cfs = getCreateFeatureState();
        if ( cfs != null ) {
            cfs.setCreatePolygonByFillingHoleFeatureState();
        }
        digitizerPanel.setCursor( CursorRegistry.DRAW_CURSOR );
    }

    /**
     * Lets the user create a polygon feature
     */
    private void setMoveFeatureState() {
        EditState.UpdateFeatureState ufs = getUpdateFeatureState();
        if ( ufs != null ) {
            ufs.setMoveFeatureState();
        }
        digitizerPanel.setCursor( CursorRegistry.MOVE_CURSOR );
    }

    /**
     * Lets the user select a feature/geometry for editing
     */
    private void setSelectByRectangleState() {
        SelectState sfs = getSelectState();
        if ( sfs != null ) {
            sfs.setRectangleSelectState();
        }
        digitizerPanel.setCursor( CursorRegistry.SELECT_CURSOR );
    }

    /**
     * Lets the user select a feature/geometry for editing
     */
    private void setUpdateAlphaNumericPropertiesState() {
        EditState.UpdateFeatureState ufs = getUpdateFeatureState();
        if ( ufs != null ) {
            ufs.setUpdateAlphaNumericPropertiesStateState();
        }
    }

    /**
     * Puts the mapModule associated into the DrawPolygonHoleState.
     * 
     */
    private void setDrawPolygonHoleState()
                            throws StateException {
        if ( digitizerPanel == null || digitizerPanel.getSelectedFeatures() == null
             || digitizerPanel.getSelectedFeatures().size() == 0 ) {
            DialogFactory.openWarningDialog( appContainer.getViewPlatform(), digitizerPanel,
                                             "you first have to select a polygon", "digitize errorr" );
            throw new StateException( "can put to DrawPolygonHoleState because no feature is selected" );
        }
        MapTool<T> mapTool = getEditFeatureState();
        EditState editState = (EditState) mapTool.getState();
        editState.setDrawPolygonHoleState();
        digitizerPanel.setCursor( CursorRegistry.DRAW_CURSOR );
    }

    /**
     * Puts the mapModule associated into the MoveVertexState.
     * 
     */
    private void setMoveVertexState() {
        MapTool<T> mapTool = getEditFeatureState();
        EditState editState = (EditState) mapTool.getState();
        editState.setMoveVertexState();
        digitizerPanel.setCursor( CursorRegistry.DRAW_CURSOR );
    }

    /**
     * Puts the mapModule associated into the MoveVertexState.
     * 
     */
    private void setDeleteVertexState() {
        MapTool<T> mapTool = getEditFeatureState();
        EditState editState = (EditState) mapTool.getState();
        editState.setDeleteVertexState();
        digitizerPanel.setCursor( CursorRegistry.DELETE_CURSOR );
    }

    /**
     * Puts the mapModule associated into the MoveVertexState.
     * 
     */
    private void setMergeVerticesState() {
        MapTool<T> mapTool = getEditFeatureState();
        EditState editState = (EditState) mapTool.getState();
        editState.setMergeVerticesState();
        digitizerPanel.setCursor( CursorRegistry.DRAW_CURSOR );
    }

    /**
     * Puts the mapModule associated into the MoveVertexState.
     * 
     */
    private void setInsertVertexState() {
        MapTool<T> mapTool = getEditFeatureState();
        EditState editState = (EditState) mapTool.getState();
        editState.setInsertVertexState();
        digitizerPanel.setCursor( CursorRegistry.DRAW_CURSOR );
    }

    /**
     * Lets the user create a point feature
     */
    private void setSplitFeatureState() {
        MapTool<T> mapTool = getEditFeatureState();
        EditState editState = (EditState) mapTool.getState();
        editState.setSplitFeatureState();
        digitizerPanel.setCursor( CursorRegistry.DRAW_CURSOR );
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

}
