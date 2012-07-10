//$HeadURL: svn+ssh://developername@svn.wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $

/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2010 by:
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
package org.deegree.igeo.views.swing.style;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.DefaultMapModule;
import org.deegree.igeo.modules.EditStyleModule;
import org.deegree.igeo.state.mapstate.ChangeLabelPositionState;
import org.deegree.igeo.state.mapstate.ToolState;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.kernel.Command;
import org.deegree.model.spatialschema.Point;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author: admin $
 * 
 * @version $Revision: $, $Date: $
 */
public class EditFeatureStyleDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = -593637293753376108L;

    private static ILogger LOG = LoggerFactory.getLogger( EditFeatureStyleDialog.class );

    private JButton btClose;

    private JButton btHelp;

    private JPanel pnHelp;

    private JButton btTake;

    private JPanel pnButtons;

    private JPanel pnControl;

    private static EditFeatureStyleDialog editFeatureStyleDialog;

    private JLabel lbX;

    private JSpinner spY;

    private JSpinner spX;

    private JLabel lbY;

    private JPanel pnRotation;

    private JPanel pnLayer;

    private JSpinner spRotation;

    private JPanel pnDisplacement;

    private JToggleButton btDisplacement;

    private JToggleButton btAssignToLine;

    private JToggleButton btRotate;

    private ButtonGroup bgToolbar = new ButtonGroup();

    private JComboBox cbLayer;

    private JToggleButton btSelect;

    private JToolBar tbToolbar;

    private EditStyleModule<Container> owner;

    private ChangeLabelPositionState state;

    private EditFeatureStylePanel panel;

    private JTextPane tpDescription;

    private JPanel pnDescription;

    private ApplicationContainer<Container> appCont;

    private EditFeatureStyleDrawingPane drawingPane;

    private MapModel mapModel;

    private boolean mapUOM;

    /**
     * just for development
     */
    public EditFeatureStyleDialog( Frame frame ) {
        super( frame );
        initGUI();
    }

    /**
     * @see #create()
     * @param owner
     */
    private EditFeatureStyleDialog( final EditStyleModule<Container> owner ) {
        this.owner = owner;
        appCont = owner.getApplicationContainer();
        state = new ChangeLabelPositionState( appCont, null, "editFeatureStyle" );
        addWindowListener( new WindowAdapter() {
            public void windowActivated( WindowEvent e ) {
                DefaultMapModule<Container> mapModule = appCont.getActiveMapModule();
                ToolState ts = mapModule.getMapTool().getState();
                if ( !ChangeLabelPositionState.class.isInstance( ts ) ) {
                    // if the dialog is activated and the current state of the active MapModule is not
                    // equal to ChangeLabelPositionState the state must be set
                    mapModule.getMapTool().setState( state );
                    resetPanel();
                    panel = new EditFeatureStylePanel( owner );
                }
            };

            public void windowClosing( WindowEvent e ) {
                dispose();
            };
        } );
        initGUI();
        setLocation( 200, 200 );
    }

    /**
     * 
     */
    public void resetToolbar() {
        setRotationEnabled( false );
        setDisplacementEnabled( false );
    }

    private void resetPanel() {
        DefaultMapModule<Container> mapModule = appCont.getActiveMapModule();
        mapModule.clear();
        if ( panel != null ) {
            panel.clear();
        }
        panel = null;
    }

    @Override
    public void dispose() {
        state = null;
        editFeatureStyleDialog = null;
        resetPanel();
        // state must be reseted to avoid keeping the map state after the dialog has been closed. This would lead
        // to strange effects if the dialog will be opened again
        DefaultMapModule<Container> mapModule = appCont.getActiveMapModule();
        mapModule.getMapTool().resetState();
        super.dispose();
    }

    /**
     * 
     * @return singleton instance of @see {@link EditFeatureStyleDialog}
     */
    public static EditFeatureStyleDialog create( EditStyleModule<Container> owner ) {
        if ( editFeatureStyleDialog == null ) {
            editFeatureStyleDialog = new EditFeatureStyleDialog( owner );
            editFeatureStyleDialog.fillLayerList();
        }
        editFeatureStyleDialog.setAlwaysOnTop( true );
        editFeatureStyleDialog.setVisible( true );
        editFeatureStyleDialog.mapModel = editFeatureStyleDialog.appCont.getMapModel( null );
        return editFeatureStyleDialog;
    }

    /**
     * 
     */
    private void fillLayerList() {
        MapModel mm = owner.getApplicationContainer().getMapModel( null );
        List<Layer> layers = mm.getLayersAsList( true );
        ComboBoxModel model = new DefaultComboBoxModel( layers.toArray() );
        cbLayer.setModel( model );

    }

    private void initGUI() {
        try {
            {
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.1 };
                thisLayout.rowHeights = new int[] { 96, 236, 7 };
                thisLayout.columnWeights = new double[] { 0.1 };
                thisLayout.columnWidths = new int[] { 7 };
                getContentPane().setLayout( thisLayout );
                {
                    pnControl = new JPanel();
                    GridBagLayout pnControlLayout = new GridBagLayout();
                    getContentPane().add(
                                          pnControl,
                                          new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnControl.setBorder( BorderFactory.createTitledBorder( Messages.get( "$MD11738" ) ) );
                    pnControlLayout.rowWeights = new double[] { 0.0, 0.0, 0.0 };
                    pnControlLayout.rowHeights = new int[] { 41, 93, 77 };
                    pnControlLayout.columnWeights = new double[] { 0.0, 0.1 };
                    pnControlLayout.columnWidths = new int[] { 152, 7 };
                    pnControl.setLayout( pnControlLayout );
                    pnControl.add( getTbToolbar(), new GridBagConstraints( 0, 0, 2, 1, 0.0, 0.0,
                                                                           GridBagConstraints.CENTER,
                                                                           GridBagConstraints.BOTH, new Insets( 0, 0,
                                                                                                                0, 0 ),
                                                                           0, 0 ) );
                    pnControl.add( getPnRotation(), new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                                                            GridBagConstraints.CENTER,
                                                                            GridBagConstraints.BOTH,
                                                                            new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    pnControl.add( getPnDisplacement(), new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0,
                                                                                GridBagConstraints.CENTER,
                                                                                GridBagConstraints.BOTH,
                                                                                new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    pnControl.add( getPnLayer(),
                                   new GridBagConstraints( 0, 2, 2, 2, 0.0, 0.0, GridBagConstraints.CENTER,
                                                           GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                }
                {
                    pnButtons = new JPanel();
                    FlowLayout pnButtonsLayout = new FlowLayout();
                    pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                    pnButtons.setLayout( pnButtonsLayout );
                    getContentPane().add(
                                          pnButtons,
                                          new GridBagConstraints( -1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    {
                        btTake = new JButton( Messages.get( "$MD11739" ), IconRegistry.getIcon( "accept.png" ) );
                        pnButtons.add( btTake );
                        btTake.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent event ) {
                                MapModel mapModel = owner.getApplicationContainer().getMapModel( null );
                                List<Layer> layers = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
                                double[] displ = getDisplacement();
                                state.setRotation( getRotation() );
                                state.setDisplacement( displ[0], displ[1] );
                                Command cmd = state.createCommand( owner, mapModel, layers.get( 0 ), (Point[]) null );
                                try {
                                    appCont.getCommandProcessor().executeSychronously( cmd, true );
                                } catch ( Exception e ) {
                                    LOG.logError( e );
                                    DialogFactory.openErrorDialog( appCont.getViewPlatform(),
                                                                   EditFeatureStyleDialog.this,
                                                                   Messages.get( "$MD11740" ),
                                                                   Messages.get( "$MD11741" ), e );
                                }
                                btSelect.doClick();
                                setRotationEnabled( false );
                                setDisplacementEnabled( false );
                            }
                        } );
                    }
                    {
                        btClose = new JButton( Messages.get( "$MD11742" ), IconRegistry.getIcon( "cancel.png" ) );
                        pnButtons.add( btClose );
                        btClose.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent event ) {
                                dispose();
                            }
                        } );
                    }
                }
                {
                    pnHelp = new JPanel();
                    FlowLayout pnHelpLayout = new FlowLayout();
                    pnHelpLayout.setAlignment( FlowLayout.RIGHT );
                    pnHelp.setLayout( pnHelpLayout );
                    getContentPane().add(
                                          pnHelp,
                                          new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    getContentPane().add(
                                          getPnDescription(),
                                          new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    {
                        btHelp = new JButton( Messages.get( "$MD11743" ), IconRegistry.getIcon( "help.png" ) );
                        pnHelp.add( btHelp );
                        btHelp.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent event ) {

                            }
                        } );
                    }
                }
            }
            this.setSize( 361, 408 );
            resetToolbar();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private JToolBar getTbToolbar() {
        if ( tbToolbar == null ) {
            tbToolbar = new JToolBar();
            tbToolbar.add( getBtSelect() );
            tbToolbar.add( getBtRotate() );
            tbToolbar.add( getBtAssignToLine() );
            tbToolbar.add( getBtDisplacement() );
        }
        return tbToolbar;
    }

    private JToggleButton getBtSelect() {
        if ( btSelect == null ) {
            btSelect = new JToggleButton( IconRegistry.getIcon( EditFeatureStyleDialog.class.getResource( "images/select.gif" ) ) );
            btSelect.setToolTipText( Messages.get( "$MD11744" ) );
            bgToolbar.add( btSelect );
            btSelect.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent event ) {
                    cbLayer.setEnabled( false );
                    resetPanel();
                    panel = new EditFeatureStylePanel( owner );
                    state.setRectangleSelectState();
                }
            } );
        }
        return btSelect;
    }

    private JToggleButton getBtRotate() {
        if ( btRotate == null ) {
            btRotate = new JToggleButton( IconRegistry.getIcon( EditFeatureStyleDialog.class.getResource( "images/label_rotate.png" ) ) );
            btRotate.setToolTipText( Messages.get( "$MD11745" ) );
            bgToolbar.add( btRotate );
            btRotate.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent event ) {
                    if ( ( (JToggleButton) event.getSource() ).isSelected() ) {
                        cbLayer.setEnabled( false );
                        try {
                            state.setRotateState();
                        } catch ( Exception e ) {
                            DialogFactory.openErrorDialog( appCont.getViewPlatform(), EditFeatureStyleDialog.this,
                                                           Messages.get( "$MD11746" ), e.getMessage(), e );
                        }
                    }
                }
            } );
        }
        return btRotate;
    }

    private JToggleButton getBtAssignToLine() {
        if ( btAssignToLine == null ) {
            btAssignToLine = new JToggleButton( IconRegistry.getIcon( EditFeatureStyleDialog.class.getResource( "images/align_to_line.png" ) ) );
            btAssignToLine.setToolTipText( Messages.get( "$MD11747" ) );
            bgToolbar.add( btAssignToLine );
            btAssignToLine.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent event ) {
                    if ( ( (JToggleButton) event.getSource() ).isSelected() ) {
                        cbLayer.setEnabled( true );
                        try {
                            state.setAssignToCurveState();
                        } catch ( Exception e ) {
                            DialogFactory.openErrorDialog( appCont.getViewPlatform(), EditFeatureStyleDialog.this,
                                                           Messages.get( "$MD11748" ), e.getMessage(), e );
                        }
                    }
                }
            } );
        }
        return btAssignToLine;
    }

    private JToggleButton getBtDisplacement() {
        if ( btDisplacement == null ) {
            btDisplacement = new JToggleButton(  IconRegistry.getIcon( EditFeatureStyleDialog.class.getResource( "images/label_move.png" ) )  );
            btDisplacement.setToolTipText( Messages.get( "$MD11749" ) );
            bgToolbar.add( btDisplacement );
            btDisplacement.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent event ) {
                    if ( ( (JToggleButton) event.getSource() ).isSelected() ) {
                        cbLayer.setEnabled( false );
                        try {
                            state.setDisplaceState();
                        } catch ( Exception e ) {
                            DialogFactory.openErrorDialog( appCont.getViewPlatform(), EditFeatureStyleDialog.this,
                                                           Messages.get( "$MD11750" ), e.getMessage(), e );
                        }
                    }
                }
            } );
        }
        return btDisplacement;
    }

    private JPanel getPnRotation() {
        if ( pnRotation == null ) {
            pnRotation = new JPanel();
            GridBagLayout pnRotationLayout = new GridBagLayout();
            pnRotation.setBorder( BorderFactory.createTitledBorder( Messages.get( "$MD11751" ) ) );
            pnRotationLayout.rowWeights = new double[] { 0.1 };
            pnRotationLayout.rowHeights = new int[] { 7 };
            pnRotationLayout.columnWeights = new double[] { 0.1 };
            pnRotationLayout.columnWidths = new int[] { 7 };
            pnRotation.setLayout( pnRotationLayout );
            spRotation = new JSpinner( new SpinnerNumberModel( 0, -360, 360, 0.5 ) );
            pnRotation.add( spRotation, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                GridBagConstraints.HORIZONTAL,
                                                                new Insets( 0, 9, 0, 9 ), 0, 0 ) );
            spRotation.addChangeListener( new ChangeListener() {

                public void stateChanged( ChangeEvent e ) {
                    if ( drawingPane != null ) {
                        drawingPane.setRotation( getRotation() );
                        appCont.getActiveMapModule().update();
                    }
                }
            } );
        }
        return pnRotation;
    }

    private JPanel getPnDisplacement() {
        if ( pnDisplacement == null ) {
            pnDisplacement = new JPanel();
            GridBagLayout pnDisplacementLayout = new GridBagLayout();
            pnDisplacement.setBorder( BorderFactory.createTitledBorder( Messages.get( "$MD11752" ) ) );
            pnDisplacementLayout.rowWeights = new double[] { 0.1, 0.1 };
            pnDisplacementLayout.rowHeights = new int[] { 7, 20 };
            pnDisplacementLayout.columnWeights = new double[] { 0.0, 0.1 };
            pnDisplacementLayout.columnWidths = new int[] { 39, 130 };
            pnDisplacement.setLayout( pnDisplacementLayout );
            lbX = new JLabel( Messages.get( "$MD11753" ) );
            pnDisplacement.add( lbX, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                             GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
            spX = new JSpinner( new SpinnerNumberModel( 0, -9E90, 9E90, 0.5 ) );
            pnDisplacement.add( spX, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                             GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 9 ),
                                                             0, 0 ) );
            spX.addChangeListener( new ChangeListener() {

                public void stateChanged( ChangeEvent e ) {
                    if ( drawingPane != null ) {
                        double[] displ = getDisplacement();
                        double scale = mapModel.getScaleDenominator();
                        if ( mapUOM ) {
                            drawingPane.setDisplacement( displ[0] / ( scale * 0.00028 ), displ[1] / ( scale * 0.00028 ) );
                        } else {
                            drawingPane.setDisplacement( displ[0], displ[1] );
                        }
                        appCont.getActiveMapModule().update();
                    }
                }
            } );
            lbY = new JLabel( Messages.get( "$MD11754" ) );
            pnDisplacement.add( lbY, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                             GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
            spY = new JSpinner( new SpinnerNumberModel( 0, -9E90, 9E90, 0.5 ) );
            pnDisplacement.add( spY, new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                             GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 9 ),
                                                             0, 0 ) );
            spY.addChangeListener( new ChangeListener() {

                public void stateChanged( ChangeEvent e ) {
                    if ( drawingPane != null ) {
                        drawingPane.setDisplacement( getDisplacement()[0], getDisplacement()[1] );
                        appCont.getActiveMapModule().update();
                    }
                }
            } );
        }
        return pnDisplacement;
    }

    private JPanel getPnLayer() {
        if ( pnLayer == null ) {
            pnLayer = new JPanel();
            GridBagLayout pnLayerLayout = new GridBagLayout();
            pnLayer.setBorder( BorderFactory.createTitledBorder( Messages.get( "$MD11755" ) ) );
            pnLayerLayout.rowWeights = new double[] { 0.1 };
            pnLayerLayout.rowHeights = new int[] { 7 };
            pnLayerLayout.columnWeights = new double[] { 0.1 };
            pnLayerLayout.columnWidths = new int[] { 7 };
            pnLayer.setLayout( pnLayerLayout );
            cbLayer = new JComboBox();
            pnLayer.add( cbLayer,
                         new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                 GridBagConstraints.HORIZONTAL, new Insets( 0, 9, 0, 9 ), 0, 0 ) );
            cbLayer.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent event ) {
                    if ( drawingPane != null && drawingPane instanceof AssignLabelToCurveDrawingPane ) {
                        ( (AssignLabelToCurveDrawingPane) drawingPane ).setLayer( getSelectedLayer() );
                    }
                }
            } );
            cbLayer.setEnabled( false );
        }
        return pnLayer;
    }

    /**
     * sets arc value
     * 
     * @param arc
     */
    public void setArc( double arc ) {
        ChangeListener cl = spRotation.getChangeListeners()[0];
        spRotation.removeChangeListener( cl );
        spRotation.setValue( arc );
        spRotation.addChangeListener( cl );
    }

    /**
     * @return
     */
    private double getRotation() {
        return ( (Number) spRotation.getValue() ).doubleValue();
    }

    /**
     * 
     * @param x
     * @param y
     */
    public void setDisplacement( double x, double y ) {
        if ( mapUOM ) {
            x = x * mapModel.getScaleDenominator() * 0.00028;
            y = y * mapModel.getScaleDenominator() * 0.00028;
        }
        ChangeListener cl = spX.getChangeListeners()[0];
        spX.removeChangeListener( cl );
        spX.setValue( x );
        spX.addChangeListener( cl );
        cl = spY.getChangeListeners()[0];
        spY.removeChangeListener( cl );
        spY.setValue( y );
        spY.addChangeListener( cl );
    }

    /**
     * 
     * @return
     */
    public double[] getDisplacement() {
        double dx = ( (Number) spX.getValue() ).doubleValue();
        double dy = ( (Number) spY.getValue() ).doubleValue();
        return new double[] { dx, dy };
    }

    /**
     * @param drawingPane
     */
    public void setDrawingPane( EditFeatureStyleDrawingPane drawingPane ) {
        this.drawingPane = drawingPane;
    }

    /**
     * 
     * @return selected layer from combo box
     */
    public Layer getSelectedLayer() {
        return (Layer) cbLayer.getSelectedItem();
    }

    /**
     * 
     * @param rotation
     */
    public void setRotationEnabled( boolean rotation ) {
        btRotate.setEnabled( rotation );
        btAssignToLine.setEnabled( rotation );
        spRotation.setEnabled( rotation );
    }

    /**
     * 
     * @param displace
     */
    public void setDisplacementEnabled( boolean displace ) {
        btDisplacement.setEnabled( displace );
        spX.setEnabled( displace );
        spY.setEnabled( displace );
    }

    /**
     * 
     * @param map
     */
    public void setUOM( boolean map ) {
        this.mapUOM = map;
    }

    private JPanel getPnDescription() {
        if ( pnDescription == null ) {
            pnDescription = new JPanel();
            pnDescription.setLayout( new BorderLayout() );
            pnDescription.setBorder( BorderFactory.createTitledBorder( Messages.get( "$MD11756" ) ) );
            pnDescription.add( getTpDescription(), BorderLayout.CENTER );
        }
        return pnDescription;
    }

    private JTextPane getTpDescription() {
        if ( tpDescription == null ) {
            tpDescription = new JTextPane();                
            tpDescription.setBackground( pnDescription.getBackground() );
            tpDescription.setEditable( false );
            tpDescription.setContentType( "text/html" );
            tpDescription.setText( Messages.get( "$MD11757" ) );
        }
        return tpDescription;
    }

}
