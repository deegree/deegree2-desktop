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

package org.deegree.desktop.dataadapter;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.datatypes.UnknownTypeException;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.mapmodel.MemoryDatasource;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;

/**
 * concrete {@link FeatureAdapter} for managing feature in the memory of an iGeoDesktop instance
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class MemoryFeatureAdapter extends FeatureAdapter {

    /**
     * @param layer
     * @param mapModel
     */
    MemoryFeatureAdapter( MemoryDatasource datasource, Layer layer, MapModel mapModel ) {
        super( datasource, layer, mapModel );
        FeatureCollection fc = (FeatureCollection) datasource.getData();
        if ( fc == null || fc.size() == 0 ) {
            // a default feature type just containing a geometry will be created if
            // an empty memory data source 
            fc = FeatureFactory.createFeatureCollection( "UUID_"+UUID.randomUUID().toString(), 1 );
            setFeature( fc );
            QualifiedName qn1 = new QualifiedName( "GEOM", URI.create( "http://www.deegree.org/app" ) );
            QualifiedName qn2 = new QualifiedName( "TYPE" + UUID.randomUUID(), URI.create( "http://www.deegree.org/app" ) );
            try {
                PropertyType[] pt = new PropertyType[]{FeatureFactory.createPropertyType( qn1, Types.GEOMETRY_PROPERTY_NAME, true )};
                FeatureType ft = FeatureFactory.createFeatureType( qn2, false, pt );
                schemas.put( datasource.getName(), ft );
            } catch ( UnknownTypeException e ) {
                e.printStackTrace();
            }
        } else {
            schemas.put( datasource.getName(), fc.getFeature( 0 ).getFeatureType() );
        }
        featureCollections.put( datasource.getName(), fc );
    }

    @Override
    public FeatureCollection getFeatureCollection() {
        synchronized ( datasource ) {
            return featureCollections.get( datasource.getName() );
        }
    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub

    }
    
    @Override
    public void refresh( boolean forceReload ) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void commitChanges()
                            throws IOException {
        // a MemoryFeatureAdapter does not have a backen so nothing to do
    }

}
