//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2012 by:
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

 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: info@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/

package org.deegree.igeo.commands.geoprocessing;

import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.deegree.crs.components.Unit;
import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.commands.CommandHelper;
import org.deegree.igeo.config.EnvelopeType;
import org.deegree.igeo.config.MemoryDatasourceType;
import org.deegree.igeo.config.LayerType.MetadataURL;
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MemoryDatasource;
import org.deegree.igeo.modules.DefaultMapModule.SelectedFeaturesVisitor;
import org.deegree.igeo.views.swing.geoprocessing.BufferModel;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.Command;
import org.deegree.model.Identifier;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Surface;

/**
 * {@link Command} implementation for creating buffers around geometries of a layer. The result will be added as new
 * layer.
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class BufferCommand extends AbstractCommand {

    public static enum BUFFERTYPE {
        inside, outside, both, inside_filled, outside_filled
    };

    public static final QualifiedName name = new QualifiedName( "Create Buffer" );

    private static final ILogger LOG = LoggerFactory.getLogger( BufferCommand.class );

    private Layer layer;

    private int segments;

    private BUFFERTYPE bufferType;

    private int capStyle;

    private double[] distances;

    private boolean mergeIntersectingBuffer = false;

    private String newLayerName;

    private QualifiedName geomProperty;

    private QualifiedName propertyForBufferDistance;

    private Layer newLayer;

    private boolean performed;

    private boolean overlayedBuffers;

    private Unit bufferUnit;

    /**
     * 
     * @param layer
     * @param bufferModel
     */
    public BufferCommand( Layer layer, BufferModel bufferModel ) {
        this.layer = layer;
        this.distances = bufferModel.getDistances();
        this.segments = bufferModel.getSegments();
        this.capStyle = bufferModel.getCapStyle();
        this.newLayerName = bufferModel.getNewLayerName();
        this.geomProperty = bufferModel.getGeometryProperty();
        this.bufferType = bufferModel.getBufferType();
        this.overlayedBuffers = bufferModel.isOverlayedBuffers();
        this.mergeIntersectingBuffer = bufferModel.shallMerge();
        this.propertyForBufferDistance = bufferModel.getPropertyForBufferDistance();
        this.bufferUnit = bufferModel.getBufferUnit();
        if ( bufferType == BUFFERTYPE.inside || bufferType == BUFFERTYPE.inside_filled ) {
            for ( int i = 0; i < distances.length; i++ ) {
                this.distances[i] = -1 * this.distances[i];
            }
        }
    }

    public void execute()
                            throws Exception {
        performed = false;
        MapModel mm = layer.getOwner();
        SelectedFeaturesVisitor visitor = new SelectedFeaturesVisitor( -1 );
        try {
            mm.walkLayerTree( visitor );
            FeatureCollection tmp = visitor.col;
            FeatureType ft = null;
            List<Layer> layers = mm.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
            if ( layers.size() == 0 ) {
                throw new Exception( "one layer must be selected" );
            }
            List<DataAccessAdapter> dada = layers.get( 0 ).getDataAccess();
            for ( DataAccessAdapter dataAccessAdapter : dada ) {
                if ( dataAccessAdapter instanceof FeatureAdapter ) {
                    if ( tmp.size() == 0 ) {
                        tmp.addAllUncontained( ( (FeatureAdapter) dataAccessAdapter ).getFeatureCollection() );
                    }
                    ft = ( (FeatureAdapter) dataAccessAdapter ).getSchema();
                }
            }

            if ( tmp.size() > 0 ) {
                if ( processMonitor != null ) {
                    processMonitor.setMaximumValue( visitor.col.size() );
                }
                FeatureCollection fc = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(),
                                                                               visitor.col.size() );
                Iterator<Feature> iterator = visitor.col.iterator();
                if ( geomProperty == null ) {
                    geomProperty = CommandHelper.findGeomProperty( visitor.col.getFeature( 0 ) );
                }
                int cnt = 0;

                FeatureType bufferedFeatureType = createBufferedFeatureType( ft );
                while ( iterator.hasNext() ) {
                    if ( processMonitor.isCanceled() ) {
                        System.gc();
                        return;
                    }
                    processMonitor.updateStatus( cnt++, "" );
                    Feature feature = (Feature) iterator.next();
                    if ( this.propertyForBufferDistance != null ) {
                        fillDistancesFromProperty( feature );
                    }
                    Geometry origGeom = feature.getDefaultGeometryPropertyValue();
                    Geometry lastGeom = null;
                    if ( overlayedBuffers ) {
                        for ( int i = distances.length - 1; i >= 0; i-- ) {
                            bufferGeometry( fc, bufferedFeatureType, feature, origGeom, lastGeom, distances[i] );
                        }
                    } else {
                        for ( int i = 0; i < distances.length; i++ ) {
                            lastGeom = bufferGeometry( fc, bufferedFeatureType, feature, origGeom, lastGeom,
                                                       distances[i] );
                        }
                    }
                }
                if ( mergeIntersectingBuffer ) {
                    fc = mergeIntersectingBuffers( fc );
                }

                Envelope env = fc.getBoundedBy();
                MemoryDatasourceType mdst = new MemoryDatasourceType();
                EnvelopeType et = new EnvelopeType();
                et.setMinx( env.getMin().getX() );
                et.setMiny( env.getMin().getY() );
                et.setMaxx( env.getMax().getX() );
                et.setMaxy( env.getMax().getY() );
                et.setCrs( mm.getEnvelope().getCoordinateSystem().getPrefixedName() );
                mdst.setExtent( et );
                mdst.setMinScaleDenominator( 0d );
                mdst.setMaxScaleDenominator( 100000000d );
                Datasource ds = new MemoryDatasource( mdst, null, null, fc );

                Identifier id = new Identifier( newLayerName );
                int i = 0;
                while ( mm.exists( id ) ) {
                    id = new Identifier( newLayerName + "_" + i++ );
                }
                newLayer = new Layer( mm, id, id.getValue(), newLayerName, singletonList( ds ),
                                      Collections.<MetadataURL> emptyList() );
                newLayer.setEditable( true );
                mm.insert( newLayer, layer.getParent(), layer, false );
            }
        } catch ( Exception e ) {
            LOG.logError( "Unknown error", e );
            throw e;
        } finally {
            if ( processMonitor != null ) {
                processMonitor.cancel();
                processMonitor = null;
            }
        }
        performed = true;
    }

    private void fillDistancesFromProperty( Feature feature ) {
        FeatureProperty fp = feature.getDefaultProperty( this.propertyForBufferDistance );
        if ( fp != null ) {
            double d = ( (Number) fp.getValue() ).doubleValue();
            for ( int i = 0; i < distances.length; i++ ) {
                this.distances[i] = d + ( i + 1 );
            }
            if ( bufferType == BUFFERTYPE.inside || bufferType == BUFFERTYPE.inside_filled ) {
                for ( int i = 0; i < distances.length; i++ ) {
                    this.distances[i] = -1 * this.distances[i];
                }
            }
        }
    }

    private Geometry bufferGeometry( FeatureCollection fc, FeatureType bufferedFeatureType, Feature feature,
                                     Geometry origGeom, Geometry lastGeom, double distance )
                            throws CloneNotSupportedException {
        if ( "cm".equals( bufferUnit.getSymbol() ) ) {
            distance /= 100d;
        } else if ( "km".equals( bufferUnit.getSymbol() ) ) {
            distance *= 1000d;
        }
        if ( distance > 0 || ( distance < 0 && origGeom instanceof Surface )
             || ( distance < 0 && origGeom instanceof MultiSurface ) ) {
            Geometry geom = null;
            switch ( bufferType ) {
            case inside:
                geom = origGeom.getBuffer( distance, segments, capStyle );
                geom = origGeom.difference( geom );
                break;
            case outside:
                if ( origGeom instanceof Surface || origGeom instanceof MultiSurface ) {
                    geom = origGeom.getBuffer( distance, segments, capStyle );
                    geom = geom.difference( origGeom );
                } else {
                    return lastGeom;
                }
                break;
            case both:
                if ( origGeom instanceof Surface || origGeom instanceof MultiSurface ) {
                    geom = origGeom.getBuffer( distance, segments, capStyle );
                    Geometry geom2 = origGeom.getBuffer( -1 * distance, segments, capStyle );
                    geom = geom.difference( geom2 );
                } else {
                    return lastGeom;
                }
                break;
            default:
                geom = origGeom.getBuffer( distance, segments, capStyle );
                break;
            }
            // buffer calculation requires a lot of memory; ensure that it will be disposed
            // after calculation of each buffer
            System.gc();
            // clone feature to ensure that the new layer contains its own instances
            Feature nfeature = createBufferedFeature( feature, bufferedFeatureType, distance );
            FeatureProperty fp = nfeature.getDefaultProperty( geomProperty );
            if ( lastGeom != null && !overlayedBuffers ) {
                Geometry tmpG = geom;
                geom = geom.difference( lastGeom );
                lastGeom = tmpG;
            } else {
                lastGeom = geom;
            }
            fp.setValue( geom );
            fc.add( nfeature );
        }
        return lastGeom;
    }

    /**
     * 
     * @param ft
     * @return feature type for buffered features
     */
    private FeatureType createBufferedFeatureType( FeatureType ft ) {
        PropertyType[] pt = ft.getProperties();
        List<PropertyType> list = new ArrayList<PropertyType>( pt.length + 1 );
        for ( PropertyType propertyType : pt ) {
            list.add( propertyType );
        }
        PropertyType npt = FeatureFactory.createSimplePropertyType( new QualifiedName( "buffer_distance",
                                                                                       ft.getNameSpace() ),
                                                                    Types.FLOAT, 0, 1 );
        list.add( npt );
        pt = list.toArray( new PropertyType[list.size()] );
        QualifiedName name = new QualifiedName( "buffered_" + ft.getName().getLocalName(), ft.getNameSpace() );
        return FeatureFactory.createFeatureType( name, false, pt );
    }

    /**
     * 
     * @param feature
     * @param bufferedFeatureType
     * @param distance
     * @return new feature
     * @throws CloneNotSupportedException
     */
    private Feature createBufferedFeature( Feature feature, FeatureType bufferedFeatureType, double distance )
                            throws CloneNotSupportedException {

        FeatureProperty[] properties = feature.getProperties();
        FeatureProperty[] fp = new FeatureProperty[properties.length + 1];
        for ( int i = 0; i < fp.length - 1; i++ ) {
            if ( properties[i].getValue() instanceof FeatureCollection ) {
                Object v = ( (FeatureCollection) properties[i].getValue() ).clone();
                fp[i] = FeatureFactory.createFeatureProperty( properties[i].getName(), v );
            } else if ( properties[i].getValue() instanceof Feature ) {
                Object v = ( (Feature) properties[i].getValue() ).clone();
                fp[i] = FeatureFactory.createFeatureProperty( properties[i].getName(), v );
            } else {
                fp[i] = FeatureFactory.createFeatureProperty( properties[i].getName(), properties[i].getValue() );
            }
        }
        QualifiedName name = new QualifiedName( "buffer_distance", bufferedFeatureType.getNameSpace() );
        fp[fp.length - 1] = FeatureFactory.createFeatureProperty( name, distance );
        return FeatureFactory.createFeature( "UUID_" + UUID.randomUUID().toString(), bufferedFeatureType, fp );
    }

    /**
     * @param fc
     * @return
     */
    private FeatureCollection mergeIntersectingBuffers( FeatureCollection fc ) {

        List<Integer> tmp1 = new ArrayList<Integer>( fc.size() );
        Feature[] features = fc.toArray();
        boolean merged = false;
        do {
            merged = false;
            for ( int i = 0; i < features.length; i++ ) {
                for ( int j = i + 1; j < features.length; j++ ) {
                    if ( !tmp1.contains( j ) ) {
                        Geometry g1 = features[i].getDefaultGeometryPropertyValue();
                        Geometry g2 = features[j].getDefaultGeometryPropertyValue();
                        if ( g1.intersects( g2 ) ) {
                            g1 = g1.union( g2 );
                            FeatureProperty fp = features[i].getDefaultProperty( geomProperty );
                            fp.setValue( g1 );
                            tmp1.add( j );
                            merged = true;
                        }

                    }
                }
            }
        } while ( merged );

        FeatureCollection mergedFc = FeatureFactory.createFeatureCollection( "UUID_" + UUID.randomUUID().toString(),
                                                                             features.length );

        for ( int i = 0; i < features.length; i++ ) {
            if ( !tmp1.contains( i ) ) {
                mergedFc.add( features[i] );
            }
        }
        return mergedFc;
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
        return newLayer;
    }

    @Override
    public boolean isUndoSupported() {
        return true;
    }

    @Override
    public void undo()
                            throws Exception {
        if ( performed ) {
            newLayer.getOwner().remove( newLayer );
            performed = false;
        }
    }

}
