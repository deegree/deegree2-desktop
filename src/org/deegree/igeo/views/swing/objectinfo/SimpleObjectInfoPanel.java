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

package org.deegree.igeo.views.swing.objectinfo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.deegree.igeo.commands.ObjectInfoCommand;
import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.views.swing.DefaultPanel;
import org.deegree.kernel.Command;
import org.deegree.kernel.CommandProcessedEvent;
import org.deegree.kernel.CommandProcessedListener;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.schema.FeatureType;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class SimpleObjectInfoPanel extends DefaultPanel implements CommandProcessedListener {

    private static final long serialVersionUID = 960915481830412762L;

    private JTabbedPane tabbedPane;

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#init(org.deegree.client.presenter.state.ComponentStateAdapter,
     * org.deegree.client.configuration.ViewForm)
     */
    public void init( ViewFormType viewForm )
                            throws Exception {
        setDoubleBuffered( false );
        setLayout( new BorderLayout() );
        JLabel label = new JLabel( owner.getName() );
        label.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        add( label, BorderLayout.NORTH );
        add( tabbedPane = new JTabbedPane(), BorderLayout.CENTER );
        setPreferredSize( new Dimension( 100, 100 ) );
        // add panel as listener to command processor to ensure that it will be informed
        // if command has been processed
        owner.getApplicationContainer().getCommandProcessor().addCommandProcessedListener( this );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.deegree.client.presenter.connector.CommandProcessedListener#commandProcessed(org.deegree.client.presenter
     * .connector.CommandProcessedEvent)
     */
    @SuppressWarnings("unchecked")
    public void commandProcessed( CommandProcessedEvent event ) {

        Command command = event.getSource();
        if ( command.getName().equals( ObjectInfoCommand.commandName ) ) {
            tabbedPane.removeAll();
            List<FeatureCollection> fc = (List<FeatureCollection>) command.getResult();
            for ( FeatureCollection collection : fc ) {
                if ( collection != null && collection.size() > 0 ) {
                    FeatureType ft = collection.getFeature( 0 ).getFeatureType();
                    TableModel model = new FeatureTableModel( collection );
                    JTable table = new JTable( model );
                    int colCount = model.getColumnCount();
                    for ( int i = 0; i < colCount; i++ ) {
                        TableColumn column = table.getColumnModel().getColumn( i );
                        column.setPreferredWidth( 50 );
                    }
                    table.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
                    JPanel panel = new JPanel();
                    panel.setLayout( new BorderLayout() );
                    panel.add( table.getTableHeader(), BorderLayout.PAGE_START );
                    panel.add( table, BorderLayout.CENTER );
                    panel.setMinimumSize( new Dimension( colCount * 50, table.getHeight() ) );
                    JScrollPane sc = new JScrollPane( panel );

                    tabbedPane.addTab( ft.getName().getLocalName(), sc );
                }
            }
        }
    }

}
