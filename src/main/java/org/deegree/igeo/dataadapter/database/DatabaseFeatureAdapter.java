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

package org.deegree.igeo.dataadapter.database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.config.JDBCConnection;
import org.deegree.igeo.dataadapter.DataAccessException;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.dataadapter.MaxDatabaseColumnValueGenerator;
import org.deegree.igeo.dataadapter.UUIDValueGenerator;
import org.deegree.igeo.dataadapter.database.oracle.OracleDataLoader;
import org.deegree.igeo.dataadapter.database.oracle.OracleDataWriter;
import org.deegree.igeo.dataadapter.database.postgis.PostgisDataLoader;
import org.deegree.igeo.dataadapter.database.postgis.PostgisDataWriter;
import org.deegree.igeo.mapmodel.DatabaseDatasource;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.ValueGenerator;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Envelope;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class DatabaseFeatureAdapter extends FeatureAdapter {

    private static final ILogger LOG = LoggerFactory.getLogger( DatabaseFeatureAdapter.class );

    private boolean isLazyLoading;

    private JDBCConnection jdbc;

    private Envelope lastEnv;

    /**
     * @param datasource
     * @param layer
     * @param mapModel
     * @param isLazyLoading
     */
    public DatabaseFeatureAdapter( DatabaseDatasource datasource, Layer layer, MapModel mapModel, boolean isLazyLoading ) {
        super( datasource, layer, mapModel );
        this.isLazyLoading = isLazyLoading;
        this.jdbc = datasource.getJdbc();
        refresh();
        loadSchema();
    }

    /**
     * loads the GML application schema assigned with the feature types of a WFS datasource
     * 
     */
    private void loadSchema() {
        if ( schemas.get( datasource.getName() ) == null ) {
            String driver = jdbc.getDriver();
            DatabaseDataLoader loader = null;
            if ( driver.toUpperCase().indexOf( "POSTGRES" ) > -1 ) {
                loader = new PostgisDataLoader( (DatabaseDatasource) datasource );
            } else if ( driver.toUpperCase().indexOf( "ORACLE" ) > -1 ) {
                loader = new OracleDataLoader( (DatabaseDatasource) datasource );
            } else {
                // TODO
                throw new DataAccessException( "database/driver: " + driver + " is not supported yet" );
            }
            FeatureType ft = loader.getFeatureType();
            schemas.put( datasource.getName(), ft );
        }
    }

    @Override
    public FeatureCollection getFeatureCollection() {
        if ( this.isLazyLoading ) {
            synchronized ( datasource ) {
                double min = datasource.getMinScaleDenominator();
                double max = datasource.getMaxScaleDenominator();
                if ( mapModel.getScaleDenominator() >= min && mapModel.getScaleDenominator() < max ) {
                    Envelope currentEnv = mapModel.getEnvelope();
                    if ( lastEnv == null || ( !currentEnv.equals( lastEnv ) && !lastEnv.contains( currentEnv ) ) ) {
                        // if lazy loading is set data must be loaded with current state of map model
                        loadData( mapModel.getEnvelope() );
                        lastEnv = mapModel.getEnvelope();
                        // perform all inserts, updates, deletes that has been performed on this
                        // datasource adapter on the feature collection read from the adapted datasource
                        updateFeatureCollection();
                    }
                }
            }
        }
        return featureCollections.get( datasource.getName() );
    }

    @Override
    public void commitChanges()
                            throws IOException {
        List<FeatureAdapter.Changes> changeList = changes.get( datasource.getName() );
        List<FeatureAdapter.Changes> tmp = null;
        try {
            DatabaseDataWriter writer = instantiateWriter();
            if ( changeList != null && changeList.size() > 0 ) {
                // a copy of the changelist is made to enable roll back in case of an error
                tmp = new ArrayList<Changes>( changeList );
                Collections.copy( tmp, changeList );
                FeatureCollection fc = getInsertCollection( changeList );
                if ( fc.size() > 0 ) {
                    writer.insertFeatures( (DatabaseDatasource) getDatasource(), fc, layer );
                }

                fc = getUpdateCollection( changeList );
                if ( fc.size() > 0 ) {
                    writer.updateFeatures( (DatabaseDatasource) getDatasource(), fc, layer );
                }

                fc = getDeleteCollection( changeList );
                if ( fc.size() > 0 ) {
                    writer.deleteFeatures( (DatabaseDatasource) getDatasource(), fc, layer );
                }
            }
            changes.get( datasource.getName() ).clear();
        } catch ( Exception e ) {
            changeList = new ArrayList<Changes>( tmp );
            Collections.copy( changeList, tmp );
            changes.put( datasource.getName(), changeList );
            LOG.logError( e.getMessage(), e );
            throw new IOException( e.getMessage() );
        }
    }

    /**
     * @return database write class
     */
    private DatabaseDataWriter instantiateWriter() {
        String driver = jdbc.getDriver();
        DatabaseDataWriter writer = null;
        if ( driver.toUpperCase().indexOf( "POSTGRES" ) > -1 ) {
            writer = new PostgisDataWriter();
        } else if ( driver.toUpperCase().indexOf( "ORACLE" ) > -1 ) {
            writer = new OracleDataWriter();
        } else {
            // TODO
            throw new DataAccessException( "database/driver: " + driver + " is not supported yet" );
        }
        return writer;
    }

    @Override
    public void refresh() {
        if ( featureCollections.get( datasource.getName() ) == null & !this.isLazyLoading ) {
            // if feature collection has already been loaded for a not lazy loading datasource
            // it don't have to loaded again.
            loadData( mapModel.getMaxExtent() );
        }

    }

    private void loadData( Envelope envelope ) {
        try {
            String driver = jdbc.getDriver();
            DatabaseDataLoader loader = null;
            if ( driver.toUpperCase().indexOf( "POSTGRES" ) > -1 ) {
                loader = new PostgisDataLoader( (DatabaseDatasource) datasource );
            } else if ( driver.toUpperCase().indexOf( "ORACLE" ) > -1 ) {
                loader = new OracleDataLoader( (DatabaseDatasource) datasource );
            } else {
                // TODO
                throw new DataAccessException( "database/driver: " + driver + " is not supported yet" );
            }
            FeatureCollection fc = loader.load( envelope );
            fc = transform( fc );
            featureCollections.put( datasource.getName(), fc );
            datasource.setExtent( mapModel.getEnvelope() );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new DataAccessException( e );
        } finally {
            fireLoadingFinishedEvent();
        }
    }

    @Override
    public Feature getDefaultFeature( QualifiedName featureType ) {
        // find GML schema matching passed feature type
        Collection<FeatureType> schemaCol = schemas.values();
        FeatureType ft = null;
        for ( FeatureType tmp : schemaCol ) {
            if ( tmp.getName().equals( featureType ) ) {
                ft = tmp;
                break;
            }
        }
        // if no matching gml schema could be found null will be returned
        if ( ft == null ) {
            LOG.logWarning( "no GML schema found for featuretype: " + featureType );
            return null;
        }

        // create feature instance from GML schema describing passed feature type
        //
        // notice: since deegree WFS does not be type safe for complex features
        // properties of type Types.FEATURE and Types.FEATURECOLLECTION will
        // be set to null. Also properties of type Types.GEOMETRY will be set to
        // null because a features geometry must be digitized via a map client
        PropertyType[] pt = ft.getProperties();
        FeatureProperty[] fp = new FeatureProperty[pt.length];
        DatabaseDatasource ds = (DatabaseDatasource) datasource;
        String sql = ds.getSqlTemplate().toUpperCase();
        int idx = sql.indexOf( " FROM " );
        String table = sql.substring( idx + 6 );
        for ( int i = 0; i < fp.length; i++ ) {
            int typeCode = pt[i].getType();
            if ( pt[i].getName().getLocalName().equalsIgnoreCase( ds.getPrimaryKeyFieldName() ) ) {
                ValueGenerator valueGenerator = null;
                switch ( typeCode ) {
                case Types.NUMERIC:
                case Types.DECIMAL:
                case Types.DOUBLE:
                case Types.REAL:
                case Types.FLOAT:
                case Types.INTEGER:
                case Types.SMALLINT:
                case Types.BIGINT: {
                    valueGenerator = new MaxDatabaseColumnValueGenerator( jdbc, table, ds.getPrimaryKeyFieldName() );
                    break;
                }
                case Types.CHAR:
                case Types.VARCHAR: {
                    valueGenerator = new UUIDValueGenerator();
                    break;
                }
                }
                fp[i] = FeatureFactory.createGeneratedValueFeatureProperty( pt[i].getName(), valueGenerator );
            } else {
                // second parameter forces creating a double value if type equals Types.NUMERIC
                Object value = getDefaultValueForType( typeCode, 1 );
                fp[i] = FeatureFactory.createFeatureProperty( pt[i].getName(), value );
            }
        }
        return FeatureFactory.createFeature( "ID1", ft, fp );
    }

}
