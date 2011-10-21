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
import java.util.Iterator;
import java.util.Map;

import org.deegree.framework.util.KVP2Map;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class OWSURLUtils {

    /**
     * removes OWS specific parameters from a GetCapabilities URL so just the base URL plus vendor specific parameters
     * will be returned
     * 
     * @param url GetCapabilities URL
     * @return base URL plus vendor specific parameters
     * @throws MalformedURLException
     */
    public static URL normalizeOWSURL( URL url )
                            throws MalformedURLException {
        String pr = url.getProtocol();
        String ho = url.getHost();
        int po = url.getPort();
        String pa = url.getPath();
        Map<String, String> param = KVP2Map.toMap( url.getQuery() );
        param.remove( "REQUEST" );
        param.remove( "SERVICE" );
        param.remove( "VERSION" );
        param.remove( "ACCEPTEDVERSIONS" );
        Iterator<String> iter = param.keySet().iterator();
        String s = "";
        while ( iter.hasNext() ) {
            String key = iter.next();
            String value = param.get( key );
            s += ( key + '=' + value + '&' );
        }
        return new URL( pr + "://" + ho + ":" + po + pa + '?' + s );
    }

}
