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

package org.deegree.igeo.views.swing.style.component.polygon;

import static org.deegree.igeo.i18n.Messages.get;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.deegree.igeo.style.model.DashArray;
import org.deegree.igeo.style.model.FillColor;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.model.classification.Column;
import org.deegree.igeo.style.model.classification.SingleDouble;
import org.deegree.igeo.style.model.classification.SingleInteger;
import org.deegree.igeo.style.model.classification.Column.COLUMNTYPE;
import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.style.perform.UnitsValue;
import org.deegree.igeo.views.swing.style.VisualPropertyPanel;
import org.deegree.igeo.views.swing.style.component.classification.AbstractClassificationPanel;
import org.deegree.igeo.views.swing.style.component.classification.ClassificationTableModel;

/**
 * <code>PolygonClassificationPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class PolygonClassificationPanel extends AbstractClassificationPanel {

    private static final long serialVersionUID = -3512994491507023281L;

    private static final List<Column> columns = new ArrayList<Column>();

    static {
        columns.add( new Column( "", get( "$MD10800" ), COLUMNTYPE.VALUE ) );
        columns.add( new Column( get( "$MD10729" ), get( "$MD10998" ), COLUMNTYPE.FILLCOLOR ) );
        columns.add( new Column( get( "$MD10767" ), get( "$MD10999" ), COLUMNTYPE.FILLTRANSPARENCY ) );
        columns.add( new Column( get( "$MD10768" ), get( "$MD11000" ), COLUMNTYPE.LINECOLOR ) );
        columns.add( new Column( get( "$MD10769" ), get( "$MD11001" ), COLUMNTYPE.LINETRANSPARENCY ) );
        columns.add( new Column( get( "$MD10770" ), get( "$MD11002" ), COLUMNTYPE.LINEWIDTH ) );
        columns.add( new Column( get( "$MD10799" ), get( "$MD11003" ), COLUMNTYPE.LINESTYLE ) );
        columns.add( new Column( get( "$MD11046" ), get( "$MD11047" ), COLUMNTYPE.COUNT ) );
    }

    /**
     * 
     * @param assignedVisualPropPanel
     *            the assigned visual property panel
     */
    public PolygonClassificationPanel( VisualPropertyPanel assignedVisualPropPanel) {
        super( assignedVisualPropPanel);
    }

    protected List<Column> getColumns() {
        return columns;
    }

    protected SYMBOLIZERTYPE getSymbolizerType() {
        return SYMBOLIZERTYPE.POLYGON;
    }

    @Override
    public void initColumnValues() {
        ClassificationTableModel<?> model = getModel();
        // fillColor
        FillColor fillC = new FillColor( SldValues.getDefaultLineColor() );
        Object fillCValue = assignedVisualPropPanel.getValue( ComponentType.FILLCOLOR, fillC );
        if ( fillCValue instanceof Color ) {
            fillC = new FillColor( (Color) fillCValue );
        }
        model.getThematicGrouping().setFillColor( fillC );
        model.update( COLUMNTYPE.FILLCOLOR, true );

        // fillTransparency
        int fillTransp = SldValues.getOpacityInPercent( SldValues.getDefaultOpacity() );
        Object fillTranspValue = assignedVisualPropPanel.getValue( ComponentType.FILLOPACITY, fillTransp );
        if ( fillTranspValue instanceof Double ) {
            fillTransp = SldValues.getOpacityInPercent( (Double) fillTranspValue );
        }
        model.getThematicGrouping().setFillTransparency( new SingleInteger( fillTransp ) );
        model.update( COLUMNTYPE.FILLTRANSPARENCY, true );

        // lineColor
        FillColor lineC = new FillColor( SldValues.getDefaultLineColor() );
        Object lineCValue = assignedVisualPropPanel.getValue( ComponentType.COLOR, lineC );
        if ( lineCValue instanceof Color ) {
            lineC = new FillColor( (Color) lineCValue );
        }
        model.getThematicGrouping().setLineColor( lineC );
        model.update( COLUMNTYPE.LINECOLOR, true );

        // lineTransparency

        int lineTrans = SldValues.getOpacityInPercent( SldValues.getDefaultOpacity() );
        Object lineTransValue = assignedVisualPropPanel.getValue( ComponentType.OPACITY, lineTrans );
        if ( lineTransValue instanceof Double ) {
            lineTrans = SldValues.getOpacityInPercent( (Double) lineTransValue );
        }
        model.getThematicGrouping().setLineTransparency( new SingleInteger( lineTrans ) );
        model.update( COLUMNTYPE.LINETRANSPARENCY, true );

        // lineWidth
        // lineWidth
        Double width = SldValues.getDefaultLineWidth();
        Object lineWidthValue = assignedVisualPropPanel.getValue( ComponentType.LINEWIDTH, width );
        if ( lineWidthValue instanceof UnitsValue ) {
            width = (Double) ( (UnitsValue) lineWidthValue ).getValue();
        }
        model.getThematicGrouping().setLineWidth( new SingleDouble( width ) );
        model.update( COLUMNTYPE.LINEWIDTH, true );

        // lineStyle
        DashArray lineStyle = (DashArray) assignedVisualPropPanel.getValue( ComponentType.LINEARRAY,
                                                                            SldValues.getDefaultLineStyle() );
        model.getThematicGrouping().setLineStyle( lineStyle );
        model.update( COLUMNTYPE.LINESTYLE, true );

    }
}
