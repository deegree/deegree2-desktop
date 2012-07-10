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
package org.deegree.igeo.views.swing.digitize.options;

import java.awt.BorderLayout;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.settings.DigitizingLinesOpt;
import org.deegree.igeo.views.HelpManager;
import org.deegree.igeo.views.swing.HelpFrame;
import org.deegree.igeo.views.swing.util.IconRegistry;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class LineOptionsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    private JPanel pnLineWidth;

    private JRadioButton rbGeomLineWidth;

    private JSpinner spGeomLineWidth;

    private JPanel pnGeomLineWidth;

    private JPanel pnHelp;

    private JRadioButton rbPixelLineWidth;

    private JPanel pnPixelLineWith;

    private JSpinner spPixelLineWidth;

    private JButton btHelp;

    private ButtonGroup buttonGroup = new ButtonGroup();

    private ApplicationContainer<Container> appContainer;

    private DigitizingLinesOpt dlo;

    /**
     * 
     * @param appContainer
     */
    public LineOptionsPanel( ApplicationContainer<Container> appContainer ) {
        this.appContainer = appContainer;
        this.dlo = appContainer.getSettings().getDigitizingLinesOptions();
        initGUI();
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            this.setPreferredSize( new Dimension( 150, 341 ) );
            thisLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.1};
            thisLayout.rowHeights = new int[] {60, 145, 53, 20};
            thisLayout.columnWeights = new double[] { 0.0, 0.1 };
            thisLayout.columnWidths = new int[] { 75, 7 };
            this.setLayout(thisLayout);
            this.setSize( 196, 341 );
            {
                pnLineWidth = new JPanel();
                GridBagLayout lineWidthPanelLayout = new GridBagLayout();
                this.add( pnLineWidth, new GridBagConstraints( 0, 0, 2, 2, 0.0, 0.0, GridBagConstraints.NORTH,
                                                               GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnLineWidth.setPreferredSize( new Dimension( 63, 47 ) );
                pnLineWidth.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD10339" ) ) );
                lineWidthPanelLayout.rowWeights = new double[] { 0.0, 0.1 };
                lineWidthPanelLayout.rowHeights = new int[] { 92, 7 };
                lineWidthPanelLayout.columnWeights = new double[] { 0.1 };
                lineWidthPanelLayout.columnWidths = new int[] { 7 };
                pnLineWidth.setLayout( lineWidthPanelLayout );
                {
                    pnPixelLineWith = new JPanel();
                    GridBagLayout pixelLineWithLayout = new GridBagLayout();
                    pnLineWidth.add( pnPixelLineWith, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                              GridBagConstraints.CENTER,
                                                                              GridBagConstraints.BOTH,
                                                                              new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    pnPixelLineWith.setPreferredSize( new Dimension( 42, 29 ) );
                    pnPixelLineWith.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                      "$MD10340" ) ) );
                    pixelLineWithLayout.rowWeights = new double[] { 0.0, 0.1 };
                    pixelLineWithLayout.rowHeights = new int[] { 28, 7 };
                    pixelLineWithLayout.columnWeights = new double[] { 0.1, 0.0 };
                    pixelLineWithLayout.columnWidths = new int[] { 7, -1 };
                    pnPixelLineWith.setLayout( pixelLineWithLayout );
                    {
                        spPixelLineWidth = new JSpinner( new SpinnerNumberModel( 2, 1, 100, 1 ) );
                        spPixelLineWidth.addChangeListener( new PixelSpinnerChangeListener() );
                        pnPixelLineWith.add( spPixelLineWidth, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                                                                       GridBagConstraints.CENTER,
                                                                                       GridBagConstraints.HORIZONTAL,
                                                                                       new Insets( 5, 5, 5, 5 ), 0, 0 ) );
                        int width = (int) dlo.getValue();
                        spPixelLineWidth.setValue( width );
                        spPixelLineWidth.setPreferredSize( new Dimension( 101, 21 ) );
                        spPixelLineWidth.setEnabled( dlo.isChangeable() );
                        spPixelLineWidth.getEditor().setPreferredSize( new Dimension( 83, 17 ) );
                    }
                    {
                        rbPixelLineWidth = new JRadioButton();
                        rbPixelLineWidth.setActionCommand( "pixelLineWidthRB" );
                        rbPixelLineWidth.addActionListener( new RBActionListener() );
                        String s = dlo.getUOM();
                        rbPixelLineWidth.setSelected( "pixel".equals( s ) );
                        buttonGroup.add( rbPixelLineWidth );
                        pnPixelLineWith.add( rbPixelLineWidth, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                                       GridBagConstraints.CENTER,
                                                                                       GridBagConstraints.BOTH,
                                                                                       new Insets( 2, 5, 2, 5 ), 0, 0 ) );
                        rbPixelLineWidth.setText( Messages.getMessage( getLocale(), "$MD10341" ) );
                        rbPixelLineWidth.setEnabled( dlo.isChangeable() );
                    }
                }
                {
                    pnGeomLineWidth = new JPanel();
                    GridBagLayout geomLineWidthPanelLayout = new GridBagLayout();
                    pnLineWidth.add( pnGeomLineWidth, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                                                              GridBagConstraints.CENTER,
                                                                              GridBagConstraints.BOTH,
                                                                              new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    pnGeomLineWidth.setPreferredSize( new Dimension( 33, 24 ) );
                    pnGeomLineWidth.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                      "$MD10342" ) ) );
                    geomLineWidthPanelLayout.rowWeights = new double[] { 0.0, 0.1 };
                    geomLineWidthPanelLayout.rowHeights = new int[] { 28, 7 };
                    geomLineWidthPanelLayout.columnWeights = new double[] { 0.1 };
                    geomLineWidthPanelLayout.columnWidths = new int[] { 7 };
                    pnGeomLineWidth.setLayout( geomLineWidthPanelLayout );
                    {
                        rbGeomLineWidth = new JRadioButton();
                        rbGeomLineWidth.setActionCommand( "geomLineWidthRB" );
                        rbGeomLineWidth.addActionListener( new RBActionListener() );
                        String s = dlo.getUOM();
                        rbGeomLineWidth.setSelected( "geom".equals( s ) );
                        buttonGroup.add( rbGeomLineWidth );
                        pnGeomLineWidth.add( rbGeomLineWidth, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                                      GridBagConstraints.CENTER,
                                                                                      GridBagConstraints.BOTH,
                                                                                      new Insets( 2, 5, 2, 5 ), 0, 0 ) );
                        rbGeomLineWidth.setText( Messages.getMessage( getLocale(), "$MD10343" ) );
                        rbGeomLineWidth.setEnabled( dlo.isChangeable() );
                    }
                    {
                        spGeomLineWidth = new JSpinner( new SpinnerNumberModel( 2d, 0.01d, 1000000d, 0.25 ) );
                        spGeomLineWidth.addChangeListener( new GeomSpinnerChangeListener() );
                        float width = dlo.getValue();
                        spGeomLineWidth.setValue( width );
                        spGeomLineWidth.setEnabled( dlo.isChangeable() );
                        pnGeomLineWidth.add( spGeomLineWidth, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                                                                      GridBagConstraints.CENTER,
                                                                                      GridBagConstraints.HORIZONTAL,
                                                                                      new Insets( 5, 5, 5, 5 ), 0, 0 ) );
                    }
                }
            }
            {
                pnHelp = new JPanel();
                BorderLayout pnHelpLayout = new BorderLayout();
                pnHelp.setLayout( pnHelpLayout );
                this.add( pnHelp, new GridBagConstraints( 1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                          GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                {
                    btHelp = new JButton( IconRegistry.getIcon( "help.png" ) );
                    pnHelp.add( btHelp, BorderLayout.SOUTH );
                    btHelp.setText( Messages.getMessage( getLocale(), "$MD10344" ) );
                    btHelp.addActionListener( new ActionListener() {

                        /*
                         * (non-Javadoc)
                         * 
                         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
                         */
                        public void actionPerformed( ActionEvent e ) {
                            HelpFrame hf = HelpFrame.getInstance( new HelpManager( appContainer ) );
                            hf.setVisible( true );
                            hf.gotoModule( "Digitizer" );
                        }

                    } );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return Messages.getMessage( getLocale(), "$MD10333" );
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // inner classes
    // ///////////////////////////////////////////////////////////////////////////////

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    private class RBActionListener implements ActionListener {

        /*
         * (non-Javadoc)
         * 
         * @see event.ActionListener#actionPerformed(event.ActionEvent)
         */
        public void actionPerformed( ActionEvent e ) {
            if ( dlo.isChangeable() ) {
                String actionCommand = e.getActionCommand();
                if ( "pixelLineWidthRB".equals( actionCommand ) ) {
                    dlo.setUOM( "pixel" );
                    dlo.setValue( ( (Number) spPixelLineWidth.getValue() ).floatValue() );
                } else if ( "geomLineWidthRB".equals( actionCommand ) ) {
                    dlo.setUOM( "geom" );
                    dlo.setValue( ( (Number) spGeomLineWidth.getValue() ).floatValue() );
                }
            } else {
                spGeomLineWidth.setValue( dlo.getValue() );
                spPixelLineWidth.setValue( dlo.getValue() );
            }
        }

    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    private class GeomSpinnerChangeListener implements ChangeListener {

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
         */
        public void stateChanged( ChangeEvent e ) {
            String value = ( (JSpinner) e.getSource() ).getValue().toString();
            if ( dlo.isChangeable() ) {
                dlo.setValue( Float.valueOf( value ) );
            } else {
                ( (JSpinner) e.getSource() ).setValue( dlo.getValue() );
            }
        }

    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    private class PixelSpinnerChangeListener implements ChangeListener {

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
         */
        public void stateChanged( ChangeEvent e ) {
            Integer value = (Integer) ( (JSpinner) e.getSource() ).getValue();
            if ( dlo.isChangeable() ) {
                dlo.setValue( value );
            } else {
                ( (JSpinner) e.getSource() ).setValue( (int) dlo.getValue() );
            }
        }

    }

}
