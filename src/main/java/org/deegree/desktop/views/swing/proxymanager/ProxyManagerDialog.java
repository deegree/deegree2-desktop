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
package org.deegree.desktop.views.swing.proxymanager;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.views.swing.DefaultDialog;
import org.deegree.desktop.config.ViewFormType;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ProxyManagerDialog extends DefaultDialog {

    private static final long serialVersionUID = 5902208538892039827L;

    private JPanel pnButton;

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
            BorderLayout thisLayout = new BorderLayout();
            getContentPane().setLayout( thisLayout );
            {
                pnButton = new JPanel();
                FlowLayout pnButtonLayout = new FlowLayout();
                pnButtonLayout.setAlignment( FlowLayout.LEFT );
                pnButton.setLayout( pnButtonLayout );
                getContentPane().add( pnButton, BorderLayout.SOUTH );
                pnButton.setPreferredSize( new java.awt.Dimension( 392, 37 ) );
                {
                    btClose = new JButton( Messages.getMessage( getLocale(), "$MD11359" ) );
                    pnButton.add( btClose );
                    btClose.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            ProxyManagerDialog.this.dispose();
                        }
                    } );
                }
            }
            {
                pnDescription = new JPanel();
                BorderLayout pnDescriptionLayout = new BorderLayout();
                pnDescription.setLayout( pnDescriptionLayout );
                getContentPane().add( pnDescription, BorderLayout.WEST );
                pnDescription.setPreferredSize( new java.awt.Dimension( 201, 341 ) );
                pnDescription.setEnabled( false );
                pnDescription.setBorder( BorderFactory.createTitledBorder( null, Messages.getMessage( getLocale(),
                                                                                                      "$MD11360" ),
                                                                           TitledBorder.LEADING,
                                                                           TitledBorder.DEFAULT_POSITION ) );
                {
                    taDescription = new JTextArea();
                    taDescription.setBackground( pnDescription.getBackground() );
                    pnDescription.add( taDescription, BorderLayout.CENTER );
                    taDescription.setText( Messages.getMessage( getLocale(), "$MD11361" ) );
                    taDescription.setEditable( false );
                    taDescription.setWrapStyleWord( true );
                    taDescription.setLineWrap( true );
                }
            }
            {
                getContentPane().add( proxyManagerPanel, BorderLayout.CENTER );
                proxyManagerPanel.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                    "$MD11362" ) ) );
            }
            this.setSize( 566, 408 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    
}
