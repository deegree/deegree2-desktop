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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * <code>SingleColor</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class FillColor implements Fill {

    private Color color;

    /**
     * @param color
     */
    public FillColor( Color color ) {
        super();
        this.color = color;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.model.Color#getRamp(int)
     */
    public List<Object> getFills( int count ) {
        List<Object> fills = new ArrayList<Object>( count );
        for ( int i = 0; i < count; i++ ) {
            fills.add( color );
        }
        return fills;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

}
