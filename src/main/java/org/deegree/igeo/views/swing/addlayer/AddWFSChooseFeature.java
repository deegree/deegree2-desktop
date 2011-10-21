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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.utils.SwingUtils;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.model.AddWFSLayerCommand;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.kernel.CommandProcessedEvent;
import org.deegree.kernel.CommandProcessedListener;
import org.deegree.kernel.ProcessMonitor;
import org.deegree.kernel.ProcessMonitorFactory;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.wfs.XMLFactory;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSFeatureType;
import org.w3c.dom.Node;

/**
 * <code>JAddWFSChooseFeature</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class AddWFSChooseFeature extends WizardDialog implements ActionListener, ItemListener {

    private static final long serialVersionUID = -3149417521102716483L;

    private static final ILogger LOG = LoggerFactory.getLogger( AddWFSChooseFeature.class );

    private static final NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    private MapModel mapModel;

    private WFSCapabilities wfsCapabilities;

    private JComboBox featureTypeChooser = new JComboBox();

    private JComboBox geomPropChooser = new JComboBox();

    private JTextArea descriptionArea = new JTextArea();

    /**
     * 
     * @param frame
     *            the previous frame
     * @param mapModel
     *            tha map model to add the new layer
     * @param appContainer
     *            the application container
    * @param wfsCapabilities
     *            the requested capabilities
     */
    public AddWFSChooseFeature( JFrame frame, MapModel mapModel, ApplicationContainer<Container> appContainer,
                                WFSCapabilities wfsCapabilities ) {
        super( frame );
        this.wfsCapabilities = wfsCapabilities;
        this.mapModel = mapModel;
        this.appContainer = appContainer;

        this.setSize( 500, 600 );
        this.setResizable( false );
        this.setTitle( Messages.getMessage( Locale.getDefault(), "$MD10128" ) );
        infoPanel.setInfoText( Messages.getMessage( Locale.getDefault(), "$MD10129" ) );

        buttonPanel.registerActionListener( this );

        this.geomPropChooser.setRenderer( new QualifiedNameRenderer() );
        this.geomPropChooser.setPreferredSize( new Dimension( 100, 20 ) );
        initFeatureTypeChooser();
        setFeatureTypeDescription();

        super.init();
    }

    /**
     * fills the combo box with all featureTypes of the requested WFS
     */
    private void initFeatureTypeChooser() {
        // add an item listener to update the featureType description
        this.featureTypeChooser.addItemListener( this );
        this.featureTypeChooser.setPreferredSize( new Dimension( 200, 20 ) );
        this.featureTypeChooser.setRenderer( new FeatureTypeRenderer() );
        WFSFeatureType[] featureTypes = this.wfsCapabilities.getFeatureTypeList().getFeatureTypes();
        featureTypes = sort( featureTypes );
        for ( int i = 0; i < featureTypes.length; i++ ) {
            this.featureTypeChooser.addItem( featureTypes[i] );
        }
    }

    /**
     * @param featureTypes
     * @return
     */
    private WFSFeatureType[] sort( WFSFeatureType[] featureTypes ) {
        WFSFeatureType temp;

        for ( int i = 0; i < featureTypes.length; i++ ) {
            for ( int j = 0; j < featureTypes.length - 1; j++ ) {
                if ( featureTypes[j + 1].getTitle().compareTo( featureTypes[j].getTitle() ) < 0 ) {
                    temp = featureTypes[j];
                    featureTypes[j] = featureTypes[j + 1];
                    featureTypes[j + 1] = temp;
                }
            }
        }

        return featureTypes;

    }

    /**
     * updates the comboBox with the list of geometrie properties
     * 
     * @param geomProps
     *            the geometrie properties to insert in the comboBox
     */
    private void updateGeomPropsChooser( List<QualifiedName> geomProps ) {
        this.geomPropChooser.removeAllItems();
        this.geomPropChooser.validate();
        if ( geomProps.size() > 0 ) {
            this.geomPropChooser.setEnabled( true );
            for ( QualifiedName name : geomProps ) {
                this.geomPropChooser.addItem( name );
            }
        } else {
            this.geomPropChooser.setEnabled( false );
        }

    }

    /**
     * @param text
     *            the text to write in the descriptionArea
     */
    private void updateDescriptionArea( String text ) {
        this.descriptionArea.setText( text );
    }

    /**
     * updates all components depends of the description of the selected featureType
     * 
     * @throws XMLParsingException
     */
    private void setFeatureTypeDescription() {
        // the selected feature type
        WFSFeatureType featureType = (WFSFeatureType) this.featureTypeChooser.getSelectedItem();

        FeatureTypeWrapper ftWrapper = null;
        String wfsUrl = null;
        try {
            XMLFragment xml = XMLFactory.export( wfsCapabilities );
            Node n = XMLTools.getNode(
                                       xml.getRootElement(),
                                       "ows:OperationsMetadata/ows:Operation[@name='DescribeFeatureType']/ows:DCP/ows:HTTP/ows:Get",
                                       nsContext );
            wfsUrl = XMLTools.getAttrValue( n, CommonNamespaces.XLNNS, "href", null );
            int index = wfsUrl.indexOf( '?' );
            if ( index > -1 ) {
                wfsUrl = wfsUrl.substring( 0, index );
            }
            ftWrapper = new FeatureTypeWrapper( new URL( wfsUrl ), wfsCapabilities, featureType.getName(), appContainer );
        } catch ( MalformedURLException e ) {
            // should never happen
            this.dispose();
            LOG.logError( Messages.get( "$DG10068", wfsUrl, e.getMessage() ) );
            DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, getClass().getSimpleName(),
                                           Messages.get( "$DG10013", wfsUrl ), e );
        } catch ( IOException e ) {
            LOG.logError( Messages.get( "$DG10013", wfsUrl ), e );
            this.dispose();
            DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, getClass().getSimpleName(),
                                           Messages.get( "$DG10013", wfsUrl ), e );
        } catch ( XMLParsingException e ) {
            LOG.logError( Messages.get( "$DG10013", wfsUrl ), e );
            this.dispose();
            DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, getClass().getSimpleName(),
                                           Messages.get( "$DG10013", wfsUrl ), e );
        } catch ( Exception e ) {
            LOG.logError( Messages.get( "$DG10013", wfsUrl ), e );
            this.dispose();
            DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, getClass().getSimpleName(),
                                           Messages.get( "$DG10013", wfsUrl ), e );
        }

        try {
            if ( ftWrapper != null ) {
                // update description area
                updateDescriptionArea( ftWrapper.getFeatureTypeDescriptionAsXML().getAsPrettyString() );
                // and the combobox with geometries of the selected featuretype
                updateGeomPropsChooser( ftWrapper.getGeometryProperties() );
            }
        } catch ( Exception e ) {
            LOG.logError( Messages.get( "$DG10101", wfsUrl ), e );
            this.dispose();
            DialogFactory.openErrorDialog(
                                           appContainer.getViewPlatform(),
                                           null,
                                           getClass().getSimpleName(),
                                           Messages.get( "$DG10101", wfsUrl,
                                                         ftWrapper.getFeatureTypeDescriptionAsXML().getAsPrettyString() ),
                                           e );
        }
    }

    /**
     * 
     * The <code>FeatureTypeRenderer</code> shows a WFSFeatureType as an entry in a JComboBox.
     * 
     * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     * 
     */
    private class FeatureTypeRenderer extends JLabel implements ListCellRenderer {

        private static final long serialVersionUID = -7255425959231493183L;

        public FeatureTypeRenderer() {
            setPreferredSize( new Dimension( 300, 18 ) );
            setBorder( BorderFactory.createEmptyBorder( 0, 2, 0, 0 ) );
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int,
         * boolean, boolean)
         */
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected,
                                                       boolean cellHasFocus ) {
            WFSFeatureType ft = (WFSFeatureType) value;
            String displayName = ft.getTitle();
            if ( displayName == null || !( displayName.length() > 0 ) ) {
                displayName = ft.getName().getLocalName();
            }
            setText( displayName );
            setToolTipText( ft.getAbstract() );
            return this;
        }

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
        JPanel chooseFeaturePanel = new JPanel();
        chooseFeaturePanel.setVisible( true );
        GridBagConstraints gbc = SwingUtils.initPanel( chooseFeaturePanel );

        // show the decription of the selected feature type
        Dimension descDim = new Dimension( 400, 225 );
        JScrollPane descriptionScroll = new JScrollPane( this.descriptionArea );
        descriptionScroll.setPreferredSize( descDim );
        descriptionScroll.setMinimumSize( descDim );
        descriptionScroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
        descriptionScroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );

        // add components to the main panel
        gbc.anchor = GridBagConstraints.LINE_START;

        chooseFeaturePanel.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10131" ) ), gbc );
        ++gbc.gridy;
        chooseFeaturePanel.add( this.featureTypeChooser, gbc );
        ++gbc.gridy;
        gbc.insets = new Insets( 5, 2, 2, 2 );
        chooseFeaturePanel.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10132" ) ), gbc );
        ++gbc.gridy;
        gbc.insets = new Insets( 2, 2, 2, 2 );
        chooseFeaturePanel.add( this.geomPropChooser, gbc );
        ++gbc.gridy;
        gbc.insets = new Insets( 10, 2, 2, 2 );
        chooseFeaturePanel.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10130" ) ), gbc );
        ++gbc.gridy;
        gbc.insets = new Insets( 2, 2, 2, 2 );
        chooseFeaturePanel.add( descriptionScroll, gbc );

        return chooseFeaturePanel;
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
                WFSFeatureType featureType = (WFSFeatureType) this.featureTypeChooser.getSelectedItem();
                QualifiedName geometryProp = (QualifiedName) this.geomPropChooser.getSelectedItem();
                AddWFSCreateFilter nextStep = null;
                try {
                    nextStep = new AddWFSCreateFilter( this, this.mapModel, this.appContainer, this.wfsCapabilities,
                                                       featureType, geometryProp );
                } catch ( Exception e ) {
                    return;
                }
                nextStep.setLocation( this.getX(), this.getY() );
                nextStep.setVisible( true );
                this.setVisible( false );
            } else if ( srcButton.getName().equals( ButtonPanel.FINISH_BT ) ) {
                // adds the new layer and closes the current window
                WFSFeatureType featureType = (WFSFeatureType) this.featureTypeChooser.getSelectedItem();
                QualifiedName geometryProp = (QualifiedName) this.geomPropChooser.getSelectedItem();
                AddWFSLayerCommand command = new AddWFSLayerCommand( this.mapModel, this.wfsCapabilities, featureType,
                                                                     geometryProp, null );

                final ProcessMonitor pm = ProcessMonitorFactory.createDialogProcessMonitor(
                                                                                            appContainer.getViewPlatform(),
                                                                                            Messages.get( "$MD11270" ),
                                                                                            Messages.get(
                                                                                                          "$MD11271",
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

    // /////////////////////////////////////////////////////////////////////////////////
    // ItemListener
    // /////////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    public void itemStateChanged( ItemEvent event ) {
        // update the decription of the selected feature type, when state of the check box changed
        // or selected fetaure type changed. Methode will be called two timed when selection of the
        // combo box changed, so avoid not required multiple updates of the description area.
        if ( !( event.getSource() instanceof JComboBox && event.getStateChange() == ItemEvent.DESELECTED ) ) {
            try {
                setFeatureTypeDescription();
            } catch ( Exception e ) {
                DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, e.getMessage(),
                                               getClass().getSimpleName(), e );
                this.dispose();
            }
        }

    }

}
