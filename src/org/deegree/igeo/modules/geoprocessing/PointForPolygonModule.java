//$HeadURL: svn+ssh://developername@svn.wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 and
 lat/lon GmbH

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
package org.deegree.igeo.modules.geoprocessing;

import org.deegree.igeo.commands.geoprocessing.PointForPolygonCommand;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.ActionDescription;
import org.deegree.igeo.modules.DefaultModule;
import org.deegree.igeo.modules.ModuleCapabilities;
import org.deegree.igeo.modules.ActionDescription.ACTIONTYPE;
import org.deegree.kernel.Command;
import org.deegree.kernel.ProcessMonitor;
import org.deegree.kernel.ProcessMonitorFactory;

/**
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author: Andreas Poth $
 * 
 * @version $Revision: $, $Date: $
 * 
 */
public class PointForPolygonModule<T> extends DefaultModule<T> {

    static {
        ActionDescription ad1 = new ActionDescription(
                                                       "calculate",
                                                       "calculates a point for each polygon of a layer that will be located inside a polygon",
                                                       null, "calculates representative points for polygons",
                                                       ACTIONTYPE.PushButton, null, null );
        moduleCapabilities = new ModuleCapabilities( ad1 );
    }

    /**
     * action handler method
     */
    public void calculate() {
        MapModel mapModel = appContainer.getMapModel( null );
        Layer layer = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION ).get( 0 );
        Command command = new PointForPolygonCommand( layer, layer.getTitle() + "_point", null );
        ProcessMonitor pm = ProcessMonitorFactory.createDialogProcessMonitor( appContainer.getViewPlatform(),
                                                                              Messages.get( "$MD10580" ),
                                                                              Messages.get( "$MD10581" ), 0, 1, command );
        command.setProcessMonitor( pm );
        appContainer.getCommandProcessor().executeASychronously( command );

        clear();
    }

}
