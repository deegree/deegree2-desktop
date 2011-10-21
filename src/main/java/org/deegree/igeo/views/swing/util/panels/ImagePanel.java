//$HeadURL$
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

package org.deegree.igeo.views.swing.util.panels;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * <code>ImagePanel</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ImagePanel extends JPanel {

    private static final long serialVersionUID = 4498439308789120291L;

    private final int width;

    private final int height;

    private BufferedImage image;

    /**
     * @param width
     * @param height
     */
    public ImagePanel( int width, int height ) {
        this.width = width;
        this.height = height;
        setPreferredSize( new Dimension( width, height ) );
        setMinimumSize( new Dimension( width, height ) );
        setMaximumSize( new Dimension( width, height ) );
    }

    /**
     * @param image
     */
    public void setImage( BufferedImage image ) {
        this.image = image;
    }

    @Override
    public void paint( Graphics g ) {
        if ( image != null ) {
            int width = this.width;
            int height = this.height;
            double ratio = (double) image.getWidth() / (double) image.getHeight();
            if ( ratio < 1 ) {
                width = (int) ( width * ratio );
            } else {
                height = (int) ( height / ratio );
            }
            g.setColor( getBackground() );
            g.fillRect( 0, 0, this.width, this.height );
            g.drawImage( image, 0, 0, width, height, null );
        }
    }

}
