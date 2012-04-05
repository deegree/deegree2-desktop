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

package org.deegree.igeo;

import java.awt.Container;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.igeo.config.MenuBarType;
import org.deegree.igeo.config.ModuleGroupType;
import org.deegree.igeo.config.ModuleRegisterType;
import org.deegree.igeo.config.ModuleType;
import org.deegree.igeo.config.PopUpEntryType;
import org.deegree.igeo.config.Project;
import org.deegree.igeo.config.SettingsType;
import org.deegree.igeo.config.ToolbarEntryType;
import org.deegree.igeo.config.Util;
import org.deegree.igeo.config._ComponentPositionType;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.io.FileSystemAccess;
import org.deegree.igeo.io.FileSystemAccessFactory;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MapModelCollection;
import org.deegree.igeo.modules.DefaultMapModule;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.modules.IModuleGroup;
import org.deegree.igeo.modules.ModuleCreator;
import org.deegree.igeo.modules.ModuleException;
import org.deegree.igeo.settings.Settings;
import org.deegree.igeo.views.IFooter;
import org.deegree.igeo.views.swing.ButtonGroup;
import org.deegree.igeo.views.swing.ControlElement;
import org.deegree.igeo.views.swing.MenuBar;
import org.deegree.igeo.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;
import org.deegree.kernel.CommandProcessor;
import org.deegree.kernel.ProcessMonitor;
import org.deegree.kernel.ProcessMonitorFactory;
import org.deegree.model.Identifier;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public abstract class ApplicationContainer<T> {

    private static final ILogger LOG = LoggerFactory.getLogger( ApplicationContainer.class );

    public static final String ASSIGNEDMAPMODEL = "assignedMapModel";

    private ModuleCreator<T> moduleCreator;

    protected Project proj = null;

    protected List<IModule<T>> modules = Collections.synchronizedList( new ArrayList<IModule<T>>( 50 ) );

    protected List<ToolbarEntryType> toolbarEntries;

    protected List<PopUpEntryType> popupEntries;

    protected ProcessMonitor processMonitor;

    protected IFooter footer;

    protected boolean isNew;

    protected MapModelCollection mapModelCollection;

    protected URL projectURL;

    protected MapModel activeMapModel;

    protected CommandProcessor commandProcessor;

    protected Settings settings;

    protected Map<String, String> certificates = new HashMap<String, String>();

    protected String user = null;

    protected String password = "";

    protected Map<String, ButtonGroup> btGroups = new LinkedHashMap<String, ButtonGroup>( 10 );

    protected Map<String, Object> instanceSettings = new HashMap<String, Object>();

    protected JPanel toolbarPanel;

    protected MenuBar menuBar;

    protected JPopupMenu popup;

    protected ControlElement menuBarController;

    protected ControlElement toolBarController;

    protected Map<Identifier, Map<String, List<Object>>> actionMap = new LinkedHashMap<Identifier, Map<String, List<Object>>>();

    protected Map<String, List<AbstractButton>> toolbarButtons;

    protected Map<String, AbstractButton> menuItems;

    protected Container rootTargetPane = null;

    /**
     * 
     * @param processMonitor
     */
    protected ApplicationContainer( ProcessMonitor processMonitor ) {
        if ( processMonitor == null ) {
            processMonitor = ProcessMonitorFactory.createConsoleProcessMonitor( "", "", 0, 100, null );
        }
        this.processMonitor = processMonitor;
        commandProcessor = new CommandProcessor();
    }

    /**
     * 
     * @return {@link CommandProcessor} assigend to a project
     */
    public CommandProcessor getCommandProcessor() {
        return commandProcessor;
    }

    /**
     * 
     * @return all {@link ButtonGroup}s used for menus, toolbars and popups
     */
    public Map<String, ButtonGroup> getButtonGroups() {
        return btGroups;
    }

    /**
     * loads a project and initializes the implementing application container
     * 
     * @param url
     *            the url where the description of the project can be found
     * @param isNew
     *            must be true if the loaded project should be treated as a new project (not already been saved)
     * @throws JAXBException
     * @throws IOException
     * @throws URISyntaxException
     */
    public void loadProject( URL url, boolean isNew )
                            throws JAXBException, IOException, URISyntaxException {

        this.isNew = isNew;
        this.projectURL = url;
        // kill al existing modules and their GUI classes
        for ( IModule<?> module : modules ) {
            Object obj = module.getGUIContainer();
            if ( obj instanceof Window ) {
                ( (Window) obj ).dispose();
            } else if ( obj instanceof JInternalFrame ) {
                ( (JInternalFrame) obj ).dispose();
            }
            module.clear();
        }
        modules.clear();
        toolbarEntries = new ArrayList<ToolbarEntryType>();
        popupEntries = new ArrayList<PopUpEntryType>();

        processMonitor.updateStatus( "loading project file" );
        JAXBContext jc = JAXBContext.newInstance( "org.deegree.igeo.config" );
        Unmarshaller u = jc.createUnmarshaller();
        this.proj = (Project) u.unmarshal( url );

        // load admin settings if available
        String as = System.getProperty( "adminSettings" );
        SettingsType asst = null;
        if ( as != null ) {
            JAXBElement<?> ele = (JAXBElement<?>) u.unmarshal( new URL( as ) );
            asst = (SettingsType) ele.getValue();
        }

        // load user settings if available
        String us = System.getProperty( "userSettings" );
        SettingsType usst = null;
        if ( us != null ) {
            JAXBElement<?> ele = (JAXBElement<?>) u.unmarshal( new URL( us ) );
            usst = (SettingsType) ele.getValue();
        }

        settings = new Settings( this, asst, usst, proj.getSettings() );

        try {
            moduleCreator = new ModuleCreator<T>( this, url );
        } catch ( Exception e ) {
            // can never happen because project configuration has already been loaded before
            e.printStackTrace();
        }

        processMonitor.updateStatus( "creating menus ..." );

        String lang = this.proj.getLanguage();
        if ( lang == null ) {
            lang = Locale.getDefault().getLanguage();
        }
        Locale.setDefault( new Locale( lang ) );
        proj.setLanguage( lang );

        // add toolbar entries of the main view into map
        if ( proj.getView().getToolBar().size() > 0 ) {
            List<ToolbarEntryType> tbe = proj.getView().getToolBar().get( 0 ).getToolBarEntry();
            for ( ToolbarEntryType entry : tbe ) {
                addToolBarEntryToList( entry );
            }
        }

        if ( proj.getView().getPopUpMenu() != null ) {
            List<PopUpEntryType> pme = proj.getView().getPopUpMenu().getPopUpEntry();
            for ( PopUpEntryType entry : pme ) {
                popupEntries.add( entry );
            }
        }

        processMonitor.updateStatus( "loading map model" );

        mapModelCollection = moduleCreator.createMapModelCollection( this.proj.getMapModelCollection(), processMonitor );
        // set active/current map model. If no map model in configuration is marked as current
        // first available map model will used as active/current.
        this.activeMapModel = null;
        List<MapModel> list = mapModelCollection.getMapModels();
        for ( MapModel mapModel : list ) {
            if ( mapModel.isCurrent() ) {
                this.activeMapModel = mapModel;
                break;
            }
        }
        if ( this.activeMapModel == null ) {
            this.activeMapModel = mapModelCollection.getMapModels().get( 0 );
            this.activeMapModel.setCurrent( true );
        }

        // initialize module
        List<ModuleRegisterType> moduleRegister = this.proj.getView().getModuleRegister();

        for ( ModuleRegisterType mr : moduleRegister ) {

            JAXBElement<? extends ModuleType> moduleType = mr.getModule();
            _ComponentPositionType componentPosition = mr.get_ComponentPosition().getValue();
            IModule<T> module = null;
            if ( moduleType != null ) {
                ModuleType mt = moduleType.getValue();
                if ( mt instanceof ModuleGroupType ) {
                    ModuleGroupType mg = (ModuleGroupType) mt;
                    processMonitor.updateStatus( "loading module: " + mg.getName() );
                    module = moduleCreator.createModuleGroup( mg, componentPosition, null, processMonitor );
                } else {
                    processMonitor.updateStatus( "loading module: " + mt.getName() );
                    module = moduleCreator.createModule( mt, componentPosition, null, processMonitor );
                }
            } else {
                URL mLoc = null;
                String href = mr.getModuleReference().getOnlineResource().getHref();
                try {
                    FileSystemAccessFactory fsaf = FileSystemAccessFactory.getInstance( this );
                    FileSystemAccess fsa = fsaf.getFileSystemAccess( FILECHOOSERTYPE.module );
                    mLoc = fsa.getFileURL( href );
                } catch ( Exception e ) {
                    LOG.logError( e.getMessage(), e );
                    String msg = Messages.getMessage( Locale.getDefault(), "$DG10043", href );
                    throw new ModuleException( msg, e );
                }
                JAXBContext jc2 = JAXBContext.newInstance( "org.deegree.igeo.config" );
                Unmarshaller u2 = jc2.createUnmarshaller();
                LOG.logDebug( "loading: ", mLoc );
                try {
                    ModuleType mt = ( (JAXBElement<? extends ModuleType>) u2.unmarshal( mLoc ) ).getValue();
                    processMonitor.updateStatus( "loading module: " + mt.getName() );
                    module = moduleCreator.createModule( mt, componentPosition, null, processMonitor );
                } catch ( Exception e ) {
                    throw new IOException( "can not read module from: " + mLoc + " \n\nstacktrace: \n"
                                           + StringTools.stackTraceToString( e ) );
                }
            }
            this.modules.add( module );
        }

        assigneMapModel( this.modules );

    }

    /**
     * ensure that a map module (if not already assigned) is assigned to map model
     * 
     * @param modules
     */
    private void assigneMapModel( List<IModule<T>> modules ) {
        for ( IModule<T> module : modules ) {
            if ( module instanceof IModuleGroup<?> ) {
                List<IModule<T>> tmp = ( (IModuleGroup<T>) module ).getChildModules();
                assigneMapModel( tmp );
            } else if ( "MapModule".equals( module.getName() ) && module.getInitParameter( ASSIGNEDMAPMODEL ) == null ) {
                module.setInitParameter( ASSIGNEDMAPMODEL, activeMapModel.getIdentifier().getAsQualifiedString() );
            }
        }
    }

    /**
     * 
     * @return projects settings
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * 
     * @return projects menu bar definition
     */
    public MenuBarType getMenuBar() {
        return proj.getView().getMenuBar();
    }

    /**
     * 
     * @param url
     * @return
     * @throws MalformedURLException
     */
    public URL resolve( String url )
                            throws MalformedURLException {
        return moduleCreator.resolve( url );
    }

    /**
     * removes a module from a project </br> an implementing class may overrides this method to remove all according
     * menu bar, tool bar and pop up entries
     * 
     * @param module
     */
    public void removeModule( IModule<T> module ) {
        this.modules.remove( module );
        List<ModuleRegisterType> moduleRegister = this.proj.getView().getModuleRegister();
        deleteModuleRegister( moduleRegister, module.getIdentifier() );
    }

    private void deleteModuleRegister( List<ModuleRegisterType> moduleRegister, Identifier id ) {
        ModuleRegisterType tmp = null;
        for ( ModuleRegisterType mr : moduleRegister ) {
            JAXBElement<? extends ModuleType> moduleType = mr.getModule();
            if ( moduleType != null ) {
                ModuleType mt = moduleType.getValue();
                if ( mt instanceof ModuleGroupType ) {
                    ModuleGroupType mg = (ModuleGroupType) mt;
                    List<ModuleRegisterType> mrt = mg.getModuleRegister();
                    // recursion
                    deleteModuleRegister( mrt, id );
                } else {
                    if ( Util.convertIdentifier( mt.getIdentifier() ).equals( id ) ) {
                        tmp = mr;
                        break;
                    }
                }
            }
        }
        moduleRegister.remove( tmp );
    }

    /**
     * 
     * @param identifier
     * @return Module identified by passed {@link Identifier} or <code>null</code> if no matching module can be found
     */
    public IModule<T> findModuleByIdentifier( Identifier identifier ) {

        for ( int i = 0; i < this.modules.size(); i++ ) {
            if ( this.modules.get( i ).getIdentifier().equals( identifier ) ) {
                return this.modules.get( i );
            }
            if ( this.modules.get( i ) instanceof IModuleGroup<?> ) {
                IModule<T> mod = searchChildren( (IModuleGroup<T>) this.modules.get( i ), identifier );
                if ( mod != null ) {
                    return mod;
                }
            }
        }
        return null;
    }

    /**
     * 
     * @param moduleGroup
     * @param identifier
     * @return module matching passed identifier
     */
    private IModule<T> searchChildren( IModuleGroup<T> moduleGroup, Identifier identifier ) {
        List<IModule<T>> list = moduleGroup.getChildModules();
        for ( IModule<T> module : list ) {
            if ( module.getIdentifier().equals( identifier ) ) {
                return module;
            }
            if ( module instanceof IModuleGroup<?> ) {
                return searchChildren( (IModuleGroup<T>) module, identifier );
            }
        }
        return null;
    }

    /**
     * 
     * @param name
     * @return Module identified by passed name or <code>null</code> if no matching module can be found
     */
    public List<IModule<T>> findModuleByName( String name ) {

        List<IModule<T>> result = new ArrayList<IModule<T>>();

        for ( int i = 0; i < this.modules.size(); i++ ) {
            if ( this.modules.get( i ).getName().equals( name ) ) {
                result.add( this.modules.get( i ) );
            } else {
                if ( this.modules.get( i ) instanceof IModuleGroup<?> ) {
                    searchChildren( (IModuleGroup<T>) this.modules.get( i ), name, result );
                }
            }
        }
        return result;
    }

    /**
     * 
     * @param moduleGroup
     * @param name
     * @param result
     */
    private void searchChildren( IModuleGroup<T> moduleGroup, String name, List<IModule<T>> result ) {
        List<IModule<T>> list = moduleGroup.getChildModules();
        for ( IModule<T> module : list ) {
            if ( module.getName().equals( name ) ) {
                result.add( module );
            } else {
                if ( module instanceof IModuleGroup<?> ) {
                    searchChildren( (IModuleGroup<T>) module, name, result );
                }
            }
        }

    }

    /**
     * 
     * @return list of all registered modules
     */
    public List<IModule<T>> getModules() {
        return this.modules;
    }

    /**
     * Fetches and returns the active map module
     * 
     * @return the active map module
     */
    public DefaultMapModule<T> getActiveMapModule() {
        MapModel mm = getMapModel( null );
        List<IModule<T>> list = findModuleByName( "MapModule" );
        for ( IModule<T> module : list ) {
            String amm = module.getInitParameter( "assignedMapModel" );
            if ( mm.getIdentifier().getValue().equals( amm ) ) {
                return (DefaultMapModule<T>) module;
            }
        }
        if ( list.size() == 0 ) {
            return null;
        }
        return (DefaultMapModule<T>) list.get( 0 );
    }

    /**
     * adds a toolbar entry into list of defined entries
     * 
     * @param entry
     */
    public void addToolBarEntryToList( ToolbarEntryType entry ) {
        toolbarEntries.add( entry );
    }

    /**
     * 
     * @return defined popup entries
     */
    public List<PopUpEntryType> getPopupEntries() {
        return popupEntries;
    }

    /**
     * 
     * @return GUI independent footer
     */
    public IFooter getFooter() {
        return footer;
    }

    /**
     * 
     * @return assigned {@link MapModelCollection}
     */
    public MapModelCollection getMapModelCollection() {
        return mapModelCollection;
    }

    /**
     * 
     * @param id
     * @return map model matching passed id
     */
    public MapModel getMapModel( Identifier id ) {
        if ( id != null ) {
            List<MapModel> mapModels = mapModelCollection.getMapModels();
            for ( MapModel mapModel : mapModels ) {
                if ( mapModel.getIdentifier().equals( id ) ) {
                    return mapModel;
                }
            }
        }
        return activeMapModel;
    }

    /**
     * sets active {@link MapModel}
     * 
     * @param mapModel
     */
    public void setActiveMapModel( MapModel mapModel ) {
        List<MapModel> list = mapModelCollection.getMapModels();
        for ( MapModel mapModel2 : list ) {
            mapModel2.setCurrent( false );
        }
        if ( !this.activeMapModel.equals( mapModel ) ) {
            mapModel.setCurrent( true );
            // just fire action event if really something has changed
            this.activeMapModel = mapModel;
            ActionEvent e = new ActiveMapModelChanged( this, "ActiveMapModelChanged", mapModel );
            for ( IModule<T> module : modules ) {
                module.actionPerformed( e );
                if ( module instanceof IModuleGroup<?> ) {
                    invokeChildren( (IModuleGroup<T>) module, e );
                }
            }
            System.gc();
        }
    }

    /**
     * 
     * @param moduleGroup
     * @param e
     */
    private void invokeChildren( IModuleGroup<T> moduleGroup, ActionEvent e ) {
        List<IModule<T>> list = moduleGroup.getChildModules();
        for ( IModule<T> module : list ) {
            module.actionPerformed( e );
            if ( module instanceof IModuleGroup<?> ) {
                invokeChildren( (IModuleGroup<T>) module, e );
            }
        }
    }

    /**
     * 
     * @param url
     * @return certificate for service that is identified by its URL
     */
    public String getCertificate( String url ) {
        String certificate = certificates.get( url );
        if ( certificate == null ) {
            certificate = certificates.get( "default" );
        }
        return certificate;
    }

    /**
     * 
     * @return name of the current user
     */
    public String getUser() {
        return user;
    }

    /**
     * 
     * @return password of the current user
     */
    public String getPassword() {
        return password;
    }

    /**
     * 
     * @param name
     * @return parameter that is global available for complete time an instance of iGeoDesktop is running or it has been
     *         deleted manually
     */
    public Object getInstanceSetting( String name ) {
        synchronized ( instanceSettings ) {
            return instanceSettings.get( name );
        }
    }

    /**
     * sets a parameter that will be global available for complete time an instance of iGeoDesktop is running or it has
     * been deleted manually but it will not be stored
     * 
     * @param name
     * @param value
     */
    public synchronized void setInstanceSetting( String name, Object value ) {
        synchronized ( instanceSettings ) {
            instanceSettings.put( name, value );
        }
    }

    /**
     * 
     * @return project language
     */
    public String getLanguage() {
        return proj.getLanguage();
    }

    /**
     * 
     * @return url/location of the loaded project file
     */
    public String getProjectURL() {
        try {
            return projectURL.toURI().toASCIIString();
        } catch ( URISyntaxException e ) {
            e.printStackTrace();
        }
        return projectURL.toExternalForm();
    }

    /**
     * 
     * @param url
     */
    public void setProjectURL( URL url ) {
        this.projectURL = url;
    }

    /**
     * 
     * @param language
     *            project language
     */
    public void setLanguage( String language ) {
        proj.setLanguage( language );
    }

    /**
     * @return the proj
     */
    public Project getProject() {
        return proj;
    }

    /**
     * @return the processMonitor
     */
    public ProcessMonitor getProcessMonitor() {
        return processMonitor;
    }

    /**
     * logs out current instance of igeodesktop by reseting all certificates
     */
    public void logout() {
        user = null;
        password = null;
        certificates.clear();
    }

    /**
     * @return the isNew
     */
    public boolean isNew() {
        return isNew;
    }

    /**
     * @param isNew
     *            the isNew to set
     */
    public void setNew( boolean isNew ) {
        this.isNew = isNew;
    }

    /**
     * @param processMonitor
     *            the processMonitor to set
     */
    public void setProcessMonitor( ProcessMonitor processMonitor ) {
        this.processMonitor = processMonitor;
    }

    /**
     * 
     * @return pane to which GUI container of all root level modules will be added
     */
    public Container getRootTargetPane() {
        return rootTargetPane;
    }

    /**
     * The key of the map contains the name of the listener class separated by an '|' from the name of the menu item:
     * <p>
     * <code>listener.getClass().getName() + "|" + mt.getName()</code>
     * </p>
     * 
     * @return all known menu items
     */
    public Map<String, AbstractButton> getMenuItems() {
        return menuItems;
    }

    /**
     * initializes iGeoDesktop
     */
    public abstract void init();

    /**
     * This method is defined abstract because it depends on concrete implementations how to access an applications
     * locale. E.g. a swing based desktop application may simply returns <code>Locale.getDefault()</code> while a web
     * based application evaluates the locale of the incomming requests.
     * 
     * @return local to be used with a application container.
     */
    public abstract Locale getLocale();

    /**
     * 
     * @param module
     *            identifier of module an action is assigned to
     * @param action
     *            name of the action
     * @param select
     */
    public abstract void selectComponentForAction( Identifier module, String action, boolean select );

    /**
     * registers a {@link KeyStroke} for an Action to be assigned to the root window
     * 
     * @param actionListener
     * @param keyStroke
     */
    public abstract void registerKeyboardAction( ActionListener actionListener, KeyStroke keyStroke );

    /**
     * 
     */
    public abstract void resetToolbar();

    /**
     * @return the platform of the application
     */
    public abstract String getViewPlatform();

    /**
     * frees all resources allocated by an instance of ApplicationContainer
     */
    public abstract void cleanUp();

    /**
     * 
     * @return main window of the application
     */
    public abstract Container getMainWndow();

    /**
     * performs a login
     * 
     * @param user
     * @param password
     */
    public abstract void login( String user, String password );

    /**
     * renders the application
     */
    public abstract void paint();

    /**
     * adapts the toolbar to current container size
     */
    public abstract void resizeToolbar();

    /**
     * add entries (buttons) to the toolbar assigned to one specific module and its children
     * 
     * @param module
     */
    public abstract void addToolBarEntries( IModule<Container> module );

    /**
     * appends Modules onto the passed contains
     * 
     * @param targetPane
     */
    public abstract void appendModules( List<IModule<Container>> modules, Container targetPane );

}
