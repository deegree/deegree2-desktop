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
package org.deegree.igeo.dataadapter.database;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import org.deegree.igeo.config.JDBCConnection;
import org.deegree.igeo.dataadapter.DataAccessException;
import org.deegree.igeo.jdbc.DatabaseConnectionManager;
import org.deegree.igeo.mapmodel.DatabaseDatasource;
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
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public abstract class AbstractDatabaseLoader implements DatabaseDataLoader {

    private static final ILogger LOG = LoggerFactory.getLogger( AbstractDatabaseLoader.class );

    protected static URI namespace = URI.create( "http://www.deegree.org/igeodesktop" );

    private DatabaseDatasource datasource;

    protected int maxFeatures = 50000000;

    protected int timeout = 60000;

    public AbstractDatabaseLoader( DatabaseDatasource datasource ) {
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
     * @param geometryFiedName
     * @param rsmd
     * @return {@link FeatureType} created from column names and types
     * @throws SQLException
     */
    protected FeatureType createFeatureType( String geometryFiedName, ResultSetMetaData rsmd )
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

    /**
     * 
     * @param geometryFiedName
     * @param columnName
     * @param columnType
     * @return type of a column
     */
    protected int getTypeCode( String geometryFiedName, String columnName, int columnType ) {
        if ( columnName.equalsIgnoreCase( geometryFiedName ) ) {
            return Types.GEOMETRY;
        }
        return columnType;
    }

    /**
     * 
     * @param jdbc
     * @param conn
     */
    protected void releaseConnection( JDBCConnection jdbc, Connection conn ) {
        try {
            DatabaseConnectionManager.releaseConnection( conn, jdbc.getDriver(), jdbc.getUrl(), jdbc.getUser(),
                                                         jdbc.getPassword() );
        } catch ( DBPoolException e ) {
            LOG.logWarning( "", e );
        }
    }

    /**
     * 
     * @return feature collection loaded from a oracle database
     */
    public FeatureCollection load( Envelope envelope ) {
        JDBCConnection jdbc = datasource.getJdbc();
        PreparedStatement stmt = null;
        Connection conn = null;
        ResultSet rs = null;
        FeatureCollection fc = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(), 10000 );
        try {
            conn = acquireConnection( jdbc );

            stmt = createPreparedStatement( datasource, envelope, conn );
            rs = stmt.executeQuery();
            LOG.logDebug( "performing database query: " + datasource.getSqlTemplate() );
            ResultSetMetaData rsmd = rs.getMetaData();
            FeatureType featureType = createFeatureType( datasource.getGeometryFieldName(), rsmd );
            LOG.logDebug( "database datastore feature type: ", featureType );
            int ccnt = rsmd.getColumnCount();
            org.deegree.model.crs.CoordinateSystem crs = datasource.getNativeCoordinateSystem();
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
                        value = handleGeometryValue( value, crs );
                        if ( value instanceof MultiPrimitive && ( (MultiPrimitive) value ).getAll().length == 1 ) {
                            value = ( (MultiPrimitive) value ).getAll()[0];
                        }
                        value = GeometryUtils.ensureClockwise( (Geometry) value );
                    } else if ( name.equalsIgnoreCase( datasource.getPrimaryKeyFieldName() ) ) {
                        pk = value;
                    }
                    properties[i] = FeatureFactory.createFeatureProperty( featureType.getPropertyName( i ), value );
                }
                if ( pk != null && !geomIsNull ) {
                    // because feature IDs are not important in case of database data source
                    // it is just 'ID' as prefix plus a number of current row
                    fc.add( FeatureFactory.createFeature( "ID_" + pk, featureType, properties ) );
                    k++;
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
                LOG.logWarning( "", e );
            }
            try {
                stmt.close();
            } catch ( Exception e ) {
                LOG.logWarning( "", e );
            }
            releaseConnection( jdbc, conn );
        }
        return fc;
    }

    @Override
    public FeatureType getFeatureType() {
        FeatureType featureType = null;
        JDBCConnection jdbc = datasource.getJdbc();
        Statement stmt = null;
        Connection conn = null;
        ResultSet rs = null;
        boolean ac = false;
        try {
            conn = acquireConnection( jdbc );
            ac = conn.getAutoCommit();
            conn.setAutoCommit( false );
            // TODO
            // if connection is not available ask user updated connection parameters
            stmt = conn.createStatement();
            stmt.setMaxRows( 1 );
            String sql = datasource.getSqlTemplate();
            if ( sql.trim().toLowerCase().endsWith( "where" ) ) {
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
                if ( rs != null ) {
                    rs.close();
                }
            } catch ( Exception e ) {
                LOG.logWarning( "", e );
            }
            try {
                if ( stmt != null ) {
                    stmt.close();
                }
            } catch ( Exception e ) {
                LOG.logWarning( "", e );
            }
            try {
                if ( conn != null ) {
                    conn.setAutoCommit( ac );
                }
            } catch ( SQLException e ) {
                LOG.logWarning( "", e );
            }
            releaseConnection( jdbc, conn );
        }
        return featureType;
    }

    protected Connection acquireConnection( JDBCConnection jdbc )
                            throws DBPoolException, SQLException {
        return DatabaseConnectionManager.aquireConnection( jdbc.getDriver(), jdbc.getUrl(), jdbc.getUser(),
                                                           jdbc.getPassword() );
    }

    /**
     * @param value
     * @param crs
     * @return
     */
    protected abstract Object handleGeometryValue( Object value, CoordinateSystem crs )
                            throws Exception;

    /**
     * @param datasource2
     * @param envelope
     * @param conn
     * @param coordinateSystem
     * @param sqlTemplate
     * @param object
     * @return
     */
    protected abstract PreparedStatement createPreparedStatement( DatabaseDatasource datasource, Envelope envelope,
                                                                  Connection conn )
                            throws Exception;

}
