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

import org.deegree.igeo.config.GeometryTopologyType;

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
public class ValidationGeomTopology extends ElementSettings {

    private GeometryTopologyType geomTopo;

    /**
     * @param geomTopo
     * @param changeable
     */
    public ValidationGeomTopology( GeometryTopologyType geomTopo, boolean changeable ) {
        super( changeable );
        this.geomTopo = geomTopo;
    }

    /**
     * 
     * @return <code>true</code> if intersection between geometries are allowed to be contained in a layer
     */
    public boolean intersectionAllowed() {
        return geomTopo.isAllowIntersection();
    }

    /**
     * @see #intersectionAllowed()
     * @param value
     */
    public void setIntersectionAllowed( boolean value ) {
        if ( isChangeable() ) {
            geomTopo.setAllowIntersection( value );
        }
    }

    /**
     * 
     * @return <code>true</code> if touching of geometries are allowed to be contained in a layer
     */
    public boolean touchingAllowed() {
        return geomTopo.isAllowTouching();
    }

    /**
     * @see #touchingAllowed()
     * @param value
     */
    public void setTouchingAllowed( boolean value ) {
        if ( isChangeable() ) {
            geomTopo.setAllowTouching( value );
        }
    }

    /**
     * 
     * @return <code>true</code> if equal eometries are allowed to be contained in a layer
     */
    public boolean equalGeometriesAllowed() {
        return geomTopo.isAllowEqualGeometries();
    }

    /**
     * @see #equalGeometriesAllowed()
     * @param value
     */
    public void setEqualGeometriesAllowed( boolean value ) {
        if ( isChangeable() ) {
            geomTopo.setAllowEqualGeometries( value );
        }
    }
}
