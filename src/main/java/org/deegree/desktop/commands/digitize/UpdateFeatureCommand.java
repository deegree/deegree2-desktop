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

import java.util.Iterator;
import java.util.UUID;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.dataadapter.DataAccessAdapter;
import org.deegree.desktop.dataadapter.FeatureAdapter;
import org.deegree.desktop.mapmodel.Datasource;
import org.deegree.desktop.mapmodel.WFSDatasource;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.CommandException;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;

/**
 * Command for inserting a feature into (feature) dataaccess adapter. Undo is supported and will be performed by
 * deleting the feature that has been inserted before from a DataAccessAdapter
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class UpdateFeatureCommand extends AbstractCommand {

    private DataAccessAdapter dataAccessAdapter;

    private FeatureCollection featureCollction;

    private FeatureCollection updatedFeatures;

    private boolean performed = false;

    /**
     * 
     * @param dataAccessAdapter
     * @param feature
     *            could be a feature or a feature collection. In case of a feature collection all contained features
     *            will be updated
     */
    public UpdateFeatureCommand( DataAccessAdapter dataAccessAdapter, Feature feature ) {
        if ( feature instanceof FeatureCollection ) {
            this.featureCollction = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(),
                                                                            ( (FeatureCollection) feature ).size() );
            this.featureCollction.addAllUncontained( (FeatureCollection) feature );
        } else {
            this.featureCollction = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(), 1 );
            this.featureCollction.add( feature );
        }
        this.updatedFeatures = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(), 1 );

        this.dataAccessAdapter = dataAccessAdapter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {

        if ( !performed && featureCollction != null ) {
            Datasource datasource = dataAccessAdapter.getDatasource();
            Iterator<Feature> iterator = featureCollction.iterator();
            while ( iterator.hasNext() ) {
                Feature feature = iterator.next();
                if ( datasource instanceof WFSDatasource ) {
                    WFSDatasource wd = (WFSDatasource) datasource;
                    QualifiedName[] fTypes = wd.getGetFeature().getQuery()[0].getTypeNames();
                    boolean flag = false;
                    for ( QualifiedName name : fTypes ) {
                        // evaluate if featuretype to be deleted is managed the datasource
                        if ( name.equals( feature.getFeatureType().getName() ) ) {
                            Feature oldFeature = ( (FeatureAdapter) dataAccessAdapter ).updateFeature( feature );
                            updatedFeatures.add( oldFeature );
                            flag = true;
                            break;
                        }
                    }
                    if ( !flag ) {
                        // this will just be reached if featuretype to be deleted is not managed
                        // by the datasource
                        throw new CommandException( "featuretype: " + feature.getFeatureType().getName()
                                                    + " is not managed by datasource: " + datasource.getName() );
                    }
                } else if ( dataAccessAdapter instanceof FeatureAdapter ) {
                    ( (FeatureAdapter) dataAccessAdapter ).updateFeature( feature );
                }
            }
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
        return new QualifiedName( "UpdateFeatureCommand" );
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

        // performs an undoing by updating the the datasources with the old version
        // of the updated feature
        if ( performed && featureCollction != null ) {
            Datasource datasource = dataAccessAdapter.getDatasource();
            Iterator<Feature> iterator = updatedFeatures.iterator();
            while ( iterator.hasNext() ) {
                Feature feature = iterator.next();
                if ( datasource instanceof WFSDatasource ) {
                    WFSDatasource wd = (WFSDatasource) datasource;
                    QualifiedName[] fTypes = wd.getGetFeature().getQuery()[0].getTypeNames();
                    for ( QualifiedName name : fTypes ) {
                        // evaluate if featuretype to be deleted is managed the datasource
                        if ( name.equals( feature.getFeatureType().getName() ) ) {
                            ( (FeatureAdapter) dataAccessAdapter ).updateFeature( feature );
                            break;
                        }
                    }
                } else if ( dataAccessAdapter instanceof FeatureAdapter ) {
                    ( (FeatureAdapter) dataAccessAdapter ).updateFeature( feature );
                }
            }
        }
        performed = false;

    }

}
