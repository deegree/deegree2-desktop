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

package org.deegree.desktop.mapmodel;

import org.deegree.desktop.ValueChangedEvent;

/**
 * Event class encapsulating information about changes on a map model
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class MapModelChangedEvent extends ValueChangedEvent {

    public enum CHANGE_TYPE {
        layerOrderChanged, layerRemoved, layerInserted, extentChanged, targetDeviceChanged, 
        crsChanged, crsAdded, crsSet, crsRemoved, layerGroupRemoved, layerGroupInserted,
        layerStateChanged, repaintForced
    };

    private CHANGE_TYPE changeType;

    private Object source;

    private ValueChangedEvent embeddedEvent;

    private Object value;

    /**
     * 
     * @param changeType
     * @param source
     * @param value
     * @param embeddedEvent
     */
    public MapModelChangedEvent( CHANGE_TYPE changeType, Object source, Object value, ValueChangedEvent embeddedEvent ) {
        this.changeType = changeType;
        this.source = source;
        this.value = value;
        this.embeddedEvent = embeddedEvent;
    }

    /**
     * @return the changeType
     */
    public CHANGE_TYPE getChangeType() {
        return changeType;
    }

    /**
     * @return the source
     */
    public Object getSource() {
        return source;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @return the embeddedEvent
     */
    public ValueChangedEvent getEmbeddedEvent() {
        return embeddedEvent;
    }

}
