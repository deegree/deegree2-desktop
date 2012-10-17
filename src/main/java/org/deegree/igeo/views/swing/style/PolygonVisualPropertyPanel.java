/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2012 by:
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
import java.awt.Color;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.sld.CssParameter;
import org.deegree.graphics.sld.ExternalGraphic;
import org.deegree.graphics.sld.Fill;
import org.deegree.graphics.sld.ParameterValueType;
import org.deegree.graphics.sld.PolygonSymbolizer;
import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.Stroke;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.model.Preset.PRESETTYPE;
import org.deegree.igeo.style.model.classification.IllegalClassificationException;
import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.style.perform.PolygonVisualPropertyPerformer;
import org.deegree.igeo.style.perform.UnitsValue;
import org.deegree.igeo.views.swing.style.component.ColorPanel;
import org.deegree.igeo.views.swing.style.component.GraphicFillPanel;
import org.deegree.igeo.views.swing.style.component.MainInformationPanel;
import org.deegree.igeo.views.swing.style.component.PresetsPanel;
import org.deegree.igeo.views.swing.style.component.TransparencyPanel;
import org.deegree.igeo.views.swing.style.component.line.LineArrayPanel;
import org.deegree.igeo.views.swing.style.component.line.LineWidthPanel;
import org.deegree.igeo.views.swing.style.component.polygon.PolygonClassificationPanel;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.filterencoding.FilterEvaluationException;

