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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
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
import org.deegree.igeo.modules.geoprocessing.BufferModule;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.HelpManager;
import org.deegree.igeo.views.swing.DefaultPanel;
import org.deegree.igeo.views.swing.HelpFrame;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.model.feature.schema.GeometryPropertyType;
import org.deegree.model.spatialschema.Geometry;

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
public class BufferPanel extends DefaultPanel implements BufferModel {

    private static final long serialVersionUID = 3779476369485942902L;

    private JPanel pnText;

    private JTextField tfNewLayer;

    private JComboBox cbGeomProperty;

    private JSpinner spSegments;

    private JComboBox cbCapStyle;

    private JSpinner spDistance;

    private JLabel jLabel5;

    private JButton btHelp;

    private JPanel pnHelp;

    private JButton btCancel;

    private JButton btOK;

    private JPanel pnButtons;

    private JLabel jLabel4;

    private JLabel jLabel3;

    private JLabel jLabel2;

    private JLabel jLabel1;

    private JTextArea tpText;

    private GeometryPropertyType[] gpt;

    private String layerTitle;

    private Container parent;

    /**
     * 
     */
    public BufferPanel() {
    }

    /**
     * 
     * @param parent
     */
    BufferPanel( Container parent ) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#init(org.deegree.igeo.config.ViewFormType)
     */
    public void init( ViewFormType viewForm )
                            throws Exception {
        MapModel mapModel = owner.getApplicationContainer().getMapModel( null );
        List<Layer> layers = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
        Layer layer = layers.get( 0 );
        FeatureAdapter fa = (FeatureAdapter) layer.getDataAccess().get( 0 );
        this.layerTitle = layer.getTitle() + "_buffer";
        gpt = fa.getSchema().getGeometryProperties();
        initGUI();
    }

