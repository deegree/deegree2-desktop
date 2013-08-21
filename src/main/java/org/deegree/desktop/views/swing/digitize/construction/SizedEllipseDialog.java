//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 and
 lat/lon GmbH

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
package org.deegree.desktop.views.swing.digitize.construction;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.modules.DigitizerModule;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.desktop.views.swing.util.IconRegistry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Surface;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

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
public class SizedEllipseDialog extends JDialog {

    private static final long serialVersionUID = -5544074487201865117L;

    private JPanel pnMessage;

    private JToggleButton tbLock;

    private JSpinner spHeight;

    private JSpinner spWidth;

    private JLabel lbHeight;

    private JLabel lbwidth;

    private JPanel pnForm;

    private JButton btCancel;

    private JButton btOK;

    private JPanel pnButtons;

    private JTextArea taMessage;

    private Surface surface;

    private MapModel mapModel;

    private DigitizerModule<Container> owner;

    private Container parent;

    private Point center;

    private boolean pressed = false;

    /**
     * 
     * @param parent
     * @param owner
     */
    public SizedEllipseDialog( Container parent, DigitizerModule<Container> owner, Point center ) {
        this.parent = parent;
        this.owner = owner;
        this.center = center;
        setTitle( Messages.getMessage( getLocale(), "$MD11114" ) );
        mapModel = owner.getApplicationContainer().getMapModel( null );
        initGUI();
        setLocation( parent.getX() + 150, parent.getY() + 150 );
        setModal( true );
        setVisible( true );
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            thisLayout.rowWeights = new double[] { 0.0, 0.1 };
            thisLayout.rowHeights = new int[] { 156, 7 };
            thisLayout.columnWeights = new double[] { 0.0, 0.1 };
            thisLayout.columnWidths = new int[] { 148, 7 };
            getContentPane().setLayout( thisLayout );
            {
                pnMessage = new JPanel();
                BorderLayout pnMessageLayout = new BorderLayout();
                pnMessage.setLayout( pnMessageLayout );
                getContentPane().add(
                                      pnMessage,
                                      new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                              GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnMessage.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11107" ) ) );
                {
                    taMessage = new JTextArea();
                    pnMessage.add( taMessage, BorderLayout.CENTER );
                    taMessage.setText( Messages.getMessage( getLocale(), "$MD11115" ) );
                    taMessage.setEnabled( false );
                    taMessage.setLineWrap( true );
                    taMessage.setWrapStyleWord( true );
                    taMessage.setEditable( false );
                    taMessage.setDisabledTextColor( new java.awt.Color( 0, 0, 0 ) );
                }
            }
            {
                pnButtons = new JPanel();
                FlowLayout pnButtonsLayout = new FlowLayout();
                pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                pnButtons.setLayout( pnButtonsLayout );
                getContentPane().add(
                                      pnButtons,
                                      new GridBagConstraints( 0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                              GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                {
                    btOK = new JButton( IconRegistry.getIcon( "accept.png" ) );
                    pnButtons.add( btOK );
                    btOK.setText( Messages.getMessage( getLocale(), "$MD11116" ) );
                    btOK.setToolTipText( Messages.getMessage( getLocale(), "$MD11117" ) );
                    btOK.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent event ) {
                            // Point center = mapModel.getEnvelope().getCentroid();
                            try {
                                double radX = ( (Number) spWidth.getValue() ).doubleValue() / 2d;
                                double radY = ( (Number) spHeight.getValue() ).doubleValue() / 2d;
                                surface = GeometryFactory.createSurfaceAsEllipse( center.getX(), center.getY(), radX,
                                                                                  radY, 100,
                                                                                  mapModel.getCoordinateSystem() );
                            } catch ( GeometryException e ) {
                                DialogFactory.openErrorDialog( owner.getApplicationContainer().getViewPlatform(),
                                                               parent, Messages.getMessage( getLocale(), "$MD11118",
                                                                                            e.getMessage() ),
                                                               Messages.getMessage( getLocale(), "$MD11119" ), e );
                            }
                            SizedEllipseDialog.this.dispose();
                        }
                    } );
                }
                {
                    btCancel = new JButton( IconRegistry.getIcon( "cancel.png" ) );
                    pnButtons.add( btCancel );
                    btCancel.setText( Messages.getMessage( getLocale(), "$MD11120" ) );
                    btCancel.setToolTipText( Messages.getMessage( getLocale(), "$MD11121" ) );
                    btCancel.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            SizedEllipseDialog.this.dispose();
                        }

                    } );
                }
            }
            {
                pnForm = new JPanel();
                FormLayout pnFormLayout = new FormLayout( "48dlu, 84dlu", "26dlu, 17dlu, 18dlu" );
                pnForm.setLayout( pnFormLayout );
                getContentPane().add(
                                      pnForm,
                                      new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                              GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnForm.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11122" ) ) );
                {
                    lbwidth = new JLabel();
                    pnForm.add( lbwidth, new CellConstraints( 1, 1, 1, 1, CellConstraints.DEFAULT,
                                                              CellConstraints.DEFAULT, new Insets( 0, 10, 0, 0 ) ) );
                    lbwidth.setText( Messages.getMessage( getLocale(), "$MD11336" ) );
                }
                {
                    lbHeight = new JLabel();
                    pnForm.add( lbHeight, new CellConstraints( 1, 3, 1, 1, CellConstraints.DEFAULT,
                                                               CellConstraints.DEFAULT, new Insets( 0, 10, 0, 0 ) ) );
                    lbHeight.setText( Messages.getMessage( getLocale(), "$MD11337" ) );
                }
                {

                    SpinnerModel spWidthModel = new SpinnerNumberModel( mapModel.getEnvelope().getWidth() / 10d,
                                                                        0.0001, 9E99,
                                                                        mapModel.getEnvelope().getWidth() / 100d );
                    spWidth = new JSpinner( spWidthModel );
                    spWidth.addChangeListener( new ChangeListener() {

                        public void stateChanged( ChangeEvent e ) {
                            if ( pressed ) {
                                spHeight.setValue( spWidth.getValue() );
                            }
                        }
                    } );
                    pnForm.add( spWidth, new CellConstraints( "2, 1, 1, 1, default, default" ) );
                }
                {
                    SpinnerModel spHeightModel = new SpinnerNumberModel( mapModel.getEnvelope().getHeight() / 10d,
                                                                         0.0001, 9E99,
                                                                         mapModel.getEnvelope().getWidth() / 100d );
                    spHeight = new JSpinner( spHeightModel );
                    pnForm.add( spHeight, new CellConstraints( "2, 3, 1, 1, default, default" ) );
                }
                {
                    tbLock = new JToggleButton( IconRegistry.getIcon( "lock_open.png" ) );
                    tbLock.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            pressed = !pressed;
                            if ( pressed ) {
                                spHeight.setValue( spWidth.getValue() );
                                tbLock.setIcon( IconRegistry.getIcon( "lock.png" ) );
                                spHeight.setEnabled( false );
                            } else {
                                tbLock.setIcon( IconRegistry.getIcon( "lock_open.png" ) );
                                spHeight.setEnabled( true );
                            }
                        }
                    } );
                    tbLock.doClick();
                    pnForm.add( tbLock, new CellConstraints( "2, 2, 1, 1, center, default" ) );
                }
            }
            this.setSize( 437, 221 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @return created rectangle as {@link Surface} or <code>null</code> if creation has been canceled
     */
    public Surface getSurface() {
        return surface;
    }

}
