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

package org.deegree.desktop.views.swing.util;

import static org.deegree.framework.log.LoggerFactory.getLogger;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.deegree.framework.log.ILogger;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class IconRegistry {

    private static final ILogger LOG = getLogger( IconRegistry.class );

    private static final String ICONROOT = "/org/deegree/desktop/views/images/";

    private static Map<String, Icon> iconMap;
    static {
        if ( iconMap == null ) {
            iconMap = new HashMap<String, Icon>( 100 );
        }
    }

    /**
     * returns an icon for a name. If a icon with passed name is not available within the registy first it will be
     * looked if the name is an absolute reference (starting with '/'). If so it will be loaded using
     * <code>IconRegistry.class.getResource( name )</code>. If not icon root path (/org/deegree/desktop/views/images/) will
     * be added and loadin will be trieed again.
     * 
     * @param name
     *            e.g. "layer.gif" or "/org/deegree/desktop/views/images/add.gif"
     * @return {@link ImageIcon}
     */
    public static Icon getIcon( String name ) {
        Icon icon = iconMap.get( name );
        if ( icon == null ) {
            try {
                if ( name.startsWith( "/" ) ) {
                    icon = new ImageIcon( IconRegistry.class.getResource( name ) );
                } else {                  
                    icon = new ImageIcon( IconRegistry.class.getResource( ICONROOT + name ) );
                }
                iconMap.put( name, icon );
            } catch ( NullPointerException e ) {
                LOG.logError( "Error while loading the icon named " + name, e );
            }
        }
        return icon;
    }

    /**
     * returns an icon for a URL. If a icon with passed name is not available within the registy if will be loaded from
     * the passed URL.
     * 
     * @param url
     *            must be absolute
     * @return {@link ImageIcon}
     */
    public static Icon getIcon( URL url ) {
        String urlTxt = null;

        try {
            urlTxt = url.toURI().toASCIIString();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        Icon icon = iconMap.get( urlTxt );
        if ( icon == null ) {            
            icon = new ImageIcon( url );
            iconMap.put( urlTxt, icon );
        }
        return icon;
    }

}
