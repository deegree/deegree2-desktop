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

package org.deegree.igeo.views.swing.drawingpanes;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.GeometryUtils;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.views.DrawingPane;
import org.deegree.igeo.views.GeoDrawingPane;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Surface;
import org.deegree.model.spatialschema.SurfacePatch;

/**
 * Specialized {@link DrawingPane} for swing environments (uses {@link Graphics2D} functions)
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public abstract class SwingGeoDrawingPane extends GeoDrawingPane {

    private static final ILogger LOG = LoggerFactory.getLogger( SwingGeoDrawingPane.class );

    /**
     * @param appCont
     */
    public SwingGeoDrawingPane( ApplicationContainer<?> appCont ) {
        super( appCont );
    }

    /**
     * draws a {@link Surface} to the passed graphic context using passed stroke, line- und fill color. If stroke or
     * lineColor is <code>null</code> no line will be drawn. If fillColor is <code>null</code> the polygon/surface will
     * not be filled
     * 
     * @param g
     * @param surface
     * @param stroke
     * @param lineColor
     * @param fillColor
     */
    protected void drawSurface( Graphics g, Surface surface, Stroke stroke, Color lineColor, Color fillColor ) {
        GeneralPath path = createPath( surface );
        if ( fillColor != null ) {
            g.setColor( fillColor );
            ( (Graphics2D) g ).fill( path );
        }
        if ( stroke != null && lineColor != null ) {
            ( (Graphics2D) g ).setStroke( stroke );
            g.setColor( lineColor );
            ( (Graphics2D) g ).draw( path );
        }
    }

    /**
     * draws a {@link Curve} to the passed graphic context using passed stroke and line-color.
     * 
     * @param g
     * @param curve
     * @param stroke
     * @param lineColor
     */
    protected void drawCurve( Graphics g, Curve curve, Stroke stroke, Color lineColor ) {
        GeneralPath path = createPath( curve );
        ( (Graphics2D) g ).setStroke( stroke );
        g.setColor( lineColor );
        ( (Graphics2D) g ).draw( path );
    }

    /**
     * draws a {@link Point} to the passed graphic context using passed stroke, line- und fill color. If stroke or
     * lineColor is <code>null</code> no line will be drawn. If fillColor is <code>null</code> the point will not be
     * filled
     * 
     * @param g
     * @param point
     * @param radius
     * @param stroke
     * @param lineColor
     * @param fillColor
     */
    protected void drawPoint( Graphics g, Point point, int radius, Stroke stroke, Color lineColor, Color fillColor ) {
        GeoTransform gt = this.mapModel.getToTargetDeviceTransformation();

        int xx = (int) Math.round( gt.getDestX( point.getX() ) );
        int yy = (int) Math.round( gt.getDestY( point.getY() ) );
        if ( fillColor != null ) {
            g.setColor( fillColor );
            g.fillOval( xx - radius, yy - radius, 2 * radius, 2 * radius );
        }
        if ( stroke != null && lineColor != null ) {
            g.setColor( lineColor );
            g.drawOval( xx - radius, yy - radius, 2 * radius, 2 * radius );
        }
    }

    /**
     * 
     * @param geom
     * @return {@link GeneralPath} for passed {@link Curve} (mapped to device size)
     */
    protected GeneralPath createPath( Curve geom ) {
        GeoTransform gt = this.mapModel.getToTargetDeviceTransformation();
        GeneralPath path = new GeneralPath();
        Position[] pos = null;
        try {
            pos = geom.getAsLineString().getPositions();
        } catch ( GeometryException e ) {
            // TODO
            LOG.logError( e.getMessage(), e );
            return path;
        }
        float xx = (float) gt.getDestX( pos[0].getX() );
        float yy = (float) gt.getDestY( pos[0].getY() );
        path.moveTo( xx, yy );
        for ( int i = 1; i < pos.length; i++ ) {
            float xx_ = (float) gt.getDestX( pos[i].getX() );
            float yy_ = (float) gt.getDestY( pos[i].getY() );
            if ( GeometryUtils.distance( xx, yy, xx_, yy_ ) > 5 || i == pos.length - 1 ) {
                path.lineTo( xx_, yy_ );
                xx = xx_;
                yy = yy_;
            }
        }

        return path;
    }

    /**
     * 
     * @param surface
     * @return
     * @return {@link GeneralPath} for passed {@link Surface} (mapped to device size)
     */
    protected GeneralPath createPath( Surface surface ) {

        GeneralPath path = new GeneralPath();

        SurfacePatch patch = null;
        try {
            patch = surface.getSurfacePatchAt( 0 );
        } catch ( GeometryException e ) {
            LOG.logError( e.getMessage(), e );
        }
        if ( patch == null ) {
            return null;
        }
        appendRingToPath( path, patch.getExteriorRing() );

        // do not draw inner rings because it is faster
        // Position[][] inner = patch.getInteriorRings();
        // if ( inner != null ) {
        // for ( int i = 0; i < inner.length; i++ ) {
        // appendRingToPath( path, inner[i] );
        // }
        // }

        return path;
    }

    private void appendRingToPath( GeneralPath path, Position[] ring ) {
        GeoTransform gt = this.mapModel.getToTargetDeviceTransformation();
        if ( ring.length == 0 ) {
            return;
        }

        Position p = gt.getDestPoint( ring[0] );
        float xx = (float) p.getX();
        float yy = (float) p.getY();
        path.moveTo( xx, yy );
        for ( int i = 1; i < ring.length; i++ ) {
            p = gt.getDestPoint( ring[i] );
            float xx_ = (float) p.getX();
            float yy_ = (float) p.getY();
            if ( GeometryUtils.distance( xx, yy, xx_, yy_ ) > 5 || i == ring.length - 1 ) {
                path.lineTo( xx_, yy_ );
                xx = xx_;
                yy = yy_;
            }
        }

    }

}
