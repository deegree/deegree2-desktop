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

package org.deegree.igeo.views;

import java.net.URL;

import org.deegree.datatypes.QualifiedName;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.views.swing.DefaultFilterPanel;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;

/**
 * <code>FilterFactory</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class FilterFactory {

    /**
     * creates a specific filter for the given featureType/viewPlattform
     * 
     * 
     * @param wfsUrl
     * @param wfsCapabilities
     * @param featureType
     *            the featureType
     * @param mapModel
     * @param appContainer
     *            the applicationContainer
     * @return the filter defined in the configuration for the given feature type/ view plattform.
     *         If no filter is configured the default filter for the view plattform will be
     *         returned.
     * @throws Exception 
     */
    public static Filter createFilter( URL wfsUrl, WFSCapabilities wfsCapabilities, QualifiedName featureType,
                                       MapModel mapModel, ApplicationContainer<?> appContainer ) throws Exception {
        // TODO
        if ( appContainer.getViewPlatform().equalsIgnoreCase(  "Application" ) ) {
            return new DefaultFilterPanel( wfsUrl, wfsCapabilities, featureType, mapModel, appContainer );
        } else {
            return null;
        }
    }
}
