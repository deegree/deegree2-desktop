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

package org.deegree.igeo.mapmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.deegree.igeo.ChangeListener;
import org.deegree.igeo.ValueChangedEvent;
import org.deegree.igeo.config.IdentifierType;
import org.deegree.igeo.config.LayerGroupType;
import org.deegree.igeo.config.Util;
import org.deegree.igeo.mapmodel.LayerGroupChangedEvent.LAYERGROUP_CHANGE_TYPE;
import org.deegree.model.Identifier;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class LayerGroup implements MapModelEntry {

    protected List<ChangeListener> listeners = new ArrayList<ChangeListener>();

    private List<Layer> layers;

    private List<LayerGroup> layerGroups;

    private List<MapModelEntry> entries;

    private LayerGroupType layerGroupType;

    private LayerGroup parent;

    private MapModel owner;

    /**
     * 
     * @param owner
     */
    private LayerGroup( MapModel owner ) {
        this.owner = owner;
        layers = new ArrayList<Layer>();
        layerGroups = new ArrayList<LayerGroup>();
        entries = new ArrayList<MapModelEntry>();
    }

    /**
     * 
     * @param owner
     * @param parent
     * @param layerGroupType
     */
    public LayerGroup( MapModel owner, LayerGroup parent, LayerGroupType layerGroupType ) {
        this( owner );
        this.owner = owner;
        this.layerGroupType = layerGroupType;
        this.parent = parent;
    }

    /**
     * 
     * @param owner
     * @param identifier
     * @param title
     * @param abstract_
     */
    public LayerGroup( MapModel owner, Identifier identifier, String title, String abstract_ ) {
        this( owner );
        layerGroupType = new LayerGroupType();
        layerGroupType.setAbstract( abstract_ );
        IdentifierType id = new IdentifierType();
        id.setValue( identifier.getValue() );
        if ( identifier.getNamespace() != null ) {
            id.setNamespace( identifier.getNamespace().toASCIIString() );
        }
        layerGroupType.setIdentifier( id );
        layerGroupType.setTitle( title );
        layerGroupType.setQueryable( true );
        layerGroupType.setVisible( true );
    }

    /**
     * 
     * @return layers identifier
     */
    public Identifier getIdentifier() {
        return Util.convertIdentifier( layerGroupType.getIdentifier() );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.mapmodel.MapModelEntry#getOwner()
     */
    public MapModel getOwner() {
        return owner;
    }

    /**
     * 
     * @return list of action a layer is selected for
     */
    public List<String> getSelectedFor() {
        return layerGroupType.getSelectedFor();
    }

    /**
     * 
     * @param selectedFor
     */
    public void setSelectedFor( List<String> selectedFor ) {
        List<String> tmp = layerGroupType.getSelectedFor();
        tmp.clear();
        tmp.addAll( selectedFor );
        fireLayerGroupChangedEvent( LAYERGROUP_CHANGE_TYPE.selectedForChanged, selectedFor );
    }

    /**
     * 
     * @param selectedFor
     */
    public void addSelectedFor( String selectedFor ) {
        List<String> tmp = layerGroupType.getSelectedFor();
        if ( !tmp.contains( selectedFor ) ) {
            tmp.add( selectedFor );
            fireLayerGroupChangedEvent( LAYERGROUP_CHANGE_TYPE.selectedForChanged, selectedFor );
        }
    }

    /**
     * 
     * @param selectedFor
     */
    public void removeSelectedFor( String selectedFor ) {
        List<String> tmp = layerGroupType.getSelectedFor();
        if ( tmp.contains( selectedFor ) ) {
            tmp.remove( selectedFor );
            fireLayerGroupChangedEvent( LAYERGROUP_CHANGE_TYPE.selectedForChanged, selectedFor );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.mapmodel.MapModelEntry#isVisible()
     */
    public boolean isVisible() {
        return layerGroupType.isVisible();
    }

    /**
     * 
     * @param visible
     */
    public void setVisible( boolean visible ) {
        if ( visible != layerGroupType.isVisible() ) {
            layerGroupType.setVisible( visible );
            fireLayerGroupChangedEvent( LAYERGROUP_CHANGE_TYPE.visibilityChanged, visible );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.mapmodel.MapModelEntry#isQueryable()
     */
    public boolean isQueryable() {
        return layerGroupType.isQueryable();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.mapmodel.MapModelEntry#setQueryable(boolean)
     */
    public void setQueryable( boolean queryable ) {
        layerGroupType.setQueryable( queryable );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.ChangeListener#valueChanged(org.deegree.igeo.ValueChangedEvent)
     */
    public void valueChanged( ValueChangedEvent event ) {
        // TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.mapmodel.MapModelEntry#addChangeListener(org.deegree.igeo.ChangeListener)
     */
    public void addChangeListener( ChangeListener listener ) {
        this.listeners.add( listener );
    }

    /**
     * @return the parent
     */
    public LayerGroup getParent() {
        return parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.mapmodel.MapModelEntry#getAntecessor()
     */
    public Layer getAntecessor() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param parent
     *            the parent to set
     */
    public void setParent( LayerGroup parent ) {
        if ( parent != null && !parent.equals( this.parent ) ) {
            // remove from old parent
            if ( this.parent != null ) {
                this.parent.removeLayerGroup( this );
            }
            this.parent = parent;
        } else if ( parent == null ) {
            this.parent = null;
        }
    }

    /**
     * @return the layerGroups
     */
    public List<LayerGroup> getLayerGroups() {
        return Collections.unmodifiableList( layerGroups );
    }

    /**
     * @return the layerGroupType
     */
    public LayerGroupType getLayerGroupType() {
        return layerGroupType;
    }

    /**
     * @return the layers
     */
    public List<Layer> getLayers() {
        return Collections.unmodifiableList( layers );
    }

    /**
     * @return the entries
     */
    public List<MapModelEntry> getMapModelEntries() {
        return Collections.unmodifiableList( entries );
    }

    /**
     * 
     * @param layer
     */
    public void addLayer( Layer layer ) {
        if ( !layers.contains( layer ) ) {
            layers.add( layer );
            entries.add( layer );
        }
        List<Object> list = layerGroupType.getLayerOrLayerReferenceOrLayerGroup();
        if ( !list.contains( layer.getLayerType() ) ) {
            layerGroupType.getLayerOrLayerReferenceOrLayerGroup().add( layer.getLayerType() );
        }
    }

    /**
     * 
     * @param layerGroup
     */
    public void addLayerGroup( LayerGroup layerGroup ) {
        layerGroups.add( layerGroup );
        entries.add( layerGroup );
        List<Object> list = layerGroupType.getLayerOrLayerReferenceOrLayerGroup();
        if ( !list.contains( layerGroup.getLayerGroupType() ) ) {
            layerGroupType.getLayerOrLayerReferenceOrLayerGroup().add( layerGroup.getLayerGroupType() );
        }
    }

    /**
     * 
     * @param layer
     */
    public void removeLayer( Layer layer ) {
        layerGroupType.getLayerOrLayerReferenceOrLayerGroup().remove( layer.getLayerType() );
        layers.remove( layer );
        entries.remove( layer );
        layer.setParent( null );
    }

    /**
     * 
     * @param layerGroup
     */
    public void removeLayerGroup( LayerGroup layerGroup ) {
        layerGroupType.getLayerOrLayerReferenceOrLayerGroup().remove( layerGroup.getLayerGroupType() );
        layerGroups.remove( layerGroup );
        entries.remove( layerGroup );
        layerGroup.setParent( null );
    }

    /**
     * 
     * @return title of a layergroup
     */
    public String getTitle() {
        return layerGroupType.getTitle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.mapmodel.MapModelEntry#getAbstract()
     */
    public String getAbstract() {
        return layerGroupType.getAbstract();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.mapmodel.MapModelEntry#setAbstract(java.lang.String)
     */
    public void setAbstract( String value ) {
        layerGroupType.setAbstract( value );
    }

    /**
     * 
     * @param title
     *            new title of a layergroup
     */
    public void setTitle( String title ) {
        layerGroupType.setTitle( title );
    }

    /**
     * 
     * @param layer
     * @param antecessor
     * @param first
     */
    public void insert( Layer layer, MapModelEntry antecessor, boolean first ) {
        if ( layer.getParent() == null || !layer.getParent().equals( parent ) ) {
            layers.remove( layer );
            layers.add( layer );
            layer.setParent( this );
            int i = 0;
            while ( i < entries.size() && !entries.get( i ).equals( antecessor ) ) {
                i++;
            }
            layerGroupType.getLayerOrLayerReferenceOrLayerGroup().remove( layer.getLayerType() );
            if ( i >= entries.size() - 1 ) {
                if ( first && antecessor == null ) {
                    entries.add( 0, layer );
                    layerGroupType.getLayerOrLayerReferenceOrLayerGroup().add( 0, layer.getLayerType() );
                } else {
                    entries.add( layer );
                    layerGroupType.getLayerOrLayerReferenceOrLayerGroup().add( layer.getLayerType() );
                }
            } else {
                if ( first && antecessor == null ) {
                    entries.add( 0, layer );
                    layerGroupType.getLayerOrLayerReferenceOrLayerGroup().add( 0, layer.getLayerType() );
                } else {
                    entries.add( i + 1, layer );
                    layerGroupType.getLayerOrLayerReferenceOrLayerGroup().add( i + 1, layer.getLayerType() );
                }
            }
        }
    }

    /**
     * 
     * @param layerGroup
     * @param antecessor
     * @param first
     */
    public void insert( LayerGroup layerGroup, MapModelEntry antecessor, boolean first ) {
        if ( layerGroup.getParent() == null || !layerGroup.getParent().equals( parent ) ) {
            // register as child
            layerGroups.add( layerGroup );
            // that this a parent
            layerGroup.setParent( this );

            int i = 0;
            while ( i < entries.size() && !entries.get( i ).equals( antecessor ) ) {
                i++;
            }
            if ( i >= entries.size() - 1 ) {
                if ( first && antecessor == null ) {
                    entries.add( 0, layerGroup );
                    layerGroupType.getLayerOrLayerReferenceOrLayerGroup().add( 0, layerGroup.getLayerGroupType() );
                } else {
                    entries.add( layerGroup );
                    layerGroupType.getLayerOrLayerReferenceOrLayerGroup().add( layerGroup.getLayerGroupType() );
                }
            } else {
                if ( first && antecessor == null ) {
                    entries.add( 0, layerGroup );
                    layerGroupType.getLayerOrLayerReferenceOrLayerGroup().add( 0, layerGroup.getLayerGroupType() );
                } else {
                    entries.add( i + 1, layerGroup );
                    layerGroupType.getLayerOrLayerReferenceOrLayerGroup().add( i + 1, layerGroup.getLayerGroupType() );
                }
            }
        }
    }

    @Override
    public boolean equals( Object other ) {
        if ( other == null || !( other instanceof LayerGroup ) ) {
            return false;
        }
        return getIdentifier().equals( ( (LayerGroup) other ).getIdentifier() );
    }

    @Override
    public String toString() {
        return getTitle();
    }

    /**
     * fires a layer changed event encapsulating a feature changed envent that has caused layer changing
     * 
     * @param changeType
     */
    protected void fireLayerGroupChangedEvent( LAYERGROUP_CHANGE_TYPE changeType, Object value ) {
        LayerGroupChangedEvent event = new LayerGroupChangedEvent( this, value, changeType );
        for ( int i = 0; i < this.listeners.size(); i++ ) {
            this.listeners.get( i ).valueChanged( event );
        }
    }

}
