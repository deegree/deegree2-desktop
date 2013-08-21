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

import java.util.ArrayList;
import java.util.List;

import org.deegree.desktop.style.model.DashArray;
import org.deegree.desktop.style.model.Fill;
import org.deegree.desktop.style.model.FillColor;
import org.deegree.desktop.style.model.SldProperty;
import org.deegree.desktop.style.model.SldValues;
import org.deegree.desktop.style.model.Symbol;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;

/**
 * <code>AbstractThematicGrouping</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public abstract class AbstractThematicGrouping<U extends Comparable<U>> implements ThematicGrouping<U> {

    private static final ILogger LOG = LoggerFactory.getLogger( AbstractThematicGrouping.class );

    private Fill fillColor;

    private IntegerRange fillTransparency;

    private Fill lineColor;

    private IntegerRange lineTransparency;

    private DoubleRange lineWidth;

    private DashArray lineStyle;

    private DoubleRange size;

    protected List<Intervallable<U>> data = new ArrayList<Intervallable<U>>();

    private Symbol symbol;

    private SldProperty lineCap;

    private Object fontFamily;

    private Object fontWeight;

    private Object fontStyle;

    private Object fontSize;

    private Object haloRadius;

    private Object haloColor;

    private Object anchorPoint;

    private Object displacement;

    private Object rotation;

    private Object fontColor;

    private Object fontTransparency;

    public AbstractThematicGrouping() {
        setFillColor( new FillColor( SldValues.getDefaultColor() ) );
        int transparency = SldValues.getOpacityInPercent( SldValues.getDefaultOpacity() );
        setFillTransparency( new SingleInteger( transparency ) );
        setLineColor( new FillColor( SldValues.getDefaultLineColor() ) );
        setLineTransparency( new SingleInteger( transparency ) );
        setLineWidth( new SingleDouble( SldValues.getDefaultLineWidth() ) );
        setLineStyle( SldValues.getDefaultLineStyle() );
        setLineCap( SldValues.getDefaultLineCapAsProperty() );
        setSize( new SingleDouble( SldValues.getDefaultSize() ) );
        setSymbol( SldValues.getDefaultWKM() );
        setFontFamily( SldValues.getDefaultFontFamily() );
        setFontWeight( SldValues.getDefaultFontWeight() );
        setFontStyle( SldValues.getDefaultFontStyle() );
        setFontSize( SldValues.getDefaultFontSize() );
        setFontColor( SldValues.getDefaultFontColor() );
        setFontTransparency( new SingleInteger( transparency ) );
    }

    public void setFillColor( Fill color ) {
        this.fillColor = color;
    }

    public void setFillTransparency( IntegerRange integerRange ) {
        this.fillTransparency = integerRange;
    }

    public void setLineColor( Fill color ) {
        this.lineColor = color;
    }

    public void setLineTransparency( IntegerRange integerRange ) {
        this.lineTransparency = integerRange;

    }

    public void setLineWidth( DoubleRange doubleRange ) {
        this.lineWidth = doubleRange;
    }

    public void setLineStyle( DashArray lineStyle ) {
        this.lineStyle = lineStyle;
    }

    public void setSize( DoubleRange size ) {
        this.size = size;
    }

    public void setSymbol( Symbol symbol ) {
        this.symbol = symbol;
    }

    public void setLineCap( SldProperty lineCap ) {
        this.lineCap = lineCap;
    }

    public Fill getFillColor() {
        return fillColor;
    }

    public IntegerRange getFillTransparency() {
        return fillTransparency;
    }

    public Fill getLineColor() {
        return lineColor;
    }

    public IntegerRange getLineTransparency() {
        return lineTransparency;
    }

    public DoubleRange getLineWidth() {
        return lineWidth;
    }

    public DashArray getLineStyle() {
        return lineStyle;
    }

    public DoubleRange getSize() {
        return size;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public SldProperty getLineCap() {
        return lineCap;
    }

    public Object getFontColor() {
        return fontColor;
    }

    public void setFontColor( Object fontColor ) {
        this.fontColor = fontColor;
    }

    public Object getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily( Object fontFamily ) {
        this.fontFamily = fontFamily;
    }

    public Object getFontSize() {
        return fontSize;
    }

    public Object getFontStyle() {
        return fontStyle;
    }

    public Object getFontWeight() {
        return fontWeight;
    }

    public void setFontSize( Object fontSize ) {
        this.fontSize = fontSize;
    }

    public void setFontStyle( Object fontStyle ) {
        this.fontStyle = fontStyle;
    }

    public void setFontWeight( Object fontWeight ) {
        this.fontWeight = fontWeight;
    }

    public Object getFontTransparency() {
        return fontTransparency;
    }

    public void setFontTransparency( Object fontTransparency ) {
        this.fontTransparency = fontTransparency;
    }

    public Object getAnchorPoint() {
        return anchorPoint;
    }

    public Object getDisplacement() {
        return displacement;
    }

    public Object getHaloColor() {
        return haloColor;
    }

    public Object getHaloRadius() {
        return haloRadius;
    }

    public Object getRotation() {
        return rotation;
    }

    public void setAnchorPoint( Object anchorPoint ) {
        this.anchorPoint = anchorPoint;
    }

    public void setDisplacement( Object displacement ) {
        this.displacement = displacement;
    }

    public void setHaloColor( Object haloColor ) {
        this.haloColor = haloColor;
    }

    public void setHaloRadius( Object haloRadius ) {
        this.haloRadius = haloRadius;
    }

    public void setRotation( Object rotation ) {
        this.rotation = rotation;
    }

    public void setData( List<Intervallable<U>> data ) {
        this.data = data;
    }

    public int getNumberOfData() {
        if ( data != null ) {
            return data.size();
        }
        return -1;
    }

    private ValueCountUpdater updater;

    public void updateValueCounts( List<ValueRange<U>> values ) {
        if ( updater != null ) {
            updater.stopped = true;
            while ( updater.isAlive() ) {
                try {
                    Thread.sleep( 50 );
                } catch ( InterruptedException e ) {
                    LOG.logError( "ValueCountUpdater is interrupted: " + e.getMessage() );
                    e.printStackTrace();
                }
            }
        }

        updater = new ValueCountUpdater( values );
        updater.start();
    }

    private class ValueCountUpdater extends Thread {

        private boolean stopped = false;

        private List<ValueRange<U>> values;

        private ValueCountUpdater( List<ValueRange<U>> values ) {
            this.values = values;
        }

        @Override
        public void run() {
            if ( values != null ) {
                // reset value count to 0
                for ( ValueRange<U> value : values ) {
                    value.setCount( 0 );
                }
                // and update value count
                for ( Intervallable<U> intervallable : data ) {
                    if ( !stopped ) {
                        for ( ValueRange<U> value : values ) {
                            if ( value.isInThisValueRange( intervallable ) ) {
                                value.increaseCount();
                            }
                        }
                    }
                }
            }
        }

    }

}
