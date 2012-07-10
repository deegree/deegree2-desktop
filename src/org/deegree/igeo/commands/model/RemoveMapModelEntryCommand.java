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

package org.deegree.igeo.commands.model;

import java.util.ArrayList;
import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.LayerGroup;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MapModelEntry;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.kernel.AbstractCommand;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class RemoveMapModelEntryCommand extends AbstractCommand {

    private static final QualifiedName name = new QualifiedName( "Remove MapModelEntry" );

    private boolean performed = false;

    private MapModel mapModel;

    private List<MapModelEntry> removeEntries;

    private List<LayerGroup> parents = new ArrayList<LayerGroup>();

    /**
     * 
     * @param mapModel
     */
    public RemoveMapModelEntryCommand( MapModel mapModel ) {
        this.mapModel = mapModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {
        removeEntries = mapModel.getMapModelEntriesSelectedForAction( MapModel.SELECTION_ACTION );
        for ( MapModelEntry mapModelEntry : removeEntries ) {
            if ( mapModelEntry.getParent() == null ) {
                DialogFactory.openErrorDialog( mapModel.getApplicationContainer().getViewPlatform(), null,
                                               Messages.get( "$DG10096" ), getName().getLocalName() );
                throw new Exception( Messages.get( "$DG10096" ) );
            }
        }
        for ( MapModelEntry mapModelEntry : removeEntries ) {
            parents.add( mapModelEntry.getParent() );
            mapModel.remove( mapModelEntry );
        }
        performed = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getName()
     */
    public QualifiedName getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getResult()
     */
    public Object getResult() {
        return mapModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#isUndoSupported()
     */
    public boolean isUndoSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#undo()
     */
    public void undo()
                            throws Exception {
        if ( performed ) {
            for ( int i = 0; i < removeEntries.size(); i++ ) {
                MapModelEntry entry = removeEntries.get( i );
                LayerGroup parent = parents.get( i );
                insertMapModelEntries( entry, parent );
            }
            performed = false;
        }

    }

    private void insertMapModelEntries( MapModelEntry entry, LayerGroup parent ) {
        mapModel.insert( entry, parent, null, false );
    }

}
