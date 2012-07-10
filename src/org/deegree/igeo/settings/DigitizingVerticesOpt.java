//$HeadURL: svn+ssh://developername@svn.wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
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

import org.deegree.igeo.config.VerticesType;

/**
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author: Andreas Poth $
 * 
 * @version $Revision: $, $Date: $
 * 
 */
public class DigitizingVerticesOpt extends ElementSettings {

    private VerticesType verticesType;

    private boolean changeable;

    /**
     * 
     * @param verticesType
     * @param changeable
     */
    DigitizingVerticesOpt( VerticesType verticesType, boolean changeable ) {
        super( changeable );
        this.verticesType = verticesType;
        if ( this.verticesType.getSearchRadius() == null ) {
            this.verticesType.setSearchRadius( new VerticesType.SearchRadius() );
        }
    }

    /**
     * 
     * @return <code>true</code> if nearest vertex shall be used
     */
    public boolean handleNearest() {
        return verticesType.isHandleNearest();
    }

    /**
     * @see #handleNearest()
     * @param value
     */
    public void setHandleNearest( boolean value ) {
        if ( changeable ) {
            verticesType.setHandleNearest( value );
        }
    }

    /**
     * 
     * @return <code>true</code> if nearest vertex shall be used as target
     */
    public boolean useNearest() {
        return verticesType.isUseNearest();
    }

    /**
     * @see #useNearest()
     * @param value
     */
    public void setUseNearest( boolean value ) {
        if ( changeable ) {
            verticesType.setUseNearest( value );
        }
    }

    /**
     * 
     * @return unit of measure used for defining a search radius around current mouse position
     */
    public String getSearchRadiusUOM() {
        return verticesType.getSearchRadius().getUom();
    }

    /**
     * @see #getSearchRadiusUOM()
     * @param uom
     */
    public void setSearchRadiusUOM( String uom ) {
        if ( changeable ) {
            verticesType.getSearchRadius().setUom( uom );
        }
    }

    /**
     * 
     * @return size of the search radius around current mouse position
     */
    public float getSearchRadiusValue() {
        return verticesType.getSearchRadius().getVal();
    }

    /**
     * @see #getSearchRadiusValue()
     * @param value
     */
    public void setSearchRadiusValue( float value ) {
        if ( changeable ) {
            verticesType.getSearchRadius().setVal( value );
        }
    }
}
