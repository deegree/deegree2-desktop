//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
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
package org.deegree.igeo.dataadapter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.Pair;
import org.deegree.igeo.config.LinkedDatabaseTableType;
import org.deegree.io.DBConnectionPool;
import org.deegree.io.DBPoolException;

/**
 * concrete {@link LinkedTable} for accessing database tables
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class LinkedDatabaseTable extends LinkedTable {

    private static final ILogger LOG = LoggerFactory.getLogger( LinkedDatabaseTable.class );

    private String[] columnNames;

    private int[] types;

    private LinkedDatabaseTableType linkedDB;

    private ResultSet rs;

    private Statement stmt;

    private Connection conn;

    private DBConnectionPool pool;

    private int cursor;

    /**
     * @param linkedTableType
     * @throws Exception
     */
    public LinkedDatabaseTable( LinkedDatabaseTableType linkedTableType ) throws IOException {
        super( linkedTableType );
        linkedDB = (LinkedDatabaseTableType) linkedTableType;
        pool = DBConnectionPool.getInstance();
        init();
    }

    private void init()
                            throws IOException {
        DBConnectionPool pool = DBConnectionPool.getInstance();
        String driver = linkedDB.getConnection().getDriver();
        String url = linkedDB.getConnection().getUrl();
        String user = linkedDB.getConnection().getUser();
        String pw = linkedDB.getConnection().getPassword();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = pool.acquireConnection( driver, url, user, pw );
            stmt = conn.createStatement();
            rs = stmt.executeQuery( linkedDB.getSqlTemplate() + " WHERE 1 = 2" );
            ResultSetMetaData rsmd = rs.getMetaData();
            int cnt = rsmd.getColumnCount();
            types = new int[cnt];
            columnNames = new String[cnt];
            for ( int i = 0; i < cnt; i++ ) {
                columnNames[i] = rsmd.getColumnName( i + 1 );
                types[i] = rsmd.getColumnType( i + 1 );
            }
        } catch ( Exception e ) {
            LOG.logError( e );
            throw new IOException( e.getMessage() );
        } finally {
            try {
                rs.close();
                stmt.close();
            } catch ( Exception e ) {
                LOG.logWarning( "", e );
            }
            try {
                pool.releaseConnection( conn, driver, url, user, pw );
            } catch ( DBPoolException e ) {
                LOG.logWarning( "", e );
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.LinkedTable#getColumnCount()
     */
    public int getColumnCount() {
        return columnNames.length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.LinkedTable#getColumnNames()
     */
    public String[] getColumnNames() {
        return columnNames;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.LinkedTable#getColumnTypes()
     */
    public int[] getColumnTypes() {
        return types;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.LinkedTable#getRow(int)
     */
    public Object[] getRow( int rowNo )
                            throws IOException {
        Object[] row = null;
        try {
            if ( conn == null || rowNo < cursor ) {
                startReading();
                cursor = 0;
            }
            while ( cursor < rowNo ) {
                rs.next();
                cursor++;
            }
            rs.next();
            row = read();
        } catch ( Exception e ) {
            throw new IOException( e.getMessage() );
        }
        return row;
    }

    private Object[] read()
                            throws SQLException {
        Object[] row = new Object[columnNames.length];
        for ( int i = 0; i < columnNames.length; i++ ) {
            row[i] = rs.getObject( i + 1 );
        }
        return row;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.LinkedTable#getRowCount()
     */
    public int getRowCount() {
        int cnt = 0;
        try {
            startReading();
            while ( rs.next() ) {
                cnt++;
            }
        } catch ( Exception e ) {
            LOG.logWarning( "", e );
            try {
                stopReading();
            } catch ( Exception e2 ) {
                LOG.logWarning( "", e2 );
            }
        }
        return cnt;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.deegree.igeo.dataadapter.LinkedTable#getRows(org.deegree.framework.util.Pair<java.lang.String,java.lang.Object
     * >[])
     */
    public Object[][] getRows( Pair<String, Object>... keys )
                            throws IOException {
        try {
            List<Object[]> rows = new ArrayList<Object[]>( 10 );
            startReading();
            while ( rs.next() ) {
                Object[] row = read();
                boolean match = true;
                for ( Pair<String, Object> pair : keys ) {
                    int idx = getIndexForColumnName( pair.first );
                    if ( !pair.second.equals( row[idx] ) ) {
                        match = false;
                        break;
                    }
                }
                if ( match ) {
                    rows.add( row );
                }
            }
            return rows.toArray( new Object[rows.size()][] );

        } catch ( Exception e ) {
            try {
                stopReading();
            } catch ( Exception e2 ) {
                LOG.logWarning( "", e2 );
            }
            LOG.logError( e );
            throw new IOException( e.getMessage() );
        }
    }

    private int getIndexForColumnName( String name ) {
        for ( int i = 0; i < columnNames.length; i++ ) {
            if ( columnNames[i].equalsIgnoreCase( name ) ) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 
     * @return first line (header)
     * @throws IOException
     */
    public void startReading()
                            throws Exception {
        stopReading();
        String driver = linkedDB.getConnection().getDriver();
        String url = linkedDB.getConnection().getUrl();
        String user = linkedDB.getConnection().getUser();
        String pw = linkedDB.getConnection().getPassword();
        cursor = 0;
        try {
            conn = pool.acquireConnection( driver, url, user, pw );
            stmt = conn.createStatement();
            rs = stmt.executeQuery( linkedDB.getSqlTemplate() );
        } catch ( Exception e ) {            
            try {
                rs.close();
                stmt.close();
            } catch ( Exception e1 ) {
                LOG.logWarning( "", e1 );
            }
            try {
                pool.releaseConnection( conn, driver, url, user, pw );
            } catch ( DBPoolException e1 ) {
                LOG.logWarning( "", e1 );
            }
            throw e;
        }
    }

    /**
     * 
     * @throws IOException
     */
    public void stopReading()
                            throws Exception {
        if ( conn != null ) {
            String driver = linkedDB.getConnection().getDriver();
            String url = linkedDB.getConnection().getUrl();
            String user = linkedDB.getConnection().getUser();
            String pw = linkedDB.getConnection().getPassword();
            try {
                rs.close();
                stmt.close();
            } catch ( Exception e ) {
                LOG.logWarning( "", e );
            }
            try {
                pool.releaseConnection( conn, driver, url, user, pw );
            } catch ( DBPoolException e ) {
                LOG.logWarning( "", e );
            }
            conn = null;
        }
    }
}
