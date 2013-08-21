//$HeadURL$
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
package org.deegree.desktop.dataadapter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.Pair;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;

/**
 * adapter class for linking tables of alpha numeric data to layers
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class LinkedTableAdapter extends FeatureAdapter {

    private static final ILogger LOG = LoggerFactory.getLogger( LinkedTableAdapter.class );

    // private boolean isLazyLoading = false;

    private FeatureAdapter featureAdapter;

    private LinkedTable linkedTable;

    private FeatureCollection fc;

    private FeatureType ft;

    /**
     * 
     * @param featureAdapter
     * @param linkedTable
     */
    public LinkedTableAdapter( FeatureAdapter featureAdapter, LinkedTable linkedTable ) {
        super( featureAdapter.getDatasource(), featureAdapter.getLayer(), featureAdapter.getLayer().getOwner() );
        this.featureAdapter = featureAdapter;
        this.linkedTable = linkedTable;
        refresh();
    }

    @Override
    public FeatureCollection getFeatureCollection() {
        // TODO check if handling of lazy loading needs to be implemented
        // if ( fc == null && !isLazyLoading ) {
        try {
            fc = createFeatureCollection();
        } catch ( IOException e ) {
            LOG.logError( e );
        }
        // } else {
        // try {
        // fc = createFeatureCollection();
        // } catch ( IOException e ) {
        // LOG.logError( e );
        // }
        // }
        return fc;
    }

    @Override
    public FeatureType getSchema() {
        if ( ft == null ) {
            this.ft = createFeatureType();
        }
        return ft;
    }

    @Override
    public void commitChanges()
                            throws IOException {
        // TODO
        // not supported

        // because 'featureAdapter' may also be an instance of LinkedTableAdapter further invocations
        // of commitChanges() possibly will occur until the instance on which the method is invoked is
        // not an instance of LinkedTableAdapter
        // featureAdapter.commitChanges();

        // List<FeatureAdapter.Changes> changeList = changes.get( datasource.getName() );
        // linkedTable.commitChanges( changeList );
    }

    @Override
    public void refresh() {
        try {
            fc = createFeatureCollection();
        } catch ( IOException e ) {
            LOG.logError( e );
        }
    }

    @Override
    public void refresh( boolean forceReload ) {
        refresh();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        featureAdapter.invalidate();
        fc = null;
    }

    /**
     * @return feature collection as merge of feature collection assigned to sourceLayer and linkedTable
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private FeatureCollection createFeatureCollection()
                            throws IOException {
        getSchema();
        FeatureCollection layerFC = featureAdapter.getFeatureCollection();
        FeatureCollection fc = FeatureFactory.createFeatureCollection( "UUID_" + UUID.randomUUID().toString(),
                                                                       layerFC.size() * 2 );
        List<Pair<QualifiedName, String>> relationKeys = linkedTable.getRelationKeys();
        Pair<String, Object>[] key = new Pair[relationKeys.size()];
        for ( int i = 0; i < layerFC.size(); i++ ) {
            Feature feature = layerFC.getFeature( i );
            // created (multiple) key for selecting table row(s) assigned to current feature
            for ( int j = 0; j < key.length; j++ ) {
                Object value = feature.getDefaultProperty( relationKeys.get( j ).first ).getValue();
                key[j] = new Pair<String, Object>( relationKeys.get( j ).second, value );
            }
            // read tables rows assigned to current feature
            // TODO
            // think of reading data in blocks of 10 or 100 rows to avoid performing one
            // one SQL statement for each row
            Object[][] rows = linkedTable.getRows( key );
            if ( rows.length > 0 ) {
                // create a new feature for each matching row
                String table = linkedTable.getLinkedTableType().getTitle();
                FeatureType layerFt = featureAdapter.getSchema();
                String[] column = linkedTable.getColumnNames();
                for ( int j = 0; j < rows.length; j++ ) {
                    FeatureProperty[] fp = new FeatureProperty[ft.getProperties().length];
                    FeatureProperty[] fps = feature.getProperties();
                    int c = 0;
                    for ( int k = 0; k < fps.length; k++ ) {
                        Object v = feature.getDefaultProperty( fps[k].getName() ).getValue();
                        fp[c++] = FeatureFactory.createFeatureProperty( fps[k].getName(), v );
                    }

                    for ( int k = 0; k < column.length; k++ ) {
                        QualifiedName qn = new QualifiedName( column[k] + "_" + table, layerFt.getNameSpace() );
                        fp[c++] = FeatureFactory.createFeatureProperty( qn, rows[j][k] );
                    }
                    fc.add( FeatureFactory.createFeature( "UUID_" + UUID.randomUUID().toString(), ft, fp ) );
                }
            }
        }
        return fc;
    }

    /**
     * @return common feature type for properties of layers feature type and table columns
     */
    private FeatureType createFeatureType() {
        FeatureType layerFt = featureAdapter.getSchema();
        PropertyType[] layerPt = layerFt.getProperties();
        String table = linkedTable.getLinkedTableType().getTitle();
        String[] column = linkedTable.getColumnNames();
        int[] types = linkedTable.getColumnTypes();
        PropertyType[] allPt = new PropertyType[layerPt.length + column.length];
        int c = 0;
        for ( int i = 0; i < layerPt.length; i++ ) {
            allPt[c++] = layerPt[i];
        }
        for ( int i = 0; i < column.length; i++ ) {
            QualifiedName qn = new QualifiedName( column[i] + "_" + table, layerFt.getNameSpace() );
            allPt[c++] = FeatureFactory.createSimplePropertyType( qn, types[i], true );
        }
        QualifiedName qn = new QualifiedName( layer.getTitle() + "_" + table, layerFt.getNameSpace() );
        return FeatureFactory.createFeatureType( qn, false, allPt );
    }
}
