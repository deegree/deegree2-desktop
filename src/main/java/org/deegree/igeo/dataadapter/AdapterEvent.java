//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.igeo.dataadapter;

import org.deegree.igeo.ValueChangedEvent;
import org.deegree.igeo.mapmodel.Layer;

/**
 * Evnet class to be used if something has changed according to a datasource or an adapter
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class AdapterEvent extends ValueChangedEvent {

    public enum ADAPTER_EVENT_TYPE {
        startedLoading, finishedLoading, canceledLoading, exceptionLoading
    };

    private Layer layer;
    private ADAPTER_EVENT_TYPE eventType;

    /**
     * 
     * @param layer
     * @param eventType
     */
    public AdapterEvent( Layer layer, ADAPTER_EVENT_TYPE eventType ) {
        this.layer = layer;
        this.eventType = eventType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.ValueChangedEvent#getValue()
     */
    @Override
    public Object getValue() {
        return layer;
    }
    
    /**
     * 
     * @return event type
     */
    public ADAPTER_EVENT_TYPE getType() {
       return eventType;
    }

}
