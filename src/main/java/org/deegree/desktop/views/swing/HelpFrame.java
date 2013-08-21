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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.views.HelpManager;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class HelpFrame extends JFrame {

    private static final long serialVersionUID = -963193689947547993L;

    private static final ILogger LOG = LoggerFactory.getLogger( HelpFrame.class );

    private JSplitPane jSplitPane1;

    private JEditorPane helpArea;

    private JPanel pnKeyword;

    private JTabbedPane jTabbedPane1;

    private JTree tree;

    private HelpManager helpManager;

    private ApplicationContainer<Container> appContainer;

    private static HelpFrame helpFrame;

    /**
     * 
     * @param helpManager
     */
    @SuppressWarnings("unchecked")
    private HelpFrame( HelpManager helpManager ) {
        this.helpManager = helpManager;
        this.appContainer = (ApplicationContainer<Container>) helpManager.getApplicationContainer();
        initGUI();
    }

    /**
     * 
     * @param appContainer
     * @param helpManager
     * @return singleton instance of HelpFrame
     */
    public static HelpFrame getInstance( HelpManager helpManager ) {
        if ( helpFrame == null ) {
            helpManager.init();
            helpFrame = new HelpFrame( helpManager );
        }
        return helpFrame;
    }

    /**
     * resets help frame so next invocation of {@link #getInstance(HelpManager)} will load help pages again
     */
    public static void reset() {
        helpFrame = null;
    }

    private void initGUI() {
        try {
            BorderLayout thisLayout = new BorderLayout();
            setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
            getContentPane().setLayout( thisLayout );
            {
                jSplitPane1 = new JSplitPane();
                getContentPane().add( jSplitPane1, BorderLayout.CENTER );
                jSplitPane1.setDividerLocation( 200 );
                {
                    JScrollPane sc = new JScrollPane();
                    sc.getHorizontalScrollBar().setAutoscrolls( true );
                    sc.getVerticalScrollBar().setAutoscrolls( true );
                    sc.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
                    jSplitPane1.add( sc, JSplitPane.RIGHT );
                    {
                        helpArea = new JEditorPane();
                        helpArea.setEditable( false );
                        // load initial/default page
                        String page = helpManager.getProjectDefaultHelpPage();
                        if ( page == null ) {
                            page = "./help/igeodesktop_main.en.html";
                        }
                        helpArea.setPage( appContainer.resolve( page ) );
                        sc.setViewportView( helpArea );
                        helpArea.addHyperlinkListener( new Hyperactive() );
                    }
                }
                {
                    jTabbedPane1 = new JTabbedPane();
                    jSplitPane1.add( jTabbedPane1, JSplitPane.LEFT );
                    jTabbedPane1.setMinimumSize( new java.awt.Dimension( 200, 5 ) );
                    jTabbedPane1.setSize( 200, 517 );
                    jTabbedPane1.setPreferredSize( new java.awt.Dimension( 131, 517 ) );
                    jTabbedPane1.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
                    {
                        JScrollPane sc = new JScrollPane( tree = new JTree() );
                        sc.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
                        JPanel p = new JPanel( new BorderLayout() );
                        p.add( sc, BorderLayout.CENTER );
                        jTabbedPane1.addTab( "modules", null, p, null );
                    }
                    {
                        JScrollPane sc = new JScrollPane();
                        sc.getHorizontalScrollBar().setAutoscrolls( true );
                        sc.getVerticalScrollBar().setAutoscrolls( true );
                        sc.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
                        pnKeyword = new JPanel();
                        sc.setViewportView( pnKeyword );
                        BoxLayout keywordPanelLayout = new BoxLayout( pnKeyword, javax.swing.BoxLayout.Y_AXIS );
                        pnKeyword.setLayout( keywordPanelLayout );
                        pnKeyword.setBackground( Color.WHITE );
                        jTabbedPane1.addTab( "keywords", null, sc, null );
                    }

                }
            }
            fillKeywordPanel();
            fillModulePanel();
            this.setBounds( 100, 100, 900, 600 );
            this.setPreferredSize( new Dimension( 800, 600 ) );
            SwingUtilities.updateComponentTreeUI( this );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * for each keyword a button will be added to a pane/list that enables accessing/loading the HTML page a keyword is
     * assigend to. For this an {@link ActionListener} will be invoked that is responsible for loading a HTML page into
     * the help area on the right
     * 
     */
    private void fillKeywordPanel() {
        Set<String> keywords = helpManager.getKeywords();
        String[] array = keywords.toArray( new String[keywords.size()] );
        Arrays.sort( array );
        Color bgColor = pnKeyword.getBackground();
        KeywordActionListener listener = new KeywordActionListener();
        for ( String keyword : array ) {
            JButton label = new JButton( "- " + keyword );
            label.setBorder( BorderFactory.createEmptyBorder( 2, 5, 1, 10 ) );
            label.setBorderPainted( false );
            label.setRolloverEnabled( true );
            label.setBackground( bgColor );
            label.setForeground( Color.BLACK );
            label.addActionListener( listener );
            label.setActionCommand( keyword );
            pnKeyword.add( label );
        }
    }

    /**
     * for each module a button will be added to a pane/list that enables accessing/loading the HTML page a keyword is
     * assigend to. For this an {@link ActionListener} will be invoked that is responsible for loading a HTML page into
     * the help area on the right
     * 
     */
    private void fillModulePanel() {
        Set<String> modules = helpManager.getModuleNames();
        String[] array = modules.toArray( new String[modules.size()] );
        Arrays.sort( array );
        DefaultMutableTreeNode root = new DefaultMutableTreeNode( Messages.getMessage( getLocale(), "$DG10077" ) );
        List<String> kw = helpManager.getKeywordsByModule( "iGeoDesktop" );
        for ( String keyword : kw ) {
            String tmp = keyword.substring( 0, keyword.lastIndexOf( ':' ) );
            DefaultMutableTreeNode child = new DefaultMutableTreeNode( tmp );
            child.setUserObject( tmp );
            root.add( child );
        }

        TreeModel model = new DefaultTreeModel( root );
        for ( String moduleName : array ) {
            DefaultMutableTreeNode child = new DefaultMutableTreeNode( moduleName );
            root.add( child );
            kw = helpManager.getKeywordsByModule( moduleName );
            for ( String keyword : kw ) {
                String tmp = keyword.substring( 0, keyword.lastIndexOf( ':' ) );
                DefaultMutableTreeNode subChild = new DefaultMutableTreeNode( tmp );
                subChild.setUserObject( tmp );
                child.add( subChild );
            }
        }
        tree.addMouseListener( new ModuleActionListener() );
        tree.setModel( model );
        tree.expandRow( 0 );
    }

    /**
     * loads help page for passed keyword. The keyword string must be qualified. This means after a keyword seperated by
     * ':' the name of the module a keyword is assigned too must follow; e.g. editing:Digitizer.
     * 
     * @param keyword
     */
    public void gotoKeyword( String keyword ) {
        try {
            String s = helpManager.getReferenceForKeyword( keyword );
            if ( s != null ) {
                URL url = appContainer.resolve( s );
                helpArea.setPage( url );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * loads help page for passed module
     * 
     * @param module
     */
    public void gotoModule( String module ) {
        try {
            URL url = appContainer.resolve( helpManager.getReferenceForModule( module ) );
            helpArea.setPage( url );
        } catch ( Exception e ) {
            LOG.logWarning( "ignore", e );
        }
    }

    // ///////////////////////////////////////////////////////////////////////
    // inner classes
    // ///////////////////////////////////////////////////////////////////////

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version. $Revision$, $Date$
     */
    private class KeywordActionListener implements ActionListener {
        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed( ActionEvent event ) {
            JButton button = (JButton) event.getSource();
            String tmp = button.getActionCommand();
            gotoKeyword( tmp );
        }
    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version. $Revision$, $Date$
     */
    private class ModuleActionListener extends MouseAdapter {

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseClicked( MouseEvent event ) {
            Point dropPoint = event.getPoint();
            TreePath path = tree.getPathForLocation( dropPoint.x, dropPoint.y );
            if ( path != null ) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                if ( parent == null ) {
                    try {
                        helpArea.setPage( appContainer.resolve( "./help/igeodesktop_main.en.html" ) );
                    } catch ( IOException e ) {
                        LOG.logWarning( Messages.getMessage( getLocale(), "$DG10078", "./help/igeodesktop_main.en.html" ) );
                    }
                } else {
                    String kw = (String) node.getUserObject();
                    String module = (String) parent.getUserObject();
                    String tmp = null;
                    try {
                        // look if node represent help for a module in general
                        tmp = helpManager.getReferenceForModule( kw );
                        if ( tmp == null ) {
                            // if not it possibly is a keyword assigend to a specific page
                            tmp = helpManager.getReferenceForKeyword( kw + ':' + module );
                        }
                        if ( tmp == null ) {
                            // and if not a keyword specific page it should be a page directly
                            // assigend to a project
                            tmp = helpManager.getReferenceForKeyword( kw + ":iGeoDesktop" );
                        }
                        URL url = appContainer.resolve( tmp );
                        helpArea.setPage( url );
                    } catch ( IOException e ) {
                        LOG.logWarning( Messages.getMessage( getLocale(), "$DG10078", tmp ) );
                    }
                }
            }
        }

    }

    /**
     * inner class for handling events forced by clicking on a link
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version. $Revision$, $Date$
     */
    private class Hyperactive implements HyperlinkListener {

        public void hyperlinkUpdate( HyperlinkEvent e ) {
            if ( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
                JEditorPane pane = (JEditorPane) e.getSource();
                if ( e instanceof HTMLFrameHyperlinkEvent ) {
                    HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
                    HTMLDocument doc = (HTMLDocument) pane.getDocument();
                    doc.processHTMLFrameHyperlinkEvent( evt );
                } else {
                    URL url = null;
                    try {
                        HTMLDocument html = (HTMLDocument) e.getSourceElement().getDocument();
                        url = html.getBase();
                        url = new URL( url, e.getURL().toExternalForm() );
                        helpArea.setPage( url );
                    } catch ( Throwable t ) {
                        LOG.logWarning( Messages.getMessage( getLocale(), "$DG10079", url ) );
                    }
                }
            }
        }
    }

}
