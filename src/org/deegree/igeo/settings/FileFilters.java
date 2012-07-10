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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.igeo.config.FileFilterType;
import org.deegree.igeo.config.FileFilterType.Format;

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
public class FileFilters extends ElementSettings {

    private List<String> extensions;

    private Map<String, String> descriptions;

    private Map<String, Boolean> isVector;

    /**
     * @param fileFilterType
     * @param changeable
     */
    public FileFilters( FileFilterType fileFilterType, boolean changeable ) {
        super( changeable );
        List<Format> list = fileFilterType.getFormat();
        extensions = new ArrayList<String>( list.size() );
        descriptions = new HashMap<String, String>( list.size() );
        isVector = new HashMap<String, Boolean>( list.size() );
        for ( Format format : list ) {
            extensions.add( format.getExtension() );
            descriptions.put( format.getExtension(), format.getDesc() );
            isVector.put( format.getExtension(), format.isIsVector() );
        }

    }

    /**
     * 
     * @return list of file extensions (upper case without leading '.') to be considered
     */
    public List<String> getFileExtensions() {
        return extensions;
    }

    /**
     * 
     * @param extension
     * @return description for passed extension
     */
    public String getDescription( String extension ) {
        return descriptions.get( extension );
    }

    /**
     * 
     * @param extension
     * @return <code>true</code> if passed extension belongs to a vector data format
     */
    public boolean isVectorFormat( String extension ) {
        return isVector.get( extension );
    }
}
