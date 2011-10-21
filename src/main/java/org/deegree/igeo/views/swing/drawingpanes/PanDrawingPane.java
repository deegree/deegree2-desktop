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

package org.deegree.igeo.views.swing.drawingpanes;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * <code>PanDrawingPane</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class PanDrawingPane extends RectangleDrawingPane {

    private BufferedImage image;

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.RectangleDrawingPane#draw(int, int, java.awt.Graphics)
     */
    @Override
    public void draw( int x, int y, Graphics g ) {
        if ( this.isDrawing ) {
            // set the origin of the original content of the image
            int x_ = ( this.image.getWidth() / 4 ) + this.startX;
            int y_ = ( this.image.getHeight() / 4 ) + this.startY;

            g.drawImage( this.image, x - x_, y - y_, null );

            this.currentX = x;
            this.currentY = y;
        }

    }

    /**
     * Sets the image to draw when draw() will be invoked. The image must be twice as large as the original content,
     * because the screen can not be cleaned without flipping.
     * 
     * @param image
     *            the image of the view to draw (must be two times larger then the original image and the content must
     *            be drawn at position image.getWidth/4 / image.getHight/4)
     */
    public void setImage( BufferedImage image ) {
        this.image = image;
    }
}
