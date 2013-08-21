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

import java.awt.Container;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.filechooser.FileFilter;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.settings.FileFilters;
import org.deegree.framework.util.StringTools;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class DesktopFileFilter extends FileFilter {
    
    /**
     * 
     */
    public static final DesktopFileFilter SHP = new DesktopFileFilter( Messages.get( "$MD10356" ), "shp" );

    /**
     * 
     */
    public static final DesktopFileFilter PDF = new DesktopFileFilter( Messages.get( "$MD10356" ), "pdf" );

    /**
     * 
     */
    public static final DesktopFileFilter HTML = new DesktopFileFilter( Messages.get( "$MD10370" ), "htm", "html" );

    /**
     * 
     */
    public static final DesktopFileFilter XML = new DesktopFileFilter( Messages.get( "$MD10371" ), "xml" );

    /**
     * 
     */
    public static final DesktopFileFilter JASPER = new DesktopFileFilter( Messages.get( "$MD10371" ), "xml", "jrxml" );

    /**
     * 
     */
    public static final DesktopFileFilter JPEG = new DesktopFileFilter( Messages.get( "$MD10372" ), "jpg", "jpeg" );

    /**
     * 
     */
    public static final DesktopFileFilter PNG = new DesktopFileFilter( Messages.get( "$MD10373" ), "png" );

    /**
     * 
     */
    public static final DesktopFileFilter GPX = new DesktopFileFilter( Messages.get( "$MD10997" ), "gpx" );

    /**
     * 
     */
    public static final DesktopFileFilter TIFF = new DesktopFileFilter( Messages.get( "$MD10378" ), "tif", "tiff" );

    /**
     * 
     */
    public static final DesktopFileFilter GIF = new DesktopFileFilter( Messages.get( "$MD10379" ), "gif" );

    /**
     * 
     */
    public static final DesktopFileFilter BMP = new DesktopFileFilter( Messages.get( "$MD10380" ), "bmp" );

    /**
     * 
     */
    public static final DesktopFileFilter IMAGES = new DesktopFileFilter( Messages.get( "$MD10381" ), "jpg", "jpeg", "png",
                                                                    "tif", "tiff", "gif", "bmp", "svg" );

    /**
     * 
     */
    public static final DesktopFileFilter PRJ = new DesktopFileFilter( Messages.get( "$MD10907" ), "prj" );

    private LinkedList<String> extensions = null;

    private String description;

    /**
     * <code>FILETYPE</code>
     * 
     * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    public enum FILETYPE {
        /**
         * 
         */
        vector,
        /**
         * 
         */
        raster,
        /**
         * 
         */
        any
    }

    /**
     * @param appContainer
     * @param type
     * @return list of {@link FileFilter}s for file formats known by iGeoDesktop
     */
    public static List<DesktopFileFilter> createForwellKnownFormats( ApplicationContainer<Container> appContainer,
                                                                  FILETYPE type ) {
        FileFilters ff = appContainer.getSettings().getFileFilters();
        List<String> ext = new ArrayList<String>();
        List<String> extensions = ff.getFileExtensions();
        if ( type == FILETYPE.any ) {
            for ( String val : extensions ) {
                ext.add( val + ';' + ff.getDescription( val ) );
            }
        } else if ( type == FILETYPE.vector ) {
            for ( String val : extensions ) {
                if ( ff.isVectorFormat( val ) ) {
                    ext.add( val + ';' + ff.getDescription( val ) );
                }
            }
        } else if ( type == FILETYPE.raster ) {
            for ( String val : extensions ) {
                if ( !ff.isVectorFormat( val ) ) {
                    ext.add( val + ';' + ff.getDescription( val ) );
                }
            }
        }
        return DesktopFileFilter.createForExtensions( ext.toArray( new String[ext.size()] ) );
    }

    /**
     * 
     * @param extensions
     * @return list of {@link FileFilter}s; one for each extension
     */
    public static List<DesktopFileFilter> createForExtensions( String... extensions ) {

        List<DesktopFileFilter> list = new ArrayList<DesktopFileFilter>( extensions.length );
        for ( String extension : extensions ) {
            if ( extension.trim().length() > 4 ) {
                String[] s = extension.split( ";" );
                DesktopFileFilter cff = new DesktopFileFilter( s[0] );
                if ( s.length > 1 ) {
                    cff.setDescription( StringTools.concat( 50, "*.", s[0].toLowerCase(), " - ", s[1] ) );
                }
                list.add( cff );
            }
        }
        return list;
    }

    /**
     * 
     * @param extensions
     *            list of considered file extensions (e.g. tif, BMP, Gif ..) The is is not case sensitive; use '*' for
     *            returning all files
     */
    public DesktopFileFilter( String... extensions ) {
        this.extensions = new LinkedList<String>();
        for ( int i = 0; i < extensions.length; i++ ) {
            this.extensions.add( extensions[i].toUpperCase() );
        }
    }

    /**
     * @return the extensions
     */
    public LinkedList<String> getExtensions() {
        return extensions;
    }

    /**
     * sets a {@link FileFilter}s description text
     * 
     * @param description
     */
    public void setDescription( String description ) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        if ( description != null ) {
            return description;
        }
        StringBuffer sb = new StringBuffer( 100 );
        for ( int i = 0; i < extensions.size(); i++ ) {
            sb.append( "*." ).append( extensions.get( i ).toLowerCase() );
            if ( i < extensions.size() - 1 ) {
                sb.append( "; " );
            }
        }
        return sb.toString();
    }

    /**
     * @param f
     * @return a file with extension
     */
    public File updateFile( File f ) {
        String n = f.getName();
        if ( n.indexOf( '.' ) == -1 ) {
            return new File( f.toString() + "." + extensions.getLast().toLowerCase() );
        }

        return f;
    }

    @Override
    public boolean accept( java.io.File file ) {
        if ( file.isDirectory() ) {
            return true;
        }
        String name = file.getName();
        int pos = name.lastIndexOf( "." );
        String ext = name.substring( pos + 1 ).toUpperCase();
        String s = file.getAbsolutePath() + '/' + name;
        File tmp = new File( s );

        return extensions.contains( ext ) || ( extensions.contains( "*" ) && !tmp.isDirectory() );
    }

}
