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

import java.util.Arrays;
import java.util.List;
import javax.swing.JTextField;
import javax.swing.text.*;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ComboboxTextField extends JTextField {

    private static final long serialVersionUID = 9061181066044572705L;

    private List<String> itemList;

    private AutoCompleteComboBox comboBox;

    private boolean isCaseSensitive;

    private boolean isStrictMode;

    /**
     * 
     * @param list
     */
    ComboboxTextField( String[] list ) {
        this( Arrays.asList( list ) );
    }

    /**
     * 
     * @param list
     */
    ComboboxTextField( List<String> list ) {
        isCaseSensitive = false;
        isStrictMode = true;
        comboBox = null;
        if ( list == null ) {
            throw new IllegalArgumentException( "values can not be null" );
        } else {
            itemList = list;
            initDocument();
            return;
        }
    }

    ComboboxTextField( List<String> list, AutoCompleteComboBox b ) {
        isCaseSensitive = false;
        isStrictMode = true;
        comboBox = null;
        if ( list == null ) {
            throw new IllegalArgumentException( "values can not be null" );
        } else {
            itemList = list;
            comboBox = b;
            initDocument();
            return;
        }
    }

    private void initDocument() {
        setDocument( new TFDocument() );
        if ( isStrictMode && itemList.size() > 0 )
            setText( itemList.get( 0 ).toString() );
    }

    private String getMatchingItem( String s ) {
        for ( int i = 0; i < itemList.size(); i++ ) {
            String s1 = itemList.get( i ).toString();
            if ( s1 != null ) {
                if ( !isCaseSensitive && s1.toLowerCase().startsWith( s.toLowerCase() ) )
                    return s1;
                if ( isCaseSensitive && s1.startsWith( s ) )
                    return s1;
            }
        }

        return null;
    }

    @Override
    public void replaceSelection( String s ) {
        TFDocument _lb = (TFDocument) getDocument();
        if ( _lb != null )
            try {
                int i = Math.min( getCaret().getDot(), getCaret().getMark() );
                int j = Math.max( getCaret().getDot(), getCaret().getMark() );
                _lb.replace( i, j - i, s, null );
            } catch ( Exception exception ) {
            }
    }

    boolean isCaseSensitive() {
        return isCaseSensitive;
    }

    void setCaseSensitive( boolean flag ) {
        isCaseSensitive = flag;
    }

    boolean isStrict() {
        return isStrictMode;
    }

    void setStrict( boolean flag ) {
        isStrictMode = flag;
    }

    List<String> getDataList() {
        return itemList;
    }

    void setDataList( List<String> list ) {
        if ( list == null ) {
            throw new IllegalArgumentException( "values can not be null" );
        } else {
            itemList = list;
            return;
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // inner classes
    // /////////////////////////////////////////////////////////////////////////////////////

    /**
     * 
     * TODO add class documentation here
     * 
     * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    class TFDocument extends PlainDocument {

        private static final long serialVersionUID = 2276955569199309661L;

        @Override
        public void replace( int i, int j, String s, AttributeSet attributeset )
                                throws BadLocationException {
            super.remove( i, j );
            insertString( i, s, attributeset );
        }

        @Override
        public void insertString( int i, String s, AttributeSet attributeset )
                                throws BadLocationException {
            if ( s == null || "".equals( s ) )
                return;
            String s1 = getText( 0, i );
            String s2 = getMatchingItem( s1 + s );
            int j = ( i + s.length() ) - 1;
            if ( isStrictMode && s2 == null ) {
                s2 = getMatchingItem( s1 );
                j--;
            } else if ( !isStrictMode && s2 == null ) {
                super.insertString( i, s, attributeset );
                return;
            }
            if ( comboBox != null && s2 != null )
                comboBox.setSelectedValue( s2 );
            super.remove( 0, getLength() );
            super.insertString( 0, s2, attributeset );
            setSelectionStart( j + 1 );
            setSelectionEnd( getLength() );
        }

        public void remove( int i, int j )
                                throws BadLocationException {
            int k = getSelectionStart();
            if ( k > 0 )
                k--;
            String s = getMatchingItem( getText( 0, k ) );
            if ( !isStrictMode && s == null ) {
                super.remove( i, j );
            } else {
                super.remove( 0, getLength() );
                super.insertString( 0, s, null );
            }
            if ( comboBox != null && s != null )
                comboBox.setSelectedValue( s );
            try {
                setSelectionStart( k );
                setSelectionEnd( getLength() );
            } catch ( Exception exception ) {
            }
        }

    }
}
