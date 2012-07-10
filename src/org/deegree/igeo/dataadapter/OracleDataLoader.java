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

package org.deegree.igeo.dataadapter;

import java.lang.reflect.Method;
import java.net.URI;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.GeometryUtils;
import org.deegree.igeo.config.JDBCConnectionType;
import org.deegree.igeo.mapmodel.DatabaseDatasource;
import org.deegree.io.DBConnectionPool;
import org.deegree.io.DBPoolException;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.MultiPrimitive;

/**
 * class for loading data as feature collection from a postgis database
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class OracleDataLoader implements DatabaseDataLoader {

    private static final ILogger LOG = LoggerFactory.getLogger( OracleDataLoader.class );

    private static URI namespace;

    private DatabaseDatasource datasource;

    private int maxFeatures = 50000000;

    private int timeout = 60000;

    /**
     * 
     * @param datasource
     */
    public OracleDataLoader( DatabaseDatasource datasource ) {
        this.datasource = datasource;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.DatabaseDataLoader#setMaxFeatures(int)
     */
    public void setMaxFeatures( int maxFeatures ) {
        this.maxFeatures = maxFeatures;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.DatabaseDataLoader#setTimeout(int)
     */
    public void setTimeout( int timeout ) {
        this.timeout = timeout / 1000;
    }

    /**
     * 
     * @return featurecollection loaded from a postgis database
     */
    public FeatureCollection load(Envelope envelope) {
        JDBCConnectionType jdbc = datasource.getJdbc();
        Statement stmt = null;
        Connection conn = null;
        ResultSet rs = null;
        FeatureCollection fc = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(), 10000 );
        try {
            conn = acquireConnection( jdbc );

            // TODO
            // if connection is not available ask user updated connection parameters
            stmt = conn.createStatement();
            stmt.setMaxRows( maxFeatures );
            // seems that not every oracle version supports this
            // stmt.setQueryTimeout( timeout );
            rs = stmt.executeQuery( datasource.getSqlTemplate() );
            LOG.logDebug( "performing database query: " + datasource.getSqlTemplate() );
            ResultSetMetaData rsmd = rs.getMetaData();
            FeatureType featureType = createFeatureType( datasource.getGeometryFieldName(), rsmd );
            int ccnt = rsmd.getColumnCount();
            CoordinateSystem crs = datasource.getNativeCoordinateSystem();
            int k = 0;
            // read each line from database and create a feature from it
            while ( rs.next() ) {
                FeatureProperty[] properties = new FeatureProperty[ccnt];
                Object pk = null;
                boolean geomIsNull = false;
                for ( int i = 0; i < ccnt; i++ ) {
                    String name = rsmd.getColumnName( i + 1 );
                    Object value = rs.getObject( i + 1 );
                    // if column name equals geometry field name the value read from
                    // database must be converted into a deegree geometry
                    if ( name.equalsIgnoreCase( datasource.getGeometryFieldName() ) ) {
                        if ( value == null ) {
                            // skip rows/feature without geometry
                            geomIsNull = true;
                            LOG.logInfo( "skip row because geometry is null" );
                            break;
                        }
                        // use reflections to avoid dependency on oracle libraries for compiling the code
                        Class<?> clzz = Class.forName( "oracle.spatial.geometry.JGeometry" );
                        Method m = clzz.getMethod( "load", new Class[] { Class.forName( "oracle.sql.STRUCT" ) } );
                        Object o = m.invoke( null, new Object[] { value } );
                        Class<?> clzz2 = Class.forName( "org.deegree.io.datastore.sql.oracle.JGeometryAdapter" );
                        m = clzz2.getMethod( "wrap", new Class[] { clzz, CoordinateSystem.class } );
                        value = m.invoke( null, new Object[] { o, crs } );
                        if ( value instanceof MultiPrimitive && ( (MultiPrimitive) value ).getAll().length == 1 ) {
                            value = ( (MultiPrimitive) value ).getAll()[0];
                        }
                        value = GeometryUtils.ensureClockwise( (Geometry) value );
                    }
                    if ( name.equalsIgnoreCase( datasource.getPrimaryKeyFieldName() ) ) {
                        pk = value;
                    }
                    properties[i] = FeatureFactory.createFeatureProperty( featureType.getPropertyName( i ), value );
                }
                if ( pk != null && !geomIsNull ) {                    
                    // because feature IDs are not important in case of database data source
                    // it is just 'ID' as prefix plus a number of current row
                    fc.add( FeatureFactory.createFeature( "ID_" + pk, featureType, properties ) );
                }
                if ( pk == null ) {
                    LOG.logInfo( "skip row because primary key is null" );
                }
            }
            LOG.logDebug( k + " features loaded from database" );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( e );
        } finally {
            try {
                rs.close();
            } catch ( Exception e ) {
            }
            try {
                stmt.close();
            } catch ( Exception e ) {
            }
            releaseConnection( jdbc, conn );
        }
        return fc;
    }
    
    public FeatureType getFeatureType() {
        FeatureType featureType = null; 
        JDBCConnectionType jdbc = datasource.getJdbc();
        Statement stmt = null;
        Connection conn = null;
        ResultSet rs = null;
        boolean ac = false ;
        try {            
            conn = acquireConnection( jdbc );
            ac = conn.getAutoCommit();
            conn.setAutoCommit( false );
            // TODO
            // if connection is not available ask user updated connection parameters
            stmt = conn.createStatement();            
            stmt.setMaxRows( 1 );
            String sql = datasource.getSqlTemplate();
            if ( sql.trim().toLowerCase().endsWith(  "where" ) ) {
                sql = sql + " 1 = 2";
            } else if ( sql.trim().toLowerCase().indexOf( " where " ) > -1 ) {
                sql = sql + " AND 1 = 2";
            } else {
                sql = sql + " WHERE 1 = 2";
            }
            rs = stmt.executeQuery( sql );
            ResultSetMetaData rsmd = rs.getMetaData();
            featureType = createFeatureType( datasource.getGeometryFieldName(), rsmd );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( e );
        } finally {
            try {
                rs.close();
            } catch ( Exception e ) {
            }
            try {
                stmt.close();
            } catch ( Exception e ) {
            }
            try {
                conn.setAutoCommit( ac );
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
            releaseConnection( jdbc, conn );
        }
        return featureType;
    }

    /**
     * 
     * @param geometryFiedName
     * @param rsmd
     * @return {@link FeatureType} created from column names and types
     * @throws SQLException
     */
    private FeatureType createFeatureType( String geometryFiedName, ResultSetMetaData rsmd )
                            throws SQLException {
        int ccnt = rsmd.getColumnCount();
        QualifiedName name = new QualifiedName( datasource.getName(), namespace );
        PropertyType[] properties = new PropertyType[ccnt];
        for ( int i = 0; i < ccnt; i++ ) {
            QualifiedName propName = new QualifiedName( rsmd.getColumnName( i + 1 ), namespace );
            int typeCode = getTypeCode( geometryFiedName, rsmd.getColumnName( i + 1 ), rsmd.getColumnType( i + 1 ) );
            if ( Types.GEOMETRY == typeCode ) {
                properties[i] = FeatureFactory.createGeometryPropertyType( propName, Types.GEOMETRY_PROPERTY_NAME, 1, 1 );
            } else {
                properties[i] = FeatureFactory.createSimplePropertyType( propName, typeCode, true );
            }
        }
        return FeatureFactory.createFeatureType( name, false, properties );
    }

    private int getTypeCode( String geometryFiedName, String columnName, int columnType ) {
        if ( columnName.equalsIgnoreCase( geometryFiedName ) ) {
            return Types.GEOMETRY;
        }
        return columnType;
    }

    private void releaseConnection( JDBCConnectionType jdbc, Connection conn ) {
        try {
            DBConnectionPool pool = DBConnectionPool.getInstance();
            pool.releaseConnection( conn, jdbc.getDriver(), jdbc.getUrl(), jdbc.getUser(), jdbc.getPassword() );
        } catch ( DBPoolException e ) {
        }
    }

    private Connection acquireConnection( JDBCConnectionType jdbc )
                            throws DBPoolException, SQLException {
        DBConnectionPool pool = DBConnectionPool.getInstance();
        return pool.acquireConnection( jdbc.getDriver(), jdbc.getUrl(), jdbc.getUser(), jdbc.getPassword() );
    }

}
