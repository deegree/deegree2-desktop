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
public class CreateCircleDrawingPane extends CreatePolygonDrawingPane {

    /**
     * 
     * @param appCont
     */
    public CreateCircleDrawingPane( ApplicationContainer<?> appCont ) {
        super( appCont );
        points = new ArrayList<Point>( 4 );
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
        points.add( GeometryFactory.createPoint( pos, mapModel.getCoordinateSystem() ) );

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

        Point p = points.get( 0 );
        int x_ = (int) gt.getDestX( p.getX() );
        int y_ = (int) gt.getDestY( p.getY() );
        int w = (int) java.awt.Point.distance( x, y, x_, y_ );

        this.currentX = x;
        this.currentY = y;

        g.setColor( FILLCOLOR );
        g.fillOval( x_ - w, y_ - w, w * 2, w * 2 );
        g.setColor( Color.RED );
        g.drawOval( x_ - w, y_ - w, w * 2, w * 2 );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#stopDrawing(int, int)
     */
    public void stopDrawing( int x, int y ) {
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        double xx = gt.getSourceX( x );
        double yy = gt.getSourceY( y );
        Point p = GeometryFactory.createPoint( xx, yy, mapModel.getCoordinateSystem() );
        points.add( p );
    }

    @Override
    public List<Point> getDrawObjectsAsGeoPoints() {
        // create list of five points that represents the drawn rectangle as a polygon
        List<Point> pnts = new ArrayList<Point>( 5 );
        double d = points.get( 0 ).distance( points.get( 1 ) );
        for ( int i = 0; i < 72; i++ ) {
            double rad = Math.toRadians( 360 - i * 5 + 10 );
            double y = Math.sin( rad ) * d + points.get( 0 ).getY();
            double x = Math.cos( rad ) * d + points.get( 0 ).getX();
            Point point = GeometryFactory.createPoint( x, y, points.get( 0 ).getCoordinateSystem() );
            pnts.add( point );
        }
        return pnts;
    }

}
