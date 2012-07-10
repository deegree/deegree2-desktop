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

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.sld.FeatureTypeStyle;
import org.deegree.graphics.sld.Geometry;
import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.StyleFactory;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.ChangeListener;
import org.deegree.igeo.settings.Settings;
import org.deegree.igeo.style.LayerCache;
import org.deegree.igeo.style.LayerCache.CachedLayer;
import org.deegree.igeo.views.swing.ScaleDenominatorPanel;
import org.deegree.igeo.views.swing.addlayer.QualifiedNameRenderer;
import org.deegree.igeo.views.swing.style.StyleDialog.GEOMTYPE;
import org.deegree.igeo.views.swing.style.component.SldFrame;
import org.deegree.model.Identifier;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.LogicalOperation;
import org.deegree.model.filterencoding.Operation;
import org.deegree.model.filterencoding.OperationDefines;
import org.deegree.model.filterencoding.PropertyIsInstanceOfOperation;
import org.deegree.model.filterencoding.PropertyName;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcbase.PropertyPathFactory;
import org.deegree.ogcbase.PropertyPathStep;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>RuleDefinitionPanel</code> represents one rule.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class RuleDefinitionPanel extends JPanel {

    private static final long serialVersionUID = -1607863664136371060L;

    private static final ILogger LOG = LoggerFactory.getLogger( RuleDefinitionPanel.class );

    private RuleDialog ruleDialog;

    private ApplicationContainer<?> appContainer;

    private Settings settings;

    private FeatureTypeStyle featureTypeStyle;

    private SldFrame sldFrame;

    private String ruleName;

    private StyleDialog styleDialog;

    private JComboBox featureTypeCBox;

    private JComboBox geomPropertyCBox;

    private ScaleDenominatorPanel scalePanel;

    private List<JRadioButton> geomTypeRBs;

    private JRadioButton rbAll;

    private JRadioButton rbPoints;

    private JRadioButton rbLines;

    private JRadioButton rbPolygons;

    private Identifier layerId;

    /*
     * @param ruleName the name of the RuleDefinitionPanel (shown in the assigned StyleDialog as FrameTitle)
     * 
     * @param layer the layer selected for editing
     * 
     * @param settings the settings of the project
     * 
     * @param rules
     */
    public RuleDefinitionPanel( RuleDialog ruleDialog, String ruleName, Identifier layerId, Settings settings,
                                FeatureTypeStyle featureTypeStyle, ApplicationContainer<?> appContainer ) {
        this.ruleDialog = ruleDialog;
        this.ruleName = ruleName;
        this.settings = settings;
        this.featureTypeStyle = featureTypeStyle;
        this.appContainer = appContainer;
        this.layerId = layerId;
        initComponents();
        init();
        setLayer();
        setStyle( featureTypeStyle );
    }

    /**
     * @param featureTypeStyle
     */
    private void setStyle( FeatureTypeStyle featureTypeStyle ) {
        if ( featureTypeStyle != null ) {
            setName( featureTypeStyle.getTitle() );
            String ftsName = featureTypeStyle.getFeatureTypeName();
            for ( int i = 0; i < featureTypeCBox.getItemCount(); i++ ) {
                if ( ( (QualifiedName) featureTypeCBox.getItemAt( i ) ).getFormattedString().equals( ftsName ) ) {
                    featureTypeCBox.setSelectedIndex( i );
                }
            }
            String typeName = null;
            if ( featureTypeStyle.getRules() != null && featureTypeStyle.getRules().length > 0 ) {
                Rule rule = featureTypeStyle.getRules()[0];
                scalePanel.setScaleDenominator( rule.getMinScaleDenominator(), rule.getMaxScaleDenominator() );
                if ( rule.getFilter() instanceof ComplexFilter
                     && ( (ComplexFilter) rule.getFilter() ).getOperation().getOperatorId() == OperationDefines.PROPERTYISINSTANCEOF ) {
                    PropertyIsInstanceOfOperation op = (PropertyIsInstanceOfOperation) ( (ComplexFilter) rule.getFilter() ).getOperation();
                    typeName = op.getTypeName().getLocalName();
                } else if ( rule.getFilter() instanceof ComplexFilter
                            && ( (ComplexFilter) rule.getFilter() ).getOperation().getOperatorId() == OperationDefines.AND ) {
                    LogicalOperation lo = (LogicalOperation) ( (ComplexFilter) rule.getFilter() ).getOperation();
                    for ( Operation op : lo.getArguments() ) {
                        if ( op.getOperatorId() == OperationDefines.PROPERTYISINSTANCEOF ) {
                            typeName = ( (PropertyIsInstanceOfOperation) op ).getTypeName().getLocalName();
                        }
                    }
                }
            }
            String[] semTypeIds = featureTypeStyle.getSemanticTypeIdentifier();
            if ( semTypeIds != null && semTypeIds.length > 0 ) {
                if ( typeName == null || "generic:any".equals( semTypeIds[0] ) ) {
                    rbAll.setSelected( true );
                } else if ( "_Curve".equals( typeName ) || "generic:line".equals( semTypeIds[0] ) ) {
                    rbLines.setSelected( true );
                } else if ( "Point".equals( typeName ) || "generic:point".equals( semTypeIds[0] ) ) {
                    rbPoints.setSelected( true );
                } else if ( "_Surface".equals( typeName ) || "generic:polygon".equals( semTypeIds[0] ) ) {
                    rbPolygons.setSelected( true );
                }
            }
        }
    }

    /**
     * sets the name of the rule and updates the title of the assigned style dialog, when user changed the name of the
     * rule
     * 
     * @param ruleName
     *            the name of the rule
     */
    public void setRuleName( String ruleName ) {
        this.ruleName = ruleName;
        if ( styleDialog != null ) {
            styleDialog.updateTitle( ruleName );
        }
    }

    /**
     * @return the name of the rule
     */
    public String getRuleName() {
        return this.ruleName;
    }

    /**
     * @return the FeatureTypeStyle of this RuleDefinitionPanel
     */
    public FeatureTypeStyle getFeatureTypeStyle() {
        List<Rule> rules = getRules();
        double minScaleDenom = scalePanel.getMinScaleDenominator();
        double maxScaleDenom = scalePanel.getMaxScaleDenominator();
        ComplexFilter geomTypeFilter = getGeomTypeFilter();
        Geometry geometry = null;
        if ( geomPropertyCBox.getSelectedItem() != null ) {
            List<PropertyPathStep> pps = new ArrayList<PropertyPathStep>();
            pps.add( PropertyPathFactory.createPropertyPathStep( (QualifiedName) geomPropertyCBox.getSelectedItem() ) );
            geometry = new Geometry( new PropertyPath( pps ), null );
        }
        for ( Rule rule : rules ) {
            rule.setMaxScaleDenominator( maxScaleDenom );
            rule.setMinScaleDenominator( minScaleDenom );
            if ( geomTypeFilter != null ) {
                if ( rule.getFilter() != null && rule.getFilter() instanceof ComplexFilter ) {
                    rule.setFilter( new ComplexFilter( geomTypeFilter, (ComplexFilter) rule.getFilter(),
                                                       OperationDefines.AND ) );
                } else {
                    rule.setFilter( geomTypeFilter );
                }
            }
            Symbolizer[] symbolizer = rule.getSymbolizers();
            if ( geometry != null ) {
                for ( int i = 0; i < symbolizer.length; i++ ) {
                    symbolizer[i].setMaxScaleDenominator( maxScaleDenom );
                    symbolizer[i].setMinScaleDenominator( minScaleDenom );
                    symbolizer[i].setGeometry( geometry );
                }
            }
        }
        Rule[] r = rules.toArray( new Rule[rules.size()] );
        FeatureTypeStyle fts = StyleFactory.createFeatureTypeStyle( r );
        fts.setTitle( ruleName );
        if ( featureTypeCBox.getSelectedItem() != null ) {
            QualifiedName qn = (QualifiedName) featureTypeCBox.getSelectedItem();
            fts.setFeatureTypeName( qn.getFormattedString() );
        }
        String[] semanticTypeIds = new String[] { getSemanticTypeId() };
        fts.setSemanticTypeIdentifier( semanticTypeIds );
        return fts;
    }

    /**
     * @return return uom
     */
    public String getUom() {
        return ruleDialog.getUom();
    }

    /**
     * @return the settings
     */
    public Settings getSettings() {
        return settings;
    }

    void closeFrames() {
        if ( sldFrame != null ) {
            sldFrame.dispose();
        }
        if ( styleDialog != null ) {
            styleDialog.dispose();
        }
    }

    /**
     * Sets the layer and updates the components.
     * 
     * @param layer
     *            the layer
     */
    private void setLayer() {
        CachedLayer wl = LayerCache.getInstance().getCachedLayer( layerId );
        if ( wl != null ) {
            scalePanel.setScaleDenominator( wl.getMinScaleDenominator(), wl.getMaxScaleDenominator() );
            for ( QualifiedName ft : wl.getFeatureTypes() ) {
                featureTypeCBox.addItem( ft );
            }
            for ( QualifiedName gp : wl.getGeometryProperties().keySet() ) {
                geomPropertyCBox.addItem( gp );
            }
            if ( featureTypeCBox.getItemCount() > 0 ) {
                featureTypeCBox.setSelectedIndex( 0 );
            }
        }
        // some elements are unneeded, if datasource is a raster
        if ( wl == null || ( wl.isRaster() && !wl.isOther() ) ) {
            featureTypeCBox.setEnabled( false );
            geomPropertyCBox.setEnabled( false );
            rbAll.setEnabled( false );
            rbPoints.setEnabled( false );
            rbLines.setEnabled( false );
            rbPolygons.setEnabled( false );
        }
    }

    private void initComponents() {
        featureTypeCBox = new JComboBox();
        featureTypeCBox.setRenderer( new QualifiedNameRenderer() );
        geomPropertyCBox = new JComboBox();
        geomPropertyCBox.setRenderer( new QualifiedNameRenderer() );

        scalePanel = new ScaleDenominatorPanel();
        scalePanel.setBorder( BorderFactory.createTitledBorder( get( "$MD10608" ) ) );
    }

    private void init() {

        FormLayout fl = new FormLayout( "default" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );

        builder.append( getFeatureTypePanel() );
        builder.nextLine();

        builder.append( getGeomPropertyPanel() );
        builder.nextLine();

        builder.append( getGeomTypePanel() );
        builder.nextLine();

        builder.append( scalePanel );
        builder.nextLine();

        builder.append( getButtonPanel() );

        add( builder.getPanel() );

    }

    private JPanel getButtonPanel() {
        JButton openStyleDialogBt = new JButton( get( "$MD10599" ) );
        openStyleDialogBt.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                ruleDialog.importSld();
                createStyleDialog();
                styleDialog.setVisible( true );
            }

        } );

        JButton showSLDBt = new JButton( get( "$MD10600" ) );
        showSLDBt.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                createStyleDialog();
                updateSldFrame();
                if ( !sldFrame.isVisible() ) {
                    sldFrame.setVisible( true );
                }
            }
        } );

        ButtonBarBuilder bbBuilder = new ButtonBarBuilder();
        bbBuilder.addGriddedButtons( new JButton[] { openStyleDialogBt, showSLDBt } );
        return bbBuilder.getPanel();
    }

    private void createSldFrame() {
        sldFrame = new SldFrame( ruleName, appContainer );
        sldFrame.setLocation( 100, 100 );
        sldFrame.setSize( 500, 400 );
    }

    private void updateSldFrame() {
        if ( sldFrame == null ) {
            createSldFrame();
        }
        FeatureTypeStyle fts = getFeatureTypeStyle();
        String sld = fts.exportAsXML();
        sldFrame.setSld( sld );
        sldFrame.repaint();
    }

    private void createStyleDialog() {
        if ( styleDialog == null ) {
            Rule[] rules = null;
            if ( featureTypeStyle != null ) {
                rules = featureTypeStyle.getRules();
            }
            long startTime = System.currentTimeMillis();
            GEOMTYPE geomtype = LayerCache.getInstance().getCachedLayer( layerId ).getGeometryType( (QualifiedName) geomPropertyCBox.getSelectedItem() );
            styleDialog = new StyleDialog( this, layerId, appContainer );
            ruleDialog.informUomChangeListener();
            styleDialog.initValues( rules, getSelectedFeatureType(), geomtype );
            if ( isShowing() ) {
                styleDialog.setLocation( getLocationOnScreen().x, getLocationOnScreen().y );
            }
            styleDialog.pack();

            if ( LOG.isDebug() ) {
                long timeNow = System.currentTimeMillis();
                LOG.logDebug( "Time needed to initialise StyleDialog for layer with id '"
                              + layerId.getAsQualifiedString() + "': " + ( timeNow - startTime ) );
            }
        }
    }

    private FeatureType getSelectedFeatureType() {
        if ( featureTypeCBox.getSelectedItem() != null ) {
            CachedLayer wl = LayerCache.getInstance().getCachedLayer( layerId );
            if ( wl != null ) {
                return wl.getFeatureType( (QualifiedName) featureTypeCBox.getSelectedItem() );
            }
        }
        return null;
    }

    private JPanel getFeatureTypePanel() {
        FormLayout fl = new FormLayout( "left:min(70dlu;pref):grow(1.0)", "$cpheight" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        builder.setBorder( BorderFactory.createTitledBorder( get( "$MD10601" ) ) );

        CellConstraints cc = new CellConstraints();
        cc.insets = new Insets( 2, 10, 2, 2 );

        builder.add( featureTypeCBox, cc.xy( 1, 1 ) );

        return builder.getPanel();
    }

    private JPanel getGeomPropertyPanel() {
        FormLayout fl = new FormLayout( "left:min(70dlu;pref):grow(1.0)", "$cpheight" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        builder.setBorder( BorderFactory.createTitledBorder( get( "$MD10602" ) ) );

        CellConstraints cc = new CellConstraints();
        cc.insets = new Insets( 2, 10, 2, 2 );

        builder.add( geomPropertyCBox, cc.xy( 1, 1 ) );

        return builder.getPanel();
    }

    private JPanel getGeomTypePanel() {
        FormLayout fl = new FormLayout( "left:pref", "$rbheight, $rbheight, $rbheight, $rbheight" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        builder.setBorder( BorderFactory.createTitledBorder( get( "$MD10603" ) ) );

        CellConstraints cc = new CellConstraints();
        cc.insets = new Insets( 2, 10, 2, 2 );
        ButtonGroup bg = new ButtonGroup();

        geomTypeRBs = new ArrayList<JRadioButton>( 4 );

        rbAll = new JRadioButton( get( "$MD10604" ), true );
        geomTypeRBs.add( rbAll );

        rbPoints = new JRadioButton( get( "$MD10605" ) );
        geomTypeRBs.add( rbPoints );

        rbLines = new JRadioButton( get( "$MD10606" ) );
        geomTypeRBs.add( rbLines );

        rbPolygons = new JRadioButton( get( "$MD10607" ) );
        geomTypeRBs.add( rbPolygons );

        int i = 1;
        for ( JRadioButton rb : geomTypeRBs ) {
            builder.add( rb, cc.xy( 1, i++ ) );
            bg.add( rb );
        }

        return builder.getPanel();
    }

    private ComplexFilter getGeomTypeFilter() {
        QualifiedName qn = null;
        if ( rbLines.isSelected() ) {
            qn = new QualifiedName( "_Curve", CommonNamespaces.GMLNS );
        } else if ( rbPoints.isSelected() ) {
            qn = new QualifiedName( "Point", CommonNamespaces.GMLNS );
        } else if ( rbPolygons.isSelected() ) {
            qn = new QualifiedName( "_Surface", CommonNamespaces.GMLNS );
        }
        if ( qn != null && geomPropertyCBox.getSelectedItem() != null ) {
            PropertyName pn = new PropertyName( (QualifiedName) geomPropertyCBox.getSelectedItem() );
            PropertyIsInstanceOfOperation op = new PropertyIsInstanceOfOperation( pn, qn );
            return new ComplexFilter( op );
        }
        return null;
    }

    public String getSemanticTypeId() {
        String semTypeId = "generic:any";
        if ( rbLines.isSelected() ) {
            semTypeId = "generic:line";
        } else if ( rbPoints.isSelected() ) {
            semTypeId = "generic:point";
        } else if ( rbPolygons.isSelected() ) {
            semTypeId = "generic:polygon";
        }
        return semTypeId;
    }

    private List<Rule> getRules() {
        createStyleDialog();
        return styleDialog.getRules();
    }

    public int getSelectedFeatureTypeIndex() {
        return featureTypeCBox.getSelectedIndex();
    }

    public int getSelectedGeomPropertyIndex() {
        return geomPropertyCBox.getSelectedIndex();
    }

    public double getMinScale() {
        return scalePanel.getMinScaleDenominator();
    }

    public double getMaxScale() {
        return scalePanel.getMaxScaleDenominator();
    }

    public void setSettings( int selectedFeatureTypeIndex, int selectedGeomPropertyIndex, double minScale,
                             double maxScale, String semanticTypeId ) {
        featureTypeCBox.setSelectedIndex( selectedFeatureTypeIndex );
        geomPropertyCBox.setSelectedIndex( selectedGeomPropertyIndex );
        scalePanel.setScaleDenominator( minScale, maxScale );

        if ( "generic:any".equals( semanticTypeId ) ) {
            rbAll.setSelected( true );
        } else if ( "generic:line".equals( semanticTypeId ) ) {
            rbLines.setSelected( true );
        } else if ( "generic:point".equals( semanticTypeId ) ) {
            rbPoints.setSelected( true );
        } else if ( "generic:polygon".equals( semanticTypeId ) ) {
            rbPolygons.setSelected( true );
        }

    }

    /**
     * Register a listener to be informed when the global setting for uom changed.
     * 
     * @param listener
     *            listener to add
     */
    public void addUomChangedListener( ChangeListener listener ) {
        ruleDialog.addUomChangedListener( listener );
    }

}
