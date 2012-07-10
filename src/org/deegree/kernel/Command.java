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

import org.deegree.datatypes.QualifiedName;
import org.deegree.model.Identifier;

/**
 * Definition of commands to be process by a CommandProcessor. Realizes Command Processor Pattern.
 * Processing a Command by calling execute-method (done by a CommandProcessor) may will produce a
 * result, e.g. a FeatureCollection. Such a result can be accessed by calling getResult-method.
 * Since a Command object will be encapsulated within the event passed to all listeners registered
 * to a CommandProcesser each listener insteressed in the result of a processed command will be abel
 * to get it.
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public interface Command {

    /**
     * returns the Commands unique identifier
     * 
     * @return Commands unique identifier
     */
    public Identifier getIdentifier();

    /**
     * 
     * @param processMonitor
     */
    public void setProcessMonitor( ProcessMonitor processMonitor );

    /**
     * returns the Commands name. It is possible to process more than one Command with same name at
     * the same time a Commands name is not unique in context of a CommandProcessor.
     * 
     * @return Commands name
     */
    public QualifiedName getName();

    /**
     * executes a Command
     */
    public void execute()
                            throws Exception;

    /**
     * undoes a processed command. This method can just be invoked after execute() has been invoked.
     * Otherwise an exception will be thrown.
     * 
     */
    public void undo()
                            throws Exception;

    /**
     * returns true if a Command supports undo operation
     * 
     * @return true if a Command supports undo operation
     */
    public boolean isUndoSupported();

    /**
     * 
     * @return result object or <code>null</code> if command does not produce a result
     */
    public Object getResult();
    
    /**
     * adds a listener to a command that will be notified if a command has been processed
     * @param listener
     */
    public void addListener(CommandProcessedListener listener);

}