//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 and
 lat/lon GmbH

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

package org.deegree.desktop.views.swing.style.component.classification;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.style.model.classification.ClassificationTableRow;
import org.deegree.desktop.views.swing.util.panels.PanelDialog;

/**
 * <code>TableMouseListener</code> mouse listener to listen on mouse events on the classification table; shows a context
 * menu to edit the label of the row.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class TableMouseListener extends MouseAdapter {

    private JPopupMenu popupMenu;

    private JTable table;

    // the index of the row, which is currently selected
    private int selectedRow;

    /**
     * @param classesTable
     *            the table representing the classification
     */
    public TableMouseListener( JTable classesTable ) {
        table = classesTable;
    }

    @Override
    public void mousePressed( MouseEvent e ) {
        maybeShowPopup( e );
    }

    @Override
    public void mouseReleased( MouseEvent e ) {
        maybeShowPopup( e );
    }

    private void maybeShowPopup( MouseEvent e ) {
        // open context menu
        if ( e.isPopupTrigger() ) {
            if ( popupMenu == null ) {
                popupMenu = new JPopupMenu();
                // popup entry to edit the label of the selected row
                JMenuItem editLabel = new JMenuItem( Messages.get( "$MD11071" ) );
                editLabel.addActionListener( new PopupActionListener() );
                popupMenu.add( editLabel );
            }

            selectedRow = table.rowAtPoint( e.getPoint() );
            if ( selectedRow > -1 ) {
                // select the row
                table.setRowSelectionInterval( selectedRow, selectedRow );
                popupMenu.show( e.getComponent(), e.getX(), e.getY() );
            }
        }
    }

    // //////////////////////////////////////////////////////////////////////////////
    // INNER CLASS
    // //////////////////////////////////////////////////////////////////////////////

    private class PopupActionListener implements ActionListener {
        public void actionPerformed( ActionEvent e1 ) {
            // open dialog, to enter a label or the selected row
            ClassificationTableModel<?> model = (ClassificationTableModel<?>) table.getModel();
            ClassificationTableRow<?> row = model.getRowAt( selectedRow );
            if ( row != null ) {
                JTextField editField = new JTextField();
                editField.setText( row.getLabel() );
                PanelDialog editLabelDlg = new PanelDialog( editField, true );
                editLabelDlg.setTitle( Messages.get( "$MD11072" ) );
                editLabelDlg.setLocation( table.getTableHeader().getLocationOnScreen() );
                editLabelDlg.setVisible( true );
                if ( editLabelDlg.clickedOk ) {
                    row.setLabel( editField.getText() );
                }
            }
        }
    }

}
