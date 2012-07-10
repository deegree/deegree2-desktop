//$HeadURL: svn+ssh://developername@svn.wald.intevation.org/deegree/base/trunk/src/org/deegree/i18n/Messages.java $
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2008 by:
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
package org.deegree.igeo.i18n;

import static java.util.Locale.getDefault;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.deegree.framework.util.BootLogger;

/**
 * Responsible for the access to messages that are visible to the user.
 * <p>
 * Messages are read from the properties file <code>messages_LANG.properties</code> (LANG is always a lowercased ISO 639
 * code), so internationalization is supported. If a certain property (or the property file) for the specific default
 * language of the system is not found, the message is taken from <code>messages_en.properties</code>.
 * 
 * @see Locale#getLanguage()
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: mschneider $
 * 
 * @version $Revision: 7740 $, $Date: 2007-07-09 16:10:16 +0200 (Mo, 09 Jul 2007) $
 */
public class Messages {

    private static Map<String, Properties> propertyMap = new HashMap<String, Properties>();

    /**
     * Initialization done at class loading time.
     */
    static {
        try {
            Locale[] locales = Locale.getAvailableLocales();

            // load all messages from default file ("org/deegree/i18n/message_en.properties")
            String fileName = "messages_en.properties";
            InputStream is = Messages.class.getResourceAsStream( fileName );
            if ( is == null ) {
                BootLogger.log( "Error while initializing " + Messages.class.getName() + " :  default message file: '"
                                + fileName + " not found." );
            }
            is = Messages.class.getResourceAsStream( fileName );
            Properties props = new Properties();
            props.load( is );
            is.close();
            propertyMap.put( "en", props );

            if ( !Locale.getDefault().getLanguage().equals( "en" ) ) {
                is = Messages.class.getResourceAsStream( "messages_" + Locale.getDefault().getLanguage()
                                                         + ".properties" );
                if ( is != null ) {
                    // override default messages
                    Properties overrideProps = new Properties();
                    overrideProps.load( is );
                    is.close();
                    props = propertyMap.get( Locale.getDefault().getLanguage() );
                    if ( props != null ) {
                        Iterator<?> iter = overrideProps.keySet().iterator();
                        while ( iter.hasNext() ) {
                            String key = (String) iter.next();
                            props.put( key, overrideProps.get( key ) );
                        }
                    } else {
                        propertyMap.put( Locale.getDefault().getLanguage(), overrideProps );
                    }
                }

            }

            for ( int i = 0; i < locales.length; i++ ) {
                Messages.overrideMessages( locales[i].getLanguage() );
            }
            overrideMessages();
        } catch ( IOException e ) {
            BootLogger.logError( "Error while initializing " + Messages.class.getName() + " : " + e.getMessage(), e );
        }
    }

    /**
     * overrides messages from a properties file read from URl defined in System properties -Dmessages
     */
    private static void overrideMessages() {
        String m = System.getProperty( "messages" );
        if ( m != null ) {
            try {
                URL url = new URL( m );
                InputStream is = url.openStream();
                // override default messages
                Properties overrideProps = new Properties();
                overrideProps.load( is );
                is.close();
                Collection<Properties> pp = propertyMap.values();
                for ( Properties properties : pp ) {
                    Iterator<?> iter = overrideProps.keySet().iterator();
                    while ( iter.hasNext() ) {
                        String key = (String) iter.next();
                        properties.put( key, overrideProps.get( key ) );
                    }
                }
            } catch ( Exception e ) {
                BootLogger.logError( e.getMessage(), e );
            }
        }
    }

    private static void overrideMessages( String lang )
                            throws IOException {
        InputStream is = Messages.class.getResourceAsStream( "/messages_" + lang + ".properties" );
        if ( is != null ) {
            // override default messages
            Properties overrideProps = new Properties();
            overrideProps.load( is );
            is.close();
            Properties props = propertyMap.get( lang );
            if ( props != null ) {
                Iterator<?> iter = overrideProps.keySet().iterator();
                while ( iter.hasNext() ) {
                    String key = (String) iter.next();
                    props.put( key, overrideProps.get( key ) );
                }
            } else {
                propertyMap.put( lang, overrideProps );
            }
        }
    }

    /**
     * Returns the message assigned to the passed key. If no message is assigned, an error message will be returned that
     * indicates the missing key. First the method tries to access messages in the local language if no messages
     * available for this language english messages will be used as default
     * 
     * @see MessageFormat for conventions on string formatting and escape characters.
     * 
     * @param key
     * @param arguments
     * @return the message assigned to the passed key
     * @deprecated use
     * @see #getMessage(Locale, String, Object[] ) instead
     */
    @Deprecated
    public static String getMessage( String key, Object... arguments ) {
        Properties props = propertyMap.get( Locale.getDefault().getLanguage() );
        if ( props == null ) {
            props = propertyMap.get( "en" );
        }
        String s = props.getProperty( key );
        if ( s == null ) {
            props = propertyMap.get( "en" );
            s = props.getProperty( key );
        }
        if ( s != null ) {
            return MessageFormat.format( s, arguments );
        }

        // to avoid NPEs
        return "$Message with key: " + key + " not found$";
    }

    /**
     * same as
     * 
     * @see #getMessage(String, Object[] ) just passing desired language. If no messages are defined for passed language
     *      'en' will be used as default.
     * @param locale
     * @param key
     * @param arguments
     * @return message assigned to the passed key
     */
    public static String getMessage( Locale locale, String key, Object... arguments ) {
        Properties props = propertyMap.get( locale.getLanguage() );
        if ( props == null ) {
            props = propertyMap.get( "en" );
        }
        String s = props.getProperty( key );
        if ( s == null ) {
            props = propertyMap.get( "en" );
            s = props.getProperty( key );
        }
        if ( s != null ) {
            return MessageFormat.format( s, arguments );
        }

        // to avoid NPEs
        return "$Message with key: " + key + " not found$";
    }

    /**
     * Convenience method with a shorter name. Uses Locale.getDefault as locale.
     * 
     * @param key
     * @param arguments
     * @return the message
     * @see #getMessage(Locale, String, Object[])
     */
    public static String get( String key, Object... arguments ) {
        return getMessage( getDefault(), key, arguments );
    }

}
