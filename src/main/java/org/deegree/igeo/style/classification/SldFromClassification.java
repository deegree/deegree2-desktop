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

package org.deegree.igeo.style.classification;

import static org.deegree.igeo.style.utils.SldCreatorUtils.getAsCssParameter;
import static org.deegree.igeo.style.utils.SldCreatorUtils.getParameterValueType;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;

import org.deegree.framework.util.ColorUtils;
import org.deegree.framework.util.Pair;
import org.deegree.graphics.sld.CssParameter;
import org.deegree.graphics.sld.ExternalGraphic;
import org.deegree.graphics.sld.Fill;
import org.deegree.graphics.sld.Font;
import org.deegree.graphics.sld.Graphic;
import org.deegree.graphics.sld.GraphicFill;
import org.deegree.graphics.sld.Halo;
import org.deegree.graphics.sld.LabelPlacement;
import org.deegree.graphics.sld.LineSymbolizer;
import org.deegree.graphics.sld.Mark;
import org.deegree.graphics.sld.ParameterValueType;
import org.deegree.graphics.sld.PointPlacement;
import org.deegree.graphics.sld.PointSymbolizer;
import org.deegree.graphics.sld.PolygonSymbolizer;
import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.Stroke;
import org.deegree.graphics.sld.StyleFactory;
import org.deegree.graphics.sld.TextSymbolizer;
import org.deegree.igeo.style.model.FillPattern;
import org.deegree.igeo.style.model.GraphicSymbol;
import org.deegree.igeo.style.model.SldProperty;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.model.Symbol;
import org.deegree.igeo.style.model.WellKnownMark;
import org.deegree.igeo.style.model.classification.ClassificationTableRow;
import org.deegree.igeo.style.model.classification.Column.COLUMNTYPE;
import org.deegree.igeo.style.model.classification.ValueRange;
import org.deegree.model.filterencoding.Expression;
import org.deegree.model.filterencoding.PropertyName;

