//$HeadURL: svn+ssh://aschmitz@wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2011 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.igeo.commands;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.i18n.Messages;
import org.deegree.kernel.AbstractCommand;

/**
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: stranger $
 * 
 * @version $Revision: $, $Date: $
 */
public class GeoRefCommand extends AbstractCommand {

    private static final ILogger LOG = LoggerFactory.getLogger( GeoRefCommand.class );

    private final String prefix;

    private final String crs;

    private final File sourceFile;

    private final File file;

    public GeoRefCommand( String prefix, String crs, File sourceFile, File file ) {
        this.prefix = prefix;
        this.crs = crs;
        this.sourceFile = sourceFile;
        this.file = file;
    }

    @Override
    public QualifiedName getName() {
        return new QualifiedName( "georefcommand" );
    }

    @Override
    public void execute()
                            throws Exception {
        // let's hope the gdalwarp/_translate output never changes
        ProcessBuilder pb = new ProcessBuilder();
        pb.command( prefix + "gdalwarp", "-t_srs", crs, sourceFile.toString(), file.toString() + "_tmp" );
        Process p = pb.start();
        Reader in = new InputStreamReader( p.getInputStream() );

        StringBuilder log = new StringBuilder();

        char c;

        while ( ( c = ( (char) in.read() ) ) != '\n' && c != '\uffff' )
            log.append( c );
        log.append( '\n' );
        while ( ( c = ( (char) in.read() ) ) != '\n' && c != '\uffff' )
            log.append( c );
        log.append( '\n' );
        while ( ( c = ( (char) in.read() ) ) != '\n' && c != '\uffff' )
            log.append( c );
        log.append( '\n' );
        int cnt = 0;
        while ( ( c = ( (char) in.read() ) ) != '\uffff' && cnt <= 50 ) {
            log.append( c );
            processMonitor.updateStatus( ++cnt, "1" );
        }
        while ( ( c = ( (char) in.read() ) ) != '\uffff' ) {
            log.append( c );
        }
        in.close();

        if ( p.waitFor() != 0 ) {
            throw new Exception( Messages.get( "$DI10091", log.toString() ) );
        }

        pb = new ProcessBuilder();
        if ( file.getName().toLowerCase().endsWith( ".png" ) ) {
            pb.command( prefix + "gdal_translate", "-co", "WORLDFILE=YES", "-of", "PNG", file.toString() + "_tmp",
                        file.toString() );
        } else {
            pb.command( prefix + "gdal_translate", "-co", "TFW=YES", file.toString() + "_tmp", file.toString() );
        }
        p = pb.start();
        in = new InputStreamReader( p.getInputStream() );
        while ( ( c = ( (char) in.read() ) ) != '\n' && c != '\uffff' )
            log.append( c );
        log.append( '\n' );
        while ( ( c = ( (char) in.read() ) ) != '\uffff' && cnt <= 100 ) {
            log.append( c );
            processMonitor.updateStatus( ++cnt, "2" );
        }
        while ( ( c = ( (char) in.read() ) ) != '\uffff' ) {
            log.append( c );
        }
        in.close();

        if ( p.waitFor() != 0 ) {
            throw new Exception( Messages.get( "$DI10091", log.toString() ) );
        }

        // do not forget to delete temporary geotiff
        new File( file.toString() + "_tmp" ).delete();

        LOG.logInfo( "GDAL output:" );
        LOG.logInfo( log.toString() );

        fireCommandProcessedEvent();
    }

    @Override
    public Object getResult() {
        return null;
    }

}
