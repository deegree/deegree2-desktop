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

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.UUID;

import net.sf.ehcache.Cache;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.igeo.config.OnlineResourceType;
import org.deegree.igeo.config.Util;
import org.deegree.igeo.config.WFSDatasourceType;
import org.deegree.igeo.config.ServiceDatasourceType.CapabilitiesURL;
import org.deegree.igeo.config.WFSDatasourceType.GetFeatureRequest;
import org.deegree.ogcwebservices.InvalidParameterValueException;
import org.deegree.ogcwebservices.wfs.XMLFactory;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.deegree.ogcwebservices.wfs.operation.GetFeatureDocument;
import org.xml.sax.SAXException;

/**
 * data source description for OGC web feature services
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class WFSDatasource extends ServiceDatasource {
    
    private static ILogger LOG = LoggerFactory.getLogger( WFSDatasource.class );

    private GetFeature getFeature;

    private QualifiedName geometryProperty;

    /**
     * 
     * @param dsType
     * @param authenticationInformation
     * @param cache
     * @throws SAXException
     * @throws IOException
     * @throws InvalidParameterValueException
     * @throws XMLParsingException
     */
    public WFSDatasource( WFSDatasourceType dsType, AuthenticationInformation authenticationInformation, Cache cache )
                            throws SAXException, IOException, InvalidParameterValueException, XMLParsingException {
        super( dsType, authenticationInformation, cache );
        String tmp = dsType.getGetFeatureRequest().getValue().trim();
        LOG.logDebug( "GetFeature request for datasource: " + dsType.getName(), tmp );
        StringReader reader = new StringReader( tmp );
        GetFeatureDocument doc = new GetFeatureDocument();
        doc.load( reader, XMLFragment.DEFAULT_URL );
        getFeature = doc.parse( UUID.randomUUID().toString() );
        this.geometryProperty = Util.convertQualifiedName( dsType.getGeometryProperty() );
    }
    
    /**
     * 
     * @return version of the service
     */
    public String getServiceVersion() {
       return ((WFSDatasourceType)dsType).getServiceVersion();
    }

    /**
     * 
     * @return GetFeature request
     */
    public GetFeature getGetFeature() {
        return this.getFeature;
    }

    /**
     * @param getFeature
     *            the getFeature to set
     * @throws XMLParsingException
     * @throws IOException
     */
    public void setGetFeature( GetFeature getFeature )
                            throws IOException, XMLParsingException {
        this.getFeature = getFeature;
        GetFeatureRequest value = new GetFeatureRequest();
        value.setValue( XMLFactory.export( getFeature ).getAsString() );
        value.setVersion( getFeature.getVersion() );
        ( (WFSDatasourceType) dsType ).setGetFeatureRequest( value );
    }

    /**
     * 
     * @return geometry property name
     */
    public QualifiedName getGeometryProperty() {
        return this.geometryProperty;
    }

    /**
     * @param geometryProperty
     *            the geometryProperty to set
     */
    public void setGeometryProperty( QualifiedName geomProperty ) {        
        ( (WFSDatasourceType) dsType ).setGeometryProperty( Util.convertQualifiedName( geomProperty ) );
        this.geometryProperty = geomProperty;
    }

    /**
     * If this method is invoked not simply the passed URL will be set, also the capabilities will
     * be read from the URL
     * 
     * @param url
     *            URL representing a service GetCapabilties request
     */
    public void setCapabilitiesURL( URL url ) {
        CapabilitiesURL cu = new CapabilitiesURL();
        OnlineResourceType ort = new OnlineResourceType();
        ort.setHref( url.toExternalForm() );
        cu.setOnlineResource( ort );
        ( (WFSDatasourceType) dsType ).setCapabilitiesURL( cu );
        this.capabilitiesURL = url;        
    }

}