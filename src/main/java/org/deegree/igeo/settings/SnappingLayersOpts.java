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

package org.deegree.igeo.settings;

import java.util.ArrayList;
import java.util.List;

import org.deegree.igeo.config.IdentifierType;
import org.deegree.igeo.config.SnapLayerType;
import org.deegree.igeo.config.Util;
import org.deegree.igeo.views.Snapper.SNAPTARGET;
import org.deegree.model.Identifier;

/**
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class SnappingLayersOpts extends ElementSettings {

    private List<SnapLayerType> snapLayers;

    /**
     * @param snapLayers
     * @param changeable
     */
    public SnappingLayersOpts( List<SnapLayerType> snapLayers, boolean changeable ) {
        super( changeable );
        this.snapLayers = snapLayers;
    }

    /**
     * 
     * @return list of layers registered for being snapping target
     */
    public List<Identifier> getLayers() {
        List<Identifier> list = new ArrayList<Identifier>( snapLayers.size() );
        for ( SnapLayerType snapLayer : snapLayers ) {
            list.add( Util.convertIdentifier( snapLayer.getLayer() ) );
        }
        return list;
    }

    /**
     * adds a layer (identifier) to the list of layers possibly considered for snapping. As default vertex is selected
     * as snapping target
     * 
     * @param identifier
     */
    public void addLayer( Identifier identifier ) {
        IdentifierType idt = convertIdentifier( identifier );
        if ( contains( idt ) == null ) {
            SnapLayerType slt = new SnapLayerType();
            slt.setLayer( idt );
            snapLayers.add( slt );
        }
    }

    /**
     * removes a layer (identifier) to the list of layers possibly considered for snapping.
     * 
     * @param identifier
     */
    public void removeLayer( Identifier identifier ) {
        IdentifierType idt = convertIdentifier( identifier );
        for ( SnapLayerType snapLayer : snapLayers ) {
            IdentifierType other = snapLayer.getLayer();
            if ( other.getValue().equals( idt.getValue() ) && other.getNamespace() != null
                 && other.getNamespace().equals( idt.getNamespace() ) ) {
                snapLayers.remove( snapLayer );
                return;
            }
        }
    }

    /**
     * 
     * @param other
     * @return {@link SnapLayerType} if snapLayer list already contains an object with passed {@link IdentifierType}
     */
    private SnapLayerType contains( IdentifierType other ) {
        for ( SnapLayerType snapLayer : snapLayers ) {
            IdentifierType idt = snapLayer.getLayer();
            if ( idt.getValue().equals( other.getValue() ) ) {
                if ( idt.getNamespace() != null && idt.getNamespace().equals( other.getNamespace() ) ) {
                    return snapLayer;
                }
                if ( idt.getNamespace() == null && other.getNamespace() == null ) {
                    return snapLayer;
                }
            }
        }
        return null;
    }

    /**
     * sets vertices for being snapping target
     * 
     * @param identifier
     * @param selected
     */
    public void selectSnappingTargetVertex( Identifier identifier, boolean selected ) {
        SnapLayerType slt = contains( convertIdentifier( identifier ) );
        if ( slt != null ) {
            slt.setVertex( selected );
        }
    }

    /**
     * sets start nodes for being snapping target
     * 
     * @param identifier
     * @param selected
     */
    public void selectSnappingTargetStartNode( Identifier identifier, boolean selected ) {
        SnapLayerType slt = contains( convertIdentifier( identifier ) );
        if ( slt != null ) {
            slt.setStartNode( selected );
        }
    }

    /**
     * sets end nodes for being snapping target
     * 
     * @param identifier
     * @param selected
     */
    public void selectSnappingTargetEndNode( Identifier identifier, boolean selected ) {
        SnapLayerType slt = contains( convertIdentifier( identifier ) );
        if ( slt != null ) {
            slt.setEndNode( selected );
        }
    }

    /**
     * sets edges for being snapping target
     * 
     * @param identifier
     * @param selected
     */
    public void selectSnappingTargetEdge( Identifier identifier, boolean selected ) {
        SnapLayerType slt = contains( convertIdentifier( identifier ) );
        if ( slt != null ) {
            slt.setEdge( selected );
        }
    }

    /**
     * sets edges centers for being snapping target
     * 
     * @param identifier
     * @param selected
     */
    public void selectSnappingTargetEdgeCenter( Identifier identifier, boolean selected ) {
        SnapLayerType slt = contains( convertIdentifier( identifier ) );
        if ( slt != null ) {
            slt.setEdgeCenter( selected );
        }
    }

    /**
     * 
     * @param identifier
     * @return <code>true</code> if layer is selected for being target for snapping vertices
     */
    public boolean isSelectedForSnappingVertices( Identifier identifier ) {
        SnapLayerType slt = contains( convertIdentifier( identifier ) );
        if ( slt != null ) {
            return slt.isVertex();
        }
        return false;
    }

    /**
     * 
     * @param identifier
     * @return <code>true</code> if layer is selected for being target for snapping start nodes
     */
    public boolean isSelectedForSnappingStartNodes( Identifier identifier ) {
        SnapLayerType slt = contains( convertIdentifier( identifier ) );
        if ( slt != null ) {
            return slt.isStartNode();
        }
        return false;
    }

    /**
     * 
     * @param identifier
     * @return <code>true</code> if layer is selected for being target for snapping end nodes
     */
    public boolean isSelectedForSnappingEndNodes( Identifier identifier ) {
        SnapLayerType slt = contains( convertIdentifier( identifier ) );
        if ( slt != null ) {
            return slt.isEndNode();
        }
        return false;
    }

    /**
     * 
     * @param identifier
     * @return <code>true</code> if layer is selected for being target for snapping edges
     */
    public boolean isSelectedForSnappingEdges( Identifier identifier ) {
        SnapLayerType slt = contains( convertIdentifier( identifier ) );
        if ( slt != null ) {
            return slt.isEdge();
        }
        return false;
    }

    /**
     * 
     * @param identifier
     * @return <code>true</code> if layer is selected for being target for snapping edge centers
     */
    public boolean isSelectedForSnappingEdgeCenters( Identifier identifier ) {
        SnapLayerType slt = contains( convertIdentifier( identifier ) );
        if ( slt != null ) {
            return slt.isEdgeCenter();
        }
        return false;
    }

    /**
     * 
     * @param identifier
     * @return <code>true</code> if a layer is selected or at least one snapping target
     */
    public boolean isSelectedForSnapping( Identifier identifier ) {
        SnapLayerType slt = contains( convertIdentifier( identifier ) );
        if ( slt != null ) {
            return slt.isEdgeCenter() || slt.isEdge() || slt.isEndNode() || slt.isStartNode() || slt.isVertex();
        }
        return false;
    }

    /**
     * 
     * @param identifier
     * @return list of snapping actions a layer is selected for
     */
    public List<SNAPTARGET> getSelectedForSnappingList( Identifier identifier ) {
        List<SNAPTARGET> list = new ArrayList<SNAPTARGET>();
        SnapLayerType slt = contains( convertIdentifier( identifier ) );
        if ( slt != null ) {
            if ( slt.isEdgeCenter() ) {
                list.add( SNAPTARGET.EdgeCenter );
            }
            if ( slt.isEdge() ) {
                list.add( SNAPTARGET.Edge );
            }
            if ( slt.isStartNode() ) {
                list.add( SNAPTARGET.StartNode );
            }
            if ( slt.isEndNode() ) {
                list.add( SNAPTARGET.EndNode );
            }
            if ( slt.isVertex() ) {
                list.add( SNAPTARGET.Vertex );
            }
        }
        return list;
    }

    private IdentifierType convertIdentifier( Identifier identifier ) {
        IdentifierType idt = new IdentifierType();
        if ( identifier.getNamespace() != null ) {
            idt.setNamespace( identifier.getNamespace().toASCIIString() );
        }
        idt.setValue( identifier.getValue() );
        return idt;
    }
}
