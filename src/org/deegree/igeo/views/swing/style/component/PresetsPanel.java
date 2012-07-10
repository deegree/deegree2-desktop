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

package org.deegree.igeo.views.swing.style.component;

import static org.deegree.igeo.i18n.Messages.get;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.igeo.settings.GraphicOptions;
import org.deegree.igeo.style.model.Preset;
import org.deegree.igeo.style.model.Preset.PRESETTYPE;
import org.deegree.igeo.views.swing.style.StyleDialogUtils;
import org.deegree.igeo.views.swing.style.VisualPropertyPanel;
import org.deegree.igeo.views.swing.style.renderer.PresetRenderer;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>PresetsPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class PresetsPanel extends JPanel {

    private static final long serialVersionUID = 4824564032207322808L;

    private static final ILogger LOG = LoggerFactory.getLogger( PresetsPanel.class );

    private VisualPropertyPanel assignedVisualPropPanel;

    private PRESETTYPE type;

    private JTextField nameTF;

    private DefaultListModel dlm;

    private JList availableSettingsList;

    private GraphicOptions go;

    private boolean isPopupTriggered = false;

    public PresetsPanel( VisualPropertyPanel assignedVisualPropPanel, PRESETTYPE type ) {
        this.assignedVisualPropPanel = assignedVisualPropPanel;
        this.type = type;
        go = assignedVisualPropPanel.getOwner().getSettings().getGraphicOptions();
        init();

    }

    private void init() {
        // init
        nameTF = new JTextField();
        nameTF.setToolTipText( get( "$MD10920" ) );

        JButton saveBt = new JButton( get( "$MD10859" ) );
        saveBt.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( nameTF.getText() != null && nameTF.getText().length() > 0 ) {
                    Symbolizer symbolizer = assignedVisualPropPanel.getPresetSymbolizer();
                    try {
                        go.addSymbolizerPreset( nameTF.getText(), symbolizer );
                        updatePresetsCB();
                        nameTF.setText( "" );
                    } catch ( Exception e1 ) {
                        LOG.logError( "can not save preset", e1 );
                        JOptionPane.showMessageDialog( PresetsPanel.this, get( "$MD10840" ), get( "$DI10017" ),
                                                       JOptionPane.ERROR_MESSAGE );
                    }
                } else {
                    JOptionPane.showMessageDialog( PresetsPanel.this, get( "$MD10865" ), get( "$MD10866" ),
                                                   JOptionPane.INFORMATION_MESSAGE );
                }
            }
        } );

        dlm = new DefaultListModel();
        availableSettingsList = new JList();
        availableSettingsList.setModel( dlm );
        availableSettingsList.setToolTipText( get( "$MD10860" ) );
        availableSettingsList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        availableSettingsList.addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                if ( !isPopupTriggered ) {
                    applyPreset();
                }
            }
        } );

        availableSettingsList.addMouseListener( new PopupListener() );
        availableSettingsList.setCellRenderer( new PresetRenderer() );
        updatePresetsCB();

        JScrollPane availableSettingsSP = new JScrollPane( availableSettingsList );
        availableSettingsSP.setPreferredSize( new Dimension( 100, 300 ) );

        // layout
        FormLayout fl = new FormLayout(
                                        "left:$rgap, left:pref, left:$rgap, right:[80dlu,pref]:grow(0.4), 5dlu, center:default:grow(0.6)",
                                        "5dlu, $sepheight, $sepheight, $cpheight, $btheight, pref:grow(1)" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        builder.setBorder( StyleDialogUtils.createStyleAttributeBorder( get( "$MD10861" ) ) );

        CellConstraints cc = new CellConstraints();
        builder.addSeparator( get( "$MD10862" ), cc.xyw( 1, 3, 4 ) );
        builder.addLabel( get( "$MD10863" ), cc.xy( 2, 4 ) );
        builder.add( nameTF, cc.xy( 4, 4, CellConstraints.FILL, CellConstraints.CENTER ) );
        builder.add( saveBt, cc.xyw( 2, 5, 3, CellConstraints.CENTER, CellConstraints.CENTER ) );

        builder.addLabel( get( "$MD10864" ), cc.xy( 6, 2 ) );
        builder.add( availableSettingsSP, cc.xywh( 6, 3, 1, 4 ) );

        add( builder.getPanel() );

    }

    // add defined presets to the list
    private void updatePresetsCB() {
        try {
            dlm.removeAllElements();
            Map<String, Preset> symbolizers = go.getSymbolizerPresets();
            for ( String presetName : symbolizers.keySet() ) {
                Preset preset = symbolizers.get( presetName );
                if ( type.equals( preset.getType() ) ) {
                    dlm.addElement( preset );
                }
            }
        } catch ( Exception e ) {
            LOG.logError( "can not get defined presets!", e );
            JOptionPane.showMessageDialog( this, get( "$MD10839" ), get( "$DI10017" ), JOptionPane.ERROR_MESSAGE );
        }
    }

    private void applyPreset() {
        try {
            if ( availableSettingsList.getSelectedValue() != null ) {
                Preset preset = go.getSymbolizerPresets().get(
                                                               ( (Preset) availableSettingsList.getSelectedValue() ).getName() );
                try {
                    assignedVisualPropPanel.setSymbolizer( preset.getSymbolizer() );
                } catch ( Exception e1 ) {
                    LOG.logError( "An error occured when trying to set the preset!", e1 );
                    JOptionPane.showMessageDialog( PresetsPanel.this, get( "$MD10794" ), get( "$DI10017" ),
                                                   JOptionPane.ERROR_MESSAGE );
                }
            }
        } catch ( Exception e2 ) {
            LOG.logError( "can not get defined presets!", e2 );
            JOptionPane.showMessageDialog( this, get( "$MD10838" ), get( "$DI10017" ), JOptionPane.ERROR_MESSAGE );
        }
    }

    // //////////////////////////////////////////////////////////////////////////////
    // INNER CLASS
    // //////////////////////////////////////////////////////////////////////////////

    private class PopupListener extends MouseAdapter {

        private JPopupMenu popupMenu;

        public void mousePressed( MouseEvent e ) {
            maybeShowPopup( e );
        }

        public void mouseReleased( MouseEvent e ) {
            maybeShowPopup( e );
        }

        private void maybeShowPopup( MouseEvent e ) {
            if ( e.isPopupTrigger() ) {
                if ( popupMenu == null ) {
                    popupMenu = new JPopupMenu();
                    JMenuItem applyLabel = new JMenuItem( get( "$MD11170" ) );
                    applyLabel.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            applyPreset();
                        }
                    } );
                    popupMenu.add( applyLabel );

                    JMenuItem removeLabel = new JMenuItem( get( "$MD11171" ) );
                    removeLabel.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            if ( availableSettingsList.getSelectedValue() != null ) {
                                Preset preset = (Preset) availableSettingsList.getSelectedValue();
                                int result = JOptionPane.showOptionDialog( PresetsPanel.this, get( "$MD11173",
                                                                                                   preset.getName() ),
                                                                           get( "$MD11172" ),
                                                                           JOptionPane.OK_CANCEL_OPTION,
                                                                           JOptionPane.QUESTION_MESSAGE, null, null,
                                                                           null );
                                if ( result == JOptionPane.OK_OPTION ) {
                                    try {
                                        go.removeSymbolizerPreset( preset.getName() );
                                        updatePresetsCB();
                                    } catch ( Exception e1 ) {
                                        LOG.logError( "can not remove presets!", e1 );
                                        JOptionPane.showMessageDialog( PresetsPanel.this, get( "$MD11174" ),
                                                                       get( "$DI10017" ), JOptionPane.ERROR_MESSAGE );
                                    }
                                }
                            }
                        }
                    } );
                    popupMenu.add( removeLabel );
                }

                int selectedIndex = availableSettingsList.locationToIndex( e.getPoint() );
                if ( selectedIndex > -1 ) {
                    isPopupTriggered = true;
                    availableSettingsList.setSelectedIndex( selectedIndex );
                    isPopupTriggered = false;
                    popupMenu.show( e.getComponent(), e.getX(), e.getY() );
                }
            }
        }

    }
}
