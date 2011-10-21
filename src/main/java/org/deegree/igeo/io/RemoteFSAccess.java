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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import org.apache.commons.httpclient.HttpException;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.FileUtils;
import org.deegree.framework.util.HttpUtils;
import org.deegree.framework.util.StringTools;
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
public class RemoteFSAccess implements FileSystemAccess {

    private static final ILogger LOG = LoggerFactory.getLogger( RemoteFSAccess.class );

    private ApplicationContainer<?> appCont;

    private String remoteAddr = "http://blizzard:8280/fileaccess/fileaccess";

    /**
     * 
     */
    public RemoteFSAccess() {
        if ( System.getProperty( "remoteFileAddress" ) != null ) {
            remoteAddr = System.getProperty( "remoteFileAddress" );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.io.FileSystemAccess#setApplicationContainer(org.deegree.igeo.ApplicationContainer)
     */
    public void setApplicationContainer( ApplicationContainer<?> appCont ) {
        this.appCont = appCont;
        // TODO
        // read remote address from settings
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.io.FileSystemAccess#deleteFile(java.io.File)
     */
    public void deleteFile( File file )
                            throws IOException {
        String req = HttpUtils.addAuthenticationForKVP( "", this.appCont.getUser(), this.appCont.getPassword(),
                                                        this.appCont.getCertificate( remoteAddr ) );
        StringBuilder sb = new StringBuilder( 500 );
        sb.append( req ).append( '&' );
        sb.append( "FILE=" ).append( file.getPath() ).append( "&ACTION=delete" );
        LOG.logDebug( "remote address: ", remoteAddr );
        LOG.logDebug( "request: ", sb );
        HttpUtils.performHttpGet( remoteAddr, sb.toString(), 15000, null, null, null );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.io.FileSystemAccess#exists(java.io.File)
     */
    public boolean exists( File file )
                            throws IOException, HttpException {
        String req = HttpUtils.addAuthenticationForKVP( remoteAddr, this.appCont.getUser(), this.appCont.getPassword(),
                                                        this.appCont.getCertificate( remoteAddr ) );
        StringBuilder sb = new StringBuilder( 500 );
        sb.append( req ).append( '&' );
        sb.append( "FILE=" ).append( file.getPath() ).append( "&ACTION=exists" );

        LOG.logDebug( "remote address: ", remoteAddr );
        LOG.logDebug( "request: ", sb );

        InputStream is = HttpUtils.performHttpGet( remoteAddr, sb.toString(), 15000, null, null, null ).getResponseBodyAsStream();
        String s = FileUtils.readTextFile( is ).toString();

        return "true".equals( s );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.io.FileSystemAccess#listDirectory(java.io.File, java.lang.String[])
     */
    public File[] listDirectory( File directory, String... extension )
                            throws IOException {
        String req = HttpUtils.addAuthenticationForKVP( "", this.appCont.getUser(), this.appCont.getPassword(),
                                                        this.appCont.getCertificate( remoteAddr ) );
        StringBuilder sb = new StringBuilder( 500 );
        sb.append( req ).append( '&' );
        if ( extension == null ) {
            extension = new String[] { "*" };
        }
        String ext = StringTools.arrayToString( extension, ',' );
        if ( ext.equalsIgnoreCase( "prj" ) ) {
            sb.append( "action=listProjects&extensions=" ).append( ext );
        } else {
            sb.append( "action=listDataFiles&extensions=" ).append( ext );
        }
        if ( directory != null ) {
            sb.append( "&root=" ).append( directory.getPath() );
        }
        LOG.logDebug( "remote address: ", remoteAddr );
        LOG.logDebug( "request: ", sb );
        InputStream is = HttpUtils.performHttpGet( remoteAddr, sb.toString(), 15000, null, null, null ).getResponseBodyAsStream();
        String s = FileUtils.readTextFile( is ).toString();
        String[] fl = StringTools.toArray( s, ",;", false );
        File[] files = new File[fl.length];
        for ( int i = 0; i < fl.length; i++ ) {
            files[i] = new File( fl[i] );
        }

        return files;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.io.FileSystemAccess#readFile(java.io.File)
     */
    public InputStream readFile( File file )
                            throws IOException {
        String req = HttpUtils.addAuthenticationForKVP( "", this.appCont.getUser(), this.appCont.getPassword(),
                                                        this.appCont.getCertificate( remoteAddr ) );
        StringBuilder sb = new StringBuilder( 500 );
        sb.append( req ).append( '&' );
        sb.append( "FILE=" ).append( file.getPath() ).append( "&ACTION=readFile" );
        LOG.logDebug( "remote address: ", remoteAddr );
        LOG.logDebug( "request: ", sb );
        return HttpUtils.performHttpGet( remoteAddr, sb.toString(), 15000, null, null, null ).getResponseBodyAsStream();
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
                fl = new File( fl.getName() );
            }
            String req = HttpUtils.addAuthenticationForKVP( "", this.appCont.getUser(), this.appCont.getPassword(),
                                                            this.appCont.getCertificate( remoteAddr ) );
            StringBuilder sb = new StringBuilder( 500 );
            String path = StringTools.replace( fl.getPath(), "/", "2F", true );
            path = StringTools.replace( path, "\\", "2F", true );
            sb.append( remoteAddr ).append( '?' ).append( req ).append( '&' );
            sb.append( "FILE=" ).append( path ).append( "&ACTION=readFile" );
            LOG.logDebug( "file URL: ", sb );
            return new URL( sb.toString() );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.io.FileSystemAccess#writeFile(java.io.File, java.io.InputStream)
     */
    public void writeFile( File file, InputStream contentStream )
                            throws IOException {
        String req = HttpUtils.addAuthenticationForKVP( remoteAddr + "?", this.appCont.getUser(),
                                                        this.appCont.getPassword(),
                                                        this.appCont.getCertificate( remoteAddr ) );
        StringBuilder sb = new StringBuilder( 500 );
        sb.append( req ).append( '&' ).append( "FILE=" ).append( file.getPath() ).append( "&ACTION=writeFile" );
        LOG.logDebug( "remote address: ", remoteAddr );
        LOG.logDebug( "request: ", sb );
        HttpUtils.performHttpPost( sb.toString(), contentStream, 15000, null, null, "text/xml", null, null );
        contentStream.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.io.FileSystemAccess#writeFile(java.io.File, java.io.Reader)
     */
    public void writeFile( File file, Reader contentReader )
                            throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream( 100000 );
        char[] buffer = new char[10240];
        int cnt = contentReader.read( buffer );
        do {
            String s = new String( buffer, 0, cnt );
            bos.write( s.getBytes() );
        } while ( ( cnt = contentReader.read( buffer ) ) > 0 );
        contentReader.close();
        writeFile( file, new ByteArrayInputStream( bos.toByteArray() ) );
        bos.close();
    }

}
