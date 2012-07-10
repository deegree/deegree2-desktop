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

import java.io.PrintStream;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.deegree.crs.configuration.CRSConfiguration;
import org.deegree.crs.configuration.CRSProvider;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.MapUtils;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.graphics.transformation.WorldToScreenTransform;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.ChangeListener;
import org.deegree.igeo.ValueChangedEvent;
import org.deegree.igeo.config.CRSType;
import org.deegree.igeo.config.EnvelopeType;
import org.deegree.igeo.config.ExternalResourceType;
import org.deegree.igeo.config.MapModelType;
import org.deegree.igeo.config.TargetDeviceType;
import org.deegree.igeo.config.Util;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.MapModelChangedEvent.CHANGE_TYPE;
import org.deegree.igeo.modules.ModuleCreator;
import org.deegree.model.Identifier;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class MapModel implements ChangeListener {

    public static final String SELECTION_EDITING = "editing";

    public static final String SELECTION_ACTION = "action";
    
    public static final ILogger LOG = LoggerFactory.getLogger( MapModel.class );

    protected List<ChangeListener> listeners = new ArrayList<ChangeListener>();

    private MapModelType mmType;

    private List<LayerGroup> layerGroups;

    private ApplicationContainer<?> appContainer;

    /**
     * @param appContainer
     * @param mmType
     * @param LayerGroups
     * @param description
     */
    public MapModel( ApplicationContainer<?> appContainer, MapModelType mmType ) {
        this.mmType = mmType;
        this.layerGroups = new ArrayList<LayerGroup>();
        this.appContainer = appContainer;
    }

    /**
     * @return the identifier
     */
    public Identifier getIdentifier() {
        return Util.convertIdentifier( mmType.getIdentifier() );
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj == null || !( obj instanceof MapModel ) ) {
            return false;
        }
        return getIdentifier().equals( ( (MapModel) obj ).getIdentifier() );

    }

    /**
     * @return the name
     */
    public String getName() {
        return mmType.getName();
    }

    /**
     * 
     * @return <code>true</code> if a {@link MapModel} is the active/current one
     */
    public boolean isCurrent() {
        return mmType.isCurrent();
    }

    /**
     * 
     * @param current
     *            true if a {@link MapModel} shall be marked as active/current one
     */
    public void setCurrent( boolean current ) {
        mmType.setCurrent( current );
    }

    /**
     * 
     * @return application container which is owner of a {@link ModuleCreator} instance
     */
    public ApplicationContainer<?> getApplicationContainer() {
        return appContainer;
    }

    /**
     * 
     * @return description
     */
    public String getDescription() {
        return mmType.getDescription();
    }

    /**
     * 
     * @param description
     *            map model description
     */
    public void setDescription( String description ) {
        mmType.setDescription( description );
    }

    /**
     * 
     * @return current selected extent
     * @throws UnknownCRSException
     */
    public Envelope getEnvelope() {
        return Util.convertEnvelope( mmType.getExtent() );
    }

    /**
     * 
     * @return map models CRS
     */
    public CoordinateSystem getCoordinateSystem() {
        return Util.convertEnvelope( mmType.getExtent() ).getCoordinateSystem();
    }

    /**
     * 
     * @param visitor
     * @throws Exception
     */
    public void walkLayerTree( MapModelVisitor visitor )
                            throws Exception {
        for ( LayerGroup layerGroup : layerGroups ) {
            applyVisitor( layerGroup, visitor );
        }
    }

    private void applyVisitor( LayerGroup layerGroup, MapModelVisitor visitor )
                            throws Exception {
        visitor.visit( layerGroup );
        List<MapModelEntry> entries = layerGroup.getMapModelEntries();
        for ( MapModelEntry entry : entries ) {
            if ( entry instanceof Layer ) {
                visitor.visit( (Layer) entry );
            } else {
                applyVisitor( (LayerGroup) entry, visitor );
            }
        }
    }

    /**
     * 
     * @param envelope
     */
    public void setEnvelope( Envelope envelope ) {

        if ( !envelope.equals( getEnvelope() ) ) {
            EnvelopeType value = new EnvelopeType();
            value.setCrs( envelope.getCoordinateSystem().getPrefixedName() );
            value.setMinx( envelope.getMin().getX() );
            value.setMiny( envelope.getMin().getY() );
            value.setMaxx( envelope.getMax().getX() );
            value.setMaxy( envelope.getMax().getY() );
            mmType.setExtent( value );
            fireMapModelChangedEvent( CHANGE_TYPE.extentChanged, envelope );
        }
    }

    /**
     * 
     * @return list of assigned external resources/documents
     */
    public List<ExternalResourceType> getExternalResources() {
        return Collections.unmodifiableList( mmType.getExternalResource() );
    }

    /**
     * 
     * @param externalResources
     */
    public void setExternalResources( List<ExternalResourceType> externalResources ) {
        List<ExternalResourceType> tmp = mmType.getExternalResource();
        tmp.clear();
        tmp.addAll( externalResources );
    }

    /**
     * 
     * @param externalResource
     */
    public void addExternalResources( ExternalResourceType externalResource ) {
        List<ExternalResourceType> tmp = mmType.getExternalResource();
        tmp.add( externalResource );
    }

    /**
     * 
     * @param externalResource
     */
    public void removeExternalResources( ExternalResourceType externalResource ) {
        List<ExternalResourceType> tmp = mmType.getExternalResource();
        tmp.remove( externalResource );
    }

    /**
     * 
     * @param identifier
     * @return {@link MapModelEntry} matching the passed {@link Identifier} or null if no {@link MapModelEntry} is
     *         matching
     */
    public MapModelEntry getMapModelEntry( final Identifier identifier ) {
        final MapModelEntry[] mme = new MapModelEntry[1];
        try {
            walkLayerTree( new MapModelVisitor() {

                public void visit( Layer layer )
                                        throws Exception {
                    if ( layer.getIdentifier().equals( identifier ) ) {
                        mme[0] = layer;
                    }
                }

                public void visit( LayerGroup layerGroup )
                                        throws Exception {
                    if ( layerGroup.getIdentifier().equals( identifier ) ) {
                        mme[0] = layerGroup;
                    }
                }

            } );
        } catch ( Exception e ) {
            // should never happen
        }
        return mme[0];
    }

    /**
     * 
     * @param layer
     *            layer to remove
     */
    public void remove( MapModelEntry mapModelEntry ) {
        if ( mapModelEntry instanceof Layer ) {
            ( (Layer) mapModelEntry ).getParent().removeLayer( (Layer) mapModelEntry );
            fireMapModelChangedEvent( CHANGE_TYPE.layerRemoved, mapModelEntry );
        } else if ( mapModelEntry instanceof LayerGroup ) {
            LayerGroup layerGroup = (LayerGroup) mapModelEntry;
            if ( layerGroup.getParent() != null ) {
                layerGroup.getParent().removeLayerGroup( layerGroup );
            } else {
                mmType.getLayerGroup().remove( layerGroup.getLayerGroupType() );
            }
            fireMapModelChangedEvent( CHANGE_TYPE.layerGroupRemoved, layerGroup );
        }
    }

    /**
     * 
     * @param mapModelEntry
     *            {@link MapModelEntry} to be inserted
     * @param parent
     *            if <code>null</code> root node of layertree will be used as parent
     * @param antecessor
     *            if <code>null</code> layer will be inserted directly underneath its parent
     * @param first
     *            if true layer will be inserted as first layer of a group if antecessor == null
     */
    public void insert( final MapModelEntry mapModelEntry, LayerGroup parent, MapModelEntry antecessor, boolean first ) {

        if ( exists( mapModelEntry.getIdentifier() ) ) {
            throw new MapModelException( Messages.get( "$DG10087", mapModelEntry.getIdentifier() ) );
        }

        if ( mapModelEntry instanceof Layer ) {
            insertLayer( (Layer) mapModelEntry, parent, antecessor, first );
        } else if ( mapModelEntry instanceof LayerGroup ) {
            insertLayerGroup( (LayerGroup) mapModelEntry, parent, antecessor, first );
        }
    }

    /**
     * 
     * @param identifier
     * @return <code>true</code> if layer with passed {@link Identifier} exists 
     */
    public boolean exists( final Identifier identifier ) {
        try {
            // check if layer already exists in map model
            walkLayerTree( new MapModelVisitor() {

                public void visit( Layer layer )
                                        throws Exception {
                    if ( layer.getIdentifier().equals( identifier ) ) {
                        throw new MapModelException( Messages.get( "$DG10087", identifier ) );
                    }
                }

                public void visit( LayerGroup layerGroup )
                                        throws Exception {
                    if ( layerGroup.getIdentifier().equals( identifier ) ) {
                        throw new MapModelException( Messages.get( "$DG10088", identifier ) );
                    }
                }

            } );
        } catch ( Exception e ) {
            if ( e instanceof MapModelException ) {
                return true;
            } else {
                throw new MapModelException( e.getMessage(), e );
            }
        }
        return false;
    }
    
    /**
     * 
     * @param identifier
     * @return layer matching passed {@link Identifier}
     */
    @SuppressWarnings("unchecked")
    public Layer getLayerByIdentifier(final Identifier identifier) {
        MapModelVisitor v = new MapModelVisitor() {
            
            private Layer layer;

            public void visit( Layer layer )
                                    throws Exception {
                if ( layer.getIdentifier().equals( identifier ) ) {
                    this.layer = layer; 
                }
            }

            public void visit( LayerGroup layerGroup )
                                    throws Exception {                    
            }
            
            @SuppressWarnings("unused")
            public Layer getLayer() {
                return layer;
            }

        };
        try {
            walkLayerTree( v );
        } catch ( Exception e ) {
            throw new MapModelException( e.getMessage(), e );
        }        
        
        Class<MapModelVisitor> c = (Class<MapModelVisitor>) v.getClass();
        Method m;
        try {
            m = c.getMethod( "getLayer", (Class[])null );
            return (Layer) m.invoke( v, (Object[])null );
        } catch ( Exception e ) {
            throw new MapModelException( e.getMessage(), e );
        }

       
    }

    /**
     * 
     * @param layer
     * @param parent
     *            if <code>null</code> root node of layertree will be used as parent
     * @param antecessor
     *            if <code>null</code> layer will be inserted directly underneath its parent
     * @param first
     *            if true layer will be inserted as first layer of a group if antecessor == null
     */
    private void insertLayer( Layer layer, LayerGroup parent, MapModelEntry antecessor, boolean first ) {
        insertLayer( layer, parent, antecessor, layerGroups, first );
        fireMapModelChangedEvent( CHANGE_TYPE.layerInserted, layer );
    }

    private void insertLayer( Layer layer, LayerGroup parent, MapModelEntry antecessor, List<LayerGroup> lgs,
                              boolean first ) {
        for ( LayerGroup layerGroup : lgs ) {
            if ( parent != null && parent.equals( layerGroup ) ) {
                layerGroup.insert( layer, antecessor, first );
                break;
            } else {
                insertLayer( layer, parent, antecessor, layerGroup.getLayerGroups(), first );
            }
        }
    }

    /**
     * 
     * @param layerGroup
     * @param parent
     *            if <code>null</code> root node of layertree will be used as parent
     * @param antecessor
     *            if <code>null</code> layer will be inserted directly underneath its parent
     * @param first
     *            if true layer will be inserted as first layergroup of a group if antecessor == null
     */
    private void insertLayerGroup( LayerGroup layerGroup, LayerGroup parent, MapModelEntry antecessor, boolean first ) {
        if ( parent == null ) {
            layerGroups.add( layerGroup );
        }
        insertLayerGroup( layerGroup, parent, antecessor, layerGroups, first );
        fireMapModelChangedEvent( CHANGE_TYPE.layerGroupInserted, layerGroup );
    }

    private void insertLayerGroup( LayerGroup lg, LayerGroup parent, MapModelEntry antecessor, List<LayerGroup> lgs,
                                   boolean first ) {
        for ( LayerGroup layerGroup : lgs ) {
            if ( parent != null && parent.equals( layerGroup ) ) {
                layerGroup.insert( lg, antecessor, first );
                break;
            } else {
                insertLayerGroup( lg, parent, antecessor, layerGroup.getLayerGroups(), first );
            }
        }
    }

    /**
     * moves the passed layer underneath a new parent and before the passed antecessor.
     * 
     * @param layer
     * @param parent
     *            if <code>null</code> root node of layertree will be used as parent
     * @param antecessor
     *            if <code>null</code> layer will be inserted directly underneath its parent
     */
    public void move( MapModelEntry mapModelEntry, LayerGroup parent, MapModelEntry antecessor, boolean first ) {
        if ( mapModelEntry instanceof Layer ) {
            move( (Layer) mapModelEntry, parent, antecessor, first );
        } else if ( mapModelEntry instanceof LayerGroup ) {
            move( (LayerGroup) mapModelEntry, parent, antecessor, first );
        }
        if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
            printModel( System.out );
        }
    }

    /**
     * moves the passed layer underneath a new parent and before the passed antecessor.
     * 
     * @param layer
     * @param parent
     *            if <code>null</code> root node of layertree will be used as parent
     * @param antecessor
     *            if <code>null</code> layer will be inserted directly underneath its parent
     * @param first
     *            if true layer will be inserted as first layer of a group if antecessor == null
     */
    private void move( Layer layer, LayerGroup parent, MapModelEntry antecessor, boolean first ) {
        if ( !layer.equals( antecessor ) ) {
            layer.getParent().removeLayer( layer );
            insertLayer( layer, parent, antecessor, layerGroups, first );
            fireMapModelChangedEvent( CHANGE_TYPE.layerOrderChanged, layer );
        }
    }

    /**
     * moves the passed layergroup underneath a new parent and before the passed antecessor.
     * 
     * @param layerGroup
     * @param parent
     *            if <code>null</code> root node of layertree will be used as parent
     * @param antecessor
     *            if <code>null</code> layergroup will be inserted directly underneath its parent
     */
    private void move( LayerGroup layerGroup, LayerGroup parent, MapModelEntry antecessor, boolean first ) {
        if ( !layerGroup.equals( antecessor ) ) {
            layerGroup.getParent().removeLayerGroup( layerGroup );
            insertLayerGroup( layerGroup, parent, antecessor, layerGroups, first );
            fireMapModelChangedEvent( CHANGE_TYPE.layerOrderChanged, layerGroup );
        }
    }

    /**
     * 
     * @return maximum extent of a MapModel
     */
    public Envelope getMaxExtent() {
        return Util.convertEnvelope( mmType.getMaxExtent() );
    }

    /**
     * 
     * @param maxExtent
     */
    public void setMaxExtent( Envelope envelope ) {
        EnvelopeType value = new EnvelopeType();
        value.setCrs( envelope.getCoordinateSystem().getPrefixedName() );
        value.setMinx( envelope.getMin().getX() );
        value.setMiny( envelope.getMin().getY() );
        value.setMaxx( envelope.getMax().getX() );
        value.setMaxy( envelope.getMax().getY() );
        mmType.setMaxExtent( value );
    }

    /**
     * @return the targetDevice
     */
    public TargetDeviceType getTargetDevice() {
        return mmType.getTargetDevice();
    }

    /**
     * 
     * @return transformation object for transforming geo coordinates to target device coordinates and vice versa
     */
    public GeoTransform getToTargetDeviceTransformation() {
        int w = mmType.getTargetDevice().getPixelWidth();
        int h = mmType.getTargetDevice().getPixelHeight();
        Envelope target = GeometryFactory.createEnvelope( 0, 0, w - 1, h - 1, null );
        return new WorldToScreenTransform( getEnvelope(), target );
    }

    /**
     * 
     * @param action
     * @return list of {@link MapModelEntry} selected for the passed action; including {@link Layer}s and
     *         {@link LayerGroup}s
     */
    public List<MapModelEntry> getMapModelEntriesSelectedForAction( String action ) {
        List<MapModelEntry> tmp = new ArrayList<MapModelEntry>();
        getMapModelEntriesSelectedForAction( layerGroups, action, tmp );
        return Collections.unmodifiableList( tmp );
    }

    private void getMapModelEntriesSelectedForAction( List<LayerGroup> lgs, String action, List<MapModelEntry> collector ) {
        for ( LayerGroup layerGroup : lgs ) {
            if ( layerGroup.getSelectedFor().contains( action ) ) {
                collector.add( layerGroup );
            }
            List<MapModelEntry> mapModelEntries = layerGroup.getMapModelEntries();
            for ( MapModelEntry mapModelEntrry : mapModelEntries ) {
                if ( mapModelEntrry.getSelectedFor().contains( action ) ) {
                    collector.add( mapModelEntrry );
                }
            }
            getMapModelEntriesSelectedForAction( layerGroup.getLayerGroups(), action, collector );
        }
    }

    /**
     * 
     * @param action
     * @return list of {@link LayerGroup} selected for the passed action
     */
    public List<LayerGroup> getLayerGroupsSelectedForAction( String action ) {
        List<LayerGroup> tmp = new ArrayList<LayerGroup>();
        getLayerGroupsForAction( layerGroups, action, tmp );
        return Collections.unmodifiableList( tmp );
    }

    private void getLayerGroupsForAction( List<LayerGroup> lgs, String action, List<LayerGroup> collector ) {
        for ( LayerGroup layerGroup : lgs ) {
            List<LayerGroup> mapModelEntries = layerGroup.getLayerGroups();
            for ( LayerGroup mapModelEntrry : mapModelEntries ) {
                if ( mapModelEntrry.getSelectedFor().contains( action ) ) {
                    collector.add( mapModelEntrry );
                }
            }
            getLayerGroupsForAction( layerGroup.getLayerGroups(), action, collector );
        }
    }

    /**
     * 
     * @param action
     * @return list of {@link Layer} selected for the passed action
     */
    public List<Layer> getLayersSelectedForAction( String action ) {
        List<Layer> tmp = new ArrayList<Layer>();
        getLayersForAction( layerGroups, action, tmp );
        return Collections.unmodifiableList( tmp );
    }

    private void getLayersForAction( List<LayerGroup> lgs, String action, List<Layer> collector ) {
        for ( LayerGroup layerGroup : lgs ) {
            List<Layer> mapModelEntries = layerGroup.getLayers();
            for ( Layer mapModelEntrry : mapModelEntries ) {
                if ( mapModelEntrry.getSelectedFor().contains( action ) ) {
                    collector.add( mapModelEntrry );
                }
            }
            getLayersForAction( layerGroup.getLayerGroups(), action, collector );
        }
    }

    private void fireMapModelChangedEvent( CHANGE_TYPE changeType, Object value ) {
        MapModelChangedEvent event = new MapModelChangedEvent( changeType, this, value, null );
        for ( int i = 0; i < this.listeners.size(); i++ ) {
            listeners.get( i ).valueChanged( event );
        }
    }

    /**
     * @param listener
     *            to be invoked if a {@link MapModel} has been changed
     */
    public void addChangeListener( ChangeListener listener ) {
        this.listeners.add( listener );
    }

    /**
     * @param listener
     *            to be removed
     */
    public void removeChangeListener( ChangeListener listener ) {
        this.listeners.remove( listener );
    }

    /**
     * 
     * @return list of registered change listeners
     */
    public List<ChangeListener> getChangeListener() {
        return this.listeners;
    }

    /**
     * @param event
     */
    public void valueChanged( ValueChangedEvent event ) {
        if ( event instanceof LayerChangedEvent ) {
            event = new MapModelChangedEvent( CHANGE_TYPE.layerStateChanged, this, event.getValue(), event );
            for ( int i = 0; i < this.listeners.size(); i++ ) {
                listeners.get( i ).valueChanged( event );
            }
        } else if ( event instanceof MapModelChangedEvent ) {
            for ( int i = 0; i < this.listeners.size(); i++ ) {
                listeners.get( i ).valueChanged( event );
            }
        }
    }

    /**
     * 
     * @param layerGroups
     */
    public void setLayerGroups( List<LayerGroup> layerGroups ) {
        this.layerGroups = layerGroups;
    }

    /**
     * @return the layerGroups
     */
    public List<LayerGroup> getLayerGroups() {
        return layerGroups;
    }

    /**
     * 
     * @param isVisible
     *            if set to true just layers set to be visible will be returned
     * @return all layers as a list
     */
    public List<Layer> getLayersAsList( final boolean isVisible ) {
        final List<Layer> list = new ArrayList<Layer>( 50 );
        try {
            walkLayerTree( new MapModelVisitor() {

                public void visit( LayerGroup layerGroup )
                                        throws Exception {
                    // do nothing
                }

                public void visit( Layer layer )
                                        throws Exception {
                    if ( ( isVisible && layer.isVisible() ) || !isVisible ) {
                        list.add( layer );
                    }
                }
            } );
        } catch ( Exception e ) {
            // ignore
        }
        return list;
    }
    
    /**
     * 
     * @return current scale denominator of a map model
     */
    public double getScaleDenominator() {
        Envelope env = getEnvelope();
        int h = getTargetDevice().getPixelHeight();
        int w = getTargetDevice().getPixelWidth();
        return MapUtils.calcScale( w, h, env, getCoordinateSystem(), MapUtils.DEFAULT_PIXEL_SIZE );
    }

    /**
     * 
     * @param supportedCRSs
     * @return array of supported CRS
     */
    public CRSEntry[] getSupportedCRSs() {
        CRSEntry[] entries;
        if ( mmType.getSupportedCRS() != null ) {
            List<CRSType> supportedCRSs = mmType.getSupportedCRS().getCRS();
            if ( supportedCRSs.size() > 0 ) {
                entries = new CRSEntry[supportedCRSs.size()];
                // user defined list of supported CRS is available
                for ( int i = 0; i < supportedCRSs.size(); i++ ) {
                    CRSType crs = supportedCRSs.get( i );
                    entries[i] = new CRSEntry( crs.getCode(), crs.getName() );
                }
            } else {
                // use all supported CRSs
                CRSProvider pr = CRSConfiguration.getCRSConfiguration().getProvider();
                List<String> tmp = pr.getAvailableCRSIds();
                List<CRSEntry> tmp2 = new ArrayList<CRSEntry>( tmp.size() / 2 );
                for ( String string : tmp ) {
                    if ( string.toLowerCase().startsWith( "epsg:" ) ) {
                        tmp2.add( new CRSEntry( string, string ) );
                    }
                }
                Collections.sort( tmp2 );
                entries = tmp2.toArray( new CRSEntry[tmp2.size()] );
            }
        } else {
            entries = new CRSEntry[0];
        }
        return entries;
    }

    /**
     * prints this mapmodel to passed stream
     * 
     * @param os
     */
    public void printModel( final PrintStream os ) {
        
        os.println("=========== Map Model ==========================");

        try {
            walkLayerTree( new MapModelVisitor() {

                
                public void visit( LayerGroup layerGroup )
                                        throws Exception {
                    String s = layerGroup.getTitle();
                    LayerGroup lg = layerGroup.getParent();
                    while ( lg != null ) {
                        s = lg.getTitle() + "." + s;
                        lg = lg.getParent();
                    }
                    os.println( "LG: " + s );
                }

                public void visit( Layer layer )
                                        throws Exception {
                    String s = layer.getTitle();
                    LayerGroup lg = layer.getParent();
                    while ( lg != null ) {
                        s = lg.getTitle() + "." + s;
                        lg = lg.getParent();
                    }
                    os.println( "Layer: " + s );
                }
            } );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return getName();
    }

}