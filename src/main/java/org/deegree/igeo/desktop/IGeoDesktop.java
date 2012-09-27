//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2012 by:
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

 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: info@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/
package org.deegree.igeo.desktop;

import static java.util.Locale.getDefault;
import static org.deegree.igeo.config.Util.convertIdentifier;
import static org.deegree.igeo.i18n.Messages.getMessage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.deegree.framework.keyboard.Key2Code;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.Pair;
import org.deegree.igeo.config.AcceleratorType;
import org.deegree.igeo.config.EntryValueType;
import org.deegree.igeo.config.FrameViewFormType;
import org.deegree.igeo.config.IdentifierType;
import org.deegree.igeo.config.LayoutType;
import org.deegree.igeo.config.MenuItemType;
import org.deegree.igeo.config.MenuType;
import org.deegree.igeo.config.Util;
import org.deegree.igeo.config.WindowType;
import org.deegree.igeo.config._AbstractViewFormType;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.modules.DefaultMapModule;
import org.deegree.igeo.modules.DefaultModuleGroup;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.modules.IModuleGroup;
import org.deegree.igeo.modules.ModuleException;
import org.deegree.igeo.views.ComponentPosition;
import org.deegree.igeo.views.ComponentPosition.BorderPosition;
import org.deegree.igeo.views.ComponentPosition.SplitterPosition;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.IFooterEntry;
import org.deegree.igeo.views.swing.ButtonGroup;
import org.deegree.igeo.views.swing.ControlElement;
import org.deegree.igeo.views.swing.Footer;
import org.deegree.igeo.views.swing.FooterEntry;
import org.deegree.igeo.views.swing.MenuBar;
import org.deegree.igeo.views.swing.SplashWindow;
import org.deegree.igeo.views.swing.actionlisteners.KeyListenerRegister;
import org.deegree.igeo.views.swing.util.PopUpRegister;
import org.deegree.kernel.Command;
import org.deegree.kernel.ProcessMonitor;
import org.deegree.model.Identifier;

import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.ExperienceBlue;

