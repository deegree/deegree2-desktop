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
package org.deegree.igeo.mapmodel;

import java.net.MalformedURLException;
import java.net.URL;

import net.sf.ehcache.Cache;

import org.deegree.igeo.config.ServiceDatasourceType;

/**
 * basic data source description for OGC web serices
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public abstract class ServiceDatasource extends Datasource {

    protected URL capabilitiesURL;

    /**
     * 
     * @param dsType
     * @param authenticationInformation
     * @param cache
     * @param capabilitiesURL
     * @throws MalformedURLException 
     */
    public ServiceDatasource( ServiceDatasourceType dsType, AuthenticationInformation authenticationInformation,
                              Cache cache) throws MalformedURLException {
        super( dsType, authenticationInformation, cache );
        this.capabilitiesURL = new URL( dsType.getCapabilitiesURL().getOnlineResource().getHref() );
        setCapabilitiesURL( capabilitiesURL );
    }

   

    /**
     * 
     * @return capabilities URL
     */
    public URL getCapabilitiesURL() {
        return this.capabilitiesURL;
    }

    /**
     * If this method is invoked not simply the passed URL will be set, also the capabilities will
     * be read from the URL
     * 
     * @param url
     *            URL representing a service GetCapabilties request
     */
    public abstract void setCapabilitiesURL( URL url );

}