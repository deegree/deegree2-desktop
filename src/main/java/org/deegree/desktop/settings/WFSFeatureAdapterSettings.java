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

import java.util.List;

import org.deegree.desktop.dataadapter.wfs.WFS110CapabilitiesEvaluator;
import org.deegree.desktop.dataadapter.wfs.WFS110DataLoader;
import org.deegree.desktop.dataadapter.wfs.WFS110DataWriter;
import org.deegree.desktop.config.WFSFeatureAdapterType;
import org.deegree.desktop.config.WFSFeatureAdapterType.DataLoader;
import org.deegree.desktop.config.WFSFeatureAdapterType.DataWriter;

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
public class WFSFeatureAdapterSettings extends ServiceAdapterSettings {

    private WFSFeatureAdapterType wfsFeatureAdapter;

    /**
     * @param wfsFeatureAdapter
     * @param changeable
     */
    public WFSFeatureAdapterSettings( WFSFeatureAdapterType wfsFeatureAdapter, boolean changeable ) {
        super( wfsFeatureAdapter, changeable );
        this.wfsFeatureAdapter = wfsFeatureAdapter;
    }

    /**
     * 
     * @param version
     * @return name of class responsible for handling capabilities of a WFS. Default is
     *         {@link WFS110CapabilitiesEvaluator}
     */
    public String getCapabilitiesEvaluator( String version ) {
        String clzz = super.getCapabilitiesEvaluator( version );
        if ( clzz == null ) {
            return WFS110CapabilitiesEvaluator.class.getName();
        } else {
            return clzz;
        }
    }

    /**
     * 
     * @param version version WFS
     * @return name of the class responsible for handling data loading/access for a WFS. Default is
     *         {@link WFS110DataLoader}
     */
    public String getDataLoader( String version ) {
        List<DataLoader> loader = wfsFeatureAdapter.getDataLoader();
        for ( DataLoader dataLoader : loader ) {
            if ( dataLoader.getVersion() == null || dataLoader.getVersion().equals( version ) ) {
                return dataLoader.getVal();
            }
        }
        return WFS110DataLoader.class.getName();
    }

    /**
     * 
     * @param version version WFS
     * @return name of the class responsible for handling data writing for a WFS. Default is {@link WFS110DataWriter}
     */
    public String getDataWriter( String version ) {        
        List<DataWriter> writer = wfsFeatureAdapter.getDataWriter();
        for ( DataWriter datawriter : writer ) {
            if ( datawriter.getVersion() == null || datawriter.getVersion().equals( version ) ) {
                return datawriter.getVal();
            }
        }        
        return WFS110DataWriter.class.getName();
    }
    
    /**
     * 
     * @return maximum number of features that can be accessed from a WFS
     */
    public int getMaxFeatures() {
        return wfsFeatureAdapter.getMaxFeature().getVal();
    }
    
    /**
     * @see #getMaxFeatures()
     * @param value
     */
    public void setMaxFeatures( int value ) {
        if ( wfsFeatureAdapter.getMaxFeature().isChangeable() ) {
            wfsFeatureAdapter.getMaxFeature().setVal( value );
        }
    }

}
