// $HeadURL:
// /deegreerepository/deegree/test/org/deegree/io/datastore2/SelectBuilderTest.java,v
// 1.2 2005/05/24 15:37:33 mschneider Exp $
/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2005 by:
 EXSE, Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/exse/
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

 Jens Fitzke
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: jens.fitzke@uni-bonn.de
 
 ---------------------------------------------------------------------------*/
package org.deegree.igeo.commands;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.deegree.datatypes.QualifiedName;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.Command;
import org.deegree.kernel.CommandList;
import org.deegree.kernel.CommandProcessedEvent;
import org.deegree.kernel.CommandProcessedListener;
import org.deegree.kernel.CommandProcessor;
import org.deegree.kernel.ProcessMonitor;
import org.deegree.model.Identifier;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class CommandTest extends TestCase implements CommandProcessedListener {

    CommandProcessor cp;

    protected void setUp()
                            throws Exception {
        super.setUp();
        cp = new CommandProcessor();
    }

    protected void tearDown()
                            throws Exception {
        super.tearDown();
    }

    public void _testPerformSynchronousCommands()
                            throws Exception {

        cp.addCommandProcessedListener( this );
        Command com1 = new Command1( new Identifier( "cmd1" ), new QualifiedName( "Command1" ) );
        cp.addCommand( com1 );
        com1 = new Command1( new Identifier( "cmd2" ), new QualifiedName( "Command2" ) );
        cp.addCommand( com1 );
        Thread.sleep( 500 );
        cp.removeCommandProcessedListener( this );

    }

    public void _testPerformAsynchronousCommands()
                            throws Exception {
        cp.addCommandProcessedListener( this );
        Command com = new Command2( new Identifier( "Asynchcmd1" ), new QualifiedName( "AsynchCommand1" ) );
        cp.addCommand( com );
        com = new Command2( new Identifier( "Asynchcmd2" ), new QualifiedName( "AsynchCommand2" ) );
        cp.addCommand( com );
        Thread.sleep( 500 );
        cp.removeCommandProcessedListener( this );
    }

    public void testPerformCommandList()
                            throws Exception {
        cp.addCommandProcessedListener( this );
        List<Command> list = new ArrayList<Command>();
        Command com = new Command1( new Identifier( "cmd1" ), new QualifiedName( "Command1" ) );
        list.add( com );
        com = new Command2( new Identifier( "Asynchcmd1" ), new QualifiedName( "AsynchCommand1" ) );
        list.add( com );
        com = new Command1( new Identifier( "cmd2" ), new QualifiedName( "Command2" ) );
        list.add( com );
        com = new Command2( new Identifier( "Asynchcmd2" ), new QualifiedName( "AsynchCommand2" ) );
        list.add( com );
        com = new CommandList( new Identifier( "cmdlist1" ), new QualifiedName( "CommandList1" ), list );
        cp.addCommand( com );
        Thread.sleep( 500 );
        cp.removeCommandProcessedListener( this );

    }

    public void commandProcessed( CommandProcessedEvent event ) {
        System.out.println( event.getSource().getIdentifier().getValue() + " processed" );
        try {
            cp.undo();
        } catch ( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private class Command1 extends AbstractCommand {

        private Identifier identifier;

        private QualifiedName name;

        Command1( Identifier identifier, QualifiedName name ) {
            this.identifier = identifier;
            this.name = name;
        }

        public void execute()
                                throws Exception {
            System.out.println( name + " executed" );

        }
        
        

        /* (non-Javadoc)
         * @see org.deegree.kernel.Command#setProcessMonitor(org.deegree.kernel.ProcessMonitor)
         */
        public void setProcessMonitor( ProcessMonitor processMonitor ) {
            // TODO Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.deegree.kernel.Command#cancel()
         */
        public void cancel()
                                throws Exception {
            // TODO Auto-generated method stub
            
        }

        public Identifier getIdentifier() {
            return identifier;
        }
        public QualifiedName getName() {
            return name;
        }

        public boolean isUndoSupported() {
            return true;
        }

        public void undo()
                                throws Exception {
            System.out.println( name + " undone" );
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deegree.client.presenter.connector.Command#getResult()
         */
        public Object getResult() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    private class Command2 extends AbstractCommand {

        public Command2( Identifier identifier, QualifiedName name ) {
            super();
        }

        public boolean isUndoSupported() {
            return true;
        }

        /* (non-Javadoc)
         * @see org.deegree.kernel.Command#setProcessMonitor(org.deegree.kernel.ProcessMonitor)
         */
        public void setProcessMonitor( ProcessMonitor processMonitor ) {
            // TODO Auto-generated method stub
            
        }

        public void undo()
                                throws Exception {
            System.out.println( getName() + " undone" );
        }

        
        /* (non-Javadoc)
         * @see org.deegree.kernel.Command#execute()
         */
        public void execute()
                                throws Exception {
            // TODO Auto-generated method stub
            
        }

        /* (non-Javadoc)
         * @see org.deegree.kernel.Command#getName()
         */
        public QualifiedName getName() {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see org.deegree.kernel.Command#cancel()
         */
        public void cancel()
                                throws Exception {
            // TODO Auto-generated method stub
            
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deegree.client.presenter.connector.Command#getResult()
         */
        public Object getResult() {
            // TODO Auto-generated method stub
            return null;
        }

    }
}
