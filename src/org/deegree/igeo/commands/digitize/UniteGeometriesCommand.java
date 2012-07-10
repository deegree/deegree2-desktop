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
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.kernel.CommandException;
import org.deegree.model.feature.DefaultFeature;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.spatialschema.Aggregate;
import org.deegree.model.spatialschema.Geometry;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class UniteGeometriesCommand extends GroupFeatureCommand {

    private static final QualifiedName name = new QualifiedName( "Unite Geometries Command" );

    /**
     * 
     * @param dataAccessAdapter
     * @param featureCollection
     * @param geomProperty
     * @param featureId
     *            Id of the feature which shall be source of the new features properties
     */
    public UniteGeometriesCommand( DataAccessAdapter dataAccessAdapter, FeatureCollection featureCollection,
                                   QualifiedName geomProperty, String featureId ) {
        super( dataAccessAdapter, featureCollection, geomProperty, featureId, (Class[]) null );
        this.dataAccessAdapter = dataAccessAdapter;
        this.featureId = featureId;
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

                if ( geom instanceof Aggregate ) {
                    // if current geometry is a multi geometry it must be split and its parts
                    // must be added to geometry list independently
                    Geometry[] g = ( (Aggregate) geom ).getAll();
                    for ( Geometry geometry : g ) {
                        geomList.add( geometry );
                    }
                } else {
                    geomList.add( geom );
                }
            }

            // now create union geometry
            Geometry newGeom = geomList.get( 0 );

            for ( int i = 1; i < geomList.size(); i++ ) {
                newGeom = newGeom.union( geomList.get( i ) );
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

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#isUndoSupported()
     */
    public boolean isUndoSupported() {
        return true;
    }

}
