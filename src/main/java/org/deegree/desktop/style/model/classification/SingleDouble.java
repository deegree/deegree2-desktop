/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2007 by:
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

package org.deegree.desktop.style.model.classification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>SingleDouble</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class SingleDouble implements DoubleRange {

    private double value;

    public SingleDouble( double value ) {
        this.value = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.model.classification.DoubleRange#getDoubles(int)
     */
    public List<Double> getDoubles( int count ) {
        List<Double> result = new ArrayList<Double>( count );
        for ( int i = 0; i < count; i++ ) {
            result.add( value );
        }
        return result;
    }

    public List<Double> getDoubles( int count, int decimalPlace ) {
        List<Double> result = new ArrayList<Double>( count );
        for ( int i = 0; i < count; i++ ) {
            BigDecimal bc = new BigDecimal( value );
            result.add( bc.setScale( decimalPlace, BigDecimal.ROUND_HALF_UP ).doubleValue() );
        }
        return result;
    }

    public double getMax() {
        return value;
    }

    public double getMin() {
        return value;
    }

    @Override
    public String toString() {
        return Double.toString( value );
    }

}
