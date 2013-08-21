//$Head URL$
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

package org.deegree.desktop.views;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.modules.DigitizerModule;
import org.deegree.framework.util.GeometryUtils;
import org.deegree.framework.util.Pair;
import org.deegree.framework.utils.LineUtils;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Position;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public abstract class GeoDrawingPane implements DrawingPane {

    /**
     * List of points drawn so far.
     * 
     */
    protected List<org.deegree.model.spatialschema.Point> points = null;

    protected int currentX;

    protected int currentY;

    protected boolean isDrawing = false;

    protected boolean stopped = false;

    protected Graphics g;

    protected MapModel mapModel;

    protected FeatureCollection fc;

    protected Snapper snapper;

    private ApplicationContainer<?> appCont;

    private Point tmpPoint = new Point();

    /**
     * 
     */
    public GeoDrawingPane( ApplicationContainer<?> appCont ) {
        this.appCont = appCont;
    }

    /**
     * sets the {@link MapModel} used by a {@link GeoDrawingPane} to enable an implementing class to geo coordinates
     * instead of pixel coordinates
     * 
     * @param mapModel
     */
    public void setMapModel( MapModel mapModel ) {
        this.mapModel = mapModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#finishDrawing()
     */
    public void finishDrawing() {
        isDrawing = false;
        if ( snapper != null ) {
            snapper.dispose();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#isDrawingStopped()
     */
    public boolean isDrawingStopped() {
        return stopped;
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
     * @see org.deegree.igeo.views.DrawingPane#getCurrent()
     */
    public Point getCurrent() {
        return new Point( this.currentX, this.currentY );
    }

    /**
     * 
     * @return current x/y coordinate as geographic coordinate according to the underlying {@link MapModel}
     */
    public org.deegree.model.spatialschema.Point getCurrentAsGeoPoint() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.GeoDrawingPane#getDrawObjectsAsGeoPoints()
     */
    public List<org.deegree.model.spatialschema.Point> getDrawObjectsAsGeoPoints() {
        return points;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#undrawLastPoint()
     */
    public void undrawLastPoint() {
        if ( points.size() > 1 ) {
            points.remove( points.size() - 1 );
        }
    }

    /**
     * sets the features that has been selected and now shall be moved
     * 
     * @param fc
     */
    public void setFeatureCollection( FeatureCollection fc ) {
        this.fc = fc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#setSnapper(org.deegree.igeo.views.swing.Snapper)
     */
    public void setSnapper( Snapper snapper ) {
        this.snapper = snapper;
    }

    /**
     * corrects current x/y coordinates read from mouse position using definitions for digitizing angle, edge-length or
     * snapping
     * 
     * @param x
     * @param y
     * @return corrected point
     */
    protected Pair<Position, Point> correctPoint( int x, int y ) {
        // read digitizing angle and edge length from instance settings if available
        double angle = -1;
        double length = -1;
        Object o = appCont.getInstanceSetting( DigitizerModule.ANGLE );
        if ( o != null ) {
            angle = ( (Number) o ).doubleValue();
        }
        o = appCont.getInstanceSetting( DigitizerModule.LENGTH );
        if ( o != null ) {
            length = ( (Number) o ).doubleValue();
        }
        Position tmpPos = null;
        // if a fixed angle for digitizing has been defined snapping will be ignored
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        if ( angle > -1 && points.size() > 0 ) {
            org.deegree.model.spatialschema.Point p = null;
            if ( length <= 0 ) {
                // if no length has been defined use distance between current (mouse) position
                // and last digitized point instead
                double cx = gt.getSourceX( x );
                double cy = gt.getSourceY( y );
                p = GeometryFactory.createPoint( cx, cy, points.get( 0 ).getCoordinateSystem() );
                length = GeometryUtils.distance( p.getPosition(), points.get( points.size() - 1 ).getPosition() );
            }
            if ( points.size() > 1 ) {
                angle -= 180;
                if ( angle >= 0 ) {
                    angle -= 180;
                } else {
                    angle = 180 - angle;
                }
                p = GeometryUtils.vectorByAngle( points.get( points.size() - 2 ), points.get( points.size() - 1 ),
                                                 length, Math.toRadians( angle ), true );
            } else {
                double cx = gt.getSourceX( x );
                double cy = gt.getSourceY( y );
                double firstX = points.get( 0 ).getX();
                double firstY = points.get( 0 ).getY();
                // get line equation for line intersecting start position and current mouse position
                double[] line = LineUtils.getLineFromPoints( firstX, firstY, cx, cy );
                Pair<Position, Position> intersection = LineUtils.getSymmetricPoints( firstX, firstY, line[0], length );
                if ( cx > firstX && ( gt.getDestX( firstX ) != x || cy <= firstY ) ) {
                    // cursor is left of first point or it has same x coordinate and is
                    // above first point
                    p = GeometryFactory.createPoint( intersection.first, points.get( 0 ).getCoordinateSystem() );
                } else if ( gt.getDestX( firstX ) != x || cy <= firstY ) {
                    p = GeometryFactory.createPoint( intersection.second, points.get( 0 ).getCoordinateSystem() );
                }
            }
            if ( p != null ) {
                tmpPos = p.getPosition();
                tmpPoint.x = (int) Math.round( gt.getDestX( p.getX() ) );
                tmpPoint.y = (int) Math.round( gt.getDestY( p.getY() ) );
            }
        } else {
            if ( snapper != null ) {
                tmpPos = snapper.snapPos( new Point( x, y ) );
                tmpPoint.x = (int) Math.round( gt.getDestX( tmpPos.getX() ) );
                tmpPoint.y = (int) Math.round( gt.getDestY( tmpPos.getY() ) );
            }
        }
        if ( tmpPos == null ) {
            double xx = gt.getSourceX( x );
            double yy = gt.getSourceY( y );
            tmpPos = GeometryFactory.createPosition( xx, yy );
            tmpPoint.x = x;
            tmpPoint.y = y;
        }
        return new Pair<Position, Point>( tmpPos, tmpPoint );
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
     * @see org.deegree.igeo.views.DrawingPane#getDrawnObject()
     */
    public List<java.awt.Point> getDrawnObject() {
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        List<java.awt.Point> list = new ArrayList<java.awt.Point>( points.size() );
        for ( org.deegree.model.spatialschema.Point point : points ) {
            int x = (int) Math.round( gt.getDestX( point.getX() ) );
            int y = (int) Math.round( gt.getDestY( point.getY() ) );
            list.add( new Point( x, y ) );
        }
        return list;
    }
}
