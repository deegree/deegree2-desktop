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
package org.deegree.igeo.dataadapter.database;

import org.deegree.igeo.mapmodel.DatabaseDatasource;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.model.feature.FeatureCollection;

/**
 * Interfaces that have to implemented by classes writing data into databases
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public interface DatabaseDataWriter {
    /**
     * @param datasource
     * @param featureCollection
     * @param layer
     */
    void insertFeatures(DatabaseDatasource datasource, FeatureCollection featureCollection, Layer layer );
    
    /**
     * 
     * @param datasource
     * @param featureCollection
     * @param layer
     * @return number of features that has been updated
     */
    int updateFeatures(DatabaseDatasource datasource, FeatureCollection featureCollection, Layer layer );
    
    /**
     * 
     * @param datasource
     * @param featureCollection
     * @param layer
     * @return number of features that has been deleted
     */
    int deleteFeatures(DatabaseDatasource datasource, FeatureCollection featureCollection, Layer layer );

    /**
     * 
     * @param timeout
     *            timeout for accessing WFS
     */
    void setTimeout( int timeout );
}
