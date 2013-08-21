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
package org.deegree.desktop.commands.digitize;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.vecmath.Vector2d;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.commands.model.AddErrorLayerCommand;
import org.deegree.desktop.dataadapter.FeatureAdapter;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.mapmodel.MapModelEntry;
import org.deegree.desktop.mapmodel.MapModelException;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.Pair;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.Command;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.PositionImpl;
import org.deegree.model.spatialschema.Surface;
import org.deegree.model.spatialschema.SurfaceInterpolationImpl;

/**
 * {@link Command} implementation for creating polygon finding a closed graph that can be used as border of an empty
 * area (no geometry already there)
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class CreatePolygonFromBordersCommand extends AbstractCommand {

    private static final ILogger LOG = LoggerFactory.getLogger( CreatePolygonFromBordersCommand.class );

    private static final QualifiedName name = new QualifiedName( "Create Polygon from Borders" );

    private ApplicationContainer<?> appCont;

    private MapModel mapModel;

    private Layer layer;

    private Point clickPoint;

    /**
     * 
     * @param appCont
     * @param clickPoint
     */
    public CreatePolygonFromBordersCommand( ApplicationContainer<?> appCont, Point clickPoint ) {
        this.appCont = appCont;
        this.clickPoint = clickPoint;
        mapModel = appCont.getMapModel( null );
        List<MapModelEntry> list = mapModel.getMapModelEntriesSelectedForAction( MapModel.SELECTION_EDITING );
        if ( list.size() == 0 ) {
            throw new MapModelException( Messages.get( "$DG10105" ) );
        }
        layer = (Layer) list.get( 0 );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {

        FeatureAdapter adapter = (FeatureAdapter) layer.getDataAccess().get( 0 );
        List<Pair<Position, Position>> edges = getEdges( adapter );
        for ( Pair<Position, Position> pair : edges ) {
            // ensure all edges have same direction according to click point
            if ( ccw( pair.first, pair.second, clickPoint.getPosition() ) < 0 ) {
                Position p = pair.first;
                pair.first = pair.second;
                pair.second = p;
            }
        }

        // find nearest edge an remove it from edges list
        Pair<Position, Position> edge = findStartEgde( clickPoint, edges );

        // create a real copy
        List<Pair<Position, Position>> polygonEdges = new ArrayList<Pair<Position, Position>>( 1000 );
        polygonEdges.add( edge );
        do {
            edge = findNextEdge( edges, polygonEdges.get( polygonEdges.size() - 1 ) );
            if ( edge != null ) {
                polygonEdges.add( edge );
            }
        } while ( edge != null && !polygonEdges.get( 0 ).first.equals( edge.second ) );

        if ( edge == null ) {
            LOG.logError( "ring is not closed" );
            createErrorlayer( "ring is not closed", edges.get( 0 ).first, edges.get( edges.size() - 1 ).second );
            return;
        }
        // create position list to create a new surface
        // for this deep clone has to made to avoid having several geometries
        // sharing the same Position instances
        List<Position> positions = new ArrayList<Position>( polygonEdges.size() + 2 );

        positions.add( (Position) ( (PositionImpl) polygonEdges.get( 0 ).first ).clone() );
        positions.add( (Position) ( (PositionImpl) polygonEdges.get( 0 ).second ).clone() );
        for ( int i = 1; i < polygonEdges.size(); i++ ) {
            positions.add( (Position) ( (PositionImpl) polygonEdges.get( i ).second ).clone() );
        }

        Position[] tt = positions.toArray( new Position[positions.size()] );
        Surface surface = GeometryFactory.createSurface( tt, null, new SurfaceInterpolationImpl(),
                                                         mapModel.getCoordinateSystem() );
        if ( !surface.contains( clickPoint ) ) {
            LOG.logError( "click point is outside result polygons" );
            createErrorlayer( "click point is outside result polygons", tt );
            return;
        }

        // create new feature for filling polygon
        FeatureType ft = adapter.getSchema();
        PropertyType[] pts = ft.getProperties();
        FeatureProperty[] fps = new FeatureProperty[pts.length];
        for ( int i = 0; i < pts.length; i++ ) {
            if ( pts[i].getType() == Types.GEOMETRY ) {
                fps[i] = FeatureFactory.createFeatureProperty( pts[i].getName(), surface );
            } else {
                fps[i] = FeatureFactory.createFeatureProperty( pts[i].getName(), null );
            }
        }
        Feature feature = FeatureFactory.createFeature( "UUID_" + UUID.randomUUID().toString(), ft, fps );
        adapter.insertFeature( feature );
    }

    private void createErrorlayer( String message, Position... posList )
                            throws Exception {
        List<Pair<String, Point>> errorLocations = new ArrayList<Pair<String, Point>>( posList.length );
        for ( Position position : posList ) {
            Pair<String, Point> pa = new Pair<String, Point>();
            pa.first = message;
            pa.second = GeometryFactory.createPoint( position, mapModel.getCoordinateSystem() );
            errorLocations.add( pa );
        }
        Command cmd = new AddErrorLayerCommand( appCont, layer, errorLocations );
        appCont.getCommandProcessor().executeSychronously( cmd, true );
    }

    /**
     * @param edges
     * @param second
     * @return
     */
    private Pair<Position, Position> findNextEdge( List<Pair<Position, Position>> edges,
                                                   Pair<Position, Position> lastEdge ) {
        List<Pair<Position, Position>> list = new ArrayList<Pair<Position, Position>>( 5 );
        // find edge where its first position is equal to last position of last edge
        for ( Pair<Position, Position> pair : edges ) {
            if ( pair.first.equals( lastEdge.second ) ) {
                list.add( pair );
            }
        }
        // if no edge has been found (which should not happen) try finding an edge that
        // also ends where the last edge ends and invert its order.
        if ( list.size() == 0 ) {
            for ( Pair<Position, Position> pair : edges ) {
                if ( pair.second.equals( lastEdge.second ) ) {
                    Position p = pair.first;
                    pair.first = pair.second;
                    pair.second = p;
                    list.add( pair );
                }
            }
        }

        if ( list.size() == 1 ) {
            edges.remove( list.get( 0 ) );
            return list.get( 0 );
        }

        // find edge that don't have an outer side between itself and click point.
        Pair<Position, Position> tmp = null;
        double a = -9E99;
        for ( int i = 0; i < list.size(); i++ ) {
            Vector2d vec1 = new Vector2d( list.get( i ).second.getX() - list.get( i ).first.getX(),
                                          list.get( i ).second.getY() - list.get( i ).first.getY() );
            Vector2d vec2 = new Vector2d( lastEdge.first.getX() - lastEdge.second.getX(), lastEdge.first.getY()
                                                                                          - lastEdge.second.getY() );
            double angle = Math.toDegrees( vec1.angle( vec2 ) );
            if ( ccw( lastEdge.first, lastEdge.second, list.get( i ).second ) >= 0 ) {
                angle = 360 - angle;
            }
            if ( angle > a ) {
                a = angle;
                tmp = list.get( i );
            }
        }
        edges.remove( tmp );
        return tmp;
    }

    /**
     * @param edges
     * @return
     * @throws GeometryException
     */
    private Pair<Position, Position> findStartEgde( Point clickPoint, List<Pair<Position, Position>> edges )
                            throws GeometryException {

        double distance = 9E99;
        Pair<Position, Position> nearest = null;
        Position[] p = new Position[2];
        for ( Pair<Position, Position> pair : edges ) {
            p[0] = pair.first;
            p[1] = pair.second;
            Curve curve = GeometryFactory.createCurve( p, mapModel.getCoordinateSystem() );
            double d = clickPoint.distance( curve );
            if ( d < distance ) {
                distance = d;
                nearest = pair;
            }
        }
        edges.remove( nearest );
        return nearest;
    }

    private List<Pair<Position, Position>> getEdges( FeatureAdapter adapter )
                            throws GeometryException {
        FeatureCollection fc = adapter.getFeatureCollection();
        List<Pair<Position, Position>> list = new ArrayList<Pair<Position, Position>>( fc.size() * 10 );
        Iterator<Feature> iterator = fc.iterator();
        while ( iterator.hasNext() ) {
            Feature feature = (Feature) iterator.next();
            Geometry g = feature.getDefaultGeometryPropertyValue();
            if ( g instanceof Curve ) {
                Position[] pos = ( (Curve) g ).getAsLineString().getPositions();
                for ( int i = 0; i < pos.length - 1; i++ ) {
                    Pair<Position, Position> p = new Pair<Position, Position>( pos[i], pos[i + 1] );
                    list.add( p );
                }
            } else if ( g instanceof MultiCurve ) {
                Curve[] curves = ( (MultiCurve) g ).getAllCurves();
                for ( Curve curve : curves ) {
                    Position[] pos = curve.getAsLineString().getPositions();
                    for ( int i = 0; i < pos.length - 1; i++ ) {
                        Pair<Position, Position> p = new Pair<Position, Position>( pos[i], pos[i + 1] );
                        list.add( p );
                    }
                }
            } else if ( g instanceof Surface ) {
                Position[] pos = ( (Surface) g ).getSurfaceBoundary().getExteriorRing().getPositions();
                for ( int i = 0; i < pos.length - 1; i++ ) {
                    Pair<Position, Position> p = new Pair<Position, Position>( pos[i], pos[i + 1] );
                    list.add( p );
                }
            } else if ( g instanceof MultiSurface ) {
                Surface[] surfaces = ( (MultiSurface) g ).getAllSurfaces();
                for ( Surface surface : surfaces ) {
                    Position[] pos = surface.getSurfaceBoundary().getExteriorRing().getPositions();
                    for ( int i = 0; i < pos.length - 1; i++ ) {
                        Pair<Position, Position> p = new Pair<Position, Position>( pos[i], pos[i + 1] );
                        list.add( p );
                    }
                }
            }
        }
        return list;
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
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 
     * @param start
     * @param end
     * @param p
     * @return -1 if the points are counter clock wise, 0 if the points have a direction and 1 if they are clockwise.
     */
    protected static int ccw( Position start, Position end, Position p ) {
        double X1 = start.getX();
        double Y1 = start.getY();
        double X2 = end.getX();
        double Y2 = end.getY();
        double PX = p.getX();
        double PY = p.getY();
        X2 -= X1;
        Y2 -= Y1;
        PX -= X1;
        PY -= Y1;

        double ccw = ( PX * Y2 ) - ( PY * X2 );

        if ( ccw == 0.0 ) {
            ccw = ( PX * X2 ) + ( PY * Y2 );

            if ( ccw > 0.0 ) {
                PX -= X2;
                PY -= Y2;
                ccw = ( PX * X2 ) + ( PY * Y2 );

                if ( ccw < 0.0 ) {
                    ccw = 0.0;
                }
            }
        }

        return ( ccw < 0.0 ) ? ( -1 ) : ( ( ccw > 0.0 ) ? 1 : 0 );
    }

}
