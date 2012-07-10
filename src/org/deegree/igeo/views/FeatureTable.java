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

package org.deegree.igeo.views;

import java.util.List;

import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.views.swing.objectinfo.FeatureTablePanel;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.filterencoding.Filter;

/**
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public interface FeatureTable {

    /**
     * selects n passed features
     * 
     * @param idList
     */
    void select( FeatureCollection fc );

    /**
     * selects n features (rows) matching passed filter expression
     * 
     * @param filter
     */
    void select( Filter filter );

    /**
     * 
     * @return
     */
    List<Feature> getSelected();

    /**
     * sets a new FeatureCollection to be displayed with a {@link FeatureTablePanel}
     * 
     * @param layer
     * @param featureCollection
     */
    void setFeatureCollection( Layer layer, FeatureCollection featureCollection );

    /**
     * Reload the features etc.
     */
    void refresh();
    
    /**
     * removes all rows 
     */
    void clear();

}
