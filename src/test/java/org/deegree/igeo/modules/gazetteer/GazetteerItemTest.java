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
package org.deegree.igeo.modules.gazetteer;

import java.util.Arrays;

import junit.framework.TestCase;

import org.deegree.framework.util.StringPair;
import org.junit.Test;

/**
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: stranger $
 * 
 * @version $Revision: $, $Date: $
 */
public class GazetteerItemTest extends TestCase {

    @Test
    public void testSplitParts() {
        StringPair p = GazetteerItem.splitGeographicIdentifier( "1" );
        assertEquals( "Unexpected digit part", "1", p.first );
        assertEquals( "Unexpected string part", "", p.second );
        p = GazetteerItem.splitGeographicIdentifier( "10" );
        assertEquals( "Unexpected digit part", "10", p.first );
        assertEquals( "Unexpected string part", "", p.second );
        p = GazetteerItem.splitGeographicIdentifier( "1a" );
        assertEquals( "Unexpected digit part", "1", p.first );
        assertEquals( "Unexpected string part", "a", p.second );
        p = GazetteerItem.splitGeographicIdentifier( "10 a" );
        assertEquals( "Unexpected digit part", "10", p.first );
        assertEquals( "Unexpected string part", "a", p.second );
        p = GazetteerItem.splitGeographicIdentifier( " 10 a " );
        assertEquals( "Unexpected digit part", "10", p.first );
        assertEquals( "Unexpected string part", "a", p.second );
    }

    @Test
    public void testSortItems() {
        GazetteerItem[] items = new GazetteerItem[6];
        // 1, 1a, 2, 3, ..., 9, 10
        items[0] = new GazetteerItem( null, "10", null, null, null, null, null, null );
        items[1] = new GazetteerItem( null, "3", null, null, null, null, null, null );
        items[2] = new GazetteerItem( null, "2", null, null, null, null, null, null );
        items[3] = new GazetteerItem( null, "1a", null, null, null, null, null, null );
        items[4] = new GazetteerItem( null, "9", null, null, null, null, null, null );
        items[5] = new GazetteerItem( null, "1", null, null, null, null, null, null );
        Arrays.sort( items );
        assertEquals( "Unexpected sort order", "1", items[0].getGeographicIdentifier() );
        assertEquals( "Unexpected sort order", "1a", items[1].getGeographicIdentifier() );
        assertEquals( "Unexpected sort order", "2", items[2].getGeographicIdentifier() );
        assertEquals( "Unexpected sort order", "3", items[3].getGeographicIdentifier() );
        assertEquals( "Unexpected sort order", "9", items[4].getGeographicIdentifier() );
        assertEquals( "Unexpected sort order", "10", items[5].getGeographicIdentifier() );
    }

}
