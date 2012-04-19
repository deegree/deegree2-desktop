//$HeadURL: svn+ssh://aschmitz@wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2011 by:
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
package org.deegree.igeo.modules.georef;

import georegression.fitting.affine.MotionAffinePoint2D_F64;
import georegression.struct.affine.Affine2D_F64;
import georegression.struct.point.Point2D_F64;
import georegression.transform.affine.AffinePointOps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.deegree.igeo.modules.georef.ControlPointModel.Point;

/**
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: stranger $
 * 
 * @version $Revision: $, $Date: $
 */
public class AffineTransformation {

    public static double[] approximate( List<Point> points ) {
        int n = points.size() - 1;
        if ( n < 3 ) {
            return null;
        }

        List<Point2D_F64> list1 = new ArrayList<Point2D_F64>( n );
        List<Point2D_F64> list2 = new ArrayList<Point2D_F64>( n );
        Iterator<Point> iter = points.iterator();
        for ( int i = 0; i < n; ++i ) {
            Point p = iter.next();
            Point2D_F64 p1 = new Point2D_F64( p.x0, p.y0 );
            Point2D_F64 p2 = new Point2D_F64( p.x1, p.y1 );
            list1.add( p1 );
            list2.add( p2 );
        }

        MotionAffinePoint2D_F64 m = new MotionAffinePoint2D_F64();
        m.process( list1, list2 );
        Affine2D_F64 t = m.getMotion();

        // calculate residuals
        Iterator<Point2D_F64> iter1 = list1.iterator();
        Iterator<Point2D_F64> iter2 = list2.iterator();
        iter = points.iterator();

        while ( iter1.hasNext() ) {
            Point p = iter.next();
            Point2D_F64 p1 = iter1.next();
            Point2D_F64 p2 = iter2.next();
            Point2D_F64 newp = AffinePointOps.transform( t, p1, null );
            p.resx = p2.x - newp.x;
            p.resy = p2.y - newp.y;
        }

        return new double[] { t.a11, t.a12, t.a21, t.a22, t.tx, t.ty };
    }

}
