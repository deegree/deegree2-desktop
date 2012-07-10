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

import static org.deegree.crs.coordinatesystems.GeographicCRS.WGS84;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.UUID;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.GeometryUtils;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.io.FileSystemAccess;
import org.deegree.igeo.io.FileSystemAccessFactory;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.FileDatasource;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;
import org.deegree.io.gpx.GPXReader;
import org.deegree.io.shpapi.ShapeFile;
import org.deegree.io.shpapi.shape_new.ShapeFileWriter;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.GMLFeatureAdapter;
import org.deegree.model.feature.GMLFeatureCollectionDocument;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryImpl;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Surface;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class FileFeatureAdapter extends FeatureAdapter {

    private static final ILogger LOG = LoggerFactory.getLogger( FileFeatureAdapter.class );

    private boolean isLazyLoading;

    private Envelope lastEnv;

    /**
     * 
     * @param module
     * @param datasource
     * @param layer
     * @param mapModel
     * @param file
     * @param isLazyLoading
     */
    FileFeatureAdapter( Datasource datasource, Layer layer, MapModel mapModel, boolean isLazyLoading ) {
        super( datasource, layer, mapModel );
        this.isLazyLoading = isLazyLoading;
        refresh();
        loadSchema();
    }

    /**
     * loads data into internal feature collection
     * 
     */
    private void loadData() {
        LOG.logDebug( "try loading: ", ( (FileDatasource) datasource ).getFile() );
        String nm = ( (FileDatasource) datasource ).getFile().getName().toLowerCase();
        if ( nm.endsWith( ".shp" ) ) {
            loadShapeFile();
        } else if ( nm.endsWith( ".gml" ) || nm.endsWith( ".xml" ) ) {
            loadGMLFile();
        } else if ( nm.endsWith( ".gpx" ) ) {
            loadGPXFile();
        }

        FeatureCollection fc = featureCollections.get( datasource.getName() );
        fc = ensureClockwiseSurfaceOrientation( fc );
        featureCollections.put( datasource.getName(), fc );

        LOG.logDebug( featureCollections.get( datasource.getName() ).size() + " features has been loaded" );
    }

    /**
     * @param fc
     * @return
     */
    private FeatureCollection ensureClockwiseSurfaceOrientation( FeatureCollection fc ) {
        int c = fc.size();
        for ( int i = 0; i < c; i++ ) {
            Feature feature = fc.getFeature( i );
            FeatureProperty[] fp = feature.getProperties();
            for ( int j = 0; j < fp.length; j++ ) {
                Object value = fp[j].getValue();
                if ( value != null && ( value instanceof Surface || value instanceof MultiSurface ) ) {
                    try {
                        fp[j].setValue( GeometryUtils.ensureClockwise( (Geometry) fp[j].getValue() ) );
                    } catch ( GeometryException e ) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return fc;
    }

    /**
     * loads the GML application schema assigned with the feature types of a WFS datasource
     * 
     */
    private void loadSchema() {
        if ( schemas.get( datasource.getName() ) == null ) {
            // to enable lazy loading for shape data sources and avoid reading complete file
            // just to get its feature type definition, read first feature from file
            FeatureType ft = null;
            File file = ( (FileDatasource) datasource ).getFile();
            file = getAbsoluteFilePath( file );
            String nm = file.getName().toLowerCase();
            if ( nm.endsWith( ".shp" ) ) {
                int p = file.getAbsolutePath().lastIndexOf( '.' );
                ShapeFile sf = null;
                try {
                    sf = new ShapeFile( file.getAbsolutePath().substring( 0, p ) );
                    ft = sf.getFeatureByRecNo( 1 ).getFeatureType();
                } catch ( Exception e ) {
                    LOG.logError( e.getMessage(), e );
                    throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10009",
                                                                        file.getAbsolutePath(), e.getMessage() ) );
                } finally {
                    if ( sf != null ) {
                        sf.close();
                    }
                }
            } else if ( nm.endsWith( ".gml" ) || nm.endsWith( ".xml" ) ) {
                if ( featureCollections.get( datasource.getName() ) == null ) {
                    // lazy loading is not supported for GML files because they are not indexed
                    loadGMLFile();
                }
                ft = featureCollections.get( datasource.getName() ).getFeature( 0 ).getFeatureType();
            } else if ( nm.endsWith( ".gpx" ) ) {
                // lazy loading is not supported for GPX files because they are not indexed
                if ( featureCollections.get( datasource.getName() ) == null ) {
                    loadGPXFile();
                }
                ft = featureCollections.get( datasource.getName() ).getFeature( 0 ).getFeatureType();
            }
            schemas.put( datasource.getName(), ft );
        }
    }

    private void loadGPXFile() {
        try {
            File file = ( (FileDatasource) datasource ).getFile();
            file = getAbsoluteFilePath( file );
            datasource.setNativeCoordinateSystem( CRSFactory.create( WGS84 ) );
            FileInputStream fis = new FileInputStream( file );
            FeatureCollection featureCollection = GPXReader.read( fis );
            featureCollection.setEnvelopesUpdated();
            featureCollection = transform( featureCollection );
            try {
                datasource.setExtent( featureCollection.getBoundedBy() );
            } catch ( GeometryException e ) {
                LOG.logError( "Unknown error", e );
            }
            featureCollections.put( datasource.getName(), featureCollection );
        } catch ( FileNotFoundException e ) {
        }
    }

    private void loadGMLFile() {
        GMLFeatureCollectionDocument doc = new GMLFeatureCollectionDocument();
        File file = ( (FileDatasource) datasource ).getFile();
        file = getAbsoluteFilePath( file );
        FileSystemAccessFactory fsaf = FileSystemAccessFactory.getInstance( mapModel.getApplicationContainer() );
        try {
            FileSystemAccess fsa = fsaf.getFileSystemAccess( FILECHOOSERTYPE.geoDataFile );
            URL url = fsa.getFileURL( file.getAbsolutePath() );
            doc.load( url );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10070",
                                                                file.getAbsolutePath(), e.getMessage() ) );
        }
        FeatureCollection featureCollection;
        try {
            featureCollection = doc.parse();
            datasource.setExtent( featureCollection.getBoundedBy() );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10071",
                                                                file.getAbsolutePath(), e.getMessage() ) );
        }
        featureCollection = transform( featureCollection );
        featureCollections.put( datasource.getName(), featureCollection );
    }

    private File getAbsoluteFilePath( File file ) {
        if ( !file.isAbsolute() ) {
            try {
                URL url = mapModel.getApplicationContainer().resolve( file.getPath() );
                file = new File( url.toExternalForm().substring( 5 ) );
            } catch ( MalformedURLException e ) {
                // should never happen
                LOG.logError( e );
            }
        }
        return file;
    }

    /**
     * reads data from a shape file restricted by the current bounding box of the map model into the internal feature
     * collection. This method will be called if file type is shape file and data source is lazy loading.
     */
    private void loadShapeFile() {
        fireStartLoadingEvent();
        Envelope currentEnv = null;
        if ( this.isLazyLoading ) {
            currentEnv = mapModel.getEnvelope();
        } else {
            currentEnv = mapModel.getMaxExtent();
        }
        // transform current envelope into native CRS of the data source if required
        if ( !mapModel.getCoordinateSystem().equals( datasource.getNativeCoordinateSystem() ) ) {
            GeoTransformer gt = new GeoTransformer( datasource.getNativeCoordinateSystem() );
            try {
                currentEnv = gt.transform( currentEnv, mapModel.getCoordinateSystem().getPrefixedName(), true );
            } catch ( Exception e ) {
                LOG.logError( e );
                throw new DataAccessException( e.getMessage() );
            }
        }

        File file = ( (FileDatasource) datasource ).getFile();
        file = getAbsoluteFilePath( file );
        int p = file.getAbsolutePath().lastIndexOf( '.' );
        String fileName = file.getAbsolutePath().substring( 0, p );
        ShapeFile sf = null;
        try {
            sf = new ShapeFile( fileName );
        } catch ( Exception e ) {
            fireLoadingExceptionEvent();
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10009",
                                                                file.getAbsolutePath(), e.getMessage() ) );
        }

        int[] ids;
        try {
            ids = sf.getGeoNumbersByRect( currentEnv );
        } catch ( IOException e ) {
            fireLoadingExceptionEvent();
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10011",
                                                                file.getAbsolutePath(), e.getMessage() ) );
        }
        if ( ids == null ) {
            ids = new int[0];
        }
        String id = "UUID_" + UUID.randomUUID().toString();
        FeatureCollection featureCollection = FeatureFactory.createFeatureCollection( id, ids.length );
        for ( int i = 0; i < ids.length; i++ ) {
            Feature feat;
            try {
                feat = sf.getFeatureByRecNo( ids[i] );
            } catch ( Exception e ) {
                fireLoadingExceptionEvent();
                LOG.logError( e.getMessage(), e );
                throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10010",
                                                                    file.getAbsolutePath(), e.getMessage() ) );
            }
            GeometryImpl gm = (GeometryImpl) feat.getDefaultGeometryPropertyValue();
            gm.setCoordinateSystem( datasource.getNativeCoordinateSystem() );
            featureCollection.add( feat );
        }

        sf.close();
        featureCollection = transform( featureCollection );
        featureCollections.put( datasource.getName(), featureCollection );
        fireLoadingFinishedEvent();
    }

    @Override
    public synchronized FeatureCollection getFeatureCollection() {
        if ( this.isLazyLoading ) {
            synchronized ( datasource ) {
                double min = datasource.getMinScaleDenominator();
                double max = datasource.getMaxScaleDenominator();
                if ( mapModel.getScaleDenominator() >= min && mapModel.getScaleDenominator() < max ) {
                    Envelope currentEnv = mapModel.getEnvelope();
                    if ( lastEnv == null || ( !currentEnv.equals( lastEnv ) && !lastEnv.contains( currentEnv ) ) ) {
                        String nm = ( (FileDatasource) datasource ).getFile().getName().toLowerCase();
                        if ( nm.endsWith( ".shp" ) ) {
                            loadShapeFile();
                        } else {
                            DialogFactory.openErrorDialog(
                                                           layer.getOwner().getApplicationContainer().getViewPlatform(),
                                                           null, "lazy loading not supported for file: " + nm,
                                                           "error lazy loading" );
                        }
                        lastEnv = mapModel.getEnvelope();
                    }
                    // perform all inserts, updates, deletes that has been performed on this
                    // data source adapter on the feature collection read from the adapted datasource
                    updateFeatureCollection();                    
                }
            }
        } else if ( featureCollections.get( datasource.getName() ) == null ) {
            loadData();
        }
        return featureCollections.get( datasource.getName() );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.IMapModelAdapter#refresh()
     */
    public void refresh() {
        if ( featureCollections.get( datasource.getName() ) == null && !this.isLazyLoading ) {
            // if feature collection has already been loaded for a not lazy loading datasource
            // it don't have to loaded again.
            loadData();
            layer.setDataRefreshed( this );
        }
        loadSchema();
    }

    @Override
    public void commitChanges()
                            throws IOException {
        LOG.logDebug( "commiting changes to: " + datasource.getName() );
        if ( this.isLazyLoading ) {
            LOG.logInfo( "commit changes on a lazy loading file based layer is not supported yet" );
        } else {
            File file = ( (FileDatasource) datasource ).getFile();
            FeatureCollection fc = getFeatureCollection();
            try {
                file.delete();
                String outName = getAbsoluteFilePath( file ).getAbsolutePath();
                if ( outName.toLowerCase().endsWith( ".gml" ) || outName.toLowerCase().endsWith( ".xml" ) ) {
                    FileOutputStream fos = new FileOutputStream( new File( outName ) );
                    GMLFeatureAdapter ada = new GMLFeatureAdapter();
                    GMLFeatureCollectionDocument doc = ada.export( fc );
                    doc.write( fos );
                    fos.close();
                } else {
                    outName = outName.substring( 0, outName.lastIndexOf( '.' ) );
                    org.deegree.io.shpapi.shape_new.ShapeFile sf = new org.deegree.io.shpapi.shape_new.ShapeFile( fc,
                                                                                                                  outName );
                    ShapeFileWriter writer = new ShapeFileWriter( sf );
                    writer.write();
                }
            } catch ( Exception e ) {
                LOG.logError( "commiting data to datasource: " + datasource.getName()
                              + " failed; restoring backend from backup", e );
                // FileUtils.copy( backup, file );
            }

        }
    }

}
