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
package org.deegree.igeo.mapmodel;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.HttpUtils;
import org.deegree.framework.util.ImageUtils;
import org.deegree.graphics.sld.AbstractStyle;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.config.NamedStyleType;
import org.deegree.igeo.config.OnlineResourceType;
import org.deegree.igeo.config.NamedStyleType.LegendURL;
import org.deegree.igeo.i18n.Messages;

/**
 * Wrapper class for a style managed/identified by its well known name
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class NamedStyle {

    private static final ILogger LOG = LoggerFactory.getLogger( NamedStyle.class );

    protected BufferedImage legendImage;

    protected AbstractStyle style;

    protected NamedStyleType nst;

    protected Layer owner;

    /**
     * 
     * @param nst
     */
    public NamedStyle( NamedStyleType nst, Layer owner ) {
        this.nst = nst;
        this.owner = owner;
        try {
            if ( nst.getLegendURL() != null ) {
                URL legendURL = new URL( nst.getLegendURL().getOnlineResource().getHref() );
                legendImage = ImageUtils.loadImage( legendURL );
            } else if ( nst.getLegendImage() != null ) {
                byte[] b = nst.getLegendImage();
                if ( b == null || b.length < 100 ) {
                    throw new Exception( "no legend image available for style: " + nst.getTitle() );
                } else {
                    ByteArrayInputStream bis = new ByteArrayInputStream( b );
                    legendImage = ImageUtils.loadImage( bis );
                }
            }
        } catch ( Exception e ) {
            LOG.logWarning( e.getMessage() );
        }
        style = new org.deegree.graphics.sld.NamedStyle( getName() );
    }

    /**
     * 
     * @return abstract
     */
    public String getAbstract() {
        return nst.getAbstract();
    }

    /**
     * 
     * @param abstract_
     */
    public void setAbstract( String abstract_ ) {
        nst.setAbstract( abstract_ );
    }

    /**
     * 
     * @return true if current style for a layer
     */
    public boolean isCurrent() {
        return nst.isCurrent();
    }

    /**
     * 
     * @param isCurrent
     */
    public void setCurrent( boolean isCurrent ) {
        nst.setCurrent( isCurrent );
    }

    /**
     * 
     * @return legend URL
     */
    public URL getLegendURL() {
        if ( nst.getLegendURL() != null && nst.getLegendURL().getOnlineResource() != null
             && nst.getLegendURL().getOnlineResource().getHref() != null ) {
            try {

                return new URL( nst.getLegendURL().getOnlineResource().getHref() );
            } catch ( Exception e ) {
                LOG.logWarning( "ignore", e );
            }
        }
        return null;
    }

    /**
     * 
     * @param legendURL must not be <code>null</code>.
     * @throws IOException
     */
    public void setLegendURL( URL legendURL )
                            throws IOException {
        OnlineResourceType ort = new OnlineResourceType();
        ort.setHref( legendURL.toExternalForm() );
        LegendURL lu = new LegendURL();
        lu.setOnlineResource( ort );
        nst.setLegendURL( lu );
        ApplicationContainer<?> appCont = owner.getOwner().getApplicationContainer();
        String tmp = HttpUtils.normalizeURL( legendURL );
        tmp = HttpUtils.addAuthenticationForKVP( legendURL.toExternalForm(), appCont.getUser(),
                                                 appCont.getPassword(), appCont.getCertificate( tmp ) );
        LOG.logDebug( "read legend image: ", tmp );
        int timeout = appCont.getSettings().getWMSGridCoveragesAdapter().getTimeout();
        InputStream is = HttpUtils.performHttpGet( tmp, "", timeout, appCont.getUser(), appCont.getPassword(), null ).getResponseBodyAsStream();
        legendImage = ImageUtils.loadImage( is );
        nst.setLegendImage( null ); // TODO why null and not legendImgage? Or this.setLegendImage(legendImage)?
    }

    /**
     * 
     * @return legend image
     */
    public BufferedImage getLegendImage() {
        return this.legendImage;
    }

    /**
     * 
     * @param image
     */
    public void setLegentImage( BufferedImage image ) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageUtils.saveImage( image, bos, "png", 1f );
        } catch ( IOException e ) {
            throw new MapModelException( Messages.getMessage( Locale.getDefault(), "$DG10076", e.getMessage() ) );
        }
        nst.setLegendImage( bos.toByteArray() );
        nst.setLegendURL( null );
        this.legendImage = image;
    }

    /**
     * 
     * @return style title
     */
    public String getTitle() {
        return nst.getTitle();
    }

    /**
     * 
     * @param title
     */
    public void setTitle( String title ) {
        nst.setTitle( title );
    }

    /**
     * 
     * @return style name
     */
    public String getName() {
        return nst.getName();
    }

    /**
     * 
     * @param name
     */
    public void setName( String name ) {
        nst.setName( name );
    }

    /**
     * 
     * @return style
     */
    public AbstractStyle getStyle() {
        return this.style;
    }

    /**
     * The passed style must be an instance of {@link org.deegree.graphics.sld.NamedStyle}. Extenting classes may
     * override this method to ensure that the passed variable is instance of another {@link AbstractStyle}
     * 
     * @param style
     */
    public void setStyle( AbstractStyle style ) {
        if ( !( style instanceof org.deegree.graphics.sld.NamedStyle ) ) {
            throw new MapModelException( Messages.getMessage( Locale.getDefault(), "$DG10073" ) );
        }
        this.style = style;
    }

}