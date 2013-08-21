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

import static java.util.prefs.Preferences.userNodeForPackage;
import static org.deegree.desktop.i18n.Messages.get;
import static org.deegree.model.feature.FeatureFactory.createFeatureCollection;
import static org.deegree.model.spatialschema.GeometryFactory.createPoint;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.TransferHandler;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.commands.CommitDataChangesCommand;
import org.deegree.desktop.commands.ExportLayerCommand;
import org.deegree.desktop.commands.UnselectFeaturesCommand;
import org.deegree.desktop.commands.model.RepaintCommand;
import org.deegree.desktop.commands.model.ZoomCommand;
import org.deegree.desktop.dataadapter.DataAccessAdapter;
import org.deegree.desktop.dataadapter.FeatureAdapter;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.main.DeegreeDesktop;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.LayerGroup;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.mapmodel.MapModelVisitor;
import org.deegree.desktop.modules.ActionDescription.ACTIONTYPE;
import org.deegree.desktop.state.mapstate.HotlinkState;
import org.deegree.desktop.state.mapstate.MapTool;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.desktop.views.swing.linkeddata.LinkedDataDialog;
import org.deegree.desktop.views.swing.map.DefaultMapComponent;
import org.deegree.desktop.views.swing.map.ObjectInfoPanel;
import org.deegree.desktop.views.swing.map.SelectByAttributeDialog;
import org.deegree.desktop.views.swing.map.SelectPanel;
import org.deegree.desktop.views.swing.map.ZoomPanel;
import org.deegree.desktop.views.swing.util.GenericFileChooser;
import org.deegree.desktop.views.swing.util.DesktopFileFilter;
import org.deegree.desktop.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.CollectionUtils;
import org.deegree.framework.util.CollectionUtils.Mapper;
import org.deegree.framework.util.ImageUtils;
import org.deegree.framework.utils.MapTools;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.desktop.config.ModuleType;
import org.deegree.desktop.config.QualifiedNameType;
import org.deegree.desktop.config._ComponentPositionType;
import org.deegree.kernel.Command;
import org.deegree.kernel.CommandProcessedEvent;
import org.deegree.kernel.CommandProcessedListener;
import org.deegree.model.Identifier;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Point;

/**
 * The <code>DefaultMapModule</code> is the specific module definition of a map module. It handles all generic events
 * belonging to a map - zoomIn, zoomOut, Pan, Center.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * @param <T>
 * 
 */
public class DefaultMapModule<T> extends DefaultModule<T> implements CommandProcessedListener {

    private static final ILogger LOG = LoggerFactory.getLogger( DefaultMapModule.class );

    protected MapTool<T> mapTool;

    private List<Envelope> history = new ArrayList<Envelope>( 100 );

    private int historyIndex = 0;

    private List<Identifier> historyIdList = new ArrayList<Identifier>();

    private Container jco;

    static {
        initCapabilities();
    }

    private static final Mapper<QualifiedName, QualifiedNameType> toqualifiedname = new Mapper<QualifiedName, QualifiedNameType>() {
        public QualifiedName apply( QualifiedNameType u ) {
            try {
                return new QualifiedName( u.getPrefix(), u.getLocalName(), new URI( u.getNamespace() ) );
            } catch ( URISyntaxException e ) {
                LOG.logError( "Unknown error", e );
                return null;
            }
        }

    };

