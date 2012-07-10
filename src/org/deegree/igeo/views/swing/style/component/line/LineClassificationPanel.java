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

package org.deegree.igeo.views.swing.style.component.line;

import static org.deegree.igeo.i18n.Messages.get;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.deegree.igeo.style.model.DashArray;
import org.deegree.igeo.style.model.FillColor;
import org.deegree.igeo.style.model.SldProperty;
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
 * <code>LineClassificationPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class LineClassificationPanel extends AbstractClassificationPanel {

    private static final long serialVersionUID = 2143373483421691677L;

    private static final List<Column> columns = new ArrayList<Column>();

    static {
        columns.add( new Column( "", get( "$MD10800" ), COLUMNTYPE.VALUE ) );
        columns.add( new Column( get( "$MD10922" ), get( "$MD10923" ), COLUMNTYPE.LINECOLOR ) );
        columns.add( new Column( get( "$MD10924" ), get( "$MD10925" ), COLUMNTYPE.LINETRANSPARENCY ) );
        columns.add( new Column( get( "$MD10926" ), get( "$MD10927" ), COLUMNTYPE.LINEWIDTH ) );
        columns.add( new Column( get( "$MD10928" ), get( "$MD10929" ), COLUMNTYPE.LINECAP ) );
        columns.add( new Column( get( "$MD10930" ), get( "$MD10931" ), COLUMNTYPE.LINESTYLE ) );
        columns.add( new Column( get( "$MD11042" ), get( "$MD11043" ), COLUMNTYPE.COUNT ) );
    }

    /**
     * @param assignedVisualPropPanel
     * @param componentType
     */
    public LineClassificationPanel( VisualPropertyPanel assignedVisualPropPanel, ComponentType componentType ) {
        super( assignedVisualPropPanel, componentType );
    }

    @Override
    public void initColumnValues() {
        ClassificationTableModel<?> model = getModel();

        // lineColor
        FillColor c = new FillColor( SldValues.getDefaultLineColor() );
        Object fillColorValue = assignedVisualPropPanel.getValue( ComponentType.COLOR, c );
        if ( fillColorValue instanceof Color ) {
            new FillColor( (Color) fillColorValue );
        }
        model.getThematicGrouping().setLineColor( c );
        model.update( COLUMNTYPE.LINECOLOR, true );

        // lineTransparency
        int transparency = SldValues.getOpacityInPercent( SldValues.getDefaultOpacity() );
        Object opacityValue = assignedVisualPropPanel.getValue( ComponentType.OPACITY, transparency );
        if ( opacityValue instanceof Double ) {
            transparency = SldValues.getOpacityInPercent( (Double) opacityValue );
        }
        model.getThematicGrouping().setLineTransparency( new SingleInteger( transparency ) );
        model.update( COLUMNTYPE.LINETRANSPARENCY, true );

        // lineWidth
        Double width = SldValues.getDefaultLineWidth();
        Object lineWidthValue = assignedVisualPropPanel.getValue( ComponentType.LINEWIDTH,
                                                                  SldValues.getDefaultLineWidth() );
        if ( lineWidthValue instanceof UnitsValue ) {
            width = (Double) ( (UnitsValue) lineWidthValue ).getValue();
        }
        model.getThematicGrouping().setLineWidth( new SingleDouble( width ) );
        model.update( COLUMNTYPE.LINEWIDTH, true );

        // lineCap
        SldProperty cap = (SldProperty) assignedVisualPropPanel.getValue( ComponentType.LINECAP,
                                                                          SldValues.getDefaultLineCapAsProperty() );
        model.getThematicGrouping().setLineCap( cap );
        model.update( COLUMNTYPE.LINECAP, true );

        // lineStyle
        DashArray lineStyle = (DashArray) assignedVisualPropPanel.getValue( ComponentType.LINEARRAY,
                                                                            SldValues.getDefaultLineStyle() );
        model.getThematicGrouping().setLineStyle( lineStyle );
        model.update( COLUMNTYPE.LINESTYLE, true );

    };

    @Override
    protected List<Column> getColumns() {
        return columns;
    }

    @Override
    protected SYMBOLIZERTYPE getSymbolizerType() {
        return SYMBOLIZERTYPE.LINE;
    }

}
