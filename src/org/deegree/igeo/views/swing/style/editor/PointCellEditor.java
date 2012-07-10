//$HeadURL: svn+ssh://lbuesching@svn.wald.intevation.de/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2010 by:
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
package org.deegree.igeo.views.swing.style.editor;

import java.awt.Component;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.AbstractCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.vecmath.Point2d;

import org.deegree.igeo.i18n.Messages;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class PointCellEditor extends AbstractCellEditor implements TableCellEditor {

    private static final long serialVersionUID = 7469361907501347267L;

    private static final DecimalFormat df = new DecimalFormat( "0.0" );

    private static final NumberFormat nf = NumberFormat.getInstance();

    private JTextField editor = new JTextField();

    private Point2d currentValue;

    public PointCellEditor( Point2d defaultValue ) {
        currentValue = defaultValue;
    }

    public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected, int row, int column ) {
        if ( value instanceof Point2d ) {
            currentValue = (Point2d) value;
            String t = df.format( currentValue.x ) + " " + df.format( currentValue.y );
            editor.setText( t );
        }
        return editor;
    }

    public Object getCellEditorValue() {
        String text = editor.getText().trim();
        if ( text != null && text.length() > 0 ) {
            String[] split = text.split( " " );
            if ( split.length == 2 ) {
                try {
                    Number xN = nf.parse( split[0] );
                    Number yN = nf.parse( split[1] );
                    double x = xN.doubleValue();
                    double y = yN.doubleValue();
                    currentValue = new Point2d( x, y );
                } catch ( Exception e ) {
                    JOptionPane.showMessageDialog( null, Messages.get( "$MD11722" ) );
                }
            } else {
                JOptionPane.showMessageDialog( null, Messages.get( "$MD11722" ) );
            }
        } else {
            JOptionPane.showMessageDialog( null, Messages.get( "$MD11722" ) );
        }
        return currentValue;
    }

}
