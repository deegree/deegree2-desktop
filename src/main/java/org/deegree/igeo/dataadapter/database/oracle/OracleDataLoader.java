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

package org.deegree.igeo.dataadapter.database.oracle;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.dataadapter.database.AbstractDatabaseLoader;
import org.deegree.igeo.mapmodel.DatabaseDatasource;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;
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
        super( datasource );
    }

    protected Object handleGeometryValue( Object value, CoordinateSystem crs )
                            throws Exception {
        // use reflections to avoid dependency on oracle libraries for compiling the code
        Class<?> clzz = Class.forName( "oracle.spatial.geometry.JGeometry" );
        Method m = clzz.getMethod( "load", new Class[] { Class.forName( "oracle.sql.STRUCT" ) } );
        Object o = m.invoke( null, new Object[] { value } );
        Class<?> clzz2 = Class.forName( "org.deegree.io.datastore.sql.oracle.JGeometryAdapter" );
        m = clzz2.getMethod( "wrap", new Class[] { clzz, CoordinateSystem.class } );
        return m.invoke( null, new Object[] { o, crs } );
    }

    protected PreparedStatement createPreparedStatement( DatabaseDatasource datasource, Envelope envelope,
                                                         Connection conn )
                            throws Exception {
        CoordinateSystem coordinateSystem = envelope.getCoordinateSystem();

        String nativeCRS = coordinateSystem.getLocalName();
        String envCRS = nativeCRS;
        if ( envelope.getCoordinateSystem() != null ) {
            envCRS = envelope.getCoordinateSystem().getLocalName();
        }

        // use the bbox operator (&&) to filter using the spatial index
        if ( !( nativeCRS.equals( envCRS ) ) ) {
            GeoTransformer gt = new GeoTransformer( coordinateSystem );
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

        PreparedStatement stmt;
        String sqlTemplate = datasource.getSqlTemplate();
        if ( sqlTemplate.trim().toUpperCase().endsWith( " WHERE" ) ) {
            LOG.logDebug( "performed SQL: ", sqlTemplate );
            stmt = conn.prepareStatement( sqlTemplate + query );
        } else if ( sqlTemplate.trim().toUpperCase().indexOf( " WHERE " ) < 0 ) {
            LOG.logDebug( "performed SQL: ", sqlTemplate + " WHERE " + query );
            stmt = conn.prepareStatement( sqlTemplate + " WHERE " + query );
        } else {
            LOG.logDebug( "performed SQL: ", sqlTemplate + " AND " + query );
            stmt = conn.prepareStatement( sqlTemplate + " AND " + query );
        }

        LOG.logDebug( "Converting JGeometry to STRUCT." );
        m = clzz.getMethod( "store",
                            new Class[] { Class.forName( "oracle.spatial.geometry.JGeometry" ), conn.getClass() } );
        Object struct = m.invoke( null, new Object[] { jgeom, conn } );
        stmt.setObject( 1, struct, java.sql.Types.STRUCT );

        // TODO
        // if connection is not available ask user updated connection parameters
        stmt.setMaxRows( maxFeatures );
        // seems that not every oracle version supports this
        // stmt.setQueryTimeout( timeout );
        return stmt;
    }

}
