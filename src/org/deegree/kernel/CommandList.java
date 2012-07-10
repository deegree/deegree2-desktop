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

import org.deegree.datatypes.QualifiedName;
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
public class CommandList extends AbstractCommand {

    private List<Command> commands;

    private Identifier identifier;

    private QualifiedName name;

    private List<Object> results;

    /**
     * 
     */
    public CommandList() {
        identifier = new Identifier();
        name = new QualifiedName( "Command List" );
        commands = new ArrayList<Command>();
        results =  new ArrayList<Object>();
    }

    /**
     * 
     * @param identifier
     * @param name
     * @param commands
     */
    public CommandList( Identifier identifier, QualifiedName name, List<Command> commands ) {
        this.name = name;
        this.identifier = identifier;
        this.commands = commands;
        results = new ArrayList<Object>( this.commands.size() );
    }

    /**
     * 
     * @param command
     */
    public void add( Command command ) {
        commands.add( command );
    }

    /**
     * 
     */
    public void execute()
                            throws Exception {

        for ( Command command : this.commands ) {
            command.execute();
            results.add( command.getResult() );
        }
    }

    /**
     * @return unique identifier for a command
     */
    public Identifier getIdentifier() {
        return this.identifier;
    }

    /**
     * @return name of a command
     */
    public QualifiedName getName() {
        return this.name;
    }

    /**
     * returns true if a Command supports undo operation. Undo is just supported if all Commands contained in a
     * CommandList supports undo operation
     * 
     * @return true if a Command supports undo operation
     */
    public boolean isUndoSupported() {
        for ( Command command : this.commands ) {
            if ( !command.isUndoSupported() ) {
                return false;
            }
        }
        return true;
    }

    /**
     * undoes a processed command. This method can just be invoked after execute() has been invoked. Otherwise an
     * exception will be thrown.
     * 
     */
    public void undo()
                            throws Exception {
        // notice that for undo the list of commands must be stepped through from least to first
        // command
        for ( int i = this.commands.size() - 1; i >= 0; i-- ) {
            Command command = this.commands.get( i );
            command.undo();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.Command#getResult()
     */
    public Object getResult() {
        return results;
    }

}