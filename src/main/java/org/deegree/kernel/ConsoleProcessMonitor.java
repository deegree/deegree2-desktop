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
package org.deegree.kernel;

import org.deegree.framework.log.LoggerFactory;

import org.deegree.framework.log.ILogger;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ConsoleProcessMonitor implements ProcessMonitor {
    
    private static final ILogger LOG = LoggerFactory.getLogger( ConsoleProcessMonitor.class );

    private boolean canceled = false;

    private Command command;

    private String title;

    private String message;

    private int min;

    private int max;

    private String label;

    private String labelold = "";

    /**
     * 
     */
    public ConsoleProcessMonitor() {
        new Thread( this ).start();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.ProcessMonitor#cancel()
     */
    public void cancel()
                            throws Exception {
        canceled = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.ProcessMonitor#init(java.lang.String, java.lang.String, int, int,
     * org.deegree.kernel.Command)
     */
    public void init( String title, String message, int min, int max, Command command ) {
        this.message = message;
        this.title = title;
        this.command = command;
        this.min = min;
        this.max = max;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.ProcessMonitor#isCanceled()
     */
    public boolean isCanceled() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.ProcessMonitor#setMaximumValue(int)
     */
    public void setMaximumValue( int maximum ) {
        this.max = maximum;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.ProcessMonitor#setMinimumValue(int)
     */
    public void setMinimumValue( int minimum ) {
        this.min = minimum;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.ProcessMonitor#updateStatus(int, java.lang.String)
     */
    public void updateStatus( int itemsDone, String itemDescription ) {
        double d = max - min;
        d = d / 100d;
        d = itemsDone / d;
        label = message + ": " + itemsDone + "; " + itemDescription + "; " + d + "%";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.ProcessMonitor#updateStatus(java.lang.String)
     */
    public void updateStatus( String description ) {
        label = description;

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        if ( command == null ) {
            System.out.println( title );
        } else {
            System.out.println( title + " " + command.getName() );
        }
        while ( !canceled ) {
            try {
                Thread.sleep( 100 );
                if ( !label.equals( labelold ) ) {
                    System.out.println( label );
                    labelold = label;
                }
            } catch ( Exception e ) {
                LOG.logWarning( "warning", e );
            }
        }
    }

}
