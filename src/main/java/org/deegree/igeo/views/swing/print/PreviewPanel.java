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
package org.deegree.igeo.views.swing.print;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.lowagie.text.Rectangle;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class PreviewPanel extends JPanel {

    private static final long serialVersionUID = 1764192533759051876L;

    private Rectangle pageSize;

    private int areaWidth;

    private int areaHeight;

    private int areaLeft;

    private int areaTop;

    /**
     * @param pageSize
     *            the pageSize to set
     */
    public void setPageSize( Rectangle pageSize ) {
        this.pageSize = pageSize;
        repaint();
    }

    /**
     * @param areaWidth
     *            the areaWidth to set
     */
    public void setAreaWidth( int areaWidth ) {
        this.areaWidth = areaWidth;
        repaint();
    }

    /**
     * @param areaHeight
     *            the areaHeight to set
     */
    public void setAreaHeight( int areaHeight ) {
        this.areaHeight = areaHeight;
        repaint();
    }

    /**
     * @param areaLeft
     *            the areaLeft to set
     */
    public void setAreaLeft( int areaLeft ) {
        this.areaLeft = areaLeft;
        repaint();
    }

    /**
     * @param areaTop
     *            the areaTop to set
     */
    public void setAreaTop( int areaTop ) {
        this.areaTop = areaTop;
        repaint();
    }

    @Override
    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        int w = getWidth();
        int h = getHeight() - 10;
        g.setColor( Color.white );
        if ( pageSize == null ) {
            // will be the case at first invocation
            g.fillRect( 10, 10, w - 20, h - 20 );
        } else {
            double d = ( h - 30 ) / pageSize.getHeight();
            // page
            int w1 = (int) Math.round( pageSize.getWidth() * d );
            int h1 = (int) Math.round( pageSize.getHeight() * d );
            g.fillRect( ( w - w1 ) / 2, ( h - h1 ) / 2, w1, h1 );

            // print area
            int x = (int) Math.round( areaLeft * 72 / 25.4 * d );
            int w2 = (int) Math.round( areaWidth * 72 / 25.4 * d );
            int y = (int) Math.round( areaTop * 72 / 25.4 * d );
            int h2 = (int) Math.round( areaHeight * 72 / 25.4 * d );
            g.setColor( Color.BLACK );
            g.drawRect( ( w - w1 ) / 2 + x, ( h - h1 ) / 2 + y, w2, h2 );
        }
    }
}
