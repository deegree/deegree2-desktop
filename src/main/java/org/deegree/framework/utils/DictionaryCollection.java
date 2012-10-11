//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2012 by:
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
package org.deegree.framework.utils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.deegree.datatypes.Code;
import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.util.Pair;
import org.deegree.framework.xml.XMLFragment;
import org.xml.sax.SAXException;

/**
 * The <code>DictionaryCollection</code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class DictionaryCollection {

    private List<Dictionary> dicts;

    /**
     * 
     */
    public DictionaryCollection() {
        dicts = new ArrayList<Dictionary>();
    }

    /**
     * 
     * @param urls
     * @throws SAXException
     * @throws IOException
     */
    public DictionaryCollection( URL[] urls ) throws IOException, SAXException {
        dicts = new ArrayList<Dictionary>( urls.length );
        for ( int i = 0; i < urls.length; i++ ) {
            XMLFragment xml = new XMLFragment( urls[i] );
            dicts.add( new Dictionary( xml ) );
        }
    }

    /**
     * adds a {@link Dictionary} to a collection
     * 
     * @param dict
     */
    public void addDictionary( Dictionary dict ) {
        dicts.add( dict );
    }

    /**
     * 
     * @param name
     * @return {@link Dictionary} by name or <code>null</code> if no {@link Dictionary} with passed name is available
     */
    public Dictionary getDictionary( QualifiedName name ) {
        for ( Dictionary dict : dicts ) {
            List<Code> names = dict.getNames();
            for ( Code code : names ) {
                if ( code.getCode().equals( name.getLocalName() )
                     && ( ( code.getCodeSpace() == null && name.getNamespace() == null ) || 
                          ( code.getCodeSpace() != null && code.getCodeSpace().equals( name.getNamespace() ) ) ) ) {
                    return dict;
                }
            }
        }
        return null;
    }

    /**
     * 
     * @return number of available {@link Dictionary}s
     */
    public int getDictionaryCount() {
        return dicts.size();
    }

    /**
     * 
     * @param index
     * @return {@link Dictionary} by index
     */
    public Dictionary getDictionary( int index ) {
        return dicts.get( index );
    }

    /**
     * 
     * @param qn
     *            {@link QualifiedName} a code list is assigned to
     * @param langague
     *            desired code value language
     * @return list of code - value pairs for a {@link QualifiedName}
     */
    public List<Pair<String, String>> getCodelist( QualifiedName qn, String langague ) {
        for ( Dictionary dict : dicts ) {
            List<Pair<String, String>> list = dict.getCodelist( qn, langague );
            if ( list != null && list.size() > 0 ) {
                return list;
            }
        }
        return new ArrayList<Pair<String, String>>();
    }

}
