//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
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
package org.deegree.framework.utils;

import org.deegree.framework.util.Pair;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Position;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class LineUtils {

    /**
     * 
     * @param m
     * @param x
     * @param y
     * @return equation parameters (m',b') for a line that is perpendicular to the line with slop <code>m</code> and
     *         intersecting point given by <code>x</code> and <code>y</code>
     */
    public static double[] getPerpendicularLine( double m, double x, double y ) {
        m = -1 / m;
        double b = y - m * x;
        return new double[] { m, b };
    }

    /**
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return equation parameters (m,b) for line intersecting points given by <code>x1</code>, <code>y1</code>,
     *         <code>x2</code> and <code>y2</code>
     */
    public static double[] getLineFromPoints( double x1, double y1, double x2, double y2 ) {
        double m = ( y2 - y1 ) / ( x2 - x1 );
        double b = -x1 * m + y1;
        return new double[] { m, b };
    }

    /**
     * 
     * @param p1
     * @param p2
     * @return equation parameters (m,b) for line intersecting points given by <code>x1</code>, <code>y1</code>,
     *         <code>x2</code> and <code>y2</code>
     */
    public static double[] getLineFromPoints( Position p1, Position p2 ) {
        return getLineFromPoints( p1.getX(), p1.getY(), p2.getX(), p2.getY() );
    }

    /**
     * 
     * @param m1
     *            slop of first line
     * @param b1
     * @param m2
     *            slop of second line
     * @param b2
     * @return intersection of two lines described by <code>m1</code>, <code>b1</code> and <code>m2</code>,
     *         <code>b2</code>
     */
    public static Position getLineIntersection( double m1, double b1, double m2, double b2 ) {
        double xs = ( b2 - b1 ) / ( m1 - m2 );
        double ys = xs * m1 + b1;
        return GeometryFactory.createPosition( xs, ys );
    }

    /**
     * The line equation is given in the <code>y = mx + n</code> form.
     * 
     * @param x
     * @param y
     * @param m
     *            the slope of the line. Is of type Double as it can be infinite.
     * @param d
     *            distance
     * @return two @see {@link Position}s on the line with distance d from point x/y
     */
    public static Pair<Position, Position> getSymmetricPoints( double x, double y, double m, double d ) {

        if ( new Double( m ).isInfinite() ) {
            Position p1 = GeometryFactory.createPosition( x, y + d );
            Position p2 = GeometryFactory.createPosition( x, y - d );
            return new Pair<Position, Position>( p1, p2 );
        }

        double alpha = Math.atan( m );
        Position p1 = GeometryFactory.createPosition( x + d * Math.cos( alpha ), y + d * Math.sin( alpha ) );
        Position p2 = GeometryFactory.createPosition( x - d * Math.cos( alpha ), y - d * Math.sin( alpha ) );
        return new Pair<Position, Position>( p1, p2 );
    }

}
