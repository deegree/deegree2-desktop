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

package org.deegree.igeo.modules;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.model.Identifier;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryException;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class LazyFeatureLayer extends org.deegree.graphics.LazyFeatureLayer {

    private static final ILogger LOG = LoggerFactory.getLogger( LazyFeatureLayer.class );

    private FeatureAdapter featureAdapter;

    private MapModel mapModel;

    /**
     * 
     * @param featureAdapter
     * @param crs
     * @throws Exception
     */
    public LazyFeatureLayer( FeatureAdapter featureAdapter, CoordinateSystem crs ) throws Exception {
        super( featureAdapter.getLayer().getIdentifier().getAsQualifiedString(), crs );
        this.featureAdapter = featureAdapter;
        mapModel = this.featureAdapter.getLayer().getOwner();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.graphics.Layer#getBoundingBox()
     */
    public Envelope getBoundingBox() {
        try {
            if ( isValid() ) {
                return featureAdapter.getFeatureCollection().getBoundedBy();
            } else {
                return mapModel.getEnvelope();
            }
        } catch ( GeometryException e ) {
            LOG.logError( e.getMessage(), e );
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.graphics.Layer#getName()
     */
    public String getName() {
        return featureAdapter.getLayer().getIdentifier().getAsQualifiedString();
    }

    /**
     * 
     * @return {@link QualifiedName} of the wrapped {@link FeatureAdapter}
     */
    public Identifier getLayerIdentfier() {
        return featureAdapter.getLayer().getIdentifier();
    }

    /**
     * @return size of the current feature collection
     */
    public int getSize() {
        if ( isValid() ) {
            return featureAdapter.getFeatureCollection().size();
        } else {
            return 0;
        }
    }

    /**
     * returns the feature that matches the submitted index
     * 
     * @param index
     * @return a feature
     */
    public Feature getFeature( int index ) {
        if ( isValid() ) {
            FeatureCollection fc = featureAdapter.getFeatureCollection();
            return fc.getFeature( index );
        } else {
            return null;
        }
    }

    @Override
    public boolean isValid() {
        Datasource ds = featureAdapter.getDatasource();
        double sc = mapModel.getScaleDenominator();
        return ds.getMinScaleDenominator() <= sc && ds.getMaxScaleDenominator() >= sc;
    }

}
