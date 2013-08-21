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

package org.deegree.desktop.views.swing.style;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * <code>SingleItemDisableComboBox</code> represents a combo box, which can contain disabled items
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class SingleItemDisableComboBox extends JComboBox {

    private static final long serialVersionUID = -6406692699610993552L;

    /**
     * @param items
     *            list of items to represent in the combo box
     */
    public SingleItemDisableComboBox( List<SingleItem> items ) {
        setModel( new SingleItemComboBoxModel( items ) );
        setRenderer( new SingleItemRenderer() );
    }

    @Override
    public void insertItemAt( Object anObject, int index ) {
        SingleItem singleItemToInsert;
        if ( anObject instanceof SingleItem ) {
            singleItemToInsert = (SingleItem) anObject;
        } else {
            singleItemToInsert = new SingleItem( anObject, true );
        }
        super.insertItemAt( singleItemToInsert, index );
    }

    @Override
    public void addItem( Object anObject ) {
        SingleItem singleItemToInsert;
        if ( anObject instanceof SingleItem ) {
            singleItemToInsert = (SingleItem) anObject;
        } else {
            singleItemToInsert = new SingleItem( anObject, true );
        }
        super.addItem( singleItemToInsert );
    }

    // //////////////////////////////////////////////////////////////////////////////
    // INNER CLASSES
    // //////////////////////////////////////////////////////////////////////////////
    private class SingleItemRenderer extends JLabel implements ListCellRenderer {

        private static final long serialVersionUID = -4777623881537342087L;

        private SingleItemRenderer() {
            setPreferredSize( new Dimension( 150, 20 ) );
            setBorder( BorderFactory.createEmptyBorder( 0, 2, 0, 0 ) );
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int,
         * boolean, boolean)
         */
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected,
                                                       boolean cellHasFocus ) {
            SingleItem v = (SingleItem) value;
            Font f = getFont();
            if ( v.isEnabled() ) {
                setFont( new Font( "SansSerif", Font.PLAIN, f.getSize() ) );
                setForeground( Color.BLACK );
            } else {
                setFont( new Font( "SansSerif", Font.ITALIC, f.getSize() ) );
                setForeground( Color.LIGHT_GRAY );
            }
            setText( v.getItem().toString() );
            return this;
        }
    }

    private class SingleItemComboBoxModel extends DefaultComboBoxModel {

        private static final long serialVersionUID = 8637101817815252261L;

        private SingleItemComboBoxModel( List<SingleItem> items ) {
            super( (SingleItem[]) items.toArray( new SingleItem[items.size()] ) );
        }

        @Override
        public void setSelectedItem( Object anObject ) {
            Object itemToSelect = getSelectedItem();
            if ( anObject instanceof SingleItem && ( (SingleItem) anObject ).isEnabled() ) {
                itemToSelect = anObject;
            }
            super.setSelectedItem( itemToSelect );
        }
    }
}
