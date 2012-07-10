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

package org.deegree.igeo.modules.remotecontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.SelectFeatureCommand;
import org.deegree.igeo.commands.model.ZoomCommand;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.LayerGroup;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MapModelVisitor;
import org.deegree.igeo.modules.ModuleException;
import org.deegree.model.Identifier;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryException;

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
public class SelectFeatureHandler implements RequestHandler {

    private static final ILogger LOG = LoggerFactory.getLogger( SelectFeatureHandler.class );

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.modules.remotecontrol.RequestHandler#perform(java.util.Map,
     * org.deegree.igeo.ApplicationContainer)
     */
    public String perform( Map<String, String> paramater, ApplicationContainer<?> appContainer )
                            throws ModuleException {
        String tmp = paramater.get( "OBJECTIDS" );
        if ( tmp == null || tmp.length() == 0 ) {
            throw new ModuleException( Messages.get( "$DG10091" ) );
        }
        List<String> objIds = StringTools.toList( tmp, ",;", true );
        tmp = paramater.get( "LAYER" );
        if ( tmp == null ) {
            throw new ModuleException( Messages.get( "$DG10092" ) );
        }

        MapModel mapModel = appContainer.getMapModel( null );
        LocalMapModelVisitor mv = new LocalMapModelVisitor( tmp );
        try {
            mapModel.walkLayerTree( mv );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new ModuleException( e.getMessage() );
        }

        Layer layer = mv.getResult();
        layer.setVisible( true );

        if ( layer == null ) {
            throw new ModuleException( Messages.get( "$DG10093", tmp ) );
        }

        // first select features in layer
        List<Identifier> ids = new ArrayList<Identifier>();
        for ( String id : objIds ) {
            ids.add( new Identifier( id ) );
        }
        SelectFeatureCommand cmd = new SelectFeatureCommand( layer, ids, false );
        try {
            appContainer.getCommandProcessor().executeSychronously( cmd, true );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new ModuleException( e.getMessage() );
        }

        // get selected features as FeatureCollection and so their common bounding box
        FeatureCollection fc = layer.getSelectedFeatures();
        if ( fc.size() == 0 ) {
            throw new ModuleException( Messages.get( "$DG10094", paramater.get( "OBJECTIDS" ) ) );
        }
        Envelope env = null;
        try {
            env = fc.getBoundedBy();
            if ( env.getWidth() < 0.0001 ) {
                // feature collection just contains one point; set fix bbox width/height
                // 25 map units
                env = env.getBuffer( 25 );
            }
        } catch ( GeometryException e ) {
            LOG.logError( e.getMessage(), e );
            throw new ModuleException( e.getMessage() );
        }

        // zoom to common bounding box of selected features
        ZoomCommand zcmd = new ZoomCommand( mapModel );
        zcmd.setZoomBox( env, mapModel.getTargetDevice().getPixelWidth(), mapModel.getTargetDevice().getPixelHeight() );
        if ( "TRUE".equalsIgnoreCase( paramater.get( "sync" ) ) ) {
            try {
                appContainer.getCommandProcessor().executeSychronously( zcmd, true );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                throw new ModuleException( e.getMessage() );
            }
        } else {
            appContainer.getCommandProcessor().executeASychronously( zcmd );
        }

        return "feature selected";
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////
    // inner classes
    // ////////////////////////////////////////////////////////////////////////////////////////////
    private class LocalMapModelVisitor implements MapModelVisitor {

        private String layerTitle;

        private Layer result;

        /**
         * 
         * @param layerTitle
         */
        LocalMapModelVisitor( String layerTitle ) {
            this.layerTitle = layerTitle;
        }

        /**
         * 
         */
        public void visit( Layer layer )
                                throws Exception {
            if ( layer.getTitle().equals( layerTitle ) ) {
                result = layer;
            }
        }

        /**
         * 
         */
        public void visit( LayerGroup layerGroup )
                                throws Exception {
        }

        /**
         * @return the result
         */
        public Layer getResult() {
            return result;
        }

    }
}
