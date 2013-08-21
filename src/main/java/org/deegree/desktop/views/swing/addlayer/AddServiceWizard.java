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

import static org.deegree.desktop.views.swing.util.GuiUtils.addToFrontListener;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.modules.IModule;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.utils.SwingUtils;

/**
 * <code>IAddServiceWizard</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * @param <T>
 * 
 */
public abstract class AddServiceWizard<T> extends WizardDialog implements ActionListener, CaretListener {

    private static final long serialVersionUID = 3084309745488600358L;

    protected MapModel mapModel;

    protected IModule<T> module;

    protected JComboBox knownServicesChooser;

    protected JTextField enterServicesField;

    protected ButtonGroup versionBG;
    
    protected JCheckBox cbSwapAxis;

    protected Map<String, String> knownServices = new HashMap<String, String>( 10 );

    /**
     * 
     * @param frame
     *            the previous frame
     * @param module
     *            the module this function is assigned
     * @param mapModel
     *            the map model adapter to add the new layer
     */
    @SuppressWarnings("unchecked")
    public AddServiceWizard( JFrame frame, IModule<T> module, MapModel mapModel ) {
        super( frame );
        addToFrontListener( this );
        this.module = module;
        this.appContainer = (ApplicationContainer<Container>) module.getApplicationContainer();
        this.mapModel = mapModel;
        this.setSize( 500, 600 );
        this.setResizable( false );

        // fill map with all known services
        knownServices.put( getComboBoxDefaultLabel(), "http://" );
        String knownWMS = module.getInitParameters().get( getKnownServicesInitParamKey() );
        String[] knownWMSs = StringTools.toArray( knownWMS, ",", false );
        for ( int i = 0; i < knownWMSs.length; i++ ) {
            int indexOfSep = knownWMSs[i].indexOf( ':' );
            if ( indexOfSep > 0 ) {
                String wmsName = knownWMSs[i].substring( 0, indexOfSep ).trim();
                String wmsUrl = knownWMSs[i].substring( indexOfSep + 1 ).trim();
                knownServices.put( wmsName, wmsUrl );
            }
        }

        // set buttons
        buttonPanel.setButtonEnabled( ButtonPanel.NEXT_BT, false );
        buttonPanel.setButtonEnabled( ButtonPanel.FINISH_BT, false );
        if ( previousFrame == null ) {
            buttonPanel.setButtonEnabled( ButtonPanel.PREVIOUS_BT, false );
        }
        buttonPanel.registerActionListener( this );

        // set text of the infoPanel
        infoPanel.setInfoText( getInfoText() );

        super.init();

    }

