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
package org.deegree.desktop.views.swing.drawingpanes;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.framework.util.GeometryUtils;
import org.deegree.framework.util.Pair;
import org.deegree.framework.utils.LineUtils;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;

/**
 * 
 * 
 * 
 * @author <a href="mailto:bh@intevation.de">Bernhard Herzog</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class CreateArcDrawingPane extends CreateLinestringDrawingPane {

    protected Color drawColor = new Color( 0f, 1f, 0.5f, 0.8f );

    /**
     * 
     * @param appCont
     */
    public CreateArcDrawingPane( ApplicationContainer<?> appCont ) {
        super( appCont );
        points = new ArrayList<Point>( 3 );
    }

    /**
     * Extends the base class method to initialize this.points if it hasn't been initialized already.
     */
    public void startDrawing( int x, int y ) {
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        Position pos = null;
        if ( snapper != null ) {
            snapper.initSnapInfoList();
            java.awt.Point p = new java.awt.Point( x, y );
            pos = snapper.snapPos( p );
            x = (int) gt.getDestX( pos.getX() );
            y = (int) gt.getDestY( pos.getY() );
        } else {
            double dx = gt.getSourceX( x );
            double dy = gt.getSourceY( y );
            pos = GeometryFactory.createPosition( dx, dy );
        }
        this.isDrawing = true;
        if ( points.size() == 3 ) {
            points.set( 2, GeometryFactory.createPoint( pos, mapModel.getCoordinateSystem() ) );
        } else {
            points.add( GeometryFactory.createPoint( pos, mapModel.getCoordinateSystem() ) );
        }

        this.draw( x, y );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#draw(int, int)
     */
    public void draw( int x, int y ) {
        this.currentX = x;
        this.currentY = y;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#draw(int, int, java.awt.Graphics)
     */
    public void draw( int x, int y, Graphics g ) {

        GeoTransform gt = mapModel.getToTargetDeviceTransformation();

        if ( points.size() == 1 ) {
            // draw line from center to arc start point
            // center is fixed
            g.setColor( Color.BLACK );
            int x1 = (int) gt.getDestX( points.get( 0 ).getX() );
            int y1 = (int) gt.getDestY( points.get( 0 ).getY() );
            g.drawLine( x1, y1, x, y );
        } else if ( points.size() == 2 ) {
            int[] sp = calcSymetricPoint( gt, x, y );
            x = sp[0];
            y = sp[1];

            // draw arc and line from arc end point to center
            // center and start point are fixed
            g.setColor( Color.BLACK );
            int x1 = (int) gt.getDestX( points.get( 0 ).getX() );
            int y1 = (int) gt.getDestY( points.get( 0 ).getY() );
            int x2 = (int) gt.getDestX( points.get( 1 ).getX() );
            int y2 = (int) gt.getDestY( points.get( 1 ).getY() );
            g.drawLine( x1, y1, x2, y2 );
            x2 = (int) gt.getDestX( points.get( 0 ).getX() );
            y2 = (int) gt.getDestY( points.get( 0 ).getY() );
            g.drawLine( x, y, x2, y2 );
            x2 = (int) gt.getDestX( points.get( 1 ).getX() );
            y2 = (int) gt.getDestY( points.get( 1 ).getY() );
            g.setColor( Color.YELLOW );
            g.drawLine( x, y, x2, y2 );
        } else if ( points.size() == 3 ) {
            // draw arc and line from arc end point to center
            // digitizing finished
            drawArc( g, gt );
            // line from root to current mouse position
            int x1 = (int) gt.getDestX( points.get( 0 ).getX() );
            int y1 = (int) gt.getDestY( points.get( 0 ).getY() );
            int[] sp = calcSymetricPoint( gt, x, y );
            x = sp[0];
            y = sp[1];
            g.setColor( Color.white );
            g.drawLine( x1, y1, x, y );
        }
        this.currentX = x;
        this.currentY = y;

        // draw red circles at already digitized positions
        drawPoints( g, gt );

        // draw circle at current position of the cursor
        g.setColor( drawColor );
        g.drawOval( x - 13, y - 13, 25, 25 );

    }

    private void drawPoints( Graphics g, GeoTransform gt ) {
        // points draw
        for ( int i = 0; i < points.size() && i < 3; i++ ) {
            int x_ = (int) gt.getDestX( points.get( i ).getX() );
            int y_ = (int) gt.getDestY( points.get( i ).getY() );
            g.setColor( Color.RED );
            g.fillOval( x_ - 6, y_ - 6, 11, 11 );
            g.setColor( Color.BLACK );
            g.drawOval( x_ - 6, y_ - 6, 11, 11 );
        }
    }

    private void drawArc( Graphics g, GeoTransform gt ) {
        g.setColor( Color.BLACK );
        int x1 = (int) gt.getDestX( points.get( 0 ).getX() );
        int y1 = (int) gt.getDestY( points.get( 0 ).getY() );
        int x2 = (int) gt.getDestX( points.get( 1 ).getX() );
        int y2 = (int) gt.getDestY( points.get( 1 ).getY() );
        g.drawLine( x1, y1, x2, y2 );
        x2 = (int) gt.getDestX( points.get( 2 ).getX() );
        y2 = (int) gt.getDestY( points.get( 2 ).getY() );
        g.drawLine( x1, y1, x2, y2 );
        x1 = (int) gt.getDestX( points.get( 1 ).getX() );
        y1 = (int) gt.getDestY( points.get( 1 ).getY() );
        g.setColor( Color.YELLOW );
        g.drawLine( x1, y1, x2, y2 );
    }

    private int[] calcSymetricPoint( GeoTransform gt, int x, int y ) {

        double dx = gt.getSourceX( x );
        double dy = gt.getSourceY( y );
        double[] lineParam = LineUtils.getLineFromPoints( points.get( 0 ).getX(), points.get( 0 ).getY(), dx, dy );
        double d = GeometryUtils.distance( points.get( 0 ).getPosition(), points.get( 1 ).getPosition() );
        Pair<Position, Position> pair = LineUtils.getSymmetricPoints( points.get( 0 ).getX(), points.get( 0 ).getY(),
                                                                      lineParam[0], d );
        if ( dx <= points.get( 0 ).getX() ) {
            x = (int) gt.getDestX( pair.second.getX() );
            y = (int) gt.getDestY( pair.second.getY() );
        } else {
            x = (int) gt.getDestX( pair.first.getX() );
            y = (int) gt.getDestY( pair.first.getY() );
        }
        return new int[] { x, y };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#stopDrawing(int, int)
     */
    public void stopDrawing( int x, int y ) {
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        if ( points.size() >= 2 ) {
            int[] sp = calcSymetricPoint( gt, x, y );
            x = sp[0];
            y = sp[1];
        }
        this.currentX = x;
        this.currentY = y;
        double xx = gt.getSourceX( x );
        double yy = gt.getSourceY( y );
        Point p = GeometryFactory.createPoint( xx, yy, mapModel.getCoordinateSystem() );
        if ( points.size() == 3 ) {
            points.set( 2, p );
        } else {
            points.add( p );
        }
    }

    @Override
    public List<Point> getDrawObjectsAsGeoPoints() {
        return points;
    }

}
