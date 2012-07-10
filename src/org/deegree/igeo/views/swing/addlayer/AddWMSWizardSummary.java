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

import static java.util.Collections.singleton;
import static org.deegree.framework.util.CollectionUtils.unzip;
import static org.deegree.framework.util.CollectionUtils.zip;
import static org.deegree.igeo.views.swing.util.GuiUtils.addToFrontListener;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;

import org.deegree.framework.util.Pair;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.utils.SwingUtils;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.model.AddWMSLayerCommand;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.views.swing.ScaleDenominatorPanel;
import org.deegree.ogcwebservices.wms.capabilities.Layer;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.owscommon_new.ServiceIdentification;

/**
 * <code>JAddWMSWizardSummary</code> is the last step to insert a wms datasource as a new layer. All Information of the
 * layer and datasource are shown and can be edit by the user.
 * 
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class AddWMSWizardSummary extends WizardDialog implements ActionListener {

    private static final long serialVersionUID = -9168560585982496762L;

    private MapModel mapModel;

    private WMSCapabilities wmsCaps;

    private Map<Layer, String> selectedLayers;

    private String format;

    private boolean transparency;

    private JTextField serviceName;

    private JTextField serviceTitle;

    private JTextArea layAbstract;

    private JTextField dsName;

    private JTextArea dsBaseRequest;

    private ScaleDenominatorPanel dsScaleDenomPanel;

    private String layerTitle;

    private boolean separately;

    private LinkedList<AddWMSLayerCommand> commands;

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
     * @param transparency
     * @param separately
     * @param commands
     */
    public AddWMSWizardSummary( JFrame frame, MapModel mapModel, ApplicationContainer<Container> appContainer,
                                WMSCapabilities wmsCapabilities, Map<Layer, String> selectedLayers, String layerTitle,
                                String format, boolean transparency, boolean separately,
                                LinkedList<AddWMSLayerCommand> commands ) {
        super( frame );
        this.commands = commands == null && separately ? new LinkedList<AddWMSLayerCommand>() : commands;
        this.separately = separately;
        this.mapModel = mapModel;
        this.appContainer = appContainer;
        this.wmsCaps = wmsCapabilities;
        this.selectedLayers = selectedLayers;
        this.layerTitle = layerTitle;
        this.format = format;
        this.transparency = transparency;

        this.setSize( 500, 600 );
        this.setResizable( false );
        this.setTitle( Messages.getMessage( Locale.getDefault(), "$MD10036" ) );
        infoPanel.setInfoText( Messages.getMessage( Locale.getDefault(), "$MD10037" ) );

        buttonPanel.registerActionListener( this );
        buttonPanel.setButtonEnabled( ButtonPanel.NEXT_BT, separately && selectedLayers.size() > 1 );
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
        ServiceIdentification service = this.wmsCaps.getServiceIdentification();

        JLabel serviceNameLabel = new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10040" ) );
        serviceNameLabel.setVisible( true );
        serviceNameLabel.setBackground( Color.BLUE );
        serviceNameLabel.setBorder( BorderFactory.createEmptyBorder() );
        this.serviceName = new JTextField( UUID.randomUUID().toString() );
        this.serviceName.setPreferredSize( textFieldDim );
        this.serviceName.setBorder( border );
        this.serviceName.setVisible( true );

        JLabel serviceTitleLabel = new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10041" ) );
        serviceTitleLabel.setVisible( true );
        serviceTitleLabel.setBorder( BorderFactory.createEmptyBorder() );
        this.serviceTitle = new JTextField( this.layerTitle );
        this.serviceTitle.setPreferredSize( textFieldDim );
        this.serviceTitle.setBorder( border );
        this.serviceTitle.setVisible( true );

        JLabel serviceAbstractLabel = new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10042" ) );
        serviceAbstractLabel.setVisible( true );
        serviceAbstractLabel.setBorder( BorderFactory.createEmptyBorder() );
        this.layAbstract = new JTextArea( createLayerAbstract() );
        this.layAbstract.setPreferredSize( textAreaDim );
        this.layAbstract.setBorder( border );
        this.layAbstract.setVisible( true );

        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.gridwidth = 2;
        summaryPanel.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10038" ) ), gbc );
        gbc.gridwidth = 1;
        ++gbc.gridy;
        summaryPanel.add( serviceNameLabel, gbc );
        ++gbc.gridx;
        gbc.gridwidth = 4;
        summaryPanel.add( this.serviceName, gbc );
        ++gbc.gridy;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        summaryPanel.add( serviceTitleLabel, gbc );
        ++gbc.gridx;
        gbc.gridwidth = 4;
        summaryPanel.add( this.serviceTitle, gbc );
        ++gbc.gridy;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        summaryPanel.add( serviceAbstractLabel, gbc );
        ++gbc.gridx;
        gbc.gridwidth = 4;
        summaryPanel.add( this.layAbstract, gbc );

        JLabel dsNameLabel = new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10043" ) );
        dsNameLabel.setVisible( true );
        dsNameLabel.setBorder( BorderFactory.createEmptyBorder() );
        this.dsName = new JTextField( service.getTitle() );
        this.dsName.setPreferredSize( textFieldDim );
        this.dsName.setBorder( border );
        this.dsName.setVisible( true );

        gbc.gridx = 0;
        gbc.insets = new Insets( 20, 2, 2, 2 );
        gbc.gridwidth = 2;
        ++gbc.gridy;
        summaryPanel.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10039" ) ), gbc );
        gbc.insets = new Insets( 2, 2, 2, 2 );
        gbc.gridwidth = 1;
        ++gbc.gridy;
        summaryPanel.add( dsNameLabel, gbc );
        ++gbc.gridx;
        gbc.gridwidth = 4;
        summaryPanel.add( this.dsName, gbc );

        // shpw the scale denominator panel
        this.dsScaleDenomPanel = new ScaleDenominatorPanel();

        JLabel dsDenominatorLabel = new JLabel( Messages.getMessage( Locale.getDefault(), "$MD11205" ) );
        dsDenominatorLabel.setVisible( true );
        dsDenominatorLabel.setBorder( BorderFactory.createEmptyBorder() );

        gbc.gridx = 0;
        ++gbc.gridy;
        gbc.gridwidth = 1;
        summaryPanel.add( dsDenominatorLabel, gbc );
        ++gbc.gridx;
        gbc.gridwidth = 4;
        summaryPanel.add( dsScaleDenomPanel, gbc );

        // show the base request
        JLabel dsBaseRequestLabel = new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10052" ) );
        dsBaseRequestLabel.setVisible( true );
        dsBaseRequestLabel.setBorder( BorderFactory.createEmptyBorder() );

        String layers = null;
        String styles = null;
        for ( Pair<Layer, String> pair : zip( selectedLayers ) ) {
            if ( layers != null && layers.length() > 0 ) {
                layers = StringTools.concat( 300, layers, ',', pair.first.getName() );
                styles = StringTools.concat( 300, styles, ',', pair.second );
            } else {
                layers = pair.first.getName();
                styles = pair.second;
            }
            if ( separately ) {
                break;
            }
        }

        String baseRequest = StringTools.concat( 500, "REQUEST=GetMap&VERSION=", this.wmsCaps.getVersion(),
                                                 "&TRANSPARENT=", ( "" + transparency ).toUpperCase(), "&LAYERS=",
                                                 layers, "&FORMAT=", format, "&STYLES=", styles );
        this.dsBaseRequest = new JTextArea( baseRequest );
        this.dsBaseRequest.setLineWrap( true );
        this.dsBaseRequest.setWrapStyleWord( true );
        this.dsBaseRequest.setEditable( false );
        this.dsBaseRequest.setVisible( true );

        Dimension brDim = new Dimension( 300, 100 );
        JScrollPane baseRequestScroll = new JScrollPane( this.dsBaseRequest );
        baseRequestScroll.setBorder( border );
        baseRequestScroll.setPreferredSize( brDim );
        baseRequestScroll.setMinimumSize( brDim );
        baseRequestScroll.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );

        gbc.gridx = 0;
        ++gbc.gridy;
        gbc.gridwidth = 1;
        summaryPanel.add( dsBaseRequestLabel, gbc );
        ++gbc.gridx;
        gbc.gridwidth = 4;
        summaryPanel.add( baseRequestScroll, gbc );

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
                if ( separately ) {
                    commands.removeLast();
                }
                close();
            } else if ( srcButton.getName().equals( ButtonPanel.NEXT_BT ) ) {
                if ( separately ) {
                    commands.add( getCurrentCommand() );
                    LinkedList<Pair<Layer, String>> list = zip( selectedLayers );
                    list.poll();
                    AddWMSWizardSummary nextStep = new AddWMSWizardSummary( this, this.mapModel, this.appContainer,
                                                                            wmsCaps, unzip( list ),
                                                                            list.getFirst().first.getName(), format,
                                                                            transparency, true, commands );
                    addToFrontListener( nextStep );
                    nextStep.setLocation( this.getX(), this.getY() );
                    nextStep.setVisible( true );
                    this.setVisible( false );
                }
            } else if ( srcButton.getName().equals( ButtonPanel.FINISH_BT ) ) {
                if ( separately ) {
                    getTheRestOfCommands();
                    for ( AddWMSLayerCommand cmd : commands ) {
                        appContainer.getCommandProcessor().executeASychronously( cmd );
                    }
                } else {
                    appContainer.getCommandProcessor().executeASychronously( getCurrentCommand() );
                }
                this.dispose();
            }
        }
    }

    private AddWMSLayerCommand getCurrentCommand() {
        // information of the layer
        String layerName = this.serviceName.getText();
        String layerTitle = this.serviceTitle.getText();

        // information about the datasource
        String nameDS = this.dsName.getText();
        double minScale = dsScaleDenomPanel.getMinScaleDenominator();
        double maxScale = dsScaleDenomPanel.getMaxScaleDenominator();

        String baseRequest = this.dsBaseRequest.getText();
        String layerAbstract = layAbstract.getText();

        // TODO:
        // extent, isQueryable, authenticationInformation, cache

        Map<Layer, String> layers = separately ? unzip( singleton( zip( selectedLayers ).getFirst() ) )
                                              : selectedLayers;

        return new AddWMSLayerCommand( this.mapModel, wmsCaps, layerName, layerTitle, layerAbstract, nameDS, minScale,
                                       maxScale, layers, baseRequest );
    }

    private String createLayerAbstract() {
        String layerAbstract = "";
        if ( separately ) {
            layerAbstract = selectedLayers.keySet().iterator().next().getAbstract();
        } else {
            Iterator<Layer> iterator = selectedLayers.keySet().iterator();
            while ( iterator.hasNext() ) {
                Layer layer = iterator.next();
                layerAbstract += layer.getAbstract();
                if ( iterator.hasNext() ) {
                    layerAbstract += " | ";
                }
            }
        }
        return layerAbstract;
    }

    private void getTheRestOfCommands() {
        commands.add( getCurrentCommand() );
        LinkedList<Pair<Layer, String>> list = zip( selectedLayers );
        list.poll();
        if ( !list.isEmpty() ) {
            new AddWMSWizardSummary( this, mapModel, appContainer, wmsCaps, unzip( list ),
                                     list.getFirst().first.getName(), format, transparency, true, commands ).getTheRestOfCommands();
        }
    }

}
