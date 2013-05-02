//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2013 by:
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

 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 http://www.lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/

package org.deegree.igeo.state.mapstate;

import static java.awt.Toolkit.getDefaultToolkit;
import static org.deegree.model.spatialschema.GeometryFactory.createPoint;

import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;

import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.views.DrawingPane;
import org.deegree.kernel.Command;
import org.deegree.model.spatialschema.Point;

/**
 * MapState that is used to copy coordinates from map by mouse click
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author last edited by: $Author: wanhoff $
 */
public class CopyCoordinatesState extends MapState {

    /**
     * @param appContainer
     */
    public CopyCoordinatesState( ApplicationContainer<?> appContainer ) {
        super( appContainer );
    }

    @Override
    public Command createCommand( IModule<?> module, MapModel mapModel, Layer layer, Point... points ) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.state.mapstate.ToolState#createDrawingPane(java.lang.String, java.awt.Graphics)
     */
    @Override
    public DrawingPane createDrawingPane( String platform, Graphics g ) {
        return null;
    }

    @Override
    public void mousePressed( MouseEvent event ) {
        Point pt = getCoordinatesFromMouseEvent( event );
        copyCoordinatesToClipboard( pt );
    }

    private void copyCoordinatesToClipboard( Point pt ) {
        if ( "application".equalsIgnoreCase( appContainer.getViewPlatform() ) ) {
            String coordinateString = pt.getX() + " " + pt.getY();
            Toolkit tk = getDefaultToolkit();
            tk.getSystemClipboard().setContents( new StringSelection( coordinateString ), null );
        }
    }

    private Point getCoordinatesFromMouseEvent( MouseEvent e ) {
        MapModel mm = appContainer.getMapModel( null );
        GeoTransform trans = mm.getToTargetDeviceTransformation();
        Point pt = createPoint( trans.getSourceX( e.getX() ), trans.getSourceY( e.getY() ), mm.getCoordinateSystem() );
        return pt;
    }

}
