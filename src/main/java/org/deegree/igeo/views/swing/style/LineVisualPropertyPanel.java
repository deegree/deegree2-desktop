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
import org.deegree.graphics.sld.LineSymbolizer;
import org.deegree.graphics.sld.ParameterValueType;
import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.Stroke;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.model.Preset.PRESETTYPE;
import org.deegree.igeo.style.model.classification.IllegalClassificationException;
import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.style.perform.LineVisualPropertyPerformer;
import org.deegree.igeo.style.perform.UnitsValue;
import org.deegree.igeo.views.swing.style.component.ColorPanel;
import org.deegree.igeo.views.swing.style.component.MainInformationPanel;
import org.deegree.igeo.views.swing.style.component.PresetsPanel;
import org.deegree.igeo.views.swing.style.component.TransparencyPanel;
import org.deegree.igeo.views.swing.style.component.line.LineArrayPanel;
import org.deegree.igeo.views.swing.style.component.line.LineCapPanel;
import org.deegree.igeo.views.swing.style.component.line.LineClassificationPanel;
import org.deegree.igeo.views.swing.style.component.line.LineWidthPanel;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.filterencoding.FilterEvaluationException;

/**
 * <code>LineVisualPropertyPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class LineVisualPropertyPanel extends AbstractVisualPropertyPanel {

    private static final long serialVersionUID = -6226952667082209526L;

    private static final ILogger LOG = LoggerFactory.getLogger( LineVisualPropertyPanel.class );

    private ColorPanel colorPanel;

    private TransparencyPanel transparencyPanel;

    private LineWidthPanel widthPanel;

    private LineCapPanel lineCapPanel;

    private LineArrayPanel lineStylePanel;

    private PresetsPanel presetsPanel;

    // private LineJoinPanel lineJoinPanel;
    // private DisplacementPanel displacementPanel;
    // private LineOffsetPanel lineOffsetPanel;

    public LineVisualPropertyPanel( StyleDialog owner ) {
        super( owner, new LineVisualPropertyPerformer() );
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
                    LOG.logInfo( "there are more than one symbolizers defined, only the first will be interpreted, if it is an LineSymbolizer!!" );
                }
                setSymbolizer( symbolizers[0] );
            }
            if ( classificationPanel != null ) {
                if ( rules.size() == 1 && rules.get( 0 ).getFilter() == null ) {
                    classificationPanel.initColumnValues();
                } else {
                    LOG.logInfo( "there are more then one rules, a manual classification will be constructed out of the given LineSymbolizers!" );
                    try {
                        classificationPanel.setValues( rules, featureType );
                        setActive( true );
                    } catch ( IllegalClassificationException e ) {
                        LOG.logInfo( "could not create a classification out of the given rules", e.getMessage() );
                    }

                }
            }
        }
    }

    public void setSymbolizer( Symbolizer symbolizer )
                            throws FilterEvaluationException {
        if ( symbolizer instanceof LineSymbolizer ) {
            Stroke stroke = ( (LineSymbolizer) symbolizer ).getStroke();
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

            CssParameter widthParam = (CssParameter) stroke.getCssParameters().get( "stroke-width" );

            if ( widthParam != null && widthParam.getValueAsPropertyName() != null ) {
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
            lineCapPanel.setValue( stroke.getLineCap( null ) );
            lineStylePanel.setValue( stroke.getDashArray( null ) );
            setActive( true );
        } else {
            LOG.logInfo( "symbolizer is not an LineSymbolizer, so style cannot be set " );
        }
    }

    private void init() {
        mainPanel = new MainInformationPanel( this, get( "$MD10683" ), get( "$MD10684" ) );
        colorPanel = new ColorPanel( this, ComponentType.COLOR, get( "$MD10943" ), getAsImageIcon( get( "$MD10944" ) ) );
        colorPanel.setValue( SldValues.getDefaultLineColor() );
        transparencyPanel = new TransparencyPanel( this, ComponentType.OPACITY, get( "$MD10971" ),
                                                   getAsImageIcon( get( "$MD10972" ) ) );

        widthPanel = new LineWidthPanel( this, ComponentType.LINEWIDTH, get( "$MD10973" ),
                                         getAsImageIcon( get( "$MD10974" ) ) );
        lineCapPanel = new LineCapPanel( this, ComponentType.LINECAP, get( "$MD10975" ),
                                         getAsImageIcon( get( "$MD10976" ) ) );
        lineStylePanel = new LineArrayPanel( this, ComponentType.LINEARRAY, get( "$MD10977" ),
                                             getAsImageIcon( get( "$MD10978" ) ) );
        // lineJoinPanel = new LineJoinPanel();
        // displacementPanel = new DisplacementPanel();
        // lineOffsetPanel = new LineOffsetPanel();
        presetsPanel = new PresetsPanel( this, PRESETTYPE.LINE );

        styleAttributeContainer.addTab( get( "$MD10685" ), mainPanel );
        styleAttributeContainer.addTab( get( "$MD10686" ), colorPanel );
        styleAttributeContainer.addTab( get( "$MD10687" ), transparencyPanel );
        styleAttributeContainer.addTab( get( "$MD10688" ), widthPanel );
        styleAttributeContainer.addTab( get( "$MD10689" ), lineCapPanel );
        styleAttributeContainer.addTab( get( "$MD10690" ), lineStylePanel );
        // lineProperties.addTab( "Line Join", lineJoinPanel );
        // lineProperties.addTab( "Line Offset", lineOffsetPanel );
        // lineProperties.addTab( "Displacement", displacementPanel );
        styleAttributeContainer.addTab( get( "$MD10919" ), presetsPanel );
        if ( getOwner().getPropertyNames() != null && getOwner().getPropertyNames().size() > 0 ) {
            classificationPanel = new LineClassificationPanel( this );
            styleAttributeContainer.addTab( get( "$MD10936" ), classificationPanel );
        }

        setLayout( new BorderLayout() );
        add( previewPanel, BorderLayout.EAST );
        add( styleAttributeContainer, BorderLayout.CENTER );
    }
}
