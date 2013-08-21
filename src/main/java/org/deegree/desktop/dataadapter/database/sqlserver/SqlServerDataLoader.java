//$HeadURL: svn+ssh://lbuesching@svn.wald.intevation.de/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2012 by:
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
package org.deegree.desktop.dataadapter.database.sqlserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deegree.desktop.dataadapter.database.AbstractDatabaseLoader;
import org.deegree.desktop.mapmodel.DatabaseDatasource;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.JTSAdapter;
import org.deegree.model.spatialschema.WKTAdapter;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKBReader;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class SqlServerDataLoader extends AbstractDatabaseLoader {

    private static final ILogger LOG = LoggerFactory.getLogger( SqlServerDataLoader.class );

    public SqlServerDataLoader( DatabaseDatasource datasource ) {
        super( datasource );
    }

    @Override
    protected Object handleGeometryValue( Object value, CoordinateSystem crs )
                            throws Exception {
        WKBReader reader = new WKBReader();
        Geometry geom = reader.read( (byte[]) value );
        return JTSAdapter.wrap( geom );
    }

    @Override
    protected PreparedStatement createPreparedStatement( DatabaseDatasource datasource, Envelope envelope,
                                                         Connection conn )
                            throws Exception {
        CoordinateSystem coordinateSystem = envelope.getCoordinateSystem();

        String nativeCRS = datasource.getNativeCoordinateSystem().getLocalName();
        String envCRS = nativeCRS;
        if ( coordinateSystem != null ) {
            envCRS = coordinateSystem.getLocalName();
        }

        // use the bbox operator (&&) to filter using the spatial index
        if ( !( nativeCRS.equals( envCRS ) ) ) {
            GeoTransformer gt = new GeoTransformer( nativeCRS );
            envelope = gt.transform( envelope, coordinateSystem );
        }
        String query = datasource.getGeometryFieldName() + ".STIntersects( geometry::STGeomFromText(?, " + nativeCRS
                       + ") ) = 1";

        String completeQuery;
        String sqlTemplate = datasource.getSqlTemplate();
        Pattern p = Pattern.compile( "\\s*select\\s+[*]\\s+from\\s+([a-zA-Z_0-9.]+)\\s*", Pattern.CASE_INSENSITIVE );
        Matcher m = p.matcher( sqlTemplate );
        if ( m.find() ) {

            String tableNamePattern = m.group( 1 );
            ResultSet columns = conn.getMetaData().getColumns( null, null, tableNamePattern, "%" );
            String cols = "";
            boolean isFirst = true;
            while ( columns.next() ) {
                String columnName = columns.getString( "COLUMN_NAME" );
                if ( datasource.getGeometryFieldName().equalsIgnoreCase( columnName ) ) {
                    columnName = columnName + ".STAsBinary() as " + columnName;
                }
                if ( !isFirst ) {
                    cols += ",";
                }
                cols += columnName;
                isFirst = false;
            }
            if ( cols.length() > 0 ) {
                sqlTemplate = sqlTemplate.replace( "*", cols );
            }
        }
        if ( sqlTemplate.trim().toUpperCase().endsWith( " WHERE" ) ) {
            LOG.logDebug( "performed SQL: ", sqlTemplate );
            completeQuery = sqlTemplate + query;
        } else if ( sqlTemplate.trim().toUpperCase().indexOf( " WHERE " ) < 0 ) {
            LOG.logDebug( "performed SQL: ", sqlTemplate + " WHERE " + query );
            completeQuery = sqlTemplate + " WHERE " + query;
        } else {
            LOG.logDebug( "performed SQL: ", sqlTemplate + " AND " + query );
            completeQuery = sqlTemplate + " AND " + query;
        }
        PreparedStatement stmt = conn.prepareStatement( completeQuery );
        LOG.logInfo( completeQuery );
        String queryEnv = WKTAdapter.export( GeometryFactory.createSurface( envelope,
                                                                            datasource.getNativeCoordinateSystem() ) ).toString();
        stmt.setString( 1, queryEnv );
        LOG.logInfo( queryEnv );

        stmt.setMaxRows( maxFeatures );
        return stmt;
    }
}
