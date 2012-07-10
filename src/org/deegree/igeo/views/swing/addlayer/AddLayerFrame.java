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

package org.deegree.igeo.views.swing.addlayer;

import static org.deegree.igeo.views.swing.util.GuiUtils.addToFrontListener;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeSelectionModel;

import org.deegree.framework.utils.SwingUtils;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.views.DatasourceEntry;
import org.deegree.igeo.views.swing.DefaultFrame;
import org.deegree.igeo.views.swing.util.IconRegistry;

/**
 * The <code>JAddLayerFrame</code> is the start dialog to add a new layer to the assigned map model. A new Layer based
 * on one datasource (adding a new layer with different datasources is not supported yet), which must be choosen in this
 * dialog.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class AddLayerFrame extends DefaultFrame implements ActionListener, TreeSelectionListener {

    private static final long serialVersionUID = 1346295643758930101L;

    private JFrame nextStep;

    private ButtonPanel buttons;

    private JTree datasourceTree;

    private MapModel mapModel;

    private static final String WMS = "WMS";

    private static final String WCS = "WCS";

    private static final String WFS = "WFS";

    private static final String MEMORY = "Memory";

    static final String FILE_RASTER = "File_raster";

    static final String FILE_VECTOR = "File_vector";

    private static final String DB_RASTER = "DB_raster";

    private static final String DB_VECTOR = "DB_vector";

    private Properties allowedDatasources = new Properties();

    @Override
    public void init( ViewFormType viewForm )
                            throws Exception {
        super.init( viewForm );
        InputStream is = getClass().getResourceAsStream( "datasources.properties" );
        allowedDatasources.load( is );
        is.close();

        ApplicationContainer<Container> appContainer = this.owner.getApplicationContainer();
        this.mapModel = appContainer.getMapModel( null );

        Container pane = this.getContentPane();
        GridBagLayout gbl = new GridBagLayout();
        pane.setLayout( gbl );

        // InfoPane
        InfoPanel infoPane = new InfoPanel();
        infoPane.setInfoText( Messages.getMessage( Locale.getDefault(), "$MD10013" ) );
        infoPane.setVisible( true );
        SwingUtils.addComponent( pane, gbl, infoPane, 0, 0, 1, 1, 1, 0, GridBagConstraints.HORIZONTAL );

        // Seperator
        SwingUtils.addComponent( pane, gbl, new JSeparator(), 0, 1, 1, 1, 1, 0, GridBagConstraints.HORIZONTAL );

        // Datasources
        DefaultMutableTreeNode datasourcesRoot = new DefaultMutableTreeNode();

        String vectorGroupString = Messages.getMessage( Locale.getDefault(), "$MD10020" );
        DatasourceEntry vectorGroupEntry = new DatasourceEntry( vectorGroupString, true );
        DefaultMutableTreeNode vectorGroupNode = new DefaultMutableTreeNode( vectorGroupEntry );

        String rasterGroupLabel = Messages.getMessage( Locale.getDefault(), "$MD10021" );
        DatasourceEntry rasterGroupEntry = new DatasourceEntry( rasterGroupLabel, true );
        DefaultMutableTreeNode rasterGroupNode = new DefaultMutableTreeNode( rasterGroupEntry );

        String wmsLabel = Messages.getMessage( Locale.getDefault(), "$MD10014" );
        DatasourceEntry wmsEntry = new DatasourceEntry( "wmslayer_icon.png", wmsLabel, WMS, false );
        DefaultMutableTreeNode wmsNode = new DefaultMutableTreeNode( wmsEntry );

        String wcsLabel = Messages.getMessage( Locale.getDefault(), "$MD10015" );
        DatasourceEntry wcsEntry = new DatasourceEntry( "wcslayer_icon.png", wcsLabel, WCS, false );
        DefaultMutableTreeNode wcsNode = new DefaultMutableTreeNode( wcsEntry );

        String wfsLabel = Messages.getMessage( Locale.getDefault(), "$MD10016" );
        DatasourceEntry wfsEntry = new DatasourceEntry( "wfslayer_icon.png", wfsLabel, WFS, false );
        DefaultMutableTreeNode wfsNode = new DefaultMutableTreeNode( wfsEntry );

        String vectorFileLabel = Messages.getMessage( Locale.getDefault(), "$MD10017" );
        DatasourceEntry vectorFileEntry = new DatasourceEntry( "vector_filelayer_icon.png", vectorFileLabel,
                                                               FILE_VECTOR, false );
        DefaultMutableTreeNode vectorFileNode = new DefaultMutableTreeNode( vectorFileEntry );

        String rasterFileLabel = Messages.getMessage( Locale.getDefault(), "$MD10017" );
        DatasourceEntry rasterFileEntry = new DatasourceEntry( "raster_filelayer_icon.png", rasterFileLabel,
                                                               FILE_RASTER, false );
        DefaultMutableTreeNode rasterFileNode = new DefaultMutableTreeNode( rasterFileEntry );

        String memoryLabel = Messages.getMessage( Locale.getDefault(), "$MD10019" );
        DatasourceEntry memoryEntry = new DatasourceEntry( "memorylayer_icon.gif", memoryLabel, MEMORY, false );
        DefaultMutableTreeNode memoryNode = new DefaultMutableTreeNode( memoryEntry );

        String dbLabel = Messages.getMessage( Locale.getDefault(), "$MD10018" );
        DatasourceEntry vectordbEntry = new DatasourceEntry( "databaselayer_icon.gif", dbLabel, DB_VECTOR, false );
        DefaultMutableTreeNode vectorDatabaseNode = new DefaultMutableTreeNode( vectordbEntry );

        String rasterdbLabel = Messages.getMessage( Locale.getDefault(), "$MD10018" );
        DatasourceEntry rasterdbEntry = new DatasourceEntry( "databaselayer_icon.gif", rasterdbLabel, DB_RASTER, false );
        DefaultMutableTreeNode rasterDatabaseNode = new DefaultMutableTreeNode( rasterdbEntry );

        datasourcesRoot.add( memoryNode );
        datasourcesRoot.add( vectorGroupNode );
        datasourcesRoot.add( rasterGroupNode );

        if ( "true".equalsIgnoreCase( allowedDatasources.getProperty( "vector.database" ) ) ) {
            vectorGroupNode.add( vectorDatabaseNode );
        }
        if ( "true".equalsIgnoreCase( allowedDatasources.getProperty( "vector.file" ) ) ) {
            vectorGroupNode.add( vectorFileNode );
        }
        if ( "true".equalsIgnoreCase( allowedDatasources.getProperty( "vector.wfs" ) ) ) {
            vectorGroupNode.add( wfsNode );
        }
        if ( "true".equalsIgnoreCase( allowedDatasources.getProperty( "raster.database" ) ) ) {
            rasterGroupNode.add( rasterDatabaseNode );
        }
        if ( "true".equalsIgnoreCase( allowedDatasources.getProperty( "raster.file" ) ) ) {
            rasterGroupNode.add( rasterFileNode );
        }
        if ( "true".equalsIgnoreCase( allowedDatasources.getProperty( "raster.wms" ) ) ) {
            rasterGroupNode.add( wmsNode );
        }
        if ( "true".equalsIgnoreCase( allowedDatasources.getProperty( "raster.wcs" ) ) ) {
            rasterGroupNode.add( wcsNode );
        }

        datasourceTree = new JTree( datasourcesRoot );
        TreeSelectionModel treeSelectionModel = new DefaultTreeSelectionModel();
        treeSelectionModel.setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
        datasourceTree.setSelectionModel( treeSelectionModel );
        datasourceTree.setCellRenderer( new DatasourceEntryRenderer() );
        datasourceTree.setShowsRootHandles( true );
        datasourceTree.addTreeSelectionListener( this );
        datasourceTree.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
        datasourceTree.setVisible( true );
        JScrollPane treeScrollPane = new JScrollPane( datasourceTree, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        treeScrollPane.setMinimumSize( new Dimension( 100, 120 ) );
        SwingUtils.addComponent( pane, gbl, treeScrollPane, 0, 2, 1, 1, 0, 1, GridBagConstraints.BOTH );

        // Seperator
        SwingUtils.addComponent( pane, gbl, new JSeparator(), 0, 3, 1, 1, 1, 0, GridBagConstraints.HORIZONTAL );

        // Buttons
        buttons = new ButtonPanel();
        buttons.registerActionListener( this );
        buttons.setButtonEnabled( ButtonPanel.NEXT_BT, false );
        buttons.setButtonEnabled( ButtonPanel.PREVIOUS_BT, false );
        buttons.setButtonEnabled( ButtonPanel.FINISH_BT, false );
        SwingUtils.addComponent( pane, gbl, buttons, 0, 4, 1, 1, 1, 0, GridBagConstraints.HORIZONTAL );
        this.owner.getComponentStateAdapter().setClosed( true );
    }

    @Override
    public void windowOpened( WindowEvent e ) {
        // the state of this component will always be closed because it is not useful to have it
        // opened initially. So it a project will be stored the state will be closed even if the
        // Style editor is opened at this moment
        this.owner.getComponentStateAdapter().setClosed( true );
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // TreeSelectionListener
    // /////////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */
    public void valueChanged( TreeSelectionEvent event ) {
        // set next-button enabled, when a datasource is selected
        if ( event.getNewLeadSelectionPath() != null
             && event.getNewLeadSelectionPath().getLastPathComponent() instanceof DefaultMutableTreeNode ) {

            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) event.getNewLeadSelectionPath().getLastPathComponent();

            if ( treeNode.getUserObject() instanceof DatasourceEntry ) {
                DatasourceEntry entry = (DatasourceEntry) treeNode.getUserObject();
                if ( !entry.isGroup() ) {
                    buttons.setButtonEnabled( ButtonPanel.NEXT_BT, true );
                    // TODO: else if(){}
                } else {
                    buttons.setButtonEnabled( ButtonPanel.NEXT_BT, false );
                }

            }
        }

    }

    // /////////////////////////////////////////////////////////////////////////////////
    // ActionListener
    // /////////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed( ActionEvent event ) {
        JButton srcButton = (JButton) event.getSource();
        this.owner.getComponentStateAdapter().setClosed( true );
        if ( srcButton.getName().equals( ButtonPanel.CANCEL_BT ) ) {
            this.dispose();
        } else if ( srcButton.getName().equals( ButtonPanel.NEXT_BT ) ) {

            if ( datasourceTree.getSelectionPath() != null
                 && datasourceTree.getSelectionPath().getLastPathComponent() instanceof DefaultMutableTreeNode ) {

                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) datasourceTree.getSelectionPath().getLastPathComponent();

                if ( treeNode.getUserObject() instanceof DatasourceEntry ) {
                    DatasourceEntry entry = (DatasourceEntry) treeNode.getUserObject();

                    // open next dialog
                    nextStep = null;
                    if ( entry.getDatasourceName().equals( WCS ) ) {
                        nextStep = new AddWCSWizard( this, this.owner, this.mapModel );
                    } else if ( entry.getDatasourceName().equals( WMS ) ) {
                        nextStep = new AddWMSWizard( this, this.owner, this.mapModel );
                    } else if ( entry.getDatasourceName().equals( WFS ) ) {
                        nextStep = new AddWFSWizard( this, this.owner, this.mapModel );
                    } else if ( entry.getDatasourceName().equals( FILE_VECTOR ) ) {
                        nextStep = new AddFileSummary( this, this.owner, this.mapModel, FILE_VECTOR );
                    } else if ( entry.getDatasourceName().equals( FILE_RASTER ) ) {
                        nextStep = new AddFileSummary( this, this.owner, this.mapModel, FILE_RASTER );
                    } else if ( entry.getDatasourceName().equals( DB_VECTOR ) ) {
                        new AddDatabaseLayerDialog( owner.getApplicationContainer() );
                        dispose();
                    }
                    if ( nextStep != null ) {
                        addToFrontListener( nextStep );
                        nextStep.setLocation( this.getX(), this.getY() );
                        nextStep.setVisible( true );
                        this.setVisible( false );
                    }
                }
            }
        }
    }

    // //////////////////////////////////////////////////////////////////////////////////////////
    // inner class
    // //////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     * <code>DatasourceEntryRenderer</code>
     * 
     * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
     * @author last edited by: $Author:$
     * 
     * @version $Revision:$, $Date:$
     * 
     */
    private class DatasourceEntryRenderer extends DefaultTreeCellRenderer {

        private static final long serialVersionUID = 8919285589498809986L;

        @Override
        public Component getTreeCellRendererComponent( JTree tree, Object value, boolean sel, boolean expanded,
                                                       boolean leaf, int row, boolean hasFocus ) {
            super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus );
            this.setPreferredSize( new Dimension( 100, 25 ) );

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            if ( node.getUserObject() instanceof DatasourceEntry ) {

                DatasourceEntry datasourceEntry = (DatasourceEntry) node.getUserObject();

                String icon = datasourceEntry.getDatasourceIcon();
                if ( icon == null ) {
                    // icon = "/org/deegree/igeo/views/images/status1.gif";
                }
                if ( icon != null ) {
                    setIcon( IconRegistry.getIcon( icon ) );
                }
            }

            return this;
        }

    }
}
