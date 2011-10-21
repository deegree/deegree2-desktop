//$HeadURL$
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

package org.deegree.igeo.views.swing.util;

import static java.lang.Character.isDigit;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static java.lang.Integer.parseInt;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * <code>JIntField</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class JIntField extends JTextField {

    private static final long serialVersionUID = -1934109955150226835L;

    int min;

    int max;

    /**
     * With min/max == Integer.MIN/MAX values.
     */
    public JIntField() {
        this( MIN_VALUE, MAX_VALUE );
    }

    /**
     * @param val
     */
    public JIntField( int val ) {
        this( MIN_VALUE, MAX_VALUE, val );
    }

    /**
     * @param min
     * @param max
     */
    public JIntField( int min, int max ) {
        this( min, max, 0 );
    }

    /**
     * @param min
     * @param max
     * @param val
     */
    public JIntField( int min, int max, int val ) {
        this.min = min;
        this.max = max;
        setDocument( new IntDocument() );
        setInt( val );
    }

    /**
     * @return the current value
     */
    public int getInt() {
        try {
            return parseInt( getText() );
        } catch ( NumberFormatException nfe ) {
            return 0;
        }
    }

    /**
     * @param val
     */
    public void setInt( int val ) {
        setText( val + "" );
    }

    /**
     * <code>IntDocument</code>
     * 
     * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    private class IntDocument extends PlainDocument {

        private static final long serialVersionUID = 6018107157866940366L;

        IntDocument() {
            // reducing warnings
        }

        @Override
        public void insertString( int offs, String s, AttributeSet a )
                                throws BadLocationException {
            int i = 0;
            char[] res = new char[s.length()];

            for ( char c : s.toCharArray() ) {
                if ( isDigit( c ) ) {
                    res[i++] = c;
                }
            }

            super.insertString( offs, new String( res, 0, i ), a );
            if ( getInt() > max ) {
                setInt( max );
            }
            if ( getInt() < min ) {
                setInt( min );
            }
        }

    }

}
