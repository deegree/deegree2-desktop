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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.utils.MapTools;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.ObjectInfoCommand;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.DefaultMapModule;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.views.DrawingPane;
import org.deegree.igeo.views.swing.drawingpanes.RectangleDrawingPane;
import org.deegree.kernel.Command;
import org.deegree.model.Identifier;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class InfoState extends MapState {

    private static final ILogger LOG = LoggerFactory.getLogger( InfoState.class );

    /**
     * @param appContainer
     * @param assignedAction
     */
    public InfoState( ApplicationContainer<?> appContainer, String assignedAction ) {
        super( appContainer, assignedAction );
    }

    @Override
    public Command createCommand( IModule<?> module, MapModel mapModel, Layer layer, Point... points ) {
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        double x1 = gt.getSourceX( points[0].getX() );
        double y1 = gt.getSourceY( points[1].getY() );
        double x2 = gt.getSourceX( points[1].getX() );
        double y2 = gt.getSourceY( points[0].getY() );
        Envelope bbox = GeometryFactory.createEnvelope( x1, y1, x2, y2, points[0].getCoordinateSystem() );
        return new ObjectInfoCommand( mapModel, bbox );
    }

    @Override
    public DrawingPane createDrawingPane( String platform, Graphics g ) {
        if ( "Application".equalsIgnoreCase( platform ) ) {
            drawingPane = new RectangleDrawingPane();
        } else if ( "Applet".equalsIgnoreCase( platform ) ) {
            drawingPane = new RectangleDrawingPane();
        } else {
            LOG.logWarning( "view platfroms other than Application and Applet are not supported yet" );
        }
        drawingPane.setGraphicContext( g );
        return drawingPane;
    }

    @Override
    public void mouseDragged( MouseEvent event ) {
        if ( drawingPane != null && drawingPane.isDrawing() ) {
            DefaultMapModule<?> owner = appContainer.getActiveMapModule();
            owner.update();
            Component c = (Component) event.getSource();
            java.awt.Point p = MapTools.adjustPointToPanelSize( event.getPoint(), c.getWidth(), c.getHeight() );
            drawingPane.stopDrawing( p.x, p.y );
        }
    }

    @Override
    public void mousePressed( MouseEvent event ) {
        if ( event.isPopupTrigger() ) {
            return;
        }
        if ( drawingPane != null ) {
            drawingPane.startDrawing( event.getX(), event.getY() );
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void mouseReleased( MouseEvent event ) {
        if ( event.isPopupTrigger() ) {
            return;
        }
        Component c = (Component) event.getSource();
        org.deegree.model.spatialschema.Point[] points;
        java.awt.Point p = MapTools.adjustPointToPanelSize( event.getPoint(), c.getWidth(), c.getHeight() );
        drawingPane.stopDrawing( p.x, p.y );
        drawingPane.finishDrawing();
        List<java.awt.Point> drawnPoints = drawingPane.getDrawnObject();
        points = new org.deegree.model.spatialschema.Point[drawnPoints.size()];
        for ( int i = 0; i < points.length; i++ ) {
            points[i] = GeometryFactory.createPoint( drawnPoints.get( i ).x, drawnPoints.get( i ).y, null );
        }
        if ( points.length == 2 && points[0].equals( points[1] ) ) {
            // ensure that box will created
            points[0] = GeometryFactory.createPoint( event.getX() - 2, event.getY() - 2, null );
            points[1] = GeometryFactory.createPoint( event.getX() + 2, event.getY() + 2, null );
        }

        DefaultMapModule<?> owner = appContainer.getActiveMapModule();
        owner.update();
        Command command = createCommand( owner, appContainer.getMapModel( null ), null, points );
        if ( command != null ) {
            try {
                appContainer.getCommandProcessor().executeSychronously( command, true );
                FeatureCollection selectedFeatures = ( (List<FeatureCollection>) command.getResult() ).get( 0 );
                Layer layer = appContainer.getMapModel( null ).getLayersSelectedForAction( MapModel.SELECTION_ACTION ).get(
                                                                                                                            0 );
                Iterator<Feature> it = selectedFeatures.iterator();
                List<Identifier> list = new ArrayList<Identifier>();
                while ( it.hasNext() ) {
                    Feature feature = (Feature) it.next();
                    list.add( new Identifier( feature.getId() ) );
                }
                layer.selectFeatures( list, true );
                owner.update();
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
            }
        } else {
            LOG.logWarning( "no command registered for: " + getClass().getName() );
        }
        c.getParent().repaint();
    }
}
