/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2012 by:
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

 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: info@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/

package org.deegree.desktop.style.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.utils.HashCodeUtil;
import org.deegree.graphics.sld.Mark;
import org.deegree.graphics.sld.StyleFactory;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;

/**
 * <code>WellKnownMark</code>
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class WellKnownMark extends Symbol {

    private static final ILogger LOG = LoggerFactory.getLogger( WellKnownMark.class );

    private String sldName;

    /**
     * @param sldName
     * @param name
     */
    public WellKnownMark( String sldName, String name ) {
        super( name );
        this.sldName = sldName;
    }

    /**
     * @return the sldName
     */
    public String getSldName() {
        return sldName;
    }

    /**
     * @param sldName
     *            the sldName to set
     */
    public void setSldName( String sldName ) {
        this.sldName = sldName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.model.Symbol#getAsImage()
     */
    public BufferedImage getAsImage() {
        BufferedImage img = null;
        try {
            Mark m = StyleFactory.createMark( getSldName() );
            img = m.getAsImage( createFeature(), 20 );
            return img;
        } catch ( FilterEvaluationException e ) {
            LOG.logWarning( "ignore", e );
        }
        return null;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj == null ) {
            return false;
        }
        if ( !( obj instanceof WellKnownMark ) ) {
            return false;
        }
        Boolean equalSldNames = false;
        if ( sldName == null ) {
        	if ( ( (WellKnownMark) obj ).sldName == null ) {
        		equalSldNames = true;
        	}
        } else if (( (WellKnownMark) obj ).sldName != null) {
        	equalSldNames = sldName.equals( ( (WellKnownMark) obj ).sldName );
        	
        }
        Boolean equalNames = false;
        if ( getName() == null ) {
        	if ( ( (WellKnownMark) obj ).getName() == null ) {
        		equalNames = true;
        	}
        } else if (( (WellKnownMark) obj ).getName() != null) {
        	equalNames = getName().equals( ( (WellKnownMark) obj ).getName() );                                
        }
        return equalSldNames && equalNames;
    }

    @Override
    public int hashCode() {
            int result = HashCodeUtil.SEED;
            result = HashCodeUtil.hash( result, sldName );
            result = HashCodeUtil.hash( result, getName() );
        return result;
    }

    private static Feature createFeature() {
        PropertyType[] ftpsGeom = new PropertyType[1];
        ftpsGeom[0] = FeatureFactory.createSimplePropertyType( new QualifiedName( "GEOM" ), Types.GEOMETRY, false );

        FeatureProperty[] featPropGeom = new FeatureProperty[1];
        Geometry geom = null;
        try {
            geom = GeometryFactory.createPoint( 30, 30, CRSFactory.create( "EPSG:4326" ) );
        } catch ( UnknownCRSException e1 ) {
            e1.printStackTrace();
        }

        featPropGeom[0] = FeatureFactory.createFeatureProperty( new QualifiedName( "GEOM" ), geom );

        FeatureType featureType = FeatureFactory.createFeatureType( "featureTypeGom", false, ftpsGeom );
        List<FeatureProperty> properties = new ArrayList<FeatureProperty>();
        return FeatureFactory.createFeature( "peview", featureType, properties );
    }

}
