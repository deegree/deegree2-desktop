//$HeadURL: svn+ssh://jwanhoff@svn.wald.intevation.org/deegree/deegree2/deegree2-base/trunk/deegree2-core/resources/eclipse/files_template.xml $
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

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Utility class to encrypt and decrypt text.
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author last edited by: $Author: wanhoff $
 * 
 * @version $Revision: 32205 $, $Date: 2011-10-21 11:37:37 +0200 (Fr, 21 Okt 2011) $
 */
public class Encryption {

    private static final ILogger LOG = LoggerFactory.getLogger( Encryption.class );

    private static final String ciph = "DES";

    private static final String passFilename = "/passphrase.properties";

    /**
     * Encrpyts text
     * 
     * @param textToEncrypt
     * @return encrypted text
     */
    public static String encrypt( String textToEncrypt ) {
        return encrypt( textToEncrypt, ciph, passFilename );
    }

    /**
     * Encrpyts text
     * 
     * @param textToEncrypt
     * @return encrypted text
     */
    public static String encrypt( String textToEncrypt, String ciph, String passFilename ) {
        try {
            Cipher cipher = Cipher.getInstance( ciph );
            Key key = loadKey( ciph, passFilename );
            cipher.init( Cipher.ENCRYPT_MODE, key );

            byte[] encrypted = cipher.doFinal( textToEncrypt.getBytes() );
            return new BASE64Encoder().encode( encrypted );
        } catch ( Exception e ) {
            LOG.logError( "Could not encrypt, return original text:" + e.getMessage() );
            return textToEncrypt;
        }
    }

    /**
     * Decrypts text
     * 
     * @param textToDecrypt
     * @return decrypted text
     */
    public static String decrypt( String textToDecrypt ) {
        return decrypt( textToDecrypt, ciph, passFilename );
    }

    /**
     * Decrypts text
     * 
     * @param textToDecryt
     * @param ciph
     * @return decrypted text
     */
    public static String decrypt( String textToDecryt, String ciph, String passFilename ) {
        try {
            Cipher cipher = Cipher.getInstance( ciph );
            Key key = loadKey( ciph, passFilename );
            cipher.init( Cipher.DECRYPT_MODE, key );
            byte[] decrypted = cipher.doFinal( new BASE64Decoder().decodeBuffer( textToDecryt ) );
            return new String( decrypted );
        } catch ( Exception e ) {
            LOG.logError( "Could not decrypt, return original text:" + e.getMessage() );
            return textToDecryt;
        }
    }

    /**
     * loads passphrase to generate key
     * 
     * @param cipher
     * @return key for decryption and encryption, if provided.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     */
    public static Key loadKey( String cipher, String passFilename )
                            throws IOException, InvalidKeyException, InvalidKeySpecException, NoSuchAlgorithmException {
        Properties prop = new Properties();
        InputStream is = Encryption.class.getResourceAsStream( passFilename );
        prop.load( is );
        is.close();
        String pass = prop.getProperty( "passphrase" );
        return new SecretKeySpec( pass.getBytes(), cipher );
    }
}