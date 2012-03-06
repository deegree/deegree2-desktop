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
package org.deegree.igeo.dataadapter.database.postgis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.config.JDBCConnection;
import org.deegree.igeo.dataadapter.database.AbstractDatabaseWriter;
import org.deegree.igeo.dataadapter.database.DatabaseDataWriter;
import org.deegree.igeo.mapmodel.DatabaseDatasource;
import org.deegree.io.DBPoolException;
import org.deegree.io.datastore.sql.postgis.PGgeometryAdapter;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.postgresql.PGConnection;

/**
 * Concrete {@link DatabaseDataWriter} for Postgis
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class PostgisDataWriter extends AbstractDatabaseWriter {

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

    protected int setFieldValues( PreparedStatement stmt, DatabaseDatasource datasource, Feature feature,
                                   PropertyType[] pt, String table, Connection conn )
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
        return pt.length;
    }

    protected void setWhereCondition( PreparedStatement stmt, DatabaseDatasource datasource, PropertyType[] pt,
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

    @Override
    protected Connection acquireConnection( JDBCConnection jdbc )
                            throws DBPoolException, SQLException {
        Connection conn = super.acquireConnection( jdbc );
        PGConnection pgConn = (PGConnection) conn;
        pgConn.addDataType( GEOMETRY_DATATYPE_NAME, pgGeometryClass );
        pgConn.addDataType( BOX3D_DATATYPE_NAME, pgBox3dClass );
        return conn;
    }

}
