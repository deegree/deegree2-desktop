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

import java.awt.Container;
import java.util.List;
import java.util.Locale;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.commands.UnselectFeaturesCommand;
import org.deegree.desktop.commands.model.RemoveMapModelEntryCommand;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.LayerGroup;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.mapmodel.MapModelEntry;
import org.deegree.desktop.mapmodel.MapModelVisitor;
import org.deegree.desktop.modules.ActionDescription.ACTIONTYPE;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.desktop.views.swing.layerlist.LayerInfoFrame;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.Identifier;

/**
 * Module for displaying and managing a map model in a tree view view
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class LayerListTreeViewModule<T> extends DefaultModule<T> {

    private static final ILogger LOG = LoggerFactory.getLogger( LayerListTreeViewModule.class );
    static {
        initCapabilities();
    }

    private static void initCapabilities() {
        ActionDescription ad1 = new ActionDescription( "addLayerGroup", "adds a new layer group to map model", null,
                                                       "adds a new layergroup", ACTIONTYPE.PushButton, null, null );
        ActionDescription ad2 = new ActionDescription(
                                                       "layerinfo",
                                                       "opens a dialog showing informations about selected layer and enabling its manipulation",
                                                       null, "open a dialog containing layer informations",
                                                       ACTIONTYPE.PushButton, null, null );
        ActionDescription ad3 = new ActionDescription( "removeLayer",
                                                       "removes selected layer or layer group from map model", null,
                                                       "removes selected layer", ACTIONTYPE.PushButton, null, null );
        ActionDescription ad4 = new ActionDescription( "rename", "renames selected layer or layer group", null,
                                                       "renames selected layer or layer group", ACTIONTYPE.PushButton,
                                                       null, null );
        ActionDescription ad5 = new ActionDescription( "setVisibility",
                                                       "sets selected layer or layer group visible or unvisible", null,
                                                       "sets visibility of a layer or layer group",
                                                       ACTIONTYPE.checkbox, null, null );
        ActionDescription ad6 = new ActionDescription( "setEditing", "sets selected layer into editing mode", null,
                                                       "sets selected layer into editing mode", ACTIONTYPE.radiobutton,
                                                       null, null );
        moduleCapabilities = new ModuleCapabilities( ad1, ad2, ad3, ad4, ad5, ad6 );
    }

    /**
     * event handler method. Moves a {@link MapModelEntry} to first position of layer list/tree
     */
    public void moveToTop() {
        // TODO
    }

    /**
     * event handler method. Moves a {@link MapModelEntry} to last position of layer list/tree
     */
    public void moveToBottom() {
        // TODO
    }

    /**
     * event handler method. Moves a {@link MapModelEntry} up within layer list/tree
     */
    public void moveUp() {
        // TODO
    }

    /**
     * event handler method. Moves a {@link MapModelEntry} down within layer list/tree
     */
    public void moveDown() {
        // TODO
    }

    /**
     * remove the currently selected layer from the map model
     * 
     */
    public void removeLayer() {
        MapModel mma = appContainer.getMapModel( null );
        // TODO: Is this the correct way to avoid visible selected features from a removed layer?
        try {
            UnselectFeaturesCommand unselectFeaturesCmd = new UnselectFeaturesCommand( mma, false );
            appContainer.getCommandProcessor().executeSychronously( unselectFeaturesCmd, true );
        } catch ( Exception e ) {
            LOG.logWarning( "Could not unselect features from MapModel " + mma.getName() + ": ", e );
        }
        RemoveMapModelEntryCommand cmd = new RemoveMapModelEntryCommand( mma );
        try {
            appContainer.getCommandProcessor().executeSychronously( cmd, true );
        } catch ( Exception e ) {
            // should never happen
            LOG.logWarning( "should never happen", e );
        }
    }

    /**
     * sets the currently selected layer into edit state or revokes it
     * 
     */
    public void setEditing() {
        MapModel mma = appContainer.getMapModel( null );
        List<Layer> layers = mma.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
        List<Layer> edLayers = mma.getLayersSelectedForAction( MapModel.SELECTION_EDITING );
        if ( layers.size() > 0 ) {
            for ( Layer layer : edLayers ) {
                // if other layer instead of the currently selected one for action has been already
                // selected for editing this selection will be removed
                if ( !layer.equals( layers.get( 0 ) ) ) {
                    layer.removeSelectedFor( MapModel.SELECTION_EDITING );
                }
            }
            if ( layers.get( 0 ).getSelectedFor().contains( MapModel.SELECTION_EDITING ) ) {
                layers.get( 0 ).removeSelectedFor( MapModel.SELECTION_EDITING );
            } else {
                if ( layers.get( 0 ).isEditable() ) {
                    layers.get( 0 ).addSelectedFor( MapModel.SELECTION_EDITING );
                } else {
                    DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null,
                                                     Messages.get( "$MD11396", layers.get( 0 ).getTitle() ),
                                                     Messages.get( "$MD11397" ) );
                }
            }

        }
    }

    /**
     * sets the currently selected layer visible or not visible
     * 
     */
    public void setVisibility() {
        MapModel mma = appContainer.getMapModel( null );
        List<MapModelEntry> mapModelEntries = mma.getMapModelEntriesSelectedForAction( MapModel.SELECTION_ACTION );
        for ( MapModelEntry mapModelEntry : mapModelEntries ) {
            mapModelEntry.setVisible( !mapModelEntry.isVisible() );
        }
    }

    /**
     * opens a window with detailed (editable) layer informations
     * 
     */
    @SuppressWarnings("unchecked")
    public void layerInfo() {
        MapModel mma = appContainer.getMapModel( null );
        List<MapModelEntry> mapModelEntries = mma.getMapModelEntriesSelectedForAction( MapModel.SELECTION_ACTION );
        if ( mapModelEntries.size() > 0 ) {
            if ( "Application".equals( appContainer.getViewPlatform() ) ) {
                LayerInfoFrame layerInfoFrame = LayerInfoFrame.getInstance( (ApplicationContainer<Container>) getApplicationContainer() );
                layerInfoFrame.setLayer( mapModelEntries.get( 0 ) );
                layerInfoFrame.setVisible( true );
            } else {
                // TODO
                LOG.logWarning( "other view platforms than Application are not supported yet" );
            }
        }
    }

    /**
     * 
     * 
     */
    public void addLayerGroup() {

        MapModel mma = appContainer.getMapModel( null );
        List<MapModelEntry> mapModelEntries = mma.getMapModelEntriesSelectedForAction( MapModel.SELECTION_ACTION );
        if ( mapModelEntries.size() != 0 ) {
            // layer must have been selected to insert a new layer group
            //
            // insert layer after the last selected layer
            MapModelEntry mapModelEntry = mapModelEntries.get( mapModelEntries.size() - 1 );
            LayerGroup parent = mapModelEntry.getParent();

            final String name = DialogFactory.openInputDialog( appContainer.getViewPlatform(), null,
                                                               Messages.getMessage( Locale.getDefault(), "$MD10346" ),
                                                               Messages.getMessage( Locale.getDefault(), "$MD10347" ) );
            if ( name != null && name.length() > 0 ) {
                try {
                    mma.walkLayerTree( new MapModelVisitor() {

                        public void visit( Layer layer )
                                                throws Exception {
                            if ( layer.getTitle().equals( name ) ) {
                                throw new Exception( Messages.get( "$MD11075", name ) );
                            }
                        }

                        public void visit( LayerGroup layerGroup )
                                                throws Exception {
                            if ( layerGroup.getTitle().equals( name ) ) {
                                throw new Exception( Messages.get( "$MD11076", name ) );
                            }
                        }

                    } );
                } catch ( Exception e ) {
                    // with with desired new name already exists
                    DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null, e.getMessage(),
                                                     Messages.get( "$MD11074" ) );
                    return;
                }
                LOG.logDebug( "new layer group name: ", name );
                LayerGroup newLayerGroup = new LayerGroup( mma, new Identifier(), name, name );
                if ( mapModelEntry instanceof LayerGroup ) {
                    mma.insert( newLayerGroup, (LayerGroup) mapModelEntry, null, true );
                } else {
                    mma.insert( newLayerGroup, parent, mapModelEntry, false );
                }
            } else {
                LOG.logDebug( "adding new layer group cancelled" );
            }
        }
        if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
            mma.printModel( System.out );
        }
    }

    /**
     * enable renaming a layer or layer group
     */
    public void rename() {
        String selName = null;
        MapModel mapModel = appContainer.getMapModel( null );

        // get name of selected layer/layergroup
        List<Layer> selLayers = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
        if ( selLayers.size() == 0 ) {
            List<LayerGroup> selGr = mapModel.getLayerGroupsSelectedForAction( MapModel.SELECTION_ACTION );
            if ( selGr.size() > 0 ) {
                selName = selGr.get( 0 ).getTitle();
            }
        } else {
            selName = selLayers.get( 0 ).getTitle();
        }

        // just if a really a layer/layergroup has been selected
        if ( selName != null ) {
            final String name = DialogFactory.openInputDialogWithValue( appContainer.getViewPlatform(),
                                                                        appContainer.getMainWndow(),
                                                                        Messages.get( "$MD11464" ), selName );
            if ( name != null && name.length() > 0 ) {
                LOG.logDebug( "new layer name: ", name );
                try {
                    mapModel.walkLayerTree( new MapModelVisitor() {

                        public void visit( Layer layer )
                                                throws Exception {
                            if ( layer.getTitle().equals( name ) ) {
                                throw new Exception( Messages.get( "$MD11078", name ) );
                            }
                        }

                        public void visit( LayerGroup layerGroup )
                                                throws Exception {
                            if ( layerGroup.getTitle().equals( name ) ) {
                                throw new Exception( Messages.get( "$MD11079", name ) );
                            }
                        }

                    } );
                } catch ( Exception e ) {
                    // with with desired new name already exists
                    DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null, e.getMessage(),
                                                     Messages.get( "$MD11077" ) );
                    return;
                }
                List<Layer> tmp1 = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
                if ( tmp1.size() > 0 ) {
                    tmp1.get( 0 ).setTitle( name );
                } else {
                    List<LayerGroup> tmp2 = mapModel.getLayerGroupsSelectedForAction( MapModel.SELECTION_ACTION );
                    if ( tmp2.size() > 0 ) {
                        tmp2.get( 0 ).setTitle( name );
                    }
                }
                update();
            } else {
                LOG.logDebug( "renaming layer cancelled" );
            }
        }
    }
}
