//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
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
package org.deegree.desktop.views.swing.util.table;

import java.awt.Component;
import java.awt.Insets;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author <a href="http://www.esus.com/docs/GetQuestionPage.jsp?uid=1270&type=pf">Nobuo Tamemasa</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class SortButtonRenderer extends JButton implements TableCellRenderer {

    private static final long serialVersionUID = 759027662360387685L;

    public static final int NONE = 0;

    public static final int DOWN = 1;

    public static final int UP = 2;

    int pushedColumn;

    Hashtable<Integer, Object> state;

    JButton downButton, upButton;

    /**
     * 
     */
    public SortButtonRenderer() {
        pushedColumn = -1;
        state = new Hashtable<Integer, Object>();

        setMargin( new Insets( 0, 0, 0, 0 ) );
        setHorizontalTextPosition( LEFT );
        setIcon( new BlankIcon() );

        // perplexed
        // ArrowIcon(SwingConstants.SOUTH, true)
        // BevelArrowIcon (int direction, boolean isRaisedView, boolean isPressedView)

        downButton = new JButton();
        downButton.setMargin( new Insets( 0, 0, 0, 0 ) );
        downButton.setHorizontalTextPosition( LEFT );
        downButton.setIcon( new BevelArrowIcon( BevelArrowIcon.DOWN, false, false ) );
        downButton.setPressedIcon( new BevelArrowIcon( BevelArrowIcon.DOWN, false, true ) );

        upButton = new JButton();
        upButton.setMargin( new Insets( 0, 0, 0, 0 ) );
        upButton.setHorizontalTextPosition( LEFT );
        upButton.setIcon( new BevelArrowIcon( BevelArrowIcon.UP, false, false ) );
        upButton.setPressedIcon( new BevelArrowIcon( BevelArrowIcon.UP, false, true ) );

    }

    public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                    int row, int column ) {
        JButton button = this;
        Object obj = state.get( new Integer( column ) );
        if ( obj != null ) {
            if ( ( (Integer) obj ).intValue() == DOWN ) {
                button = downButton;
            } else {
                button = upButton;
            }
        }
        button.setText( ( value == null ) ? "" : value.toString() );
        boolean isPressed = ( column == pushedColumn );
        button.getModel().setPressed( isPressed );
        button.getModel().setArmed( isPressed );
        return button;
    }

    public void setPressedColumn( int col ) {
        pushedColumn = col;
    }

    public void setSelectedColumn( int col ) {
        if ( col < 0 )
            return;
        Integer value = null;
        Object obj = state.get( new Integer( col ) );
        if ( obj == null ) {
            value = new Integer( DOWN );
        } else {
            if ( ( (Integer) obj ).intValue() == DOWN ) {
                value = new Integer( UP );
            } else {
                value = new Integer( DOWN );
            }
        }
        state.clear();
        state.put( new Integer( col ), value );
    }

    public int getState( int col ) {
        int retValue;
        Object obj = state.get( new Integer( col ) );
        if ( obj == null ) {
            retValue = NONE;
        } else {
            if ( ( (Integer) obj ).intValue() == DOWN ) {
                retValue = DOWN;
            } else {
                retValue = UP;
            }
        }
        return retValue;
    }
}
