//$HeadURL$
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

package org.deegree.igeo;

import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class Version {

    private static String versionNumber;

    private static String versionDate;

    private static String svn;

    static {
        if ( versionNumber == null ) {
            try {
                Properties prop = new Properties();
                InputStream is = Version.class.getResourceAsStream( "version.properties" );
                prop.load( is );
                is.close();
                versionNumber = prop.getProperty( "version.number" );
                versionDate = prop.getProperty( "version.date" );
                svn = prop.getProperty( "version.svn.revision" );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return the versionDate
     */
    public static String getVersionDate() {
        return versionDate;
    }

    /**
     * @return the versionNumber
     */
    public static String getVersionNumber() {
        return versionNumber;
    }

    /**
     * 
     * @return svn revision number
     */
    public static String getSVNRevison() {
        return svn;
    }

}
