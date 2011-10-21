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
package org.deegree.igeo.mapmodel;

import java.awt.image.BufferedImage;
import java.util.Locale;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.legend.LegendElement;
import org.deegree.graphics.legend.LegendFactory;
import org.deegree.graphics.sld.AbstractStyle;
import org.deegree.graphics.sld.UserStyle;
import org.deegree.igeo.config.DefinedStyleType;
import org.deegree.igeo.i18n.Messages;

/**
 * a DefinedStyle is a style that is identified by its name and that is defined within a style repository that is
 * accessable for the using client.
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public abstract class DefinedStyle extends NamedStyle {

    private static final ILogger LOG = LoggerFactory.getLogger( DefinedStyle.class );

    public static final String UOM_MAP = "map";

    public static final String UOM_PIXEL = "pixel";

    private String uom = UOM_PIXEL;

    /**
     * 
     * @param dst
     * @param style
     * @param owner
     */
    public DefinedStyle( DefinedStyleType dst, UserStyle style, Layer owner ) {
        super( dst, owner );
        this.style = style;
        LegendFactory lf = new LegendFactory();
        try {
            LegendElement le = lf.createLegendElement( style, 20, 20, style.getName() );
            legendImage = le.exportAsImage( "image/png" );
        } catch ( Exception e ) {
            legendImage = new BufferedImage( 20, 20, BufferedImage.TYPE_INT_ARGB );
            LOG.logWarning( "can not create default legend symbol" );
        }
        this.uom = dst.getUom();
    }

    /**
     * The passed style must be an instance of {@link org.deegree.graphics.sld.UserStyle}. Extenting classes may
     * override this method to ensure that the passed variable is instance of another {@link AbstractStyle}
     * 
     * @param style
     */
    public void setStyle( AbstractStyle style ) {
        if ( !( style instanceof UserStyle ) ) {
            throw new MapModelException( Messages.getMessage( Locale.getDefault(), "$DG10074" ) );
        }
        this.style = style;
    }

    /**
     * @return the type of the unit of measure (pixel or map are support) used by default
     */
    public String getUom() {
        return uom;
    }

    /**
     * @param value
     *            the type of the unit of measure (pixel or map are support) used by default
     */
    public void setUom( String value ) {
        uom = value;
    }
}