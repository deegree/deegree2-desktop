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

package org.deegree.igeo.commands.digitize;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.deegree.datatypes.QualifiedName;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.CommandHelper;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.kernel.AbstractCommand;
import org.deegree.model.feature.DefaultFeature;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.JTSAdapter;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Ring;
import org.deegree.model.spatialschema.Surface;
import org.deegree.model.spatialschema.SurfaceInterpolationImpl;

import com.vividsolutions.jts.algorithm.CGAlgorithms;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class CuttingPolygonHoleCommand extends AbstractCommand {

    private QualifiedName name = new QualifiedName( "Cutting Polygon Hole" );

    private boolean performed = false;

    private FeatureCollection featureCollection;

    private FeatureCollection oldFeatureCollection;

    private Feature root;

    private QualifiedName geomProperty;

    private ApplicationContainer<?> appCont;

    /**
     * 
     * @param appCont
     */
    public void setApplicationContainer( ApplicationContainer<?> appCont ) {
        this.appCont = appCont;
    }

    /**
     * 
     * @param geomProperty
     */
    public void setGeometryProperty( QualifiedName geomProperty ) {
        this.geomProperty = geomProperty;
    }

    /**
     * 
     * @param featureCollection
     */
    public void setFeatureCollection( FeatureCollection featureCollection ) {
        this.featureCollection = featureCollection;
        // create a deep copy for undoing
        Feature[] features = this.featureCollection.toArray();
        for ( int i = 0; i < features.length; i++ ) {
            try {
                features[i] = ( (DefaultFeature) features[i] ).cloneDeep();
            } catch ( CloneNotSupportedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        this.oldFeatureCollection = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(), features );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {
        if ( featureCollection != null && featureCollection.size() > 1 ) {

            if ( geomProperty == null ) {
                geomProperty = CommandHelper.findGeomProperty( featureCollection.getFeature( 0 ) );
            }
            Feature[] features = featureCollection.toArray();
            // find root surface
            root = features[0];
            Geometry rootGeom = (Geometry) root.getProperties( geomProperty )[0].getValue();
            int idx = 0;
            for ( int i = 1; i < features.length; i++ ) {
                Geometry otherGeom = (Geometry) features[i].getProperties( geomProperty )[0].getValue();
                if ( otherGeom.contains( rootGeom ) ) {
                    root = features[i];
                    rootGeom = otherGeom;
                    idx = i;
                } else if ( !rootGeom.contains( otherGeom ) ) {
                    throw new Exception( Messages.getMessage( appCont.getLocale(), "$MD11608" ) );
                }
            }
            // check again
            for ( int i = 0; i < features.length; i++ ) {
                if ( i != idx ) {
                    Geometry otherGeom = (Geometry) features[i].getProperties( geomProperty )[0].getValue();
                    if ( otherGeom.contains( rootGeom ) ) {
                        throw new Exception( Messages.getMessage( appCont.getLocale(), "$MD11608" ) );
                    }
                }
            }

            // cut hole(s)
            Geometry resultGeom = null;
            if ( rootGeom instanceof Surface ) {
                resultGeom = handleSurface( features, idx, (Surface) rootGeom );
            } else {
                Surface[] surfaces = ( (MultiSurface) rootGeom ).getAllSurfaces();
                for ( int k = 0; k < surfaces.length; k++ ) {
                    for ( int i = 0; i < features.length; i++ ) {
                        if ( i != idx ) {
                            Geometry geom = (Geometry) features[i].getProperties( geomProperty )[0].getValue();
                            if ( surfaces[k].contains( geom ) ) {
                                surfaces[k] = handleSurface( new Feature[] { features[i] }, idx, surfaces[k] );
                            }
                        }
                    }
                }
                resultGeom = GeometryFactory.createMultiSurface( surfaces, rootGeom.getCoordinateSystem() );
            }

            // update root feature and delete all other features (features that are now holes)
            setGeometryProperty( root, resultGeom );
//            Layer layer = appCont.getMapModel( null ).getLayersSelectedForAction( MapModel.SELECTION_EDITING ).get( 0 );
//            FeatureAdapter fa = (FeatureAdapter) layer.getDataAccess().get( 0 );
//            fa.updateFeature( root );
//            for ( int i = 0; i < features.length; i++ ) {
//                if ( i != idx ) {
//                    fa.deleteFeature( features[i] );
//                }
//            }
        }
        performed = true;
        fireCommandProcessedEvent();
    }

    private Surface handleSurface( Feature[] features, int idx, Surface rootSurface )
                            throws Exception, GeometryException {
        Position[] exterior = rootSurface.getSurfaceBoundary().getExteriorRing().getPositions();
        // collect old inner rings
        Ring[] rings = rootSurface.getSurfaceBoundary().getInteriorRings();
        List<Position[]> interior = new ArrayList<Position[]>();
        for ( Ring ring : rings ) {
            interior.add( ring.getPositions() );
        }
        // add exterior rings of cutting surfaces as inner rings
        for ( int i = 0; i < features.length; i++ ) {
            if ( i != idx ) {
                Geometry geom = (Geometry) features[i].getProperties( geomProperty )[0].getValue();
                if ( geom instanceof Surface ) {
                    Surface surface = (Surface) geom;
                    Position[] tmp = surface.getSurfaceBoundary().getExteriorRing().getPositions();
                    if ( isClockwise( tmp ) ) {
                        invertPositionOrder( tmp );
                    }
                    interior.add( tmp );
                } else if ( geom instanceof MultiSurface ) {
                    Surface[] surfaces = ( (MultiSurface) geom ).getAllSurfaces();
                    for ( Surface surface : surfaces ) {
                        Position[] tmp = surface.getSurfaceBoundary().getExteriorRing().getPositions();
                        if ( isClockwise( tmp ) ) {
                            invertPositionOrder( tmp );
                        }
                        interior.add( tmp );
                    }
                } else {
                    throw new Exception( Messages.getMessage( appCont.getLocale(), "$MD11609" ) );
                }
            }
        }
        Position[][] innerRings = (Position[][]) interior.toArray( new Position[interior.size()][] );
        return GeometryFactory.createSurface( exterior, innerRings, new SurfaceInterpolationImpl(),
                                              rootSurface.getCoordinateSystem() );
    }

    /**
     * 
     * @param feature
     * @param geom
     */
    protected void setGeometryProperty( Feature feature, Geometry geom ) {
        FeatureProperty fp = feature.getProperties( geomProperty )[0];
        fp.setValue( geom );
        feature.setProperty( fp, 0 );
    }

    /**
     * 
     * @param ring
     * @return true if an array of passed {@link Position} forms a clockwise orientated ring
     */
    private boolean isClockwise( Position[] ring ) {
        return !CGAlgorithms.isCCW( JTSAdapter.export( ring ).getCoordinates() );
    }

    /**
     * inverts the order of the positions of a ring
     * 
     * @param inner
     */
    private void invertPositionOrder( Position[] inner ) {
        for ( int i = 0; i < inner.length / 2; i++ ) {
            Position tmp = inner[i];
            inner[i] = inner[inner.length - 1 - i];
            inner[inner.length - 1 - i] = tmp;
        }
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
        return root;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#isUndoSupported()
     */
    public boolean isUndoSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#undo()
     */
    public void undo()
                            throws Exception {
        if ( performed ) {
            Layer layer = appCont.getMapModel( null ).getLayersSelectedForAction( MapModel.SELECTION_EDITING ).get( 0 );
            FeatureAdapter fa = (FeatureAdapter) layer.getDataAccess().get( 0 );
            fa.deleteFeature( root );
            Iterator<Feature> iter = oldFeatureCollection.iterator();
            while ( iter.hasNext() ) {
                Feature feature = (Feature) iter.next();
                fa.insertFeature( feature );
            }
            performed = false;
        }
    }

}
