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

package org.deegree.igeo.views.swing.layerlist;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
class SelectableLabel extends JLabel {

    private static final long serialVersionUID = 7966673276150886469L;

    enum STATUS {
        SELECTED, UNSELECTED, INDIFFERENT
    };

    private Icon selectedIcon;

    private Icon indifferentIcon;

    private Icon unSelectedIcon;

    private STATUS status = STATUS.INDIFFERENT;

    private List<ActionListener> listeners = new ArrayList<ActionListener>();

    /**
     * 
     * @param text
     * @param selectedIcon
     * @param indifferentIcon
     * @param unSelectedIcon
     */
    SelectableLabel( String text, Icon selectedIcon, Icon indifferentIcon, Icon unSelectedIcon ) {
        super( text, indifferentIcon, LEFT );
        this.selectedIcon = selectedIcon;
        this.indifferentIcon = indifferentIcon;
        this.unSelectedIcon = unSelectedIcon;
        addMouseListener( new MouseClick() );
    }

    /**
     * 
     * @param status
     */
    void setStatus( STATUS status ) {
        this.status = status;
        if ( status.equals( STATUS.SELECTED ) ) {
            setIcon( selectedIcon );
        } else if ( status.equals( STATUS.UNSELECTED ) ) {
            setIcon( unSelectedIcon );
        } else {
            setIcon( indifferentIcon );
        }
        ActionEvent ee = new ActionEvent( this, 0, status.name() );
        for ( int i = 0; i < listeners.size(); i++ ) {
            listeners.get( i ).actionPerformed( ee );
        }
    }

    /**
     * similar to {@link #setSelected(boolean)} but without informing listeners
     * 
     * @param selected
     */
    void update( boolean selected ) {
        if ( selected ) {
            setIcon( selectedIcon );
        } else {
            setIcon( unSelectedIcon );
        }
    }

    /**
     * 
     * @param selected
     */
    void setSelected( boolean selected ) {
        if ( selected ) {
            setStatus( STATUS.SELECTED );
        } else {
            setStatus( STATUS.UNSELECTED );
        }
    }

    /**
     * 
     * @param listener
     */
    public void addActionListener( ActionListener listener ) {
        listeners.add( listener );
    }

    /**
     * 
     * @param listener
     */
    public void removeActionListener( ActionListener listener ) {
        listeners.remove( listener );
    }

    /**
     * 
     * @return current status of a label
     */
    STATUS getStatus() {
        return status;
    }

    private class MouseClick extends MouseAdapter {

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseClicked( MouseEvent e ) {
            if ( listeners.size() > 0 && !status.equals( STATUS.INDIFFERENT ) ) {
                if ( status.equals( STATUS.SELECTED ) ) {
                    setSelected( false );
                } else {
                    setSelected( true );
                }
            }
        }

    }

}
