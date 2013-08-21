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

package org.deegree.desktop.settings;

import org.deegree.desktop.dataadapter.wms.WMS111CapabilitiesEvaluator;
import org.deegree.desktop.config.WMSGridCoverageAdapterType;
import org.deegree.desktop.config.WMSGridCoverageAdapterType.FeatureCount;

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
public class WMSGridCoverageAdapterSettings extends ServiceAdapterSettings {

    /**
     * @param serviceAdapterType
     * @param changeable
     */
    public WMSGridCoverageAdapterSettings( WMSGridCoverageAdapterType serviceAdapterType, boolean changeable ) {
        super( serviceAdapterType, changeable );
    }

    /**
     * 
     * @param version
     * @return name of class responsible for handling capabilities of a WFS. Default is
     *         {@link WMS111CapabilitiesEvaluator}
     */
    public String getCapabilitiesEvaluator( String version ) {
        String clzz = super.getCapabilitiesEvaluator( version );
        if ( clzz == null ) {
            return WMS111CapabilitiesEvaluator.class.getName();
        } else {
            return clzz;
        }
    }

    /**
     * 
     * @return feature count for GetFeatureInfo requests
     */
    public int getFeatureCount() {
        FeatureCount fcnt = ( (WMSGridCoverageAdapterType) serviceAdapterType ).getFeatureCount();
        if ( fcnt == null ) {
            setFeatureCount( 1 );
            fcnt = ( (WMSGridCoverageAdapterType) serviceAdapterType ).getFeatureCount();
        }
        return ( (WMSGridCoverageAdapterType) serviceAdapterType ).getFeatureCount().getVal();
    }

    /**
     * 
     * @param count
     */
    public void setFeatureCount( int count ) {
        FeatureCount fcnt = new WMSGridCoverageAdapterType.FeatureCount();
        fcnt.setVal( count );
        ( (WMSGridCoverageAdapterType) serviceAdapterType ).setFeatureCount( fcnt );
    }

}
