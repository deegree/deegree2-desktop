//$HeadURL$
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
package org.deegree.igeo.views.swing.style.renderer;

import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.vecmath.Point2d;

import org.deegree.framework.util.Pair;
import org.deegree.igeo.style.model.SldProperty;
import org.deegree.model.filterencoding.PropertyName;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ClassificationTCRenderer extends JLabel implements TableCellRenderer {

    private static final long serialVersionUID = 6266497302756084890L;

    private Border unselectedBorder = null;

    private Border selectedBorder = null;

    private static final DecimalFormat df = new DecimalFormat( "0.0" );

    @SuppressWarnings("unchecked")
    public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                    int row, int column ) {
        if ( isSelected ) {
            if ( selectedBorder == null ) {
                selectedBorder = BorderFactory.createMatteBorder( 2, 5, 2, 5, table.getSelectionBackground() );
            }
            setBorder( selectedBorder );
        } else {
            if ( unselectedBorder == null ) {
                unselectedBorder = BorderFactory.createMatteBorder( 2, 5, 2, 5, table.getBackground() );
            }
            setBorder( unselectedBorder );
        }
        if ( value instanceof PropertyName ) {
            PropertyName pn = (PropertyName) value;
            String s = pn.toString();
            if ( s.contains( ":" ) ) {
                s = s.substring( s.indexOf( ':' ) + 1 );
            }
            setText( s );
        } else if ( value instanceof Integer ) {
            setText( "" + (Integer) value );
        } else if ( value instanceof Double ) {
            setText( "" + (Double) value );
        } else if ( value instanceof SldProperty ) {
            SldProperty sldProperty = (SldProperty) value;
            setText( sldProperty.getName() );
        } else if ( value instanceof String ) {
            setText( (String) value );
        } else if ( value instanceof Pair<?, ?> && ( (Pair<?, ?>) value ).first instanceof PropertyName
                    && ( (Pair<?, ?>) value ).second instanceof PropertyName ) {
            Pair<PropertyName, PropertyName> p = (Pair<PropertyName, PropertyName>) value;
            String x = p.first.toString();
            if ( x.contains( ":" ) ) {
                x = x.substring( x.indexOf( ':' ) + 1 );
            }
            String y = p.second.toString();
            if ( y.contains( ":" ) ) {
                y = y.substring( y.indexOf( ':' ) + 1 );
            }
            setText( x + " " + y );
        } else if ( value instanceof Point2d ) {
            Point2d point2d = (Point2d) value;
            setText( df.format( point2d.x ) + " " + df.format( point2d.y ) );
        } else {
            setText( value.toString() );
        }
        return this;
    }
}
