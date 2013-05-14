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
package org.deegree.igeo.views.swing.objectinfo;

import static java.awt.Cursor.DEFAULT_CURSOR;
import static java.awt.Cursor.HAND_CURSOR;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.showInputDialog;
import static org.deegree.framework.log.LoggerFactory.getLogger;
import static org.deegree.framework.util.StringTools.countString;
import static org.deegree.igeo.i18n.Messages.get;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.SelectFeatureCommand;
import org.deegree.igeo.commands.model.ZoomCommand;
import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.FeatureTable;
import org.deegree.igeo.views.HelpManager;
import org.deegree.igeo.views.swing.DefaultPanel;
import org.deegree.igeo.views.swing.HelpFrame;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.igeo.views.swing.util.table.HeaderListener;
import org.deegree.igeo.views.swing.util.table.SortButtonRenderer;
import org.deegree.kernel.Command;
import org.deegree.model.Identifier;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.GMLFeatureAdapter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryException;

/**
 * 
 * The <code>FeatureTablePanel</code> class. TODO add class documentation here.
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class FeatureTablePanel extends DefaultPanel implements FeatureTable, ClipboardOwner, ListSelectionListener {

    static final Cursor defaultCursor = new Cursor( DEFAULT_CURSOR );

    static final Cursor handCursor = new Cursor( HAND_CURSOR );

    private static final long serialVersionUID = -3251085544494165934L;

    static final ILogger LOG = getLogger( FeatureTablePanel.class );

    private static final String LINEBREAK = System.getProperty( "line.separator" );

    private JToolBar jToolBar;

    private JPanel pnTable;

    private JButton btCopySelected;

    private JButton btCopyAll;

    private JButton btHelp;

    private JTabbedPane layersTabbedPane;

    private JTextArea taGML;

    private JScrollPane scGML;

    @SuppressWarnings("unused")
    private JTree treeView;

    private JButton btZoomto;

    @SuppressWarnings("unused")
    private JPanel pnTree;

    private JPanel pnGML;

    private JTable tabFeat;

    private JScrollPane featTableSC;

    private JTabbedPane viewsTabbedPane;

    private Layer layer;

    private FeatureCollection featureCollection;

    /**
     * 
     */
    public FeatureTablePanel() {
        initGUI();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#init(org.deegree.igeo.config.ViewFormType)
     */
    public void init( ViewFormType viewForm )
                            throws Exception {
        ApplicationContainer<Container> appContainer = this.owner.getApplicationContainer();

        MapModel mapModel = appContainer.getMapModel( null );

        List<Layer> layers = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
        if ( layers.size() > 0 ) {
            Layer layer = layers.get( 0 );
            List<DataAccessAdapter> data = layer.getDataAccess();
            for ( DataAccessAdapter dataAccessAdapter : data ) {
                if ( dataAccessAdapter instanceof FeatureAdapter ) {
                    FeatureCollection fc = ( (FeatureAdapter) dataAccessAdapter ).getFeatureCollection();
                    setFeatureCollection( layer, fc );
                }
            }
        }
    }

    private void initGUI() {
        try {
            BorderLayout thisLayout = new BorderLayout();
            this.setLayout( thisLayout );
            this.setPreferredSize( new java.awt.Dimension( 814, 460 ) );
            {
                jToolBar = new JToolBar();
                this.add( jToolBar, BorderLayout.NORTH );
                jToolBar.setPreferredSize( new java.awt.Dimension( 814, 35 ) );
                {
                    btCopySelected = new JButton( IconRegistry.getIcon( "copy.gif" ) );
                    btCopySelected.setToolTipText( Messages.getMessage( getLocale(), "$MD10541a" ) );
                    jToolBar.add( btCopySelected );
                    btCopySelected.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            int[] rows = tabFeat.getSelectedRows();
                            if ( rows != null && rows.length > 0 ) {
                                copyToClipboard( false );
                            }
                        }
                    } );
                }
                {
                    btCopyAll = new JButton( IconRegistry.getIcon( "copy_all.gif" ) );
                    btCopyAll.setToolTipText( Messages.getMessage( getLocale(), "$MD10541b" ) );
                    jToolBar.add( btCopyAll );
                    btCopyAll.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            copyToClipboard( true );
                        }
                    } );
                }
                {
                    btZoomto = new JButton( getImageIcon( "zoom.gif" ) );
                    btZoomto.setToolTipText( Messages.getMessage( getLocale(), "$MD10540" ) );
                    jToolBar.add( btZoomto );
                    btZoomto.addActionListener( new ZoomToActionListener() );
                }
                {
                    btHelp = new JButton( IconRegistry.getIcon( "help.png" ) );
                    jToolBar.add( btHelp );
                    btHelp.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            HelpFrame hf = HelpFrame.getInstance( new HelpManager( owner.getApplicationContainer() ) );
                            hf.setVisible( true );
                            hf.gotoModule( "LayerInfo" );
                        }
                    } );
                }
            }
            {
                layersTabbedPane = new JTabbedPane();
                this.add( layersTabbedPane, BorderLayout.CENTER );
                {
                    viewsTabbedPane = new JTabbedPane();
                    layersTabbedPane.addTab( Messages.getMessage( getLocale(), "$MD11343" ), null, viewsTabbedPane,
                                             null );
                    viewsTabbedPane.setTabPlacement( JTabbedPane.LEFT );
                    {
                        pnTable = new JPanel();
                        BorderLayout tablePanelLayout = new BorderLayout();
                        viewsTabbedPane.addTab( Messages.getMessage( getLocale(), "$MD11342" ), null, pnTable, null );
                        pnTable.setLayout( tablePanelLayout );
                        {
                            {
                                tabFeat = new JTable();
                                featTableSC = new JScrollPane( tabFeat );
                                tabFeat.setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
                                tabFeat.getSelectionModel().addListSelectionListener( this );
                                tabFeat.addMouseMotionListener( new MouseMotionAdapter() {
                                    @Override
                                    public void mouseMoved( MouseEvent e ) {
                                        updateCursor( e );
                                    }
                                } );
                                tabFeat.addMouseListener( new MouseAdapter() {
                                    @Override
                                    public void mouseClicked( MouseEvent e ) {
                                        String val = getCellValue( e.getPoint() );
                                        int count = 0;
                                        if ( val == null || ( count = countString( val, "http://" ) ) == 0 ) {
                                            return;
                                        }

                                        if ( count > 1 ) {
                                            String[] vals = val.split( "[ ,]" );
                                            val = (String) showInputDialog( null, get( "$MD10896" ), get( "$DI10019" ),
                                                                            QUESTION_MESSAGE, null, vals, vals[0] );
                                            if ( val == null ) {
                                                return;
                                            }
                                        }

                                        LinkedList<String> cmd = new LinkedList<String>();
                                        if ( System.getProperty( "os.name" ).equalsIgnoreCase( "linux" ) ) {
                                            cmd.add( "firefox" );
                                        } else {
                                            cmd.add( "cmd" );
                                            cmd.add( "/c" );
                                            cmd.add( "start" );
                                        }
                                        cmd.add( val );

                                        // leave the process alone, whatever happens
                                        ProcessBuilder pb = new ProcessBuilder( cmd );
                                        try {
                                            pb.start();
                                        } catch ( IOException e1 ) {
                                            LOG.logError( "Unknown error", e1 );
                                        }
                                    }

                                    @Override
                                    public void mouseEntered( MouseEvent e ) {
                                        updateCursor( e );
                                    }

                                    @Override
                                    public void mouseExited( MouseEvent e ) {
                                        updateCursor( null );
                                    }
                                } );
                            }
                            pnTable.add( featTableSC, BorderLayout.CENTER );
                        }
                    }
                    {
                        pnGML = new JPanel();
                        BorderLayout gmlPanelLayout = new BorderLayout();
                        pnGML.setLayout( gmlPanelLayout );
                        viewsTabbedPane.addTab( Messages.getMessage( getLocale(), "$MD11341" ), null, pnGML, null );
                        {
                            scGML = new JScrollPane();
                            pnGML.add( scGML, BorderLayout.CENTER );
                            {
                                taGML = new JTextArea();
                                taGML.setEditable( false );
                                scGML.setViewportView( taGML );
                            }
                        }
                    }
                    {
                        /*
                         * pnTree = new JPanel(); BorderLayout treePanelLayout = new BorderLayout(); pnTree.setLayout(
                         * treePanelLayout ); viewsTabbedPane.addTab( "tree view", null, pnTree, null ); { treeView =
                         * new JTree(); pnTree.add( treeView, BorderLayout.CENTER ); }
                         */
                    }
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    String getCellValue( Point p ) {
        int column = tabFeat.columnAtPoint( p );
        int row = tabFeat.rowAtPoint( p );

        if ( column == -1 || row == -1 ) {
            return null;
        }

        Object val = tabFeat.getModel().getValueAt( row, column );
        if ( val instanceof String ) {
            return (String) val;
        }
        return null;
    }

    void updateCursor( MouseEvent e ) {
        if ( e == null ) {
            setCursor( defaultCursor );
            return;
        }

        String val = getCellValue( e.getPoint() );
        if ( val != null && val.startsWith( "http://" ) ) {
            setCursor( handCursor );
        } else {
            setCursor( defaultCursor );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer.Clipboard,
     * java.awt.datatransfer.Transferable)
     */
    public void lostOwnership( Clipboard clipboard, Transferable contents ) {
        // TODO Auto-generated method stub

    }

    /**
     * sets a new FeatureCollection to be displayed with a {@link FeatureTablePanel}
     * 
     * @param layer
     * @param featureCollection
     *            must not be <code>null</code>
     */
    public void setFeatureCollection( Layer layer, final FeatureCollection featureCollection ) {
        this.featureCollection = featureCollection;
        this.layer = layer;
        viewsTabbedPane.setName( layer.getTitle() );

        DefaultTableModel ftm;

        if ( featureCollection.size() == 1 ) {
            ftm = new WMSFeatureTableModel( featureCollection );
        } else {
            ftm = new FeatureTableModel( featureCollection );
        }
        this.tabFeat.setModel( ftm );

        markSelectedFeatures( layer, featureCollection );

        this.tabFeat.invalidate();

        for ( int i = 0; i < this.tabFeat.getColumnCount(); i++ ) {
            TableColumn column = this.tabFeat.getColumnModel().getColumn( i );
            column.setPreferredWidth( 120 );
        }

        SortButtonRenderer renderer = new SortButtonRenderer();
        for ( int i = 0; i < this.tabFeat.getColumnCount(); i++ ) {
            TableColumn column = this.tabFeat.getColumnModel().getColumn( i );
            column.setHeaderRenderer( renderer );
        }
        JTableHeader header = tabFeat.getTableHeader();
        header.addMouseListener( new FeatHeaderListener( header, renderer ) );

        this.invalidate();
        this.repaint();
        Thread th = new Thread() {
            @Override
            public void run() {
                GMLFeatureAdapter ada = new GMLFeatureAdapter( true );
                try {
                    XMLFragment xml = ada.export( featureCollection );
                    taGML.setText( xml.getAsPrettyString() );
                } catch ( Exception e ) {
                    LOG.logDebug( "Stack trace", e );
                    DialogFactory.openErrorDialog( "Application", null, Messages.get( "$MD10538" ),
                                                   Messages.get( "$MD10539" ), e );
                }
            }
        };
        th.start();
    }

    private void markSelectedFeatures( Layer layer, final FeatureCollection featureCollection ) {
        // for reaster layers / datasource feature collection ist null
        if ( featureCollection != null ) {
            FeatureCollection fc = layer.getSelectedFeatures();
            List<Integer> list = new ArrayList<Integer>();
            Iterator<Feature> iterator = fc.iterator();
            Feature[] features = featureCollection.toArray();
            while ( iterator.hasNext() ) {
                Feature feature = (Feature) iterator.next();
                String id = feature.getId();
                for ( int i = 0; i < features.length; i++ ) {
                    if ( features[i].getId().equals( id ) ) {
                        list.add( i );
                        break;
                    }
                }
            }
            for ( Integer c : list ) {
                this.tabFeat.getSelectionModel().addSelectionInterval( c, c );
            }
        }
    }

    /**
     * @return list of features assigned to selected rows
     */
    public List<Feature> getSelected() {
        int[] rows = tabFeat.getSelectedRows();
        FeatureTableModel ftm = (FeatureTableModel) tabFeat.getModel();
        FeatureCollection fc = ftm.getFeatureCollection();
        List<Feature> list = new ArrayList<Feature>( rows.length );
        for ( int i : rows ) {
            list.add( fc.getFeature( rows[i] ) );
        }
        return list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.FeatureTable#select(org.deegree.model.filter.Filter)
     */
    public void select( Filter filter ) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.FeatureTable#select(org.deegree.model.feature.FeatureCollection)
     */
    public void select( FeatureCollection featureCollection ) {
        if ( tabFeat.getModel() instanceof FeatureTableModel ) {
            FeatureCollection fc = ( (FeatureTableModel) tabFeat.getModel() ).getFeatureCollection();

            // if selected layer/datasource is of raster type fc will be null
            if ( fc != null ) {
                Feature[] features = fc.toArray();
                Iterator<Feature> iter = featureCollection.iterator();
                tabFeat.getSelectionModel().removeListSelectionListener( this );
                tabFeat.clearSelection();
                while ( iter.hasNext() ) {
                    String id = iter.next().getId();
                    for ( int i = 0; i < features.length; i++ ) {
                        if ( features[i].getId().equals( id ) ) {
                            tabFeat.addRowSelectionInterval( i, i );
                            break;
                        }
                    }
                }
                tabFeat.getSelectionModel().addListSelectionListener( this );
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged( ListSelectionEvent e ) {

        // e.getValueIsAdjusting() is required to ensure that a selecting event just will be processed one time
        if ( e.getSource() == tabFeat.getSelectionModel() && tabFeat.getRowSelectionAllowed()
             && e.getValueIsAdjusting() ) {
            // Row selection changed
            int[] rows = tabFeat.getSelectedRows();
            if ( tabFeat.getModel() instanceof FeatureTableModel ) {
                FeatureTableModel ftm = (FeatureTableModel) tabFeat.getModel();
                FeatureCollection fc = ftm.getFeatureCollection();
                List<Identifier> list = new ArrayList<Identifier>( rows.length );
                for ( int i = 0; i < rows.length; i++ ) {
                    list.add( new Identifier( fc.getFeature( rows[i] ).getId() ) );
                }
                Command cmd = new SelectFeatureCommand( layer, list, false );
                ApplicationContainer<Container> appContainer = owner.getApplicationContainer();
                try {
                    appContainer.getCommandProcessor().executeSychronously( cmd, true );
                } catch ( Exception ex ) {
                    LOG.logError( ex.getMessage(), ex );
                }
            }
        }

    }

    private Icon getImageIcon( String image ) {
        URL url = FeatureTablePanel.class.getResource( image );
        return IconRegistry.getIcon( url );
    }

    /**
     * 
     */
    public void refresh() {
        setFeatureCollection( layer, featureCollection );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.FeatureTable#clear()
     */
    public void clear() {
        this.tabFeat.setModel( new DefaultTableModel() );
    }

    void copyToClipboard( boolean all ) {
        int[] rows = tabFeat.getSelectedRows();
        if ( rows == null || rows.length == 0 || all ) {
            int rowCnt = tabFeat.getRowCount();
            rows = new int[rowCnt];
            for ( int i = 0; i < rows.length; i++ ) {
                rows[i] = i;
            }
        }
        int cols = tabFeat.getColumnCount();

        StringBuilder sb = new StringBuilder( 10000 );
        for ( int j = 0; j < cols; j++ ) {
            sb.append( tabFeat.getColumnName( j ) );
            if ( j < cols ) {
                sb.append( "\t" );
            }
        }
        sb.append( LINEBREAK );
        for ( int i = 0; i < rows.length; i++ ) {
            for ( int j = 0; j < cols; j++ ) {
                sb.append( tabFeat.getValueAt( rows[i], j ) );
                if ( j < cols ) {
                    sb.append( "\t" );
                }
            }
            if ( i < rows.length ) {
                sb.append( LINEBREAK );
            }
        }
        StringSelection stringSelection = new StringSelection( sb.toString() );
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents( stringSelection, FeatureTablePanel.this );
    }

    // ////////////////////////////////////////////////////////////////////////////////
    // inner classes
    // ////////////////////////////////////////////////////////////////////////////////

    class FeatHeaderListener extends HeaderListener {

        /**
         * @param header
         * @param renderer
         */
        public FeatHeaderListener( JTableHeader header, SortButtonRenderer renderer ) {
            super( header, renderer );
        }

        public void mousePressed( MouseEvent e ) {

            JTable table = header.getTable();

            if ( table.getModel() instanceof FeatureTableModel && e.getButton() == 1 ) {
                // just feature tables can be sorted
                int col = header.columnAtPoint( e.getPoint() );
                int sortCol = table.convertColumnIndexToModel( col );
                renderer.setPressedColumn( col );
                renderer.setSelectedColumn( col );
                header.repaint();

                if ( header.getTable().isEditing() ) {
                    header.getTable().getCellEditor().stopCellEditing();
                }

                boolean isAscent;
                if ( SortButtonRenderer.DOWN == renderer.getState( col ) ) {
                    isAscent = true;
                } else {
                    isAscent = false;
                }

                ( (FeatureTableModel) table.getModel() ).sortByColumn( sortCol, isAscent );
                FeatureTablePanel.this.invalidate();
                FeatureTablePanel.this.getParent().repaint();
            }

        }
    }

    class ZoomToActionListener implements ActionListener {
        public void actionPerformed( ActionEvent e ) {
            Envelope env = null;
            if ( tabFeat.getModel() instanceof FeatureTableModel ) {
                env = getEnvelopeFromFeatureTableModel( (FeatureTableModel) tabFeat.getModel() );
            } else if ( tabFeat.getModel() instanceof WMSFeatureTableModel ) {
                env = getEnevelopeFromWmsFeatureTableModel( (WMSFeatureTableModel) tabFeat.getModel() );
            }
            if ( env != null ) {
                zoomToSelected( env );
            }
        }

        private Envelope getEnvelopeFromFeatureTableModel( FeatureTableModel ftm ) {
            int[] rows = tabFeat.getSelectedRows();
            if ( rows != null && rows.length > 0 ) {
                FeatureCollection fc = ftm.getFeatureCollection();
                try {
                    Envelope env = fc.getFeature( rows[0] ).getBoundedBy();
                    for ( int i = 1; i < rows.length; i++ ) {
                        env = env.merge( fc.getFeature( rows[i] ).getBoundedBy() );
                    }
                    return expandEnvelopeIfItIsAPoint( fc, env );
                } catch ( GeometryException e ) {
                    LOG.logError( "Could not zoom to selected features: " + e.getMessage() );
                    LOG.logDebug( "Could not zoom to selected features!", e );
                }
            }
            return null;
        }

        private Envelope getEnevelopeFromWmsFeatureTableModel( WMSFeatureTableModel tableModel ) {
            FeatureCollection fc = tableModel.getFeatureCollection();
            try {
                Envelope env = fc.getBoundedBy();
                return expandEnvelopeIfItIsAPoint( fc, env );
            } catch ( GeometryException e ) {
                LOG.logError( "Could not zoom to selected features: " + e.getMessage() );
                LOG.logDebug( "Could not zoom to selected features!", e );
            }
            return null;
        }

        private Envelope expandEnvelopeIfItIsAPoint( FeatureCollection fc, Envelope env )
                                throws GeometryException {
            if ( env.getWidth() < 0.0001 ) {
                // selected object must be a Point; set bbox width/height to 1%
                // of the overall map width
                env = env.getBuffer( fc.getBoundedBy().getWidth() / 200d );
            }
            if ( env.getWidth() < 0.0001 ) {
                // feature collection just contains one point; set fix bbox width/height
                // 25 map units
                env = env.getBuffer( 25 );
            }
            return env;
        }

        private void zoomToSelected( Envelope env ) {
            ApplicationContainer<Container> appContainer = owner.getApplicationContainer();
            MapModel mm = appContainer.getMapModel( null );
            ZoomCommand cmd = new ZoomCommand( mm );
            cmd.setZoomBox( env, mm.getTargetDevice().getPixelWidth(), mm.getTargetDevice().getPixelHeight() );
            try {
                appContainer.getCommandProcessor().executeSychronously( cmd, true );
            } catch ( Exception ex ) {
                LOG.logError( ex.getMessage(), ex );
            }
        }
    }

}