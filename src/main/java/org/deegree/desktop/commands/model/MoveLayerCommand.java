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
package org.deegree.desktop.commands.model;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.LayerGroup;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.mapmodel.MapModelEntry;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.Command;

/**
 * {@link Command} implementation for moving a layer in the map model layer tree to a new parent 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class MoveLayerCommand extends AbstractCommand {

    private MapModelEntry layer;

    private LayerGroup parent;

    private MapModelEntry antecessor;

    private MapModel mapModel;

    private LayerGroup oldParent;

    private Layer oldAntecessor;

    private boolean performed = false;
    
    private boolean first;

    /**
     * 
     * @param layer
     * @param parent
     * @param antecessor
     * @param mapModel
     * @param first
     */
    public MoveLayerCommand( MapModelEntry layer, LayerGroup parent, MapModelEntry antecessor, MapModel mapModel, boolean first ) {
        this.layer = layer;
        this.parent = parent;
        this.antecessor = antecessor;
        this.mapModel = mapModel;
        this.first = first;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.Command#execute()
     */
    public void execute()
                            throws Exception {
        oldParent = layer.getParent();
        oldAntecessor = layer.getAntecessor();

        mapModel.move( layer, parent, antecessor, first );
        performed = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.Command#getName()
     */
    public QualifiedName getName() {
        return new QualifiedName( "MoveLayer" );
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
        if ( performed ) {
            mapModel.move( layer, oldParent, oldAntecessor, first );
            performed = false;
        }
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