/**
 * Main class for running deegree desktop as a swing application
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class IGeoDesktop extends AbstractiGeoDesktop {

    private static final ILogger LOG = LoggerFactory.getLogger( IGeoDesktop.class );

    private static final String CONTEXT = "Application";

    // main window/container
    JFrame frame;

    /**
     * @param processMonitor
     * 
     * 
     */
    public IGeoDesktop( ProcessMonitor processMonitor ) {
        super( processMonitor );
        try {
            String manager = System.getProperty( "UIManager" );
            if ( manager != null ) {
                UIManager.setLookAndFeel( manager );
            } else {
                // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                UIManager.setLookAndFeel( "com.jgoodies.looks.plastic.Plastic3DLookAndFeel" );
                Plastic3DLookAndFeel laf = new Plastic3DLookAndFeel();
                ExperienceBlue eb = new ExperienceBlue();
                PlasticLookAndFeel.setPlasticTheme( eb );
                UIManager.setLookAndFeel( laf );
            }
        } catch ( Exception e ) {
            LOG.logError( "Could not load LookAndFeel: " + System.getProperty( "UIManager" ), e );
        }
    }

    /**
     * 
     */
    @Override
    public void init() {
        toolBarController = new ControlElement( "ToolBar" );
        menuBarController = new ControlElement( "MenuBar" );
        footer = new Footer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.ApplicationContainer#getViewPlatform()
     */
    @Override
    public String getViewPlatform() {
        return IGeoDesktop.CONTEXT;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.AbstractApplicationContainer#getLocale()
     */
    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }

    /**
     * 
     * 
     */
    @Override
    public void paint() {
        if ( this.proj != null ) {
            processMonitor.updateStatus( "initializing frame ..." );
            initFrame();

            _AbstractViewFormType vfc = this.proj.getView().getViewForm().get_AbstractViewForm().getValue();

            this.frame.setResizable( ( (FrameViewFormType) vfc ).isResizeable() );
            String name = ( (FrameViewFormType) vfc ).getFrameTitle();
            this.frame.setTitle( getFrameTitle( name ) );

            if ( modules.size() > 0 && modules.get( 0 ).getViewForm() instanceof JInternalFrame ) {
                // if at least one child module is an JInternalFrame the current content
                // pane must be replaced by a JDesktopPane
                this.frame.getContentPane().setLayout( new BorderLayout() );
                rootTargetPane = new JDesktopPane();
                // rootTargetPane.setBackground( new Color( 145,129,98) );
                rootTargetPane.setBackground( new Color( 138, 127, 106 ) );
                this.frame.getContentPane().add( rootTargetPane, BorderLayout.CENTER );
            } else {
                rootTargetPane = new JPanel( new BorderLayout() );
                this.frame.setContentPane( rootTargetPane );
            }

            boolean hscb = vfc.isUseHorizontalScrollBar();
            boolean vscb = vfc.isUseVerticalScrollBar();

            addFooter( this.frame.getContentPane() );
            LayoutType layout = this.proj.getView().getViewForm().getLayout();
            rootTargetPane = setTargetLayout( layout, rootTargetPane, this.modules, hscb, vscb );

            addMenuBarEntries();
            addToolBarEntries();
            addPopupEntries();

            try {
                processMonitor.cancel();
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
            }
            appendModules( this.modules, rootTargetPane );
            SwingUtilities.updateComponentTreeUI( frame );
            // frame.pack();

            WindowType w = this.proj.getView().getWindow();
            if ( w.getWidth() > 0 ) {
                this.frame.setSize( w.getWidth(), w.getHeight() );
                this.frame.setLocation( w.getLeft(), w.getTop() );
                this.frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                this.frame.addWindowListener( new WindowAdapter() {

                    @Override
                    public void windowClosing( WindowEvent e ) {
                        if ( e.getWindow().equals( frame ) ) {
                            cleanUp();
                        }
                    }

                } );
                this.frame.setVisible( true );
            } else {
                LOG.logInfo( "Application Container defined to be invisible" );
            }
            this.frame.repaint();
        }
    }

    private String getFrameTitle( String name ) {
        String file = this.projectURL.toExternalForm();
        int index = file.lastIndexOf( File.separator );
        if ( index > 0 )
            file = file.substring( index + 1, file.length() );
        return getMessage( getDefault(), name, file );
    }

    @Override
    public void cleanUp() {
        // TODO
        // cleanup all resources
        try {
            // if something has been changed use will be asked if changes shall be safed
            // before quitting the program
            if ( getCommandProcessor().availableUndos().size() > 0
                 && DialogFactory.openConfirmDialogYESNO( "application", frame,
                                                          Messages.getMessage( frame.getLocale(), "$DI10063" ),
                                                          Messages.getMessage( frame.getLocale(), "$DI10064" ) ) ) {
                if ( isNew ) {
                    IGeoDesktopEventHandler.saveProject( this );
                } else {
                    IGeoDesktopEventHandler.saveProject( this, null );
                }
            }
        } catch ( Exception e ) {
            LOG.logWarning( "", e );
        }
    }

    private void addFooter( Container targetPane ) {
        if ( this.proj.getView().isHasFooter() ) {
            ( (JComponent) footer ).setMinimumSize( new Dimension( 100, 30 ) );
            ( (JComponent) footer ).setPreferredSize( new Dimension( 100, 30 ) );
            targetPane.add( (JComponent) footer, BorderLayout.SOUTH );
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
            frame.addMouseListener( popupListener );
            menuBar.addMouseListener( popupListener );
        }

    }

    private void initFrame() {
        // remove all listeners and dispose current frame
        Frame[] frames = Frame.getFrames();
        for ( Frame frame : frames ) {
            WindowListener[] wl = frame.getWindowListeners();
            for ( WindowListener windowListener : wl ) {
                frame.removeWindowListener( windowListener );
            }
            ComponentListener[] cl = frame.getComponentListeners();
            for ( ComponentListener componentListener : cl ) {
                frame.removeComponentListener( componentListener );
            }
            frame.dispose();
        }
        frame = new JFrame();
        frame.addWindowListener( new WindowAdapter() {

            @Override
            public void windowActivated( WindowEvent e ) {
                // e.g. reorder windows
                // Frame[] frames = Frame.getFrames();
                // for ( Frame fr : frames ) {
                // if ( !frame.equals( fr ) ) {
                // fr.toFront();
                // }
                // }
                // frame.requestFocus();
            }
        } );
        // add listeners to store window position and size within current project
        frame.addComponentListener( new ComponentAdapter() {

            @Override
            public void componentMoved( ComponentEvent event ) {
                WindowType wt = proj.getView().getWindow();
                wt.setLeft( (int) event.getComponent().getLocation().getX() );
                wt.setTop( (int) event.getComponent().getLocation().getY() );
            }

            @Override
            public void componentResized( ComponentEvent event ) {
                WindowType wt = proj.getView().getWindow();
                wt.setWidth( event.getComponent().getWidth() );
                wt.setHeight( event.getComponent().getHeight() );
                resizeToolbar();
            }

        } );
        // add listeners that ensures that all frames assigned to a project will be (de-)iconified
        // if main window/frame will be (de-)iconified
        frame.addWindowListener( new WindowAdapter() {
            @Override
            public void windowIconified( WindowEvent e ) {
                Frame[] frames = Frame.getFrames();
                for ( Frame frame : frames ) {
                    frame.setState( Frame.ICONIFIED );
                }
            }

            @Override
            public void windowDeiconified( WindowEvent e ) {
                Frame[] frames = Frame.getFrames();
                for ( Frame frame : frames ) {
                    frame.setState( Frame.NORMAL );
                }
            }
        } );
        Component[] comps = this.frame.getContentPane().getComponents();
        for ( Component component : comps ) {
            this.frame.getContentPane().remove( component );
        }
        this.frame.setLocale( Locale.getDefault() );
        this.frame.getContentPane().removeAll();
        this.frame.setName( "iGeoDesktop" );

        this.menuBar = (MenuBar) menuBarController.getView();
        this.menuBar.putClientProperty( Options.HEADER_STYLE_KEY, HeaderStyle.BOTH );
        this.menuBar.removeAll();

        this.menuBar.setVisible( true );
        this.frame.setJMenuBar( this.menuBar );
        KeyListenerRegister.registerDefaultKeyListener( frame.getRootPane() );

        // initialize new list for tool bar and menu buttons
        toolbarButtons = new HashMap<String, List<AbstractButton>>();
        menuItems = new HashMap<String, AbstractButton>();
        System.gc();
    }

    @Override
    public void registerKeyboardAction( ActionListener actionListener, KeyStroke keyStroke ) {
        this.frame.getRootPane().registerKeyboardAction( actionListener, keyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW );
    }

    /**
     * appends Modules onto the passed contains
     * 
     * @param targetPane
     */
    @Override
    public void appendModules( List<IModule<Container>> modules, Container targetPane ) {

        for ( IModule<Container> module : modules ) {
            module.setGUIContainer( targetPane );
            Object view = module.getViewForm();
            if ( module.getComponentPositionAdapter().getHeaderPosition() > -1 ) {
                JComponent toolBar = (JComponent) toolBarController.getView();
                if ( toolBar != null ) {
                    int index = module.getComponentPositionAdapter().getHeaderPosition();
                    if ( index > toolBar.getComponentCount() ) {
                        index = toolBar.getComponentCount();
                    }
                    // components located in tool bar shall have a transparent background
                    // to keep look&feel of the tool bar
                    ( (JComponent) view ).setBackground( new Color( 0, 0, 0, 0 ) );
                    toolBar.add( (JComponent) view, index );
                }
            } else if ( module.getComponentPositionAdapter().getFooterPosition() > -1 ) {
                if ( getFooter() != null ) {
                    IFooterEntry fe = new FooterEntry( module.getName(), (JComponent) view );
                    getFooter().addEntry( fe, module.getComponentPositionAdapter().getFooterPosition() );
                }
            } else {
                if ( view instanceof JFrame ) {
                    ( (JFrame) view ).setVisible( true );
                } else if ( view instanceof JInternalFrame ) {
                    ( (JInternalFrame) view ).setVisible( true );
                    if ( targetPane instanceof JTabbedPane ) {
                        throw new ModuleException( Messages.getMessage( Locale.getDefault(), "$DG10001",
                                                                        module.getIdentifier() ) );
                    } else if ( targetPane instanceof JSplitPane ) {
                        throw new ModuleException( Messages.getMessage( Locale.getDefault(), "$DG10002",
                                                                        module.getIdentifier() ) );
                    }
                    targetPane.add( (JInternalFrame) view );
                } else if ( view instanceof JComponent ) {
                    JComponent c = (JComponent) view;
                    if ( targetPane instanceof JTabbedPane ) {
                        ( (JTabbedPane) targetPane ).addTab( module.getName(), c );
                    } else if ( targetPane instanceof JSplitPane ) {
                        SplitterPosition type = module.getComponentPositionAdapter().getSplitterPosition();
                        switch ( type ) {
                        case BOTTOM:
                            ( (JSplitPane) targetPane ).add( c, JSplitPane.BOTTOM );
                            break;
                        case TOP:
                            ( (JSplitPane) targetPane ).add( c, JSplitPane.TOP );
                            break;
                        case LEFT:
                            ( (JSplitPane) targetPane ).add( c, JSplitPane.LEFT );
                            break;
                        case RIGHT:
                            ( (JSplitPane) targetPane ).add( c, JSplitPane.RIGHT );
                            break;
                        }
                    } else if ( targetPane.getLayout() instanceof BorderLayout ) {
                        BorderPosition pos = module.getComponentPositionAdapter().getBorderPosition();
                        switch ( pos ) {
                        case CENTER:
                            targetPane.add( c, BorderLayout.CENTER );
                            break;
                        case NORTH:
                            targetPane.add( c, BorderLayout.NORTH );
                            break;
                        case SOUTH:
                            targetPane.add( c, BorderLayout.SOUTH );
                            break;
                        case WEST:
                            targetPane.add( c, BorderLayout.WEST );
                            break;
                        case EAST:
                            targetPane.add( c, BorderLayout.EAST );
                            break;
                        }
                    } else if ( targetPane.getLayout() instanceof BoxLayout ) {
                        ComponentPosition cpa = module.getComponentPositionAdapter();
                        int col = cpa.getGridColumn();
                        int row = cpa.getGridRow();
                        ( (GridPanel) targetPane ).add( c, row, col );
                    } else {
                        targetPane.add( c );
                    }
                }

                if ( module instanceof DefaultModuleGroup<?> ) {
                    // recursion on sub modules if current module is a module group
                    Container tp = (Container) view;
                    List<IModule<Container>> children = ( (DefaultModuleGroup<Container>) module ).getChildModules();
                    LayoutType layout = module.getLayout();
                    tp = setTargetLayout( layout, tp, children, module.useHorizontalScrollbar(),
                                          module.useVerticalScrollbar() );
                    appendModules( children, tp );
                }
            }
        }
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
            frame.add( toolbarPanel, BorderLayout.WEST );
        } else {
            frame.add( toolbarPanel, BorderLayout.BEFORE_FIRST_LINE );
        }

    }

    /**
     * add entries (buttons) to the toolbar assigned to one specific module and its children
     * 
     * @param module
     */
    @Override
    public void addToolBarEntries( IModule<Container> module ) {
        if ( module instanceof IModuleGroup<?> ) {
            for ( IModule<Container> childModule : ( (IModuleGroup<Container>) module ).getChildModules() ) {
                addToolBarEntries( childModule );
            }
        } else {
            String key = module.getIdentifier().getValue();
            if ( module instanceof DefaultMapModule<?> ) {
                key = module.getClass().getName();
            }
            if ( toolbarButtons.containsKey( key ) ) {
                // if already a module of current type has been initialized avoid adding
                // same buttons twice; use already existing buttons and assign current
                // module as listener
                List<AbstractButton> list = toolbarButtons.get( key );
                for ( AbstractButton button : list ) {
                    button.addActionListener( module );
                }
            } else {
                appendToolBar( module, module.getToolBarEntries() );
            }
        }
    }

    /**
     * appends entries into the menu bar
     * 
     */
    private void addMenuBarEntries() {
        if ( getMenuBar() != null ) {
            addModuleMenus( getMenuBar().getMenu() );

            JMenu menu = new JMenu( Messages.getMessage( frame.getLocale(), "$DI10053" ) );
            menu.setMnemonic( KeyEvent.VK_H );
            JMenuItem item = new JMenuItem( Messages.getMessage( frame.getLocale(), "$DI10054" ) );
            addIcon( "/org/deegree/igeo/views/images/help.png", item, 15 );
            item.setName( "iGeoDesktop:open help" );
            item.addActionListener( this );
            item.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F1, 0 ) );
            // item.setMnemonic( KeyEvent.VK_O );
            menu.add( item );

            item = new JMenuItem( Messages.getMessage( frame.getLocale(), "$DI10055" ) );
            item.setName( "iGeoDesktop:online help" );
            item.addActionListener( this );
            // item.setMnemonic( KeyEvent.VK_L );

            menu.add( item );
            menu.addSeparator();
            item = new JMenuItem( Messages.getMessage( frame.getLocale(), "$DI10056" ) );
            item.setName( "iGeoDesktop:about" );
            item.addActionListener( this );
            item.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_A, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK ) );
            // item.setMnemonic( KeyEvent.VK_A );
            menu.add( item );
            this.menuBar.add( menu );
        }
    }

    private void addModuleMenus( Collection<MenuType> projectMenus ) {

        MenuNode root = new MenuNode( null, null );

        LinkedList<Pair<IModule<Container>, MenuType>> menus = new LinkedList<Pair<IModule<Container>, MenuType>>();

        for ( MenuType menu : projectMenus ) {
            menus.add( new Pair<IModule<Container>, MenuType>( null, menu ) );
        }

        // first run to find all menus
        List<IModule<Container>> modules = getModules();
        for ( IModule<Container> module : modules ) {
            for ( MenuType menu : module.getMenus() ) {
                menus.add( new Pair<IModule<Container>, MenuType>( module, menu ) );
            }
        }

        constructMenuTree( root, menus );

        buildJMenus( null, root );

    }

    private void buildJMenus( JMenu parent, MenuNode node ) {
        JMenu menu = null;
        if ( node.menu != null ) {
            String title = getMessage( getDefault(), node.menu.getName() );
            menu = new JMenu( title );
            setMenuAttributes( node.menu, menu );
            menu.setVisible( true );
            menu.setName( convertIdentifier( node.menu.getIdentifier() ).getAsQualifiedString() );
            addIcon( node.menu.getIcon(), menu, 15 );
            if ( parent == null ) {
                menuBar.add( menu );
                if ( node.module == null ) {
                    appendChildren( this, node.menu, menu );
                } else {
                    appendChildren( node.module, node.menu, menu );
                }
            } else {
                // parent.addSeparator();
                appendChildren( node.module, node.menu, parent );
            }
        }

        for ( MenuNode child : node.children ) {
            buildJMenus( menu, child );
        }
    }

    private void constructMenuTree( MenuNode node, Collection<Pair<IModule<Container>, MenuType>> children ) {
        for ( Pair<IModule<Container>, MenuType> child : children ) {
            IdentifierType parent = child.second.getParent();
            MenuType menu = node.menu;
            if ( ( menu == null && parent == null )
                 || ( menu != null && parent != null && parent.getValue().equals( menu.getIdentifier().getValue() ) ) ) {
                MenuNode newNode = new MenuNode( child.second, child.first );
                node.children.add( newNode );
                constructMenuTree( newNode, children );
            }
        }
    }

    /**
     * appends entries to an already existing menu item
     * 
     * @param root
     * @param menu
     */

    @SuppressWarnings("unchecked")
    private void appendChildren( ActionListener listener, MenuType root, JMenu menu ) {

        try {
            List<Object> list = root.getMenuItemOrMenu();
            for ( Object object : list ) {
                if ( object instanceof MenuType ) {
                    MenuType mt = (MenuType) object;
                    if ( !menuItems.containsKey( listener.getClass().getName() + "|" + mt.getName() ) ) {
                        String title = Messages.getMessage( Locale.getDefault(), mt.getName() );
                        JMenu item = new JMenu( title );
                        item.setName( Util.convertIdentifier( mt.getIdentifier() ).getAsQualifiedString() );
                        setMenuAttributes( mt, item );
                        addIcon( mt.getIcon(), item, 15 );
                        menu.add( item );
                        appendChildren( listener, mt, item );
                        // store item assign with an action to avoid initializing it twice
                        menuItems.put( listener.getClass().getName() + "|" + mt.getName(), item );
                    }
                } else {
                    MenuItemType menuItem = (MenuItemType) object;
                    if ( !menuItems.containsKey( listener.getClass().getName() + "|" + menuItem.getAssignedAction() ) ) {
                        String title = Messages.getMessage( Locale.getDefault(), menuItem.getName() );
                        JMenuItem item = null;
                        String assignedGroup = menuItem.getAssignedGroup();
                        // set simple menuItem as default
                        if ( menuItem.getName().equals( "-" ) ) {
                            menu.addSeparator();
                            return;
                        } else if ( menuItem.getEntryType() == null
                                    || menuItem.getEntryType() == EntryValueType.SIMPLE_ITEM ) {
                            item = new JMenuItem( title );
                        } else if ( menuItem.getEntryType() == EntryValueType.RADIO_BUTTON_ITEM ) {
                            item = new JRadioButtonMenuItem( title );
                            URL url = PopUpRegister.class.getResource( "/org/deegree/igeo/views/images/radiobutton_unselected.gif" );
                            item.setIcon( new ImageIcon( url ) );
                            url = PopUpRegister.class.getResource( "/org/deegree/igeo/views/images/radiobutton_selected.gif" );
                            item.setSelectedIcon( new ImageIcon( url ) );
                        } else if ( menuItem.getEntryType() == EntryValueType.CHECK_BOX_ITEM ) {
                            item = new JCheckBoxMenuItem( title );
                            URL url = PopUpRegister.class.getResource( "/org/deegree/igeo/views/images/checkbox_unselected.gif" );
                            item.setIcon( new ImageIcon( url ) );
                            url = PopUpRegister.class.getResource( "/org/deegree/igeo/views/images/checkbox_selected.gif" );
                            item.setSelectedIcon( new ImageIcon( url ) );
                        } else {
                        	// item is null.
                        	return;
                        }
                        item.setName( menuItem.getAssignedAction() );
                        if ( listener instanceof IModule ) {
                            // register component for a module and action. This will be used
                            // to synchronize state of different components responsible for
                            // the same action
                            registerAction( ( (IModule<Container>) listener ).getIdentifier(),
                                            menuItem.getAssignedAction(), item );
                        }
                        setMenuAttributes( menuItem, item );
                        addIcon( menuItem.getIcon(), item, 15 );
                        menu.add( item );

                        if ( assignedGroup != null && assignedGroup.length() > 0
                             && item instanceof JRadioButtonMenuItem ) {
                            ButtonGroup bgp = btGroups.get( assignedGroup );
                            if ( bgp == null ) {
                                bgp = new ButtonGroup();
                                btGroups.put( assignedGroup, bgp );
                            }
                            bgp.add( item );
                        }
                        // menu bar entry belongs to main application
                        item.addActionListener( listener );
                        // store item assign with an action to avoid initializing it twice
                        menuItems.put( listener.getClass().getName() + "|" + menuItem.getAssignedAction(), item );
                    } else {
                        AbstractButton ab = menuItems.get( listener.getClass().getName() + "|"
                                                           + menuItem.getAssignedAction() );
                        ab.addActionListener( listener );
                    }
                }
            }

        } catch ( Exception e ) {
            LOG.logWarning( e.getMessage(), e );
        }

    }

    private void setMenuAttributes( MenuType entry, JMenuItem item ) {
        if ( entry.getTooltip() != null ) {
            item.setToolTipText( Messages.getMessage( getLocale(), entry.getTooltip() ) );
        }
        if ( entry.getAccelerator() != null ) {
            AcceleratorType at = entry.getAccelerator();
            KeyStroke ks = KeyStroke.getKeyStroke( Key2Code.getKeyCode( at.getMnemonic().toString() ),
                                                   Key2Code.getMaskCode( at.getMask().toString() ), false );
            item.setAccelerator( ks );
        }
        if ( entry.getMnemonic() != null ) {
            String mt = entry.getMnemonic();
            item.setMnemonic( Key2Code.getKeyCode( mt ) );
        }
    }

    private void setMenuAttributes( MenuItemType entry, JMenuItem item ) {
        if ( entry.getTooltip() != null ) {
            item.setToolTipText( Messages.getMessage( getLocale(), entry.getTooltip() ) );
        }
        if ( entry.getAccelerator() != null ) {
            AcceleratorType at = entry.getAccelerator();
            KeyStroke ks = KeyStroke.getKeyStroke( Key2Code.getKeyCode( at.getMnemonic().toString() ),
                                                   Key2Code.getMaskCode( at.getMask().toString() ), false );
            item.setAccelerator( ks );
        }
        if ( entry.getMnemonic() != null ) {
            String mt = entry.getMnemonic();
            item.setMnemonic( Key2Code.getKeyCode( mt ) );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed( ActionEvent event ) {
        IGeoDesktopEventHandler.actionPerformed( this, event );
    }

    /**
     * performs a login
     * 
     * @param user
     * @param password
     */
    @Override
    public void login( String user, String password ) {

        if ( user == null || user.length() < 3 ) {
            DialogFactory.openWarningDialog( "application", frame,
                                             Messages.getMessage( frame.getLocale(), "$MD10874" ),
                                             Messages.getMessage( frame.getLocale(), "$MD10875" ) );
            return;
        }
        this.user = user;
        this.password = password;

        Command command = null;
        Class<?> clzz = null;
        String method = null;
        try {
            // method contains full name of command to be performed to authenticate a user
            // this may be a dummy if server side security component uses IP address of a
            // client for authentication
            method = settings.getSecurityOptions().getAuthenticationMethod( null ).trim();
            clzz = Class.forName( method );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openWarningDialog( "application", frame,
                                             Messages.getMessage( frame.getLocale(), "$MD10884", method ),
                                             Messages.getMessage( frame.getLocale(), "$MD10885" ) );
            return;
        }
        URL url = null;
        try {
            String authServer = settings.getSecurityOptions().getAuthenticationServer( null );
            if ( authServer != null ) {
                url = new URL( authServer );
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openWarningDialog( "application", frame,
                                             Messages.getMessage( frame.getLocale(), "$MD10876" ),
                                             Messages.getMessage( frame.getLocale(), "$MD10877" ) );
            return;
        }
        try {
            Class<?>[] types = new Class[] { String.class, String.class, URL.class };
            Object[] values = new Object[] { user, password, url };
            Constructor<?> construtctor = clzz.getConstructor( types );
            command = (Command) construtctor.newInstance( values );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openWarningDialog( "application", frame,
                                             Messages.getMessage( frame.getLocale(), "$MD10886", method ),
                                             Messages.getMessage( frame.getLocale(), "$MD10887" ) );
            return;
        }
        try {
            getCommandProcessor().executeSychronously( command, false );
            if ( command.getResult() instanceof String ) {
                // command that returns a certificate has been performed
                String certificate = (String) command.getResult();
                certificates.put( "default", certificate );
            } else
            // command that just says if a user/password combination is valid has been performed
            if ( command.getResult() instanceof Boolean && ( (Boolean) command.getResult() ) == false ) {
                // user could not be authenticated using his user/password
                DialogFactory.openWarningDialog( "application", frame,
                                                 Messages.getMessage( frame.getLocale(), "$MD10882" ),
                                                 Messages.getMessage( frame.getLocale(), "$MD10883" ) );
                return;
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openWarningDialog( "application", frame,
                                             Messages.getMessage( frame.getLocale(), "$MD10878" ),
                                             Messages.getMessage( frame.getLocale(), "$MD10879" ) );
            return;
        }
        DialogFactory.openInformationDialog( "application", frame,
                                             Messages.getMessage( frame.getLocale(), "$MD10880" ),
                                             Messages.getMessage( frame.getLocale(), "$MD10881" ) );

    }

    @Override
    public void selectComponentForAction( Identifier module, String action, boolean select ) {
        Map<String, List<Object>> map = actionMap.get( module );
        List<Object> list = null;
        if ( map != null ) {
            list = map.get( action );
        }
        if ( list != null ) {
            for ( Object object : list ) {
                ( (AbstractButton) object ).setSelected( select );
            }
        }
    }

    /**
     * 
     * @return main window of the application
     */
    @Override
    public Container getMainWndow() {
        return frame;
    }

    /**
     * adapts the toolbar to current container size
     */
    @Override
    public void resizeToolbar() {
        Component[] comps = toolbarPanel.getComponents();
        int w = 0;
        for ( Component comp : comps ) {
            w += comp.getWidth();
        }
        if ( w > frame.getWidth() * 2 - 30 ) {
            toolbarPanel.setPreferredSize( new Dimension( 100, 125 ) );
        } else if ( w > frame.getWidth() - 20 ) {
            toolbarPanel.setPreferredSize( new Dimension( 100, 80 ) );
        } else {
            toolbarPanel.setPreferredSize( new Dimension( 100, 45 ) );
        }
        toolbarPanel.revalidate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.ApplicationContainer#resetToolbar()
     */
    @Override
    public void resetToolbar() {

        Collection<ButtonGroup> gr = btGroups.values();
        for ( ButtonGroup buttonGroup : gr ) {
            buttonGroup.removeSelection();
        }

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
    class MenuNode {
        MenuType menu;

        IModule<Container> module;

        LinkedList<MenuNode> children = new LinkedList<MenuNode>();

        MenuNode( MenuType menu, IModule<Container> module ) {
            this.menu = menu;
            this.module = module;
        }

        @Override
        public String toString() {
            String str = "["
                         + ( menu == null ? "(null)" : ( menu.getName() + "(" + menu.getIdentifier().getValue() + ")" ) )
                         + " " + children + "]";
            return str;
        }
    }

    /**
     * @param args
     */
    public static void main( String[] args ) {
        initProxy();

        SplashWindow spw = null;
        try {
            JLabel label = new JLabel( new ImageIcon( IGeoDesktop.class.getResource( "igeodesktop.jpg" ) ) );
            label.setSize( 420, 303 );
            spw = new SplashWindow( label );
            spw.setVisible( true );
            IGeoDesktop g = new IGeoDesktop( spw );
            URL url = null;
            if ( args == null || args.length == 0 ) {
                url = IGeoDesktop.class.getResource( "/default.prj" );
            } else {
                if ( args[0].startsWith( "http://" ) ) {
                    url = new URL( args[0] );
                } else {
                    url = new File( args[0] ).toURI().toURL();
                }
            }
            g.init();
            g.getCommandProcessor().clear();
            g.loadProject( url, false );
            g.paint();
            // opens a login dialog if system property 'autoLogin' is set to 'true'
            if ( "true".equalsIgnoreCase( System.getProperty( "autoLogin" ) ) ) {
                new LoginDialog( g );
            }
        } catch ( Exception e ) {
            spw.dispose();
            String msg = e.getMessage();
            if ( msg == null || msg.trim().length() == 0 ) {
                msg = e.getClass().getName();
            }
            DialogFactory.openErrorDialog( "Application", null, "Error starting iGeoDesktop", msg, e );
            LOG.logError( e );
        }
    }

}
