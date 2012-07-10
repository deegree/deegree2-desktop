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
import static org.deegree.igeo.i18n.Messages.get;
import static org.deegree.igeo.views.swing.util.GuiUtils.addToFrontListener;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.values.TypedLiteral;
import org.deegree.framework.util.Pair;
import org.deegree.framework.utils.SwingUtils;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.model.AddWMSLayerCommand;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.ogcwebservices.wms.capabilities.Layer;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.owscommon_new.DomainType;
import org.deegree.owscommon_new.Operation;
import org.deegree.owscommon_new.OperationsMetadata;

/**
 * <code>JAddWMSWizardChooseCRS</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class AddWMSWizardChooseFormat extends WizardDialog implements ActionListener {

    private static final long serialVersionUID = 7746816571048832101L;

    private MapModel mapModel;

    private WMSCapabilities wmsCaps;

    private Map<Layer, String> selectedLayers;

    private JComboBox formatChooser;

    private JCheckBox transparent;

    private String layerTitle;

    private JCheckBox requestSeparately;

    /**
     * 
     * @param frame
     *            the previous dialog
     * @param mapModel
     *            the mapModel to add the new layer
     * @param appContainer
     *            the application container
     * @param wmsCapabilities
     *            the capabailities of the requested wms
     * @param selectedLayers
     *            the selected layers
     * @param layerTitle
     *            the title of the layer
     */
    public AddWMSWizardChooseFormat( JFrame frame, MapModel mapModel, ApplicationContainer<Container> appContainer,
                                     WMSCapabilities wmsCapabilities, Map<Layer, String> selectedLayers,
                                     String layerTitle ) {
        super( frame );
        this.mapModel = mapModel;
        this.appContainer = appContainer;
        this.wmsCaps = wmsCapabilities;
        this.selectedLayers = selectedLayers;
        this.layerTitle = layerTitle;

        this.setSize( 500, 600 );
        this.setResizable( false );
        this.setTitle( Messages.getMessage( Locale.getDefault(), "$MD10033" ) );
        infoPanel.setInfoText( Messages.getMessage( Locale.getDefault(), "$MD10034" ) );

        buttonPanel.registerActionListener( this );
        super.init();

    }

    // /////////////////////////////////////////////////////////////////////////////////
    // WizardDialog
    // /////////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.WizardDialog#getMainPanel()
     */
    @Override
    public JPanel getMainPanel() {
        JPanel chooseFormatPanel = new JPanel();
        GridBagConstraints gbc = SwingUtils.initPanel( chooseFormatPanel );

        JLabel formatChooserLabel = new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10035" ) );

        formatChooser = new JComboBox();
        formatChooser.setVisible( true );
        // fill combo box with available formates
        OperationsMetadata om = wmsCaps.getOperationMetadata();
        Operation op = om.getOperation( new QualifiedName( "GetMap" ) );
        if ( op == null ) {
            op = om.getOperation( new QualifiedName( "map" ) );
        }
        DomainType parameter = (DomainType) op.getParameter( new QualifiedName( "Format" ) );
        List<TypedLiteral> values = parameter.getValues();
        for ( int i = 0; i < values.size(); ++i ) {
            formatChooser.addItem( values.get( i ).getValue() );
        }

        transparent = new JCheckBox( Messages.getMessage( Locale.getDefault(), "$MD10103" ), true );
        transparent.setVisible( true );

        requestSeparately = new JCheckBox( get( "$MD10902" ) );
        requestSeparately.setEnabled( selectedLayers.size() > 1 );

        gbc.anchor = GridBagConstraints.LINE_START;
        chooseFormatPanel.add( formatChooserLabel, gbc );
        ++gbc.gridy;
        chooseFormatPanel.add( formatChooser, gbc );
        gbc.insets = new Insets( 10, 2, 2, 2 );
        ++gbc.gridy;
        chooseFormatPanel.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10102" ) ), gbc );
        gbc.insets = new Insets( 2, 2, 2, 2 );
        ++gbc.gridy;
        chooseFormatPanel.add( transparent, gbc );
        ++gbc.gridy;
        chooseFormatPanel.add( requestSeparately, gbc );

        return chooseFormatPanel;
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
            } else if ( srcButton.getName().equals( ButtonPanel.NEXT_BT ) ) {
                AddWMSWizardSummary nextStep;
                if ( requestSeparately.isSelected() ) {
                    nextStep = new AddWMSWizardSummary( this, this.mapModel, this.appContainer, wmsCaps,
                                                        selectedLayers,
                                                        selectedLayers.keySet().iterator().next().getName(),
                                                        getFormat(), isTransparent(), true, null );
                } else {
                    nextStep = new AddWMSWizardSummary( this, this.mapModel, this.appContainer, wmsCaps,
                                                        unzip( singleton( zip( selectedLayers ).getFirst() ) ),
                                                        this.layerTitle, getFormat(), isTransparent(), false, null );
                }
                addToFrontListener( nextStep );
                nextStep.setLocation( this.getX(), this.getY() );
                nextStep.setVisible( true );
                this.setVisible( false );
            } else if ( srcButton.getName().equals( ButtonPanel.FINISH_BT ) ) {
                if ( requestSeparately.isSelected() ) {
                    for ( Pair<Layer, String> pair : zip( selectedLayers ) ) {
                        AddWMSLayerCommand addWMSCmd = new AddWMSLayerCommand( this.mapModel, this.wmsCaps,
                                                                               unzip( singleton( pair ) ),
                                                                               pair.first.getName(), getFormat(),
                                                                               isTransparent() );
                        appContainer.getCommandProcessor().executeASychronously( addWMSCmd );
                    }
                } else {
                    AddWMSLayerCommand addWMSCmd = new AddWMSLayerCommand( this.mapModel, this.wmsCaps, selectedLayers,
                                                                           this.layerTitle, getFormat(),
                                                                           isTransparent() );
                    appContainer.getCommandProcessor().executeASychronously( addWMSCmd );
                }
                this.dispose();
            }
        }
    }

    private String getFormat() {
        return (String) formatChooser.getSelectedItem();
    }

    private boolean isTransparent() {
        return this.transparent.isSelected();
    }

}
