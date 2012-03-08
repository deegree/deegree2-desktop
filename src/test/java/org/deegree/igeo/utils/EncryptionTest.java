//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2010 by:
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
package org.deegree.igeo.utils;

import static org.junit.Assert.*;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Assert;
import org.junit.Test;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class EncryptionTest {

    @Test
    public void encryptWithNoPropertiesFileTest() {
        // arrange / build
        String origText = "text to encrypt";
        String expText = origText;
        // act / operate
        String encText = Encryption.encrypt( origText, "DES", "ThisFileDoesNotExist" );
        // assert / check
        Assert.assertEquals( expText, encText );
    }


    @Test
    public void encryptWithPropertiesFileTest() {
        // arrange / build
        String origText = "original text";
        String expText = "MTWjwR8LsmcZ3eFlFJBrug==";
        // act / operate
        String encText = Encryption.encrypt( origText );
        // assert / check
        Assert.assertEquals( expText, encText );
    }
    
    @Test
    public void decryptWithNoPropertiesFileTest() {
        // arrange / build
        String origText = "text to decrypt";
        String expText = origText;
        // act / operate
        String encText = Encryption.decrypt( origText, "DES", "ThisFileDoesNotExist" );
        // assert / check
        Assert.assertEquals( expText, encText );
    }


    @Test
    public void decryptWithPropertiesFileTest() {
        // arrange / build
        String origText = "MTWjwR8LsmcZ3eFlFJBrug==";
        String expText = "original text";
        // act / operate
        String decText = Encryption.decrypt( origText );
        // assert / check
        Assert.assertEquals( expText, decText );
    }
}
