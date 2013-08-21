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

package org.deegree.desktop.commands.digitize;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.dataadapter.DataAccessAdapter;
import org.deegree.desktop.dataadapter.FeatureAdapter;
import org.deegree.desktop.i18n.Messages;
import org.deegree.framework.util.GeometryUtils;
import org.deegree.kernel.Command;
import org.deegree.kernel.CommandException;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.GeneratedValueFeatureProperty;
import org.deegree.model.feature.ValueGenerator;
import org.deegree.model.spatialschema.Aggregate;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Ring;
import org.deegree.model.spatialschema.Surface;
import org.deegree.model.spatialschema.SurfaceInterpolationImpl;

/**
 * {@link Command} implementation for splitting a feature/geometry into two or more feature/geometries along a defined
 * {@link Curve}.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class SplitFeatureCommand extends UngroupFeatureCommand {

    private static final QualifiedName name = new QualifiedName( "Split Feature Command" );

    private Surface splitter;

    private Curve originalSplitter;

    /**
     * 
     * @param dataAccessAdapter
     * @param feature
     * @param geomProperty
     * @param splitter
     *            line that will split the geometries of the passed feature
     */
    public SplitFeatureCommand( DataAccessAdapter dataAccessAdapter, Feature feature, QualifiedName geomProperty,
                                Curve splitter ) {
        super( dataAccessAdapter, feature, geomProperty );
        this.originalSplitter = splitter;
        this.splitter = (Surface) splitter.getBuffer( 0.000001, 1, Geometry.BUFFER_CAP_SQUARE );
    }

    @Override
    protected void createNewFeatures( FeatureProperty[] geomFP )
                            throws GeometryException {
        resultFC = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(), 30 );
        for ( FeatureProperty geometryProperty : geomFP ) {
            Geometry geometry = (Geometry) geometryProperty.getValue();
            boolean a = geometry.contains( originalSplitter.getEndPoint() )
                        && !equalToVertex( geometry, originalSplitter.getEndPoint() );
            boolean b = geometry.contains( originalSplitter.getStartPoint() )
                        && !equalToVertex( geometry, originalSplitter.getStartPoint() );
            if ( a || b ) {
                throw new CommandException( Messages.get( "$DG10095" ) );
            }
            List<Geometry> geometries = new ArrayList<Geometry>( 30 );

            // this realizes splitting the original geometry. It works because the
            // splitter geometry is a very narrow polygon the divides a original
            // geometry into one or more parts
            Geometry geom = geometry.difference( splitter );

            if ( geom instanceof Aggregate ) {
                Geometry[] geoms = ( (Aggregate) geom ).getAll();
                for ( Geometry geometry2 : geoms ) {
                    geometries.add( geometry2 );
                }
            } else {
                geometries.add( geometry );
            }

            // after splitting a geometry by a narrow polygon points must be moved to their neighbors if
            // distance is less than ~ double of splitting polygon thickness to ensure that result geometries
            // are really touching their direct neighbor
            List<Geometry> finalGeom = new ArrayList<Geometry>( geometries.size() );
            for ( int i = 0; i < geometries.size(); i++ ) {
                Geometry geom1 = geometries.get( i );
                if ( geom1 instanceof Surface ) {
                    handleSurface( geometries, finalGeom, i, geom1 );
                } else if ( geom1 instanceof Curve ) {
                    Position[] g1Pos = ( (Curve) geom1 ).getAsLineString().getPositions();
                    g1Pos = clean( g1Pos, false );
                    for ( int j = i + 1; j < geometries.size(); j++ ) {
                        Geometry geom2 = geometries.get( j );
                        Position[] g2Pos = ( (Curve) geom2 ).getAsLineString().getPositions();
                        g2Pos = clean( g2Pos, false );
                        for ( int k = 0; k < g1Pos.length; k++ ) {
                            double minD = 9E9;
                            int u = -1;
                            for ( int k2 = 0; k2 < g2Pos.length; k2++ ) {
                                double d = GeometryUtils.distance( g1Pos[k], g2Pos[k2] );
                                if ( d < 0.00001 && d < minD ) {
                                    u = k2;
                                    minD = d;
                                }
                            }
                            if ( u > -1 ) {
                                g1Pos[k] = GeometryFactory.createPosition( g2Pos[u].getX(), g2Pos[u].getY() );
                            }
                        }
                    }
                    finalGeom.add( GeometryFactory.createCurve( g1Pos, geom1.getCoordinateSystem() ) );
                }
            }
            FeatureProperty[] fp = feature.getProperties();
            Feature defaultFeature = ( (FeatureAdapter) dataAccessAdapter ).getDefaultFeature( feature.getName() );
            for ( Geometry geometry2 : finalGeom ) {
                FeatureProperty[] newFp = new FeatureProperty[fp.length];
                for ( int i = 0; i < newFp.length; i++ ) {
                    if ( fp[i].getName().equals( geometryProperty.getName() ) ) {
                        newFp[i] = FeatureFactory.createFeatureProperty( geometryProperty.getName(), geometry2 );
                    } else {
                        FeatureProperty tmpProperty = defaultFeature.getDefaultProperty( fp[i].getName() );
                        if ( tmpProperty instanceof GeneratedValueFeatureProperty ) {
                            ValueGenerator valueGenerator = ( (GeneratedValueFeatureProperty) tmpProperty ).getValueGenerator();
                            newFp[i] = FeatureFactory.createGeneratedValueFeatureProperty( fp[i].getName(),
                                                                                           valueGenerator );
                        } else {
                            newFp[i] = FeatureFactory.createFeatureProperty( fp[i].getName(), fp[i].getValue() );
                        }
                    }
                }
                Feature newFeature = FeatureFactory.createFeature( "ID_" + UUID.randomUUID().toString(),
                                                                   feature.getFeatureType(), newFp );
                resultFC.add( newFeature );
            }
        }
    }

    private void handleSurface( List<Geometry> geometries, List<Geometry> finalGeom, int i, Geometry geom1 )
                            throws GeometryException {
        Position[] g1Ext = ( (Surface) geom1 ).getSurfaceBoundary().getExteriorRing().getPositions();
        g1Ext = clean( g1Ext, true );

        for ( int j = i + 1; j < geometries.size(); j++ ) {
            Geometry geom2 = geometries.get( j );
            if ( geom2 instanceof Surface ) {
                Position[] g2Ext = ( (Surface) geom2 ).getSurfaceBoundary().getExteriorRing().getPositions();
                g2Ext = clean( g2Ext, true );
                for ( int k = 0; k < g1Ext.length; k++ ) {
                    double minD = 9E9;
                    int u = -1;
                    for ( int k2 = 0; k2 < g2Ext.length; k2++ ) {
                        double d = GeometryUtils.distance( g1Ext[k], g2Ext[k2] );
                        if ( d < 0.00001 && d < minD ) {
                            u = k2;
                            minD = d;
                        }
                    }
                    if ( u > -1 ) {
                        g1Ext[k] = GeometryFactory.createPosition( g2Ext[u].getX(), g2Ext[u].getY() );
                    }
                }
            }
        }
        Position[][] g1IrPos = getInnerRings( geom1 );
        Surface surface = GeometryFactory.createSurface( g1Ext, g1IrPos, new SurfaceInterpolationImpl(),
                                                         geom1.getCoordinateSystem() );
        surface = (Surface) GeometryUtils.ensureClockwise( surface );
        finalGeom.add( surface );
    }

    /**
     * 
     * @param ext
     * @param close
     * @return
     */
    private Position[] clean( Position[] ext, boolean close ) {
        List<Position> tmp = new ArrayList<Position>( ext.length );
        tmp.add( ext[0] );
        int k = 0;

        for ( int i = 0; i < ext.length; i++ ) {
            if ( GeometryUtils.distance( tmp.get( k ), ext[i] ) > 0.00001 ) {
                tmp.add( ext[i] );
                k++;
            }
        }
        // ensure that first an last position are equal
        if ( close && !tmp.get( 0 ).equals( tmp.get( tmp.size() - 1 ) ) ) {
            tmp.add( tmp.get( tmp.size() - 1 ) );
        }

        return tmp.toArray( new Position[tmp.size()] );
    }

    /**
     * @param geometry
     * @param point
     * @return
     * @throws GeometryException
     */
    private boolean equalToVertex( Geometry geometry, Point point )
                            throws GeometryException {
        geometry.setTolerance( 0.001 );
        if ( geometry instanceof Curve ) {
            Curve curve = (Curve) geometry;
            Position[] positions = curve.getAsLineString().getPositions();
            for ( Position position : positions ) {
                if ( position.equals( point.getPosition() ) ) {
                    return true;
                }
            }
        } else if ( geometry instanceof Surface ) {
            Surface surface = (Surface) geometry;
            Position[] positions = surface.getSurfaceBoundary().getExteriorRing().getPositions();
            for ( Position position : positions ) {
                if ( position.equals( point.getPosition() ) ) {
                    return true;
                }
            }
            Ring[] rings = surface.getSurfaceBoundary().getInteriorRings();
            for ( Ring ring : rings ) {
                positions = ring.getPositions();
                for ( Position position : positions ) {
                    if ( position.equals( point.getPosition() ) ) {
                        return true;
                    }
                }
            }
        } else if ( geometry instanceof MultiSurface ) {
            Curve[] curves = ( (MultiCurve) geometry ).getAllCurves();
            for ( Curve curve : curves ) {
                Position[] positions = curve.getAsLineString().getPositions();
                for ( Position position : positions ) {
                    if ( position.equals( point.getPosition() ) ) {
                        return true;
                    }
                }
            }
        } else if ( geometry instanceof Surface ) {
            Surface[] surfaces = ( (MultiSurface) geometry ).getAllSurfaces();
            for ( Surface surface : surfaces ) {
                Position[] positions = surface.getSurfaceBoundary().getExteriorRing().getPositions();
                for ( Position position : positions ) {
                    if ( position.equals( point.getPosition() ) ) {
                        return true;
                    }
                }
                Ring[] rings = surface.getSurfaceBoundary().getInteriorRings();
                for ( Ring ring : rings ) {
                    positions = ring.getPositions();
                    for ( Position position : positions ) {
                        if ( position.equals( point.getPosition() ) ) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private Position[][] getInnerRings( Geometry geom1 ) {
        Position[][] irPos = null;
        Ring[] g1Ir = ( (Surface) geom1 ).getSurfaceBoundary().getInteriorRings();
        if ( g1Ir != null && g1Ir.length > 0 ) {
            irPos = new Position[g1Ir.length][];
            for ( int k = 0; k < g1Ir.length; k++ ) {
                irPos[k] = g1Ir[k].getPositions();
            }
        }
        return irPos;
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
