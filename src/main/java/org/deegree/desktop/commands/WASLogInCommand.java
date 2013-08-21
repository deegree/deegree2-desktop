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

import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.deegree.datatypes.QualifiedName;
import org.deegree.enterprise.WebUtils;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.Command;
import org.deegree.ogcwebservices.OWSUtils;

/**
 * {@link Command} implementation for performing a login to a security environment (WAS)
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class WASLogInCommand extends AbstractCommand {

    public static final QualifiedName name = new QualifiedName( "log in" );

    private String sessionId;

    private String user;

    private String password;

    private URL was;

    /**
     * @param user
     * @param password
     * @param was
     */
    public WASLogInCommand( String user, String password, URL was ) {
        this.user = user;
        this.password = password;
        this.was = was;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {

        StringBuffer sb = new StringBuffer( 500 );
        String address = OWSUtils.validateHTTPGetBaseURL( was.toURI().toASCIIString() );
        sb.append( address );
        sb.append( "SERVICE=WAS&VERSION=1.0.0&REQUEST=GetSession&" );
        sb.append( "AUTHMETHOD=urn:x-gdi-nrw:authnMethod:1.0:password&CREDENTIALS=" );
        sb.append( user ).append( ',' ).append( password );
        URL url = new URL( sb.toString() );
        HttpClient client = new HttpClient();
        WebUtils.enableProxyUsage( client, url );
        GetMethod get = new GetMethod( url.toURI().toASCIIString() );
        client.executeMethod( get );
        String response = get.getResponseBodyAsString();
        if ( response.contains( "ServiceExceptionReport" ) ) {
            throw new LoginException( response );
        }
        sessionId = response;

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
        return sessionId;
    }

}
