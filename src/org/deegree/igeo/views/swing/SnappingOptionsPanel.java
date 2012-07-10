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
package org.deegree.igeo.views.swing;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.LayerGroup;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MapModelVisitor;
import org.deegree.igeo.settings.Settings;
import org.deegree.igeo.settings.SnappingLayersOpts;
import org.deegree.igeo.settings.SnappingToleranceOpt;
import org.deegree.igeo.views.HelpManager;
import org.deegree.igeo.views.swing.util.IconRegistry;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class SnappingOptionsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = -6326745848985062619L;

    private JPanel snapOptionsPanel;

    private JComboBox cbUOM;

    private JScrollPane scLayers;

    private JCheckBox cbEdgeCenter;

    private JLabel jLabel2;

    private JCheckBox cbEndNode;

    private JCheckBox cbStartNode;

    private JCheckBox cbEdge;

    private JCheckBox cbVertex;

    private JPanel snapTargetPanel;

    private JList layerList;

    private JPanel layerPanel;

    private JSpinner spSnapDistance;

    private JLabel jLabel1;

    private JButton btHelp;

    private ApplicationContainer<Container> appCont;

    private Map<String, Layer> featureLayerMap;

    private ActionListener cbListener = new CheckBoxActionListener();

    /**
     * 
     * @param appCont
     */
    public SnappingOptionsPanel( ApplicationContainer<Container> appCont ) {
        this.appCont = appCont;
        featureLayerMap = new HashMap<String, Layer>();
        initGUI();
    }

    private void initGUI() {
        try {
            final Settings settings = appCont.getSettings();
            this.setPreferredSize( new java.awt.Dimension( 360, 311 ) );
            GridBagLayout thisLayout = new GridBagLayout();
            this.setSize( 360, 311 );
            thisLayout.rowWeights = new double[] { 0.0, 0.1 };
            thisLayout.rowHeights = new int[] { 234, 7 };
            thisLayout.columnWeights = new double[] { 0.1 };
            thisLayout.columnWidths = new int[] { 7 };
            this.setLayout( thisLayout );
            {
                snapOptionsPanel = new JPanel();
                GridBagLayout snapOptionsPanelLayout = new GridBagLayout();
                this.add( snapOptionsPanel, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                    GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ),
                                                                    0, 0 ) );
                snapOptionsPanelLayout.rowWeights = new double[] { 0.1, 0.0, 0.1 };
                snapOptionsPanelLayout.rowHeights = new int[] { 7, 12, 7 };
                snapOptionsPanelLayout.columnWeights = new double[] { 0.0, 0.0, 0.1, 0.1 };
                snapOptionsPanelLayout.columnWidths = new int[] { 110, 110, 7, 7 };
                snapOptionsPanel.setLayout( snapOptionsPanelLayout );
                {
                    btHelp = new JButton();
                    snapOptionsPanel.add( btHelp, new GridBagConstraints( 2, 2, 2, 1, 0.0, 0.0,
                                                                          GridBagConstraints.CENTER,
                                                                          GridBagConstraints.HORIZONTAL,
                                                                          new Insets( 0, 5, 0, 5 ), 0, 0 ) );
                    btHelp.setText( Messages.getMessage( getLocale(), "$MD10463" ) );
                    btHelp.setIcon( IconRegistry.getIcon( "help.png" ) );
                    btHelp.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            HelpFrame hf = HelpFrame.getInstance( new HelpManager( appCont ) );
                            hf.setVisible( true );
                            hf.gotoModule( "Digitizer" );
                        }

                    } );
                }
                {
                    jLabel1 = new JLabel();
                    snapOptionsPanel.add( jLabel1, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                           GridBagConstraints.WEST,
                                                                           GridBagConstraints.HORIZONTAL,
                                                                           new Insets( 0, 5, 0, 5 ), 0, 0 ) );
                    jLabel1.setText( Messages.getMessage( getLocale(), "$MD10464" ) );
                }
                {
                    double value = settings.getSnappingToleranceOptions().getValue();
                    SpinnerNumberModel model = new SpinnerNumberModel( value, 1d, 10000d, 1 );
                    spSnapDistance = new JSpinner();
                    snapOptionsPanel.add( spSnapDistance, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0,
                                                                                  GridBagConstraints.CENTER,
                                                                                  GridBagConstraints.HORIZONTAL,
                                                                                  new Insets( 0, 5, 0, 5 ), 0, 0 ) );
                    spSnapDistance.setModel( model );
                    spSnapDistance.addChangeListener( new ChangeListener() {

                        public void stateChanged( ChangeEvent e ) {
                            SnappingToleranceOpt smo = settings.getSnappingToleranceOptions();
                            String value = ( (JSpinner) e.getSource() ).getValue().toString();
                            smo.setValue( Float.valueOf( value ) );
                        }

                    } );
                }
                {
                    String[] values = new String[] { Messages.getMessage( getLocale(), "$MD10465" ),
                                                    Messages.getMessage( getLocale(), "$MD10466" ) };
                    ComboBoxModel cbUOMModel = new DefaultComboBoxModel( values );
                    cbUOMModel.setSelectedItem( settings.getSnappingToleranceOptions().getUOM() );
                    cbUOM = new JComboBox( cbUOMModel );
                    snapOptionsPanel.add( cbUOM, new GridBagConstraints( 2, 0, 2, 1, 0.0, 0.0,
                                                                         GridBagConstraints.CENTER,
                                                                         GridBagConstraints.HORIZONTAL,
                                                                         new Insets( 0, 5, 0, 5 ), 0, 0 ) );
                    cbUOM.addItemListener( new ItemListener() {

                        public void itemStateChanged( ItemEvent e ) {
                            if ( e.getStateChange() == 1 ) {
                                SnappingToleranceOpt smo = settings.getSnappingToleranceOptions();
                                smo.setUOM( e.getItem().toString() );
                            }
                        }

                    } );

                }
            }
            {
                layerPanel = new JPanel();
                GridBagLayout layerPanelLayout = new GridBagLayout();
                this.add( layerPanel, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                              GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                layerPanelLayout.rowWeights = new double[] { 0.1 };
                layerPanelLayout.rowHeights = new int[] { 7 };
                layerPanelLayout.columnWeights = new double[] { 0.0, 0.1 };
                layerPanelLayout.columnWidths = new int[] { 182, 7 };
                layerPanel.setLayout( layerPanelLayout );
                {
                    scLayers = new JScrollPane();
                    layerPanel.add( scLayers, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                      GridBagConstraints.BOTH,
                                                                      new Insets( 5, 5, 5, 5 ), 0, 0 ) );
                    {
                        List<String> layers = getFeatureLayers();
                        ListModel layerListModel = new DefaultComboBoxModel( layers.toArray( new String[layers.size()] ) );
                        layerList = new JList();
                        layerList.addListSelectionListener( new LayerSelectListener() );
                        scLayers.setViewportView( layerList );
                        layerList.setModel( layerListModel );
                        layerList.addListSelectionListener( new ListSelectionListener() {

                            public void valueChanged( ListSelectionEvent e ) {
                                cbEdge.setEnabled( true );
                                cbEdgeCenter.setEnabled( true );
                                cbEndNode.setEnabled( true );
                                cbStartNode.setEnabled( true );
                                cbVertex.setEnabled( true );
                            }
                        } );
                    }
                }
                {
                    snapTargetPanel = new JPanel();
                    GridBagLayout snapTargetPanelLayout = new GridBagLayout();
                    layerPanel.add( snapTargetPanel, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0,
                                                                             GridBagConstraints.CENTER,
                                                                             GridBagConstraints.BOTH,
                                                                             new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    snapTargetPanelLayout.rowWeights = new double[] { 0.1, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1 };
                    snapTargetPanelLayout.rowHeights = new int[] { 7, 30, 30, 30, 30, 30, 7 };
                    snapTargetPanelLayout.columnWeights = new double[] { 0.1 };
                    snapTargetPanelLayout.columnWidths = new int[] { 7 };
                    snapTargetPanel.setLayout( snapTargetPanelLayout );
                    {
                        jLabel2 = new JLabel();
                        snapTargetPanel.add( jLabel2, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                              GridBagConstraints.CENTER,
                                                                              GridBagConstraints.HORIZONTAL,
                                                                              new Insets( 0, 5, 0, 5 ), 0, 0 ) );
                        jLabel2.setText( Messages.getMessage( getLocale(), "$MD10467" ) );
                    }
                    addCheckboxes();
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void addCheckboxes() {
        {
            cbVertex = new JCheckBox( IconRegistry.getIcon( "checkbox_unselected.gif" ) );
            cbVertex.addActionListener( cbListener );
            snapTargetPanel.add( cbVertex,
                                 new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL, new Insets( 0, 5, 0, 5 ), 0, 0 ) );
            cbVertex.setText( Messages.getMessage( getLocale(), "$MD10468" ) );
            cbVertex.setActionCommand( "snapVertex" );
            cbVertex.setEnabled( false );
        }
        {
            cbEdge = new JCheckBox( IconRegistry.getIcon( "checkbox_unselected.gif" ) );
            cbEdge.addActionListener( cbListener );
            snapTargetPanel.add( cbEdge, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                 GridBagConstraints.HORIZONTAL,
                                                                 new Insets( 0, 5, 0, 5 ), 0, 0 ) );
            cbEdge.setText( Messages.getMessage( getLocale(), "$MD10469" ) );
            cbEdge.setActionCommand( "snapEdge" );
            cbEdge.setEnabled( false );
        }
        {
            cbStartNode = new JCheckBox( IconRegistry.getIcon( "checkbox_unselected.gif" ) );
            cbStartNode.addActionListener( cbListener );
            snapTargetPanel.add( cbStartNode, new GridBagConstraints( 0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                      GridBagConstraints.HORIZONTAL,
                                                                      new Insets( 0, 5, 0, 5 ), 0, 0 ) );
            cbStartNode.setText( Messages.getMessage( getLocale(), "$MD10470" ) );
            cbStartNode.setActionCommand( "snapStartNode" );
            cbStartNode.setEnabled( false );
        }
        {
            cbEndNode = new JCheckBox( IconRegistry.getIcon( "checkbox_unselected.gif" ) );
            cbEndNode.addActionListener( cbListener );
            snapTargetPanel.add( cbEndNode, new GridBagConstraints( 0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                    GridBagConstraints.HORIZONTAL, new Insets( 0, 5, 0,
                                                                                                               5 ), 0,
                                                                    0 ) );
            cbEndNode.setText( Messages.getMessage( getLocale(), "$MD10471" ) );
            cbEndNode.setActionCommand( "snapEndNode" );
            cbEndNode.setEnabled( false );
        }
        {
            cbEdgeCenter = new JCheckBox( IconRegistry.getIcon( "checkbox_unselected.gif" ) );
            cbEdgeCenter.addActionListener( cbListener );
            snapTargetPanel.add( cbEdgeCenter, new GridBagConstraints( 0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                       GridBagConstraints.HORIZONTAL,
                                                                       new Insets( 0, 5, 0, 5 ), 0, 0 ) );
            cbEdgeCenter.setText( Messages.getMessage( getLocale(), "$MD10472" ) );
            cbEdgeCenter.setActionCommand( "snapEdgeCenter" );
            cbEdgeCenter.setEnabled( false );
        }
    }

    private List<String> getFeatureLayers()
                            throws Exception {

        SnappingLayersOpts slo = appCont.getSettings().getSnappingLayersOptions();
        MapModel mapModel = appCont.getMapModel( null );
        CollectFeatureLayersVisitor visitor = new CollectFeatureLayersVisitor( slo );
        mapModel.walkLayerTree( visitor );
        List<String> list = visitor.getCollectedLayers();

        return list;
    }

    /**
     * 
     * @param command
     * @return {@link ActionListener} assigned to the panel enabling dynamic setting of the panels checkboxes
     */
    public ActionListener createActionListener( String command ) {
        return new SnapActionListener( command );
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // inner classes
    // ///////////////////////////////////////////////////////////////////////////////

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    private class CheckBoxActionListener implements ActionListener {

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed( ActionEvent e ) {
            JCheckBox cb = (JCheckBox) e.getSource();
            if ( cb.isSelected() ) {
                cb.setIcon( IconRegistry.getIcon( "checkbox_selected.gif" ) );
            } else {
                cb.setIcon( IconRegistry.getIcon( "checkbox_unselected.gif" ) );
            }
            SnappingLayersOpts slo = appCont.getSettings().getSnappingLayersOptions();
            String action = cb.getActionCommand();
            Object[] values = layerList.getSelectedValues();
            for ( int i = 0; i < values.length; i++ ) {
                Layer layer = featureLayerMap.get( values[i] );
                if ( cb.isSelected() ) {
                    if ( action.equals( "snapVertex" ) ) {
                        slo.selectSnappingTargetVertex( layer.getIdentifier(), true );
                    } else if ( action.equals( "snapEdge" ) ) {
                        slo.selectSnappingTargetEdge( layer.getIdentifier(), true );
                    } else if ( action.equals( "snapStartNode" ) ) {
                        slo.selectSnappingTargetStartNode( layer.getIdentifier(), true );
                    } else if ( action.equals( "snapEndNode" ) ) {
                        slo.selectSnappingTargetEndNode( layer.getIdentifier(), true );
                    } else if ( action.equals( "snapEdgeCenter" ) ) {
                        slo.selectSnappingTargetEdgeCenter( layer.getIdentifier(), true );
                    }
                } else {
                    if ( action.equals( "snapVertex" ) ) {
                        slo.selectSnappingTargetVertex( layer.getIdentifier(), false );
                    } else if ( action.equals( "snapEdge" ) ) {
                        slo.selectSnappingTargetEdge( layer.getIdentifier(), false );
                    } else if ( action.equals( "snapStartNode" ) ) {
                        slo.selectSnappingTargetStartNode( layer.getIdentifier(), false );
                    } else if ( action.equals( "snapEndNode" ) ) {
                        slo.selectSnappingTargetEndNode( layer.getIdentifier(), false );
                    } else if ( action.equals( "snapEdgeCenter" ) ) {
                        slo.selectSnappingTargetEdgeCenter( layer.getIdentifier(), false );
                    }
                }
            }
        }

    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    private class LayerSelectListener implements ListSelectionListener {

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        public void valueChanged( ListSelectionEvent e ) {
            if ( e.getValueIsAdjusting() == false ) {
                SnappingLayersOpts slo = appCont.getSettings().getSnappingLayersOptions();
                JList list = (JList) e.getSource();
                int vertex = 0;
                int edge = 0;
                int edgeCenter = 0;
                int startNode = 0;
                int endNode = 0;
                Object[] values = list.getSelectedValues();
                for ( int i = 0; i < values.length; i++ ) {
                    Layer layer = featureLayerMap.get( values[i] );
                    if ( slo.isSelectedForSnappingVertices( layer.getIdentifier() ) ) {
                        vertex++;
                    }
                    if ( slo.isSelectedForSnappingEdges( layer.getIdentifier() ) ) {
                        edge++;
                    }
                    if ( slo.isSelectedForSnappingEdgeCenters( layer.getIdentifier() ) ) {
                        edgeCenter++;
                    }
                    if ( slo.isSelectedForSnappingStartNodes( layer.getIdentifier() ) ) {
                        startNode++;
                    }
                    if ( slo.isSelectedForSnappingEndNodes( layer.getIdentifier() ) ) {
                        endNode++;
                    }
                }
                updateCheckbox( cbVertex, vertex );
                updateCheckbox( cbEdge, edge );
                updateCheckbox( cbEdgeCenter, edgeCenter );
                updateCheckbox( cbStartNode, startNode );
                updateCheckbox( cbEndNode, endNode );
            }
        }

        private void updateCheckbox( JCheckBox cb, int value ) {
            if ( value == 0 ) {
                cb.setSelected( false );
                cb.setIcon( IconRegistry.getIcon( "checkbox_unselected.gif" ) );
            } else if ( value == 1 ) {
                cb.setSelected( true );
                cb.setIcon( IconRegistry.getIcon( "checkbox_selected.gif" ) );
            } else {
                cb.setSelected( true );
                cb.setIcon( IconRegistry.getIcon( "checkbox_indifferent.gif" ) );
            }
        }

    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    public class SnapActionListener implements ActionListener {

        private String command;

        /**
         * 
         * @param command
         */
        public SnapActionListener( String command ) {
            this.command = command;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed( ActionEvent e ) {
            if ( "vertex".equals( command ) ) {
                cbVertex.doClick();
            } else if ( "edge".equals( command ) ) {
                cbEdge.doClick();
            } else if ( "startnode".equals( command ) ) {
                cbStartNode.doClick();
            } else if ( "endnode".equals( command ) ) {
                cbEndNode.doClick();
            } else if ( "edgecenter".equals( command ) ) {
                cbEdgeCenter.doClick();
            }
        }

    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    private class CollectFeatureLayersVisitor implements MapModelVisitor {

        private List<String> layers = new ArrayList<String>( 50 );

        private SnappingLayersOpts snappingLayersOpts;

        private CollectFeatureLayersVisitor( SnappingLayersOpts snappingLayersOpts ) {
            this.snappingLayersOpts = snappingLayersOpts;
        }

        List<String> getCollectedLayers() {
            return layers;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deegree.igeo.mapmodel.MapModelVisitor#visit(org.deegree.igeo.mapmodel.Layer)
         */
        public void visit( Layer layer )
                                throws Exception {
            List<DataAccessAdapter> daa = layer.getDataAccess();
            for ( DataAccessAdapter adapter : daa ) {
                if ( adapter instanceof FeatureAdapter ) {
                    layers.add( layer.getTitle() );
                    featureLayerMap.put( layer.getTitle(), layer );
                    snappingLayersOpts.addLayer( layer.getIdentifier() );
                    break;
                }
            }

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deegree.igeo.mapmodel.MapModelVisitor#visit(org.deegree.igeo.mapmodel.LayerGroup)
         */
        public void visit( LayerGroup layerGroup )
                                throws Exception {
            // do nothing
        }

    }
}
