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
package org.deegree.desktop.modules.bookmarks;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.main.DeegreeDesktop;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.modules.ActionDescription;
import org.deegree.desktop.modules.DefaultModule;
import org.deegree.desktop.modules.IModule;
import org.deegree.desktop.modules.ModuleCapabilities;
import org.deegree.desktop.modules.ActionDescription.ACTIONTYPE;
import org.deegree.desktop.views.swing.bookmark.NewBookmarkDialog;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.desktop.config.ModuleType;
import org.deegree.desktop.config._ComponentPositionType;
import org.deegree.model.Identifier;
import org.deegree.model.spatialschema.Envelope;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class BookmarkModule<T> extends DefaultModule<T> {

    private static final ILogger LOG = LoggerFactory.getLogger( BookmarkModule.class );

    public static final String BOOKMARK = BookmarkModule.class.getName();

    static {
        ActionDescription ad1 = new ActionDescription( "open", "opens a dialog for selecting a bookmark", null,
                                                       "open bookmark dialog", ACTIONTYPE.PushButton, null, null );
        ActionDescription ad2 = new ActionDescription( "open", "opens a dialog for adding a new bookmark", null,
                                                       "open bookmark dialog", ACTIONTYPE.PushButton, null, null );
        moduleCapabilities = new ModuleCapabilities( ad1, ad2 );
    }

    /**
     * action method for adding a book mark
     */
    public void addBookmark() {
        MapModel mm = appContainer.getMapModel( null );
        String name = mm.getName();
        String desc = "-";
        boolean allMapModels = false;
        if ( "application".equalsIgnoreCase( appContainer.getViewPlatform() ) ) {
            NewBookmarkDialog dlg = new NewBookmarkDialog( ( (DeegreeDesktop) appContainer ).getMainWndow() );
            if ( dlg.isOK() ) {
                name = dlg.getBookmarkName();
                desc = dlg.getDescription();
                allMapModels = dlg.isAllMapModels();
            } else {
                return;
            }
        }
        BookmarkEntry bme = new BookmarkEntry( mm.getIdentifier(), name, desc, mm.getEnvelope().getBuffer( 0 ),
                                               allMapModels );
        List<BookmarkEntry> bookmarks = readFromCache();
        bookmarks.add( bme );
        writeToCache( bookmarks );
    }

    /**
     * 
     * @param bookmarks
     */
    public void writeToCache( List<BookmarkEntry> bookmarks ) {
        try {
            String file = System.getProperty( "user.home" ) + System.getProperty( "file.separator" )
                          + "bookmarks.igeo.xml";
            File f = new File( file );
            Util.saveBookmarks( bookmarks, f );
        } catch ( Exception e ) {
            LOG.logError( e );
        }
    }

    /**
     * 
     * @param bookmarks
     * @return
     */
    public List<BookmarkEntry> readFromCache() {
        String file = System.getProperty( "user.home" ) + System.getProperty( "file.separator" ) + "bookmarks.igeo.xml";
        File f = new File( file );
        List<BookmarkEntry> bookmarks = null;
        try {
            bookmarks = Util.loadBookmarks( f );
        } catch ( Exception e ) {
            LOG.logError( e );
        }
        return bookmarks;
    }

    @Override
    public void init( ModuleType moduleType, _ComponentPositionType componentPosition, ApplicationContainer<T> appCont,
                      IModule<T> parent, Map<String, String> initParams ) {
        super.init( moduleType, componentPosition, appCont, parent, initParams );
    }

    /**
     * action method for opening the book mark dialog
     */
    public void open() {
        this.componentStateAdapter.setClosed( false );
        createIView();
    }

    public void closed() {
        view = null;
    }

    /**
     * 
     * TODO add class documentation here
     * 
     * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    public static class BookmarkEntry implements Serializable {

        private static final long serialVersionUID = 545158831968146547L;

        public Envelope env;

        public String name;

        public boolean allMapModels;

        public String description;

        public Identifier mapModel;

        /**
         * @param mapModel
         * @param name
         * @param description
         * @param env
         * @param allMapModels
         */
        public BookmarkEntry( Identifier mapModel, String name, String description, Envelope env, boolean allMapModels ) {
            this.mapModel = mapModel;
            this.name = name;
            this.description = description;
            this.env = env;
            this.allMapModels = allMapModels;
        }

    }
}
