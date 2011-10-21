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

package org.deegree.igeo.dataadapter;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.utils.PathManipulator;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.config.AbstractLinkedTableType;
import org.deegree.igeo.config.FileDatasourceType;
import org.deegree.igeo.config.LinkedDatabaseTableType;
import org.deegree.igeo.config.LinkedFileTableType;
import org.deegree.igeo.config.MemoryDatasourceType;
import org.deegree.igeo.config.Util;
import org.deegree.igeo.mapmodel.DatabaseDatasource;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.FileDatasource;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MemoryDatasource;
import org.deegree.igeo.mapmodel.RasterFileDatasource;
import org.deegree.igeo.mapmodel.VectorFileDatasource;
import org.deegree.igeo.mapmodel.WCSDatasource;
import org.deegree.igeo.mapmodel.WFSDatasource;
import org.deegree.igeo.mapmodel.WMSDatasource;
import org.deegree.io.JDBCConnection;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.spatialschema.Envelope;

/**
 * Factory class for creating data access adapters and data sources
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class DataAccessFactory {

    private static final ILogger LOG = LoggerFactory.getLogger( DataAccessFactory.class );

    private static String rasterFormats;

    static {
        if ( rasterFormats == null ) {
            rasterFormats = "|gif|tif|tiff|jpg|jpeg|png|bmp|";
        }
    }

    /**
     * creates a data access adapter for a file datasource
     * 
     * @param file
     * @param crs
     * @return the data access adapter
     */
    public static Datasource createDatasource( ApplicationContainer<?> appCont, File file, String crs ) {

        FileDatasourceType fdst = new FileDatasourceType();
        fdst.setName( file.getName() );
        fdst.setMinScaleDenominator( 0d );
        fdst.setMaxScaleDenominator( 9E99 );
        fdst.setEditable( true );
        fdst.setLazyLoading( false );
        fdst.setQueryable( true );
        fdst.setSupportToolTips( true );
        if ( file.isAbsolute() ) {
            fdst.setFile( file.getAbsolutePath() );
            String folder = null;
            try {
                folder = new File( appCont.resolve( "." ).getFile() ).getPath();
            } catch ( MalformedURLException e1 ) {
                // never happens
                LOG.logWarning( "", e1 );
            }
            try {
                fdst.setFile( PathManipulator.mapRelativePath( folder, file.getAbsolutePath() ) );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        } else {
            fdst.setFile( file.getPath() );
        }
        if ( crs != null ) {
            fdst.setNativeCRS( crs );
        }

        if ( file.getName().toLowerCase().endsWith( ".shp" ) || file.getName().toLowerCase().endsWith( ".mif" )
             || file.getName().toLowerCase().endsWith( ".gml" ) || file.getName().toLowerCase().endsWith( ".xml" ) ) {
            // at the moment just shape files and map info files are supported as
            // vector sources
            return new VectorFileDatasource( fdst, null, null );
        } else {
            return new RasterFileDatasource( fdst, null, null );
        }
    }

    /**
     * @param name
     * @param featureCollection
     * @return memory based datasource
     */
    public static Datasource createDatasource( String name, FeatureCollection featureCollection ) {
        Envelope env = null;
        try {
            env = featureCollection.getBoundedBy();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        MemoryDatasourceType mdst = new MemoryDatasourceType();
        mdst.setName( name );
        mdst.setExtent( Util.convertEnvelope( env ) );
        mdst.setEditable( true );
        mdst.setLazyLoading( false );
        mdst.setQueryable( true );
        mdst.setSupportToolTips( true );
        mdst.setMinScaleDenominator( 0d );
        mdst.setMaxScaleDenominator( 9999999999d );

        return new MemoryDatasource( mdst, null, null, featureCollection );

    }

    /**
     * creates a data access adapter for a service datasource
     * 
     * @param capabilitiesURL
     * @param serviceType
     * 
     * @return the data access adapter
     */
    public static Datasource createDatasource( URL capabilitiesURL, String serviceType ) {
        // TODO
        return null;
    }

    /**
     * creates a data access adapter for a database datasource
     * 
     * @param jdbc
     * @param mapModel
     * @param layer
     * @return the data access adapter
     */
    public static Datasource createDatasource( JDBCConnection jdbc, MapModel mapModel ) {
        // TODO
        return null;
    }

    /**
     * 
     * @param datasource
     * @param mapModel
     * @param layer
     * @return according adapter for the passed datasource
     */
    public static DataAccessAdapter createDataAccessAdapter( Datasource datasource, MapModel mapModel, Layer layer ) {
        DataAccessAdapter daa = null;

        LOG.logDebug( "creating adapter for datasource : ", datasource.getName() );

        if ( datasource instanceof FileDatasource ) {
            File file = ( (FileDatasource) datasource ).getFile();
            // a file datasource may describes a vector or a raster file. Depending on file
            // extension this decision will be made and corresponding adapter will be used
            boolean rasterFormat = isRasterFormat( file );
            boolean isLazyLoading = ( (FileDatasource) datasource ).isLazyLoading();
            if ( rasterFormat ) {
                daa = new FileGridCoverageAdapter( datasource, layer, mapModel, isLazyLoading );
            } else {
                daa = new FileFeatureAdapter( datasource, layer, mapModel, isLazyLoading );
            }
        } else if ( datasource instanceof WFSDatasource ) {
            URL url = ( (WFSDatasource) datasource ).getCapabilitiesURL();
            boolean isLazyLoading = ( (WFSDatasource) datasource ).isLazyLoading();
            daa = new WFSFeatureAdapter( datasource, layer, mapModel, url, isLazyLoading );
        } else if ( datasource instanceof WMSDatasource ) {
            URL url = ( (WMSDatasource) datasource ).getCapabilitiesURL();
            daa = new WMSGridCoverageAdapter( datasource, layer, mapModel, url );
        } else if ( datasource instanceof WCSDatasource ) {
            URL url = ( (WCSDatasource) datasource ).getCapabilitiesURL();
            daa = new WCSGridCoverageAdapter( datasource, layer, mapModel, url );
        } else if ( datasource instanceof DatabaseDatasource ) {
            boolean isLazyLoading = ( (DatabaseDatasource) datasource ).isLazyLoading();
            daa = new DatabaseFeatureAdapter( (DatabaseDatasource) datasource, layer, mapModel, isLazyLoading );
        } else if ( datasource instanceof MemoryDatasource ) {
            daa = new MemoryFeatureAdapter( (MemoryDatasource) datasource, layer, mapModel );
        }
        if ( datasource.getLinkedTables().size() > 0 ) {
            for ( AbstractLinkedTableType lk : datasource.getLinkedTables() ) {
                LinkedTable lt = createLinkedTable( lk );
                // notice that the returned DataAccessAdapter will be passed to the 
                // method in next iteration
                daa = new LinkedTableAdapter( (FeatureAdapter) daa, lt );
            }
        }
        daa.addChangeListener( layer );

        return daa;
    }

    private static LinkedTable createLinkedTable( AbstractLinkedTableType lk ) {
        LinkedTable lt = null;
        if ( lk instanceof LinkedDatabaseTableType ) {
            try {
                lt = new LinkedDatabaseTable( (LinkedDatabaseTableType) lk );
            } catch ( IOException e ) {
                LOG.logError( e );
                throw new DataAccessException( e );
            }
        } else if ( lk instanceof LinkedFileTableType ) {
            String s = ( (LinkedFileTableType) lk ).getFile();
            try {
                if ( s.toLowerCase().endsWith( ".dbf" ) ) {
                    lt = new LinkedDBaseTable( lk, new File( s ) );
                } else if ( s.toLowerCase().endsWith( ".csv" ) || s.toLowerCase().endsWith( ".tab" ) ) {
                    lt = new LinkedCSVTable( lk, new File( s ) );
                } else if ( s.toLowerCase().endsWith( ".xls" ) || s.toLowerCase().endsWith( ".xlsx" ) ) {
                    lt = new LinkedExcelTable( lk, new File( s ) );
                }
            } catch ( IOException e ) {
                LOG.logError( e );
                throw new DataAccessException( e );
            }
        }
        return lt;
    }

    private static boolean isRasterFormat( File file ) {
        String name = file.getName().toLowerCase();
        int pos = name.lastIndexOf( '.' );
        String ext = name.substring( pos + 1, name.length() );
        return rasterFormats.indexOf( '|' + ext + '|' ) > -1;
    }

}
