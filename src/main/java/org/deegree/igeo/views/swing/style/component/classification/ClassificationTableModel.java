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

package org.deegree.igeo.views.swing.style.component.classification;

import static org.deegree.igeo.style.model.classification.Column.COLUMNTYPE.VALUE;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import org.deegree.graphics.sld.Rule;
import org.deegree.igeo.style.classification.SldFromClassification;
import org.deegree.igeo.style.model.DashArray;
import org.deegree.igeo.style.model.Fill;
import org.deegree.igeo.style.model.FillColor;
import org.deegree.igeo.style.model.LinearGradient;
import org.deegree.igeo.style.model.SldProperty;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.model.Symbol;
import org.deegree.igeo.style.model.classification.ClassificationTableRow;
import org.deegree.igeo.style.model.classification.ClassificationTableRowComparator;
import org.deegree.igeo.style.model.classification.Column;
import org.deegree.igeo.style.model.classification.DoubleRange;
import org.deegree.igeo.style.model.classification.IntegerRamp;
import org.deegree.igeo.style.model.classification.IntegerRange;
import org.deegree.igeo.style.model.classification.Intervallable;
import org.deegree.igeo.style.model.classification.SingleDouble;
import org.deegree.igeo.style.model.classification.SingleInteger;
import org.deegree.igeo.style.model.classification.ThematicGrouping;
import org.deegree.igeo.style.model.classification.ValueRange;
import org.deegree.igeo.style.model.classification.Column.COLUMNTYPE;
import org.deegree.igeo.views.swing.style.StyleDialog;
import org.deegree.igeo.views.swing.style.component.classification.AbstractClassificationPanel.SYMBOLIZERTYPE;
import org.deegree.igeo.views.swing.style.editor.ClassificationValuesEditor;
import org.deegree.model.filterencoding.PropertyName;