    private static void initCapabilities() {
        ActionDescription ad1 = new ActionDescription(
                                                       "center",
                                                       "puts application into center mode; a click into current map will center map model on click point",
                                                       null, "puts application into center mode",
                                                       ACTIONTYPE.ToggleButton, null, null );
        ActionDescription ad2 = new ActionDescription( "pan", "puts application into center mode", null,
                                                       "puts application into center mode", ACTIONTYPE.ToggleButton,
                                                       null, null );
        ActionDescription ad3 = new ActionDescription( "zoomIn", "puts application into zoom in mode", null,
                                                       "puts application into zoom in mode", ACTIONTYPE.ToggleButton,
                                                       null, null );
        ActionDescription ad4 = new ActionDescription( "zoomOut", "puts application into zoom out mode", null,
                                                       "puts application into zoom out mode", ACTIONTYPE.ToggleButton,
                                                       null, null );
        ActionDescription ad5 = new ActionDescription( "zoomToFullExtent",
                                                       "zoom to maximum extent as defined in current project", null,
                                                       "zoom to maximum extent", ACTIONTYPE.PushButton, null, null );
        ActionDescription ad6 = new ActionDescription( "zoomToSelectedFeatures", "zoom to extent of selected features",
                                                       null, "zoom to extent of selected features",
                                                       ACTIONTYPE.PushButton, null, null );
        ActionDescription ad7 = new ActionDescription( "zoomToSelectedLayers", "zoom to extent of selected layers",
                                                       null, "zoom to extent of selected layers",
                                                       ACTIONTYPE.PushButton, null, null );
        ActionDescription ad8 = new ActionDescription(
                                                       "synchronizeMapModels",
                                                       "sets extents of all map models to the extent of the selected one",
                                                       null, "synchronizes extent of map models",
                                                       ACTIONTYPE.PushButton, null, null );
        ActionDescription ad9 = new ActionDescription( "historyForward", "set extent to next one", null,
                                                       "set extent to next one", ACTIONTYPE.PushButton, null, null );
        ActionDescription ad10 = new ActionDescription( "historyBackward", "set extent to previous one", null,
                                                        "set extent to previous one", ACTIONTYPE.PushButton, null, null );
        ActionDescription ad11 = new ActionDescription( "objectInfo", "puts application into objectInfo state", null,
                                                        "puts application into objectInfo state",
                                                        ACTIONTYPE.ToggleButton, null, null );
        ActionDescription ad12 = new ActionDescription(
                                                        "exportLayer",
                                                        "exports selected layer depending on contained datatypes as file",
                                                        null, "exports selected layer", ACTIONTYPE.PushButton, null,
                                                        null );
        ActionDescription ad13 = new ActionDescription(
                                                        "commitAll",
                                                        "writes changes for all layers performed to geometries and/or properties into according backend",
                                                        null, "commits all changes to backends", ACTIONTYPE.PushButton,
                                                        null, null );
        ActionDescription ad14 = new ActionDescription(
                                                        "commitSelected",
                                                        "writes changes for selected layer performed to geometries and/or properties into according backend",
                                                        null, "commits changes for selected layer to backend",
                                                        ACTIONTYPE.PushButton, null, null );
        ActionDescription ad15 = new ActionDescription( "exportAsImage", "exports current map as image into a file",
                                                        null, "exports current map as image", ACTIONTYPE.PushButton,
                                                        null, null );
        ActionDescription ad16 = new ActionDescription( "exportToClipBoard",
                                                        "copies current map as png image into clip board", null,
                                                        "copies current map as image into clip board",
                                                        ACTIONTYPE.PushButton, null, null );
        moduleCapabilities = new ModuleCapabilities( ad1, ad2, ad3, ad4, ad5, ad6, ad7, ad8, ad9, ad10, ad11, ad12,
                                                     ad13, ad14, ad15, ad16 );
    }

    @Override
    public void init( ModuleType moduleType, _ComponentPositionType componentPosition, ApplicationContainer<T> appCont,
                      IModule<T> parent, Map<String, String> initParams ) {
        super.init( moduleType, componentPosition, appCont, parent, initParams );

        // register at CommandProcessor to be informed about changes of the maps
        // envelope. This is required to enable managing of the history of map
        // envelopes.
        appContainer.getCommandProcessor().addCommandProcessedListener( this );
        mapTool = new MapTool<T>( appCont );

        if ( getViewForm() instanceof JFrame ) {
            JFrame viewForm = (JFrame) getViewForm();
            Component[] components = viewForm.getContentPane().getComponents();
            for ( int i = 0; i < components.length; i++ ) {
                if ( components[i] instanceof DefaultMapComponent ) {
                    jco = (Container) components[i];
                    break;
                }
            }
            if ( jco == null ) {
                LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10072" ) );
                return;
            }
        } else if ( getViewForm() instanceof JInternalFrame ) {
            JInternalFrame viewForm = (JInternalFrame) getViewForm();
            Component[] components = viewForm.getContentPane().getComponents();
            for ( int i = 0; i < components.length; i++ ) {
                if ( components[i] instanceof DefaultMapComponent ) {
                    jco = (Container) components[i];
                    break;
                }
            }
            if ( jco == null ) {
                LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10072" ) );
                return;
            }
        } else {
            jco = (Container) getViewForm();
        }

    }

    /**
     * 
     * @return assigned maptool
     */
    public MapTool<T> getMapTool() {
        return mapTool;
    }

    private void removeControlPanels() {

        try {
            String mmId = getInitParameter( "assignedMapModel" );
            MapModel mm = appContainer.getMapModel( new Identifier( mmId ) );
            Command cmd = new UnselectFeaturesCommand( mm, false );
            appContainer.getCommandProcessor().executeSychronously( cmd, true );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
        }

        if ( jco != null ) {
            Component[] comps = jco.getComponents();

            for ( int i = 0; i < comps.length; i++ ) {

                MouseListener[] ml = comps[i].getMouseListeners();
                for ( MouseListener mouseListener : ml ) {
                    if ( !( mouseListener instanceof DefaultMapComponent.DMCMouseListener ) ) {
                        // keep mouse listener of map component
                        comps[i].removeMouseListener( mouseListener );
                    }
                }
                MouseMotionListener[] mml = comps[i].getMouseMotionListeners();
                for ( MouseMotionListener mouseMotionListener : mml ) {
                    if ( !( mouseMotionListener instanceof DefaultMapComponent.DMCMouseMotionListener ) ) {
                        // keep mouse motion listener of map component
                        comps[i].removeMouseMotionListener( mouseMotionListener );
                    }
                }

                if ( comps[i] instanceof JPanel ) {
                    jco.remove( comps[i] );
                }
                if ( !( getViewForm() instanceof JFrame ) ) {
                    if ( comps[i] instanceof MouseListener ) {
                        jco.removeMouseListener( (MouseListener) comps[i] );
                    }
                    if ( comps[i] instanceof MouseMotionListener ) {
                        jco.removeMouseMotionListener( (MouseMotionListener) comps[i] );
                    }
                }
            }
        }
    }

