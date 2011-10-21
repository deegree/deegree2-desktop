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

package org.deegree.igeo.modules;

import java.awt.Container;
import java.io.File;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.model.AddFileLayerCommand;
import org.deegree.igeo.desktop.IGeoDesktop;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.ActionDescription.ACTIONTYPE;
import org.deegree.igeo.views.swing.util.GenericFileChooser;
import org.deegree.igeo.views.swing.util.IGeoFileFilter;
import org.deegree.igeo.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;
import org.deegree.igeo.views.swing.util.IGeoFileFilter.FILETYPE;
import org.deegree.kernel.CommandProcessedEvent;
import org.deegree.kernel.CommandProcessedListener;
import org.deegree.kernel.ProcessMonitor;
import org.deegree.kernel.ProcessMonitorFactory;

/**
 * <code>DefaultAddLayerModule</code> to add a new Layer to the assigned map model.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class AddLayerModule<T> extends DefaultModule<T> {

    private static final ILogger LOG = LoggerFactory.getLogger( AddLayerModule.class );

    static {
        ActionDescription ad1 = new ActionDescription(
                                                       "addLayer",
                                                       "opens a dialog for adding a new layer from one of all supported datasources",
                                                       null, "add a new layer", ACTIONTYPE.PushButton, null, null );
        ActionDescription ad2 = new ActionDescription(
                                                       "openFile",
                                                       "opens a file dialog for selecting a geo data file to add as new layer",
                                                       null, "add file as new layer", ACTIONTYPE.PushButton, null, null );
        moduleCapabilities = new ModuleCapabilities( ad1, ad2 );
    }

    /**
     * adds a new layer to the map model
     */
    public void addLayer() {
        this.componentStateAdapter.setClosed( false );
        this.createIView();
        Object view = this.getViewForm();
        if ( view instanceof JFrame ) {
            ( (JFrame) view ).setVisible( true );
        }
    }

    /**
     * adds a new layer to the map be loading a file; this is a convenience method offering a subset of options
     * available from {@link #addLayer()}
     * 
     */
    @SuppressWarnings("unchecked")
    public void openFile() {
        MapModel mapModel = appContainer.getMapModel( null );
        File file = null;
        if ( "Application".equalsIgnoreCase( appContainer.getViewPlatform() ) ) {
            Preferences prefs = Preferences.userNodeForPackage( AddLayerModule.class );
            List<IGeoFileFilter> ff = IGeoFileFilter.createForwellKnownFormats(
                                                                                (ApplicationContainer<Container>) appContainer,
                                                                                FILETYPE.any );
            file = GenericFileChooser.showOpenDialog( FILECHOOSERTYPE.geoDataFile, appContainer,
                                                      ( (IGeoDesktop) appContainer ).getMainWndow(), prefs,
                                                      "geoDataFile", ff );

            if ( file != null ) {
                LOG.logDebug( "load file for new datasource: ", file );
            }
        }
        if ( file != null ) {

            AddFileLayerCommand command = new AddFileLayerCommand( mapModel, file, null, null, null,
                                                                   mapModel.getCoordinateSystem().getPrefixedName() );

            final ProcessMonitor pm = ProcessMonitorFactory.createDialogProcessMonitor(
                                                                                        appContainer.getViewPlatform(),
                                                                                        Messages.get( "$MD11264" ),
                                                                                        Messages.get( "$MD11265", file ),
                                                                                        0, -1, command );
            command.setProcessMonitor( pm );
            command.addListener( new CommandProcessedListener() {

                public void commandProcessed( CommandProcessedEvent event ) {
                    try {
                        pm.cancel();
                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }

            } );
            appContainer.getCommandProcessor().executeASychronously( command );
        }

    }

}
