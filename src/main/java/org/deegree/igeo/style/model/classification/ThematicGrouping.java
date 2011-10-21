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

package org.deegree.igeo.style.model.classification;

import java.util.List;

import org.deegree.igeo.style.model.DashArray;
import org.deegree.igeo.style.model.Fill;
import org.deegree.igeo.style.model.SldProperty;
import org.deegree.igeo.style.model.Symbol;

/**
 * <code>ThematicGrouping</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public interface ThematicGrouping<U extends Comparable<U>> {

    /**
     * @return the number of classes
     */
    public int getNumberOfClasses();

    /**
     * @param noOfClasses
     *            the number of classes to set
     */
    public void setNoOfClasses( int noOfClasses );

    /**
     * @return the title of the value column
     */
    public String getAttributeHeader();

    /**
     * @param color
     *            the fill color to set
     */
    public void setFillColor( Fill color );

    /**
     * @return the fill color
     */
    public Fill getFillColor();

    /**
     * @param doubleRange
     *            the double range to set for the fill transparency
     */
    public void setFillTransparency( IntegerRange integerRange );

    /**
     * 
     * @return the double range of the fill transparency
     */
    public IntegerRange getFillTransparency();

    /**
     * @param color
     *            the line color to set
     */
    public void setLineColor( Fill color );

    /**
     * @return the line color
     */
    public Fill getLineColor();

    /**
     * @param doubleRange
     *            the double range to set for the line transparency
     */
    public void setLineTransparency( IntegerRange doubleRange );

    /**
     * 
     * @return the double range of the line transparency
     */
    public IntegerRange getLineTransparency();

    /**
     * @param doubleRange
     *            the double range to set for the line width
     */
    public void setLineWidth( DoubleRange doubleRange );

    /**
     * 
     * @return the double range of the line width
     */

    public DoubleRange getLineWidth();

    /**
     * @return the line style
     */
    public DashArray getLineStyle();

    /**
     * @param dashArray
     *            the line style
     */
    public void setLineStyle( DashArray lineStyle );

    /**
     * @return the size
     */
    public DoubleRange getSize();

    /**
     * @param size
     *            the size
     */
    public void setSize( DoubleRange size );

    /**
     * @return the symbol
     */
    public Symbol getSymbol();

    /**
     * @param symbol
     *            the symbol
     */
    public void setSymbol( Symbol symbol );

    /**
     * @return the line cap
     */
    public SldProperty getLineCap();

    /**
     * @param lineCap
     *            the line cap
     */
    public void setLineCap( SldProperty lineCap );

    /**
     * @return
     * 
     */
    public Object getFontColor();

    /**
     * @param fontColor
     *            the font color
     */
    public void setFontColor( Object fontColor );

    /**
     * @return the font family
     */
    public Object getFontFamily();

    /**
     * @param fontFamily
     *            the font family
     */
    public void setFontFamily( Object fontFamily );

    /**
     * @return the font style
     */
    public Object getFontStyle();

    /**
     * @param fontStyle
     *            the font style
     */
    public void setFontStyle( Object fontStyle );

    /**
     * @return the font weight
     */
    public Object getFontWeight();

    /**
     * @param fontWeight
     *            the font weight
     */
    public void setFontWeight( Object fontWeight );

    /**
     * @return the font size
     */
    public Object getFontSize();

    /**
     * @param fontSize
     *            the font size
     */
    public void setFontSize( Object fontSize );

    /**
     * @return the ancor point
     */
    public Object getAnchorPoint();

    /**
     * @param anchor
     *            point the anchor point
     */
    public void setAnchorPoint( Object anchorPoint );

    /**
     * @return the rotation
     */
    public Object getRotation();

    /**
     * @param rotation
     *            the rotation
     */
    public void setRotation( Object rotation );

    /**
     * @return the displacement
     */
    public Object getDisplacement();

    /**
     * @param haloRadius
     *            the halo radius
     */
    public void setHaloRadius( Object haloRadius );

    /**
     * @return the haloRadius
     */
    public Object getHaloRadius();

    /**
     * @param haloColor
     *            the halo color
     */
    public void setHaloColor( Object haloColor );

    /**
     * @return the haloColor
     */
    public Object getHaloColor();

    /**
     * @param displacement
     *            the displacement
     */
    public void setDisplacement( Object displacement );

    /**
     * @return the values and their counts of the classification
     */
    public List<ValueRange<U>> getValues();

    /**
     * data means all single values to classify
     * 
     * @param data
     *            the data to set
     */
    public void setData( List<Intervallable<U>> data );

    /**
     * @return true, if the classification allows classes with the same min and max value
     */
    public boolean hasSameClassBorders();

    /**
     * @param values
     *            the values to update the count of values.
     */
    public void updateValueCounts( List<ValueRange<U>> values );

    /**
     * @return the number of values which should be classified; -1 if no datas are set
     */
    public int getNumberOfData();

    /**
     * @return the font transparency
     * 
     */
    public Object getFontTransparency();

    /**
     * @param fontTransparency
     *            the font transparency
     */
    public void setFontTransparency( Object fontTransparency );

}
