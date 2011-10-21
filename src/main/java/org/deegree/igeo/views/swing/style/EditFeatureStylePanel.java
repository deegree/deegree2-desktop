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
package org.deegree.igeo.views.swing.style;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;

import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.modules.EditStyleModule;
import org.deegree.igeo.state.mapstate.MapTool;
import org.deegree.igeo.views.DrawingPane;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class EditFeatureStylePanel extends JPanel {

    private static final long serialVersionUID = 2415338786149657730L;

    private ApplicationContainer<Container> appCont;

    private MapTool<Container> mapTool;

    private DrawingPane drawingPane;

    private MouseListener mouseListener;

    private MouseMoveListener mouseMotionListener;

    /**
     * 
     * @param owner
     */
    public EditFeatureStylePanel( EditStyleModule<Container> owner ) {            
        appCont = owner.getApplicationContainer();
        this.mapTool = appCont.getActiveMapModule().getMapTool();
        Container jco = appCont.getActiveMapModule().getMapContainer();
        jco.addMouseListener( mouseListener = new MouseListener() );
        jco.addMouseMotionListener( mouseMotionListener = new MouseMoveListener() );
        jco.add( this, 0 );
    }
    
    public void clear() {
        Container jco = appCont.getActiveMapModule().getMapContainer();
        jco.removeMouseListener( mouseListener );
        jco.removeMouseMotionListener( mouseMotionListener );
        drawingPane = null;
    }   

    @Override
    public void repaint() {
        // do not invoke superclass method
    }

    @Override
    public void paint( Graphics g ) {
        if ( drawingPane != null && drawingPane.isDrawing() ) {            
            Point point = drawingPane.getCurrent();
            drawingPane.draw( point.x, point.y, g );
        }
    }

    // ///////////////////////////////////////////////////////////////////////////////////////////
    // inner classes
    // ///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 
     * TODO add class documentation here
     * 
     * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    private class MouseListener extends MouseAdapter {

        @Override
        public void mousePressed( MouseEvent e ) {
            drawingPane = mapTool.getState().createDrawingPane( appCont.getViewPlatform(), getGraphics() );
            mapTool.getState().mousePressed( e );
        }

        @Override
        public void mouseReleased( MouseEvent e ) {
            mapTool.getState().mouseReleased( e );
        }
    }

    /**
     * 
     * TODO add class documentation here
     * 
     * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    private class MouseMoveListener extends MouseMotionAdapter {
        @Override
        public void mouseDragged( MouseEvent e ) {
            mapTool.getState().mouseDragged( e );
        }
    }

}