    @Override
    public void clear() {
        removeControlPanels();
        update();
    }

    /**
     * sets map/module into select state
     * 
     */
    public void select() {
        mapTool.setSelectState();
        removeControlPanels();
        if ( getViewForm() instanceof Container ) {
            String mmId = getInitParameter( "assignedMapModel" );
            MapModel mapModel = appContainer.getMapModel( new Identifier( mmId ) );
            SelectPanel<T> sp = new SelectPanel<T>( this, mapTool, jco );
            sp.setBounds( 0, 0, mapModel.getTargetDevice().getPixelWidth(), mapModel.getTargetDevice().getPixelHeight() );
            sp.setBackground( new Color( 255, 255, 255, 0 ) );
            sp.setVisible( true );
        }
    }

    public void selectByAttributes() {
        String mmId = getInitParameter( "assignedMapModel" );
        MapModel mapModel = appContainer.getMapModel( new Identifier( mmId ) );
        if ( "application".equalsIgnoreCase( appContainer.getViewPlatform() ) ) {
            new SelectByAttributeDialog( mapModel );
        }
    }

    /**
     * sets map/module into zoomin state
     * 
     */
    @SuppressWarnings("unchecked")
    public void zoomIn() {
        mapTool.setZoomInState( "zoomIn" );
        removeControlPanels();
        if ( getViewForm() instanceof Container ) {
            String mmId = getInitParameter( "assignedMapModel" );
            MapModel mapModel = appContainer.getMapModel( new Identifier( mmId ) );
            ZoomPanel sp = new ZoomPanel( (IModule<Container>) this, (MapTool<Container>) mapTool, jco );
            sp.setBounds( 0, 0, mapModel.getTargetDevice().getPixelWidth(), mapModel.getTargetDevice().getPixelHeight() );
            sp.setBackground( new Color( 255, 255, 255, 0 ) );
            sp.setVisible( true );
        }
    }

    /**
     * sets map/module into zoomout state
     * 
     */
    @SuppressWarnings("unchecked")
    public void zoomOut() {
        mapTool.setZoomOutState( "zoomOut" );
        removeControlPanels();
        if ( getViewForm() instanceof Container ) {
            String mmId = getInitParameter( "assignedMapModel" );
            MapModel mapModel = appContainer.getMapModel( new Identifier( mmId ) );
            ZoomPanel sp = new ZoomPanel( (IModule<Container>) this, (MapTool<Container>) mapTool, jco );
            sp.setBounds( 0, 0, mapModel.getTargetDevice().getPixelWidth(), mapModel.getTargetDevice().getPixelHeight() );
            sp.setBackground( new Color( 255, 255, 255, 0 ) );
            sp.setVisible( true );
        }
    }

    /**
     * sets map/module into pan state
     * 
     */
    @SuppressWarnings("unchecked")
    public void pan() {
        mapTool.setPanState( "pan" );
        removeControlPanels();
        if ( getViewForm() instanceof Container ) {
            String mmId = getInitParameter( "assignedMapModel" );
            MapModel mapModel = appContainer.getMapModel( new Identifier( mmId ) );
            ZoomPanel sp = new ZoomPanel( (IModule<Container>) this, (MapTool<Container>) mapTool, jco );
            sp.setBounds( 0, 0, mapModel.getTargetDevice().getPixelWidth(), mapModel.getTargetDevice().getPixelHeight() );
            sp.setBackground( new Color( 255, 255, 255, 0 ) );
            sp.setVisible( true );
        }
    }

    /**
     * sets map/module into re-center state
     * 
     */
    @SuppressWarnings("unchecked")
    public void center() {
        mapTool.setCenterState( "center" );
        removeControlPanels();
        if ( getViewForm() instanceof Container ) {
            String mmId = getInitParameter( "assignedMapModel" );
            MapModel mapModel = appContainer.getMapModel( new Identifier( mmId ) );
            ZoomPanel sp = new ZoomPanel( (IModule<Container>) this, (MapTool<Container>) mapTool, jco );
            sp.setBounds( 0, 0, mapModel.getTargetDevice().getPixelWidth(), mapModel.getTargetDevice().getPixelHeight() );
            sp.setBackground( new Color( 255, 255, 255, 0 ) );
            sp.setVisible( true );
        }
    }

