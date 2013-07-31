//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2013 by:
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

 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 http://www.lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/
package org.deegree.desktop.modules;

import static org.deegree.framework.log.LoggerFactory.getLogger;

import java.awt.Color;
import java.awt.Container;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.ChangeListener;
import org.deegree.desktop.ValueChangedEvent;
import org.deegree.desktop.config.ModuleType;
import org.deegree.desktop.config._ComponentPositionType;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.modules.ActionDescription.ACTIONTYPE;
import org.deegree.desktop.state.mapstate.CopyCoordinatesState;
import org.deegree.desktop.state.mapstate.MapStateChangedEvent;
import org.deegree.desktop.state.mapstate.MapTool;
import org.deegree.desktop.state.mapstate.ToolState;
import org.deegree.desktop.views.swing.map.CopyCoordinatesPanel;
import org.deegree.model.Identifier;

/**
 * Module that is used to copy coordinates from map by mouse click
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author last edited by: $Author: wanhoff $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class CopyCoordinatesModule<T> extends DefaultModule<T> implements ChangeListener {

    private static final ILogger LOG = getLogger( CopyCoordinatesModule.class );

    private DefaultMapModule<?> mapModule;

    private CopyCoordinatesPanel copyCoordinatesPanel;

    static {
        ActionDescription ad1 = new ActionDescription( "copyCoordinates", "copy coordinates into clip board", null,
                                                       "copy coordinates clip board", ACTIONTYPE.ToggleButton, null,
                                                       null );

        moduleCapabilities = new ModuleCapabilities( ad1 );

    }

    @Override
    public void init( ModuleType moduleType, _ComponentPositionType componentPosition, ApplicationContainer<T> appCont,
                      IModule<T> parent, Map<String, String> initParams ) {
        super.init( moduleType, componentPosition, appCont, parent, initParams );

        this.mapModule = appContainer.getActiveMapModule();
        if ( this.mapModule == null ) {
            LOG.logError( "no map module found " );
            return;
        }

        this.mapModule.getMapTool().addChangeListener( this );
    }

    /**
     * method assigned to action
     */
    public void copyCoordinates() {
        MapTool<?> mapTool = this.mapModule.getMapTool();
        initCopyCoordinatesPanel( mapTool );
        mapTool.setState( new CopyCoordinatesState( appContainer ) );
    }

    public void initCopyCoordinatesPanel( MapTool<?> mapTool ) {
        if ( copyCoordinatesPanel == null ) {
            Object mapModelViewForm = this.mapModule.getViewForm();
            if ( mapModelViewForm instanceof Container ) {
                String mmId = getInitParameter( "assignedMapModel" );
                MapModel mapModel = appContainer.getMapModel( new Identifier( mmId ) );
                Container container = (Container) mapModelViewForm;
                copyCoordinatesPanel = createCopyCoordinatesPanel( mapTool, mapModel, container );
            }
        }
    }

    private CopyCoordinatesPanel createCopyCoordinatesPanel( MapTool<?> mapTool, MapModel mapModel, Container container ) {
        CopyCoordinatesPanel copyCoordinatesPanel = new CopyCoordinatesPanel( mapTool, container );
        int width = mapModel.getTargetDevice().getPixelWidth();
        int height = mapModel.getTargetDevice().getPixelHeight();
        copyCoordinatesPanel.setBounds( 0, 0, width, height );
        copyCoordinatesPanel.setBackground( new Color( 255, 255, 255, 0 ) );
        copyCoordinatesPanel.setVisible( true );
        return copyCoordinatesPanel;
    }

    @Override
    public void valueChanged( ValueChangedEvent event ) {
        if ( event instanceof MapStateChangedEvent ) {
            this.mapModule.getMapTool().removeChangeListener( this );
            this.mapModule = appContainer.getActiveMapModule();
            this.mapModule.getMapTool().addChangeListener( this );
            ToolState state = ( (MapStateChangedEvent) event ).getState();
            if ( state instanceof CopyCoordinatesState ) {
                copyCoordinatesPanel.connect();
            } else {
                copyCoordinatesPanel.disconnect();
            }
        }
    }

}