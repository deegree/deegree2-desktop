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

package org.deegree.igeo.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.config.HelpContextType;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.modules.IModuleGroup;
import org.deegree.igeo.settings.HelpPage;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class HelpManager {

    private static final ILogger LOG = LoggerFactory.getLogger( HelpManager.class );

    protected Map<String, String> pageRefByKW = new HashMap<String, String>();

    protected Map<String, String> pageRefByModule = new HashMap<String, String>();

    protected Map<String, String> pageRefByModuleClass = new HashMap<String, String>();

    protected Map<String, String> pageRefByProj = new HashMap<String, String>();

    private ApplicationContainer<?> appContainer;

    private boolean inited = false;

    /**
     * 
     * @param modules
     *            list of modules to manage their associated help references ({@link HelpContextType})
     * @throws IOException
     */
    public HelpManager( ApplicationContainer<?> appContainer ) {
        this.appContainer = appContainer;
    }

    public void init() {
        if ( !inited ) {
            // create/fill maps for project help files
            List<HelpPage> helpPages = appContainer.getSettings().getHelp();
            for ( HelpPage page : helpPages ) {
                List<String> keywords = page.getKeyword();
                if ( page.getLanguage() == null || appContainer.getLocale().getLanguage().equals( page.getLanguage() ) ) {
                    for ( String keyword : keywords ) {
                        pageRefByKW.put( keyword + ":iGeoDesktop", page.getOnlineResource() );
                    }
                    if ( page.isMainPage() ) {
                        pageRefByProj.put( "iGeoDesktop", page.getOnlineResource() );
                    }
                }
            }

            // create/fill maps for module specific help files
            for ( IModule<?> module : appContainer.getModules() ) {
                appendModule( module );
            }
            inited = true;
        }
    }

    private void appendModule( IModule<?> module ) {
        if ( module instanceof IModuleGroup<?> ) {
            for ( IModule<?> mod : ( (IModuleGroup<?>) module ).getChildModules() ) {
                appendModule( mod );
            }
        } else {
            addModule( module );
        }
    }

    /**
     * adds a map of keywords and assigend HTML files for a module
     * 
     * @param module
     * @throws IOException
     */
    public void addModule( IModule<?> module ) {
        LOG.logDebug( "add help pages for module: ", module.getName() );
        List<HelpContextType> helpContext = module.getHelpContext();
        for ( HelpContextType type : helpContext ) {
            List<String> keywords = type.getPage().getKeyword();
            if ( type.getPage().getLanguage() == null
                 || appContainer.getLocale().getLanguage().equals( type.getPage().getLanguage() ) ) {
                for ( String keyword : keywords ) {
                    pageRefByKW.put( keyword + ':' + module.getName(), type.getPage().getOnlineResource().getHref() );
                }
                if ( type.getPage().isMainPage() ) {
                    pageRefByModule.put( module.getName(), type.getPage().getOnlineResource().getHref() );
                    pageRefByModuleClass.put( module.getClass().getName(), type.getPage().getOnlineResource().getHref() );
                }
            }
        }
    }

    /**
     * removes all keywords assigend to a module from help
     * 
     * @param module
     */
    public void removeModule( IModule<?> module ) {
        String name = module.getName();
        Iterator<String> iter = pageRefByKW.keySet().iterator();
        List<String> tmp = new ArrayList<String>( 50 );
        while ( iter.hasNext() ) {
            String key = iter.next();
            if ( key.endsWith( ':' + name ) ) {
                tmp.add( key );
            }
        }
        for ( String key : tmp ) {
            pageRefByKW.remove( key );
        }
        pageRefByModule.remove( module.getName() );
        pageRefByModuleClass.remove( module.getClass().getName() );
    }

    /**
     * 
     * @return Set of all available keywords
     */
    public Set<String> getKeywords() {
        return pageRefByKW.keySet();
    }

    /**
     * 
     * @return Set of all available modules
     */
    public Set<String> getModuleNames() {
        return pageRefByModule.keySet();
    }

    /**
     * 
     * @param keyword
     * @return URL to help HTML file for a keyword
     */
    public String getReferenceForKeyword( String keyword ) {
        return pageRefByKW.get( keyword );
    }

    /**
     * 
     * @param moduleName module name or module class name
     * @return URL to help HTML file for a module name
     */
    public String getReferenceForModule( String moduleName ) {
        String s = pageRefByModule.get( moduleName );
        if ( s == null ) {
            pageRefByModuleClass.get( moduleName );
        }
        return s;
    }

    /**
     * 
     * @param moduleName
     * @return keywords assigned to a module
     */
    public List<String> getKeywordsByModule( String moduleName ) {
        Iterator<String> iter = pageRefByKW.keySet().iterator();
        List<String> kw = new ArrayList<String>();
        while ( iter.hasNext() ) {
            String key = iter.next();
            if ( key.endsWith( ':' + moduleName ) ) {
                kw.add( key );
            }
        }
        return kw;
    }

    /**
     * 
     * @return keywords directly assigned to a project
     */
    public List<String> getProjectKeyWords() {
        Iterator<String> iter = pageRefByProj.keySet().iterator();
        List<String> kw = new ArrayList<String>();
        while ( iter.hasNext() ) {
            kw.add( iter.next() );
        }
        return kw;
    }

    /**
     * 
     * @return reference to default help page of a project or <code>null</code> if not defined
     */
    public String getProjectDefaultHelpPage() {
        List<HelpPage> helpPages = appContainer.getSettings().getHelp();
        for ( HelpPage page : helpPages ) {
            if ( page.isMainPage() ) {
                return page.getOnlineResource();
            }
        }
        return null;
    }

    /**
     * 
     * @return assigned {@link ApplicationContainer}
     */
    public ApplicationContainer<?> getApplicationContainer() {
        return appContainer;
    }

}
