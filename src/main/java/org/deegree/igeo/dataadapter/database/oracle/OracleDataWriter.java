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
package org.deegree.igeo.dataadapter.database.oracle;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.deegree.datatypes.Types;
import org.deegree.igeo.dataadapter.database.AbstractDatabaseWriter;
import org.deegree.igeo.dataadapter.database.DatabaseDataWriter;
import org.deegree.igeo.mapmodel.DatabaseDatasource;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.schema.PropertyType;

/**
 * concrete {@link DatabaseDataWriter} for Oracle Spatial
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class OracleDataWriter extends AbstractDatabaseWriter {
   
    protected int setFieldValues( PreparedStatement stmt, DatabaseDatasource datasource, Feature feature,
                                 PropertyType[] pt, String table, Connection conn )
                            throws Exception {
        for ( int i = 0; i < pt.length; i++ ) {
            Object value = feature.getDefaultProperty( pt[i].getName() ).getValue();
            if ( value != null ) {
                if ( pt[i].getType() == Types.GEOMETRY ) {
                    Class<?> clzz = Class.forName( "org.deegree.io.datastore.sql.oracle.JGeometryAdapter" );
                    Class<?>[] p = new Class[] { Class.forName( "org.deegree.model.spatialschema.Geometry" ),
                                                Class.forName( "java.lang.Integer" ) };
                    Method m = clzz.getMethod( "load", p );
                    value = m.invoke( null, new Object[] { value }, Integer.parseInt( datasource.getSRID() ) );
                    // value = JGeometryAdapter.export( (Geometry) value, Integer.parseInt( datasource.getSRID() ) );
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
            Object value = feature.getDefaultProperty( pt[i].getName() ).getValue();
            if ( pt[i].getName().getLocalName().equalsIgnoreCase( datasource.getPrimaryKeyFieldName() ) ) {
                String s = feature.getId().substring( 3 );
                if ( pt[i].getType() == Types.BIGINT ) {
                    value = Long.parseLong( s );
                } else if ( pt[i].getType() == Types.SMALLINT ) {
                    value = Short.parseShort( s );
                } else if ( pt[i].getType() == Types.INTEGER ) {
                    value = Integer.parseInt( s );
                } else if ( pt[i].getType() == Types.DECIMAL || pt[i].getType() == Types.DOUBLE ) {
                    value = Double.parseDouble( s );
                } else if ( pt[i].getType() == Types.FLOAT ) {
                    value = Float.parseFloat( s );
                } else {
                    value = s;
                }
                stmt.setObject( index, value, pt[i].getType() );
                break;
            }
        }
    }

}
