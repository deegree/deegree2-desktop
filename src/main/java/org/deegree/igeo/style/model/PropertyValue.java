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

import java.util.ArrayList;
import java.util.List;

import org.deegree.model.feature.schema.PropertyType;

/**
 * A <code>PropertyValue</code> gives a statistic view to a feature property. It does not contain all values but each
 * value one time and also its occurence.
 * 
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class PropertyValue<T extends Comparable<T>> {

    private final PropertyType propertyType;

    private List<T> values = new ArrayList<T>();

    /**
     * @param propertyType
     *            the type of the property
     */
    public PropertyValue( PropertyType propertyType ) {
        this.propertyType = propertyType;
    }

    /**
     * @param value
     *            not the value directly will be added, but its occurence in the map will be updated (if no entry for
     *            the value exist, it will be added with the inital occurence of 1, otherwise the counter will be
     *            increased)
     */
    public void putInMap( T value ) {
        values.add( value );
    }

    public List<T> getValues() {
        return values;
    }

    /**
     * @return the data type of the property type as code
     */
    public int getDatatyp() {
        return propertyType.getType();
    }

    /**
     * @return the propertyType
     */
    public PropertyType getPropertyType() {
        return propertyType;
    }

    @Override
    public String toString() {
        return values.toString();
    }

}
