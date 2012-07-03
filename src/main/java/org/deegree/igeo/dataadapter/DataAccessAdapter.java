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
package org.deegree.igeo.dataadapter;

import java.io.IOException;

import org.deegree.igeo.dataadapter.AdapterEvent.ADAPTER_EVENT_TYPE;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;

/**
 * abstract data access adapter offering some methods that are common to most concrete data adapters
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public abstract class DataAccessAdapter extends Adapter {

    public enum DATASOURCETYPE {
        file, wms, wcs, wfs, database, mixed, memory
    };

    protected Layer layer;

    protected MapModel mapModel;

    protected Datasource datasource;

    /**
     * 
     * @param datasource
     * @param layer
     * @param mapModel
     */
    public DataAccessAdapter( Datasource datasource, Layer layer, MapModel mapModel ) {
        this.datasource = datasource;
        this.layer = layer;
        this.mapModel = mapModel;
    }

    /**
     * 
     * @return layer a a {@link DataAccessAdapter} belongs too
     */
    public Layer getLayer() {
        return layer;
    }

    /**
     * package protected access to adapted data source
     * 
     * @return
     */
    public Datasource getDatasource() {
        return datasource;
    }

    /**
     * notifies all registered listeners that a feature has been deleted
     * 
     */
    protected void fireStartLoadingEvent() {
        AdapterEvent event = new AdapterEvent( layer, ADAPTER_EVENT_TYPE.startedLoading );
        for ( int i = 0; i < this.listeners.size(); i++ ) {
            this.listeners.get( i ).valueChanged( event );
        }
    }

    /**
     * notifies all registered listeners that a feature has been deleted
     * 
     */
    protected void fireLoadingFinishedEvent() {
        AdapterEvent event = new AdapterEvent( layer, ADAPTER_EVENT_TYPE.finishedLoading );
        for ( int i = 0; i < this.listeners.size(); i++ ) {
            this.listeners.get( i ).valueChanged( event );
        }
    }

    /**
     * notifies all registered listeners that a feature has been deleted
     * 
     */
    protected void fireLoadingExceptionEvent() {
        AdapterEvent event = new AdapterEvent( layer, ADAPTER_EVENT_TYPE.exceptionLoading );
        for ( int i = 0; i < this.listeners.size(); i++ ) {
            this.listeners.get( i ).valueChanged( event );
        }
    }

    /**
     * refreshes the interal state of a DataAccessAdapter. Same as {@link #refresh(boolean)} with paramter true
     * 
     */
    public abstract void refresh();

    /**
     * refreshes the interal state of a DataAccessAdapter
     * 
     * @param forceReload
     *            if true the reload of the data is forced, if false {@link #refresh()} is called
     */
    public abstract void refresh( boolean forceReload );

    /**
     * sets the state of a DateaccessAdapter to be invalid. Implementations are responsible for handle an invalid state.
     * 
     */
    public abstract void invalidate();

    /**
     * commit changes that has been performed on data provided by a {@link DataAccessAdapter} to the adapted back end
     * 
     * @throws IOException
     */
    public abstract void commitChanges()
                            throws IOException;

}