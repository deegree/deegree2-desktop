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
package org.deegree.desktop.dataadapter.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.deegree.desktop.config.JDBCConnection;
import org.deegree.desktop.dataadapter.DataAccessException;
import org.deegree.desktop.mapmodel.DatabaseDatasource;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.io.DBConnectionPool;
import org.deegree.io.DBPoolException;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public abstract class AbstractDatabaseWriter implements DatabaseDataWriter {

    private static final ILogger LOG = LoggerFactory.getLogger( AbstractDatabaseWriter.class );

    protected int timeout;

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
        JDBCConnection jdbc = datasource.getJdbc();
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
        Iterator<Feature> iterator = featureCollection.iterator();
        JDBCConnection jdbc = datasource.getJdbc();
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = acquireConnection( jdbc );
            conn.setAutoCommit( false );

            StringBuilder sb = new StringBuilder( 2000 );
            sb.append( "INSERT INTO " ).append( table ).append( " (" );
            FeatureType ft = featureCollection.getFeature( 0 ).getFeatureType();
            PropertyType[] pt = ft.getProperties();
            boolean isFirst = true;
            List<String> sqlSnippets = new ArrayList<String>();
            for ( int i = 0; i < pt.length; i++ ) {
                String columnName = pt[i].getName().getLocalName();
                String sqlSnippet = getSqlSnippet( columnName, table, conn, datasource );
                if ( sqlSnippet != null ) {
                    if ( !isFirst ) {
                        sb.append( ',' );
                    }
                    sb.append( columnName );
                    isFirst = false;
                    sqlSnippets.add( sqlSnippet );
                }
            }
            sb.append( ") VALUES (" );
            isFirst = true;
            for ( String sqlSnippet : sqlSnippets ) {
                if ( !isFirst ) {
                    sb.append( ',' );
                }

                sb.append( sqlSnippet );
                isFirst = false;
            }
            sb.append( ")" );
            LOG.logDebug( "INSERT Statement: ", sb );

            stmt = conn.prepareStatement( sb.toString() );
            // seems that not every oracle version supports this
            // stmt.setQueryTimeout( timeout );
            while ( iterator.hasNext() ) {
                Feature feature = iterator.next();
                setFieldValues( stmt, datasource, feature, pt, table, conn );
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

    protected String getSqlSnippet( String columnName, String tableName, Connection connection,
                                    DatabaseDatasource datasource ) {
        return "?";
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
        JDBCConnection jdbc = datasource.getJdbc();
        Connection conn = null;
        PreparedStatement stmt = null;
        Iterator<Feature> iterator = featureCollection.iterator();
        try {
            conn = acquireConnection( jdbc );
            conn.setAutoCommit( false );

            StringBuilder sb = new StringBuilder( 1000 );
            sb.append( "UPDATE " ).append( table ).append( " SET " );
            boolean isFirst = true;
            for ( int i = 0; i < pt.length; i++ ) {
                String columnName = pt[i].getName().getLocalName();
                String sqlSnipppet = getSqlSnippet( columnName, table, conn, datasource );
                if ( sqlSnipppet != null ) {
                    if ( !isFirst ) {
                        sb.append( ',' );
                    }
                    sb.append( columnName ).append( " = " ).append( sqlSnipppet );
                    isFirst = false;
                }
            }
            sb.append( " WHERE " ).append( datasource.getPrimaryKeyFieldName() ).append( " = ?" );
            LOG.logDebug( "UPDATE features SQL: ", sb );

            stmt = conn.prepareStatement( sb.toString() );
            // seems that not every postgres version supports this
            // stmt.setQueryTimeout( timeout );
            while ( iterator.hasNext() ) {
                Feature feature = iterator.next();
                int nextIndex = setFieldValues( stmt, datasource, feature, pt, table, conn );
                setWhereCondition( stmt, datasource, pt, feature, nextIndex );
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

    /**
     * @param sqlTemplate
     * @return name of the table addressed by passed sql (SELECT-) statement
     */
    protected String extractTableName( String sqlTemplate ) {
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

    protected void releaseConnection( JDBCConnection jdbc, Connection conn ) {
        try {
            DBConnectionPool pool = DBConnectionPool.getInstance();
            pool.releaseConnection( conn, jdbc.getDriver(), jdbc.getUrl(), jdbc.getUser(), jdbc.getPassword() );
        } catch ( DBPoolException e ) {
            LOG.logWarning( "", e );
        }
    }

    protected Connection acquireConnection( JDBCConnection jdbc )
                            throws DBPoolException, SQLException {
        DBConnectionPool pool = DBConnectionPool.getInstance();
        return pool.acquireConnection( jdbc.getDriver(), jdbc.getUrl(), jdbc.getUser(), jdbc.getPassword() );
    }

    /**
     * 
     * @param stmt
     *            to add the values, never <code>null</code>
     * @param datasource
     *            never <code>null</code>
     * @param feature
     *            never <code>null</code>
     * @param pt
     *            list of {@link PropertyType}s containing the values to add, never <code>null</code>
     * @param table
     *            name of the table, never <code>null</code>
     * @param conn
     *            never <code>null</code>
     * @return the next index a value can be inserted (beginning with 1)
     * @throws Exception
     */
    abstract protected int setFieldValues( PreparedStatement stmt, DatabaseDatasource datasource, Feature feature,
                                           PropertyType[] pt, String table, Connection conn )
                            throws Exception;

    abstract protected void setWhereCondition( PreparedStatement stmt, DatabaseDatasource datasource,
                                               PropertyType[] pt, Feature feature, int index )
                            throws SQLException;
}
