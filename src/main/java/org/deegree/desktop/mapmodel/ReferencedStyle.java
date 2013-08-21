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
package org.deegree.desktop.mapmodel;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.deegree.desktop.i18n.Messages;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.graphics.sld.AbstractStyle;
import org.deegree.graphics.sld.SLDFactory;
import org.deegree.graphics.sld.StyledLayerDescriptor;
import org.deegree.graphics.sld.UserStyle;
import org.deegree.desktop.config.OnlineResourceType;
import org.deegree.desktop.config.ReferencedStyleType;
import org.deegree.desktop.config.ReferencedStyleType.Linkage;

/**
 * Wrapper class for a style that are assigned to a project by reference
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class ReferencedStyle extends DefinedStyle {

    /**
     * 
     * @param rst
     * @param style
     * @param owner
     */
    public ReferencedStyle( ReferencedStyleType rst, UserStyle style, Layer owner ) {
        super( rst, style, owner );
    }

    /**
     * 
     * @return link to SLD document
     */
    public URL getLinkage() {
        try {
            return new URL( ( (ReferencedStyleType) nst ).getLinkage().getOnlineResource().getHref() );
        } catch ( MalformedURLException e ) {
            throw new MapModelException( e.getMessage(), e );
        }
    }

    /**
     * sets a new style resource. Setting of a new linkage (style resource) requires loading the new style
     * 
     * @param url
     *            URL to new style definition (SLD document)
     */
    public void setLinkage( URL url ) {
        OnlineResourceType ort = new OnlineResourceType();
        ort.setHref( url.toExternalForm() );
        Linkage linkage = new Linkage();
        linkage.setOnlineResource( ort );
        // setting of a new linkage (style resource) requires loading the new style
        StyledLayerDescriptor sld;
        try {
            sld = SLDFactory.createSLD( url );
        } catch ( XMLParsingException e ) {
            throw new MapModelException( e.getMessage(), e );
        }
        this.style = sld.getNamedLayers()[0].getStyles()[0];
    }

    /**
     * The passed style must be an instance of {@link org.deegree.graphics.sld.UserStyle}. Extenting classes may
     * override this method to ensure that the passed variable is instance of another {@link AbstractStyle}. This method
     * also will throw a {@link RuntimeException} if the reference/linkage if not a file URL so that a changed style can
     * not be stored
     * 
     * @param style
     */
    public void setStyle( AbstractStyle style ) {
        String tmp = ( (ReferencedStyleType) nst ).getLinkage().getOnlineResource().getHref();
        if ( !tmp.toLowerCase().startsWith( "file:" ) ) {
            throw new MapModelException( Messages.getMessage( Locale.getDefault(), "$DG10076", tmp ) );
        }
        super.setStyle( style );

    }

}