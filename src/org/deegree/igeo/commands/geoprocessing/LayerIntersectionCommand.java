//$HeadURL: svn+ssh://developername@svn.wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
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

package org.deegree.igeo.commands.geoprocessing;

import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.util.Pair;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.CommandHelper;
import org.deegree.igeo.commands.model.AddErrorLayerCommand;
import org.deegree.igeo.config.LayerType.MetadataURL;
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.dataadapter.DataAccessFactory;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.Command;
import org.deegree.model.Identifier;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;

/**
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author: Andreas Poth $
 * 
 * @version $Revision: $, $Date: $
 * 
 */
public class LayerIntersectionCommand extends AbstractCommand {

    public static final QualifiedName name = new QualifiedName( "Layer Intersection" );

    public enum INTERSECTION_TYPE {
        Intersection, Union, Difference, SymmetricDifference
    };

    private Layer mainLayer;

    private QualifiedName mainGeom;

    private Layer compareLayer;

    private QualifiedName compGeom;

    private String newLayerName;

    private Layer newLayer;

    private INTERSECTION_TYPE intersectiontype = INTERSECTION_TYPE.Intersection;

    private boolean performed;

    /**
     * 
     * @param mainLayer
     *            layer that will be used to create alpha numeric properties for new layer
     * @param mainGeom
     * @param compareLayer
     * @param compGeom
     * @param newLayerName
     *            name of the new layer
     */
    public LayerIntersectionCommand( Layer mainLayer, QualifiedName mainGeom, Layer compareLayer,
                                     QualifiedName compGeom, String newLayerName ) {
        this.mainLayer = mainLayer;
        this.compareLayer = compareLayer;
        this.newLayerName = newLayerName;
    }

    /**
     * 
     * @param mainLayer
     *            layer that will be used to create alpha numeric properties for new layer
     * @param mainGeom
     * @param compareLayer
     * @param compGeom
     * @param newLayerName
     *            name of the new layer
     * @param intersectiontype
     */
    public LayerIntersectionCommand( Layer mainLayer, QualifiedName mainGeom, Layer compareLayer,
                                     QualifiedName compGeom, String newLayerName, INTERSECTION_TYPE intersectiontype ) {
        this.mainLayer = mainLayer;
        this.compareLayer = compareLayer;
        this.newLayerName = newLayerName;
        if ( intersectiontype != null ) {
            this.intersectiontype = intersectiontype;
        }
    }

    /**
     * 
     * @throws Exception
     */
    public void execute()
                            throws Exception {
        performed = false;
        try {
            FeatureCollection mainFC = getMainFeatures();
            FeatureCollection compareFC = getCompareFeatures();

            FeatureCollection resultFC = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(),
                                                                                 mainFC.size() + compareFC.size() );
            mainGeom = CommandHelper.findGeomProperty( mainFC.getFeature( 0 ) );
            compGeom = CommandHelper.findGeomProperty( compareFC.getFeature( 0 ) );
            // calculate intersection between each geometry of main layer with each geometry of comparing layer
            // if an intersection is not null; the result geometry will be used to create a new feature filled
            // with alpha numeric properties of the source feature from main layer
            List<Position> posList = new ArrayList<Position>();
            switch ( intersectiontype ) {
            case Intersection:
                handleIntersection( mainFC, compareFC, resultFC, posList );
                break;
            case Union:
                handleUnion( mainFC, compareFC, resultFC );
                break;
            case Difference:
                handleDifference( mainFC, compareFC, resultFC, posList );
                break;
            case SymmetricDifference:
                handleSymmetricDifference( mainFC, compareFC, resultFC, posList );
                break;
            }
            Datasource ds = DataAccessFactory.createDatasource( newLayerName, resultFC );
            MapModel mm = mainLayer.getOwner();
            Identifier id = new Identifier( newLayerName );
            int i = 0;
            while ( mm.exists( id ) ) {
                id = new Identifier( newLayerName + "_" + i++ );
            }
            newLayer = new Layer( mm, id, id.getValue(), newLayerName, singletonList( ds ),
                                  Collections.<MetadataURL> emptyList() );
            newLayer.setEditable( true );
            mm.insert( newLayer, mainLayer.getParent(), mainLayer, false );
            if ( posList.size() > 0 ) {
                createErrorlayer( "intersection error", posList );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            throw e;
        } finally {
            if ( processMonitor != null ) {
                processMonitor.cancel();
            }
        }
        performed = true;

    }

