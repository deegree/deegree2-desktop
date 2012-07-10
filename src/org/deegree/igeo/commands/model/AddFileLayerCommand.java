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
package org.deegree.igeo.commands.model;

import java.io.File;

import org.deegree.datatypes.QualifiedName;
import org.deegree.igeo.config.Util;
import org.deegree.igeo.dataadapter.DataAccessFactory;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.LayerGroup;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.VectorFileDatasource;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.kernel.AbstractCommand;
import org.deegree.model.Identifier;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class AddFileLayerCommand extends AbstractCommand {

    // private static final ILogger LOG = getLogger( AddFileLayerCommand.class );

    public static final QualifiedName commandName = new QualifiedName( "add layer from file command" );

    private MapModel mapModel;

    private File file;

    private Object result;

    private String serviceName;

    private String serviceTitle;

    private String serviceAbstract;

    private double minScaleDenominator = 0;

    private double maxScaleDenominator = 9E99;

    private String crs;

    private Layer newLayer;

    private boolean performed = false;

    private boolean lazyLoading = false;

    /**
     * 
     * @param mapModel
     * @param file
     * @param serviceName
     * @param serviceTitle
     * @param serviceAbstract
     * @param crs
     */
    public AddFileLayerCommand( MapModel mapModel, File file, String serviceName, String serviceTitle,
                                String serviceAbstract, String crs ) {
        this( mapModel, file, serviceName, serviceTitle, serviceAbstract, 0.0, 9E99, false, crs );
    }

    /**
     * 
     * @param mapModel
     * @param file
     * @param serviceName
     * @param serviceTitle
     * @param serviceAbstract
     * @param maxScaleDenominator
     * @param minScaleDenominator
     * @param lazyLoading
     * @param crs
     */
    public AddFileLayerCommand( MapModel mapModel, File file, String serviceName, String serviceTitle,
                                String serviceAbstract, double minScaleDenominator, double maxScaleDenominator,
                                boolean lazyLoading, String crs ) {
        this.mapModel = mapModel;
        this.file = file;
        this.serviceName = serviceName;
        this.serviceTitle = serviceTitle;
        this.serviceAbstract = serviceAbstract;
        this.minScaleDenominator = minScaleDenominator;
        this.maxScaleDenominator = maxScaleDenominator;
        this.lazyLoading = lazyLoading;
        this.crs = crs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.Command#execute()
     */
    public void execute()
                            throws Exception {
        String name = this.serviceName;
        if ( name == null || !( name.length() > 0 ) ) {
            name = this.file.getName();
            int p = name.lastIndexOf( '.' );
            name = name.substring( 0, p );
        }
        String title = this.serviceTitle;
        if ( title == null || !( title.length() > 0 ) ) {
            title = name;
        }
        if ( isCanceled() ) {
            result = cancelResult;
            return;
        }
        // avoid double layer name/id
        Identifier id = new Identifier( name );
        int i = 0;
        String tmp = title;
        while ( mapModel.exists( id ) ) {
            tmp = title + "_" + i;
            id = new Identifier( name + "_" + i++ );           
        }
        newLayer = new Layer( mapModel, id, tmp, this.serviceAbstract );
        if ( isCanceled() ) {
            result = cancelResult;
            return;
        }
        Datasource ds = DataAccessFactory.createDatasource( mapModel.getApplicationContainer(), file, crs );
        try {
            ds.setMinScaleDenominator( minScaleDenominator );
            ds.setMaxScaleDenominator( maxScaleDenominator );
            ds.getDatasourceType().setExtent( Util.convertEnvelope( mapModel.getMaxExtent() ) );
            ds.setLazyLoading( lazyLoading );
            newLayer.addDatasource( ds );
            newLayer.setMinScaleDenominator( minScaleDenominator );
            newLayer.setMaxScaleDenominator( maxScaleDenominator );
        } catch ( Exception e ) {
            processMonitor.cancel();
            throw e;
        }
        if ( ds instanceof VectorFileDatasource ) {
            newLayer.setEditable( true );
        } else {
            newLayer.setEditable( false );
        }

        if ( isCanceled() ) {
            result = cancelResult;
            return;
        }
        if ( mapModel.getLayerGroups().size() == 0 ) {
            LayerGroup layerGroup = new LayerGroup( mapModel, new Identifier(), "LayerGroup", "" );
            mapModel.insert( layerGroup, null, null, false );
        }
        
        if ( mapModel.exists( newLayer.getIdentifier() ) ) {
            processMonitor.cancel();
            DialogFactory.openWarningDialog( mapModel.getApplicationContainer().getViewPlatform(), null,
                                             Messages.get( "$DG10087", newLayer.getIdentifier() ),
                                             Messages.get( "$MD11395" ) );
            return;
        }
        mapModel.insert( newLayer, mapModel.getLayerGroups().get( 0 ), null, false );
        performed = true;
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

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.Command#isUndoSupported()
     */
    public boolean isUndoSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.Command#undo()
     */
    public void undo()
                            throws Exception {
        if ( performed ) {
            mapModel.remove( newLayer );
            performed = false;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.Command#getResult()
     */
    public Object getResult() {
        return result;
    }

}
