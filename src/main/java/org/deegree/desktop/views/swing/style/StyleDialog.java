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

package org.deegree.desktop.views.swing.style;

import static org.deegree.desktop.i18n.Messages.get;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.ChangeListener;
import org.deegree.desktop.mapmodel.DefinedStyle;
import org.deegree.desktop.settings.Settings;
import org.deegree.desktop.style.LayerCache;
import org.deegree.desktop.style.LayerCache.CachedLayer;
import org.deegree.desktop.style.model.PropertyValue;
import org.deegree.desktop.views.HelpManager;
import org.deegree.desktop.views.swing.HelpFrame;
import org.deegree.desktop.views.swing.util.IconRegistry;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.sld.LineSymbolizer;
import org.deegree.graphics.sld.PointSymbolizer;
import org.deegree.graphics.sld.PolygonSymbolizer;
import org.deegree.graphics.sld.RasterSymbolizer;
import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.TextSymbolizer;
import org.deegree.model.Identifier;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.deegree.model.spatialschema.Envelope;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * The <code>StyleDialog</code> presents all different types of visual properties - theses are Lines, Polygons, Symbols,
 * Labels and Raster (not yet supported)
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class StyleDialog extends JFrame implements ActionListener, WindowListener {

    private static final long serialVersionUID = -6700117642111230974L;

    private static final ILogger LOG = LoggerFactory.getLogger( StyleDialog.class );

    public enum GEOMTYPE {
        LINE, POLYGON, POINT, UNKNOWN
    };

    private JButton okBt;

    // private JButton cancelBt;

    private JButton helpBt;

    private JTabbedPane types;

    private LineVisualPropertyPanel lineTypePanel;

    private PolygonVisualPropertyPanel polygonTypePanel;

    private LabelVisualPropertyPanel labelTypePanel;

    private SymbolVisualPropertyPanel symbolTypePanel;

    private RasterVisualPropertyPanel rasterTypePanel;

    private ApplicationContainer<?> appContainer;

    private Settings settings;

    private Identifier layerId;

    private final RuleDefinitionPanel rule;

    /**
     * initialises all components of the style dialog (the different visual properties, buttons, ...)
     * 
     * @param layer
     *            the layer the user selected to edit the style
     * @param settings
     *            the settings of the project
     * @param rules
     *            the rules to edit
     * @param geomtype
     * @param isUnitPixel
     */
    public StyleDialog( RuleDefinitionPanel rule, Identifier layerId, ApplicationContainer<?> appContainer ) {
        this.rule = rule;
        this.appContainer = appContainer;
        this.settings = rule.getSettings();
        this.layerId = layerId;
        this.setTitle( get( "$MD10586", this.rule.getRuleName() ) );
        initComponents();
        init();
        // initValues( rules, featureType, geomtype );
        setResizable( true );
        addWindowListener( this );
    }

    /**
     * @param panelToSetIcon
     *            the panel, to change the icon
     * @param isActive
     *            indicates, if the panel is activated or deactivated
     */
    public void setTypePanelIcon( VisualPropertyPanel panelToSetIcon, boolean isActive ) {
        ImageIcon icon;
        if ( isActive ) {
            URL active = StyleDialog.class.getResource( "/org/deegree/desktop/views/images/flag_green.png" );
            icon = new ImageIcon( active );
        } else {
            URL inactive = StyleDialog.class.getResource( "/org/deegree/desktop/views/images/flag_red.png" );
            icon = new ImageIcon( inactive );
        }
        int index = Integer.MIN_VALUE;
        for ( int i = 0; i < types.getComponentCount(); i++ ) {
            Component comp = types.getComponent( i );
            if ( comp == panelToSetIcon ) {
                index = i;
            }
        }
        if ( index != Integer.MIN_VALUE ) {
            types.setIconAt( index, icon );
        }
    }

    private void initComponents() {
        okBt = new JButton( get( "$DI10001" ), IconRegistry.getIcon( "accept.png" ) );
        okBt.addActionListener( this );

        // cancelBt = new JButton( get( "$DI10002" ), IconRegistry.getIcon( "cancel.png" ) );
        // cancelBt.addActionListener( this );

        helpBt = new JButton( get( "$DI10016" ), IconRegistry.getIcon( "help.png" ) );
        helpBt.addActionListener( this );

        types = new JTabbedPane();

        CachedLayer wl = LayerCache.getInstance().getCachedLayer( layerId );
        ImageIcon inactiveIcon = (ImageIcon) IconRegistry.getIcon( "flag_red.png" );

        if ( wl == null || wl.isOther() ) {
            long t = System.currentTimeMillis();
            lineTypePanel = new LineVisualPropertyPanel( this );
            LOG.logDebug( "Reqired time to instantiate LineVisualPropertyPanel : " + ( System.currentTimeMillis() - t ) );

            t = System.currentTimeMillis();
            polygonTypePanel = new PolygonVisualPropertyPanel( this );
            LOG.logDebug( "Reqired time to instantiate PolygonVisualPropertyPanel : "
                          + ( System.currentTimeMillis() - t ) );

            t = System.currentTimeMillis();
            symbolTypePanel = new SymbolVisualPropertyPanel( this );
            LOG.logDebug( "Reqired time to instantiate SymbolVisualPropertyPanel : "
                          + ( System.currentTimeMillis() - t ) );

            t = System.currentTimeMillis();
            if ( getPropertyNames().size() > 0 ) {
                labelTypePanel = new LabelVisualPropertyPanel( this );
            }
            LOG.logDebug( "Reqired time to instantiate LabelVisualPropertyPanel : " + ( System.currentTimeMillis() - t ) );

            types.addTab( get( "$MD10587" ), inactiveIcon, polygonTypePanel );
            types.addTab( get( "$MD10588" ), inactiveIcon, lineTypePanel );
            types.addTab( get( "$MD10589" ), inactiveIcon, symbolTypePanel );
            if ( labelTypePanel != null ) {
                types.addTab( get( "$MD10590" ), inactiveIcon, labelTypePanel );
            }

        } else if ( wl != null && wl.isRaster() ) {
            rasterTypePanel = new RasterVisualPropertyPanel( this );
            types.addTab( get( "$MD10857" ), inactiveIcon, rasterTypePanel );
            rasterTypePanel.setActive( true );
        }
    }

    private void init() {
        FormLayout fl = new FormLayout( "fill:pref:grow(1.0)", "fill:default:grow(1.0), 10dlu, $btheight" );

        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        builder.setDefaultDialogBorder();

        builder.append( types );
        builder.nextLine();

        builder.nextLine();

        builder.append( buildMainButtonBar() );

        getContentPane().add( builder.getPanel() );

    }

    public void initValues( Rule[] rules, FeatureType featureType, GEOMTYPE geomtype ) {
        List<Rule> textSym = new ArrayList<Rule>();
        List<Rule> lineSym = new ArrayList<Rule>();
        List<Rule> polygonSym = new ArrayList<Rule>();
        List<Rule> pointSym = new ArrayList<Rule>();
        List<Rule> rasterSym = new ArrayList<Rule>();

        if ( rules != null ) {
            // sort rules by types
            for ( int i = 0; i < rules.length; i++ ) {
                if ( rules[i].getSymbolizers() != null && rules[i].getSymbolizers()[0] instanceof TextSymbolizer ) {
                    textSym.add( rules[i] );
                } else if ( rules[i].getSymbolizers() != null && rules[i].getSymbolizers()[0] instanceof LineSymbolizer ) {
                    lineSym.add( rules[i] );
                } else if ( rules[i].getSymbolizers() != null
                            && rules[i].getSymbolizers()[0] instanceof PolygonSymbolizer ) {
                    polygonSym.add( rules[i] );
                } else if ( rules[i].getSymbolizers() != null
                            && rules[i].getSymbolizers()[0] instanceof PointSymbolizer ) {
                    pointSym.add( rules[i] );
                } else if ( rules[i].getSymbolizers() != null
                            && rules[i].getSymbolizers()[0] instanceof RasterSymbolizer ) {
                    rasterSym.add( rules[i] );
                }
            }

            try {
                if ( symbolTypePanel != null ) {
                    symbolTypePanel.setRules( pointSym, featureType );
                }
                if ( lineTypePanel != null ) {
                    lineTypePanel.setRules( lineSym, featureType );
                }
                if ( polygonTypePanel != null ) {
                    polygonTypePanel.setRules( polygonSym, featureType );
                }
                if ( labelTypePanel != null ) {
                    labelTypePanel.setRules( textSym, featureType );
                }
                if ( rasterTypePanel != null ) {
                    rasterTypePanel.setRules( rasterSym, featureType );
                }
            } catch ( FilterEvaluationException e ) {
                LOG.logError( "An error occured when trying to set the given symbolizer !", e );
                JOptionPane.showMessageDialog( this, get( "$MD10793" ), get( "$DI10017" ), JOptionPane.ERROR_MESSAGE );
            }
        } else {
            // if there are no rules to add, mark the panel which geometry can be edited by the
            // selected geometry property as active
            switch ( geomtype ) {
            case POINT:
                if ( symbolTypePanel != null ) {
                    symbolTypePanel.setActive( true );
                }
                break;
            case LINE:
                if ( lineTypePanel != null ) {
                    lineTypePanel.setActive( true );
                }
                break;
            case POLYGON:
                if ( polygonTypePanel != null ) {
                    polygonTypePanel.setActive( true );
                }
                break;
            }
        }
        switch ( geomtype ) {
        case POINT:
            if ( symbolTypePanel != null ) {
                types.setSelectedComponent( symbolTypePanel );
            }
            break;
        case LINE:
            if ( lineTypePanel != null ) {
                types.setSelectedComponent( lineTypePanel );
            }
            break;
        case POLYGON:
            if ( polygonTypePanel != null ) {
                types.setSelectedComponent( polygonTypePanel );
            }
            break;
        }
    }

    private JPanel buildMainButtonBar() {
        ButtonBarBuilder bbBuilder = new ButtonBarBuilder();
        // bbBuilder.addGriddedButtons( new JButton[] { okBt, cancelBt } );
        bbBuilder.addGriddedButtons( new JButton[] { okBt } );
        bbBuilder.addUnrelatedGap();
        bbBuilder.addGlue();
        bbBuilder.addGridded( helpBt );
        return bbBuilder.getPanel();
    }

    /**
     * @param type
     *            the data type of the property name as code
     * @return all property names available by the layer which are from the given type, if no type is given, all names
     *         are returned
     */
    public List<QualifiedName> getPropertyNames( int... type ) {
        Map<QualifiedName, PropertyValue<?>> properties = getProperties();
        List<QualifiedName> propertyTypes = new ArrayList<QualifiedName>();
        for ( QualifiedName s : properties.keySet() ) {
            if ( type.length > 0 ) {

                for ( int i = 0; i < type.length; i++ ) {
                    PropertyValue<?> prop = properties.get( s );
                    if ( prop.getDatatyp() == type[i] ) {
                        propertyTypes.add( s );
                    }
                }
            } else {
                propertyTypes.add( s );
            }
        }
        return propertyTypes;
    }

    /**
     * @return true, if pixel is selected as default
     */
    public boolean isDefaultUnitPixel() {
        if ( DefinedStyle.UOM_MAP.equalsIgnoreCase( this.rule.getUom() ) ) {
            return false;
        }
        return true;
    }

    /**
     * @param name
     *            the qualified name of the property value
     * @return the property value with the given name
     */
    public PropertyValue<?> getPropertyValue( QualifiedName name ) {
        return getProperties().get( name );
    }

    /**
     * @param selectedItem
     * @return the property value with the given name in the current map extent
     */
    public PropertyValue<?> getExtentPropertyValue( QualifiedName name ) {
        Envelope extent = appContainer.getMapModel( null ).getEnvelope();
        return LayerCache.getInstance().getProperties( layerId, extent ).get( name );
    }

    /**
     * @param name
     *            the qualified name of the property value
     * @return the property value with the given name
     */
    public PropertyValue<?> getAllPropertyValue( QualifiedName name ) {
        return LayerCache.getInstance().getAllProperties( layerId ).get( name );
    }

    private Map<QualifiedName, PropertyValue<?>> getProperties() {
        return LayerCache.getInstance().getProperties( layerId );
    }

    public CachedLayer getCachedLayer() {
        return LayerCache.getInstance().getCachedLayer( layerId );
    }

    /**
     * @return all sld rules, the user has defined by editing the components of the different visual properties
     */
    public List<Rule> getRules() {
        List<Rule> rules = new ArrayList<Rule>();
        if ( symbolTypePanel != null ) {
            rules.addAll( symbolTypePanel.getRules() );
        }
        if ( lineTypePanel != null ) {
            rules.addAll( lineTypePanel.getRules() );
        }
        if ( polygonTypePanel != null ) {
            rules.addAll( polygonTypePanel.getRules() );
        }
        if ( labelTypePanel != null ) {
            rules.addAll( labelTypePanel.getRules() );
        }
        if ( rasterTypePanel != null ) {
            rules.addAll( rasterTypePanel.getRules() );
        }
        return rules;
    }

    /**
     * @return the settings of the project
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * updates the title of the frame, when user changed the name of the rule
     * 
     * @param ruleName
     *            the new name of the rule
     */
    public void updateTitle( String ruleName ) {
        this.rule.getRuleName();
        setTitle( get( "$MD10586", ruleName ) );
        repaint();
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // ActionListener
    // ////////////////////////////////////////////////////////////////////////////////////
    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed( ActionEvent e ) {
        if ( e.getSource() instanceof JButton ) {
            JButton srcBt = (JButton) e.getSource();
            // if ( srcBt == cancelBt ) {
            // dispose();
            // } else
            if ( srcBt == helpBt && appContainer != null ) {
                HelpFrame hf = HelpFrame.getInstance( new HelpManager( appContainer ) );
                hf.setVisible( true );
                hf.gotoKeyword( "style:EditStyle" );
            } else if ( srcBt == okBt ) {
                setVisible( false );
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
     */
    public void windowActivated( WindowEvent e ) {
        // update the fillGraphicPanel, when component is activeated, to have the correct color
        if ( polygonTypePanel != null ) {
            polygonTypePanel.updateFillGraphicPanel();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
     */
    public void windowClosed( WindowEvent e ) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
     */
    public void windowClosing( WindowEvent e ) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
     */
    public void windowDeactivated( WindowEvent e ) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
     */
    public void windowDeiconified( WindowEvent e ) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
     */
    public void windowIconified( WindowEvent e ) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
     */
    public void windowOpened( WindowEvent e ) {
    }

    /**
     * Register a listener to be informed when the global setting for uom changed.
     * 
     * @param listener
     *            listener to add
     */
    public void addUomChangedListener( ChangeListener listener ) {
        rule.addUomChangedListener( listener );
    }

}
