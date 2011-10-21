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

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.JPanel;

import org.deegree.igeo.modules.DefaultMapModule;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.state.mapstate.MapTool;
import org.deegree.igeo.views.DrawingPane;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class ZoomPanel extends JPanel implements MouseListener, MouseMotionListener {

    private static final long serialVersionUID = -4942021725028457262L;

    protected DrawingPane drawingPane;

    private IModule<Container> owner;

    private final MapTool<Container> mapTool;

    private boolean popupTrigger = false;

    /**
     * 
     * @param module
     * @param mapTool
     * @param parent
     */
    public ZoomPanel( IModule<Container> module, MapTool<Container> mapTool, Container parent ) {
        this.owner = module;
        this.mapTool = mapTool;
        parent.removeMouseListener( this );
        parent.addMouseListener( this );
        parent.removeMouseMotionListener( this );
        parent.addMouseMotionListener( this );
        parent.remove( this );
        parent.add( this, 0 );
    }

    @Override
    public void repaint() {
        // must be defined empty to avoid performing standard action
        // do not invoke super.repaint()!!!!!
    }

    @Override
    public void paint( Graphics g ) {
        super.paint( g );
        if ( drawingPane != null && drawingPane.isDrawing() ) {
            List<Point> points = drawingPane.getDrawnObject();
            drawingPane.draw( points.get( 1 ).x, points.get( 1 ).y, g );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked( MouseEvent event ) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered( MouseEvent event ) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited( MouseEvent event ) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed( MouseEvent event ) {
        if ( !( popupTrigger = event.isPopupTrigger() ) ) {
            drawingPane = mapTool.getState().createDrawingPane( "Application", getGraphics() );
            if ( drawingPane != null ) {
                drawingPane.startDrawing( event.getX(), event.getY() );
            }
            mapTool.getState().mousePressed( event );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased( MouseEvent event ) {

        // it seems that there are differences between the operating system if
        // event.isPopupTrigger()
        // returns true or false in mousePressed() or mouseReleased() method. So better test
        // if both return false!
        if ( !popupTrigger && !event.isPopupTrigger() ) {
            mapTool.getState().mouseReleased( event );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged( MouseEvent event ) {
        MapTool<?> mapTool = ( (DefaultMapModule<Container>) owner ).getMapTool();
        mapTool.getState().mouseDragged( event );
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved( MouseEvent event ) {
        super.processMouseMotionEvent( event );
    }

}
