//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2008 by:
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

package org.deegree.framework.keyboard;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class Key2Code {

    private static Map<String, Integer> keyMap = new HashMap<String, Integer>( 250 );

    private static Map<String, Integer> maskMap = new HashMap<String, Integer>();
    static {
        keyMap.put( "cancel", 3 );
        keyMap.put( "backspace", 8 );
        keyMap.put( "tab", 9 );
        keyMap.put( "enter", 10 );
        keyMap.put( "clear", 12 );
        keyMap.put( "shift", 16 );
        keyMap.put( "ctrl", 17 );
        keyMap.put( "alt", 18 );
        keyMap.put( "pause", 19 );
        keyMap.put( "caps lock", 20 );
        keyMap.put( "kana", 21 );
        keyMap.put( "final", 24 );
        keyMap.put( "kanji", 25 );
        keyMap.put( "escape", 27 );
        keyMap.put( "convert", 28 );
        keyMap.put( "no convert", 29 );
        keyMap.put( "accept", 30 );
        keyMap.put( "mode change", 31 );
        keyMap.put( "space", 32 );
        keyMap.put( "page up", 33 );
        keyMap.put( "page down", 34 );
        keyMap.put( "end", 35 );
        keyMap.put( "home", 36 );
        keyMap.put( "left", 37 );
        keyMap.put( "up", 38 );
        keyMap.put( "right", 39 );
        keyMap.put( "down", 40 );
        keyMap.put( "comma", 44 );
        keyMap.put( "minus", 45 );
        keyMap.put( "period", 46 );
        keyMap.put( "slash", 47 );
        keyMap.put( "0", 48 );
        keyMap.put( "1", 49 );
        keyMap.put( "2", 50 );
        keyMap.put( "3", 51 );
        keyMap.put( "4", 52 );
        keyMap.put( "5", 53 );
        keyMap.put( "6", 54 );
        keyMap.put( "7", 55 );
        keyMap.put( "8", 56 );
        keyMap.put( "9", 57 );
        keyMap.put( "semicolon", 59 );
        keyMap.put( "equals", 61 );
        keyMap.put( "a", 65 );
        keyMap.put( "b", 66 );
        keyMap.put( "c", 67 );
        keyMap.put( "d", 68 );
        keyMap.put( "e", 69 );
        keyMap.put( "f", 70 );
        keyMap.put( "g", 71 );
        keyMap.put( "h", 72 );
        keyMap.put( "i", 73 );
        keyMap.put( "j", 74 );
        keyMap.put( "k", 75 );
        keyMap.put( "l", 76 );
        keyMap.put( "m", 77 );
        keyMap.put( "n", 78 );
        keyMap.put( "o", 79 );
        keyMap.put( "p", 80 );
        keyMap.put( "q", 81 );
        keyMap.put( "r", 82 );
        keyMap.put( "s", 83 );
        keyMap.put( "t", 84 );
        keyMap.put( "u", 85 );
        keyMap.put( "v", 86 );
        keyMap.put( "w", 87 );
        keyMap.put( "x", 88 );
        keyMap.put( "y", 89 );
        keyMap.put( "z", 90 );
        keyMap.put( "open bracket", 91 );
        keyMap.put( "back slash", 92 );
        keyMap.put( "close bracket", 93 );
        keyMap.put( "numpad-0", 96 );
        keyMap.put( "numpad-1", 97 );
        keyMap.put( "numpad-2", 98 );
        keyMap.put( "numpad-3", 99 );
        keyMap.put( "numpad-4", 100 );
        keyMap.put( "numpad-5", 101 );
        keyMap.put( "numpad-6", 102 );
        keyMap.put( "numpad-7", 103 );
        keyMap.put( "numpad-8", 104 );
        keyMap.put( "numpad-9", 105 );
        keyMap.put( "numpad *", 106 );
        keyMap.put( "numpad +", 107 );
        keyMap.put( "numpad ,", 108 );
        keyMap.put( "numpad -", 109 );
        keyMap.put( "numpad .", 110 );
        keyMap.put( "numpad /", 111 );
        keyMap.put( "f1", 112 );
        keyMap.put( "f2", 113 );
        keyMap.put( "f3", 114 );
        keyMap.put( "f4", 115 );
        keyMap.put( "f5", 116 );
        keyMap.put( "f6", 117 );
        keyMap.put( "f7", 118 );
        keyMap.put( "f8", 119 );
        keyMap.put( "f9", 120 );
        keyMap.put( "f10", 121 );
        keyMap.put( "f11", 122 );
        keyMap.put( "f12", 123 );
        keyMap.put( "delete", 127 );
        keyMap.put( "dead grave", 128 );
        keyMap.put( "dead acute", 129 );
        keyMap.put( "dead circumflex", 130 );
        keyMap.put( "dead tilde", 131 );
        keyMap.put( "dead macron", 132 );
        keyMap.put( "dead breve", 133 );
        keyMap.put( "dead above dot", 134 );
        keyMap.put( "dead diaeresis", 135 );
        keyMap.put( "dead above ring", 136 );
        keyMap.put( "dead double acute", 137 );
        keyMap.put( "dead caron", 138 );
        keyMap.put( "dead cedilla", 139 );
        keyMap.put( "dead ogonek", 140 );
        keyMap.put( "dead iota", 141 );
        keyMap.put( "dead voiced sound", 142 );
        keyMap.put( "dead semivoiced sound", 143 );
        keyMap.put( "num lock", 144 );
        keyMap.put( "scroll lock", 145 );
        keyMap.put( "ampersand", 150 );
        keyMap.put( "asterisk", 151 );
        keyMap.put( "double quote", 152 );
        keyMap.put( "less", 153 );
        keyMap.put( "print screen", 154 );
        keyMap.put( "insert", 155 );
        keyMap.put( "help", 156 );
        keyMap.put( "meta", 157 );
        keyMap.put( "greater", 160 );
        keyMap.put( "left brace", 161 );
        keyMap.put( "right brace", 162 );
        keyMap.put( "back quote", 192 );
        keyMap.put( "quote", 222 );
        keyMap.put( "up", 224 );
        keyMap.put( "down", 225 );
        keyMap.put( "left", 226 );
        keyMap.put( "right", 227 );
        keyMap.put( "alphanumeric", 240 );
        keyMap.put( "katakana", 241 );
        keyMap.put( "hiragana", 242 );
        keyMap.put( "full-width", 243 );
        keyMap.put( "half-width", 244 );
        keyMap.put( "roman characters", 245 );

        maskMap.put( "alt", ActionEvent.ALT_MASK );
        maskMap.put( "ctrl", ActionEvent.CTRL_MASK );
        maskMap.put( "meta", ActionEvent.META_MASK );
        maskMap.put( "shift", ActionEvent.SHIFT_MASK );

    }

    /**
     * 
     * @param name
     *            english keyname (not case sensitive)
     * @return code for passed key name; -1 if name is unknown
     */
    public static final int getKeyCode( String name ) {
        Integer tmp = keyMap.get( name.toLowerCase() );
        if ( tmp == null ) {
            return -1;
        } else {
            return tmp;
        }
    }

    /**
     * lists all known keys
     * 
     */
    public static final void listKeyNames() {
        Iterator<String> iterator = keyMap.keySet().iterator();
        while ( iterator.hasNext() ) {
            System.out.println( iterator.next() );
        }
    }

    /**
     * 
     * @param name
     *            english keyname (not case sensitive)
     * @return code for passed key name; -1 if name is unknown
     */
    public static final int getMaskCode( String name ) {
        Integer tmp = maskMap.get( name.toLowerCase() );
        if ( tmp == null ) {
            return -1;
        } else {
            return tmp;
        }
    }

    /**
     * lists all known keys
     * 
     */
    public static final void listMaskNames() {
        Iterator<String> iterator = maskMap.keySet().iterator();
        while ( iterator.hasNext() ) {
            System.out.println( iterator.next() );
        }
    }

}
