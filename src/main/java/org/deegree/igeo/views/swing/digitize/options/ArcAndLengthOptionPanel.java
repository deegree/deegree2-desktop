//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
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
package org.deegree.igeo.views.swing.digitize.options;

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
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.modules.DigitizerModule;
import org.deegree.igeo.views.HelpManager;
import org.deegree.igeo.views.swing.HelpFrame;
import org.deegree.igeo.views.swing.util.IconRegistry;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ArcAndLengthOptionPanel extends JPanel {

    private static final long serialVersionUID = -8352238339390513392L;

    private ApplicationContainer<Container> appContainer;

    private JPanel pnDescription;

    private JPanel pnParam;

    private JButton btReset;

    private JSpinner spLength;

    private JLabel lbLength;

    private JCheckBox cbLength;

    private JSpinner spAngle;

    private JLabel lbAngle;

    private JButton btHelp;

    private JButton btTake;

    private JTextArea taDescription;

    private JPanel pnHelp;

    private JPanel pnButtons;

    /**
     * 
     * @param appContainer
     */
    public ArcAndLengthOptionPanel( ApplicationContainer<Container> appContainer ) {
        this.appContainer = appContainer;
        initGUI();
    }

    private void initGUI() {
        try {
            {
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] { 0.0, 0.1, 0.1 };
                thisLayout.rowHeights = new int[] { 190, 20, 7 };
                thisLayout.columnWeights = new double[] { 0.0, 0.0, 0.1 };
                thisLayout.columnWidths = new int[] { 163, 97, 20 };
                this.setLayout( thisLayout );
                {
                    pnDescription = new JPanel();
                    BorderLayout pnDescriptionLayout = new BorderLayout();
                    pnDescription.setLayout( pnDescriptionLayout );
                    this.add( pnDescription, new GridBagConstraints( 0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                     GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ),
                                                                     0, 0 ) );
                    pnDescription.setBorder( BorderFactory.createTitledBorder( null, Messages.getMessage( getLocale(),
                                                                                                          "$MD11283" ),
                                                                               TitledBorder.LEADING,
                                                                               TitledBorder.DEFAULT_POSITION ) );
                    {
                        taDescription = new JTextArea();
                        taDescription.setText( Messages.getMessage( getLocale(), "$MD11284" ) );
                        taDescription.setWrapStyleWord( true );
                        taDescription.setLineWrap( true );
                        taDescription.setBackground( pnDescription.getBackground() );
                        taDescription.setEditable( false );
                        pnDescription.add( taDescription, BorderLayout.CENTER );
                    }
                }
                {
                    pnButtons = new JPanel();
                    FlowLayout pnButtonsLayout = new FlowLayout();
                    pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                    pnButtons.setLayout( pnButtonsLayout );
                    this.add( pnButtons, new GridBagConstraints( 0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                 GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                 0 ) );
                    {
                        btTake = new JButton( Messages.getMessage( getLocale(), "$MD11275" ) );
                        btTake.setToolTipText( Messages.getMessage( getLocale(), "$MD11276" ) );
                        btTake.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                double value = ( (Number) spAngle.getValue() ).doubleValue() +180;
                                appContainer.setInstanceSetting( DigitizerModule.ANGLE, value );
                                if ( cbLength.isSelected() ) {
                                    value = ( (Number) spLength.getValue() ).doubleValue();
                                } else {
                                    value = -1;
                                }
                                appContainer.setInstanceSetting( DigitizerModule.LENGTH, value );
                            }
                        } );
                        pnButtons.add( btTake );
                    }
                    {
                        btReset = new JButton( Messages.getMessage( getLocale(), "$MD11780" )  );
                        pnButtons.add( btReset );
                        btReset.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent arg0 ) {
                                appContainer.setInstanceSetting( DigitizerModule.ANGLE, -1 );
                                appContainer.setInstanceSetting( DigitizerModule.LENGTH, -1 );
                            }
                        } );
                    }
                }
                {
                    pnParam = new JPanel();
                    GridBagLayout pnParamLayout = new GridBagLayout();
                    pnParamLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.1 };
                    pnParamLayout.rowHeights = new int[] { 44, 41, 43, 7 };
                    pnParamLayout.columnWeights = new double[] { 0.0, 0.1 };
                    pnParamLayout.columnWidths = new int[] { 106, 7 };
                    pnParam.setLayout( pnParamLayout );
                    this.add( pnParam, new GridBagConstraints( 0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                               GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    pnParam.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11285" ) ) );
                    {
                        lbAngle = new JLabel( Messages.getMessage( getLocale(), "$MD11279" ) );
                        pnParam.add( lbAngle, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                      GridBagConstraints.HORIZONTAL,
                                                                      new Insets( 0, 10, 0, 0 ), 0, 0 ) );
                    }
                    {
                        spAngle = new JSpinner( new SpinnerNumberModel( 0, -180, 180, 0.1 ) );
                        pnParam.add( spAngle, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                      GridBagConstraints.HORIZONTAL,
                                                                      new Insets( 0, 0, 0, 10 ), 0, 0 ) );
                    }
                    {
                        cbLength = new JCheckBox( Messages.getMessage( getLocale(), "$MD11280" ) );
                        pnParam.add( cbLength, new GridBagConstraints( 0, 1, 2, 1, 0.0, 0.0,
                                                                       GridBagConstraints.SOUTHEAST,
                                                                       GridBagConstraints.HORIZONTAL,
                                                                       new Insets( 0, 10, 0, 0 ), 0, 0 ) );
                    }
                    {
                        lbLength = new JLabel();
                        pnParam.add( lbLength, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                       GridBagConstraints.HORIZONTAL,
                                                                       new Insets( 0, 10, 0, 0 ), 0, 0 ) );
                        lbLength.setText( Messages.getMessage( getLocale(), "$MD11281" ) );
                    }
                    {
                        spLength = new JSpinner( new SpinnerNumberModel( 10, 0, 9E99, 0.1 ) );
                        pnParam.add( spLength, new GridBagConstraints( 1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                       GridBagConstraints.HORIZONTAL,
                                                                       new Insets( 0, 0, 0, 10 ), 0, 0 ) );
                    }
                }
                {
                    pnHelp = new JPanel();
                    FlowLayout pnHelpLayout = new FlowLayout();
                    pnHelpLayout.setAlignment( FlowLayout.RIGHT );
                    pnHelp.setLayout( pnHelpLayout );
                    this.add( pnHelp, new GridBagConstraints( 2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                              GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    {
                        btHelp = new JButton( Messages.getMessage( getLocale(), "$MD11282" ),
                                              IconRegistry.getIcon( "help.png" ) );
                        btHelp.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent event ) {
                                HelpFrame hf = HelpFrame.getInstance( new HelpManager( appContainer ) );
                                hf.setVisible( true );
                                hf.toFront();
                                hf.gotoModule( "Digitizer" );
                            }
                        } );
                        pnHelp.add( btHelp );
                    }
                }
            }
            this.setSize( 400, 257 );
            this.setPreferredSize( new java.awt.Dimension( 400, 366 ) );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return "Arc and Length";
    }
}
