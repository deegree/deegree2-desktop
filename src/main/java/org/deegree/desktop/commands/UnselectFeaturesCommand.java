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

package org.deegree.desktop.commands;

import java.util.ArrayList;
import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.LayerGroup;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.mapmodel.MapModelVisitor;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.Command;
import org.deegree.model.Identifier;
import org.deegree.model.feature.FeatureCollection;

/**
 * {@link Command} implementation for unselecting selected features
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class UnselectFeaturesCommand extends AbstractCommand {

    public static final QualifiedName commandName = new QualifiedName( "Unselect Features Command" );

    private static final ILogger LOG = LoggerFactory.getLogger( UnselectFeaturesCommand.class );

    private List<Layer> layers;

    private FeatureCollection selectedFeatures;
    
    private boolean supportUndo = true;

    /**
     * 
     * @param layer
     */
    public UnselectFeaturesCommand( Layer layer, boolean supportUndo  ) {
        this.supportUndo = supportUndo;
        this.layers = new ArrayList<Layer>( 1 );
        this.layers.add( layer );
    }

    /**
     * 
     * @param layer
     * @param supportUndo
     */
    public UnselectFeaturesCommand( MapModel mapModel, boolean supportUndo ) {
        this.supportUndo = supportUndo;
        this.layers = new ArrayList<Layer>( 100 );
        try {
            mapModel.walkLayerTree( new MapModelVisitor() {

                public void visit( Layer layer )
                                        throws Exception {
                    layers.add( layer );

                }

                public void visit( LayerGroup layerGroup )
                                        throws Exception {
                }

            } );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {
        for ( Layer layer : layers ) {
            selectedFeatures = layer.getSelectedFeatures();
            layer.unselectAllFeatures();
        }
        fireCommandProcessedEvent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getName()
     */
    public QualifiedName getName() {
        return commandName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getResult()
     */
    public Object getResult() {
        // an unselect features command does not have a result
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#isUndoSupported()
     */
    public boolean isUndoSupported() {
        return supportUndo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#undo()
     */
    public void undo()
                            throws Exception {
        int size = selectedFeatures.size();
        List<Identifier> fids = new ArrayList<Identifier>( size );
        for ( int i = 0; i < size; i++ ) {
            Identifier fid = new Identifier( selectedFeatures.getFeature( i ).getId() );
            fids.add( fid );
        }
        for ( Layer layer : layers ) {
            layer.selectFeatures( fids, true );
        }
    }

}
