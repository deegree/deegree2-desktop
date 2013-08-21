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

package org.deegree.desktop.dataadapter.wms;

import java.net.MalformedURLException;
import java.net.URL;

import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;

/**
 * Definiton of convenience methods for accessing request target URLs from a WMS capabilities
 * document. These methods are defined within an interface because concrete realization depends on
 * WMS version
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public interface WMSCapabilitiesEvaluator {

    /**
     * sets XML document representing WFS capabilities
     * 
     * @param xml
     */
    void setCapabilities( XMLFragment xml );

    /**
     * 
     * @return target URL for performing GetMap requests via HTTP Get
     * @throws XMLParsingException
     * @throws MalformedURLException
     */
    URL getGetMapHTTPGetURL()
                            throws XMLParsingException, MalformedURLException;

    /**
     * 
     * @return target URL for performing GetMap requests via HTTP Post
     * @throws XMLParsingException
     * @throws MalformedURLException
     */
    URL getGetMapHTTPPostURL()
                            throws XMLParsingException, MalformedURLException;

    /**
     * 
     * @return target URL for performing GetLegendGrapgic requests
     * @throws XMLParsingException
     * @throws MalformedURLException
     */
    public URL getGetLegendGraphicURL()
                            throws XMLParsingException, MalformedURLException;

}
