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

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.config.FileAccessType;
import org.deegree.igeo.config.FileAccessType.Access;
import org.deegree.igeo.io.FileSystemAccess;
import org.deegree.igeo.io.LocalFSAccess;
import org.deegree.igeo.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;

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
public class FileAccessOptions extends ElementSettings {

    private static final ILogger LOG = LoggerFactory.getLogger( FileAccessOptions.class );

    private Map<FILECHOOSERTYPE, Class<?>> map = new HashMap<FILECHOOSERTYPE, Class<?>>();

    private FileAccessType fileAccessType;

    /**
     * @param fileAccessType
     * @param changeable
     */
    public FileAccessOptions( FileAccessType fileAccessType, boolean changeable ) {
        super( changeable );
        this.fileAccessType = fileAccessType;
        List<Access> list = this.fileAccessType.getAccess();
        for ( Access access : list ) {
            try {
                map.put( FILECHOOSERTYPE.valueOf( access.getFileType() ), Class.forName( access.getClazz() ) );
            } catch ( ClassNotFoundException e ) {
                LOG.logWarning( "Class: '" + access.getClazz()
                                + "' could not be created; use default file access class for fileType: "
                                + access.getFileType() );
            }
        }
    }

    /**
     * 
     * @param fileChooserType
     * @return the class name of the {@link FileSystemAccess} implementation to be used by the passed class. If no
     *         special class is registered name of {@link LocalFSAccess} will be returned as default
     */
    @SuppressWarnings("unchecked")
    public Class<FileSystemAccess> getFileSystemAccess( FILECHOOSERTYPE fileChooserType ) {
        Class<?> value = LocalFSAccess.class;
        if ( map.containsKey( fileChooserType ) ) {
            value = map.get( fileChooserType );
        }
        return (Class<FileSystemAccess>) value;
    }
    
    /**
     * 
     * @param fileChooserType
     * @param clzz
     */
    public void setFileSystemAccess(FILECHOOSERTYPE fileChooserType, Class<FileSystemAccess> clzz) {
        if ( changeable ) {
            
        }
    }

}
