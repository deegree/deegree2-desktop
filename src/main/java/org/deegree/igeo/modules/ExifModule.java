//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2012 by:
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

 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: info@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/

package org.deegree.igeo.modules;

import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;
import static java.util.prefs.Preferences.userNodeForPackage;
import static org.deegree.crs.coordinatesystems.GeographicCRS.WGS84;
import static org.deegree.framework.log.LoggerFactory.getLogger;
import static org.deegree.framework.util.CollectionUtils.collectionToString;
import static org.deegree.igeo.Version.getVersionNumber;
import static org.deegree.igeo.i18n.Messages.get;
import static org.deegree.model.spatialschema.GeometryFactory.createPoint;
import static org.deegree.ogcbase.CommonNamespaces.getNamespaceContext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.TiffImageMetadata.GPSInfo;
import org.apache.sanselan.formats.tiff.write.TiffImageWriterLossless;
import org.apache.sanselan.formats.tiff.write.TiffOutputSet;
import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.util.Pair;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.igeo.commands.UnselectFeaturesCommand;
import org.deegree.igeo.config.LayerType.MetadataURL;
import org.deegree.igeo.dataadapter.DataAccessFactory;
import org.deegree.igeo.desktop.IGeoDesktop;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.ActionDescription.ACTIONTYPE;
import org.deegree.igeo.modules.DefaultMapModule.SelectedFeaturesVisitor;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.model.Identifier;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CRSTransformationException;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.Point;

/**
 * Module for to read data from exif header of georeferenced jpeg images
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * @param <T>
 */
public class ExifModule<T> extends DefaultModule<T> {

    private static final ILogger LOG = getLogger( ExifModule.class );

    private static final NamespaceContext nsContext = getNamespaceContext();

    private static URI APPNS;

    private static final CoordinateSystem unrealWGS84 = CRSFactory.create( WGS84 );

    private static final QualifiedName geometry, imageLocation;

    private static final FeatureType featureType;

    static {
        APPNS = URI.create( "http://www.deegree.org/app" );
        nsContext.addNamespace( "app", APPNS );

        geometry = new QualifiedName( "app", "geometry", APPNS );
        imageLocation = new QualifiedName( "app", "imageLocation", APPNS );
        PropertyType[] pts = {
                              FeatureFactory.createGeometryPropertyType( geometry,
                                                                         new QualifiedName( "gml", "PointPropertyType",
                                                                                            APPNS ), 1, 1 ),
                              FeatureFactory.createSimplePropertyType( imageLocation, Types.VARCHAR, 1, 1 ) };

        featureType = FeatureFactory.createFeatureType( new QualifiedName( "app", "geoimage", APPNS ), false, pts );
    }

    static {
        ActionDescription ad1 = new ActionDescription(
                                                       "addImages",
                                                       "opens a dialog for adding coordinates read from exif header of an image as new feature",
                                                       null, "add feature from exif header", ACTIONTYPE.PushButton,
                                                       null, null );
        ActionDescription ad2 = new ActionDescription( "linkImage",
                                                       "links an image to an already existing point feature", null,
                                                       "link image to feature", ACTIONTYPE.PushButton, null, null );
        moduleCapabilities = new ModuleCapabilities( ad1, ad2 );
    }

