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
package org.deegree.igeo.dataadapter;

import java.net.MalformedURLException;
import java.net.URL;

import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;

/**
 * Helper class for extracting access points (URLs) from a WCS 1.0 capabilities document
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class WCS100CapabilitiesEvaluator implements WCSCapabilitiesEvaluator {

    private XMLFragment xml;

    private static NamespaceContext nsContext = CommonNamespaces.getNamespaceContext();

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.WCSCapabilitiesEvaluator#setCapabilities(org.deegree.framework.xml.XMLFragment)
     */
    public void setCapabilities( XMLFragment xml ) {
        this.xml = xml;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.WCSCapabilitiesEvaluator#getDescribeCoverageHTTPGetURL()
     */
    public URL getDescribeCoverageHTTPGetURL()
                            throws XMLParsingException, MalformedURLException {
        String xPathQuery = "//wcs:DescribeCoverage/wcs:DCPType/wcs:HTTP/wcs:Get/wcs:OnlineResource/@xlink:href";
        String tmp = XMLTools.getRequiredNodeAsString( xml.getRootElement(), xPathQuery, nsContext );
        return new URL( tmp );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.WCSCapabilitiesEvaluator#getGetCoverageHTTPGetURL()
     */
    public URL getGetCoverageHTTPGetURL()
                            throws XMLParsingException, MalformedURLException {
        String xPathQuery = "//wcs:GetCoverage/wcs:DCPType/wcs:HTTP/wcs:Get/wcs:OnlineResource/@xlink:href";
        String tmp = XMLTools.getRequiredNodeAsString( xml.getRootElement(), xPathQuery, nsContext );
        return new URL( tmp );
    }

}