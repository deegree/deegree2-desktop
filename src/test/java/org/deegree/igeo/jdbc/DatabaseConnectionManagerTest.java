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
package org.deegree.igeo.jdbc;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;

import org.deegree.io.DBPoolException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Test the {@link DatabaseConnectionManager}
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class DatabaseConnectionManagerTest {

    private static String driverLabel;

    private static String driverClass;

    private static String host;

    private static int port;

    private static String database;

    private static String user;

    private static String password;

    private static String connectionUrl;

    @BeforeClass
    public static void init() {
        driverLabel = "SqlSerVEr";
        driverClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        host = "sqlserver2008";
        port = 1433;
        database = "deegreedesktop";
        user = "deegreedesktop";
        password = "deegreedesktop";
        connectionUrl = "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + database + ";";
        // TODO:suer/password required?
        // ";user=" + user + ";password=" + password;
        // "jdbc:postgresql://" + host + ':' + port + '/' + database;
    }

    @Category(DatabaseTest.class)
    @Test
    public void testTestConnection()
                            throws IllegalArgumentException, SQLException {
        DatabaseConnectionManager.testConnection( driverClass, connectionUrl, user, password );
    }

    @Category(DatabaseTest.class)
    @Test(expected = IllegalArgumentException.class)
    public void testTestConnectionWithUnknownDriver()
                            throws IllegalArgumentException, SQLException {
        DatabaseConnectionManager.testConnection( "NotAKnownDriver", connectionUrl, user, password );
    }

    @Category(DatabaseTest.class)
    @Test(expected = SQLException.class)
    public void testTestConnectionWhereConnectionUrlIsNull()
                            throws IllegalArgumentException, SQLException {
        DatabaseConnectionManager.testConnection( driverClass, null, user, password );
    }

    @Category(DatabaseTest.class)
    @Test(expected = IllegalArgumentException.class)
    public void testTestConnectionWhereDriverIsNull()
                            throws IllegalArgumentException, SQLException {
        DatabaseConnectionManager.testConnection( null, connectionUrl, user, password );
    }

    @Category(DatabaseTest.class)
    @Test
    public void testAquireConnection()
                            throws DBPoolException {
        Connection connection = DatabaseConnectionManager.aquireConnection( driverClass, connectionUrl, user, password );
        assertNotNull( connection );
    }

    @Category(DatabaseTest.class)
    @Test(expected = DBPoolException.class)
    public void testAquireConnectionWithUnkownDriver()
                            throws DBPoolException {
        DatabaseConnectionManager.aquireConnection( "NotAKnownDriver", connectionUrl, user, password );
    }

    @Category(DatabaseTest.class)
    @Test(expected = DBPoolException.class)
    public void testAquireConnectionWhereConnectionUrlIsNull()
                            throws DBPoolException {
        Connection connection = DatabaseConnectionManager.aquireConnection( driverClass, null, user, password );
        assertNotNull( connection );
    }

    @Category(DatabaseTest.class)
    @Test(expected = DBPoolException.class)
    public void testAquireConnectionWherDriverIsNull()
                            throws DBPoolException {
        Connection connection = DatabaseConnectionManager.aquireConnection( null, connectionUrl, user, password );
        assertNotNull( connection );
    }

    @Category(DatabaseTest.class)
    @Test
    public void testReleaseConnection()
                            throws DBPoolException {
        Connection connection = DatabaseConnectionManager.aquireConnection( driverClass, connectionUrl, user, password );
        DatabaseConnectionManager.releaseConnection( connection, driverClass, connectionUrl, user, password );
    }

    @Test
    public void testGetConnectionUrl() {
        String connUrl = DatabaseConnectionManager.getConnectionUrl( driverLabel, host, port, database );
        assertEquals( connectionUrl, connUrl );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetConnectionUrlWithUnkownDriver() {
        DatabaseConnectionManager.getConnectionUrl( "NotAKnownDriver", host, port, database );
    }

    @Test
    public void testGetDriver() {
        String driver = DatabaseConnectionManager.getDriver( driverLabel );
        assertEquals( driverClass, driver );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetDriverWithUnkownDriver() {
        DatabaseConnectionManager.getDriver( "NotAKnownDriver" );
    }
}