    /**
     * Inserts serviceName and serviceUrl as a new entry to the init parameter of known services if not yet registered.
     * 
     * @param serviceName
     *            the name of the service to insert
     * @param serviceUrl
     *            the url of the service to insert
     */
    protected void insertKnownService( String serviceName, String serviceUrl ) {
        String knownServices = this.module.getInitParameter( getKnownServicesInitParamKey() );
        String[] knownServicesArray = StringTools.toArray( knownServices, ",", false );
        boolean isKnown = false;
        for ( int i = 0; i < knownServicesArray.length; i++ ) {
            int indexOfSep = knownServicesArray[i].indexOf( ':' );
            if ( indexOfSep > 0 ) {
                String wmsUrl = knownServicesArray[i].substring( indexOfSep + 1 ).trim();
                if ( serviceUrl.equals( wmsUrl ) ) {
                    isKnown = true;
                    break;
                }
            }
        }
        if ( !isKnown ) {
            String newKnownServices = StringTools.concat( 500, knownServices, ",", serviceName, ": ", serviceUrl );
            this.module.updateInitParameter( getKnownServicesInitParamKey(), newKnownServices );
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // WizardDialog
    // /////////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.IWizardDialog#getMainPanel()
     */
    @Override
    public JPanel getMainPanel() {
        JPanel addServicePanel = new JPanel();
        addServicePanel.setVisible( true );
        Dimension dim = new Dimension( 400, 200 );
        addServicePanel.setPreferredSize( dim );
        addServicePanel.setMinimumSize( dim );

        // ows version array
        String owsVersions = module.getInitParameters().get( getVersionInitParamKey() );
        String[] versionArray = StringTools.toArray( owsVersions, ",", true );
        // int nrOfWMSVersions = versionArray.length + 1;

        GridBagConstraints gbc = SwingUtils.initPanel( addServicePanel );
        gbc.anchor = GridBagConstraints.LINE_START;

        // -- ComboBox for known services
        JLabel knownServicesLabel = new JLabel( getKnownServicesLabel() );
        knownServicesLabel.setVisible( true );
        knownServicesLabel.setPreferredSize( new Dimension( 200, 25 ) );
        knownServicesChooser = new JComboBox();
        knownServicesChooser.addActionListener( this );
        knownServicesChooser.setVisible( true );
        knownServicesChooser.setPreferredSize( new Dimension( 200, 25 ) );
        knownServicesChooser.setEditable( false );

        knownServicesChooser.addItem( getComboBoxDefaultLabel() );

        for ( String serviceName : knownServices.keySet() ) {
            if ( !serviceName.equals( getComboBoxDefaultLabel() ) ) {
                knownServicesChooser.addItem( serviceName );
            }
        }

        addServicePanel.add( knownServicesLabel, gbc );
        ++gbc.gridy;
        addServicePanel.add( knownServicesChooser, gbc );

        // -- TextField to enter a new service
        JLabel enterServicesLabel = new JLabel( getEnterServiceLabel() );
        enterServicesLabel.setVisible( true );
        enterServicesLabel.setPreferredSize( new Dimension( 200, 25 ) );
        enterServicesField = new JTextField();
        enterServicesField.setPreferredSize( new Dimension( 350, 25 ) );
        enterServicesField.setVisible( true );
        enterServicesField.addCaretListener( this );
        enterServicesField.setText( "http://" );

        gbc.insets = new Insets( 10, 2, 2, 2 );
        ++gbc.gridy;
        addServicePanel.add( enterServicesLabel, gbc );
        gbc.insets = new Insets( 2, 2, 2, 2 );
        ++gbc.gridy;
        addServicePanel.add( enterServicesField, gbc );

        // -- RadioButtons to choose the desired version
        JLabel chooseVersionLabel = new JLabel( getVersionChooserLabel() );
        chooseVersionLabel.setVisible( true );
        chooseVersionLabel.setPreferredSize( new Dimension( 200, 25 ) );

        gbc.insets = new Insets( 10, 2, 2, 2 );
        ++gbc.gridy;
        addServicePanel.add( chooseVersionLabel, gbc );

        versionBG = new ButtonGroup();
        JRadioButton versionHighestButton = new JRadioButton( getHighestVersionLabel() );
        versionHighestButton.setVisible( true );
        versionHighestButton.setSelected( true );
        versionBG.add( versionHighestButton );

        gbc.insets = new Insets( 2, 2, 2, 2 );
        gbc.gridwidth = 1;
        ++gbc.gridy;

        JPanel versionRadioBtsPanel = new JPanel();
        GridBagConstraints versionsGbc = SwingUtils.initPanel( versionRadioBtsPanel );
        versionRadioBtsPanel.add( versionHighestButton, versionsGbc );
        for ( int i = 0; i < versionArray.length; i++ ) {
            String version = versionArray[i].trim();
            JRadioButton versionButton = new JRadioButton( version );
            versionButton.setActionCommand( version );
            versionButton.setVisible( true );
            versionBG.add( versionButton );
            ++versionsGbc.gridx;
            versionRadioBtsPanel.add( versionButton, versionsGbc );
        }

        addServicePanel.add( versionRadioBtsPanel, gbc );

        if ( this instanceof AddWMSWizard ) {
            ++gbc.gridy;
            cbSwapAxis = new JCheckBox( Messages.getMessage( getLocale(), "$MD11828" ) );
            addServicePanel.add( cbSwapAxis, gbc );            
        }

        addServicePanel.setVisible( true );
        return addServicePanel;
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
                handleNextButtonAction();
            }
        } else if ( event.getSource() instanceof JComboBox ) {
            JComboBox srcComboBox = (JComboBox) event.getSource();
            String serviceName = (String) srcComboBox.getSelectedItem();
            String serviceUrl = knownServices.get( serviceName );
            if ( enterServicesField != null ) {
                enterServicesField.setText( serviceUrl );
            }
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // CaretListener
    // /////////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
     */
    public void caretUpdate( CaretEvent event ) {
        JTextField src = (JTextField) event.getSource();
        if ( buttonPanel != null ) {
            if ( src.getText() != null && src.getText().length() > 0 ) {
                buttonPanel.setButtonEnabled( ButtonPanel.NEXT_BT, true );
            } else {
                buttonPanel.setButtonEnabled( ButtonPanel.NEXT_BT, false );
            }
        }
    }

    // all required labels to make this general dialog specific to one service

    /**
     * @return the text shown in the header of this wizard dialog
     */
    protected abstract String getInfoText();

    /**
     * @return the title of the combo box of known services
     */
    protected abstract String getKnownServicesLabel();

    /**
     * @return the label of the default entry in the combo box
     */
    protected abstract String getComboBoxDefaultLabel();

    /**
     * @return the title of the text field to enter new services
     */
    protected abstract String getEnterServiceLabel();

    /**
     * @return the title of the radio buttons to choose the desired version of the service
     */
    protected abstract String getVersionChooserLabel();

    /**
     * @return the entry to indicate the highest implemented version
     */
    protected abstract String getHighestVersionLabel();

    /**
     * @return the key of the init parameters for the version numbers
     */
    protected abstract String getVersionInitParamKey();

    /**
     * @return the key of the init parameters for the known services
     */
    protected abstract String getKnownServicesInitParamKey();

    /**
     * called if the user pressed the next button
     */
    protected abstract void handleNextButtonAction();

}
