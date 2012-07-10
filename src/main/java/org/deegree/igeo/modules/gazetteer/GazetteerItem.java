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
package org.deegree.igeo.modules.gazetteer;

import org.deegree.framework.util.StringPair;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.Point;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class GazetteerItem implements Comparable<Object> {

    private String gmlID;

    private String geographicIdentifier;

    private String parentIdentifier;

    private String alternativeGeographicIdentifier;

    private String displayName;

    private Geometry geographicExtent;

    private Point position;

    private Geometry highlightGeometry;

    private Integer identPart1;

    private String identPart2;

    /**
     * 
     * @param gmlID
     * @param geographicIdentifier
     * @param parentIdentifier
     * @param alternativeGeographicIdentifier
     * @param geographicExtent
     * @param position
     * @param displayName
     * @param highlightGeometry
     */
    public GazetteerItem( String gmlID, String geographicIdentifier, String parentIdentifier,
                          String alternativeGeographicIdentifier, Geometry geographicExtent, Point position,
                          String displayName, Geometry highlightGeometry ) {
        this.gmlID = gmlID;
        this.geographicIdentifier = geographicIdentifier;
        this.parentIdentifier = parentIdentifier;
        this.alternativeGeographicIdentifier = alternativeGeographicIdentifier;
        this.geographicExtent = geographicExtent;
        this.position = position;
        this.displayName = displayName;
        this.highlightGeometry = highlightGeometry;
        StringPair p = splitGeographicIdentifier( geographicIdentifier );
        try {
            this.identPart1 = Integer.parseInt( p.first );
            this.identPart2 = p.second;
        } catch ( Throwable e ) {
            identPart1 = null;
            identPart2 = null;
        }
    }

    /**
     * Splits a string: numberstring -> number, string
     * 
     * @param geographicIdentifier
     * @return a pair of string, with empty strings where applicable (never null)
     */
    public static StringPair splitGeographicIdentifier( String geographicIdentifier ) {
        geographicIdentifier = geographicIdentifier.trim();
        StringBuilder sb = new StringBuilder();
        for ( int i = 0; i < geographicIdentifier.length(); ++i ) {
            if ( !Character.isDigit( geographicIdentifier.charAt( i ) ) ) {
                return new StringPair( sb.toString().trim(), geographicIdentifier.substring( i ).trim() );
            }
            sb.append( geographicIdentifier.charAt( i ) );
        }
        return new StringPair( sb.toString().trim(), "" );
    }

    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return the highlightGeometry
     */
    public Geometry getHighlightGeometry() {
        return highlightGeometry;
    }

    /**
     * @return the gmlID
     */
    public String getGmlID() {
        return gmlID;
    }

    /**
     * @return the geographicIdentifier
     */
    public String getGeographicIdentifier() {
        return geographicIdentifier;
    }

    /**
     * @return the parentIdentifier
     */
    public String getParentIdentifier() {
        return parentIdentifier;
    }

    /**
     * @return the alternativeGeographicIdentifier
     */
    public String getAlternativeGeographicIdentifier() {
        return alternativeGeographicIdentifier;
    }

    /**
     * @return the geographicExtent
     */
    public Geometry getGeographicExtent() {
        return geographicExtent;
    }

    /**
     * @return the position
     */
    public Point getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return displayName;
    }

    @Override
    public int compareTo( Object o ) {
        if ( o instanceof GazetteerItem ) {
            GazetteerItem oth = (GazetteerItem) o;
            if ( identPart1 == null || identPart2 == null ) {
                return geographicIdentifier.compareTo( oth.geographicIdentifier );
            }
            if ( identPart1.equals( oth.identPart1 ) ) {
                return identPart2.compareTo( oth.identPart2 );
            }
            return identPart1.compareTo( oth.identPart1 );
        }
        return geographicIdentifier.compareTo( o.toString() );
    }

}
