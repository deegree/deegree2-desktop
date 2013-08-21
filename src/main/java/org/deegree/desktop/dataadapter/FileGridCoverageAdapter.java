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

package org.deegree.desktop.dataadapter;

import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.TiledImage;

import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.io.FileSystemAccess;
import org.deegree.desktop.io.FileSystemAccessFactory;
import org.deegree.desktop.io.RemoteFSAccess;
import org.deegree.desktop.mapmodel.Datasource;
import org.deegree.desktop.mapmodel.FileDatasource;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.desktop.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.HttpUtils;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.graphics.transformation.WorldToScreenTransform;
import org.deegree.desktop.config.TargetDeviceType;
import org.deegree.model.coverage.grid.GridCoverage;
import org.deegree.model.coverage.grid.ImageGridCoverage;
import org.deegree.model.coverage.grid.WorldFile;
import org.deegree.model.coverage.grid.WorldFile.TYPE;
import org.deegree.model.crs.CRSTransformationException;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;

import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.MemoryCacheSeekableStream;
import com.sun.media.jai.codec.SeekableStream;

/**
 * concrete {@link GridCoverageAdapter} for read grid coverages from files
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class FileGridCoverageAdapter extends GridCoverageAdapter {

    private static ILogger LOG = LoggerFactory.getLogger( FileGridCoverageAdapter.class );

    private boolean isLazyLoading;

    private Envelope currentEnv;

    private BufferedImage image;

    private TiledImage tiledImage;

    private int oldWith;

    private int oldHeight;

    private Envelope oldEnv;

    private Envelope bbox;

    /**
     * 
     * @param module
     * @param datasource
     * @param layer
     * @param mapModel
     * @param isLazyLoading
     */
    FileGridCoverageAdapter( Datasource datasource, Layer layer, MapModel mapModel, boolean isLazyLoading ) {
        super( datasource, layer, mapModel );
        this.isLazyLoading = isLazyLoading;
        oldEnv = GeometryFactory.createEnvelope( -9E9, -9E9, 9E9, 9E9, null );
        File file = ( (FileDatasource) datasource ).getFile();
        file = getAbsoluteFilePath( file );

        URL url = null;
        SeekableStream fss = null;
        FileSystemAccessFactory fsaf = FileSystemAccessFactory.getInstance( mapModel.getApplicationContainer() );

        try {
            FileSystemAccess fsa = fsaf.getFileSystemAccess( FILECHOOSERTYPE.geoDataFile );
            url = fsa.getFileURL( file.getAbsolutePath() );
            fss = new MemoryCacheSeekableStream( url.openStream() );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10070",
                                                                file.getAbsolutePath(), e.getMessage() ) );
        }

        BufferedImage bi;
        try {
            RenderedOp rop = JAI.create( "stream", fss );
            int iw = ( (Integer) rop.getProperty( "image_width" ) ).intValue();
            int ih = ( (Integer) rop.getProperty( "image_height" ) ).intValue();
            LOG.logInfo( "size of image: " + url + " -> " + iw + "x" + ih );
            if ( iw * ih > 50000000 ) {
                boolean ok = DialogFactory.openConfirmDialogYESNO( mapModel.getApplicationContainer().getViewPlatform(),
                                                                   null, Messages.get( "$MD11585" ),
                                                                   Messages.get( "$MD11586" ) );
                if ( !ok ) {
                    throw new DataAccessException( "Will not load image: " + url + " because it is too large" );
                }
            }
            bi = rop.getAsBufferedImage();
            fss.close();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( e.getMessage() );
        }

        String fname = findWorldFileName( fsaf, file.getAbsolutePath() );
        WorldFile worldFile = null;
        try {
            FileSystemAccess fsa = fsaf.getFileSystemAccess( FILECHOOSERTYPE.geoDataFile );
            url = fsa.getFileURL( fname );
            LOG.logDebug( "world file URL: ", url );
            worldFile = WorldFile.readWorldFile( url.openStream(), TYPE.CENTER, bi.getWidth(), bi.getHeight() );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10070",
                                                                file.getAbsolutePath(), e.getMessage() ) );
        }

        Envelope env = worldFile.getEnvelope();
        LOG.logDebug( "envelope read from world file: ", env );
        env = GeometryFactory.createEnvelope( env.getMin(), env.getMax(), datasource.getNativeCoordinateSystem() );
        datasource.setExtent( env );
        if ( !datasource.getNativeCoordinateSystem().equals( mapModel.getCoordinateSystem() ) ) {
            GeoTransformer transformer = new GeoTransformer( mapModel.getCoordinateSystem() );
            try {
                bbox = transformer.transform( env, datasource.getNativeCoordinateSystem(), true );
                // BufferedImage bi = ( (PlanarImage) ri ).getAsBufferedImage();
                bi = transformer.transform( bi, env, bbox, mapModel.getTargetDevice().getPixelWidth(),
                                            mapModel.getTargetDevice().getPixelHeight(), 16, 3, null );
                currentEnv = bbox;
            } catch ( CRSTransformationException e ) {
                LOG.logError( e.getMessage(), e );
                throw new DataAccessException( e.getMessage() );
            }
        } else {
            bbox = env;
            currentEnv = bbox;
        }

        tiledImage = new TiledImage( bi, 500, 500 );

        refresh();
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
     * @param absolutePath
     * @return
     */
    private static String findWorldFileName( FileSystemAccessFactory fsaf, String fname ) {
        int pos = fname.lastIndexOf( "." );
        URL url = null;
        FileSystemAccess fsa;
        try {
            fsa = fsaf.getFileSystemAccess( FILECHOOSERTYPE.geoDataFile );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10070", fname, e.getMessage() ) );
        }

        if ( fsa instanceof RemoteFSAccess ) {
            // if a raster file is access via RemoteFSAccess its world file
            // must have extension .wld
            return fname.substring( 0, pos ) + ".wld";
        }
        String[] ext = new String[] { ".tfw", ".wld", ".jgw", ".gfw", ".gifw", ".pgw", ".pngw" };
        for ( String extension : ext ) {
            String tmp = fname.substring( 0, pos ) + extension;
            try {
                url = fsa.getFileURL( tmp );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10070", fname,
                                                                    e.getMessage() ) );
            }
            try {
                HttpUtils.validateURL( url.toExternalForm() );
                // worldfile with current extension exist
                return tmp;
            } catch ( Exception e ) {
                // don't do nothing
                LOG.logWarning( "", e );
            }
        }
        throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10102", fname ) );
    }

    /**
     * 
     * @return adapted coverage
     */
    @Override
    public GridCoverage getCoverage() {

        TargetDeviceType td = mapModel.getTargetDevice();
        if ( isLazyLoading ) {
            if ( !mapModel.getEnvelope().equals( oldEnv ) || td.getPixelWidth() != oldWith
                 || td.getPixelHeight() != oldHeight ) {
                double min = datasource.getMinScaleDenominator();
                double max = datasource.getMaxScaleDenominator();
                if ( mapModel.getScaleDenominator() >= min && mapModel.getScaleDenominator() < max ) {
                    // raster data just has to loaded if bounding box of the map model or size of
                    // target device has been changed
                    loadRasterSubset();
                }
            }
        } else if ( coverage == null ) {
            loadFullRaster();
        }

        if ( isLazyLoading
             && ( !mapModel.getEnvelope().equals( oldEnv ) || td.getPixelWidth() != oldWith || td.getPixelHeight() != oldHeight ) ) {
            double min = datasource.getMinScaleDenominator();
            double max = datasource.getMaxScaleDenominator();
            // raster data may have to be rescaled if bounding box of the map model or size of
            // target device has been changed
            if ( ( mapModel.getScaleDenominator() >= min && mapModel.getScaleDenominator() < max )
                 && ( td.getPixelWidth() != image.getWidth() || td.getPixelHeight() != image.getHeight() ) ) {
                // pixel size of target device is different from image size, the image must
                // be rescaled to avoid strange optical effects and increasing rendering performance
                float scaleX = ( (float) td.getPixelWidth() ) / (float) image.getWidth();
                float scaleY = ( (float) td.getPixelHeight() ) / (float) image.getHeight();
                image = scale( image, scaleX, scaleY );
            }
        }

        // store current relevant map model parameters
        oldHeight = td.getPixelHeight();
        oldWith = td.getPixelWidth();
        oldEnv = mapModel.getEnvelope();
        this.coverage = new ImageGridCoverage( null, currentEnv, image );
        return this.coverage;
    }

    /**
     * 
     * @param img
     * @param interpolation
     * @param scaleX
     * @param scaleY
     * @return the scaled image
     */
    private static BufferedImage scale( BufferedImage img, float scaleX, float scaleY ) {

        Interpolation interpolation = new InterpolationBilinear();

        LOG.logDebug( "Scale image: by factors: " + scaleX + ' ' + scaleY );
        ParameterBlock pb = new ParameterBlock();
        pb.addSource( img );
        pb.add( scaleX ); // The xScale
        pb.add( scaleY ); // The yScale
        pb.add( 0.0F ); // The x translation
        pb.add( 0.0F ); // The y translation
        pb.add( interpolation ); // The interpolation
        // Create the scale operation
        RenderedOp ro = JAI.create( "scale", pb, null );
        try {
            img = ro.getAsBufferedImage();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return img;
    }

    /**
     * loads entire raster source (image) and sets current envelope for the adapted grid coverage to the envelope read
     * from assigned world file
     * 
     */
    private void loadFullRaster() {
        image = tiledImage.getAsBufferedImage();
    }

    /**
     * loads subset matching the current map models boundingbox
     * 
     */
    private void loadRasterSubset() {
        Envelope modelBbox = mapModel.getEnvelope();
        if ( modelBbox.contains( bbox ) ) {
            // if current bounding box of the map model contains the complete source image
            // no processing is required. So image will be read and current envelope will
            // be set to envelope read from the images world file
            image = tiledImage.getAsBufferedImage();
            currentEnv = bbox;
        } else if ( modelBbox.intersects( bbox ) ) {
            Envelope intersection = modelBbox.createIntersection( bbox );
            Envelope full = bbox;
            SeekableStream fss = null;
            try {
                fss = new FileSeekableStream( ( (FileDatasource) datasource ).getFile().getAbsoluteFile() );
            } catch ( IOException e ) {
                // never happens
                LOG.logWarning( "", e );
            }
            RenderedOp rop = JAI.create( "stream", fss );
            int iw = ( (Integer) rop.getProperty( "image_width" ) ).intValue();
            int ih = ( (Integer) rop.getProperty( "image_height" ) ).intValue();
            GeoTransform gt = new WorldToScreenTransform( full.getMin().getX(), full.getMin().getY(),
                                                          full.getMax().getX(), full.getMax().getY(), 0, 0, iw, ih );
            int x1 = (int) Math.round( gt.getDestX( intersection.getMin().getX() ) );
            int y1 = (int) Math.round( gt.getDestY( intersection.getMax().getY() ) );
            int x2 = (int) Math.round( gt.getDestX( intersection.getMax().getX() ) );
            int y2 = (int) Math.round( gt.getDestY( intersection.getMin().getY() ) );
            image = tiledImage.getSubImage( x1, y1, x2 - x1, y2 - y1 ).getAsBufferedImage();
        } else {
            image = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB );
        }
        this.coverage = new ImageGridCoverage( null, modelBbox, image );
    }

    @Override
    public void refresh() {
        if ( !this.isLazyLoading ) {
            refreshRaster();
        }
    }

    private void refreshRaster() {
        loadFullRaster();
        layer.setDataRefreshed( this );
    }

    @Override
    public void refresh( boolean forceReload ) {
        if ( forceReload ) {
            refreshRaster();
        } else {
            refresh();
        }
    }

    @Override
    public void commitChanges()
                            throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void invalidate() {
        this.tiledImage = null;
        this.image = null;
        super.invalidate();
    }

}
