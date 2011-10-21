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

package org.deegree.igeo.style.perform;

import static org.deegree.igeo.style.utils.SldCreatorUtils.getAsCssParameter;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.ColorUtils;
import org.deegree.framework.util.Pair;
import org.deegree.graphics.sld.CssParameter;
import org.deegree.graphics.sld.Fill;
import org.deegree.graphics.sld.Font;
import org.deegree.graphics.sld.Halo;
import org.deegree.graphics.sld.LabelPlacement;
import org.deegree.graphics.sld.ParameterValueType;
import org.deegree.graphics.sld.PointPlacement;
import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.StyleFactory;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.graphics.sld.TextSymbolizer;
import org.deegree.igeo.style.ImageFactory;
import org.deegree.igeo.style.model.SldProperty;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.utils.SldCreatorUtils;
import org.deegree.model.filterencoding.Expression;
import org.deegree.model.filterencoding.PropertyName;

/**
 * <code>LabelVisualPropertyPerformer</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class LabelVisualPropertyPerformer implements VisualPropertyPerformer {

    private static final ILogger LOG = LoggerFactory.getLogger( LabelVisualPropertyPerformer.class );

    private boolean isHaloActive;

    private Boolean isAutoPlacement = false;

    private QualifiedName labelName;

    private String fontFamily;

    private QualifiedName fontFamilyDynamic;

    private String fontStyle = SldValues.getDefaultFontStyle();

    private QualifiedName fontStyleDynamic;

    private String fontWeight = SldValues.getDefaultFontWeight();

    private QualifiedName fontWeightDynamic;

    private UnitsValue fontSize;

    private QualifiedName fontSizeDynamic;

    private Color fillColor = SldValues.getDefaultColor();

    private QualifiedName fillColorDynamic;

    private Color haloColor = SldValues.getDefaultHaloColor();

    private QualifiedName haloColorDynamic;

    private double haloRadius = SldValues.getDefaultHaloRadius();

    private QualifiedName haloRadiusDynamic;

    private Point2d pointAnchor = SldValues.getDefaultAnchorPoint();

    private Pair<QualifiedName, QualifiedName> pointAnchorDynamic;

    private Pair<UnitsValue, UnitsValue> pointDisplacement;

    private Pair<QualifiedName, QualifiedName> pointDisplacementDynamic;

    private double pointRotation = SldValues.getDefaultRotation();

    private QualifiedName pointRotationDynamic;

    private double fillTransparency = SldValues.getDefaultOpacity();

    private QualifiedName fillTransparencyDynamic;

    public List<Rule> getRules( boolean isInPixel ) {
        List<Rule> rules = new ArrayList<Rule>();
        rules.add( StyleFactory.createRule( getSymbolizer( isInPixel ) ) );
        return rules;
    }

    public Symbolizer getSymbolizer( boolean isInPixel ) {
        // font
        Map<String, CssParameter> fontParams = new HashMap<String, CssParameter>();

        // font-weight
        CssParameter fontWeightCSS;
        if ( this.fontWeightDynamic != null ) {
            fontWeightCSS = getAsCssParameter( "font-weight", true, fontWeightDynamic );
        } else {
            if ( fontWeight != null ) {
                fontWeightCSS = StyleFactory.createCssParameter( "font-weight", this.fontWeight );
            } else {
                fontWeightCSS = StyleFactory.createCssParameter( "font-weight", SldValues.getDefaultFontWeight() );
            }
        }
        fontParams.put( "font-weight", fontWeightCSS );

        // font-style
        CssParameter fontStyleCSS;
        if ( this.fontStyleDynamic != null ) {
            fontStyleCSS = getAsCssParameter( "font-style", true, fontStyleDynamic );
        } else {
            if ( fontStyle != null ) {
                fontStyleCSS = StyleFactory.createCssParameter( "font-style", this.fontStyle );
            } else {
                fontStyleCSS = StyleFactory.createCssParameter( "font-style", SldValues.getDefaultFontStyle() );
            }
        }
        fontParams.put( "font-style", fontStyleCSS );

        // font-family
        CssParameter fontFamilyCSS;
        if ( this.fontFamilyDynamic != null ) {
            fontFamilyCSS = getAsCssParameter( "font-family", true, fontFamilyDynamic );
        } else {
            fontFamilyCSS = StyleFactory.createCssParameter( "font-family", this.fontFamily );
        }
        fontParams.put( "font-family", fontFamilyCSS );

        // font-color
        CssParameter fontColorCSS;
        if ( this.fillColorDynamic != null ) {
            fontColorCSS = getAsCssParameter( "font-color", true, fillColorDynamic );
        } else {
            fontColorCSS = StyleFactory.createCssParameter( "font-color", ColorUtils.toHexCode( "#", this.fillColor ) );
        }
        fontParams.put( "font-color", fontColorCSS );

        CssParameter fontCSS = null;
        if ( this.fontSizeDynamic != null ) {
            fontCSS = getAsCssParameter( "font-size", isInPixel, fontSizeDynamic, 1 );
        } else {
            if ( fontSize != null ) {
                try {
                    fontCSS = new CssParameter( "font-size", fontSize.getAsParameterValueType( 1 ) );
                } catch ( Exception e1 ) {
                    LOG.logWarning( "ignore", e1 );
                }
            }
        }
        fontParams.put( "font-size", fontCSS );

        Font font = new Font( fontParams );

        // point placement
        ParameterValueType pvtRotation;
        if ( this.pointRotationDynamic != null ) {
            PropertyName pn = new PropertyName( this.pointRotationDynamic );
            pvtRotation = StyleFactory.createParameterValueType( new Expression[] { pn } );
        } else {
            pvtRotation = new ParameterValueType( new Object[] { this.pointRotation } );
        }

        ParameterValueType[] pvtAnchorPoint;
        if ( this.pointAnchorDynamic != null ) {
            PropertyName pnX = new PropertyName( pointAnchorDynamic.first );
            PropertyName pnY = new PropertyName( pointAnchorDynamic.second );
            pvtAnchorPoint = new ParameterValueType[] {
                                                       StyleFactory.createParameterValueType( new Expression[] { pnX } ),
                                                       StyleFactory.createParameterValueType( new Expression[] { pnY } ) };
        } else {
            ParameterValueType pvtAnchorX = new ParameterValueType( new Object[] { this.pointAnchor.x } );
            ParameterValueType pvtAnchorY = new ParameterValueType( new Object[] { this.pointAnchor.y } );
            pvtAnchorPoint = new ParameterValueType[] { pvtAnchorX, pvtAnchorY };
        }

        ParameterValueType[] pvtDisplacementPoint = null;
        if ( pointDisplacementDynamic != null ) {
            PropertyName pnX = new PropertyName( pointDisplacementDynamic.first );
            PropertyName pnY = new PropertyName( pointDisplacementDynamic.second );
            if ( isInPixel ) {
                pvtDisplacementPoint = new ParameterValueType[] {
                                                                 StyleFactory.createParameterValueType( new Expression[] { pnX } ),
                                                                 StyleFactory.createParameterValueType( new Expression[] { pnY } ) };
            } else {
                pvtDisplacementPoint = new ParameterValueType[] { SldCreatorUtils.getParameterValueType( pnX ),
                                                                 SldCreatorUtils.getParameterValueType( pnY ) };
            }
        } else if ( pointDisplacement != null ) {
            try {
                ParameterValueType pvtAnchorX = this.pointDisplacement.first.getAsParameterValueType();
                ParameterValueType pvtAnchorY = this.pointDisplacement.second.getAsParameterValueType();
                pvtDisplacementPoint = new ParameterValueType[] { pvtAnchorX, pvtAnchorY };
            } catch ( Exception e ) {
                LOG.logWarning( "ignore", e );
            }
        }

        PointPlacement pointPlacement = new PointPlacement( pvtAnchorPoint, pvtDisplacementPoint, pvtRotation, false );
        pointPlacement.setAuto( isAutoPlacement );
        LabelPlacement labelPlacement = StyleFactory.createLabelPlacement( pointPlacement );

        TextSymbolizer ts = StyleFactory.createTextSymbolizer( null, "", font, labelPlacement, createHalo(),
                                                               createFill(), 0.0, 9E99 );

        PropertyName pnLabel = new PropertyName( this.labelName );
        ParameterValueType label = StyleFactory.createParameterValueType( new Expression[] { pnLabel } );
        ts.setLabel( label );
        return ts;
    }

    private Fill createFill() {
        // fill-color
        HashMap<String, Object> fillParams = new HashMap<String, Object>();
        CssParameter fillColor;
        if ( fillColorDynamic != null ) {
            fillColor = getAsCssParameter( "fill", true, fillColorDynamic );
        } else {
            fillColor = StyleFactory.createCssParameter( "fill", ColorUtils.toHexCode( "#", this.fillColor ) );
        }
        fillParams.put( "fill", fillColor );
        // fill-transparency
        CssParameter fillTranspraency;
        if ( this.fillTransparencyDynamic != null ) {
            fillTranspraency = getAsCssParameter( "fill-opacity", true, fillTransparencyDynamic );
        } else {
            fillTranspraency = StyleFactory.createCssParameter( "fill-opacity", this.fillTransparency );
        }
        fillParams.put( "fill-opacity", fillTranspraency );
        return new Fill( fillParams, null );
    }

    private Halo createHalo() {
        if ( this.isHaloActive ) {
            ParameterValueType pvtHaloRadius;
            if ( this.haloRadiusDynamic != null ) {
                PropertyName pn = new PropertyName( this.haloRadiusDynamic );
                pvtHaloRadius = StyleFactory.createParameterValueType( new Expression[] { pn } );
            } else {
                pvtHaloRadius = StyleFactory.createParameterValueType( this.haloRadius );
            }
            HashMap<String, Object> haloFillParams = new HashMap<String, Object>();
            CssParameter haloFillColor;
            if ( haloColorDynamic != null ) {
                PropertyName pn = new PropertyName( this.haloColorDynamic );
                ParameterValueType pvt = StyleFactory.createParameterValueType( new Expression[] { pn } );
                haloFillColor = new CssParameter( "fill", pvt );
            } else {
                haloFillColor = StyleFactory.createCssParameter( "fill", ColorUtils.toHexCode( "#", this.haloColor ) );
            }
            haloFillParams.put( "fill", haloFillColor );
            Fill haloFill = new Fill( haloFillParams, null );
            return new Halo( pvtHaloRadius, haloFill, StyleFactory.createStroke() );
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public void update( StyleChangedEvent changeEvent ) {
        Object value = changeEvent.getValue();
        switch ( changeEvent.getType() ) {
        case LABEL:
            if ( value instanceof QualifiedName ) {
                labelName = (QualifiedName) value;
            }
            break;
        case LABEL_HALO:
            if ( value instanceof Boolean ) {
                isHaloActive = (Boolean) value;
            }
            break;
        case LABEL_PLACEMENT:
            if ( value instanceof Boolean ) {
                isAutoPlacement = (Boolean) value;
            }
            break;
        case FONTFAMILY:
            if ( value instanceof String ) {
                fontFamily = (String) value;
                fontFamilyDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                fontFamilyDynamic = (QualifiedName) value;
            }
            break;
        case FONTSTYLE:
            if ( value instanceof SldProperty ) {
                fontStyle = ( (SldProperty) value ).getSldName();
                fontStyleDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                fontStyleDynamic = (QualifiedName) value;
            }
            break;
        case FONTWEIGHT:
            if ( value instanceof SldProperty ) {
                fontWeight = ( (SldProperty) value ).getSldName();
                fontWeightDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                fontWeightDynamic = (QualifiedName) value;
            }
            break;
        case SIZE:
            if ( value instanceof UnitsValue ) {
                fontSize = (UnitsValue) value;
                fontSizeDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                fontSizeDynamic = (QualifiedName) value;
            }
            break;
        case FILLCOLOR:
            if ( value instanceof Color ) {
                fillColor = (Color) value;
                fillColorDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                fillColorDynamic = (QualifiedName) value;
            }
            break;
        case HALORADIUS:
            if ( value instanceof Double ) {
                haloRadius = (Double) value;
                haloRadiusDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                haloRadiusDynamic = (QualifiedName) value;
            }
            break;
        case HALOFILLCOLOR:
            if ( value instanceof Color ) {
                haloColor = (Color) value;
                haloColorDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                haloColorDynamic = (QualifiedName) value;
            }
            break;
        case ANCHOR:
            if ( value instanceof Point2d ) {
                pointAnchor = (Point2d) value;
                pointAnchorDynamic = null;
            } else if ( value instanceof Pair<?, ?> && ( (Pair<?, ?>) value ).first != null
                        && ( (Pair<?, ?>) value ).first instanceof QualifiedName
                        && ( (Pair<?, ?>) value ).second != null
                        && ( (Pair<?, ?>) value ).second instanceof QualifiedName ) {
                pointAnchorDynamic = (Pair<QualifiedName, QualifiedName>) value;
            }
            break;
        case DISPLACEMENT:
            if ( value instanceof Pair<?, ?> ) {
                Pair<?, ?> pair = (Pair<?, ?>) value;
                if ( pair.first instanceof UnitsValue && pair.second instanceof UnitsValue ) {
                    pointDisplacement = (Pair<UnitsValue, UnitsValue>) value;
                    pointDisplacementDynamic = null;
                } else if ( pair.first instanceof QualifiedName && pair.second instanceof QualifiedName ) {
                    pointDisplacementDynamic = (Pair<QualifiedName, QualifiedName>) value;
                }
            }
            break;
        case ROTATION:
            if ( value instanceof Double ) {
                pointRotation = (Double) value;
                pointRotationDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                pointRotationDynamic = (QualifiedName) value;
            }
            break;
        case OPACITY:
            if ( value instanceof Double ) {
                fillTransparency = (Double) value;
                fillTransparencyDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                fillTransparencyDynamic = (QualifiedName) value;
            }
            break;
        }
    }

    public BufferedImage getAsImage() {
        return ImageFactory.createImage( (TextSymbolizer) getSymbolizer( true ) );
    }

}
