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

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.DefaultFormatter;

import org.deegree.datatypes.QualifiedName;

/**
 * <code>SpinnerTableCellEditor</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class SpinnerTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    private static final long serialVersionUID = -6032472167650524681L;

    private JSpinner spinner;

    public SpinnerTableCellEditor( int value, int min, int max, int step ) {
        this( new SpinnerNumberModel( value, min, max, step ) );
    }

    public SpinnerTableCellEditor( double value, double min, double max, double step ) {
        this( new SpinnerNumberModel( value, min, max, step ) );
    }

    public SpinnerTableCellEditor( SpinnerNumberModel spinnerModel ) {
        spinner = new JSpinner( spinnerModel );
        JComponent comp = spinner.getEditor();
        JFormattedTextField field = (JFormattedTextField) comp.getComponent( 0 );
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit( true );
    }

    public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected, int row, int column ) {
        if ( value instanceof QualifiedName ) {
            return null;
        }
        spinner.setValue( value );
        return spinner;
    }

    public Object getCellEditorValue() {
        return spinner.getValue();
    }

    @Override
    public boolean isCellEditable( EventObject e ) {
        if ( e instanceof MouseEvent )
            return ( (MouseEvent) e ).getClickCount() > 1;
        return true;
    }

}
