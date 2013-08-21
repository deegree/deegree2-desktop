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
 * Event class encapsulating information about changes on a layer
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class LayerChangedEvent extends ValueChangedEvent {

    public enum LAYER_CHANGE_TYPE {
        visibilityChanged, dataChanged, parentChanged, datasourceAdded, datasourceRemoved, datasourceChanged, featureSelected, featureUnselected, styleAdded, styleRemoved, stylesSet, scaleRangeChanged, selectedForChanged, dataLoadState
    };

    private Layer source;

    private Object value;

    private LAYER_CHANGE_TYPE changeType;

    private ValueChangedEvent embeddedEvent;

    /**
     * 
     * @param source
     * @param value
     * @param changeType
     * @param embeddedEvent
     */
    public LayerChangedEvent( Layer source, Object value, LAYER_CHANGE_TYPE changeType, ValueChangedEvent embeddedEvent ) {
        this.value = value;
        this.source = source;
        this.changeType = changeType;
        this.embeddedEvent = embeddedEvent;
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
     * 
     * @return source of a event
     */
    public Layer getSource() {
        return source;
    }

    /**
     * @return the changeType
     */
    public LAYER_CHANGE_TYPE getChangeType() {
        return changeType;
    }

    /**
     * @return the embeddedEvent
     */
    public ValueChangedEvent getEmbeddedEvent() {
        return embeddedEvent;
    }

}
