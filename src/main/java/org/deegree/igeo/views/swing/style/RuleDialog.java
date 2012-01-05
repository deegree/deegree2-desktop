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

package org.deegree.igeo.views.swing.style;

import static org.deegree.igeo.i18n.Messages.get;
import static org.deegree.igeo.views.DialogFactory.openErrorDialog;
import static org.deegree.igeo.views.swing.util.IconRegistry.getIcon;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.graphics.sld.AbstractLayer;
import org.deegree.graphics.sld.AbstractStyle;
import org.deegree.graphics.sld.FeatureTypeStyle;
import org.deegree.graphics.sld.NamedLayer;
import org.deegree.graphics.sld.SLDFactory;
import org.deegree.graphics.sld.StyleFactory;
import org.deegree.graphics.sld.StyledLayerDescriptor;
import org.deegree.graphics.sld.UserStyle;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.ChangeListener;
import org.deegree.igeo.commands.ChangeLayerStyleCommand;
import org.deegree.igeo.config.DirectStyleType;
import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.DefinedStyle;
import org.deegree.igeo.mapmodel.DirectStyle;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.NamedStyle;
import org.deegree.igeo.settings.Settings;
import org.deegree.igeo.style.LayerCache;
import org.deegree.igeo.style.perform.SldIO;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.HelpManager;
import org.deegree.igeo.views.swing.DefaultFrame;
import org.deegree.igeo.views.swing.HelpFrame;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.igeo.views.swing.util.panels.PanelDialog;
import org.deegree.kernel.Command;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.Operation;
import org.deegree.model.filterencoding.PropertyIsCOMPOperation;
import org.deegree.model.filterencoding.PropertyName;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * The <code>RuleDialog</code> handles the definition of rules.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class RuleDialog extends DefaultFrame implements ActionListener {

    private static final long serialVersionUID = -4152608665359578717L;

    private static final ILogger LOG = LoggerFactory.getLogger( RuleDialog.class );

    private Layer layer;

    private ApplicationContainer<?> appContainer;

    private Settings settings;

    private List<RuleDefinitionPanel> ruleDefinitionPanels = new ArrayList<RuleDefinitionPanel>();

    private JTabbedPane rules;

    private JButton okBt;

    private JButton applyBt;

    private JButton cancelBt;

    private JButton helpBt;

    private JMenuBar menuBar;

    private JMenuItem addRuleItem;

    private JMenuItem removeRuleItem;

    private JMenuItem renameRuleItem;

    private JMenuItem duplicateRuleItem;

    private JMenuItem importStyleItem;

    private JMenuItem exportStyleItem;

    private JMenuItem settingsItem;

    private JMenuItem editSymbolItem;

    private SettingsPanel settingsPanel = new SettingsPanel();

    private List<ChangeListener> uomChangedListener = new ArrayList<ChangeListener>();

    private StyledLayerDescriptor importedSls = null;

    private boolean styleChanged = false;

    static {
        StyleDialogUtils.prepareFormConstants();
    }

    /**
     * default constructor
     */
    public RuleDialog() {
    }

    /**
     * Sets the layer and updates the RuleDefinitionPanels.
     * 
     * @param layer
     *            the layer
     */
    public void setLayer( Layer layer ) {
        this.layer = layer;
        LayerCache.getInstance().addLayer( layer );
        AbstractStyle abstractStyle = layer.getCurrentStyle().getStyle();
        settingsPanel.setUom( getUom() );
        setStyle( abstractStyle );
    }

    private void setStyle( AbstractStyle abstractStyle ) {
        if ( abstractStyle instanceof UserStyle ) {
            FeatureTypeStyle[] featureStyles = ( (UserStyle) abstractStyle ).getFeatureTypeStyles();
            for ( int i = 0; i < featureStyles.length; i++ ) {
                String name;
                if ( featureStyles[i].getTitle() != null ) {
                    name = featureStyles[i].getTitle();
                } else if ( featureStyles[i].getName() != null ) {
                    name = featureStyles[i].getName();
                } else {
                    name = get( "$MD10591", ruleDefinitionPanels.size() );
                }
                addRuleDefinitionPanel( name, featureStyles[i] );
            }
        }
        // no ruleDefinitionPanels defined, so create a default one
        if ( ruleDefinitionPanels.size() == 0 ) {
            String name = get( "$MD10591", 1 );
            addRuleDefinitionPanel( name, null );
        }
    }

    /**
     * 
     * @param appContainer
     */
    public void setApplicationContainer( ApplicationContainer<?> appContainer ) {
        this.appContainer = appContainer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#init(org.deegree.igeo.config.ViewFormType)
     */
    @Override
    public void init( ViewFormType viewForm )
                            throws Exception {
        super.init( viewForm );
        settings = owner.getApplicationContainer().getSettings();
        initComponents();
        initRuleDialog();
    }

    /**
     * sets the ruleDialog in the initial state
     */
    public void reset() {
        ruleDefinitionPanels.clear();
        rules.removeAll();
    }

    private void initComponents() {

        // TODO: mnemonics(!-i18n?)/shortcuts(?)
        menuBar = new JMenuBar();
        JMenu menuRules = new JMenu( get( "$MD11069" ) );

        addRuleItem = new JMenuItem( get( "$MD10592" ), getIcon( "rule_add.png" ) );
        addRuleItem.addActionListener( this );
        removeRuleItem = new JMenuItem( get( "$MD10593" ), getIcon( "rule_delete.png" ) );
        removeRuleItem.addActionListener( this );
        renameRuleItem = new JMenuItem( get( "$MD10594" ), getIcon( "rule_edit.png" ) );
        renameRuleItem.addActionListener( this );
        duplicateRuleItem = new JMenuItem( get( "$MD11605" ), getIcon( "rule_copy.png" ) );
        duplicateRuleItem.addActionListener( this );

        menuRules.add( addRuleItem );
        menuRules.add( removeRuleItem );
        menuRules.add( renameRuleItem );
        menuRules.add( duplicateRuleItem );

        JMenu menuStyles = new JMenu( get( "$MD11070" ) );

        importStyleItem = new JMenuItem( get( "$MD11067" ), getIcon( "sld_import.png" ) );
        importStyleItem.addActionListener( this );
        exportStyleItem = new JMenuItem( get( "$MD11068" ), getIcon( "sld_export.png" ) );
        exportStyleItem.addActionListener( this );

        menuStyles.add( importStyleItem );
        menuStyles.add( exportStyleItem );

        JMenu menuSettings = new JMenu( get( "$MD11615" ) );
        settingsItem = new JMenuItem( get( "$MD11616" ) );
        settingsItem.addActionListener( this );
        menuSettings.add( settingsItem );

        JMenu menuEdit = new JMenu( get( "$MD11833" ) );
        editSymbolItem = new JMenuItem( get( "$MD11834" ) );
        editSymbolItem.addActionListener( this );
        menuEdit.add( editSymbolItem );

        menuBar.add( menuEdit );
        menuBar.add( menuRules );
        menuBar.add( menuStyles );
        menuBar.add( menuSettings );

        rules = new JTabbedPane();
        rules.setVisible( true );

        okBt = new JButton( get( "$DI10001" ), IconRegistry.getIcon( "accept.png" ) );
        okBt.addActionListener( this );

        cancelBt = new JButton( get( "$DI10002" ), IconRegistry.getIcon( "cancel.png" ) );
        cancelBt.addActionListener( this );

        helpBt = new JButton( get( "$DI10016" ), IconRegistry.getIcon( "help.png" ) );
        helpBt.addActionListener( this );

        applyBt = new JButton( get( "$DI10043" ), IconRegistry.getIcon( "eye.png" ) );
        applyBt.addActionListener( this );
    }

    private void initRuleDialog() {
        FormLayout fl = new FormLayout( "left:default:grow(0.5), right:default:grow(0.5)",
                                        "top:default, $btheight:grow(1.0), $btheight:grow(1.0)" );

        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        builder.setDefaultDialogBorder();

        CellConstraints cc = new CellConstraints();

        builder.add( rules, cc.xyw( 1, 1, 2 ) );
        builder.add( helpBt, cc.xy( 1, 2 ) );
        builder.add( applyBt, cc.xy( 2, 2 ) );
        builder.add( buildMainButtonBar(), cc.xywh( 1, 3, 2, 1, CellConstraints.RIGHT, CellConstraints.BOTTOM ) );

        setJMenuBar( menuBar );
        getContentPane().add( builder.getPanel() );
    }

    private JPanel buildMainButtonBar() {
        ButtonBarBuilder bbBuilder = new ButtonBarBuilder();
        bbBuilder.addRelatedGap();
        bbBuilder.addGlue();
        bbBuilder.addGriddedButtons( new JButton[] { okBt, cancelBt } );
        return bbBuilder.getPanel();
    }

    private void closeDialog() {
        for ( int i = 0; i < rules.getComponentCount(); i++ ) {
            if ( rules.getComponent( i ) instanceof RuleDefinitionPanel ) {
                RuleDefinitionPanel ruleDefPanel = (RuleDefinitionPanel) rules.getComponent( i );
                ruleDefPanel.closeFrames();
            }
        }
        dispose();
    }

    private NamedStyle getMergedStyle() {
        DirectStyleType dst = new DirectStyleType();
        FeatureTypeStyle[] fts = new FeatureTypeStyle[ruleDefinitionPanels.size()];
        for ( int i = 0; i < ruleDefinitionPanels.size(); i++ ) {
            fts[i] = ruleDefinitionPanels.get( i ).getFeatureTypeStyle();
        }
        Filter filter = fts[0].getRules()[0].getFilter();
        String s = "";
        if ( filter != null ) {
            // find name of the property used for classification
            Operation op = ( (ComplexFilter) filter ).getOperation();
            if ( op instanceof PropertyIsCOMPOperation ) {
                PropertyName pn = (PropertyName) ( (PropertyIsCOMPOperation) op ).getFirstExpression();
                s = " (" + pn.getValue().getAsString() + ')';
            }
        }
        UserStyle us = (UserStyle) StyleFactory.createStyle( "default1", layer.getTitle() + s, "default3", fts );
        dst.setName( us.getName() );
        dst.setTitle( us.getTitle() );
        dst.setAbstract( us.getAbstract() );
        dst.setCurrent( true );
        dst.setUom( getUom().toString().toLowerCase() );
        return new DirectStyle( dst, us, layer );
    }

    private RuleDefinitionPanel addRuleDefinitionPanel( String name, FeatureTypeStyle rules ) {
        RuleDefinitionPanel newRuleDefPanel = new RuleDefinitionPanel( this, name, layer.getIdentifier(), settings,
                                                                       rules, appContainer );
        ruleDefinitionPanels.add( newRuleDefPanel );
        this.rules.addTab( name, newRuleDefPanel );
        this.rules.setSelectedComponent( newRuleDefPanel );
        return newRuleDefPanel;
    }

    private void removeAllRuleDefinitionPanels() {
        ruleDefinitionPanels.clear();
        rules.removeAll();
    }

    private void removeSelectedRuleDefinitionPanel() {
        ruleDefinitionPanels.remove( rules.getSelectedComponent() );
        rules.remove( rules.getSelectedIndex() );
    }

    // //////////////////////////////////////////////////////////////////////////////
    // ACTIONLISTENER
    // //////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed( ActionEvent e ) {
        if ( e.getSource() instanceof JButton ) {
            JButton srcBt = (JButton) e.getSource();
            if ( srcBt == cancelBt ) {
                closeDialog();
            } else if ( srcBt == okBt ) {
                applyStyle();
                closeDialog();
            } else if ( srcBt == helpBt && appContainer != null ) {
                HelpFrame hf = HelpFrame.getInstance( new HelpManager( appContainer ) );
                hf.setVisible( true );
                hf.gotoKeyword( "rule:EditStyle" );
            } else if ( srcBt == applyBt ) {
                applyStyle();
            }
        } else if ( e.getSource() instanceof JMenuItem ) {
            JMenuItem srcItem = (JMenuItem) e.getSource();
            if ( srcItem == addRuleItem ) {
                int noOfNewRules = rules.getComponentCount() + 1;
                String s = (String) JOptionPane.showInputDialog( this, get( "$MD10595" ), get( "$MD10596" ),
                                                                 JOptionPane.PLAIN_MESSAGE, null, null,
                                                                 get( "$MD10591", noOfNewRules ) );
                if ( s != null ) {
                    if ( s.length() == 0 ) {
                        s = get( "$MD10591", noOfNewRules );
                    }
                    addRuleDefinitionPanel( s, null );
                }

            } else if ( srcItem == removeRuleItem ) {
                if ( rules.getSelectedIndex() > -1 && rules.getComponentCount() > 1 ) {
                    removeSelectedRuleDefinitionPanel();
                }
            } else if ( srcItem == renameRuleItem ) {
                int selectedIndex = rules.getSelectedIndex();
                if ( rules.getSelectedComponent() instanceof RuleDefinitionPanel && selectedIndex > -1 ) {
                    String tabTitle = rules.getTitleAt( selectedIndex );
                    String s = (String) JOptionPane.showInputDialog( this, get( "$MD10597" ),
                                                                     get( "$MD10598", tabTitle ),
                                                                     JOptionPane.PLAIN_MESSAGE, null, null, tabTitle );
                    if ( s != null && s.length() > 0 ) {
                        rules.setTitleAt( selectedIndex, s );
                        RuleDefinitionPanel selectedPanel = (RuleDefinitionPanel) rules.getSelectedComponent();
                        selectedPanel.setRuleName( s );
                    }
                }
            } else if ( srcItem == duplicateRuleItem ) {

                int noOfNewRules = rules.getComponentCount() + 1;
                String s = (String) JOptionPane.showInputDialog( this, get( "$MD10595" ), get( "$MD10596" ),
                                                                 JOptionPane.PLAIN_MESSAGE, null, null,
                                                                 get( "$MD10591", noOfNewRules ) );
                if ( s != null ) {
                    if ( s.length() == 0 ) {
                        s = get( "$MD10591", noOfNewRules );
                    }
                    RuleDefinitionPanel selectedPanel = (RuleDefinitionPanel) this.rules.getSelectedComponent();
                    RuleDefinitionPanel newPanel = addRuleDefinitionPanel( s, selectedPanel.getFeatureTypeStyle() );
                    newPanel.setSettings( selectedPanel.getSelectedFeatureTypeIndex(),
                                          selectedPanel.getSelectedGeomPropertyIndex(), selectedPanel.getMinScale(),
                                          selectedPanel.getMaxScale(), selectedPanel.getSemanticTypeId() );
                }

            } else if ( srcItem == importStyleItem ) {
                File file = SldIO.importSld( appContainer, this );
                if ( file != null ) {
                    try {
                        importedSls = SLDFactory.createSLD( file.toURI().toURL() );
                        if ( importedSls != null ) {
                            NamedLayer[] namedLayers = importedSls.getNamedLayers();
                            if ( namedLayers.length > 0 ) {
                                AbstractStyle[] styles = namedLayers[0].getStyles();
                                for ( int i = 0; i < styles.length; i++ ) {
                                    if ( styles[i] instanceof UserStyle ) {
                                        removeAllRuleDefinitionPanels();
                                        setStyle( styles[i] );
                                        break;
                                    }
                                }
                            }
                        }
                        styleChanged = false;
                    } catch ( MalformedURLException e1 ) {
                        LOG.logError( get( "$DG10099", file.getPath(), e1.getMessage() ) );
                    } catch ( XMLParsingException e2 ) {
                        LOG.logError( get( "$DG10100", file.getName(), e2.getMessage() ) );
                        openErrorDialog( appContainer.getViewPlatform(), this, get( "$MD11073" ), get( "$DI10017" ), e2 );
                    }
                }
            } else if ( srcItem == exportStyleItem ) {
                AbstractLayer al = new NamedLayer( getTitle(), null,
                                                   new AbstractStyle[] { getMergedStyle().getStyle() } );
                StyledLayerDescriptor sld = new StyledLayerDescriptor( new AbstractLayer[] { al }, "1.0.0" );
                SldIO.exportSld( sld.exportAsXML(), appContainer, this );
            } else if ( srcItem == settingsItem ) {
                String old = settingsPanel.getUom();
                PanelDialog dlg = new PanelDialog( settingsPanel, true );
                dlg.setLocationRelativeTo( this );
                dlg.setVisible( true );
                if ( dlg.clickedOk ) {
                    NamedStyle currentStyle = layer.getCurrentStyle();
                    if ( currentStyle instanceof DefinedStyle && !old.equals( settingsPanel.getUom() ) ) {
                        ( (DefinedStyle) currentStyle ).setUom( settingsPanel.getUom() );
                        informUomChangeListener();
                    }
                }
            } else if ( srcItem == editSymbolItem ) {
                PanelDialog dlg = new PanelDialog( new EditSymbollibraryPanel( settings.getGraphicOptions() ), false );
                dlg.setLocationRelativeTo( this );
                dlg.setVisible( true );
            }
        }
    }

    private void applyStyle() {
        NamedStyle style = null;
        if ( !styleChanged && importedSls != null && importedSls.getNamedLayers().length > 0 ) {
            AbstractStyle[] styles = importedSls.getNamedLayers()[0].getStyles();
            for ( AbstractStyle at : styles ) {
                if ( at instanceof UserStyle ) {
                    UserStyle us = (UserStyle) at;
                    DirectStyleType dst = new DirectStyleType();
                    dst.setName( us.getName() );
                    dst.setTitle( us.getTitle() );
                    dst.setAbstract( us.getAbstract() );
                    dst.setCurrent( true );
                    dst.setUom( getUom().toString().toLowerCase() );
                    style = new DirectStyle( dst, us, layer );
                    break;
                }
            }
        }
        if ( style == null ) {
            style = getMergedStyle();
        }
        style.setCurrent( true );
        Command cmd = new ChangeLayerStyleCommand( layer, style );
        ApplicationContainer<Container> appContainer = owner.getApplicationContainer();
        try {
            appContainer.getCommandProcessor().executeSychronously( cmd, true );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openErrorDialog( appContainer.getViewPlatform(), this,
                                           Messages.getMessage( getLocale(), "$MD11250" ),
                                           Messages.getMessage( getLocale(), "$MD11251" ), e );
        }
    }

    /**
     * @return the uom defined for the current layer
     */
    public String getUom() {
        NamedStyle currentStyle = layer.getCurrentStyle();
        String uom = DefinedStyle.UOM_PIXEL;
        if ( currentStyle instanceof DefinedStyle ) {
            uom = ( (DefinedStyle) currentStyle ).getUom();
        }
        return uom;
    }

    @Override
    public void windowOpened( WindowEvent e ) {
        // the state of this component will always be closed because it is not useful to have it
        // opened initially. So it a project will be stored the state will be closed even if the
        // Style editor is opened at this moment
        this.owner.getComponentStateAdapter().setClosed( true );
    }

    /**
     * Register a listener to be informed when the global setting for uom changed.
     * 
     * @param listener
     *            listener to add
     */
    public void addUomChangedListener( ChangeListener uomChangeListener ) {
        uomChangedListener.add( uomChangeListener );
    }

    void informUomChangeListener() {
        for ( ChangeListener listener : uomChangedListener ) {
            listener.valueChanged( new UomChangedEvent( settingsPanel.getUom() ) );
        }
    }

    void setStyleChanged() {
        this.styleChanged = true;
    }

}
