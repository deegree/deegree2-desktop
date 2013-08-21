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
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.deegree.datatypes.QualifiedName;
import org.deegree.kernel.AbstractCommand;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.Point;

/**
 * Command for inserting a feature into (feature) dataaccess adapter. Undo is supported and will be performed by
 * deleting the feature that has been inserted before from a DataAccessAdapter
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class MoveFeatureCommand extends AbstractCommand {

    private FeatureCollection featureCollction;

    private QualifiedName geomProperty;

    private List<Geometry> geometries;

    private double dx;

    private double dy;

    private boolean performed = false;

    private double[] distance;

    /**
     * 
     * @param feature
     *            could be a feature or a feature collection. In case of a feature collection all contained features
     *            will be moved
     * @param geomProperty
     * @param distance
     */
    public MoveFeatureCommand( Feature feature, QualifiedName geomProperty, double[] distance ) {
        if ( feature instanceof FeatureCollection ) {
            this.featureCollction = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(),
                                                                            ( (FeatureCollection) feature ).size() );
            this.featureCollction.addAllUncontained( (FeatureCollection) feature );
        } else {
            this.featureCollction = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(), 1 );
            this.featureCollction.add( feature );
        }
        this.geomProperty = geomProperty;
        this.distance = distance;
    }

    /**
     * 
     * @param feature
     *            could be a feature or a feature collection. In case of a feature collection all contained features
     *            will be moved
     * @param geomProperty
     * @param geometries
     */
    public MoveFeatureCommand( Feature feature, QualifiedName geomProperty, List<Geometry> geometries ) {
        if ( feature instanceof FeatureCollection ) {
            this.featureCollction = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(),
                                                                            ( (FeatureCollection) feature ).size() );
            this.featureCollction.addAllUncontained( (FeatureCollection) feature );
        } else {
            this.featureCollction = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(), 1 );
            this.featureCollction.add( feature );
        }
        this.geometries = new ArrayList<Geometry>();
        for ( Geometry geometry : geometries ) {
            this.geometries.add( geometry );
        }
        this.geomProperty = geomProperty;
        calculatedDistance();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {

        if ( !performed && featureCollction != null ) {
            Iterator<Feature> iterator = featureCollction.iterator();
            while ( iterator.hasNext() ) {
                Feature feature = iterator.next();
                if ( geomProperty == null ) {
                    // move/translate all geometries of a feature
                    Geometry[] geoms = feature.getGeometryPropertyValues();
                    for ( Geometry geometry : geoms ) {
                        geometry.translate( distance );
                    }
                } else {
                    FeatureProperty[] geoms = feature.getProperties( geomProperty );
                    for ( FeatureProperty property : geoms ) {
                        if ( property.getValue() != null ) {
                            ( (Geometry) property.getValue() ).translate( distance );
                        }
                    }
                }
            }
        }
        performed = true;
        fireCommandProcessedEvent();
    }

    private void calculatedDistance() {
        Point p1 = (Point) this.geometries.get( 0 );
        Point p2 = (Point) this.geometries.get( 1 );
        dx = -1 * ( p1.getX() - p2.getX() );
        dy = -1 * ( p1.getY() - p2.getY() );
        distance = new double[] { dx, dy };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getName()
     */
    public QualifiedName getName() {
        return new QualifiedName( "MoveFeatureCommand" );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getResult()
     */
    public Object getResult() {
        return featureCollction;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#isUndoSupported()
     */
    public boolean isUndoSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#undo()
     */
    public void undo()
                            throws Exception {

        // performs an undoing by inverting the performed translation
        if ( performed && featureCollction != null ) {
            double[] d = new double[] { -distance[0], -distance[1] };
            Iterator<Feature> iterator = featureCollction.iterator();
            while ( iterator.hasNext() ) {
                Feature feature = iterator.next();
                if ( geomProperty == null ) {
                    // move/translate all geometries of a feature
                    Geometry[] geoms = feature.getGeometryPropertyValues();
                    for ( Geometry geometry : geoms ) {
                        geometry.translate( d );
                    }
                } else {
                    FeatureProperty[] geoms = feature.getProperties( geomProperty );
                    for ( FeatureProperty property : geoms ) {
                        if ( property.getValue() != null ) {
                            ( (Geometry) property.getValue() ).translate( d );
                        }
                    }
                }
            }
        }
        performed = false;

    }

}
