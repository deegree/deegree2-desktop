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
package org.deegree.framework.utils;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.deegree.igeo.i18n.Messages;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ExternalPrograms {

    /**
     * 
     * @param program
     * @param parameters
     * @param paramValues
     * @return started process
     * @throws IOException
     */
    public static Process startProgram( String program, List<String> parameters, List<String> paramValues )
                            throws IOException {
        if ( parameters.size() != paramValues.size() ) {
            throw new IOException( Messages.get( "$DG10119", parameters.size(), paramValues.size() ) );
        }
        ListIterator<String> iter = parameters.listIterator();
        int i = 0;
        while ( iter.hasNext() ) {
            String item = URLDecoder.decode( iter.next().trim(), "UTF-8" );
            item = item.replace( "$PROPERTY", paramValues.get( i ) );
            iter.set( item );
        }
        parameters.add( 0, URLDecoder.decode( program.trim(), "UTF-8" ) );

        ProcessBuilder pb = new ProcessBuilder( parameters );
        return pb.start();
    }

    /**
     * 
     * @param program
     * @param parameter
     * @param parameterValue
     * @return started process
     * @throws IOException
     */
    public static Process startProgram( String program, String parameter, String parameterValue )
                            throws IOException {
        List<String> parameters = new ArrayList<String>();
        parameters.add( parameter );
        List<String> parameterValues = new ArrayList<String>();
        parameterValues.add( parameterValue );
        return startProgram( program, parameters, parameterValues );
    }
}
