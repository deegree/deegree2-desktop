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

package org.deegree.igeo.views.swing.style.component.point;

import static org.deegree.igeo.i18n.Messages.get;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.deegree.igeo.style.model.FillColor;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.model.Symbol;
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
 * <code>PointClassificationPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class PointClassificationPanel extends AbstractClassificationPanel {

    private static final long serialVersionUID = -4254026245223760580L;

    private static final List<Column> columns = new ArrayList<Column>();

    static {
        columns.add( new Column( "", get( "$MD10800" ), COLUMNTYPE.VALUE ) );
        columns.add( new Column( get( "$MD10908" ), get( "$MD10909" ), COLUMNTYPE.SYMBOL ) );
        columns.add( new Column( get( "$MD10729" ), get( "$MD10729" ), COLUMNTYPE.FILLCOLOR ) );
        columns.add( new Column( get( "$MD10768" ), get( "$MD10768" ), COLUMNTYPE.LINECOLOR ) );
        columns.add( new Column( get( "$MD10767" ), get( "$MD10767" ), COLUMNTYPE.FILLTRANSPARENCY ) );
        columns.add( new Column( get( "$MD10910" ), get( "$MD10911" ), COLUMNTYPE.SIZE ) );
        columns.add( new Column( get( "$MD11648" ), get( "$MD11649" ), COLUMNTYPE.ROTATION ) );
        columns.add( new Column( get( "$MD11044" ), get( "$MD11045" ), COLUMNTYPE.COUNT ) );
    }

    /**
     * @param symbolVisualPropertyPanel
     */
    public PointClassificationPanel( VisualPropertyPanel assignedVisualPropPanel ) {
        super( assignedVisualPropPanel);
    }

    protected List<Column> getColumns() {
        return columns;
    }

    protected SYMBOLIZERTYPE getSymbolizerType() {
        return SYMBOLIZERTYPE.POINT;
    }

    @Override
    public void initColumnValues() {
        ClassificationTableModel<?> model = getModel();
        // symbol
        Symbol symbol = SldValues.getDefaultWKM();
        Object symbolValue = assignedVisualPropPanel.getValue( ComponentType.MARK, SldValues.getDefaultWKM() );
        if ( symbolValue instanceof Symbol ) {
            symbol = (Symbol) symbolValue;
        }
        model.getThematicGrouping().setSymbol( symbol );
        model.update( COLUMNTYPE.SYMBOL, true );
        
        // fillColor
        FillColor fillC = new FillColor( SldValues.getDefaultLineColor() );
        Object colorValue = assignedVisualPropPanel.getValue( ComponentType.FILLCOLOR, fillC );
        if ( colorValue instanceof Color ) {
            fillC = new FillColor( (Color) colorValue );
        }
        model.getThematicGrouping().setFillColor( fillC );
        model.update( COLUMNTYPE.FILLCOLOR, true );

        // lineColor
        FillColor lineC = new FillColor( SldValues.getDefaultLineColor() );
        Object lineColorValue = assignedVisualPropPanel.getValue( ComponentType.COLOR, lineC );
        if ( lineColorValue instanceof Color ) {
            lineC = new FillColor( (Color) lineColorValue );
        }
        model.getThematicGrouping().setLineColor( lineC );
        model.update( COLUMNTYPE.LINECOLOR, true );

        // fillTransparency
        int transparency = SldValues.getOpacityInPercent( SldValues.getDefaultOpacity() );
        Object fillTransValue = assignedVisualPropPanel.getValue( ComponentType.OPACITY, SldValues.getDefaultOpacity() );
        if ( fillTransValue instanceof Double ) {
            transparency = SldValues.getOpacityInPercent( (Double) fillTransValue );
        }
        model.getThematicGrouping().setFillTransparency( new SingleInteger( transparency ) );
        model.update( COLUMNTYPE.FILLTRANSPARENCY, true );

        // size
        Double size = SldValues.getDefaultSize();
        if ( assignedVisualPropPanel.getValue( ComponentType.SIZE, SldValues.getDefaultSize() ) instanceof UnitsValue ) {
            size = ( (UnitsValue) assignedVisualPropPanel.getValue( ComponentType.SIZE, SldValues.getDefaultSize() ) ).getValue();
        }
        model.getThematicGrouping().setSize( new SingleDouble( size ) );
        model.update( COLUMNTYPE.SIZE, true );

    }
}
