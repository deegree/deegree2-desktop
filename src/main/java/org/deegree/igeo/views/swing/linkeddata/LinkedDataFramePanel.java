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
package org.deegree.igeo.views.swing.linkeddata;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.model.LinkTableCommand;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.modules.IModule;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class LinkedDataFramePanel extends JPanel {

    private static final long serialVersionUID = -2903382507409285780L;

    private JPanel pnNavigation;

    private JButton btNext;

    private InitialPanel initialPanel;

    private JButton btFinish;

    private JButton btBack;

    private AbstractLinkedDataPanel current;

    private ApplicationContainer<Container> appCont;

    private JEditorPane descriptionArea;

    private Window parent;

    private int index;

    /**
     * 
     * @param owner
     */
    @SuppressWarnings("unchecked")
    public LinkedDataFramePanel( Window parent, IModule<?> owner, JEditorPane descriptionArea ) {
        this.parent = parent;
        this.appCont = (ApplicationContainer<Container>) owner.getApplicationContainer();
        this.descriptionArea = descriptionArea;
        initGUI();
    }

    private void initGUI() {
        try {
            BorderLayout thisLayout = new BorderLayout();
            // this.setPreferredSize( new Dimension( 489, 438 ) );
            this.setLayout( thisLayout );
            {
                pnNavigation = new JPanel();
                this.add( pnNavigation, BorderLayout.SOUTH );
                pnNavigation.setPreferredSize( new Dimension( 540, 38 ) );
                {
                    btBack = new JButton( Messages.getMessage( getLocale(), "$MD11578" ) );
                    pnNavigation.add( btBack );
                    btBack.setEnabled( false );
                    btBack.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            AbstractLinkedDataPanel previous = current.getPrevious();
                            if ( previous != null ) {
                                btFinish.setEnabled( false );
                                btNext.setEnabled( true );
                                index--;
                                if ( index == 0 ) {
                                    btBack.setEnabled( false );
                                }
                                LinkedDataFramePanel.this.remove( current );
                                current = previous;
                                descriptionArea.setText( current.getDescription() );
                                LinkedDataFramePanel.this.add( current, BorderLayout.CENTER );
                                SwingUtilities.updateComponentTreeUI( LinkedDataFramePanel.this );
                                LinkedDataFramePanel.this.repaint();
                            }
                        }
                    } );
                }
                {
                    btNext = new JButton( Messages.getMessage( getLocale(), "$MD11563" ) );
                    pnNavigation.add( btNext );
                    btNext.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            AbstractLinkedDataPanel next = current.getNext();
                            if ( next != null ) {
                                btBack.setEnabled( true );
                                index++;
                                if ( index == 3 ) {
                                    btNext.setEnabled( false );
                                    btFinish.setEnabled( true );
                                }
                                LinkedDataFramePanel.this.remove( current );
                                current = next;
                                descriptionArea.setText( current.getDescription() );
                                LinkedDataFramePanel.this.add( current, BorderLayout.CENTER );
                                SwingUtilities.updateComponentTreeUI( LinkedDataFramePanel.this );
                                LinkedDataFramePanel.this.repaint();
                            }
                        }
                    } );
                }
                {
                    btFinish = new JButton( Messages.getMessage( getLocale(), "$MD11564" ) );
                    pnNavigation.add( btFinish );
                    btFinish.setEnabled( false );
                    btFinish.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {

                            // LinkedTable lk = null;
                            // if ( current.getLinkedTable() instanceof LinkedFileTableType ) {
                            // String s = ( (LinkedFileTableType) current.getLinkedTable() ).getFile();
                            // try {
                            // if ( s.toLowerCase().endsWith( ".dbf" ) ) {
                            // lk = new LinkedDBaseTable( current.getLinkedTable(), new File( s ) );
                            // } else if ( s.toLowerCase().endsWith( ".csv" ) || s.toLowerCase().endsWith( ".tab" ) ) {
                            // lk = new LinkedCSVTable( current.getLinkedTable(), new File( s ) );
                            // } else if ( s.toLowerCase().endsWith( ".xls" )
                            // || s.toLowerCase().endsWith( ".xlsx" ) ) {
                            // lk = new LinkedExcelTable( current.getLinkedTable(), new File( s ) );
                            // }
                            // } catch ( IOException ex ) {
                            // // TODO Auto-generated catch block
                            // ex.printStackTrace();
                            // parent.dispose();
                            // return;
                            // }
                            // } else if ( current.getLinkedTable() instanceof LinkedDatabaseTableType ) {
                            // try {
                            // lk = new LinkedDatabaseTable( (LinkedDatabaseTableType) current.getLinkedTable() );
                            // } catch ( IOException e1 ) {
                            // // TODO Auto-generated catch block
                            // e1.printStackTrace();
                            // }
                            // }
                            LinkTableCommand cmd = new LinkTableCommand();
                            cmd.setView( current.isView() );
                            cmd.setLinkedTable( current.getLinkedTable() );
                            cmd.setMapModel( appCont.getMapModel( null ) );
                            String title = ( (NamesPanel) current ).getLayerTitle();
                            if ( title != null ) {
                                cmd.setLayerTitle( title );
                                try {
                                    appCont.getCommandProcessor().executeSychronously( cmd, true );
                                } catch ( Exception ex ) {
                                    // TODO Auto-generated catch block
                                    ex.printStackTrace();
                                }
                            }
                            parent.dispose();
                        }
                    } );
                }
            }
            {
                initialPanel = new InitialPanel();
                initialPanel.setApplicationContainer( appCont );
                current = initialPanel;
                descriptionArea.setText( current.getDescription() );
                this.add( initialPanel, BorderLayout.CENTER );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
