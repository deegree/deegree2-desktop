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

package org.deegree.igeo.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.deegree.framework.util.ConvenienceFileFilter;
import org.deegree.igeo.ApplicationContainer;

/**
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class LocalFSAccess implements FileSystemAccess {

    private ApplicationContainer<?> appCont;

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.io.FileSystemAccess#setApplicationContainer(org.deegree.igeo.ApplicationContainer)
     */
    public void setApplicationContainer( ApplicationContainer<?> appCont ) {
        this.appCont = appCont;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.io.FileSystemAccess#deleteFile(java.io.File)
     */
    public void deleteFile( File file )
                            throws IOException {
        file.delete();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.io.FileSystemAccess#exists(java.io.File)
     */
    public boolean exists( File file ) {
        return file.exists();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.io.FileSystemAccess#listDirectory(java.io.File)
     */
    public File[] listDirectory( File directory, String... extension )
                            throws IOException {
        return directory.listFiles( new ConvenienceFileFilter( true, extension ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.io.FileSystemAccess#readFile(java.io.File)
     */
    public InputStream readFile( File file )
                            throws IOException {
        return new FileInputStream( file );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.io.FileSystemAccess#getFileURL(java.io.File)
     */
    public URL getFileURL( String file )
                            throws IOException {
        if ( file.toLowerCase().startsWith( "http://" ) ) {
            return new URL( file );
        } else {
            File fl = new File( file );
            if ( fl.isAbsolute() ) {
                return fl.toURL();
            }
            return appCont.resolve( fl.getPath() );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.io.FileSystemAccess#writeFile(java.io.File, java.io.InputStream)
     */
    public void writeFile( File file, InputStream contentStream )
                            throws IOException {
        FileOutputStream fos = new FileOutputStream( file );
        byte[] buffer = new byte[10240];
        int cnt = contentStream.read( buffer );
        do {
            fos.write( buffer, 0, cnt );
        } while ( ( cnt = contentStream.read( buffer ) ) > 0 );

        contentStream.close();
        fos.close();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.io.FileSystemAccess#writeFile(java.io.File, java.io.Reader)
     */
    public void writeFile( File file, Reader contentReader )
                            throws IOException {
        FileWriter fw = new FileWriter( file );
        char[] buffer = new char[10240];
        int cnt = contentReader.read( buffer );
        do {
            fw.write( buffer, 0, cnt );
        } while ( ( cnt = contentReader.read( buffer ) ) > 0 );

        contentReader.close();
        fw.close();

    }

}