    /**
     * 
     */
    public void addImages() {
        if ( appContainer.getViewPlatform().equalsIgnoreCase( "application" ) ) {
            Preferences prefs = userNodeForPackage( ExifModule.class );
            String last = prefs.get( "lastExifDir" + getVersionNumber(), null );
            JFileChooser chooser = new JFileChooser( last );
            chooser.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );
            chooser.setMultiSelectionEnabled( true );
            if ( chooser.showOpenDialog( ( (IGeoDesktop) appContainer ).getMainWndow() ) == JFileChooser.APPROVE_OPTION ) {
                String name = DialogFactory.openInputDialog( appContainer.getViewPlatform(), getViewForm(),
                                                             get( "$MD10558" ), get( "$MD10559" ) );
                if ( name == null ) {
                    return;
                }

                String mmId = getInitParameter( "assignedMapModel" );
                MapModel mm = appContainer.getMapModel( new Identifier( mmId ) );

                CoordinateSystem crs = mm.getCoordinateSystem();
                GeoTransformer transformer = null;
                if ( !mm.getCoordinateSystem().getCRS().equals( WGS84 ) ) {
                    transformer = new GeoTransformer( crs );
                }

                FeatureCollection col = FeatureFactory.createFeatureCollection( randomUUID().toString(), 0 );
                // col.setFeatureType( featureType );

                LinkedList<String> errors = new LinkedList<String>();
                for ( File sel : chooser.getSelectedFiles() ) {
                    if ( sel.isDirectory() ) {
                        File[] fs = sel.listFiles();
                        if ( fs != null ) {
                            for ( File f : fs ) {
                                handleResult( obtainFeatureFromImage( f, transformer ), errors, col );
                            }
                        }
                    } else {
                        handleResult( obtainFeatureFromImage( sel, transformer ), errors, col );
                    }
                }

                if ( !errors.isEmpty() ) {
                    DialogFactory.openWarningDialog( appContainer.getViewPlatform(),
                                                     ( (IGeoDesktop) appContainer ).getMainWndow(),
                                                     get( "$MD10895", collectionToString( errors, "\n" ) ),
                                                     get( "$DI10036" ) );
                }

                Datasource ds = DataAccessFactory.createDatasource( UUID.randomUUID().toString(), col );

                Layer layer = new Layer( mm, new Identifier( name ), name, name, singletonList( ds ),
                                         Collections.<MetadataURL> emptyList() );
                try {
                    appContainer.getCommandProcessor().executeSychronously( new UnselectFeaturesCommand( mm, false ),
                                                                            true );
                } catch ( Exception e ) {
                    LOG.logError( e.getMessage(), e );
                    DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11238" ),
                                                   Messages.get( "$MD11239" ), e );
                    return;
                }

                List<Layer> list = mm.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
                Layer selLayer = list.isEmpty() ? mm.getLayerGroups().get( 0 ).getLayers().get( 0 ) : list.get( 0 );
                mm.insert( layer, selLayer.getParent(), selLayer, false );

                layer.fireRepaintEvent();

                prefs.put( "lastExifDir" + getVersionNumber(), chooser.getSelectedFile().getParent() );
            }
        }
    }

    private void handleResult( Pair<Feature, String> pair, LinkedList<String> errors, FeatureCollection fc ) {
        Feature feat = pair.first;
        if ( feat != null ) {
            try {
                fc.add( feat );
            } catch ( Exception e ) {
                LOG.logError( "Unknown error", e );
            }
        } else {
            errors.add( pair.second );
        }
    }

    private Pair<Feature, String> obtainFeatureFromImage( File file, GeoTransformer transformer ) {
        try {
            IImageMetadata metadata = Sanselan.getMetadata( file );
            GPSInfo gps = null;
            if ( metadata instanceof JpegImageMetadata ) {
                gps = ( (JpegImageMetadata) metadata ).getExif().getGPS();
            } else if ( metadata instanceof TiffImageMetadata ) {
                gps = ( (TiffImageMetadata) metadata ).getGPS();
            } else {
                DialogFactory.openErrorDialog( appContainer.getViewPlatform(),
                                               ( (IGeoDesktop) appContainer ).getMainWndow(), get( "$MD10898" ),
                                               get( "$DI10017" ) );
                return new Pair<Feature, String>();
            }

            Point point = createPoint( gps.getLongitudeAsDegreesEast(), gps.getLatitudeAsDegreesNorth(), unrealWGS84 );
            point = (Point) transformer.transform( point );

            FeatureProperty geom = FeatureFactory.createFeatureProperty( geometry, point );
            FeatureProperty loc = FeatureFactory.createFeatureProperty( imageLocation, file.getAbsolutePath() );
            return new Pair<Feature, String>( FeatureFactory.createFeature( randomUUID().toString(), featureType,
                                                                            new FeatureProperty[] { loc, geom } ), null );

        } catch ( IOException e ) {
            LOG.logError( "While loading a file: ", e );
            return new Pair<Feature, String>( null, e.getLocalizedMessage() ); // do not include file (it's usually
            // included in the message)
        } catch ( ImageReadException e ) {
            LOG.logError( "While loading a file: ", e );
            return new Pair<Feature, String>( null, file.getName() + ": " + e.getLocalizedMessage() );
        } catch ( IllegalArgumentException e ) {
            LOG.logError( "Unknown error", e );
            return new Pair<Feature, String>( null, file.getName() + ": " + e.getLocalizedMessage() );
        } catch ( CRSTransformationException e ) {
            LOG.logError( "Unknown error", e );
            return new Pair<Feature, String>( null, file.getName() + ": " + e.getLocalizedMessage() );
        }
    }

    /**
     * 
     */
    public void linkImage() {
        if ( appContainer.getViewPlatform().equalsIgnoreCase( "application" ) ) {
            String mmId = getInitParameter( "assignedMapModel" );
            MapModel mm = appContainer.getMapModel( new Identifier( mmId ) );
            SelectedFeaturesVisitor visitor = new SelectedFeaturesVisitor( 2 );
            try {
                mm.walkLayerTree( visitor );
                if ( visitor.col.size() == 1 ) {
                    Geometry g = visitor.col.getFeature( 0 ).getDefaultGeometryPropertyValue();
                    if ( !( g instanceof Point ) ) {
                        return;
                    }
                    Point pt = (Point) g;
                    if ( !pt.getCoordinateSystem().equals( unrealWGS84 ) ) {
                        GeoTransformer trans = new GeoTransformer( unrealWGS84 );
                        pt = (Point) trans.transform( pt );
                    }

                    Preferences prefs = userNodeForPackage( ExifModule.class );
                    String last = prefs.get( "lastExifSaveDir" + getVersionNumber(), null );
                    JFileChooser chooser = new JFileChooser( last );
                    if ( chooser.showOpenDialog( ( (IGeoDesktop) appContainer ).getMainWndow() ) == JFileChooser.APPROVE_OPTION ) {
                        File file = chooser.getSelectedFile();

                        IImageMetadata metadata = Sanselan.getMetadata( file );
                        TiffOutputSet outputSet = null;
                        if ( metadata instanceof JpegImageMetadata ) {
                            TiffImageMetadata exif = ( (JpegImageMetadata) metadata ).getExif();
                            outputSet = exif.getOutputSet();
                        } else if ( metadata instanceof TiffImageMetadata ) {
                            outputSet = ( (TiffImageMetadata) metadata ).getOutputSet();
                        }
                        if ( metadata == null ) {
                            outputSet = new TiffOutputSet();
                        }

                        if ( outputSet == null ) {
                            DialogFactory.openErrorDialog( appContainer.getViewPlatform(),
                                                           ( (IGeoDesktop) appContainer ).getMainWndow(),
                                                           get( "$MD10898" ), get( "$DI10017" ) );
                            return;
                        }

                        outputSet.setGPSInDegrees( pt.getX(), pt.getY() );

                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        FileInputStream in = new FileInputStream( file );

                        byte[] buf = new byte[65536];
                        int read;
                        while ( ( read = in.read( buf ) ) != -1 ) {
                            out.write( buf, 0, read );
                        }

                        in.close();
                        out.close();

                        byte[] bs = out.toByteArray();
                        FileOutputStream os = new FileOutputStream( file );
                        if ( metadata == null || metadata instanceof JpegImageMetadata ) {
                            new ExifRewriter().updateExifMetadataLossless( bs, os, outputSet );
                        } else {
                            new TiffImageWriterLossless( bs ).write( os, outputSet );
                        }
                        prefs.put( "lastExifSaveDir" + getVersionNumber(), file.getParent() );

                        DialogFactory.openInformationDialog( appContainer.getViewPlatform(),
                                                             ( (IGeoDesktop) appContainer ).getMainWndow(),
                                                             get( "$MD10900" ), get( "$DI10018" ) );

                    }
                } else {
                    DialogFactory.openErrorDialog( appContainer.getViewPlatform(),
                                                   ( (IGeoDesktop) appContainer ).getMainWndow(), get( "$MD10897" ),
                                                   get( "$DI10017" ) );
                }
            } catch ( IOException e ) {
                DialogFactory.openErrorDialog( appContainer.getViewPlatform(),
                                               ( (IGeoDesktop) appContainer ).getMainWndow(), get( "$MD10899", e ),
                                               get( "$DI10017" ) );
            } catch ( Exception e ) {
                LOG.logError( "Unknown error", e );
            }
        }
    }

}
