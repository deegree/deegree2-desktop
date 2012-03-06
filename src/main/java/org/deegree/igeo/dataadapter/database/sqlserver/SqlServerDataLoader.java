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
package org.deegree.igeo.dataadapter.database.sqlserver;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.dataadapter.database.AbstractDatabaseLoader;
import org.deegree.igeo.mapmodel.DatabaseDatasource;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.JTSAdapter;

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
        // ResultSet rs = stmt.executeQuery( "select geom.STAsText(), geom.STAsBinary()  from testtable" );
        // byte[] b = (byte[]) rs.getObject( 2 );
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
        // geom.STIntersects( geometry::STGeomFromText(?, 0) ) = 1" )
        String query = "geometry.STIntersects( geometry::STGeomFromText(?, 0) ) = 1";

        String completeQuery;
        String sqlTemplate = datasource.getSqlTemplate();
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
        LOG.logInfo(completeQuery);
        
        // POLYGON ((425593 4503920,425593 4505159,427138 4505159,427138 4503920,425593 4503920))
        StringBuffer geomAsString = new StringBuffer( 500 );
        geomAsString.append( "POLYGON ((" );
        geomAsString.append( envelope.getMin().getX() ).append( ' ' ).append( envelope.getMin().getY() ).append( ',' );
        geomAsString.append( envelope.getMin().getX() ).append( ' ' ).append( envelope.getMin().getY() ).append( ',' );
        geomAsString.append( envelope.getMin().getX() ).append( ' ' ).append( envelope.getMax().getY() ).append( ',' );
        geomAsString.append( envelope.getMax().getX() ).append( ' ' ).append( envelope.getMax().getY() ).append( ',' );
        geomAsString.append( envelope.getMax().getX() ).append( ' ' ).append( envelope.getMin().getY() ).append( ',' );
        geomAsString.append( envelope.getMin().getX() ).append( ' ' ).append( envelope.getMin().getY() );
        geomAsString.append( "))" );
        stmt.setString( 1, geomAsString.toString() );

        // TODO
        // if connection is not available ask user updated connection parameters
        stmt.setMaxRows( maxFeatures );
        return stmt;
    }
}
