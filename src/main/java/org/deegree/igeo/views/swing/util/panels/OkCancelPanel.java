//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2007 by:
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

package org.deegree.igeo.views.swing.util.panels;

import static java.awt.event.KeyEvent.VK_ESCAPE;
import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.KeyStroke.getKeyStroke;
import static org.deegree.igeo.i18n.Messages.get;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 * <code>OkCancelPanel</code> provides an ok and cancel button.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class OkCancelPanel extends JPanel {

    private static final long serialVersionUID = -230307366548116822L;

    /**
     * 
     */
    public JButton okButton;

    /**
     * 
     */
    public JButton cancelButton;

    /**
     * @param listener
     * @param root
     * @param cancel
     */
    public OkCancelPanel( ActionListener listener, JRootPane root, boolean cancel ) {
        setLayout( new GridBagLayout() );
        setBorder( createEmptyBorder( 2, 2, 2, 2 ) );

        GridBagConstraints gb = new GridBagConstraints();
        gb.insets = new Insets( 2, 2, 2, 2 );
        gb.gridx = 0;
        gb.gridy = 0;
        URL okIcon = OkCancelPanel.class.getResource( "/org/deegree/igeo/views/images/accept.png" );
        okButton = new JButton( get( "$DI10001" ), new ImageIcon( okIcon ) );
        okButton.addActionListener( listener );
        add( okButton, gb );
        if ( cancel ) {
            URL cancelIcon = OkCancelPanel.class.getResource( "/org/deegree/igeo/views/images/cancel.png" );
            cancelButton = new JButton( get( "$DI10002" ), new ImageIcon( cancelIcon ) );
            cancelButton.addActionListener( listener );
            ++gb.gridx;
            add( cancelButton, gb );
        }

        root.setDefaultButton( okButton );
        KeyStroke stroke = getKeyStroke( VK_ESCAPE, 0 );
        root.registerKeyboardAction( listener, stroke, WHEN_IN_FOCUSED_WINDOW );
    }

}
