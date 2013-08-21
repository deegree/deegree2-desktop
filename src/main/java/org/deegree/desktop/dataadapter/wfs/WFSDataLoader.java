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
package org.deegree.desktop.dataadapter.wfs;

import java.net.URL;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.schema.GMLSchema;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcwebservices.wfs.operation.Query;

/**
 * Definition of convenience methods for accessing FeatureCollections and GML application schema from
 * a WFS. These methods are defined within an interface because concrete realization depends on WFS
 * version
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public interface WFSDataLoader {

    /**
     * reads data from a WFS and returns the result as feature collection. returned data may be
     * limited by a restricting bounding box and/or a filter expression
     * 
     * @param wfs
     * @param property
     * @param bbox
     * @param query
     * @param layer
     * @return feature collection
     */
    FeatureCollection readFeatureCollection( URL wfs, QualifiedName property, Envelope bbox, Query query, Layer layer );

    /**
     * @param wfs
     * @param layer
     * @param featureTypes
     * 
     * @return GML application schema describing passed feature type
     */
    GMLSchema readGMLApplicationSchema( URL wfs, Layer layer, QualifiedName[] featureTypes );
    
    /**
     * 
     * @param timeout timeout for accessing WFS 
     */
    void setTimeout(int timeout);

    /**
     * @param maxFeatures
     */
    void setMaxFeatures( int maxFeatures );

}
