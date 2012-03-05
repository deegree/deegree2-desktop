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

package org.deegree.igeo.dataadapter.wfs;

import java.net.MalformedURLException;
import java.net.URL;

import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;

/**
 * Helper class for extracting access points (URLs) from a WFS 1.1.0 capabilities document
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class WFS110CapabilitiesEvaluator implements WFSCapabilitiesEvaluator {

    private XMLFragment xml;

    private static NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.WFSCapabilitiesEvaluator#getDescribeFeatureTypeURL()
     */
    public URL getDescribeFeatureTypeURL()
                            throws XMLParsingException, MalformedURLException {
        String xPathQuery = "//ows:Operation[@name='DescribeFeatureType']/ows:DCP/ows:HTTP/ows:Get/@xlink:href";
        String tmp = XMLTools.getRequiredNodeAsString( xml.getRootElement(), xPathQuery, nsContext );
        return new URL( tmp );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.WFSCapabilitiesEvaluator#getGetFeatureURL()
     */
    public URL getGetFeatureURL()
                            throws XMLParsingException, MalformedURLException {
        String xPathQuery = "//ows:Operation[@name='GetFeature']/ows:DCP/ows:HTTP/ows:Post/@xlink:href";
        String tmp = XMLTools.getRequiredNodeAsString( xml.getRootElement(), xPathQuery, nsContext );
        return new URL( tmp );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.WFSCapabilitiesEvaluator#getTransactionURL()
     */
    public URL getTransactionURL()
                            throws XMLParsingException, MalformedURLException {
        String xPathQuery = "//ows:Operation[@name='Transaction']/ows:DCP/ows:HTTP/ows:Post/@xlink:href";
        String tmp = XMLTools.getNodeAsString( xml.getRootElement(), xPathQuery, nsContext, null );
        if ( tmp == null ) {
            // transaction is optional
            return null;
        }
        return new URL( tmp );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.WFSCapabilitiesEvaluator#setCapabilities(org.deegree.framework.xml.XMLFragment)
     */
    public void setCapabilities( XMLFragment xml ) {
        this.xml = xml;
    }

}
