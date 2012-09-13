/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-20012 by:
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

package org.deegree.model;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

/**
 * Test Class for <code>DashArray</code>.
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class IdentifierTest {

    @Test
    public void test_equals_with_same_Identifier()
                            throws URISyntaxException {
        // arrange
        String name = "testIdentifier";
        URI namespace = new URI( "org.deegree.TestIdentifier" );
        Identifier id = new Identifier( name, namespace );
        // act
        int hashCode1 = id.hashCode();
        int hashCode2 = id.hashCode();
        // assert
        assertTrue( id.equals( id ) );
        assertTrue( hashCode1 == hashCode2 );
    }

    @Test
    public void test_equals_with_same_values()
                            throws URISyntaxException {
        // arrange
        String name = "testIdentifier";
        URI namespace = new URI( "org.deegree.TestIdentifier" );
        Identifier id1 = new Identifier( name, namespace );
        Identifier id2 = new Identifier( name, namespace );
        // act
        int hashCode1 = id1.hashCode();
        int hashCode2 = id2.hashCode();
        // assert
        assertTrue( id1.equals( id2 ) );
        assertTrue( hashCode1 == hashCode2 );
    }

    @Test
    public void test_equals_with_same_name_and_namespace()
                            throws URISyntaxException {
        // arrange
        String name = "testIdentifier";
        URI namespace1 = new URI( "org.deegree.TestIdentifier1" );
        URI namespace2 = new URI( "org.deegree.TestIdentifier2" );
        Identifier id1 = new Identifier( name, namespace1 );
        Identifier id2 = new Identifier( name, namespace2 );
        // act
        int hashCode1 = id1.hashCode();
        int hashCode2 = id2.hashCode();
        // assert
        assertFalse( id1.equals( id2 ) );
        assertFalse( hashCode1 == hashCode2 );
    }

    @Test
    public void test_equals_with_different_name_and_same_namespace()
                            throws URISyntaxException {
        // arrange
        String name1 = "testIdentifier1";
        String name2 = "testIdentifier2";
        URI namespace = new URI( "org.deegree.TestIdentifier" );
        Identifier id1 = new Identifier( name1, namespace );
        Identifier id2 = new Identifier( name2, namespace );
        // act
        int hashCode1 = id1.hashCode();
        int hashCode2 = id2.hashCode();
        // assert
        assertFalse( id1.equals( id2 ) );
        assertFalse( hashCode1 == hashCode2 );
    }

    @Test
    public void test_equals_with_different_values()
                            throws URISyntaxException {
        // arrange
        String name1 = "testIdentifier1";
        String name2 = "testIdentifier2";
        URI namespace1 = new URI( "org.deegree.TestIdentifier1" );
        URI namespace2 = new URI( "org.deegree.TestIdentifier2" );
        Identifier id1 = new Identifier( name1, namespace1 );
        Identifier id2 = new Identifier( name2, namespace2 );
        // act
        int hashCode1 = id1.hashCode();
        int hashCode2 = id2.hashCode();
        // assert
        assertFalse( id1.equals( id2 ) );
        assertFalse( hashCode1 == hashCode2 );
    }
}