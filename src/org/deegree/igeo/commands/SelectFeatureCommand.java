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
import org.deegree.igeo.mapmodel.LayerGroup;
import org.deegree.igeo.mapmodel.MapModelVisitor;
import org.deegree.kernel.AbstractCommand;
import org.deegree.model.Identifier;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Point;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class SelectFeatureCommand extends AbstractCommand {

    public static final QualifiedName commandName = new QualifiedName( "Select Feature Command" );

    private List<Identifier> fids;

    private Envelope envelope;

    private Point point;

    private Filter filter;

    private Layer layer;

    private boolean additive;

    /**
     * 
     * @param layer
     * @param fids
     *            fid of the feature to be selected
     * @param additive
     *            true if selection should be added to already selected features.
     */
    public SelectFeatureCommand( Layer layer, List<Identifier> fids, boolean additive ) {

        this.fids = fids;
        this.layer = layer;
        this.additive = additive;
    }

    /**
     * 
     * @param layer
     * @param envelope
     * @param additive
     *            true if selection should be added to already selected features.
     */
    public SelectFeatureCommand( Layer layer, Envelope envelope, boolean additive ) {

        this.envelope = envelope;
        if ( envelope.getWidth() == 0 || envelope.getHeight() == 0 ) {         
            // required because intersection test of JTS does not accept zero sized polygons
            this.envelope = envelope.getBuffer( 0.000001 );            
        }
        this.layer = layer;
        this.additive = additive;
    }

    /**
     * 
     * @param layer
     * @param point
     * @param additive
     *            true if selection should be added to already selected features.
     */
    public SelectFeatureCommand( Layer layer, Point point, boolean additive ) {

        this.point = point;
        this.layer = layer;
        this.additive = additive;
    }

    /**
     * 
     * @param layer
     * @param filter
     * @param additive
     *            true if selection should be added to already selected features.
     */
    public SelectFeatureCommand( Layer layer, Filter filter, boolean additive ) {

        this.filter = filter;
        this.layer = layer;
        this.additive = additive;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {     
        if ( !additive ) {
            layer.getOwner().walkLayerTree( new MapModelVisitor() {

                public void visit( Layer layer )
                                        throws Exception {
                    layer.unselectAllFeatures();                    
                }

                public void visit( LayerGroup layerGroup )
                                        throws Exception {
                    // nothing to do
                }
                
            });
        }
        if ( fids != null ) {
            layer.selectFeatures( fids, additive );
        } else if ( envelope != null ) {
            layer.selectFeatures( envelope, additive );
        } else if ( point != null ) {
            layer.selectFeatures( point, additive );
        } else if ( filter != null ) {
            layer.selectFeatures( filter, additive );
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
        return layer.getSelectedFeatures();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#isUndoSupported()
     */
    public boolean isUndoSupported() {
        // undo is just supported if feature selection is not additive. Otherwise
        // a feature remains selected until the complete selection is removed
        return !additive;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#undo()
     */
    public void undo()
                            throws Exception {
        if ( !additive ) {
            layer.unselectAllFeatures();
        }
    }

}
