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

package org.deegree.igeo.views.swing.digitize;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.deegree.framework.util.GeometryUtils;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.settings.SnappingToleranceOpt;
import org.deegree.igeo.views.GeoDrawingPane;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Ring;
import org.deegree.model.spatialschema.Surface;
import org.deegree.model.spatialschema.SurfaceBoundary;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class MoveVertexDrawingPane extends GeoDrawingPane {

    protected BasicStroke stroke = new BasicStroke( 4 );

    protected int targetSize = 0;

    protected int halfTargetSize = 0;

    protected double tolerance;

    protected Position[] neighbors = null;

    protected Color drawColor = new Color( 0f, 1f, 0.5f, 0.8f );
    
    protected enum LINES {first, second, both};
    
    protected LINES lines = LINES.both;
    

    /**
     * 
     *
     */
    public MoveVertexDrawingPane( ApplicationContainer<?> appContainer ) {
        super( appContainer );
        SnappingToleranceOpt opt = appContainer.getSettings().getSnappingToleranceOptions();
        // TODO
        // consider geometric value instead of pixel value
        targetSize = (int) opt.getValue();
        halfTargetSize = targetSize / 2;
    }

    @Override
    public void setFeatureCollection( FeatureCollection fc ) {
        super.setFeatureCollection( fc );
    }

    /**
     * 
     * @param curve
     * @param sourcePoint
     * @return
     * @throws GeometryException
     */
    private Position[] handleCurve( Curve curve, Point sourcePoint ) {
        Position[] positions = null;
        try {
            positions = curve.getAsLineString().getPositions();
        } catch ( GeometryException e ) {
           // ignore
        }
        if ( positions[0].equals( sourcePoint.getPosition() ) ) {
            lines = LINES.first;
        } else if ( positions[positions.length-1].equals( sourcePoint.getPosition() )) {
            lines = LINES.second;            
        } 
        return findNearest( positions, sourcePoint );
    }

    /**
     * 
     * @param geom
     * @param sourcePoint
     * @return
     */
    private Position[] handleSurface( Surface geom, Point sourcePoint ) {
        SurfaceBoundary sb = geom.getSurfaceBoundary();
        Ring[] inner = sb.getInteriorRings();
        Position[][] positions = new Position[inner.length + 1][];
        positions[0] = sb.getExteriorRing().getPositions();
        for ( int i = 1; i < positions.length; i++ ) {
            positions[i] = inner[i - 1].getPositions();
        }
        return findNearest( positions, sourcePoint );
    }

    /**
     * 
     * @param positions
     * @param sourcePoint
     * @return
     */
    protected Position[] findNearest( Position[][] positions, Point sourcePoint ) {
        double tolerance = sourcePoint.getTolerance();
        double dist = Double.MAX_VALUE;
        int outIndex = 0;
        int index = 0;
        for ( int i = 0; i < positions.length; i++ ) {
            for ( int j = 0; j < positions[i].length; j++ ) {
                double tmp = GeometryUtils.distance( sourcePoint.getPosition(), positions[i][j] );
                if ( tmp < dist && tmp <= tolerance ) {
                    dist = tmp;
                    index = j;
                    outIndex = i;
                }
            }
        }
        if ( index == 0 || positions[outIndex].length == index + 1 ) {
            return new Position[] { positions[outIndex][1], positions[outIndex][positions[outIndex].length - 2] };
        }
        return new Position[] { positions[outIndex][index - 1], positions[outIndex][index + 1] };
    }

    /**
     * 
     * @param positions
     * @param sourcePoint
     * @return
     */
    protected Position[] findNearest( Position[] positions, Point sourcePoint ) {
        double tolerance = sourcePoint.getTolerance();
        double dist = Double.MAX_VALUE;
        int index = 0;
        for ( int i = 0; i < positions.length; i++ ) {
            double tmp = GeometryUtils.distance( sourcePoint.getPosition(), positions[i] );
            if ( tmp < dist && tmp <= tolerance ) {
                dist = tmp;
                index = i;
            }
        }
        if ( index == 0 || positions.length == index + 1 ) {
            return new Position[] { positions[1], positions[positions.length - 2] };
        }
        return new Position[] { positions[index - 1], positions[index + 1] };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#draw(int, int, java.awt.Graphics)
     */
    public void draw( int x, int y, Graphics g ) {

        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        if ( snapper != null ) {
            java.awt.Point p = new java.awt.Point( x, y );
            p = snapper.snap( p );
            double xx = gt.getSourceX( p.x );
            double yy = gt.getSourceY( p.y );
            Point pp = GeometryFactory.createPoint( xx, yy, mapModel.getCoordinateSystem() );
            if ( !pp.equals( points.get( 0 ) ) ) {
                x = p.x;
                y = p.y;
            }
        }
        this.currentX = x;
        this.currentY = y;
        g.setColor( drawColor );
        Stroke temp = ( (Graphics2D) g ).getStroke();
        ( (Graphics2D) g ).setStroke( stroke );
        g.drawOval( x - halfTargetSize, y - halfTargetSize, targetSize, targetSize );
        if ( neighbors != null ) {
            // draw preview of new line segments
            int x1 = (int) gt.getDestX( neighbors[0].getX() );
            int y1 = (int) gt.getDestY( neighbors[0].getY() );
            int x2 = (int) gt.getDestX( neighbors[1].getX() );
            int y2 = (int) gt.getDestY( neighbors[1].getY() );
            if ( lines == LINES.first || lines == LINES.both ) {
                g.drawLine( x1, y1, x, y );
            }
            if ( lines == LINES.second || lines == LINES.both ) {
                g.drawLine( x2, y2, x, y );
            }
        }
        ( (Graphics2D) g ).setStroke( temp );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#draw(int, int)
     */
    public void draw( int x, int y ) {
        if ( snapper != null ) {
            java.awt.Point p = new java.awt.Point( x, y );
            p = snapper.snap( p );
            GeoTransform gt = mapModel.getToTargetDeviceTransformation();
            double xx = gt.getSourceX( p.x );
            double yy = gt.getSourceY( p.y );
            Point pp = GeometryFactory.createPoint( xx, yy, mapModel.getCoordinateSystem() );
            if ( !pp.equals( points.get( 0 ) ) ) {
                x = p.x;
                y = p.y;
            }
        }
        this.currentX = x;
        this.currentY = y;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#getDrawnObject()
     */
    public List<java.awt.Point> getDrawnObject() {
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        List<java.awt.Point> list = new ArrayList<java.awt.Point>( points.size() );
        for ( Point point : points ) {
            int x = (int) Math.round( gt.getDestX( point.getX() ) );
            int y = (int) Math.round( gt.getDestY( point.getY() ) );
            list.add( new java.awt.Point( x, y ) );
        }
        return list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#setGraphicContext(java.awt.Graphics)
     */
    public void setGraphicContext( Graphics g ) {
        this.g = g;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#startDrawing(int, int)
     */
    public void startDrawing( int x, int y ) {
        isDrawing = true;
        points = new ArrayList<Point>();
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        Position pos = null;
        if ( snapper != null ) {
            snapper.initSnapInfoList();
            java.awt.Point p = new java.awt.Point( x, y );
            pos = snapper.snapPos( p );
            x = (int) gt.getDestX( pos.getX() );
            y = (int) gt.getDestY( pos.getY() );
        } else {
            double dx = gt.getSourceX( x );
            double dy = gt.getSourceY( y );
            pos = GeometryFactory.createPosition( dx, dy );
        }
        Point p = GeometryFactory.createPoint( pos, mapModel.getCoordinateSystem() );
        // set tolerance to defined value to find vertices to be moved
        tolerance = Math.abs( gt.getSourceX( 0 ) - gt.getSourceX( targetSize ) ) / 2d;
        p.setTolerance( tolerance );
        points.add( p );
        points.add( p );
        this.draw( x, y );
        stopped = false;

        // find neighbor vertices of vertex to be moved to enable update of connecting line segments
        // while moving
        Iterator<Feature> iter = fc.iterator();
        while ( iter.hasNext() ) {
            Feature feature = iter.next();
            //QualifiedName geomProperty = CommandHelper.findGeomProperty( feature );
            Geometry geom = (Geometry) feature.getDefaultGeometryPropertyValue();
            if ( geom instanceof Curve ) {
                neighbors = handleCurve( (Curve) geom, p );
            } else if ( geom instanceof Surface ) {
                neighbors = handleSurface( (Surface) geom, p );
            } else if ( geom instanceof MultiCurve ) {
                // TODO
                // neighbors = handleMultiCurve( (MultiCurve) geom, p );
            } else if ( geom instanceof MultiSurface ) {
                // TODO
                // neighbors = handleMultiSurface( (MultiSurface) geom, p );
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#stopDrawing(int, int)
     */
    public void stopDrawing( int x, int y ) {
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        double xx = gt.getSourceX( x );
        double yy = gt.getSourceY( y );
        Position p = GeometryFactory.createPosition( xx, yy );
        if ( snapper != null ) {
            p = snapper.snap( p );
        }
        Point point = GeometryFactory.createPoint( p, mapModel.getCoordinateSystem() );
        // set tolerance to defined value to find vertices to be moved
        point.setTolerance( tolerance );
        points.set( 1, point );
        stopped = true;
    }

}
