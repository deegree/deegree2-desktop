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
package org.deegree.igeo.dataadapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.igeo.dataadapter.FeatureChangedEvent.FEATURE_CHANGE_TYPE;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.model.crs.CRSTransformationException;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.deegree.model.filterencoding.OperationDefines;
import org.deegree.model.filterencoding.PropertyName;
import org.deegree.model.filterencoding.SpatialOperation;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Surface;

/**
 * Abstract basis class for all adapter class accessing vector data
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public abstract class FeatureAdapter extends DataAccessAdapter {

    private static final ILogger LOG = LoggerFactory.getLogger( FeatureAdapter.class );

    protected static enum CHANGE_TYPE {
        insert, update, delete
    };

    // protected Map<QualifiedName, Feature> featureTemplates;

    /**
     * feature collections and schemas are stored in static maps where the keys are identifiers of the datasources. This
     * avoids loading data n-times for not lazy loading data sources if more than one adapter is needed for a layer
     */
    protected Map<String, FeatureCollection> featureCollections = new HashMap<String, FeatureCollection>( 100 );

    /**
     * contains a collection of all feature of a data source specified by its identifier that has been inserted
     */
    protected Map<String, List<Changes>> changes = new HashMap<String, List<Changes>>( 100 );

    /**
     * contains a collection of all feature type schemas
     */
    protected Map<String, FeatureType> schemas = new HashMap<String, FeatureType>( 100 );

    /**
     * 
     * @param module
     * @param datasource
     * @param layer
     * @param mapModel
     */
    public FeatureAdapter( Datasource datasource, Layer layer, MapModel mapModel ) {
        super( datasource, layer, mapModel );
        // initial transactional feature collections
        List<Changes> changeList = new ArrayList<Changes>( 100 );
        changes.put( datasource.getName(), changeList );
    }

    /**
     * 
     * @return data as feature collection
     */
    public abstract FeatureCollection getFeatureCollection();

    /**
     * convenience method for returning features of an already loaded feature collection at passed point
     * 
     * @param point
     * @return data as feature collection
     * @throws FilterEvaluationException
     */
    public FeatureCollection getFeatureCollection( Point point )
                            throws FilterEvaluationException {

        PropertyName pn = getGeomPropertyName();
        SpatialOperation so = new SpatialOperation( OperationDefines.INTERSECTS, pn, point );

        return getFeatureCollection( new ComplexFilter( so ) );
    }

    /**
     * transforms a feature collection into map model crs if native crs and map model crs are not the same
     * 
     * @param featureCollection
     *            the {@link FeatureCollection} instance to transform
     * @return feature collection in map model crs
     */
    protected FeatureCollection transformToMapModelCrs( FeatureCollection featureCollection ) {
        CoordinateSystem crs = mapModel.getCoordinateSystem();
        CoordinateSystem nativeCoordinateSystem = datasource.getNativeCoordinateSystem();
        return transform( featureCollection, crs, nativeCoordinateSystem );
    }

    /**
     * transforms a feature collection into native crs if native crs and map model crs are not the same
     * 
     * @param featureCollection
     *            the {@link FeatureCollection} instance to transform
     * @return the feature collection in the natice crs of the datasource
     */
    protected FeatureCollection transformToDatasourceCrs( FeatureCollection featureCollection ) {
        CoordinateSystem crs = mapModel.getCoordinateSystem();
        CoordinateSystem nativeCoordinateSystem = datasource.getNativeCoordinateSystem();
        return transform( featureCollection, nativeCoordinateSystem, crs );
    }

    protected FeatureCollection transform( FeatureCollection featureCollection, CoordinateSystem targetCrs,
                                           CoordinateSystem expectedCrs ) {
        if ( !expectedCrs.equals( targetCrs ) ) {
            GeoTransformer gt = new GeoTransformer( targetCrs );
            try {
                featureCollection = gt.transform( featureCollection );
            } catch ( CRSTransformationException e ) {
                throw new DataAccessException( e.getMessage(), e );
            }
        }
        return featureCollection;
    }

    /**
     * convenience method for returning features of an already loaded feature collection intersecting passed bbox
     * 
     * @param bbox
     * @return data as feature collection
     * @throws FilterEvaluationException
     */
    public FeatureCollection getFeatureCollection( Envelope bbox )
                            throws FilterEvaluationException {
        PropertyName pn = getGeomPropertyName();
        Surface surface = null;
        try {
            surface = GeometryFactory.createSurface( bbox, bbox.getCoordinateSystem() );
        } catch ( GeometryException e ) {
            LOG.logDebug( e.getMessage(), e );
        }
        SpatialOperation so = new SpatialOperation( OperationDefines.BBOX, pn, surface );

        return getFeatureCollection( new ComplexFilter( so ) );
    }

    /**
     * 
     * @return name of first geometry property
     */
    private PropertyName getGeomPropertyName() {
        PropertyType[] pts = getSchema().getProperties();

        // Feature feat = featureCollections.get( datasource.getName() ).getFeature( 0 );
        // FeatureType ft = feat.getFeatureType();
        // PropertyType[] pts = ft.getProperties();
        PropertyName pn = null;
        for ( PropertyType type : pts ) {
            if ( type.getType() == Types.GEOMETRY ) {
                pn = new PropertyName( type.getName() );
                break;
            }
        }
        return pn;
    }

    /**
     * convenience method for returning features of an already loaded feature collection matching passed filter
     * 
     * @param bbox
     * @return data as feature collection
     * @throws FilterEvaluationException
     */
    public FeatureCollection getFeatureCollection( Filter filter )
                            throws FilterEvaluationException {
        FeatureCollection fc = featureCollections.get( datasource.getName() );
        FeatureCollection result = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(), 200 );
        if ( fc != null ) {
            Iterator<Feature> iter = fc.iterator();
            while ( iter.hasNext() ) {
                Feature feat = iter.next();
                if ( filter == null || filter.evaluate( feat ) ) {
                    String msg = StringTools.concat( 200, "Adding feature '", feat.getId(), "' to FeatureCollection." );
                    LOG.logDebug( msg );

                    result.add( feat );
                }
            }
        }
        return result;
    }

    /**
     * 
     * @param feature
     */
    public void insertFeature( Feature feature ) {
        featureCollections.get( datasource.getName() ).add( feature );
        Changes ch = new Changes( feature, CHANGE_TYPE.insert );
        changes.get( datasource.getName() ).add( ch );
        fireFeatureInsertedEvent( feature );
    }

    /**
     * Updates the feature provided.
     * 
     * @param feature
     *            to update
     */
    public Feature updateFeature( Feature feature ) {
        FeatureCollection fc = featureCollections.get( datasource.getName() );
        Feature oldFeature = fc.remove( feature.getId() );
        fc.add( feature );

        // if the feature has been updated before according change tag can be removed from list
        List<Changes> list = changes.get( datasource.getName() );
        ListIterator<Changes> li = list.listIterator();
        while ( li.hasNext() ) {
            Changes change = li.next();
            if ( change.feature.getId().equals( feature.getId() ) && change.changeType == CHANGE_TYPE.update ) {
                li.remove();
            }
        }

        list.add( new Changes( feature, CHANGE_TYPE.update ) );
        fireFeatureUpdatedEvent( feature );
        return oldFeature;
    }

    /**
     * Deletes the feature provided
     * 
     * @param feature
     *            to delete
     */
    public void deleteFeature( Feature feature ) {
        featureCollections.get( datasource.getName() ).remove( feature.getId() );

        List<Changes> list = changes.get( datasource.getName() );
        ListIterator<Changes> li = list.listIterator();
        while ( li.hasNext() ) {
            Changes change = li.next();
            if ( change.feature.getId().equals( feature.getId() ) ) {
                li.remove();
            }
        }

        list.add( new Changes( feature, CHANGE_TYPE.delete ) );
        Changes ch = new Changes( feature, CHANGE_TYPE.delete );
        changes.get( datasource.getName() ).add( ch );
        fireFeatureDeletedEvent( feature );
    }

    /**
     * 
     * @return GML application schema of the adapted data
     */
    public FeatureType getSchema() {
        return schemas.get( datasource.getName() );
    }

    /**
     * 
     * @return a Feature instance created on basis of the underlying GML application schema and defined feature template
     *         or <code>null</code> if passed featureType is not supported by the adapted datasources
     */
    public Feature getDefaultFeature( QualifiedName featureType ) {
        // find GML schema matching passed feature type
        Collection<FeatureType> schemaCol = schemas.values();
        FeatureType ft = null;
        for ( FeatureType tmp : schemaCol ) {
            if ( tmp.getName().equals( featureType ) ) {
                ft = tmp;
                break;
            }
        }
        // if no matching gml schema could be found null will be returned
        if ( ft == null ) {
            LOG.logWarning( "no GML schema found for featuretype: " + featureType );
            return null;
        }

        // create feature instance from GML schema describing passed feature type
        //
        // notice: since deegree WFS does not be type safe for complex features
        // properties of type Types.FEATURE and Types.FEATURECOLLECTION will
        // be set to null. Also properties of type Types.GEOMETRY will be set to
        // null because a features geometry must be digitized via a map client
        PropertyType[] pt = ft.getProperties();
        FeatureProperty[] fp = new FeatureProperty[pt.length];
        for ( int i = 0; i < fp.length; i++ ) {
            fp[i] = FeatureFactory.createFeatureProperty( pt[i].getName(), null );
        }
        return FeatureFactory.createFeature( "ID1", ft, fp );
    }

    /**
     * 
     * @param featureCollection
     */
    public void setFeature( FeatureCollection featureCollection ) {
        featureCollections.put( datasource.getName(), featureCollection );
    }

    /**
     * removes all deleted features, updates feature and inserts features into main feature collection. This is required
     * for lazy loading data sources.
     * 
     */
    protected void updateFeatureCollection() {
        FeatureCollection fc = featureCollections.get( datasource.getName() );
        List<Changes> changeList = changes.get( datasource.getName() );
        for ( Changes change : changeList ) {
            switch ( change.changeType ) {
            case delete: {
                if ( fc.getFeature( change.feature.getId() ) != null ) {
                    fc.remove( change.feature.getId() );
                }
                break;
            }
            case insert: {
                if ( fc.getFeature( change.feature.getId() ) == null ) {
                    fc.add( change.feature );
                }
                break;
            }
            case update: {
                if ( fc.getFeature( change.feature.getId() ) != null ) {
                    fc.remove( change.feature.getId() );
                }
                fc.add( change.feature );
                break;
            }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.DataAccessAdapter#invalidate()
     */
    @Override
    public void invalidate() {
        featureCollections.put( datasource.getName(), null );
        schemas.put( datasource.getName(), null );
    }

    /**
     * notifies all registered listeners that a feature has been changes
     * 
     * @param feature
     */
    protected void fireFeatureUpdatedEvent( Feature feature ) {
        FeatureChangedEvent event = new FeatureChangedEvent( feature, FEATURE_CHANGE_TYPE.updated, this );
        for ( int i = 0; i < this.listeners.size(); i++ ) {
            if ( this.listeners.get( i ) != null ) {
                this.listeners.get( i ).valueChanged( event );
            }
        }
    }

    /**
     * notifies all registered listeners that a feature has been inserted
     * 
     * @param feature
     */
    protected void fireFeatureInsertedEvent( Feature feature ) {
        FeatureChangedEvent event = new FeatureChangedEvent( feature, FEATURE_CHANGE_TYPE.inserted, this );
        for ( int i = 0; i < this.listeners.size(); i++ ) {
            if ( this.listeners.get( i ) != null ) {
                this.listeners.get( i ).valueChanged( event );
            }
        }
    }

    /**
     * notifies all registered listeners that a feature has been deleted
     * 
     * @param feature
     */
    protected void fireFeatureDeletedEvent( Feature feature ) {
        FeatureChangedEvent event = new FeatureChangedEvent( feature, FEATURE_CHANGE_TYPE.deleted, this );
        for ( int i = 0; i < this.listeners.size(); i++ ) {
            if ( this.listeners.get( i ) != null ) {
                this.listeners.get( i ).valueChanged( event );
            }
        }
    }

    protected FeatureCollection getDeleteCollection( List<FeatureAdapter.Changes> changeList ) {
        Feature[] features;
        FeatureCollection fc;
        List<Feature> deleteList = new ArrayList<Feature>( 100 );
        for ( Changes change : changeList ) {
            if ( change.changeType == CHANGE_TYPE.delete ) {
                deleteList.add( change.feature );
            }
        }
        features = deleteList.toArray( new Feature[deleteList.size()] );
        fc = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(), features );
        return fc;
    }

    /**
     * @param changeList
     * @return collection of {@link Feature} to be inserted
     */
    protected FeatureCollection getInsertCollection( List<Changes> changeList ) {
        List<Feature> inserter = new ArrayList<Feature>( changeList.size() );
        List<String> toBeRemoved = new ArrayList<String>( 100 );

        // collect features that must be inserted (ignore features that will be deleted at last)
        for ( int i = 0; i < changeList.size(); i++ ) {
            Changes current = changeList.get( i );
            List<Changes> coll = new ArrayList<Changes>();
            if ( current.changeType == CHANGE_TYPE.insert ) {
                coll.clear();
                String id = current.feature.getId();
                for ( int j = i; j < changeList.size(); j++ ) {
                    Changes compare = changeList.get( j );
                    if ( compare.feature.getId().equals( id ) && !coll.contains( compare ) ) {
                        coll.add( compare );
                    }
                }
                if ( coll.get( coll.size() - 1 ).changeType == CHANGE_TYPE.delete ) {
                    toBeRemoved.add( coll.get( coll.size() - 1 ).feature.getId() );
                } else if ( coll.get( coll.size() - 1 ).changeType == CHANGE_TYPE.update ) {
                    inserter.add( coll.get( coll.size() - 1 ).feature );
                } else {
                    inserter.add( coll.get( 0 ).feature );
                }
            }
        }
        // remove features that will be inserted from changes list
        for ( Feature feature : inserter ) {
            String id = feature.getId();
            for ( int i = changeList.size(); i > 0; i-- ) {
                if ( changeList.get( i - 1 ).feature.getId().equals( id ) ) {
                    changeList.remove( i - 1 );
                }
            }
        }

        // remove all inserted features that has been deleted at least
        for ( String id : toBeRemoved ) {
            for ( int i = changeList.size(); i > 0; i-- ) {
                if ( changeList.get( i - 1 ).feature.getId().equals( id ) ) {
                    changeList.remove( i - 1 );
                }
            }
        }

        Feature[] features = inserter.toArray( new Feature[inserter.size()] );
        return FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(), features );

    }

    /**
     * @param changeList
     * @return collection of {@link Feature} to be updated
     */
    protected FeatureCollection getUpdateCollection( List<Changes> changeList ) {
        List<Feature> updater = new ArrayList<Feature>( changeList.size() );
        List<Changes> tmp = new ArrayList<Changes>( 100 );
        // collect features that must be updated (ignore features that will be deleted at last)
        for ( int i = 0; i < changeList.size(); i++ ) {
            Changes current = changeList.get( i );
            if ( current.changeType == CHANGE_TYPE.update ) {
                String id = current.feature.getId();
                for ( int j = i; j < changeList.size(); j++ ) {
                    Changes compare = changeList.get( j );
                    if ( compare.feature.getId().equals( id ) ) {
                        tmp.add( compare );
                    }
                }
                if ( tmp.size() > 0 && tmp.get( tmp.size() - 1 ).changeType != CHANGE_TYPE.delete ) {
                    for ( Changes changes : tmp ) {
                        if ( !updater.contains( changes.feature ) ) {
                            updater.add( changes.feature );
                        }
                    }
                }
            }
        }

        // remove features that will be updated from changes list
        for ( int i = changeList.size(); i > 0; i-- ) {
            if ( changeList.get( i - 1 ).changeType == CHANGE_TYPE.update ) {
                changeList.remove( i - 1 );
            }
        }
        Feature[] features = updater.toArray( new Feature[updater.size()] );
        return FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(), features );
    }

    // //////////////////////////////////////////////////////////////////////////////
    // inner classes //
    // //////////////////////////////////////////////////////////////////////////////
    public class Changes {

        Feature feature;

        CHANGE_TYPE changeType;

        /**
         * @param feature
         * @param changeType
         */
        Changes( Feature feature, CHANGE_TYPE changeType ) {
            this.feature = feature;
            this.changeType = changeType;
        }

    }

}