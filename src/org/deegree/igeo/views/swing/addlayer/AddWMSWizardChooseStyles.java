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

import static org.deegree.igeo.i18n.Messages.get;
import static org.deegree.igeo.views.swing.util.GuiUtils.addToFrontListener;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.values.TypedLiteral;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.ImageUtils;
import org.deegree.framework.utils.SwingUtils;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.model.AddWMSLayerCommand;
import org.deegree.igeo.dataadapter.Adapter;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.ogcwebservices.wms.capabilities.Layer;
import org.deegree.ogcwebservices.wms.capabilities.LegendURL;
import org.deegree.ogcwebservices.wms.capabilities.Style;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.owscommon_new.DCP;
import org.deegree.owscommon_new.DomainType;
import org.deegree.owscommon_new.HTTP;
import org.deegree.owscommon_new.Operation;
import org.deegree.owscommon_new.OperationsMetadata;

/**
 * <code>JAddWMSWizardChooseStyles</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class AddWMSWizardChooseStyles extends WizardDialog implements ActionListener, FocusListener, MouseListener,
                                                          ChangeListener {

    private static final long serialVersionUID = -2799571066510285487L;

    private static final ILogger LOG = LoggerFactory.getLogger( AddWMSWizardChooseStyles.class );

    private MapModel mapModel;

    private WMSCapabilities wmsCaps;

    private Map<Layer, String> selectedLayers;

    private JLabel previewImage = new JLabel();

    private JCheckBox preview = new JCheckBox();

    private JComboBox lastSelected = null;

    private String layerTitle;

    /**
     * @param frame
     *            the dialog which was visible before
     * @param mapModel
     *            the map model to add the new layer
     * @param appContainer
     *            the application container
     * @param wmsCapabilities
     *            the capablilties of the service to add as wms-datasource
     * @param selectedLayers
     *            the wms-layers selected to add
     * @param layerTitle
     *            the title of the layer
     * @param capabilitiesURL
     *            the capabilities url of the service to add as wms-datasource
     */
    public AddWMSWizardChooseStyles( JFrame frame, MapModel mapModel, ApplicationContainer<Container> appContainer,
                                     WMSCapabilities wmsCapabilities, List<Layer> selectedLayers, String layerTitle) {
        super( frame );
        this.mapModel = mapModel;
        this.appContainer = appContainer;
        this.wmsCaps = wmsCapabilities;
        this.layerTitle = layerTitle;
        
        this.setSize( 750, 600 );
        this.setResizable( false );

        // initialise a new assoziated array containing all selected layers and assigned styles
        this.selectedLayers = new LinkedHashMap<Layer, String>( selectedLayers.size() );
        for ( Layer layer : selectedLayers ) {
            this.selectedLayers.put( layer, "" );
        }

        this.setTitle( get( "$MD10055" ) );
        infoPanel.setInfoText( get( "$MD10056" ) );

        buttonPanel.registerActionListener( this );
        super.init();
    }

    /**
     * update the entry in the map containing the map with assigned styles and show preview of the style
     * 
     * @param srcComboBox
     *            the comboBox whose style selection changed
     */
    private void setSelected( JComboBox srcComboBox ) {
        String styleName = (String) srcComboBox.getSelectedItem();
        Container p = srcComboBox.getParent();
        String layerName = null;
        for ( int i = 0; i < p.getComponentCount(); i++ ) {
            p.setBackground( Color.LIGHT_GRAY );
            if ( p.getComponent( i ) instanceof JLabel ) {
                layerName = ( (JLabel) p.getComponent( i ) ).getName();
            }
        }

        Layer selectedLayer = wmsCaps.getLayer( layerName );

        if ( styleName.equals( "default" ) ) {
            this.selectedLayers.put( selectedLayer, "" );
        } else {
            this.selectedLayers.put( selectedLayer, styleName );
        }

        if ( this.preview.isSelected() ) {
            showStyle( selectedLayer, styleName );
        } else {
            this.previewImage.setIcon( null );
        }
    }

    /**
     * update preview of the styles assigned to the selected style of the layer
     * 
     * @param selectedLayer
     *            the selected layer
     * @param styleName
     *            the selected style
     */
    private void showStyle( Layer selectedLayer, String styleName ) {
        Style style = selectedLayer.getStyleResource( styleName );

        ImageIcon ii = null;
        // get legend out of the legendurl of the style
        if ( style != null ) {
            LegendURL[] legendURLs = style.getLegendURL();
            ii = getLegendImage( legendURLs[0].getOnlineResource() );
        }

        if ( ii == null ) {
            // request legend from the wms, if the layer does not define a style
            ii = getLegendFromWMS( selectedLayer.getName(), styleName );
            if ( ii == null ) {
                // if no legend is available show missing legend image
                URL missingLegendURL = Adapter.class.getResource( "missingLegend.png" );
                try {
                    LOG.logDebug( "No legend available, show missing legend image" );
                    ii = new ImageIcon( ImageUtils.loadImage( missingLegendURL.openStream() ) );
                } catch ( IOException e ) {
                    LOG.logError( get( "$DG10062", missingLegendURL, e.getMessage() ) );
                    DialogFactory.openErrorDialog( appContainer.getViewPlatform(), this, e.getMessage(),
                                                   getClass().getSimpleName(), e );
                    this.dispose();
                }
            }
        }
        if ( ii != null ) {
            this.previewImage.setIcon( ii );
        }

    }

    /**
     * @param layer
     *            the layer name
     * @param style
     *            the style name
     * @param format
     *            the format
     * @param version
     *            the version
     * @param url
     *            the wms url
     * @return the GetLegendRequest as string
     */
    private String createGetLegendRequest( String layer, String style, String format, String version, String url ) {

        StringBuffer sb = new StringBuffer( 500 );
        sb.append( url ).append( '?' );
        sb.append( "&VERSION=" ).append( version );
        sb.append( "&REQUEST=GetLegendGraphic" );
        sb.append( "&FORMAT=" ).append( format );
        sb.append( "&WIDTH=15&HEIGHT=15&EXCEPTIONS=application/vnd.ogc.se_inimage" );
        sb.append( "&LAYER=" ).append( layer );
        sb.append( "&STYLE=" ).append( style );

        return sb.toString();
    }

    /**
     * @param url
     *            the url to load the image
     * @return theIimageIcon of the url; returns null, if an exception is thrown
     */
    private ImageIcon getLegendImage( URL url ) {
        try {
            LOG.logDebug( "Try to get legend from URL: " + url.toExternalForm() );
            ImageIcon ii = new ImageIcon( ImageUtils.loadImage( url.openStream() ) );
            return ii;
        } catch ( Exception e ) {
            LOG.logError( get( "$DG10038", url, e.getMessage() ) );
            return null;
        }
    }

    /**
     * @param layer
     *            the name of the layer
     * @param styleName
     *            the name of the style
     * @return the legend out of a wms GetLegendRequest
     */
    private ImageIcon getLegendFromWMS( String layer, String styleName ) {
        OperationsMetadata om = wmsCaps.getOperationMetadata();
        Operation op = om.getOperation( new QualifiedName( "GetLegendGraphic" ) );
        if ( op != null ) {
            URL legendGraphicURL = null;
            List<DCP> dcps = op.getDCP();
            for ( DCP dcp : dcps ) {
                if ( dcp instanceof HTTP ) {
                    List<URL> getOnlineResources = ( (HTTP) dcp ).getGetOnlineResources();
                    legendGraphicURL = getOnlineResources.get( 0 );
                }
            }
            if ( legendGraphicURL != null ) {
                DomainType parameter = (DomainType) op.getParameter( new QualifiedName( "Format" ) );
                List<TypedLiteral> values = parameter.getValues();
                String format = values.get( 0 ).getValue();
                String s = createGetLegendRequest( layer, styleName, format, wmsCaps.getVersion(),
                                                   legendGraphicURL.toString() );

                URL legendFromWMS = null;
                try {
                    LOG.logDebug( "Try to get legend from WMS: " + s );
                    legendFromWMS = new URL( s );
                } catch ( MalformedURLException e ) {
                    LOG.logError( get( "$DG10049" ) );
                    return null;
                }

                try {
                    return new ImageIcon( ImageUtils.loadImage( legendFromWMS ) );
                } catch ( IOException e ) {
                    LOG.logError( get( "$DG10049" ) );
                }
            }
        }
        return null;
    }

    /**
     * @param layer
     *            the layer to display informations on the panel
     * @return a panel containing the title of the layer and assigned styöles in a combo box
     */
    private JPanel getLayerStylePanel( Layer layer ) {
        JPanel layerStylePanel = new JPanel();

        layerStylePanel.setLayout( null );
        layerStylePanel.setVisible( true );
        layerStylePanel.setSize( new Dimension( 500, 26 ) );
        layerStylePanel.setBackground( Color.WHITE );

        JLabel layerTitle = new JLabel( layer.getTitle() );
        layerTitle.setBounds( 2, 0, 248, 26 );
        layerTitle.setToolTipText( layer.getAbstract() );
        layerTitle.setName( layer.getName() );
        layerTitle.addMouseListener( this );

        JComboBox styleComboBox = new JComboBox();
        styleComboBox.setBounds( 250, 0, 150, 26 );
        styleComboBox.addItem( "default" );

        Style[] styles = layer.getStyles();
        for ( int i = 0; i < styles.length; i++ ) {
            if ( !styles[i].getName().equals( "default" ) ) {
                styleComboBox.addItem( styles[i].getName() );
            }
        }

        layerStylePanel.add( layerTitle );
        layerStylePanel.add( styleComboBox );
        styleComboBox.addActionListener( this );
        styleComboBox.addFocusListener( this );
        return layerStylePanel;
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
        JPanel stylePanel = new JPanel();
        GridBagConstraints gbc = SwingUtils.initPanel( stylePanel );

        JPanel layerStylesPanel = new JPanel();
        int entryHeight = 30;

        layerStylesPanel.setBackground( Color.WHITE );
        Dimension lsDim = new Dimension( 400, ( entryHeight * selectedLayers.size() ) + 2 );
        layerStylesPanel.setSize( lsDim );
        layerStylesPanel.setPreferredSize( lsDim );
        layerStylesPanel.setMinimumSize( lsDim );
        layerStylesPanel.setMaximumSize( lsDim );

        Dimension scrollDim = new Dimension( 420, 320 );
        JScrollPane layerStylesScroll = new JScrollPane( layerStylesPanel );
        layerStylesScroll.setSize( scrollDim );
        layerStylesScroll.setPreferredSize( scrollDim );
        layerStylesScroll.setMinimumSize( scrollDim );
        layerStylesScroll.setMaximumSize( scrollDim );
        layerStylesScroll.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );
        layerStylesScroll.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );

        // add selected layers with styles to the scrollPane
        int y = 2;
        layerStylesPanel.setLayout( null );
        for ( Layer layer : this.selectedLayers.keySet() ) {
            JPanel layerStylesEntry = getLayerStylePanel( layer );
            layerStylesEntry.setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
            layerStylesEntry.setLocation( 0, y );
            layerStylesPanel.add( layerStylesEntry );
            y = y + entryHeight;
        }

        // area to show styles
        JPanel p = new JPanel();
        GridBagConstraints pgbc = SwingUtils.initPanel( p );
        p.add( this.previewImage, pgbc );
        Dimension previewDim = new Dimension( 200, 320 );
        JScrollPane previewScroll = new JScrollPane( p );
        previewScroll.setSize( previewDim );
        previewScroll.setPreferredSize( previewDim );
        previewScroll.setMinimumSize( previewDim );
        previewScroll.setMaximumSize( previewDim );
        previewScroll.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        previewScroll.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED );

        this.preview.setText( get( "$MD10101" ) );
        this.preview.setSelected( false );
        this.preview.addChangeListener( this );

        gbc.anchor = GridBagConstraints.LINE_START;
        stylePanel.add( new JLabel( get( "$MD10057" ) ), gbc );
        ++gbc.gridx;
        gbc.insets = new Insets( 2, 20, 2, 2 );
        stylePanel.add( this.preview, gbc );
        gbc.insets = new Insets( 2, 2, 2, 2 );
        gbc.gridx = 0;
        ++gbc.gridy;
        stylePanel.add( layerStylesScroll, gbc );
        ++gbc.gridx;
        gbc.insets = new Insets( 2, 20, 2, 2 );
        stylePanel.add( previewScroll, gbc );

        return stylePanel;
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
                // set the previousframe visible and close this frame
                if ( this.previousFrame != null ) {
                    this.previousFrame.setVisible( true );
                }
                this.close();
            } else if ( srcButton.getName().equals( ButtonPanel.NEXT_BT ) ) {
                AddWMSWizardChooseFormat nextStep = new AddWMSWizardChooseFormat( this, this.mapModel,
                                                                                  this.appContainer, this.wmsCaps,
                                                                                  this.selectedLayers, this.layerTitle );
                addToFrontListener( nextStep );
                nextStep.setLocation( this.getX(), this.getY() );
                nextStep.setVisible( true );
                this.setVisible( false );
            } else if ( srcButton.getName().equals( ButtonPanel.FINISH_BT ) ) {
                AddWMSLayerCommand addWMSCmd = new AddWMSLayerCommand( this.mapModel, this.wmsCaps,
                                                                       this.selectedLayers, this.layerTitle );
                appContainer.getCommandProcessor().executeASychronously( addWMSCmd );
                this.dispose();
            }

        } else if ( event.getSource() instanceof JComboBox ) {
            JComboBox srcComboBox = (JComboBox) event.getSource();
            setSelected( srcComboBox );
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // FocusListener
    // //////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    public void focusGained( FocusEvent event ) {
        // select the line of the combobox
        JComboBox srcComboBox = (JComboBox) event.getSource();
        // deselect all other entries
        Container c = srcComboBox.getParent().getParent();
        for ( int i = 0; i < c.getComponentCount(); i++ ) {
            c.getComponent( i ).setBackground( Color.WHITE );
        }
        // and select the current
        setSelected( srcComboBox );
        lastSelected = srcComboBox;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    public void focusLost( FocusEvent event ) {
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // MouseListener
    // //////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked( MouseEvent event ) {
        // set focus to style combo box of the selected layer
        JComponent srcComponent = (JComponent) event.getSource();
        Container p = srcComponent.getParent();
        if ( srcComponent instanceof JLabel ) {
            for ( int i = 0; i < p.getComponentCount(); i++ ) {
                if ( p.getComponent( i ) instanceof JComboBox ) {
                    p.getComponent( i ).requestFocus();
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered( MouseEvent event ) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited( MouseEvent event ) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed( MouseEvent event ) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased( MouseEvent event ) {
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // ChangeListener
    // //////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged( ChangeEvent event ) {
        // set focus to last selected layer/style if user changed status of preview
        if ( lastSelected != null ) {
            lastSelected.requestFocus();
        }
    }

}
