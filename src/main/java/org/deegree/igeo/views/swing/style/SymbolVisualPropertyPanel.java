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
import org.deegree.graphics.sld.ExternalGraphic;
import org.deegree.graphics.sld.Fill;
import org.deegree.graphics.sld.Graphic;
import org.deegree.graphics.sld.Mark;
import org.deegree.graphics.sld.ParameterValueType;
import org.deegree.graphics.sld.PointSymbolizer;
import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.Stroke;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.model.Preset.PRESETTYPE;
import org.deegree.igeo.style.model.classification.IllegalClassificationException;
import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.style.perform.SymbolVisualPropertyPerformer;
import org.deegree.igeo.style.perform.UnitsValue;
import org.deegree.igeo.views.swing.style.component.ColorPanel;
import org.deegree.igeo.views.swing.style.component.MainInformationPanel;
import org.deegree.igeo.views.swing.style.component.MarkPanel;
import org.deegree.igeo.views.swing.style.component.PresetsPanel;
import org.deegree.igeo.views.swing.style.component.SizePanel;
import org.deegree.igeo.views.swing.style.component.TransparencyPanel;
import org.deegree.igeo.views.swing.style.component.placement.RotationPanel;
import org.deegree.igeo.views.swing.style.component.point.PointClassificationPanel;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.filterencoding.FilterEvaluationException;

