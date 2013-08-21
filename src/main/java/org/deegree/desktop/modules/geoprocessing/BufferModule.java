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

package org.deegree.desktop.modules.geoprocessing;

import java.util.List;

import org.deegree.desktop.commands.UnselectFeaturesCommand;
import org.deegree.desktop.commands.geoprocessing.BufferCommand;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.modules.ActionDescription;
import org.deegree.desktop.modules.DefaultModule;
import org.deegree.desktop.modules.ModuleCapabilities;
import org.deegree.desktop.modules.ActionDescription.ACTIONTYPE;
import org.deegree.desktop.views.swing.geoprocessing.BufferModel;
import org.deegree.kernel.Command;
import org.deegree.kernel.CommandList;
import org.deegree.kernel.ProcessMonitor;
import org.deegree.kernel.ProcessMonitorFactory;

/**
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class BufferModule<T> extends DefaultModule<T> {

    static {
        ActionDescription ad1 = new ActionDescription(
                                                       "open",
                                                       "opens a dialog for defining parameters and starting buffer operation",
                                                       null, "open buffer dialog", ACTIONTYPE.PushButton, null, null );
        moduleCapabilities = new ModuleCapabilities( ad1 );
    }

    /**
     * opens a dialog for making settings to calculate buffer
     */
    public void open() {
        MapModel mapModel = appContainer.getMapModel( null );
        List<Layer> layers = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
        if ( layers.size() > 0 && this.componentStateAdapter.isClosed() ) {
            this.componentStateAdapter.setClosed( false );
            this.view = null;
            createIView();
        }
    }

    public void buffer() {
        BufferModel bm = (BufferModel) view;
        if ( bm != null ) {
            MapModel mapModel = appContainer.getMapModel( null );
            Layer layer = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION ).get( 0 );

            Command command1 = new BufferCommand( layer, bm );
            Command command2 = new UnselectFeaturesCommand( mapModel, false );
            CommandList command = new CommandList();
            command.add( command1 );
            command.add( command2 );
            ProcessMonitor pm = ProcessMonitorFactory.createDialogProcessMonitor( appContainer.getViewPlatform(),
                                                                                  Messages.get( "$MD10580" ),
                                                                                  Messages.get( "$MD10581" ), 0, 1,
                                                                                  command1 );
            command1.setProcessMonitor( pm );
            appContainer.getCommandProcessor().executeASychronously( command );
        }
        clear();
    }

}
