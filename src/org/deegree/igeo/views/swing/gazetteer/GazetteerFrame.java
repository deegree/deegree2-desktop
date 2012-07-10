//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.igeo.views.swing.gazetteer;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.views.swing.DefaultFrame;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class GazetteerFrame extends DefaultFrame {

    private static final long serialVersionUID = -8974501505541610245L;

    private JPanel pnButtons;

    private JButton btClose;

    private GazetteerPanel gazetteerPanel;

    private JPanel pnDescription;

    private JPanel pnMain;

    @Override
    public void init( ViewFormType viewForm )
                            throws Exception {
        super.init( viewForm );
        initGUI();
        setResizable( false );
        gazetteerPanel.registerModule( this.owner );
    }

    private void initGUI() {
        try {
            this.setPreferredSize( new java.awt.Dimension( 502, 406 ) );
            this.setBounds( 0, 0, 502, 406 );
            BorderLayout thisLayout = new BorderLayout();
            setVisible( true );
            getContentPane().setLayout( thisLayout );
            {
                pnButtons = new JPanel();
                FlowLayout jPanel1Layout = new FlowLayout();
                jPanel1Layout.setAlignment( FlowLayout.LEFT );
                pnButtons.setLayout( jPanel1Layout );
                getContentPane().add( pnButtons, BorderLayout.SOUTH );
                pnButtons.setPreferredSize( new java.awt.Dimension( 342, 37 ) );
                {
                    btClose = new JButton( Messages.getMessage( getLocale(), "$MD11310" ) );
                    btClose.setToolTipText( Messages.getMessage( getLocale(), "$MD11311" ) );
                    btClose.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            dispose();
                        }
                    } );
                    pnButtons.add( btClose );
                }
            }
            {
                pnMain = new JPanel();
                getContentPane().add( pnMain, BorderLayout.CENTER );
                BorderLayout pnMainLayout = new BorderLayout();
                pnMain.setLayout( pnMainLayout );
                pnMain.setPreferredSize( new java.awt.Dimension( 408, 279 ) );
                {
                    pnDescription = new JPanel();
                    pnDescription.setLayout( new BorderLayout() );
                    JTextArea ta = new JTextArea( Messages.getMessage( getLocale(), "$MD11313" ) );
                    ta.setLineWrap( true );
                    ta.setWrapStyleWord( true );
                    ta.setEditable( false );
                    ta.setBackground( pnDescription.getBackground() );
                    pnDescription.add( ta , BorderLayout.CENTER);
                    pnMain.add( pnDescription, BorderLayout.WEST );
                    pnDescription.setBorder( BorderFactory.createTitledBorder( null, Messages.getMessage( getLocale(), "$MD11312" ),
                                                                               TitledBorder.LEADING,
                                                                               TitledBorder.DEFAULT_POSITION ) );
                    pnDescription.setPreferredSize( new java.awt.Dimension( 161, 345 ) );
                }
                {
                    gazetteerPanel = new GazetteerPanel();
                    pnMain.add( gazetteerPanel, BorderLayout.CENTER );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
