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

package org.deegree.igeo.views.swing.drawingpanes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.deegree.igeo.views.DrawingPane;
import org.deegree.igeo.views.Snapper;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class RectangleDrawingPane implements DrawingPane {

    protected int startX;

    protected int startY;

    protected int currentX;

    protected int currentY;

    protected boolean isDrawing = false;

    protected boolean stopped = false;

    protected Graphics g;

    protected Snapper snapper;

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#setGraphicContext(java.awt.Graphics)
     */
    public void setGraphicContext( Graphics g ) {
        this.g = g;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#draw(int, int)
     */
    public void draw( int x, int y ) {
        draw( x, y, this.g );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#draw(int, int, java.awt.Graphics)
     */
    public void draw( int x, int y, Graphics g ) {
        if ( this.isDrawing ) {
            if ( snapper != null ) {
                java.awt.Point p = new java.awt.Point( x, y );
                p = snapper.snap( p );
                x = p.x;
                y = p.y;
            }
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
            g.setColor( new Color( 1f, 1f, 1f, 0.6f ) );
            g.fillRect( x_, y_, w, h );
            g.setColor( new Color( 1f, 0f, 0f ) );
            g.drawRect( x_, y_, w, h );

            this.currentX = x;
            this.currentY = y;
            stopped = false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#finishDrawing()
     */
    public void finishDrawing() {
        this.isDrawing = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#getDrawnObject()
     */
    public List<Point> getDrawnObject() {
        List<Point> list = new ArrayList<Point>( 2 );
        list.add( new Point( this.startX, this.startY ) );
        list.add( new Point( this.currentX, this.currentY ) );
        return list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#isDrawing()
     */
    public boolean isDrawing() {
        return this.isDrawing;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#startDrawing(int, int)
     */
    public void startDrawing( int x, int y ) {
        if ( snapper != null ) {
            java.awt.Point p = new java.awt.Point( x, y );
            p = snapper.snap( p );
            x = p.x;
            y = p.y;
        }
        this.startX = x;
        this.startY = y;
        this.currentX = this.startX;
        this.currentY = this.startY;
        this.isDrawing = true;
        stopped = false;
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
        this.currentX = x;
        this.currentY = y;
        stopped = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#isDrawingStopped()
     */
    public boolean isDrawingStopped() {
        return stopped;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#getCurrent()
     */
    public Point getCurrent() {
        return new Point( this.currentX, this.currentY );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#undrawLastPoint()
     */
    public void undrawLastPoint() {
        // do nothing
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#setSnapper(org.deegree.igeo.views.swing.Snapper)
     */
    public void setSnapper( Snapper snapper ) {
        this.snapper = snapper;
    }

}
