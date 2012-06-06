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

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.util.ColorUtils;
import org.deegree.graphics.sld.CssParameter;
import org.deegree.graphics.sld.ExternalGraphic;
import org.deegree.graphics.sld.Fill;
import org.deegree.graphics.sld.Graphic;
import org.deegree.graphics.sld.GraphicFill;
import org.deegree.graphics.sld.PolygonSymbolizer;
import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.Stroke;
import org.deegree.graphics.sld.StyleFactory;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.igeo.style.model.DashArray;
import org.deegree.igeo.style.model.GraphicSymbol;
import org.deegree.igeo.style.model.SldProperty;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.utils.SldCreatorUtils;
import org.deegree.model.filterencoding.PropertyName;

/**
 * <code>PolygonVisualPropertyPerformer</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class PolygonVisualPropertyPerformer extends AbstractVisualPropertyPerformer {

    private Color lineColor = SldValues.getDefaultLineColor();

    private QualifiedName lineColorDynamic;

    private double lineTransparency = SldValues.getDefaultOpacity();

    private QualifiedName lineTransparencyDynamic;

    private UnitsValue lineWidth;

    private QualifiedName lineWidthDynamic;

    private int lineCap = SldValues.getDefaultLineCapCode();

    private int lineJoin = SldValues.getDefaultLineJoinCode();

    private float[] dashArray = null;

    private Color fillColor = SldValues.getDefaultColor();

    private QualifiedName fillColorDynamic;

    private double fillTransparency = SldValues.getDefaultOpacity();

    private QualifiedName fillTransparencyDynamic;

    private URL fillGraphicUrl;

    private String fillGraphicFormat;

    private double fillGraphicSize;

    public List<Rule> getRules( boolean isInPixel ) {
        List<Rule> rules = new ArrayList<Rule>();
        rules.add( StyleFactory.createRule( getSymbolizer( isInPixel ) ) );
        return rules;
    }

    public void update( StyleChangedEvent changeEvent ) {
        Object value = changeEvent.getValue();
        switch ( changeEvent.getType() ) {
        case COLOR:
            if ( value instanceof Color ) {
                this.lineColor = (Color) value;
                this.lineColorDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                this.lineColorDynamic = (QualifiedName) value;
            }
            break;
        case OPACITY:
            if ( value instanceof Double ) {
                this.lineTransparency = (Double) value;
                this.lineTransparencyDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                this.lineTransparencyDynamic = (QualifiedName) value;
            }
            break;
        case LINEWIDTH:
            if ( value instanceof UnitsValue ) {
                this.lineWidth = (UnitsValue) value;
                this.lineWidthDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                this.lineWidthDynamic = (QualifiedName) value;
            }
            break;
        case LINECAP:
            if ( value instanceof SldProperty ) {
                this.lineCap = ( (SldProperty) value ).getTypeCode();
            }
            break;
        case LINEJOIN:
            if ( value instanceof SldProperty ) {
                this.lineJoin = ( (SldProperty) value ).getTypeCode();
            }
            break;
        case LINEARRAY:
            if ( value instanceof DashArray ) {
                this.dashArray = ( (DashArray) value ).getDashArray();
            }
            break;
        case FILLCOLOR:
            if ( value instanceof Color ) {
                this.fillColor = (Color) value;
                this.fillColorDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                this.fillColorDynamic = (QualifiedName) value;
            }
            break;
        case FILLOPACITY:
            if ( value instanceof Double ) {
                this.fillTransparency = (Double) value;
                this.fillTransparencyDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                this.fillTransparencyDynamic = (QualifiedName) value;
            }
            break;
        case EXTERNALGRAPHIC:
            if ( value instanceof GraphicSymbol ) {
                GraphicSymbol s = (GraphicSymbol) value;
                this.fillGraphicUrl = s.getUrl();
                this.fillGraphicFormat = s.getFormat();
                this.fillGraphicSize = s.getSize();
            } else {
                this.fillGraphicUrl = null;
                this.fillGraphicFormat = null;
                this.fillGraphicSize = Double.NaN;
            }
            break;
        }
    }

    @Override
    int getSize() {
        return 70;
    }

    public Symbolizer getSymbolizer( boolean isInPixel ) {
        PolygonSymbolizer ps = StyleFactory.createPolygonSymbolizer( createStroke( isInPixel ), createFill() );
        return ps;
    }

    private Stroke createStroke( boolean isInPixel ) {
        HashMap<String, Object> strokeParams = new HashMap<String, Object>();
        // stroke-width
        CssParameter widthParam;
        if ( lineWidthDynamic != null ) {
            if ( !isInPixel ) {
                widthParam = new CssParameter(
                                               "stroke-width",
                                               SldCreatorUtils.getParameterValueType( new PropertyName(
                                                                                                        lineWidthDynamic ) ) );
            } else {
                widthParam = getAsCssParameter( "stroke-width", lineWidthDynamic );
            }
        } else {
            if ( lineWidth != null ) {
                widthParam = lineWidth.getAsCssParameter( "stroke-width" );
            } else {
                widthParam = StyleFactory.createCssParameter( "stroke-width", SldValues.getDefaultLineWidth() );
            }
        }
        strokeParams.put( "stroke-width", widthParam );

        // stroke-color
        CssParameter lineColorParam;
        if ( lineColorDynamic != null ) {
            lineColorParam = getAsCssParameter( "stroke", lineColorDynamic );
        } else {
            lineColorParam = StyleFactory.createCssParameter( "stroke", ColorUtils.toHexCode( "#", this.lineColor ) );
        }
        strokeParams.put( "stroke", lineColorParam );

        // stroke-opacity
        CssParameter lineTransparencyParam;
        if ( lineTransparencyDynamic != null ) {
            lineTransparencyParam = getAsCssParameter( "stroke-opacity", lineTransparencyDynamic );
        } else {
            lineTransparencyParam = StyleFactory.createCssParameter( "stroke-opacity", lineTransparency );
        }
        strokeParams.put( "stroke-opacity", lineTransparencyParam );

        Stroke stroke = new Stroke( strokeParams, null, null );
        stroke.setLineCap( this.lineCap );
        stroke.setLineJoin( this.lineJoin );
        if ( !SldValues.isContinous( dashArray ) ) {
            stroke.setDashArray( this.dashArray );
        }
        return stroke;
    }

    private Fill createFill() {
        GraphicFill graphicFill = null;
        if ( this.fillGraphicUrl != null && this.fillGraphicFormat != null && this.fillGraphicFormat.length() > 0 ) {
            ExternalGraphic externalGraphic = StyleFactory.createExternalGraphic( this.fillGraphicUrl,
                                                                                  this.fillGraphicFormat );
            Graphic graphic = StyleFactory.createGraphic( externalGraphic, null, SldValues.getDefaultOpacity(),
                                                          SldValues.getDefaultSize(), SldValues.getDefaultRotation() );
            if ( Double.isNaN( fillGraphicSize ) || fillGraphicSize < 0 ) {
                graphic.setSize( null );
            } else {
                graphic.setSize( fillGraphicSize );
            }
            graphicFill = StyleFactory.createGraphicFill( graphic );
        }
        // fill-color
        HashMap<String, Object> fillParams = new HashMap<String, Object>();
        CssParameter fillColor;
        if ( fillColorDynamic != null ) {
            fillColor = getAsCssParameter( "fill", fillColorDynamic );
        } else {
            fillColor = StyleFactory.createCssParameter( "fill", ColorUtils.toHexCode( "#", this.fillColor ) );
        }
        fillParams.put( "fill", fillColor );
        // fill-transparency
        CssParameter fillTranspraency;
        if ( this.fillTransparencyDynamic != null ) {
            fillTranspraency = getAsCssParameter( "fill-opacity", fillTransparencyDynamic );
        } else {
            fillTranspraency = StyleFactory.createCssParameter( "fill-opacity", this.fillTransparency );
        }
        fillParams.put( "fill-opacity", fillTranspraency );

        return new Fill( fillParams, graphicFill );
    }
}
