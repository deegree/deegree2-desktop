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

package org.deegree.desktop.views.swing;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import org.deegree.desktop.state.mapstate.ToolState;

/**
 * <code>MenuBar</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class MenuBar extends JMenuBar implements ControlElementView {

    private static final long serialVersionUID = 7200236883272323595L;

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.ControlElementView#synchronize(org.deegree.client.presenter.state.mapstate.ToolState)
     */
    public void synchronize( ToolState state ) {
        for ( int i = 0; i < getComponentCount(); i++ ) {
            if ( getComponent( i ) instanceof JMenu ) {
                JMenu menu = (JMenu) getComponent( i );
                setMenuSelected( menu, state );

            }
        }
    }

    /**
     * synchronize a menu and submenus of the menubar
     * 
     * @param menu
     * @param state
     */
    private void setMenuSelected( JMenu menu, ToolState state ) {

        for ( int j = 0; j < menu.getItemCount(); j++ ) {
            if ( menu.getItem( j ) instanceof JMenu ) {

                setMenuSelected( (JMenu) menu.getItem( j ), state );

            } else if ( menu.getItem( j ) instanceof JRadioButtonMenuItem
                        || menu.getItem( j ) instanceof JCheckBoxMenuItem ) {
                JMenuItem item = menu.getItem( j );
                if ( item.getName().equals( state.getInvokingAction() ) ) {
                    item.setSelected( true );
                }

            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.ControlElementView#isAvailable()
     */
    public boolean isAvailable() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.ControlElementView#setAvailable(boolean)
     */
    public void setAvailable( boolean available ) {
        // TODO Auto-generated method stub

    }

}
