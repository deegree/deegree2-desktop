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

package org.deegree.desktop.style.model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;

/**
 * <code>FillGraphic</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class FillPattern extends GraphicSymbol {

    private static final ILogger LOG = LoggerFactory.getLogger( FillPattern.class );

    private Color color;

    private boolean colorHasChanged = false;

    private BufferedImage image;

    /**
     * 
     * @param name
     * @param url
     * @param color
     */
    public FillPattern( String name, URL url, Color color ) {
        super( name, url );
        this.color = color;
    }

    /**
     * @param fillPattern
     */
    public FillPattern( FillPattern fillPattern ) {
        this( fillPattern.getName(), fillPattern.getUrl(), fillPattern.getColor() );
        setSize( fillPattern.getSize() );
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @param color
     *            the color to set
     */
    public void setColor( Color color ) {
        this.color = color;
        this.colorHasChanged = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.model.GraphicSymbol#getAsImage()
     */
    @Override
    public BufferedImage getAsImage() {
        try {
            if ( image == null || colorHasChanged ) {
                BufferedImage bi = ImageIO.read( getUrl() );

                for ( int i = 0; i < bi.getWidth(); i++ ) {
                    for ( int j = 0; j < bi.getHeight(); j++ ) {
                        if ( bi.getRGB( i, j ) == Color.BLACK.getRGB() ) {
                            bi.setRGB( i, j, color.getRGB() );
                        }
                    }
                }

                image = bi;
                this.colorHasChanged = false;
            }
            return image;
        } catch ( Exception e ) {
            LOG.logError( "Could not create icon for URL " + getUrl(), e );
        }
        return null;
    }

}