    /**
     * sets map/module into object info state
     * 
     */
    @SuppressWarnings("unchecked")
    public void objectInfo() {
        mapTool.setInfoState( "objectInfo" );
        removeControlPanels();
        if ( getViewForm() instanceof Container ) {
            String mmId = getInitParameter( "assignedMapModel" );
            MapModel mapModel = appContainer.getMapModel( new Identifier( mmId ) );
            List<Layer> layers = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
            if ( layers.size() == 0 ) {
                DialogFactory.openWarningDialog( mapModel.getApplicationContainer().getViewPlatform(), null,
                                                 Messages.get( "$MD11402" ), Messages.get( "$MD11403" ) );
            } else if ( !layers.get( 0 ).isQueryable() ) {
                DialogFactory.openWarningDialog( mapModel.getApplicationContainer().getViewPlatform(), null,
                                                 Messages.get( "$MD11400", layers.get( 0 ).getTitle() ),
                                                 Messages.get( "$MD11401" ) );
            } else {
                ObjectInfoPanel oi = new ObjectInfoPanel( (IModule<Container>) this, (MapTool<Container>) mapTool, jco );
                oi.setBounds( 0, 0, mapModel.getTargetDevice().getPixelWidth(),
                              mapModel.getTargetDevice().getPixelHeight() );
                oi.setBackground( new Color( 255, 255, 255, 0 ) );
                oi.setVisible( true );
            }
        }
    }

    public void repaint() {
        if ( getViewForm() instanceof Container ) {
            MapModel mapModel = getMapModuleIfPassive();
            if ( mapModel != null ) {
                RepaintCommand repaintCommand = new RepaintCommand( mapModel );
                try {
                    appContainer.getCommandProcessor().executeSychronously( repaintCommand, true );
                } catch ( Exception e ) {
                    LOG.logWarning( "Repaint failed: " + e.getMessage() );
                }
            }
        }
    }

    private MapModel getMapModuleIfPassive() {
        String mmId = getInitParameter( "assignedMapModel" );
        MapModel mapModel = appContainer.getMapModel( new Identifier( mmId ) );
        MapModel temp = appContainer.getMapModel( null );
        // ensure that just the current model will be returned only if it is not the active one
        if ( mapModel != temp ) {
            return mapModel;
        }
        return null;
    }

    /**
     * zooms map to its full extent
     * 
     */
    public void zoomToFullExtent() {
        String mmId = getInitParameter( "assignedMapModel" );
        MapModel mapModel = appContainer.getMapModel( new Identifier( mmId ) );
        Envelope env = mapModel.getMaxExtent();

        ZoomCommand command = new ZoomCommand( mapModel );
        int w = mapModel.getTargetDevice().getPixelWidth();
        int h = mapModel.getTargetDevice().getPixelHeight();
        command.setZoomBox( env, w, h );
        try {
            appContainer.getCommandProcessor().executeSychronously( command, true );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11220" ),
                                           Messages.get( "$MD11221", env ), e );
        }

