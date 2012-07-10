/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2007 by:
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

package org.deegree.igeo.views.swing.addlayer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.deegree.framework.utils.SwingUtils;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.model.AddWCSLayerCommand;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.views.swing.ScaleDenominatorPanel;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageOffering;
import org.deegree.ogcwebservices.wcs.getcapabilities.WCSCapabilities;

/**
 * <code>JAddWMSWizardSummary</code> is the last step to insert a wms datasource as a new layer. All Information of the
 * layer and datasource are shown and can be edit by the user.
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class AddWCSWizardSummary extends WizardDialog implements ActionListener {

    private static final long serialVersionUID = -9168560585982496762L;

    private MapModel mapModel;

    private WCSCapabilities wcsCaps;

    private URL capabilitiesURL;

    private List<CoverageOffering> selectedCoverages;

    private String format;

    private String timestamp;

    private JTextField tfLayerName;

    private JTextField tfLayerTitle;

    private JTextArea taLayerAbstract;

    private JTextField tfDsName;

    private ScaleDenominatorPanel pnDsScaleDenomPanel;

    private String layerTitle;

    private LinkedList<AddWCSLayerCommand> commands;

    /**
     * @param frame
     *            the previous dialog
     * @param mapModel
     *            the mapModelAdapter to add the new layer
     * @param appContainer
     *            the application container
     * @param wmsCapabilities
     *            the capabailities of the requested wms
     * @param capabilitiesURL
     *            the capabailities url of the wms
     * @param selectedLayers
     *            the selected layers
     * @param format
     *            the desired format
     * @param layerTitle
     *            the title of the layer
     * @param timestamp
     * @param separately
     */
    public AddWCSWizardSummary( JFrame frame, MapModel mapModel, ApplicationContainer<Container> appContainer,
                                WCSCapabilities wcsCapabilities, URL capabilitiesURL,
                                List<CoverageOffering> selectedCoverages, String format, String timestamp,
                                boolean separately ) {
        super( frame );
        this.commands = new LinkedList<AddWCSLayerCommand>();
        this.mapModel = mapModel;
        this.appContainer = appContainer;
        this.wcsCaps = wcsCapabilities;
        this.capabilitiesURL = capabilitiesURL;
        this.selectedCoverages = selectedCoverages;
        this.format = format;
        this.timestamp = timestamp;
        this.layerTitle = selectedCoverages.get( 0 ).getLabel();

        this.setSize( 500, 600 );
        this.setResizable( false );
        this.setTitle( Messages.getMessage( Locale.getDefault(), "$MD11385" ) );
        infoPanel.setInfoText( Messages.getMessage( Locale.getDefault(), "$MD11386" ) );

        buttonPanel.registerActionListener( this );
        buttonPanel.setButtonEnabled( ButtonPanel.NEXT_BT, false );
        super.init();

    }

    // /////////////////////////////////////////////////////////////////////////////////
    // WizardDialog
    // //////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.WizardDialog#getMainPanel()
     */
    @Override
    public JPanel getMainPanel() {
        JPanel summaryPanel = new JPanel();
        GridBagConstraints gbc = SwingUtils.initPanel( summaryPanel );

        Dimension textFieldDim = new Dimension( 300, 25 );
        Dimension textAreaDim = new Dimension( 300, 50 );
        Border border = BorderFactory.createEmptyBorder();

        // information about the layer:

        JLabel serviceNameLabel = new JLabel( Messages.getMessage( Locale.getDefault(), "$MD11387" ) );
        serviceNameLabel.setVisible( true );
        serviceNameLabel.setBackground( Color.BLUE );
        serviceNameLabel.setBorder( BorderFactory.createEmptyBorder() );
        this.tfLayerName = new JTextField( UUID.randomUUID().toString() );
        this.tfLayerName.setPreferredSize( textFieldDim );
        this.tfLayerName.setBorder( border );
        this.tfLayerName.setVisible( true );

        JLabel serviceTitleLabel = new JLabel( Messages.getMessage( Locale.getDefault(), "$MD11388" ) );
        serviceTitleLabel.setVisible( true );
        serviceTitleLabel.setBorder( BorderFactory.createEmptyBorder() );
        this.tfLayerTitle = new JTextField( this.layerTitle );
        this.tfLayerTitle.setPreferredSize( textFieldDim );
        this.tfLayerTitle.setBorder( border );
        this.tfLayerTitle.setVisible( true );

        JLabel serviceAbstractLabel = new JLabel( Messages.getMessage( Locale.getDefault(), "$MD11389" ) );
        serviceAbstractLabel.setVisible( true );
        serviceAbstractLabel.setBorder( BorderFactory.createEmptyBorder() );
        this.taLayerAbstract = new JTextArea( selectedCoverages.get( 0 ).getDescription() );
        this.taLayerAbstract.setPreferredSize( textAreaDim );
        this.taLayerAbstract.setBorder( border );
        this.taLayerAbstract.setVisible( true );

        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridwidth = 2;
        summaryPanel.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD11390" ) ), gbc );
        gbc.gridwidth = 1;
        ++gbc.gridy;
        summaryPanel.add( serviceNameLabel, gbc );
        ++gbc.gridx;
        gbc.gridwidth = 4;
        summaryPanel.add( this.tfLayerName, gbc );
        ++gbc.gridy;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        summaryPanel.add( serviceTitleLabel, gbc );
        ++gbc.gridx;
        gbc.gridwidth = 4;
        summaryPanel.add( this.tfLayerTitle, gbc );
        ++gbc.gridy;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        summaryPanel.add( serviceAbstractLabel, gbc );
        ++gbc.gridx;
        gbc.gridwidth = 4;
        summaryPanel.add( this.taLayerAbstract, gbc );

        JLabel dsNameLabel = new JLabel( Messages.getMessage( Locale.getDefault(), "$MD11391" ) );
        dsNameLabel.setVisible( true );
        dsNameLabel.setBorder( BorderFactory.createEmptyBorder() );
        this.tfDsName = new JTextField( this.wcsCaps.getService().getLabel() );
        this.tfDsName.setPreferredSize( textFieldDim );
        this.tfDsName.setBorder( border );
        this.tfDsName.setVisible( true );

        gbc.gridx = 0;
        gbc.insets = new Insets( 20, 2, 2, 2 );
        gbc.gridwidth = 2;
        ++gbc.gridy;
        summaryPanel.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD11392" ) ), gbc );
        gbc.insets = new Insets( 2, 2, 2, 2 );
        gbc.gridwidth = 1;
        ++gbc.gridy;
        summaryPanel.add( dsNameLabel, gbc );
        ++gbc.gridx;
        gbc.gridwidth = 4;
        summaryPanel.add( this.tfDsName, gbc );

        // show the scale denominator panel
        this.pnDsScaleDenomPanel = new ScaleDenominatorPanel();

        JLabel dsDenominatorLabel = new JLabel( Messages.getMessage( Locale.getDefault(), "$MD11393" ) );
        dsDenominatorLabel.setVisible( true );
        dsDenominatorLabel.setBorder( BorderFactory.createEmptyBorder() );

        gbc.gridx = 0;
        ++gbc.gridy;
        gbc.gridwidth = 1;
        summaryPanel.add( dsDenominatorLabel, gbc );
        ++gbc.gridx;
        gbc.gridwidth = 4;
        summaryPanel.add( pnDsScaleDenomPanel, gbc );

        summaryPanel.setVisible( true );
        return summaryPanel;
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // ActionListener
    // /////////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed( ActionEvent event ) {
        if ( event.getSource() instanceof JButton ) {
            JButton srcButton = (JButton) event.getSource();
            if ( srcButton.getName().equals( ButtonPanel.CANCEL_BT ) ) {
                // close this and the previous frame
                this.dispose();
            } else if ( srcButton.getName().equals( ButtonPanel.PREVIOUS_BT ) ) {
                // set the previous frame visible and close this frame
                if ( this.previousFrame != null ) {
                    this.previousFrame.setVisible( true );
                }
                if ( commands.size() > 0 ) {
                    commands.removeLast();
                }
                close();
            } else if ( srcButton.getName().equals( ButtonPanel.FINISH_BT ) ) {
                commands.add( getCurrentCommand( selectedCoverages.get( 0 ) ) );
                for ( AddWCSLayerCommand cmd : commands ) {
                    appContainer.getCommandProcessor().executeASychronously( cmd );
                }
                this.dispose();
            }
        }
    }

    private AddWCSLayerCommand getCurrentCommand( CoverageOffering coverageOffering ) {
        // information of the layer
        String layerName = this.tfLayerName.getText();
        String layerTitle = this.tfLayerTitle.getText();

        // information about the datasource
        String nameDS = this.tfDsName.getText();
        double minScale = pnDsScaleDenomPanel.getMinScaleDenominator();
        double maxScale = pnDsScaleDenomPanel.getMaxScaleDenominator();

        String layerAbstract = selectedCoverages.iterator().next().getDescription();

        return new AddWCSLayerCommand( this.mapModel, this.capabilitiesURL, wcsCaps, layerName, layerTitle,
                                       layerAbstract, nameDS, minScale, maxScale, coverageOffering, format, timestamp );
    }

}
