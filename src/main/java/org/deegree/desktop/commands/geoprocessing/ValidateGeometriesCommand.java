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

package org.deegree.desktop.commands.geoprocessing;

import java.util.HashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.commands.model.AddErrorLayerCommand;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.settings.Settings;
import org.deegree.desktop.settings.ValidationGeomMetrics;
import org.deegree.framework.util.GeometryUtils;
import org.deegree.framework.util.Pair;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.Command;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.JTSAdapter;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiPrimitive;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Ring;
import org.deegree.model.spatialschema.Surface;

import com.vividsolutions.jts.algorithm.RobustCGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.valid.RepeatedPointTester;

/**
 * command class for validating a list of geometries considering to configuration settings on what types of tests shall
 * be performed. Available options are:
 * <ul>
 * <li>general validation check
 * <li>check for double geometries in collections
 * <li>check for polygon ring(s) orientation
 * <li>check if lines are simple
 * <li>check for minimum segment length
 * <li>check for minimum polygon area size
 * </ul>
 * Most test are performed by validation methods offered by JTS.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class ValidateGeometriesCommand extends AbstractCommand {

    protected Layer layer;

    protected List<Geometry> geometries;

    protected Map<String, String> errorReport = new HashMap<String, String>();

    protected List<Pair<String, Point>> errorPositions = new ArrayList<Pair<String, Point>>();

    protected int errorCount;

    protected static final RepeatedPointTester rpTester = new RepeatedPointTester();

    private static final QualifiedName name = new QualifiedName( "Validate Geometries" );

    protected Settings settings;

    protected MapModel mapModel;

    /**
     * 
     * @param layer
     *            layer the geometries belong too (for future usage)
     * @param geometries
     */
    public ValidateGeometriesCommand( Layer layer, List<Geometry> geometries ) {
        this.layer = layer;
        this.mapModel = layer.getOwner();
        this.geometries = geometries;
        settings = layer.getOwner().getApplicationContainer().getSettings();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {

        errorCount = 0;
        ValidationGeomMetrics vm = settings.getValidationGeomMetrics();
        float minDist = vm.getMinSegmentLength();
        float minArea = vm.getMinPolygonArea();
        for ( int i = 0; i < geometries.size(); i++ ) {
            Geometry geometry = geometries.get( i );
            com.vividsolutions.jts.geom.Geometry jtsGeom = JTSAdapter.export( geometry );
            if ( vm.checkForValidGeometries() && !jtsGeom.isValid() ) {
                addErrorPosition( geometry.getCentroid().getPosition(), Messages.getMessage( Locale.getDefault(),
                                                                                             "$MD10426", i ) );
                errorReport.put( "checkForValidGeometries_" + ( errorCount++ ),
                                 Messages.getMessage( Locale.getDefault(), "$MD10426", i ) );
            }

            if ( vm.disallowRepeatedPoints() && rpTester.hasRepeatedPoint( jtsGeom ) ) {
                Coordinate c = rpTester.getCoordinate();
                Position p = GeometryFactory.createPosition( c.x, c.y );
                addErrorPosition( p, Messages.getMessage( Locale.getDefault(), "$MD10427", i ) );
                errorReport.put( "disallowRepeatedPoints_" + ( errorCount++ ),
                                 Messages.getMessage( Locale.getDefault(), "$MD10427", i ) );
            }
            if ( vm.checkForPolygonOrientation() ) {
                handleCheckForPolygonOrientation( jtsGeom, i );
            }
            if ( vm.disallowDoubleGeomerties() ) {
                handleDisallowDoubleGeomerties( geometry, i );
            }
            if ( vm.ensureSimpleLines() ) {
                handleSimpleLines( geometry, jtsGeom, i );
            }
            if ( vm.limitMinSegmentLength() ) {
                handleMinSegmentLength( geometry, i, minDist );
            }
            if ( vm.limitMinPolygonArea() ) {
                handleMinPolygonArea( geometry, i, minArea );
            }
        }
        if ( errorCount > 0 ) {
            errorReport.put( "errorCount", Integer.toString( errorCount ) );
            Command cmd = new AddErrorLayerCommand( layer.getOwner().getApplicationContainer(), layer, errorPositions );
            layer.getOwner().getApplicationContainer().getCommandProcessor().executeSychronously( cmd, true );
        }
        fireCommandProcessedEvent();
    }

    protected void handleSimpleLines( Geometry geometry, com.vividsolutions.jts.geom.Geometry jtsGeom, int index )
                            throws GeometryException {
        if ( geometry instanceof Curve && !jtsGeom.isSimple() ) {
            addErrorPosition( geometry.getCentroid().getPosition(), Messages.getMessage( Locale.getDefault(),
                                                                                         "$MD10436", index ) );
            errorReport.put( "checkForValidGeometries_" + ( errorCount++ ), Messages.getMessage( Locale.getDefault(),
                                                                                                 "$MD10436", index ) );
        } else if ( geometry instanceof MultiCurve ) {
            if ( !jtsGeom.isSimple() ) {
                addErrorPosition( geometry.getCentroid().getPosition(), Messages.getMessage( Locale.getDefault(),
                                                                                             "$MD10436", index ) );
                errorReport.put( "checkForValidGeometries_" + ( errorCount++ ),
                                 Messages.getMessage( Locale.getDefault(), "$MD10436", index ) );
            }
            int cnt = ( (MultiLineString) jtsGeom ).getNumGeometries();
            for ( int j = 0; j < cnt; j++ ) {
                if ( !( (MultiLineString) jtsGeom ).getGeometryN( j ).isSimple() ) {
                    addErrorPosition( geometry.getCentroid().getPosition(), Messages.getMessage( Locale.getDefault(),
                                                                                                 "$MD10436", index ) );
                    errorReport.put( "checkForValidGeometries_" + ( errorCount++ ),
                                     Messages.getMessage( Locale.getDefault(), "$MD10436", index ) );
                }
            }
        }
    }

    protected void handleCheckForPolygonOrientation( com.vividsolutions.jts.geom.Geometry jtsGeom, int index ) {
        if ( jtsGeom instanceof Polygon ) {
            Polygon polygon = (Polygon) jtsGeom;
            Coordinate[] coords = polygon.getExteriorRing().getCoordinates();
            if ( RobustCGAlgorithms.isCCW( coords ) ) {
                Position p = GeometryFactory.createPosition( polygon.getCentroid().getX(), polygon.getCentroid().getY() );

                addErrorPosition( p, Messages.getMessage( Locale.getDefault(), "$MD10428", index ) );

                errorReport.put( "checkForPolygonOrientation_" + ( errorCount++ ),
                                 Messages.getMessage( Locale.getDefault(), "$MD10428", index ) );
            }
            for ( int j = 0; j < polygon.getNumInteriorRing(); j++ ) {
                coords = polygon.getInteriorRingN( j ).getCoordinates();
                if ( !RobustCGAlgorithms.isCCW( coords ) ) {
                    Position p = GeometryFactory.createPosition( polygon.getCentroid().getX(),
                                                                 polygon.getCentroid().getY() );
                    addErrorPosition( p, Messages.getMessage( Locale.getDefault(), "$MD10429", index ) );

                    errorReport.put( "checkForPolygonOrientation_" + ( errorCount++ ),
                                     Messages.getMessage( Locale.getDefault(), "$MD10429", index ) );
                }
            }
        }
    }

    protected void handleDisallowDoubleGeomerties( Geometry geometry, int index ) {
        if ( geometry instanceof MultiPrimitive ) {
            Geometry[] geoms = ( (MultiPrimitive) geometry ).getAll();
            for ( int j = 0; j < geoms.length; j++ ) {
                for ( int k = j + 1; k < geoms.length; k++ ) {
                    if ( geoms[j].equals( geoms[k] ) ) {
                        String s = Messages.getMessage( Locale.getDefault(), "$MD10430", index );
                        addErrorPosition( geoms[j].getCentroid().getPosition(), s );
                        addErrorPosition( geoms[k].getCentroid().getPosition(), s );
                        errorReport.put( "disallowDoubleGeomerties_" + ( errorCount++ ), s );
                    }

                }
            }
        }
    }

    protected void handleMinSegmentLength( Geometry geometry, int index, float minDist )
                            throws GeometryException {
        if ( geometry instanceof MultiCurve ) {
            Curve[] curves = ( (MultiCurve) geometry ).getAllCurves();
            for ( Curve curve : curves ) {
                if ( !hasMinSegmentLength( curve, minDist, index ) ) {
                    errorReport.put( "minSegmentLength_" + ( errorCount++ ), Messages.getMessage( Locale.getDefault(),
                                                                                                  "$MD10431", index ) );
                }
            }
        } else if ( geometry instanceof MultiSurface ) {
            Surface[] surfaces = ( (MultiSurface) geometry ).getAllSurfaces();
            for ( Surface surface : surfaces ) {
                if ( !hasMinSegmentLength( surface, minDist, index ) ) {
                    errorReport.put( "minSegmentLength_" + ( errorCount++ ), Messages.getMessage( Locale.getDefault(),
                                                                                                  "$MD10431", index ) );
                }
            }
        } else if ( geometry instanceof Curve ) {
            if ( !hasMinSegmentLength( (Curve) geometry, minDist, index ) ) {
                errorReport.put( "minSegmentLength_" + ( errorCount++ ), Messages.getMessage( Locale.getDefault(),
                                                                                              "$MD10431", index ) );
            }
        } else if ( geometry instanceof Surface ) {
            if ( !hasMinSegmentLength( (Surface) geometry, minDist, index ) ) {
                errorReport.put( "minSegmentLength_" + ( errorCount++ ), Messages.getMessage( Locale.getDefault(),
                                                                                              "$MD10431", index ) );
            }
        }
    }

    protected boolean hasMinSegmentLength( Surface surface, float minDist, int index ) {
        Ring ring = surface.getSurfaceBoundary().getExteriorRing();
        Position[] pos = ring.getPositions();
        for ( int i = 0; i < pos.length - 1; i++ ) {
            if ( GeometryUtils.distance( pos[i], pos[i + 1] ) < minDist ) {
                String s = Messages.getMessage( Locale.getDefault(), "$MD10431", index );
                addErrorPosition( pos[i], s );
                addErrorPosition( pos[i + 1], s );
                return false;
            }
        }
        Ring[] rings = surface.getSurfaceBoundary().getInteriorRings();
        for ( Ring ring2 : rings ) {
            pos = ring2.getPositions();
            for ( int i = 0; i < pos.length - 1; i++ ) {
                if ( GeometryUtils.distance( pos[i], pos[i + 1] ) < minDist ) {
                    String s = Messages.getMessage( Locale.getDefault(), "$MD10431", index );
                    addErrorPosition( pos[i], s );
                    addErrorPosition( pos[i + 1], s );
                    return false;
                }
            }
        }
        return true;
    }

    protected boolean hasMinSegmentLength( Curve curve, float minDist, int index )
                            throws GeometryException {
        Position[] pos = curve.getAsLineString().getPositions();
        for ( int i = 0; i < pos.length - 1; i++ ) {
            if ( GeometryUtils.distance( pos[i], pos[i + 1] ) < minDist ) {
                String s = Messages.getMessage( Locale.getDefault(), "$MD10431", index );
                addErrorPosition( pos[i], s );
                addErrorPosition( pos[i + 1], s );

                return false;
            }
        }
        return true;
    }

    protected void handleMinPolygonArea( Geometry geometry, int index, float minArea ) {

        if ( geometry instanceof Surface ) {
            if ( ( (Surface) geometry ).getArea() < minArea ) {
                addErrorPosition( geometry.getCentroid().getPosition(), Messages.getMessage( Locale.getDefault(),
                                                                                             "$MD10432", index ) );
                errorReport.put( "minPolygonArea_" + ( errorCount++ ), Messages.getMessage( Locale.getDefault(),
                                                                                            "$MD10432", index ) );

            }
        } else if ( geometry instanceof MultiSurface ) {
            Surface[] surfaces = ( (MultiSurface) geometry ).getAllSurfaces();
            for ( Surface surface : surfaces ) {
                if ( surface.getArea() < minArea ) {
                    addErrorPosition( surface.getCentroid().getPosition(), Messages.getMessage( Locale.getDefault(),
                                                                                                "$MD10432", index ) );
                    errorReport.put( "minPolygonArea_" + ( errorCount++ ), Messages.getMessage( Locale.getDefault(),
                                                                                                "$MD10433", index ) );
                }
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getName()
     */
    public QualifiedName getName() {
        return name;
    }

    /**
     * @return report (Map) of validation errors. If no errors has occured an empty map will be returned
     */
    public Object getResult() {
        return errorReport;
    }

    /**
     * 
     * @param pos
     * @param message
     */
    protected void addErrorPosition( Position pos, String message ) {
        Pair<String, Point> pa;
        Point p = GeometryFactory.createPoint( pos.getX(), pos.getY(), mapModel.getCoordinateSystem() );
        pa = new Pair<String, Point>();
        pa.first = message;
        pa.second = p;
        errorPositions.add( pa );
    }
}
