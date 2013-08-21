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
package org.deegree.desktop.commands.model;

import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.Cache;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.config.JDBCConnection;
import org.deegree.desktop.config.Util;
import org.deegree.desktop.mapmodel.AuthenticationInformation;
import org.deegree.desktop.mapmodel.DatabaseDatasource;
import org.deegree.desktop.mapmodel.Datasource;
import org.deegree.desktop.mapmodel.DirectStyle;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.LayerGroup;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.mapmodel.NamedStyle;
import org.deegree.desktop.settings.Settings;
import org.deegree.graphics.sld.UserStyle;
import org.deegree.desktop.config.DatabaseDatasourceType;
import org.deegree.desktop.config.DatabaseDatasourceType.GeometryField;
import org.deegree.desktop.config.DirectStyleType;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.Command;
import org.deegree.model.Identifier;

/**
 * {@link Command} implementation for adding a layer reading its data from a database
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class AddDatabaseLayerCommand extends AbstractCommand {

    private static final QualifiedName name = new QualifiedName( "Add Database Layer" );

    private String driver;

    private String database;

    private String user;

    private String password;

    private String geomField;

    private String pkField;

    private String sql;

    private double minScale;

    private double maxScale;

    private boolean supportTransactions;

    private String layerName;

    private MapModel mapModel;

    private boolean lazyLoading;

    private String nativeCRS;

    private String srid;

    private boolean performed = false;

    private Layer newLayer;

    private final boolean saveLogin;

    /**
     * 
     * @param mapModel
     * @param driver
     * @param database
     * @param user
     * @param password
     * @param geomField
     * @param pkField
     * @param minScale
     * @param maxScale
     * @param supportTransactions
     * @param lazyLoading
     * @param nativeCRS
     * @param sql
     * @param layerName
     */
    public AddDatabaseLayerCommand( MapModel mapModel, String driver, String database, String user, String password,
                                    String geomField, String pkField, double minScale, double maxScale,
                                    boolean supportTransactions, boolean lazyLoading, String nativeCRS, String sql,
                                    String srid, String layerName, boolean savePasswd ) {
        this.mapModel = mapModel;
        this.driver = driver;
        this.database = database;
        this.user = user;
        this.password = password;
        this.geomField = geomField;
        this.pkField = pkField;
        this.sql = sql;
        this.minScale = minScale;
        this.maxScale = maxScale;
        this.supportTransactions = supportTransactions;
        this.layerName = layerName;
        this.lazyLoading = lazyLoading;
        this.nativeCRS = nativeCRS;
        this.srid = srid;
        this.saveLogin = savePasswd;

    }

    @Override
    public void execute()
                            throws Exception {
        List<Datasource> datasources = new ArrayList<Datasource>();

        AuthenticationInformation authenticationInformation = null;

        Cache cache = null;

        DatabaseDatasourceType dsType = new DatabaseDatasourceType();
        dsType.setName( "ds_" + layerName );
        dsType.setMinScaleDenominator( minScale );
        dsType.setMaxScaleDenominator( maxScale );
        dsType.setEditable( supportTransactions );
        dsType.setQueryable( true );
        dsType.setLazyLoading( lazyLoading );
        dsType.setSupportToolTips( true );
        dsType.setExtent( Util.convertEnvelope( mapModel.getMaxExtent() ) );
        GeometryField gf = new GeometryField();
        gf.setValue( geomField );
        gf.setSrs( srid );
        dsType.setGeometryField( gf );
        dsType.setPrimaryKeyField( pkField );
        JDBCConnection jdbc = new JDBCConnection( driver, database, user, password, saveLogin );
        dsType.setSqlTemplate( sql );
        dsType.setNativeCRS( nativeCRS );

        DatabaseDatasource dbDatasource = new DatabaseDatasource( dsType, authenticationInformation, cache, jdbc );
        datasources.add( dbDatasource );

        newLayer = new Layer( mapModel, new Identifier( layerName ), layerName, null, datasources, null );
        List<NamedStyle> styles = new ArrayList<NamedStyle>();
        Settings settings = mapModel.getApplicationContainer().getSettings();
        DirectStyleType dst = new DirectStyleType();
        dst.setCurrent( true );
        UserStyle us = settings.getWFSDefaultStyle().getDefaultStyle();
        dst.setName( us.getName() );
        dst.setTitle( us.getTitle() );
        dst.setAbstract( us.getAbstract() );
        dst.setCurrent( true );
        styles.add( new DirectStyle( dst, us, newLayer ) );
        // styles.add( new NamedStyle( dst, newLayer ) );
        newLayer.setStyles( styles );
        newLayer.setMinScaleDenominator( minScale );
        newLayer.setMaxScaleDenominator( maxScale );
        newLayer.setEditable( supportTransactions );

        newLayer.setVisible( true );
        if ( mapModel.getLayerGroups().size() == 0 ) {
            LayerGroup layerGroup = new LayerGroup( mapModel, new Identifier(), "LayerGroup", "" );
            mapModel.insert( layerGroup, null, null, false );
        }
        mapModel.insert( newLayer, mapModel.getLayerGroups().get( 0 ), null, false );
        performed = true;
        if ( processMonitor != null ) {
            processMonitor.cancel();
            processMonitor = null;
        }
        fireCommandProcessedEvent();
    }

    @Override
    public QualifiedName getName() {
        return name;
    }

    @Override
    public Object getResult() {
        return null;
    }

    @Override
    public boolean isUndoSupported() {
        return true;
    }

    @Override
    public void undo()
                            throws Exception {
        if ( performed ) {
            mapModel.remove( newLayer );
            performed = false;
        }
    }
}
