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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.commands.CommandHelper;
import org.deegree.desktop.dataadapter.FeatureAdapter;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.kernel.Command;
import org.deegree.kernel.CommandException;
import org.deegree.model.feature.Feature;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Envelope;
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
import org.deegree.model.spatialschema.SurfaceBoundary;
import org.deegree.model.spatialschema.SurfaceInterpolationImpl;

/**
 * {@link Command} implementation for merging two or more vertices of a {@link Surface} or a {@link Curve} into one
 * vertex
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class MergeVerticesCommand extends MoveVertexCommand {

    private Envelope mergeArea;

    /**
     * 
     * @param appCont
     * @param feature
     * @param geomProperty
     * @param targetPoint
     * @param mergeArea
     */
    public MergeVerticesCommand( ApplicationContainer<?> appCont, Feature feature, QualifiedName geomProperty,
                                 Point targetPoint, Envelope mergeArea ) {
        super( appCont, feature, geomProperty, targetPoint, targetPoint );
        this.mergeArea = mergeArea;
        name = new QualifiedName( "Merge Vertices" );
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
            old.put( feature, geom );

            boolean nearest = verticesOpt.useNearest();

            if ( geom instanceof Point ) {
                throw new CommandException( Messages.getMessage( Locale.getDefault(), "$MD10319" ) );
            } else if ( geom instanceof Curve ) {
                geom = handleCurve( (Curve) geom, nearest );
            } else if ( geom instanceof Surface ) {
                geom = handleSurface( (Surface) geom, nearest );
            } else if ( geom instanceof MultiPoint ) {
                throw new CommandException( Messages.getMessage( Locale.getDefault(), "$MD10321" ) );
            } else if ( geom instanceof MultiCurve ) {
                geom = handleMultiCurve( (MultiCurve) geom, nearest );
            } else if ( geom instanceof MultiSurface ) {
                geom = handleMultiSurface( (MultiSurface) geom, nearest );
            }
            setGeometryProperty( feature, geom );
            Layer layer = appCont.getMapModel( null ).getLayersSelectedForAction( MapModel.SELECTION_EDITING ).get( 0 );
            FeatureAdapter fa = (FeatureAdapter) layer.getDataAccess().get( 0 );
            fa.updateFeature( feature );
        }
        performed = true;
        fireCommandProcessedEvent();
    }

    private Position[] filterPositions( Position[] positions, int minPos, Position... target ) {
        List<Position> posList = new ArrayList<Position>( positions.length );
        int cnt = 0;
        for ( int i = 0; i < positions.length; i++ ) {
            if ( mergeArea.contains( positions[i] ) ) {
                if ( cnt < target.length ) {
                    // if the first position contained within merge envelope is found set
                    // the target point/position instead. After this all other points contained
                    // within merge envelope will be ignored
                    posList.add( target[0] );
                    cnt++;
                }
            } else {
                posList.add( positions[i] );
            }
        }
        if ( posList.size() < minPos ) {
            throw new CommandException( Messages.getMessage( Locale.getDefault(), "$MD10315", minPos ) );
        }
        return posList.toArray( new Position[posList.size()] );
    }

    /**
     * 
     * @param feature
     * @param curve
     * @param nearest
     * @return
     * @throws GeometryException
     */
    private Geometry handleCurve( Curve curve, boolean nearest )
                            throws GeometryException {
        Position[] positions = curve.getAsLineString().getPositions();
        if ( nearest ) {
            // just remove position nearest to click point
            Position position = findNearest( positions );
            positions = filterPositions( positions, 2, position );
        } else {
            positions = filterPositions( positions, 2, targetPoint.getPosition() );
        }
        return GeometryFactory.createCurve( positions, curve.getCoordinateSystem() );

    }

    /**
     * 
     * @param geom
     * @param nearest
     * @return
     * @throws GeometryException
     */
    private Geometry handleMultiCurve( MultiCurve geom, boolean nearest )
                            throws GeometryException {
        Curve[] curves = geom.getAllCurves();
        List<Curve> curveList = new ArrayList<Curve>( curves.length );
        if ( nearest ) {
            // just remove position nearest nearest to clickpoint
            Position[][] positions = new Position[curves.length][];
            for ( int i = 0; i < curves.length; i++ ) {
                positions[i] = curves[i].getAsLineString().getPositions();
            }
            Position[] position = findNearest( positions );
            for ( int i = 0; i < positions.length; i++ ) {
                positions[i] = filterPositions( positions[i], 0, position );
                if ( positions[i].length > 1 ) {
                    curveList.add( GeometryFactory.createCurve( positions[i], geom.getCoordinateSystem() ) );
                }
            }
        } else {
            for ( Curve curve : curves ) {
                Position[] positions = curve.getAsLineString().getPositions();
                positions = filterPositions( positions, 0, targetPoint.getPosition() );
                if ( positions.length > 1 ) {
                    curveList.add( GeometryFactory.createCurve( positions, geom.getCoordinateSystem() ) );
                }
            }
        }
        if ( curveList.size() == 0 ) {
            throw new CommandException( Messages.getMessage( Locale.getDefault(), "$MD10316" ) );
        }
        return GeometryFactory.createMultiCurve( curves );
    }

    /**
     * 
     * @param geom
     * @param nearest
     * @return
     * @throws GeometryException
     */
    private Geometry handleMultiSurface( MultiSurface geom, boolean nearest )
                            throws GeometryException {
        Surface[] surfaces = geom.getAllSurfaces();
        List<Surface> surfaceList = new ArrayList<Surface>( surfaces.length );
        if ( nearest ) {
            // just move position nearest to click point

            // find nearest position
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

            // delete nearest position
            for ( Surface surface : surfaces ) {
                SurfaceBoundary sb = surface.getSurfaceBoundary();
                Position[] ext = sb.getExteriorRing().getPositions();
                ext = validateRing( filterPositions( ext, 4, position ) );
                Ring[] inner = sb.getInteriorRings();
                List<Position[]> innerList = new ArrayList<Position[]>( inner.length );
                for ( Ring ring : inner ) {
                    Position[] in = ring.getPositions();
                    in = filterPositions( in, 0, position );
                    if ( in.length > 3 ) {
                        innerList.add( validateRing( in ) );
                    }
                }
                Position[][] innerPos = innerList.toArray( new Position[innerList.size()][] );
                surfaceList.add( GeometryFactory.createSurface( ext, innerPos, new SurfaceInterpolationImpl(),
                                                                surface.getCoordinateSystem() ) );
            }
        } else {
            for ( Surface surface : surfaces ) {
                surfaceList.add( (Surface) handleSurface( surface, false ) );
            }
        }
        if ( surfaceList.size() == 0 ) {
            throw new CommandException( Messages.getMessage( Locale.getDefault(), "$MD10318" ) );
        }
        return GeometryFactory.createMultiSurface( surfaces );
    }

    /**
     * 
     * @param surface
     * @param nearest
     * @return
     * @throws GeometryException
     */
    private Geometry handleSurface( Surface surface, boolean nearest )
                            throws GeometryException {
        SurfaceBoundary sb = surface.getSurfaceBoundary();
        Position[] ext = sb.getExteriorRing().getPositions();
        Ring[] inner = sb.getInteriorRings();
        List<Position[]> innerList = new ArrayList<Position[]>( inner.length );
        if ( nearest ) {
            // just add external ring for searching nearest position
            innerList.add( ext );
            for ( Ring ring : inner ) {
                Position[] in = ring.getPositions();
                innerList.add( in );
            }
            Position[][] tmp = innerList.toArray( new Position[innerList.size()][] );
            Position[] position = findNearest( tmp );
            ext = validateRing( filterPositions( tmp[0], 4, position ) );
            innerList.clear();
            for ( int i = 1; i < tmp.length; i++ ) {
                Position[] in = filterPositions( tmp[i], 0, position );
                if ( in.length > 3 ) {
                    innerList.add( validateRing( in ) );
                }
            }
        } else {
            ext = validateRing( filterPositions( ext, 4, targetPoint.getPosition() ) );
            for ( Ring ring : inner ) {
                Position[] in = ring.getPositions();
                in = filterPositions( in, 0, targetPoint.getPosition() );
                if ( in.length > 3 ) {
                    innerList.add( validateRing( in ) );
                }
            }
        }
        Position[][] innerPos = innerList.toArray( new Position[innerList.size()][] );
        return GeometryFactory.createSurface( ext, innerPos, new SurfaceInterpolationImpl(),
                                              surface.getCoordinateSystem() );
    }

}
