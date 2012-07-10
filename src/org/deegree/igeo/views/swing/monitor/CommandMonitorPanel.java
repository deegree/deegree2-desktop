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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.deegree.datatypes.QualifiedName;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.kernel.CommandProcessor;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class CommandMonitorPanel extends JPanel {

    private static final long serialVersionUID = 6979740823437095878L;

    private JPanel pnDone;

    private JPanel pnUndone;

    private JScrollPane scUndone;

    private JList lsUndone;

    private JList lsDone;

    private JScrollPane scDone;

    private ApplicationContainer<Container> appCont;

    
    public CommandMonitorPanel() {
        initGUI();   
    }
    /**
     * 
     * @param appCont
     */
    public CommandMonitorPanel( ApplicationContainer<Container> appCont ) {
        this.appCont = appCont;
        initGUI();
        Timer timer = new Timer();
        timer.schedule( new Task(), 0, 2000 );
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            this.setPreferredSize( new java.awt.Dimension( 482, 375 ) );
            thisLayout.rowWeights = new double[] { 0.0, 0.1 };
            thisLayout.rowHeights = new int[] { 328, 7 };
            thisLayout.columnWeights = new double[] { 0.0, 0.0, 0.1 };
            thisLayout.columnWidths = new int[] { 240, 7, 7 };
            this.setLayout( thisLayout );
            {
                pnDone = new JPanel();
                BorderLayout pnDoneLayout = new BorderLayout();
                pnDone.setLayout( pnDoneLayout );
                this.add( pnDone, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                          GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnDone.setBorder( BorderFactory.createTitledBorder( null, "done commands", TitledBorder.LEADING,
                                                                    TitledBorder.DEFAULT_POSITION ) );
                {
                    scDone = new JScrollPane();
                    pnDone.add( scDone, BorderLayout.CENTER );
                    {
                        lsDone = new JList( new DefaultListModel() );
                        scDone.setViewportView( lsDone );
                    }
                }
            }
            {
                pnUndone = new JPanel();
                BorderLayout pnUndoneLayout = new BorderLayout();
                pnUndone.setLayout( pnUndoneLayout );
                this.add( pnUndone, new GridBagConstraints( 2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                            GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnUndone.setBorder( BorderFactory.createTitledBorder( "undone commands" ) );
                {
                    scUndone = new JScrollPane();
                    pnUndone.add( scUndone, BorderLayout.CENTER );
                    {
                        lsUndone = new JList( new DefaultListModel() );
                        scUndone.setViewportView( lsUndone );
                    }
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void fillLists() {
        CommandProcessor processor = appCont.getCommandProcessor();
        List<QualifiedName> undone = processor.availableRedos();
        DefaultListModel model = (DefaultListModel) lsUndone.getModel();
        model.clear();
        for ( QualifiedName qualifiedName : undone ) {
            model.addElement( qualifiedName.getLocalName() );
        }
        lsUndone.setModel( model );

        List<QualifiedName> done = processor.availableUndos();
        model = (DefaultListModel) lsDone.getModel();
        model.clear();
        for ( QualifiedName qualifiedName : done ) {
            model.addElement( qualifiedName.getLocalName() );
        }
        lsDone.setModel( model );
        repaint();
    }

    class Task extends TimerTask {

        @Override
        public void run() {
            CommandMonitorPanel.this.fillLists();
        }

    }

}
