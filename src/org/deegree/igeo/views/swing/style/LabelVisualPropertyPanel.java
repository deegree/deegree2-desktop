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

import java.awt.BorderLayout;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.sld.CssParameter;
import org.deegree.graphics.sld.Fill;
import org.deegree.graphics.sld.Font;
import org.deegree.graphics.sld.Halo;
import org.deegree.graphics.sld.ParameterValueType;
import org.deegree.graphics.sld.PointPlacement;
import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.StyleFactory;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.graphics.sld.TextSymbolizer;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.model.Preset.PRESETTYPE;
import org.deegree.igeo.style.model.classification.IllegalClassificationException;
import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.style.perform.LabelVisualPropertyPerformer;
import org.deegree.igeo.style.perform.UnitsValue;
import org.deegree.igeo.style.utils.SldCreatorUtils;
import org.deegree.igeo.views.swing.style.component.ColorPanel;
import org.deegree.igeo.views.swing.style.component.HaloRadiusPanel;
import org.deegree.igeo.views.swing.style.component.LabelChoosePanel;
import org.deegree.igeo.views.swing.style.component.PresetsPanel;
import org.deegree.igeo.views.swing.style.component.TransparencyPanel;
import org.deegree.igeo.views.swing.style.component.font.FontClassificationPanel;
import org.deegree.igeo.views.swing.style.component.font.FontFamilyPanel;
import org.deegree.igeo.views.swing.style.component.font.FontSizePanel;
import org.deegree.igeo.views.swing.style.component.font.FontStylePanel;
import org.deegree.igeo.views.swing.style.component.font.FontWeightPanel;
import org.deegree.igeo.views.swing.style.component.placement.AnchorPanel;
import org.deegree.igeo.views.swing.style.component.placement.DisplacementPanel;
import org.deegree.igeo.views.swing.style.component.placement.RotationPanel;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.filterencoding.Expression;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.deegree.model.filterencoding.PropertyName;

