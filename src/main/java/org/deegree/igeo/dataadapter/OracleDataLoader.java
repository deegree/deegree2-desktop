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
import org.deegree.igeo.config.JDBCConnectionType;
import org.deegree.igeo.mapmodel.DatabaseDatasource;
import org.deegree.io.DBConnectionPool;
import org.deegree.io.DBPoolException;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.MultiPrimitive;
import org.deegree.model.spatialschema.Surface;

/**
 * class for loading data as feature collection from a postgis database
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class OracleDataLoader extends AbstractDatabaseLoader {

    private static final ILogger LOG = LoggerFactory.getLogger( OracleDataLoader.class );

    /**
     * 
     * @param datasource
     */
    public OracleDataLoader( DatabaseDatasource datasource ) {
        this.datasource = datasource;
    }

    /**
     * 
     * @return feature collection loaded from a oracle database
     */
    public FeatureCollection load( Envelope envelope ) {
        JDBCConnectionType jdbc = datasource.getJdbc();
        PreparedStatement stmt = null;
        Connection conn = null;
        ResultSet rs = null;
        FeatureCollection fc = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(), 10000 );
        try {
            conn = acquireConnection( jdbc );

            // TODO
            // if connection is not available ask user updated connection parameters
            stmt = createPreparedStatement( datasource, envelope, conn, envelope.getCoordinateSystem(),
                                            datasource.getSqlTemplate(), null );
            stmt.setMaxRows( maxFeatures );
            // seems that not every oracle version supports this
            // stmt.setQueryTimeout( timeout );
            rs = stmt.executeQuery();
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

    private static PreparedStatement createPreparedStatement( DatabaseDatasource datasource, Envelope envelope,
                                                              Connection conn, CoordinateSystem crs, String sql,
                                                              String extraClauses )
                            throws Exception {
        PreparedStatement stmt;

        String nativeCRS = crs.getLocalName();
        String envCRS = nativeCRS;
        if ( envelope.getCoordinateSystem() != null ) {
            envCRS = envelope.getCoordinateSystem().getLocalName();
        }

        // use the bbox operator (&&) to filter using the spatial index
        if ( !( nativeCRS.equals( envCRS ) ) ) {
            GeoTransformer gt = new GeoTransformer( crs );
            envelope = gt.transform( envelope, envelope.getCoordinateSystem() );
        }
        Surface surface = GeometryFactory.createSurface( envelope, envelope.getCoordinateSystem() );
        Class<?> clzz = Class.forName( "oracle.spatial.geometry.JGeometry" );
        Method m = clzz.getMethod( "export", new Class[] { Geometry.class, Integer.class } );
        Object jgeom = m.invoke( null, new Object[] { surface, Integer.parseInt( nativeCRS ) } );
        StringBuffer query = new StringBuffer( 1000 );
        query.append( " MDSYS.SDO_RELATE(" );
        query.append( datasource.getGeometryFieldName() );
        query.append( ',' );
        query.append( '?' );
        query.append( ",'MASK=ANYINTERACT QUERYTYPE=WINDOW')='TRUE'" );

        if ( extraClauses != null ) {
            query.append( extraClauses );
        }

        if ( sql.trim().toUpperCase().endsWith( " WHERE" ) ) {
            LOG.logDebug( "performed SQL: ", sql );
            stmt = conn.prepareStatement( sql + query );
        } else if ( sql.trim().toUpperCase().indexOf( " WHERE " ) < 0 ) {
            LOG.logDebug( "performed SQL: ", sql + " WHERE " + query );
            stmt = conn.prepareStatement( sql + " WHERE " + query );
        } else {
            LOG.logDebug( "performed SQL: ", sql + " AND " + query );
            stmt = conn.prepareStatement( sql + " AND " + query );
        }

        LOG.logDebug( "Converting JGeometry to STRUCT." );
        m = clzz.getMethod( "store", new Class[] { Class.forName( "oracle.spatial.geometry.JGeometry" ),
                                                  conn.getClass() } );
        Object struct = m.invoke( null, new Object[] { jgeom, conn } );
        stmt.setObject( 1, struct, java.sql.Types.STRUCT );
        return stmt;
    }

    public FeatureType getFeatureType() {
        FeatureType featureType = null;
        JDBCConnectionType jdbc = datasource.getJdbc();
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

    private Connection acquireConnection( JDBCConnectionType jdbc )
                            throws DBPoolException, SQLException {
        DBConnectionPool pool = DBConnectionPool.getInstance();
        return pool.acquireConnection( jdbc.getDriver(), jdbc.getUrl(), jdbc.getUser(), jdbc.getPassword() );
    }

}
