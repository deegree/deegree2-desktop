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
package org.deegree.desktop.views.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.deegree.desktop.modules.IModule;
import org.deegree.desktop.views.ComponentPosition;
import org.deegree.desktop.views.IView;
import org.deegree.desktop.views.swing.util.PopUpRegister;

/**
 * 
 * <code>DefaultPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public abstract class DefaultPanel extends JPanel implements IView<Container>, ComponentListener {

    private static final long serialVersionUID = 8289361001846473392L;

    protected IModule<Container> owner;

    protected JPopupMenu popup;

    /**
     * 
     * 
     */
    protected DefaultPanel() {
        addComponentListener( this );
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // IVIEW
    // /////////////////////////////////////////////////////////////////////////////////
    /**
     * @param module
     */
    public void registerModule( IModule<Container> module ) {
        this.owner = module;
        ControlElement popUpController = PopUpRegister.registerPopups( module.getApplicationContainer(), this, owner,
                                                                       null, new PopupListener() );
        popup = (PopUpMenu) popUpController.getView();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#update()
     */
    public void update() {

    }

    /**
     * 
     * @param action
     * @return menu item identified by assigned action
     */
    public JMenuItem getMenuItemByActionName( String action ) {
        Component[] components = popup.getComponents();
        for ( Component component : components ) {
            if ( component instanceof JMenuItem && action.equals( ( (JMenuItem) component ).getName() ) ) {
                return ( (JMenuItem) component );
            }
        }
        return null;
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // COMPONENTLISTENER
    // /////////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
     */
    public void componentMoved( ComponentEvent e ) {
        ComponentPosition compPos = this.owner.getComponentPositionAdapter();
        if ( compPos.hasWindow() ) {
            compPos.setWindowPosition( (int) e.getComponent().getLocation().getX(),
                                       (int) e.getComponent().getLocation().getY() );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    public void componentResized( ComponentEvent e ) {
        /*
         * ComponentPosition compPos = this.owner.getComponentPositionAdapter(); if ( compPos.hasWindow() ) {
         * compPos.setWindowSize( (int) e.getComponent().getSize().getWidth(), (int)
         * e.getComponent().getSize().getHeight() ); }
         */
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
     */
    public void componentHidden( ComponentEvent e ) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
     */
    public void componentShown( ComponentEvent e ) {
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // inner classe //
    // /////////////////////////////////////////////////////////////////////////////////

    /**
     * 
     * 
     * 
     * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     * 
     */
    class PopupListener extends MouseAdapter {
        public void mousePressed( MouseEvent e ) {
            maybeShowPopup( e );
        }

        public void mouseReleased( MouseEvent e ) {
            maybeShowPopup( e );
        }

        private void maybeShowPopup( MouseEvent e ) {

            if ( e.isPopupTrigger() ) {
                popup.show( e.getComponent(), e.getX(), e.getY() );
            }
        }
    }

}
