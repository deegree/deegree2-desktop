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
package org.deegree.igeo.views.swing.util.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author <a href="http://www.esus.com/docs/GetQuestionPage.jsp?uid=1270&type=pf">Nobuo Tamemasa</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class BlankIcon implements Icon {
    private Color fillColor;

    private int size;

    /**
     * 
     */
    public BlankIcon() {
        this( null, 11 );
    }

    /**
     * 
     * @param color
     * @param size
     */
    public BlankIcon( Color color, int size ) {
        fillColor = color;
        this.size = size;
    }

    public void paintIcon( Component c, Graphics g, int x, int y ) {
        if ( fillColor != null ) {
            g.setColor( fillColor );
            g.drawRect( x, y, size - 1, size - 1 );
        }
    }

    public int getIconWidth() {
        return size;
    }

    public int getIconHeight() {
        return size;
    }
}