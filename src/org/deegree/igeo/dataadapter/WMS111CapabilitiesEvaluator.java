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
package org.deegree.igeo.dataadapter;

import java.net.MalformedURLException;
import java.net.URL;

import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcwebservices.OWSUtils;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class WMS111CapabilitiesEvaluator implements WMSCapabilitiesEvaluator {

    private XMLFragment xml;

    private static NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.WMSCapabilitiesEvaluator#getGetLegendGraphicURL()
     */
    public URL getGetLegendGraphicURL()
                            throws XMLParsingException, MalformedURLException {
        String xPathQuery = "//GetLegendGraphic/DCPType/HTTP/Get/OnlineResource/@xlink:href";
        String tmp = XMLTools.getNodeAsString( xml.getRootElement(), xPathQuery, nsContext, null );
        if ( tmp == null ) {
            // GetLegedGraphic is optional
            return null;
        }
        tmp = OWSUtils.validateHTTPGetBaseURL( tmp );
        return new URL( tmp );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.WMSCapabilitiesEvaluator#getGetMapHTTPGetURL()
     */
    public URL getGetMapHTTPGetURL()
                            throws XMLParsingException, MalformedURLException {
        String xPathQuery = "//GetMap/DCPType/HTTP/Get/OnlineResource/@xlink:href";
        String tmp = XMLTools.getRequiredNodeAsString( xml.getRootElement(), xPathQuery, nsContext );
        tmp = OWSUtils.validateHTTPGetBaseURL( tmp );
        return new URL( tmp );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.WMSCapabilitiesEvaluator#getGetMapHTTPPostURL()
     */
    public URL getGetMapHTTPPostURL()
                            throws XMLParsingException, MalformedURLException {
        String xPathQuery = "//GetMap/DCPType/HTTP/Poth/OnlineResource/@xlink:href";
        String tmp = XMLTools.getNodeAsString( xml.getRootElement(), xPathQuery, nsContext, null );
        if ( tmp == null ) {
            // GetMap via HTTP Post is optional
            return null;
        }        
        return new URL( tmp );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.deegree.client.presenter.connector.WMSCapabilitiesEvaluator#setCapabilities(org.deegree.framework.xml.XMLFragment
     * )
     */
    public void setCapabilities( XMLFragment xml ) {
        this.xml = xml;
    }

}
