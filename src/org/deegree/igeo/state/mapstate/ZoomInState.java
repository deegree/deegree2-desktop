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

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JFrame;

import org.deegree.framework.utils.MapTools;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.model.ZoomCommand;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MapModelException;
import org.deegree.igeo.modules.DefaultMapModule;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.views.DrawingPane;
import org.deegree.igeo.views.swing.drawingpanes.RectangleDrawingPane;
import org.deegree.kernel.Command;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class ZoomInState extends MapState {

    /**
     * @param appContainer
     * @param invokingAction
     */
    public ZoomInState( ApplicationContainer<?> appContainer, String invokingAction ) {
        super( appContainer, invokingAction );
    }

    @Override
    public Command createCommand( IModule<?> module, MapModel mapModel, Layer layer, Point... points ) {

        float zl = appContainer.getSettings().getZoomLevel() / 100f;
        if ( zl > 0.45 ) {
            // correct zoom level because a buffer can not be >= 50% of a box size
            zl = -0.45f;
        } else {
            zl *= -1f;
        }
        ZoomCommand zc = new ZoomCommand( mapModel );
        zc.setZoom( points[0].getX(), points[0].getY(), points[1].getX(), points[1].getY(), zl,
                    mapModel.getTargetDevice().getPixelWidth(), mapModel.getTargetDevice().getPixelHeight() );
        return zc;
    }

    /**
     * 
     * @param platform
     *            Swing, Applet, Portlet ...
     * @param g
     *            target graphic context
     * @return
     */
    public DrawingPane createDrawingPane( String platform, Graphics g ) {
        if ( "Application".equalsIgnoreCase( platform ) ) {
            drawingPane = new RectangleDrawingPane();
        } else if ( "Applet".equalsIgnoreCase( platform ) ) {
            drawingPane = new RectangleDrawingPane();
        } else if ( "Portlet".equalsIgnoreCase( platform ) ) {

        } else if ( "JSP/HTML".equalsIgnoreCase( platform ) ) {

        } else if ( "JWS".equalsIgnoreCase( platform ) ) {
            drawingPane = new RectangleDrawingPane();
        } else if ( "JME".equalsIgnoreCase( platform ) ) {

        }
        drawingPane.setGraphicContext( g );
        return drawingPane;
    }

    @Override
    public void mouseDragged( MouseEvent event ) {
        if ( drawingPane != null && drawingPane.isDrawing() ) {
            DefaultMapModule<?> owner = appContainer.getActiveMapModule();
            owner.update();
            Component c = (Component) event.getSource();
            java.awt.Point p = MapTools.adjustPointToPanelSize( event.getPoint(), c.getWidth(), c.getHeight() );
            drawingPane.stopDrawing( p.x, p.y );

            // force repainting of the container, otherwise the
            // zoom rectangle is never visible, when view form is a frame
            if ( owner.getViewForm() instanceof JFrame ) {
                Container con = c.getParent();
                con.repaint();
            }
        }
    }

    @Override
    public void mouseReleased( MouseEvent event ) {
        Component c = (Component) event.getSource();
        java.awt.Point p = MapTools.adjustPointToPanelSize( event.getPoint(), c.getWidth(), c.getHeight() );
        drawingPane.stopDrawing( p.x, p.y );
        drawingPane.finishDrawing();
        List<java.awt.Point> drawnPoints = drawingPane.getDrawnObject();
        Point[] points = new Point[drawnPoints.size()];
        for ( int i = 0; i < points.length; i++ ) {
            points[i] = GeometryFactory.createPoint( drawnPoints.get( i ).x, drawnPoints.get( i ).y, null );
        }
        if ( points.length == 2 && points[0].equals( points[1] ) ) {
            // ensure that box will created
            points[0] = GeometryFactory.createPoint( event.getX() - 1, event.getY() - 1, null );
            points[1] = GeometryFactory.createPoint( event.getX() + 1, event.getY() + 1, null );
        }

        DefaultMapModule<?> owner = appContainer.getActiveMapModule();

        Command command = createCommand( owner, appContainer.getMapModel( null ), null, points );
        try {
            appContainer.getCommandProcessor().executeSychronously( command, true );
        } catch ( Exception e ) {
            throw new MapModelException( e.getMessage() );
        }

    }

}
