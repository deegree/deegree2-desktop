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
package org.deegree.igeo.views.swing.proxymanager;

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
import org.deegree.igeo.views.swing.DefaultInnerFrame;
import org.deegree.igeo.views.swing.util.IconRegistry;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ProxyManagerlFrame extends DefaultInnerFrame {

    private static final long serialVersionUID = -2279919826733393981L;

    private JPanel pnButtons;

    private JButton btClose;

    private JTextArea taDescription;

    private ProxyManagerPanel proxyManagerPanel;

    private JPanel pnDescription;

    @Override
    public void init( ViewFormType viewForm )
                            throws Exception {
        super.init( viewForm );
        proxyManagerPanel = new ProxyManagerPanel();
        proxyManagerPanel.registerModule( this.owner );
        proxyManagerPanel.init( viewForm );
        initGUI();
        setVisible( true );
        toFront();
    }

    private void initGUI() {
        try {
            this.setPreferredSize( new java.awt.Dimension( 601, 417 ) );
            this.setBounds( 0, 0, 601, 417 );
            BorderLayout thisLayout = new BorderLayout();
            setVisible( true );
            getContentPane().setLayout( thisLayout );
            {
                pnButtons = new JPanel();
                FlowLayout pnButtonsLayout = new FlowLayout();
                pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                pnButtons.setLayout( pnButtonsLayout );
                getContentPane().add( pnButtons, BorderLayout.SOUTH );
                pnButtons.setPreferredSize( new java.awt.Dimension( 559, 36 ) );
                {
                    btClose = new JButton( Messages.getMessage( getLocale(), "$MD11364" ),
                                            IconRegistry.getIcon( "cancel.png" ) );
                    pnButtons.add( btClose );
                    btClose.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            ProxyManagerlFrame.this.dispose();
                        }
                    } );
                }
            }
            {
                pnDescription = new JPanel();
                BorderLayout pnDescriptionLayout = new BorderLayout();
                getContentPane().add( pnDescription, BorderLayout.WEST );
                pnDescription.setLayout( pnDescriptionLayout );
                pnDescription.setPreferredSize( new java.awt.Dimension( 188, 357 ) );
                pnDescription.setBorder( BorderFactory.createTitledBorder( null, Messages.getMessage( getLocale(),
                                                                                                      "$MD11365" ),
                                                                           TitledBorder.LEADING,
                                                                           TitledBorder.DEFAULT_POSITION ) );
                {
                    taDescription = new JTextArea();
                    taDescription.setBackground( pnDescription.getBackground() );
                    pnDescription.add( taDescription, BorderLayout.CENTER );
                    taDescription.setText( Messages.getMessage( getLocale(), "$MD11366" ) );
                    taDescription.setEditable( false );
                    taDescription.setWrapStyleWord( true );
                    taDescription.setLineWrap( true );
                }
            }
            {
                getContentPane().add( proxyManagerPanel, BorderLayout.CENTER );
                proxyManagerPanel.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                    "$MD11367" ) ) );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
