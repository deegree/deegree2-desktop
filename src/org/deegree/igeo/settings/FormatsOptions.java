//$HeadURL: svn+ssh://developername@svn.wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
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

package org.deegree.igeo.settings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.igeo.config.FormatsType;
import org.deegree.igeo.config.FormatsType.Format;

/**
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author: Andreas Poth $
 * 
 * @version $Revision: $, $Date: $
 * 
 */
public class FormatsOptions extends ElementSettings {

    private FormatsType formatsType;

    private Map<String, String> map = new HashMap<String, String>();

    /**
     * @param formatsType
     * @param changeable
     */
    public FormatsOptions( FormatsType formatsType, boolean changeable ) {
        super( changeable );
        this.formatsType = formatsType;
        List<Format> formats = formatsType.getFormat();
        for ( Format format : formats ) {
            map.put( format.getName(), format.getPattern() );
        }
    }

    /**
     * 
     * @param name
     * @return pattern assigned to name
     */
    public String getPattern( String name ) {
        return map.get( name );
    }

    /**
     * set a new pattern or changes an existing one
     * 
     * @param name
     * @param pattern
     */
    public void setPattern( String name, String pattern ) {
        if ( changeable ) {
            map.put( name, pattern );
            List<Format> list = formatsType.getFormat();
            for ( Format format : list ) {
                if ( format.getName().equals( name ) ) {
                    format.setPattern( pattern );
                    return;
                }
            }
            Format frm = new FormatsType.Format();
            frm.setName( name );
            frm.setPattern( pattern );
            list.add( frm );
        }
    }

}
