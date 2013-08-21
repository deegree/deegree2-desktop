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
package org.deegree.desktop.views.swing.digitize.options;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.settings.DigitizingVerticesOpt;
import org.deegree.desktop.views.HelpManager;
import org.deegree.desktop.views.swing.HelpFrame;
import org.deegree.desktop.views.swing.util.IconRegistry;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class VerticesOptionsPanel extends JPanel {

    private static final long serialVersionUID = 1227683435877956835L;

    private JPanel verticesPanel1;

    private JSpinner radiusSpinner;

    private JCheckBox nearestVertexCheckBox;

    private JButton helpButton;

    private JCheckBox mergeToNearestVertexCheckBox;

    private JPanel jPanel1;

    private JPanel jPanel3;

    private JPanel pnHelp;

    private JTextArea ta2;

    private JTextArea ta1;

    private ApplicationContainer<Container> appContainer;

    private DigitizingVerticesOpt dvo;

    /**
     * 
     * @param appContainer
     */
    public VerticesOptionsPanel( ApplicationContainer<Container> appContainer ) {
        this.appContainer = appContainer;
        this.dvo = appContainer.getSettings().getDigitizingVerticesOptions();
        initGUI();
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            this.setPreferredSize(new java.awt.Dimension(196, 311));
            thisLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0};
            thisLayout.rowHeights = new int[] {80, 53, 125, 36};
            thisLayout.columnWeights = new double[] { 0.0, 0.1 };
            thisLayout.columnWidths = new int[] { 50, 7 };
            this.setLayout(thisLayout);
            this.setSize( 196, 341 );
            {
                verticesPanel1 = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
                this.add(verticesPanel1, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                verticesPanel1.setPreferredSize( new Dimension( 157, 61 ) );
                verticesPanel1.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                 "$MD10334" ) ) );
                {
                    radiusSpinner = new JSpinner();
                    radiusSpinner.addChangeListener( new RadiusSpinnerListener() );
                    verticesPanel1.add( radiusSpinner );
                    SpinnerNumberModel model = new SpinnerNumberModel( 1, 1, 100, 1 );
                    radiusSpinner.setModel( model );
                    radiusSpinner.setValue( dvo.getSearchRadiusValue() );
                    radiusSpinner.setPreferredSize( new java.awt.Dimension( 138, 24 ) );
                    radiusSpinner.setEnabled( dvo.isChangeable() );
                }
            }
            {
                pnHelp = new JPanel();
                BorderLayout pnHelpLayout = new BorderLayout();
                pnHelp.setLayout( pnHelpLayout );
                this.add( pnHelp, new GridBagConstraints( 1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                          GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                {
                    helpButton = new JButton( IconRegistry.getIcon( "help.png" ) );
                    pnHelp.add( helpButton, BorderLayout.SOUTH );
                    helpButton.setText( Messages.getMessage( getLocale(), "$MD10338" ) );
                    helpButton.addActionListener( new ActionListener() {

                        /*
                         * (non-Javadoc)
                         * 
                         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
                         */
                        public void actionPerformed( ActionEvent e ) {
                            HelpFrame hf = HelpFrame.getInstance( new HelpManager( appContainer ) );
                            hf.setVisible( true );
                            hf.gotoKeyword( "digitize:Digitizer" );
                        }

                    } );
                }
            }
            {
                jPanel1 = new JPanel();
                FlowLayout jPanel1Layout = new FlowLayout();
                jPanel1Layout.setAlignment( FlowLayout.LEFT );
                jPanel1.setLayout( jPanel1Layout );
                this.add( jPanel1, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                           GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                {
                    nearestVertexCheckBox = new JCheckBox();
                    jPanel1.add( nearestVertexCheckBox );
                    nearestVertexCheckBox.setSelected( dvo.handleNearest() );
                    nearestVertexCheckBox.addActionListener( new VertexActionListener() );
                    nearestVertexCheckBox.setActionCommand( "nearestVertexCheckBox" );
                    nearestVertexCheckBox.setText( "" );
                    nearestVertexCheckBox.setEnabled( dvo.isChangeable() );
                }
            }
            {
                jPanel3 = new JPanel();
                FlowLayout jPanel3Layout = new FlowLayout();
                jPanel3Layout.setAlignment( FlowLayout.LEFT );
                jPanel3.setLayout( jPanel3Layout );
                this.add( jPanel3, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                           GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                {
                    mergeToNearestVertexCheckBox = new JCheckBox();
                    jPanel3.add( mergeToNearestVertexCheckBox );
                    mergeToNearestVertexCheckBox.setSelected( dvo.useNearest() );
                    mergeToNearestVertexCheckBox.setActionCommand( "mergeToNearestVertexCheckBox" );
                    mergeToNearestVertexCheckBox.setText( "" );
                    mergeToNearestVertexCheckBox.setPreferredSize( new java.awt.Dimension( 74, 19 ) );
                    mergeToNearestVertexCheckBox.setEnabled( dvo.isChangeable() );
                }
            }
            {
                ta1 = new JTextArea();
                this.add( ta1, new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                       GridBagConstraints.BOTH, new Insets( 5, 5, 5, 5 ), 0, 0 ) );
                ta1.setText( Messages.getMessage( getLocale(), "$MD10336" ) );
                ta1.setBackground( jPanel3.getBackground() );
                ta1.setLineWrap( true );
                ta1.setWrapStyleWord( true );
                ta1.setEditable( false );
            }
            {
                ta2 = new JTextArea();
                this.add( ta2, new GridBagConstraints( 1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                       GridBagConstraints.BOTH, new Insets( 5, 5, 5, 5 ), 0, 0 ) );
                ta2.setText( Messages.getMessage( getLocale(), "$MD10337" ) );
                ta2.setBackground( jPanel3.getBackground() );
                ta2.setLineWrap( true );
                ta2.setWrapStyleWord( true );
                ta2.setEditable( false );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return Messages.getMessage( getLocale(), "$MD10332" );
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // inner classes
    // /////////////////////////////////////////////////////////////////////////////////

    private class RadiusSpinnerListener implements ChangeListener {

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
         */
        public void stateChanged( ChangeEvent e ) {
            float value = Float.parseFloat( ( (JSpinner) e.getSource() ).getValue().toString() );
            if ( dvo.isChangeable() ) {
                dvo.setSearchRadiusValue( value );
            }
        }

    }

    private class VertexActionListener implements ActionListener {

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed( ActionEvent event ) {
            String actionCommand = event.getActionCommand();
            if ( dvo.isChangeable() ) {
                if ( "nearestVertexCheckBox".equals( actionCommand ) ) {
                    JCheckBox cb = (JCheckBox) event.getSource();
                    dvo.setUseNearest( cb.isSelected() );
                } else if ( "mergeToNearestVertexCheckBox".equals( actionCommand ) ) {
                    JCheckBox cb = (JCheckBox) event.getSource();
                    dvo.setUseNearest( cb.isSelected() );
                }
            }

        }

    }

}
