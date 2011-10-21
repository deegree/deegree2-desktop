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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.IDGenerator;
import org.deegree.igeo.config.JDBCConnectionType;
import org.deegree.io.DBConnectionPool;
import org.deegree.model.feature.ValueGenerator;

/**
 * Implement {@link ValueGenerator} interface by reading max value + 1 from a column of a database table
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class MaxDatabaseColumnValueGenerator implements ValueGenerator {
    
    private static final ILogger LOG = LoggerFactory.getLogger( MaxDatabaseColumnValueGenerator.class );

    private JDBCConnectionType jdbc;

    private String table;

    private String column;

    /**
     * @param jdbc
     * @param table
     * @param column
     */
    public MaxDatabaseColumnValueGenerator( JDBCConnectionType jdbc, String table, String column ) {
        super();
        this.jdbc = jdbc;
        this.table = table;
        this.column = column;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.model.feature.ValueGenerator#generate()
     */
    public Object generate() {
        Object value = null;
        DBConnectionPool pool = DBConnectionPool.getInstance();
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = pool.acquireConnection( jdbc.getDriver(), jdbc.getUrl(), jdbc.getUser(), jdbc.getPassword() );
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery( "select max(" + column + ") from " + table );
            ResultSetMetaData rsmd = rs.getMetaData();
            if ( rs.next() ) {
                value = rs.getObject( 1 );
                long idOff = IDGenerator.getInstance().generateUniqueID();
                if ( value != null ) {
                    switch ( rsmd.getColumnType( 1 ) ) {
                    case Types.BIGINT:
                    case Types.SMALLINT:
                    case Types.INTEGER: {
                        value = ( (Number) value ).intValue() + 1 + idOff;
                        break;
                    }
                    case Types.FLOAT: {
                        value = ( (Number) value ).floatValue() + 1 + idOff;
                        break;
                    }
                    case Types.REAL:
                    case Types.NUMERIC:
                    case Types.DECIMAL:
                    case Types.DOUBLE: {
                        value = ( (Number) value ).doubleValue() + 1 + idOff;
                        break;
                    }
                    default: {
                        value = ( (Number) value ).doubleValue() + 1 + idOff;
                        break;
                    }
                    }
                }
            }
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        } finally {
            try {
                stmt.close();
            } catch ( Exception e2 ) {
                LOG.logWarning( "", e2 );
            }
            try {
                pool.releaseConnection( conn, jdbc.getDriver(), jdbc.getUrl(), jdbc.getUser(), jdbc.getPassword() );
            } catch ( Exception e2 ) {
                LOG.logWarning( "", e2 );
            }
        }
        return value;
    }

}
