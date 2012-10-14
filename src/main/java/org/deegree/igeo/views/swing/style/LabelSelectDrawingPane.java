//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2012 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.igeo.views.swing.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.model.spatialschema.Point;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class LabelSelectDrawingPane extends EditFeatureStyleDrawingPane {

    private static Color fillColor = new Color( 1f, 1f, 1f, 0.4f );

    private static Color rectDrawColor = new Color( 0f, 0.9f, 0.4f );
    
    private static Color anchorColor = Color.RED;

    private static Stroke rectStroke = new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1,
                                                        new float[] { 5, 5 }, 0 );

    /**
     * @param appCont
     */
    public LabelSelectDrawingPane( ApplicationContainer<?> appCont ) {
        super( appCont );
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
        if ( points.size() > 0 ) {
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
        if ( width > 0 ) {
            AffineTransform transform = new AffineTransform();
            // draw selection result rectangle with 60% transparent fill and red outline
            ( (Graphics2D) g ).setStroke( rectStroke );
            transform.rotate( rotation / 180d * Math.PI, dx + minx , maxy - dy  + height);
            transform.translate( dx, - dy );
            ( (Graphics2D) g ).setTransform( transform );
            g.setColor( fillColor );
            g.fillRect( (int) minx, (int) maxy, (int) width, (int) height );
            g.setColor( rectDrawColor );
            g.drawRect( (int) minx, (int) maxy, (int) width, (int) height );           
            g.setColor( anchorColor );
            g.fillOval( (int) minx - 3, (int) (maxy + height) - 3, 7, 7 );
            g.setColor( Color.BLACK );
            g.drawOval( (int) minx - 3, (int) (maxy + height) - 3, 7, 7 );
        }
    }

    /**
     * 
     */
    public void clear() {
        points = new ArrayList<Point>();
    }

}
