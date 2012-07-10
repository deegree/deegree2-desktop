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

package org.deegree.igeo.views;

import static java.awt.geom.Line2D.ptSegDist;
import static java.lang.Double.MAX_VALUE;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.GeometryUtils;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.ChangeListener;
import org.deegree.igeo.ValueChangedEvent;
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.LayerGroup;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MapModelVisitor;
import org.deegree.igeo.settings.Settings;
import org.deegree.igeo.settings.SnappingLayersOpts;
import org.deegree.igeo.settings.SnappingToleranceOpt;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiPoint;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Ring;
import org.deegree.model.spatialschema.Surface;

/**
 * This class enables snapping a digitizing cursor to existing vertexes and/or lines. It reads snapping settings from
 * current configuration document and layers to be used as snapping targets from current map model. A layer to be used
 * as target must be selected for snapping.<br>
 * 
 * <pre>
 * appCont.getMapModel( null ).getLayersSelectedForAction( &quot;snapping&quot; );
 * </pre>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class Snapper implements ChangeListener {

    private static final ILogger LOG = LoggerFactory.getLogger( Snapper.class );

    public static final String SNAPPING = "snapping";

    public enum SNAPTARGET {
        Vertex, StartNode, EndNode, Edge, EdgeCenter
    };

    private GeoTransform gt;

    private List<LayerSnapInfo> layers = new ArrayList<LayerSnapInfo>( 50 );

    private MapModel mapModel;

    private ApplicationContainer<?> appCont;

    /**
     * 
     * @param appCont
     *            container for currently loaded project
     */
    public Snapper( ApplicationContainer<?> appCont ) {
        this.appCont = appCont;
        mapModel = appCont.getMapModel( null );
        initSnapInfoList();

        // register as change listener to update snapping targets if map model
        // (e.g. boundingbox) changes
        mapModel.addChangeListener( this );

    }

    /**
     * fills list of layers to be considered for snapping
     */
    public void initSnapInfoList() {
        Settings settings = appCont.getSettings();
        final SnappingLayersOpts slo = settings.getSnappingLayersOptions();
        layers.clear();
        try {
            mapModel.walkLayerTree( new MapModelVisitor() {

                public void visit( Layer layer )
                                        throws Exception {
                    if ( slo.isSelectedForSnapping( layer.getIdentifier() ) ) {
                        List<SNAPTARGET> snapOrder = slo.getSelectedForSnappingList( layer.getIdentifier() );
                        layers.add( new LayerSnapInfo( layer, snapOrder ) );
                    }

                }

                public void visit( LayerGroup layerGroup )
                                        throws Exception {
                }

            } );
        } catch ( Exception e ) {
            // TODO: handle exception
        }
        // collect vertices and segments of a map
        valueChanged( null );
    }

    /**
     * removes Snapper as listener from current map model. For example will be invoked when finishDrawing() method of a
     * DrawingPane will be invoked
     */
    public void dispose() {
        mapModel.removeChangeListener( this );
    }

    /**
     * this method should just be used for drawing !!!!!!!!
     * @see #snap(Position)
     * @param point
     * @return snapped point
     */
    public Point snap( Point point ) {
        double xx = gt.getSourceX( point.x );
        double yy = gt.getSourceY( point.y );
        Position p = snap( xx, yy );
        if ( p != null ) {
            point.x = (int) Math.round( gt.getDestX( p.getX() ) );
            point.y = (int) Math.round( gt.getDestY( p.getY() ) );
        }
        return point;
    }
    
    /**
     * @see #snap(Position)
     * @param point
     * @return snapped point
     */
    public Position snapPos( Point point ) {
        double xx = gt.getSourceX( point.x );
        double yy = gt.getSourceY( point.y );
        Position p = snap( xx, yy );       
        return p;
    }

    /**
     * 
     * @see #snap(Position)
     * @param x
     * @param y
     * @return snapped point
     */
    public Position snap( double x, double y ) {
        return snap( GeometryFactory.createPosition( x, y ) );
    }

    /**
     * snaps a passed point (geographic coordinates) to the matching target. If no matching target can be found because
     * no geometry of a layer selected for snapping is within current snapping distance, the passed point will be
     * returned.
     * 
     * @param position
     * @return snapped point
     */
    public Position snap( Position position ) {
        Position result = position;
        double distance = Double.MAX_VALUE;
        for ( LayerSnapInfo layerSnapInfo : layers ) {
            Position tmp = layerSnapInfo.snap( position );
            double td = GeometryUtils.distance( tmp, position );
            if ( td < distance && tmp != position ) {
                distance = td;
                result = tmp;
            }
        }
        return GeometryFactory.createPosition( result.getAsArray() );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.ChangeListener#valueChanged(org.deegree.igeo.ValueChangedEvent)
     */
    public void valueChanged( ValueChangedEvent event ) {
        gt = mapModel.getToTargetDeviceTransformation();
        try {
            for ( LayerSnapInfo snapInfo : layers ) {
                snapInfo.collectVertexes();
            }
        } catch ( FilterEvaluationException e ) {
            LOG.logError( e.getMessage(), e );
        }
    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    private class LayerSnapInfo {

        private Layer layer;

        private List<Position> vertices;

        private List<Position[]> edges;

        private List<Position> startNodes;

        private List<Position> endNodes;

        private List<Position> edgeCenters;

        private List<SNAPTARGET> snapOrder;

        private double tolerance;

        /**
         * 
         * @param layer
         */
        private LayerSnapInfo( Layer layer, List<SNAPTARGET> snapOrder ) {
            this.layer = layer;
            this.snapOrder = snapOrder;
            vertices = new ArrayList<Position>( 5000 );
            startNodes = new ArrayList<Position>( 5000 );
            endNodes = new ArrayList<Position>( 5000 );
            edges = new ArrayList<Position[]>( 5000 );
            edgeCenters = new ArrayList<Position>( 5000 );
        }

        private void collectVertexes()
                                throws FilterEvaluationException {
            vertices.clear();
            startNodes.clear();
            endNodes.clear();
            edges.clear();
            edgeCenters.clear();
            SnappingToleranceOpt sto = appCont.getSettings().getSnappingToleranceOptions();
            tolerance = sto.getValue();
            String snapUnits = sto.getUOM();
            if ( snapUnits.equalsIgnoreCase( "Pixel" ) ) {
                // if snapping tolerance has been defined as pixels it must be converted
                // into map units
                GeoTransform gt = mapModel.getToTargetDeviceTransformation();
                double x1 = gt.getSourceX( tolerance );
                double x2 = gt.getSourceX( 0 );
                tolerance = Math.abs( x2 - x1 );
            }

            List<DataAccessAdapter> tmp = layer.getDataAccess();
            for ( DataAccessAdapter adapter : tmp ) {
                if ( adapter instanceof FeatureAdapter ) {
                    FeatureCollection fc = ( (FeatureAdapter) adapter ).getFeatureCollection( mapModel.getEnvelope() );
                    Iterator<Feature> iterator = fc.iterator();
                    while ( iterator.hasNext() ) {
                        Geometry[] geometries = iterator.next().getGeometryPropertyValues();
                        for ( Geometry geometry : geometries ) {
                            if ( geometry instanceof org.deegree.model.spatialschema.Point ) {
                                vertices.add( ( (org.deegree.model.spatialschema.Point) geometry ).getPosition() );
                            } else if ( geometry instanceof Curve ) {
                                Curve curve = (Curve) geometry;
                                collectCurveVertexes( curve );
                            } else if ( geometry instanceof Surface ) {
                                Surface surface = (Surface) geometry;
                                collectSurfaceVertexes( surface );
                            } else if ( geometry instanceof MultiPoint ) {
                                org.deegree.model.spatialschema.Point[] points = ( (MultiPoint) geometry ).getAllPoints();
                                for ( org.deegree.model.spatialschema.Point point : points ) {
                                    vertices.add( point.getPosition() );
                                }
                            } else if ( geometry instanceof MultiCurve ) {
                                Curve[] curves = ( (MultiCurve) geometry ).getAllCurves();
                                for ( Curve curve : curves ) {
                                    collectCurveVertexes( curve );
                                }
                            } else if ( geometry instanceof MultiSurface ) {
                                Surface[] surfaces = ( (MultiSurface) geometry ).getAllSurfaces();
                                for ( Surface surface : surfaces ) {
                                    collectSurfaceVertexes( surface );
                                }
                            }
                        }
                    }
                }
            }
            LOG.logDebug( vertices.size() + " vertices found for snapping for layer: " + layer.getTitle() );
            LOG.logDebug( startNodes.size() + " edge start positions found for snapping for layer: " + layer.getTitle() );
            LOG.logDebug( endNodes.size() + " edge end positions found for snapping for layer: " + layer.getTitle() );
            LOG.logDebug( edges.size() + " edges found for snapping for layer: " + layer.getTitle() );
            LOG.logDebug( edgeCenters.size() + " edge centers found for snapping for layer: " + layer.getTitle() );
        }

        private void collectSurfaceVertexes( Surface surface ) {
            Position[] pos = surface.getSurfaceBoundary().getExteriorRing().getPositions();
            for ( int i = 0; i < pos.length - 1; i++ ) {
                vertices.add( pos[i] );
                edges.add( new Position[] { pos[i], pos[i + 1] } );
                double x = pos[i].getX() / 2d + pos[i + 1].getX() / 2d;
                double y = pos[i].getY() / 2d + pos[i + 1].getY() / 2d;
                edgeCenters.add( GeometryFactory.createPosition( x, y ) );
            }
            vertices.add( pos[pos.length - 1] );
            Ring[] rings = surface.getSurfaceBoundary().getInteriorRings();
            for ( int k = 0; k < rings.length; k++ ) {
                pos = rings[k].getPositions();
                for ( int i = 0; i < pos.length - 1; i++ ) {
                    vertices.add( pos[i] );
                    edges.add( new Position[] { pos[i], pos[i + 1] } );
                    double x = pos[i].getX() / 2d + pos[i + 1].getX() / 2d;
                    double y = pos[i].getY() / 2d + pos[i + 1].getY() / 2d;
                    edgeCenters.add( GeometryFactory.createPosition( x, y ) );
                }
                vertices.add( pos[pos.length - 1] );
            }
        }

        private void collectCurveVertexes( Curve curve ) {
            startNodes.add( curve.getStartPoint().getPosition() );
            endNodes.add( curve.getEndPoint().getPosition() );
            try {
                Position[] pos = curve.getAsLineString().getPositions();
                for ( int i = 0; i < pos.length - 1; i++ ) {
                    vertices.add( pos[i] );
                    edges.add( new Position[] { pos[i], pos[i + 1] } );
                    double x = pos[i].getX() / 2d + pos[i + 1].getX() / 2d;
                    double y = pos[i].getY() / 2d + pos[i + 1].getY() / 2d;
                    edgeCenters.add( GeometryFactory.createPosition( x, y ) );
                }
                vertices.add( pos[pos.length - 1] );
            } catch ( GeometryException e ) {
                // ignore
            }
        }

        /**
         * @see #snap(double, double)
         * @param position
         * @return snapped point
         */
        private Position snap( Position position ) {
            Position target = position;
            if ( layers.size() != 0 ) {
                for ( SNAPTARGET snapTarget : snapOrder ) {
                    switch ( snapTarget ) {
                    case Vertex:
                        target = vertexCheck( position );
                        break;
                    case StartNode:
                        target = startNodeCheck( position );
                        break;
                    case EndNode:
                        target = endNodeCheck( position );
                        break;
                    case Edge:
                        target = edgeCheck( position );
                        break;
                    case EdgeCenter:
                        target = edgeCenterCheck( position );
                        break;
                    }
                    if ( !position.equals( target ) ) {
                        break;
                    }
                }
            }
            return target;
        }

        private Position edgeCheck( Position position ) {
            Position target = position;
            final double posx = position.getX();
            final double posy = position.getY();
            double distance = MAX_VALUE;
            for ( Position[] segment : edges ) {
                double tmp = ptSegDist( segment[0].getX(), segment[0].getY(), segment[1].getX(), segment[1].getY(),
                                        position.getX(), position.getY() );

                if ( tmp <= tolerance && tmp < distance ) {
                    final double dx = segment[1].getX() - segment[0].getX();
                    final double dy = segment[1].getY() - segment[0].getY();
                    final double m1 = dy / dx;
                    final double m2 = -dx / dy;
                    final double nx = ( posy - m2 * posx - segment[0].getY() + m1 * segment[0].getX() ) / ( m1 - m2 );
                    final double ny = m2 * ( nx - posx ) + posy;
                    return GeometryFactory.createPosition( nx, ny );
                }
            }
            return target;
        }

        private Position endNodeCheck( Position position ) {
            Position target = position;
            double distance = Double.MAX_VALUE;
            for ( Position pos : endNodes ) {
                double tmp = GeometryUtils.distance( pos, position );
                if ( tmp <= tolerance && tmp < distance ) {
                    distance = tmp;
                    target = pos;
                }
            }
            return target;
        }

        private Position startNodeCheck( Position position ) {
            Position target = position;
            double distance = Double.MAX_VALUE;
            for ( Position pos : startNodes ) {
                double tmp = GeometryUtils.distance( pos, position );
                if ( tmp <= tolerance && tmp < distance ) {
                    distance = tmp;
                    target = pos;
                }
            }
            return target;
        }

        private Position vertexCheck( Position position ) {
            Position target = position;
            double distance = Double.MAX_VALUE;
            for ( Position pos : vertices ) {
                double tmp = GeometryUtils.distance( pos, position );
                if ( tmp <= tolerance && tmp < distance ) {
                    distance = tmp;
                    target = pos;
                }
            }
            return target;
        }

        private Position edgeCenterCheck( Position position ) {
            Position target = position;
            double distance = Double.MAX_VALUE;
            for ( Position pos : edgeCenters ) {
                double tmp = GeometryUtils.distance( pos, position );
                if ( tmp <= tolerance && tmp < distance ) {
                    distance = tmp;
                    target = pos;
                }
            }
            return target;
        }

    }

}
