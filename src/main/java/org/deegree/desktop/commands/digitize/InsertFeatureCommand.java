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

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.dataadapter.DataAccessAdapter;
import org.deegree.desktop.dataadapter.FeatureAdapter;
import org.deegree.desktop.mapmodel.DatabaseDatasource;
import org.deegree.desktop.mapmodel.Datasource;
import org.deegree.desktop.mapmodel.FileDatasource;
import org.deegree.desktop.mapmodel.MemoryDatasource;
import org.deegree.desktop.mapmodel.WFSDatasource;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.CommandException;
import org.deegree.model.feature.Feature;

/**
 * Command for inserting a feature into (feature) dataaccess adapter. Undo is supported and will be performed by
 * deleting the feature that has been inserted before from a DataAccessAdapter
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class InsertFeatureCommand extends AbstractCommand {

    private DataAccessAdapter dataAccessAdapter;

    private Feature feature;

    private boolean performed = false;

    private Feature[] features;

    /**
     * 
     */
    public static final QualifiedName commandName = new QualifiedName( "Insert Feature Command" );

    /**
     * 
     * @param dataAccessAdapter
     * @param feature
     */
    public InsertFeatureCommand( DataAccessAdapter dataAccessAdapter, Feature feature ) {
        this.feature = feature;
        this.dataAccessAdapter = dataAccessAdapter;
    }

    /**
     * @param adapter
     * @param features
     */
    public InsertFeatureCommand( DataAccessAdapter adapter, Feature[] features ) {
        this.features = features;
        dataAccessAdapter = adapter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {
        if ( feature != null || features != null ) {
            Datasource datasource = dataAccessAdapter.getDatasource();
            if ( datasource instanceof WFSDatasource ) {
                WFSDatasource wd = (WFSDatasource) datasource;
                QualifiedName[] fTypes = wd.getGetFeature().getQuery()[0].getTypeNames();
                for ( QualifiedName name : fTypes ) {
                    // evaluate if featuretype to be inserted is managed the datasource
                    if ( feature != null && name.equals( feature.getFeatureType().getName() ) ) {
                        add();
                        return;
                    } else if ( features != null && name.equals( features[0].getFeatureType().getName() ) ) {
                        add();
                        return;
                    }  
                }
                // this will just be reached if featuretype to be inserted is not managed
                // by the datasource
                throw new CommandException( "featuretype: " + feature.getFeatureType().getName()
                                            + " is not managed by datasource: " + datasource.getName() );
            } else if ( datasource instanceof FileDatasource ) {
                add();
            } else if ( datasource instanceof DatabaseDatasource ) {
                add();
            } else if ( datasource instanceof MemoryDatasource ) {
                add();
            }
        }
        performed = true;
        fireCommandProcessedEvent();
    }

    private void add() {
        if ( feature == null ) {
            for ( Feature f : features ) {
                ( (FeatureAdapter) dataAccessAdapter ).insertFeature( f );
            }
        } else {
            ( (FeatureAdapter) dataAccessAdapter ).insertFeature( feature );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getName()
     */
    public QualifiedName getName() {
        return commandName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getResult()
     */
    public Object getResult() {
        if ( features != null ) {
            return features;
        }
        return feature;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#isUndoSupported()
     */
    @Override
    public boolean isUndoSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#undo()
     */
    @Override
    public void undo()
                            throws Exception {

        // performs an undoing by deleting the feature that has been inserted
        if ( performed && feature != null ) {
            Datasource datasource = dataAccessAdapter.getDatasource();
            if ( datasource instanceof WFSDatasource ) {
                WFSDatasource wd = (WFSDatasource) datasource;
                QualifiedName[] fTypes = wd.getGetFeature().getQuery()[0].getTypeNames();
                for ( QualifiedName name : fTypes ) {
                    // evaluate if featuretype to be inserted is managed the datasource
                    if ( name.equals( feature.getFeatureType().getName() ) ) {
                        ( (FeatureAdapter) dataAccessAdapter ).deleteFeature( feature );
                        return;
                    }
                }
            } else if ( dataAccessAdapter.getDatasource() instanceof FileDatasource ) {
                ( (FeatureAdapter) dataAccessAdapter ).deleteFeature( feature );
            }
        }
    }

}