/**
 * <code>ClassificationTableModel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class ClassificationTableModel<U extends Comparable<U>> extends AbstractTableModel implements CellEditorListener {

    private static final long serialVersionUID = 7835812908636984046L;

    private SldFromClassification<U> sldClassificationCalculator;

    private ThematicGrouping<U> thematicGrouping;

    private List<ClassificationTableRow<U>> rows = new ArrayList<ClassificationTableRow<U>>();

    private List<ClassificationTableRow<U>> oldRows = new ArrayList<ClassificationTableRow<U>>();

    private List<Column> columns = new ArrayList<Column>();

    private StyleDialog styleDialog;

    /**
     * @param thematicGrouping
     *            the classification
     */
    public ClassificationTableModel( List<Column> columns, StyleDialog styleDialog ) {
        this.styleDialog = styleDialog;
        this.columns = columns;
        sldClassificationCalculator = new SldFromClassification<U>();
    }

    /**
     * updates the column with the given index
     * 
     * @param columnIndex
     *            the index of the column to update
     * @param updateTable
     *            true, if the table should be redrawn
     */
    public void update( COLUMNTYPE type, boolean updateTable ) {
        if ( thematicGrouping != null ) {
            switch ( type ) {
            case VALUE:
                oldRows.addAll( rows );
                List<ValueRange<U>> values = thematicGrouping.getValues();
                rows.clear();
                for ( ValueRange<U> value : values ) {
                    rows.add( new ClassificationTableRow<U>( value ) );
                }
                sort( COLUMNTYPE.VALUE, false );
                // update all other components too!
                update( COLUMNTYPE.FILLCOLOR, false );
                update( COLUMNTYPE.FILLTRANSPARENCY, false );
                update( COLUMNTYPE.LINECOLOR, false );
                update( COLUMNTYPE.LINETRANSPARENCY, false );
                update( COLUMNTYPE.LINEWIDTH, false );
                update( COLUMNTYPE.SIZE, false );
                update( COLUMNTYPE.SYMBOL, false );
                update( COLUMNTYPE.LINECAP, false );
                update( COLUMNTYPE.LINESTYLE, false );
                update( COLUMNTYPE.FONTCOLOR, false );
                update( COLUMNTYPE.FONTFAMILY, false );
                update( COLUMNTYPE.FONTWEIGHT, false );
                update( COLUMNTYPE.FONTSTYLE, false );
                update( COLUMNTYPE.FONTSIZE, false );
                update( COLUMNTYPE.ANCHORPOINT, false );
                update( COLUMNTYPE.DISPLACEMENT, false );
                update( COLUMNTYPE.ROTATION, false );
                update( COLUMNTYPE.HALOCOLOR, false );
                update( COLUMNTYPE.HALORADIUS, false );
                update( COLUMNTYPE.FONTTRANSPARENCY, false );
                oldRows.clear();
                break;
            case FILLCOLOR:
                Fill fillColor = thematicGrouping.getFillColor();
                if ( fillColor != null ) {
                    if ( fillColor instanceof FillColor
                         && SldValues.getDefaultColor().equals( ( (FillColor) fillColor ).getColor() ) ) {
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            if ( oldRows.size() > 0 && i < oldRows.size() ) {
                                rows.get( i ).setFillColor( oldRows.get( i ).getFillColor() );
                            } else if ( oldRows.size() > 0 ) {
                                rows.get( i ).setFillColor( oldRows.get( oldRows.size() - 1 ).getFillColor() );
                            }
                        }
                    } else {
                        List<Object> colors = thematicGrouping.getFillColor().getFills( getRowCount() );
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            rows.get( i ).setFillColor( colors.get( i ) );
                        }
                    }
                }
                break;
            case FILLTRANSPARENCY:
                IntegerRange fillTransparency = thematicGrouping.getFillTransparency();
                if ( fillTransparency != null ) {
                    if ( fillTransparency instanceof SingleInteger
                         && SldValues.getOpacityInPercent( SldValues.getDefaultOpacity() ) == fillTransparency.getMax() ) {
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            if ( oldRows.size() > 0 && i < oldRows.size() ) {
                                rows.get( i ).setFillTransparency( oldRows.get( i ).getFillTransparency() );
                            } else if ( oldRows.size() > 0 ) {
                                rows.get( i ).setFillTransparency(
                                                                   oldRows.get( oldRows.size() - 1 ).getFillTransparency() );
                            }
                        }
                    } else {
                        List<Integer> integers = fillTransparency.getIntegers( getRowCount() );
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            rows.get( i ).setFillTransparency( integers.get( i ) );
                        }
                    }
                }
                break;
            case LINECOLOR:
                Fill lineColor = thematicGrouping.getLineColor();
                if ( lineColor != null ) {
                    if ( lineColor instanceof FillColor
                         && SldValues.getDefaultLineColor().equals( ( (FillColor) lineColor ).getColor() ) ) {
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            if ( oldRows.size() > 0 && i < oldRows.size() ) {
                                rows.get( i ).setLineColor( oldRows.get( i ).getLineColor() );
                            } else if ( oldRows.size() > 0 ) {
                                rows.get( i ).setLineColor( oldRows.get( oldRows.size() - 1 ).getLineColor() );
                            }
                        }
                    } else {

                        List<Object> colors = lineColor.getFills( getRowCount() );
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            rows.get( i ).setLineColor( (Color) colors.get( i ) );
                        }
                    }
                }
                break;
            case LINETRANSPARENCY:
                IntegerRange lineTransparency = thematicGrouping.getLineTransparency();
                if ( lineTransparency != null ) {
                    if ( lineTransparency instanceof SingleInteger
                         && SldValues.getOpacityInPercent( SldValues.getDefaultOpacity() ) == lineTransparency.getMax() ) {
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            if ( oldRows.size() > 0 && i < oldRows.size() ) {
                                rows.get( i ).setLineTransparency( oldRows.get( i ).getLineTransparency() );
                            } else if ( oldRows.size() > 0 ) {
                                rows.get( i ).setLineTransparency(
                                                                   oldRows.get( oldRows.size() - 1 ).getLineTransparency() );
                            }
                        }
                    } else {
                        List<Integer> integers = lineTransparency.getIntegers( getRowCount() );
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            rows.get( i ).setLineTransparency( integers.get( i ) );
                        }
                    }
                }
                break;
            case LINEWIDTH:
                DoubleRange lineWidth = thematicGrouping.getLineWidth();
                if ( lineWidth != null ) {
                    if ( lineWidth instanceof SingleDouble && SldValues.getDefaultLineWidth() == lineWidth.getMax() ) {
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            if ( oldRows.size() > 0 && i < oldRows.size() ) {
                                rows.get( i ).setLineWidth( oldRows.get( i ).getLineWidth() );
                            } else if ( oldRows.size() > 0 ) {
                                rows.get( i ).setLineWidth( oldRows.get( oldRows.size() - 1 ).getLineWidth() );
                            }
                        }
                    } else {
                        List<Double> doubles = lineWidth.getDoubles( getRowCount(), 2 );
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            rows.get( i ).setLineWidth( doubles.get( i ) );
                        }
                    }
                }
                break;
            case LINESTYLE:
                DashArray lineStyle = thematicGrouping.getLineStyle();
                if ( lineStyle != null ) {
                    if ( SldValues.getDefaultLineStyle().equals( lineStyle ) ) {
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            if ( oldRows.size() > 0 && i < oldRows.size() ) {
                                rows.get( i ).setLineStyle( oldRows.get( i ).getLineStyle() );
                            } else if ( oldRows.size() > 0 ) {
                                rows.get( i ).setLineStyle( oldRows.get( oldRows.size() - 1 ).getLineStyle() );
                            }
                        }
                    } else {
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            rows.get( i ).setLineStyle( lineStyle );
                        }
                    }
                }
                break;
            case SIZE:
                DoubleRange size = thematicGrouping.getSize();
                if ( size != null ) {
                    if ( size instanceof SingleDouble && SldValues.getDefaultSize() == size.getMax() ) {
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            if ( oldRows.size() > 0 && i < oldRows.size() ) {
                                rows.get( i ).setSize( oldRows.get( i ).getSize() );
                            } else if ( oldRows.size() > 0 ) {
                                rows.get( i ).setSize( oldRows.get( oldRows.size() - 1 ).getSize() );
                            }
                        }
                    } else {
                        List<Double> doubles = size.getDoubles( getRowCount(), 2 );
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            rows.get( i ).setSize( doubles.get( i ) );
                        }
                    }
                }
                break;
            case SYMBOL:
                Symbol symbol = thematicGrouping.getSymbol();
                if ( symbol != null ) {
                    if ( SldValues.getDefaultWKM().equals( symbol ) ) {
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            if ( oldRows.size() > 0 && i < oldRows.size() ) {
                                rows.get( i ).setSymbol( oldRows.get( i ).getSymbol() );
                            } else if ( oldRows.size() > 0 ) {
                                rows.get( i ).setSymbol( oldRows.get( oldRows.size() - 1 ).getSymbol() );
                            }
                        }
                    } else {
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            rows.get( i ).setSymbol( symbol );
                        }
                    }
                }
                break;
            case LINECAP:
                SldProperty lineCap = thematicGrouping.getLineCap();
                if ( lineCap != null ) {
                    if ( SldValues.getDefaultLineCapAsProperty().equals( lineCap ) ) {
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            if ( oldRows.size() > 0 && i < oldRows.size() ) {
                                rows.get( i ).setLineCap( oldRows.get( i ).getLineCap() );
                            } else if ( oldRows.size() > 0 ) {
                                rows.get( i ).setLineCap( oldRows.get( oldRows.size() - 1 ).getLineCap() );
                            }
                        }
                    } else {
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            rows.get( i ).setLineCap( lineCap );
                        }
                    }
                }
                break;
            case FONTCOLOR:
                Object fontColor = thematicGrouping.getFontColor();
                if ( fontColor != null ) {
                    if ( fontColor instanceof FillColor
                         && SldValues.getDefaultFontColor().equals( ( (FillColor) fontColor ).getColor() ) ) {
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            if ( oldRows.size() > 0 && i < oldRows.size() ) {
                                rows.get( i ).setValue( COLUMNTYPE.FONTCOLOR,
                                                        oldRows.get( i ).getValue( COLUMNTYPE.FONTCOLOR ) );
                            } else if ( oldRows.size() > 0 ) {
                                rows.get( i ).setValue(
                                                        COLUMNTYPE.FONTCOLOR,
                                                        oldRows.get( oldRows.size() - 1 ).getValue(
                                                                                                    COLUMNTYPE.FONTCOLOR ) );
                            }
                        }
                    } else if ( fontColor instanceof Fill ) {
                        List<Object> colors = ( (Fill) fontColor ).getFills( getRowCount() );
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            rows.get( i ).setValue( COLUMNTYPE.FONTCOLOR, (Color) colors.get( i ) );
                        }
                    } else if ( fontColor instanceof PropertyName ) {
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            rows.get( i ).setValue( COLUMNTYPE.FONTCOLOR, fontColor );
                        }
                    }
                }
                break;
            case FONTFAMILY:
                Object fontFamily = thematicGrouping.getFontFamily();
                if ( fontFamily != null ) {
                    for ( int i = 0; i < getRowCount(); i++ ) {
                        rows.get( i ).setValue( COLUMNTYPE.FONTFAMILY, fontFamily );
                    }
                }
                break;
            case FONTWEIGHT:
                Object fontWeight = thematicGrouping.getFontWeight();
                if ( fontWeight != null ) {
                    for ( int i = 0; i < getRowCount(); i++ ) {
                        rows.get( i ).setValue( COLUMNTYPE.FONTWEIGHT, fontWeight );
                    }
                }
                break;
            case FONTSTYLE:
                Object fontStyle = thematicGrouping.getFontStyle();
                if ( fontStyle != null ) {
                    for ( int i = 0; i < getRowCount(); i++ ) {
                        rows.get( i ).setValue( COLUMNTYPE.FONTSTYLE, fontStyle );
                    }
                }
                break;
            case FONTSIZE:
                Object fontSize = thematicGrouping.getFontSize();
                if ( fontSize != null ) {
                    for ( int i = 0; i < getRowCount(); i++ ) {
                        rows.get( i ).setValue( COLUMNTYPE.FONTSIZE, fontSize );
                    }
                }
                break;

            case FONTTRANSPARENCY:
                Object fontTransparency = thematicGrouping.getFontTransparency();
                if ( fontTransparency != null ) {
                    if ( fontTransparency instanceof SingleInteger ) {
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            if ( oldRows.size() > 0 && i < oldRows.size() ) {
                                rows.get( i ).setValue( COLUMNTYPE.FONTTRANSPARENCY,
                                                        oldRows.get( i ).getValue( COLUMNTYPE.FONTTRANSPARENCY ) );
                            } else if ( oldRows.size() > 0 ) {
                                rows.get( i ).setValue(
                                                        COLUMNTYPE.FONTTRANSPARENCY,
                                                        oldRows.get( oldRows.size() - 1 ).getValue(
                                                                                                    COLUMNTYPE.FONTTRANSPARENCY ) );
                            }
                        }
                    } else if ( fontTransparency instanceof IntegerRamp ) {
                        List<Integer> integers = ( (IntegerRamp) fontTransparency ).getIntegers( getRowCount() );
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            rows.get( i ).setValue( COLUMNTYPE.FONTTRANSPARENCY, integers.get( i ) );
                        }
                    } else if ( fontTransparency instanceof PropertyName ) {
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            rows.get( i ).setValue( COLUMNTYPE.FONTTRANSPARENCY, (PropertyName) fontTransparency );
                        }
                    }
                }
                break;
            case ROTATION:
                Object rotation = thematicGrouping.getRotation();
                if ( rotation != null ) {
                    for ( int i = 0; i < getRowCount(); i++ ) {
                        rows.get( i ).setValue( COLUMNTYPE.ROTATION, rotation );
                    }
                }
                break;
            case DISPLACEMENT:
                Object displacement = thematicGrouping.getDisplacement();
                if ( displacement != null ) {
                    for ( int i = 0; i < getRowCount(); i++ ) {
                        rows.get( i ).setValue( COLUMNTYPE.DISPLACEMENT, displacement );
                    }
                }
                break;
            case ANCHORPOINT:
                Object anchorPoint = thematicGrouping.getAnchorPoint();
                if ( anchorPoint != null ) {
                    for ( int i = 0; i < getRowCount(); i++ ) {
                        rows.get( i ).setValue( COLUMNTYPE.ANCHORPOINT, anchorPoint );
                    }
                }
                break;
            case HALOCOLOR:
                Object haloColor = thematicGrouping.getHaloColor();
                if ( haloColor != null ) {
                    if ( haloColor instanceof FillColor
                         && SldValues.getDefaultFontColor().equals( ( (FillColor) haloColor ).getColor() ) ) {
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            if ( oldRows.size() > 0 && i < oldRows.size() ) {
                                rows.get( i ).setValue( COLUMNTYPE.HALOCOLOR,
                                                        oldRows.get( i ).getValue( COLUMNTYPE.HALOCOLOR ) );
                            } else if ( oldRows.size() > 0 ) {
                                rows.get( i ).setValue(
                                                        COLUMNTYPE.HALOCOLOR,
                                                        oldRows.get( oldRows.size() - 1 ).getValue(
                                                                                                    COLUMNTYPE.HALOCOLOR ) );
                            }
                        }
                    } else if ( haloColor instanceof Fill ) {
                        List<Object> colors = ( (Fill) haloColor ).getFills( getRowCount() );
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            rows.get( i ).setValue( COLUMNTYPE.HALOCOLOR, (Color) colors.get( i ) );
                        }
                    } else if ( haloColor instanceof PropertyName ) {
                        for ( int i = 0; i < getRowCount(); i++ ) {
                            rows.get( i ).setValue( COLUMNTYPE.HALOCOLOR, haloColor );
                        }
                    }
                }
                break;
            case HALORADIUS:
                Object haloRadius = thematicGrouping.getHaloRadius();
                if ( haloRadius != null ) {
                    for ( int i = 0; i < getRowCount(); i++ ) {
                        rows.get( i ).setValue( COLUMNTYPE.HALORADIUS, haloRadius );
                    }
                }

                break;
            }
            if ( updateTable ) {
                fireTableDataChanged();
            }
        }
    }

    /**
     * sorts the column specified with the given column index in ascending order
     * 
     * @param column
     *            the index of the column to sort
     * @param updateTable
     *            true, if the table should be redrawn
     * 
     */
    public void sort( COLUMNTYPE type, boolean updateTable ) {
        Collections.sort( rows, new ClassificationTableRowComparator<U>( type ) );
        if ( updateTable ) {
            fireTableDataChanged();
        }
    }

    /**
     * @param thematicGrouping
     *            the classification to set
     */
    public void setThematicGrouping( ThematicGrouping<U> thematicGrouping ) {
        this.thematicGrouping = thematicGrouping;
    }

    /**
     * @return the classification represented in the table
     */
    public ThematicGrouping<U> getThematicGrouping() {
        return thematicGrouping;
    }

    /**
     * @return the classification; each entry in the list means one row in the table
     */
    public List<ClassificationTableRow<U>> getClassification() {
        return rows;
    }

    /**
     * @param set
     *            the rows of the classification
     */
    public void setClassification( List<ClassificationTableRow<U>> rows ) {
        this.rows = rows;
        fireTableDataChanged();
    }

    /**
     * inserts a new row in the table before the selected row
     * 
     * @param selectedRow
     *            the index of the selected row
     */
    public void addRowBefore( int selectedRow ) {
        ClassificationTableRow<U> newRow = new ClassificationTableRow<U>();
        // fillColor
        Fill fillColor = thematicGrouping.getFillColor();
        if ( fillColor instanceof FillColor ) {
            newRow.setFillColor( ( (FillColor) fillColor ).getColor() );
        } else if ( fillColor instanceof LinearGradient ) {
            Color prevColor = null;
            if ( rows.size() > 0 && selectedRow > 0 && rows.get( selectedRow - 1 ).getFillColor() instanceof Color ) {
                prevColor = (Color) rows.get( selectedRow - 1 ).getFillColor();
            }
            Color nextColor = null;
            if ( rows.size() > 0 && selectedRow < rows.size()
                 && rows.get( selectedRow ).getFillColor() instanceof Color ) {
                nextColor = (Color) rows.get( selectedRow ).getFillColor();
            }
            Color c = calculateMiddleColor( prevColor, nextColor );
            if ( c != null ) {
                newRow.setFillColor( c );
            }
        }

        // fillTransparency
        if ( thematicGrouping.getFillTransparency() instanceof SingleInteger ) {
            newRow.setFillTransparency( thematicGrouping.getFillTransparency().getMin() );
        } else {
            int prevTrans = -1;
            if ( rows.size() > 0 && selectedRow > 0 ) {
                prevTrans = rows.get( selectedRow - 1 ).getFillTransparency();
            }
            int nextTrans = -1;
            if ( rows.size() > 0 && selectedRow < rows.size() ) {
                nextTrans = rows.get( selectedRow ).getFillTransparency();
            }
            int trans = calculateIntMiddle( prevTrans, nextTrans );
            if ( trans != -1 ) {
                newRow.setFillTransparency( trans );
            }
        }

        // lineColor
        Fill lineColor = thematicGrouping.getLineColor();
        if ( lineColor instanceof FillColor ) {
            newRow.setLineColor( ( (FillColor) thematicGrouping.getLineColor() ).getColor() );
        } else if ( lineColor instanceof LinearGradient ) {
            Color prevColor = null;
            if ( rows.size() > 0 && selectedRow > 0 && rows.get( selectedRow - 1 ).getLineColor() instanceof Color ) {
                prevColor = (Color) rows.get( selectedRow - 1 ).getLineColor();
            }
            Color nextColor = null;
            if ( rows.size() > 0 && selectedRow < rows.size()
                 && rows.get( selectedRow ).getLineColor() instanceof Color ) {
                nextColor = (Color) rows.get( selectedRow ).getLineColor();
            }
            Color c = calculateMiddleColor( prevColor, nextColor );
            if ( c != null ) {
                newRow.setLineColor( c );
            }
        }

        // lineTransparency
        if ( thematicGrouping.getLineTransparency() instanceof SingleInteger ) {
            newRow.setLineTransparency( thematicGrouping.getLineTransparency().getMin() );
        } else {
            int prevTrans = -1;
            if ( rows.size() > 0 && selectedRow > 0 ) {
                prevTrans = rows.get( selectedRow - 1 ).getLineTransparency();
            }
            int nextTrans = -1;
            if ( rows.size() > 0 && selectedRow < rows.size() ) {
                nextTrans = rows.get( selectedRow ).getLineTransparency();
            }
            int trans = calculateIntMiddle( prevTrans, nextTrans );
            if ( trans != -1 ) {
                newRow.setLineTransparency( trans );
            }
        }

        // lineWidth
        if ( thematicGrouping.getLineWidth() instanceof SingleDouble ) {
            newRow.setLineWidth( thematicGrouping.getLineWidth().getMin() );
        } else {
            double prevWidth = Double.NaN;
            if ( rows.size() > 0 && selectedRow > 0 ) {
                prevWidth = rows.get( selectedRow - 1 ).getLineWidth();
            }
            double nextWidth = Double.NaN;
            if ( rows.size() > 0 && selectedRow < rows.size() ) {
                nextWidth = rows.get( selectedRow ).getLineWidth();
            }
            double width = calculateDoubleMiddle( prevWidth, nextWidth );
            if ( !Double.isNaN( width ) ) {
                BigDecimal bc = new BigDecimal( width );
                newRow.setLineWidth( bc.setScale( 2, BigDecimal.ROUND_HALF_UP ).doubleValue() );
            }
        }

        // lineStyle
        newRow.setLineStyle( thematicGrouping.getLineStyle() );

        // size
        if ( thematicGrouping.getSize() instanceof SingleDouble ) {
            newRow.setSize( thematicGrouping.getSize().getMin() );
        } else {
            double prevSize = Double.NaN;
            if ( rows.size() > 0 && selectedRow > 0 ) {
                prevSize = rows.get( selectedRow - 1 ).getSize();
            }
            double nextSize = Double.NaN;
            if ( rows.size() > 0 && selectedRow < rows.size() ) {
                nextSize = rows.get( selectedRow ).getSize();
            }
            double size = calculateDoubleMiddle( prevSize, nextSize );
            if ( !Double.isNaN( size ) ) {
                BigDecimal bc = new BigDecimal( size );
                newRow.setSize( bc.setScale( 2, BigDecimal.ROUND_HALF_UP ).doubleValue() );
            }
        }

        // symbol
        newRow.setSymbol( thematicGrouping.getSymbol() );

        // lineCap
        newRow.setLineCap( thematicGrouping.getLineCap() );

        rows.add( selectedRow, newRow );
        fireTableRowsInserted( selectedRow, selectedRow );
    }

    /**
     * removes the selected rows
     * 
     * @param selectedRows
     *            the rows to remove
     */
    public void removeRows( int[] selectedRows ) {
        Arrays.sort( selectedRows );
        for ( int i = selectedRows.length - 1; i > -1; i-- ) {
            rows.remove( selectedRows[i] );
            fireTableRowsDeleted( i, i );
        }
    }

    private Color calculateMiddleColor( Color c1, Color c2 ) {
        if ( c1 == null && c2 != null ) {
            return c2;
        } else if ( c2 == null && c1 != null ) {
            return c1;
        } else if ( c2 != null && c1 != null ) {
            float r, g, b;
            float[] rgbs1 = c1.getRGBColorComponents( null );
            float[] rgbs2 = c2.getRGBColorComponents( null );

            float r1 = Math.min( rgbs1[0], rgbs2[0] );
            float r2 = Math.max( rgbs1[0], rgbs2[0] );
            r = r1 + ( ( r2 - r1 ) / 2 );

            float g1 = Math.min( rgbs1[1], rgbs2[1] );
            float g2 = Math.max( rgbs1[1], rgbs2[1] );
            g = g1 + ( ( g2 - g1 ) / 2 );

            float b1 = Math.min( rgbs1[2], rgbs2[2] );
            float b2 = Math.max( rgbs1[2], rgbs2[2] );
            b = b1 + ( ( b2 - b1 ) / 2 );

            return new Color( r, g, b );
        }
        return null;
    }

    private int calculateIntMiddle( int i1, int i2 ) {
        if ( i1 == -1 && i2 != -1 ) {
            return i2;
        } else if ( i2 == -1 && i1 != -1 ) {
            return i1;
        } else if ( i2 != -1 && i1 != -1 ) {
            return Math.min( i1, i2 ) + ( ( Math.max( i1, i2 ) - Math.min( i1, i2 ) ) / 2 );
        }
        return -1;
    }

    private double calculateDoubleMiddle( double d1, double d2 ) {
        if ( Double.isNaN( d1 ) && !Double.isNaN( d2 ) ) {
            return d2;
        } else if ( Double.isNaN( d2 ) && !Double.isNaN( d1 ) ) {
            return d1;
        } else if ( !Double.isNaN( d2 ) && !Double.isNaN( d1 ) ) {
            return Math.min( d1, d2 ) + ( ( Math.max( d1, d2 ) - Math.min( d1, d2 ) ) / 2 );
        }
        return Double.NaN;
    }

    /**
     * @param symbolizertype
     * @param map
     * @return a list of rules, representing the classification as SLD
     */
    public List<Rule> getClassifiedData( SYMBOLIZERTYPE symbolizertype, PropertyName propertyName ) {
        switch ( symbolizertype ) {
        case POINT:
            return sldClassificationCalculator.createPointClassificationRules( rows, propertyName,
                                                                               styleDialog.isDefaultUnitPixel() );
        case POLYGON:
            return sldClassificationCalculator.createPolygonClassificationRules( rows, propertyName,
                                                                                 styleDialog.isDefaultUnitPixel() );
        case LINE:
            return sldClassificationCalculator.createLineClassificationRules( rows, propertyName,
                                                                              styleDialog.isDefaultUnitPixel() );
        case LABEL:
            return sldClassificationCalculator.createLabelClassificationRules( rows, propertyName,
                                                                               styleDialog.isDefaultUnitPixel() );
        }
        return new ArrayList<Rule>();
    }

    public int getColumnCount() {
        return columns.size();
    }

    public int getRowCount() {
        return rows.size();
    }

    public Object getValueAt( int rowIndex, int columnIndex ) {
        ClassificationTableRow<U> row = rows.get( rowIndex );
        return row.getValue( columns.get( columnIndex ).getType() );
    }

    @Override
    public String getColumnName( int column ) {
        return columns.get( column ).getHeader();
    }

    public String getColumnTooltip( int column ) {
        return columns.get( column ).getTooltip();
    }

    @Override
    public void setValueAt( Object value, int rowIndex, int columnIndex ) {
        ClassificationTableRow<U> row = rows.get( rowIndex );
        TableModelEvent event = new TableModelEvent( this, rowIndex, rowIndex, columnIndex, TableModelEvent.UPDATE );
        fireTableChanged( event );
        row.setValue( getColumnType( columnIndex ), value );
    }

    @Override
    public boolean isCellEditable( int rowIndex, int columnIndex ) {
        COLUMNTYPE type = getColumnType( columnIndex );
        switch ( type ) {
        case VALUE:
            if ( rows.get( rowIndex ).getValue().getMin() == null && rows.get( rowIndex ).getValue().getMax() != null ) {
                return false;
            }
            return true;
        case COUNT:
            return false;
        case FILLCOLOR:
        case LINECOLOR:
        case LINETRANSPARENCY:
        case FILLTRANSPARENCY:
        case FONTTRANSPARENCY:
        case LINEWIDTH:
        case LINESTYLE:
        case SIZE:
        case SYMBOL:
        case LINECAP:
            return true;
        case FONTCOLOR:
            if ( thematicGrouping.getFontColor() instanceof PropertyName ) {
                return false;
            }
            return true;
        case FONTFAMILY:
            if ( thematicGrouping.getFontFamily() instanceof PropertyName ) {
                return false;
            }
            return true;
        case FONTWEIGHT:
            if ( thematicGrouping.getFontWeight() instanceof PropertyName ) {
                return false;
            }
            return true;
        case FONTSTYLE:
            if ( thematicGrouping.getFontStyle() instanceof PropertyName ) {
                return false;
            }
            return true;
        case FONTSIZE:
            if ( thematicGrouping.getFontSize() instanceof PropertyName ) {
                return false;
            }
            return true;
        case ANCHORPOINT:
            if ( thematicGrouping.getAnchorPoint() instanceof PropertyName ) {
                return false;
            }
            return true;
        case DISPLACEMENT:
            if ( thematicGrouping.getDisplacement() instanceof PropertyName ) {
                return false;
            }
            return true;
        case ROTATION:
            if ( thematicGrouping.getRotation() instanceof PropertyName ) {
                return false;
            }
            return true;
        case HALOCOLOR:
            if ( thematicGrouping.getHaloRadius() instanceof PropertyName ) {
                return false;
            }
            return true;
        case HALORADIUS:
            if ( thematicGrouping.getHaloColor() instanceof PropertyName ) {
                return false;
            }
            return true;
        default:
            return false;
        }
    }

    public void editingCanceled( ChangeEvent e ) {
    }

    @SuppressWarnings("unchecked")
    public void editingStopped( ChangeEvent e ) {
        if ( e.getSource() instanceof ClassificationValuesEditor<?> ) {
            // update all classes!
            ClassificationValuesEditor<U> editor = (ClassificationValuesEditor<U>) e.getSource();
            ValueRange<U> vr = editor.getValueRange();
            Collections.sort( rows, new ClassificationTableRowComparator<U>( VALUE ) );
            Intervallable<U> lastMin = null;
            boolean deleteEditingRow = false;

            int indexOfEditedRow = -1;

            for ( int i = rows.size() - 1; i > -1; i-- ) {
                ValueRange<U> currentRowValue = rows.get( i ).getValue();

                if ( currentRowValue == vr ) {
                    indexOfEditedRow = i;
                }

                // update other class borders, only, when classification does not support same class
                // borders
                if ( !thematicGrouping.hasSameClassBorders() ) {
                    currentRowValue.setMax( lastMin );
                    lastMin = currentRowValue.getMin();
                } else {
                    // take a look, if class with the same border exist
                    if ( vr.getMin().equals( currentRowValue.getMin() ) && i != indexOfEditedRow ) {
                        deleteEditingRow = true;
                    }
                }
            }

            // delete row, when min and max value are the same and classification does not support
            // same class borders
            if ( !thematicGrouping.hasSameClassBorders()
                 && ( ( vr.getMin() != null && vr.getMax() != null && vr.getMin().getValue().equals(
                                                                                                     vr.getMax().getValue() ) ) )
                 || ( vr.getMin() == null && vr.getMax() == null ) && indexOfEditedRow > -1 ) {
                rows.remove( indexOfEditedRow );
            } else if ( thematicGrouping.hasSameClassBorders() ) {
                // if same class borders are supported, set max value to min value, otherwise update
                // class borders
                if ( deleteEditingRow ) {
                    rows.remove( indexOfEditedRow );
                } else {
                    vr.setMax( vr.getMin() );
                }
            }

            editor.setValueRange( vr );
            sort( COLUMNTYPE.VALUE, true );
            updatedCount();
        }
    }

    /**
     * @param column
     *            the type of the column to return the index
     * @return the index of the column with the given type or -1 if no column with this type exist
     */
    public int getColumnIndex( COLUMNTYPE column ) {
        for ( Column col : columns ) {
            if ( col.getType().equals( column ) ) {
                return columns.indexOf( col );
            }
        }
        return -1;
    }

    /**
     * @param columnIndex
     *            the index of the column to return the type
     * @return the type of the column in the specified index or null, if a column with the index does not exist
     */
    public COLUMNTYPE getColumnType( int columnIndex ) {
        if ( columns.get( columnIndex ) != null ) {
            return columns.get( columnIndex ).getType();
        }
        return null;
    }

    /**
     * @return the rows
     */
    public List<ClassificationTableRow<U>> getRows() {
        return rows;
    }

    /**
     * @param index
     *            the index of the row to return
     * @return the row on the given index, or null, if no row exists
     */
    public ClassificationTableRow<U> getRowAt( int index ) {
        if ( index > -1 && index < rows.size() ) {
            return rows.get( index );
        }
        return null;
    }

    private void updatedCount() {
        List<ValueRange<U>> values = new ArrayList<ValueRange<U>>();
        for ( ClassificationTableRow<U> row : rows ) {
            values.add( row.getValue() );
        }
        thematicGrouping.updateValueCounts( values );
    }

}
