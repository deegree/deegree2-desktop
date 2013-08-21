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
package org.deegree.desktop.views.swing.monitor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class LoggerWindow extends JFrame {

    private static final long serialVersionUID = 7254488505481153936L;

    private JPanel pnButtons;

    private JButton btClose;

    private JTextArea taMessage;

    private JScrollPane scMessage;

    /**
     * 
     */
    public LoggerWindow() {
        initGUI();
        setVisible( false );
        setAlwaysOnTop( true );
    }

    private void initGUI() {
        try {
            BorderLayout thisLayout = new BorderLayout();
            setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
            getContentPane().setLayout( thisLayout );
            {
                pnButtons = new JPanel();
                FlowLayout pnButtonsLayout = new FlowLayout();
                pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                getContentPane().add( pnButtons, BorderLayout.SOUTH );
                pnButtons.setLayout( pnButtonsLayout );
                pnButtons.setPreferredSize( new java.awt.Dimension( 577, 37 ) );
                {
                    btClose = new JButton( "close" );
                    pnButtons.add( btClose );
                    btClose.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            dispose();
                        }
                    } );
                }
            }
            {
                scMessage = new JScrollPane();
                getContentPane().add( scMessage, BorderLayout.CENTER );
                {
                    taMessage = new JTextArea();
                    scMessage.setViewportView( taMessage );
                    taMessage.setLineWrap( true );
                    taMessage.setWrapStyleWord( true );
                    taMessage.setEditable( false );
                }
            }
            pack();
            this.setSize( 585, 326 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param list
     */
    public void setText( List<String> list ) {
        StringBuilder sb = new StringBuilder( 10000 );
        for ( String s : list ) {
            sb.append( s ).append( "\n" );
        }
        taMessage.setText( sb.toString() );
    }

}
