//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2008 by:
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

package org.deegree.desktop.modules;

import org.deegree.desktop.commands.model.ZoomCommand;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.modules.ActionDescription.ACTIONTYPE;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.Identifier;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;

/**
 * Module for zooming active map(model) to a defined location (coordinate)  
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class ZoomByCoordinatesModule<T> extends DefaultModule<T> {

    private static final ILogger LOG = LoggerFactory.getLogger( ZoomByCoordinatesModule.class );

    static {
        ActionDescription actionDescription = new ActionDescription(
                                                                     "open",
                                                                     "opens a dialog entering a coordinate and a map size to zoom to",
                                                                     null, "zoom to defined coordinate",
                                                                     ACTIONTYPE.PushButton, null, null );
        moduleCapabilities = new ModuleCapabilities( actionDescription );
    }

    /**
     * opens the modules view
     * 
     */
    public void open() {
        if ( this.componentStateAdapter.isClosed() ) {
            this.componentStateAdapter.setClosed( false );
            createIView();
        }
    }

    /**
     * 
     * @param x
     *            new x coordinate
     * @param y
     *            new y coordinate
     * @param size
     *            minimum size of the new map
     */
    public void performZoom( double x, double y, double size ) {
        String mmId = getInitParameter( "assignedMapModel" );
        MapModel mm = appContainer.getMapModel( new Identifier( mmId ) );
        ZoomCommand cmd = new ZoomCommand( mm );
        double s2 = size / 2d;
        Envelope envelope = GeometryFactory.createEnvelope( x - s2, y - s2, x + s2, y + s2, mm.getCoordinateSystem() );
        cmd.setZoomBox( envelope, mm.getTargetDevice().getPixelWidth(), mm.getTargetDevice().getPixelHeight() );
        try {
            this.appContainer.getCommandProcessor().executeSychronously( cmd, true );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11240" ),
                                           Messages.get( "$MD11241", x, y, size ), e );
        }
        clear();
    }

}
