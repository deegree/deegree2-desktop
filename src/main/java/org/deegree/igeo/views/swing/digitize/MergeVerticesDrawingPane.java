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

package org.deegree.igeo.views.swing.digitize;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;

import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class MergeVerticesDrawingPane extends MoveVertexDrawingPane {

    private static Color fillColor = new Color( 1f, 1f, 1f, 0.4f );

    private static Color rectDrawColor = new Color( 0f, 0.9f, 0.4f );

    private static Stroke rectStroke = new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1,
                                                        new float[] { 5, 5 }, 0 );

    private int startX;

    private int startY;

    /**
     * @param appContainer
     */
    public MergeVerticesDrawingPane( ApplicationContainer<?> appContainer ) {
        super( appContainer );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.digitize.MoveVertexDrawingPane#draw(int, int, java.awt.Graphics)
     */
    @Override
    public void draw( int x, int y, Graphics g ) {
        if ( this.isDrawing ) {
            if ( snapper != null ) {
                java.awt.Point p = new java.awt.Point( x, y );
                p = snapper.snap( p );
                x = p.x;
                y = p.y;
            }
            this.currentX = x;
            this.currentY = y;
            if ( stopped ) {
                g.setColor( drawColor );
                Stroke temp = ( (Graphics2D) g ).getStroke();
                ( (Graphics2D) g ).setStroke( stroke );
                g.drawOval( x - halfTargetSize, y - halfTargetSize, targetSize, targetSize );
                ( (Graphics2D) g ).setStroke( temp );
                // draw rectangle that has been defined in the first step
                GeoTransform gt = mapModel.getToTargetDeviceTransformation();
                Position p1 = gt.getDestPoint( points.get( 1 ).getPosition() );
                drawRectangle( (int) p1.getX(), (int) p1.getY(), g );
            } else {
                drawRectangle( x, y, g );
            }
            this.currentX = x;
            this.currentY = y;
        }
    }

    private void drawRectangle( int x, int y, Graphics g ) {
        int x_ = this.startX;
        int y_ = this.startY;
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

        // draw new rectangle with 60% transparent fill and red outline
        Stroke temp = ( (Graphics2D) g ).getStroke();
        ( (Graphics2D) g ).setStroke( rectStroke );
        g.setColor( fillColor );
        g.fillRect( x_, y_, w, h );
        g.setColor( rectDrawColor );
        g.drawRect( x_, y_, w, h );
        ( (Graphics2D) g ).setStroke( temp );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#startDrawing(int, int)
     */
    public void startDrawing( int x, int y ) {
        if ( snapper != null ) {
            snapper.initSnapInfoList();
        }
        isDrawing = true;
        points = new ArrayList<Point>();
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        double xx = gt.getSourceX( x );
        double yy = gt.getSourceY( y );
        Point p = GeometryFactory.createPoint( xx, yy, mapModel.getCoordinateSystem() );
        p.setTolerance( 200000 );
        points.add( p );
        this.startX = x;
        this.startY = y;
        this.draw( x, y );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#stopDrawing(int, int)
     */
    public void stopDrawing( int x, int y ) {
        if ( snapper != null ) {
            java.awt.Point p = new java.awt.Point( x, y );
            p = snapper.snap( p );
            x = p.x;
            y = p.y;
        }
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        double xx = gt.getSourceX( x );
        double yy = gt.getSourceY( y );
        Point p = GeometryFactory.createPoint( xx, yy, mapModel.getCoordinateSystem() );
        p.setTolerance( 200000 );
        points.add( p );
        stopped = true;
    }

}
