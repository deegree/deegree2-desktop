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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.concurrent.ExecutionFinishedEvent;
import org.deegree.framework.concurrent.ExecutionFinishedListener;
import org.deegree.framework.concurrent.Executor;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.views.DialogFactory;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class CommandProcessor implements ExecutionFinishedListener<Command> {

    private static final ILogger LOG = LoggerFactory.getLogger( CommandProcessor.class );

    private List<Command> doneCommands;

    private List<Command> undoneCommands;

    private List<CommandProcessedListener> listeners;
    
    private static final int MAX_COMMANDS = 30;

    /**
     * 
     */
    public CommandProcessor() {
        this.doneCommands = Collections.synchronizedList( new ArrayList<Command>( 100 ) );
        this.undoneCommands = Collections.synchronizedList( new ArrayList<Command>( 100 ) );
        this.listeners = new ArrayList<CommandProcessedListener>();
    }

    /**
     * removes all listeners and commands from done and undone lists 
     * 
     */
    public void clear() {
        clearCommands();
        this.listeners.clear();
    }
    
    /**
     * removes all commands from done and undone lists
     */
    public void clearCommands() {
        this.doneCommands.clear();
        this.undoneCommands.clear();
    }

    /**
     * executes a command, waits until execution finished and returns result of processed command.
     * 
     * @param command
     * @param notifyListener
     *            true if listeners registered to {@link CommandProcessor} shall be notified about executed command
     * @throws Exception
     */
    public Object executeSychronously( Command command, boolean notifyListener )
                            throws Exception {
        CommandTask task = new CommandTask( command );
        try {
            Executor.getInstance().performSynchronously( task, Long.MAX_VALUE );
        } catch ( Throwable e ) {
            LOG.logError( e.getMessage(), e );
            throw new Exception( e.getMessage() );
        }
        if ( notifyListener ) {
            CommandProcessedEvent event = new CommandProcessedEvent( command );
            for ( CommandProcessedListener listener : CommandProcessor.this.listeners ) {
                listener.commandProcessed( event );
            }
        }
        if ( command.isUndoSupported() ) {
            this.doneCommands.add( command );
            if ( this.doneCommands.size() > MAX_COMMANDS ) {
                this.doneCommands.remove( 0 );
            }
        }
        return command.getResult();
    }

    /**
     * 
     * @param command
     */
    public void executeASychronously( Command command ) {
        CommandTask ct = new CommandTask( command );
        Executor.getInstance().performAsynchronously( ct, this );
    }

    /**
     * performs a synchronous execution of passed command with notifying registered listeners 
     * @param command
     * @deprecated use {@link #executeASychronously(Command)} or {@link #executeSychronously(Command, boolean)} instead
     * 
     */
    public void addCommand( Command command ) {

        // CommandTask ct = new CommandTask( command );
        try {
            // Executor.getInstance().performAsynchronously( ct, this );
            executeSychronously( command, true );
        } catch ( Throwable e ) {
            LOG.logError( e.getMessage(), e );
        }

    }

    /**
     * returns a list containing the names of commands that can be undone
     * 
     * @return
     */
    public List<QualifiedName> availableRedos() {
        List<QualifiedName> list = new ArrayList<QualifiedName>( this.undoneCommands.size() );
        for ( Command command : this.undoneCommands ) {
            list.add( command.getName() );
        }
        return list;
    }

    /**
     * @throws Exception
     * 
     * 
     */
    public void redo()
                            throws Exception {
        synchronized ( this.undoneCommands ) {
            if ( this.undoneCommands.size() > 0 ) {
                Command command = this.undoneCommands.remove( this.undoneCommands.size() - 1 );
                while ( command == null ) {
                    int k = this.undoneCommands.size() - 1;
                    if ( k < 0 ) {
                        command = this.undoneCommands.remove( k );
                    } else {
                        command = null;
                    }
                }
                if ( command != null ) {
                    addCommand( command );
                }
            }
        }
    }

    /**
     * returns a list containing the names of commands that can be undone
     * 
     * @return
     */
    public List<QualifiedName> availableUndos() {
        List<QualifiedName> list = new ArrayList<QualifiedName>( this.doneCommands.size() );
        for ( Command command : this.doneCommands ) {
            if ( command.isUndoSupported() ) {
                list.add( command.getName() );
            }
        }
        return list;
    }

    /**
     * @throws Exception
     * 
     * 
     */
    public void undo()
                            throws Exception {
        synchronized ( this.doneCommands ) {
            if ( this.doneCommands.size() > 0 ) {
                Command command = this.doneCommands.remove( this.doneCommands.size() - 1 );
                if ( command != null ) {
                    command.undo();
                    this.undoneCommands.add( command );
                    if ( this.undoneCommands.size() > MAX_COMMANDS ) {
                        this.undoneCommands.remove( 0 );
                    }
                }
                CommandProcessedEvent event = new CommandProcessedEvent( command );
                for ( CommandProcessedListener listener : CommandProcessor.this.listeners ) {
                    listener.commandProcessed( event );
                }
            }
        }
    }

    /**
     * 
     * @param listener
     *            listener
     */
    public void addCommandProcessedListener( CommandProcessedListener listener ) {
        this.listeners.add( listener );
    }

    /**
     * 
     * @param command
     * 
     *            command
     */
    public void removeCommandProcessedListener( CommandProcessedListener listener ) {
        this.listeners.remove( listener );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.deegree.framework.concurrent.ExecutionFinishedListener#executionFinished(org.deegree.framework.concurrent
     * .ExecutionFinishedEvent)
     */
    public void executionFinished( ExecutionFinishedEvent<Command> finishedEvent ) {
        try {
            Command command = finishedEvent.getResult();
            CommandProcessedEvent event;
            try {
                CommandProcessor.this.doneCommands.add( command );
                if ( CommandProcessor.this.doneCommands.size() > MAX_COMMANDS ) {
                    CommandProcessor.this.doneCommands.remove( 0 );
                }
                event = new CommandProcessedEvent( command );
            } catch ( Throwable e ) {
                LOG.logError( e.getMessage(), e );
                throw new CommandException( Messages.getMessage( Locale.getDefault(), "$DG10050" ) );
            }
            for ( CommandProcessedListener listener : CommandProcessor.this.listeners ) {
                listener.commandProcessed( event );
            }
        } catch ( CancellationException e ) {
            LOG.logInfo( "command canceled" );
        } catch ( Throwable e ) {
            LOG.logError( e.getMessage(), e );
        }

    }

    // //////////////////////////////////////////////////////////////////////////////////
    // Inner class
    // //////////////////////////////////////////////////////////////////////////////////
    private class CommandTask implements Callable<Command> {

        private Command command;

        /**
         * 
         * @param command
         */
        public CommandTask( Command command ) {
            this.command = command;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.concurrent.Callable#call()
         */
        public Command call()
                                throws Exception {
            try {
                command.execute();
            } catch (Exception e) {
                LOG.logError( e );
                DialogFactory.openErrorDialog( "application", null, "Command ERROR", e.getMessage(), e );
            }
            return command;
        }

    }

}