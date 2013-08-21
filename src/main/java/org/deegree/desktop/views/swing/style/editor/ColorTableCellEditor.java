/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2007 by:
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

package org.deegree.desktop.views.swing.style.editor;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Locale;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.deegree.desktop.i18n.Messages;

/**
 * <code>ColorTableCellEditor</code>enables editing of colors in a table cell. When user selects a
 * table cell an dialog to choose a color will be opened.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class ColorTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    private static final long serialVersionUID = -1803214152949687781L;

    private JButton colorButton;

    private Color currentColor = Color.WHITE;

    public ColorTableCellEditor() {
        colorButton = new JButton();
        colorButton.setBorderPainted( false );
        colorButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Color color = JColorChooser.showDialog( colorButton, Messages.getMessage( Locale.getDefault(),
                                                                                          "$MD10744" ), currentColor );
                if ( color != null ) {
                    currentColor = color;
                }
                fireEditingStopped();
            }
        } );
    }

    public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected, int row, int column ) {
        currentColor = (Color) value;
        return colorButton;
    }

    public Object getCellEditorValue() {
        return currentColor;
    }

    @Override
    public boolean isCellEditable( EventObject e ) {
        if ( e instanceof MouseEvent )
            return ( (MouseEvent) e ).getClickCount() > 1;
        return true;
    }

}
