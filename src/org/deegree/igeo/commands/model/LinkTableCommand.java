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
package org.deegree.igeo.commands.model;

import static java.util.Collections.singletonList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.Pair;
import org.deegree.igeo.config.EnvelopeType;
import org.deegree.igeo.config.MemoryDatasourceType;
import org.deegree.igeo.config.LayerType.MetadataURL;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.dataadapter.LinkedTable;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MemoryDatasource;
import org.deegree.kernel.AbstractCommand;
import org.deegree.model.Identifier;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Envelope;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class LinkTableCommand extends AbstractCommand {

    private static final QualifiedName name = new QualifiedName( "Link Table Command" );

    private static final ILogger LOG = LoggerFactory.getLogger( LinkTableCommand.class );

    private Layer resultLayer;

    private Layer sourceLayer;

    private LinkedTable linkedTable;

    private MapModel mm;

    private boolean view = false;

    private String title;

    /**
     * @param linkedTable
     *            the linkedTable to set
     */
    public void setLinkedTable( LinkedTable linkedTable ) {
        this.linkedTable = linkedTable;
    }

    /**
     * 
     * @param mapModel
     */
    public void setMapModel( MapModel mapModel ) {
        this.mm = mapModel;
        this.sourceLayer = mm.getLayersSelectedForAction( MapModel.SELECTION_ACTION ).get( 0 );
    }

    /**
     * 
     * @param view
     */
    public void setView( boolean view ) {
        this.view = view;
    }

    /**
     * @param title
     */
    public void setLayerTitle( String title ) {
        this.title = title;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {
        if ( view ) {
            String abstract_ = "created from layer " + sourceLayer.getTitle() + " and table " + linkedTable.getTitle();
            List<Datasource> datasources = sourceLayer.getDatasources();
            datasources.get( 0 ).addLinkedTable( linkedTable.getLinkedTableType() );
            resultLayer = new Layer( mm, new Identifier( title ), title, abstract_, datasources,
                                     new ArrayList<MetadataURL>() );
            resultLayer.setEditable( false );
            resultLayer.setMinScaleDenominator( sourceLayer.getMinScaleDenominator() );
            resultLayer.setMaxScaleDenominator( sourceLayer.getMaxScaleDenominator() );
            mm.insert( resultLayer, sourceLayer.getParent(), sourceLayer, false );
        } else {
            linkAsNewLayer();
        }
    }

    /**
     * 
     * @throws Exception
     */
    void linkAsNewLayer()
                            throws Exception {
        FeatureType ft = createFeatureType();

        FeatureAdapter adapter = (FeatureAdapter) sourceLayer.getDataAccess().get( 0 );
        Envelope env = adapter.getDatasource().getExtent();
        MemoryDatasourceType mdst = new MemoryDatasourceType();
        EnvelopeType et = new EnvelopeType();
        et.setMinx( env.getMin().getX() );
        et.setMiny( env.getMin().getY() );
        et.setMaxx( env.getMax().getX() );
        et.setMaxy( env.getMax().getY() );
        et.setCrs( mm.getEnvelope().getCoordinateSystem().getPrefixedName() );
        mdst.setExtent( et );

        FeatureCollection fc = createFeatureCollection( ft );
        if ( fc.size() > 0 ) {
            Datasource ds = new MemoryDatasource( mdst, null, null, fc );

            String abstract_ = "created from layer " + sourceLayer.getTitle() + " and table " + linkedTable.getTitle();
            resultLayer = new Layer( mm, new Identifier( title ), title, abstract_, singletonList( ds ),
                                     Collections.<MetadataURL> emptyList() );
            resultLayer.setEditable( true );
            resultLayer.setMinScaleDenominator( sourceLayer.getMinScaleDenominator() );
            resultLayer.setMaxScaleDenominator( sourceLayer.getMaxScaleDenominator() );
            mm.insert( resultLayer, sourceLayer.getParent(), sourceLayer, false );
        } else {
            LOG.logWarning( "not matching set of keys found; no new layer will be created" );
        }
    }

    /**
     * @return feature collection as merge of feature collection assigned to sourceLayer and linkedTable
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    private FeatureCollection createFeatureCollection( FeatureType ft )
                            throws Exception {
        FeatureAdapter adapter = (FeatureAdapter) sourceLayer.getDataAccess().get( 0 );
        FeatureCollection layerFC = adapter.getFeatureCollection();
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
            Object[][] rows = linkedTable.getRows( key );
            if ( rows.length > 0 ) {
                // create a new feature for each matching row
                String table = linkedTable.getLinkedTableType().getTitle();
                FeatureType layerFt = adapter.getSchema();
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
                        try {
                            QualifiedName qn = new QualifiedName( column[k] + "_" + table, layerFt.getNameSpace() );
                            fp[c++] = FeatureFactory.createFeatureProperty( qn, rows[j][k] );
                        } catch ( Exception e ) {
                            throw new Exception(
                                                 Messages.getMessage(
                                                                      sourceLayer.getOwner().getApplicationContainer().getLocale(),
                                                                      "$MD11612" ) );
                        }
                    }
                    fc.add( FeatureFactory.createFeature( "UUID_" + UUID.randomUUID().toString(), ft, fp ) );
                }
            }
        }
        return fc;
    }

    /**
     * @return
     */
    private FeatureType createFeatureType() {
        FeatureAdapter adapter = (FeatureAdapter) sourceLayer.getDataAccess().get( 0 );
        FeatureType layerFt = adapter.getSchema();
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
        QualifiedName qn = new QualifiedName( sourceLayer.getTitle() + "_" + table, layerFt.getNameSpace() );
        return FeatureFactory.createFeatureType( qn, false, allPt );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getName()
     */
    public QualifiedName getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getResult()
     */
    public Object getResult() {
        return resultLayer;
    }

}
