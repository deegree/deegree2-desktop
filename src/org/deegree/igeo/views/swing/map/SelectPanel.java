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

package org.deegree.igeo.views.swing.map;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import static org.deegree.framework.log.LoggerFactory.getLogger;
import static org.deegree.model.spatialschema.GeometryFactory.createEnvelope;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;

import javax.swing.JPanel;

import org.deegree.framework.log.ILogger;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.SelectFeatureCommand;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.state.mapstate.MapTool;
import org.deegree.igeo.state.mapstate.SelectState;
import org.deegree.igeo.views.DrawingPane;
import org.deegree.igeo.views.GeoDrawingPane;
import org.deegree.model.Identifier;
import org.deegree.model.spatialschema.Envelope;

/**
 * <code>SelectPanel</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * @param <T>
 */
public class SelectPanel<T> extends JPanel implements MouseListener, MouseMotionListener {

    private static final long serialVersionUID = 7060223921766287736L;

    private static final ILogger LOG = getLogger( SelectPanel.class );

    private final MapTool<T> tool;

    private final ApplicationContainer<T> app;

    private Container parent;

    private double dragx0, dragy0;

    private DrawingPane pane;

    private int evx0, evx1, evy0, evy1;
    
    private IModule<T> owner; 

    /**
     * @param module
     * @param mapTool
     * @param parent
     */
    public SelectPanel( IModule<T> module, MapTool<T> mapTool, Container parent ) {
        this.owner = module;
        tool = mapTool;
        app = module.getApplicationContainer();         
        this.parent = parent;
        parent.removeMouseListener( this );
        parent.addMouseListener( this );
        parent.removeMouseMotionListener( this );
        parent.addMouseMotionListener( this );
        parent.remove( this );
        parent.add( this, 0 );        
    }

    @Override
    public void repaint() {
        // this is a dummy panel
    }

    @Override
    public void paint( Graphics g ) {
        if ( pane != null && pane.isDrawing() ) {
            pane.draw( evx1, evy1, g );
        }
    }

    public void mouseClicked( MouseEvent e ) {
     // nothing to do here 
    }

    public void mouseEntered( MouseEvent e ) {
        // nothing to do here
    }

    public void mouseExited( MouseEvent e ) {
        // nothing to do here
    }

    public void mousePressed( MouseEvent e ) {
        if ( tool.getState() instanceof SelectState && !e.isPopupTrigger() ) {
            String mmId = owner.getInitParameter( "assignedMapModel" );
            MapModel mm = app.getMapModel( new Identifier( mmId ) );
            GeoTransform trans = mm.getToTargetDeviceTransformation();
            evx0 = e.getX();
            evy0 = e.getY();
            dragx0 = trans.getSourceX( evx0 );
            dragy0 = trans.getSourceY( evy0 );
            ( (SelectState) tool.getState() ).setRectangleSelectState();
            pane = tool.getState().createDrawingPane( "Application", null );
            ( (GeoDrawingPane) pane ).setMapModel( mm ); // setting state while casting - yeah
        }
    }

    public void mouseReleased( MouseEvent e ) {
        if ( tool.getState() instanceof SelectState && e.getClickCount() != 2 ) {
            boolean additive = ( e.getModifiersEx() & CTRL_DOWN_MASK ) != 0;

            String mmId = owner.getInitParameter( "assignedMapModel" );
            MapModel mm = app.getMapModel( new Identifier( mmId ) );
            GeoTransform trans = mm.getToTargetDeviceTransformation();
            double dragx1 = trans.getSourceX( e.getX() );
            double dragy1 = trans.getSourceY( e.getY() );
            double minx = dragx1 < dragx0 ? dragx1 : dragx0;
            double miny = dragy1 < dragy0 ? dragy1 : dragy0;
            double maxx = dragx1 < dragx0 ? dragx0 : dragx1;
            double maxy = dragy1 < dragy0 ? dragy0 : dragy1;
            Envelope bbox = createEnvelope( minx, miny, maxx, maxy, mm.getCoordinateSystem() );

            // ensure minimum size of search bbox
            double d = mm.getToTargetDeviceTransformation().getSourceX( 0 );
            d = mm.getToTargetDeviceTransformation().getSourceX( 5 ) - d;
            if ( bbox.getWidth() < d ) {
                bbox = bbox.getBuffer( d );
            }
            
            HashSet<Layer> selected = new HashSet<Layer>( mm.getLayersSelectedForAction( MapModel.SELECTION_ACTION ) );
            for ( Layer l : selected ) {
                try {        
                    if ( l.isVisible() ) {
                        app.getCommandProcessor().executeSychronously( new SelectFeatureCommand( l, bbox, additive ), true );
                    }
                } catch ( Exception ex ) {
                    LOG.logError( ex.getMessage(), ex );
                }
            }

            pane.finishDrawing();
            pane = null;
        }
    }

    public void mouseDragged( MouseEvent e ) {
        if ( pane != null ) {
            pane.startDrawing( evx0, evy0 );
            evx1 = e.getX();
            evy1 = e.getY();
            parent.repaint();
        }
    }

    public void mouseMoved( MouseEvent e ) {
        // unused
    }

}
