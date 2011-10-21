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

package org.deegree.igeo.modules.geoprocessing;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.ActionDescription;
import org.deegree.igeo.modules.DefaultModule;
import org.deegree.igeo.modules.ModuleCapabilities;
import org.deegree.igeo.modules.ModuleException;
import org.deegree.igeo.modules.ActionDescription.ACTIONTYPE;
import org.deegree.igeo.modules.DefaultMapModule.SelectedFeaturesVisitor;
import org.deegree.kernel.Command;
import org.deegree.model.Identifier;
import org.deegree.model.feature.FeatureCollection;

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
public class WPSClientModule<T> extends DefaultModule<T> {

    static {
        ActionDescription ad1 = new ActionDescription(
                                                       "open",
                                                       "opens a dialog selecting a WPS process that will be invoked from iGeoDesktop",
                                                       null, "open WPS dialog", ACTIONTYPE.PushButton, null, null );
        moduleCapabilities = new ModuleCapabilities( ad1 );
    }

    /**
     * opens a dialog for selecting a WPS dialog
     */
    public void open() {
        MapModel mapModel = appContainer.getMapModel( null );
        List<Layer> layers = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
        if ( layers.size() > 0 && this.componentStateAdapter.isClosed() ) {
            this.componentStateAdapter.setClosed( false );
            createIView();
        }
    }

    /**
     * force invoking a WPS process
     * 
     * @param parameter
     */
    @SuppressWarnings("unchecked")
    public void process( Map<String, Object> parameter ) {

        String s1 = (String) parameter.get( "$PROCESS" );
        String s2 = (String) parameter.get( "$WPS" );

        String mmId = getInitParameter( "assignedMapModel" );
        MapModel mm = appContainer.getMapModel( new Identifier( mmId ) );
        SelectedFeaturesVisitor visitor = new SelectedFeaturesVisitor( -1 );
        try {
            mm.walkLayerTree( visitor );
        } catch ( Exception e ) {
            throw new ModuleException( e.getMessage(), e );
        }
        FeatureCollection tmp = visitor.col;
        List<Layer> layers = mm.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
        if ( tmp.size() == 0 ) {
            for ( Layer layer : layers ) {
                List<DataAccessAdapter> dada = layer.getDataAccess();
                for ( DataAccessAdapter dataAccessAdapter : dada ) {
                    if ( dataAccessAdapter instanceof FeatureAdapter ) {
                        tmp.addAllUncontained( ( (FeatureAdapter) dataAccessAdapter ).getFeatureCollection() );
                    }
                }

            }
        }
        parameter.put( "$InputGeometry$", tmp );

        String className = getInitParameter( s1 + '@' + s2 );
        try {
            Class<Command> clzz = (Class<Command>) Class.forName( className );
            Constructor<Command> constructor = clzz.getConstructor( new Class[] { Layer.class, Map.class } );
            Command command = constructor.newInstance( new Object[] { layers.get( 0 ), parameter } );
            appContainer.getCommandProcessor().executeASychronously( command );
        } catch ( Exception e ) {
            throw new ModuleException( e.getMessage(), e );
        }
    }

}
