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

package org.deegree.desktop.commands.digitize;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.commands.CommandHelper;
import org.deegree.desktop.dataadapter.FeatureAdapter;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.settings.DigitizingVerticesOpt;
import org.deegree.framework.util.GeometryUtils;
import org.deegree.kernel.AbstractCommand;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.GeometryImpl;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiPoint;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Ring;
import org.deegree.model.spatialschema.Surface;
import org.deegree.model.spatialschema.SurfaceBoundary;

/**
 * Command class for moving a or a group of vertexes belonging to one or more features/geometries
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class MoveVertexCommand extends AbstractCommand {

    protected QualifiedName name = new QualifiedName( "Move Vertex" );

    protected boolean performed = false;

    protected FeatureCollection featureCollection;

    protected Point sourcePoint;

    protected Point targetPoint;

    protected QualifiedName geomProperty;

    protected Map<Feature, Geometry> old = new HashMap<Feature, Geometry>();

    protected DigitizingVerticesOpt verticesOpt;

    protected ApplicationContainer<?> appCont;

    /**
     * 
     * @param verticesOpt
     * @param feature
     * @param geomProperty
     * @param sourcePoint
     * @param targetPoint
     */
    public MoveVertexCommand( ApplicationContainer<?> appCont, Feature feature, QualifiedName geomProperty,
                              Point sourcePoint, Point targetPoint ) {
        if ( feature instanceof FeatureCollection ) {
            this.featureCollection = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(),
                                                                             ( (FeatureCollection) feature ).size() );
            this.featureCollection.addAllUncontained( (FeatureCollection) feature );
        } else {
            this.featureCollection = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(),
                                                                             new Feature[] { feature } );
        }
        this.appCont = appCont;
        this.verticesOpt = appCont.getSettings().getDigitizingVerticesOptions();
        this.sourcePoint = sourcePoint;
        this.targetPoint = targetPoint;
        this.geomProperty = geomProperty;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {
        double dx = targetPoint.getX() - sourcePoint.getX();
        double dy = targetPoint.getY() - sourcePoint.getY();
        double[] dxy = new double[] { dx, dy };
        Iterator<Feature> iter = featureCollection.iterator();
        while ( iter.hasNext() ) {
            Feature feature = iter.next();
            if ( geomProperty == null ) {
                geomProperty = CommandHelper.findGeomProperty( feature );
            }
            Geometry geom = (Geometry) feature.getProperties( geomProperty )[0].getValue();
            Geometry tmpGeom = (Geometry) ( (GeometryImpl) geom ).clone();
            old.put( feature, tmpGeom );

            boolean nearest = verticesOpt.handleNearest();
            if ( geom instanceof Point ) {
                handlePoint( dxy, geom );
            } else if ( geom instanceof Curve ) {
                handleCurve( dxy, (Curve) geom, nearest );
            } else if ( geom instanceof Surface ) {
                handleSurface( dxy, (Surface) geom, nearest );
            } else if ( geom instanceof MultiPoint ) {
                handleMultiPoint( dxy, (MultiPoint) geom, nearest );
            } else if ( geom instanceof MultiCurve ) {
                handleMultiCurve( dxy, (MultiCurve) geom, nearest );
            } else if ( geom instanceof MultiSurface ) {
                handleMultiSurface( dxy, (MultiSurface) geom, nearest );
            }
            Layer layer = appCont.getMapModel( null ).getLayersSelectedForAction( MapModel.SELECTION_EDITING ).get( 0 );
            FeatureAdapter fa = (FeatureAdapter) layer.getDataAccess().get( 0 );
            fa.updateFeature( feature );
        }
        performed = true;
        fireCommandProcessedEvent();
    }

    /**
     * 
     * @param allPoints
     * @return {@link Point} nearest to click point
     */
    protected Point findNearest( Point[] allPoints ) {
        double tolerance = sourcePoint.getTolerance();
        Point result = null;
        double dist = Double.MAX_VALUE;
        for ( Point point : allPoints ) {
            double tmp = GeometryUtils.distance( sourcePoint.getPosition(), point.getPosition() );
            if ( tmp < dist && tmp <= tolerance ) {
                dist = tmp;
                result = point;
            }
        }
        return result;
    }

    /**
     * 
     * @param positions
     * @return {@link Position} nearest to click point
     */
    protected Position findNearest( Position[] positions ) {
        double tolerance = sourcePoint.getTolerance();
        Position result = null;
        double dist = Double.MAX_VALUE;
        for ( Position position : positions ) {
            double tmp = GeometryUtils.distance( sourcePoint.getPosition(), position );
            if ( tmp < dist && tmp <= tolerance ) {
                dist = tmp;
                result = position;
            }
        }
        return result;
    }

    /**
     * 
     * @param positions
     * @return {@link Position} nearest to click point
     */
    protected Position[] findNearest( Position[][] positions ) {
        double tolerance = sourcePoint.getTolerance();
        Position result = null;
        double dist = Double.MAX_VALUE;
        int outIndex = 0;
        int index = 0;
        for ( int i = 0; i < positions.length; i++ ) {
            for ( int j = 0; j < positions[i].length; j++ ) {
                double tmp = GeometryUtils.distance( sourcePoint.getPosition(), positions[i][j] );
                if ( tmp < dist && tmp <= tolerance ) {
                    dist = tmp;
                    result = positions[i][j];
                    index = j;
                    outIndex = i;
                }
            }
        }
        if ( index == 0 || positions[outIndex].length == index + 1 ) {
            return new Position[] { result, positions[outIndex][positions[outIndex].length - 1] };
        }
        return new Position[] { result };
    }

    private void handleMultiPoint( double[] dxy, MultiPoint geom, boolean nearest ) {
        Point[] points = geom.getAllPoints();
        if ( nearest ) {
            // just move position nearest to click point
            Point point = findNearest( points );
            point.translate( dxy );
        } else {
            for ( Point point : points ) {
                point.translate( dxy );
            }
        }
    }

    private void handleMultiSurface( double[] dxy, MultiSurface geom, boolean nearest ) {
        Surface[] surfaces = geom.getAllSurfaces();
        if ( nearest ) {
            // just move position nearest to click point
            List<Position[]> list = new ArrayList<Position[]>();
            for ( Surface surface : surfaces ) {
                SurfaceBoundary sb = surface.getSurfaceBoundary();
                Ring[] inner = sb.getInteriorRings();
                list.add( sb.getExteriorRing().getPositions() );
                for ( int i = 0; i < inner.length; i++ ) {
                    list.add( inner[i].getPositions() );
                }
            }
            Position[][] positions = list.toArray( new Position[list.size()][] );
            Position[] position = findNearest( positions );
            for ( int i = 0; i < position.length; i++ ) {
                position[i].translate( dxy );
            }
        } else {
            for ( Surface surface : surfaces ) {
                handleSurface( dxy, surface, false );
            }
        }
    }

    private void handleMultiCurve( double[] dxy, MultiCurve geom, boolean nearest )
                            throws GeometryException {
        Curve[] curves = geom.getAllCurves();
        if ( nearest ) {
            // just move position nearest to click point
            Position[][] positions = new Position[curves.length][];
            for ( int i = 0; i < curves.length; i++ ) {
                positions[i] = curves[i].getAsLineString().getPositions();
            }
            Position[] position = findNearest( positions );
            position[0].translate( dxy );
        } else {
            for ( Curve curve : curves ) {
                handleCurve( dxy, curve, false );
            }
        }
    }

    private void handlePoint( double[] dxy, Geometry geom ) {
        ( (Point) geom ).translate( dxy );
    }

    /**
     * 
     * @param dxy
     * @param curve
     * @param nearest
     * @throws GeometryException
     */
    private void handleCurve( double[] dxy, Curve curve, boolean nearest )
                            throws GeometryException {
        Position[] positions = curve.getAsLineString().getPositions();
        if ( nearest ) {
            // just move position nearest to click point
            Position position = findNearest( positions );
            position.translate( dxy );
        } else {
            moveVertex( positions, dxy );
        }
    }

    /**
     * 
     * @param dxy
     * @param surface
     */
    private void handleSurface( double[] dxy, Surface surface, boolean nearest ) {
        SurfaceBoundary sb = surface.getSurfaceBoundary();
        if ( nearest ) {
            // just move position nearest to click point
            Ring[] inner = sb.getInteriorRings();
            Position[][] positions = new Position[inner.length + 1][];
            positions[0] = sb.getExteriorRing().getPositions();
            for ( int i = 1; i < positions.length; i++ ) {
                positions[i] = inner[i - 1].getPositions();
            }
            Position[] position = findNearest( positions );
            for ( int i = 0; i < position.length; i++ ) {
                position[i].translate( dxy );
            }
        } else {
            Position[] positions = sb.getExteriorRing().getPositions();
            moveVertex( positions, dxy );
            Ring[] inner = sb.getInteriorRings();
            for ( Ring ring : inner ) {
                positions = ring.getPositions();
                moveVertex( positions, dxy );
            }
        }
    }

    /**
     * 
     * @param positions
     * @param dxy
     */
    private void moveVertex( Position[] positions, double[] dxy ) {
        double tolerance = sourcePoint.getTolerance();
        for ( Position position : positions ) {
            if ( GeometryUtils.distance( position, sourcePoint.getPosition() ) <= tolerance ) {
                position.translate( dxy );
            }
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
        return featureCollection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#isUndoSupported()
     */
    public boolean isUndoSupported() {
        return true;
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
     * @return
     */
    protected Position[] validateRing( Position[] ring ) {
        if ( !ring[0].equals( ring[ring.length - 1] ) ) {
            Position[] tmp = new Position[ring.length];
            for ( int i = 0; i < ring.length; i++ ) {
                tmp[i] = ring[i];
            }
            // ensure that first an last point are
            tmp[tmp.length - 1] = GeometryFactory.createPosition( ring[0].getAsArray() );
            ring = tmp;
        }
        return ring;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#undo()
     */
    public void undo()
                            throws Exception {
        if ( performed ) {
            Iterator<Feature> iter = old.keySet().iterator();
            while ( iter.hasNext() ) {
                Feature feature = iter.next();
                setGeometryProperty( feature, old.get( feature ) );
            }
            performed = false;
        }

    }

}
