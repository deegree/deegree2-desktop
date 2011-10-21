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
package org.deegree.igeo.views.swing.layerlist;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.ChangeListener;
import org.deegree.igeo.ValueChangedEvent;
import org.deegree.igeo.commands.ChangeMapModelEntryStateCommand;
import org.deegree.igeo.commands.ChangeMapModelEntryStateCommand.MAPMODELENTRYSTATE;
import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.LayerGroup;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MapModelChangedEvent;
import org.deegree.igeo.mapmodel.MapModelEntry;
import org.deegree.igeo.mapmodel.MapModelChangedEvent.CHANGE_TYPE;
import org.deegree.igeo.views.swing.DefaultPanel;
import org.deegree.igeo.views.swing.layerlist.CheckNode.NODE_TYPE;
import org.deegree.kernel.Command;

/**
 * 
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class LayerTreePanel extends DefaultPanel {

    private static final long serialVersionUID = -4578390323009236537L;

    private JSplitPane splPane;

    private JTextComponent taDescription;

    private JLabel lbLayer;

    private JPanel pnInfo;

    private JScrollPane scTree;

    private DnDJTree tree;

    private MapModel mapModel;

    private ApplicationContainer<Container> appContainer;

    private MapModelChangeListener mmCL = new MapModelChangeListener();

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#init(org.deegree.client.presenter.state.ComponentStateAdapter,
     * org.deegree.client.configuration.ViewForm)
     */
    public void init( ViewFormType viewForm )
                            throws Exception {
        this.appContainer = this.owner.getApplicationContainer();
        this.mapModel = appContainer.getMapModel( null );
        this.mapModel.addChangeListener( mmCL );
        initGUI();
        createTreeView();
    }

    @Override
    public void update() {
        super.update();

        MapModel tmp = appContainer.getMapModel( null );
        if ( this.mapModel == null || !tmp.equals( this.mapModel ) ) {
            this.mapModel = tmp;
            this.mapModel.removeChangeListener( mmCL );
            this.mapModel.addChangeListener( mmCL );
            initGUI();
            createTreeView();
        }

    }

    private void createTreeView() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        while ( root.getChildCount() > 0 ) {
            root.remove( 0 );
        }
        List<LayerGroup> list = mapModel.getLayerGroups();
        for ( LayerGroup node : list ) {
            addLayerGroupNodes( node, (DefaultMutableTreeNode) tree.getModel().getRoot() );
        }
        tree.expandRow( 0 );
        tree.expandRow( 1 );
    }

    private void addLayerNodes( Layer layer, DefaultMutableTreeNode parent ) {
        LayerNode cn = new LayerNode( layer, NODE_TYPE.unknown );
        cn.setSelected( layer.isVisible() );
        parent.add( cn );
        // addComplexContent( layer, cn, layer.isVisible() );
    }

    private void addLayerGroupNodes( LayerGroup layerGroup, DefaultMutableTreeNode parent ) {
        LayerGroupNode cn = new LayerGroupNode( layerGroup, null );
        cn.setSelected( layerGroup.isVisible() );
        parent.add( cn );
        List<MapModelEntry> entries = layerGroup.getMapModelEntries();
        for ( MapModelEntry entry : entries ) {
            if ( entry instanceof Layer ) {
                addLayerNodes( (Layer) entry, cn );
            } else {
                addLayerGroupNodes( (LayerGroup) entry, cn );
            }
        }
    }

    private void initGUI() {
        try {
            BorderLayout thisLayout = new BorderLayout();
            this.setLayout( thisLayout );
            this.setPreferredSize( new java.awt.Dimension( 335, 593 ) );
            {
                if ( splPane != null ) {
                    this.remove( splPane );
                }
                splPane = new JSplitPane();
                this.add( splPane, BorderLayout.CENTER );
                splPane.setOrientation( JSplitPane.VERTICAL_SPLIT );
                splPane.setPreferredSize( new java.awt.Dimension( 167, 300 ) );
                splPane.setRequestFocusEnabled( false );
                {
                    scTree = new JScrollPane();
                    splPane.add( scTree, JSplitPane.TOP );
                    scTree.setPreferredSize( new java.awt.Dimension( 333, 466 ) );
                    scTree.setSize( 200, 400 );
                    scTree.setMinimumSize( new java.awt.Dimension( 22, 250 ) );
                    {
                        DefaultMutableTreeNode root = new DefaultMutableTreeNode( mapModel.getName() );
                        tree = new DnDJTree( root, appContainer, this.mapModel );
                        tree.getSelectionModel().setSelectionMode( TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION );
                        tree.putClientProperty( "JTree.lineStyle", "Angled" );
                        tree.addMouseListener( new NodeMouseListener( tree ) );
                        scTree.setViewportView( tree );
                    }
                }
                {
                    pnInfo = new JPanel();
                    GridBagLayout pnInfoLayout = new GridBagLayout();
                    splPane.add( pnInfo, JSplitPane.BOTTOM );
                    pnInfo.setPreferredSize( new java.awt.Dimension( 333, 113 ) );
                    pnInfo.setSize( 333, 134 );
                    pnInfoLayout.rowWeights = new double[] { 0.0, 0.1 };
                    pnInfoLayout.rowHeights = new int[] { 35, 7 };
                    pnInfoLayout.columnWeights = new double[] { 0.1 };
                    pnInfoLayout.columnWidths = new int[] { 7 };
                    pnInfo.setLayout( pnInfoLayout );
                    {
                        lbLayer = new JLabel();
                        pnInfo.add( lbLayer, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                     GridBagConstraints.HORIZONTAL, new Insets( 0, 10,
                                                                                                                0, 0 ),
                                                                     0, 0 ) );
                        lbLayer.setText( Messages.getMessage( getLocale(), "$MD10007" ) );
                    }
                    {
                        taDescription = new JTextPane();
                        taDescription.setEditable( false );
                        JScrollPane sc = new JScrollPane();
                        sc.setViewportView( taDescription );
                        pnInfo.add( sc, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                GridBagConstraints.BOTH, new Insets( 5, 10, 5, 10 ), 0,
                                                                0 ) );
                    }
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * sets the adapter for a layer which info shall be displayed
     * 
     * @param mapModelEntry
     */
    private void setMapModelEntry( MapModelEntry mapModelEntry ) {
        LayerInfoFrame.getInstance( appContainer ).setLayer( mapModelEntry );
        lbLayer.setText( mapModelEntry.getTitle() );
        String tmp = mapModelEntry.getAbstract();
        if ( tmp != null && tmp.trim().length() > 0 ) {
            taDescription.setText( tmp );
        } else {
            taDescription.setText( "no abstract" );
        }

    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // inner classes //
    // /////////////////////////////////////////////////////////////////////////////////////

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version. $Revision$, $Date$
     */
    class MapModelChangeListener implements ChangeListener {

        /*
         * (non-Javadoc)
         * 
         * @see org.deegree.client.presenter.ChangeListener#valueChanged(org.deegree.client.presenter.ValueChangedEvent)
         */
        public void valueChanged( ValueChangedEvent event ) {
            synchronized ( tree ) {
                CHANGE_TYPE ct = ( (MapModelChangedEvent) event ).getChangeType();
                if ( ct == CHANGE_TYPE.layerInserted ) {
                    MapModelEntry entry = (MapModelEntry) ( (MapModelChangedEvent) event ).getValue();
                    insertLayerNode( (Layer) entry );
                } else if ( ct == CHANGE_TYPE.layerGroupInserted ) {
                    MapModelEntry entry = (MapModelEntry) ( (MapModelChangedEvent) event ).getValue();
                    insertLayerGroupNode( (LayerGroup) entry );
                } else if ( ct == CHANGE_TYPE.layerRemoved || ct == CHANGE_TYPE.layerGroupRemoved ) {
                    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                    DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
                    MapModelEntry entry = (MapModelEntry) ( (MapModelChangedEvent) event ).getValue();
                    removeChild( model, root, entry );
                } else if ( ct == CHANGE_TYPE.layerStateChanged ) {
                    tree.revalidate();
                    tree.repaint();
                }

                owner.update();
            }
        }

        private void insertLayerGroupNode( LayerGroup layerGroup ) {
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
            DefaultMutableTreeNode ln = findParent( root, layerGroup );
            if ( ln != null ) {
                root = ln;
            }
            CheckNode cn = new LayerGroupNode( layerGroup, NODE_TYPE.layerGroup );
            cn.setSelected( true );
            int index = getAntecessorIndex( layerGroup );

            if ( index < 0 ) {
                index = root.getChildCount();
            } else if ( index > root.getChildCount() ) {
                index = root.getChildCount();
            }
            model.insertNodeInto( cn, root, index );
            List<MapModelEntry> entries = layerGroup.getMapModelEntries();
            for ( MapModelEntry entry : entries ) {
                if ( entry instanceof Layer ) {
                    insertLayerNode( (Layer) entry );
                } else if ( entry instanceof LayerGroup ) {
                    insertLayerGroupNode( (LayerGroup) entry );
                }
            }
        }

        private void insertLayerNode( Layer layer ) {
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
            DefaultMutableTreeNode ln = findParent( root, layer );
            if ( ln != null ) {
                root = ln;
            }
            CheckNode cn = new LayerNode( layer, NODE_TYPE.unknown );
            cn.setSelected( true );
            int index = getAntecessorIndex( layer );

            if ( index < 0 ) {
                index = root.getChildCount();
            } else if ( index > root.getChildCount() ) {
                index = root.getChildCount();
            }
            model.insertNodeInto( cn, root, index );
            // addComplexContent( la, cn, la.isVisible() );

        }

        /**
         * 
         * @param root
         * @param entry
         * @return assigned treenode of a layers parent
         */
        private DefaultMutableTreeNode findParent( DefaultMutableTreeNode root, MapModelEntry entry ) {
            if ( root.getUserObject() instanceof LayerGroup ) {
                LayerGroup la = (LayerGroup) root.getUserObject();
                if ( la.equals( entry.getParent() ) ) {
                    return root;
                }
            }
            int cnt = root.getChildCount();
            for ( int i = 0; i < cnt; i++ ) {
                TreeNode child = root.getChildAt( i );
                if ( child instanceof CheckNode ) {
                    DefaultMutableTreeNode ln = findParent( (DefaultMutableTreeNode) child, entry );
                    if ( ln != null ) {
                        return ln;
                    }
                }
            }
            return null;
        }

        /**
         * 
         * @param entry
         * @return index of the antecessore layer underneath the parent of the passed layer
         */
        private int getAntecessorIndex( MapModelEntry entry ) {
            if ( entry.getParent() != null ) {
                List<MapModelEntry> entries = entry.getParent().getMapModelEntries();
                for ( int i = 0; i < entries.size(); i++ ) {
                    if ( entries.get( i ).equals( entry ) ) {
                        return i;
                    }
                }
            }
            return -1;
        }

        /**
         * 
         * @param model
         * @param parent
         * @param mapModelEntry
         */
        private void removeChild( DefaultTreeModel model, DefaultMutableTreeNode parent, MapModelEntry mapModelEntry ) {
            int cc = parent.getChildCount();
            for ( int i = 0; i < cc; i++ ) {
                DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) parent.getChildAt( i );
                if ( mapModelEntry.equals( dmt.getUserObject() ) ) {
                    model.removeNodeFromParent( dmt );
                    return;
                } else {
                    removeChild( model, dmt, mapModelEntry );
                }
            }
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
    class NodeMouseListener extends MouseAdapter {

        private JTree tree;

        /**
         * 
         * @param tree
         */
        NodeMouseListener( JTree tree ) {
            this.tree = tree;
        }

        private void handleSelectFor( CheckNode node, String s, int modifier ) {            
            node.setSelectedFor( s, !node.isSelectedFor( s ), KeyEvent.VK_CONTROL != modifier );
            
            if ( node.isSelectedFor( s ) ) {
                if ( KeyEvent.VK_CONTROL != modifier ) {
                    // remove selection for XXXX from all nodes
                    removeSelectedForFromAllLAyers( s, (DefaultMutableTreeNode) node.getRoot() );
                }
                // mark layer as being selected for XXXX
                if ( node.getUserObject() != null && node.getUserObject() instanceof MapModelEntry ) {
                    ( (MapModelEntry) node.getUserObject() ).addSelectedFor( s );
                }
            }
        }

        private void removeSelectedForFromAllLAyers( String selectedFor, DefaultMutableTreeNode node ) {
            if ( node.getUserObject() != null && node.getUserObject() instanceof MapModelEntry ) {
                ( (MapModelEntry) node.getUserObject() ).removeSelectedFor( selectedFor );
            }

            int count = node.getChildCount();
            for ( int i = 0; i < count; i++ ) {
                if ( node.getChildAt( i ) instanceof DefaultMutableTreeNode ) {
                    removeSelectedForFromAllLAyers( selectedFor, (DefaultMutableTreeNode) node.getChildAt( i ) );
                }
            }
        }

        /**
         * expands or collapses a tree path depending on selection status of the node receiving a mouse click event
         */
        @Override
        public void mouseReleased( MouseEvent e ) {

            if ( e.getButton() == MouseEvent.BUTTON1 ) {
                int x = e.getX();
                int y = e.getY();
                int row = tree.getRowForLocation( x, y );
                TreePath path = tree.getPathForRow( row );
                if ( tree.getRowBounds( row ) != null ) {
                    int min = tree.getRowBounds( row ).x;
                    if ( path != null ) {
                        Object o = path.getLastPathComponent();
                        if ( o instanceof CheckNode ) {
                            CheckNode node = (CheckNode) path.getLastPathComponent();
                            if ( x < min + 15 ) {
                                boolean isSelected = !( node.isSelected() );
                                ( (DefaultTreeModel) tree.getModel() ).nodeChanged( node );
                                switchVisibility( isSelected, (MapModelEntry) node.getUserObject() );
                                traverseDown( node, isSelected );
                                traverseUp( (MapModelEntry) node.getUserObject() );
                            }
                            // call back to owner Panel
                            setMapModelEntry( (MapModelEntry) node.getUserObject() );
                            handleSelectFor( node, MapModel.SELECTION_ACTION, e.getModifiers() );
                            if ( row == 0 ) {
                                tree.revalidate();
                                tree.repaint();
                            }
                        }
                    }
                }
            } else {
                maybeShowPopup( e );
            }
        }

        /**
         * ensures that all nodes on path to root are switched to invisible/not selected if all of child nodes are
         * invisible/not selected and vice versa
         * 
         * @param mme
         * @param selected
         */
        private void traverseUp( MapModelEntry mme ) {
            LayerGroup lg = mme.getParent();
            if ( lg != null ) {
                List<MapModelEntry> list = lg.getMapModelEntries();
                boolean vis = traverse( list, false );
                if ( vis != lg.isVisible() ) {
                    switchVisibility( vis, lg );
                    traverseUp( lg );
                }
            }
        }

        private boolean traverse( List<MapModelEntry> list, boolean vis ) {
            for ( MapModelEntry mapModelEntry : list ) {
                if ( mapModelEntry.isVisible() ) {
                    return true;
                }
                if ( mapModelEntry instanceof LayerGroup ) {
                    vis = traverse( ( (LayerGroup) mapModelEntry ).getMapModelEntries(), vis );
                    if ( vis ) {
                        return true;
                    }
                }
            }
            return vis;
        }

        /**
         * ensures that all nodes underneath the passed {@link MapModelEntry} are switch to the same visibility if the
         * passed {@link MapModelEntry} is a LayerGroup
         * 
         * @param mme
         * @param selected
         */
        private void traverseDown( CheckNode node, boolean selected ) {
            MapModelEntry mme = (MapModelEntry) node.getUserObject();
            int cnt = node.getChildCount();
            if ( mme instanceof LayerGroup ) {
                for ( int i = 0; i < cnt; i++ ) {
                    CheckNode child = (CheckNode) node.getChildAt( i );
                    MapModelEntry mapModelEntry = (MapModelEntry) child.getUserObject();
                    if ( mapModelEntry.isVisible() != selected ) {
                        // child.setSelected( selected );
                        switchVisibility( selected, mapModelEntry );
                    }
                    if ( mapModelEntry instanceof LayerGroup ) {
                        traverseDown( child, selected );
                    }
                }

            }

        }

        private void switchVisibility( boolean selected, MapModelEntry mapModelEntry ) {
            Command command = new ChangeMapModelEntryStateCommand( mapModelEntry, MAPMODELENTRYSTATE.visibility,
                                                                   selected );
            try {
                appContainer.getCommandProcessor().executeSychronously( command, true );
            } catch ( Exception e1 ) {
                e1.printStackTrace();
            }
        }

        @Override
        public void mousePressed( MouseEvent e ) {
            maybeShowPopup( e );
        }

        private void maybeShowPopup( MouseEvent e ) {

            if ( e.isPopupTrigger() ) {
                JMenuItem item = getMenuItemByActionName( "setVisibility" );
                if ( item != null ) {
                    List<MapModelEntry> mapModelEntries = mapModel.getMapModelEntriesSelectedForAction( MapModel.SELECTION_ACTION );
                    for ( MapModelEntry mapModelEntry : mapModelEntries ) {
                        item.setSelected( mapModelEntry.isVisible() );
                    }
                }
                popup.show( e.getComponent(), e.getX(), e.getY() );
            }
        }

    }

}
