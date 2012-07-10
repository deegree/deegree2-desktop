//$HeadURL: svn+ssh://mschneider@svn.wald.intevation.org/deegree/base/trunk/resources/eclipse/svn_classfile_header_template.xml $
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
package org.deegree.igeo.views.swing.drawingpanes;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.deegree.framework.util.Pair;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.settings.SnappingToleranceOpt;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Surface;

/**
 * 
 * 
 * 
 * @author <a href="mailto:bh@intevation.de">Bernhard Herzog</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class CreatePolygonDrawingPane extends SwingGeoDrawingPane {

    protected static Color FILLCOLOR = new Color( 1f, 1f, 1f, 0.6f );

    protected static Color DRAWCOLOR = new Color( 0f, 1f, 0.5f, 0.8f );

    protected BasicStroke stroke = new BasicStroke( 4 );

    protected int targetSize = 0;

    protected int halfTargetSize =0;

    protected double tolerance;

    /**
     * 
     * @param appCont
     */
    public CreatePolygonDrawingPane( ApplicationContainer<?> appCont ) {
        super( appCont );
        SnappingToleranceOpt opt = appCont.getSettings().getSnappingToleranceOptions();
        // TODO
        // consider geometric value instead of pixel value
        targetSize = (int) opt.getValue();
        halfTargetSize = targetSize / 2; 
    }

    /**
     * Extends the base class method to initialize this.points if it hasn't been initialized already.
     */
    public void startDrawing( int x, int y ) {
        this.isDrawing = true;

        points = new LinkedList<Point>();
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
        points.add( GeometryFactory.createPoint( pos, mapModel.getCoordinateSystem() ) );

        this.draw( x, y );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#draw(int, int)
     */
    public void draw( int x, int y ) {
        Pair<Position, java.awt.Point> tmp = correctPoint( x, y );
        this.currentX = tmp.second.x;
        this.currentY = tmp.second.y;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#draw(int, int, java.awt.Graphics)
     */
    public void draw( int x, int y, Graphics g ) {
        
        Pair<Position, java.awt.Point> tmp = correctPoint( x, y );
        this.currentX = tmp.second.x;
        this.currentY = tmp.second.y;
        
        if ( snapper != null ) {
            g.setColor( DRAWCOLOR );
            Stroke temp = ( (Graphics2D) g ).getStroke();
            ( (Graphics2D) g ).setStroke( stroke );
            g.drawOval( x - halfTargetSize, y - halfTargetSize, targetSize, targetSize );
            ( (Graphics2D) g ).setStroke( temp );
        }
        
        drawSurface( g, null, new BasicStroke( 1 ), Color.RED, FILLCOLOR );

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#stopDrawing(int, int)
     */
    public void stopDrawing( int x, int y ) {        
        Pair<Position, java.awt.Point> tmp = correctPoint( x, y );
        Position pos = tmp.first;
        points.add( GeometryFactory.createPoint( pos, mapModel.getCoordinateSystem() ) );

        Point p = GeometryFactory.createPoint( pos, mapModel.getCoordinateSystem() );
        if ( !( p.equals( points.get( points.size() - 1 ) ) ) ) {
            points.add( p );
        }
    }

    /**
     * @param surface
     *            may be use by extending classes
     * @return A new GeneralPath with the points from this.points.
     */
    protected GeneralPath createPath( Surface surface ) {

        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        GeneralPath path = new GeneralPath();
        ListIterator<Point> i = points.listIterator( 0 );
        if ( i.hasNext() ) {
            Point p = (Point) i.next();
            float xx = (float) gt.getDestX( p.getX() );
            float yy = (float) gt.getDestY( p.getY() );
            path.moveTo( xx, yy );
            while ( i.hasNext() ) {
                p = (Point) i.next();
                xx = (float) gt.getDestX( p.getX() );
                yy = (float) gt.getDestY( p.getY() );
                path.lineTo( xx, yy );
            }
        }

        path.lineTo( currentX, currentY );
        path.closePath();

        return path;
    }

    /**
     * The getDrawnObject method only return the points given by the last user interaction.
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

}
