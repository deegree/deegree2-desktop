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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.config.JDBCConnection;
import org.deegree.igeo.dataadapter.jdbc.JdbcConnectionParameter;
import org.deegree.igeo.dataadapter.jdbc.JdbcConnectionParameterCache;
import org.deegree.igeo.mapmodel.DatabaseDatasource;
import org.deegree.io.DBConnectionPool;
import org.deegree.io.DBPoolException;
import org.deegree.model.feature.FeatureFactory;
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
public abstract class AbstractDatabaseLoader implements DatabaseDataLoader {

    private static final ILogger LOG = LoggerFactory.getLogger( AbstractDatabaseLoader.class );

    protected static URI namespace;

    protected DatabaseDatasource datasource;

    protected int maxFeatures = 50000000;

    protected int timeout = 60000;

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
            DBConnectionPool pool = DBConnectionPool.getInstance();
            JdbcConnectionParameter connParam = JdbcConnectionParameterCache.getInstance().getJdbcConnectionParameter( jdbc.getDriver(),
                                                                                                                       jdbc.getUrl(),
                                                                                                                       jdbc.getUser(),
                                                                                                                       jdbc.getPassword() );
            pool.releaseConnection( conn, connParam.getDriver(), connParam.getUrl(), connParam.getUser(),
                                    connParam.getPasswd() );
        } catch ( DBPoolException e ) {
            LOG.logWarning( "", e );
        }
    }

}
