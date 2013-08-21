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
package org.deegree.desktop.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.deegree.io.DBConnectionPool;
import org.deegree.io.DBPoolException;

/**
 * Manage connections to databases
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class DatabaseConnectionManager {

    /**
     * Test the connection with the given arguments. Throws exception if connection is not valid.
     * 
     * @param driver
     *            the class name of the database driver
     * @param connectionUrl
     *            the jdbc connection url
     * @param user
     *            the user
     * @param password
     *            the password
     * @throws SQLException
     *             if the connection is not valid
     * @throws IllegalArgumentException
     *             if the driver is not supported
     */
    public static void testConnection( String driver, String connectionUrl, String user, String password )
                            throws SQLException, IllegalArgumentException {
        Driver driverClass;
        try {
            driverClass = (Driver) Class.forName( driver ).newInstance();
        } catch ( Exception e ) {
            throw new IllegalArgumentException( "Driver " + driver + " is not supported." );
        }
        DriverManager.registerDriver( driverClass );
        Connection conn = DriverManager.getConnection( connectionUrl, user, password );
        conn.close();
    }

    /**
     * Connect to the database. Throws exception if connection could not be aquired.
     * 
     * @param driver
     *            the class name of the database driver, not <code>null</code>
     * @param connectionUrl
     *            the connection url, not <code>null</code>
     * @param user
     *            the user, may be <code>null</code>
     * @param password
     *            the password, may be <code>null</code>
     * @return a {@link Connection} to the database
     * @throws DBPoolException
     *             if the connection could not be aquired
     */
    public static Connection aquireConnection( String driver, String connectionUrl, String user, String password )
                            throws DBPoolException {
        if ( user == null )
            user = "";
        if ( password == null )
            password = "";
        DBConnectionPool pool = DBConnectionPool.getInstance();
        return pool.acquireConnection( driver, connectionUrl, user, password );
    }

    /**
     * Releases a connection.
     * 
     * @param connection
     *            the connection to release
     * @param driver
     *            the class name of the database driver
     * @param connectionUrl
     *            the connection url
     * @param user
     *            the user
     * @param password
     *            the passord
     * @throws DBPoolException
     *             if an exception occured when the connection is released
     */
    public static void releaseConnection( Connection connection, String driver, String connectionUrl, String user,
                                          String password )
                            throws DBPoolException {
        DBConnectionPool pool = DBConnectionPool.getInstance();
        pool.releaseConnection( connection, driver, connectionUrl, user, password );
    }

    /**
     * Return the connection url for the specific driver. Throws exception if driver is not supported.
     * 
     * @param driver
     *            the database driver label (case-insensitive)
     * @param host
     *            the host
     * @param port
     *            the port
     * @param database
     *            the database name
     * @return the connection url
     * @throws IllegalArgumentException
     *             if the driver is not supported
     */
    public static String getConnectionUrl( String driver, String host, int port, String database ) {
        if ( driver.toLowerCase().indexOf( "postgis" ) > -1 ) {
            return "jdbc:postgresql://" + host + ':' + port + '/' + database;
        } else if ( driver.toLowerCase().indexOf( "oracle" ) > -1 ) {
            return "jdbc:oracle:thin:@" + host + ':' + port + ':' + database;
        } else if ( driver.toLowerCase().indexOf( "mysql" ) > -1 ) {
            return "jdbc:mysql://" + host + ':' + port + '/' + database;
        } else if ( driver.toLowerCase().indexOf( "sqlserver" ) > -1 ) {
            return "jdbc:sqlserver://" + host + ':' + port + ";databaseName=" + database + ";";
        } else {
            throw new IllegalArgumentException( "Driver " + driver + " is not supported." );
        }
    }

    /**
     * 
     * @param label
     *            the label indicating the driver
     * @return the class name of the database driver
     * @throws IllegalArgumentException
     *             if the driver is not supported
     */
    public static String getDriver( String label ) {
        if ( label.toLowerCase().indexOf( "postgis" ) > -1 ) {
            return "org.postgresql.Driver";
        } else if ( label.toLowerCase().indexOf( "oracle" ) > -1 ) {
            return "oracle.jdbc.OracleDriver";
        } else if ( label.toLowerCase().indexOf( "mysql" ) > -1 ) {
            return "com.mysql.jdbc.Driver";
        } else if ( label.toLowerCase().indexOf( "sqlserver" ) > -1 ) {
            return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        } else {
            throw new IllegalArgumentException( "Could not identify driver out of the given label " + label );
        }
    }

}
