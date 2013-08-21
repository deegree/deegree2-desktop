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
package org.deegree.desktop.views.swing.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import org.deegree.desktop.ApplicationContainer;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ChangeLabelPositionDrawingPane extends EditFeatureStyleDrawingPane {

    protected static final Color fillColor = new Color( 1f, 1f, 1f, 0.4f );

    protected static final Color rectDrawColor = new Color( 0f, 0.9f, 0.4f );

    protected static final Color anchorColor = Color.RED;

    protected static final Stroke rectStroke = new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1,
                                                          new float[] { 5, 5 }, 0 );

    /**
     * @param appCont
     */
    public ChangeLabelPositionDrawingPane( ApplicationContainer<?> appCont ) {
        super( appCont );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#draw(int, int, java.awt.Graphics)
     */
    public void draw( int x, int y, Graphics g ) {
        // TODO
        if ( width > 0 ) {
            AffineTransform transform = new AffineTransform();
            // draw selection result rectangle with 60% transparent fill and red outline
            Stroke tmp = ( (Graphics2D) g ).getStroke();
            ( (Graphics2D) g ).setStroke( rectStroke );
            transform.rotate( rotation / 180d * Math.PI, dx + minx, maxy - dy + height );
            transform.translate( dx, -dy );
            ( (Graphics2D) g ).setTransform( transform );
            g.setColor( fillColor );
            g.fillRect( (int) minx, (int) maxy, (int) width, (int) height );
            g.setColor( rectDrawColor );            
            g.drawRect( (int) minx, (int) maxy, (int) width, (int) height );
            ( (Graphics2D) g ).setStroke( tmp ); 
            g.setColor( anchorColor );
            g.fillOval( (int) minx - 3, (int) (maxy + height) - 3, 7, 7 );
            g.setColor( Color.BLACK );
            g.drawOval( (int) minx - 3, (int) (maxy + height) - 3, 7, 7 );
        }
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

}
