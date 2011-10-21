/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2010 by:
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

package org.deegree.igeo.views.swing;

import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.plaf.basic.BasicComboBoxEditor;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class AutoCompleteComboBox extends JComboBox {

    private static final long serialVersionUID = 5895634176041076893L;

    private boolean isFired;

    /**
     * 
     * @param list
     */
    public AutoCompleteComboBox( String[] list ) {
        this( Arrays.asList( list ) );
    }

    /**
     * 
     * @param list
     */
    public AutoCompleteComboBox( List<String> list ) {
        isFired = false;
        AutoCompleteTextFieldEditor aCTFE = new AutoCompleteTextFieldEditor( list );
        setEditable( true );
        setModel( new DefaultComboBoxModel( list.toArray() ) {

            private static final long serialVersionUID = -5408757632805950099L;

            protected void fireContentsChanged( Object obj, int i, int j ) {
                if ( !isFired )
                    super.fireContentsChanged( obj, i, j );
            }

        } );
        setEditor( aCTFE );
    }

    void setSelectedValue( Object obj ) {
        if ( isFired ) {
            return;
        } else {
            isFired = true;
            setSelectedItem( obj );
            fireItemStateChanged( new ItemEvent( this, 701, selectedItemReminder, 1 ) );
            isFired = false;
            return;
        }
    }

    @Override
    protected void fireActionEvent() {
        if ( !isFired ) {
            super.fireActionEvent();
        }
    }

    // ///////////////////////////////////////////////////////////////////////////////////////
    // inner classes
    // ///////////////////////////////////////////////////////////////////////////////////////
    /**
     * 
     * TODO add class documentation here
     * 
     * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    private class AutoCompleteTextFieldEditor extends BasicComboBoxEditor {

        /**
         * 
         * @param list
         */
        AutoCompleteTextFieldEditor( List<String> list ) {
            editor = new ComboboxTextField( list, AutoCompleteComboBox.this );
        }
    }
}