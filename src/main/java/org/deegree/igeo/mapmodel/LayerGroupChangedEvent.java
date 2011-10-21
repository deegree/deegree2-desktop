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

package org.deegree.igeo.mapmodel;

import org.deegree.igeo.ValueChangedEvent;

/**
 * Event class encapsulating information about changes on a layer group
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class LayerGroupChangedEvent extends ValueChangedEvent {

    public enum LAYERGROUP_CHANGE_TYPE {
        visibilityChanged, parentChanged, scaleRangeChanged, selectedForChanged
    };

    private LayerGroup source;

    private Object value;

    private LAYERGROUP_CHANGE_TYPE changeType;

    /**
     * 
     * @param source
     * @param value
     * @param changeType
     */
    public LayerGroupChangedEvent( LayerGroup source, Object value, LAYERGROUP_CHANGE_TYPE changeType ) {
        this.value = value;
        this.source = source;
        this.changeType = changeType;
    }

    @Override
    public Object getValue() {
        return value;
    }

    /**
     * 
     * @return source of a event
     */
    public LayerGroup getSource() {
        return source;
    }

    /**
     * @return the changeType
     */
    public LAYERGROUP_CHANGE_TYPE getChangeType() {
        return changeType;
    }

}
