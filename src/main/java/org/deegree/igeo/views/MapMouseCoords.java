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

import org.deegree.model.crs.UnknownCRSException;



/**
 * <code>MapMouseCoords</code> is used to display mouse coordinates of a map.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public interface MapMouseCoords {

    /**
     * @param mouseX
     *            the x-coordinate of the mouse position in the map in pixel
     * @param mouseY
     *            the y-coordinate of the mouse position in the map in pixel
     * @param componentWidth
     *            the width of the component
     * @param componentHeight
     *            the height of the component
     */
    void setMouseCoords( double mouseX, double mouseY, double componentWidth, double componentHeight );

    /**
     * sets mouseCoordinates to null (can be used when e.g. the cursor leaves the map)
     */
    void deleteMouseCoords();
    
    /**
     * 
     * @param name name of CRS to be used for displaying coordinates; default is current map crs
     * @throws UnknownCRSException
     */
    void setDisplayCRS(String name) throws UnknownCRSException;

}
