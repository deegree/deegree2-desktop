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

package org.deegree.desktop.style.model.classification;

import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.ANCHORPOINT;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.COUNT;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.DISPLACEMENT;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.FILLCOLOR;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.FILLTRANSPARENCY;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.FONTCOLOR;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.FONTFAMILY;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.FONTSIZE;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.FONTSTYLE;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.FONTTRANSPARENCY;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.FONTWEIGHT;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.HALOCOLOR;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.HALORADIUS;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.LINECAP;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.LINECOLOR;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.LINESTYLE;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.LINETRANSPARENCY;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.LINEWIDTH;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.ROTATION;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.SIZE;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.SYMBOL;
import static org.deegree.desktop.style.model.classification.Column.COLUMNTYPE.VALUE;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.style.model.DashArray;
import org.deegree.desktop.style.model.SldProperty;
import org.deegree.desktop.style.model.SldValues;
import org.deegree.desktop.style.model.Symbol;
import org.deegree.desktop.style.model.classification.Column.COLUMNTYPE;
import org.deegree.desktop.style.model.classification.ThematicGroupingInformation.GROUPINGTYPE;

/**
 * <code>ClassificationTableRow</code> represents one row in the classification table
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class ClassificationTableRow<U extends Comparable<U>> {

    private Map<COLUMNTYPE, Object> values = new HashMap<COLUMNTYPE, Object>();

    private String label;

    /**
     * Creates a new {@link ClassificationTableRow} instance with a value of the passed groupingType
     * 
     * @param groupingType
     *            the groupingType of the value of the new {@link ClassificationTableRow} instance
     */
    public ClassificationTableRow( GROUPINGTYPE groupingType ) {
        this( new ValueRange<U>( groupingType ) );
    }

    /**
     * 
     * @param value
     * @param fillColor
     */
    public ClassificationTableRow( ValueRange<U> value ) {
        values.put( VALUE, value );
        values.put( FILLCOLOR, SldValues.getDefaultColor() );
        values.put( FILLTRANSPARENCY, SldValues.getOpacityInPercent( SldValues.getDefaultOpacity() ) );
        values.put( LINECOLOR, SldValues.getDefaultLineColor() );
        values.put( LINETRANSPARENCY, SldValues.getOpacityInPercent( SldValues.getDefaultOpacity() ) );
        values.put( LINEWIDTH, SldValues.getDefaultLineWidth() );
        values.put( LINESTYLE, SldValues.getDefaultLineStyle() );
        values.put( SIZE, SldValues.getDefaultSize() );
        values.put( SYMBOL, SldValues.getDefaultWKM() );
        values.put( LINECAP, SldValues.getDefaultLineCapAsProperty() );
        values.put( FONTTRANSPARENCY, SldValues.getOpacityInPercent( SldValues.getDefaultOpacity() ) );
        values.put( FONTCOLOR, SldValues.getDefaultFontColor() );
        values.put( FONTFAMILY, SldValues.getDefaultFontFamily() );
        values.put( FONTWEIGHT, SldValues.getDefaultFontWeight() );
        values.put( FONTSTYLE, SldValues.getDefaultFontStyle() );
        values.put( FONTSIZE, SldValues.getDefaultFontSize() );
        values.put( HALOCOLOR, SldValues.getDefaultHaloColor() );
        values.put( HALORADIUS, SldValues.getDefaultHaloRadius() );
        values.put( ANCHORPOINT, SldValues.getDefaultAnchorPoint() );
        values.put( DISPLACEMENT, SldValues.getDefaultDisplacement() );
        values.put( ROTATION, SldValues.getDefaultRotation() );
    }

    /**
     * @return the value
     */
    @SuppressWarnings("unchecked")
    public ValueRange<U> getValue() {
        if ( values.get( VALUE ) instanceof ValueRange<?> ) {
            return (ValueRange<U>) values.get( VALUE );
        }
        return null;
    }

    /**
     * @return the fill color
     */
    public Object getFillColor() {
        return values.get( FILLCOLOR );
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue( ValueRange<U> value ) {
        this.values.put( COLUMNTYPE.VALUE, value );
    }

    /**
     * @param fill
     *            color the fill color (<code>java.awt.Color</code> or
     *            <code>org.deegree.igeo.style.model.GraphicSymbol</code>) to set
     */
    public void setFillColor( Object color ) {
        this.values.put( COLUMNTYPE.FILLCOLOR, color );
    }

    /**
     * @return the fillTransparency
     */
    public int getFillTransparency() {
        if ( values.get( FILLTRANSPARENCY ) instanceof Integer ) {
            return (Integer) values.get( FILLTRANSPARENCY );
        }
        return SldValues.getOpacityInPercent( SldValues.getDefaultOpacity() );
    }

    /**
     * @param fillTransparency
     *            the fillTransparency to set
     */
    public void setFillTransparency( int fillTransparency ) {
        this.values.put( COLUMNTYPE.FILLTRANSPARENCY, fillTransparency );
    }

    /**
     * @return the lineColor
     */
    public Color getLineColor() {
        if ( values.get( LINECOLOR ) instanceof Color ) {
            return (Color) values.get( LINECOLOR );
        }
        return SldValues.getDefaultLineColor();
    }

    /**
     * @param lineColor
     *            the lineColor to set
     */
    public void setLineColor( Color lineColor ) {
        this.values.put( COLUMNTYPE.LINECOLOR, lineColor );
    }

    /**
     * @return the lineTransparency
     */
    public int getLineTransparency() {
        if ( values.get( LINETRANSPARENCY ) instanceof Integer ) {
            return (Integer) values.get( LINETRANSPARENCY );
        }
        return SldValues.getOpacityInPercent( SldValues.getDefaultOpacity() );
    }

    /**
     * @param lineTransparency
     *            the lineTransparency to set
     */
    public void setLineTransparency( int lineTransparency ) {
        this.values.put( COLUMNTYPE.LINETRANSPARENCY, lineTransparency );
    }

    /**
     * @return the lineWidth
     */
    public double getLineWidth() {
        if ( values.get( LINEWIDTH ) instanceof Double ) {
            return (Double) values.get( LINEWIDTH );
        }
        return SldValues.getDefaultLineWidth();
    }

    /**
     * @param lineWidth
     *            the lineWidth to set
     */
    public void setLineWidth( double lineWidth ) {
        this.values.put( COLUMNTYPE.LINEWIDTH, lineWidth );
    }

    /**
     * @param lineStyle
     *            the line style to set
     */
    public void setLineStyle( DashArray lineStyle ) {
        this.values.put( COLUMNTYPE.LINESTYLE, lineStyle );
    }

    /**
     * @return the line style
     */
    public DashArray getLineStyle() {
        if ( values.get( LINESTYLE ) instanceof DashArray ) {
            return (DashArray) values.get( LINESTYLE );
        }
        return SldValues.getDefaultLineStyle();
    }

    /**
     * @return the size
     */
    public double getSize() {
        if ( values.get( SIZE ) instanceof Double ) {
            return (Double) values.get( SIZE );
        }
        return SldValues.getDefaultSize();
    }

    /**
     * @param size
     *            the size to set
     */
    public void setSize( double size ) {
        this.values.put( COLUMNTYPE.SIZE, size );
    }

    /**
     * @return the symbol
     */
    public Symbol getSymbol() {
        if ( values.get( SYMBOL ) instanceof Symbol ) {
            return (Symbol) values.get( SYMBOL );
        }
        return SldValues.getDefaultWKM();
    }

    /**
     * @param symbol
     *            the symbol to set
     */
    public void setSymbol( Symbol symbol ) {
        this.values.put( COLUMNTYPE.SYMBOL, symbol );
    }

    /**
     * @return the lineCap
     */
    public SldProperty getLineCap() {
        if ( values.get( LINECAP ) instanceof SldProperty ) {
            return (SldProperty) values.get( LINECAP );
        }
        return SldValues.getDefaultLineCapAsProperty();
    }

    /**
     * @param lineCap
     *            the lineCap to set
     */
    public void setLineCap( SldProperty lineCap ) {
        this.values.put( COLUMNTYPE.LINECAP, lineCap );
    }

    @SuppressWarnings("unchecked")
    public Object getValue( COLUMNTYPE column ) {
        if ( column.equals( COUNT ) && values.get( VALUE ) != null ) {
            return ( (ValueRange<U>) values.get( VALUE ) ).getCount();
        }
        return values.get( column );
    }

    public boolean isQualidfiedName( COLUMNTYPE column ) {
        Object value = values.get( column );
        if ( value != null && value instanceof QualifiedName ) {
            return true;
        }
        return false;
    }

    /**
     * @return the label of the row, if the label is null, the toolTip of the value will be returned
     */
    public String getLabel() {
        if ( label == null && values.get( VALUE ) != null && values.get( VALUE ) instanceof ValueRange<?> ) {
            return ( (ValueRange<?>) values.get( VALUE ) ).getToolTip();
        }
        return label;
    }

    /**
     * @param label
     *            the label to set
     */
    public void setLabel( String label ) {
        this.label = label;
    }

    public void setValue( COLUMNTYPE column, Object value ) {
        values.put( column, value );
    }

}
