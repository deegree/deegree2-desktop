//$HeadURL: svn+ssh://developername@svn.wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.deegree.datatypes.QualifiedName;
import org.deegree.igeo.commands.geoprocessing.LayerIntersectionCommand.INTERSECTION_TYPE;
import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.LayerGroup;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MapModelVisitor;
import org.deegree.igeo.modules.geoprocessing.IntersectionModule;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.HelpManager;
import org.deegree.igeo.views.swing.DefaultPanel;
import org.deegree.igeo.views.swing.HelpFrame;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.model.Identifier;

/**
 * 
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author: Andreas Poth $
 * 
 * @version $Revision: $, $Date: $
 * 
 */
public class IntersectionPanel extends DefaultPanel implements IntersectionModel {

    private static final long serialVersionUID = -4418996653212276373L;

    private JPanel pnButtons;

    private JButton btCancel;

    private JPanel pnHelp;

    private JButton btHelp;

    private JPanel pnText;

    private JComboBox cbComparisonLayer;

    private JComboBox cbMainLayer;

    private JTextField tfNewLayer;

    private JLabel jLabel3;

    private JLabel jLabel2;

    private JLabel jLabel1;

    private JTextArea tpText;

    private JButton btOK;

    private Container parent;

    private Layer[] cbModel;

    private String layerTitle;

    /**
     * 
     */
    public IntersectionPanel() {
        initGUI();
    }

    /**
     * 
     * @param parent
     */
    public IntersectionPanel( Container parent ) {
        this.parent = parent;
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
        String mmId = owner.getInitParameter( "assignedMapModel" );
        MapModel mapModel = owner.getApplicationContainer().getMapModel( new Identifier( mmId ) );

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
        cbComparisonLayer.setModel( new DefaultComboBoxModel( cbModel ) );
        if ( list.size() > 0 ) {
            cbMainLayer.setSelectedItem( list.get( 0 ) );
            if ( list.size() > 1 ) {
                cbComparisonLayer.setSelectedItem( list.get( 1 ) );
            }
        }
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.1 };
            thisLayout.rowHeights = new int[] { 35, 35, 35, 32, 7 };
            thisLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.1 };
            thisLayout.columnWidths = new int[] { 157, 115, 57, 7 };
            this.setPreferredSize( new java.awt.Dimension( 444, 151 ) );
            this.setLayout( thisLayout );
            {
                pnButtons = new JPanel();
                this.add( pnButtons, new GridBagConstraints( 0, 4, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                             GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                FlowLayout pnButtonsLayout = new FlowLayout();
                pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                pnButtons.setLayout( pnButtonsLayout );
                {
                    btOK = new JButton( Messages.getMessage( getLocale(), "$MD10571" ),
                                        IconRegistry.getIcon( "accept.png" ) );
                    pnButtons.add( btOK );
                    btOK.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            if ( tfNewLayer.getText().trim().length() == 0 ) {
                                DialogFactory.openWarningDialog( "application", IntersectionPanel.this,
                                                                 Messages.getMessage( getLocale(), "$MD10572" ),
                                                                 "Warning" );
                                return;
                            }
                            ( (IntersectionModule<Container>) owner ).intersect();
                        }
                    } );
                }
                {
                    if ( parent != null ) {
                        btCancel = new JButton( Messages.getMessage( getLocale(), "$MD10573" ),
                                                IconRegistry.getIcon( "cancel.png" ) );
                        btCancel.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                owner.clear();
                                try {
                                    Method m = parent.getClass().getMethod( "dispose", new Class<?>[0] );
                                    if ( m != null ) {
                                        m.invoke( parent, new Object[0] );
                                    }
                                } catch ( Exception e1 ) {
                                    e1.printStackTrace();
                                }
                            }
                        } );
                        pnButtons.add( btCancel );
                    }

                }
            }
            {
                if ( !( parent instanceof JDialog ) ) {
                    // help window can not be opened if a modal dialog is visible
                    pnHelp = new JPanel();
                    FlowLayout pnHelpLayout = new FlowLayout();
                    pnHelpLayout.setAlignment( FlowLayout.RIGHT );
                    pnHelp.setLayout( pnHelpLayout );
                    this.add( pnHelp, new GridBagConstraints( 3, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                              GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
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
            }
            {
                pnText = new JPanel();
                BorderLayout pnTextLayout = new BorderLayout();
                this.add( pnText, new GridBagConstraints( 0, 0, 1, 4, 0.0, 0.0, GridBagConstraints.CENTER,
                                                          GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnText.setLayout( pnTextLayout );
                pnText.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD10575" ) ) );
                {
                    tpText = new JTextArea();
                    tpText.setEditable( false );
                    tpText.setWrapStyleWord( true );
                    tpText.setLineWrap( true );
                    pnText.add( tpText, BorderLayout.CENTER );
                    tpText.setText( Messages.getMessage( getLocale(), "$MD10576" ) );
                    tpText.setBackground( pnText.getBackground() );
                }
            }
            {
                jLabel1 = new JLabel();
                this.add( jLabel1, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                           GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0, 5 ), 0,
                                                           0 ) );
                jLabel1.setText( Messages.getMessage( getLocale(), "$MD10577" ) );
            }
            {
                jLabel2 = new JLabel();
                this.add( jLabel2, new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                           GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0, 5 ), 0,
                                                           0 ) );
                jLabel2.setText( Messages.getMessage( getLocale(), "$MD10578" ) );
            }
            {
                jLabel3 = new JLabel();
                this.add( jLabel3, new GridBagConstraints( 1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                           GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0, 5 ), 0,
                                                           0 ) );
                jLabel3.setText( Messages.getMessage( getLocale(), "$MD10579" ) );
            }
            {
                tfNewLayer = new JTextField( layerTitle );
                this.add( tfNewLayer, new GridBagConstraints( 2, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                              GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 5 ),
                                                              0, 0 ) );
            }
            {
                cbMainLayer = new JComboBox();
                this.add( cbMainLayer, new GridBagConstraints( 2, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                               GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 5 ),
                                                               0, 0 ) );
            }
            {
                cbComparisonLayer = new JComboBox();
                this.add( cbComparisonLayer, new GridBagConstraints( 2, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                     GridBagConstraints.HORIZONTAL, new Insets( 0, 0,
                                                                                                                0, 5 ),
                                                                     0, 0 ) );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.IntersectionModel#getCompareLayer()
     */
    public Layer getCompareLayer() {
        return cbModel[cbComparisonLayer.getSelectedIndex()];
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
        // TODO Auto-generated method stub
        return null;
    }

}
