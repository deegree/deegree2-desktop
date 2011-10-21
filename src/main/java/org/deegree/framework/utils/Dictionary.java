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
package org.deegree.framework.utils;

import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.deegree.datatypes.Code;
import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.Pair;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class Dictionary {

    private static final ILogger LOG = LoggerFactory.getLogger( Dictionary.class );

    private static final NamespaceContext nsc = CommonNamespaces.getNamespaceContext();

    private XMLFragment xml;

    private String description;

    private List<Code> names;

    private boolean cached = true;

    private static Map<QualifiedName, List<Pair<String, String>>> codeLists;
    static {
        if ( codeLists == null ) {
            codeLists = new HashMap<QualifiedName, List<Pair<String, String>>>();
        }
    }

    /**
     * 
     * @param xml
     */
    public Dictionary( XMLFragment xml ) {
        this.xml = xml;
    }

    /**
     * 
     * @param xml
     * @param cached
     */
    public Dictionary( XMLFragment xml, boolean cached ) {
        this.xml = xml;
        this.cached = cached;
    }

    /**
     * 
     * @return some description of a dictionary
     */
    public String getDescription() {
        if ( description == null || !cached ) {
            try {
                description = XMLTools.getNodeAsString( xml.getRootElement(), "gml:description", nsc, null );
            } catch ( XMLParsingException e ) {
                LOG.logError( e );
            }
        }
        return description;
    }

    /**
     * Each dictionary must have at least one name. and each name may have a codeSpace
     * 
     * @return name(s) of a dictionary.
     */
    public List<Code> getNames() {
        if ( names == null || !cached ) {
            names = new ArrayList<Code>();
            try {
                List<Node> nodes = XMLTools.getNodes( xml.getRootElement(), "gml:name", nsc );
                for ( Node node : nodes ) {
                    Element elem = (Element) node;
                    String name = XMLTools.getStringValue( elem );
                    String codeSpace = elem.getAttribute( "codeSpace" );
                    if ( codeSpace == null ) {
                        names.add( new Code( name ) );
                    } else {
                        names.add( new Code( name, URI.create( codeSpace ) ) );
                    }
                }
            } catch ( XMLParsingException e ) {
                LOG.logError( e );
            }

        }
        return names;
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
        List<Pair<String, String>> list = codeLists.get( qn );
        if ( list == null ) {
            Node tmp = null;
            try {
                tmp = XMLTools.getNode( xml.getRootElement(), "gml:indirectEntry", nsc );
            } catch ( XMLParsingException e1 ) {
                e1.printStackTrace();
            }
            if ( tmp != null ) {
                //read dictionary from a remote location
                list = handleIndirect( tmp, qn, langague );
            } else {
                list = readCodeList( xml.getRootElement(), qn, langague );
            }
        }
        return list;
    }

    /**
     * 
     * @param root
     * @param qn
     * @param language
     * @return @return list of code mappings for passed name and desired language
     */
    private List<Pair<String, String>> readCodeList( Element root, QualifiedName qn, String language ) {
        if ( language == null ) {
           language = Locale.getDefault().getLanguage();
        }
        List<Pair<String, String>> list = new ArrayList<Pair<String, String>>( 50 );
        String c = "[gml:csName/@codeSpace = '" + qn.getNamespace().toASCIIString() + "' and gml:csName = '"
                   + qn.getLocalName() + "']";
        String xpath = "//gml:DefinitionCollection" + c + "/gml:dictionaryEntry/gml:Definition";
        try {
            List<Node> nodes = XMLTools.getNodes( root, xpath, nsc );
            if ( nodes == null || nodes.size() == 0 ) {
                c = "[gml:name/@codeSpace = '" + qn.getNamespace().toASCIIString() + "' and gml:name = '"
                    + qn.getLocalName() + "']";
                xpath = "//gml:DefinitionCollection" + c + "/gml:dictionaryEntry/gml:Definition";
                nodes = XMLTools.getNodes( root, xpath, nsc );
            }
            for ( Node node : nodes ) {
                Element elem = (Element) node;
                List<Node> names = XMLTools.getNodes( elem, "gml:name", nsc );
                Pair<String, String> pair = new Pair<String, String>();
                String en = null;
                String deflt = null;
                for ( Node node2 : names ) {
                    Element name = (Element) node2;
                    String cs = name.getAttribute( "codeSpace" );
                    if ( "urn:org:deegree:igeodesktop:code".equals( cs ) ) {
                        // is machine readable code?
                        pair.first = XMLTools.getStringValue( name );
                    } else if ( ( "urn:org:deegree:igeodesktop:" + language ).equals( cs ) ) {
                        // is human readable value in desired language
                        pair.second = XMLTools.getStringValue( name );
                    }
                    if ( pair.second == null ) {
                        if ( "urn:org:deegree:igeodesktop:en".equals( cs ) ) {
                            // is human readable value in english language
                            en = pair.second = XMLTools.getStringValue( name );
                        } else if ( cs == null || cs.length() == 0 ) {
                            // is human readable value without language code
                            deflt = XMLTools.getStringValue( name );
                        }
                    }
                }
                if ( pair.second == null && deflt != null ) {
                    pair.second = deflt;
                }
                if ( pair.second == null && en != null ) {
                    pair.second = en;
                }
                if ( pair.second == null ) {
                    pair.second = pair.first;
                }
                list.add( pair );
            }
            if ( cached ) {
                codeLists.put( qn, list );
            }
        } catch ( XMLParsingException e ) {
            LOG.logError( e );
        }
        return list;
    }

    /**
     * read dictionary from a remote location
     * 
     * @param tmp
     * @param qn
     * @param langague
     * @return list of code mappings for passed name and desired language
     */
    private List<Pair<String, String>> handleIndirect( Node tmp, QualifiedName qn, String langague ) {
        List<Pair<String, String>> list = new ArrayList<Pair<String, String>>( 50 );
        try {
            String xpath = "gml:DefinitionProxy/gml:definitionRef/@xlink:href";
            String url = XMLTools.getRequiredNodeAsString( tmp, xpath, nsc );
            String s = "name=" + URLEncoder.encode( qn.getLocalName(), Charset.defaultCharset().displayName() )
                       + "&codespace="
                       + URLEncoder.encode( qn.getNamespace().toASCIIString(), Charset.defaultCharset().displayName() );
            URL u = new URL( url + "?" + s );
            XMLFragment tXml = new XMLFragment( u );
            list = readCodeList( tXml.getRootElement(), qn, langague );
        } catch ( Exception e ) {
            LOG.logError( e );
        }
        return list;
    }
}
