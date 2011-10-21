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

package org.deegree.igeo.settings;

import org.deegree.igeo.config.GeometryTypeType;

/**
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class ValidationGeomTypes extends ElementSettings {

    private GeometryTypeType geometryType;

    /**
     * @param changeable
     * @param geometryType
     */
    public ValidationGeomTypes( GeometryTypeType geometryType, boolean changeable ) {
        super( changeable );
        this.geometryType = geometryType;
    }

    /**
     * 
     * @return <code>true</code> if points are allowed to be contained in a layer
     */
    public boolean pointsAllowed() {
        return geometryType.isAllowPoints();
    }

    /**
     * @see #pointsAllowed()
     * @param value
     */
    public void setPointsAllowed( boolean value ) {
        if ( isChangeable() ) {
            geometryType.setAllowPoints( value );
        }
    }

    /**
     * 
     * @return <code>true</code> if linestrings are allowed to be contained in a layer
     */
    public boolean linestringsAllowed() {
        return geometryType.isAllowLinestrings();
    }

    /**
     * @see #linestringsAllowed()
     * @param value
     */
    public void setLinestringsAllowed( boolean value ) {
        if ( isChangeable() ) {
            geometryType.setAllowLinestrings( value );
        }
    }

    /**
     * 
     * @return <code>true</code> if polygons are allowed to be contained in a layer
     */
    public boolean polygonsAllowed() {
        return geometryType.isAllowPolygons();
    }

    /**
     * @see #polygonsAllowed()
     * @param value
     */
    public void setPolygonsAllowed( boolean value ) {
        if ( isChangeable() ) {
            geometryType.setAllowPolygons( value );
        }
    }

    /**
     * 
     * @return <code>true</code> if multi points are allowed to be contained in a layer
     */
    public boolean multiPointsAllowed() {
        return geometryType.isAllowMultiPoints();
    }

    /**
     * @see #multiPointsAllowed()
     * @param value
     */
    public void setMultiPointsAllowed( boolean value ) {
        if ( isChangeable() ) {
            geometryType.setAllowMultiPoints( value );
        }
    }

    /**
     * 
     * @return <code>true</code> if multi linestrings are allowed to be contained in a layer
     */
    public boolean multiLinestringsAllowed() {
        return geometryType.isAllowMultiLinestrings();
    }

    /**
     * @see #multiLinestringsAllowed()
     * @param value
     */
    public void setMultiLinestringsAllowed( boolean value ) {
        if ( isChangeable() ) {
            geometryType.setAllowMultiLinestrings( value );
        }
    }

    /**
     * 
     * @return <code>true</code> if multi polygons are allowed to be contained in a layer
     */
    public boolean multiPolygonsAllowed() {
        return geometryType.isAllowMultiPolygons();
    }

    /**
     * @see #multiPolygonsAllowed()
     * @param value
     */
    public void setMultiPolygonsAllowed( boolean value ) {
        if ( isChangeable() ) {
            geometryType.setAllowMultiPolygons( value );
        }
    }

    /**
     * 
     * @return <code>true</code> if polygons with holes are allowed to be contained in a layer
     */
    public boolean polygonsWithHolesAllowed() {
        return geometryType.isAllowHoles();
    }

    /**
     * @see #polygonsWithHolesAllowed()
     * @param value
     */
    public void setPolygonsWithHolesAllowed( boolean value ) {
        if ( isChangeable() ) {
            geometryType.setAllowHoles( value );
        }
    }

    /**
     * 
     * @return <code>true</code> if none linear interpolation between vertices are allowed to be contained in a layer
     */
    public boolean noneLinearInterpolationAllowed() {
        return geometryType.isAllowNoneLinearInterpolation();
    }

    /**
     * @see #noneLinearInterpolationAllowed()
     * @param value
     */
    public void setNoneLinearInterpolationAllowed( boolean value ) {
        if ( isChangeable() ) {
            geometryType.setAllowNoneLinearInterpolation( value );
        }
    }

    /**
     * 
     * @return <code>true</code> if untyped geometry collections are allowed to be contained in a layer
     */
    public boolean geometryCollectionsAllowed() {
        return geometryType.isAllowGeometryCollections();
    }

    /**
     * @see #geometryCollectionsAllowed()
     * @param value
     */
    public void setGeometryCollectionsAllowed( boolean value ) {
        if ( isChangeable() ) {
            geometryType.setAllowGeometryCollections( value );
        }
    }
}
