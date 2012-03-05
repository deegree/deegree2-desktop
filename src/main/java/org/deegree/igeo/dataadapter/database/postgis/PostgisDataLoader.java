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

package org.deegree.igeo.dataadapter.database.postgis;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.GeometryUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.igeo.config.JDBCConnection;
import org.deegree.igeo.dataadapter.DataAccessException;
import org.deegree.igeo.dataadapter.database.AbstractDatabaseLoader;
import org.deegree.igeo.dataadapter.jdbc.JdbcConnectionParameter;
import org.deegree.igeo.dataadapter.jdbc.JdbcConnectionParameterCache;
import org.deegree.igeo.mapmodel.DatabaseDatasource;
import org.deegree.io.DBConnectionPool;
import org.deegree.io.DBPoolException;
import org.deegree.io.datastore.sql.postgis.PGgeometryAdapter;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.MultiPrimitive;
import org.deegree.model.spatialschema.Surface;
import org.postgis.PGboxbase;
import org.postgis.PGgeometry;
import org.postgresql.PGConnection;

/**
 * class for loading data as feature collection from a postgis database
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class PostgisDataLoader extends AbstractDatabaseLoader {

    private static final ILogger LOG = LoggerFactory.getLogger( PostgisDataLoader.class );

    private static final String GEOMETRY_DATATYPE_NAME = "geometry";

    private static final String BOX3D_DATATYPE_NAME = "box3d";

    private static final String PG_GEOMETRY_CLASS_NAME = "org.postgis.PGgeometry";

    private static final String PG_BOX3D_CLASS_NAME = "org.postgis.PGbox3d";

    private static Class<?> pgGeometryClass;

    private static Class<?> pgBox3dClass;

    static {
        namespace = URI.create( "http://www.deegree.org/igeodesktop" );
        try {
            pgGeometryClass = Class.forName( PG_GEOMETRY_CLASS_NAME );
        } catch ( ClassNotFoundException e ) {
            LOG.logError( "Cannot find class '" + PG_GEOMETRY_CLASS_NAME + "'.", e );
        }
        try {
            pgBox3dClass = Class.forName( PG_BOX3D_CLASS_NAME );
        } catch ( ClassNotFoundException e ) {
            LOG.logError( "Cannot find class '" + PG_BOX3D_CLASS_NAME + "'.", e );
        }
    }

    /**
     * 
     * @param datasource
     */
    public PostgisDataLoader( DatabaseDatasource datasource ) {
        this.datasource = datasource;
    }

    /**
     * @param envelope
     * @return featurecollection loaded from a postgis database
     */
    public FeatureCollection load( Envelope envelope ) {
        JDBCConnection jdbc = datasource.getJdbc();
        PreparedStatement stmt = null;
        Connection conn = null;
        ResultSet rs = null;
        FeatureCollection fc = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(), 10000 );
        try {
            conn = acquireConnection( jdbc );

            String envCRS = envelope.getCoordinateSystem().getLocalName();
            String nativeCRS = getSRSCode( datasource.getSRID() );

            PGboxbase box = PGgeometryAdapter.export( envelope );
            Surface surface = GeometryFactory.createSurface( envelope, envelope.getCoordinateSystem() );
            PGgeometry pggeom = PGgeometryAdapter.export( surface, Integer.parseInt( envCRS ) );
            StringBuffer query = new StringBuffer( 1000 );
            if ( nativeCRS.equals( "-1" ) ) {
                query.append( " (" );
                query.append( datasource.getGeometryFieldName() );
                query.append( " && SetSRID( ?, -1) " );
                query.append( " AND intersects(" );
                query.append( datasource.getGeometryFieldName() );
                query.append( ",SetSRID( ?,-1 ) ) ) " );
            } else {
                // use the bbox operator (&&) to filter using the spatial index
                query.append( " (" );
                query.append( datasource.getGeometryFieldName() );
                query.append( " && transform(SetSRID( ?, " );
                query.append( envCRS );
                query.append( "), " );
                query.append( nativeCRS );
                query.append( ")) AND intersects(" );
                query.append( datasource.getGeometryFieldName() );
                query.append( ",transform(?, " );
                query.append( nativeCRS );
                query.append( "))" );
            }

            String sql = datasource.getSqlTemplate();
            System.out.println( sql );
            if ( sql.trim().toUpperCase().endsWith( " WHERE" ) ) {
                LOG.logDebug( "performed SQL: ", sql + query );
                stmt = conn.prepareStatement( sql + query );
            } else if ( sql.trim().toUpperCase().indexOf( " WHERE " ) < 0 ) {
                LOG.logDebug( "performed SQL: ", sql + " WHERE " + query );
                stmt = conn.prepareStatement( sql + " WHERE " + query );
            } else {
                LOG.logDebug( "performed SQL: ", sql + " AND " + query );
                stmt = conn.prepareStatement( sql + " AND " + query );
            }

            // TODO
            // if connection is not available ask user updated connection parameters
            stmt.setObject( 1, box, java.sql.Types.OTHER );
            stmt.setObject( 2, pggeom, java.sql.Types.OTHER );
            stmt.setMaxRows( maxFeatures );
            // seems that not every postgres version supports this
            // stmt.setQueryTimeout( timeout );
            rs = stmt.executeQuery();
            LOG.logDebug( "performing database query: " + datasource.getSqlTemplate() );
            ResultSetMetaData rsmd = rs.getMetaData();
            FeatureType featureType = createFeatureType( datasource.getGeometryFieldName(), rsmd );
            LOG.logDebug( "database datastore feature type: ", featureType );
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
                        value = PGgeometryAdapter.wrap( (PGgeometry) value, crs );
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

    /**
     * @param srid
     * @return
     */
    private String getSRSCode( String srid ) {
        if ( srid.indexOf( ":" ) > -1 ) {
            String[] t = StringTools.toArray( srid, ":", false );
            return t[t.length - 1];
        } else {
            return srid;
        }
    }

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
                rs.close();
            } catch ( Exception e ) {
                LOG.logWarning( "", e );
            }
            try {
                stmt.close();
            } catch ( Exception e ) {
                LOG.logWarning( "", e );
            }
            try {
                conn.setAutoCommit( ac );
            } catch ( SQLException e ) {
                LOG.logWarning( "", e );
            }
            releaseConnection( jdbc, conn );
        }
        return featureType;
    }

    private Connection acquireConnection( JDBCConnection jdbc )
                            throws DBPoolException, SQLException {
        JdbcConnectionParameter connParam = JdbcConnectionParameterCache.getInstance().getJdbcConnectionParameter( jdbc.getDriver(),
                                                                                                                   jdbc.getUrl(),
                                                                                                                   jdbc.getUser(),
                                                                                                                   jdbc.getPassword() );
        Connection conn;
        DBConnectionPool pool = DBConnectionPool.getInstance();
        conn = pool.acquireConnection( connParam.getDriver(), connParam.getUrl(), connParam.getUser(),
                                       connParam.getPasswd() );
        PGConnection pgConn = (PGConnection) conn;
        pgConn.addDataType( GEOMETRY_DATATYPE_NAME, pgGeometryClass );
        pgConn.addDataType( BOX3D_DATATYPE_NAME, pgBox3dClass );
        return conn;
    }
}
