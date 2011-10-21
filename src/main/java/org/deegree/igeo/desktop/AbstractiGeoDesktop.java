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
package org.deegree.igeo.desktop;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.ImageUtils;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.config.GridLayoutType;
import org.deegree.igeo.config.LayoutType;
import org.deegree.igeo.config.OnlineResourceType;
import org.deegree.igeo.config.SplittedLayoutType;
import org.deegree.igeo.config.TabLayoutType;
import org.deegree.igeo.config.ToolbarEntryType;
import org.deegree.igeo.config.Util;
import org.deegree.igeo.config._AbstractLayoutType;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.modules.DefaultMapModule;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.views.ComponentPosition;
import org.deegree.igeo.views.swing.ButtonGroup;
import org.deegree.igeo.views.swing.ToolBar;
import org.deegree.igeo.views.swing.proxymanager.ProxyManagerPanel;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.kernel.ProcessMonitor;
import org.deegree.model.Identifier;

import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public abstract class AbstractiGeoDesktop extends ApplicationContainer<Container> implements ActionListener {
    
    private static final ILogger LOG = LoggerFactory.getLogger( AbstractiGeoDesktop.class );

    /**
     * @param processMonitor
     */
    public AbstractiGeoDesktop( ProcessMonitor processMonitor ) {
        super( processMonitor );
    }

    /**
     * appends entries into the tool bar
     * 
     * @param module
     *            the module the entries belongs to
     * @param toolBarEntries
     *            the entries to add as new toolBar
     */
    protected void appendToolBar( IModule<Container> module, List<ToolbarEntryType> toolBarEntries ) {

        if ( toolBarEntries.size() > 0 ) {

            ToolBar toolBar;
            if ( module != null ) {
                toolBar = (ToolBar) module.getToolBarController().getView();
            } else {
                toolBar = (ToolBar) this.toolBarController.getView();
            }
            toolBar.putClientProperty( Options.HEADER_STYLE_KEY, HeaderStyle.BOTH );

            List<AbstractButton> btList = null;
            String key = "";
            if ( module != null ) {
                key = module.getIdentifier().getValue();
                if ( module instanceof DefaultMapModule<?> ) {
                    key = module.getClass().getName();
                }
            }
            if ( module != null && !toolbarButtons.containsKey( key ) ) {
                btList = new ArrayList<AbstractButton>();
                toolbarButtons.put( key, btList );
            } else {
                btList = new ArrayList<AbstractButton>();
            }
            for ( ToolbarEntryType entry : toolBarEntries ) {

                toolBar.setFloatable( proj.getView().getToolBar().get( 0 ).isFloatable() );
                toolBar.setRollover( proj.getView().getToolBar().get( 0 ).isUseRollover() );

                AbstractButton button = null;
                if ( entry.getEntryType() == null || entry.getEntryType().value().equalsIgnoreCase( "ToggleButton" ) ) {
                    // use ToggleButton as default
                    if ( entry.getName() != null && entry.getName().length() > 0 ) {
                        String title = Messages.getMessage( Locale.getDefault(), entry.getName() );
                        button = new JToggleButton( title );
                    } else {
                        button = new JToggleButton();
                    }
                } else if ( entry.getEntryType().value().equalsIgnoreCase( "PushButton" ) ) {
                    if ( entry.getName() != null && entry.getName().length() > 0 ) {
                        String title = Messages.getMessage( Locale.getDefault(), entry.getName() );
                        button = new JButton( title );
                    } else {
                        button = new JButton();
                    }
                } else if ( entry.getEntryType().value().equalsIgnoreCase( "RadioButton" ) ) {
                    if ( entry.getName() != null && entry.getName().length() > 0 ) {
                        String title = Messages.getMessage( Locale.getDefault(), entry.getName() );
                        button = new JRadioButton( title );
                    } else {
                        button = new JRadioButton();
                    }
                } else if ( entry.getEntryType().value().equalsIgnoreCase( "CheckBox" ) ) {
                    if ( entry.getName() != null && entry.getName().length() > 0 ) {
                        String title = Messages.getMessage( Locale.getDefault(), entry.getName() );
                        button = new JCheckBox( title );
                    } else {
                        button = new JCheckBox();
                    }
                } else {
                    throw new RuntimeException( "not supported toolbar entry type: " + entry.getEntryType() );
                }
                // store button assign with an action to avoid initializing it twice
                btList.add( button );
                button.setBorder( BorderFactory.createEmptyBorder() );
                String groupName = entry.getAssignedGroup();
                // if group a button is assigned to already exists add button
                // otherwise create a new group
                if ( groupName != null && groupName.length() > 0 ) {
                    ButtonGroup assignedGroup = btGroups.get( groupName );
                    if ( assignedGroup == null ) {
                        assignedGroup = new ButtonGroup();
                        btGroups.put( groupName, assignedGroup );
                    }
                    assignedGroup.add( button );
                }

                // register component for a module and action. This will be used
                // to synchronize state of different components responsible for
                // the same action
                if ( module == null ) {
                    // must be assigned to application itself
                    registerAction( Util.convertIdentifier( proj.getIdentifier() ), entry.getAssignedAction(), button );
                    // if assigned module != null it tool bar item will be assigned to main
                    // application
                    button.addActionListener( this );
                } else {
                    registerAction( module.getIdentifier(), entry.getAssignedAction(), button );
                    button.addActionListener( module );
                }

                button.setName( entry.getAssignedAction() );
                button.setVisible( true );
                if ( entry.getTooltip() != null ) {
                    button.setToolTipText( Messages.getMessage( getLocale(), entry.getTooltip() ) );
                }

                addIcon( entry.getIcon(), button, 25 );
                toolBar.add( button );

            }
            toolBar.setMargin( new Insets( 3, 3, 3, 3 ) );
            if ( proj.getView().getToolBar().get( 0 ).isVertical() ) {
                toolBar.setOrientation( JToolBar.VERTICAL );
            }
            this.toolbarPanel.add( toolBar );
        }

    }
    
    /**
     * register component for a module and action. This will be used to synchronize state of different components
     * responsible for the same action
     * 
     * @param identifier
     * @param action
     * @param button
     */
    protected void registerAction( Identifier identifier, String action, AbstractButton button ) {

        Map<String, List<Object>> map = actionMap.get( identifier );
        if ( map == null ) {
            map = new LinkedHashMap<String, List<Object>>();
            actionMap.put( identifier, map );
        }
        List<Object> components = map.get( action );
        if ( components == null ) {
            components = new ArrayList<Object>();
            map.put( action, components );
        }
        components.add( button );
    }
    
    /**
     * 
     * @param href
     * @param aButton
     * @param size
     */
    protected void addIcon( String href, AbstractButton aButton, int size ) {
        Image img = null;
        try {
            img = ImageUtils.loadImage( resolve( href ) );
        } catch ( MalformedURLException e ) {
            LOG.logError( "Icon href is not a valied URL: " + href, e );
            return;
        } catch ( Exception e ) {
            // LOG.logError( "Image from URL: " + href + " can not be loaded", e );
            // return;
            int idx = href.lastIndexOf( "/" );
            try {
                ImageIcon ic = (ImageIcon) IconRegistry.getIcon( href.substring( idx + 1 ) );
                img = ic.getImage();
            } catch ( Exception e2 ) {
                LOG.logError( "Image from URL: " + href + " can not be loaded", e );
                return;
            }
        }
        BufferedImage tmp = new BufferedImage( size, size, BufferedImage.TYPE_INT_ARGB );
        int x = ( size - img.getWidth( null ) ) / 2;
        int y = ( size - img.getHeight( null ) ) / 2;
        Graphics g = tmp.getGraphics();
        g.drawImage( img, x, y, null );
        g.dispose();
        aButton.setIcon( new ImageIcon( tmp ) );
    }

    /**
     * 
     * @param icon
     * @param aButton
     */
    protected void addIcon( OnlineResourceType icon, AbstractButton aButton, int size ) {
        if ( icon != null ) {
            String href = icon.getHref();
            addIcon( href, aButton, size );
        }
    }
    
    /**
     * sets the layout for the target container of a module and adds the module to it (if layout is of type BorderLayot,
     * SplittedLayout, TabbedLayout or GridLayout
     * 
     * @param layout
     * @param targetPane
     * @return targetPane with defined layout
     */
    protected Container setTargetLayout( LayoutType layout, Container targetPane, List<IModule<Container>> modList,
                                       boolean useHorizontalScrollbar, boolean useVerticalScrollbar ) {

        if ( !( targetPane.getLayout() instanceof BorderLayout ) && !( targetPane instanceof JDesktopPane ) ) {
            // ensure that target pane uses border layout if it is not a container for
            // internal frames
            targetPane.setLayout( new BorderLayout() );
        }

        _AbstractLayoutType alt = layout.get_AbstractLayout().getValue();
        if ( alt instanceof TabLayoutType ) {
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setLocale( targetPane.getLocale() );
            targetPane.add( tabbedPane, BorderLayout.CENTER );
            targetPane = tabbedPane;
        } else if ( alt instanceof SplittedLayoutType ) {
            JSplitPane splitPane = new JSplitPane();
            splitPane.setLocale( targetPane.getLocale() );
            if ( ( (SplittedLayoutType) alt ).isVertical() ) {
                splitPane.setOrientation( JSplitPane.VERTICAL_SPLIT );
            } else {
                splitPane.setOrientation( JSplitPane.HORIZONTAL_SPLIT );
            }
            targetPane.add( splitPane, BorderLayout.CENTER );
            targetPane = splitPane;
        } else if ( alt instanceof GridLayoutType ) {
            int[] cols = determineGridSize( modList );
            GridPanel gridPanel = new GridPanel( cols, 5, 10, false, false );
            gridPanel.setLocale( targetPane.getLocale() );
            targetPane.add( gridPanel );
            targetPane = gridPanel;
        }
        if ( useHorizontalScrollbar || useVerticalScrollbar ) {
            JScrollPane paneScrollPane = new JScrollPane( targetPane );
            paneScrollPane.setLocale( targetPane.getLocale() );
            if ( useHorizontalScrollbar ) {
                paneScrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
            }
            if ( useVerticalScrollbar ) {
                paneScrollPane.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );
            }
        }
        return targetPane;
    }

    /**
     * 
     * @param modules
     * @return width for each column of a grid
     */
    protected int[] determineGridSize( List<IModule<Container>> modules ) {
        int maxCol = -1;
        for ( IModule<Container> module : modules ) {
            ComponentPosition cpa = module.getComponentPositionAdapter();
            if ( cpa.getGridColumn() > maxCol ) {
                maxCol = cpa.getGridColumn();
            }
        }
        int[] width = new int[maxCol + 1];
        for ( int i = 0; i < width.length; i++ ) {
            width[i] = 200;
        }
        return width;
    }
    
    /**
     * 
     */
    protected static void initProxy() {
        Preferences prefs = Preferences.userNodeForPackage( ProxyManagerPanel.class );
        String host = prefs.get( "PROXYDEF_HTTP_HOST", null );
        int port = prefs.getInt( "PROXYDEF_HTTP_PORT", -1 );
        String user = prefs.get( "PROXYDEF_HTTP_USER", null );
        String pw = prefs.get( "PROXYDEF_HTTP_PASSWORD", null );
        String nonProxyHosts = prefs.get( "PROXYDEF_HTTP_NONPROXYHOSTS", null );
        if ( host != null ) {
            System.setProperty( "http.proxyHost", host );
            System.setProperty( "http.proxyPort", Integer.toString( port ) );
            if ( user != null && user.length() > 0 ) {
                System.setProperty( "http.proxyUser", user );
            }
            if ( pw != null && pw.length() > 0 ) {
                System.setProperty( "http.proxyPassword", pw );
            }
            if ( nonProxyHosts != null && nonProxyHosts.length() > 0 ) {
                System.setProperty( "http.nonProxyHosts", nonProxyHosts );
            }
        }
    }
}
