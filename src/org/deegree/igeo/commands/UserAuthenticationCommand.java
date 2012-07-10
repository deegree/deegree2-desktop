//$HeadURL: svn+ssh://developername@svn.wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
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

package org.deegree.igeo.commands;

import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.deegree.datatypes.QualifiedName;
import org.deegree.enterprise.WebUtils;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.kernel.AbstractCommand;
import org.deegree.ogcwebservices.OWSUtils;

/**
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author: Andreas Poth $
 * 
 * @version $Revision: $, $Date: $
 * 
 */
public class UserAuthenticationCommand extends AbstractCommand {

    public static final QualifiedName name = new QualifiedName( "user authentication" );
    
    private static final ILogger LOG = LoggerFactory.getLogger( UserAuthenticationCommand.class );     

    private boolean authenticated;

    private String user;

    private String password;

    private URL was;

    /**
     * @param user
     * @param password
     * @param was
     */
    public UserAuthenticationCommand( String user, String password, URL was ) {
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

        try {
            StringBuffer sb = new StringBuffer( 500 );
            String address = OWSUtils.validateHTTPGetBaseURL( was.toURI().toASCIIString() );
            sb.append( address );
            sb.append( "user=" ).append( user ).append( "&password=" ).append( password );
            LOG.logDebug( "authentication request: ", sb );
            URL url = new URL( sb.toString() );
            HttpClient client = new HttpClient();
            WebUtils.enableProxyUsage( client, url );
            GetMethod get = new GetMethod( url.toURI().toASCIIString() );
            client.executeMethod( get );
            String response = get.getResponseBodyAsString();
            authenticated = "true".equalsIgnoreCase( response );
        } catch ( Exception e ) {
            LOG.logError( "can not authenticate against: " + was );
            throw e;
        }
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
        return new Boolean( authenticated );
    }

}
