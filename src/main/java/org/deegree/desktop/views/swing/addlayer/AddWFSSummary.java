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

package org.deegree.desktop.views.swing.addlayer;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.commands.model.AddWFSLayerCommand;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.views.swing.ScaleDenominatorPanel;
import org.deegree.framework.utils.SwingUtils;
import org.deegree.kernel.CommandProcessedEvent;
import org.deegree.kernel.CommandProcessedListener;
import org.deegree.kernel.ProcessMonitor;
import org.deegree.kernel.ProcessMonitorFactory;
import org.deegree.model.filterencoding.Filter;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSFeatureType;

/**
 * <code>JAddWFSWizardSummary</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class AddWFSSummary extends WizardDialog implements ActionListener {

    private static final long serialVersionUID = 6530540011590019717L;

    private MapModel mapModel;

    private WFSCapabilities wfsCapabilities;

    private WFSFeatureType featureType;

    private QualifiedName geometryProp;

    private Filter filter;

    private JTextField serviceName = new JTextField();

    private JTextField serviceTitle = new JTextField();

    private JTextArea serviceAbstract = new JTextArea();

    private JTextField dsName = new JTextField();

    private ScaleDenominatorPanel dsScaleDenomPanel;

    private JCheckBox cbLazyLoading;

    /**
     * @param frame
     *            the previous dialog
     * @param mapModel
     *            the maModel to add the new layer
     * @param appContainer
     *            the applicationContainer
     * @param wfsCapabilities
     *            the capabilities of the WES to add as new layer
     * @param featureType
     *            the selected featureType of the WFS to add as new layer
     * @param geometryProp
     *            the geometry property of the selected featureType to add as new layer
     * @param filter
     *            the filter
     */
    public AddWFSSummary( JFrame frame, MapModel mapModel, ApplicationContainer<Container> appContainer,
                          WFSCapabilities wfsCapabilities, WFSFeatureType featureType,
                          QualifiedName geometryProp, Filter filter ) {
        super( frame );

        this.mapModel = mapModel;
        this.appContainer = appContainer;
        this.wfsCapabilities = wfsCapabilities;
        this.featureType = featureType;
        this.geometryProp = geometryProp;
        this.filter = filter;

        this.setSize( 500, 600 );
        this.setResizable( false );
        this.setTitle( Messages.getMessage( Locale.getDefault(), "$MD10135" ) );
        infoPanel.setInfoText( Messages.getMessage( Locale.getDefault(), "$MD10136" ) );

        buttonPanel.setButtonEnabled( ButtonPanel.NEXT_BT, false );
        buttonPanel.registerActionListener( this );
        super.init();
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // WizardDialog
    // /////////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.addlayer.WizardDialog#getMainPanel()
     */
    @Override
    public JPanel getMainPanel() {
        JPanel summaryPanel = new JPanel();
        GridBagConstraints gbc = SwingUtils.initPanel( summaryPanel );

        Dimension textFieldDim = new Dimension( 300, 25 );
        Dimension textAreaDim = new Dimension( 300, 50 );
        Border border = BorderFactory.createEmptyBorder();

        // information about the layer:
        this.serviceName = new JTextField( UUID.randomUUID().toString() );
        this.serviceName.setPreferredSize( textFieldDim );
        this.serviceName.setBorder( border );
        this.serviceName.setVisible( true );

        String title;
        if ( featureType.getTitle() != null && featureType.getTitle().length() > 0 ) {
            title = featureType.getTitle();
        } else {
            title = featureType.getName().getLocalName();
        }

        this.serviceTitle = new JTextField( title );
        this.serviceTitle.setPreferredSize( textFieldDim );
        this.serviceTitle.setBorder( border );
        this.serviceTitle.setVisible( true );

        this.serviceAbstract = new JTextArea( featureType.getAbstract() );
        this.serviceAbstract.setPreferredSize( textAreaDim );
        this.serviceAbstract.setBorder( border );
        this.serviceAbstract.setVisible( true );

        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridwidth = 2;

        summaryPanel.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10137" ) ), gbc );
        gbc.gridwidth = 1;
        ++gbc.gridy;
        summaryPanel.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10139" ) ), gbc );
        ++gbc.gridx;
        gbc.gridwidth = 4;
        summaryPanel.add( this.serviceName, gbc );
        ++gbc.gridy;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        summaryPanel.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10140" ) ), gbc );
        ++gbc.gridx;
        gbc.gridwidth = 4;
        summaryPanel.add( this.serviceTitle, gbc );
        ++gbc.gridy;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        summaryPanel.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10141" ) ), gbc );
        ++gbc.gridx;
        gbc.gridwidth = 4;
        summaryPanel.add( this.serviceAbstract, gbc );

        String dsName = featureType.getName().getLocalName();
        if ( wfsCapabilities.getServiceIdentification() != null
             && wfsCapabilities.getServiceIdentification().getName() != null ) {
            dsName = dsName + " " + wfsCapabilities.getServiceIdentification().getName();
        }

        this.dsName = new JTextField( dsName );
        this.dsName.setPreferredSize( textFieldDim );
        this.dsName.setBorder( border );
        this.dsName.setVisible( true );

        gbc.gridx = 0;
        gbc.insets = new Insets( 20, 2, 2, 2 );
        gbc.gridwidth = 2;
        ++gbc.gridy;
        summaryPanel.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10138" ) ), gbc );
        gbc.insets = new Insets( 2, 2, 2, 2 );
        gbc.gridwidth = 1;
        ++gbc.gridy;
        summaryPanel.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10142" ) ), gbc );
        ++gbc.gridx;
        gbc.gridwidth = 4;
        summaryPanel.add( this.dsName, gbc );

        // show the scale denominator panel
        this.dsScaleDenomPanel = new ScaleDenominatorPanel();

        gbc.gridx = 0;
        gbc.insets = new Insets( 2, 2, 2, 2 );
        gbc.gridwidth = 1;
        ++gbc.gridy;
        summaryPanel.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD11204" ) ), gbc );
        ++gbc.gridx;
        gbc.gridwidth = 4;
        summaryPanel.add( dsScaleDenomPanel, gbc );

        // add checkbox for selecting a layer as being lazy loaded
        this.cbLazyLoading = new JCheckBox( "lazy loading" );
        gbc.gridx = 0;
        gbc.insets = new Insets( 2, 2, 2, 2 );
        gbc.gridwidth = 1;
        ++gbc.gridy;
        summaryPanel.add( cbLazyLoading, gbc );

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
                this.dispose();
            } else if ( srcButton.getName().equals( ButtonPanel.PREVIOUS_BT ) ) {
                if ( this.previousFrame != null ) {
                    this.previousFrame.setVisible( true );
                }
                this.close();
            } else if ( srcButton.getName().equals( ButtonPanel.FINISH_BT ) ) {
                // adds the new layer and closes the current window
                String serviceName = this.serviceName.getText();
                String serviceTitle = this.serviceTitle.getText();
                String serviceAbstract = this.serviceAbstract.getText();
                String dsName = this.dsName.getText();
                double minScale = dsScaleDenomPanel.getMinScaleDenominator();
                double maxScale = dsScaleDenomPanel.getMaxScaleDenominator();
                AddWFSLayerCommand command = new AddWFSLayerCommand( this.mapModel,
                                                                     this.wfsCapabilities, this.featureType,
                                                                     this.geometryProp, filter, serviceName,
                                                                     serviceTitle, serviceAbstract, dsName, minScale,
                                                                     maxScale, cbLazyLoading.isSelected() );

                final ProcessMonitor pm = ProcessMonitorFactory.createDialogProcessMonitor(
                                                                                            appContainer.getViewPlatform(),
                                                                                            Messages.get( "$MD11266" ),
                                                                                            Messages.get(
                                                                                                          "$MD11267",
                                                                                                          featureType.getTitle() ),
                                                                                            0, -1, command );
                command.setProcessMonitor( pm );
                command.addListener( new CommandProcessedListener() {

                    public void commandProcessed( CommandProcessedEvent event ) {
                        try {
                            pm.cancel();
                        } catch ( Exception e ) {
                            e.printStackTrace();
                        }
                    }

                } );

                appContainer.getCommandProcessor().executeASychronously( command );
                this.dispose();
            }
        }
    }

}