/**
 * <code>PolygonStylePanel</code>
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class PolygonVisualPropertyPanel extends AbstractVisualPropertyPanel {

    private static final long serialVersionUID = 2793915439268073656L;

    private static final ILogger LOG = LoggerFactory.getLogger( PolygonVisualPropertyPanel.class );

    private ColorPanel colorPanel;

    private TransparencyPanel transparencyPanel;

    private LineWidthPanel widthPanel;

    // private LineJoinPanel lineJoinPanel;

    // private LineCapPanel lineCapPanel;

    // private DisplacementPanel displacementPanel;

    private LineArrayPanel lineStylePanel;

    // private LineOffsetPanel lineOffsetPanel;

    private ColorPanel fillColorPanel;

    private TransparencyPanel fillTransparencyPanel;

    private GraphicFillPanel fillGraphicPanel;

    private PresetsPanel settingsPanel;

    public PolygonVisualPropertyPanel( StyleDialog owner ) {
        super( owner, new PolygonVisualPropertyPerformer() );
        init();
    }

    @Override
    public List<Rule> getRules() {
        if ( isActive() && classificationPanel != null && classificationPanel.isActive() ) {
            return classificationPanel.getRules();
        }
        return super.getRules();
    }

    public void setRules( List<Rule> rules, FeatureType featureType )
                            throws FilterEvaluationException {
        if ( rules.size() > 0 ) {
            Symbolizer[] symbolizers = rules.get( 0 ).getSymbolizers();
            if ( symbolizers.length > 0 ) {
                if ( symbolizers.length > 1 ) {
                    LOG.logInfo( "there are more than one symbolizers defined, only the first will be interpreted, if it is an PolygonSymbolizer!!" );
                }
                setSymbolizer( symbolizers[0] );
            }
            if ( classificationPanel != null ) {
                if ( rules.size() == 1 && rules.get( 0 ).getFilter() == null ) {
                    classificationPanel.initColumnValues();
                } else {
                    LOG.logInfo( "there are more then one rules, a manual classification will be constructed out of the given PolygonSymbolizers!" );
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
        if ( symbolizer instanceof PolygonSymbolizer ) {
            PolygonSymbolizer ps = (PolygonSymbolizer) symbolizer;
            Fill fill = ps.getFill();
            if ( fill != null ) {

                CssParameter fillColorParam = (CssParameter) fill.getCssParameters().get( "fill" );
                if ( fillColorParam != null && fillColorParam.getValueAsPropertyName() != null ) {
                    fillColorPanel.setValue( fillColorParam.getValueAsPropertyName() );
                } else {
                    fillColorPanel.setValue( fill.getFill( null ) );
                }

                CssParameter fillTransparencyParam = (CssParameter) fill.getCssParameters().get( "fill-opacity" );
                if ( fillTransparencyParam != null && fillTransparencyParam.getValueAsPropertyName() != null ) {
                    fillTransparencyPanel.setValue( fillTransparencyParam.getValueAsPropertyName() );
                } else {
                    fillTransparencyPanel.setValue( fill.getOpacity( null ) );
                }

                if ( fill.getGraphicFill() != null && fill.getGraphicFill().getGraphic() != null
                     && fill.getGraphicFill().getGraphic().getMarksAndExtGraphics() != null ) {
                    Object[] marksAndExtGrapics = fill.getGraphicFill().getGraphic().getMarksAndExtGraphics();
                    if ( marksAndExtGrapics.length > 0 && marksAndExtGrapics[0] instanceof ExternalGraphic ) {
                        ExternalGraphic eg = (ExternalGraphic) marksAndExtGrapics[0];
                        double size = fill.getGraphicFill().getGraphic().getSize( null );
                        fillGraphicPanel.setValue( eg.getOnlineResource(), size );
                    } else {
                        fillGraphicPanel.setValue( null, Double.NaN );
                    }
                } else {
                    fillGraphicPanel.setValue( null, Double.NaN );
                }
            }
            Stroke stroke = ps.getStroke();
            if ( stroke != null ) {
                CssParameter strokeColorParam = (CssParameter) stroke.getCssParameters().get( "stroke" );
                if ( strokeColorParam != null && strokeColorParam.getValueAsPropertyName() != null ) {
                    colorPanel.setValue( strokeColorParam.getValueAsPropertyName() );
                } else {
                    colorPanel.setValue( stroke.getStroke( null ) );
                }
                CssParameter strokeTransparencyParam = (CssParameter) stroke.getCssParameters().get( "stroke-opacity" );
                if ( strokeTransparencyParam != null && strokeTransparencyParam.getValueAsPropertyName() != null ) {
                    transparencyPanel.setValue( strokeTransparencyParam.getValueAsPropertyName() );
                } else {
                    transparencyPanel.setValue( stroke.getOpacity( null ) );
                }

                // widthPanel
                CssParameter widthParam = (CssParameter) stroke.getCssParameters().get( "stroke-width" );
                if ( widthParam != null ) {
	                if ( widthParam.getValueAsPropertyName() != null ) {
	                    widthPanel.setValue( widthParam.getValueAsPropertyName() );
	                } else {
	                    ParameterValueType pvt = (ParameterValueType) widthParam.getValue();
	                    double defaultValue;
	                    try {
	                        defaultValue = stroke.getWidth( null );
	                    } catch ( Exception e ) {
	                        defaultValue = SldValues.getDefaultLineWidth();
	                    }
	                    widthPanel.setValue( UnitsValue.readFromParameterValueType( pvt, defaultValue ) );
	                }
                }
                lineStylePanel.setValue( stroke.getDashArray( null ) );
            }
            setActive( true );
        } else {
            LOG.logInfo( "symbolizer is not an PolygonSymbolizer, so style cannot be set " );
        }
        Color fillGColor = SldValues.getDefaultColor();
        if ( fillColorPanel.getValue() instanceof Color ) {
            fillGColor = (Color) fillColorPanel.getValue();
        }
        double fillGTrans = SldValues.getDefaultOpacity();
        if ( fillTransparencyPanel.getValue() instanceof Double ) {
            fillGTrans = (Double) fillTransparencyPanel.getValue();
        }
        fillGraphicPanel.setColorAndOpacitiy( fillGColor, fillGTrans );
    }

    private void init() {
        mainPanel = new MainInformationPanel( this, get( "$MD10691" ), get( "$MD10692" ) );
        settingsPanel = new PresetsPanel( this, PRESETTYPE.POLYGON );
        colorPanel = new ColorPanel( this, ComponentType.COLOR, get( "$MD10945" ), getAsImageIcon( get( "$MD10946" ) ) );

        colorPanel.setValue( SldValues.getDefaultLineColor() );
        transparencyPanel = new TransparencyPanel( this, ComponentType.OPACITY, get( "$MD10965" ),
                                                   getAsImageIcon( get( "$MD10966" ) ) );
        widthPanel = new LineWidthPanel( this, ComponentType.LINEWIDTH, get( "$MD10979" ),
                                         getAsImageIcon( get( "$MD10980" ) ) );
        // lineJoinPanel = new LineJoinPanel( false, this, ComponentType.LINEJOIN );
        // lineCapPanel = new LineCapPanel( false, this, ComponentType.LINECAP );
        lineStylePanel = new LineArrayPanel( this, ComponentType.LINEARRAY, get( "$MD10981" ),
                                             getAsImageIcon( get( "$MD10982" ) ) );
        // lineOffsetPanel = new LineOffsetPanel();
        // displacementPanel = new DisplacementPanel();
        fillColorPanel = new ColorPanel( this, ComponentType.FILLCOLOR, get( "$MD10947" ),
                                         getAsImageIcon( get( "$MD10948" ) ) );
        fillColorPanel.setValue( SldValues.getDefaultColor() );
        fillTransparencyPanel = new TransparencyPanel( this, ComponentType.FILLOPACITY, get( "$MD10967" ),
                                                       getAsImageIcon( get( "$MD10968" ) ) );
        fillGraphicPanel = new GraphicFillPanel( this, ComponentType.EXTERNALGRAPHIC, get( "$MD10969" ),
                                                 getAsImageIcon( get( "$MD10970" ) ) );
        fillGraphicPanel.setColorAndOpacitiy( (Color) fillColorPanel.getValue(),
                                              (Double) fillTransparencyPanel.getValue() );
        addStyleChangedListener( fillGraphicPanel );

        styleAttributeContainer.addTab( get( "$MD10693" ), mainPanel );
        styleAttributeContainer.addTab( get( "$MD10694" ), colorPanel );
        styleAttributeContainer.addTab( get( "$MD10695" ), transparencyPanel );
        styleAttributeContainer.addTab( get( "$MD10696" ), widthPanel );
        // polygonProperties.addTab( "Line Join", lineJoinPanel );
        // polygonProperties.addTab( "Line Cap", lineCapPanel );
        styleAttributeContainer.addTab( get( "$MD10697" ), lineStylePanel );
        // polygonProperties.addTab( "Line Offset", lineOffsetPanel );
        // polygonProperties.addTab( "Displacement", displacementPanel );
        styleAttributeContainer.addTab( get( "$MD10698" ), fillColorPanel );
        styleAttributeContainer.addTab( get( "$MD10699" ), fillTransparencyPanel );
        styleAttributeContainer.addTab( get( "$MD10700" ), fillGraphicPanel );
        styleAttributeContainer.addTab( get( "$MD10858" ), settingsPanel );

        if ( getOwner().getPropertyNames() != null && getOwner().getPropertyNames().size() > 0 ) {
            classificationPanel = new PolygonClassificationPanel( this );
            styleAttributeContainer.addTab( get( "$MD10718" ), classificationPanel );
        }

        setLayout( new BorderLayout() );
        add( previewPanel, BorderLayout.EAST );
        add( styleAttributeContainer, BorderLayout.CENTER );
    }

    /**
     * updates the color of the fillGraphicPanel
     */
    public void updateFillGraphicPanel() {
        fillGraphicPanel.setColorAndOpacitiy( (Color) fillColorPanel.getValue(),
                                              (Double) fillTransparencyPanel.getValue() );
    }

}
