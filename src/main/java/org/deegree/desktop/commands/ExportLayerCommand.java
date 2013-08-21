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

package org.deegree.desktop.commands;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.dataadapter.DataAccessAdapter;
import org.deegree.desktop.dataadapter.FeatureAdapter;
import org.deegree.desktop.dataadapter.GridCoverageAdapter;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.io.FileSystemAccess;
import org.deegree.desktop.io.FileSystemAccessFactory;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;
import org.deegree.framework.util.ImageUtils;
import org.deegree.io.shpapi.shape_new.ShapeFile;
import org.deegree.io.shpapi.shape_new.ShapeFileWriter;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.Command;
import org.deegree.model.coverage.grid.AbstractGridCoverage;
import org.deegree.model.coverage.grid.WorldFile;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureException;
import org.deegree.model.feature.GMLFeatureAdapter;
import org.deegree.model.spatialschema.Envelope;

/**
 * {@link Command} implementation for exporting a layer into a file. Supported formats -depending on data type - are:
 * <ul>
 * <li>shape
 * <li>gml
 * <li>gif
 * <li>png
 * <li>tif
 * <li>jpeg
 * <li>bmp
 * </ul>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class ExportLayerCommand extends AbstractCommand {

    public static final QualifiedName name = new QualifiedName( "Export Layer" );

    private Layer layer;

    private File file;

    /**
     * @param layer
     * @param file
     */
    public ExportLayerCommand( Layer layer, File file ) {
        this.layer = layer;
        this.file = file;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {
        String baseName = null;
        if ( file.isAbsolute() ) {
            baseName = file.getAbsolutePath();
        } else {
            baseName = file.getPath();
        }
        int idx = baseName.lastIndexOf( '.' );
        if ( idx < 1 ) {
            file = new File( baseName + ".gml" );
            idx = baseName.lastIndexOf( '.' );
        }
        baseName = baseName.substring( 0, idx );

        List<DataAccessAdapter> adapters = layer.getDataAccess();
        String postFix = "";
        int cnt = 1;
        for ( DataAccessAdapter adapter : adapters ) {
            if ( adapter instanceof FeatureAdapter ) {
                String tmp = file.getAbsolutePath().toLowerCase();
                FeatureCollection fc = ( (FeatureAdapter) adapter ).getFeatureCollection();
                if ( tmp.endsWith( ".shp" ) ) {
                    ShapeFile sf = new ShapeFile( fc, baseName + postFix );
                    ShapeFileWriter sfw = new ShapeFileWriter( sf );
                    sfw.write();
                } else if ( tmp.endsWith( ".xml" ) || tmp.endsWith( ".gml" ) ) {
                    exportGML( baseName, postFix, fc );
                } else {
                    throw new Exception( Messages.get( "$DG10086", file.getName() ) );
                }
            } else {
                // must be grid
                exportGrid( (GridCoverageAdapter) adapter, baseName + postFix );
            }
            postFix = "_" + cnt++;
        }
    }

    private void exportGML( String baseName, String postFix, FeatureCollection fc )
                            throws IOException, FeatureException {
        GMLFeatureAdapter ada = new GMLFeatureAdapter();

        FileSystemAccessFactory fsaf = FileSystemAccessFactory.getInstance( layer.getOwner().getApplicationContainer() );
        FileSystemAccess fsa = null;
        try {
            fsa = fsaf.getFileSystemAccess( FILECHOOSERTYPE.geoDataFile );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream( 100000 );
        ada.export( fc, bos );
        ByteArrayInputStream bis = new ByteArrayInputStream( bos.toByteArray() );
        fsa.writeFile( new File( baseName + postFix + ".gml" ), bis );
    }

    private void exportGrid( GridCoverageAdapter adapter, String baseName )
                            throws IOException {

        BufferedImage bi = ( (AbstractGridCoverage) adapter.getCoverage() ).getAsImage( -1, -1 );
        double w = bi.getWidth();
        double h = bi.getHeight();
        Envelope env = adapter.getDatasource().getExtent();

        FileSystemAccessFactory fsaf = FileSystemAccessFactory.getInstance( layer.getOwner().getApplicationContainer() );
        FileSystemAccess fsa = null;
        try {
            fsa = fsaf.getFileSystemAccess( FILECHOOSERTYPE.geoDataFile );
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        WorldFile wf = new WorldFile( env.getWidth() / w, env.getHeight() / h, 0, 0, env );
        ByteArrayOutputStream bos = new ByteArrayOutputStream( 100000 );
        WorldFile.writeWorldFile( bos, wf );
        ByteArrayInputStream bis = new ByteArrayInputStream( bos.toByteArray() );
        fsa.writeFile( new File( baseName + ".wld" ), bis );

        bos = new ByteArrayOutputStream( 100000 );
        int idx = file.getName().lastIndexOf( '.' );
        ImageUtils.saveImage( bi, bos, file.getName().substring( idx + 1 ), 0.98f );
        bis = new ByteArrayInputStream( bos.toByteArray() );
        fsa.writeFile( file, bis );
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
        return file;
    }

}
