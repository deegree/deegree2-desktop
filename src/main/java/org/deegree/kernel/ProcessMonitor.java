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

package org.deegree.kernel;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public interface ProcessMonitor extends Runnable {

    /**
     * 
     * @param title
     * @param message
     * @param min
     * @param max
     */
    public void init(String title, String message, int min, int max, Command command);
    
    /**
     * 
     * @param minimum Specifies the minimum progress value
     */
    public void setMinimumValue(int minimum);
    
    /**
     * 
     * @param maximum Specifies the maximum progress value
     */
    public void setMaximumValue(int maximum);

    /**
     * updates the status of a monitor with a new description
     * 
     * @param description
     */
    public void updateStatus( String description );

    /**
     * updates the status of a monitor with a number of processed items
     * and a description of the least processed item
     * 
     * @param itemsDone
     * @param itemDescription
     */
    public void updateStatus( int itemsDone, String itemDescription );

    /**
     * cancels of monitored process if allowed; otherwise nothing happens
     */
    public void cancel()
                            throws Exception;

    /**
     * 
     * @return <code>true</code> if process has been canceled
     */
    public boolean isCanceled();
    
}
