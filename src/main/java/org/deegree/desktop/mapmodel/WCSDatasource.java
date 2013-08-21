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
package org.deegree.desktop.mapmodel;

import java.net.MalformedURLException;
import java.net.URL;

import net.sf.ehcache.Cache;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.config.Util;
import org.deegree.desktop.config.OnlineResourceType;
import org.deegree.desktop.config.WCSDatasourceType;
import org.deegree.desktop.config.ServiceDatasourceType.CapabilitiesURL;

/**
 * data source description for OGC web coverage services
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class WCSDatasource extends ServiceDatasource {

    private QualifiedName coverage;

    /**
     * 
     * @param dsType
     * @param authenticationInformation
     * @param cache
     * @throws MalformedURLException
     */
    public WCSDatasource( WCSDatasourceType dsType, AuthenticationInformation authenticationInformation, Cache cache )
                            throws MalformedURLException {
        super( dsType, authenticationInformation, cache );
        if ( dsType.getCoverage().getNamespace() == null ) {
            coverage = new QualifiedName( dsType.getCoverage().getLocalName() );
        } else {
            coverage = Util.convertQualifiedName( dsType.getCoverage() );
        }

    }
    
    /**
     * 
     * @return version of the service
     */
    public String getServiceVersion() {
       return ((WCSDatasourceType)dsType).getServiceVersion();
    }

    /**
     * 
     * @return coverage name
     */
    public QualifiedName getCoverage() {
        return this.coverage;
    }

    /**
     * @param coverage
     *            the coverage to set
     */
    public void setCoverage( QualifiedName coverage ) {
        this.coverage = coverage;
        ( (WCSDatasourceType) dsType ).setCoverage( Util.convertQualifiedName( coverage ) );
    }

    /**
     * 
     * @return format used to request a coverage
     */
    public String getFormat() {
        return ( (WCSDatasourceType) dsType ).getFormat();
    }

    /**
     * 
     * @param format format used to request a coverage
     */
    public void setFormat( String format ) {
        ( (WCSDatasourceType) dsType ).setFormat( format );
    }
    
    /**
     * 
     * @return time parameter for GetCoverage request; can be <code>null</code>
     */
    public String getTime() {
        return ( (WCSDatasourceType) dsType ).getTime(); 
    }
    
    /**
     * 
     * @param time time parameter for GetCoverage request
     */
    public void setTime(String time) {
        ( (WCSDatasourceType) dsType ).setTime( time );        
    }

    /**
     * If this method is invoked not simply the passed URL will be set, also the capabilities will be read from the URL
     * 
     * @param url
     *            URL representing a service GetCapabilties request
     */
    public void setCapabilitiesURL( URL url ) {
        CapabilitiesURL cu = new CapabilitiesURL();
        OnlineResourceType ort = new OnlineResourceType();
        ort.setHref( url.toExternalForm() );
        cu.setOnlineResource( ort );
        ( (WCSDatasourceType) dsType ).setCapabilitiesURL( cu );
        this.capabilitiesURL = url;
    }

}