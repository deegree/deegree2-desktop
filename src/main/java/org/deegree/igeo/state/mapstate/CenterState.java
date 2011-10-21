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

package org.deegree.igeo.state.mapstate;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.model.ZoomCommand;
import org.deegree.igeo.config.TargetDeviceType;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MapModelException;
import org.deegree.igeo.modules.DefaultMapModule;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.views.DrawingPane;
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
public class CenterState extends MapState {

    /**
     * @param appContainer
     * @param invokingAction
     */
    public CenterState( ApplicationContainer<?> appContainer, String invokingAction ) {
        super( appContainer, invokingAction );
    }

    @Override
    public Command createCommand( IModule<?> module, MapModel mapModel, Layer layer, Point... points ) {
        ZoomCommand zoom = new ZoomCommand( mapModel );
        TargetDeviceType td = mapModel.getTargetDevice();
        double x = mapModel.getToTargetDeviceTransformation().getSourceX( points[0].getX() );
        double y = mapModel.getToTargetDeviceTransformation().getSourceY( points[0].getY() );
        double minx = x - ( mapModel.getEnvelope().getWidth() / 2 );
        double maxx = x + ( mapModel.getEnvelope().getWidth() / 2 );
        double miny = y - ( mapModel.getEnvelope().getHeight() / 2 );
        double maxy = y + ( mapModel.getEnvelope().getHeight() / 2 );
        // zoom.setZoom( minx, miny, maxx, maxy, 1f, td.getPixelWidth(), td.getPixelHeight() );
        zoom.setZoomBox( GeometryFactory.createEnvelope( minx, miny, maxx, maxy, mapModel.getCoordinateSystem() ),
                         td.getPixelWidth(), td.getPixelHeight() );
        return zoom;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.state.mapstate.ToolState#createDrawingPane(java.lang.String, java.awt.Graphics)
     */
    @Override
    public DrawingPane createDrawingPane( String platform, Graphics g ) {
        return null;
    }

    @Override
    public void mouseReleased( MouseEvent event ) {
        Point[] points = new Point[1];
        points[0] = GeometryFactory.createPoint( event.getX(), event.getY(), null );

        DefaultMapModule<?> owner = appContainer.getActiveMapModule();
        Command command = createCommand( owner, appContainer.getMapModel( null ), null, points );
        try {
            appContainer.getCommandProcessor().executeSychronously( command, true );
        } catch ( Exception e ) {
            throw new MapModelException( e.getMessage() );
        }
    }

}
