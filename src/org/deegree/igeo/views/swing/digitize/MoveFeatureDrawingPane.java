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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.swing.drawingpanes.SwingGeoDrawingPane;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.GeometryImpl;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiPoint;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Surface;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class MoveFeatureDrawingPane extends SwingGeoDrawingPane {

    private static ILogger LOG = LoggerFactory.getLogger( MoveFeatureDrawingPane.class );

    private static BasicStroke dashedStroke = new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1,
                                                               new float[] { 10, 10 }, 1 );

    private static BasicStroke pointDashedStroke = new BasicStroke( 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER,
                                                                    1, new float[] { 2, 2 }, 1 );

    private List<Geometry> geometryList;

    private Point startPoint;

    private Point lastPoint;

    /**
     * @param appCont
     */
    public MoveFeatureDrawingPane( ApplicationContainer<?> appCont ) {
        super( appCont );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#draw(int, int, java.awt.Graphics)
     */
    public void draw( int x, int y, Graphics g ) {

        draw( x, y );
        Point tmp = getCurrentAsGeoPoint();
        points.remove( 1 );
        points.add( tmp );
        double dx = -1 * ( lastPoint.getX() - tmp.getX() );
        double dy = -1 * ( lastPoint.getY() - tmp.getY() );
        lastPoint = tmp;
        double[] d = new double[] { dx, dy };
        if ( geometryList != null ) {
            for ( Geometry geometry : geometryList ) {
                geometry.translate( d );
                if ( geometry instanceof Point ) {
                    Point p = (Point) geometry;
                    drawPoint( g, p, 6, pointDashedStroke, Color.BLACK, null );
                } else if ( geometry instanceof MultiPoint ) {
                    Point[] points = ( (MultiPoint) geometry ).getAllPoints();
                    for ( Point point : points ) {
                        drawPoint( g, point, 6, pointDashedStroke, Color.BLACK, null );
                    }
                } else if ( geometry instanceof Curve ) {
                    drawCurve( g, (Curve) geometry, dashedStroke, Color.BLACK );
                } else if ( geometry instanceof MultiCurve ) {
                    Curve[] curves = ( (MultiCurve) geometry ).getAllCurves();
                    for ( Curve curve : curves ) {
                        drawCurve( g, curve, dashedStroke, Color.BLACK );
                    }
                } else if ( geometry instanceof Surface ) {
                    drawSurface( g, (Surface) geometry, dashedStroke, Color.BLACK, null );
                } else if ( geometry instanceof MultiSurface ) {
                    Surface[] surfaces = ( (MultiSurface) geometry ).getAllSurfaces();
                    for ( Surface surface : surfaces ) {
                        drawSurface( g, surface, dashedStroke, Color.BLACK, null );
                    }
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#draw(int, int)
     */
    public void draw( int x, int y ) {
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
     * @see org.deegree.igeo.views.DrawingPane#isDrawing()
     */
    public boolean isDrawing() {
        return isDrawing;
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
        points = new LinkedList<Point>();
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        double xx = gt.getSourceX( x );
        double yy = gt.getSourceY( y );
        startPoint = GeometryFactory.createPoint( xx, yy, mapModel.getCoordinateSystem() );
        lastPoint = GeometryFactory.createPoint( xx, yy, mapModel.getCoordinateSystem() );
        points.add( startPoint );
        points.add( lastPoint );
        isDrawing = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#stopDrawing(int, int)
     */
    public void stopDrawing( int x, int y ) {
        draw( x, y, g );
    }

    /**
     * 
     * @return current x/y coordinate as geographic coordinate according to the underlying {@link MapModel}
     */
    public Point getCurrentAsGeoPoint() {
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        double xx = gt.getSourceX( currentX );
        double yy = gt.getSourceY( currentY );
        return GeometryFactory.createPoint( xx, yy, mapModel.getCoordinateSystem() );
    }

    @Override
    public void setFeatureCollection( FeatureCollection fc ) {

        if ( fc == null || fc.size() == 0 ) {
            DialogFactory.openErrorDialog( "application", null, Messages.get( "$MD11094" ), Messages.get( "$MD11095" ) );
            return;
        }

        geometryList = new ArrayList<Geometry>();
        Iterator<Feature> iterator = fc.iterator();
        while ( iterator.hasNext() ) {
            Feature feature = iterator.next();
            Geometry[] geometries = feature.getGeometryPropertyValues();
            for ( Geometry geometry : geometries ) {
                try {
                    geometry = (Geometry) ( (GeometryImpl) geometry ).clone();
                } catch ( CloneNotSupportedException e ) {
                    LOG.logError( e.getMessage(), e );
                }
                geometryList.add( geometry );
            }
        }

        super.setFeatureCollection( fc );
    }

}
