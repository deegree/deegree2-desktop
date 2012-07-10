//$HeadURL: svn+ssh://developername@svn.wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
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

package org.deegree.igeo.modules;

import java.util.List;
import java.util.Map;

import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.ChangeListener;
import org.deegree.igeo.ValueChangedEvent;
import org.deegree.igeo.commands.ObjectInfoCommand;
import org.deegree.igeo.commands.RefreshFeatureInfoCommand;
import org.deegree.igeo.commands.SelectFeatureCommand;
import org.deegree.igeo.config.ModuleType;
import org.deegree.igeo.config._ComponentPositionType;
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.LayerChangedEvent;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MapModelChangedEvent;
import org.deegree.igeo.mapmodel.MapModelCollection;
import org.deegree.igeo.mapmodel.LayerChangedEvent.LAYER_CHANGE_TYPE;
import org.deegree.igeo.mapmodel.MapModelChangedEvent.CHANGE_TYPE;
import org.deegree.igeo.modules.ActionDescription.ACTIONTYPE;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.FeatureTable;
import org.deegree.kernel.Command;
import org.deegree.kernel.CommandProcessedEvent;
import org.deegree.kernel.CommandProcessedListener;
import org.deegree.model.feature.FeatureCollection;

/**
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author: Andreas Poth $
 * 
 * @version $Revision: $, $Date: $
 * @param <T>
 * 
 */
public class LayerInfoModule<T> extends DefaultModule<T> implements ChangeListener, CommandProcessedListener {

    static {
        ActionDescription ad1 = new ActionDescription( "open", "opens a dialog showing a layers properties as a table",
                                                       null, "open layerinfo dialog", ACTIONTYPE.PushButton, null, null );
        moduleCapabilities = new ModuleCapabilities( ad1 );
    }

    @Override
    public void init( ModuleType moduleType, _ComponentPositionType componentPosition, ApplicationContainer<T> appCont,
                      IModule<T> parent, Map<String, String> initParams ) {
        super.init( moduleType, componentPosition, appCont, parent, initParams );
        // add this as listener to get informed if a objects has been selected
        appCont.getCommandProcessor().addCommandProcessedListener( this );
        // add this as listener to get informed if a new layer has been selected in layerlist
        MapModelCollection mmc = appContainer.getMapModelCollection();
        List<MapModel> mm = mmc.getMapModels();
        for ( MapModel mapModel : mm ) {
            mapModel.addChangeListener( this );
        }
    }

    /**
     * opens the modules view
     * 
     */
    public void open() {
        if ( this.componentStateAdapter.isClosed() ) {
            MapModel mm = appContainer.getMapModel( null );
            List<Layer> layers = mm.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
            if ( layers.size() > 0 ) {
                if ( layers.get( 0 ).isQueryable() ) {
                    this.componentStateAdapter.setClosed( false );
                    createIView();
                    displayFeatures( layers.get( 0 ) );
                } else {
                    DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null,
                                                     "layer can not be queried for attibute values", "warning" );
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.ChangeListener#valueChanged(org.deegree.igeo.ValueChangedEvent)
     */
    public void valueChanged( ValueChangedEvent event ) {
        if ( !this.componentStateAdapter.isClosed() ) {
            MapModelChangedEvent mmce = (MapModelChangedEvent) event;
            if ( mmce.getChangeType() == CHANGE_TYPE.layerStateChanged ) {
                LayerChangedEvent lce = (LayerChangedEvent) mmce.getEmbeddedEvent();
                if ( lce != null && lce.getChangeType() == LAYER_CHANGE_TYPE.selectedForChanged ) {
                    Layer layer = lce.getSource();
                    if ( layer.getSelectedFor().contains( MapModel.SELECTION_ACTION ) ) {
                        displayFeatures( layer );
                    }
                }
            } else if ( mmce.getChangeType() == CHANGE_TYPE.layerRemoved ) {
                ( (FeatureTable) getViewForm() ).clear();
            }
        }
    }

    private void displayFeatures( Layer layer ) {
        if ( layer.isQueryable() ) {
            List<DataAccessAdapter> data = layer.getDataAccess();
            for ( DataAccessAdapter dataAccessAdapter : data ) {
                if ( dataAccessAdapter instanceof FeatureAdapter ) {
                    FeatureCollection fc = ( (FeatureAdapter) dataAccessAdapter ).getFeatureCollection();
                    ( (FeatureTable) getViewForm() ).setFeatureCollection( layer, fc );
                } else {
                    ( (FeatureTable) getViewForm() ).setFeatureCollection( layer, null );
                }
            }
        } else {
            ( (FeatureTable) getViewForm() ).setFeatureCollection( layer, null );
            DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11398",
                                                                                                 layer.getTitle() ),
                                             Messages.get( "$MD11399" ) );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.deegree.client.presenter.connector.CommandProcessedListener#commandProcessed(org.deegree.client.presenter
     * .connector.CommandProcessedEvent)
     */
    @SuppressWarnings("unchecked")
    public void commandProcessed( CommandProcessedEvent event ) {
        Command command = event.getSource();
        if ( command.getName().equals( ObjectInfoCommand.commandName ) ) {
            // will open the info panel/window if not already opened
            if ( this.componentStateAdapter.isClosed() ) {
                this.componentStateAdapter.setClosed( false );
                createIView();
            }
            List<FeatureCollection> fc = (List<FeatureCollection>) command.getResult();
            for ( FeatureCollection collection : fc ) {
                if ( collection != null && collection.size() > 0 ) {
                    MapModel mm = appContainer.getMapModel( null );
                    List<Layer> list = mm.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
                    ( (FeatureTable) getViewForm() ).setFeatureCollection( list.get( 0 ), fc.get( 0 ) );
                }
            }
        } else if ( command.getName().equals( SelectFeatureCommand.commandName ) ) {
            if ( !this.componentStateAdapter.isClosed() ) {
                FeatureCollection fc = (FeatureCollection) command.getResult();
                if ( fc != null ) {
                    ( (FeatureTable) getViewForm() ).select( fc );
                }
            }
        } else if ( command.getName().equals( RefreshFeatureInfoCommand.commandName ) ) {
            if ( !this.componentStateAdapter.isClosed() ) {
                ( (FeatureTable) getViewForm() ).refresh();
            }
        }
    }

}
