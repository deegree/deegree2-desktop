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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.deegree.framework.utils.SwingUtils;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.model.AddWMSLayerCommand;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.ogcwebservices.wms.capabilities.Layer;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.owscommon_new.ServiceIdentification;

/**
 * The <code>JAddWMSChooseLayerFrame</code> describes the second step to add a WMS Datasource as layer. It contains the
 * possibility to choose the desired layers of a wms.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class AddWMSWizardChooseLayer extends WizardDialog implements TreeSelectionListener, ActionListener,
                                                         TreeModelListener {

    private static final long serialVersionUID = -992388656162061596L;

    private static final String SEL_ALL_BT = "selectAll";

    private static final String SEL_SELECTED_BT = "selectSelected";

    private static final String DESEL_ALL_BT = "deselectAll";

    private static final String DESEL_SELECTED_BT = "deselectSelected";

    private static final String UP_BT = "layerUp";

    private static final String DOWN_BT = "layerDown";

    private MapModel mapModel;

    private WMSCapabilities wmsCaps;

    private JTree availableLayers;

    private DefaultMutableTreeNode availableLayersRoot;

    private JTree selectedLayers;

    private DefaultTreeModel selectedLayersModel;

    private DefaultMutableTreeNode selectedLayersRoot;

    private JButton selectSelected;

    private JButton deselectSelected;

    private JButton deselectAll;

    private JButton layerUp;

    private JButton layerDown;

    private Map<Layer, Object[]> selectedNodes = new HashMap<Layer, Object[]>();

    /**
     * @param frame
     *            the previous dialog
     * @param appContainer
     *            the application container
     * @param mapModel
     *            the mapModel
     * @param wmsCapabilities
     *            the capabilities of the requested wms
     */
    public AddWMSWizardChooseLayer( JFrame frame, MapModel mapModel, ApplicationContainer<Container> appContainer,
                                    WMSCapabilities wmsCapabilities) {
        super( frame );
        this.mapModel = mapModel;
        this.appContainer = appContainer;
        this.wmsCaps = wmsCapabilities;

        this.setSize( 750, 600 );
        this.setResizable( false );
        this.setTitle( Messages.getMessage( Locale.getDefault(), "$MD10029" ) );

        String wmsTitle = wmsCapabilities.getServiceIdentification().getTitle();
        this.infoPanel.setInfoText( Messages.getMessage( Locale.getDefault(), "$MD10030", wmsTitle ) );

        this.buttonPanel.registerActionListener( this );
        this.buttonPanel.setButtonEnabled( ButtonPanel.NEXT_BT, false );
        this.buttonPanel.setButtonEnabled( ButtonPanel.FINISH_BT, false );
        super.init();
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // WizardDialog
    // /////////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.WizardDialog#getMainPanel()
     */
    @Override
    public JPanel getMainPanel() {
        Layer availableRootLayer = wmsCaps.getLayer();

        // size of the two tree components
        Dimension treeDim = new Dimension( 300, 350 );

        // tree, containing all available layers described in the wms capabilities
        availableLayersRoot = createTreeNode( availableRootLayer );
        availableLayers = new JTree( availableLayersRoot );
        availableLayers.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        availableLayers.addTreeSelectionListener( this );
        availableLayers.setCellRenderer( new WMSLayerRenderer() );

        JScrollPane availableLayersScroll = new JScrollPane( availableLayers );
        availableLayersScroll.setPreferredSize( treeDim );
        availableLayersScroll.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        availableLayersScroll.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );

        // the buttons to select/deselect layers
        Dimension selectBtDim = new Dimension( 55, 25 );
        selectSelected = new JButton( IconRegistry.getIcon( "/org/deegree/igeo/views/images/for_green.png" ) );
        selectSelected.setName( SEL_SELECTED_BT );
        selectSelected.addActionListener( this );
        selectSelected.setEnabled( false );
        selectSelected.setPreferredSize( selectBtDim );
        JButton selectAll = new JButton( IconRegistry.getIcon( "/org/deegree/igeo/views/images/forward_green.png" ) );
        selectAll.setName( SEL_ALL_BT );
        selectAll.addActionListener( this );
        selectAll.setPreferredSize( selectBtDim );
        deselectAll = new JButton( IconRegistry.getIcon( "/org/deegree/igeo/views/images/backward_green.png" ) );
        deselectAll.setName( DESEL_ALL_BT );
        deselectAll.addActionListener( this );
        deselectAll.setEnabled( false );
        deselectAll.setPreferredSize( selectBtDim );
        deselectSelected = new JButton( IconRegistry.getIcon( "/org/deegree/igeo/views/images/back_green.png" ) );
        deselectSelected.setName( DESEL_SELECTED_BT );
        deselectSelected.addActionListener( this );
        deselectSelected.setEnabled( false );
        deselectSelected.setPreferredSize( selectBtDim );

        // add select/deselect buttons to a panel
        JPanel selectBtPanel = new JPanel();
        GridBagConstraints gbcBt = SwingUtils.initPanel( selectBtPanel );
        selectBtPanel.add( selectSelected, gbcBt );
        ++gbcBt.gridy;
        selectBtPanel.add( selectAll, gbcBt );
        gbcBt.insets = new Insets( 10, 2, 2, 2 );
        ++gbcBt.gridy;
        selectBtPanel.add( deselectAll, gbcBt );
        gbcBt.insets = new Insets( 2, 2, 2, 2 );
        ++gbcBt.gridy;
        selectBtPanel.add( deselectSelected, gbcBt );

        // tree which contains all layers selected by the user
        selectedLayersRoot = new DefaultMutableTreeNode();
        selectedLayersModel = new DefaultTreeModel( selectedLayersRoot );
        selectedLayersModel.addTreeModelListener( this );
        selectedLayers = new JTree( selectedLayersModel );
        selectedLayers.setRootVisible( false );
        selectedLayers.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        selectedLayers.addTreeSelectionListener( this );
        selectedLayers.setCellRenderer( new WMSLayerRenderer() );
        selectedLayers.setVisible( true );

        JScrollPane selectedLayersScroll = new JScrollPane( selectedLayers );
        selectedLayersScroll.setPreferredSize( treeDim );
        selectedLayersScroll.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        selectedLayersScroll.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );

        // the buttons to change the order of the layers
        Dimension orderBtDim = new Dimension( 30, 50 );
        ImageIcon iconUp = new ImageIcon( this.getClass().getResource( "/org/deegree/igeo/views/images/arrow_up.png" ) );
        layerUp = new JButton( iconUp );
        layerUp.setName( UP_BT );
        layerUp.setPreferredSize( orderBtDim );
        layerUp.addActionListener( this );
        layerUp.setEnabled( false );

        ImageIcon iconDown = new ImageIcon(
                                            this.getClass().getResource(
                                                                         "/org/deegree/igeo/views/images/arrow_down.png" ) );
        layerDown = new JButton( iconDown );
        layerDown.setName( DOWN_BT );
        layerDown.setPreferredSize( orderBtDim );
        layerDown.addActionListener( this );
        layerDown.setEnabled( false );

        // add buttons to change the order of the layers
        JPanel orderBtPanel = new JPanel();
        GridBagConstraints gbcOrderBt = SwingUtils.initPanel( orderBtPanel );

        orderBtPanel.add( layerUp, gbcOrderBt );
        ++gbcOrderBt.gridy;
        orderBtPanel.add( layerDown, gbcOrderBt );

        // add components of this dialog to the panel
        JPanel layerList = new JPanel();
        GridBagConstraints gbc = SwingUtils.initPanel( layerList );

        layerList.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10031" ) ), gbc );
        gbc.gridx = gbc.gridx + 2;
        layerList.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10032" ) ), gbc );
        ++gbc.gridy;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        layerList.add( availableLayersScroll, gbc );
        ++gbc.gridx;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        layerList.add( selectBtPanel, gbc );
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        ++gbc.gridx;
        layerList.add( selectedLayersScroll, gbc );
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        ++gbc.gridx;
        layerList.add( orderBtPanel, gbc );

        layerList.setVisible( true );

        ToolTipManager.sharedInstance().registerComponent( availableLayers );
        ToolTipManager.sharedInstance().registerComponent( selectedLayers );

        return layerList;
    }

    /**
     * @param rootLayer
     *            the wms layer to create a node
     */
    private DefaultMutableTreeNode createTreeNode( Layer rootLayer ) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode( rootLayer );
        Layer[] childLayers = rootLayer.getLayer();
        for ( int i = 0; i < childLayers.length; i++ ) {
            node.add( createTreeNode( childLayers[i] ) );
        }
        return node;
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
        if ( event.getSource() instanceof JButton ) {
            JButton srcButton = (JButton) event.getSource();
            if ( srcButton.getName().equals( ButtonPanel.CANCEL_BT ) ) {
                this.dispose();
            } else if ( srcButton.getName().equals( ButtonPanel.PREVIOUS_BT ) ) {
                if ( this.previousFrame != null ) {
                    this.previousFrame.setVisible( true );
                }
                this.close();
            } else if ( srcButton.getName().equals( ButtonPanel.NEXT_BT ) ) {

                AddWMSWizardChooseStyles nextStep = new AddWMSWizardChooseStyles( this, this.mapModel,
                                                                                  this.appContainer, this.wmsCaps,
                                                                                  getSelectedLayers(), getLayerTitle() );
                addToFrontListener( nextStep );
                nextStep.setLocation( this.getX(), this.getY() );
                nextStep.setVisible( true );
                this.setVisible( false );
            } else if ( srcButton.getName().equals( ButtonPanel.FINISH_BT ) ) {
                AddWMSLayerCommand addWMSCmd = new AddWMSLayerCommand( this.mapModel, this.wmsCaps,
                                                                       getSelectedLayers(), this.getLayerTitle() );
                appContainer.getCommandProcessor().executeASychronously( addWMSCmd );
                this.dispose();
            } else if ( srcButton.getName().equals( SEL_SELECTED_BT ) ) {
                TreePath[] layers = this.availableLayers.getSelectionPaths();
                for ( int i = 0; i < layers.length; i++ ) {
                    if ( layers[i].getLastPathComponent() instanceof DefaultMutableTreeNode ) {
                        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) layers[i].getLastPathComponent();
                        Layer layer = (Layer) selectedNode.getUserObject();
                        appendLayerToRoot( layer, this.selectedLayersModel );
                        appendChilds( selectedNode, this.selectedLayersModel );
                        this.selectedNodes.put( layer, layers[i].getPath() );
                    }
                }
                // make changes visible!
                this.selectedLayers.setRootVisible( true );
                this.selectedLayers.expandRow( 0 );
                this.selectedLayers.setRootVisible( false );
            } else if ( srcButton.getName().equals( SEL_ALL_BT ) ) {
                appendChilds( this.availableLayersRoot, this.selectedLayersModel );
                // make changes visible!
                this.selectedLayers.setRootVisible( true );
                this.selectedLayers.expandRow( 0 );
                this.selectedLayers.setRootVisible( false );
            } else if ( srcButton.getName().equals( DESEL_ALL_BT ) ) {
                int childCount = this.selectedLayersRoot.getChildCount();
                for ( int i = 0; i < childCount; i++ ) {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) this.selectedLayersRoot.getChildAt( 0 );
                    this.selectedLayersModel.removeNodeFromParent( child );
                }
                this.selectedNodes.clear();
            } else if ( srcButton.getName().equals( DESEL_SELECTED_BT ) ) {
                TreePath[] layers = this.selectedLayers.getSelectionPaths();
                for ( int i = 0; i < layers.length; i++ ) {
                    if ( layers[i].getLastPathComponent() instanceof DefaultMutableTreeNode ) {
                        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) layers[i].getLastPathComponent();
                        this.selectedLayersModel.removeNodeFromParent( selectedNode );
                        if ( selectedNode.getUserObject() instanceof Layer ) {
                            this.selectedNodes.remove( selectedNode.getUserObject() );
                        }
                    }
                }
            } else if ( srcButton.getName().equals( UP_BT ) ) {
                moveLayerUp();
            } else if ( srcButton.getName().equals( DOWN_BT ) ) {
                moveLayerDown();
            }
        }
    }

    /**
     * @return the layers selected by the user
     */
    private List<Layer> getSelectedLayers() {
        List<Layer> layers = new ArrayList<Layer>( this.selectedLayersRoot.getChildCount() );
        for ( int i = 0; i < this.selectedLayersRoot.getChildCount(); i++ ) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) this.selectedLayersRoot.getChildAt( i );
            if ( child.getUserObject() instanceof Layer ) {
                layers.add( (Layer) child.getUserObject() );
            }
        }
        return layers;
    }

    /**
     * @return the title of the layer - depends of the selected layers
     */
    private String getLayerTitle() {

        DefaultMutableTreeNode tmpHighest = null;
        for ( Layer l : this.selectedNodes.keySet() ) {
            Object[] nodes = this.selectedNodes.get( l );
            if ( nodes[nodes.length - 1] instanceof DefaultMutableTreeNode ) {
                DefaultMutableTreeNode n = (DefaultMutableTreeNode) nodes[nodes.length - 1];

                if ( n.isRoot() ) {
                    tmpHighest = n;
                    break;
                }
                // initial: set first node to tmpNode
                if ( tmpHighest == null ) {
                    tmpHighest = n;
                } else {
                    // if currentNode is ancestor of the tmpNode, tmpNode must be n
                    if ( tmpHighest.isNodeAncestor( n ) ) {
                        tmpHighest = n;
                        // if currentNode is neither ancestor nor desendant of the tmpNode, find
                        // common node
                    } else if ( !tmpHighest.isNodeDescendant( n ) ) {
                        while ( !tmpHighest.isRoot() ) {
                            tmpHighest = (DefaultMutableTreeNode) tmpHighest.getParent();
                            if ( tmpHighest.isNodeDescendant( n ) ) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        ServiceIdentification service = this.wmsCaps.getServiceIdentification();

        String title = service.getTitle();
        if ( tmpHighest != null ) {
            title = ( (Layer) tmpHighest.getUserObject() ).getTitle();
        }
        return title;
    }

    /**
     * append all child nodes of the source node to the model
     * 
     * @param sourceNode
     *            node to add
     * @param model
     *            the model to add the node
     */
    private void appendChilds( DefaultMutableTreeNode sourceNode, DefaultTreeModel model ) {
        for ( int i = 0; i < sourceNode.getChildCount(); i++ ) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) sourceNode.getChildAt( i );
            if ( child.isLeaf() ) {
                if ( child.getUserObject() instanceof Layer ) {
                    Layer layer = (Layer) child.getUserObject();
                    appendLayerToRoot( layer, model );
                    this.selectedNodes.put( layer, child.getPath() );
                }
            } else {
                appendChilds( child, model );
            }
        }
    }

    /**
     * append the layer as node to the model
     * 
     * @param layer
     *            the layer to append as node
     * @param model
     *            the model to add the node
     */
    private void appendLayerToRoot( Layer layer, DefaultTreeModel model ) {
        DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) model.getRoot();
        if ( layer.getName() != null && layer.getName().length() > 0 ) {
            boolean isSelected = false;
            for ( int i = 0; i < targetNode.getChildCount(); i++ ) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) targetNode.getChildAt( i );
                if ( child.getUserObject() instanceof Layer ) {
                    Layer childLayer = (Layer) child.getUserObject();
                    if ( childLayer.getName().equals( layer.getName() )
                         || childLayer.getTitle().equals( layer.getTitle() ) )
                        isSelected = true;
                }
            }
            if ( !isSelected ) {
                model.insertNodeInto( new DefaultMutableTreeNode( layer ), targetNode, targetNode.getChildCount() );
            }
        }

    }

    /**
     * move selected layer one entry up
     */
    private void moveLayerUp() {
        TreePath selLayer = this.selectedLayers.getSelectionPath();
        int row = this.selectedLayers.getSelectionRows()[0];
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selLayer.getLastPathComponent();
        if ( row > 0 ) {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) node.getRoot();
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( node.getUserObject() );
            this.selectedLayersModel.insertNodeInto( newNode, root, row - 1 );
            TreePath tp = this.selectedLayers.getPathForRow( row - 1 );
            this.selectedLayers.setSelectionPath( tp );
            this.selectedLayersModel.removeNodeFromParent( node );
        }
    }

    /**
     * move selected layer one entry down
     */
    private void moveLayerDown() {
        TreePath selLayer = this.selectedLayers.getSelectionPath();
        int row = this.selectedLayers.getSelectionRows()[0];
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selLayer.getLastPathComponent();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) node.getRoot();
        if ( row < root.getChildCount() - 1 ) {
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( node.getUserObject() );
            this.selectedLayersModel.insertNodeInto( newNode, root, row + 2 );
            TreePath tp = this.selectedLayers.getPathForRow( row + 2 );
            this.selectedLayers.setSelectionPath( tp );
            this.selectedLayersModel.removeNodeFromParent( node );
        }

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
        // enable/disable select-button
        if ( this.availableLayers.getSelectionCount() > 0 ) {
            this.selectSelected.setEnabled( true );
        } else {
            this.selectSelected.setEnabled( false );
        }

        // enable/disable deselect-button
        if ( this.selectedLayers.getSelectionCount() > 0 ) {
            this.deselectSelected.setEnabled( true );
            if ( this.selectedLayers.getSelectionCount() == 1 ) {
                this.layerUp.setEnabled( true );
                this.layerDown.setEnabled( true );
            } else {
                this.layerUp.setEnabled( false );
                this.layerDown.setEnabled( false );
            }
        } else {
            this.deselectSelected.setEnabled( false );
            this.layerUp.setEnabled( false );
            this.layerDown.setEnabled( false );
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // TreeModelListener
    // /////////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.TreeModelListener#treeNodesChanged(javax.swing.event.TreeModelEvent)
     */
    public void treeNodesChanged( TreeModelEvent arg0 ) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.TreeModelListener#treeNodesInserted(javax.swing.event.TreeModelEvent)
     */
    public void treeNodesInserted( TreeModelEvent arg0 ) {
        // at least one layer must be selected -> enable deselectAll-button and next-button
        this.deselectAll.setEnabled( true );
        this.buttonPanel.setButtonEnabled( ButtonPanel.NEXT_BT, true );
        this.buttonPanel.setButtonEnabled( ButtonPanel.FINISH_BT, true );
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.TreeModelListener#treeNodesRemoved(javax.swing.event.TreeModelEvent)
     */
    public void treeNodesRemoved( TreeModelEvent arg0 ) {
        // disable deselectAll-button and next-button if no layers are selected
        if ( this.selectedLayers.getRowCount() < 1 ) {
            this.deselectAll.setEnabled( false );
            this.buttonPanel.setButtonEnabled( ButtonPanel.NEXT_BT, false );
            this.buttonPanel.setButtonEnabled( ButtonPanel.FINISH_BT, false );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.TreeModelListener#treeStructureChanged(javax.swing.event.TreeModelEvent)
     */
    public void treeStructureChanged( TreeModelEvent arg0 ) {
    }

}
