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

package org.deegree.igeo.views.swing.measure;

import java.text.DecimalFormat;

import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.model.spatialschema.Point;

/**
 * The <code>DistanceMeasureLabel</code> represents the result of a distance measurement.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class DistanceMeasureResultLabel extends MeasureResultLabel {

    private static final long serialVersionUID = -6036373366065955654L;

    private String s1;

    /**
     * @param mapModel
     */
    public DistanceMeasureResultLabel( MapModel mapModel ) {
        super( mapModel );
        s1 = Messages.getMessage( getLocale(), "$MD11348" );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.measure.MeasureResultLabel#calculateResult()
     */
    @Override
    protected void calculateResult() {
        Point startPoint = points.get( points.size() - 1 );
        double deltaX = Math.abs( startPoint.getX() ) - Math.abs( this.currentPoint.getX() );
        double deltaY = Math.abs( startPoint.getY() ) - Math.abs( this.currentPoint.getY() );
        currentResult = resultOfInsertedPoints + Math.sqrt( ( deltaX * deltaX ) + ( deltaY * deltaY ) );
        updateText();
    }

    /**
     * update the text of the label
     */
    private void updateText() {
        DecimalFormat df = new DecimalFormat( "#.000" );
        this.setText( " " + s1 + " " + df.format( currentResult ) + ' ' + units );
    }

}
