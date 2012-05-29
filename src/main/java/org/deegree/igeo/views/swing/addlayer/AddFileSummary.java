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
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.text.TextAction;

import org.deegree.crs.coordinatesystems.CoordinateSystem;
import org.deegree.framework.utils.CRSUtils;
import org.deegree.framework.utils.SwingUtils;
import org.deegree.igeo.commands.model.AddFileLayerCommand;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.views.swing.AutoCompleteComboBox;
import org.deegree.igeo.views.swing.ScaleDenominatorPanel;
import org.deegree.igeo.views.swing.util.GenericFileChooser;
import org.deegree.igeo.views.swing.util.IGeoFileFilter;
import org.deegree.igeo.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;
import org.deegree.kernel.CommandProcessedEvent;
import org.deegree.kernel.CommandProcessedListener;
import org.deegree.kernel.ProcessMonitor;
import org.deegree.kernel.ProcessMonitorFactory;

/**
 * <code>JAddFileSummary</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class AddFileSummary extends WizardDialog implements ActionListener {

    private static final long serialVersionUID = -323849501956243778L;

    private static final String SELECT_BT = "select_bt";

    private MapModel mapModel;

    private JTextField fileField;

    private JTextField serviceName = new JTextField();

    private JTextField serviceTitle = new JTextField();

    private JTextArea serviceAbstract = new JTextArea();

    private JLabel crsChooserLabel = new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10329" ) );

    private JComboBox cbCRSChooser;

    private ScaleDenominatorPanel dsScaleDenomPanel;

    private JCheckBox cbLazyLoading;

    private File file;

    private String datasourceName;

    private static String[] crsList;

    static {
        if ( crsList == null ) {
            crsList = CRSUtils.getAvailableEPSGCodesAsArray();
        }
    }

    /**
     * @param appContainer
     * @param frame
     *            the previous frame
     * @param module
     *            the module this function is assigned
     * @param mapModel
     *            the map model adapter to add the new layer
     */
    public AddFileSummary( JFrame frame, IModule<Container> module, MapModel mapModel, String datasourceName ) {
        super( frame );
        this.datasourceName = datasourceName;
        this.appContainer = module.getApplicationContainer();
        this.mapModel = mapModel;
        this.setSize( 500, 600 );
        this.setResizable( false );
        this.setTitle( Messages.getMessage( Locale.getDefault(), "$MD10321" ) );

        buttonPanel.registerActionListener( this );
        buttonPanel.setButtonEnabled( ButtonPanel.NEXT_BT, false );

        // set text of the infoPanel
        infoPanel.setInfoText( Messages.getMessage( Locale.getDefault(), "$MD10322" ) );

        super.init();
    }

    /**
     * opens the dialog to choose a file
     */
    private void chooseFile() {

        List<IGeoFileFilter> ff = null;
        if ( datasourceName.equals( AddLayerFrame.FILE_RASTER ) ) {
            ff = IGeoFileFilter.createForwellKnownFormats( appContainer, IGeoFileFilter.FILETYPE.raster );
        } else {
            ff = IGeoFileFilter.createForwellKnownFormats( appContainer, IGeoFileFilter.FILETYPE.vector );
        }
        Preferences prefs = Preferences.userNodeForPackage( AddFileSummary.class );
        file = GenericFileChooser.showOpenDialog( FILECHOOSERTYPE.geoDataFile, appContainer, this, prefs, "layerFile",
                                                  ff );

        if ( file != null ) {
            fileField.setText( this.file.getPath() );
            fileField.setCaretPosition( 0 );
            setLayerInformation( this.file.getName() );

            // Show ComboBox to choose a CRS, if file type does not store the CRS information.
            if ( !hasCRS() ) {
                this.cbCRSChooser.setVisible( true );
                this.crsChooserLabel.setVisible( true );
            } else {
                this.cbCRSChooser.setVisible( false );
                this.crsChooserLabel.setVisible( false );
            }

        }

    }

    /**
     * ALL: .wld, .wf, .worldfile TIF/TIFF: .twf, .tifw, .tiffw, .tfw BMP: .bwf, .bmpw, .bpw PNG: .pwf, .pngw, .pnw,
     * .pgw JPEG: .jwf, .jpgw, .jpegw, .jpw, .jgw, .gif</br> Even .gpx does not has no special definition format for a
     * CRS but it always will be WGS84
     * 
     * 
     * @return true if file has CRS information, false otherwise
     */
    private boolean hasCRS() {
        String fileName = this.file.getName();

        if ( fileName.endsWith( ".shp" ) || fileName.endsWith( ".jpg" ) || fileName.endsWith( ".jpeg" )
             || fileName.endsWith( ".bmp" ) || fileName.endsWith( "tif" ) || fileName.endsWith( "tiff" )
             || fileName.endsWith( ".png" ) || fileName.endsWith( ".gif" ) ) {
            return false;
        }
        return true;
    }

    /**
     * inserts the layer name
     * 
     * @param name
     *            the name to insert as service name
     */
    private void setLayerInformation( String name ) {
        int index = name.lastIndexOf( '.' );
        if ( index != -1 ) {
            name = name.substring( 0, index );
        }
        this.serviceTitle.setText( name );
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
        JPanel addFieldPanel = new JPanel();
        GridBagConstraints gbc = SwingUtils.initPanel( addFieldPanel );

        JPanel formPanel = new JPanel();
        GridBagConstraints formGbc = SwingUtils.initPanel( formPanel );

        Dimension textFieldDim = new Dimension( 290, 25 );
        Dimension textAreaDim = new Dimension( 290, 45 );
        Border border = BorderFactory.createEmptyBorder();

        // button to open the dialog to choose the file
        JButton selectFileBt = new JButton( Messages.getMessage( Locale.getDefault(), "$MD10323" ) );
        selectFileBt.setName( SELECT_BT );
        selectFileBt.addActionListener( this );

        // text field to display the file
        this.fileField = new JTextField();
        this.fileField.setEditable( false );
        this.fileField.setPreferredSize( new Dimension( textFieldDim ) );

        // crs combo box
        this.cbCRSChooser = new AutoCompleteComboBox( crsList );
        this.cbCRSChooser.setVisible( false );
        this.crsChooserLabel.setVisible( false );
        this.cbCRSChooser.setPreferredSize( textFieldDim );
        this.cbCRSChooser.setMaximumSize( textFieldDim );
        this.cbCRSChooser.setRenderer( new CRSComboBoxRenderer() );

        // select CRS of the mapModel
        CoordinateSystem mapModelCRS = this.mapModel.getCoordinateSystem().getCRS();
        this.cbCRSChooser.setSelectedItem( mapModelCRS.getIdentifier() );

        // information about the layer:
        this.serviceName = new JTextField( UUID.randomUUID().toString() );
        this.serviceName.setPreferredSize( textFieldDim );
        this.serviceName.setBorder( border );
        this.serviceName.setVisible( true );

        this.serviceTitle = new JTextField();
        this.serviceTitle.setPreferredSize( textFieldDim );
        this.serviceTitle.setBorder( border );
        this.serviceTitle.setVisible( true );

        this.serviceAbstract = new JTextArea();
        this.serviceAbstract.setPreferredSize( textAreaDim );
        this.serviceAbstract.setBorder( border );
        this.serviceAbstract.setVisible( true );

        // show the scale denominator panel
        this.dsScaleDenomPanel = new ScaleDenominatorPanel();
        this.cbLazyLoading = new JCheckBox( "lazy loading" );

        // enable tab on the textarea to moving the focus
        KeyStroke mTabKey = KeyStroke.getKeyStroke( KeyEvent.VK_TAB, 0 );
        KeyStroke mShiftTabKey = KeyStroke.getKeyStroke( KeyEvent.VK_TAB, 1 );

        Action mNewTabKeyAction = new TextAction( "tab" ) {

            private static final long serialVersionUID = 7184981167750183465L;

            public void actionPerformed( ActionEvent evt ) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
            }
        };

        Action mNewShiftTabKeyAction = new TextAction( "shift-tab" ) {

            private static final long serialVersionUID = -6540232704836503656L;

            public void actionPerformed( ActionEvent evt ) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent();
            }
        };
        this.serviceAbstract.setFocusTraversalKeys( KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null );
        this.serviceAbstract.setFocusTraversalKeys( KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null );

        this.serviceAbstract.getKeymap().addActionForKeyStroke( mTabKey, mNewTabKeyAction );
        this.serviceAbstract.getKeymap().addActionForKeyStroke( mShiftTabKey, mNewShiftTabKeyAction );

        formGbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add( selectFileBt, formGbc );
        ++formGbc.gridx;
        formPanel.add( this.fileField, formGbc );

        formGbc.gridx = 0;
        ++formGbc.gridy;
        formGbc.insets = new Insets( 10, 2, 2, 2 );

        formPanel.add( crsChooserLabel, formGbc );
        ++formGbc.gridx;
        formPanel.add( this.cbCRSChooser, formGbc );
        formGbc.gridx = 0;
        ++formGbc.gridy;

        formPanel.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10324" ) ), formGbc );
        formGbc.insets = new Insets( 2, 2, 2, 2 );
        formGbc.gridwidth = 1;
        ++formGbc.gridy;

        formPanel.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10325" ) ), formGbc );
        ++formGbc.gridx;
        formGbc.gridwidth = 4;
        formPanel.add( this.serviceName, formGbc );
        ++formGbc.gridy;
        formGbc.gridx = 0;
        formGbc.gridwidth = 1;

        formPanel.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10326" ) ), formGbc );
        ++formGbc.gridx;
        formGbc.gridwidth = 4;
        formPanel.add( this.serviceTitle, formGbc );
        ++formGbc.gridy;
        formGbc.gridx = 0;
        formGbc.gridwidth = 1;
        formPanel.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10327" ) ), formGbc );
        ++formGbc.gridx;
        formGbc.gridwidth = 4;
        formPanel.add( this.serviceAbstract, formGbc );
        ++formGbc.gridy;
        formGbc.gridx = 0;
        formGbc.gridwidth = 1;
        formPanel.add( new JLabel( Messages.getMessage( Locale.getDefault(), "$MD11206" ) ), formGbc );
        ++formGbc.gridx;
        formGbc.gridwidth = 4;
        formPanel.add( this.dsScaleDenomPanel, formGbc );
        ++formGbc.gridy;
        formGbc.gridx = 0;
        formGbc.gridwidth = 1;
        if ( !datasourceName.equals( AddLayerFrame.FILE_RASTER ) ) {
            formPanel.add( this.cbLazyLoading, formGbc );
        }

        addFieldPanel.add( formPanel, gbc );

        gbc.insets = new Insets( 10, 2, 2, 2 );
        ++gbc.gridy;

        return addFieldPanel;
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
                close();
            } else if ( srcButton.getName().equals( ButtonPanel.FINISH_BT ) ) {
                if ( this.file != null ) {
                    String crs = (String) this.cbCRSChooser.getSelectedItem();
                    // add file as new layer
                    AddFileLayerCommand command = new AddFileLayerCommand(
                                                                           this.mapModel,
                                                                           this.file,
                                                                           this.serviceName.getText(),
                                                                           this.serviceTitle.getText(),
                                                                           this.serviceAbstract.getText(),
                                                                           this.dsScaleDenomPanel.getMinScaleDenominator(),
                                                                           this.dsScaleDenomPanel.getMaxScaleDenominator(),
                                                                           this.cbLazyLoading.isSelected(), crs );
                    final ProcessMonitor pm = ProcessMonitorFactory.createDialogProcessMonitor( appContainer.getViewPlatform(),
                                                                                                Messages.get( "$MD11211" ),
                                                                                                Messages.get( "$MD11212",
                                                                                                              file.getName() ),
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
                } else {
                    // show message if no file is selected
                    JOptionPane.showMessageDialog( this, Messages.getMessage( Locale.getDefault(), "$MD10328" ) );
                }
            } else if ( srcButton.getName().equals( SELECT_BT ) ) {
                // opens the dialog to choose the file to add as new layer
                chooseFile();
            }
        }

    }

    private class CRSComboBoxRenderer extends BasicComboBoxRenderer {

        private static final long serialVersionUID = -5929601500484039267L;

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.plaf.basic.BasicComboBoxRenderer#getListCellRendererComponent(javax.swing .JList,
         * java.lang.Object, int, boolean, boolean)
         */
        @Override
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected,
                                                       boolean cellHasFocus ) {
            if ( isSelected ) {
                setBackground( list.getSelectionBackground() );
                setForeground( list.getSelectionForeground() );
                list.setToolTipText( ( value == null ) ? "" : value.toString() );
            } else {
                setBackground( list.getBackground() );
                setForeground( list.getForeground() );
            }
            setFont( list.getFont() );
            setText( ( value == null ) ? "" : value.toString() );
            return this;
        }

    }

}
