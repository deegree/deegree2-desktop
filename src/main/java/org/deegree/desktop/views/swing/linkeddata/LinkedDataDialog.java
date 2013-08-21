/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2010 by:
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
package org.deegree.desktop.views.swing.linkeddata;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.modules.IModule;
import org.deegree.desktop.views.HelpManager;
import org.deegree.desktop.views.swing.HelpFrame;
import org.deegree.desktop.views.swing.util.IconRegistry;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class LinkedDataDialog extends JDialog {

    private static final long serialVersionUID = 8731398854314868152L;

    private JButton btCancel;

    private JButton btHelp;

    private JPanel pnDescription;

    private JScrollPane scDescription;

    private LinkedDataFramePanel linkedDataFramePanel;

    private JEditorPane epDescription;

    private IModule<?> owner;

    /**
     * 
     * @param owner
     */
    public LinkedDataDialog( IModule<?> owner ) {
        this.owner = owner;
        initGUI();
        setLocation( 200, 200 );
        setAlwaysOnTop( true );
        setModal( true );
        setVisible( true );
    }

    private void initGUI() {
        try {
            {
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] { 0.0, 0.1 };
                thisLayout.rowHeights = new int[] { 371, 7 };
                thisLayout.columnWeights = new double[] { 0.0, 0.1 };
                thisLayout.columnWidths = new int[] { 197, 7 };
                getContentPane().setLayout( thisLayout );
                {
                    btCancel = new JButton( Messages.getMessage( getLocale(), "$MD11560" ),
                                            IconRegistry.getIcon( "cancel.png" ) );
                    getContentPane().add(
                                          btCancel,
                                          new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                                  GridBagConstraints.NONE, new Insets( 0, 9, 0, 0 ), 0,
                                                                  0 ) );
                    btCancel.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            LinkedDataDialog.this.dispose();
                        }
                    } );
                }
                {
                    btHelp = new JButton( Messages.getMessage( getLocale(), "$MD11561" ),
                                          IconRegistry.getIcon( "help.png" ) );
                    getContentPane().add(
                                          btHelp,
                                          new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                                                  GridBagConstraints.NONE, new Insets( 0, 0, 0, 9 ), 0,
                                                                  0 ) );
                    btHelp.addActionListener( new ActionListener() {

                        @SuppressWarnings("unchecked")
                        public void actionPerformed( ActionEvent e ) {
                            ApplicationContainer<Container> appCont = (ApplicationContainer<Container>) owner.getApplicationContainer();
                            HelpFrame hf = HelpFrame.getInstance( new HelpManager( appCont ) );
                            hf.setVisible( true );
                            hf.gotoModule( "org.deegree.igeo.modules.DefaultMapModule" );
                        }
                    } );
                }
                {
                    pnDescription = new JPanel();
                    BorderLayout pnDescriptionLayout = new BorderLayout();
                    pnDescription.setLayout( pnDescriptionLayout );
                    getContentPane().add(
                                          pnDescription,
                                          new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnDescription.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                    "$MD11562" ) ) );
                    {
                        scDescription = new JScrollPane( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                         JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
                        pnDescription.add( scDescription, BorderLayout.CENTER );
                        scDescription.setPreferredSize( new Dimension( 40, 56 ) );

                        scDescription.setBorder( BorderFactory.createEmptyBorder( 0, 0, 0, 0 ) );
                        {
                            epDescription = new JEditorPane() ;
                            epDescription.setContentType( "text/html" );
                            epDescription.setBackground( pnDescription.getBackground() );
                            epDescription.setEditable( false );
                            scDescription.setViewportView( epDescription );
                        }
                    }
                }
                {
                    linkedDataFramePanel = new LinkedDataFramePanel( this, owner, epDescription );
                    getContentPane().add(
                                          linkedDataFramePanel,
                                          new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                }
            }
            this.setSize( 645, 437 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
