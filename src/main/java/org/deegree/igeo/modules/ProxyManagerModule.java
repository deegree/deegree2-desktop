//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
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
package org.deegree.igeo.modules;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.igeo.modules.ActionDescription.ACTIONTYPE;

/**
 * Module for managing proxy information for establishing network connections
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ProxyManagerModule<T> extends DefaultModule<T> {

    private static final ILogger LOG = LoggerFactory.getLogger( ProxyManagerModule.class );

    static {
        ActionDescription actionDescription = new ActionDescription( "open", "opens a dialog managing proxy settings",
                                                                     null, "manage proxy settings",
                                                                     ACTIONTYPE.PushButton, null, null );
        moduleCapabilities = new ModuleCapabilities( actionDescription );
    }

    /**
     * event handler method
     */
    public void open() {
        if ( this.componentStateAdapter.isClosed() ) {
            this.componentStateAdapter.setClosed( false );
            createIView();
        }
    }

    /**
     * @param protocol
     * @param proxyHost
     * @param proxyPort
     * @param proxyUser
     * @param proxyPassword
     * @param nonProxyHosts
     */
    public void setProxySettings( String protocol, String proxyHost, int proxyPort, String proxyUser,
                                  String proxyPassword, String[] nonProxyHosts ) {
        LOG.logDebug( "proxy settings for protocol: ", protocol );
        LOG.logDebug( "set proxy host: ", proxyHost );
        LOG.logDebug( "set proxy port: ", proxyPort );

        System.setProperty( protocol + ".proxyHost", proxyHost );
        System.setProperty( protocol + ".proxyPort", Integer.toString( proxyPort ) );
        if ( proxyUser != null && proxyUser.trim().length() > 0 ) {
            LOG.logDebug( "set proxy user: ", proxyUser );
            System.setProperty( protocol + ".proxyUser", proxyHost );
            if ( proxyPassword != null && proxyPassword.trim().length() > 0 ) {
                LOG.logDebug( "set proxy password: ", proxyPassword );
                System.setProperty( protocol + ".proxyPassword", proxyHost );
            }
        }
        if ( nonProxyHosts != null && nonProxyHosts.length > 0 ) {
            String tmp = StringTools.arrayToString( nonProxyHosts, '|' );
            LOG.logDebug( "set non proxy hosts: ", tmp );
            System.setProperty( protocol + ".nonProxyHosts", tmp );
        }
    }

}
