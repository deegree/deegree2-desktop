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

package org.deegree.desktop.main;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.deegree.desktop.Version;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class VersionDialog extends JDialog {

    private static final long serialVersionUID = 5565180637713870982L;

    /**
     * 
     * 
     */
    public VersionDialog() {
        setBounds( 300, 300, 420, 303 );
        setLayout( null );
        getContentPane().setLayout( null );
        setModal( true );
        setUndecorated( true );
        setResizable( false );

        JLabel vlabel = new JLabel( "Version: " + Version.getVersionNumber() );
        vlabel.setBounds( 200, 240, 240, 20 );
        add( vlabel );
        JLabel vdlabel = new JLabel( "Date: " + Version.getVersionDate() );
        vdlabel.setBounds( 200, 255, 240, 20 );
        add( vdlabel );
        JLabel svnlabel = new JLabel( "SVN revision: " + Version.getSVNRevison() );
        svnlabel.setBounds( 200, 270, 240, 20 );
        add( svnlabel );

        // Icon icon = new ImageIcon( VersionDialog.class.getResource(
        // "/org/deegree/igeo/views/images/accept.png" ) );
        JButton button = new JButton( "close" );
        button.setMargin( new Insets( 0, 0, 0, 0 ) );
        button.setBounds( 50, 270, 70, 20 );
        button.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent event ) {
                VersionDialog.this.dispose();
            }

        } );

        add( button );

        JLabel label = new JLabel( new ImageIcon( VersionDialog.class.getResource( "igeodesktop.jpg" ) ) );
        label.setSize( 420, 303 );
        add( label );

        setVisible( true );
    }
}
