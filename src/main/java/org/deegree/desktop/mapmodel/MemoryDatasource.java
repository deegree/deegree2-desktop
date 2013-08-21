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

import net.sf.ehcache.Cache;

import org.deegree.desktop.config.AbstractDatasourceType;

/**
 * 
 * data source description for vector and raster data managed in memory of a iGeoDesktop instance
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class MemoryDatasource extends Datasource {

    private Object data;

    /**
     * 
     * @param dsType
     * @param authenticationInformation
     * @param cache
     * @param extent
     * @param data
     */
    public MemoryDatasource( AbstractDatasourceType dsType, AuthenticationInformation authenticationInformation,
                             Cache cache, Object data ) {
        super( dsType, authenticationInformation, cache );
        this.data = data;
    }

    /**
     * 
     * @return
     */
    public Object getData() {
        return data;
    }

    /**
     * 
     * @param data
     */
    public void setData( Object data ) {
        this.data = data;
    }

}