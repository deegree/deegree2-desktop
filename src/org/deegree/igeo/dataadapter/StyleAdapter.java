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
package org.deegree.igeo.dataadapter;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.net.URL;

import org.deegree.graphics.sld.AbstractStyle;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.NamedStyle;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class StyleAdapter extends Adapter {

    private NamedStyle style;

    private Layer layer;

    /**
     * 
     * @param style
     * @param layer
     */
    public StyleAdapter( NamedStyle style, Layer layer ) {
        this.style = style;
        this.layer = layer;
    }

    /**
     * 
     * @return style name
     */
    public String getName() {
        return style.getName();
    }

    /**
     * 
     * @return style title
     */
    public String getTitle() {
        return style.getTitle();
    }

    /**
     * 
     * @return abstract describing a style
     */
    public String getAbstract() {
        return style.getAbstract();
    }

    /**
     * 
     * @return URL to access a legend image
     */
    public URL getLegendURL() {
        return style.getLegendURL();
    }

    /**
     * 
     * @return SLD style definition
     */
    public AbstractStyle getStyle() {
        return this.style.getStyle();
    }

    /**
     * 
     * @param format
     * @param height
     * @param width
     */
    public BufferedImage getLegend( int height, int width ) {
        BufferedImage bi = style.getLegendImage();
        if ( height > -1 && width > -1 ) {
            if ( height != bi.getHeight() || width != bi.getWidth() ) {
                BufferedImage bii = new BufferedImage( width, height, bi.getType() );
                Graphics g = bii.getGraphics();
                g.drawImage( bi, 0, 0, width, height, null );
                g.dispose();
                bi = bii;
            }
        }
        return bi;
    }

    /**
     * 
     * @return
     */
    public boolean isCurrent() {
        return this.style.isCurrent();
    }

    /**
     * set style to be a layers current style
     * 
     */
    void setCurrent( boolean isCurrent ) {
        if ( isCurrent ) {
            // first revoke status = true from current style
            layer.getCurrentStyle().setCurrent( false );            
        }
        style.setCurrent( isCurrent );

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.IMapModelAdapter#refresh()
     */
    public void refresh() {
        // TODO Auto-generated method stub

    }

}