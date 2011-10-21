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

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.config.HelpContextType;
import org.deegree.igeo.config.LayoutType;
import org.deegree.igeo.config.MenuType;
import org.deegree.igeo.config.ModuleType;
import org.deegree.igeo.config.PopUpEntryType;
import org.deegree.igeo.config.ToolbarEntryType;
import org.deegree.igeo.config._ComponentPositionType;
import org.deegree.igeo.views.ComponentPosition;
import org.deegree.igeo.views.ComponentState;
import org.deegree.igeo.views.swing.ControlElement;
import org.deegree.model.Identifier;

/**
 * 
 * The interface <code>IModule</code> is the upperclass of all concrete modules. The class extends
 * the interface ActionListener, so the assigned actions of a module can be handled in the
 * implementation of this interface.
 * 
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public interface IModule<T> extends ActionListener {

    /**
     * 
     * @param moduleType
     * @param componentPosition
     * @param appCont
     * @param parent
     * @param initParams
     */
    void init( ModuleType moduleType, _ComponentPositionType componentPosition, ApplicationContainer<T> appCont,
               IModule<T> parent, Map<String, String> initParams );
    
    /**
     * 
     * @return registered help context
     */
    public List<HelpContextType> getHelpContext();

    /**
     * clears status a module. as default behavior nothing happens
     * 
     */
    void clear();

    /**
     * creates the view assigend to a module
     * 
     */
    void createIView();

    /**
     * 
     * @return name of a Module
     */
    String getName();

    /**
     * The method update can be used to inform modules about changes in the mapModel.
     * 
     */
    void update();

    /**
     * creates the graphical representataion of the module; dependent on current layout and
     * componentState
     * 
     * @return
     */
    Object getViewForm();

    /**
     * 
     * @return layout of a module
     */
    LayoutType getLayout();

    /**
     * 
     * @return instance of the application container a module is contained within
     */
    ApplicationContainer<T> getApplicationContainer();

    /**
     * 
     * @return instance of a modules parent module; <code>null</code> if a module is directly
     *         contained within a application container
     */
    IModule<T> getParentModule();

    /**
     * sets a modules parent
     * 
     * @param module
     */
    void setParentModule( IModule<T> module );

    /**
     * 
     * @return unique identifier of a module
     */
    Identifier getIdentifier();
   
    /**
     * 
     * @return StateAdapter that stores the state of a modules view
     */
    ComponentState getComponentStateAdapter();

    /**
     * 
     * @return components position according to parent module viewform
     */
    ComponentPosition getComponentPositionAdapter();

    /**
     * 
     * @return list of all currently available actions; this will be a subset of all actions a
     *         module generaly would support
     */
    ArrayList<String> getAvailableActions();

    /**
     * 
     * @return map of all popup entries assigend to a module
     */
    List<PopUpEntryType> getPopUpEntries();

    /**
     * @return list of all toolbar entries assigned to a module
     */
    List<ToolbarEntryType> getToolBarEntries();

    /**
     * 
     * @return
     */
    List<MenuType> getMenus();

    /**
     * @return the controller of the toolbar assigned to this module
     */
    ControlElement getToolBarController();

    /**
     * 
     * @return all init parameters; the method ensures that the return value is not
     *         <code>null</code>
     */
    Map<String, String> getInitParameters();

    /**
     * 
     * @param name
     * @return named init parameter or <code>null</code> if not available
     */
    String getInitParameter( String name );

    /**
     * 
     * @param name
     * @param value
     */
    void setInitParameter( String name, String value );

    /**
     * @param name
     *            the name of the init parameter
     * @param newValue
     *            the new value of the init parameter
     */
    void updateInitParameter( String name, String newValue );

    /**
     * This method shall be called if a module will be removed from an application container. All
     * resources accessed from a module and/or bound to it shall be released; state adapters shall
     * be un-registered ...
     * 
     */
    void cleanUp();

    /**
     * 
     * @return true if a horizontal scroll bar should be used
     */
    boolean useHorizontalScrollbar();

    /**
     * 
     * @return true if a horizontal scroll bar should be used
     */
    boolean useVerticalScrollbar();
    
    /**
     * 
     * @param guiContainer
     */
    void setGUIContainer(T guiContainer);
    
    /**
     * 
     * @return
     */
    T getGUIContainer();
        
}