/**
 * <code>LabelVisualPropertyPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class LabelVisualPropertyPanel extends AbstractVisualPropertyPanel {

    private static final long serialVersionUID = 7279479987114264888L;

    private static final ILogger LOG = LoggerFactory.getLogger( LabelVisualPropertyPanel.class );

    private FontFamilyPanel fontFamilyPanel;

    private FontStylePanel fontStylePanel;

    private FontWeightPanel fontWeightPanel;

    private FontSizePanel fontSizePanel;

    private AnchorPanel pointAnchorPanel;

    private DisplacementPanel pointDisplacementPanel;

    private RotationPanel pointRotationPanel;

    private PresetsPanel presetsPanel;

    // private PerpendicularOffsetPanel lineOffsetPanel;

    // private GraphicFillPanel graphicFillPanel;

    private ColorPanel fillColorPanel;

    private TransparencyPanel fillTransparenceyPanel;

    private HaloRadiusPanel haloRadiusPanel;

    // private GraphicFillPanel haloGraphicFillPanel;

    private ColorPanel haloFillColorPanel;

    // private TransparencyPanel haloFillTransparenceyPanel;

    public LabelVisualPropertyPanel( StyleDialog owner ) {
        super( owner, new LabelVisualPropertyPerformer() );
        init();
    }

    public void setRules( List<Rule> rules, FeatureType featureType )
                            throws FilterEvaluationException {
        if ( rules.size() > 0 ) {
            Symbolizer[] symbolizers = rules.get( 0 ).getSymbolizers();
            if ( symbolizers.length > 0 ) {
                if ( symbolizers.length > 1 ) {
                    LOG.logInfo( "there are more than one symbolizers defined, only the first will be interpreted, if it is an TextSymbolizer!!" );
                }
                setSymbolizer( symbolizers[0] );
            }
            if ( classificationPanel != null ) {
                if ( rules.size() == 1 && rules.get( 0 ).getFilter() == null ) {
                    classificationPanel.initColumnValues();
                } else {
                    LOG.logInfo( "there are more then one rules, a manual classification will be constructed out of the given TextSymbolizers!" );
                    try {
                        classificationPanel.setValues( rules, featureType );
                        setActive( true );
                    } catch ( IllegalClassificationException e ) {
                        LOG.logInfo( "could not create a classification out of the given rules" );
                    }
                }
            }
        }
    }

    public void setSymbolizer( Symbolizer symbolizer )
                            throws FilterEvaluationException {
        if ( symbolizer instanceof TextSymbolizer ) {
            TextSymbolizer ts = (TextSymbolizer) symbolizer;
            ( (LabelChoosePanel) mainPanel ).setLabel( ts.getLabelAsString() );

            Font font = ts.getFont();
            if ( font != null ) {
                // font-color
                CssParameter fontColorParam = (CssParameter) font.getCssParameters().get( "font-color" );
                if ( fontColorParam != null && fontColorParam.getValueAsPropertyName() != null ) {
                    fillColorPanel.setValue( fontColorParam.getValueAsPropertyName() );
                } else {
                    fillColorPanel.setValue( font.getColor( null ) );
                }

                // font-family
                CssParameter fontFamilyParam = (CssParameter) font.getCssParameters().get( "font-family" );
                if ( fontFamilyParam != null && fontFamilyParam.getValueAsPropertyName() != null ) {
                    fontFamilyPanel.setValue( fontFamilyParam.getValueAsPropertyName() );
                } else {
                    fontFamilyPanel.setValue( font.getFamily( null ) );
                }

                // font-size
                CssParameter fontSizeParam = (CssParameter) font.getCssParameters().get( "font-size" );
                if ( fontSizeParam != null ) {
                    ParameterValueType pvt = (ParameterValueType) fontSizeParam.getValue();
                    PropertyName propertyNameFromPvt = SldCreatorUtils.getPropertyNameFromPvt( pvt );
                    if ( propertyNameFromPvt != null ) {
                        fontSizePanel.setValue( propertyNameFromPvt );
                    } else {
                        double defaultValue;
                        try {
                            defaultValue = font.getSize( null );
                        } catch ( Exception e ) {
                            defaultValue = SldValues.getDefaultFontSize();
                        }
                        fontSizePanel.setValue( UnitsValue.readFromParameterValueType( pvt, defaultValue ) );
                    }
                }

                // font-style
                CssParameter fontStyleParam = (CssParameter) font.getCssParameters().get( "font-style" );
                if ( fontStyleParam != null && fontStyleParam.getValueAsPropertyName() != null ) {
                    fontStylePanel.setValue( fontStyleParam.getValueAsPropertyName() );
                } else {
                    fontStylePanel.setValue( font.getStyle( null ) );
                }

                // font-weight
                CssParameter fontWeightParam = (CssParameter) font.getCssParameters().get( "font-weight" );
                if ( fontWeightParam != null && fontWeightParam.getValueAsPropertyName() != null ) {
                    fontWeightPanel.setValue( fontWeightParam.getValueAsPropertyName() );
                } else {
                    fontWeightPanel.setValue( font.getWeight( null ) );
                }
            }
            if ( ts.getLabelPlacement() != null && ts.getLabelPlacement().getPointPlacement() != null ) {
                PointPlacement pp = ts.getLabelPlacement().getPointPlacement();
                if ( pp.getAnchorPoint() != null && pp.getAnchorPoint()[0].getValueAsPropertyName() != null
                     && pp.getAnchorPoint()[1].getValueAsPropertyName() != null ) {
                    ParameterValueType[] anchorPoint = pp.getAnchorPoint();
                    pointAnchorPanel.setValue( anchorPoint[0].getValueAsPropertyName(),
                                               anchorPoint[1].getValueAsPropertyName() );
                } else {
                    double[] anchorPoint = pp.getAnchorPoint( null );
                    pointAnchorPanel.setValue( anchorPoint[0], anchorPoint[1] );
                }

                ParameterValueType[] displacement = pp.getDisplacement();
                if ( displacement != null && displacement.length > 1 ) {
                    PropertyName pn1 = SldCreatorUtils.getPropertyNameFromPvt( displacement[0] );
                    PropertyName pn2 = SldCreatorUtils.getPropertyNameFromPvt( displacement[1] );
                    if ( pn1 != null && pn2 != null ) {
                        pointDisplacementPanel.setValue( pn1, pn2 );
                    } else {
                        double defX = SldValues.getDefaultDisplacement().x;
                        double defY = SldValues.getDefaultDisplacement().y;
                        try {
                            double[] displacement2 = pp.getDisplacement( null );
                            defX = displacement2[0];
                            defY = displacement2[1];
                        } catch ( Exception e ) {
                            // Nothing to do
                        }
                        UnitsValue uv1 = UnitsValue.readFromParameterValueType( displacement[0], defX );
                        UnitsValue uv2 = UnitsValue.readFromParameterValueType( displacement[1], defY );
                        pointDisplacementPanel.setValue( uv1, uv2 );
                    }
                }

                ParameterValueType rotation = pp.getRotation();
                if ( rotation != null ) {
                    PropertyName pointRotationPropName = pp.getRotationPropertyName();
                    if ( pointRotationPropName != null ) {
                        pointRotationPanel.setValue( pointRotationPropName );
                    } else {
                        pointRotationPanel.setValue( pp.getRotation( null ) );
                    }
                }
                ( (LabelChoosePanel) mainPanel ).setAutoPlacement( pp.isAuto() );
            }
            // halo
            Halo halo = ts.getHalo();
            if ( halo != null ) {
                ParameterValueType haloRadius = halo.getRadius();
                if ( haloRadius != null ) {
                    PropertyName haloRadiusPropName = haloRadius.getValueAsPropertyName();
                    if ( haloRadiusPropName != null ) {
                        haloRadiusPanel.setValue( haloRadiusPropName );
                    } else {
                        haloRadiusPanel.setValue( halo.getRadius( null ) );
                    }
                }
                if ( halo.getFill() != null && halo.getFill().getCssParameters().get( "fill" ) != null ) {
                    CssParameter haloFillColor = (CssParameter) halo.getFill().getCssParameters().get( "fill" );
                    if ( haloFillColor != null && haloFillColor.getValueAsPropertyName() != null ) {
                        haloFillColorPanel.setValue( ( (CssParameter) haloFillColor ).getValueAsPropertyName() );
                    } else {
                        haloFillColorPanel.setValue( halo.getFill().getFill( null ) );
                    }
                }

                ( (LabelChoosePanel) mainPanel ).setHalo( true );
            } else {
                ( (LabelChoosePanel) mainPanel ).setHalo( false );
            }

            // fill
            Fill fill = ts.getFill();
            if ( fill != null ) {
                CssParameter fillOpacityParam = (CssParameter) fill.getCssParameters().get( "fill-opacity" );
                if ( fillOpacityParam != null && fillOpacityParam.getValueAsPropertyName() != null ) {
                    fillTransparenceyPanel.setValue( fillOpacityParam.getValueAsPropertyName() );
                } else {
                    fillTransparenceyPanel.setValue( fill.getOpacity( null ) );
                }
            }

            setActive( true );
        } else {
            LOG.logInfo( "symbolizer is not an TextSymbolizer, so style cannot be set " );
        }
    }

    private void init() {
        mainPanel = new LabelChoosePanel( this, ComponentType.LABEL );
        fontFamilyPanel = new FontFamilyPanel( this, ComponentType.FONTFAMILY, get( "$MD10983" ),
                                               getAsImageIcon( get( "$MD10984" ) ) );
        fontStylePanel = new FontStylePanel( this, ComponentType.FONTSTYLE, get( "$MD10985" ),
                                             getAsImageIcon( get( "$MD10986" ) ) );
        fontWeightPanel = new FontWeightPanel( this, ComponentType.FONTWEIGHT, get( "$MD10991" ),
                                               getAsImageIcon( get( "$MD10992" ) ) );
        fontSizePanel = new FontSizePanel( this, ComponentType.SIZE, get( "$MD10987" ),
                                           getAsImageIcon( get( "$MD10988" ) ) );
        pointAnchorPanel = new AnchorPanel( this, ComponentType.ANCHOR, get( "$MD10993" ),
                                            getAsImageIcon( get( "$MD10994" ) ) );
        pointDisplacementPanel = new DisplacementPanel( this, ComponentType.DISPLACEMENT, get( "$MD10995" ),
                                                        getAsImageIcon( get( "$MD10996" ) ) );
        pointRotationPanel = new RotationPanel( this, ComponentType.ROTATION, get( "$MD10989" ),
                                                getAsImageIcon( get( "$MD10990" ) ) );

        // lineOffsetPanel = new PerpendicularOffsetPanel();
        // graphicFillPanel = new GraphicFillPanel();
        fillColorPanel = new ColorPanel( this, ComponentType.FILLCOLOR, get( "$MD10939" ),
                                         getAsImageIcon( get( "$MD10940" ) ) );
        fillColorPanel.setValue( SldValues.getDefaultFontColor() );
        fillTransparenceyPanel = new TransparencyPanel( this, ComponentType.OPACITY, get( "$MD11039" ),
                                                        getAsImageIcon( get( "$MD11040" ) ) );
        haloRadiusPanel = new HaloRadiusPanel( this, ComponentType.HALORADIUS, get( "$MD10953" ),
                                               getAsImageIcon( get( "$MD10954" ) ) );
        // haloGraphicFillPanel = new GraphicFillPanel();
        haloFillColorPanel = new ColorPanel( this, ComponentType.HALOFILLCOLOR, get( "$MD10941" ),
                                             getAsImageIcon( get( "$MD10942" ) ) );
        haloFillColorPanel.setValue( SldValues.getDefaultHaloColor() );
        // haloFillTransparenceyPanel = new TransparencyPanel();
        classificationPanel = new FontClassificationPanel( this, ComponentType.DYNAMIC );
        presetsPanel = new PresetsPanel( this, PRESETTYPE.TEXT );

        styleAttributeContainer.addTab( get( "$MD10701" ), mainPanel );
        styleAttributeContainer.addTab( get( "$MD10702" ), fontFamilyPanel );
        styleAttributeContainer.addTab( get( "$MD10703" ), fontStylePanel );
        styleAttributeContainer.addTab( get( "$MD10704" ), fontWeightPanel );
        styleAttributeContainer.addTab( get( "$MD10705" ), fontSizePanel );
        styleAttributeContainer.addTab( get( "$MD10706" ), pointAnchorPanel );
        styleAttributeContainer.addTab( get( "$MD10707" ), pointDisplacementPanel );
        styleAttributeContainer.addTab( get( "$MD10708" ), pointRotationPanel );
        // labelProperties.addTab( "Line Offset", lineOffsetPanel );
        // labelProperties.addTab( "Fill Graphic", graphicFillPanel );
        styleAttributeContainer.addTab( get( "$MD10709" ), fillColorPanel );
        styleAttributeContainer.addTab( get( "$MD11041" ), fillTransparenceyPanel );
        styleAttributeContainer.addTab( get( "$MD10710" ), haloRadiusPanel );
        // labelProperties.addTab( "Halo Graphic Fill", haloGraphicFillPanel );
        styleAttributeContainer.addTab( get( "$MD10711" ), haloFillColorPanel );
        // labelProperties.addTab( "Halo Transparency", haloFillTransparenceyPanel );
        styleAttributeContainer.addTab( get( "$MD11658" ), classificationPanel );
        styleAttributeContainer.addTab( get( "$MD10937" ), presetsPanel );

        setLayout( new BorderLayout() );
        add( previewPanel, BorderLayout.EAST );
        add( styleAttributeContainer, BorderLayout.CENTER );
    }

    @Override
    public List<Rule> getRules() {
        if ( isActive() && classificationPanel != null && classificationPanel.isActive() ) {
            // rules label geom must be set!
            List<Rule> rules = classificationPanel.getRules();
            PropertyName pn = new PropertyName( ( (LabelChoosePanel) mainPanel ).getLabel() );
            for ( Rule rule : rules ) {
                Symbolizer[] syms = rule.getSymbolizers();
                for ( int i = 0; i < syms.length; i++ ) {
                    if ( syms[i] instanceof TextSymbolizer ) {
                        TextSymbolizer ts = (TextSymbolizer) syms[i];
                        ts.setLabel( StyleFactory.createParameterValueType( new Expression[] { pn } ) );
                        if ( !( (LabelChoosePanel) mainPanel ).isHaloActive() ) {
                            ts.setHalo( null );
                        }
                        if ( ( (LabelChoosePanel) mainPanel ).isAutoPlacement() ) {
                            ts.setLabelPlacement( null );
                        }
                    }
                }
            }
            return rules;
        }
        return super.getRules();
    }
}
