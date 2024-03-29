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
package org.deegree.framework.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.deegree.crs.configuration.CRSConfiguration;
import org.deegree.crs.configuration.CRSProvider;
import org.deegree.igeo.i18n.Messages;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class CRSUtils {

    /**
     * 
     * @return array of available Coordinate Reference Systems
     */
    public static String[] getAvailableEPSGCodesAsArray() {
        CRSProvider pr = CRSConfiguration.getCRSConfiguration().getProvider();
        List<String> tmp = pr.getAvailableCRSIds();
        List<String> tmp2 = new ArrayList<String>( tmp.size() / 2 );
        for ( String string : tmp ) {
            if ( string.toLowerCase().startsWith( "epsg:" ) ) {
                tmp2.add( string );
            }
        }
        Collections.sort( tmp2 );
        tmp2.add( 0, Messages.get( "$MD11421" ) );
        return tmp2.toArray( new String[tmp2.size()] );        
    }
}
