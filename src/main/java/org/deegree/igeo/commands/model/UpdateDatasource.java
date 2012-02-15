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

package org.deegree.igeo.commands.model;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.igeo.config.JDBCConnection;
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.mapmodel.AuthenticationInformation;
import org.deegree.igeo.mapmodel.DatabaseDatasource;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.Datasource.DS_PARAMETER;
import org.deegree.igeo.mapmodel.FileDatasource;
import org.deegree.igeo.mapmodel.WCSDatasource;
import org.deegree.igeo.mapmodel.WFSDatasource;
import org.deegree.igeo.mapmodel.WMSDatasource;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.Command;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;

/**
 * {@link Command} implementation for updating a datasource assigned to a layer (e.g. new bounding box)
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class UpdateDatasource extends AbstractCommand {

    private DataAccessAdapter dataAccessAdapter;

    private Datasource datasource;

    private Map<Datasource.DS_PARAMETER, Object> parameters;

    private Map<Datasource.DS_PARAMETER, Object> backup = new HashMap<Datasource.DS_PARAMETER, Object>();

    public static final QualifiedName name = new QualifiedName( "Qualified Name" );

    private boolean performed = false;

    /**
     * 
     * @param layer
     * @param datasource
     * @param parameters
     */
    public UpdateDatasource( DataAccessAdapter dataAccessAdapter, Map<Datasource.DS_PARAMETER, Object> parameters ) {
        this.dataAccessAdapter = dataAccessAdapter;
        this.datasource = dataAccessAdapter.getDatasource();
        this.parameters = parameters;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {
        createDatasourceBackup();
        updateDatasource( parameters );
        fireCommandProcessedEvent();
    }

    private void updateDatasource( Map<Datasource.DS_PARAMETER, Object> newParameters )
                            throws IOException, XMLParsingException {
        if ( newParameters.containsKey( DS_PARAMETER.authenticationInfo ) )
            datasource.setAuthenticationInformation( (AuthenticationInformation) newParameters.get( DS_PARAMETER.authenticationInfo ) );
        if ( newParameters.containsKey( DS_PARAMETER.extent ) )
            datasource.setExtent( (Envelope) newParameters.get( DS_PARAMETER.extent ) );
        if ( newParameters.containsKey( DS_PARAMETER.minScaleDenom ) )
            datasource.setMinScaleDenominator( (Double) newParameters.get( DS_PARAMETER.minScaleDenom ) );
        if ( newParameters.containsKey( DS_PARAMETER.maxScaleDenom ) )
            datasource.setMaxScaleDenominator( (Double) newParameters.get( DS_PARAMETER.maxScaleDenom ) );
        if ( newParameters.containsKey( DS_PARAMETER.name ) )
            datasource.setName( (String) newParameters.get( DS_PARAMETER.name ) );
        if ( datasource instanceof FileDatasource ) {
            FileDatasource ds = (FileDatasource) datasource;
            ds.setFile( (File) newParameters.get( DS_PARAMETER.file ) );
            ds.setLazyLoading( (Boolean) newParameters.get( DS_PARAMETER.lazyLoading ) );
        } else if ( datasource instanceof WMSDatasource ) {
            WMSDatasource ds = (WMSDatasource) datasource;
            ds.setCapabilitiesURL( (URL) newParameters.get( DS_PARAMETER.capabilitiesURL ) );
            ds.setBaseRequest( (String) newParameters.get( DS_PARAMETER.baseRequest ) );
        } else if ( datasource instanceof WFSDatasource ) {
            WFSDatasource ds = (WFSDatasource) datasource;
            ds.setCapabilitiesURL( (URL) newParameters.get( DS_PARAMETER.capabilitiesURL ) );
            ds.setGeometryProperty( (QualifiedName) newParameters.get( DS_PARAMETER.geomProperty ) );
            ds.setGetFeature( (GetFeature) newParameters.get( DS_PARAMETER.getFeature ) );
            ds.setLazyLoading( (Boolean) newParameters.get( DS_PARAMETER.lazyLoading ) );
        } else if ( datasource instanceof WCSDatasource ) {
            WCSDatasource ds = (WCSDatasource) datasource;
            ds.setCapabilitiesURL( (URL) newParameters.get( DS_PARAMETER.capabilitiesURL ) );
            ds.setCoverage( (QualifiedName) newParameters.get( DS_PARAMETER.coverage ) );
        } else if ( datasource instanceof DatabaseDatasource ) {
            DatabaseDatasource ds = (DatabaseDatasource) datasource;
            ds.setJdbc( (JDBCConnection) newParameters.get( DS_PARAMETER.jdbc ) );
            // TODO
            // ds.setSqlTemplate( (String) newParameters.get( DS_PARAMETER.sqlTemplate ) );
        }
        dataAccessAdapter.invalidate();
        dataAccessAdapter.refresh();
    }

    private void createDatasourceBackup() {
        backup.put( DS_PARAMETER.extent, datasource.getExtent() );
        backup.put( DS_PARAMETER.name, datasource.getName() );
        backup.put( DS_PARAMETER.minScaleDenom, datasource.getMinScaleDenominator() );
        backup.put( DS_PARAMETER.maxScaleDenom, datasource.getMaxScaleDenominator() );
        backup.put( DS_PARAMETER.authenticationInfo, datasource.getAuthenticationInformation() );
        if ( datasource instanceof FileDatasource ) {
            FileDatasource ds = (FileDatasource) datasource;
            backup.put( DS_PARAMETER.file, ds.getFile() );
        } else if ( datasource instanceof WMSDatasource ) {
            WMSDatasource ds = (WMSDatasource) datasource;
            backup.put( DS_PARAMETER.baseRequest, ds.getBaseRequest() );
            backup.put( DS_PARAMETER.capabilitiesURL, ds.getCapabilitiesURL() );
        } else if ( datasource instanceof WFSDatasource ) {
            WFSDatasource ds = (WFSDatasource) datasource;
            backup.put( DS_PARAMETER.geomProperty, ds.getGeometryProperty() );
            backup.put( DS_PARAMETER.getFeature, ds.getGetFeature() );
            backup.put( DS_PARAMETER.capabilitiesURL, ds.getCapabilitiesURL() );
        } else if ( datasource instanceof WCSDatasource ) {
            WCSDatasource ds = (WCSDatasource) datasource;
            backup.put( DS_PARAMETER.coverage, ds.getCoverage() );
            backup.put( DS_PARAMETER.capabilitiesURL, ds.getCapabilitiesURL() );
        } else if ( datasource instanceof DatabaseDatasource ) {
            DatabaseDatasource ds = (DatabaseDatasource) datasource;
            backup.put( DS_PARAMETER.jdbc, ds.getJdbc() );
            backup.put( DS_PARAMETER.sqlTemplate, ds.getSqlTemplate() );
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

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getResult()
     */
    public Object getResult() {
        if ( performed ) {
            // updated datasource
            return datasource;
        } else {
            return null;
        }
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
            updateDatasource( backup );
        }
    }

}
