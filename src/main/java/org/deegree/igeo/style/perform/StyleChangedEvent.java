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

package org.deegree.igeo.style.perform;

import org.deegree.igeo.ValueChangedEvent;

/**
 * The <code>StyleChangedEvent</code> indicates that a component of a symbolizer has changed
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class StyleChangedEvent extends ValueChangedEvent {

    private Object value;

    private ComponentType type;

    private boolean dynamic = false;

    /**
     * @param value
     *            the changed value
     * @param type
     *            the type of the changeEvent
     */
    public StyleChangedEvent( Object value, ComponentType type ) {
        this.value = value;
        this.type = type;
    }

    /**
     * @param value
     *            the changed value
     * @param type
     *            the type of the changeEvent
     * @param isDynamic
     *            has dynamic value or fixed value changed?
     */
    public StyleChangedEvent( Object value, ComponentType type, boolean isDynamic ) {
        this.value = value;
        this.type = type;
        this.dynamic = isDynamic;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.ValueChangedEvent#getValue()
     */
    @Override
    public Object getValue() {
        return value;
    }

    /**
     * @return the type of the changeEvent
     */
    public ComponentType getType() {
        return type;
    }

    /**
     * @return true if the changed style is dynamic, false otherwise
     */
    public boolean isDynamic() {
        return dynamic;
    }

}
