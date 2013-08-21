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
package org.deegree.desktop.modules.gazetteer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.modules.DefaultModule;
import org.deegree.desktop.modules.IModule;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.desktop.config.ModuleType;
import org.deegree.desktop.config._ComponentPositionType;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class GazetteerModule<T> extends DefaultModule<T>{
    
    private static final ILogger LOG = LoggerFactory.getLogger( GazetteerModule.class );
    
    private List<Hierarchy> hierarchies;
   
    @Override
    public void init( ModuleType moduleType, _ComponentPositionType componentPosition, ApplicationContainer<T> appCont,
                      IModule<T> parent, Map<String, String> initParams ) {
        // TODO Auto-generated method stub
        super.init( moduleType, componentPosition, appCont, parent, initParams );
        Iterator<String> iterator = initParams.keySet().iterator();
        hierarchies = new ArrayList<Hierarchy>(initParams.size());
        while ( iterator.hasNext() ) {
            String key = (String) iterator.next();
            String value = initParams.get( key ).trim();
            // remove CDATA
            if ( value.startsWith( "<![CDATA[" )) { 
            value = value.substring( 9, value.length()-3 ).trim();
            }
            XMLFragment xml = new XMLFragment();
            try {                
                xml.load( new StringReader( value ), XMLFragment.DEFAULT_URL );
                hierarchies.add( new Hierarchy( xml ) );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                LOG.logError( value );
            }
        }        
    }
    
    /**
     * event handler method for opening gazetteer dialog/window
     */
    public void open() {
        if ( this.componentStateAdapter.isClosed() ) {
            this.componentStateAdapter.setClosed( false );
            createIView();
        }
    }
    
    /**
     * 
     * @return list of {@link Hierarchy}s known by a {@link GazetteerModule}
     */
    public List<Hierarchy> getHierarchyList() {
        return hierarchies;
    }
    
}
