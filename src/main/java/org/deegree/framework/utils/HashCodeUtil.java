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

import java.lang.reflect.Array;

/**
 * Collected methods which allow easy implementation of <code>hashCode</code>.
 * 
 * Example use case:
 * 
 * <pre>
 * public int hashCode() {
 *     int result = HashCodeUtil.SEED;
 *     //collect the contributions of various fields
 *     result = HashCodeUtil.hash( result, fPrimitive );
 *     result = HashCodeUtil.hash( result, fObject );
 *     result = HashCodeUtil.hash( result, fArray );
 *     return result;
 * }
 * </pre>
 * copyright: http://www.javapractices.com/topic/TopicAction.do?Id=28
 */
public final class HashCodeUtil {

    /**
     * An initial value for a <code>hashCode</code>, to which is added contributions from fields. Using a non-zero value
     * decreases collisions of <code>hashCode</code> values.
     */
    public static final int SEED = 23;

    /**
     * booleans.
     */
    public static int hash( int aSeed, boolean aBoolean ) {
        return firstTerm( aSeed ) + ( aBoolean ? 1 : 0 );
    }

    /**
     * chars.
     */
    public static int hash( int aSeed, char aChar ) {
        return firstTerm( aSeed ) + (int) aChar;
    }

    /**
     * ints.
     */
    public static int hash( int aSeed, int aInt ) {
        /*
         * Implementation Note Note that byte and short are handled by this method, through implicit conversion.
         */
        return firstTerm( aSeed ) + aInt;
    }

    /**
     * longs.
     */
    public static int hash( int aSeed, long aLong ) {
        return firstTerm( aSeed ) + (int) ( aLong ^ ( aLong >>> 32 ) );
    }

    /**
     * floats.
     */
    public static int hash( int aSeed, float aFloat ) {
        return hash( aSeed, Float.floatToIntBits( aFloat ) );
    }

    /**
     * doubles.
     */
    public static int hash( int aSeed, double aDouble ) {
        return hash( aSeed, Double.doubleToLongBits( aDouble ) );
    }

    /**
     * <code>aObject</code> is a possibly-null object field, and possibly an array.
     * 
     * If <code>aObject</code> is an array, then each element may be a primitive or a possibly-null object.
     */
    public static int hash( int aSeed, Object aObject ) {
        int result = aSeed;
        if ( aObject == null ) {
            result = hash( result, 0 );
        } else if ( !isArray( aObject ) ) {
            result = hash( result, aObject.hashCode() );
        } else {
            int length = Array.getLength( aObject );
            for ( int idx = 0; idx < length; ++idx ) {
                Object item = Array.get( aObject, idx );
                // recursive call!
                result = hash( result, item );
            }
        }
        return result;
    }

    // / PRIVATE ///
    private static final int fODD_PRIME_NUMBER = 37;

    private static int firstTerm( int aSeed ) {
        return fODD_PRIME_NUMBER * aSeed;
    }

    private static boolean isArray( Object aObject ) {
        return aObject.getClass().isArray();
    }
}
