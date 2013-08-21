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

import java.awt.image.BufferedImage;

import org.deegree.desktop.style.ImageFactory;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.legend.LegendElement;
import org.deegree.graphics.legend.LegendException;
import org.deegree.graphics.legend.LegendFactory;
import org.deegree.graphics.sld.AbstractStyle;
import org.deegree.graphics.sld.LineSymbolizer;
import org.deegree.graphics.sld.PointSymbolizer;
import org.deegree.graphics.sld.PolygonSymbolizer;
import org.deegree.graphics.sld.RasterSymbolizer;
import org.deegree.graphics.sld.StyleFactory;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.graphics.sld.TextSymbolizer;

/**
 * <code>PolygonSetting</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class Preset {
    
    private static final ILogger LOG = LoggerFactory.getLogger( Preset.class );

    public enum PRESETTYPE {
        POLYGON, POINT, LINE, RASTER, TEXT
    }

    private String name;

    private Symbolizer symbolizer;

    /**
     * @param name
     * @param symbolizer
     *            (must not be null)
     * @throws IllegalArgumentException
     *             if symbolizer is null
     */
    public Preset( String name, Symbolizer symbolizer ) {
        if ( symbolizer == null ) {
            throw new IllegalArgumentException( "symbolizer must not be null" );
        }
        this.name = name;
        this.symbolizer = symbolizer;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the symbolizer
     */
    public Symbolizer getSymbolizer() {
        return symbolizer;
    }

    public BufferedImage getAsImage() {
        if ( symbolizer instanceof RasterSymbolizer ) {
            return ImageFactory.createImage( (RasterSymbolizer) symbolizer );
        } else if ( symbolizer instanceof TextSymbolizer ) {
            return ImageFactory.createImage( (TextSymbolizer) symbolizer );
        } else {
            BufferedImage img = null;
            AbstractStyle as = StyleFactory.createStyle( symbolizer );

            LegendFactory lf = new LegendFactory();
            LegendElement le = null;
            try {
                le = lf.createLegendElement( as, 75, 30, null );
                if ( le != null ) {
                    img = le.exportAsImage( "image/gif" );
                }
            } catch ( LegendException e ) {
                LOG.logWarning( "ignore", e );
            }
            return img;
        }
    }

    /**
     * @return the type of the preset, dependent on the symbolizer
     */
    public PRESETTYPE getType() {
        if ( symbolizer instanceof PolygonSymbolizer ) {
            return PRESETTYPE.POLYGON;
        } else if ( symbolizer instanceof PointSymbolizer ) {
            return PRESETTYPE.POINT;
        } else if ( symbolizer instanceof LineSymbolizer ) {
            return PRESETTYPE.LINE;
        } else if ( symbolizer instanceof RasterSymbolizer ) {
            return PRESETTYPE.RASTER;
        } else if ( symbolizer instanceof TextSymbolizer ) {
            return PRESETTYPE.TEXT;
        }
        return null;
    }

}
