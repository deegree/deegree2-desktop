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

package org.deegree.igeo.state.mapstate;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.views.DrawingPane;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public abstract class ToolState {

    private Map<String, Object> parameter;

    protected ToolState substate;

    protected String invokingAction;

    protected ApplicationContainer<?> appContainer;
    
    protected DrawingPane drawingPane;

    /**
     * 
     * @param appContainer
     */
    public ToolState( ApplicationContainer<?> appContainer ) {
        parameter = new HashMap<String, Object>();
        this.appContainer = appContainer;
    }

    /**
     * 
     * @param appContainer
     * @param invokingAction
     *            the name of the action setting the map to this state
     */
    public ToolState( ApplicationContainer<?> appContainer, String invokingAction ) {
        this( appContainer );
        this.invokingAction = invokingAction;
    }

    /**
     * 
     * @param appContainer
     * @param parameter
     * @param invokingAction
     *            the name of the action setting the map to this state
     */
    public ToolState( ApplicationContainer<?> appContainer, HashMap<String, Object> parameter, String invokingAction ) {
        this.appContainer = appContainer;
        this.parameter = parameter;
        this.invokingAction = invokingAction;
    }

    /**
     * 
     * @param name
     * @return named parameter
     */
    public Object getParameter( String name ) {
        return parameter.get( name );
    }

    /**
     * adds a parameter to a state
     * 
     * @param name
     * @param value
     */
    public void addParameter( String name, Object value ) {
        parameter.put( name, value );
    }

    /**
     * removes a parameter from a state
     * 
     * @param name
     */
    public void removeParameter( String name ) {
        parameter.remove( name );
    }

    /**
     * 
     * @return names of all parameters assigned to a state
     */
    public Set<String> getParameterNames() {
        return parameter.keySet();
    }

    /**
     * 
     * @return the subState
     */
    public ToolState getSubstate() {
        return substate;
    }

    /**
     * setting sub state must be overwritten by extending classes otherwise invoking this method will throw an
     * UnsupportedOperationException.
     * 
     * @param substate
     */
    public void setSubstate( ToolState substate ) {
        throw new UnsupportedOperationException();
    }

    /**
     * 
     * @param platform
     *            Swing, Applet, Portlet ...
     * @param g
     *            target graphic context
     * @return
     */
    public abstract DrawingPane createDrawingPane( String platform, Graphics g );

    /**
     * @return the name of the action setting the state
     */
    public String getInvokingAction() {
        return this.invokingAction;
    }

    /**
     * @param invokingAction
     *            the name of the action setting the map to this state
     */
    public void setInvokingAction( String invokingAction ) {
        this.invokingAction = invokingAction;
    }

    public void mouseMoved( MouseEvent event ) {

    }

    public void mouseDragged( MouseEvent event ) {

    }

    public void mousePressed( MouseEvent event ) {

    }

    public void mouseReleased( MouseEvent event ) {

    }
}
