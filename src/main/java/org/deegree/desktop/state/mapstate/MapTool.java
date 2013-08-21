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

package org.deegree.desktop.state.mapstate;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.ChangeListener;
import org.deegree.desktop.views.DrawingPane;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 * @param <T>
 */
public class MapTool<T> {

    private ToolState state;

    private ApplicationContainer<T> appContainer;

    private List<ChangeListener> listeners = new ArrayList<ChangeListener>();

    /**
     * @param appContainer
     * 
     * 
     */
    public MapTool( ApplicationContainer<T> appContainer ) {
        this.appContainer = appContainer;
        setSelectState();
    }

    /**
     * @param listener
     */
    public void addChangeListener( ChangeListener listener ) {
        this.listeners.add( listener );
    }

    /**
     * @param listener
     */
    public void removeChangeListener( ChangeListener listener ) {
        this.listeners.remove( listener );
    }

    /**
     * 
     * @param state
     */
    public void setState( ToolState state ) {
        this.state = state;
        fireMapStateChangedEvent();
    }

    /**
     * 
     * @return current state
     */
    public ToolState getState() {
        ToolState tmp = state;
        if ( tmp != null ) {
            while ( tmp.getSubstate() != null ) {
                tmp = tmp.getSubstate();
            }
        }
        if ( tmp == null ) {
            // ensure that not null will be returned;
            resetState();
            tmp = state;
        }
        return tmp;
    }

    /**
     * resets the state of a MapTool by setting an anonymous inner class/instance of {@link ToolState}
     * 
     */
    public void resetState() {
        state = new ResetedToolState( appContainer );
    }

    private class ResetedToolState extends ToolState {

        public ResetedToolState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
            createDrawingPane( null, null );
        }

        @Override
        public DrawingPane createDrawingPane( String platform, Graphics g ) {
            drawingPane = null;
            return null;
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // convenience methods for setting well known states //
    // /////////////////////////////////////////////////////////////////////////////////

    /**
     * sets zoomin state
     * 
     * @param assignedAction
     */
    public void setZoomInState( String assignedAction ) {
        this.state = new ZoomInState( appContainer, assignedAction );
        fireMapStateChangedEvent();
    }

    /**
     * sets zoomout state
     * 
     * @param assignedAction
     */
    public void setZoomOutState( String assignedAction ) {
        this.state = new ZoomOutState( appContainer, assignedAction );
        fireMapStateChangedEvent();
    }

    /**
     * sets pan state
     * 
     * @param assignedAction
     */
    public void setPanState( String assignedAction ) {
        this.state = new PanState( appContainer, assignedAction );
        fireMapStateChangedEvent();
    }

    /**
     * sets center state
     * 
     * @param assignedAction
     */
    public void setCenterState( String assignedAction ) {
        this.state = new CenterState( appContainer, assignedAction );
        fireMapStateChangedEvent();
    }

    /**
     * sets select state
     */
    public void setSelectState() {
        this.state = new SelectState( appContainer );
    }

    /**
     * sets edit state
     */
    public void setEditState() {
        this.state = new EditState( appContainer );
    }

    /**
     * sets measure distance state
     */
    public void setMeasureDistanceState() {
        this.state = new MeasureDistanceState( appContainer );
        fireMapStateChangedEvent();
    }

    /**
     * sets measure area state
     * 
     */
    public void setMeasureAreaState() {
        this.state = new MeasureAreaState( appContainer );
        fireMapStateChangedEvent();
    }

    /**
     * 
     */
    public void setHotlinkState() {
        state = new HotlinkState<T>( appContainer );
        fireMapStateChangedEvent();
    }

    /**
     * sets info state
     * 
     * @param assignedAction
     */
    public void setInfoState( String assignedAction ) {
        this.state = new InfoState( appContainer, assignedAction );
    }

    protected void fireMapStateChangedEvent() {
        MapStateChangedEvent event = new MapStateChangedEvent( state );
        for ( int i = 0; i < this.listeners.size(); i++ ) {
            this.listeners.get( i ).valueChanged( event );
        }
    }
}
