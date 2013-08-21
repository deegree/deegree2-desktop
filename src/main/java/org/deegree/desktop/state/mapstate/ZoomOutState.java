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

package org.deegree.desktop.state.mapstate;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.commands.model.ZoomCommand;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.mapmodel.MapModelException;
import org.deegree.desktop.modules.DefaultMapModule;
import org.deegree.desktop.modules.IModule;
import org.deegree.desktop.views.DrawingPane;
import org.deegree.kernel.Command;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class ZoomOutState extends MapState {

    /**
     * 
     * @param invokingActionName
     */
    public ZoomOutState( ApplicationContainer<?> appContaier, String invokingActionName ) {
        super( appContaier, invokingActionName );
    }

    @Override
    public Command createCommand( IModule<?> module, MapModel mapModel, Layer layer, Point... points ) {

        ZoomCommand zoom = new ZoomCommand( mapModel );

        float sZL = appContainer.getSettings().getZoomLevel();
        if ( sZL > 95 ) {
            sZL = 95;
        }
        float zl = -100f / ( 100f - sZL );

        zoom.setZoom( points[0].getX(), points[0].getY(), points[1].getX(), points[1].getY(), zl,
                      mapModel.getTargetDevice().getPixelWidth(), mapModel.getTargetDevice().getPixelHeight() );
        return zoom;
    }

    @Override
    public DrawingPane createDrawingPane( String platform, Graphics g ) {
        return null;
    }

    @Override
    public void mousePressed( MouseEvent event ) {
        Point[] points = new Point[2];
        points[0] = GeometryFactory.createPoint( event.getX() - 1, event.getY() - 1, null );
        points[1] = GeometryFactory.createPoint( event.getX() + 1, event.getY() + 1, null );

        DefaultMapModule<?> owner = appContainer.getActiveMapModule();
        Command command = createCommand( owner, appContainer.getMapModel( null ), null, points );
        if ( command != null ) {
            try {
                appContainer.getCommandProcessor().executeSychronously( command, true );
            } catch ( Exception e ) {
                throw new MapModelException( e.getMessage() );
            }
        }
    }

}