    /**
     * @param mainFC
     * @param compareFC
     * @param resultFC
     * @param posList
     * @throws CloneNotSupportedException
     */
    private void handleSymmetricDifference( FeatureCollection mainFC, FeatureCollection compareFC,
                                            FeatureCollection resultFC, List<Position> posList )
                            throws CloneNotSupportedException {
        FeatureCollection tmpFC1 = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(),
                                                                           mainFC.size() + compareFC.size() );
        FeatureCollection tmpFC2 = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(),
                                                                           mainFC.size() + compareFC.size() );
        handleUnion( mainFC, compareFC, tmpFC1 );
        if ( processMonitor != null ) {
            // reset
        }
        handleIntersection( mainFC, compareFC, tmpFC2, posList );
        if ( processMonitor != null ) {
            // reset
        }
        handleDifference( tmpFC1, tmpFC2, resultFC, posList );
    }

    /**
     * @param mainFC
     * @param compareFC
     * @param resultFC
     * @param posList
     * @throws CloneNotSupportedException
     */
    private void handleDifference( FeatureCollection mainFC, FeatureCollection compareFC, FeatureCollection resultFC,
                                   List<Position> posList )
                            throws CloneNotSupportedException {
        if ( processMonitor != null ) {
            processMonitor.setMaximumValue( mainFC.size() + compareFC.size() );
        }
        Iterator<Feature> iterator = mainFC.iterator();
        int cnt = 0;
        while ( iterator.hasNext() ) {
            Feature feature = (Feature) iterator.next();
            Geometry mainG = (Geometry) feature.getDefaultProperty( mainGeom ).getValue();
            Iterator<Feature> compare = compareFC.iterator();
            while ( compare.hasNext() ) {
                if ( processMonitor != null ) {
                    processMonitor.updateStatus( cnt++, "" );
                }
                Feature compFeature = (Feature) compare.next();
                Geometry compG = (Geometry) compFeature.getDefaultProperty( compGeom ).getValue();
                try {
                    if ( compG != null && mainG != null ) {
                        mainG = mainG.difference( compG );
                    }
                } catch ( Exception e ) {
                    if ( mainG != null && compG != null ) {
                        posList.add( mainG.getCentroid().getPosition() );
                        posList.add( compG.getCentroid().getPosition() );
                    }
                }
            }
            if ( mainG != null ) {
                Feature feat = feature.cloneDeep();
                feat.getDefaultProperty( mainGeom ).setValue( mainG );
                resultFC.add( feat );
            }
        }
    }

    /**
     * @param mainFC
     * @param compareFC
     * @param resultFC
     * @throws CloneNotSupportedException
     */
    private void handleUnion( FeatureCollection mainFC, FeatureCollection compareFC, FeatureCollection resultFC )
                            throws CloneNotSupportedException {
        if ( processMonitor != null ) {
            processMonitor.setMaximumValue( mainFC.size() + compareFC.size() );
        }
        int cnt = 0;
        Iterator<Feature> iterator = mainFC.iterator();
        while ( iterator.hasNext() ) {
            Feature feature = (Feature) iterator.next();
            feature = feature.cloneDeep();
            resultFC.add( feature );
            if ( processMonitor != null ) {
                processMonitor.updateStatus( cnt++, "" );
            }
        }
        iterator = compareFC.iterator();
        while ( iterator.hasNext() ) {
            Feature feature = (Feature) iterator.next();
            feature = feature.cloneDeep();
            resultFC.add( feature );
            if ( processMonitor != null ) {
                processMonitor.updateStatus( cnt++, "" );
            }
        }
    }

    private void handleIntersection( FeatureCollection mainFC, FeatureCollection compareFC, FeatureCollection resultFC,
                                     List<Position> posList )
                            throws CloneNotSupportedException {
        if ( processMonitor != null ) {
            processMonitor.setMaximumValue( mainFC.size() * compareFC.size() );
        }
        int cnt = 0;
        Iterator<Feature> iterator = mainFC.iterator();
        while ( iterator.hasNext() ) {
            Feature feature = (Feature) iterator.next();
            Geometry mainG = (Geometry) feature.getDefaultProperty( mainGeom ).getValue();
            Iterator<Feature> compare = compareFC.iterator();
            while ( compare.hasNext() ) {
                if ( processMonitor != null ) {
                    processMonitor.updateStatus( cnt++, "" );
                }
                Feature compFeature = (Feature) compare.next();
                if ( !compFeature.getId().equals( feature.getId() ) ) {
                    Geometry compG = (Geometry) compFeature.getDefaultProperty( compGeom ).getValue();
                    Geometry tmp = null;
                    try {
                        tmp = mainG.intersection( compG );
                    } catch ( Exception e ) {
                        posList.add( mainG.getCentroid().getPosition() );
                        posList.add( compG.getCentroid().getPosition() );
                    }
                    if ( tmp != null ) {
                        Feature feat = feature.cloneDeep();
                        feat.getDefaultProperty( mainGeom ).setValue( tmp );
                        resultFC.add( feat );
                    }
                }
            }
        }
    }

    private void createErrorlayer( String message, List<Position> posList )
                            throws Exception {
        MapModel mapModel = mainLayer.getOwner();
        ApplicationContainer<?> appCont = mapModel.getApplicationContainer();
        List<Pair<String, Point>> errorLocations = new ArrayList<Pair<String, Point>>( posList.size() );
        for ( Position position : posList ) {
            Pair<String, Point> pa = new Pair<String, Point>();
            pa.first = message;
            pa.second = GeometryFactory.createPoint( position, mapModel.getCoordinateSystem() );
            errorLocations.add( pa );
        }
        Command cmd = new AddErrorLayerCommand( appCont, mainLayer, errorLocations );
        appCont.getCommandProcessor().executeSychronously( cmd, true );
    }

    private FeatureCollection getCompareFeatures() {
        FeatureCollection compareFC = compareLayer.getSelectedFeatures();
        if ( compareFC.size() == 0 ) {
            List<DataAccessAdapter> dataAdapters = compareLayer.getDataAccess();
            for ( DataAccessAdapter dataAccessAdapter : dataAdapters ) {
                if ( dataAccessAdapter instanceof FeatureAdapter ) {
                    FeatureCollection fc = ( (FeatureAdapter) dataAccessAdapter ).getFeatureCollection();
                    Iterator<Feature> iterator = fc.iterator();
                    while ( iterator.hasNext() ) {
                        Feature feature = (Feature) iterator.next();
                        compareFC.add( feature );
                    }
                }
            }
        }
        return compareFC;
    }

    private FeatureCollection getMainFeatures() {
        FeatureCollection mainFC = mainLayer.getSelectedFeatures();
        if ( mainFC.size() == 0 ) {
            List<DataAccessAdapter> dataAdapters = mainLayer.getDataAccess();
            for ( DataAccessAdapter dataAccessAdapter : dataAdapters ) {
                if ( dataAccessAdapter instanceof FeatureAdapter ) {
                    FeatureCollection fc = ( (FeatureAdapter) dataAccessAdapter ).getFeatureCollection();
                    Iterator<Feature> iterator = fc.iterator();
                    while ( iterator.hasNext() ) {
                        Feature feature = (Feature) iterator.next();
                        mainFC.add( feature );
                    }
                }
            }
        }
        return mainFC;
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
        return newLayer;
    }

    @Override
    public boolean isUndoSupported() {
        return true;
    }

    @Override
    public void undo()
                            throws Exception {
        if ( performed ) {
            newLayer.getOwner().remove( newLayer );
            performed = false;
        }
    }
}
