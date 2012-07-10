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

package org.deegree.igeo.views.swing;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class CursorRegistry {

    public static Cursor DRAW_CURSOR = new Cursor( Cursor.CROSSHAIR_CURSOR );

    public static Cursor DRAG_CURSOR = new Cursor( Cursor.HAND_CURSOR );

    public static Cursor MOVE_CURSOR = new Cursor( Cursor.MOVE_CURSOR );

    public static Cursor DELETE_CURSOR = new Cursor( Cursor.MOVE_CURSOR );

    public static Cursor SELECT_CURSOR = new Cursor( Cursor.CROSSHAIR_CURSOR );

    public static Cursor DEFAULT_CURSOR = new Cursor( Cursor.DEFAULT_CURSOR );

    public static Cursor WAIT_CURSOR = new Cursor( Cursor.WAIT_CURSOR );

    public static Cursor RULER_CURSOR;
    static {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        URL url = CursorRegistry.class.getResource( "/org/deegree/igeo/views/images/rulercursor.gif" );
        try {
            Image rulerImage = ImageIO.read( url );
            RULER_CURSOR = toolkit.createCustomCursor( rulerImage, new Point( 16, 16 ), "RulerCursor" );
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
