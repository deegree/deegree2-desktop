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

import java.util.Locale;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.commands.CommandHelper;
import org.deegree.desktop.dataadapter.FeatureAdapter;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.kernel.Command;
import org.deegree.kernel.CommandException;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Ring;
import org.deegree.model.spatialschema.Surface;
import org.deegree.model.spatialschema.SurfaceInterpolationImpl;

/**
 * {@link Command} implementation for creating a polygon hole by explicit defined hole (geometry)  
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class CreatePolygonHoleCommand extends AbstractPolygonHoleCommand {

    private static QualifiedName name = new QualifiedName( "Create Polygon Hole" );

    private boolean performed = false;

    private Feature feature;

    private FeatureAdapter dataAccessAdapter;

    private Geometry oldGeom;

    private Surface hole;

    private QualifiedName geomProperty;

    /**
     * 
     * @param dataAccessAdapter
     * @param feature
     * @param geomProperty
     * @param hole
     */
    public CreatePolygonHoleCommand( FeatureAdapter dataAccessAdapter, Feature feature, QualifiedName geomProperty,
                                     Surface hole ) {
        if ( feature instanceof FeatureCollection ) {
            this.feature = ( (FeatureCollection) feature ).getFeature( 0 );
        } else {
            this.feature = feature;
        }
        this.dataAccessAdapter = dataAccessAdapter;
        this.hole = hole;
        this.geomProperty = geomProperty;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {
        if ( feature != null ) {
            if ( geomProperty == null ) {
                geomProperty = CommandHelper.findGeomProperty( feature );
            }

            Geometry geom = (Geometry) feature.getProperties( geomProperty )[0].getValue();
            oldGeom = geom;
            if ( !geom.contains( hole ) ) {
                throw new CommandException( Messages.getMessage( Locale.getDefault(), "$MD10309" ) );
            }

            if ( geom instanceof Surface ) {
                Surface surface = (Surface) geom;

                Position[] ext = surface.getSurfaceBoundary().getExteriorRing().getPositions();
                Ring[] interiorRings = surface.getSurfaceBoundary().getInteriorRings();
                Position[][] innerPos = new Position[interiorRings.length + 1][];
                for ( int i = 0; i < innerPos.length - 1; i++ ) {
                    innerPos[i] = interiorRings[i].getPositions();
                }
                innerPos[innerPos.length - 1] = hole.getSurfaceBoundary().getExteriorRing().getPositions();
                if ( isClockwise( innerPos[innerPos.length - 1] ) ) {
                    invertPositionOrder( innerPos[innerPos.length - 1] );
                }

                geom = GeometryFactory.createSurface( ext, innerPos, new SurfaceInterpolationImpl(),
                                                      surface.getCoordinateSystem() );
            } else {
                // must be multi surface
                Surface[] surfaces = ( (MultiSurface) geom ).getAllSurfaces();
                for ( int i = 0; i < surfaces.length; i++ ) {
                    if ( surfaces[i].contains( hole ) ) {
                        Position[] ext = surfaces[i].getSurfaceBoundary().getExteriorRing().getPositions();
                        Ring[] interiorRings = surfaces[i].getSurfaceBoundary().getInteriorRings();
                        Position[][] innerPos = new Position[interiorRings.length + 1][];
                        for ( int j = 0; j < innerPos.length - 1; j++ ) {
                            innerPos[j] = interiorRings[j].getPositions();
                        }
                        innerPos[innerPos.length - 1] = hole.getSurfaceBoundary().getExteriorRing().getPositions();
                        if ( isClockwise( innerPos[innerPos.length - 1] ) ) {
                            invertPositionOrder( innerPos[innerPos.length - 1] );
                        }

                        surfaces[i] = GeometryFactory.createSurface( ext, innerPos, new SurfaceInterpolationImpl(),
                                                                     surfaces[i].getCoordinateSystem() );
                        break;
                    }
                }
                geom = GeometryFactory.createMultiSurface( surfaces );

            }
            FeatureProperty fp = FeatureFactory.createFeatureProperty( geomProperty, geom );
            feature.setProperty( fp, 0 );
        }
        performed = true;
        fireCommandProcessedEvent();
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
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#undo()
     */
    public void undo()
                            throws Exception {
        if ( performed ) {
            FeatureProperty fp = FeatureFactory.createFeatureProperty( geomProperty, oldGeom );
            feature.setProperty( fp, 0 );
            Feature feature2 = dataAccessAdapter.getFeatureCollection().getFeature( feature.getId() );
            feature2.setProperty( fp, 0 );
            Layer layer = dataAccessAdapter.getLayer();
            layer.fireRepaintEvent();
            performed = false;
        }
    }

}
