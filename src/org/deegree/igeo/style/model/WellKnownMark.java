/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2007 by:
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

package org.deegree.igeo.style.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
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
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class WellKnownMark extends Symbol {

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

        }
        return null;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj == null || !( obj instanceof WellKnownMark ) ) {
            return false;
        }
        if ( sldName == null && ( (WellKnownMark) obj ).sldName != null ) {
            return false;
        }
        return sldName.equals( ( (WellKnownMark) obj ).sldName );
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