/**
 * <code>SldFromClassification</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class SldFromClassification<U extends Comparable<U>> {

    public final List<Rule> createPolygonClassificationRules( List<ClassificationTableRow<U>> classification,
                                                              PropertyName propertyName, boolean isInPixel ) {
        List<Rule> rules = new ArrayList<Rule>();
        for ( int i = 0; i < classification.size(); i++ ) {
            ClassificationTableRow<U> row = classification.get( i );

            double v = ( 100d - row.getLineTransparency() ) / 100d;
            CssParameter width = getAsCssParameter( "stroke-width", isInPixel, row.getLineWidth() );

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put( "stroke-width", width );
            Stroke stroke = new Stroke( params, null, null );

            stroke.setOpacity( v );
            stroke.setStroke( row.getLineColor() );
            if ( row.getLineStyle() != null && !SldValues.isContinous( row.getLineStyle().getDashArray() ) ) {
                stroke.setDashArray( row.getLineStyle().getDashArray() );
            }

            PolygonSymbolizer ps = StyleFactory.createPolygonSymbolizer( stroke, getFill( row ) );

            Rule newRule = StyleFactory.createRule( ps );
            ValueRange<U> vr = row.getValue();
            newRule.setTitle( row.getLabel() );
            newRule.setFilter( vr.getFilter( propertyName ) );
            rules.add( newRule );
        }
        return rules;
    }

    public final List<Rule> createPointClassificationRules( List<ClassificationTableRow<U>> classification,
                                                            PropertyName propertyName, boolean isInPixel ) {
        List<Rule> rules = new ArrayList<Rule>();
        for ( int i = 0; i < classification.size(); i++ ) {
            ClassificationTableRow<U> row = classification.get( i );
            ExternalGraphic eg = null;
            Mark wkm = null;
            Symbol s = row.getSymbol();
            if ( s instanceof GraphicSymbol ) {
                eg = StyleFactory.createExternalGraphic( ( (GraphicSymbol) s ).getUrl(),
                                                         ( (GraphicSymbol) s ).getFormat(),
                                                         ( (GraphicSymbol) s ).getName() );
            } else if ( s instanceof WellKnownMark ) {
                wkm = StyleFactory.createMark( ( (WellKnownMark) s ).getSldName() );
                wkm.setFill( getFill( row ) );
                wkm.setStroke( StyleFactory.createStroke( row.getLineColor() ) );
            }

            Graphic graphic = StyleFactory.createGraphic( eg, wkm, SldValues.getDefaultOpacity(),
                                                          SldValues.getDefaultSize(), SldValues.getDefaultRotation() );
            Object rotationValue = row.getValue( COLUMNTYPE.ROTATION );
            if ( rotationValue instanceof PropertyName ) {
                graphic.setRotation( StyleFactory.createParameterValueType( new Expression[] { (PropertyName) rotationValue } ) );
            } else if ( rotationValue instanceof Double ) {
                graphic.setRotation( (Double) rotationValue );
            }

            if ( isInPixel ) {
                graphic.setSize( row.getSize() );
            } else {
                graphic.setSize( getParameterValueType( row.getSize() ) );
            }
            PointSymbolizer ps = StyleFactory.createPointSymbolizer();
            ps.setGraphic( graphic );

            Rule newRule = StyleFactory.createRule( ps );
            ValueRange<U> vr = row.getValue();
            newRule.setTitle( row.getLabel() );
            newRule.setFilter( vr.getFilter( propertyName ) );
            rules.add( newRule );
        }
        return rules;
    }

    public final List<Rule> createLineClassificationRules( List<ClassificationTableRow<U>> classification,
                                                           PropertyName propertyName, boolean isInPixel ) {
        List<Rule> rules = new ArrayList<Rule>();

        for ( int i = 0; i < classification.size(); i++ ) {
            ClassificationTableRow<U> row = classification.get( i );

            CssParameter width = getAsCssParameter( "stroke-width", isInPixel, row.getLineWidth() );

            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put( "stroke-width", width );
            Stroke stroke = new Stroke( params, null, null );

            stroke.setStroke( row.getLineColor() );
            stroke.setOpacity( SldValues.getOpacity( row.getLineTransparency() ) );
            if ( row.getLineCap() != null ) {
                stroke.setLineCap( row.getLineCap().getTypeCode() );
            }
            if ( row.getLineStyle() != null && !SldValues.isContinous( row.getLineStyle().getDashArray() ) ) {
                stroke.setDashArray( row.getLineStyle().getDashArray() );
            }
            LineSymbolizer ls = StyleFactory.createLineSymbolizer( stroke );

            Rule newRule = StyleFactory.createRule( ls );
            ValueRange<U> vr = row.getValue();
            newRule.setTitle( row.getLabel() );
            newRule.setFilter( vr.getFilter( propertyName ) );
            rules.add( newRule );
        }
        return rules;
    }

    @SuppressWarnings("unchecked")
    public final List<Rule> createLabelClassificationRules( List<ClassificationTableRow<U>> classification,
                                                            PropertyName propertyName, boolean isInPixel ) {
        List<Rule> rules = new ArrayList<Rule>();
        for ( int i = 0; i < classification.size(); i++ ) {
            ClassificationTableRow<U> row = classification.get( i );
            Map<String, CssParameter> fontParams = new HashMap<String, CssParameter>();
            addParameter( fontParams, "font-family", row.getValue( COLUMNTYPE.FONTFAMILY ) );
            addParameter( fontParams, "font-style", row.getValue( COLUMNTYPE.FONTSTYLE ) );
            addParameter( fontParams, "font-weight", row.getValue( COLUMNTYPE.FONTWEIGHT ) );
            addParameter( fontParams, "font-color", row.getValue( COLUMNTYPE.FONTCOLOR ) );
            addParameter( fontParams, "font-size", row.getValue( COLUMNTYPE.FONTSIZE ), isInPixel );
            Font font = new Font( (HashMap<String, CssParameter>) fontParams );

            HashMap<String, Object> fillParams = new HashMap<String, Object>();
            Object fontTrans = row.getValue( COLUMNTYPE.FONTTRANSPARENCY );
            if ( fontTrans instanceof Integer ) {
                fontTrans = SldValues.getOpacity( (Integer) fontTrans );
            }
            addParameterAsObject( fillParams, "fill-opacity", fontTrans );
            addParameterAsObject( fillParams, "fill", row.getValue( COLUMNTYPE.FONTCOLOR ) );
            Fill fill = new Fill( fillParams, null );

            // placement
            ParameterValueType[] pvtAnchorPoint = null;
            Object anchorPointValue = row.getValue( COLUMNTYPE.ANCHORPOINT );
            if ( anchorPointValue instanceof Pair<?, ?>
                 && ( (Pair<?, ?>) anchorPointValue ).first instanceof PropertyName
                 && ( (Pair<?, ?>) anchorPointValue ).second instanceof PropertyName ) {

                pvtAnchorPoint = new ParameterValueType[] {
                                                           StyleFactory.createParameterValueType( new Expression[] { (PropertyName) ( (Pair<?, ?>) anchorPointValue ).first } ),
                                                           StyleFactory.createParameterValueType( new Expression[] { (PropertyName) ( (Pair<?, ?>) anchorPointValue ).second } ) };
            } else if ( anchorPointValue instanceof Point2d ) {
                ParameterValueType pvtAnchorX = new ParameterValueType(
                                                                        new Object[] { ( (Point2d) anchorPointValue ).x } );
                ParameterValueType pvtAnchorY = new ParameterValueType(
                                                                        new Object[] { ( (Point2d) anchorPointValue ).y } );
                pvtAnchorPoint = new ParameterValueType[] { pvtAnchorX, pvtAnchorY };
            }

            ParameterValueType[] pvtDisplacementPoint = null;
            Object displacementPointValue = row.getValue( COLUMNTYPE.DISPLACEMENT );
            if ( displacementPointValue instanceof Pair<?, ?>
                 && ( (Pair<?, ?>) displacementPointValue ).first instanceof PropertyName
                 && ( (Pair<?, ?>) displacementPointValue ).second instanceof PropertyName ) {
                Pair<PropertyName, PropertyName> pair = (Pair<PropertyName, PropertyName>) displacementPointValue;
                ParameterValueType pvtAnchorX;
                ParameterValueType pvtAnchorY;
                if ( !isInPixel ) {
                    pvtAnchorX = getParameterValueType( pair.first );
                    pvtAnchorY = getParameterValueType( pair.second );
                } else {
                    pvtAnchorX = StyleFactory.createParameterValueType( new Expression[] { (PropertyName) pair.first } );
                    pvtAnchorY = StyleFactory.createParameterValueType( new Expression[] { (PropertyName) pair.second } );
                }
                pvtDisplacementPoint = new ParameterValueType[] { pvtAnchorX, pvtAnchorY };
            } else if ( displacementPointValue instanceof Point2d ) {
                ParameterValueType pvtAnchorX;
                ParameterValueType pvtAnchorY;
                if ( !isInPixel ) {
                    pvtAnchorX = getParameterValueType( ( (Point2d) displacementPointValue ).x );
                    pvtAnchorY = getParameterValueType( ( (Point2d) displacementPointValue ).y );
                } else {
                    pvtAnchorX = new ParameterValueType( new Object[] { ( (Point2d) displacementPointValue ).x } );
                    pvtAnchorY = new ParameterValueType( new Object[] { ( (Point2d) displacementPointValue ).y } );
                }
                pvtDisplacementPoint = new ParameterValueType[] { pvtAnchorX, pvtAnchorY };
            }

            ParameterValueType rotation = null;
            Object rotationValue = row.getValue( COLUMNTYPE.ROTATION );
            if ( rotationValue instanceof PropertyName ) {
                rotation = StyleFactory.createParameterValueType( new Expression[] { (PropertyName) rotationValue } );
            } else if ( rotationValue instanceof Double ) {
                rotation = StyleFactory.createParameterValueType( (Double) rotationValue );
            }

            PointPlacement pointPlacement = new PointPlacement( pvtAnchorPoint, pvtDisplacementPoint, rotation, false );
            // pointPlacement.setAuto( isAutoPlacement );
            LabelPlacement labelPlacement = StyleFactory.createLabelPlacement( pointPlacement );

            // halo
            ParameterValueType haloRadius = null;
            Object radiusValue = row.getValue( COLUMNTYPE.HALORADIUS );
            if ( radiusValue instanceof PropertyName ) {
                haloRadius = StyleFactory.createParameterValueType( new Expression[] { (PropertyName) radiusValue } );
            } else if ( radiusValue instanceof Double ) {
                haloRadius = StyleFactory.createParameterValueType( (Double) radiusValue );
            }

            // Map<String, CssParameter> haloFillParams = new HashMap<String, CssParameter>();
            Map<String, Object> haloFillParams = new HashMap<String, Object>();
            addParameterAsObject( haloFillParams, "fill", row.getValue( COLUMNTYPE.HALOCOLOR ) );
            Halo halo = new Halo( haloRadius, new Fill( haloFillParams, null ), StyleFactory.createStroke() );

            TextSymbolizer ts = StyleFactory.createTextSymbolizer( null, "", font, labelPlacement, halo, fill, 0.0,
                                                                   9E99 );
            Rule newRule = StyleFactory.createRule( ts );
            ValueRange<U> vr = row.getValue();
            newRule.setTitle( row.getLabel() );
            newRule.setFilter( vr.getFilter( propertyName ) );
            rules.add( newRule );
        }
        return rules;
    }

    /**
     * @param fontParams
     * @param string
     * @param value
     * @param isInPixel
     */
    private void addParameter( Map<String, CssParameter> params, String name, Object value, boolean isInPixel ) {
        if ( value != null ) {
            if ( !isInPixel && ( value instanceof Integer || value instanceof Double || value instanceof PropertyName ) ) {
                ParameterValueType pvt = null;
                if ( value instanceof Double ) {
                    pvt = getParameterValueType( (Double) value, 1 );
                } else if ( value instanceof Integer ) {
                    pvt = getParameterValueType( ( (Integer) value ).toString(), 1 );
                } else if ( value instanceof PropertyName ) {
                    pvt = getParameterValueType( (PropertyName) value, 1 );
                }
                if ( pvt != null )
                    params.put( name, new CssParameter( name, pvt ) );
            } else {
                addParameter( params, name, value );
            }
        }
    }

    private void addParameter( Map<String, CssParameter> params, String name, Object value ) {
        if ( value != null ) {
            if ( value instanceof PropertyName ) {
                params.put( name, getAsCssParameter( name, (PropertyName) value ) );
            } else if ( value instanceof Integer ) {
                params.put( name, StyleFactory.createCssParameter( name, (Integer) value ) );
            } else if ( value instanceof Double ) {
                params.put( name, StyleFactory.createCssParameter( name, (Double) value ) );
            } else if ( value instanceof Color ) {
                params.put( name, StyleFactory.createCssParameter( name, ColorUtils.toHexCode( "#", (Color) value ) ) );
            } else if ( value instanceof SldProperty ) {
                params.put( name, StyleFactory.createCssParameter( name, ( (SldProperty) value ).getSldName() ) );
            } else if ( value instanceof String ) {
                params.put( name, StyleFactory.createCssParameter( name, (String) value ) );
            }
        }
    }

    private void addParameterAsObject( Map<String, Object> params, String name, Object value ) {
        if ( value != null ) {
            if ( value instanceof PropertyName ) {
                params.put( name, getAsCssParameter( name, (PropertyName) value ) );
            } else if ( value instanceof Integer ) {
                params.put( name, StyleFactory.createCssParameter( name, (Integer) value ) );
            } else if ( value instanceof Double ) {
                params.put( name, StyleFactory.createCssParameter( name, (Double) value ) );
            } else if ( value instanceof Color ) {
                params.put( name, StyleFactory.createCssParameter( name, ColorUtils.toHexCode( "#", (Color) value ) ) );
            } else if ( value instanceof SldProperty ) {
                params.put( name, StyleFactory.createCssParameter( name, ( (SldProperty) value ).getSldName() ) );
            } else if ( value instanceof String ) {
                params.put( name, StyleFactory.createCssParameter( name, (String) value ) );
            }
        }
    }

    private Fill getFill( ClassificationTableRow<U> row ) {
        Fill fill = StyleFactory.createFill();
        fill.setOpacity( SldValues.getOpacity( row.getFillTransparency() ) );
        if ( row.getFillColor() instanceof Color ) {
            fill.setFill( (Color) row.getFillColor() );
        } else if ( row.getFillColor() instanceof GraphicSymbol ) {
            GraphicSymbol fp = (GraphicSymbol) row.getFillColor();
            ExternalGraphic eg = StyleFactory.createExternalGraphic( fp.getUrl(), fp.getFormat(), fp.getName() );
            Graphic graphic = StyleFactory.createGraphic( eg, null, SldValues.getDefaultOpacity(),
                                                          SldValues.getDefaultSize(), SldValues.getDefaultRotation() );
            double fillGraphicSize = fp.getSize();
            if ( Double.isNaN( fillGraphicSize ) || fillGraphicSize < 0 ) {
                graphic.setSize( null );
            } else {
                graphic.setSize( fillGraphicSize );
            }
            GraphicFill graphicFill = StyleFactory.createGraphicFill( graphic );
            fill.setGraphicFill( graphicFill );
            if ( fp instanceof FillPattern ) {
                fill.setFill( ( (FillPattern) fp ).getColor() );
            }
        }
        return fill;
    }

}