    private void initGUI() {
        try {
            {
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.1 };
                thisLayout.rowHeights = new int[] { 35, 35, 35, 35, 40, 7 };
                thisLayout.columnWeights = new double[] { 0.0, 0.0, 0.1, 0.1 };
                thisLayout.columnWidths = new int[] { 155, 146, 7, 7 };
                setLayout( thisLayout );
                {
                    pnText = new JPanel();
                    BorderLayout pnTextLayout = new BorderLayout();
                    pnText.setLayout( pnTextLayout );
                    add( pnText, new GridBagConstraints( 0, 0, 1, 5, 0.0, 0.0, GridBagConstraints.CENTER,
                                                         GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    pnText.setBorder( BorderFactory.createTitledBorder( null, Messages.getMessage( getLocale(),
                                                                                                   "$MD10560" ),
                                                                        TitledBorder.LEADING,
                                                                        TitledBorder.DEFAULT_POSITION ) );
                    {
                        tpText = new JTextArea();
                        tpText.setLineWrap( true );
                        tpText.setWrapStyleWord( true );
                        tpText.setEditable( false );
                        pnText.add( tpText, BorderLayout.CENTER );
                        tpText.setText( Messages.getMessage( getLocale(), "$MD10561" ) );
                        tpText.setBackground( pnText.getBackground() );
                    }
                }
                {
                    jLabel1 = new JLabel();
                    add( jLabel1, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                          GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0, 5 ), 0,
                                                          0 ) );
                    jLabel1.setText( Messages.getMessage( getLocale(), "$MD10562" ) );
                }
                {
                    jLabel2 = new JLabel();
                    add( jLabel2, new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                          GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0, 5 ), 0,
                                                          0 ) );
                    jLabel2.setText( Messages.getMessage( getLocale(), "$MD10563" ) );
                }
                {
                    jLabel3 = new JLabel();
                    add( jLabel3, new GridBagConstraints( 1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                          GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0, 5 ), 0,
                                                          0 ) );
                    jLabel3.setText( Messages.getMessage( getLocale(), "$MD10564" ) );
                }
                {
                    jLabel4 = new JLabel();
                    add( jLabel4, new GridBagConstraints( 1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                          GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0, 5 ), 0,
                                                          0 ) );
                    jLabel4.setText( Messages.getMessage( getLocale(), "$MD10565" ) );
                }
                {
                    jLabel5 = new JLabel();
                    add( jLabel5, new GridBagConstraints( 1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                          GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0, 5 ), 0,
                                                          0 ) );
                    jLabel5.setText( Messages.getMessage( getLocale(), "$MD10566" ) );
                }
                {
                    pnButtons = new JPanel();
                    FlowLayout pnButtonsLayout = new FlowLayout();
                    add( pnButtons, new GridBagConstraints( 0, 10, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                            GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    pnButtons.setLayout( pnButtonsLayout );
                    pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                    pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                    {
                        btOK = new JButton( Messages.getMessage( getLocale(), "$MD10567" ),
                                            IconRegistry.getIcon( "accept.png" ) );
                        btOK.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                if ( tfNewLayer.getText().trim().length() == 0 ) {
                                    DialogFactory.openWarningDialog( "application", this, "layer name must be entered",
                                                                     "Warning" );
                                    return;
                                }
                                ( (BufferModule<Container>) owner ).buffer();
                            }
                        } );
                        pnButtons.add( btOK );
                    }
                    {
                        if ( parent != null ) {
                            btCancel = new JButton( Messages.getMessage( getLocale(), "$MD10568" ),
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
                        add( pnHelp, new GridBagConstraints( 2, 10, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                             GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                        {
                            btHelp = new JButton( Messages.getMessage( getLocale(), "$MD10569" ),
                                                  IconRegistry.getIcon( "help.png" ) );
                            pnHelp.add( btHelp );
                            btHelp.addActionListener( new ActionListener() {
                                public void actionPerformed( ActionEvent e ) {
                                    HelpFrame hf = HelpFrame.getInstance( new HelpManager(
                                                                                           owner.getApplicationContainer() ) );
                                    hf.setVisible( true );
                                    hf.gotoModule( "Buffer" );
                                }
                            } );
                        }
                    }
                }
                {
                    spDistance = new JSpinner( new SpinnerNumberModel( 1d, 0, Integer.MAX_VALUE, 1d ) );
                    add( spDistance, new GridBagConstraints( 2, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                             GridBagConstraints.HORIZONTAL, new Insets( 0, 5, 0, 5 ),
                                                             0, 0 ) );
                }
                {
                    String[] tmp = StringTools.toArray( Messages.getMessage( getLocale(), "$MD10570" ), ",;", true );
                    cbCapStyle = new JComboBox( tmp );
                    add( cbCapStyle, new GridBagConstraints( 2, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                             GridBagConstraints.HORIZONTAL, new Insets( 0, 5, 0, 5 ),
                                                             0, 0 ) );
                }
                {
                    spSegments = new JSpinner( new SpinnerNumberModel( 12, 1, Integer.MAX_VALUE, 1 ) );
                    add( spSegments, new GridBagConstraints( 2, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                             GridBagConstraints.HORIZONTAL, new Insets( 0, 5, 0, 5 ),
                                                             0, 0 ) );
                }
                {
                    String[] s = new String[gpt.length];
                    for ( int i = 0; i < s.length; i++ ) {
                        s[i] = gpt[i].getName().getPrefixedName();
                    }
                    cbGeomProperty = new JComboBox( s );
                    add( cbGeomProperty, new GridBagConstraints( 2, 3, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                 GridBagConstraints.HORIZONTAL,
                                                                 new Insets( 0, 5, 0, 5 ), 0, 0 ) );
                }
                {
                    tfNewLayer = new JTextField( layerTitle );
                    add( tfNewLayer, new GridBagConstraints( 2, 4, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                             GridBagConstraints.HORIZONTAL, new Insets( 0, 5, 0, 5 ),
                                                             0, 0 ) );
                }
            }
            this.setSize( 520, 250 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * This method should return an instance of this class which does NOT initialize it's GUI elements. This method is
     * ONLY required by Jigloo if the superclass of this class is abstract or non-public. It is not needed in any other
     * situation.
     */
    public static Object getGUIBuilderInstance() {
        return new BufferPanel( Boolean.FALSE );
    }

    /**
     * This constructor is used by the getGUIBuilderInstance method to provide an instance of this class which has not
     * had it's GUI elements initialized (ie, initGUI is not called in this constructor).
     */
    public BufferPanel( Boolean initGUI ) {
        super();
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
        return new double[] { ( (Number) spDistance.getValue() ).doubleValue() };
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
        return tfNewLayer.getText();
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
        return BUFFERTYPE.outside_filled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#shallMerge()
     */
    public boolean shallMerge() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#getPropertyForBufferDistance()
     */
    public QualifiedName getPropertyForBufferDistance() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#isOverlayedBuffers()
     */
    public boolean isOverlayedBuffers() {
        return true;
    }

    /* (non-Javadoc)
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#getBufferUnit()
     */
    public Unit getBufferUnit() {
        MapModel mapModel = owner.getApplicationContainer().getMapModel( null );        
        return mapModel.getCoordinateSystem().getAxisUnits()[0];
    }
    
    

}
