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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.igeo.config.JDBCConnectionType;
import org.deegree.igeo.mapmodel.DatabaseDatasource;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.io.DBConnectionPool;
import org.deegree.io.DBPoolException;
import org.deegree.io.datastore.sql.postgis.PGgeometryAdapter;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.postgresql.PGConnection;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class PostgisDataWriter implements DatabaseDataWriter {

    private static final ILogger LOG = LoggerFactory.getLogger( PostgisDataWriter.class );

    private static final String GEOMETRY_DATATYPE_NAME = "geometry";

    private static final String BOX3D_DATATYPE_NAME = "box3d";

    private static final String PG_GEOMETRY_CLASS_NAME = "org.postgis.PGgeometry";

    private static final String PG_BOX3D_CLASS_NAME = "org.postgis.PGbox3d";

    private static Class<?> pgGeometryClass;

    private static Class<?> pgBox3dClass;

    static {
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

    private int timeout;

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.DatabaseDataWriter#deleteFeatures(org.deegree.igeo.mapmodel.DatabaseDatasource,
     * org.deegree.model.feature.FeatureCollection, org.deegree.igeo.mapmodel.Layer)
     */
    public int deleteFeatures( DatabaseDatasource datasource, FeatureCollection featureCollection, Layer layer ) {
        String table = extractTableName( datasource.getSqlTemplate() );
        StringBuilder sb = new StringBuilder( 1000 );
        sb.append( "DELETE from " ).append( table ).append( " WHERE " );
        sb.append( datasource.getPrimaryKeyFieldName() ).append( " = ?" );
        LOG.logDebug( "DELETE features SQL: ", sb );
        FeatureType ft = featureCollection.getFeature( 0 ).getFeatureType();
        PropertyType[] pt = ft.getProperties();
        JDBCConnectionType jdbc = datasource.getJdbc();
        Connection conn = null;
        PreparedStatement stmt = null;
        Iterator<Feature> iterator = featureCollection.iterator();
        try {
            conn = acquireConnection( jdbc );
            conn.setAutoCommit( false );
            stmt = conn.prepareStatement( sb.toString() );
            // seems that not every postgres version supports this
            // stmt.setQueryTimeout( timeout );
            while ( iterator.hasNext() ) {
                Feature feature = iterator.next();
                setWhereCondition( stmt, datasource, pt, feature, 1 );
                stmt.execute();
            }
            conn.commit();
        } catch ( Exception e ) {
            LOG.logError( e );
            throw new DataAccessException( e );
        } finally {
            try {
                stmt.close();
                releaseConnection( jdbc, conn );
            } catch ( Exception e ) {
                LOG.logError( e );
            }
        }
        return featureCollection.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.DatabaseDataWriter#insertFeatures(org.deegree.igeo.mapmodel.DatabaseDatasource,
     * org.deegree.model.feature.FeatureCollection, org.deegree.igeo.mapmodel.Layer)
     */
    public void insertFeatures( DatabaseDatasource datasource, FeatureCollection featureCollection, Layer layer ) {
        String table = extractTableName( datasource.getSqlTemplate() );
        StringBuilder sb = new StringBuilder( 2000 );
        sb.append( "INSERT INTO " ).append( table ).append( " (" );
        FeatureType ft = featureCollection.getFeature( 0 ).getFeatureType();
        PropertyType[] pt = ft.getProperties();
        for ( int i = 0; i < pt.length - 1; i++ ) {
            sb.append( pt[i].getName().getLocalName() ).append( ',' );
        }
        sb.append( pt[pt.length - 1].getName().getLocalName() ).append( ") VALUES (" );
        for ( int i = 0; i < pt.length - 1; i++ ) {
            sb.append( "?," );
        }
        sb.append( "?)" );
        LOG.logDebug( "INSERT Statement: ", sb );
        Iterator<Feature> iterator = featureCollection.iterator();
        JDBCConnectionType jdbc = datasource.getJdbc();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = acquireConnection( jdbc );
            conn.setAutoCommit( false );
            stmt = conn.prepareStatement( sb.toString() );
            // seems that not every postgres version supports this
            // stmt.setQueryTimeout( timeout );
            while ( iterator.hasNext() ) {
                Feature feature = iterator.next();
                setFieldValues( stmt, datasource, feature, pt );
                stmt.execute();
            }
            conn.commit();
        } catch ( Exception e ) {
            LOG.logError( e );
            throw new DataAccessException( e );
        } finally {
            try {
                stmt.close();
                releaseConnection( jdbc, conn );
            } catch ( Exception e ) {
                LOG.logError( e );
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.DatabaseDataWriter#setTimeout(int)
     */
    public void setTimeout( int timeout ) {
        this.timeout = timeout / 1000;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.DatabaseDataWriter#updateFeatures(org.deegree.igeo.mapmodel.DatabaseDatasource,
     * org.deegree.model.feature.FeatureCollection, org.deegree.igeo.mapmodel.Layer)
     */
    public int updateFeatures( DatabaseDatasource datasource, FeatureCollection featureCollection, Layer layer ) {
        String table = extractTableName( datasource.getSqlTemplate() );
        FeatureType ft = featureCollection.getFeature( 0 ).getFeatureType();
        PropertyType[] pt = ft.getProperties();
        StringBuilder sb = new StringBuilder( 1000 );
        sb.append( "UPDATE " ).append( table ).append( " SET " );
        for ( int i = 0; i < pt.length - 1; i++ ) {
            sb.append( pt[i].getName().getLocalName() ).append( " = ?," );
        }
        sb.append( pt[pt.length - 1].getName().getLocalName() ).append( " = ? " );
        sb.append( " WHERE " ).append( datasource.getPrimaryKeyFieldName() ).append( " = ?" );
        LOG.logDebug( "UPDATE features SQL: ", sb );
        JDBCConnectionType jdbc = datasource.getJdbc();
        Connection conn = null;
        PreparedStatement stmt = null;
        Iterator<Feature> iterator = featureCollection.iterator();
        try {
            conn = acquireConnection( jdbc );
            conn.setAutoCommit( false );
            stmt = conn.prepareStatement( sb.toString() );
            // seems that not every postgres version supports this
            // stmt.setQueryTimeout( timeout );
            while ( iterator.hasNext() ) {
                Feature feature = iterator.next();
                setFieldValues( stmt, datasource, feature, pt );
                setWhereCondition( stmt, datasource, pt, feature, pt.length + 1 );
                stmt.execute();
            }
            conn.commit();
        } catch ( Exception e ) {
            LOG.logError( e );
            throw new DataAccessException( e );
        } finally {
            try {
                stmt.close();
                releaseConnection( jdbc, conn );
            } catch ( Exception e ) {
                LOG.logError( e );
            }
        }
        return featureCollection.size();
    }

    private void setFieldValues( PreparedStatement stmt, DatabaseDatasource datasource, Feature feature,
                                 PropertyType[] pt )
                            throws GeometryException, SQLException {
        for ( int i = 0; i < pt.length; i++ ) {
            Object value = feature.getDefaultProperty( pt[i].getName() ).getValue();
            if ( pt[i].getName().getLocalName().equalsIgnoreCase( datasource.getPrimaryKeyFieldName() ) ) {
                feature.getDefaultProperty( pt[i].getName() ).setValue( value );
            }
            if ( value != null ) {
                if ( pt[i].getType() == Types.GEOMETRY ) {
                    value = PGgeometryAdapter.export( (Geometry) value, Integer.parseInt( datasource.getSRID() ) );
                    stmt.setObject( i + 1, value );
                } else {
                    stmt.setObject( i + 1, value, pt[i].getType() );
                }
            } else {
                if ( pt[i].getType() == Types.GEOMETRY ) {
                    stmt.setNull( i + 1, Types.OTHER );
                } else {
                    stmt.setNull( i + 1, pt[i].getType() );
                }
            }
        }
    }

    private void setWhereCondition( PreparedStatement stmt, DatabaseDatasource datasource, PropertyType[] pt,
                                    Feature feature, int index )
                            throws SQLException {
        for ( int i = 0; i < pt.length; i++ ) {            
            if ( pt[i].getName().getLocalName().equalsIgnoreCase( datasource.getPrimaryKeyFieldName() ) ) {
                Object value = feature.getDefaultProperty( pt[i].getName() ).getValue();
                stmt.setObject( index, value, pt[i].getType() );
                break;
            }
        }
    }

    /**
     * @param sqlTemplate
     * @return name of the table addressed by passed sql (SELECT-) statement
     */
    private String extractTableName( String sqlTemplate ) {
        String[] tmp = StringTools.toArray( sqlTemplate, " ", false );
        String tableName = null;
        for ( int i = 0; i < tmp.length; i++ ) {
            if ( tmp[i].equalsIgnoreCase( "from" ) ) {
                tableName = tmp[i + 1];
                break;
            }
        }
        return tableName;
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
        Connection conn;
        DBConnectionPool pool = DBConnectionPool.getInstance();
        conn = pool.acquireConnection( jdbc.getDriver(), jdbc.getUrl(), jdbc.getUser(), jdbc.getPassword() );
        PGConnection pgConn = (PGConnection) conn;
        pgConn.addDataType( GEOMETRY_DATATYPE_NAME, pgGeometryClass );
        pgConn.addDataType( BOX3D_DATATYPE_NAME, pgBox3dClass );
        return conn;
    }

}
