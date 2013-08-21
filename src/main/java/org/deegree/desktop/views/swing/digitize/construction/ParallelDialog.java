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
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.modules.DigitizerModule;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.desktop.views.HelpManager;
import org.deegree.desktop.views.swing.HelpFrame;
import org.deegree.desktop.views.swing.util.IconRegistry;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ParallelDialog extends JDialog {

    private static final long serialVersionUID = 7872135100337069700L;

    private JPanel pnDescription;

    private JPanel pnHelp;

    private JButton btHelp;

    private JCheckBox cbLeft;

    private JSpinner spDistance;

    private JLabel lbDistance;

    private JCheckBox cbRight;

    private JPanel pnForm;

    private JButton btCancel;

    private JButton btOK;

    private JPanel pnButtons;

    private JTextArea taDescription;

    private MapModel mapModel;

    private Container parent;

    private DigitizerModule<Container> owner;

    /**
     * 
     * @param parent
     * @param owner
     */
    public ParallelDialog( Container parent, DigitizerModule<Container> owner ) {
        this.parent = parent;
        this.owner = owner;
        setTitle( Messages.getMessage( getLocale(), "$MD11175" ) );
        mapModel = owner.getApplicationContainer().getMapModel( null );
        initGUI();
        setLocation( parent.getX() + 150, parent.getY() + 150 );
        setAlwaysOnTop( true );
        setVisible( true );
    }

    private void initGUI() {
        try {
            {
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] { 0.0, 0.1 };
                thisLayout.rowHeights = new int[] { 196, 7 };
                thisLayout.columnWeights = new double[] { 0.0, 0.1 };

                thisLayout.columnWidths = new int[] { 192, 7 };
                getContentPane().setLayout( thisLayout );
                {
                    pnDescription = new JPanel();
                    BorderLayout pnDescriptionLayout = new BorderLayout();
                    getContentPane().add(
                                          pnDescription,
                                          new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnDescription.setLayout( pnDescriptionLayout );
                    pnDescription.setBorder( BorderFactory.createTitledBorder( "create parallel" ) );
                    {
                        taDescription = new JTextArea( Messages.getMessage( getLocale(), "$MD11176" ) );
                        taDescription.setBackground( pnDescription.getBackground() );
                        taDescription.setEditable( false );
                        taDescription.setLineWrap( true );
                        taDescription.setWrapStyleWord( true );
                        pnDescription.add( taDescription, BorderLayout.CENTER );
                        taDescription.setPreferredSize( new java.awt.Dimension( 182, 90 ) );
                    }
                }
                {
                    pnButtons = new JPanel();
                    FlowLayout pnButtonsLayout = new FlowLayout();
                    pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                    getContentPane().add(
                                          pnButtons,
                                          new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnButtons.setLayout( pnButtonsLayout );
                    {
                        btOK = new JButton( Messages.getMessage( getLocale(), "$MD11177" ),
                                            IconRegistry.getIcon( "accept.png" ) );
                        btOK.setToolTipText( Messages.getMessage( getLocale(), "$MD11178" ) );
                        pnButtons.add( btOK );
                        btOK.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent event ) {
                                if ( !cbLeft.isSelected() && !cbRight.isSelected() ) {
                                    DialogFactory.openErrorDialog( "application", parent,
                                                                   Messages.getMessage( getLocale(), "$MD11187" ),
                                                                   Messages.getMessage( getLocale(), "$MD11188" ) );
                                } else {
                                    Map<String, Object> parameter = new HashMap<String, Object>();
                                    parameter.put( "distance", spDistance.getValue() );
                                    parameter.put( "left", cbLeft.isSelected() );
                                    parameter.put( "right", cbRight.isSelected() );
                                    owner.performDigitizingAction( "createParallel", parameter );
                                    ParallelDialog.this.dispose();
                                }
                            }
                        } );
                    }
                    {
                        btCancel = new JButton( Messages.getMessage( getLocale(), "$MD11179" ),
                                                IconRegistry.getIcon( "cancel.png" ) );
                        btCancel.setToolTipText( Messages.getMessage( getLocale(), "$MD11180" ) );
                        pnButtons.add( btCancel );
                        btCancel.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent event ) {
                                ParallelDialog.this.dispose();
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
                                          new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    {
                        btHelp = new JButton( Messages.getMessage( getLocale(), "$MD11181" ),
                                              IconRegistry.getIcon( "help.png" ) );
                        btHelp.setToolTipText( Messages.getMessage( getLocale(), "$MD11182" ) );
                        pnHelp.add( btHelp );
                        btHelp.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                HelpFrame hf = HelpFrame.getInstance( new HelpManager( owner.getApplicationContainer() ) );
                                hf.setVisible( true );
                                hf.gotoModule( "Digitizer" );
                            }
                        } );

                    }
                }
                {
                    pnForm = new JPanel();
                    GridBagLayout pnFormLayout = new GridBagLayout();
                    getContentPane().add(
                                          pnForm,
                                          new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnFormLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.1 };
                    pnFormLayout.rowHeights = new int[] { 41, 40, 38, 40, 20 };
                    pnFormLayout.columnWeights = new double[] { 0.0, 0.1 };
                    pnFormLayout.columnWidths = new int[] { 142, 7 };
                    pnForm.setLayout( pnFormLayout );
                    {
                        cbLeft = new JCheckBox();
                        pnForm.add( cbLeft, new GridBagConstraints( 0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                    GridBagConstraints.HORIZONTAL, new Insets( 0, 10,
                                                                                                               0, 10 ),
                                                                    0, 0 ) );
                        cbLeft.setText( Messages.getMessage( getLocale(), "$MD11183" ) );
                    }
                    {
                        cbRight = new JCheckBox();
                        pnForm.add( cbRight, new GridBagConstraints( 0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                     GridBagConstraints.HORIZONTAL,
                                                                     new Insets( 0, 10, 0, 10 ), 0, 0 ) );
                        cbRight.setText( Messages.getMessage( getLocale(), "$MD11184" ) );
                    }
                    {
                        lbDistance = new JLabel();
                        pnForm.add( lbDistance, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0,
                                                                        GridBagConstraints.CENTER,
                                                                        GridBagConstraints.HORIZONTAL,
                                                                        new Insets( 0, 10, 0, 10 ), 0, 0 ) );
                        lbDistance.setText( Messages.getMessage( getLocale(), "$MD11185" ) );
                    }
                    {
                        double d = mapModel.getEnvelope().getWidth() / 100d;
                        if ( d >= 1 ) {
                            d = (int) d;
                        } else {
                            d = 0.01;
                        }

                        spDistance = new JSpinner( new SpinnerNumberModel( 1, 0.0001, 9E99, d ) );
                        pnForm.add( spDistance, new GridBagConstraints( 1, 2, 1, 1, 0.0, 0.0,
                                                                        GridBagConstraints.CENTER,
                                                                        GridBagConstraints.HORIZONTAL,
                                                                        new Insets( 0, 10, 0, 10 ), 0, 0 ) );
                    }
                }
            }
            this.setSize( 452, 261 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
