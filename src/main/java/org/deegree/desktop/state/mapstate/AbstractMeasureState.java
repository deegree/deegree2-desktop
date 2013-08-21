//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
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
package org.deegree.desktop.state.mapstate;

import java.awt.Component;
import java.awt.event.MouseEvent;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.modules.IModule;
import org.deegree.desktop.views.swing.measure.MeasureResultLabel;
import org.deegree.framework.utils.MapTools;
import org.deegree.kernel.Command;
import org.deegree.model.spatialschema.Point;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
abstract class AbstractMeasureState extends MapState {

    protected MeasureResultLabel measureResultLabel;

    /**
     * @param appContainer
     */
    public AbstractMeasureState( ApplicationContainer<?> appContainer ) {
        super( appContainer );
    }

    @Override
    public Command createCommand( IModule<?> module, MapModel mapModel, Layer layer, Point... points ) {
        return null;
    }

    /**
     * sets the label calculating and representing the result of the measurment
     * 
     * @param label
     *            the label
     */
    public void setMeasureResultLabel( MeasureResultLabel label ) {
        this.measureResultLabel = label;
    }

    @Override
    public void mouseMoved( MouseEvent event ) {
        if ( drawingPane != null && drawingPane.isDrawing() ) {
            Component c = event.getComponent();
            java.awt.Point p = MapTools.adjustPointToPanelSize( event.getPoint(), c.getWidth(), c.getHeight() );
            drawingPane.draw( p.x, p.y );
            if ( measureResultLabel != null ) {
                measureResultLabel.setCurrentPoint( p.getX(), p.getY(), c.getWidth(), c.getHeight() );
            }
            c.repaint();
        }
    }
}
