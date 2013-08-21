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
import org.deegree.desktop.dataadapter.DataAccessAdapter;
import org.deegree.desktop.dataadapter.FeatureAdapter;
import org.deegree.desktop.mapmodel.Datasource;
import org.deegree.desktop.mapmodel.WFSDatasource;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.Command;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.GeometryPropertyType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Aggregate;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;

/**
 * {@link Command} implementation for splitting a multi-geometry in seperate geometries/features
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class UngroupFeatureCommand extends AbstractCommand {

    protected DataAccessAdapter dataAccessAdapter;

    protected FeatureCollection resultFC;

    protected Feature feature;

    protected QualifiedName geomProperty;

    protected boolean performed = false;

    /**
     * 
     * @param dataAccessAdapter
     * @param feature
     * @param geomProperty
     */
    public UngroupFeatureCommand( DataAccessAdapter dataAccessAdapter, Feature feature, QualifiedName geomProperty ) {
        this.feature = feature;
        this.geomProperty = geomProperty;
        this.dataAccessAdapter = dataAccessAdapter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {
        // collect all geometry properties to consider
        FeatureProperty[] geomFP = collectGeometryProperties();

        // create a new feature for each geometry
        createNewFeatures( geomFP );

        // delete feature to be ungrouped from datasource
        Datasource datasource = deleteOldFeature();

        // add new features to datasource
        insertNewFeatures( datasource );
        
        dataAccessAdapter.getLayer().unselectAllFeatures();

        fireCommandProcessedEvent();
        
        performed = true;
    }

    protected void insertNewFeatures( Datasource datasource ) {
        Iterator<Feature> iterator = resultFC.iterator();
        while ( iterator.hasNext() ) {
            Feature newFeature = iterator.next();
            if ( datasource instanceof WFSDatasource ) {
                WFSDatasource wd = (WFSDatasource) datasource;
                QualifiedName[] fTypes = wd.getGetFeature().getQuery()[0].getTypeNames();
                for ( QualifiedName name : fTypes ) {
                    // evaluate if featuretype to be inserted is managed the datasource
                    if ( name.equals( newFeature.getFeatureType().getName() ) ) {
                        ( (FeatureAdapter) dataAccessAdapter ).insertFeature( newFeature );
                        break;
                    }
                }
            } else if ( dataAccessAdapter instanceof FeatureAdapter ) {
                ( (FeatureAdapter) dataAccessAdapter ).insertFeature( newFeature );
            }
        }
    }

    protected Datasource deleteOldFeature() {
        Datasource datasource = dataAccessAdapter.getDatasource();
        if ( datasource instanceof WFSDatasource ) {
            WFSDatasource wd = (WFSDatasource) datasource;
            QualifiedName[] fTypes = wd.getGetFeature().getQuery()[0].getTypeNames();
            for ( QualifiedName name : fTypes ) {
                // evaluate if featuretype to be deleted is managed the datasource
                if ( name.equals( feature.getFeatureType().getName() ) ) {
                    ( (FeatureAdapter) dataAccessAdapter ).deleteFeature( feature );
                    break;
                }
            }
        } else if ( dataAccessAdapter instanceof FeatureAdapter ) {
            ( (FeatureAdapter) dataAccessAdapter ).deleteFeature( feature );
        }
        return datasource;
    }

    protected void createNewFeatures( FeatureProperty[] geomFP ) throws GeometryException {
        resultFC = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(), 30 );
        for ( FeatureProperty property : geomFP ) {
            Geometry geometry = (Geometry) property.getValue();
            List<Geometry> geometries = new ArrayList<Geometry>( 30 );
            if ( geometry instanceof Aggregate ) {
                Geometry[] geoms = ( (Aggregate) geometry ).getAll();
                for ( Geometry geometry2 : geoms ) {
                    geometries.add( geometry2 );
                }
            } else {
                geometries.add( geometry );
            }
            FeatureProperty[] fp = feature.getProperties();
            for ( Geometry geometry2 : geometries ) {
                FeatureProperty[] newFp = new FeatureProperty[fp.length];
                for ( int i = 0; i < newFp.length; i++ ) {
                    if ( fp[i].getName().equals( property.getName() ) ) {
                        newFp[i] = FeatureFactory.createFeatureProperty( property.getName(), geometry2 );
                    } else {
                        newFp[i] = FeatureFactory.createFeatureProperty( fp[i].getName(), fp[i].getValue() );
                    }
                }
                Feature newFeature = FeatureFactory.createFeature( UUID.randomUUID().toString(),
                                                                   feature.getFeatureType(), newFp );
                resultFC.add( newFeature );
            }
        }
    }

    protected FeatureProperty[] collectGeometryProperties() {
        FeatureProperty[] geomFP;
        if ( geomProperty != null ) {
            geomFP = feature.getProperties( geomProperty );
        } else {
            List<FeatureProperty> list = new ArrayList<FeatureProperty>( 10 );
            PropertyType[] pt = feature.getFeatureType().getProperties();
            for ( PropertyType type : pt ) {
                if ( type instanceof GeometryPropertyType ) {
                    FeatureProperty[] tmp = feature.getProperties( type.getName() );
                    for ( FeatureProperty property : tmp ) {
                        list.add( property );
                    }
                }
            }
            geomFP = list.toArray( new FeatureProperty[list.size()] );
        }
        return geomFP;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getName()
     */
    public QualifiedName getName() {
        return new QualifiedName( "UngroupFeatureCommand" );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getResult()
     */
    public Object getResult() {
        return resultFC;
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
            Iterator<Feature> iterator = resultFC.iterator();
            while ( iterator.hasNext() ) {
                Feature newFeature = iterator.next();
                if ( datasource instanceof WFSDatasource ) {
                    WFSDatasource wd = (WFSDatasource) datasource;
                    QualifiedName[] fTypes = wd.getGetFeature().getQuery()[0].getTypeNames();
                    for ( QualifiedName name : fTypes ) {
                        // evaluate if featuretype to be inserted is managed the datasource
                        if ( name.equals( newFeature.getFeatureType().getName() ) ) {
                            ( (FeatureAdapter) dataAccessAdapter ).deleteFeature( newFeature );
                            return;
                        }
                    }
                } else if ( dataAccessAdapter instanceof FeatureAdapter ) {
                    ( (FeatureAdapter) dataAccessAdapter ).deleteFeature( newFeature );
                }
            }
                        
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
            } else if ( dataAccessAdapter instanceof FeatureAdapter ) {
                ( (FeatureAdapter) dataAccessAdapter ).insertFeature( feature);
            }
            performed = false;
        }
    }

}
