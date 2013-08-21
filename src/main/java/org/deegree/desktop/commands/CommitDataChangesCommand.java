//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2012 by:
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

 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: info@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/

package org.deegree.desktop.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.dataadapter.DataAccessAdapter;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.LayerGroup;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.Command;

/**
 * {@link Command} implementation for storing changes of data/layer into according backend
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class CommitDataChangesCommand extends AbstractCommand {

    private QualifiedName name = new QualifiedName( "Commit Data Changes" );

    private List<Layer> layers;

    /**
     * 
     * @param layers
     * @throws IOException
     */
    public CommitDataChangesCommand( MapModel mapModel ) throws IOException {
        List<LayerGroup> layerGroups = mapModel.getLayerGroups();
        layers = new ArrayList<Layer>( 50 );
        collect( layerGroups );
    }

    /**
     * 
     * @param layers
     */
    public CommitDataChangesCommand( List<Layer> layers ) {
        this.layers = layers;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {

        for ( Layer layer : this.layers ) {
            List<DataAccessAdapter> adapters = layer.getDataAccess();
            for ( DataAccessAdapter adapter : adapters ) {
                adapter.commitChanges();
            }
        }
        fireCommandProcessedEvent();
    }

    private void collect( List<LayerGroup> layerGroups )
                            throws IOException {
        for ( LayerGroup group : layerGroups ) {
            List<Layer> layers = group.getLayers();
            for ( Layer layer : layers ) {
                this.layers.add( layer );
            }
            List<LayerGroup> groups = group.getLayerGroups();
            collect( groups );
        }
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
        return layers;
    }

}
