/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2012 by:
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

 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: info@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/

package org.deegree.desktop.style.model.classification;

import java.awt.Color;

import java.util.Comparator;

import javax.vecmath.Point2d;

import org.deegree.desktop.style.model.GraphicSymbol;
import org.deegree.desktop.style.model.classification.Column.COLUMNTYPE;
import org.deegree.model.filterencoding.PropertyName;

/**
 * <code>ClassificationTableRowComparator</code> TODO add class documentation
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Lyn Buesching</a>
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class ClassificationTableRowComparator<U extends Comparable<U>> implements Comparator<ClassificationTableRow<U>> {

    private COLUMNTYPE selectedColumn;

    public ClassificationTableRowComparator( COLUMNTYPE type ) {
        selectedColumn = type;
    }

    /**
     * compares two ClassificationTableRows dependendent from the given column
     * 
     * 
     */
    // f < s -> -
    // f > s -> +
    public int compare( ClassificationTableRow<U> o1, ClassificationTableRow<U> o2 ) {
    	Boolean o1IsNull = Boolean.valueOf(o1 == null);
    	Boolean o2IsNull = Boolean.valueOf(o2 == null);
    	if ( o1IsNull && o2IsNull ) {
            return 0;
        } else if ( o1IsNull ) {
            return 1;
        } else if ( o2IsNull ) {
            return -1;
        }
    	
        Object value1 = o1.getValue( selectedColumn );
        Object value2 = o1.getValue( selectedColumn );
        Boolean value1IsNull = Boolean.valueOf(value1 == null);
        Boolean value2IsNull = Boolean.valueOf(value2 == null);        
        if ( value1IsNull && value2IsNull ) {
            return 0;
        } else if ( value1IsNull ) {
            return 1;
        } else if ( value2IsNull ) {
            return -1;
        }
        
        if ( value1 instanceof PropertyName && value2 instanceof PropertyName ) {
        	Boolean value1StringIsNull = Boolean.valueOf(((PropertyName) value1 ).toString() == null);
        	Boolean value2StringIsNull = Boolean.valueOf(((PropertyName) value2 ).toString() == null);
        	if ( value1StringIsNull && value2StringIsNull ) {
        		return 0;
        	} else if ( value1StringIsNull ) {
                return 1;
            } else if ( value2StringIsNull ) {
                return -1;
            } else {
            	return ( (PropertyName) value1 ).toString().compareTo( ( (PropertyName) value2 ).toString() );
            }
        }

        switch ( selectedColumn ) {
        case VALUE:
            return o1.getValue().compareTo( o2.getValue() );
        case FILLCOLOR:
            return compareColors( o1.getFillColor(), o2.getFillColor() );
        case FILLTRANSPARENCY:
            return ( (Integer) o1.getFillTransparency() ).compareTo( o2.getFillTransparency() );
        case LINECOLOR:
            return compareColors( o1.getLineColor(), o2.getLineColor() );
        case LINETRANSPARENCY:
            return ( (Integer) o1.getLineTransparency() ).compareTo( o2.getLineTransparency() );
        case LINEWIDTH:
            return ( (Double) o1.getLineWidth() ).compareTo( o2.getLineWidth() );
        case SIZE:
            return ( (Double) o1.getSize() ).compareTo( o2.getSize() );
        case FONTFAMILY:
        case FONTSTYLE:
        case FONTWEIGHT:
            if ( value1 instanceof String && value2 instanceof String ) {
                return ( (String) value1 ).compareTo( (String) ( value2 ) );
            }
            break;
        case FONTSIZE:
        case HALORADIUS:
        case FONTTRANSPARENCY:
        case ROTATION:
            if ( value1 instanceof Double && value2 instanceof Double ) {
                return ( (Double) value1 ).compareTo( (Double) ( value2 ) );
            }
            break;
        case ANCHORPOINT:
        case DISPLACEMENT:
            if ( value1 instanceof Point2d && value2 instanceof Point2d ) {
                if ( ( (Point2d) value1 ).x != ( (Point2d) value2 ).x ) {
                    return ( (Double) ( (Point2d) value1 ).x ).compareTo( ( (Point2d) value2 ).x );
                } else {
                    return ( (Double) ( (Point2d) value1 ).y ).compareTo( ( (Point2d) value2 ).y );
                }
            }
            break;
        case HALOCOLOR:
        case FONTCOLOR:
            if ( value1 instanceof Color && value2 instanceof Color ) {
                return compareColors( (Color) value1, (Color) value2 );
            }
            break;
        case COUNT:
            return ( (Integer) o1.getValue().getCount() ).compareTo( o2.getValue().getCount() );
        }
        return 0;
    }

    private int compareColors( Object c1, Object c2 ) {
        if ( c1 instanceof Color && c2 instanceof Color ) {
            Color co1 = (Color) c1;
            Color co2 = (Color) c2;
            int r1 = co1.getRed();
            int g1 = co1.getGreen();
            int b1 = co1.getBlue();

            int r2 = co2.getRed();
            int g2 = co2.getGreen();
            int b2 = co2.getBlue();

            if ( r1 < r2 ) {
                return 1;
            } else if ( r1 == r2 ) {
                if ( g1 < g2 ) {
                    return 1;
                } else if ( g1 == g2 ) {
                    if ( b1 < b2 ) {
                        return 1;
                    } else if ( b1 == b2 ) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        } else if ( c1 instanceof Color && c2 instanceof GraphicSymbol ) {
            return -1;
        } else if ( c1 instanceof GraphicSymbol && c2 instanceof Color ) {
            return 1;
        }
        return 0;
    }
}
