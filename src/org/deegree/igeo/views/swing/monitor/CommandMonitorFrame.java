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
package org.deegree.igeo.views.swing.monitor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import org.deegree.igeo.ApplicationContainer;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class CommandMonitorFrame extends JFrame {

    private static final long serialVersionUID = -6941110386870767639L;

    private JPanel pnClose;

    private JButton btClose;

    private CommandMonitorPanel commandMonitorPanel;

    private JPanel pnDescription;

    public CommandMonitorFrame( ApplicationContainer<Container> appCont ) {
        initGUI( appCont );
        setVisible( true );
        toFront();
    }

    private void initGUI( ApplicationContainer<Container> appCont ) {
        try {
            BorderLayout thisLayout = new BorderLayout();
            setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
            getContentPane().setLayout( thisLayout );
            {
                pnClose = new JPanel();
                FlowLayout jPanel1Layout = new FlowLayout();
                jPanel1Layout.setAlignment( FlowLayout.LEFT );
                pnClose.setLayout( jPanel1Layout );
                getContentPane().add( pnClose, BorderLayout.SOUTH );
                pnClose.setPreferredSize( new java.awt.Dimension( 590, 37 ) );
                {
                    btClose = new JButton();
                    pnClose.add( btClose );
                    btClose.setText( "close" );
                }
            }
            {
                pnDescription = new JPanel();
                FlowLayout pnDescriptionLayout = new FlowLayout();
                getContentPane().add( pnDescription, BorderLayout.WEST );
                pnDescription.setLayout( pnDescriptionLayout );
                pnDescription.setPreferredSize( new java.awt.Dimension( 177, 376 ) );
                pnDescription.setBorder( BorderFactory.createTitledBorder( null, "description", TitledBorder.LEADING,
                                                                           TitledBorder.DEFAULT_POSITION ) );
            }
            {
                commandMonitorPanel = new CommandMonitorPanel( appCont );
                getContentPane().add( commandMonitorPanel, BorderLayout.CENTER );
            }
            pack();
            this.setSize( 677, 443 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