        clearHistoryForwarding();
    }

    /**
     * removes all entries from the history list that enables forward stepping. This method should be called if the
     * envelope of the map ahs been changed on an other way than moving back in history
     * 
     */
    protected void clearHistoryForwarding() {
        int k = history.size();
        while ( k > historyIndex ) {
            history.remove( --k );
        }
    }

    /**
     * commits changes of all changeable layers to their back ends
     * 
     * @throws Exception
     * 
     */
    public void commitAll()
                            throws Exception {
        String mmId = getInitParameter( "assignedMapModel" );
        MapModel mapModel = appContainer.getMapModel( new Identifier( mmId ) );
        MapModel temp = appContainer.getMapModel( null );
        // this check must be done to ensure that just layers of current model will be committed
        // because this method is registered as action method to all map models
        if ( mapModel == temp ) {
            final List<Layer> layers = new ArrayList<Layer>( 50 );
            mapModel.walkLayerTree( new MapModelVisitor() {

                public void visit( Layer layer )
                                        throws Exception {
                    layers.add( layer );
                }

                public void visit( LayerGroup layerGroup )
                                        throws Exception {
                }

            } );
            CommitDataChangesCommand command = new CommitDataChangesCommand( layers );
            appContainer.getCommandProcessor().executeSychronously( command, true );
        }
    }

    /**
     * commits changes of all selected changeable layers to their back ends
     * 
     */
    public void commitSelected() {
        String mmId = getInitParameter( "assignedMapModel" );
        MapModel mapModel = appContainer.getMapModel( new Identifier( mmId ) );
        MapModel temp = appContainer.getMapModel( null );
        // this check must be done to ensure that just the selected layer of current model will be committed
        // because this method is registered as action method to all map models
        if ( mapModel == temp ) {
            List<Layer> layers = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
            CommitDataChangesCommand command = new CommitDataChangesCommand( layers );
            try {
                appContainer.getCommandProcessor().executeSychronously( command, true );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11222" ),
                                               Messages.get( "$MD11223" ), e );
            }
        }
    }

    /**
     * exports a layer into a file. Depending on data source type (grid - feature)
     */
    @SuppressWarnings("unchecked")
    public void exportLayer() {
        String mmId = getInitParameter( "assignedMapModel" );
        MapModel mapModel = appContainer.getMapModel( new Identifier( mmId ) );
        MapModel temp = appContainer.getMapModel( null );
        // this check must be done to ensure that just the selected layer of current model will be exported
        // because this method is registered as action method to all map models
        if ( mapModel == temp ) {
            File file = null;
            List<Layer> layers = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
            if ( "Application".equals( appContainer.getViewPlatform() ) ) {
                if ( layers.size() > 0 ) {
                    List<DesktopFileFilter> cff = null;
                    if ( layers.get( 0 ).getDataAccess().get( 0 ) instanceof FeatureAdapter ) {
                        cff = DesktopFileFilter.createForwellKnownFormats( (ApplicationContainer<Container>) appContainer,
                                                                        DesktopFileFilter.FILETYPE.vector );
                    } else {
                        cff = DesktopFileFilter.createForwellKnownFormats( (ApplicationContainer<Container>) appContainer,
                                                                        DesktopFileFilter.FILETYPE.raster );
                    }

                    Preferences prefs = Preferences.userNodeForPackage( DefaultMapModule.class );

                    file = GenericFileChooser.showSaveDialog( FILECHOOSERTYPE.geoDataFile, appContainer,
                                                              ( (DeegreeDesktop) appContainer ).getMainWndow(), prefs,
                                                              "layerFile", cff );
                    if ( file != null ) {
                        LOG.logDebug( "export layer to file: ", file );
                    }
                } else {
                    DialogFactory.openWarningDialog( "Application", view, Messages.get( "$MD10867" ),
                                                     Messages.get( "$MD10868" ) );
                }
            }
            if ( file != null ) {
                Command command = new ExportLayerCommand( layers.get( 0 ), file );
                try {
                    appContainer.getCommandProcessor().executeSychronously( command, true );
                } catch ( Exception e ) {
                    LOG.logError( e.getMessage(), e );
                    DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11224" ),
                                                   Messages.get( "$MD11225", layers.get( 0 ), file ), e );
                }
            }
        }
    }

    /**
     * zooms the map to the next entry within the list of history envelopes.
     * 
     */
    public void historyForward() {

        if ( historyIndex < history.size() - 1 ) {
            historyIndex++;
            Envelope env = history.get( historyIndex );
            String mmId = getInitParameter( "assignedMapModel" );
            MapModel mapModel = appContainer.getMapModel( new Identifier( mmId ) );
            // use id to identify zoom commands that has been performed in context of
            // stepping through the maps history
            Identifier id = new Identifier();
            historyIdList.add( id );
            ZoomCommand zc = new ZoomCommand( id, mapModel );
            zc.setZoomBox( env, mapModel.getTargetDevice().getPixelWidth(), mapModel.getTargetDevice().getPixelHeight() );
            try {
                appContainer.getCommandProcessor().executeSychronously( zc, true );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11226" ),
                                               Messages.get( "$MD11227" ), e );
            }
        }

    }

    /**
     * zooms the map to the previous entry within the list of history envelopes.
     * 
     */
    public void historyBackward() {
        if ( historyIndex > 0 ) {
            String mmId = getInitParameter( "assignedMapModel" );
            MapModel mapModel = appContainer.getMapModel( new Identifier( mmId ) );
            if ( historyIndex == history.size() ) {
                history.add( mapModel.getEnvelope() );
            }

            historyIndex--;
            Envelope env = history.get( historyIndex );
            // use id to identify zoom commands that has been performed in context of
            // stepping through the maps history
            Identifier id = new Identifier();
            historyIdList.add( id );
            ZoomCommand zc = new ZoomCommand( id, mapModel );
            zc.setZoomBox( env, mapModel.getTargetDevice().getPixelWidth(), mapModel.getTargetDevice().getPixelHeight() );
            try {
                appContainer.getCommandProcessor().executeSychronously( zc, true );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11228" ),
                                               Messages.get( "$MD11229" ), e );
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.CommandProcessedListener#commandProcessed(org.deegree.kernel.CommandProcessedEvent)
     */
    public void commandProcessed( CommandProcessedEvent event ) {
        if ( event.getSource() instanceof ZoomCommand
             && !( historyIdList.contains( event.getSource().getIdentifier() ) ) ) {
            // clear forward history section because a new anchor/path has been set/opened
            clearHistoryForwarding();
            // get Envelope of the map before zoom/pan action has been performed
            ZoomCommand zc = (ZoomCommand) event.getSource();
            history.add( zc.getUndoExtent() );
            historyIndex++;
        } else {
            historyIdList.remove( event.getSource().getIdentifier() );
        }
    }

    /**
     * zooms map to extent of the selected layers
     */
    public void zoomToSelectedLayers() {
        ZoomCommand cmd = new ZoomCommand( appContainer.getMapModel( null ) );
        try {
            appContainer.getCommandProcessor().executeSychronously( cmd, true );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11230" ),
                                           Messages.get( "$MD11231" ), e );
        }

    }

    /**
     * 
     */
    public void zoomToSelectedFeatures() {
        String mmId = getInitParameter( "assignedMapModel" );
        MapModel mm = appContainer.getMapModel( new Identifier( mmId ) );
        try {
            SelectedFeaturesEnvelopeVisitor visitor = new SelectedFeaturesEnvelopeVisitor();
            mm.walkLayerTree( visitor );
            if ( visitor.bbox != null ) {
                ZoomCommand zoom = new ZoomCommand( mm );
                zoom.setZoomBox( visitor.bbox, mm.getTargetDevice().getPixelWidth(),
                                 mm.getTargetDevice().getPixelHeight() );
                appContainer.getCommandProcessor().executeSychronously( zoom, true );
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11232" ),
                                           Messages.get( "$MD11233" ), e );
        }
    }

    /**
     * 
     */
    public void activateHyperlink() {
        mapTool.setHotlinkState();

        Container jco = null;
        if ( getViewForm() instanceof JFrame ) {
            JFrame viewForm = (JFrame) getViewForm();
            Component[] components = viewForm.getContentPane().getComponents();
            for ( int i = 0; i < components.length; i++ ) {
                if ( components[i] instanceof DefaultMapComponent ) {
                    jco = (Container) components[i];
                    break;
                }
            }
            if ( jco == null ) {
                LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10072" ) );
                return;
            }
        } else if ( getViewForm() instanceof JInternalFrame ) {
            JInternalFrame viewForm = (JInternalFrame) getViewForm();
            Component[] components = viewForm.getContentPane().getComponents();
            for ( int i = 0; i < components.length; i++ ) {
                if ( components[i] instanceof DefaultMapComponent ) {
                    jco = (Container) components[i];
                    break;
                }
            }
            if ( jco == null ) {
                LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10072" ) );
                return;
            }
        } else {
            jco = (Container) getViewForm();
        }

        jco.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked( MouseEvent e ) {
                if ( mapTool.getState().getClass().equals( HotlinkState.class ) ) {
                    LinkedList<String> urls = getURLsAtPoint( e );

                    if ( urls.isEmpty() ) {
                        return;
                    }

                    String val = urls.getFirst();

                    if ( urls.size() > 1 ) {
                        val = (String) JOptionPane.showInputDialog( null, get( "$MD10896" ), get( "$DI10019" ),
                                                                    JOptionPane.QUESTION_MESSAGE, null, urls.toArray(),
                                                                    val );
                        if ( val == null ) {
                            return;
                        }
                    }

                    String ext = CollectionUtils.last( val.split( "[.]" ) );
                    String program = appContainer.getSettings().getExternalReferencesOptions().getProgram( ext );

                    LinkedList<String> cmd = new LinkedList<String>();

                    if ( program == null ) {
                        if ( System.getProperty( "os.name" ).equalsIgnoreCase( "linux" ) ) {
                            cmd.add( "firefox" );
                        } else {
                            cmd.add( "cmd" );
                            cmd.add( "/c" );
                            cmd.add( "start" );
                        }
                    } else {
                        cmd.add( program );
                    }

                    cmd.add( val );

                    // leave the process alone, whatever happens
                    ProcessBuilder pb = new ProcessBuilder( cmd );
                    try {
                        pb.start();
                    } catch ( IOException e1 ) {
                        LOG.logError( "Unknown error", e1 );
                    }
                }
            }

        } );

        final Container jco2 = jco;
        final Cursor handCursor = Cursor.getPredefinedCursor( Cursor.HAND_CURSOR );
        final Cursor defaultCursor = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
        jco.addMouseMotionListener( new MouseMotionAdapter() {
            @Override
            public void mouseMoved( MouseEvent e ) {
                if ( mapTool.getState().getClass().equals( HotlinkState.class ) ) {
                    LinkedList<String> urls = getURLsAtPoint( e );
                    if ( !urls.isEmpty() ) {
                        jco2.setCursor( handCursor );
                    } else {
                        jco2.setCursor( defaultCursor );
                    }
                }
            }
        } );
    }

    LinkedList<String> getURLsAtPoint( MouseEvent e ) {
        String mmId = getInitParameter( "assignedMapModel" );
        MapModel mm = appContainer.getMapModel( new Identifier( mmId ) );
        List<Layer> layers = mm.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
        LinkedList<String> urls = new LinkedList<String>();

        GeoTransform trans = mm.getToTargetDeviceTransformation();
        Point pt = createPoint( trans.getSourceX( e.getX() ), trans.getSourceY( e.getY() ), mm.getCoordinateSystem() );

        for ( Layer l : layers ) {
            for ( DataAccessAdapter adapter : l.getDataAccess() ) {
                if ( adapter instanceof FeatureAdapter ) {
                    FeatureAdapter fa = (FeatureAdapter) adapter;

                    List<QualifiedName> refProps = CollectionUtils.map( fa.getDatasource().getDatasourceType().getReferenceProperty(),
                                                                        toqualifiedname );

                    try {
                        FeatureCollection col = fa.getFeatureCollection( pt );
                        for ( int i = 0; i < col.size(); i++ ) {
                            Feature f = col.getFeature( i );
                            for ( FeatureProperty prop : f.getProperties() ) {
                                if ( refProps.isEmpty() || refProps.contains( prop.getName() ) ) {
                                    String val = prop.getValue( "" ).toString();
                                    if ( val.contains( "http://" ) ) {
                                        String[] vals = val.split( "[ ,]" );
                                        urls.addAll( Arrays.asList( vals ) );
                                    } else {
                                        File file = new File( val );
                                        if ( file.exists() ) {
                                            urls.add( file.toURI().toURL().toExternalForm() );
                                        }
                                    }
                                }
                            }
                        }
                    } catch ( FilterEvaluationException e1 ) {
                        LOG.logError( "Unknown error", e1 );
                    } catch ( MalformedURLException e1 ) {
                        LOG.logError( "Unknown error", e1 );
                    }
                }
            }
        }

        return urls;
    }

    /**
     * event handler method; setting extent of all map model to active one
     */
    public void synchronizeMapModels() {
        MapModel current = appContainer.getMapModel( null );
        Envelope env = current.getEnvelope();
        List<MapModel> list = appContainer.getMapModelCollection().getMapModels();
        try {
            for ( MapModel mapModel : list ) {
                if ( !mapModel.equals( current ) ) {
                    ZoomCommand cmd = new ZoomCommand( mapModel );
                    cmd.setZoomBox( env, -1, -1 );
                    appContainer.getCommandProcessor().executeSychronously( cmd, true );
                }
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11234" ),
                                           Messages.get( "$MD11235" ), e );
        }
    }

    /**
     * action handler function for exporting a map as image
     */
    public void exportAsImage() {
        String mmId = getInitParameter( "assignedMapModel" );
        MapModel mapModel = appContainer.getMapModel( new Identifier( mmId ) );
        MapModel temp = appContainer.getMapModel( null );
        // this check must be done to ensure that just layers of current model will be committed
        // because this method is registered as action method to all map models
        if ( mapModel == temp ) {

            Preferences prefs = userNodeForPackage( DefaultMapModule.class );
            File f = GenericFileChooser.showSaveDialog( FILECHOOSERTYPE.printResult, appContainer, null, prefs,
                                                        "outputdir", DesktopFileFilter.JPEG, DesktopFileFilter.PNG,
                                                        DesktopFileFilter.BMP );

            if ( f != null ) {
                BufferedImage img;
                try {
                    img = MapTools.getMapAsImage( mapModel, f.getAbsolutePath().toLowerCase().endsWith( ".png" ), 1.5f );
                } catch ( Exception e ) {
                    LOG.logError( e.getMessage(), e );
                    return;
                }
                try {
                    ImageUtils.saveImage( img, f, 0.99f );
                } catch ( IOException e ) {
                    LOG.logError( e.getMessage(), e );
                }
            }
        }

    }

    /**
     * action handler function for copying a map into clip board
     */
    public void exportToClipBoard() {
        String mmId = getInitParameter( "assignedMapModel" );
        MapModel mapModel = appContainer.getMapModel( new Identifier( mmId ) );
        MapModel temp = appContainer.getMapModel( null );
        // this check must be done to ensure that just layers of current model will be committed
        // because this method is registered as action method to all map models
        if ( mapModel == temp ) {
            BufferedImage img;
            try {
                img = MapTools.getMapAsImage( mapModel, true, 1.5f );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                return;
            }

            final JLabel label = new JLabel( new ImageIcon( img ) );
            label.setTransferHandler( new ImageSelection() );

            // use both clip boards for text?
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemSelection();
            if ( clip != null ) {
                TransferHandler handler = label.getTransferHandler();
                handler.exportToClipboard( label, clip, TransferHandler.COPY );
            }
            clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            TransferHandler handler = label.getTransferHandler();
            handler.exportToClipboard( label, clip, TransferHandler.COPY );
        }

    }

    public void addLinkedTable() {
        MapModel mm = appContainer.getMapModel( null );
        if ( mm.getLayersSelectedForAction( MapModel.SELECTION_ACTION ).size() == 0 ) {
            DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11580" ),
                                             Messages.get( "$MD11581" ) );
            return;
        }
        if ( appContainer.getViewPlatform().equalsIgnoreCase( "application" ) ) {
            try {
                new LinkedDataDialog( this );
            } catch ( Exception e ) {
                DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11582" ),
                                               Messages.get( "$MD11583" ), e );
            }
        }
    }

    /**
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public T getMapContainer() {
        return (T) jco;
    }

    // //////////////////////////////////////////////////////////////////////////////////////////
    // inner classes
    // //////////////////////////////////////////////////////////////////////////////////////////
    /**
     * <code>SelectedFeaturesVisitor</code>
     * 
     * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    public static class SelectedFeaturesVisitor implements MapModelVisitor {

        /**
         * The collection of selected features.
         */
        public FeatureCollection col = createFeatureCollection( "col", 0 );

        private int max;

        /**
         * @param max
         *            maximum number of objects to be fetched in the feature collection
         */
        public SelectedFeaturesVisitor( int max ) {
            this.max = max;
        }

        public void visit( Layer layer )
                                throws Exception {
            FeatureCollection sel = layer.getSelectedFeatures();
            if ( max == -1 || sel.size() <= max ) {
                col.addAllUncontained( sel );
            } else {
                for ( int i = 0; i < max; ++i ) {
                    col.add( sel.getFeature( i ) );
                }
            }
        }

        public void visit( LayerGroup layerGroup )
                                throws Exception {
            // unused
        }
    }

    /**
     * 
     * The <code>DefaultMapModule</code> class TODO add class documentation here.
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * 
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     * 
     */
    class SelectedFeaturesEnvelopeVisitor implements MapModelVisitor {

        Envelope bbox;

        public void visit( Layer layer )
                                throws Exception {
            FeatureCollection selected = layer.getSelectedFeatures();
            for ( int i = 0; i < selected.size(); ++i ) {
                Feature feature = selected.getFeature( i );
                if ( bbox == null ) {
                    if ( feature.getDefaultGeometryPropertyValue() instanceof Point ) {
                        bbox = feature.getBoundedBy().getBuffer( 25 );
                    } else {
                        bbox = feature.getBoundedBy();
                    }
                } else {
                    bbox.expandToContain( feature.getBoundedBy() );
                }
            }
        }

        public void visit( LayerGroup layerGroup )
                                throws Exception {
            // unused
        }
    }

    /**
     * 
     * TODO add class documentation here
     * 
     * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    private static class ImageSelection extends TransferHandler implements Transferable {

        private static final long serialVersionUID = 6897713712887196351L;

        private static final DataFlavor flavors[] = { DataFlavor.imageFlavor };

        private Image image;

        @Override
        public int getSourceActions( JComponent c ) {
            return TransferHandler.COPY;
        }

        @Override
        public boolean canImport( JComponent comp, DataFlavor flavor[] ) {
            if ( !( comp instanceof JLabel ) ) {
                return false;
            }
            for ( int i = 0, n = flavor.length; i < n; i++ ) {
                for ( int j = 0, m = flavors.length; j < m; j++ ) {
                    if ( flavor[i].equals( flavors[j] ) ) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public Transferable createTransferable( JComponent comp ) {
            // Clear
            image = null;

            if ( comp instanceof JLabel ) {
                JLabel label = (JLabel) comp;
                Icon icon = label.getIcon();
                if ( icon instanceof ImageIcon ) {
                    image = ( (ImageIcon) icon ).getImage();
                    return this;
                }
            }
            return null;
        }

        @Override
        public boolean importData( JComponent comp, Transferable t ) {
            if ( comp instanceof JLabel ) {
                JLabel label = (JLabel) comp;
                if ( t.isDataFlavorSupported( flavors[0] ) ) {
                    try {
                        image = (Image) t.getTransferData( flavors[0] );
                        ImageIcon icon = new ImageIcon( image );
                        label.setIcon( icon );
                        return true;
                    } catch ( UnsupportedFlavorException ignored ) {
                        LOG.logWarning( "ignored", ignored );
                    } catch ( IOException ignored ) {
                        LOG.logWarning( "ignored", ignored );
                    }
                }
            }
            return false;
        }

        // Transferable
        public Object getTransferData( DataFlavor flavor ) {
            if ( isDataFlavorSupported( flavor ) ) {
                return image;
            }
            return null;
        }

        // Transferable
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        // Transferable
        public boolean isDataFlavorSupported( DataFlavor flavor ) {
            return flavor.equals( DataFlavor.imageFlavor );
        }
    }

}
