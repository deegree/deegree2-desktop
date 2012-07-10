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
import java.util.List;

import org.deegree.igeo.config.EnvelopeType;
import org.deegree.igeo.config.ExternalResourceType;
import org.deegree.igeo.config.MapModelCollectionType;
import org.deegree.igeo.config.MapModelType;
import org.deegree.igeo.config.Util;
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.model.Identifier;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
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
public class MapModelCollection {

    private List<MapModel> mapModels;

    private MapModelCollectionType mmcType;

    /**
     * @param mmcType
     * @param name
     * @param maxExtent
     * @param externalResources
     * @param mapModels
     */
    public MapModelCollection( MapModelCollectionType mmcType, List<MapModel> mapModels) {

        this.mmcType = mmcType;
        this.mapModels = mapModels;
    }

    /**
     * @return the name
     */
    public String getName() {
        return mmcType.getName();
    }
    
    /**
     * 
     * @param name
     */
    public void setName(String name) {
       mmcType.setName( name ); 
    }

    /**
     * 
     * @return description of a MapModelCollection
     */
    public String getDescription() {
        return mmcType.getDescription();
    }
    
    /**
     * 
     * @param description map model collection description
     */
    public void setDescription(String description) {
        mmcType.setDescription( description );
    }

    /**
     * 
     * @return list of assigned external resources/documents
     */
    public List<ExternalResourceType> getExternalResources() {
        return mmcType.getExternalResource();
    }

    /**
     * 
     * @param externalResources
     */
    public void setExternalResources( List<ExternalResourceType> externalResources ) {
        List<ExternalResourceType> tmp = mmcType.getExternalResource();
        tmp.clear();
        tmp.addAll( externalResources );
    }

    /**
     * 
     * @param externalResource
     */
    public void addExternalResources( ExternalResourceType externalResource ) {
        List<ExternalResourceType> tmp = mmcType.getExternalResource();
        tmp.add( externalResource );
    }

    /**
     * 
     * @param externalResource
     */
    public void removeExternalResources( ExternalResourceType externalResource ) {
        List<ExternalResourceType> tmp = mmcType.getExternalResource();
        tmp.remove( externalResource );
    }

    /**
     * 
     * @return list of MapModels
     */
    public List<MapModel> getMapModels() {
        return this.mapModels;
    }

    /**
     * 
     * @param mapModels
     */
    public void setMapModels( List<MapModel> mapModels ) {
        this.mapModels = mapModels;
    }

    /**
     * 
     * @param mapModel
     */
    public void addMapModel( MapModel mapModel ) {
        this.mapModels.add( mapModel );
    }

    /**
     * 
     * @param mapModel
     */
    public void removeMapModel( MapModel mapModel ) {
        this.mapModels.remove( mapModel );
        List<MapModelType> mms = mmcType.getMapModel();
        MapModelType tmp = null;
        for ( MapModelType mapModelType : mms ) {
            if ( mapModelType.getName().equals( mapModel.getName() ) ) {
                tmp = mapModelType;
                break;
            }
        }
        mms.remove( tmp );
    }

    /**
     * 
     * @return maximum extent considering all contained MapModels
     * @throws UnknownCRSException
     */
    public Envelope getMaxExtent() {
        return Util.convertEnvelope( mmcType.getMaxExtent() );
    }

    /**
     * 
     * @param maxExtent
     */
    public void setMaxExtent( Envelope maxExtent ) {
        EnvelopeType value = new EnvelopeType();
        value.setCrs( maxExtent.getCoordinateSystem().getPrefixedName() );
        value.setMinx( maxExtent.getMin().getX() );
        value.setMiny( maxExtent.getMin().getY() );
        value.setMaxx( maxExtent.getMax().getX() );
        value.setMaxy( maxExtent.getMax().getY() );
        mmcType.setMaxExtent( value );
    }

    /**
     * 
     * @param identifier
     * @return {@link MapModel} matching the passed {@link Identifier} or <code>null</code> if no map model with passed
     *         identifier can be find
     */
    public MapModel getMapModel( Identifier identifier ) {
        for ( int i = 0; i < mapModels.size(); i++ ) {
            if ( mapModels.get( i ).getIdentifier().equals( identifier ) ) {
                return mapModels.get( i );
            }
        }
        return null;
    }

    /**
     * 
     * @param crs
     * @throws Exception
     */
    public void setCoordinateSystem( CoordinateSystem crs )
                            throws Exception {
        if ( !crs.equals( getMaxExtent().getCoordinateSystem() ) ) {
            Envelope env = getMaxExtent();
            GeoTransformer gt = new GeoTransformer( crs );
            env = gt.transform( env, env.getCoordinateSystem() );
            env = GeometryFactory.createEnvelope( env.getMin(), env.getMax(), crs );
            setMaxExtent( env );
            List<MapModel> mm = getMapModels();
            for ( MapModel model : mm ) {
                env = model.getMaxExtent();
                env = gt.transform( env, env.getCoordinateSystem() );
                env = GeometryFactory.createEnvelope( env.getMin(), env.getMax(), crs );
                model.setMaxExtent( env );
                env = model.getEnvelope();
                env = gt.transform( env, env.getCoordinateSystem() );
                env = GeometryFactory.createEnvelope( env.getMin(), env.getMax(), crs );
                model.setEnvelope( env );
                final List<Layer> layers = new ArrayList<Layer>();
                model.walkLayerTree( new MapModelVisitor() {

                    public void visit( LayerGroup layerGroup )
                                            throws Exception {
                    }

                    public void visit( Layer layer )
                                            throws Exception {
                        layers.add( layer );
                    }
                } );
                for ( Layer layer : layers ) {
                    List<Datasource> datasources = layer.getDatasources();
                    for ( Datasource datasource : datasources ) {
                        datasource.setExtent( env );
                    }
                    List<DataAccessAdapter> adapters = layer.getDataAccess();
                    for ( DataAccessAdapter dataAccessAdapter : adapters ) {
                        dataAccessAdapter.invalidate();
                        dataAccessAdapter.refresh();
                    }
                }
            }
        }
    }

}