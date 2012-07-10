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

package org.deegree.igeo.views.swing.geoprocessing;

import org.deegree.crs.components.Unit;
import org.deegree.datatypes.QualifiedName;
import org.deegree.igeo.commands.geoprocessing.BufferCommand.BUFFERTYPE;
import org.deegree.model.spatialschema.Geometry;

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
public interface BufferModel {
    
    /**
     * 
     * @return buffer distances in units of the current map
     */
    double[] getDistances();
    
    /**
     * 
     * @return number of segments to create for arcs/circles
     */
    int getSegments();
    
    /**
     * 
     * @return cap style; see {@link Geometry}
     */
    int getCapStyle();
    
    /**
     * 
     * @return name of the geometry property that shall be buffered
     */
    QualifiedName getGeometryProperty();
    
    /**
     * 
     * @return name/title of the new layer that shall receive the features (buffers) 
     */
    String getNewLayerName();
    
    /**
     * 
     * @return type of buffer to create
     */
    BUFFERTYPE getBufferType();
    
    /**
     * 
     * @return <code>true</code> if intersecting buffers shall be merged
     */
    boolean shallMerge();

    /**
     * 
     * @return name of the property containing buffer size or <code>null</code> if no property has been selected
     */
    QualifiedName getPropertyForBufferDistance();
    
    /**
     * 
     * @return true if buffers should be overlayed if multiple bufferes are selected
     */
    boolean isOverlayedBuffers();
    
    /**
     * 
     * @return units to be used for buffer calculation
     */
    Unit getBufferUnit();
}
