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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
class BufferExpertOptionsDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = -7448749281360481618L;

    private JCheckBox cbUseProperty;

    private JComboBox cbProperty;

    private JButton btCancel;

    private JButton btOK;

    private JPanel pnButtons;

    private int multibleBuffer = -1;

    private JRadioButton rbWithHoles;

    private JRadioButton rbOverlayed;

    private ButtonGroup bgBufferCombination;

    private JSpinner spMultipleBuffer;

    private JCheckBox cbMultibleBuffer;

    private JPanel pnMultipleBuffer;

    private FeatureType featureType;

    private QualifiedName selectedProperty;

    private JLabel lbNoOfBuffers;

    // public BufferExpertOptionsDialog() {
    // initGUI();
    // }

    /**
     * 
     * @param multibleBuffer
     * @param featureType
     * @param selectedProperty
     */
    BufferExpertOptionsDialog( int multibleBuffer, FeatureType featureType, QualifiedName selectedProperty ) {
        this.multibleBuffer = multibleBuffer;
        this.featureType = featureType;
        this.selectedProperty = selectedProperty;
        initGUI();
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        setLocation( screenWidth / 2 - getWidth() / 2, screenHeight / 2 - getHeight() / 2 );
        setModal( true );
        setVisible( true );
        toFront();

    }

    private void initGUI() {
        try {
            {
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.1 };
                thisLayout.rowHeights = new int[] { 40, 158, 7 };
                thisLayout.columnWeights = new double[] { 0.0, 0.1 };
                thisLayout.columnWidths = new int[] { 190, 7 };
                getContentPane().setLayout( thisLayout );
                {
                    cbUseProperty = new JCheckBox( Messages.getMessage( getLocale(), "$MD11485" ) );
                    cbUseProperty.setSelected( selectedProperty != null );
                    cbUseProperty.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            cbProperty.setEnabled( cbUseProperty.isSelected() );
                        }

                    } );
                    getContentPane().add(
                                          cbUseProperty,
                                          new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL,
                                                                  new Insets( 0, 9, 0, 0 ), 0, 0 ) );

                }
                {
                    PropertyType[] pt = featureType.getProperties();
                    List<QualifiedName> list = new ArrayList<QualifiedName>();
                    for ( PropertyType propertyType : pt ) {
                        if ( propertyType.getType() == Types.BIGINT || propertyType.getType() == Types.DECIMAL
                             || propertyType.getType() == Types.DOUBLE || propertyType.getType() == Types.FLOAT
                             || propertyType.getType() == Types.INTEGER || propertyType.getType() == Types.NUMERIC
                             || propertyType.getType() == Types.REAL || propertyType.getType() == Types.SMALLINT
                             || propertyType.getType() == Types.TINYINT ) {
                            list.add( propertyType.getName() );
                        }
                    }

                    cbProperty = new JComboBox(
                                                new DefaultComboBoxModel( list.toArray( new QualifiedName[list.size()] ) ) );
                    cbProperty.setEnabled( selectedProperty != null );
                    if ( selectedProperty != null ) {
                        cbProperty.setSelectedItem( selectedProperty );
                    }
                    getContentPane().add(
                                          cbProperty,
                                          new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL,
                                                                  new Insets( 0, 0, 0, 9 ), 0, 0 ) );
                }
                {
                    pnButtons = new JPanel();
                    FlowLayout pnButtonsLayout = new FlowLayout();
                    pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                    pnButtons.setLayout( pnButtonsLayout );
                    getContentPane().add(
                                          pnButtons,
                                          new GridBagConstraints( 0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    {
                        btOK = new JButton( Messages.getMessage( getLocale(), "$MD11483" ),
                                            IconRegistry.getIcon( "accept.png" ) );
                        pnButtons.add( btOK );
                        btOK.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                dispose();
                            }
                        } );
                    }
                    {
                        btCancel = new JButton( Messages.getMessage( getLocale(), "$MD11484" ),
                                                IconRegistry.getIcon( "cancel.png" ) );
                        pnButtons.add( btCancel );
                        btCancel.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                multibleBuffer = -1;
                                dispose();
                            }
                        } );
                    }
                }
                {
                    pnMultipleBuffer = new JPanel();
                    GridBagLayout jPanel1Layout = new GridBagLayout();
                    getContentPane().add(
                                          pnMultipleBuffer,
                                          new GridBagConstraints( 0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    jPanel1Layout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.1 };
                    jPanel1Layout.rowHeights = new int[] { 29, 31, 39, 7 };
                    jPanel1Layout.columnWeights = new double[] { 0.0, 0.1 };
                    jPanel1Layout.columnWidths = new int[] { 187, 7 };
                    pnMultipleBuffer.setLayout( jPanel1Layout );
                    pnMultipleBuffer.setBorder( BorderFactory.createTitledBorder( null,
                                                                                  Messages.getMessage( getLocale(),
                                                                                                       "$MD11486" ),
                                                                                  TitledBorder.LEADING,
                                                                                  TitledBorder.DEFAULT_POSITION ) );
                    {
                        cbMultibleBuffer = new JCheckBox( Messages.getMessage( getLocale(), "$MD11492" ) );
                        pnMultipleBuffer.add( cbMultibleBuffer, new GridBagConstraints( 0, 0, 2, 1, 0.0, 0.0,
                                                                                        GridBagConstraints.CENTER,
                                                                                        GridBagConstraints.HORIZONTAL,
                                                                                        new Insets( 0, 9, 0, 0 ), 0, 0 ) );
                        cbMultibleBuffer.setSelected( multibleBuffer > 1 );
                        cbMultibleBuffer.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                spMultipleBuffer.setEnabled( cbMultibleBuffer.isSelected() );
                                rbOverlayed.setEnabled( cbMultibleBuffer.isSelected() );
                                rbWithHoles.setEnabled( cbMultibleBuffer.isSelected() );
                            }

                        } );
                    }
                    {
                        spMultipleBuffer = new JSpinner( new SpinnerNumberModel( 2, 2, 20, 1 ) );
                        pnMultipleBuffer.add( spMultipleBuffer, new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0,
                                                                                        GridBagConstraints.CENTER,
                                                                                        GridBagConstraints.HORIZONTAL,
                                                                                        new Insets( 0, 0, 0, 9 ), 0, 0 ) );
                        spMultipleBuffer.setEnabled( cbMultibleBuffer.isSelected() );
                    }
                    {
                        lbNoOfBuffers = new JLabel( Messages.getMessage( getLocale(), "$MD11487" ) );
                        pnMultipleBuffer.add( lbNoOfBuffers, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                                                                     GridBagConstraints.CENTER,
                                                                                     GridBagConstraints.HORIZONTAL,
                                                                                     new Insets( 0, 9, 0, 0 ), 0, 0 ) );
                        lbNoOfBuffers.setEnabled( cbMultibleBuffer.isSelected() );
                    }
                    {
                        rbOverlayed = new JRadioButton( Messages.getMessage( getLocale(), "$MD11488" ) );
                        pnMultipleBuffer.add( rbOverlayed, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0,
                                                                                   GridBagConstraints.CENTER,
                                                                                   GridBagConstraints.HORIZONTAL,
                                                                                   new Insets( 0, 9, 0, 0 ), 0, 0 ) );
                        getBgBufferCombination().add( rbOverlayed );
                        rbOverlayed.setSelected( true );
                        rbOverlayed.setEnabled( cbMultibleBuffer.isSelected() );
                    }
                    {
                        rbWithHoles = new JRadioButton( Messages.getMessage( getLocale(), "$MD11489" ) );
                        pnMultipleBuffer.add( rbWithHoles, new GridBagConstraints( 0, 3, 1, 1, 0.0, 0.0,
                                                                                   GridBagConstraints.CENTER,
                                                                                   GridBagConstraints.HORIZONTAL,
                                                                                   new Insets( 0, 9, 0, 0 ), 0, 0 ) );
                        rbWithHoles.setEnabled( cbMultibleBuffer.isSelected() );
                        getBgBufferCombination().add( rbWithHoles );
                    }
                }
            }
            this.setSize( 420, 264 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private ButtonGroup getBgBufferCombination() {
        if ( bgBufferCombination == null ) {
            bgBufferCombination = new ButtonGroup();
        }
        return bgBufferCombination;
    }

    /**
     * 
     * @return number of buffers to create or -1 of multible buffers has not been selected
     */
    int getNumberOfBuffers() {
        if ( cbMultibleBuffer.isSelected() ) {
            return ( (Number) spMultipleBuffer.getValue() ).intValue();
        }
        return -1;
    }

    /**
     * 
     * @return name of the property containing buffer size or <code>null</code> if no property has been selected
     */
    QualifiedName getPropertyForBufferDistance() {
        if ( cbUseProperty.isSelected() ) {
            return (QualifiedName) cbProperty.getSelectedItem();
        }
        return null;
    }

    /**
     * 
     * @return true if buffers should be overlayed if multiple bufferes are selected
     */
    boolean isOverlayedBuffers() {
        return rbOverlayed.isSelected();
    }
}
