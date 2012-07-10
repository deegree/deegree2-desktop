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

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Surface;
import org.deegree.model.spatialschema.SurfaceInterpolationImpl;

/**
 * The <code>AreaMeasureResultLabel</code> represents the result of a area measurement.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class AreaMeasureResultLabel extends MeasureResultLabel {

    private static final long serialVersionUID = 2703711812966243281L;

    private static final ILogger LOG = LoggerFactory.getLogger( AreaMeasureResultLabel.class );

    private String s1;

    /**
     * @param mapModel
     */
    public AreaMeasureResultLabel( MapModel mapModel ) {
        super( mapModel );
        s1 = Messages.getMessage( getLocale(), "$MD11349" );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.measure.MeasureResultLabel#calculateResult()
     */
    @Override
    protected void calculateResult() {

        int nrOfPoints = points.size() + 2;
        if ( nrOfPoints > 2 ) {
            Position[] positions = new Position[nrOfPoints];
            int i = 0;
            for ( Point point : this.points ) {
                positions[i] = GeometryFactory.createPosition( point.getX(), point.getY() );
                i++;
            }
            positions[i++] = GeometryFactory.createPosition( this.currentPoint.getX(), this.currentPoint.getY() );
            positions[i] = GeometryFactory.createPosition( this.points.get( 0 ).getX(), this.points.get( 0 ).getY() );
            try {
                Surface s = GeometryFactory.createSurface( positions, null, new SurfaceInterpolationImpl(),
                                                           this.mapModel.getCoordinateSystem() );
                this.currentResult = s.getArea();
            } catch ( GeometryException e ) {
                LOG.logDebug( "can not create a geometry of the drawn points" + e.getMessage() );
            }
        } else {
            this.currentResult = 0;
        }

        updateText();
    }

    /**
     * update the text of the label
     */
    private void updateText() {
        DecimalFormat df = new DecimalFormat( "#.000" );
        this.setText( " " + s1 + " " + df.format( currentResult ) + ' ' + units + '²' );
    }

}
