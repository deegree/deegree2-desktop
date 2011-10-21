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
package org.deegree.igeo.dataadapter;

import java.util.UUID;

import org.deegree.model.feature.ValueGenerator;

/**
 * Implements interface {@link ValueGenerator} by creating random strings  (no uuids)
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class RandomStringValueGenerator implements ValueGenerator {
    private int maxLength = 10;

    protected static java.util.Random r = new java.util.Random();

    /**
     * 
     * @param maxLength if >= 32 a UUID will be created
     */
    public RandomStringValueGenerator( int maxLength ) {
        this.maxLength = maxLength;
    }

    /*
     * Set of characters that is valid. Must be printable, memorable, and "won't break HTML" (i.e., not ' <', '>', '&',
     * '=', ...). or break shell commands (i.e., not ' <', '>', '$', '!', ...). I, L and O are good to leave out, as are
     * numeric zero and one.
     */
    protected static char[] goodChar = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
                                        'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E',
                                        'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
                                        'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '-',
                                        '_' };

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.model.feature.ValueGenerator#generate()
     */
    public Object generate() {
        if ( maxLength >= 32 ) {
            return UUID.randomUUID().toString();
        } else {
            StringBuffer sb = new StringBuffer();
            for ( int i = 0; i < maxLength; i++ ) {
                sb.append( goodChar[r.nextInt( goodChar.length )] );
            }
            return sb.toString();
        }
    }

}
