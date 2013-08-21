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
package org.deegree.framework.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.deegree.desktop.views.swing.monitor.LoggerWindow;
import org.deegree.framework.util.TimeTools;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class DeegreeDesktopAppender extends ConsoleAppender {
    
    private static List<String> messages = Collections.synchronizedList( new ArrayList<String>(100) );
    private static LoggerWindow lw = new LoggerWindow();

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.log4j.WriterAppender#append(org.apache.log4j.spi.LoggingEvent)
     */
    @Override
    public void append( LoggingEvent event ) {
        super.append( event );
        synchronized ( messages ) {
            while ( messages.size() >= 100 ) {
                messages.remove( 0 );
            }
            messages.add( TimeTools.getISOFormattedTime() + ": " + event.getMessage() );
            if ( lw.isVisible() ) {
                lw.setText( messages );
            }
        }        
    }
        
    /**
     * 
     */
    public static void show() {
        lw.setText( messages );
        lw.setVisible( true );
        lw.toFront();
    }
    
}
