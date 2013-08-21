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

package org.deegree.desktop.views.swing.measure;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.framework.utils.MapTools;
import org.deegree.model.spatialschema.Point;

/**
 * The <code>MeasureResultLabel</code> calculates a measure result and represents it as string.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public abstract class MeasureResultLabel extends JLabel {

    private static final long serialVersionUID = -5580101345154000081L;

    protected MapModel mapModel;

    protected List<Point> points = new ArrayList<Point>();

    protected Point currentPoint;

    protected double resultOfInsertedPoints;

    protected double currentResult;
    
    protected String units;

    /**
     * @param mapModel
     *            the mapModel
     */
    protected MeasureResultLabel( MapModel mapModel ) {
        Dimension d = new Dimension( 200, 20 );
        this.setPreferredSize( d );
        this.setVisible( true );
        this.mapModel = mapModel;
        units = mapModel.getCoordinateSystem().getAxisUnits()[0].getSymbol();
    }

    /**
     * set the start point of the measurement
     * 
     * @param mouseX
     *            the x-coordinate of the mouse cursor
     * @param mouseY
     *            the y-coordinate of the mouse cursor
     * @param componentWidth
     *            the height of the component
     * @param componentHeight
     *            the width of the component
     */
    public void setStartPoint( double mouseX, double mouseY, double componentWidth, double componentHeight ) {
        points.clear();
        currentResult = 0;
        Point newPoint = MapTools.calculateMouseCoord( this.mapModel, mouseX, mouseY, componentWidth, componentHeight );
        this.currentPoint = newPoint;
        this.resultOfInsertedPoints = this.currentResult;
        this.points.add( newPoint );
    }

    /**
     * 
     * add a new Point to the measurement
     * 
     * @param mouseX
     *            the x-coordinate of the mouse cursor
     * @param mouseY
     *            the y-coordinate of the mouse cursor
     * @param componentWidth
     *            the height of the comonent
     * @param componentHeight
     *            the width of the comonent
     */
    public void addPoint( double mouseX, double mouseY, double componentWidth, double componentHeight ) {
        Point newPoint = MapTools.calculateMouseCoord( this.mapModel, mouseX, mouseY, componentWidth, componentHeight );
        this.currentPoint = newPoint;
        calculateResult();
        this.resultOfInsertedPoints = this.currentResult;
        this.points.add( newPoint );
    }

    /**
     * sets a temporary point of the measurement
     * 
     * @param mouseX
     *            the x-coordinate of the mouse cursor
     * @param mouseY
     *            the y-coordinate of the mouse cursor
     * @param componentWidth
     *            the height of the component
     * @param componentHeight
     *            the width of the component
     */
    public void setCurrentPoint( double mouseX, double mouseY, double componentWidth, double componentHeight ) {
        this.currentPoint = MapTools.calculateMouseCoord( this.mapModel, mouseX, mouseY, componentWidth,
                                                          componentHeight );
        calculateResult();
    }    

    /**
     * calculate the result of the current stored points and the temporary point
     */
    protected abstract void calculateResult();

}
