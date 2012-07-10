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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
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
import org.deegree.igeo.commands.model.AddWCSLayerCommand;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.views.swing.util.GuiUtils;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.ogcwebservices.wcs.CoverageOfferingBrief;
import org.deegree.ogcwebservices.wcs.getcapabilities.WCSCapabilities;
import org.deegree.ogcwebservices.wms.capabilities.Layer;

/**
 * The <code>JAddWCSChooseLayerFrame</code> describes the second step to add a WCS datasource as layer. It contains the
 * possibility to choose the desired layers of a WCS.<br>
 * At the moment just selection of one coverage is possible, but this will be changed in future versions of this class
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class AddWCSWizardChooseCoverage extends WizardDialog implements TreeSelectionListener, ActionListener,
                                                            TreeModelListener {

    private static final long serialVersionUID = 8507723159013643420L;

    private static final String SEL_ALL_BT = "selectAll";

    private static final String SEL_SELECTED_BT = "selectSelected";

    private static final String DESEL_ALL_BT = "deselectAll";

    private static final String DESEL_SELECTED_BT = "deselectSelected";

    private static final String UP_BT = "layerUp";

    private static final String DOWN_BT = "layerDown";

    private MapModel mapModel;

    private WCSCapabilities wcsCaps;

    private URL capabilitiesURL;

    private JTree trAvailableCoverages;

    private DefaultMutableTreeNode availableCoveragesRoot;

    private JTree trSelectedCoverages;

    private DefaultTreeModel selectedCoveragesModel;

    private DefaultMutableTreeNode selectedCoveragesRoot;

    private JButton btSelectSelected;

    private JButton btDeselectSelected;

    private JButton btDeselectAll;

    private JButton btCoverageUp;

    private JButton btCoverageDown;

    private JButton btSelectAll;

    private Map<CoverageOfferingBrief, Object[]> selectedNodes = new HashMap<CoverageOfferingBrief, Object[]>();

    /**
     * @param frame
     *            the previous dialog
     * @param appContainer
     *            the application container
     * @param mapModel
     *            the mapModel
     * @param wcsCapabilities
     *            the capabilities of the requested WCS
     * @param capabilitiesURL
     */
    public AddWCSWizardChooseCoverage( JFrame frame, MapModel mapModel, ApplicationContainer<Container> appContainer,
                                       WCSCapabilities wcsCapabilities, URL capabilitiesURL ) {
        super( frame );
        this.mapModel = mapModel;
        this.appContainer = appContainer;
        this.wcsCaps = wcsCapabilities;
        this.capabilitiesURL = capabilitiesURL;

        this.setSize( 750, 600 );
        this.setResizable( false );
        this.setTitle( Messages.getMessage( Locale.getDefault(), "$MD11377" ) );

        String wcsTitle = wcsCapabilities.getService().getLabel();
        this.infoPanel.setInfoText( Messages.getMessage( Locale.getDefault(), "$MD11378", wcsTitle ) );

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
        CoverageOfferingBrief[] coverageOfferings = wcsCaps.getContentMetadata().getCoverageOfferingBrief();

        // size of the two tree components
        Dimension treeDim = new Dimension( 300, 350 );

        // tree, containing all available layers described in the wcs capabilities
        availableCoveragesRoot = createTreeNode( coverageOfferings );
        trAvailableCoverages = new JTree( availableCoveragesRoot );
        trAvailableCoverages.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        trAvailableCoverages.addTreeSelectionListener( this );
        trAvailableCoverages.setCellRenderer( new WCSCoverageRenderer() );

        JScrollPane availableLayersScroll = new JScrollPane( trAvailableCoverages );
        availableLayersScroll.setPreferredSize( treeDim );
        availableLayersScroll.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        availableLayersScroll.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );

        // the buttons to select/deselect layers
        Dimension selectBtDim = new Dimension( 55, 25 );
        btSelectSelected = new JButton( IconRegistry.getIcon( "for_green.png" ) );
        btSelectSelected.setName( SEL_SELECTED_BT );
        btSelectSelected.addActionListener( this );
        btSelectSelected.setEnabled( false );
        btSelectSelected.setPreferredSize( selectBtDim );

        btSelectAll = new JButton( IconRegistry.getIcon( "forward_green.png" ) );
        btSelectAll.setName( SEL_ALL_BT );
        btSelectAll.addActionListener( this );
        btSelectAll.setPreferredSize( selectBtDim );
        // TODO
        btSelectAll.setVisible( false );

        btDeselectAll = new JButton( IconRegistry.getIcon( "backward_green.png" ) );
        btDeselectAll.setName( DESEL_ALL_BT );
        btDeselectAll.addActionListener( this );
        btDeselectAll.setEnabled( false );
        btDeselectAll.setPreferredSize( selectBtDim );
        // TODO
        btDeselectAll.setVisible( false );

        btDeselectSelected = new JButton( IconRegistry.getIcon( "back_green.png" ) );
        btDeselectSelected.setName( DESEL_SELECTED_BT );
        btDeselectSelected.addActionListener( this );
        btDeselectSelected.setEnabled( false );
        btDeselectSelected.setPreferredSize( selectBtDim );

        // add select/deselect buttons to a panel
        JPanel selectBtPanel = new JPanel();
        GridBagConstraints gbcBt = SwingUtils.initPanel( selectBtPanel );
        selectBtPanel.add( btSelectSelected, gbcBt );
        ++gbcBt.gridy;
        selectBtPanel.add( btSelectAll, gbcBt );
        gbcBt.insets = new Insets( 10, 2, 2, 2 );
        ++gbcBt.gridy;
        selectBtPanel.add( btDeselectAll, gbcBt );
        gbcBt.insets = new Insets( 2, 2, 2, 2 );
        ++gbcBt.gridy;
        selectBtPanel.add( btDeselectSelected, gbcBt );

        // tree which contains all layers selected by the user
        selectedCoveragesRoot = new DefaultMutableTreeNode();
        selectedCoveragesModel = new DefaultTreeModel( selectedCoveragesRoot );
        selectedCoveragesModel.addTreeModelListener( this );
        trSelectedCoverages = new JTree( selectedCoveragesModel );
        trSelectedCoverages.setRootVisible( false );
        trSelectedCoverages.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        trSelectedCoverages.addTreeSelectionListener( this );
        trSelectedCoverages.setCellRenderer( new WCSCoverageRenderer() );
        trSelectedCoverages.setVisible( true );

        JScrollPane selectedLayersScroll = new JScrollPane( trSelectedCoverages );
        selectedLayersScroll.setPreferredSize( treeDim );
        selectedLayersScroll.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        selectedLayersScroll.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );

        // the buttons to change the order of the layers
        Dimension orderBtDim = new Dimension( 30, 50 );
        Icon iconUp = IconRegistry.getIcon( "arrow_up.png" );
        btCoverageUp = new JButton( iconUp );
        btCoverageUp.setName( UP_BT );
        btCoverageUp.setPreferredSize( orderBtDim );
        btCoverageUp.addActionListener( this );
        btCoverageUp.setEnabled( false );
        // TODO
        btCoverageUp.setVisible( false );

        Icon iconDown = IconRegistry.getIcon( "arrow_down.png" );
        btCoverageDown = new JButton( iconDown );
        btCoverageDown.setName( DOWN_BT );
        btCoverageDown.setPreferredSize( orderBtDim );
        btCoverageDown.addActionListener( this );
        btCoverageDown.setEnabled( false );
        // TODO
        btCoverageDown.setVisible( false );

        // add buttons to change the order of the layers
        JPanel orderBtPanel = new JPanel();
        GridBagConstraints gbcOrderBt = SwingUtils.initPanel( orderBtPanel );

        orderBtPanel.add( btCoverageUp, gbcOrderBt );
        ++gbcOrderBt.gridy;
        orderBtPanel.add( btCoverageDown, gbcOrderBt );

        // add components of this dialog to the panel
        JPanel pnCoverageList = new JPanel();
        GridBagConstraints gbc = SwingUtils.initPanel( pnCoverageList );

        pnCoverageList.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD11379" ) ), gbc );
        gbc.gridx = gbc.gridx + 2;
        pnCoverageList.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD11380" ) ), gbc );
        ++gbc.gridy;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        pnCoverageList.add( availableLayersScroll, gbc );
        ++gbc.gridx;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        pnCoverageList.add( selectBtPanel, gbc );
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.5;
        ++gbc.gridx;
        pnCoverageList.add( selectedLayersScroll, gbc );
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        ++gbc.gridx;
        pnCoverageList.add( orderBtPanel, gbc );

        pnCoverageList.setVisible( true );

        ToolTipManager.sharedInstance().registerComponent( trAvailableCoverages );
        ToolTipManager.sharedInstance().registerComponent( trSelectedCoverages );

        return pnCoverageList;
    }

    /**
     * @param rootLayer
     *            the wms layer to create a node
     */
    private DefaultMutableTreeNode createTreeNode( CoverageOfferingBrief[] coverageOfferings ) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode( "root" );
        for ( int i = 0; i < coverageOfferings.length; i++ ) {
            node.add( new DefaultMutableTreeNode( coverageOfferings[i] ) );
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

                AddWCSWizardChooseFormat nextStep = new AddWCSWizardChooseFormat( this, this.mapModel,
                                                                                  this.appContainer, this.wcsCaps,
                                                                                  getSelectedCoverages(),
                                                                                  capabilitiesURL );
                GuiUtils.addToFrontListener( nextStep );
                nextStep.setLocation( this.getX(), this.getY() );
                nextStep.setVisible( true );
                this.setVisible( false );
            } else if ( srcButton.getName().equals( ButtonPanel.FINISH_BT ) ) {
                List<CoverageOfferingBrief> coverages = getSelectedCoverages();
                for ( CoverageOfferingBrief coverage : coverages ) {
                    AddWCSLayerCommand addWCSCmd = new AddWCSLayerCommand( this.mapModel, this.capabilitiesURL,
                                                                           this.wcsCaps, coverage );
                    appContainer.getCommandProcessor().executeASychronously( addWCSCmd );
                }
                this.dispose();
            } else if ( srcButton.getName().equals( SEL_SELECTED_BT ) ) {
                TreePath[] cos = this.trAvailableCoverages.getSelectionPaths();
                for ( int i = 0; i < cos.length; i++ ) {
                    if ( cos[i].getLastPathComponent() instanceof DefaultMutableTreeNode ) {
                        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) cos[i].getLastPathComponent();
                        CoverageOfferingBrief coverageOffering = (CoverageOfferingBrief) selectedNode.getUserObject();
                        appendLayerToRoot( coverageOffering, this.selectedCoveragesModel );
                        this.selectedNodes.put( coverageOffering, cos[i].getPath() );
                    }
                }
                btSelectSelected.setEnabled( false );
                btDeselectSelected.setEnabled( true );
                // make changes visible!
                this.trSelectedCoverages.setRootVisible( true );
                this.trSelectedCoverages.expandRow( 0 );
                this.trSelectedCoverages.setRootVisible( false );
            } else if ( srcButton.getName().equals( SEL_ALL_BT ) ) {
                appendChilds( this.availableCoveragesRoot, this.selectedCoveragesModel );
                // make changes visible!
                this.trSelectedCoverages.setRootVisible( true );
                this.trSelectedCoverages.expandRow( 0 );
                this.trSelectedCoverages.setRootVisible( false );
            } else if ( srcButton.getName().equals( DESEL_ALL_BT ) ) {
                int childCount = this.selectedCoveragesRoot.getChildCount();
                for ( int i = 0; i < childCount; i++ ) {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode) this.selectedCoveragesRoot.getChildAt( 0 );
                    this.selectedCoveragesModel.removeNodeFromParent( child );
                }
                this.selectedNodes.clear();
            } else if ( srcButton.getName().equals( DESEL_SELECTED_BT ) ) {
                TreePath[] layers = this.trSelectedCoverages.getSelectionPaths();
                for ( int i = 0; i < layers.length; i++ ) {
                    if ( layers[i].getLastPathComponent() instanceof DefaultMutableTreeNode ) {
                        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) layers[i].getLastPathComponent();
                        this.selectedCoveragesModel.removeNodeFromParent( selectedNode );
                        if ( selectedNode.getUserObject() instanceof Layer ) {
                            this.selectedNodes.remove( selectedNode.getUserObject() );
                        }
                    }
                }
                btDeselectSelected.setEnabled( false );
                btSelectSelected.setEnabled( true );
            } else if ( srcButton.getName().equals( UP_BT ) ) {
                moveLayerUp();
            } else if ( srcButton.getName().equals( DOWN_BT ) ) {
                moveLayerDown();
            }
        }
    }

    /**
     * @return the coverages selected by the user
     */
    private List<CoverageOfferingBrief> getSelectedCoverages() {
        List<CoverageOfferingBrief> coverages = new ArrayList<CoverageOfferingBrief>(
                                                                                      this.selectedCoveragesRoot.getChildCount() );
        for ( int i = 0; i < this.selectedCoveragesRoot.getChildCount(); i++ ) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) this.selectedCoveragesRoot.getChildAt( i );
            if ( child.getUserObject() instanceof CoverageOfferingBrief ) {
                coverages.add( (CoverageOfferingBrief) child.getUserObject() );
            }
        }
        return coverages;
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
                    CoverageOfferingBrief coverageOffering = (CoverageOfferingBrief) child.getUserObject();
                    appendLayerToRoot( coverageOffering, model );
                    this.selectedNodes.put( coverageOffering, child.getPath() );
                }
            } else {
                appendChilds( child, model );
            }
        }
    }

    /**
     * append the layer as node to the model
     * 
     * @param coverageOffering
     *            the layer to append as node
     * @param model
     *            the model to add the node
     */
    private void appendLayerToRoot( CoverageOfferingBrief coverageOffering, DefaultTreeModel model ) {
        DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) model.getRoot();
        if ( coverageOffering.getName() != null && coverageOffering.getName().length() > 0 ) {
            boolean isSelected = false;
            for ( int i = 0; i < targetNode.getChildCount(); i++ ) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) targetNode.getChildAt( i );
                if ( child.getUserObject() instanceof Layer ) {
                    Layer childLayer = (Layer) child.getUserObject();
                    if ( childLayer.getName().equals( coverageOffering.getName() )
                         || childLayer.getTitle().equals( coverageOffering.getLabel() ) )
                        isSelected = true;
                }
            }
            if ( !isSelected ) {
                model.insertNodeInto( new DefaultMutableTreeNode( coverageOffering ), targetNode,
                                      targetNode.getChildCount() );
            }
        }

    }

    /**
     * move selected layer one entry up
     */
    private void moveLayerUp() {
        TreePath selLayer = this.trSelectedCoverages.getSelectionPath();
        int row = this.trSelectedCoverages.getSelectionRows()[0];
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selLayer.getLastPathComponent();
        if ( row > 0 ) {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) node.getRoot();
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( node.getUserObject() );
            this.selectedCoveragesModel.insertNodeInto( newNode, root, row - 1 );
            TreePath tp = this.trSelectedCoverages.getPathForRow( row - 1 );
            this.trSelectedCoverages.setSelectionPath( tp );
            this.selectedCoveragesModel.removeNodeFromParent( node );
        }
    }

    /**
     * move selected layer one entry down
     */
    private void moveLayerDown() {
        TreePath selLayer = this.trSelectedCoverages.getSelectionPath();
        int row = this.trSelectedCoverages.getSelectionRows()[0];
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) selLayer.getLastPathComponent();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) node.getRoot();
        if ( row < root.getChildCount() - 1 ) {
            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode( node.getUserObject() );
            this.selectedCoveragesModel.insertNodeInto( newNode, root, row + 2 );
            TreePath tp = this.trSelectedCoverages.getPathForRow( row + 2 );
            this.trSelectedCoverages.setSelectionPath( tp );
            this.selectedCoveragesModel.removeNodeFromParent( node );
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
        if ( this.trAvailableCoverages.getSelectionCount() > 0 && this.trSelectedCoverages.getSelectionCount() == 0 ) {
            this.btSelectSelected.setEnabled( true );
        } else {
            this.btSelectSelected.setEnabled( false );
        }

        // enable/disable deselect-button
        if ( this.trSelectedCoverages.getSelectionCount() > 0 ) {
            this.btDeselectSelected.setEnabled( true );
            if ( this.trSelectedCoverages.getSelectionCount() == 1 ) {
                this.btCoverageUp.setEnabled( true );
                this.btCoverageDown.setEnabled( true );
            } else {
                this.btCoverageUp.setEnabled( false );
                this.btCoverageDown.setEnabled( false );
            }
        } else {
            this.btDeselectSelected.setEnabled( false );
            this.btCoverageUp.setEnabled( false );
            this.btCoverageDown.setEnabled( false );
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
        this.btDeselectAll.setEnabled( true );
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
        if ( this.trSelectedCoverages.getRowCount() < 1 ) {
            this.btDeselectAll.setEnabled( false );
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
