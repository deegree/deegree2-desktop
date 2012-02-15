//$HeadURL: svn+ssh://lbuesching@svn.wald.intevation.de/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2012 by:
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
package org.deegree.igeo.config;

import java.util.Locale;

import org.deegree.framework.util.StringTools;
import org.deegree.igeo.i18n.Messages;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class DatabaseDriverUtils {

    // TODO: is'nt it part of the project settings?
    
    public static String getDriver( String label ) {
        label = label.toLowerCase();
        String driver = null;
        if ( label.indexOf( "postgis" ) > -1 ) {
            driver = "org.postgresql.Driver";
        } else if ( label.indexOf( "oracle" ) > -1 ) {
            driver = "oracle.jdbc.OracleDriver";
        } else if ( label.indexOf( "mysql" ) > -1 ) {
            driver = "com.mysql.jdbc.Driver";
        } else if ( label.indexOf( "sqlserver" ) > -1 ) {
            driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        }
        return driver;
    }

    public static String[] getDriverLabels() {
        String s = Messages.getMessage( Locale.getDefault(), "$MD11542" );
        return StringTools.toArray( s, ",;", false );
    }
}
