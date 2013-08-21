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
package org.deegree.desktop.views.swing.layerlist;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.desktop.views.swing.ExternalResourceDialog;
import org.deegree.desktop.views.swing.util.IconRegistry;
import org.deegree.framework.util.MimeTypeMapper;
import org.deegree.framework.util.StringTools;
import org.deegree.desktop.config.ExternalResourceType;
import org.deegree.desktop.config.OnlineResourceType;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class ExtResourcesPanel extends JPanel {

    private static final long serialVersionUID = 6007730424575382619L;

    private ApplicationContainer<Container> appCont;

    private JTable table;

    private JFrame parent;

    /**
     * 
     * @param appCont
     */
    ExtResourcesPanel( ApplicationContainer<Container> appCont, JFrame parent ) {
        this.appCont = appCont;
        this.parent = parent;
    }

    void init( final Layer layer ) {
        if ( layer != null ) {
            JPanel center = new JPanel();
            center.setBorder( BorderFactory.createEmptyBorder( 10, 10, 5, 10 ) );
            center.setLayout( new BorderLayout() );

            JLabel title = new JLabel( layer.getTitle() );
            title.setBorder( BorderFactory.createEmptyBorder( 10, 10, 5, 10 ) );
            add( title, BorderLayout.NORTH );

            final List<ExternalResourceType> extResList = layer.getExternalResources();
            Object[][] data = new Object[extResList.size()][5];
            for ( int i = 0; i < extResList.size(); i++ ) {
                data[i][0] = extResList.get( i ).getExternalResourceTitle();
                data[i][1] = extResList.get( i ).getAbstract();
                data[i][2] = extResList.get( i ).getExternalResourceType();
                data[i][3] = extResList.get( i ).getOnlineResource().getHref();
                data[i][4] = "go to";
            }
            String[] tabHeader = StringTools.toArray( Messages.getMessage( Locale.getDefault(), "$MD10067" ), ",",
                                                      false );
            table = new JTable( new DefaultTableModel( data, tabHeader ) );
            table.getColumn( "go to" ).setCellRenderer( new ButtonRenderer() );
            table.getColumn( "go to" ).setCellEditor( new ButtonEditor( new JCheckBox( "go to" ) ) );
            table.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
            center.add( table.getTableHeader(), BorderLayout.PAGE_START );
            center.add( table, BorderLayout.CENTER );

            JPanel panel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
            JButton addRowBT = new JButton( Messages.getMessage( Locale.getDefault(), "$MD10104" ) );
            addRowBT.setIcon( IconRegistry.getIcon( "add.png" ) );
            addRowBT.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent evt ) {
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    ExternalResourceDialog dg = new ExternalResourceDialog( parent );
                    if ( !dg.isCanceled() ) {
                        String[] values = dg.getValues();
                        model.addRow( values );
                        ExternalResourceType extResType = new ExternalResourceType();
                        extResType.setExternalResourceTitle( values[0] );
                        extResType.setAbstract( values[1] );
                        extResType.setExternalResourceType( values[2] );
                        OnlineResourceType olr = new OnlineResourceType();
                        olr.setHref( values[3] );
                        extResType.setOnlineResource( olr );
                        layer.addExternalResources( extResType );
                    }
                }
            } );
            panel.add( addRowBT );
            JButton remRowBT = new JButton( Messages.getMessage( Locale.getDefault(), "$MD10105" ) );
            remRowBT.setIcon( IconRegistry.getIcon( "remove.png" ) );
            remRowBT.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent evt ) {
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    int idx = table.getSelectedRow();
                    if ( idx >= 0 ) {
                        model.removeRow( idx );
                        extResList.remove( idx );
                    }
                }
            } );
            panel.add( remRowBT );
            center.add( panel, BorderLayout.SOUTH );

            add( center, BorderLayout.CENTER );
        }
    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version. $Revision$, $Date$
     */
    class ButtonRenderer extends JButton implements TableCellRenderer {

        private static final long serialVersionUID = 2403325936293687523L;

        public ButtonRenderer() {
            setOpaque( true );
        }

        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected,
                                                        boolean hasFocus, int row, int column ) {
            if ( isSelected ) {
                setForeground( table.getSelectionForeground() );
                setBackground( table.getSelectionBackground() );
            } else {
                setForeground( table.getForeground() );
                setBackground( UIManager.getColor( "Button.background" ) );
            }
            setText( ( value == null ) ? "" : value.toString() );
            return this;
        }
    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version. $Revision$, $Date$
     */
    class ButtonEditor extends DefaultCellEditor {

        private static final long serialVersionUID = 5171885926340378592L;

        protected JButton button;

        private String label = "go to";

        private boolean isPushed;

        private int row;

        private JTable table;

        /**
         * 
         * @param checkBox
         */
        public ButtonEditor( JCheckBox checkBox ) {
            super( checkBox );
            button = new JButton();
            button.setOpaque( true );
            button.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    fireEditingStopped();
                }
            } );
        }

        @Override
        public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected, int row,
                                                      int column ) {
            if ( isSelected ) {
                button.setForeground( table.getSelectionForeground() );
                button.setBackground( table.getSelectionBackground() );
            } else {
                button.setForeground( table.getForeground() );
                button.setBackground( table.getBackground() );
            }
            label = ( value == null ) ? "" : value.toString();
            button.setText( label );
            isPushed = true;
            this.row = row;
            this.table = table;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if ( isPushed ) {
                String format = table.getModel().getValueAt( row, 2 ).toString();
                String value = table.getModel().getValueAt( row, 3 ).toString();
                if ( "text/html".equalsIgnoreCase( format ) ) {
                    ShowHTML.show( value );
                } else if ( MimeTypeMapper.isKnownImageType( format ) ) {
                    try {
                        ImageViewer.show( value );
                    } catch ( Exception e ) {
                        DialogFactory.openErrorDialog( appCont.getViewPlatform(), ExtResourcesPanel.this,
                                                       Messages.getMessage( getLocale(), "$MD10473" ),
                                                       Messages.getMessage( getLocale(), "$MD10474" ), e );
                    }
                }
            }
            isPushed = false;
            return new String( label );
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
     
    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version. $Revision$, $Date$
     */
    private static class ShowHTML {

        public static void show( String page ) {

            JEditorPane jep = new JEditorPane();
            jep.setEditable( false );

            try {
                jep.setPage( page );
            } catch ( IOException e ) {
                jep.setContentType( "text/html" );
                jep.setText( "<html>Could not load " + page + "</html>" );
            }

            JScrollPane scrollPane = new JScrollPane( jep );
            JFrame f = new JFrame( page );
            f.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
            f.getContentPane().add( scrollPane );
            f.setSize( 512, 342 );
            f.setVisible( true );
            f.toFront();

        }

    }

}
