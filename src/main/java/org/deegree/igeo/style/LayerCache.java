//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 and
 lat/lon GmbH

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/

package org.deegree.igeo.style;

import static org.deegree.framework.util.DateUtil.formatISO8601Date;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.ChangeListener;
import org.deegree.igeo.ValueChangedEvent;
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.dataadapter.GridCoverageAdapter;
import org.deegree.igeo.dataadapter.database.DatabaseFeatureAdapter;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.LayerChangedEvent;
import org.deegree.igeo.mapmodel.LayerChangedEvent.LAYER_CHANGE_TYPE;
import org.deegree.igeo.style.model.PropertyValue;
import org.deegree.igeo.views.swing.style.StyleDialog.GEOMTYPE;
import org.deegree.model.Identifier;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiPoint;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Surface;

/**
 * <code>LayerCache</code> manages a list of layers.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class LayerCache {

    private static final ILogger LOG = LoggerFactory.getLogger( LayerCache.class );

    private static final String NO_DATA_STRING = "no data";

    private static final int NO_DATA_INT = 0;

    private static final BigInteger NO_DATA_BIGINT = BigInteger.ZERO;

    private static final double NO_DATA_DOUBLE = Double.NaN;

    private static final Date NO_DATA_DATE = new Date();

    private static LayerCache layerCache = new LayerCache();

    private List<CachedLayer> layers = new ArrayList<CachedLayer>();

    private LayerCache() {
    }

    /**
     * @return the only instance of a layer cache
     */
    public static LayerCache getInstance() {
        return layerCache;
    }

    /**
     * @param layer
     *            the layer to add to the cached layers, if not exits
     */
    public void addLayer( Layer layer ) {
        if ( getCachedLayer( layer.getIdentifier() ) == null ) {
            layers.add( new CachedLayer( layer ) );
        }
    }

    /**
     * Returns all properties loaded until now.
     * 
     * @param layerId
     *            the identifier of the layer
     * @return the properties of the layer with the given id loaded until now, or an empty list, if no layer with the
     *         identifier exist
     * @throws
     */
    public Map<QualifiedName, PropertyValue<?>> getProperties( Identifier layerId ) {
        CachedLayer layer = getCachedLayer( layerId );
        if ( layer != null ) {
            return layer.getProperties();
        }
        return new HashMap<QualifiedName, PropertyValue<?>>();
    }

    /**
     * Loads all properties in the passed extent.
     * 
     * @param layerId
     *            the identifier of the layer
     * @param extent
     *            the extent limiting the layers.
     * @return the properties of the layer with the given id in the extent, or an empty list, if no layer with the
     *         identifier exist
     * @throws FilterEvaluationException
     */
    public Map<QualifiedName, PropertyValue<?>> getProperties( Identifier layerId, Envelope extent ) {
        CachedLayer layer = getCachedLayer( layerId );
        if ( layer != null ) {
            return layer.getProperties( extent );
        }
        return new HashMap<QualifiedName, PropertyValue<?>>();
    }

    /**
     * Loads all properties.
     * 
     * @param layerId
     *            the identifier of the layer
     * @return all properties of the layer with the given id, or an empty list, if no layer with the identifier exist
     */
    public Map<QualifiedName, PropertyValue<?>> getAllProperties( Identifier layerId ) {
        CachedLayer layer = getCachedLayer( layerId );
        if ( layer != null ) {
            return layer.getAllProperties();
        }
        return new HashMap<QualifiedName, PropertyValue<?>>();
    }

    /**
     * @param layerId
     *            the identifier of the layer
     * @return the cached layer with the given id, or null if no layer with the identifier exist
     */
    public CachedLayer getCachedLayer( Identifier layerId ) {
        for ( CachedLayer layer : layers ) {
            if ( layerId.equals( layer.layer.getIdentifier() ) ) {
                return layer;
            }
        }
        return null;
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // INNERCLASS
    // ////////////////////////////////////////////////////////////////////////////////////

    /**
     * <code>CachedLayer</code> caches a single org.deegree.igeo.mapmodel.Layer. All information required in the style
     * dialog will be cached in this class
     */
    public class CachedLayer implements ChangeListener {

        private Layer layer;

        private boolean refreshRequested = true;

        private Map<QualifiedName, PropertyValue<?>> properties = new HashMap<QualifiedName, PropertyValue<?>>();

        private Map<Envelope, Map<QualifiedName, PropertyValue<?>>> extentToProperties = new HashMap<Envelope, Map<QualifiedName, PropertyValue<?>>>();

        private boolean refreshAllPropertiesRequested = true;

        private Map<QualifiedName, PropertyValue<?>> propertiesFullExtent = new HashMap<QualifiedName, PropertyValue<?>>();

        private boolean isRaster = false;

        private boolean isOther = false;

        private Map<QualifiedName, FeatureType> featureTypes = new HashMap<QualifiedName, FeatureType>();

        private Map<QualifiedName, GEOMTYPE> geometryProperties = new HashMap<QualifiedName, GEOMTYPE>();

        public CachedLayer( Layer layer ) {
            this.layer = layer;
            layer.addChangeListener( this );
        }

        /**
         * @return the properties
         */
        public Map<QualifiedName, PropertyValue<?>> getProperties() {
            if ( refreshRequested )
                load();
            return properties;
        }

        /**
         * @return the properties
         */
        public Map<QualifiedName, PropertyValue<?>> getProperties( Envelope extent ) {
            if ( !extentToProperties.containsKey( extent ) ) {
                load( extent );
            }
            return extentToProperties.get( extent );
        }

        /**
         * @return the properties
         */
        public Map<QualifiedName, PropertyValue<?>> getAllProperties() {
            if ( refreshAllPropertiesRequested ) {
                loadFullExtent();
            }
            return propertiesFullExtent;
        }

        /**
         * @return the isRaster
         */
        public boolean isRaster() {
            if ( refreshRequested )
                load();
            return isRaster;
        }

        /**
         * @return the isOther
         */
        public boolean isOther() {
            if ( refreshRequested )
                load();
            return isOther;
        }

        /**
         * @return the featureTypes
         */
        public Set<QualifiedName> getFeatureTypes() {
            if ( refreshRequested )
                load();
            return featureTypes.keySet();
        }

        /**
         * @return the featureTypes
         */
        public FeatureType getFeatureType( QualifiedName qn ) {
            if ( refreshRequested )
                load();
            return featureTypes.get( qn );
        }

        /**
         * @return the geometryProperties
         */
        public Map<QualifiedName, GEOMTYPE> getGeometryProperties() {
            if ( refreshRequested )
                load();
            return geometryProperties;
        }

        /**
         * @return the max scale denominator of the layer
         */
        public double getMaxScaleDenominator() {
            return layer.getMaxScaleDenominator();
        }

        /**
         * @return the min scale denominator of the layer
         */
        public double getMinScaleDenominator() {
            return layer.getMinScaleDenominator();
        }

        /**
         * @param qn
         *            the name of the geometry type
         * @return the type of the geometry identified by the given qualified name
         */
        public GEOMTYPE getGeometryType( QualifiedName qn ) {
            GEOMTYPE gt = geometryProperties.get( qn );
            if ( gt == null ) {
                geometryProperties.put( qn, GEOMTYPE.UNKNOWN );
                for ( DataAccessAdapter adapter : layer.getDataAccess() ) {
                    if ( adapter instanceof FeatureAdapter ) {
                        Iterator<Feature> features = getFeatureCollection( adapter, null ).iterator();
                        if(features.hasNext()){
                        FeatureProperty[] fts = features.next().getProperties( qn );
                        if ( fts != null && fts.length > 0 ) {
                            Object value = fts[0].getValue();
                            if ( value instanceof org.deegree.model.spatialschema.Point || value instanceof MultiPoint ) {
                                geometryProperties.remove( qn );
                                geometryProperties.put( qn, GEOMTYPE.POINT );
                            } else if ( value instanceof Curve || value instanceof MultiCurve ) {
                                geometryProperties.remove( qn );
                                geometryProperties.put( qn, GEOMTYPE.LINE );
                            } else if ( value instanceof Surface || value instanceof MultiSurface ) {
                                geometryProperties.remove( qn );
                                geometryProperties.put( qn, GEOMTYPE.POLYGON );
                            }
                        }
                        }
                    }
                }
            }
            return geometryProperties.get( qn );
        }

        private void loadFullExtent() {
            for ( DataAccessAdapter adapter : layer.getDataAccess() ) {
                boolean loadFully = isFullLoadingSupported( adapter );
                if ( loadFully ) {
                    DatabaseFeatureAdapter fa = (DatabaseFeatureAdapter) adapter;
                    FeatureType ft = ( (FeatureAdapter) adapter ).getSchema();
                    featureTypes.put( ft.getName(), ft );
                    PropertyType[] propertyTypes = ft.getProperties();
                    fa.getDistinctPropertyValues( propertiesFullExtent, geometryProperties, propertyTypes );
                    isOther = true;
                } else {
                    propertiesFullExtent.putAll( properties );
                }
            }
            refreshAllPropertiesRequested = false;
        }

        public boolean isFullLoadingSupported() {
            for ( DataAccessAdapter adapter : layer.getDataAccess() ) {
                if ( isFullLoadingSupported( adapter ) )
                    return true;
            }
            return false;
        }

        private boolean isFullLoadingSupported( DataAccessAdapter adapter ) {
            return adapter instanceof DatabaseFeatureAdapter && adapter.getDatasource().isLazyLoading();
        }

        private void load() {
            load( null );
        }

        /**
         * Reads all properties available for the selected layer. If a property is not from type 'geometry', the
         * features of the property will read and all values stored (not the doubles!).
         * 
         * @param extent
         */
        private void load( Envelope extent ) {
            resetProperties( extent );
            for ( DataAccessAdapter adapter : layer.getDataAccess() ) {

                if ( adapter instanceof FeatureAdapter ) {
                    adapter.refresh();
                    FeatureType ft = ( (FeatureAdapter) adapter ).getSchema();
                    featureTypes.put( ft.getName(), ft );
                    PropertyType[] propertyTypes = ft.getProperties();

                    for ( PropertyType type : propertyTypes ) {
                        if ( type.getType() != Types.GEOMETRY ) {
                            FeatureCollection fc = getFeatureCollection( adapter, extent );
                            int pt = type.getType();

                            switch ( pt ) {
                            case Types.VARCHAR:
                                PropertyValue<String> pv1 = new PropertyValue<String>( type );
                                for ( Iterator<Feature> iter = fc.iterator(); iter.hasNext(); ) {
                                    Feature element = iter.next();
                                    // TODO
                                    FeatureProperty[] fps = element.getProperties( type.getName() );
                                    String stringValue = NO_DATA_STRING;
                                    if ( fps != null && fps.length > 0 && fps[0].getValue() != null ) {
                                        try {
                                            if ( fps[0].getValue() instanceof Date ) {
                                                stringValue = formatISO8601Date( (Date) fps[0].getValue() );
                                            } else {
                                                stringValue = (String) fps[0].getValue();
                                            }
                                        } catch ( ClassCastException e ) {
                                            LOG.logError( "Could not cast value to string, where type is VARCHAR" );
                                        }
                                    }
                                    pv1.putInMap( stringValue );
                                }
                                insertInMap( type, pv1, extent );
                                break;
                            case Types.BIGINT:
                                PropertyValue<BigInteger> pv5 = new PropertyValue<BigInteger>( type );
                                for ( Iterator<Feature> iter = fc.iterator(); iter.hasNext(); ) {
                                    Feature element = iter.next();
                                    // TODO
                                    FeatureProperty[] fps = element.getProperties( type.getName() );
                                    BigInteger intValue = NO_DATA_BIGINT;
                                    if ( fps != null && fps.length > 0 && fps[0].getValue() != null ) {
                                        try {
                                            intValue = new BigInteger( fps[0].getValue().toString() );
                                        } catch ( Exception e ) {
                                            LOG.logError( "Could not cast value to integer, where type is INTEGER", e );
                                        }
                                    }
                                    pv5.putInMap( intValue );
                                }
                                insertInMap( type, pv5, extent );
                                break;

                            case Types.INTEGER:
                            case Types.SMALLINT:
                                PropertyValue<Integer> pv2 = new PropertyValue<Integer>( type );
                                for ( Iterator<Feature> iter = fc.iterator(); iter.hasNext(); ) {
                                    Feature element = iter.next();
                                    // TODO
                                    FeatureProperty[] fps = element.getProperties( type.getName() );
                                    int intValue = NO_DATA_INT;
                                    if ( fps != null && fps.length > 0 && fps[0].getValue() != null ) {
                                        try {
                                            intValue = Integer.parseInt( fps[0].getValue().toString() );
                                        } catch ( Exception e ) {
                                            LOG.logError( "Could not cast value to integer, where type is INTEGER", e );
                                        }
                                    }
                                    pv2.putInMap( intValue );
                                }
                                insertInMap( type, pv2, extent );
                                break;
                            case Types.DOUBLE:
                            case Types.FLOAT:
                                PropertyValue<Double> pv3 = new PropertyValue<Double>( type );
                                for ( Iterator<Feature> iter = fc.iterator(); iter.hasNext(); ) {
                                    Feature element = iter.next();
                                    // TODO
                                    FeatureProperty[] fps = element.getProperties( type.getName() );
                                    double doubleValue = NO_DATA_DOUBLE;
                                    if ( fps != null && fps.length > 0 ) {
                                        Object value = fps[0].getValue();
                                        if ( value != null && value instanceof String
                                             && ( (String) value ).length() > 0 ) {
                                            try {
                                                doubleValue = Double.parseDouble( (String) value );
                                            } catch ( Exception e ) {
                                                LOG.logError( "Could not cast value to double, where type is DOUBLE", e );
                                            }
                                        } else if ( value instanceof Double ) {
                                            try {
                                                doubleValue = (Double) value;
                                            } catch ( Exception e ) {
                                                LOG.logError( "Could not cast value to double, where type is DOUBLE", e );
                                            }
                                        }
                                    }
                                    pv3.putInMap( doubleValue );
                                }
                                insertInMap( type, pv3, extent );
                                break;
                            case Types.DATE:
                                PropertyValue<Date> pv4 = new PropertyValue<Date>( type );
                                for ( Iterator<Feature> iter = fc.iterator(); iter.hasNext(); ) {
                                    Feature element = iter.next();
                                    // TODO
                                    FeatureProperty[] fps = element.getProperties( type.getName() );
                                    Date dateValue = NO_DATA_DATE;
                                    if ( fps != null && fps.length > 0 ) {
                                        Object value = fps[0].getValue();
                                        if ( value != null && value instanceof String
                                             && ( (String) value ).length() > 0 ) {
                                            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-mm-dd" );
                                            try {
                                                dateValue = sdf.parse( (String) value );
                                            } catch ( ParseException e ) {
                                                LOG.logError( "Could not cast value to date, where type is DATE", e );
                                            }
                                        } else if ( value != null && value instanceof Date ) {
                                            dateValue = (Date) value;
                                        }
                                    }
                                    pv4.putInMap( dateValue );
                                }
                                insertInMap( type, pv4, extent );
                                break;
                            }
                        } else {
                            geometryProperties.put( type.getName(), null );
                        }
                    }
                    isOther = true;
                } else if ( adapter instanceof GridCoverageAdapter ) {
                    isRaster = true;
                }
            }
            if ( extent == null ) {
                refreshRequested = false;
            }
        }

        private void insertInMap( PropertyType type, PropertyValue<?> pv, Envelope extent ) {
            if ( extent != null ) {
                extentToProperties.get( extent ).put( type.getName(), pv );
            } else {
                properties.put( type.getName(), pv );
            }
        }

        private void resetProperties( Envelope extent ) {
            if ( extent != null ) {
                if ( extentToProperties.containsKey( extent ) ) {
                    extentToProperties.get( extent ).clear();
                } else {
                    extentToProperties.put( extent, new HashMap<QualifiedName, PropertyValue<?>>() );
                }
            } else {
                properties.clear();
            }
        }

        private FeatureCollection getFeatureCollection( DataAccessAdapter adapter, Envelope extent ) {
            if ( extent != null ) {
                try {
                    return ( (FeatureAdapter) adapter ).getFeatureCollection( extent );
                } catch ( FilterEvaluationException e ) {
                    LOG.logWarning( "Could not get limited FeatureCollection to extent " + extent + ": "
                                    + e.getMessage() );
                    return ( (FeatureAdapter) adapter ).getFeatureCollection();
                }
            }
            return ( (FeatureAdapter) adapter ).getFeatureCollection();
        }

        @Override
        public void valueChanged( ValueChangedEvent event ) {
            // if the datasource changes, the properties have to be refreshed!
            if ( ( event instanceof LayerChangedEvent )
                 && ( ( (LayerChangedEvent) event ).getChangeType().equals( LAYER_CHANGE_TYPE.datasourceAdded )
                      || ( (LayerChangedEvent) event ).getChangeType().equals( LAYER_CHANGE_TYPE.datasourceRemoved ) || ( (LayerChangedEvent) event ).getChangeType().equals( LAYER_CHANGE_TYPE.datasourceChanged ) ) ) {
                refreshRequested = true;
                refreshAllPropertiesRequested = true;
                extentToProperties.clear();
            }
        }
    }

}
