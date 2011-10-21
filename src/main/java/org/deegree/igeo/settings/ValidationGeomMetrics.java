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

import org.deegree.igeo.config.GeometryMetricsType;

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
public class ValidationGeomMetrics extends ElementSettings {

    private GeometryMetricsType geometryMetrics;

    /**
     * 
     * @param geometryMetrics
     * @param changeable
     */
    public ValidationGeomMetrics( GeometryMetricsType geometryMetrics, boolean changeable ) {
        super( changeable );
        this.geometryMetrics = geometryMetrics;
    }

    /**
     * 
     * @return <code>true</code> if lines shall be checked for being simple (no self intersection)
     */
    public boolean ensureSimpleLines() {
        return geometryMetrics.isEnsureSimpleLines();
    }

    /**
     * @see #ensureSimpleLines()
     * @param value
     */
    public void setEnsureSimpleLines( boolean value ) {
        if ( isChangeable() ) {
            geometryMetrics.setEnsureSimpleLines( value );
        }
    }

    /**
     * 
     * @return <code>true</code> if geometries shall be checked for being valid
     */
    public boolean checkForValidGeometries() {
        return geometryMetrics.isCheckForValidGeometries();
    }

    /**
     * @see #checkForValidGeometries()
     * @param value
     */
    public void setCheckForValidGeometries( boolean value ) {
        if ( isChangeable() ) {
            geometryMetrics.setCheckForValidGeometries( value );
        }
    }

    /**
     * 
     * @return <code>true</code> if repeating the same point in a polygon ring or a linestring is not allowed
     */
    public boolean disallowRepeatedPoints() {
        return geometryMetrics.isDisallowRepeatedPoints();
    }

    /**
     * @see #disallowRepeatedPoints()
     * @param value
     */
    public void setDisallowRepeatedPoints( boolean value ) {
        if ( isChangeable() ) {
            geometryMetrics.setDisallowRepeatedPoints( value );
        }
    }

    /**
     * 
     * @return <code>true</code> if orientation of polygon rings shall be checked
     */
    public boolean checkForPolygonOrientation() {
        return geometryMetrics.isCheckForPolygonOrientation();
    }

    /**
     * @see #checkForPolygonOrientation()
     * @param value
     */
    public void setCheckForPolygonOrientation( boolean value ) {
        if ( isChangeable() ) {
            geometryMetrics.setCheckForPolygonOrientation( value );
        }
    }

    /**
     * 
     * @return <code>true</code> if no double geometries are allowed in multi geometries
     */
    public boolean disallowDoubleGeomerties() {
        return geometryMetrics.isDisallowDoubleGeomerties();
    }

    /**
     * @see #disallowDoubleGeomerties()
     * @param value
     */
    public void setDisallowDoubleGeomerties( boolean value ) {
        if ( isChangeable() ) {
            geometryMetrics.setDisallowDoubleGeomerties( value );
        }
    }

    /**
     * 
     * @return <code>true</code> if linestring shall be checked for having at least a minimum distance between two
     *         vertices
     */
    public boolean limitMinSegmentLength() {
        return geometryMetrics.isMinSegmentLength();
    }

    /**
     * @see #limitMinSegmentLength()
     * @param value
     */
    public void setLimitMinSegmentLength( boolean value ) {
        if ( isChangeable() ) {
            geometryMetrics.setMinSegmentLength( value );
        }
    }

    /**
     * 
     * @return minimum allowed distance between two vertices of a linestring
     */
    public float getMinSegmentLength() {
        return geometryMetrics.getMinSegmentLengthValue();
    }

    /**
     * @see #getMinSegmentLength()
     * @param value
     */
    public void setMinSegmentLength( float value ) {
        if ( isChangeable() ) {
            geometryMetrics.setMinSegmentLengthValue( value );
        }
    }

    /**
     * 
     * @return <code>true</code> if polygon rings shall be checked for having at least a minimum distance between two
     *         vertices
     */
    public boolean limitMinPolygonArea() {
        return geometryMetrics.isMinPolygonArea();
    }

    /**
     * @see #limitMinPolygonArea()
     * @param value
     */
    public void setLimitMinPolygonArea( boolean value ) {
        if ( isChangeable() ) {
            geometryMetrics.setMinPolygonArea( value );
        }
    }

    /**
     * 
     * @return minimum allowed distance between two vertices of a polygon ring
     */
    public float getMinPolygonArea() {
        return geometryMetrics.getMinPolygonAreaValue();
    }

    /**
     * @see #getMinPolygonArea()
     * @param value
     */
    public void setMinPolygonArea( float value ) {
        if ( isChangeable() ) {
            geometryMetrics.setMinPolygonAreaValue( value );
        }
    }
}
