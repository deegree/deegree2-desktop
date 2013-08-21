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
package org.deegree.desktop.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.modules.IModule;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.desktop.views.swing.ControlElement;
import org.deegree.desktop.views.swing.Footer;
import org.deegree.desktop.views.swing.actionlisteners.KeyListenerRegister;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.desktop.config.LayoutType;
import org.deegree.desktop.config.WindowType;
import org.deegree.desktop.config._AbstractViewFormType;
import org.deegree.kernel.ProcessMonitor;

/**
 * Main class for running iGeodesktop as a swing application
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class DeegreeDesktopPanel extends DeegreeDesktop {

    private static final long serialVersionUID = -8423037307095733192L;

    private static final ILogger LOG = LoggerFactory.getLogger( DeegreeDesktopPanel.class );

    private JPanel mainPanel;

    /**
     * @param processMonitor
     * 
     * 
     */
    public DeegreeDesktopPanel( ProcessMonitor processMonitor ) {
        super( processMonitor );
        mainPanel = new JPanel( new BorderLayout() );
        initProxy();
    }

    /**
     * 
     */
    public void init() {
        toolBarController = new ControlElement( "ToolBar" );
        footer = new Footer();
    }

    /**
     * adapts the toolbar to current container size
     */
    public void resizeToolbar() {
        Component[] comps = toolbarPanel.getComponents();
        int w = 0;
        for ( Component comp : comps ) {
            w += comp.getWidth();
        }
        if ( w > mainPanel.getWidth() * 2 - 30 ) {
            toolbarPanel.setPreferredSize( new Dimension( 100, 125 ) );
        } else if ( w > mainPanel.getWidth() - 20 ) {
            toolbarPanel.setPreferredSize( new Dimension( 100, 80 ) );
        } else {
            toolbarPanel.setPreferredSize( new Dimension( 100, 45 ) );
        }
        toolbarPanel.revalidate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.AbstractApplicationContainer#paint()
     */
    public void paint() {

        if ( this.proj != null ) {
            processMonitor.updateStatus( "initializing panel ..." );
            initPanel();

            _AbstractViewFormType vfc = this.proj.getView().getViewForm().get_AbstractViewForm().getValue();

            if ( modules.size() > 0 && modules.get( 0 ).getViewForm() instanceof JInternalFrame ) {
                // if at least one child module is an JInternalFrame the current content
                // pane must be a JDesktopPane
                this.mainPanel.setLayout( new BorderLayout() );
                rootTargetPane = new JDesktopPane();
                // rootTargetPane.setBackground( new Color( 145,129,98) );
                rootTargetPane.setBackground( new Color( 138, 127, 106 ) );
                this.mainPanel.add( rootTargetPane, BorderLayout.CENTER );
            } else {
                rootTargetPane = new JPanel( new BorderLayout() );
                this.mainPanel.add( rootTargetPane, BorderLayout.CENTER );
            }

            boolean hscb = vfc.isUseHorizontalScrollBar();
            boolean vscb = vfc.isUseVerticalScrollBar();

            addFooter();
            LayoutType layout = this.proj.getView().getViewForm().getLayout();
            rootTargetPane = setTargetLayout( layout, rootTargetPane, this.modules, hscb, vscb );

            addToolBarEntries();
            addPopupEntries();

            try {
                processMonitor.cancel();
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
            }
            appendModules( this.modules, rootTargetPane );
            SwingUtilities.updateComponentTreeUI( mainPanel );

            WindowType w = this.proj.getView().getWindow();
            if ( w.getWidth() > 0 ) {
                this.mainPanel.setSize( w.getWidth(), w.getHeight() );
                this.mainPanel.setPreferredSize( new Dimension( w.getWidth(), w.getHeight() ) );
                this.mainPanel.setLocation( w.getLeft(), w.getTop() );
                this.mainPanel.setVisible( true );
            } else {
                LOG.logInfo( "Application Container defined to be invisible" );
            }
            this.mainPanel.repaint();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.AbstractApplicationContainer#cleanUp()
     */
    public void cleanUp() {
        // TODO
        // cleanup all resources
        try {
            // if something has been changed use will be asked if changes shall be safed
            // before quitting the program
            if ( getCommandProcessor().availableUndos().size() > 0
                 && DialogFactory.openConfirmDialogYESNO( "application", mainPanel,
                                                          Messages.getMessage( mainPanel.getLocale(), "$DI10063" ),
                                                          Messages.getMessage( mainPanel.getLocale(), "$DI10064" ) ) ) {
                if ( isNew ) {
                    DeegreeDesktopEventHandler.saveProject( this );
                } else {
                    DeegreeDesktopEventHandler.saveProject( this, null );
                }
            }
        } catch ( Exception e ) {
            LOG.logWarning( "", e );
        }
    }

    private void addFooter() {
        if ( this.proj.getView().isHasFooter() ) {
            ( (JComponent) footer ).setMinimumSize( new Dimension( 100, 30 ) );
            ( (JComponent) footer ).setPreferredSize( new Dimension( 100, 30 ) );
            mainPanel.add( (JComponent) footer, BorderLayout.AFTER_LAST_LINE );
        }
    }

    private void addPopupEntries() {
        if ( popupEntries.size() > 0 ) {
            popup = new JPopupMenu();
            JMenuItem menuItem = new JMenuItem( "A popup menu item" );
            menuItem.addActionListener( this );
            popup.add( menuItem );
            popup.addSeparator();
            MouseListener popupListener = new PopupListener();
            mainPanel.addMouseListener( popupListener );
        }
    }

    private void initPanel() {

        // add listeners to store window position and size within current project
        mainPanel.addComponentListener( new ComponentAdapter() {

            @Override
            public void componentMoved( ComponentEvent event ) {
                WindowType wt = proj.getView().getWindow();
                wt.setLeft( (int) event.getComponent().getLocation().getX() );
                wt.setTop( (int) event.getComponent().getLocation().getY() );
            }

            @Override
            public void componentResized( ComponentEvent event ) {
                WindowType wt = proj.getView().getWindow();
                wt.setWidth( (int) event.getComponent().getWidth() );
                wt.setHeight( (int) event.getComponent().getHeight() );
                resizeToolbar();
            }

        } );

        this.mainPanel.setLocale( Locale.getDefault() );
        this.mainPanel.removeAll();
        this.mainPanel.setName( "iGeoDesktop" );

        KeyListenerRegister.registerDefaultKeyListener( mainPanel.getRootPane() );

        // initialize new list for tool bar
        toolbarButtons = new HashMap<String, List<AbstractButton>>();
        menuItems = new HashMap<String, AbstractButton>();
    }

    /**
     * add all toolBarEntries of each module and of the container to the toolbar
     */
    private void addToolBarEntries() {
        this.toolbarPanel = new JPanel( new FlowLayout( FlowLayout.LEADING ) );
        appendToolBar( null, this.toolbarEntries );
        for ( IModule<Container> module : this.modules ) {
            addToolBarEntries( module );
        }
        if ( proj.getView().getToolBar().get( 0 ).isVertical() ) {
            mainPanel.add( toolbarPanel, BorderLayout.WEST );
        } else {
            mainPanel.add( toolbarPanel, BorderLayout.BEFORE_FIRST_LINE );
        }

    }

    /**
     * 
     * @return iGeoDesktop as a {@link JPanel}
     */
    public JPanel getMainWndow() {
        return mainPanel;
    }

    

    // /////////////////////////////////////////////////////////////////////////////////
    // inner classes
    // /////////////////////////////////////////////////////////////////////////////////

    /**
     * 
     * The <code>IGeoDesktop</code> class TODO add class documentation here.
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * 
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     * 
     */
    class PopupListener extends MouseAdapter {

        @Override
        public void mousePressed( MouseEvent e ) {
            maybeShowPopup( e );
        }

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
