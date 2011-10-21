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

package org.deegree.igeo.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.deegree.datatypes.QualifiedName;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.dataadapter.WMSGridCoverageAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.Command;
import org.deegree.model.Identifier;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.spatialschema.Envelope;

/**
 * {@link Command} implementation for requesting alpha numerical data to one or more objects/features identified by
 * their {@link Identifier} or an {@link Envelope}
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class ObjectInfoCommand extends AbstractCommand {

    public static final QualifiedName commandName = new QualifiedName( "Object Info Command" );

    private MapModel mapModel;

    private Envelope bbox;

    private Object result;

    private List<Identifier> fids;

    /**
     * 
     * @param mapModel
     * @param bbox
     */
    public ObjectInfoCommand( MapModel mapModel, Envelope bbox ) {
        this.mapModel = mapModel;
        this.bbox = bbox;
        this.result = new ArrayList<FeatureCollection>();
    }

    /**
     * 
     * @param mapModel
     * @param fids
     */
    public ObjectInfoCommand( MapModel mapModel, List<Identifier> fids ) {
        this.mapModel = mapModel;
        this.fids = fids;
        this.result = new ArrayList<FeatureCollection>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.Command#execute()
     */
    @SuppressWarnings("unchecked")
    public void execute()
                            throws Exception {

        List<Layer> layers = this.mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION );

        if ( isCanceled() ) {
            result = cancelResult;
            return;
        }
        // iterate on all layers
        for ( Layer layer : layers ) {
            if ( isCanceled() ) {
                result = cancelResult;
                return;
            }
            // if a layer is selected for answering object info requests ...
            if ( layer.isQueryable() ) {
                // ... get its data access adapters
                List<DataAccessAdapter> daa = layer.getDataAccess();
                for ( DataAccessAdapter adapter2 : daa ) {
                    if ( isCanceled() ) {
                        result = cancelResult;
                        return;
                    }
                    if ( adapter2 instanceof FeatureAdapter ) {
                        // feature adapter will return a collection of features intersecting
                        // the passed bbox
                        if ( bbox != null ) {
                            FeatureCollection fc = ( (FeatureAdapter) adapter2 ).getFeatureCollection( bbox );
                            ( (List<FeatureCollection>) result ).add( fc );
                        } else {
                            String id = "ID_" + UUID.randomUUID().toString();
                            FeatureCollection rFC = FeatureFactory.createFeatureCollection( id, 100 );
                            FeatureCollection fc = ( (FeatureAdapter) adapter2 ).getFeatureCollection();
                            for ( Identifier fid : fids ) {
                                Feature feature = fc.getFeature( fid.getAsQualifiedString() );
                                if ( feature != null ) {
                                    rFC.add( feature );
                                }
                            }
                            ( (List<FeatureCollection>) result ).add( rFC );
                        }
                    } else if ( adapter2 instanceof WMSGridCoverageAdapter ) {
                        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
                        int x = (int) gt.getDestX( bbox.getCentroid().getX() );
                        int y = (int) gt.getDestY( bbox.getCentroid().getY() );
                        FeatureCollection fc = ( (WMSGridCoverageAdapter) adapter2 ).getFeatureInfo( x, y );
                        ( (List<FeatureCollection>) result ).add( fc );

                    }
                }
            } else {
                DialogFactory.openWarningDialog( mapModel.getApplicationContainer().getViewPlatform(), null,
                                                 Messages.get( "$MD11404", layers.get( 0 ).getTitle() ),
                                                 Messages.get( "$MD11405" ) );
            }
        }
        fireCommandProcessedEvent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.Command#getName()
     */
    public QualifiedName getName() {
        return commandName;
    }

    /**
     * @return feature collection containing info objects or <code>CancelResult</code>
     */
    public Object getResult() {
        return result;
    }

}
