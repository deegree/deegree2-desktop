//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

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
package org.deegree.igeo.commands.digitize;

import java.util.ArrayList;
import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.GeometryUtils;
import org.deegree.framework.util.Pair;
import org.deegree.framework.utils.LineUtils;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.Command;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;

/**
 * {@link Command} implementation for joining two {@link Curve}s. Two modes are available:
 * <ul>
 * <li>1. the result will be one curve
 * <li>2. the connection will be modeled a new curve
 * </ul>
 * Curves can be connected by direct connection, by stretching each each curve till intersection and by an arc.
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class JoinCurvesCommand extends AbstractCommand {

    private static final ILogger LOG = LoggerFactory.getLogger( JoinCurvesCommand.class );

    private static final QualifiedName name = new QualifiedName( "Connect Curves" );

    public enum CURVE_CONNECTION_TYPE {
        tangent, circleFixedRadiusEndNodes, circleFixedRadiusShortenCurves, spline, direct, stretchLines, shortenLines, stretchAndShortenLines, splitLines, splitAndAlternate
    };

    private CURVE_CONNECTION_TYPE curveConnType;

    private boolean connectionAsNewCurve;

    private Curve curve1;

    private Curve curve2;

    private Feature feature1;

    private Feature feature2;

    private boolean performed = false;

    private FeatureAdapter featureAdapter;

    private QualifiedName geomProperty;

    private Feature newFeature;

    private int noOfSegments;

    private Point point1;

    private Point point2;

    /**
     * 
     * @param featureAdapter
     * @param geomProperty
     * @param curveConnType
     * @param connectionAsNewCurve
     * @param feature1
     * @param point1
     * @param feature2
     * @param point2
     * @param noOfSegments
     */
    public JoinCurvesCommand( FeatureAdapter featureAdapter, QualifiedName geomProperty,
                              CURVE_CONNECTION_TYPE curveConnType, boolean connectionAsNewCurve, Feature feature1,
                              Point point1, Feature feature2, Point point2, int noOfSegments ) {
        this.featureAdapter = featureAdapter;
        this.geomProperty = geomProperty;
        this.curveConnType = curveConnType;
        this.connectionAsNewCurve = connectionAsNewCurve;
        this.feature1 = feature1;
        this.feature2 = feature2;
        this.point1 = point1;
        this.point2 = point2;
        if ( geomProperty == null ) {
            this.curve1 = (Curve) feature1.getDefaultGeometryPropertyValue();
            this.curve2 = (Curve) feature2.getDefaultGeometryPropertyValue();
        } else {
            this.curve1 = (Curve) feature1.getDefaultProperty( geomProperty ).getValue();
            this.curve2 = (Curve) feature2.getDefaultProperty( geomProperty ).getValue();
        }
        this.noOfSegments = noOfSegments;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {
        LOG.logInfo( "connect with method: " + curveConnType );
        switch ( curveConnType ) {
        case tangent:
            executeTangent();
            break;
        case circleFixedRadiusEndNodes:
            executeCircleFixedRadiusEndNodes();
            break;
        case circleFixedRadiusShortenCurves:
            executeCircleFixedRadiusShortenCurves();
            break;
        case spline:
            executeSpline();
            break;
        case direct:
            executeDirect();
            break;
        case stretchLines:
            executeStretchLines();
            break;
        case shortenLines:
            executeShortenLines();
            break;
        case stretchAndShortenLines:
            executeStretchAndShortenLines();
            break;
        case splitLines:
            executeSplitLines();
            break;
        case splitAndAlternate:
            executeSplitAndAlternate();
            break;
        default:
            executeDirect();
            break;
        }
        performed = true;
    }

    private void executeTangent()
                            throws Exception {
        Pair<Position[], Position[]> segments = findSegements( );
        Position[] segment1 = segments.first;
        Position[] segment2 = segments.second;

        // ensure that segments intersection is not part one of the segments
        double[] line1 = LineUtils.getLineFromPoints( segment1[0], segment1[1] );
        double[] line2 = LineUtils.getLineFromPoints( segment2[0], segment2[1] );
        Position intersection = LineUtils.getLineIntersection( line1[0], line1[1], line2[0], line2[1] );
        // if ( tmp1.contains( intersection ) || tmp2.contains( intersection ) ) {
        // throw new Exception( "intersection between lines constructed on curve segements to "
        // + "connect must not been contained by one of the segments. Use direct, "
        // + "stretchLines, shortenLines or stretchAndShortenLines instead" );
        // }

        // correct line segment that is farthest from intersection point and get intersection
        // between perpendicular lines going through points to connect
        double d1 = GeometryUtils.distance( point1.getX(), point1.getY(), intersection.getX(), intersection.getY() );
        double d2 = GeometryUtils.distance( point2.getX(), point2.getY(), intersection.getX(), intersection.getY() );
        Pair<Position, Position> pair = null;
        double[] line1p = null;
        double[] line2p = null;
        Position p1 = point1.getPosition();
        Position p2 = point2.getPosition();
        if ( d1 > d2 ) {
            pair = LineUtils.getSymmetricPoints( intersection.getX(), intersection.getY(), line1[0], d2 );
            if ( GeometryUtils.distance( pair.first, point1.getPosition() ) < GeometryUtils.distance(
                                                                                                      pair.second,
                                                                                                      point1.getPosition() ) ) {
                p1 = pair.first;
            } else {
                p1 = pair.second;
            }
            line1p = LineUtils.getPerpendicularLine( line1[0], p1.getX(), p1.getY() );
            line2p = LineUtils.getPerpendicularLine( line2[0], p2.getX(), p2.getY() );
            QualifiedName qn = feature1.getFeatureType().getGeometryProperties()[0].getName();
            curve1 = stretchCurve( curve1, p1 );
            feature1.getDefaultProperty( qn ).setValue( curve1 );
        } else {
            pair = LineUtils.getSymmetricPoints( intersection.getX(), intersection.getY(), line2[0], d1 );
            if ( GeometryUtils.distance( pair.first, point2.getPosition() ) < GeometryUtils.distance(
                                                                                                      pair.second,
                                                                                                      point2.getPosition() ) ) {
                p2 = pair.first;
            } else {
                p2 = pair.second;
            }
            line2p = LineUtils.getPerpendicularLine( line2[0], p2.getX(), p2.getY() );
            line1p = LineUtils.getPerpendicularLine( line1[0], p1.getX(), p1.getY() );
            QualifiedName qn = feature2.getFeatureType().getGeometryProperties()[0].getName();
            curve2 = stretchCurve( curve2, p2 );
            feature2.getDefaultProperty( qn ).setValue( curve2 );
        }
        // intersection between perpendicular lines is center of required circle
        Position center = LineUtils.getLineIntersection( line1p[0], line1p[1], line2p[0], line2p[1] );
        double r = GeometryUtils.distance( center, p1 );
        Curve connection = GeometryUtils.calcCircleCoordinates( center, r, noOfSegments, p1, p2,
                                                                curve1.getCoordinateSystem() );

        if ( connectionAsNewCurve ) {
            insertNewCurve( connection );
        } else {
            uniteCurves( connection );
        }
    }

    private void executeCircleFixedRadiusEndNodes() {

    }

    private void executeCircleFixedRadiusShortenCurves() {

    }

    private void executeSpline() {

    }

    private void executeDirect()
                            throws Exception {
        Pair<Point, Point> points = getNewCurveStartAndEndPoints();
        Position[] positions = new Position[] { points.first.getPosition(), points.second.getPosition() };
        Curve connection = GeometryFactory.createCurve( positions, points.first.getCoordinateSystem() );
        if ( connectionAsNewCurve ) {
            insertNewCurve( connection );
        } else {
            uniteCurves( connection );
        }
    }

    private void executeStretchLines()
                            throws Exception {

        Pair<Position[], Position[]> segments = findSegements( );
        Position[] segment1 = segments.first;
        Position[] segment2 = segments.second;
        // find intersection of segments
        double[] line1 = LineUtils.getLineFromPoints( segment1[0], segment1[1] );
        double[] line2 = LineUtils.getLineFromPoints( segment2[0], segment2[1] );
        Position intersection = LineUtils.getLineIntersection( line1[0], line1[1], line2[0], line2[1] );
        Envelope tmp1 = GeometryFactory.createCurve( segment1, curve1.getCoordinateSystem() ).getEnvelope();
        Envelope tmp2 = GeometryFactory.createCurve( segment2, curve1.getCoordinateSystem() ).getEnvelope();

        if ( tmp1.contains( intersection ) ) {
            // intersection is contained within curve segment 1, so just segment 2 must be stretched
            if ( connectionAsNewCurve ) {
                Position[] positions = new Position[] { segment2[0], intersection };
                Curve connection1 = GeometryFactory.createCurve( positions, curve1.getCoordinateSystem() );
                insertNewCurve( connection1 );
            } else {
                // positions = new Position[] { segment2[1], intersection };
                // Curve connection = GeometryFactory.createCurve( positions, curve1.getCoordinateSystem() );
                // uniteCurves( connection );
                LOG.logWarning( "should never happen" );
            }
        } else if ( tmp2.contains( intersection ) ) {
            // intersection is contained within curve segment 2, so just segment 1 must be stretched
            if ( connectionAsNewCurve ) {
                Position[] positions = new Position[] { segment1[0], intersection };
                Curve connection1 = GeometryFactory.createCurve( positions, curve1.getCoordinateSystem() );
                insertNewCurve( connection1 );
            } else {
                // positions = new Position[] { segment1[1], intersection };
                // Curve connection = GeometryFactory.createCurve( positions, curve1.getCoordinateSystem() );
                // uniteCurves( connection );
                LOG.logWarning( "should never happen" );
            }
        } else {
            // intersection is outside of both curve segments
            if ( connectionAsNewCurve ) {
                Position[] positions = new Position[] { segment1[1], intersection };
                Curve connection1 = GeometryFactory.createCurve( positions, curve1.getCoordinateSystem() );
                positions = new Position[] { segment2[1], intersection };
                Curve connection2 = GeometryFactory.createCurve( positions, curve1.getCoordinateSystem() );
                insertNewCurve( connection1 );
                insertNewCurve( connection2 );
            } else {
                Position[] positions = new Position[] { segment1[1], intersection, segment2[1] };
                // Curve connection = GeometryFactory.createCurve( positions, curve1.getCoordinateSystem() );
                // uniteCurves( connection );
                stretchCurves( positions );
            }
        }
    }

    private void executeShortenLines() {

    }

    private void executeStretchAndShortenLines()
                            throws Exception {
        Pair<Position[], Position[]> segments = findSegements( );
        Position[] segment1 = segments.first;
        Position[] segment2 = segments.second;
        // find intersection of segments
        double[] line1 = LineUtils.getLineFromPoints( segment1[0], segment1[1] );
        double[] line2 = LineUtils.getLineFromPoints( segment2[0], segment2[1] );
        Position intersection = LineUtils.getLineIntersection( line1[0], line1[1], line2[0], line2[1] );
        Envelope tmp1 = GeometryFactory.createCurve( segment1, curve1.getCoordinateSystem() ).getEnvelope();
        Envelope tmp2 = GeometryFactory.createCurve( segment2, curve1.getCoordinateSystem() ).getEnvelope();
        if ( tmp1.contains( intersection ) ) {
            // intersection is contained within curve segment 1, so just segment 2 must be stretched
            LOG.logWarning( "not implemented yet" );
        } else if ( tmp2.contains( intersection ) ) {
            // intersection is contained within curve segment 2, so just segment 1 must be stretched
            LOG.logWarning( "not implemented yet" );
        } else {
            // intersection is outside of both curve segments
            LOG.logWarning( "not implemented yet" );
        }

    }

    private void executeSplitLines() {
        LOG.logWarning( "not implemented yet" );
    }

    private void executeSplitAndAlternate() {
        LOG.logWarning( "not implemented yet" );
    }

    private Pair<Position[], Position[]> findSegements( )
                            throws Exception {
        Position[] segment1 = new Position[2];
        Position[] segment2 = new Position[2];
        // get required curve segments (line string)
        Position[] positions = curve1.getAsLineString().getAsLineString().getPositions();
        if ( point1.equals( curve1.getEndPoint() ) || point2.equals( curve1.getEndPoint() ) ) {
            segment1[0] = positions[positions.length - 2];
            segment1[1] = positions[positions.length - 1];
        } else {
            segment1[0] = positions[1];
            segment1[1] = positions[0];
        }
        positions = curve2.getAsLineString().getAsLineString().getPositions();
        if ( point1.equals( curve2.getEndPoint() ) || point2.equals( curve2.getEndPoint() ) ) {
            segment2[0] = positions[positions.length - 2];
            segment2[1] = positions[positions.length - 1];
        } else {
            segment2[0] = positions[1];
            segment2[1] = positions[0];
        }
        return new Pair<Position[], Position[]>( segment1, segment2 );
    }

    /**
     * 
     * @return two points to connect to each other
     * @throws Exception
     */
    private Pair<Point, Point> getNewCurveStartAndEndPoints()
                            throws Exception {
        Point p1 = curve1.getStartPoint();
        if ( !p1.equals( point1 ) ) {
            p1 = curve1.getEndPoint();
        }
        Point p2 = curve2.getStartPoint();
        if ( !p2.equals( point2 ) ) {
            p2 = curve2.getEndPoint();
        }
        return new Pair<Point, Point>( p1, p2 );
    }

    /**
     * @param connection
     * @throws CloneNotSupportedException
     */
    private void insertNewCurve( Curve connection )
                            throws CloneNotSupportedException {
        newFeature = featureAdapter.getDefaultFeature( featureAdapter.getSchema().getName() );
        // create a feature for connection curve
        newFeature = newFeature.cloneDeep();
        // create geometry property for connection curve
        FeatureProperty fp = null;
        if ( geomProperty != null ) {
            fp = FeatureFactory.createFeatureProperty( geomProperty, connection );
        } else {
            QualifiedName qn = newFeature.getFeatureType().getGeometryProperties()[0].getName();
            fp = FeatureFactory.createFeatureProperty( qn, connection );
        }
        // update new feature with connection curve as geometry
        newFeature.setProperty( fp, 0 );
        featureAdapter.insertFeature( newFeature );
    }

    /**
     * @param connection
     * @throws GeometryException
     * @throws CloneNotSupportedException
     */
    private void uniteCurves( Curve connection )
                            throws GeometryException, CloneNotSupportedException {
        Position[] pos1 = curve1.getAsLineString().getPositions();
        Position[] pos2 = curve2.getAsLineString().getPositions();
        Position[] conPos = connection.getAsLineString().getPositions();
        List<Position> list = new ArrayList<Position>( pos1.length + pos2.length + conPos.length );
        if ( pos1[0].equals( conPos[0] ) ) {
            invertOrder( pos1 );
        } else if ( pos1[pos1.length - 1].equals( conPos[0] ) ) {
            LOG.logWarning( "should never happen" );
        } else if ( pos1[0].equals( conPos[conPos.length - 1] ) ) {
            invertOrder( pos1 );
            invertOrder( conPos );
        } else if ( pos1[pos1.length - 1].equals( conPos[conPos.length - 1] ) ) {
            invertOrder( conPos );
        }
        if ( pos2[pos2.length - 1].equals( conPos[conPos.length - 1] ) ) {
            invertOrder( pos2 );
        }
        for ( Position position : pos1 ) {
            list.add( position );
        }
        for ( Position position : conPos ) {
            list.add( position );
        }
        for ( Position position : pos2 ) {
            list.add( position );
        }

        // must subtract 2 from size of new position array because first and last point
        // of connection curve must be ignored
        Position[] newPositions = list.toArray( new Position[list.size()] );

        Curve newCurve = GeometryFactory.createCurve( newPositions, curve1.getCoordinateSystem() );

        // delete old curves first ...
        featureAdapter.deleteFeature( feature1 );
        featureAdapter.deleteFeature( feature2 );
        // .. and replace by new one
        insertNewCurve( newCurve );
    }

    /**
     * 
     * @param connection
     * @throws GeometryException
     * @throws CloneNotSupportedException
     */
    private void stretchCurves( Position[] positions )
                            throws GeometryException, CloneNotSupportedException {
        Position[] pos1 = curve1.getAsLineString().getPositions();
        Position[] pos2 = curve2.getAsLineString().getPositions();
        List<Position> list = new ArrayList<Position>( pos1.length + pos2.length );
        if ( pos1[0].equals( positions[0] ) || pos1[0].equals( positions[positions.length - 1] ) ) {
            invertOrder( pos1 );
        }

        if ( pos2[0].equals( positions[0] ) || pos2[0].equals( positions[positions.length - 1] ) ) {
            invertOrder( pos2 );
        }

        // delete old curves first
        featureAdapter.deleteFeature( feature1 );
        featureAdapter.deleteFeature( feature2 );

        // first curve
        for ( Position position : pos1 ) {
            list.add( position );
        }
        list.add( positions[1] );
        Position[] newPositions = list.toArray( new Position[list.size()] );
        Curve newCurve = GeometryFactory.createCurve( newPositions, curve1.getCoordinateSystem() );
        // .. and replace first by new one
        insertNewCurve( newCurve );

        list.clear();
        // second curve
        for ( Position position : pos2 ) {
            list.add( position );
        }
        list.add( positions[1] );
        newPositions = list.toArray( new Position[list.size()] );
        newCurve = GeometryFactory.createCurve( newPositions, curve1.getCoordinateSystem() );
        // .. and replace second by new one
        insertNewCurve( newCurve );
    }

    private void invertOrder( Position[] positions ) {
        for ( int i = 0; i < positions.length / 2; i++ ) {
            Position p = positions[i];
            positions[i] = positions[positions.length - i - 1];
            positions[positions.length - i - 1] = p;
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

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getResult()
     */
    public Object getResult() {
        return newFeature;
    }

    @Override
    public boolean isUndoSupported() {
        return false;
    }

    @Override
    public void undo()
                            throws Exception {
        if ( performed ) {
            LOG.logWarning( "undo not implemented yet" );
        }
    }

    /**
     * @param curve
     * @param intersection
     * @throws GeometryException
     */
    private Curve stretchCurve( Curve curve, Position intersection )
                            throws GeometryException {
        Position[] pos = curve.getAsLineString().getPositions();
        ArrayList<Position> list = new ArrayList<Position>( pos.length + 1 );
        if ( GeometryUtils.distance( pos[0], intersection ) < GeometryUtils.distance( pos[pos.length - 1], intersection ) ) {
            list.add( intersection );
            for ( Position position2 : pos ) {
                list.add( position2 );
            }
        } else {
            for ( Position position2 : pos ) {
                list.add( position2 );
            }
            list.add( intersection );
        }
        return GeometryFactory.createCurve( list.toArray( new Position[list.size()] ), curve.getCoordinateSystem() );
    }

}
