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
package org.deegree.igeo.views.swing.digitize.construction;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import org.deegree.framework.utils.CRSUtils;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.DigitizerModule;
import org.deegree.igeo.views.HelpManager;
import org.deegree.igeo.views.swing.AutoCompleteComboBox;
import org.deegree.igeo.views.swing.HelpFrame;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;

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
public class PointByCoordinateDialog extends JDialog {

    private static final long serialVersionUID = -8542956780460169455L;

    private JLabel jLabel2;

    private JSpinner spX;

    private JLabel jLabel3;

    private JComboBox cbCRS;

    private JLabel jLabel1;

    private JButton btHelp;

    private JPanel pnHelp;

    private JButton btCancel;

    private JButton btOK;

    private JPanel btPanel;

    private JSpinner spY;

    private Container parent;

    private Point point;

    private DigitizerModule<Container> owner;

    private static String[] crsList;

    static {
        if ( crsList == null ) {
            crsList = CRSUtils.getAvailableEPSGCodesAsArray();
        }
    }

    /**
     * 
     * @param parent
     * @param owner
     */
    public PointByCoordinateDialog( Container parent, DigitizerModule<Container> owner ) {
        this.parent = parent;
        this.owner = owner;
        setTitle( Messages.getMessage( getLocale(), "$MD11111" ) );
        initGUI();
        setXY();
        MapModel mm = owner.getApplicationContainer().getMapModel( null );
        cbCRS.setSelectedItem( mm.getCoordinateSystem().getIdentifier() );
        setLocation( parent.getX() + 150, parent.getY() + 150 );
        setSize( 410, 140 );
        setModal( true );
        setVisible( true );        
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            this.setPreferredSize( new Dimension( 400, 128 ) );
            thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.1 };
            thisLayout.rowHeights = new int[] { 35, 34, 7 };
            thisLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.1 };
            thisLayout.columnWidths = new int[] { 30, 79, 81, 30, 7 };
            this.setLayout( thisLayout );
            {
                jLabel2 = new JLabel();
                this.add( jLabel2, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                           GridBagConstraints.HORIZONTAL, new Insets( 0, 5, 0, 5 ), 0,
                                                           0 ) );
                jLabel2.setText( "x:" );
            }
            {
                SpinnerModel spXModel = new SpinnerNumberModel( 1d, -50000000d, 50000000d, 1d );
                spX = new JSpinner( spXModel );
                this.add( spX, new GridBagConstraints( 1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                       GridBagConstraints.HORIZONTAL, new Insets( 0, 5, 0, 5 ), 0, 0 ) );
            }
            {
                jLabel3 = new JLabel();
                this.add( jLabel3, new GridBagConstraints( 3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                           GridBagConstraints.HORIZONTAL, new Insets( 0, 5, 0, 5 ), 0,
                                                           0 ) );
                jLabel3.setText( "y:" );
            }
            {
                SpinnerModel spYModel = new SpinnerNumberModel( 1d, -50000000d, 50000000d, 1d );
                spY = new JSpinner( spYModel );
                this.add( spY, new GridBagConstraints( 4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                       GridBagConstraints.HORIZONTAL, new Insets( 0, 5, 0, 5 ), 0, 0 ) );
            }
            {
                btPanel = new JPanel();
                FlowLayout btPanelLayout = new FlowLayout();
                btPanelLayout.setAlignment( FlowLayout.LEFT );
                this.add( btPanel, new GridBagConstraints( 0, 2, 4, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                           GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                btPanel.setLayout( btPanelLayout );
                {
                    btOK = new JButton( IconRegistry.getIcon( "accept.png" ) );
                    btPanel.add( btOK );
                    btOK.setText( Messages.getMessage( getLocale(), "$MD11096" ) );
                    btOK.setToolTipText( Messages.getMessage( getLocale(), "$MD11097" ) );
                    btOK.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {

                            double x = ( (Number) spX.getValue() ).doubleValue();
                            double y = ( (Number) spY.getValue() ).doubleValue();
                            GeoTransformer gt;
                            try {
                                CoordinateSystem srcCRS = CRSFactory.create( (String) cbCRS.getSelectedItem() );
                                MapModel mapModel = owner.getApplicationContainer().getMapModel( null );
                                CoordinateSystem crs = mapModel.getCoordinateSystem();
                                if ( !srcCRS.equals( crs ) ) {
                                    gt = new GeoTransformer( crs );
                                    point = GeometryFactory.createPoint( x, y, srcCRS );
                                    point = (Point) gt.transform( point );
                                    x = point.getX();
                                    y = point.getY();
                                }
                                point = GeometryFactory.createPoint( x, y, crs );
                            } catch ( Exception e1 ) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                            PointByCoordinateDialog.this.dispose();
                        }
                    } );
                }
                {
                    if ( parent != null ) {
                        btCancel = new JButton( IconRegistry.getIcon( "cancel.png" ) );
                        btPanel.add( btCancel );
                        btCancel.setText( Messages.getMessage( getLocale(), "$MD11098" ) );
                        btCancel.setToolTipText( Messages.getMessage( getLocale(), "$MD11099" ) );
                        btCancel.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                point = null;
                                PointByCoordinateDialog.this.dispose();
                            }
                        } );
                    }
                }
            }
            {
                if ( !( parent instanceof JDialog ) ) {
                    // just show help if used as panel and not as (modal) window
                    pnHelp = new JPanel();
                    FlowLayout pnHelpLayout = new FlowLayout();
                    pnHelpLayout.setAlignment( FlowLayout.RIGHT );
                    this.add( pnHelp, new GridBagConstraints( 4, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                              GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    pnHelp.setLayout( pnHelpLayout );
                    {
                        btHelp = new JButton( IconRegistry.getIcon( "help.png" ) );
                        btHelp.setText( Messages.getMessage( getLocale(), "$DI10016" ) );
                        btHelp.setToolTipText( Messages.getMessage( getLocale(), "$MD11100" ) );
                        btHelp.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                HelpFrame hf = HelpFrame.getInstance( new HelpManager( owner.getApplicationContainer() ) );
                                hf.setVisible( true );
                                hf.gotoModule( "Digitizer" );
                            }
                        } );
                        pnHelp.add( btHelp );
                    }
                }
                {
                    jLabel1 = new JLabel();
                    getContentPane().add(jLabel1, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 0), 0, 0));
                    jLabel1.setText( Messages.getMessage( getLocale(), "$MD11340" ) );
                }
                {
                    cbCRS = new AutoCompleteComboBox( crsList );
                    getContentPane().add(cbCRS, new GridBagConstraints(3, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
                    cbCRS.addActionListener( new CRSChangedListener() );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void setXY() {
        MapModel mm = owner.getApplicationContainer().getMapModel( null );
        Envelope env = mm.getEnvelope();
        double inc = getIncrementValue( env.getWidth() );
        spX.setValue( new Double( env.getMin().getX() + env.getWidth() / 2 ) );
        ( (SpinnerNumberModel) spX.getModel() ).setStepSize( inc );
        spY.setValue( new Double( env.getMin().getY() + env.getHeight() / 2 ) );
        ( (SpinnerNumberModel) spY.getModel() ).setStepSize( inc );
    }

    /**
     * sets values of both spinner elements
     * 
     * @param x
     * @param y
     */
    void setXY( double x, double y ) {
        spX.setValue( new Double( x ) );
        spY.setValue( new Double( y ) );
    }

    private double getIncrementValue( double value ) {
        double val = 0.001;
        double ex = -4;
        if ( value < Math.pow( 10, ex ) ) {
            return val;
        }
        while ( value > Math.pow( 10, ex ) ) {
            ex++;
        }
        return Math.pow( 10, ex - 3 );
    }

    /**
     * 
     * @return coordinates entered by user as {@link Point}
     */
    public Point getPoint() {
        return point;
    }

    // //////////////////////////////////////////////////////////////////////////////////////////////////
    // inner classes
    // //////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 
     */
    private class CRSChangedListener implements ActionListener {

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed( ActionEvent e ) {

            JComboBox cb = (JComboBox) e.getSource();
            try {
                GeoTransformer gt = new GeoTransformer( (String) cb.getSelectedItem() );
                MapModel mm = owner.getApplicationContainer().getMapModel( null );
                CoordinateSystem crs = mm.getCoordinateSystem();
                double x = ( (Number) spX.getValue() ).doubleValue();
                double y = ( (Number) spY.getValue() ).doubleValue();
                Point point = GeometryFactory.createPoint( x, y, crs );
                point = (Point) gt.transform( point );
                double inc = getIncrementValue( Math.abs( point.getX() ) );
                spX.setValue( new Double( point.getX() ) );
                ( (SpinnerNumberModel) spX.getModel() ).setStepSize( inc );
                spY.setValue( new Double( point.getY() ) );
                ( (SpinnerNumberModel) spY.getModel() ).setStepSize( inc );
            } catch ( Exception e1 ) {
                e1.printStackTrace();
            }

        }

    }

}
