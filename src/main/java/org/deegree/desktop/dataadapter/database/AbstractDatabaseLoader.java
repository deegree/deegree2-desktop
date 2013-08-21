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

import static org.deegree.framework.util.DateUtil.formatISO8601Date;

import java.math.BigInteger;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.desktop.config.JDBCConnection;
import org.deegree.desktop.dataadapter.DataAccessException;
import org.deegree.desktop.jdbc.DatabaseConnectionManager;
import org.deegree.desktop.mapmodel.DatabaseDatasource;
import org.deegree.desktop.style.model.PropertyValue;
import org.deegree.desktop.views.swing.style.StyleDialog.GEOMTYPE;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.GeometryUtils;
import org.deegree.io.DBPoolException;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.feature.Feature;
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

    private static final String NO_DATA_STRING = "no data";

    private static final int NO_DATA_INT = 0;

    private static final BigInteger NO_DATA_BIGINT = BigInteger.ZERO;

    private static final double NO_DATA_DOUBLE = Double.NaN;

    private static final Date NO_DATA_DATE = new Date();

    public AbstractDatabaseLoader( DatabaseDatasource datasource ) {
        this.datasource = datasource;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.DatabaseDataLoader#setMaxFeatures(int)
     */
    @Override
    public void setMaxFeatures( int maxFeatures ) {
        this.maxFeatures = maxFeatures;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.DatabaseDataLoader#setTimeout(int)
     */
    @Override
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
    protected static int getTypeCode( String geometryFiedName, String columnName, int columnType ) {
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
    protected static void releaseConnection( JDBCConnection jdbc, Connection conn ) {
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
    @Override
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
            releaseConnection( jdbc, conn );
        }
        return fc;
    }

    @SuppressWarnings("unchecked")
    private static void updateDistinctMapsFromFeature( Map<QualifiedName, PropertyValue<?>> properties,
                                                       PropertyType[] propertyTypes, Feature element ) {

        for ( PropertyType type : propertyTypes ) {
            if ( type.getType() != Types.GEOMETRY ) {
                int pt = type.getType();

                switch ( pt ) {
                case Types.VARCHAR: {
                    PropertyValue<String> pv1 = (PropertyValue<String>) properties.get( type.getName() );
                    // TODO
                    FeatureProperty[] fps = element.getProperties( type.getName() );
                    String stringValue = NO_DATA_STRING;
                    if ( fps != null && fps.length > 0 && fps[0].getValue() != null ) {
                        try {
                            if ( fps[0].getValue() instanceof Date ) {
                                stringValue = formatISO8601Date( (Date) fps[0].getValue() );
                            } else {
                                stringValue = (String) fps[0].getValue();
                            }
                        } catch ( ClassCastException e ) {
                            LOG.logError( "Could not cast value to string, where type is VARCHAR" );
                        }
                    }
                    pv1.putInMap( stringValue );
                    break;
                }
                case Types.BIGINT: {
                    PropertyValue<BigInteger> pv5 = (PropertyValue<BigInteger>) properties.get( type.getName() );
                    // TODO
                    FeatureProperty[] fps = element.getProperties( type.getName() );
                    BigInteger intValue = NO_DATA_BIGINT;
                    if ( fps != null && fps.length > 0 && fps[0].getValue() != null ) {
                        try {
                            intValue = new BigInteger( fps[0].getValue().toString() );
                        } catch ( Exception e ) {
                            LOG.logError( "Could not cast value to integer, where type is INTEGER", e );
                        }
                    }
                    pv5.putInMap( intValue );
                    break;
                }

                case Types.INTEGER:
                case Types.SMALLINT: {
                    PropertyValue<Integer> pv2 = (PropertyValue<Integer>) properties.get( type.getName() );
                    // TODO
                    FeatureProperty[] fps = element.getProperties( type.getName() );
                    int intValue = NO_DATA_INT;
                    if ( fps != null && fps.length > 0 && fps[0].getValue() != null ) {
                        try {
                            intValue = Integer.parseInt( fps[0].getValue().toString() );
                        } catch ( Exception e ) {
                            LOG.logError( "Could not cast value to integer, where type is INTEGER", e );
                        }
                    }
                    pv2.putInMap( intValue );
                    break;
                }
                case Types.DOUBLE:
                case Types.FLOAT: {
                    PropertyValue<Double> pv3 = (PropertyValue<Double>) properties.get( type.getName() );
                    // TODO
                    FeatureProperty[] fps = element.getProperties( type.getName() );
                    double doubleValue = NO_DATA_DOUBLE;
                    if ( fps != null && fps.length > 0 ) {
                        Object value = fps[0].getValue();
                        if ( value != null && value instanceof String && ( (String) value ).length() > 0 ) {
                            try {
                                doubleValue = Double.parseDouble( (String) value );
                            } catch ( Exception e ) {
                                LOG.logError( "Could not cast value to double, where type is DOUBLE", e );
                            }
                        } else if ( value instanceof Double ) {
                            try {
                                doubleValue = (Double) value;
                            } catch ( Exception e ) {
                                LOG.logError( "Could not cast value to double, where type is DOUBLE", e );
                            }
                        }
                    }
                    pv3.putInMap( doubleValue );
                    break;
                }
                case Types.DATE: {
                    PropertyValue<Date> pv4 = (PropertyValue<Date>) properties.get( type.getName() );
                    // TODO
                    FeatureProperty[] fps = element.getProperties( type.getName() );
                    Date dateValue = NO_DATA_DATE;
                    if ( fps != null && fps.length > 0 ) {
                        Object value = fps[0].getValue();
                        if ( value != null && value instanceof String && ( (String) value ).length() > 0 ) {
                            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-mm-dd" );
                            try {
                                dateValue = sdf.parse( (String) value );
                            } catch ( ParseException e ) {
                                LOG.logError( "Could not cast value to date, where type is DATE", e );
                            }
                        } else if ( value != null && value instanceof Date ) {
                            dateValue = (Date) value;
                        }
                    }
                    pv4.putInMap( dateValue );
                    break;
                }
                }
            }
        }
    }

    private static void initializeDistinctValuesMap( Map<QualifiedName, PropertyValue<?>> properties,
                                                     Map<QualifiedName, GEOMTYPE> geometryProperties,
                                                     PropertyType[] propertyTypes ) {
        for ( PropertyType type : propertyTypes ) {
            if ( type.getType() != Types.GEOMETRY ) {
                int pt = type.getType();

                switch ( pt ) {
                case Types.VARCHAR:
                    PropertyValue<String> pv1 = new PropertyValue<String>( type );
                    properties.put( type.getName(), pv1 );
                    break;
                case Types.BIGINT:
                    PropertyValue<BigInteger> pv5 = new PropertyValue<BigInteger>( type );
                    properties.put( type.getName(), pv5 );
                    break;

                case Types.INTEGER:
                case Types.SMALLINT:
                    PropertyValue<Integer> pv2 = new PropertyValue<Integer>( type );
                    properties.put( type.getName(), pv2 );
                    break;
                case Types.DOUBLE:
                case Types.FLOAT:
                    PropertyValue<Double> pv3 = new PropertyValue<Double>( type );
                    properties.put( type.getName(), pv3 );
                    break;
                case Types.DATE:
                    PropertyValue<Date> pv4 = new PropertyValue<Date>( type );
                    properties.put( type.getName(), pv4 );
                    break;
                }
            } else {
                geometryProperties.put( type.getName(), null );
            }
        }
    }

    public void loadDistinctValues( Map<QualifiedName, PropertyValue<?>> propertiesMap,
                                    Map<QualifiedName, GEOMTYPE> geometryProperties, PropertyType[] propertyTypes ) {
        initializeDistinctValuesMap( propertiesMap, geometryProperties, propertyTypes );

        JDBCConnection jdbc = datasource.getJdbc();
        PreparedStatement stmt = null;
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = acquireConnection( jdbc );

            stmt = createPreparedStatement( datasource, null, conn );
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
                    Feature f = FeatureFactory.createFeature( "ID_" + pk, featureType, properties );
                    updateDistinctMapsFromFeature( propertiesMap, propertyTypes, f );
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
            releaseConnection( jdbc, conn );
        }
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
