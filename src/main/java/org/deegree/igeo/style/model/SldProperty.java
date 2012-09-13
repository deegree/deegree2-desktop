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

import org.deegree.framework.utils.HashCodeUtil;

/**
 * <code>SldProperty</code> to store an SLdValue like FontStyle and hist type
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class SldProperty {

    private int typeCode;

    private String sldName;

    private String name;

    /**
     * 
     * @param typeCode
     * @param sldName
     * @param name
     */
    public SldProperty( int typeCode, String sldName, String name ) {
        this.typeCode = typeCode;
        this.sldName = sldName;
        this.name = name;
    }

    /**
     * @return the typeCode
     */
    public int getTypeCode() {
        return typeCode;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the sldName
     */
    public String getSldName() {
        return sldName;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj == null || !( obj instanceof SldProperty ) ) {
            return false;
        }
        if ( ( name == null && ( (SldProperty) obj ).name != null )
             || ( sldName == null && ( (SldProperty) obj ).sldName != null ) ) {
            return false;
        }
        return typeCode == ( (SldProperty) obj ).typeCode && name.equals( ( (SldProperty) obj ).name )
               && sldName.equals( ( (SldProperty) obj ).sldName );
    }

    @Override
    public int hashCode() {
        int result = HashCodeUtil.SEED;
        result = HashCodeUtil.hash( result, name );
        result = HashCodeUtil.hash( result, sldName );
        result = HashCodeUtil.hash( result, typeCode );
        return result;
    }
}
