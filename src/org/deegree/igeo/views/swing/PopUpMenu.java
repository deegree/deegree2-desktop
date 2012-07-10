//$HeadURL: svn+ssh://mschneider@svn.wald.intevation.org/deegree/base/trunk/resources/eclipse/svn_classfile_header_template.xml $
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

package org.deegree.igeo.views.swing;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import org.deegree.igeo.state.mapstate.ToolState;

/**
 * <code>PopUpMenu</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class PopUpMenu extends JPopupMenu implements ControlElementView {

    private static final long serialVersionUID = -8045434114902707282L;

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.ControlElementView#synchronize(org.deegree.client.presenter.state.mapstate.ToolState)
     */
    public void synchronize( ToolState state ) {
        for ( int i = 0; i < getComponentCount(); i++ ) {
            if ( getComponent( i ) instanceof JCheckBoxMenuItem || getComponent( i ) instanceof JRadioButtonMenuItem ) {
                JMenuItem popUpItem = (JMenuItem) getComponent( i );
                if ( popUpItem.getName().equals( state.getInvokingAction() ) ) {
                    popUpItem.setSelected( true );
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
