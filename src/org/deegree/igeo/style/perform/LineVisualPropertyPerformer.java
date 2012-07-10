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
import org.deegree.graphics.sld.Stroke;
import org.deegree.graphics.sld.StyleFactory;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.igeo.style.model.DashArray;
import org.deegree.igeo.style.model.SldProperty;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.utils.SldCreatorUtils;
import org.deegree.model.filterencoding.PropertyName;

/**
 * <code>LineVisualPropertyPerformer</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class LineVisualPropertyPerformer extends AbstractVisualPropertyPerformer {

    private Color color = SldValues.getDefaultLineColor();

    private QualifiedName colorDynamic;

    private double transparency = SldValues.getDefaultOpacity();

    private QualifiedName transparencyDynamic;

    private UnitsValue width;

    private QualifiedName widthDynamic;

    private int lineCap = SldValues.getDefaultLineCapCode();

    private float[] dashArray = null;

    public Symbolizer getSymbolizer( boolean isInPixel ) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        // stroke-width
        CssParameter widthParam;
        if ( widthDynamic != null ) {
            if ( !isInPixel ) {
                widthParam = new CssParameter( "stroke-width",
                                               SldCreatorUtils.getParameterValueType( new PropertyName( widthDynamic ) ) );
            } else {
                widthParam = getAsCssParameter( "stroke-width", widthDynamic );
            }
        } else {
            if ( width != null ) {
                widthParam = width.getAsCssParameter( "stroke-width" );
            } else {
                widthParam = StyleFactory.createCssParameter( "stroke-width", SldValues.getDefaultLineWidth() );
            }
        }
        params.put( "stroke-width", widthParam );

        // stroke-color
        CssParameter colorParam;
        if ( colorDynamic != null ) {
            colorParam = getAsCssParameter( "stroke", colorDynamic );
        } else {
            colorParam = StyleFactory.createCssParameter( "stroke", ColorUtils.toHexCode( "#", this.color ) );
        }
        params.put( "stroke", colorParam );

        // stroke-opacity
        CssParameter opacityParam;
        if ( transparencyDynamic != null ) {
            opacityParam = getAsCssParameter( "stroke-opacity", transparencyDynamic );
        } else {
            opacityParam = StyleFactory.createCssParameter( "stroke-opacity", this.transparency );
        }
        params.put( "stroke-opacity", opacityParam );

        Stroke stroke = new Stroke( params, null, null );
        stroke.setLineCap( this.lineCap );

        if ( dashArray != null ) {
            stroke.setDashArray( this.dashArray );
        }
        return StyleFactory.createLineSymbolizer( stroke );
    }

    public void update( StyleChangedEvent changeEvent ) {
        Object value = changeEvent.getValue();
        switch ( changeEvent.getType() ) {
        case COLOR:
            if ( value instanceof Color ) {
                this.color = (Color) value;
                this.colorDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                this.colorDynamic = (QualifiedName) value;
            }
            break;
        case OPACITY:
            if ( value instanceof Double ) {
                this.transparency = (Double) value;
                this.transparencyDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                this.transparencyDynamic = (QualifiedName) value;
            }
            break;
        case LINEWIDTH:
            if ( value instanceof UnitsValue ) {
                this.width = (UnitsValue) value;
                this.widthDynamic = null;
            } else if ( value instanceof QualifiedName ) {
                this.widthDynamic = (QualifiedName) value;
            }
            break;
        case LINECAP:
            if ( value instanceof SldProperty ) {
                this.lineCap = ( (SldProperty) value ).getTypeCode();
            }
            break;
        case LINEARRAY:
            if ( value instanceof DashArray ) {
                this.dashArray = ( (DashArray) value ).getDashArray();
            }
            break;
        }

    }

    @Override
    int getSize() {
        return 70;
    }

}
