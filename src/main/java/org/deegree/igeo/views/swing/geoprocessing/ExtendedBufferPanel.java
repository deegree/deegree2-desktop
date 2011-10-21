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
package org.deegree.igeo.views.swing.geoprocessing;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import org.deegree.crs.components.Unit;
import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.util.StringTools;
import org.deegree.igeo.commands.geoprocessing.BufferCommand.BUFFERTYPE;
import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.views.HelpManager;
import org.deegree.igeo.views.swing.DefaultPanel;
import org.deegree.igeo.views.swing.HelpFrame;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.model.feature.schema.GeometryPropertyType;
import org.deegree.model.spatialschema.Geometry;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ExtendedBufferPanel extends DefaultPanel implements BufferModel {

    private static final long serialVersionUID = 300282309425961273L;

    private JButton btHelp;

    private JSpinner spDistance;

    private JLabel imgOutside;

    private JLabel imgInside;

    private JCheckBox cbMerge;

    private JRadioButton rbOutsideFilled;

    private JLabel imgOuterFill;

    private JComboBox cbUnits;

    private JLabel lbUnits;

    private JButton btExpert;

    private JLabel imgMerge;

    private JPanel pnMerge;

    private JLabel imgInnerFill;

    private JLabel imgBoth;

    private JRadioButton rbInsideFilled;

    private JRadioButton rbBoth;

    private JRadioButton rbOutside;

    private JRadioButton rbInside;

    private ButtonGroup bgBufferType;

    private JTextField tfNewLayerName;

    private JComboBox cbCapStyle;

    private JComboBox cbGeomProperty;

    private JSpinner spSegments;

    private JLabel lbNewLayerName;

    private JLabel lbGeomProperty;

    private JLabel lbSegments;

    private JLabel lbCapStyle;

    private JLabel lbDistance;

    private JPanel pnControl;

    private JPanel pnButtons;

    private GeometryPropertyType[] gpt;

    private String layerTitle;

    private int numberOfBuffers = 1;

    private QualifiedName propertyForBufferDistance;

    private boolean overlayedBuffers = true;

    private MapModel mapModel;

    /**
     * 
     */
    public ExtendedBufferPanel() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#init(org.deegree.igeo.config.ViewFormType)
     */
    public void init( ViewFormType viewForm )
                            throws Exception {
        mapModel = owner.getApplicationContainer().getMapModel( null );
        List<Layer> layers = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
        Layer layer = layers.get( 0 );
        FeatureAdapter fa = (FeatureAdapter) layer.getDataAccess().get( 0 );
        this.layerTitle = layer.getTitle() + "_buffer";
        gpt = fa.getSchema().getGeometryProperties();
        initGUI();
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            this.setPreferredSize( new java.awt.Dimension( 627, 335 ) );
            thisLayout.rowWeights = new double[] { 0.0, 0.1 };
            thisLayout.rowHeights = new int[] { 298, 7 };
            thisLayout.columnWeights = new double[] { 0.1 };
            thisLayout.columnWidths = new int[] { 7 };
            this.setLayout( thisLayout );
            {
                pnButtons = new JPanel();
                FlowLayout pnButtonsLayout = new FlowLayout();
                pnButtonsLayout.setAlignment( FlowLayout.RIGHT );
                pnButtons.setLayout( pnButtonsLayout );
                this.add( pnButtons, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                             GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                {
                    btHelp = new JButton( Messages.getMessage( getLocale(), "$MD10569" ),
                                          IconRegistry.getIcon( "help.png" ) );
                    btHelp.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            HelpFrame hf = HelpFrame.getInstance( new HelpManager( owner.getApplicationContainer() ) );
                            hf.setVisible( true );
                            hf.gotoModule( "Buffer" );
                        }
                    } );
                    pnButtons.add( btHelp );
                }
            }
            {
                pnControl = new JPanel();
                GridBagLayout pnControlLayout = new GridBagLayout();
                this.add( pnControl, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                             GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnControlLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1 };
                pnControlLayout.rowHeights = new int[] { 40, 40, 40, 40, 39, 35, 7 };
                pnControlLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.1 };
                pnControlLayout.columnWidths = new int[] { 137, 105, 170, 7 };
                pnControl.setLayout( pnControlLayout );
                pnControl.setBorder( BorderFactory.createTitledBorder( null, Messages.getMessage( getLocale(),
                                                                                                  "$MD11420" ),
                                                                       TitledBorder.LEADING,
                                                                       TitledBorder.DEFAULT_POSITION ) );
                {
                    lbDistance = new JLabel( Messages.getMessage( getLocale(), "$MD10562" ) );
                    pnControl.add( lbDistance, new GridBagConstraints( 2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                       GridBagConstraints.HORIZONTAL,
                                                                       new Insets( 0, 10, 0, 0 ), 0, 0 ) );
                }
                {
                    lbCapStyle = new JLabel( Messages.getMessage( getLocale(), "$MD10563" ) );
                    pnControl.add( lbCapStyle, new GridBagConstraints( 2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                       GridBagConstraints.HORIZONTAL,
                                                                       new Insets( 0, 10, 0, 0 ), 0, 0 ) );
                }
                {
                    lbSegments = new JLabel( Messages.getMessage( getLocale(), "$MD10564" ) );
                    pnControl.add( lbSegments, new GridBagConstraints( 2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                       GridBagConstraints.HORIZONTAL,
                                                                       new Insets( 0, 10, 0, 0 ), 0, 0 ) );
                }
                {
                    lbGeomProperty = new JLabel( Messages.getMessage( getLocale(), "$MD10565" ) );
                    pnControl.add( lbGeomProperty, new GridBagConstraints( 2, 4, 1, 1, 0.0, 0.0,
                                                                           GridBagConstraints.CENTER,
                                                                           GridBagConstraints.HORIZONTAL,
                                                                           new Insets( 0, 10, 0, 0 ), 0, 0 ) );
                }
                {
                    lbNewLayerName = new JLabel( Messages.getMessage( getLocale(), "$MD10566" ) );
                    pnControl.add( lbNewLayerName, new GridBagConstraints( 2, 6, 1, 1, 0.0, 0.0,
                                                                           GridBagConstraints.NORTH,
                                                                           GridBagConstraints.HORIZONTAL,
                                                                           new Insets( 16, 10, 0, 0 ), 0, 0 ) );
                }
                {
                    spDistance = new JSpinner( new SpinnerNumberModel( 1d, 0, Integer.MAX_VALUE, 1d ) );
                    pnControl.add( spDistance, new GridBagConstraints( 3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                       GridBagConstraints.HORIZONTAL,
                                                                       new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                }
                {
                    String[] tmp = StringTools.toArray( Messages.getMessage( getLocale(), "$MD10570" ), ",;", true );
                    cbCapStyle = new JComboBox( tmp );
                    pnControl.add( cbCapStyle, new GridBagConstraints( 3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                       GridBagConstraints.HORIZONTAL,
                                                                       new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                }
                {
                    spSegments = new JSpinner( new SpinnerNumberModel( 12, 1, Integer.MAX_VALUE, 1 ) );
                    pnControl.add( spSegments, new GridBagConstraints( 3, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                       GridBagConstraints.HORIZONTAL,
                                                                       new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                }
                {
                    String[] s = new String[gpt.length];
                    for ( int i = 0; i < s.length; i++ ) {
                        s[i] = gpt[i].getName().getPrefixedName();
                    }
                    cbGeomProperty = new JComboBox( s );
                    pnControl.add( cbGeomProperty, new GridBagConstraints( 3, 4, 1, 1, 0.0, 0.0,
                                                                           GridBagConstraints.EAST,
                                                                           GridBagConstraints.HORIZONTAL,
                                                                           new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                }
                {
                    tfNewLayerName = new JTextField();
                    pnControl.add( tfNewLayerName, new GridBagConstraints( 3, 6, 1, 1, 0.0, 0.0,
                                                                           GridBagConstraints.NORTH,
                                                                           GridBagConstraints.HORIZONTAL,
                                                                           new Insets( 12, 9, 0, 9 ), 0, 0 ) );
                    tfNewLayerName.setText( layerTitle );
                }
                {
                    pnControl.add( getRbInside(), new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                          GridBagConstraints.CENTER,
                                                                          GridBagConstraints.HORIZONTAL,
                                                                          new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                    pnControl.add( getRbOutside(), new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                                                           GridBagConstraints.CENTER,
                                                                           GridBagConstraints.HORIZONTAL,
                                                                           new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                    pnControl.add( getRbBoth(), new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0,
                                                                        GridBagConstraints.CENTER,
                                                                        GridBagConstraints.HORIZONTAL,
                                                                        new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                    pnControl.add( getRbInnerFill(), new GridBagConstraints( 0, 3, 1, 1, 0.0, 0.0,
                                                                             GridBagConstraints.CENTER,
                                                                             GridBagConstraints.HORIZONTAL,
                                                                             new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                    pnControl.add( getRbOuterFill(), new GridBagConstraints( 0, 4, 1, 1, 0.0, 0.0,
                                                                             GridBagConstraints.CENTER,
                                                                             GridBagConstraints.HORIZONTAL,
                                                                             new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                    pnControl.add( getImgInside(), new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0,
                                                                           GridBagConstraints.WEST,
                                                                           GridBagConstraints.NONE, new Insets( 0, 0,
                                                                                                                0, 0 ),
                                                                           0, 0 ) );
                    pnControl.add( getImgOutside(), new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0,
                                                                            GridBagConstraints.WEST,
                                                                            GridBagConstraints.NONE,
                                                                            new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    pnControl.add( getImgBoth(),
                                   new GridBagConstraints( 1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                           GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    pnControl.add( getImgInnerFill(), new GridBagConstraints( 1, 3, 1, 1, 0.0, 0.0,
                                                                              GridBagConstraints.WEST,
                                                                              GridBagConstraints.NONE,
                                                                              new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    pnControl.add( getImgOuterFill(), new GridBagConstraints( 1, 4, 1, 1, 0.0, 0.0,
                                                                              GridBagConstraints.WEST,
                                                                              GridBagConstraints.NONE,
                                                                              new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    pnControl.add( getPnMerge(),
                                   new GridBagConstraints( 0, 6, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                           GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    pnControl.add( getBtMultibleDistance(), new GridBagConstraints( 3, 1, 1, 1, 0.0, 0.0,
                                                                                    GridBagConstraints.EAST,
                                                                                    GridBagConstraints.NONE,
                                                                                    new Insets( 0, 10, 0, 9 ), 0, 0 ) );
                    pnControl.add( getLbUnits(), new GridBagConstraints( 2, 5, 1, 1, 0.0, 0.0,
                                                                         GridBagConstraints.CENTER,
                                                                         GridBagConstraints.HORIZONTAL,
                                                                         new Insets( 9, 9, 0, 0 ), 0, 0 ) );
                    pnControl.add( getCbUnits(), new GridBagConstraints( 3, 5, 1, 1, 0.0, 0.0,
                                                                         GridBagConstraints.CENTER,
                                                                         GridBagConstraints.HORIZONTAL,
                                                                         new Insets( 6, 9, 0, 9 ), 0, 0 ) );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private ButtonGroup getBgBufferType() {
        if ( bgBufferType == null ) {
            bgBufferType = new ButtonGroup();
        }
        return bgBufferType;
    }

    private JRadioButton getRbInside() {
        if ( rbInside == null ) {
            rbInside = new JRadioButton( Messages.getMessage( getLocale(), "$MD11406" ) );
            rbInside.setActionCommand( BUFFERTYPE.inside.name() );
            getBgBufferType().add( rbInside );
        }
        return rbInside;
    }

    private JRadioButton getRbOutside() {
        if ( rbOutside == null ) {
            rbOutside = new JRadioButton( Messages.getMessage( getLocale(), "$MD11407" ) );
            rbOutside.setActionCommand( BUFFERTYPE.outside.name() );
            getBgBufferType().add( rbOutside );
        }
        return rbOutside;
    }

    private JRadioButton getRbBoth() {
        if ( rbBoth == null ) {
            rbBoth = new JRadioButton( Messages.getMessage( getLocale(), "$MD11408" ) );
            rbBoth.setActionCommand( BUFFERTYPE.both.name() );
            getBgBufferType().add( rbBoth );
        }
        return rbBoth;
    }

    private JRadioButton getRbInnerFill() {
        if ( rbInsideFilled == null ) {
            rbInsideFilled = new JRadioButton( Messages.getMessage( getLocale(), "$MD11409" ) );
            rbInsideFilled.setActionCommand( BUFFERTYPE.inside_filled.name() );
            getBgBufferType().add( rbInsideFilled );
        }
        return rbInsideFilled;
    }

    private JRadioButton getRbOuterFill() {
        if ( rbOutsideFilled == null ) {
            rbOutsideFilled = new JRadioButton( Messages.getMessage( getLocale(), "$MD11410" ) );
            rbOutsideFilled.setActionCommand( BUFFERTYPE.outside_filled.name() );
            rbOutsideFilled.setSelected( true );
            getBgBufferType().add( rbOutsideFilled );
        }
        return rbOutsideFilled;
    }

    private JCheckBox getCbMerge() {
        if ( cbMerge == null ) {
            cbMerge = new JCheckBox( Messages.getMessage( getLocale(), "$MD11411" ) );
        }
        return cbMerge;
    }

    private JButton getBtMultibleDistance() {
        if ( btExpert == null ) {
            btExpert = new JButton( Messages.getMessage( getLocale(), "$MD11412" ) );
            btExpert.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    // JOptionPane.showMessageDialog( null, "not supported yet" );
                    MapModel mapModel = owner.getApplicationContainer().getMapModel( null );
                    List<Layer> layers = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
                    Layer layer = layers.get( 0 );
                    FeatureAdapter fa = (FeatureAdapter) layer.getDataAccess().get( 0 );
                    BufferExpertOptionsDialog beo = new BufferExpertOptionsDialog( -1, fa.getSchema(), null );
                    numberOfBuffers = beo.getNumberOfBuffers();
                    propertyForBufferDistance = beo.getPropertyForBufferDistance();
                    overlayedBuffers = beo.isOverlayedBuffers();
                }
            } );
        }
        return btExpert;
    }

    private JLabel getImgInside() {
        if ( imgInside == null ) {
            imgInside = new JLabel();
            imgInside.setIcon( new ImageIcon( getClass().getResource( "inner_ring.png" ) ) );
        }
        return imgInside;
    }

    private JLabel getImgOutside() {
        if ( imgOutside == null ) {
            imgOutside = new JLabel();
            imgOutside.setIcon( new ImageIcon( getClass().getResource( "outer_ring.png" ) ) );
        }
        return imgOutside;
    }

    private JLabel getImgBoth() {
        if ( imgBoth == null ) {
            imgBoth = new JLabel();
            imgBoth.setIcon( new ImageIcon( getClass().getResource( "innerouter_ring.png" ) ) );
        }
        return imgBoth;
    }

    private JLabel getImgInnerFill() {
        if ( imgInnerFill == null ) {
            imgInnerFill = new JLabel();
            imgInnerFill.setIcon( new ImageIcon( getClass().getResource( "inner_polygon.png" ) ) );
        }
        return imgInnerFill;
    }

    private JLabel getImgOuterFill() {
        if ( imgOuterFill == null ) {
            imgOuterFill = new JLabel();
            imgOuterFill.setIcon( new ImageIcon( getClass().getResource( "outer_polygon.png" ) ) );
        }
        return imgOuterFill;
    }

    private JPanel getPnMerge() {
        if ( pnMerge == null ) {
            pnMerge = new JPanel();
            FlowLayout pnMergeLayout = new FlowLayout();
            pnMergeLayout.setAlignment( FlowLayout.LEFT );
            pnMerge.setLayout( pnMergeLayout );
            pnMerge.add( getCbMerge() );
            pnMerge.add( getImgMerge() );
        }
        return pnMerge;
    }

    private JLabel getImgMerge() {
        if ( imgMerge == null ) {
            imgMerge = new JLabel();
            imgMerge.setIcon( new ImageIcon( getClass().getResource( "merge_buffer.png" ) ) );
        }
        return imgMerge;
    }

    private JLabel getLbUnits() {
        if ( lbUnits == null ) {
            lbUnits = new JLabel( Messages.getMessage( getLocale(), "$MD11490" ) );
        }
        return lbUnits;
    }

    private JComboBox getCbUnits() {
        if ( cbUnits == null ) {
            String[] s = StringTools.toArray( Messages.getMessage( getLocale(), "$MD11491" ), ",;", true );
            Unit unit = mapModel.getCoordinateSystem().getAxisUnits()[0];
            if ( unit.equals( Unit.DEGREE ) ) {
                s = new String[] { s[0] };
            }
            cbUnits = new JComboBox( new DefaultComboBoxModel( s ) );
        }
        return cbUnits;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#getCapStyle()
     */
    public int getCapStyle() {
        int idx = cbCapStyle.getSelectedIndex();
        if ( idx == 0 ) {
            return Geometry.BUFFER_CAP_ROUND;
        }
        if ( idx == 1 ) {
            return Geometry.BUFFER_CAP_BUTT;
        }
        if ( idx == 2 ) {
            return Geometry.BUFFER_CAP_SQUARE;
        }
        return Geometry.BUFFER_CAP_ROUND;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#getDistance()
     */
    public double[] getDistances() {
        int k = 1;
        if ( numberOfBuffers > 0 ) {
            k = numberOfBuffers;
        }
        double[] distances = new double[k];
        for ( int i = 0; i < distances.length; i++ ) {
            distances[i] = ( (Number) spDistance.getValue() ).doubleValue() * ( i + 1 );
        }
        return distances;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#getGeometryProperty()
     */
    public QualifiedName getGeometryProperty() {
        return gpt[cbGeomProperty.getSelectedIndex()].getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#getNewLayerName()
     */
    public String getNewLayerName() {
        return tfNewLayerName.getText();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#getSegments()
     */
    public int getSegments() {
        return ( (Number) spSegments.getValue() ).intValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#getBufferType()
     */
    public BUFFERTYPE getBufferType() {
        return BUFFERTYPE.valueOf( bgBufferType.getSelection().getActionCommand() );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#shallMerge()
     */
    public boolean shallMerge() {
        return cbMerge.isSelected();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#getPropertyForBufferDistance()
     */
    public QualifiedName getPropertyForBufferDistance() {
        return propertyForBufferDistance;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#isOverlayedBuffers()
     */
    public boolean isOverlayedBuffers() {
        return overlayedBuffers;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#getBufferUnit()
     */
    public Unit getBufferUnit() {
        int idx = cbUnits.getSelectedIndex();
        Unit unit = null;
        String name = cbUnits.getSelectedItem().toString();
        if ( idx == 0 ) {
            unit = mapModel.getCoordinateSystem().getAxisUnits()[0];
        } else if ( idx == 1 ) {
            unit = new Unit( "cm", name );
        } else if ( idx == 2 ) {
            unit = Unit.METRE;
        } else if ( idx == 3 ) {
            unit = new Unit( "km", name );
        }
        return unit;
    }

}