/**
 * The <code>SymbolVisualPropertyPanel</code> collects the attributes of the visual property 'symbol', which gives the
 * possibility to edit a point symbol.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class SymbolVisualPropertyPanel extends AbstractVisualPropertyPanel {

    private static final long serialVersionUID = 3759715272976685618L;

    private static final ILogger LOG = LoggerFactory.getLogger( SymbolVisualPropertyPanel.class );

    private MarkPanel markPanel;

    private ColorPanel fillColorPanel;

    private ColorPanel lineColorPanel;

    private TransparencyPanel transparencyPanel;

    private SizePanel sizePanel;

    private PresetsPanel presetPanel;

    private RotationPanel rotationPanel;

    // private AnchorPanel anchorPanel;
    //
    // private DisplacementPanel displacementPanel;

    /**
     * inits all required components
     * 
     * @param owner
     */
    public SymbolVisualPropertyPanel( StyleDialog owner ) {
        super( owner, new SymbolVisualPropertyPerformer() );
        setLayout( new BorderLayout() );
        initComponents();
    }

    public void setRules( List<Rule> rules, FeatureType featureType )
                            throws FilterEvaluationException {
        if ( rules.size() > 0 ) {
            Symbolizer[] symbolizers = rules.get( 0 ).getSymbolizers();
            if ( symbolizers.length > 0 ) {
                if ( symbolizers.length > 1 ) {
                    LOG.logInfo( "there are more than one symbolizers defined, only the first will be interpreted, if it is an PointSymbolizer!!" );
                }
                setSymbolizer( symbolizers[0] );
            }
            if ( classificationPanel != null ) {
                if ( rules.size() == 1 && rules.get( 0 ).getFilter() == null ) {
                    classificationPanel.initColumnValues();
                } else {
                    LOG.logInfo( "there are more then one rules, a manual classification will be constructed out of the given PointSymbolizers!" );
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
        if ( symbolizer instanceof PointSymbolizer ) {

            Graphic g = ( (PointSymbolizer) symbolizer ).getGraphic();
            Object[] marksAndExtGrapics = g.getMarksAndExtGraphics();
            if ( marksAndExtGrapics != null && marksAndExtGrapics.length > 0 ) {
                if ( marksAndExtGrapics[0] instanceof Mark ) {
                    Mark m = (Mark) marksAndExtGrapics[0];
                    markPanel.setValue( m.getWellKnownName() );
                    Fill fill = m.getFill();
                    if ( fill != null ) {
                        CssParameter fillColorParam = (CssParameter) fill.getCssParameters().get( "fill" );
                        if ( fillColorParam != null && fillColorParam.getValueAsPropertyName() != null ) {
                            fillColorPanel.setValue( fillColorParam.getValueAsPropertyName() );
                        } else {
                            fillColorPanel.setValue( fill.getFill( null ) );
                        }

                        CssParameter fillTransparencyParam = (CssParameter) fill.getCssParameters().get( "fill-opacity" );
                        if ( fillTransparencyParam != null && fillTransparencyParam.getValueAsPropertyName() != null ) {
                            transparencyPanel.setValue( fillTransparencyParam.getValueAsPropertyName() );
                        } else {
                            transparencyPanel.setValue( fill.getOpacity( null ) );
                        }

                        CssParameter fillSymbolParam = (CssParameter) fill.getCssParameters().get( "symbol" );
                        if ( fillSymbolParam != null && fillSymbolParam.getValueAsPropertyName() != null ) {
                            markPanel.setValue( fillSymbolParam.getValueAsPropertyName() );
                        }
                    }
                    Stroke stroke = m.getStroke();
                    if ( stroke != null ) {
                        CssParameter strokeColorParam = (CssParameter) stroke.getCssParameters().get( "stroke" );
                        if ( strokeColorParam != null && strokeColorParam.getValueAsPropertyName() != null ) {
                            lineColorPanel.setValue( strokeColorParam.getValueAsPropertyName() );
                        } else {
                            lineColorPanel.setValue( stroke.getStroke( null ) );
                        }
                    }
                } else if ( marksAndExtGrapics[0] instanceof ExternalGraphic ) {
                    ExternalGraphic eg = (ExternalGraphic) marksAndExtGrapics[0];
                    markPanel.setValue( eg.getOnlineResource(), eg.getTitle() );
                }

                ParameterValueType rotationPVT = g.getRotation();
                if ( rotationPVT != null && rotationPVT.getValueAsPropertyName() != null ) {
                    rotationPanel.setValue( rotationPVT.getValueAsPropertyName() );
                } else {
                    rotationPanel.setValue( g.getRotation( null ) );
                }

            }
            // sizePanel
            ParameterValueType pvt = g.getSize();
            if ( pvt.getValueAsPropertyName() != null ) {
                sizePanel.setValue( pvt.getValueAsPropertyName() );
            } else {
                double defaultValue;
                try {
                    defaultValue = g.getSize( null );
                } catch ( Exception e ) {
                    defaultValue = SldValues.getDefaultLineWidth();
                }
                sizePanel.setValue( UnitsValue.readFromParameterValueType( pvt, defaultValue ) );
            }
            setActive( true );
        } else {
            LOG.logInfo( "symbolizer is not an PointSymbolizer, so style cannot be set " );
        }
    }

    @Override
    public List<Rule> getRules() {
        if ( isActive() && classificationPanel != null && classificationPanel.isActive() ) {
            return classificationPanel.getRules();
        }
        return super.getRules();
    }

    private void initComponents() {
        mainPanel = new MainInformationPanel( this, get( "$MD10676" ), get( "$MD10677" ) );

        markPanel = new MarkPanel( this, ComponentType.MARK, get( "$MD10955" ), getAsImageIcon( get( "$MD10956" ) ) );

        fillColorPanel = new ColorPanel( this, ComponentType.FILLCOLOR, get( "$MD10949" ),
                                         getAsImageIcon( get( "$MD10950" ) ) );
        fillColorPanel.setValue( SldValues.getDefaultColor() );
        transparencyPanel = new TransparencyPanel( this, ComponentType.OPACITY, get( "$MD10957" ),
                                                   getAsImageIcon( get( "$MD10958" ) ) );
        sizePanel = new SizePanel( this, ComponentType.SIZE, get( "$MD10959" ), getAsImageIcon( get( "$MD10960" ) ) );
        lineColorPanel = new ColorPanel( this, ComponentType.COLOR, get( "$MD10951" ),
                                         getAsImageIcon( get( "$MD10952" ) ) );
        lineColorPanel.setValue( SldValues.getDefaultLineColor() );
        presetPanel = new PresetsPanel( this, PRESETTYPE.POINT );
        rotationPanel = new RotationPanel( this, ComponentType.ROTATION, get( "$MD11670" ),
                                           getAsImageIcon( get( "$MD11671" ) ) );
        // anchorPanel = new AnchorPanel();
        // displacementPanel = new DisplacementPanel();

        styleAttributeContainer.addTab( get( "$MD10678" ), mainPanel );
        styleAttributeContainer.addTab( get( "$MD10679" ), markPanel );
        styleAttributeContainer.addTab( get( "$MD10680" ), fillColorPanel );
        styleAttributeContainer.addTab( get( "$MD10779" ), lineColorPanel );
        styleAttributeContainer.addTab( get( "$MD10681" ), transparencyPanel );
        styleAttributeContainer.addTab( get( "$MD10682" ), sizePanel );
        styleAttributeContainer.addTab( get( "$MD11669" ), rotationPanel );
        styleAttributeContainer.addTab( get( "$MD10921" ), presetPanel );
        // symbolProperties.addTab( "Anchor", anchorPanel );
        // symbolProperties.addTab( "Displacement", displacementPanel );

        if ( getOwner().getPropertyNames() != null && getOwner().getPropertyNames().size() > 0 ) {
            classificationPanel = new PointClassificationPanel( this );
            styleAttributeContainer.addTab( get( "$MD10718" ), classificationPanel );
        }

        add( previewPanel, BorderLayout.EAST );
        add( styleAttributeContainer, BorderLayout.CENTER );
    }
}
