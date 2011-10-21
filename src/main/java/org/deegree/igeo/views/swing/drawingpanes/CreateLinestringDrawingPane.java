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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.util.ListIterator;

import org.deegree.framework.util.Pair;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class CreateLinestringDrawingPane extends CreatePolygonDrawingPane {

    protected static BasicStroke stroke = null;
    static {
        if ( stroke == null ) {
            stroke = new BasicStroke( 4 );
        }
    }

    /**
     * @param appCont
     */
    public CreateLinestringDrawingPane( ApplicationContainer<?> appCont ) {
        super( appCont );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#draw(int, int, java.awt.Graphics)
     */
    public void draw( int x, int y, Graphics g ) {

        Pair<Position, java.awt.Point> tmp = correctPoint( x, y );
        this.currentX = tmp.second.x;
        this.currentY = tmp.second.y;

        if ( snapper != null ) {
            g.setColor( DRAWCOLOR );
            Stroke temp = ( (Graphics2D) g ).getStroke();
            ( (Graphics2D) g ).setStroke( stroke );
            g.drawOval( x - halfTargetSize, y - halfTargetSize, targetSize, targetSize );
            ( (Graphics2D) g ).setStroke( temp );
        }

        drawCurve( g, null, stroke, Color.RED );

    }

    /**
     * @param curve
     *            may be used by extending classes
     * @return A new GeneralPath with the points from this.points.
     */
    protected GeneralPath createPath( Curve curve ) {
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        GeneralPath path = new GeneralPath();
        ListIterator<Point> i = points.listIterator( 0 );
        if ( i.hasNext() ) {
            Point p = (Point) i.next();
            float xx = (float) gt.getDestX( p.getX() );
            float yy = (float) gt.getDestY( p.getY() );
            path.moveTo( xx, yy );
            while ( i.hasNext() ) {
                p = (Point) i.next();
                xx = (float) gt.getDestX( p.getX() );
                yy = (float) gt.getDestY( p.getY() );
                path.lineTo( xx, yy );
            }
        }

        path.lineTo( currentX, currentY );

        return path;
    }

    /**
     * 
     * @param width
     *            in pixel
     */
    public void setLineWidth( float width ) {
        stroke = new BasicStroke( width );
    }

}
