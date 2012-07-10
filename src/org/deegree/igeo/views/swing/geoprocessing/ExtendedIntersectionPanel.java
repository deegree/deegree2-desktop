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
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.deegree.datatypes.QualifiedName;
import org.deegree.igeo.commands.geoprocessing.LayerIntersectionCommand.INTERSECTION_TYPE;
import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.LayerGroup;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MapModelVisitor;
import org.deegree.igeo.views.HelpManager;
import org.deegree.igeo.views.swing.DefaultPanel;
import org.deegree.igeo.views.swing.HelpFrame;
import org.deegree.igeo.views.swing.util.IconRegistry;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ExtendedIntersectionPanel extends DefaultPanel implements IntersectionModel {

    private static final long serialVersionUID = -7778657267845163052L;

    private JPanel pnHelp;

    private JButton btHelp;

    private JLabel lbNewLayer;

    private JComboBox cbComparsionLayer;

    private ButtonGroup bgIntersection;

    private JLabel imgSymDiff;

    private JLabel imgDifference;

    private JLabel imgUnion;

    private JLabel imgIntersection;

    private JTextField tfNewLayer;

    private JComboBox cbMainLayer;

    private JLabel lbComparsionLayer;

    private JLabel lbMainLayer;

    private JRadioButton rbSymDiff;

    private JRadioButton rbDifference;

    private JRadioButton rbUnion;

    private JRadioButton rbIntersection;

    private JPanel pnControl;

    private Layer[] cbModel;

    private String layerTitle;

    /**
     * 
     */
    public ExtendedIntersectionPanel() {
        this.layerTitle = "intersection_result";
        initGUI();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#init(org.deegree.igeo.config.ViewFormType)
     */
    public void init( ViewFormType viewForm )
                            throws Exception {
        MapModel mapModel = owner.getApplicationContainer().getMapModel( null );

        final List<Layer> layers = new ArrayList<Layer>();
        mapModel.walkLayerTree( new MapModelVisitor() {

            public void visit( Layer layer )
                                    throws Exception {
                layers.add( layer );
            }

            public void visit( LayerGroup layerGroup )
                                    throws Exception {
            }

        } );
        List<Layer> list = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
        cbModel = layers.toArray( new Layer[layers.size()] );
        cbMainLayer.setModel( new DefaultComboBoxModel( cbModel ) );
        cbComparsionLayer.setModel( new DefaultComboBoxModel( cbModel ) );
        if ( list.size() > 0 ) {
            cbMainLayer.setSelectedItem( list.get( 0 ) );
            if ( list.size() > 1 ) {
                cbComparsionLayer.setSelectedItem( list.get( 1 ) );
            }
        }
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            this.setPreferredSize( new java.awt.Dimension( 561, 251 ) );
            thisLayout.rowWeights = new double[] { 0.0, 0.1 };
            thisLayout.rowHeights = new int[] { 217, 7 };
            thisLayout.columnWeights = new double[] { 0.1 };
            thisLayout.columnWidths = new int[] { 7 };
            this.setLayout( thisLayout );
            {
                pnHelp = new JPanel();
                FlowLayout pnHelpLayout = new FlowLayout();
                pnHelpLayout.setAlignment( FlowLayout.RIGHT );
                this.add( pnHelp, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                          GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnHelp.setLayout( pnHelpLayout );
                {
                    btHelp = new JButton( Messages.getMessage( getLocale(), "$MD10574" ),
                                          IconRegistry.getIcon( "help.png" ) );
                    pnHelp.add( btHelp );
                    btHelp.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            HelpFrame hf = HelpFrame.getInstance( new HelpManager( owner.getApplicationContainer() ) );
                            hf.setVisible( true );
                            hf.gotoModule( "Intersection" );
                        }
                    } );
                }
            }
            {
                pnControl = new JPanel();
                GridBagLayout pnControlLayout = new GridBagLayout();
                this.add( pnControl, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                             GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnControlLayout.rowWeights = new double[] { 0.1, 0.1, 0.1, 0.1 };
                pnControlLayout.rowHeights = new int[] { 7, 7, 7, 7 };
                pnControlLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.1 };
                pnControlLayout.columnWidths = new int[] { 127, 69, 146, 7 };
                pnControl.setLayout( pnControlLayout );
                pnControl.setBorder( BorderFactory.createTitledBorder( null, Messages.getMessage( getLocale(),
                                                                                                  "$MD11418" ),
                                                                       TitledBorder.LEADING,
                                                                       TitledBorder.DEFAULT_POSITION ) );
                {
                    rbIntersection = new JRadioButton( Messages.getMessage( getLocale(), "$MD11413" ) );
                    pnControl.add( rbIntersection, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                           GridBagConstraints.CENTER,
                                                                           GridBagConstraints.HORIZONTAL,
                                                                           new Insets( 0, 9, 0, 0 ), 0, 0 ) );
                    rbIntersection.setActionCommand( INTERSECTION_TYPE.Intersection.name() );
                    getBgIntersection().add( rbIntersection );
                    rbIntersection.setSelected( true );
                }
                {
                    rbUnion = new JRadioButton( Messages.getMessage( getLocale(), "$MD11414" ) );
                    pnControl.add( rbUnion, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                    GridBagConstraints.HORIZONTAL, new Insets( 0, 9, 0,
                                                                                                               0 ), 0,
                                                                    0 ) );
                    rbUnion.setActionCommand( INTERSECTION_TYPE.Union.name() );
                    getBgIntersection().add( rbUnion );
                }
                {
                    rbDifference = new JRadioButton( Messages.getMessage( getLocale(), "$MD11415" ) );
                    pnControl.add( rbDifference, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0,
                                                                         GridBagConstraints.CENTER,
                                                                         GridBagConstraints.HORIZONTAL,
                                                                         new Insets( 0, 9, 0, 0 ), 0, 0 ) );
                    rbDifference.setActionCommand( INTERSECTION_TYPE.Difference.name() );
                    getBgIntersection().add( rbDifference );
                }
                {
                    rbSymDiff = new JRadioButton( Messages.getMessage( getLocale(), "$MD11416" ) );
                    pnControl.add( rbSymDiff, new GridBagConstraints( 0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                      GridBagConstraints.HORIZONTAL,
                                                                      new Insets( 0, 9, 0, 0 ), 0, 0 ) );
                    rbSymDiff.setActionCommand( INTERSECTION_TYPE.SymmetricDifference.name() );
                    getBgIntersection().add( rbSymDiff );
                }
                {
                    lbMainLayer = new JLabel( Messages.getMessage( getLocale(), "$MD10577" ) );
                    pnControl.add( lbMainLayer, new GridBagConstraints( 2, 0, 1, 1, 0.0, 0.0,
                                                                        GridBagConstraints.CENTER,
                                                                        GridBagConstraints.HORIZONTAL,
                                                                        new Insets( 0, 9, 0, 0 ), 0, 0 ) );
                }
                {
                    lbComparsionLayer = new JLabel( Messages.getMessage( getLocale(), "$MD10578" ) );
                    pnControl.add( lbComparsionLayer, new GridBagConstraints( 2, 1, 1, 1, 0.0, 0.0,
                                                                              GridBagConstraints.CENTER,
                                                                              GridBagConstraints.HORIZONTAL,
                                                                              new Insets( 0, 9, 0, 0 ), 0, 0 ) );
                }
                {
                    lbNewLayer = new JLabel( Messages.getMessage( getLocale(), "$MD10579" ) );
                    pnControl.add( lbNewLayer, new GridBagConstraints( 2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                       GridBagConstraints.HORIZONTAL,
                                                                       new Insets( 0, 9, 0, 0 ), 0, 0 ) );
                }
                {
                    cbMainLayer = new JComboBox();
                    pnControl.add( cbMainLayer, new GridBagConstraints( 3, 0, 1, 1, 0.0, 0.0,
                                                                        GridBagConstraints.CENTER,
                                                                        GridBagConstraints.HORIZONTAL,
                                                                        new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                }
                {
                    cbComparsionLayer = new JComboBox();
                    pnControl.add( cbComparsionLayer, new GridBagConstraints( 3, 1, 1, 1, 0.0, 0.0,
                                                                              GridBagConstraints.CENTER,
                                                                              GridBagConstraints.HORIZONTAL,
                                                                              new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                }
                {
                    tfNewLayer = new JTextField( layerTitle );
                    pnControl.add( tfNewLayer, new GridBagConstraints( 3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                       GridBagConstraints.HORIZONTAL,
                                                                       new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                }
                {
                    imgIntersection = new JLabel( new ImageIcon( getClass().getResource( "intersection.png" ) ) );
                    pnControl.add( imgIntersection, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0,
                                                                            GridBagConstraints.WEST,
                                                                            GridBagConstraints.NONE,
                                                                            new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                }
                {
                    imgUnion = new JLabel( new ImageIcon( getClass().getResource( "union.png" ) ) );
                    pnControl.add( imgUnion, new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                                     GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ),
                                                                     0, 0 ) );
                }
                {
                    imgDifference = new JLabel( new ImageIcon( getClass().getResource( "not.png" ) ) );
                    pnControl.add( imgDifference, new GridBagConstraints( 1, 2, 1, 1, 0.0, 0.0,
                                                                          GridBagConstraints.WEST,
                                                                          GridBagConstraints.NONE, new Insets( 0, 0, 0,
                                                                                                               0 ), 0,
                                                                          0 ) );
                }
                {
                    imgSymDiff = new JLabel( new ImageIcon( getClass().getResource( "2xor.png" ) ) );
                    pnControl.add( imgSymDiff, new GridBagConstraints( 1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                                       GridBagConstraints.NONE,
                                                                       new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private ButtonGroup getBgIntersection() {
        if ( bgIntersection == null ) {
            bgIntersection = new ButtonGroup();
        }
        return bgIntersection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.IntersectionModel#getCompareLayer()
     */
    public Layer getCompareLayer() {
        return cbModel[cbComparsionLayer.getSelectedIndex()];
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.IntersectionModel#getMainLayer()
     */
    public Layer getMainLayer() {
        return cbModel[cbMainLayer.getSelectedIndex()];
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.IntersectionModel#getNewLayerName()
     */
    public String getNewLayerName() {
        return tfNewLayer.getText();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.IntersectionModel#getCompareLayerGeometry()
     */
    public QualifiedName getCompareLayerGeometry() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.IntersectionModel#getMainLayerGeometry()
     */
    public QualifiedName getMainLayerGeometry() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.IntersectionModel#getIntersectionType()
     */
    public INTERSECTION_TYPE getIntersectionType() {
        return INTERSECTION_TYPE.valueOf( bgIntersection.getSelection().getActionCommand() );
    }

}
