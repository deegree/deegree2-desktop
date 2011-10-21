//$HeadURL$
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

package org.deegree.igeo.views.swing.util;

import static javax.swing.JFileChooser.APPROVE_OPTION;
import static org.deegree.igeo.views.DialogFactory.openConfirmDialogYESNO;

import java.awt.Component;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.desktop.IGeoDesktop;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.io.FileSystemAccess;
import org.deegree.igeo.io.FileSystemAccessFactory;
import org.deegree.igeo.io.RemoteFSAccess;
import org.deegree.igeo.views.swing.IGeoFileChooser;

/**
 * <code>GenericFileChooser</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class GenericFileChooser {

    public static enum FILECHOOSERTYPE {
        project, module, geoDataFile, metadata, stylePattern, externalResource, printTemplate, printResult, image, local, remote
    }

    /**
     * @param defaultFile
     *            the default file to select, may be null
     * @param fileChooserType
     * @param appCont
     * @param parent
     * @param prefs
     * @param key
     * @param types
     * @return null, if none was chosen
     */
    public static File showSaveDialog( File defaultFile, FILECHOOSERTYPE fileChooserType,
                                       ApplicationContainer<?> appCont, Component parent, Preferences prefs,
                                       String key, IGeoFileFilter... types ) {
        FileSystemAccessFactory fsaf = FileSystemAccessFactory.getInstance( appCont );
        FileSystemAccess fsa;
        try {
            fsa = fsaf.getFileSystemAccess( fileChooserType );
        } catch ( Exception e ) {
            return null;
        }
        if ( fsa instanceof RemoteFSAccess ) {
            LinkedList<String> ext = getExtensions( types );
            IGeoFileChooser fch = new IGeoFileChooser( (IGeoDesktop) appCont, null, ext.get( 1 ), fileChooserType,
                                                       false );
            if ( fch.showOpenDialog() == JFileChooser.APPROVE_OPTION ) {
                return fch.getSelectedFile();
            }
        } else {
            JFileChooser chooser = new JFileChooser( prefs.get( key, null ) );
            if ( defaultFile != null ) {
                chooser.setSelectedFile( defaultFile );
            }
            for ( IGeoFileFilter f : types ) {
                chooser.addChoosableFileFilter( f );
            }
            return approveSave( parent, prefs, key, chooser );
        }

        return null;
    }

    /**
     * @param fileChooserType
     *            type of file to be saved
     * @param appCont
     * @param parent
     * @param prefs
     * @param key
     * @param types
     * @return null, if none was chosen
     */
    public static File showSaveDialog( FILECHOOSERTYPE fileChooserType, ApplicationContainer<?> appCont,
                                       Component parent, Preferences prefs, String key, IGeoFileFilter... types ) {
        return showSaveDialog( null, fileChooserType, appCont, parent, prefs, key, types );
    }

    /**
     * @param fileChooserType
     *            type of file to be saved
     * @param appCont
     * @param parent
     * @param prefs
     * @param key
     * @param types
     * @return null, if none was chosen
     */
    public static File showSaveDialog( FILECHOOSERTYPE fileChooserType, ApplicationContainer<?> appCont,
                                       Component parent, Preferences prefs, String key, List<IGeoFileFilter> types ) {
        FileSystemAccessFactory fsaf = FileSystemAccessFactory.getInstance( appCont );
        FileSystemAccess fsa;
        try {
            fsa = fsaf.getFileSystemAccess( fileChooserType );
        } catch ( Exception e ) {
            return null;
        }
        if ( fsa instanceof RemoteFSAccess ) {
            LinkedList<String> ext = getExtensions( types );
            IGeoFileChooser fch = new IGeoFileChooser( (IGeoDesktop) appCont, null, ext.get( 0 ), fileChooserType,
                                                       false );
            if ( fch.showOpenDialog() == JFileChooser.APPROVE_OPTION ) {
                return fch.getSelectedFile();
            }
        } else {
            JFileChooser chooser = new JFileChooser( prefs.get( key, null ) );
            for ( IGeoFileFilter f : types ) {
                chooser.addChoosableFileFilter( f );
            }
            return approveSave( parent, prefs, key, chooser );
        }

        return null;
    }

    /**
     * @param fileChooserType
     *            type of file to be loaded
     * @param parent
     * @param prefs
     * @param key
     * @param types
     * @return null, if none was chosen/cancel clicked
     */
    public static File showOpenDialog( FILECHOOSERTYPE fileChooserType, ApplicationContainer<?> appCont,
                                       Component parent, Preferences prefs, String key, IGeoFileFilter... types ) {
        FileSystemAccessFactory fsaf = FileSystemAccessFactory.getInstance( appCont );
        FileSystemAccess fsa;
        try {
            fsa = fsaf.getFileSystemAccess( fileChooserType );
        } catch ( Exception e ) {
            return null;
        }
        if ( fsa instanceof RemoteFSAccess ) {
            LinkedList<String> ext = getExtensions( types );
            IGeoFileChooser fch = new IGeoFileChooser( (IGeoDesktop) appCont, null, ext.get( 1 ), fileChooserType, true );
            if ( fch.showOpenDialog() == JFileChooser.APPROVE_OPTION ) {
                return fch.getSelectedFile();
            }
        } else {
            JFileChooser chooser = new JFileChooser( prefs.get( key, null ) );
            for ( IGeoFileFilter f : types ) {
                chooser.addChoosableFileFilter( f );
            }
            return approveOpen( parent, prefs, key, chooser );
        }

        return null;
    }

    /**
     * @param fileChooserType
     *            type of file to be loaded
     * @param parent
     * @param prefs
     * @param key
     * @param types
     * @return null, if none was chosen/cancel clicked
     */
    public static File showOpenDialog( FILECHOOSERTYPE fileChooserType, ApplicationContainer<?> appCont,
                                       Component parent, Preferences prefs, String key, List<IGeoFileFilter> types ) {
        FileSystemAccessFactory fsaf = FileSystemAccessFactory.getInstance( appCont );
        FileSystemAccess fsa;
        try {
            fsa = fsaf.getFileSystemAccess( fileChooserType );
        } catch ( Exception e ) {
            return null;
        }
        if ( fsa instanceof RemoteFSAccess ) {
            LinkedList<String> ext = getExtensions( types );
            IGeoFileChooser fch = new IGeoFileChooser( (IGeoDesktop) appCont, null, ext.get( 0 ), fileChooserType, true );
            if ( fch.showOpenDialog() == JFileChooser.APPROVE_OPTION ) {
                return fch.getSelectedFile();
            }
        } else {
            JFileChooser chooser = new JFileChooser( prefs.get( key, null ) );
            for ( IGeoFileFilter f : types ) {
                chooser.addChoosableFileFilter( f );
            }
            return approveOpen( parent, prefs, key, chooser );
        }

        return null;
    }

    private static File approveOpen( Component parent, Preferences prefs, String key, JFileChooser chooser ) {
        if ( chooser.showOpenDialog( parent ) == APPROVE_OPTION ) {
            prefs.put( key, chooser.getCurrentDirectory().getAbsoluteFile().toString() );
            File f = chooser.getSelectedFile();
            if ( chooser.getFileFilter() instanceof IGeoFileFilter ) {
                IGeoFileFilter filter = (IGeoFileFilter) chooser.getFileFilter();
                f = filter.updateFile( f );
            }
            if ( !f.exists() ) {
                return null;
            }
            return f;
        }
        return null;
    }

    private static File approveSave( Component parent, Preferences prefs, String key, JFileChooser chooser ) {
        if ( chooser.showSaveDialog( parent ) == APPROVE_OPTION ) {
            prefs.put( key, chooser.getCurrentDirectory().getAbsoluteFile().toString() );
            File f = chooser.getSelectedFile();
            if ( chooser.getFileFilter() instanceof IGeoFileFilter ) {
                IGeoFileFilter filter = (IGeoFileFilter) chooser.getFileFilter();
                f = filter.updateFile( f );
            }
            if ( f.exists()
                 && !openConfirmDialogYESNO( "Application", parent, Messages.get( "$DI10022", f.getName() ),
                                             Messages.get( "$DI10019" ) ) ) {
                return null;
            }

            return f;
        }
        return null;
    }

    private static LinkedList<String> getExtensions( IGeoFileFilter... types ) {
        LinkedList<String> ext = new LinkedList<String>();
        for ( IGeoFileFilter f : types ) {
            ext = f.getExtensions();
        }
        if ( ext.size() == 0 ) {
            ext.add( "#Any" );
            ext.add( "*.*" );
        }
        return ext;
    }

    private static LinkedList<String> getExtensions( List<IGeoFileFilter> types ) {
        LinkedList<String> ext = new LinkedList<String>();
        for ( IGeoFileFilter f : types ) {
            // TODO
            // ext = f.getExtensions();
        }
        if ( ext.size() == 0 ) {
            ext.add( "#Any" );
            ext.add( "*.*" );
        }
        return ext;
    }
}
