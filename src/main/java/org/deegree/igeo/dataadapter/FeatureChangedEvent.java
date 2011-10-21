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

package org.deegree.igeo.dataadapter;

import org.deegree.igeo.ValueChangedEvent;
import org.deegree.model.feature.Feature;

/**
 * Event indicating that a feature has changed
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class FeatureChangedEvent extends ValueChangedEvent {

    public enum FEATURE_CHANGE_TYPE {
        inserted, deleted, updated
    };

    private Feature feature;

    private FEATURE_CHANGE_TYPE changedType;

    private FeatureAdapter owner;

    /**
     * 
     * @param feature
     * @param changedType
     * @param owner
     */
    FeatureChangedEvent( Feature feature, FEATURE_CHANGE_TYPE changedType, FeatureAdapter owner ) {
        this.changedType = changedType;
        this.feature = feature;
        this.owner = owner;
    }

    /**
     * @return the changedType
     */
    public FEATURE_CHANGE_TYPE getChangedType() {
        return changedType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.presenter.ValueChangedEvent#getValue()
     */
    @Override
    public Object getValue() {

        return feature;
    }

    /**
     * @return the feature
     */
    public Feature getFeature() {
        return feature;
    }

    /**
     * @return the owner
     */
    public FeatureAdapter getOwner() {
        return owner;
    }

}
