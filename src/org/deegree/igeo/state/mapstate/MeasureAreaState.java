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

package org.deegree.igeo.state.mapstate;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import org.deegree.framework.utils.MapTools;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.views.DrawingPane;
import org.deegree.igeo.views.GeoDrawingPane;
import org.deegree.igeo.views.swing.CursorRegistry;
import org.deegree.igeo.views.swing.drawingpanes.CreatePolygonDrawingPane;
import org.deegree.igeo.views.swing.measure.MeasureResultLabel;
import org.deegree.kernel.Command;
import org.deegree.model.spatialschema.Point;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class MeasureAreaState extends MapState {

    private MeasureResultLabel measureResultLabel;

    /**
     * 
     * @param appContainer
     */
    public MeasureAreaState( ApplicationContainer<?> appContainer ) {
        super( appContainer );
    }

    @Override
    public Command createCommand( IModule<?> module, MapModel mapModel, Layer layer, Point... points ) {
        return null;
    }

    @Override
    public DrawingPane createDrawingPane( String platform, Graphics g ) {

        if ( "Application".equalsIgnoreCase( platform ) ) {
            drawingPane = new CreatePolygonDrawingPane( appContainer );
        } else if ( "Applet".equalsIgnoreCase( platform ) ) {
            drawingPane = new CreatePolygonDrawingPane( appContainer );
        } else if ( "Portlet".equalsIgnoreCase( platform ) ) {

        } else if ( "JSP/HTML".equalsIgnoreCase( platform ) ) {

        } else if ( "JWS".equalsIgnoreCase( platform ) ) {
            drawingPane = new CreatePolygonDrawingPane( appContainer );
        } else if ( "JME".equalsIgnoreCase( platform ) ) {

        }
        ( (GeoDrawingPane) drawingPane ).setMapModel( appContainer.getMapModel( null ) );
        drawingPane.setGraphicContext( g );
        return drawingPane;
    }

    /**
     * sets the label calculating and representing the result of the measurment
     * 
     * @param label
     *            the label
     */
    public void setMeasureResultLabel( MeasureResultLabel label ) {
        this.measureResultLabel = label;
    }

    @Override
    public void mouseMoved( MouseEvent event ) {
        if ( drawingPane != null && drawingPane.isDrawing() ) {
            Component c = event.getComponent();
            java.awt.Point p = MapTools.adjustPointToPanelSize( event.getPoint(), c.getWidth(), c.getHeight() );
            drawingPane.draw( p.x, p.y );
            if ( measureResultLabel != null ) {
                measureResultLabel.setCurrentPoint( p.getX(), p.getY(), c.getWidth(), c.getHeight() );
            }
            c.repaint();
        }
    }

    @Override
    public void mousePressed( MouseEvent event ) {

        Component c = event.getComponent();
        java.awt.Point p = MapTools.adjustPointToPanelSize( event.getPoint(), c.getWidth(), c.getHeight() );
        if ( event.getClickCount() > 1 ) {
            if ( drawingPane != null && drawingPane.isDrawing() ) {
                drawingPane.stopDrawing( p.x, p.y );
                drawingPane.finishDrawing();
                drawingPane = null;
                c.setCursor( CursorRegistry.DEFAULT_CURSOR );
            }
        } else {
            if ( drawingPane != null && drawingPane.isDrawing() ) {
                drawingPane.stopDrawing( p.x, p.y );
                if ( measureResultLabel != null ) {
                    measureResultLabel.addPoint( p.getX(), p.getY(), c.getWidth(), c.getHeight() );
                }
            } else {
                c.setCursor( CursorRegistry.RULER_CURSOR );
                if ( drawingPane != null ) {
                    drawingPane.startDrawing( p.x, p.y );
                }
                if ( measureResultLabel != null ) {
                    measureResultLabel.setStartPoint( p.getX(), p.getY(), c.getWidth(), c.getHeight() );
                }
            }
        }
        c.repaint();
    }

}
