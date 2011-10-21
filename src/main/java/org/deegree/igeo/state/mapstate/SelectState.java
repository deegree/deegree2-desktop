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

package org.deegree.igeo.state.mapstate;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Locale;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.SelectFeatureCommand;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.DrawingPane;
import org.deegree.igeo.views.swing.drawingpanes.SelectRectangleDrawingPane;
import org.deegree.kernel.Command;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class SelectState extends ToolState {

    private static final ILogger LOG = LoggerFactory.getLogger( SelectState.class );

    /**
     * 
     * @param appContainer
     */
    public SelectState( ApplicationContainer<?> appContainer ) {
        super( appContainer );
    }

    /**
     * special createCommand method for select states
     * 
     * @param module
     * @param layer
     * @param points
     * @param additive
     * @return select command
     */
    public Command createCommand( Layer layer, List<Geometry> geometries, boolean additive ) {
        if ( substate != null ) {
            return ( (SelectState) substate ).createCommand( layer, geometries, additive );
        }
        return null;
    }

    /**
     * special createCommand method for select states
     * 
     * @param module
     * @param layer
     * @param filter
     * @param additive
     * @return select command
     */
    public Command createCommand( Layer layer, Filter filter, boolean additive ) {
        if ( substate != null ) {
            return ( (SelectState) substate ).createCommand( layer, filter, additive );
        }
        return null;
    }

    @Override
    public DrawingPane createDrawingPane( String platform, Graphics g ) {
        if ( this.substate != null ) {
            return this.substate.createDrawingPane( platform, g );
        } else {
            return null;
        }
    }

    public FeatureCollection handle( ApplicationContainer<?> appContainer, List<Geometry> geometries, Layer layer,
                                     int keyModifiers ) {
        // everything is fine
        // select features/geometries of layer selected for editing interacting
        // with rectangle created from passed point list (contains two points)
        Command command = null;
        // don't ask me why KeyEvent.VK_ALT must be used to identify Ctrl key
        if ( keyModifiers == KeyEvent.VK_ALT ) {
            command = createCommand( layer, geometries, true );
        } else {
            command = createCommand( layer, geometries, false );
        }
        try {
            appContainer.getCommandProcessor().executeSychronously( command, true );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null,
                                             Messages.getMessage( Locale.getDefault(), "$MD10280" ),
                                             Messages.getMessage( Locale.getDefault(), "$MD10281" ) );
        }
        // opens as panel for editing alphanumeric properties of a feature if init param
        // 'autoOpenEditorPanel' has been set to true/1
        return layer.getSelectedFeatures();
    }

    // /////////////////////////////////////////////////////////////////
    // convenience methods for setting sub states //
    // /////////////////////////////////////////////////////////////////

    /**
     * 
     */
    public void setRectangleSelectState() {
        this.substate = new RectangleSelectState( appContainer );
    }

    /**
     * 
     * 
     */
    public void setPolygonSelectState() {
        this.substate = new PolygonSelectState( appContainer );
    }

    /**
     * 
     * 
     */
    public void setLineStringSelectState() {
        this.substate = new LineStringSelectState( appContainer );
    }

    // /////////////////////////////////////////////////////////////////
    // inner classes ... well known select sub states //
    // /////////////////////////////////////////////////////////////////

    /**
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version. $Revision$, $Date$
     */
    public static class RectangleSelectState extends SelectState {

        /**
         * 
         * @param appContainer
         */
        public RectangleSelectState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Command createCommand( Layer layer, Filter filter, boolean additive ) {
            return null;
        }

        @Override
        public Command createCommand( Layer layer, List<Geometry> geometries, boolean additive ) {
            Position min = ( (Point) geometries.get( 0 ) ).getPosition();
            Position max = ( (Point) geometries.get( 1 ) ).getPosition();
            Envelope envelope = GeometryFactory.createEnvelope( min, max, geometries.get( 0 ).getCoordinateSystem() );
            return new SelectFeatureCommand( layer, envelope, additive );
        }

        @Override
        public DrawingPane createDrawingPane( String platform, Graphics g ) {

            DrawingPane drawingPane = null;
            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new SelectRectangleDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new SelectRectangleDrawingPane( appContainer );
            } else {
                LOG.logWarning( "view platfroms other than Application and Applet are not supported yet" );
            }
            return drawingPane;
        }

    }

    /**
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version. $Revision$, $Date$
     */
    public static class PolygonSelectState extends SelectState {

        /**
         * 
         * @param appContainer
         */
        public PolygonSelectState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Command createCommand( Layer layer, Filter filter, boolean additive ) {
            return null;
        }

        @Override
        public Command createCommand( Layer layer, List<Geometry> geometries, boolean additive ) {
            return null;
        }

    }

    /**
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version. $Revision$, $Date$
     */
    public static class LineStringSelectState extends SelectState {

        /**
         * 
         * @param appContainer
         */
        public LineStringSelectState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Command createCommand( Layer layer, Filter filter, boolean additive ) {
            return null;
        }

        @Override
        public Command createCommand( Layer layer, List<Geometry> geometries, boolean additive ) {
            return null;
        }
    }

}
