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
package org.deegree.igeo.views.swing;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;

import org.deegree.igeo.config.FrameViewFormType;
import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.ComponentPosition;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.views.IView;
import org.deegree.igeo.views.swing.util.PopUpRegister;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class DefaultFrame extends JFrame implements IView<Container>, WindowListener, WindowStateListener,
                                        ComponentListener {

    private static final long serialVersionUID = -4093281502719081186L;

    protected IModule<Container> owner;

    private JPopupMenu popup;

    /**
     * 
     * 
     */
    public DefaultFrame() {
        addWindowListener( this );
        addWindowStateListener( this );
        addComponentListener( this );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#init(org.deegree.client.presenter.state.ComponentStateAdapter,
     * org.deegree.client.configuration.ViewForm)
     */
    public void init( ViewFormType viewForm )
                            throws Exception {
        FrameViewFormType fvt = (FrameViewFormType) viewForm.get_AbstractViewForm().getValue();

        setResizable( fvt.isResizeable() );
        if ( !fvt.isClosable() ) {
            setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        }
        ComponentPosition compPos = this.owner.getComponentPositionAdapter();
        if ( compPos.hasWindow() ) {
            setLocation( compPos.getWindowLeft(), compPos.getWindowTop() );
            setSize( compPos.getWindowWidth(), compPos.getWindowHeight() );
        }
        setTitle( Messages.getMessage( getLocale(), fvt.getFrameTitle() ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#registerModule(org.deegree.client.application.modules.IModule)
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
     * Override this method if a PopUpContrller is required!
     */
    public ControlElement getPopUpController() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    public void windowActivated( WindowEvent e ) {
        this.owner.getComponentStateAdapter().setActive( true );

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
    public void windowDeactivated( WindowEvent e ) {
        this.owner.getComponentStateAdapter().setActive( false );
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
    public void windowOpened( WindowEvent e ) {
        this.owner.getComponentStateAdapter().setClosed( false );
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
    public void windowClosed( WindowEvent e ) {
        this.owner.getComponentStateAdapter().setClosed( true );
        this.owner.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
    public void windowDeiconified( WindowEvent e ) {
        // means minimized
        this.owner.getComponentStateAdapter().setMinimized( true );
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    public void windowIconified( WindowEvent e ) {
        // means 'normal' state
        this.owner.getComponentStateAdapter().setMinimized( false );
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    public void windowClosing( WindowEvent e ) {
        if ( getDefaultCloseOperation() != JFrame.DO_NOTHING_ON_CLOSE ) {
            this.owner.getComponentStateAdapter().setClosed( true );
            this.owner.clear();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowStateListener#windowStateChanged(java.awt.event.WindowEvent)
     */
    public void windowStateChanged( WindowEvent e ) {
        if ( e.getNewState() == Frame.MAXIMIZED_BOTH ) {
            this.owner.getComponentStateAdapter().setMaximized( true );
        } else if ( e.getNewState() == Frame.NORMAL ) {
            this.owner.getComponentStateAdapter().setNormal( true );
        }
    }

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
     * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
     */
    public void componentHidden( ComponentEvent e ) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
     */
    public void componentResized( ComponentEvent e ) {
        ComponentPosition compPos = this.owner.getComponentPositionAdapter();
        if ( compPos.hasWindow() ) {
            compPos.setWindowSize( (int) e.getComponent().getSize().getWidth(),
                                   (int) e.getComponent().getSize().getHeight() );
        }
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
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    class PopupListener extends MouseAdapter {

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
         */
        @Override
        public void mousePressed( MouseEvent e ) {
            maybeShowPopup( e );
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
         */
        @Override
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
