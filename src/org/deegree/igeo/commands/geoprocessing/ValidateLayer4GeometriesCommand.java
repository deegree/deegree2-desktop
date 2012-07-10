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

package org.deegree.igeo.commands.geoprocessing;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.commands.model.AddErrorLayerCommand;
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.WFSDatasource;
import org.deegree.igeo.settings.ValidationGeomMetrics;
import org.deegree.igeo.settings.ValidationGeomTopology;
import org.deegree.igeo.settings.ValidationGeomTypes;
import org.deegree.kernel.Command;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.JTSAdapter;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiGeometry;
import org.deegree.model.spatialschema.MultiPoint;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Surface;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Command class for validating a layer considerung current configuration settings. A layer will be validated for
 * presents of not allowed geometry types and for topological releations between geometries. Additionally each geometry
 * will be validated if it is valid itself. Checking geometry validity will be done as describe for
 * {@link ValidateGeometriesCommand}
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class ValidateLayer4GeometriesCommand extends ValidateGeometriesCommand {

    private static final ILogger LOG = LoggerFactory.getLogger( ValidateLayer4GeometriesCommand.class );

    private static final QualifiedName name = new QualifiedName( "Validate layer for geometries" );

    /**
     * @param layer
     * @param geometries
     */
    public ValidateLayer4GeometriesCommand( Layer layer, List<Geometry> geometries ) {
        super( layer, geometries );

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {
        try {
            errorCount = 0;
            List<DataAccessAdapter> daaList = layer.getDataAccess();
            int max = 0;
            for ( DataAccessAdapter adapter : daaList ) {
                if ( adapter instanceof FeatureAdapter ) {
                    FeatureCollection fc = ( (FeatureAdapter) adapter ).getFeatureCollection();
                    max += fc.size();
                }
            }
            if ( processMonitor != null ) {
                processMonitor.setMaximumValue( ( max - 1 ) * 2 );
            }
            for ( DataAccessAdapter adapter : daaList ) {
                if ( adapter instanceof FeatureAdapter ) {
                    LOG.logDebug( "validating datasource: ", adapter.getDatasource().getName() );
                    FeatureCollection fc = ( (FeatureAdapter) adapter ).getFeatureCollection();
                    performGeometryValidation( adapter, fc );
                    performTopologicalValidation( adapter, fc );
                }
            }
            if ( errorCount > 0 ) {
                errorReport.put( "errorCount", Integer.toString( errorCount ) );
                Command cmd = new AddErrorLayerCommand( layer.getOwner().getApplicationContainer(), layer,
                                                        errorPositions );
                layer.getOwner().getApplicationContainer().getCommandProcessor().executeSychronously( cmd, true );
            }
        } catch ( Exception e ) {
            throw e;
        } finally {
            if ( processMonitor != null ) {
                processMonitor.cancel();
            }
        }
        fireCommandProcessedEvent();
    }

    private void performTopologicalValidation( DataAccessAdapter adapter, FeatureCollection fc )
                            throws GeometryException {
        LOG.logDebug( "start topological validation" );
        ValidationGeomTopology vgt = settings.getValidationGeomTopology();

        QualifiedName geomProperty = findGeomProperty( (FeatureAdapter) adapter, fc );
        if ( geomProperty != null ) {
            int cnt = fc.size();
            for ( int i = 0; i < cnt; i++ ) {
                if ( processMonitor != null ) {
                    processMonitor.updateStatus( cnt + i - 1, "" );
                }
                Feature feature = fc.getFeature( i );
                Geometry g1 = (Geometry) feature.getProperties( geomProperty )[0].getValue();
                com.vividsolutions.jts.geom.Geometry jtsG1 = JTSAdapter.export( g1 );
                for ( int j = i + 1; j < cnt; j++ ) {
                    feature = fc.getFeature( j );
                    Geometry g2 = (Geometry) feature.getProperties( geomProperty )[0].getValue();
                    com.vividsolutions.jts.geom.Geometry jtsG2 = JTSAdapter.export( g2 );
                    if ( !vgt.intersectionAllowed() ) {
                        try {
                            if ( jtsG1.intersects( jtsG2 ) && !jtsG1.touches( jtsG2 ) ) {
                                errorReport.put( "allowIntersection_" + ( errorCount++ ),
                                                 Messages.getMessage( Locale.getDefault(), "$MD10451", i, j ) );
                                addErrorPosition( g1.getCentroid().getPosition(),
                                                  Messages.getMessage( Locale.getDefault(), "$MD10451", i, j ) );
                            }
                        } catch ( Exception e ) {
                            errorReport.put( "allowIntersection_" + ( errorCount++ ),
                                             Messages.getMessage( Locale.getDefault(), "$MD10451", i, j ) );
                            addErrorPosition( g1.getCentroid().getPosition(), Messages.getMessage( Locale.getDefault(),
                                                                                                   "$MD10451", i, j ) );
                        }
                    }
                    if ( !vgt.touchingAllowed() ) {
                        try {
                            if ( jtsG1.touches( jtsG2 ) ) {
                                errorReport.put( "allowTouching_" + ( errorCount++ ),
                                                 Messages.getMessage( Locale.getDefault(), "$MD10452", i, j ) );
                            }
                            addErrorPosition( g1.getCentroid().getPosition(), Messages.getMessage( Locale.getDefault(),
                                                                                                   "$MD10452", i, j ) );
                        } catch ( Exception e ) {
                            errorReport.put( "allowTouching_" + ( errorCount++ ),
                                             Messages.getMessage( Locale.getDefault(), "$MD10452", i, j ) );
                            addErrorPosition( g1.getCentroid().getPosition(), Messages.getMessage( Locale.getDefault(),
                                                                                                   "$MD10452", i, j ) );
                        }
                    }
                    if ( !vgt.equalGeometriesAllowed() ) {
                        try {
                            if ( jtsG1.equals( jtsG2 ) ) {
                                errorReport.put( "allowEqualGeometries_" + ( errorCount++ ),
                                                 Messages.getMessage( Locale.getDefault(), "$MD10453", i, j ) );
                                addErrorPosition( g1.getCentroid().getPosition(),
                                                  Messages.getMessage( Locale.getDefault(), "$MD10453", i, j ) );
                            }
                        } catch ( Exception e ) {
                            errorReport.put( "allowEqualGeometries_" + ( errorCount++ ),
                                             Messages.getMessage( Locale.getDefault(), "$MD10453", i, j ) );
                            addErrorPosition( g1.getCentroid().getPosition(), Messages.getMessage( Locale.getDefault(),
                                                                                                   "$MD10453", i, j ) );
                        }
                    }
                }
            }
        }

    }

    private void performGeometryValidation( DataAccessAdapter adapter, FeatureCollection fc )
                            throws GeometryException {
        LOG.logDebug( "start geometric validation" );
        ValidationGeomTypes vgt = settings.getValidationGeomTypes();
        ValidationGeomMetrics vgm = settings.getValidationGeomMetrics();
        float minDist = vgm.getMinSegmentLength();
        float minArea = vgm.getMinPolygonArea();
        Iterator<Feature> iterator = fc.iterator();
        QualifiedName geomProperty = findGeomProperty( (FeatureAdapter) adapter, fc );
        if ( geomProperty != null ) {
            int i = -1;
            while ( iterator.hasNext() ) {
                i++;
                if ( processMonitor != null ) {
                    processMonitor.updateStatus( i, "" );
                }
                Feature feature = iterator.next();
                Geometry geometry = (Geometry) feature.getProperties( geomProperty )[0].getValue();
                com.vividsolutions.jts.geom.Geometry jtsGeom = JTSAdapter.export( geometry );
                if ( vgm.checkForValidGeometries() ) {
                    if ( !jtsGeom.isValid() ) {
                        addErrorPosition( geometry.getCentroid().getPosition(),
                                          Messages.getMessage( Locale.getDefault(), "$MD10426", i ) );
                        errorReport.put( "checkForValidGeometries_" + ( errorCount++ ),
                                         Messages.getMessage( Locale.getDefault(), "$MD10426", i ) );
                    }
                }
                if ( vgm.disallowRepeatedPoints() ) {
                    if ( rpTester.hasRepeatedPoint( jtsGeom ) ) {
                        Coordinate c = rpTester.getCoordinate();
                        Position p = GeometryFactory.createPosition( c.x, c.y );
                        addErrorPosition( p, Messages.getMessage( Locale.getDefault(), "$MD10427", i ) );
                        errorReport.put( "disallowRepeatedPoints_" + ( errorCount++ ),
                                         Messages.getMessage( Locale.getDefault(), "$MD10427", i ) );
                    }
                }
                if ( vgm.checkForPolygonOrientation() ) {
                    handleCheckForPolygonOrientation( jtsGeom, i );
                }
                if ( vgm.disallowDoubleGeomerties() ) {
                    handleDisallowDoubleGeomerties( geometry, i );
                }
                if ( vgm.ensureSimpleLines() ) {
                    handleSimpleLines( geometry, jtsGeom, i );
                }
                if ( vgm.limitMinSegmentLength() ) {
                    handleMinSegmentLength( geometry, i, minDist );
                }
                if ( vgm.limitMinPolygonArea() ) {
                    handleMinPolygonArea( geometry, i, minArea );
                }
                if ( !vgt.pointsAllowed() ) {
                    if ( geometry instanceof Point ) {
                        addErrorPosition( geometry.getCentroid().getPosition(),
                                          Messages.getMessage( Locale.getDefault(), "$MD10443", i ) );
                        errorReport.put( "allowPoints_" + ( errorCount++ ), Messages.getMessage( Locale.getDefault(),
                                                                                                 "$MD10443", i ) );
                    }
                }
                if ( !vgt.linestringsAllowed() ) {
                    if ( geometry instanceof Curve ) {
                        addErrorPosition( geometry.getCentroid().getPosition(),
                                          Messages.getMessage( Locale.getDefault(), "$MD10444", i ) );
                        errorReport.put( "allowLinestrings_" + ( errorCount++ ),
                                         Messages.getMessage( Locale.getDefault(), "$MD10444", i ) );
                    }
                }
                if ( !vgt.polygonsAllowed() ) {
                    if ( geometry instanceof Surface ) {
                        addErrorPosition( geometry.getCentroid().getPosition(),
                                          Messages.getMessage( Locale.getDefault(), "$MD10444", i ) );
                        errorReport.put( "allowPolygons_" + ( errorCount++ ), Messages.getMessage( Locale.getDefault(),
                                                                                                   "$MD10445", i ) );
                    }
                }
                if ( !vgt.multiPointsAllowed() ) {
                    if ( geometry instanceof MultiPoint ) {
                        Point[] pos = ( (MultiPoint) geometry ).getAllPoints();
                        for ( Point point : pos ) {
                            addErrorPosition( point.getPosition(), Messages.getMessage( Locale.getDefault(),
                                                                                        "$MD10444", i ) );
                        }
                        errorReport.put( "allowMultiPoints_" + ( errorCount++ ),
                                         Messages.getMessage( Locale.getDefault(), "$MD10446", i ) );
                    }
                }
                if ( !vgt.multiLinestringsAllowed() ) {
                    if ( geometry instanceof MultiCurve ) {
                        Curve[] curves = ( (MultiCurve) geometry ).getAllCurves();
                        for ( Curve curve : curves ) {
                            addErrorPosition( curve.getCentroid().getPosition(),
                                              Messages.getMessage( Locale.getDefault(), "$MD10444", i ) );
                        }
                        errorReport.put( "allowMultiLinestrings_" + ( errorCount++ ),
                                         Messages.getMessage( Locale.getDefault(), "$MD10447", i ) );
                    }
                }
                if ( !vgt.multiPolygonsAllowed() ) {
                    if ( geometry instanceof MultiSurface ) {
                        Surface[] surfaces = ( (MultiSurface) geometry ).getAllSurfaces();
                        for ( Surface surface : surfaces ) {
                            addErrorPosition( surface.getCentroid().getPosition(),
                                              Messages.getMessage( Locale.getDefault(), "$MD10444", i ) );
                        }
                        errorReport.put( "allowMultiPolygons_" + ( errorCount++ ),
                                         Messages.getMessage( Locale.getDefault(), "$MD10448", i ) );
                    }
                }
                if ( !vgt.geometryCollectionsAllowed() ) {
                    if ( geometry instanceof MultiGeometry ) {
                        errorReport.put( "allowGeometryCollections_" + ( errorCount++ ),
                                         Messages.getMessage( Locale.getDefault(), "$MD10449", i ) );
                    }
                }
                if ( !vgt.polygonsWithHolesAllowed() ) {
                    handleAllowHoles( geometry, i );
                }
                if ( !vgt.noneLinearInterpolationAllowed() ) {
                    // TODO
                }
            }
        }
    }

    private void handleAllowHoles( Geometry geometry, int index ) {
        if ( geometry instanceof Surface ) {
            Surface surface = (Surface) geometry;
            if ( surface.getSurfaceBoundary().getInteriorRings().length > 0 ) {
                addErrorPosition( surface.getCentroid().getPosition(), Messages.getMessage( Locale.getDefault(),
                                                                                            "$MD10450", index ) );
                errorReport.put( "allowHoles_" + ( errorCount++ ), Messages.getMessage( Locale.getDefault(),
                                                                                        "$MD10450", index ) );
            }
        } else if ( geometry instanceof MultiSurface ) {
            Surface[] surfaces = ( (MultiSurface) geometry ).getAllSurfaces();
            for ( Surface surface : surfaces ) {
                if ( surface.getSurfaceBoundary().getInteriorRings().length > 0 ) {
                    addErrorPosition( surface.getCentroid().getPosition(), Messages.getMessage( Locale.getDefault(),
                                                                                                "$MD10450", index ) );
                    errorReport.put( "allowHoles_" + ( errorCount++ ), Messages.getMessage( Locale.getDefault(),
                                                                                            "$MD10450", index ) );
                }
            }
        }
    }

    /**
     * 
     * @return name of the first geometry property found or <code>null</code> if feature does not contains a geometry
     *         property
     */
    protected QualifiedName findGeomProperty( FeatureAdapter adapter, FeatureCollection fc ) {
        if ( adapter.getDatasource() instanceof WFSDatasource ) {
            return ( (WFSDatasource) adapter.getDatasource() ).getGeometryProperty();
        } else {
            Feature feature = fc.getFeature( 0 );
            PropertyType[] pt = feature.getFeatureType().getProperties();
            for ( PropertyType type : pt ) {
                if ( type.getType() == Types.GEOMETRY ) {
                    return type.getName();
                }
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getName()
     */
    public QualifiedName getName() {
        return name;
    }

}
