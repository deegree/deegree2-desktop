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
package org.deegree.desktop.views;

import org.deegree.desktop.ChangeListener;
import org.deegree.desktop.config.ComponentStateType;
import org.deegree.desktop.config.WindowStateType;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class ComponentState extends AbstractComponent {

    private ComponentStateType cs;

    /**
     * 
     * @param identifier
     * @param componentState
     */
    public ComponentState( ComponentStateType componentState ) {
        this.cs = componentState;
    }

    /**
     * 
     * @return true if closed
     */
    public boolean isClosed() {
        return this.cs.getWindowState() == WindowStateType.CLOSED;
    }

    /**
     * 
     * @return true if active
     */
    public boolean isActive() {
        return this.cs.isActive();
    }

    /**
     * 
     * @return true if modal
     */
    public boolean isModal() {
        return this.cs.isModal();
    }

    /**
     * 
     * @return
     */
    public long getOrder() {        
        if ( this.cs.getOrder() != null ) {
            return this.cs.getOrder().longValue();
        } else {
            return 1;
        }
    }

    /**
     * 
     * @param closed
     */
    public void setClosed( boolean closed ) {
        if ( closed ) {
            this.cs.setWindowState( WindowStateType.CLOSED );
        } else {
            this.cs.setWindowState( WindowStateType.NORMAL );
        }
    }

    /**
     * 
     * @param minimized
     */
    public void setMinimized( boolean minimized ) {
        if ( minimized ) {
            this.cs.setWindowState( WindowStateType.MINIMIZED );
        }
    }

    /**
     * 
     * @param maximized
     */
    public void setMaximized( boolean maximized ) {
        if ( maximized ) {
            this.cs.setWindowState( WindowStateType.MAXIMIZED );
        }
    }

    /**
     * 
     * @param normalized
     */
    public void setNormal( boolean normal ) {
        if ( normal ) {
            this.cs.setWindowState( WindowStateType.NORMAL );
        }
    }

    /**
     * 
     * @param isModal
     */
    public void setModal( boolean modal ) {
        this.cs.setModal( modal );
    }

    /**
     * 
     * @param isActive
     */
    public void setActive( boolean active ) {
        this.cs.setActive( active );
    }

    /**
     * 
     * @param order
     */
    public void orderPosition( int order ) {
        this.cs.setOrder( order );
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append( "ComponentState: " );
        sb.append( "\n    Active: " );
        sb.append( this.cs.isActive() );
        sb.append( "\n    Modal: " );
        sb.append( this.cs.isModal() );
        sb.append( "\n    Closed: " );
        sb.append( this.cs.getWindowState() );
        sb.append( "\n    Order: " );
        sb.append( this.cs.getOrder() );
        sb.append( "\n    CurrentLayout: " );

        return sb.toString();
    }

    /**
     * 
     */
    public void update() {
        if ( this.listener != null ) {
            for ( ChangeListener appListener : this.listener ) {
                appListener.valueChanged( new ComponentStateEvent() );
            }
        }
    }

}
