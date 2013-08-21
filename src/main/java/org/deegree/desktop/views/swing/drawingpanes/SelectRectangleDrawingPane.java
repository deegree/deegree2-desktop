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
import java.util.LinkedList;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.views.GeoDrawingPane;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class SelectRectangleDrawingPane extends GeoDrawingPane {

    /**
     * @param appCont
     */
    public SelectRectangleDrawingPane( ApplicationContainer<?> appCont ) {
        super( appCont );
        mapModel = appCont.getMapModel( null );
    }

    /**
     * Extends the base class method to initialize this.points if it hasn't been initialized already.
     */
    public void startDrawing( int x, int y ) {
        this.isDrawing = true;

        points = new LinkedList<Point>();
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        double xx = gt.getSourceX( x );
        double yy = gt.getSourceY( y );
        Point p = GeometryFactory.createPoint( xx, yy, mapModel.getCoordinateSystem() );
        points.add( p );

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
        int w = x - x_;
        int h = y - y_;
        if ( w < 0 ) {
            x_ = x;
            w *= -1;
        }
        if ( h < 0 ) {
            y_ = y;
            h *= -1;
        }

        g.setColor( new Color( 1f, 1f, 1f, 0.6f ) );
        g.fillRect( x_, y_, w, h );
        g.setColor( new Color( 1f, 0f, 0f ) );
        g.drawRect( x_, y_, w, h );

        this.currentX = x;
        this.currentY = y;
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

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#isDrawing()
     */
    public boolean isDrawing() {
        return this.isDrawing;
    }

}
