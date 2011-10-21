/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2008 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

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

package org.deegree.framework.utils;

import java.io.File;
import java.text.ParseException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.deegree.framework.util.StringTools;

/**
 * The class manipulates strings and concatenates them using mutliple delimeters and maps between relative and absolute
 * paths. some method names are similar to those in the class String but with different parameters.
 * 
 * @author <a href="mailto:elmasry@lat-lon.de">Moataz Elmasry</a>
 * @author last edited by: $Author: elmasri$
 * 
 * @version $Revision$, $Date: 8 May 2008 14:25:36$
 */
public class PathManipulator {

    private PathManipulator() {
    }

    /**
     * Gets the first match from the many delimiters give as input. So it takes multiple delimets, and returns the
     * string from the begining to the first delimet
     * 
     * @param source
     * @param delimiters
     *            array of delimiters, the delimiter found first will be used and the function returns the text from
     *            firstIndex to the delimiter
     * @param fromIndex
     *            the start index to look from
     * @return String
     */
    public static String getFirstMatch( String source, String[] delimiters, int fromIndex ) {

        Vector<Delimiter> indices = new Vector<Delimiter>();

        for ( int i = 0; i < delimiters.length; i++ ) {
            // we added the indices of all matches to the vector
            indices.add( new Delimiter( i, source.indexOf( delimiters[i] ), delimiters[i] ) );
        }

        Delimiter delimiter = getFirstIndex( indices );
        if ( delimiter.value == null ) {
            return null;
        }

        return source.substring( fromIndex, delimiter.foundAt + 1 );
    }

    /**
     * Gets the first index of a match from the many delimiters given as input Takes many delimiters returns the
     * delimiter that occured first
     * 
     * @param collection
     * @return instance of Delimiter class
     */
    private static Delimiter getFirstIndex( Collection<Delimiter> collection ) {

        Delimiter delimiter = new Delimiter( -1, 999, null );
        Iterator<Delimiter> it = collection.iterator();
        // comparing the matches to see which match occured first
        while ( it.hasNext() ) {
            Delimiter temp = (Delimiter) it.next();
            int indexOf = temp.foundAt;
            if ( indexOf < delimiter.foundAt && indexOf > -1 ) {
                delimiter = temp;
            }
        }

        if ( delimiter == null ) {
            return null;
        }
        return delimiter;

    }

    /**
     * Split a string based on the given delimiters and return an array of strings(tokens)
     * 
     * @param source
     * @param delimiters
     * @return tokens from a given string
     */
    public static String[] splitString( String source, String[] delimiters ) {

        if ( source == null || delimiters == null )
            return null;

        Vector<String> returnedStrings = new Vector<String>();
        String tempSource = source;

        while ( tempSource.length() != 0 ) {

            int delimiterLength = 0;
            String match = getFirstMatch( tempSource, delimiters, 0 );
            // if this is the last token in the String
            if ( match == null ) {
                returnedStrings.add( tempSource );
                break;
            } else {

                // removing any delimiters that could exist
                for ( int i = 0; i < delimiters.length; i++ ) {
                    if ( match.contains( delimiters[i] ) ) {
                        match = match.replace( delimiters[i], "" );
                        delimiterLength = delimiters[i].length();
                        break;
                    }

                }
                // Ignore the . and don't add it to the array in case there was a ./ in the path for
                // example
                if ( !match.equals( "." ) ) {
                    returnedStrings.add( match );
                }
                tempSource = tempSource.substring( match.length() + delimiterLength, tempSource.length() );
            }
        }

        String[] strings = new String[returnedStrings.size()];
        for ( int i = 0; i < returnedStrings.size(); i++ ) {
            strings[i] = returnedStrings.elementAt( i );
        }
        return strings;
    }

