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

package org.deegree.igeo.commands;

import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MapModelEntry;
import org.deegree.kernel.AbstractCommand;

/**
 * command to changed to state a layer is selected for. E.g. a layer can be selected for editing, querying ...
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class ChangeMapModelEntrySelectedForCommand extends AbstractCommand {

    public static final QualifiedName name = new QualifiedName( "ChangeMapModelEntrySelectionState" );

    private MapModelEntry mapModelEntry;

    private MapModel mapModel;

    private String selectedFor;

    private boolean add;

    private boolean exclusive;

    /**
     * @param applicationContainer
     * @param mapModel
     * @param layer
     * @param state
     * @param value
     * @param exclusive
     */
    public ChangeMapModelEntrySelectedForCommand( MapModel mapModel, Layer layer, String selectedFor, boolean add,
                                                  boolean exclusive ) {
        this.mapModel = mapModel;
        this.mapModelEntry = layer;
        this.selectedFor = selectedFor;
        this.add = add;
        this.exclusive = exclusive;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.Command#execute()
     */
    public void execute()
                            throws Exception {
        if ( add ) {
            if ( exclusive ) {
                List<MapModelEntry> list = mapModel.getMapModelEntriesSelectedForAction( selectedFor );
                for ( MapModelEntry mapModelEntry : list ) {
                    mapModelEntry.removeSelectedFor( selectedFor );
                }
                mapModelEntry.addSelectedFor( selectedFor );
            } else {
                mapModelEntry.addSelectedFor( selectedFor );
            }
        } else {
            mapModelEntry.removeSelectedFor( selectedFor );
        }
        fireCommandProcessedEvent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.Command#getName()
     */
    public QualifiedName getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.Command#getResult()
     */
    public Object getResult() {
        return mapModelEntry;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.Command#isUndoSupported()
     */
    public boolean isUndoSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.Command#undo()
     */
    public void undo()
                            throws Exception {
        if ( add ) {
            mapModelEntry.removeSelectedFor( selectedFor );
        } else {
            mapModelEntry.addSelectedFor( selectedFor );
        }
    }

}
