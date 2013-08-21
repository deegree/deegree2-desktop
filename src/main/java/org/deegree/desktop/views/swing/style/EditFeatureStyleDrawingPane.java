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
package org.deegree.desktop.views.swing.style;

import java.util.LinkedList;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.views.swing.drawingpanes.SwingGeoDrawingPane;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public abstract class EditFeatureStyleDrawingPane extends SwingGeoDrawingPane {

    protected double minx, maxy, width, height, rotation, dx, dy;

    /**
     * @param appCont
     */
    public EditFeatureStyleDrawingPane( ApplicationContainer<?> appCont ) {
        super( appCont );
        mapModel = appCont.getMapModel( null );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.DrawingPane#startDrawing(int, int)
     */
    public void startDrawing( int x, int y ) {
        this.isDrawing = true;
        points = new LinkedList<Point>();
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        double xx = gt.getSourceX( x );
        double yy = gt.getSourceY( y );
        Point p = GeometryFactory.createPoint( xx, yy, mapModel.getCoordinateSystem() );
        points.add( p );
        // dummy point
        xx = gt.getSourceX( x + 1 );
        yy = gt.getSourceY( y + 1 );
        p = GeometryFactory.createPoint( xx, yy, mapModel.getCoordinateSystem() );
        points.add( p );
        this.draw( x, y );

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
        Point p = GeometryFactory.createPoint( xx, yy, mapModel.getCoordinateSystem() );
        points.set( 1, p );
    }

    /**
     * 
     * @param rotation
     */
    public void setRotation( double rotation ) {
        this.rotation = rotation;
    }

    /**
     * 
     * @param minx
     * @param maxy
     * @param width
     * @param height
     */
    public void setStringEnvelope( double minx, double maxy, double width, double height ) {
        this.minx = minx;
        this.maxy = maxy;
        this.width = width;
        this.height = height;
    }

    /**
     * 
     * @param dx
     * @param dy
     */
    public void setDisplacement( double dx, double dy ) {
        this.dx = dx;
        this.dy = dy;
    }

}
