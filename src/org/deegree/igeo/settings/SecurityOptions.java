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

package org.deegree.igeo.settings;

import java.util.List;

import org.deegree.igeo.config.AuthenticationType;
import org.deegree.igeo.config.SecurityType;

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
public class SecurityOptions extends ElementSettings {

    private SecurityType securityType;

    /**
     * @param changeable
     * @param securityType
     */
    public SecurityOptions( SecurityType securityType, boolean changeable ) {
        super( changeable );
        this.securityType = securityType;
    }

    /**
     * 
     * @param protectedServerURL
     *            URL of the server that is protected by a security component. If <code>null</code> is passed default
     *            authentication method is returned
     * @return authentication method for protected server. If protected server is unknown <code>null</code> will be
     *         returned
     */
    public String getAuthenticationMethod( String protectedServerURL ) {
        List<AuthenticationType> list = securityType.getAuthentication();
        if ( protectedServerURL == null ) {
            for ( AuthenticationType authenticationType : list ) {
                if ( authenticationType.isDefault() ) {
                    return authenticationType.getMethod();
                }
            }
        } else {
            for ( AuthenticationType authenticationType : list ) {
                if ( authenticationType.getProtectedServer().contains( protectedServerURL ) ) {
                    return authenticationType.getMethod();
                }
            }
        }
        return null;
    }

    /**
     * 
     * @param protectedServerURL
     * @return depending on authentication method a server is used to authenticate an user before he can access a
     *         protected server. If protected server is unknown or no authentication server is required/used
     *         <code>null</code> will be returned
     */
    public String getAuthenticationServer( String protectedServerURL ) {
        List<AuthenticationType> list = securityType.getAuthentication();
        if ( protectedServerURL == null ) {
            for ( AuthenticationType authenticationType : list ) {
                if ( authenticationType.isDefault() ) {
                    return authenticationType.getAuthenticationServer();
                }
            }
        } else {
            for ( AuthenticationType authenticationType : list ) {
                if ( authenticationType.getProtectedServer().contains( protectedServerURL ) ) {
                    return authenticationType.getAuthenticationServer();
                }
            }
        }
        return null;
    }

    /**
     * adds a protected server to listed a managed servers
     * @param protectedServerURL
     * @param method
     * @param authenticationServer
     */
    public void addProtectedServerURL( String protectedServerURL, String method, String authenticationServer ) {
        if ( changeable ) {
            List<AuthenticationType> list = securityType.getAuthentication();
            for ( AuthenticationType authenticationType : list ) {
                if ( authenticationServer != null
                     && authenticationType.getAuthenticationServer().equals( authenticationServer ) ) {
                    if ( authenticationType.getMethod().equals( method )
                         && !authenticationType.getProtectedServer().contains( protectedServerURL ) ) {
                        authenticationType.getProtectedServer().add( protectedServerURL );
                        return;
                    }
                }
            }
            AuthenticationType at = new AuthenticationType();
            at.setAuthenticationServer( authenticationServer );
            at.setMethod( method );
            at.getProtectedServer().add( protectedServerURL );
            list.add( at );
        }
    }

}
