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

package org.deegree.desktop.modules.remotecontrol;

import java.util.Map;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.commands.model.ZoomCommand;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.modules.ModuleException;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;

/**
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class SetBBOXHandler implements RequestHandler {

    private static final ILogger LOG = LoggerFactory.getLogger( SetBBOXHandler.class );

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.modules.remotecontrol.RequestHandler#perform(java.util.Map,
     * org.deegree.igeo.ApplicationContainer)
     */
    public String perform( Map<String, String> paramater, ApplicationContainer<?> appContainer )
                            throws ModuleException {

        String tmp = paramater.get( "BBOX" );
        if ( tmp == null ) {
            throw new ModuleException( Messages.get( "$DG10090" ) );
        }
        MapModel mapModel = appContainer.getMapModel( null );
        Envelope env = GeometryFactory.createEnvelope( tmp, mapModel.getCoordinateSystem() );
        ZoomCommand cmd = new ZoomCommand( mapModel );
        cmd.setZoomBox( env, mapModel.getTargetDevice().getPixelWidth(), mapModel.getTargetDevice().getPixelHeight() );

        if ( "TRUE".equalsIgnoreCase( paramater.get( "sync" ) ) ) {
            try {
                appContainer.getCommandProcessor().executeSychronously( cmd, true );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                throw new ModuleException( e.getMessage() );
            }
        } else {
            appContainer.getCommandProcessor().executeASychronously( cmd );
        }
        return "request performed, new boundingbox is: " + mapModel.getEnvelope();
    }

    @Override
    public void init( Map<String, String> parameters ) {
        // does not use parameters
    }
}
