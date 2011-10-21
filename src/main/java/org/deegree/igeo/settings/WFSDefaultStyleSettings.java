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
package org.deegree.igeo.settings;

import java.io.Reader;
import java.io.StringReader;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.graphics.sld.SLDFactory;
import org.deegree.graphics.sld.UserStyle;
import org.deegree.igeo.config.WFSDefaultStyleType;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class WFSDefaultStyleSettings extends ElementSettings {

    private static final ILogger LOG = LoggerFactory.getLogger( WFSDefaultStyleSettings.class );

    private WFSDefaultStyleType defaultStyle;

    private UserStyle style;

    /**
     * @param changeable
     * @param defaultStyle
     */
    public WFSDefaultStyleSettings( WFSDefaultStyleType defaultStyle, boolean changeable ) {
        super( changeable );
        this.defaultStyle = defaultStyle;
        try {
            XMLFragment xml = new XMLFragment();      
            Reader reader = new StringReader( this.defaultStyle.getStyle().trim() );
            xml.load( reader, XMLFragment.DEFAULT_URL );
            style = SLDFactory.createUserStyle( xml.getRootElement() );
        } catch ( Exception e ) {
            LOG.logError( e );
        }
    }

    /**
     * 
     * @return WFS data source based layers default style
     */
    public UserStyle getDefaultStyle() {
        return style;
    }

    /**
     * set new default style (just if default style is changeable)
     * 
     * @param style
     */
    public void setDefaultStyle( UserStyle style ) {
        if ( changeable ) {
            this.style = style;
            defaultStyle.setStyle( style.exportAsXML() );
        }
    }

}
