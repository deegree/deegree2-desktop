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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Surface;

/**
 * 
 * 
 * 
 * @author <a href="mailto:bh@intevation.de">Bernhard Herzog</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class CreateRectangleDrawingPane extends CreatePolygonDrawingPane {

    /**
     * @param appCont
     */
    public CreateRectangleDrawingPane( ApplicationContainer<?> appCont ) {
        super( appCont );
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

        points = new LinkedList<Point>();
        points.add( GeometryFactory.createPoint( pos, mapModel.getCoordinateSystem() ) );

        this.draw( x, y );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#draw(int, int)
     */
    public void draw( int x, int y ) {
        if ( snapper != null ) {
            java.awt.Point p = new java.awt.Point( x, y );
            p = snapper.snap( p );
            x = p.x;
            y = p.y;
        }

        this.currentX = x;
        this.currentY = y;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#draw(int, int, java.awt.Graphics)
     */
    public void draw( int x, int y, Graphics g ) {
        if ( snapper != null ) {
            java.awt.Point p = new java.awt.Point( x, y );
            p = snapper.snap( p );
            x = p.x;
            y = p.y;
            g.setColor( DRAWCOLOR );
            Stroke temp = ( (Graphics2D) g ).getStroke();
            ( (Graphics2D) g ).setStroke( stroke );
            g.drawOval( x - halfTargetSize, y - halfTargetSize, targetSize, targetSize );
            ( (Graphics2D) g ).setStroke( temp );
        }

        this.currentX = x;
        this.currentY = y;

        drawSurface( g, null, new BasicStroke( 1 ), Color.RED, FILLCOLOR );
    }

    /**
     * @param surface
     *            may be use by extending classes
     * @return A new GeneralPath with the points from this.points.
     */
    protected GeneralPath createPath( Surface surface ) {

        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        GeneralPath path = new GeneralPath();

        float xx = (float) gt.getDestX( points.get( 0 ).getX() );
        float yy = (float) gt.getDestY( points.get( 0 ).getY() );
        path.moveTo( xx, yy );
        xx = (float) gt.getDestX( points.get( 0 ).getX() );
        yy = currentY;
        path.lineTo( xx, yy );
        xx = currentX;
        yy = currentY;
        path.lineTo( xx, yy );
        xx = currentX;
        yy = (float) gt.getDestY( points.get( 0 ).getY() );
        path.lineTo( xx, yy );
        xx = (float) gt.getDestX( points.get( 0 ).getX() );
        yy = (float) gt.getDestY( points.get( 0 ).getY() );
        path.lineTo( xx, yy );
        path.closePath();

        return path;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#stopDrawing(int, int)
     */
    public void stopDrawing( int x, int y ) {
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
        points.add( GeometryFactory.createPoint( pos, mapModel.getCoordinateSystem() ) );
    }

    @Override
    public List<Point> getDrawObjectsAsGeoPoints() {
        // create list of five points that represents the drawn rectangle as a polygon
        List<Point> pnts = new ArrayList<Point>( 4 );
        Point point = null;

        point = GeometryFactory.createPoint( points.get( 0 ).getX(), points.get( 0 ).getY(),
                                             points.get( 0 ).getCoordinateSystem() );
        pnts.add( point );

        point = GeometryFactory.createPoint( points.get( 1 ).getX(), points.get( 0 ).getY(),
                                             points.get( 0 ).getCoordinateSystem() );
        pnts.add( point );

        point = GeometryFactory.createPoint( points.get( 1 ).getX(), points.get( 1 ).getY(),
                                             points.get( 0 ).getCoordinateSystem() );
        pnts.add( point );

        point = GeometryFactory.createPoint( points.get( 0 ).getX(), points.get( 1 ).getY(),
                                             points.get( 0 ).getCoordinateSystem() );
        pnts.add( point );

        return pnts;
    }

}
