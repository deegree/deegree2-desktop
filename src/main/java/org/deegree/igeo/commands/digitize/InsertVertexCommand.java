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

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.deegree.datatypes.QualifiedName;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.CommandHelper;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.kernel.Command;
import org.deegree.kernel.CommandException;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.feature.Feature;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiPoint;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Ring;
import org.deegree.model.spatialschema.Surface;
import org.deegree.model.spatialschema.SurfaceInterpolationImpl;

/**
 * {@link Command} implementation for inserting a new vertex into a {@link Surface} or a {@link Curve}
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class InsertVertexCommand extends MoveVertexCommand {

    private QualifiedName name = new QualifiedName( "Insert Vertex" );

    private double minDistance = Double.MAX_VALUE;

    private boolean inserted = false;

    /**
     * 
     * @param appCont
     * @param feature
     * @param geomProperty
     * @param sourcePoint
     */
    public InsertVertexCommand( ApplicationContainer<?> appCont, Feature feature, QualifiedName geomProperty,
                                Point sourcePoint ) {
        super( appCont, feature, geomProperty, sourcePoint, null );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {
        Iterator<Feature> iter = featureCollection.iterator();
        while ( iter.hasNext() ) {
            Feature feature = iter.next();
            if ( geomProperty == null ) {
                geomProperty = CommandHelper.findGeomProperty( feature );
            }
            Geometry geom = (Geometry) feature.getProperties( geomProperty )[0].getValue();

            if ( geom instanceof Point ) {
                throw new CommandException( Messages.getMessage( Locale.getDefault(), "$MD10330" ) );
            } else if ( geom instanceof Curve ) {
                geom = handleCurve( (Curve) geom );
            } else if ( geom instanceof Surface ) {
                geom = handleSurface( (Surface) geom );
            } else if ( geom instanceof MultiPoint ) {
                throw new CommandException( Messages.getMessage( Locale.getDefault(), "$MD10331" ) );
            } else if ( geom instanceof MultiCurve ) {
                geom = handleMultiCurve( (MultiCurve) geom );
            } else if ( geom instanceof MultiSurface ) {
                geom = handleMultiSurface( (MultiSurface) geom );
            }
            setGeometryProperty( feature, geom );
            Layer layer = appCont.getMapModel( null ).getLayersSelectedForAction( MapModel.SELECTION_EDITING ).get( 0 );
            FeatureAdapter fa =  (FeatureAdapter) layer.getDataAccess().get( 0 );
            fa.updateFeature( feature );
        }
        performed = true;
        fireCommandProcessedEvent();
    }

    private Geometry handleCurve( Curve curve )
                            throws GeometryException {
        Position[] positions = curve.getAsLineString().getPositions();
        double distance = Double.MAX_VALUE;
        int index = -1;
        for ( int i = 0; i < positions.length - 1; i++ ) {
            double tmp = Line2D.ptSegDist( positions[i].getX(), positions[i].getY(), positions[i + 1].getX(),
                                           positions[i + 1].getY(), sourcePoint.getX(), sourcePoint.getY() );
            if ( tmp < distance && tmp < minDistance ) {
                distance = tmp;
                index = i;
            }
        }
        if ( index > -1 ) {
            curve = insertVertexintoCurve( curve.getCoordinateSystem(), curve, index );
        }
        return curve;
    }

    private Geometry handleMultiCurve( MultiCurve mcurve )
                            throws GeometryException {

        Curve[] curves = mcurve.getAllCurves();
        int curveIndex = -1;
        double distance = Double.MAX_VALUE;
        int index = -1;
        for ( int j = 0; j < curves.length; j++ ) {
            Position[] positions = curves[j].getAsLineString().getPositions();
            for ( int i = 0; i < positions.length - 1; i++ ) {
                double tmp = Line2D.ptSegDist( positions[i].getX(), positions[i].getY(), positions[i + 1].getX(),
                                               positions[i + 1].getY(), sourcePoint.getX(), sourcePoint.getY() );
                if ( tmp < distance && tmp < minDistance ) {
                    distance = tmp;
                    index = i;
                    curveIndex = j;
                }
            }
        }

        if ( index > -1 ) {
            curves[curveIndex] = insertVertexintoCurve( mcurve.getCoordinateSystem(), curves[curveIndex], index );
            mcurve = GeometryFactory.createMultiCurve( curves );
        }

        return mcurve;
    }

    private Curve insertVertexintoCurve( CoordinateSystem crs, Curve curve, int index )
                            throws GeometryException {
        Position[] positions = curve.getAsLineString().getPositions();
        List<Position> list = new ArrayList<Position>( positions.length );
        for ( int i = 0; i < positions.length; i++ ) {
            list.add( positions[i] );
            if ( i == index ) {
                // add new vertex
                list.add( sourcePoint.getPosition() );
            }
        }
        inserted = true;
        positions = list.toArray( new Position[list.size()] );
        return GeometryFactory.createCurve( positions, crs );
    }

    private Geometry handleSurface( Surface surface )
                            throws GeometryException {

        List<Position[]> rings = new ArrayList<Position[]>();
        rings.add( surface.getSurfaceBoundary().getExteriorRing().getPositions() );
        Ring[] inner = surface.getSurfaceBoundary().getInteriorRings();
        for ( Ring ring : inner ) {
            rings.add( ring.getPositions() );
        }
        int ringIndex = -1;
        double distance = Double.MAX_VALUE;
        int index = -1;
        for ( int j = 0; j < rings.size(); j++ ) {
            Position[] positions = rings.get( j );
            for ( int i = 0; i < positions.length - 1; i++ ) {
                double tmp = Line2D.ptSegDist( positions[i].getX(), positions[i].getY(), positions[i + 1].getX(),
                                               positions[i + 1].getY(), sourcePoint.getX(), sourcePoint.getY() );
                if ( tmp < distance && tmp < minDistance ) {
                    distance = tmp;
                    index = i;
                    ringIndex = j;
                }
            }
        }
        if ( index > -1 ) {
            surface = insertVertexIntoSurface( surface, rings, ringIndex, index );
        }

        return surface;
    }

    private Surface insertVertexIntoSurface( Surface surface, List<Position[]> rings, int ringIndex, int index )
                            throws GeometryException {

        Position[] positions = rings.get( ringIndex );
        List<Position> list = new ArrayList<Position>( positions.length );
        for ( int i = 0; i < positions.length; i++ ) {
            list.add( positions[i] );
            if ( i == index ) {
                // add new vertex
                list.add( sourcePoint.getPosition() );
            }
        }
        positions = list.toArray( new Position[list.size()] );
        rings.set( ringIndex, positions );
        Position[] exteriorRing = rings.remove( 0 );
        Position[][] interiorRings = rings.toArray( new Position[rings.size()][] );
        inserted = true;
        return GeometryFactory.createSurface( exteriorRing, interiorRings, new SurfaceInterpolationImpl(),
                                              surface.getCoordinateSystem() );
    }

    private Geometry handleMultiSurface( MultiSurface msurface )
                            throws GeometryException {

        int surfaceIndex = -1;
        int ringIndex = -1;
        int index = -1;
        double distance = Double.MAX_VALUE;

        Surface[] surfaces = msurface.getAllSurfaces();
        for ( int k = 0; k < surfaces.length; k++ ) {
            List<Position[]> rings = new ArrayList<Position[]>();
            rings.add( surfaces[k].getSurfaceBoundary().getExteriorRing().getPositions() );
            Ring[] inner = surfaces[k].getSurfaceBoundary().getInteriorRings();
            for ( Ring ring : inner ) {
                rings.add( ring.getPositions() );
            }
            for ( int j = 0; j < rings.size(); j++ ) {
                Position[] positions = rings.get( j );
                for ( int i = 0; i < positions.length - 1; i++ ) {
                    double tmp = Line2D.ptSegDist( positions[i].getX(), positions[i].getY(), positions[i + 1].getX(),
                                                   positions[i + 1].getY(), sourcePoint.getX(), sourcePoint.getY() );
                    if ( tmp < distance && tmp < minDistance ) {
                        distance = tmp;
                        index = i;
                        ringIndex = j;
                        surfaceIndex = k;
                    }
                }
            }
        }
        if ( index > -1 ) {
            List<Position[]> rings = new ArrayList<Position[]>();
            rings.add( surfaces[surfaceIndex].getSurfaceBoundary().getExteriorRing().getPositions() );
            Ring[] inner = surfaces[surfaceIndex].getSurfaceBoundary().getInteriorRings();
            for ( Ring ring : inner ) {
                rings.add( ring.getPositions() );
            }
            surfaces[surfaceIndex] = insertVertexIntoSurface( surfaces[surfaceIndex], rings, ringIndex, index );
            msurface = GeometryFactory.createMultiSurface( surfaces );
        }

        return msurface;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.commands.MoveVertexCommand#getName()
     */
    @Override
    public QualifiedName getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.commands.MoveVertexCommand#undo()
     */
    @Override
    public void undo()
                            throws Exception {
        if ( performed && inserted ) {
            DeleteVertexCommand dvc = new DeleteVertexCommand( appCont, this.featureCollection, geomProperty,
                                                               sourcePoint );
            dvc.execute();
            performed = false;
        }
    }

}
