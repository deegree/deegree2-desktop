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

package org.deegree.desktop.views.swing.actionlisteners;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.KeyStroke;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class KeyListenerRegister {

    /**
     * registers default key listeners to passed component
     * @param component
     */
    public static final void registerDefaultKeyListener( JComponent component ) {

        // move main window to background 
        KeyStroke keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_O, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK, false );
        component.registerKeyboardAction( OrderFramesListener.getListener(), keyStroke,
                                          JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
        
        // close all windows except main window
        keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_Q, InputEvent.ALT_MASK | InputEvent.SHIFT_MASK, false );
        component.registerKeyboardAction( CloseFramesListener.getListener(), keyStroke,
                                          JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
    }
    
    
}
