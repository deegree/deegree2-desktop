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

package org.deegree.desktop.style.model;

import static org.junit.Assert.*;

import org.deegree.desktop.style.model.SldProperty;
import org.junit.Test;

/**
 * Test Class for <code>SldProperty</code>.
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class SldPropertyTest {

    private static String SLD_NAME = "testSldName";
    private static String SLD_NAME1 = "testSldName1";
    private static String SLD_NAME2 = "testSldName2";

    private static String NAME = "testName";
    private static String NAME1 = "testName1";
    private static String NAME2 = "testName2";
    
    private static int TYPE_CODE = 0;
    private static int TYPE_CODE1 = 1;
    private static int TYPE_CODE2 = 2;

	@Test
	public void test_equals_with_same_SldPropertys() {
		// arrange
		SldProperty sld = new SldProperty(TYPE_CODE, SLD_NAME, NAME);
		// act
		int hashCode1 = sld.hashCode();
		int hashCode2 = sld.hashCode();
		// assert
		assertTrue(sld.equals(sld));
		assertTrue(hashCode1 == hashCode2);
	}

	@Test
	public void test_equals_with_same_properties() {
		// arrange
		SldProperty sld1 = new SldProperty(TYPE_CODE, SLD_NAME, NAME);
		SldProperty sld2 = new SldProperty(TYPE_CODE, SLD_NAME, NAME);
		// act
		int hashCode1 = sld1.hashCode();
		int hashCode2 = sld2.hashCode();
		// assert
		assertTrue(sld1.equals(sld2));
		assertTrue(hashCode1 == hashCode2);
	}

	@Test
	public void test_equals_with_different_sldName() {
		// arrange
		SldProperty sld1 = new SldProperty(TYPE_CODE, SLD_NAME1, NAME);
		SldProperty sld2 = new SldProperty(TYPE_CODE, SLD_NAME2, NAME);
		// act
		int hashCode1 = sld1.hashCode();
		int hashCode2 = sld2.hashCode();
		// assert
		assertFalse(sld1.equals(sld2));
		assertFalse(hashCode1 == hashCode2);
	}

	@Test
	public void test_equals_with_different_name() {
		// arrange
		SldProperty sld1 = new SldProperty(TYPE_CODE, SLD_NAME, NAME1);
		SldProperty sld2 = new SldProperty(TYPE_CODE, SLD_NAME, NAME2);
		// act
		int hashCode1 = sld1.hashCode();
		int hashCode2 = sld2.hashCode();
		// assert
		assertFalse(sld1.equals(sld2));
		assertFalse(hashCode1 == hashCode2);
	}

	@Test
	public void test_equals_with_different_typeCode() {
		// arrange
		SldProperty sld1 = new SldProperty(TYPE_CODE1, SLD_NAME, NAME);
		SldProperty sld2 = new SldProperty(TYPE_CODE2, SLD_NAME, NAME);
		// act
		int hashCode1 = sld1.hashCode();
		int hashCode2 = sld2.hashCode();
		// assert
		assertFalse(sld1.equals(sld2));
		assertFalse(hashCode1 == hashCode2);
	}

	@Test
	public void test_equals_with_different_values() {
		// arrange
		SldProperty sld1 = new SldProperty(TYPE_CODE1, SLD_NAME1, NAME1);
		SldProperty sld2 = new SldProperty(TYPE_CODE2, SLD_NAME2, NAME2);
		// act
		int hashCode1 = sld1.hashCode();
		int hashCode2 = sld2.hashCode();
		// assert
		assertFalse(sld1.equals(sld2));
		assertFalse(hashCode1 == hashCode2);
	}
	
}