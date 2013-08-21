//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2012 by:
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

 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: info@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/

package org.deegree.desktop.views.swing;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * TODO add class description
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class CursorRegistry {

    public final static Cursor DRAW_CURSOR = new Cursor( Cursor.CROSSHAIR_CURSOR );

    public final static Cursor DRAG_CURSOR = new Cursor( Cursor.HAND_CURSOR );

    public final static Cursor MOVE_CURSOR = new Cursor( Cursor.MOVE_CURSOR );

    public final static Cursor DELETE_CURSOR = new Cursor( Cursor.MOVE_CURSOR );

    public final static Cursor SELECT_CURSOR = new Cursor( Cursor.CROSSHAIR_CURSOR );

    public final static Cursor DEFAULT_CURSOR = new Cursor( Cursor.DEFAULT_CURSOR );

    public final static Cursor WAIT_CURSOR = new Cursor( Cursor.WAIT_CURSOR );

    public final static Cursor RULER_CURSOR;
    static {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        URL url = CursorRegistry.class.getResource( "/org/deegree/desktop/views/images/rulercursor.gif" );
        Image rulerImage = null;
        try {
            rulerImage = ImageIO.read( url );
        } catch ( IOException e ) {
        	// TODO add default image to prevent cursor to disappear if exception is catched
            e.printStackTrace();
        }
        RULER_CURSOR = toolkit.createCustomCursor( rulerImage, new Point( 16, 16 ), "RulerCursor" );
    }

}
