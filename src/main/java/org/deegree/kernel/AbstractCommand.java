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

import java.util.ArrayList;
import java.util.List;

import org.deegree.model.Identifier;

/**
 * abstract implementation of {@link Command} that implements some methods that are common to most concrete command
 * implementations
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public abstract class AbstractCommand implements Command {

    protected ProcessMonitor processMonitor;

    protected List<CommandProcessedListener> listeners = new ArrayList<CommandProcessedListener>();

    protected static final CancelResult cancelResult = new CancelResult();

    private Identifier identifier = new Identifier();

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getIdentifier()
     */
    public Identifier getIdentifier() {
        return identifier;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#isUndoSupported()
     */
    public boolean isUndoSupported() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#undo()
     */
    public void undo()
                            throws Exception {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#setProcessMonitor(org.deegree.kernel.ProcessMonitor)
     */
    public void setProcessMonitor( ProcessMonitor processMonitor ) {
        this.processMonitor = processMonitor;
    }

    /**
     * 
     * @return true if a {@link Command} has been canceled
     */
    protected boolean isCanceled() {
        return ( processMonitor != null && processMonitor.isCanceled() );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#addListener(org.deegree.kernel.CommandProcessedListener)
     */
    public void addListener( CommandProcessedListener listener ) {
        listeners.add( listener );
    }

    /**
     * notifies all registered listeneres that processing of a command has been finished
     * 
     */
    protected void fireCommandProcessedEvent() {
        CommandProcessedEvent event = new CommandProcessedEvent( this );
        for ( CommandProcessedListener listener : listeners ) {
            listener.commandProcessed( event );
        }
    }

}
