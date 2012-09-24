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

package org.deegree.igeo.style.model;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test Class for <code>WellKnownMark</code>.
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class WellKnownMarkTest {

    private static String wkmName = "wkmName";

    private static String wkmName1 = "wkmName1";

    private static String wkmName2 = "wkmName2";

    private static String sldName = "sldName";

    private static String sldName1 = "sldName1";

    private static String sldName2 = "sldName2";

    @Test
    public void test_equals_with_same_WellKnownMark() {
        // arrange
        WellKnownMark wkm = new WellKnownMark( wkmName, sldName );
        // act
        int hashCode1 = wkm.hashCode();
        int hashCode2 = wkm.hashCode();
        // assert
        assertTrue( wkm.equals( wkm ) );
        assertTrue( hashCode1 == hashCode2 );
    }

    @Test
    public void test_equals_with_same_values() {
        // arrange
        WellKnownMark wkm1 = new WellKnownMark( wkmName, sldName );
        WellKnownMark wkm2 = new WellKnownMark( wkmName, sldName );
        // act
        int hashCode1 = wkm1.hashCode();
        int hashCode2 = wkm2.hashCode();
        // assert
        assertTrue( wkm1.equals( wkm2 ) );
        assertTrue( hashCode1 == hashCode2 );
    }

    @Test
    public void test_equals_with_same_name_and_diffenent_sldName() {
        // arrange
        WellKnownMark wkm1 = new WellKnownMark( wkmName, sldName1 );
        WellKnownMark wkm2 = new WellKnownMark( wkmName, sldName2 );
        // act
        int hashCode1 = wkm1.hashCode();
        int hashCode2 = wkm2.hashCode();
        // assert
        assertFalse( wkm1.equals( wkm2 ) );
        assertFalse( hashCode1 == hashCode2 );
    }

    @Test
    public void test_equals_with_different_name_and_same_sldName() {
        // arrange
        WellKnownMark wkm1 = new WellKnownMark( wkmName1, sldName );
        WellKnownMark wkm2 = new WellKnownMark( wkmName2, sldName );
        // act
        int hashCode1 = wkm1.hashCode();
        int hashCode2 = wkm2.hashCode();
        // assert
        assertFalse( wkm1.equals( wkm2 ) );
        assertFalse( hashCode1 == hashCode2 );
    }

    @Test
    public void test_equals_with_different_values() {
        // arrange
        WellKnownMark wkm1 = new WellKnownMark( wkmName1, sldName1 );
        WellKnownMark wkm2 = new WellKnownMark( wkmName2, sldName2 );
        // act
        int hashCode1 = wkm1.hashCode();
        int hashCode2 = wkm2.hashCode();
        // assert
        assertFalse( wkm1.equals( wkm2 ) );
        assertFalse( hashCode1 == hashCode2 );
    }
}