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
package org.deegree.desktop.mapmodel;

import net.sf.ehcache.Cache;

import org.deegree.desktop.config.JDBCConnection;
import org.deegree.desktop.dataadapter.jdbc.JdbcConnectionCreator;
import org.deegree.desktop.config.DatabaseDatasourceType;

/**
 * data source description for databases
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class DatabaseDatasource extends Datasource {

    private JDBCConnection jdbc;

    /**
     * 
     * @param dsType
     * @param authenticationInformation
     * @param cache
     * @param saveLogin
     */
    public DatabaseDatasource( DatabaseDatasourceType dsType, AuthenticationInformation authenticationInformation,
                               Cache cache, JDBCConnection jdbc ) {
        super( dsType, authenticationInformation, cache );
        setJdbc( jdbc );
    }

    /**
     * 
     * @return connection information
     */
    public JDBCConnection getJdbc() {
        return jdbc;
    }

    /**
     * @param jdbc
     *            the database connection description to set
     */
    public void setJdbc( JDBCConnection jdbc ) {
        this.jdbc = jdbc;
        ( (DatabaseDatasourceType) dsType ).setConnection( JdbcConnectionCreator.getAsJDBCConnectionType( jdbc ) );
    }

    /**
     * 
     * @return SQL template
     */
    public String getSqlTemplate() {
        return ( (DatabaseDatasourceType) dsType ).getSqlTemplate();
    }

    /**
     * @param sqlTemplate
     *            the sqlTemplate to set
     */
    public void setSqlTemplate( String sqlTemplate ) {
        ( (DatabaseDatasourceType) dsType ).setSqlTemplate( sqlTemplate );
    }

    /**
     * 
     * @return name of the geometry field (in upper case characters)
     */
    public String getGeometryFieldName() {
        return ( (DatabaseDatasourceType) dsType ).getGeometryField().getValue().toUpperCase();
    }

    /**
     * 
     * @param name
     *            name of the geometry field
     */
    public void setGeometryFieldName( String name ) {
        ( (DatabaseDatasourceType) dsType ).getGeometryField().setValue( name.toUpperCase() );
    }

    /**
     * 
     * @return name of the PrimaryKey field (in upper case characters)
     */
    public String getPrimaryKeyFieldName() {
        return ( (DatabaseDatasourceType) dsType ).getPrimaryKeyField().toUpperCase();
    }

    /**
     * 
     * @param name
     *            name of the PrimaryKey field
     */
    public void setPrimaryKeyFieldName( String name ) {
        ( (DatabaseDatasourceType) dsType ).setPrimaryKeyField( name );
    }

    /**
     * 
     * @return srid (database internal CRS code) of geometry column assigned to a database data source
     */
    public String getSRID() {
        return ( (DatabaseDatasourceType) dsType ).getGeometryField().getSrs();
    }

    /**
     * 
     * @param srid
     *            srid (database internal CRS code) of geometry column assigned to a database data source
     */
    public void setSRID( String srid ) {
        ( (DatabaseDatasourceType) dsType ).getGeometryField().setSrs( srid );
    }

    public boolean isSaveLogin() {
        return jdbc.isSaveLogin();
    }

}