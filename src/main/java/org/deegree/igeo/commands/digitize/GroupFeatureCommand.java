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

package org.deegree.igeo.commands.digitize;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.WFSDatasource;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.Command;
import org.deegree.kernel.CommandException;
import org.deegree.model.feature.DefaultFeature;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.spatialschema.Aggregate;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Surface;

/**
 * {@link Command} implementation for grouping two or more features/geometries into a multi geometry
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class GroupFeatureCommand extends AbstractCommand {
    
    private static final ILogger LOG = LoggerFactory.getLogger( GroupFeatureCommand.class );

    private static final QualifiedName name = new QualifiedName( "Group Feature Command" );

    protected DataAccessAdapter dataAccessAdapter;

    protected FeatureCollection featureCollction;

    protected QualifiedName geomProperty;

    protected String featureId;

    protected boolean performed;

    protected Feature newFeature;

    private Class<?>[] classes;

    protected Feature[] originalFeatures;

    /**
     * 
     * @param dataAccessAdapter
     * @param featureCollection
     * @param geomProperty
     * @param featureId
     *            Id of the feature which shall be source of the new features properties
     * @param classes
     *            geometry classes to consider. If one of the passed feature contains a geometry that does not match the
     *            passed class list the command will fail.
     */
    public GroupFeatureCommand( DataAccessAdapter dataAccessAdapter, FeatureCollection featureCollection,
                                QualifiedName geomProperty, String featureId, Class<?>... classes ) {
        this.dataAccessAdapter = dataAccessAdapter;
        this.featureCollction = featureCollection;
        this.originalFeatures = featureCollection.toArray();
        this.featureId = featureId;
        this.classes = classes;
        this.geomProperty = geomProperty;
        this.performed = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {

        if ( !this.performed ) {
            Feature referenceFeature = featureCollction.getFeature( featureId );

            if ( referenceFeature == null ) {
                String s = Messages.getMessage( Locale.getDefault(), "$MD10282", featureId );
                throw new CommandException( s );
            }
            List<Geometry> geomList = new ArrayList<Geometry>( featureCollction.size() );
            Iterator<Feature> iterator = featureCollction.iterator();
            while ( iterator.hasNext() ) {
                Feature feature = iterator.next();

                Geometry geom = null;
                if ( geomProperty == null ) {
                    // use features default geometry if no specific geometry property for
                    // grouping is defined
                    geom = feature.getDefaultGeometryPropertyValue();
                } else {
                    geom = (Geometry) feature.getProperties( geomProperty )[0].getValue();
                }
                // check if current geometry is instance of one of defined types
                boolean correctInstance = false;
                for ( Class<?> clzz : classes ) {
                    if ( clzz.isInstance( geom ) ) {
                        correctInstance = true;
                        break;
                    }
                }
                if ( !correctInstance ) {
                    throw new CommandException( Messages.getMessage( Locale.getDefault(), "$MD10283" ) );
                }
                if ( geom instanceof Aggregate ) {
                    // if current geometry is a multi geometry it must be split and its parts
                    // must be added to geometrylist independently
                    Geometry[] g = ( (Aggregate) geom ).getAll();
                    for ( Geometry geometry : g ) {
                        geomList.add( geometry );
                    }
                } else {
                    geomList.add( geom );
                }
            }

            // now create Multigeometry
            Geometry newGeom = null;
            if ( classes[0] == Geometry.class ) {
                // TODO
                // not supported at the moment: untyped geometry collections
                LOG.logWarning( "not supported at the moment: untyped geometry collections" );
            } else {
                if ( geomList.get( 0 ) instanceof Point ) {
                    Point[] points = geomList.toArray( new Point[geomList.size()] );
                    newGeom = GeometryFactory.createMultiPoint( points, points[0].getCoordinateSystem() );
                } else if ( geomList.get( 0 ) instanceof Curve ) {
                    Curve[] curves = geomList.toArray( new Curve[geomList.size()] );
                    newGeom = GeometryFactory.createMultiCurve( curves, curves[0].getCoordinateSystem() );
                } else if ( geomList.get( 0 ) instanceof Surface ) {
                    Surface[] surfaces = geomList.toArray( new Surface[geomList.size()] );
                    newGeom = GeometryFactory.createMultiSurface( surfaces, surfaces[0].getCoordinateSystem() );
                }
            }

            // create feature property for created multi geometry
            FeatureProperty fp = null;
            if ( geomProperty != null ) {
                fp = FeatureFactory.createFeatureProperty( geomProperty, newGeom );
            } else {
                QualifiedName qn = referenceFeature.getFeatureType().getGeometryProperties()[0].getName();
                fp = FeatureFactory.createFeatureProperty( qn, newGeom );
            }

            // referenced feature must be cloned to ensure that it will have a different
            // ID than one of the base features
            newFeature = (Feature) ( (DefaultFeature) referenceFeature ).clone();
            newFeature.setProperty( fp, 0 );

            // now remove base features from data access ...
            deleteOldFeatures();
            // ... and add the new one
            insertNewFeature( newFeature );
        }
        this.performed = true;
        fireCommandProcessedEvent();
    }

    protected void insertNewFeature( Feature feature ) {
        Datasource datasource = dataAccessAdapter.getDatasource();
        if ( datasource instanceof WFSDatasource ) {
            WFSDatasource wd = (WFSDatasource) datasource;
            QualifiedName[] fTypes = wd.getGetFeature().getQuery()[0].getTypeNames();
            for ( QualifiedName name : fTypes ) {
                // evaluate if featuretype to be inserted is managed the datasource
                if ( name.equals( feature.getFeatureType().getName() ) ) {
                    ( (FeatureAdapter) dataAccessAdapter ).insertFeature( feature );
                    return;
                }
            }
            // this will just be reached if featuretype to be inserted is not managed
            // by the datasource
            String s = Messages.getMessage( Locale.getDefault(), "$MD10284", feature.getFeatureType().getName(),
                                            datasource.getName() );
            throw new CommandException( s );
        } else if ( dataAccessAdapter instanceof FeatureAdapter ) {
            ( (FeatureAdapter) dataAccessAdapter ).insertFeature( feature );
        }
    }

    protected void deleteOldFeatures() {

        Datasource datasource = dataAccessAdapter.getDatasource();
        Iterator<Feature> iterator = featureCollction.iterator();
        while ( iterator.hasNext() ) {
            Feature feat = iterator.next();
            deleteFeature( datasource, feat );
        }
    }

    protected void deleteFeature( Datasource datasource, Feature feat ) {
        if ( datasource instanceof WFSDatasource ) {
            WFSDatasource wd = (WFSDatasource) datasource;
            QualifiedName[] fTypes = wd.getGetFeature().getQuery()[0].getTypeNames();
            for ( QualifiedName name : fTypes ) {
                // evaluate if featuretype to be deleted is managed the datasource
                if ( name.equals( feat.getFeatureType().getName() ) ) {
                    ( (FeatureAdapter) dataAccessAdapter ).deleteFeature( feat );
                    return;
                }
            }
            // this will just be reached if featuretype to be deleted is not managed
            // by the datasource
            String s = Messages.getMessage( Locale.getDefault(), "$MD10284", feat.getFeatureType().getName(),
                                            datasource.getName() );
            throw new CommandException( s );
        } else if ( dataAccessAdapter instanceof FeatureAdapter ) {
            ( (FeatureAdapter) dataAccessAdapter ).deleteFeature( feat );
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
     * @return the new feature containing grouped geometries as a MultiGeometry property
     */
    public Object getResult() {
        return newFeature;
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
        if ( performed ) {
            Datasource datasource = dataAccessAdapter.getDatasource();
            deleteFeature( datasource, newFeature );
            for ( int i = 0; i < originalFeatures.length; i++ ) {
                insertNewFeature( originalFeatures[i] );
            }
            performed = false;
        }

    }

}
