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
package org.deegree.igeo.views.swing.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.Pair;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.dataadapter.DataAccessException;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Ring;
import org.deegree.model.spatialschema.Surface;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class AssignLabelToCurveDrawingPane extends ChangeLabelPositionDrawingPane {

    private static final ILogger LOG = LoggerFactory.getLogger( AssignLabelToCurveDrawingPane.class );

    protected static Stroke segmentStroke = new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, null,
                                                             0 );

    private Pair<Position, Position> segment;

    private Layer layer;

    /**
     * @param appCont
     */
    public AssignLabelToCurveDrawingPane( ApplicationContainer<?> appCont ) {
        super( appCont );
    }

    @Override
    public void draw( int x, int y, Graphics g ) {
        if ( segment != null ) {
            GeoTransform gt = mapModel.getToTargetDeviceTransformation();
            Position p1 = gt.getDestPoint( segment.first );
            Position p2 = gt.getDestPoint( segment.second );
            ( (Graphics2D) g ).setStroke( segmentStroke );
            g.setColor( rectDrawColor );
            g.drawLine( (int) p1.getX(), (int) p1.getY(), (int) p2.getX(), (int) p2.getY() );
            g.setColor( Color.RED );
            g.fillRect( (int) p1.getX() - 3, (int) p1.getY() - 3, 7, 7 );
            g.fillRect( (int) p2.getX() - 3, (int) p2.getY() - 3, 7, 7 );
        }
        if ( points.size() > 0 ) {
            GeoTransform gt = mapModel.getToTargetDeviceTransformation();

            Point p = points.get( 0 );
            int x_ = (int) gt.getDestX( p.getX() );
            int y_ = (int) gt.getDestY( p.getY() );
            int w = x - x_;
            int h = y - y_;
            if ( w < 0 ) {
                x_ = x;
                w *= -1;
            }
            if ( h < 0 ) {
                y_ = y;
                h *= -1;
            }

            g.setColor( new Color( 1f, 1f, 1f, 0.6f ) );
            g.fillRect( x_, y_, w, h );
            g.setColor( new Color( 1f, 0f, 0f ) );
            g.drawRect( x_, y_, w, h );
        }

        this.currentX = x;
        this.currentY = y;
        super.draw( x, y, g );
    }

    /**
     * 
     * @param envelope
     * @return selected segment (or <code>null</code>)
     * @throws GeometryException
     */
    public Pair<Position, Position> selectSegment( Envelope envelope )
                            throws GeometryException {

        Surface env = GeometryFactory.createSurface( envelope, mapModel.getCoordinateSystem() );
        List<DataAccessAdapter> dataAccess = layer.getDataAccess();
        for ( DataAccessAdapter adapter : dataAccess ) {
            if ( adapter instanceof FeatureAdapter ) {
                FeatureCollection fc;
                try {
                    fc = ( (FeatureAdapter) adapter ).getFeatureCollection( envelope );
                } catch ( FilterEvaluationException e ) {
                    LOG.logError( e.getMessage(), e );
                    throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10053" ) );
                }
                Iterator<Feature> iterator = fc.iterator();
                while ( iterator.hasNext() ) {
                    Geometry geom = iterator.next().getDefaultGeometryPropertyValue();
                    // it must be considered that segments can be selected from Curves, Surfaces,
                    // MultiCurves and MultiSurfaces
                    if ( geom instanceof Curve ) {
                        handleCurves( env, (Curve) geom );
                    } else if ( geom instanceof MultiCurve ) {
                        handleMultiCurves( env, (MultiCurve) geom );
                    } else if ( geom instanceof Surface ) {
                        handleSurfaces( env, (Surface) geom );
                    } else if ( geom instanceof MultiSurface ) {
                        handleMultiSurfaces( env, (MultiSurface) geom );
                    }
                }
            }
        }
        return segment;
    }

    /**
     * @param env
     * @param geom
     * @throws GeometryException
     */
    private void handleSurfaces( Surface env, Surface geom )
                            throws GeometryException {
        Position[] positions = geom.getSurfaceBoundary().getExteriorRing().getPositions();
        for ( int i = 0; i < positions.length - 1; i++ ) {
            Curve tmp = GeometryFactory.createCurve( new Position[] { positions[i], positions[i + 1] },
                                                     geom.getCoordinateSystem() );
            if ( env.intersects( tmp ) ) {
                segment = new Pair<Position, Position>( positions[i], positions[i + 1] );
                return;
            }
        }
        Ring[] interior = geom.getSurfaceBoundary().getInteriorRings();
        for ( Ring ring : interior ) {
            positions = ring.getPositions();
            for ( int i = 0; i < positions.length - 1; i++ ) {
                Curve tmp = GeometryFactory.createCurve( new Position[] { positions[i], positions[i + 1] },
                                                         geom.getCoordinateSystem() );
                if ( env.intersects( tmp ) ) {
                    segment = new Pair<Position, Position>( positions[i], positions[i + 1] );
                    return;
                }
            }
        }
    }

    /**
     * @param env
     * @param geom
     * @throws GeometryException
     */
    private void handleMultiSurfaces( Surface env, MultiSurface geom )
                            throws GeometryException {

        Surface[] surfaces = geom.getAllSurfaces();
        for ( Surface surface : surfaces ) {
            Position[] positions = surface.getSurfaceBoundary().getExteriorRing().getPositions();
            for ( int i = 0; i < positions.length - 1; i++ ) {
                Curve tmp = GeometryFactory.createCurve( new Position[] { positions[i], positions[i + 1] },
                                                         geom.getCoordinateSystem() );
                if ( env.intersects( tmp ) ) {
                    segment = new Pair<Position, Position>( positions[i], positions[i + 1] );
                    return;
                }
            }
            Ring[] interior = surface.getSurfaceBoundary().getInteriorRings();
            for ( Ring ring : interior ) {
                positions = ring.getPositions();
                for ( int i = 0; i < positions.length - 1; i++ ) {
                    Curve tmp = GeometryFactory.createCurve( new Position[] { positions[i], positions[i + 1] },
                                                             geom.getCoordinateSystem() );
                    if ( env.intersects( tmp ) ) {
                        segment = new Pair<Position, Position>( positions[i], positions[i + 1] );
                        return;
                    }
                }
            }
        }
    }

    /**
     * 
     * @param layer
     */
    public void setLayer( Layer layer ) {
        this.layer = layer;
    }

    private void handleMultiCurves( Surface env, MultiCurve geom )
                            throws GeometryException {
        Curve[] curves = geom.getAllCurves();
        for ( Curve curve : curves ) {
            Position[] positions = curve.getAsLineString().getPositions();
            for ( int i = 0; i < positions.length - 1; i++ ) {
                Curve tmp = GeometryFactory.createCurve( new Position[] { positions[i], positions[i + 1] },
                                                         geom.getCoordinateSystem() );
                if ( env.intersects( tmp ) ) {
                    segment = new Pair<Position, Position>( positions[i], positions[i + 1] );
                    return;
                }
            }
        }
    }

    private void handleCurves( Surface env, Curve geom )
                            throws GeometryException {
        Position[] positions = geom.getAsLineString().getPositions();
        for ( int i = 0; i < positions.length - 1; i++ ) {
            Curve tmp = GeometryFactory.createCurve( new Position[] { positions[i], positions[i + 1] },
                                                     geom.getCoordinateSystem() );
            if ( env.intersects( tmp ) ) {
                segment = new Pair<Position, Position>( positions[i], positions[i + 1] );
                return;
            }
        }
    }

    /**
     * 
     */
    public void clear() {
        points = new LinkedList<Point>();
    }
}
