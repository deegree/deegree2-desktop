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

package org.deegree.igeo.commands.geoprocessing;

import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.deegree.datatypes.QualifiedName;
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
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.Command;
import org.deegree.model.Identifier;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.JTSAdapter;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Surface;

import com.vividsolutions.jts.algorithm.InteriorPointArea;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * {@link Command} implementation for creating a representativ point for each polygon contained in a layer. The result
 * will be added as new layer
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class PointForPolygonCommand extends AbstractCommand {

    public static final QualifiedName name = new QualifiedName( "Point for Polygon" );

    private static final ILogger LOG = LoggerFactory.getLogger( PointForPolygonCommand.class );

    private Layer layer;

    private String newLayerName;

    private QualifiedName geomProperty;

    private Layer newLayer;

    private boolean performed;

    /**
     * 
     * @param layer
     * @param newLayerName
     * @param geomProperty
     */
    public PointForPolygonCommand( Layer layer, String newLayerName, QualifiedName geomProperty ) {
        this.layer = layer;
        this.newLayerName = newLayerName;
        this.geomProperty = geomProperty;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {
        performed = false;
        MapModel mm = layer.getOwner();
        List<Feature> featureList = new ArrayList<Feature>( 5000 );
        List<DataAccessAdapter> dada = layer.getDataAccess();
        for ( DataAccessAdapter dataAccessAdapter : dada ) {
            if ( dataAccessAdapter instanceof FeatureAdapter ) {
                FeatureCollection fc = ( (FeatureAdapter) dataAccessAdapter ).getFeatureCollection();
                int cnt = fc.size();
                for ( int i = 0; i < cnt; i++ ) {
                    featureList.add( fc.getFeature( i ) );
                }
            }
        }
        try {
            if ( processMonitor != null ) {
                processMonitor.setMaximumValue( featureList.size() );
            }
            FeatureCollection fc = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(),
                                                                           featureList.size() );
            if ( geomProperty == null ) {
                geomProperty = CommandHelper.findGeomProperty( featureList.get( 0 ) );
            }
            int cnt = 0;
            GeometryFactory gf = new GeometryFactory();
            Iterator<Feature> iterator = featureList.iterator();
            while ( iterator.hasNext() ) {
                if ( processMonitor != null ) {
                    processMonitor.updateStatus( cnt++, "" );
                }
                Feature feature = (Feature) ( (Feature) iterator.next() ).clone();
                FeatureProperty fp = feature.getDefaultProperty( geomProperty );
                if ( fp.getValue() instanceof Surface || fp.getValue() instanceof MultiSurface ) {
                    Geometry geom = (Geometry) fp.getValue();
                    com.vividsolutions.jts.geom.Geometry g = JTSAdapter.export( geom );
                    InteriorPointArea ipa = new InteriorPointArea( g );
                    Point po = gf.createPoint( ipa.getInteriorPoint() );
                    org.deegree.model.spatialschema.Point p = (org.deegree.model.spatialschema.Point) JTSAdapter.wrap( po );
                    fp.setValue( p );
                    fc.add( feature );
                }
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

            newLayer = new Layer( mm, new Identifier( newLayerName ), newLayerName, newLayerName, singletonList( ds ),
                                  Collections.<MetadataURL> emptyList() );
            newLayer.setEditable( true );
            mm.insert( newLayer, layer.getParent(), layer, false );

        } catch ( Exception e ) {
            LOG.logError( "Unknown error", e );
            throw e;
        } finally {
            if ( processMonitor != null ) {
                processMonitor.cancel();
            }
        }
        performed = true;
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