    /**
     * Maps from a source sTring to a target String, based on the delimiters given the delimiters are basically "\\" or
     * "/", but it also could be anything else Two absolute pathes should be given here Don't give relative
     * 
     * @param folder
     * @param fileName
     * @param delimiters
     * @return the mapped path
     * @throws ParseException
     */
    public static String mapRelativePath( String folder, String fileName, String[] delimiters )
                            throws ParseException {
        folder = StringTools.replace( folder, "\\", "/", true );
        fileName = StringTools.replace( fileName, "\\", "/", true );        
        if ( !fileName.toLowerCase().startsWith( "http:" ) ) {
            if ( fileName.toLowerCase().startsWith( "file:" ) ) {
                File f = new File( fileName.substring( 5 ) );
                fileName = f.getAbsolutePath();
            }
        } else {
            return fileName;
        }

        if ( !new File( folder ).isAbsolute() ) {
            throw new ParseException( "The source path is not absolute", 0 );
        }
        if ( !new File( fileName ).isAbsolute() ) {
            throw new ParseException( "The target path is not absolute", 0 );
        }

        String[] sourceTokens = splitString( folder, delimiters );
        String[] targetTokens = splitString( fileName, delimiters );
        if ( sourceTokens == null || targetTokens == null ) {
            return null;
        }
        if ( sourceTokens.length == 0 || targetTokens.length == 0 ) {
            return null;
        }

        int lessTokens = 0;
        if ( sourceTokens.length < targetTokens.length ) {
            lessTokens = sourceTokens.length;
        } else {
            lessTokens = targetTokens.length;
        }

        int counter = 0;
        for ( counter = 0; counter < lessTokens; counter++ ) {
            if ( counter == 0 ) {
                if ( !sourceTokens[counter].toLowerCase().equals( targetTokens[counter].toLowerCase() ) ) {
                    break;
                }
            } else {
                if ( !sourceTokens[counter].equals( targetTokens[counter] ) ) {
                    break;
                }
            }
        }

        StringBuffer buffer = new StringBuffer();
        for ( int i = counter; i < sourceTokens.length; i++ ) {
            buffer.append( "../" );
        }

        // This is used when the target is only one token different/larger than the source, so we
        // just take
        // the last token in the target
        if ( ( counter == sourceTokens.length ) && ( sourceTokens.length == targetTokens.length + 1 )
             && ( sourceTokens[counter - 1].equals( targetTokens[counter - 1] ) ) ) {
            return targetTokens[targetTokens.length - 1];
        }
        for ( int i = counter; i < targetTokens.length; i++ ) {
            if ( buffer.length() == 0 ) {
                // This is the first token in the path
                buffer.append( "./" );
            }
            buffer.append( targetTokens[i] );
            if ( i != targetTokens.length - 1 ) {
                buffer.append( "/" );
            }
        }
        return buffer.toString();
    }

    /**
     * Maps from a source file to a target folder, based on the delimiters given the delimiters are basically "/" or
     * "/", but it also could be anything else Two absolute pathes should be given here. Don't give relative. Default
     * delimiters to use in order to divide each path into tokens are / and /
     * 
     * @param file
     * @param folder
     * @return the mapped path
     * @throws ParseException
     */
    public static String mapRelativePath( String folder, String file )
                            throws ParseException {
        String delimiters[] = { "/", "/" };
        return mapRelativePath( folder, file, delimiters );
    }

    static class Delimiter {
        int index;

        String value;

        int foundAt;

        /**
         * @param index
         * @param foundAt
         * @param value
         */
        public Delimiter( int index, int foundAt, String value ) {
            this.index = index;
            this.value = value;
            this.foundAt = foundAt;
        }
    }

    public static void main( String[] args ) {
        // String folder = "C:/das/ist/ein/ordner";
        // String file = "C:/das/ist/ein/file/another/test.xml";
        String folder = "D:\\java\\projekte\\deegree2_client\\resources\\";
        String file = "D:/java/projekte/deegree2_client/resources/data/data/SGID100_CountyBoundaries.shp";
        try {
            File f  = new File( PathManipulator.mapRelativePath( folder, file ) );
            System.out.println(f.getPath());
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
