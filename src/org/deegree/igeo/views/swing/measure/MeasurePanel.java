/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2007 by:
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

package org.deegree.igeo.views.swing.measure;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.state.mapstate.MapState;
import org.deegree.igeo.state.mapstate.MapTool;
import org.deegree.igeo.state.mapstate.MeasureAreaState;
import org.deegree.igeo.state.mapstate.MeasureDistanceState;
import org.deegree.igeo.views.DrawingPane;
import org.deegree.igeo.views.swing.map.DefaultMapComponent;

/**
 * The <code>JMeasurePanel</code> is a swing component to handle measurements.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class MeasurePanel extends JPanel {

    private static final long serialVersionUID = 7929275811832458305L;

    private static final ILogger LOG = LoggerFactory.getLogger( MeasurePanel.class );

    private MeasureResultLabel measureResultLabel;

    private IModule<Container> module;

    private MapTool<?> mapTool;

    private DrawingPane drawingPane;

    /**
     * 
     * @param module
     *            the module
     * @param assignedMapModel
     *            the mapModel
     * @param mapTool
     *            the mapTool representing the state of the mapModule
     */
    public MeasurePanel( IModule<Container> module, MapTool<?> mapTool, Container parent ) {
        this.module = module;
        this.mapTool = mapTool;

        if ( parent instanceof JFrame ) {
            JFrame viewForm = (JFrame) parent;
            Component[] components = viewForm.getContentPane().getComponents();
            for ( int i = 0; i < components.length; i++ ) {
                if ( components[i] instanceof DefaultMapComponent ) {
                    parent = (Container) components[i];
                    break;
                }
            }
            if ( parent == null ) {
                LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10072" ) );
                return;
            }
        } else if ( parent instanceof JInternalFrame ) {
            JInternalFrame viewForm = (JInternalFrame) parent;
            Component[] components = viewForm.getContentPane().getComponents();
            for ( int i = 0; i < components.length; i++ ) {
                if ( components[i] instanceof DefaultMapComponent ) {
                    parent = (Container) components[i];
                    break;
                }
            }
            if ( parent == null ) {
                LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10072" ) );
                return;
            }
        }

        Dimension d = new Dimension( 200, 20 );
        this.setPreferredSize( d );
        this.setVisible( true );

        MouseListener ml = new MouseListener();
        parent.addMouseListener( ml );
        parent.addMouseMotionListener( ml );
        parent.remove( this );
        parent.add( this, 0 );
    }

    @Override
    public void repaint() {
        // this is a dummy panel
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#paint(java.awt.Graphics)
     */
    @Override
    public void paintComponent( Graphics g ) {
        super.paintComponent( g );
        if ( drawingPane != null && drawingPane.isDrawing() ) {
            drawingPane.draw( drawingPane.getCurrent().x, drawingPane.getCurrent().y, g );
        }

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

    // /////////////////////////////////////////////////////////////////////////////
    // inner classes
    // /////////////////////////////////////////////////////////////////////////////

    private class MouseListener extends MouseAdapter implements MouseMotionListener {

        @Override
        public void mousePressed( MouseEvent e ) {
            if ( drawingPane == null ) {
                MapState ms = (MapState) mapTool.getState();
                if ( ms instanceof MeasureDistanceState ) {
                    ( (MeasureDistanceState) ms ).setMeasureResultLabel( measureResultLabel );
                } else if ( ms instanceof MeasureAreaState ) {
                    ( (MeasureAreaState) ms ).setMeasureResultLabel( measureResultLabel );
                }
                drawingPane = ms.createDrawingPane( module.getApplicationContainer().getViewPlatform(), getGraphics() );
            }
            mapTool.getState().mousePressed( e );
            if ( e.getClickCount() > 1 ) {
                drawingPane = null;
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
         */
        public void mouseMoved( MouseEvent e ) {
            if ( mapTool.getState() != null && drawingPane != null && drawingPane.isDrawing() ) {
                mapTool.getState().mouseMoved( e );
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
         */
        public void mouseDragged( MouseEvent e ) {

        }

    }

}
