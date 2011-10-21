//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2010 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.igeo.views.swing.style.component.font;

import static org.deegree.igeo.i18n.Messages.get;

import java.util.ArrayList;
import java.util.List;

import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.model.classification.Column;
import org.deegree.igeo.style.model.classification.Column.COLUMNTYPE;
import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.views.swing.style.VisualPropertyPanel;
import org.deegree.igeo.views.swing.style.component.classification.AbstractClassificationPanel;
import org.deegree.igeo.views.swing.style.component.classification.ClassificationTableModel;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class FontClassificationPanel extends AbstractClassificationPanel {

    private static final long serialVersionUID = 2337145936915708606L;

    private static final List<Column> columns = new ArrayList<Column>();

    static {
        columns.add( new Column( "", get( "$MD10800" ), COLUMNTYPE.VALUE ) );
        columns.add( new Column( get( "$MD11636" ), get( "$MD11637" ), COLUMNTYPE.FONTFAMILY ) );
        columns.add( new Column( get( "$MD11638" ), get( "$MD11639" ), COLUMNTYPE.FONTSTYLE ) );
        columns.add( new Column( get( "$MD11640" ), get( "$MD11641" ), COLUMNTYPE.FONTWEIGHT ) );
        columns.add( new Column( get( "$MD11642" ), get( "$MD11643" ), COLUMNTYPE.FONTSIZE ) );
        columns.add( new Column( get( "$MD11644" ), get( "$MD11645" ), COLUMNTYPE.ANCHORPOINT ) );
        columns.add( new Column( get( "$MD11646" ), get( "$MD11647" ), COLUMNTYPE.DISPLACEMENT ) );
        columns.add( new Column( get( "$MD11648" ), get( "$MD11649" ), COLUMNTYPE.ROTATION ) );
        columns.add( new Column( get( "$MD11650" ), get( "$MD11651" ), COLUMNTYPE.FONTCOLOR ) );
        columns.add( new Column( get( "$MD11652" ), get( "$MD11653" ), COLUMNTYPE.FONTTRANSPARENCY ) );
        columns.add( new Column( get( "$MD11654" ), get( "$MD11655" ), COLUMNTYPE.HALORADIUS ) );
        columns.add( new Column( get( "$MD11656" ), get( "$MD11657" ), COLUMNTYPE.HALOCOLOR ) );
        columns.add( new Column( get( "$MD11042" ), get( "MD11043" ), COLUMNTYPE.COUNT ) );
    }

    /**
     * 
     * @param assignedVisualPropPanel
     */
    public FontClassificationPanel( VisualPropertyPanel assignedVisualPropPanel ) {
        super( assignedVisualPropPanel );
    }

    @Override
    protected List<Column> getColumns() {
        return columns;
    }

    @Override
    protected SYMBOLIZERTYPE getSymbolizerType() {
        return SYMBOLIZERTYPE.LABEL;
    }

    @Override
    public void initColumnValues() {
        ClassificationTableModel<?> model = getModel();

        // FONT
        model.getThematicGrouping().setFontFamily(
                                                   assignedVisualPropPanel.getValue( ComponentType.FONTFAMILY,
                                                                                     SldValues.getDefaultFontFamily() ) );
        model.getThematicGrouping().setFontColor(
                                                  assignedVisualPropPanel.getValue( ComponentType.FILLCOLOR,
                                                                                    SldValues.getDefaultFontColor() ) );

        model.getThematicGrouping().setFontStyle(
                                                  assignedVisualPropPanel.getValue( ComponentType.FONTSTYLE,
                                                                                    SldValues.getDefaultFontStyle() ) );
        model.getThematicGrouping().setFontSize(
                                                 assignedVisualPropPanel.getValue( ComponentType.SIZE,
                                                                                   SldValues.getDefaultFontSize() ) );
        model.getThematicGrouping().setFontWeight(
                                                   assignedVisualPropPanel.getValue( ComponentType.FONTWEIGHT,
                                                                                     SldValues.getDefaultFontWeight() ) );
        model.getThematicGrouping().setFontTransparency(
                                                         assignedVisualPropPanel.getValue(
                                                                                           ComponentType.OPACITY,
                                                                                           SldValues.getOpacityInPercent( SldValues.getDefaultOpacity() ) ) );

        model.getThematicGrouping().setAnchorPoint(
                                                    assignedVisualPropPanel.getValue( ComponentType.ANCHOR,
                                                                                      SldValues.getDefaultAnchorPoint() ) );
        model.getThematicGrouping().setDisplacement(
                                                     assignedVisualPropPanel.getValue(
                                                                                       ComponentType.DISPLACEMENT,
                                                                                       SldValues.getDefaultDisplacement() ) );

        model.getThematicGrouping().setRotation(
                                                 assignedVisualPropPanel.getValue( ComponentType.ROTATION,
                                                                                   SldValues.getDefaultRotation() ) );
        model.getThematicGrouping().setHaloColor(
                                                  assignedVisualPropPanel.getValue( ComponentType.HALOFILLCOLOR,
                                                                                    SldValues.getDefaultHaloColor() ) );
        model.getThematicGrouping().setHaloRadius(
                                                   assignedVisualPropPanel.getValue( ComponentType.HALORADIUS,
                                                                                     SldValues.getDefaultHaloRadius() ) );

    }
}
