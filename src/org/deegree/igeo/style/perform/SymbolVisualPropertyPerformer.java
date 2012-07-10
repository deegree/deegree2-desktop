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
import java.util.HashMap;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.util.ColorUtils;
import org.deegree.graphics.sld.CssParameter;
import org.deegree.graphics.sld.Fill;
import org.deegree.graphics.sld.Graphic;
import org.deegree.graphics.sld.ParameterValueType;
import org.deegree.graphics.sld.PointSymbolizer;
import org.deegree.graphics.sld.Stroke;
import org.deegree.graphics.sld.StyleFactory;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.igeo.style.model.GraphicSymbol;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.model.WellKnownMark;
import org.deegree.igeo.style.utils.SldCreatorUtils;
import org.deegree.model.filterencoding.Expression;
import org.deegree.model.filterencoding.PropertyName;

/**
 * The <code>SymbolVisualPropertyPerformer</code> manages the definition of a Symbol, represented by one ore more
 * sld-rules
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class SymbolVisualPropertyPerformer extends AbstractVisualPropertyPerformer {

    private static enum TYPE {
        WKM, URL, DYNAMIC
    };

    private TYPE markType = TYPE.WKM;

    private GraphicSymbol symbol;

    private WellKnownMark wkm;

    private QualifiedName symbolDynamic;

    private Color fillColor = SldValues.getDefaultColor();

    private QualifiedName fillColorDynamic;

    private Color lineColor = SldValues.getDefaultLineColor();

    private QualifiedName lineColorDynamic;

    private double opacity = SldValues.getDefaultOpacity();

    private QualifiedName opacityDynamic;

    private double rotation = SldValues.getDefaultRotation();

    private QualifiedName rotationDynamic;

    private UnitsValue size;

    private QualifiedName sizeDynamic;

    public Symbolizer getSymbolizer( boolean isInPixel ) {

        PointSymbolizer ps = StyleFactory.createPointSymbolizer();
        Object graphicObject = null;
        switch ( markType ) {
        case WKM:
            String wkmName;
            if ( wkm != null && wkm.getSldName() != null ) {
                wkmName = wkm.getSldName();
            } else {
                wkmName = SldValues.getDefaultWKM().getSldName();
            }
            graphicObject = StyleFactory.createMark( wkmName, createFill(), createStroke() );
            break;
        case URL:
            if ( symbol != null && symbol.getUrl() != null && symbol.getFormat() != null ) {
                graphicObject = StyleFactory.createExternalGraphic( symbol.getUrl(), symbol.getFormat() );
            }
            break;
        case DYNAMIC:
            // set as default wellknownmark, graphic is handled in fill, but placement must be set in the graphic
            // object!
            graphicObject = StyleFactory.createMark( SldValues.getDefaultWKM().getSldName(), createFill(),
                                                     createStroke() );
            break;
        }

        if ( graphicObject != null ) {
            ParameterValueType sizePVT = null;
            if ( sizeDynamic != null ) {
                if ( !isInPixel ) {
                    sizePVT = SldCreatorUtils.getParameterValueType( new PropertyName( sizeDynamic ) );
                } else {
                    sizePVT = StyleFactory.createParameterValueType( new Expression[] { new PropertyName( sizeDynamic ) } );
                }
            } else if ( size != null ) {
                try {
                    sizePVT = size.getAsParameterValueType();
                } catch ( Exception e ) {
                    // Nothing to do
                }
            }
            if ( sizePVT == null ) {
                sizePVT = StyleFactory.createParameterValueType( SldValues.getDefaultSize() );
            }
            ParameterValueType opacityPVT = StyleFactory.createParameterValueType( opacity );
            ParameterValueType rotationPVT;
            if ( rotationDynamic != null ) {
                rotationPVT = StyleFactory.createParameterValueType( new Expression[] { new PropertyName(
                                                                                                          rotationDynamic ) } );
            } else {
                rotationPVT = StyleFactory.createParameterValueType( rotation );
            }
            Graphic graphic = new Graphic( new Object[] { graphicObject }, opacityPVT, sizePVT, rotationPVT );
            ps.setGraphic( graphic );
        }
        return ps;
    }

    private Stroke createStroke() {
        HashMap<String, Object> strokeParams = new HashMap<String, Object>();

        // stroke-color
        CssParameter lineColorParam;
        if ( lineColorDynamic != null ) {
            lineColorParam = getAsCssParameter( "stroke", lineColorDynamic );
        } else {
            lineColorParam = StyleFactory.createCssParameter( "stroke", ColorUtils.toHexCode( "#", lineColor ) );
        }
        strokeParams.put( "stroke", lineColorParam );
        return new Stroke( strokeParams, null, null );
    }

    private Fill createFill() {
        // fill-color
        HashMap<String, Object> fillParams = new HashMap<String, Object>();
        CssParameter fillColorParam;
        if ( fillColorDynamic != null ) {
            fillColorParam = getAsCssParameter( "fill", fillColorDynamic );
        } else {
            fillColorParam = StyleFactory.createCssParameter( "fill", ColorUtils.toHexCode( "#", fillColor ) );
        }
        fillParams.put( "fill", fillColorParam );
        // fill-transparency
        CssParameter fillTranspraency;
        if ( opacityDynamic != null ) {
            fillTranspraency = getAsCssParameter( "fill-opacity", opacityDynamic );
        } else {
            fillTranspraency = StyleFactory.createCssParameter( "fill-opacity", opacity );
        }
        fillParams.put( "fill-opacity", fillTranspraency );

        // set symbol if dynamic
        if ( markType == TYPE.DYNAMIC ) {
            fillParams.put( "symbol", getAsCssParameter( "symbol", symbolDynamic ) );
        }
        return new Fill( fillParams, null );
    }

    public void update( StyleChangedEvent changeEvent ) {
        ComponentType type = changeEvent.getType();
        Object value = changeEvent.getValue();

        switch ( type ) {
        case OPACITY:
            if ( value instanceof Double ) {
                opacity = (Double) value;
                opacityDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                opacityDynamic = (QualifiedName) value;
            }
            break;
        case SIZE:
            if ( value instanceof UnitsValue ) {
                size = (UnitsValue) value;
                sizeDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                sizeDynamic = (QualifiedName) value;
            }
            break;
        case ROTATION:
            if ( value instanceof Double ) {
                rotation = (Double) value;
                rotationDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                rotationDynamic = (QualifiedName) value;
            }
            break;
        case MARK:
            if ( value instanceof WellKnownMark ) {
                wkm = (WellKnownMark) value;
                markType = TYPE.WKM;
            } else if ( value instanceof GraphicSymbol ) {
                symbol = (GraphicSymbol) value;
                markType = TYPE.URL;
            } else if ( value instanceof QualifiedName ) {
                symbolDynamic = (QualifiedName) value;
                markType = TYPE.DYNAMIC;
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
        case COLOR:
            if ( value instanceof Color ) {
                lineColor = (Color) value;
                lineColorDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                lineColorDynamic = (QualifiedName) value;
            }
            break;
        }
    }

    @Override
    int getSize() {
        double s = SldValues.getDefaultSize();
        if ( size != null ) {
            s = size.getValue();
        }
        Double symbolSize = s * 1.5;
        return symbolSize.intValue();
    }

}
