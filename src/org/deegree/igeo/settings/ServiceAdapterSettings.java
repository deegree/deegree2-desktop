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

import org.deegree.igeo.config.ServiceAdapterType;
import org.deegree.igeo.config.ServiceAdapterType.CapabilitiesEvaluator;

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
class ServiceAdapterSettings extends ElementSettings {

    protected ServiceAdapterType serviceAdapterType;

    /**
     * @param serviceAdapterType
     * @param changeable
     */
    ServiceAdapterSettings( ServiceAdapterType serviceAdapterType, boolean changeable ) {
        super( changeable );
        this.serviceAdapterType = serviceAdapterType;
    }
    
    /**
     * 
     * @return timeout for accessing WFS
     */
    public int getTimeout() {
        return serviceAdapterType.getTimeout().getVal();
    }
    
    /**
     * @see #getTimeout()
     * @param value
     */
    public void setTimeout( int value ) {
        if ( serviceAdapterType.getTimeout().isChangeable() ) {
            serviceAdapterType.getTimeout().setVal( value );
        }
    }
    
    /**
     * 
     * @param version
     * @return name of class responsible for handling capabilities of a WFS. Default is <code>null</code>
     */
    protected String getCapabilitiesEvaluator( String version ) {
        List<CapabilitiesEvaluator> eval = serviceAdapterType.getCapabilitiesEvaluator();
        for ( CapabilitiesEvaluator capabilitiesEvaluator : eval ) {
            if ( capabilitiesEvaluator.getVersion() == null || "version".equals( capabilitiesEvaluator.getVersion() )
                 && capabilitiesEvaluator.getVal() != null ) {
                return capabilitiesEvaluator.getVal();
            }
        }
        return null;
    }

}
