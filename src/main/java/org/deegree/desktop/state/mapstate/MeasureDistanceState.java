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

package org.deegree.desktop.state.mapstate;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.views.DrawingPane;
import org.deegree.desktop.views.GeoDrawingPane;
import org.deegree.desktop.views.swing.CursorRegistry;
import org.deegree.desktop.views.swing.drawingpanes.CreateLinestringDrawingPane;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.utils.MapTools;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class MeasureDistanceState extends AbstractMeasureState {
    
    private static final ILogger LOG = LoggerFactory.getLogger( MeasureDistanceState.class );

    /**
     * 
     * @param appContainer
     */
    public MeasureDistanceState( ApplicationContainer<?> appContainer ) {
        super( appContainer );
    }
    
    @Override
    public DrawingPane createDrawingPane( String platform, Graphics g ) {

        if ( "Application".equalsIgnoreCase( platform ) ) {
            drawingPane = new CreateLinestringDrawingPane( appContainer );
        } else if ( "Applet".equalsIgnoreCase( platform ) ) {
            drawingPane = new CreateLinestringDrawingPane( appContainer );
        } else {
            LOG.logWarning( "view platfroms other than Application and Applet are not supported yet" );
        }
        ( (GeoDrawingPane) drawingPane ).setMapModel( appContainer.getMapModel( null ) );
        drawingPane.setGraphicContext( g );
        return drawingPane;
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
                measureResultLabel = null;
                c.setCursor( CursorRegistry.DEFAULT_CURSOR );
            }
        } else {
            if ( drawingPane.isDrawing() ) {
                drawingPane.stopDrawing( p.x, p.y );
                if ( measureResultLabel != null ) {
                    measureResultLabel.addPoint( p.getX(), p.getY(), c.getWidth(), c.getHeight() );
                }
            } else {
                c.setCursor( CursorRegistry.RULER_CURSOR );
                drawingPane.startDrawing( p.x, p.y );
                if ( measureResultLabel != null ) {
                    measureResultLabel.setStartPoint( p.getX(), p.getY(), c.getWidth(), c.getHeight() );
                }
            }
        }
        c.repaint();
    }

}
