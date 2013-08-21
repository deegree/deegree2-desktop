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
package org.deegree.desktop.views.swing.bookmark;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.commands.model.ZoomCommand;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.io.FileSystemAccess;
import org.deegree.desktop.io.FileSystemAccessFactory;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.modules.bookmarks.BookmarkModule;
import org.deegree.desktop.modules.bookmarks.Util;
import org.deegree.desktop.modules.bookmarks.BookmarkModule.BookmarkEntry;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.desktop.views.HelpManager;
import org.deegree.desktop.views.swing.HelpFrame;
import org.deegree.desktop.views.swing.util.GenericFileChooser;
import org.deegree.desktop.views.swing.util.DesktopFileFilter;
import org.deegree.desktop.views.swing.util.IconRegistry;
import org.deegree.desktop.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.kernel.CommandProcessor;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class BookmarkPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 3216012911925381311L;

    private static final ILogger LOG = LoggerFactory.getLogger( BookmarkPanel.class );

    private JToolBar pn_toolbar;

    private JButton bt_goto;

    private JTable tab_bookmarks;

    private JButton bt_load;

    private JButton bt_export;

    private JButton bt_add;

    private JButton bt_help;

    private JButton bt_remove;

    private JTextArea ta_description;

    private JScrollPane sc_list;

    private JPanel pn_list;

    private ApplicationContainer<Container> appCont;

    private BookmarkModule<Container> owner;

    /**
     * 
     * @param appCont
     * @param owner
     */
    public BookmarkPanel( ApplicationContainer<Container> appCont, BookmarkModule<Container> owner ) {
        this.appCont = appCont;
        this.owner = owner;
        initGUI();
    }

    private void initGUI() {
        try {
            BorderLayout thisLayout = new BorderLayout();
            this.setLayout( thisLayout );
            this.setPreferredSize( new java.awt.Dimension( 399, 504 ) );
            {
                pn_toolbar = new JToolBar();
                FlowLayout pn_toolbarLayout = new FlowLayout();
                pn_toolbarLayout.setAlignment( FlowLayout.LEFT );
                this.add( pn_toolbar, BorderLayout.NORTH );
                pn_toolbar.setLayout( pn_toolbarLayout );
                pn_toolbar.setPreferredSize( new java.awt.Dimension( 316, 40 ) );
                initGotoButton();
                initAddButton();
                initRemoveButton();
                initExportButton();
                initLoadButton();
                initHelpButton();
            }
            {
                pn_list = new JPanel();
                GridBagLayout pn_listLayout = new GridBagLayout();
                this.add( pn_list, BorderLayout.CENTER );
                pn_listLayout.rowWeights = new double[] { 0.0, 0.1 };
                pn_listLayout.rowHeights = new int[] { 344, 20 };
                pn_listLayout.columnWeights = new double[] { 0.1 };
                pn_listLayout.columnWidths = new int[] { 7 };
                pn_list.setLayout( pn_listLayout );
                {
                    sc_list = new JScrollPane();
                    pn_list.add( sc_list, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH,
                                                                  new Insets( 10, 10, 10, 10 ), 0, 0 ) );
                    {
                        DefaultTableModel tab_bookmarksModel = updateTableModel( owner.readFromCache() );
                        tab_bookmarks = new JTable( tab_bookmarksModel );
                        tab_bookmarks.getColumnModel().getColumn( 0 ).setCellRenderer( new BookmarkRenderer() );
                        tab_bookmarks.getColumnModel().getColumn( 1 ).setCellRenderer( new BookmarkRenderer() );
                        packRows( tab_bookmarks, 5 );
                        sc_list.setViewportView( tab_bookmarks );
                    }
                }
                {
                    ta_description = new JTextArea( Messages.getMessage( getLocale(), "$MD11156" ) );
                    pn_list.add( ta_description, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                                                         GridBagConstraints.CENTER,
                                                                         GridBagConstraints.BOTH, new Insets( 0, 10,
                                                                                                              10, 10 ),
                                                                         0, 0 ) );
                    ta_description.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                     "$MD11329" ) ) );
                    ta_description.setBackground( pn_toolbar.getBackground() );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

	private void initHelpButton() {
		{
		    bt_help = new JButton( IconRegistry.getIcon( "help.png" ) );
		    bt_help.setToolTipText( Messages.getMessage( getLocale(), "$MD11154" ) );
		    pn_toolbar.add( bt_help );
		    bt_help.addActionListener( new ActionListener() {
		        public void actionPerformed( ActionEvent e ) {
		            HelpFrame hf = HelpFrame.getInstance( new HelpManager( owner.getApplicationContainer() ) );
		            hf.setVisible( true );
		            hf.gotoKeyword( "Bookmark:Bookmark" );
		        }
		    } );
		}
	}

	private void initLoadButton() {
		{
		    bt_load = new JButton( IconRegistry.getIcon( "bookmark_import.png" ) );
		    bt_load.setToolTipText( Messages.getMessage( getLocale(), "$MD11153" ) );
		    pn_toolbar.add( bt_load );
		    bt_load.addActionListener( new ActionListener() {
		        public void actionPerformed( ActionEvent e ) {
		            try {
		                Preferences prefs = Preferences.userNodeForPackage( BookmarkPanel.class );
		                File file = GenericFileChooser.showOpenDialog( FILECHOOSERTYPE.externalResource,
		                                                               appCont, BookmarkPanel.this, prefs,
		                                                               "bookmark file", DesktopFileFilter.XML );
		                FileSystemAccessFactory fsaf = FileSystemAccessFactory.getInstance( appCont );
		                FileSystemAccess fsa = fsaf.getFileSystemAccess( FILECHOOSERTYPE.externalResource );
		                fsa.getFileURL( file.getAbsolutePath() );
		                List<BookmarkEntry> bookmarks = Util.loadBookmarks( file );
		                owner.writeToCache( bookmarks );
		                DefaultTableModel model = updateTableModel( bookmarks );
		                tab_bookmarks.setModel( model );
		            } catch ( Exception ex ) {
		                LOG.logError( ex );
		            }
		        }
		    } );
		}
	}

	private void initExportButton() {
		{
		    bt_export = new JButton( IconRegistry.getIcon( "bookmark_export.png" ) );
		    bt_export.setToolTipText( Messages.getMessage( getLocale(), "$MD11152" ) );
		    pn_toolbar.add( bt_export );
		    bt_export.addActionListener( new ActionListener() {
		        public void actionPerformed( ActionEvent e ) {
		            try {
		                Preferences prefs = Preferences.userNodeForPackage( BookmarkPanel.class );
		                File file = GenericFileChooser.showSaveDialog( FILECHOOSERTYPE.externalResource,
		                                                               appCont, BookmarkPanel.this, prefs,
		                                                               "bookmark file", DesktopFileFilter.XML );
		                FileSystemAccessFactory fsaf = FileSystemAccessFactory.getInstance( appCont );
		                FileSystemAccess fsa = fsaf.getFileSystemAccess( FILECHOOSERTYPE.externalResource );
		                fsa.getFileURL( file.getAbsolutePath() );
		                List<BookmarkEntry> bookmarks = owner.readFromCache();
		                Util.saveBookmarks( bookmarks, file );
		            } catch ( Exception ex ) {
		                LOG.logError( ex );
		            }
		        }
		    } );
		}
	}

	private void initRemoveButton() {
		{
		    bt_remove = new JButton( IconRegistry.getIcon( "bookmark_delete.png" ) );
		    bt_remove.setToolTipText( Messages.getMessage( getLocale(), "$MD11149" ) );
		    pn_toolbar.add( bt_remove );
		    bt_remove.addActionListener( new ActionListener() {

		        public void actionPerformed( ActionEvent arg0 ) {
		            int[] rows = tab_bookmarks.getSelectedRows();
		            if ( rows == null || rows.length == 0 ) {
		                DialogFactory.openWarningDialog( "application", BookmarkPanel.this,
		                                                 Messages.getMessage( getLocale(), "$MD11150" ),
		                                                 Messages.getMessage( getLocale(), "$MD11151" ) );
		                return;
		            }
		            DefaultTableModel dtm = (DefaultTableModel) tab_bookmarks.getModel();
		            dtm.removeRow( rows[0] );
		            List<BookmarkEntry> bookmarks = owner.readFromCache();
		            bookmarks.remove( rows[0] );
		            owner.writeToCache( bookmarks );
		        }
		    } );
		}
	}

	private void initAddButton() {
		{
		    bt_add = new JButton( IconRegistry.getIcon( "bookmark_add.png" ) );
		    bt_add.setToolTipText( Messages.getMessage( getLocale(), "$MD11148" ) );
		    pn_toolbar.add( bt_add );
		    bt_add.addActionListener( new ActionListener() {
		        public void actionPerformed( ActionEvent e ) {
		            owner.addBookmark();
		            DefaultTableModel tab_bookmarksModel = updateTableModel( owner.readFromCache() );
		            tab_bookmarks.setModel( tab_bookmarksModel );
		        }
		    } );
		}
	}

	private void initGotoButton() {
		{
		    bt_goto = new JButton( IconRegistry.getIcon( "bookmark_show.png" ) );
		    bt_goto.setToolTipText( Messages.getMessage( getLocale(), "$MD11145" ) );
		    pn_toolbar.add( bt_goto );
		    bt_goto.addActionListener( new ActionListener() {
		        public void actionPerformed( ActionEvent e ) {
		            int[] rows = tab_bookmarks.getSelectedRows();
		            if ( rows == null || rows.length == 0 ) {
		                DialogFactory.openWarningDialog( "application", BookmarkPanel.this,
		                                                 Messages.getMessage( getLocale(), "$MD11146" ),
		                                                 Messages.getMessage( getLocale(), "$MD11147" ) );
		                return;
		            }
		            List<BookmarkEntry> bookmarks = owner.readFromCache();
		            String name = (String) tab_bookmarks.getValueAt( rows[0], 0 );                                   
		            BookmarkModule.BookmarkEntry bme = null;
		            for ( BookmarkEntry bookmarkEntry : bookmarks ) {
		                if ( bookmarkEntry.name.equals( name ) ) {
		                    bme = bookmarkEntry;
		                    break;
		                }
		            }
		            CommandProcessor processor = appCont.getCommandProcessor();
		            try {
		                if ( bme.allMapModels ) {
		                    List<MapModel> mms = appCont.getMapModelCollection().getMapModels();
		                    for ( MapModel mapModel : mms ) {
		                        ZoomCommand cmd = new ZoomCommand( mapModel );
		                        cmd.setZoomBox( bme.env, -1, -1 );
		                        processor.executeSychronously( cmd, true );
		                    }
		                } else {
		                    MapModel mapModel = appCont.getMapModel( bme.mapModel );
		                    ZoomCommand cmd = new ZoomCommand( mapModel );
		                    cmd.setZoomBox( bme.env, -1, -1 );
		                    processor.executeSychronously( cmd, true );
		                }
		            } catch ( Exception ex ) {
		                LOG.logError( ex.getMessage(), ex );
		                DialogFactory.openErrorDialog( appCont.getViewPlatform(), BookmarkPanel.this,
		                                               Messages.getMessage( getLocale(), "$MD11330" ),
		                                               Messages.getMessage( getLocale(), "$MD11331" ), ex );
		            }
		        }
		    } );
		}
	}

    private DefaultTableModel updateTableModel( List<BookmarkEntry> bookmarks ) {
        String s = Messages.getMessage( getLocale(), "$MD11155" );
        String[] header = StringTools.toArray( s, ",", false );

        List<BookmarkEntry> tmp = new ArrayList<BookmarkEntry>();
        for ( BookmarkEntry bookmarkEntry : bookmarks ) {
            if ( appCont.getMapModel( null ).getMaxExtent().intersects( bookmarkEntry.env ) ) {
                tmp.add( bookmarkEntry );
            }
        }

        Object[][] data = new Object[tmp.size()][];
        for ( int i = 0; i < tmp.size(); i++ ) {
            Object[] val = new Object[2];
            val[0] = tmp.get( i ).name;
            val[1] = tmp.get( i ).description;
            data[i] = val;
        }

        DefaultTableModel tab_bookmarksModel = new DefaultTableModel( data, header );
        return tab_bookmarksModel;
    }

    // Returns the preferred height of a row.
    // The result is equal to the tallest cell in the row.
    public int getPreferredRowHeight( JTable table, int rowIndex, int margin ) {
        // Get the current default height for all rows
        int height = table.getRowHeight();

        // Determine highest cell in the row
        for ( int c = 0; c < table.getColumnCount(); c++ ) {
            TableCellRenderer renderer = table.getCellRenderer( rowIndex, c );
            Component comp = table.prepareRenderer( renderer, rowIndex, c );
            int h = comp.getPreferredSize().height + 2 * margin;
            height = Math.max( height, h );
        }
        return height;
    }

    public void packRows( JTable table, int margin ) {
        for ( int r = 0; r < table.getRowCount(); r++ ) {
            // Get the preferred height
            int h = getPreferredRowHeight( table, r, margin );

            // Now set the row height using the preferred height
            if ( table.getRowHeight( r ) != h ) {
                table.setRowHeight( r, h );
            }
        }
    }

    // //////////////////////////////////////////////////////////////////////////////////
    // inner classes
    // //////////////////////////////////////////////////////////////////////////////////

    /**
     * 
     * TODO add class documentation here
     * 
     * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    public class BookmarkRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 5726962277146245752L;

        @Override
        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected,
                                                        boolean hasFocus, int row, int column ) {
            if ( value instanceof BufferedImage ) {
                ImageIcon icon = new ImageIcon( (BufferedImage) value );
                setText( "" );
                setIcon( icon );
                this.setBackground( Color.BLUE );
                return this;
            } else {
                JTextArea ta = new JTextArea( (String) value );
                ta.setLineWrap( true );
                ta.setWrapStyleWord( true );
                if ( isSelected ) {
                    ta.setBackground( Color.BLUE );
                }
                return ta;
            }
        }

    }

}
