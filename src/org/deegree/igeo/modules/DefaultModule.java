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
package org.deegree.igeo.modules;

import java.awt.Component;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.config.ComponentStateType;
import org.deegree.igeo.config.HelpContextType;
import org.deegree.igeo.config.LayoutType;
import org.deegree.igeo.config.MenuType;
import org.deegree.igeo.config.ModuleType;
import org.deegree.igeo.config.PopUpEntryType;
import org.deegree.igeo.config.ToolbarEntryType;
import org.deegree.igeo.config.Util;
import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.config._AbstractViewFormType;
import org.deegree.igeo.config._ComponentPositionType;
import org.deegree.igeo.config._AbstractViewFormType.ContainerClass;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.ComponentPosition;
import org.deegree.igeo.mapmodel.ComponentState;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.IView;
import org.deegree.igeo.views.swing.ControlElement;
import org.deegree.model.Identifier;

/**
 * 
 * The <code>DefaultModule</code> is the representation of a module configured in the configurationfile of the project
 * without special requirements (for example availableActions).
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class DefaultModule<T> implements IModule<T> {

    private static final ILogger LOG = LoggerFactory.getLogger( DefaultModule.class );

    protected ApplicationContainer<T> appContainer;

    protected ComponentState componentStateAdapter;

    protected ComponentPosition componentPositionAdapter;

    protected Identifier identifier;

    protected IModule<T> parentModule;

    protected IView<T> view;

    protected ArrayList<String> availableActions = new ArrayList<String>();

    protected Map<String, String> initParams = new HashMap<String, String>();

    protected LayoutType layout;

    protected ControlElement toolBarController;

    protected ModuleType moduleType;

    protected T guiContainer;

    protected static ModuleCapabilities moduleCapabilities;

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.modules.IModule#init(org.deegree.igeo.config.ModuleType,
     * org.deegree.igeo.config._ComponentPositionType, org.deegree.igeo.AbstractApplicationContainer,
     * org.deegree.igeo.modules.IModule, java.util.Map)
     */
    public void init( ModuleType moduleType, _ComponentPositionType componentPosition, ApplicationContainer<T> appCont,
                      IModule<T> parent, Map<String, String> initParams ) {
        this.moduleType = moduleType;
        this.identifier = Util.convertIdentifier( moduleType.getIdentifier() );
        this.appContainer = appCont;

        ComponentStateType componentState = moduleType.getViewForm().get_AbstractViewForm().getValue().getComponentState();

        this.componentStateAdapter = new ComponentState( componentState );

        this.componentPositionAdapter = new ComponentPosition( componentPosition );

        this.layout = moduleType.getViewForm().getLayout();

        this.toolBarController = new ControlElement( "ToolBar" );

        setParentModule( parent );
        this.initParams = initParams;

        createIView();

    }

    /**
     * 
     * @return capabilities of a module
     */
    public static ModuleCapabilities getModuleCapabilities() {
        return moduleCapabilities;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.modules.IModule#setGUIContainer(java.lang.Object)
     */
    public void setGUIContainer( T guiContainer ) {
        this.guiContainer = guiContainer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.modules.IModule#getGUIContainer()
     */
    public T getGUIContainer() {
        return guiContainer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.modules.IModule#getHelpContext()
     */
    public List<HelpContextType> getHelpContext() {
        return moduleType.getHelpContext();
    }

    /**
     * clears status a module. as default behavior nothing happens
     * 
     */
    public void clear() {
        if ( view instanceof Component && ( (Component) view ).isVisible() ) {
            if ( view instanceof JDialog ) {
                ( (JDialog) view ).dispose();
            } else if ( view instanceof Window ) {
                ( (Window) view ).dispose();
            } else if ( view instanceof JInternalFrame ) {
                ( (JInternalFrame) view ).dispose();
            }
        }
        this.componentStateAdapter.setClosed( true );
        this.view = null;
    }

    /**
     * creates the view assigned to a module
     * 
     */
    @SuppressWarnings("unchecked")
    public void createIView() {
        String className = null;
        try {
            if ( !this.componentStateAdapter.isClosed() && this.view == null ) {               
                String viewPlatform = this.appContainer.getViewPlatform();
                className = getViewClassName( moduleType.getViewForm(), viewPlatform ).trim();
                Class<?> c = Class.forName( className );
                this.view = (IView<T>) c.newInstance();
                this.view.registerModule( this );
                this.view.init( moduleType.getViewForm() );
                LOG.logDebug( "creating view for: ", moduleType.getName() );
            }
        } catch ( Exception e ) {
            this.view = null;
            this.componentStateAdapter.setClosed( true ); 
            LOG.logError( e.getMessage(), e );
            throw new RuntimeException( e );
        }

    }

    /**
     * 
     * @param viewForm
     * @param viewPlatform
     * @return class name for ViewForm and named viewPlatform assigned to a module
     */
    private String getViewClassName( ViewFormType viewForm, String viewPlatform ) {
        _AbstractViewFormType avft = viewForm.get_AbstractViewForm().getValue();
        List<ContainerClass> cc = avft.getContainerClass();
        for ( ContainerClass element : cc ) {
            if ( element.getViewPlatform().equals( viewPlatform ) ) {
                return element.getValue();
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.IModule#cleanUp()
     */
    public void cleanUp() {
        // TODO
        // remove menu bar, pop up and toolbar entries
        this.appContainer = null;

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.modules.IModule#getName()
     */
    public String getName() {
        return moduleType.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.modules.IModule#getViewForm()
     */
    public Object getViewForm() {
        return this.view;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.modules.IModule#getLayout()
     */
    public LayoutType getLayout() {
        return layout;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.modules.IModule#update()
     */
    public void update() {
        if ( this.view != null && !this.componentStateAdapter.isClosed() ) {
            this.view.update();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.IModule#getApplicationStateContainer()
     */
    public ApplicationContainer<T> getApplicationContainer() {
        return this.appContainer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.IModule#getAvailableActions()
     */
    public ArrayList<String> getAvailableActions() {
        return this.availableActions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.IModule#getComponentStateAdapter()
     */
    public ComponentState getComponentStateAdapter() {
        return this.componentStateAdapter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.modules.IModule#getComponentPosition()
     */
    public ComponentPosition getComponentPositionAdapter() {
        return componentPositionAdapter;
    }

   
    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.IModule#getIdentifier()
     */
    public Identifier getIdentifier() {
        return this.identifier;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.IModule#getParentModule()
     */
    public IModule<T> getParentModule() {
        return this.parentModule;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.modules.IModule#getInitParameter(java.lang.String)
     */
    public String getInitParameter( String name ) {
        return initParams.get( name );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.modules.IModule#setInitParameter(java.lang.String, java.lang.String)
     */
    public void setInitParameter( String name, String value ) {
        initParams.put( name, value );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.modules.IModule#getInitParameters()
     */
    public Map<String, String> getInitParameters() {
        return initParams;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.modules.IModule#updateInitParameter(java.lang.String, java.lang.String)
     */
    public void updateInitParameter( String name, String newValue ) {
        if ( initParams.containsKey( name ) ) {
            initParams.put( name, newValue );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.IModule#setParentModule( IModule module )
     */
    public void setParentModule( IModule<T> module ) {
        this.parentModule = module;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.modules.IModule#getToolbarEntries()
     */
    public List<PopUpEntryType> getPopUpEntries() {
        return moduleType.getPopUpEntry();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.modules.IModule#getToolBarEntries()
     */
    public List<ToolbarEntryType> getToolBarEntries() {
        return moduleType.getToolBarEntry();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.modules.IModule#getMenus()
     */
    public List<MenuType> getMenus() {
        return moduleType.getMenu();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.modules.IModule#getToolBarController()
     */
    public ControlElement getToolBarController() {
        return this.toolBarController;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.modules.IModule#useHorizontalScrollbar()
     */
    public boolean useHorizontalScrollbar() {
        return moduleType.getViewForm().get_AbstractViewForm().getValue().isUseHorizontalScrollBar();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.modules.IModule#useVerticalScrollbar()
     */
    public boolean useVerticalScrollbar() {
        return moduleType.getViewForm().get_AbstractViewForm().getValue().isUseVerticalScrollBar();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed( ActionEvent e ) {

        // get name (equivalent to the assigned action) of the source
        String actionName = null;
        if ( e.getSource() instanceof JMenuItem ) {
            JMenuItem currentEntry = (JMenuItem) e.getSource();
            actionName = currentEntry.getName();
        } else if ( e.getSource() instanceof AbstractButton ) {
            AbstractButton button = (AbstractButton) e.getSource();
            actionName = button.getName();
        } else if ( e.getSource() instanceof JPopupMenu ) {
            JPopupMenu popupEntry = (JPopupMenu) e.getSource();
            actionName = popupEntry.getName();
        }

        // call method with this name
        if ( actionName != null && actionName.length() > 0 ) {
            try {
                if ( actionName.startsWith( "$script:" ) ) {
                    // TODO
                } else if ( actionName.startsWith( "$module:" ) ) {
                    // if a event name starts with '$module:' the event name contains the name
                    // of a registered module (or ApplicationContainer) and a method that shall be
                    // invoked. E.g.: $module:MyModule#test or $module:ApplicationContainer#test
                    invokeModuleEventHandler( actionName );
                } else if ( actionName.startsWith( "$class:" ) ) {
                    // if a event name starts with '$class:' the event name contains name of class
                    // and a static method that shall be invoked. E.g.:
                    // $class:de.lat-lon.igeodesktop.example.EventHandler#test
                    invokeStaticEventHandler( actionName );
                } else {
                    LOG.logInfo( "invoked class: ", this.getClass().getName() );
                    LOG.logInfo( "invoked method: ", actionName );
                    Method action = this.getClass().getMethod( actionName, (Class[]) null );
                    // synchronize GUI components
                    appContainer.selectComponentForAction( this.getIdentifier(), actionName, true );
                    // invoke action method
                    action.invoke( this, (Object[]) null );
                }
            } catch ( SecurityException e1 ) {
                LOG.logError( e1.getMessage(), e1 );
            } catch ( NoSuchMethodException e1 ) {
                LOG.logError( e1.getMessage(), e1 );
            } catch ( IllegalArgumentException e1 ) {
                LOG.logError( e1.getMessage(), e1 );
            } catch ( IllegalAccessException e1 ) {
                LOG.logError( e1.getMessage(), e1 );
            } catch ( InvocationTargetException e1 ) {
                LOG.logError( e1.getMessage(), e1 );
            }
        }
        if ( view != null ) {
            view.update();
        }
    }

    /**
     * @param actionName
     */
    private void invokeModuleEventHandler( String actionName ) {
        try {
            String[] tmp = StringTools.toArray( actionName, ":#", false );
            if ( "ApplicationContainer".equals( tmp[1] ) ) {
                Class<?> clzz = appContainer.getClass();
                Method method = clzz.getDeclaredMethod( tmp[2], (Class[]) null );
                method.invoke( appContainer, (Object[]) null );
            } else {
                List<IModule<T>> modules = appContainer.findModuleByName( tmp[1] );
                for ( IModule<T> module : modules ) {
                    Class<?> clzz = module.getClass();
                    Method method = clzz.getDeclaredMethod( tmp[2], (Class[]) null );
                    method.invoke( module, (Object[]) null );
                }
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null,
                                             Messages.getMessage( Locale.getDefault(), "$DI10013", actionName ),
                                             Messages.getMessage( Locale.getDefault(), "$DI10014" ) );
        }

    }

    /**
     * if a event name starts with '$class:' the event name contains name of class and a static method that shall be
     * invoked. E.g.: $class:de.lat-lon.igeodesktop.example.EventHandler#test
     * 
     * @param action
     *            action to be performed
     */
    private void invokeStaticEventHandler( String action ) {
        try {
            String[] tmp = StringTools.toArray( action, ":#", false );
            Class<?> clzz = Class.forName( tmp[1] );
            Class<?>[] varTypes = new Class[] { ApplicationContainer.class };
            Method method = clzz.getDeclaredMethod( tmp[2], varTypes );
            Object[] var = new Object[] { this };
            method.invoke( null, var );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null,
                                             Messages.getMessage( Locale.getDefault(), "$DI10013", action ),
                                             Messages.getMessage( Locale.getDefault(), "$DI10014" ) );
        }
    }

}
