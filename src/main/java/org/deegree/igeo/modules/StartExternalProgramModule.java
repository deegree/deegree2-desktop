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

package org.deegree.igeo.modules;

import static java.net.URLDecoder.decode;
import static java.util.Arrays.asList;
import static java.util.prefs.Preferences.userNodeForPackage;
import static org.deegree.framework.log.LoggerFactory.getLogger;
import static org.deegree.igeo.Version.getVersionNumber;
import static org.deegree.igeo.i18n.Messages.get;
import static org.deegree.igeo.views.DialogFactory.openErrorDialog;
import static org.deegree.igeo.views.swing.util.IGeoFileFilter.GPX;

import java.awt.Container;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.prefs.Preferences;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.igeo.commands.model.AddFileLayerCommand;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.ActionDescription.ACTIONTYPE;
import org.deegree.igeo.views.swing.util.GenericFileChooser;
import org.deegree.kernel.Command;
import org.deegree.kernel.CommandProcessor;
import org.deegree.model.Identifier;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureProperty;

/**
 * Module for invoking/starting external applications
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * @param <T>
 */
public class StartExternalProgramModule<T> extends DefaultModule<T> {

    static final ILogger LOG = getLogger( StartExternalProgramModule.class );

    static {
        ActionDescription ad = new ActionDescription(
                                                      "startProgram",
                                                      "starts an external program assigned to value type read from a defined property",
                                                      null, "starts an external program", ACTIONTYPE.PushButton, null,
                                                      null );
        moduleCapabilities = new ModuleCapabilities( ad );
    }

    /**
     * 
     */
    public void startProgram() {
        try {
            String property = getInitParameter( "property" );

            MapModel mapModel = appContainer.getMapModel( null );
            List<Layer> layers = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
            Container window = appContainer.getMainWndow();
            if ( layers.isEmpty() ) {
                openErrorDialog( appContainer.getViewPlatform(), window, get( "$MD10890" ), get( "$DI10017" ) );
                return;
            }
            if ( layers.size() > 1 ) {
                openErrorDialog( appContainer.getViewPlatform(), window, get( "$MD10891" ), get( "$DI10017" ) );
                return;
            }
            Layer l = layers.get( 0 );
            FeatureCollection selectedFeatures = l.getSelectedFeatures();
            if ( selectedFeatures.size() == 0 ) {
                openErrorDialog( appContainer.getViewPlatform(), window, get( "$MD10892" ), get( "$DI10017" ) );
                return;
            }
            if ( selectedFeatures.size() > 1 ) {
                openErrorDialog( appContainer.getViewPlatform(), window, get( "$MD10893" ), get( "$DI10017" ) );
                return;
            }

            Feature f = selectedFeatures.getFeature( 0 );
            FeatureProperty[] properties = f.getProperties( new QualifiedName( property,
                                                                               f.getFeatureType().getNameSpace() ) );
            if ( property != null && properties == null || properties.length == 0 ) {
                openErrorDialog( appContainer.getViewPlatform(), window, get( "$MD10889" ), get( "$DI10017" ) );
                return;
            }
            FeatureProperty p = properties[0];

            String name = getInitParameter( "programName" );
            if ( name == null ) {
                openErrorDialog( appContainer.getViewPlatform(), window, get( "$MD10894" ), get( "$DI10017" ) );
                return;
            }
            String params = getInitParameter( "programParameters" );
            LinkedList<String> parameters = params == null ? new LinkedList<String>()
                                                          : new LinkedList<String>( asList( params.split( "," ) ) );
            ListIterator<String> iter = parameters.listIterator();
            while ( iter.hasNext() ) {
                String item = decode( iter.next().trim(), "UTF-8" );
                item = item.replace( "$PROPERTY", p.getValue().toString() );
                iter.set( item );
            }
            parameters.addFirst( decode( name.trim(), "UTF-8" ) );
            LOG.logDebug( "invoke: ", parameters );
            ProcessBuilder pb = new ProcessBuilder( parameters );
            logOutput( pb.start() );
        } catch ( IOException e ) {
            LOG.logError( "Unknown error", e );
        }
    }

    /**
     * 
     */
    public void importGDALGPXShapes() {
        try {
            Container window = appContainer.getMainWndow();

            Preferences prefs = userNodeForPackage( StartExternalProgramModule.class );
            File file = GenericFileChooser.showOpenDialog( GenericFileChooser.FILECHOOSERTYPE.geoDataFile,
                                                           appContainer, window, prefs, "lastGpxLoadDir"
                                                                                        + getVersionNumber(), GPX );
            if ( file != null ) {
                File outDir = new File( System.getProperty( "java.io.tmpdir" ), file.getName() + "_converted" );
                String[] parameters = { "gpx2shp.sh", "-i=" + file.toString(), "-o=" + outDir.getName() };

                ProcessBuilder pb = new ProcessBuilder( parameters );
                Process process = pb.start();
                logOutput( process );
                int res = process.waitFor();
                if ( res == 0 ) {
                    String mmId = getInitParameter( "assignedMapModel" );
                    MapModel mm = appContainer.getMapModel( new Identifier( mmId ) );
                    Command cmd1 = new AddFileLayerCommand( mm, new File( outDir, "routes.shp" ), null, null, null,
                                                            "EPSG:4326" );
                    Command cmd2 = new AddFileLayerCommand( mm, new File( outDir, "tracks.shp" ), null, null, null,
                                                            "EPSG:4326" );
                    Command cmd3 = new AddFileLayerCommand( mm, new File( outDir, "waypoints.shp" ), null, null, null,
                                                            "EPSG:4326" );
                    CommandProcessor proc = appContainer.getCommandProcessor();
                    proc.executeSychronously( cmd1, true );
                    proc.executeSychronously( cmd2, true );
                    proc.executeSychronously( cmd3, true );
                } else {
                    openErrorDialog( appContainer.getViewPlatform(), window, get( "$MD10901", res ), get( "$DI10017" ) );
                }
            }
        } catch ( Exception e ) {
            LOG.logError( "Unknown error", e );
        }
    }

    private static void logOutput( final Process process ) {
        if ( LOG.isDebug() ) {
            new Thread() {
                @Override
                public void run() {
                    BufferedReader stdin = new BufferedReader( new InputStreamReader( process.getInputStream() ) );
                    BufferedReader stderr = new BufferedReader( new InputStreamReader( process.getErrorStream() ) );
                    try {
                        while ( stdin.ready() || stderr.ready() ) {
                            if ( stdin.ready() ) {
                                LOG.logDebug( stdin.readLine() );
                            }
                            if ( stderr.ready() ) {
                                LOG.logDebug( stderr.readLine() );
                            }
                        }
                    } catch ( IOException e ) {
                        LOG.logError( "Unknown error", e );
                    }
                }
            }.start();
        }
    }

}
