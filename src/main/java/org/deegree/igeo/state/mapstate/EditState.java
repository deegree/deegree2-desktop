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

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JLabel;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.GeometryUtils;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.digitize.CreatePolygonFromBordersCommand;
import org.deegree.igeo.commands.digitize.CreatePolygonHoleCommand;
import org.deegree.igeo.commands.digitize.CuttingPolygonHoleCommand;
import org.deegree.igeo.commands.digitize.DeleteFeatureCommand;
import org.deegree.igeo.commands.digitize.DeleteVertexCommand;
import org.deegree.igeo.commands.digitize.InsertFeatureCommand;
import org.deegree.igeo.commands.digitize.InsertVertexCommand;
import org.deegree.igeo.commands.digitize.MergeVerticesCommand;
import org.deegree.igeo.commands.digitize.MoveFeatureCommand;
import org.deegree.igeo.commands.digitize.MoveVertexCommand;
import org.deegree.igeo.commands.digitize.SplitFeatureCommand;
import org.deegree.igeo.commands.digitize.UpdateFeatureCommand;
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.DigitizerModule;
import org.deegree.igeo.state.StateException;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.DrawingPane;
import org.deegree.igeo.views.GeoDrawingPane;
import org.deegree.igeo.views.swing.Footer;
import org.deegree.igeo.views.swing.digitize.DeleteVertexDrawingPane;
import org.deegree.igeo.views.swing.digitize.InsertVertexDrawingPane;
import org.deegree.igeo.views.swing.digitize.MergeVerticesDrawingPane;
import org.deegree.igeo.views.swing.digitize.MoveFeatureDrawingPane;
import org.deegree.igeo.views.swing.digitize.MoveVertexDrawingPane;
import org.deegree.igeo.views.swing.digitize.construction.DrawArcDialog;
import org.deegree.igeo.views.swing.digitize.construction.SizedEllipseDialog;
import org.deegree.igeo.views.swing.digitize.construction.SizedRectangleDialog;
import org.deegree.igeo.views.swing.drawingpanes.CreateArcDrawingPane;
import org.deegree.igeo.views.swing.drawingpanes.CreateCircleDrawingPane;
import org.deegree.igeo.views.swing.drawingpanes.CreateLinestringDrawingPane;
import org.deegree.igeo.views.swing.drawingpanes.CreatePointDrawingPane;
import org.deegree.igeo.views.swing.drawingpanes.CreatePolygonDrawingPane;
import org.deegree.igeo.views.swing.drawingpanes.CreateRectangleDrawingPane;
import org.deegree.igeo.views.swing.drawingpanes.SwingGeoDrawingPane;
import org.deegree.kernel.Command;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiPoint;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Surface;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class EditState extends ToolState {

    private static ILogger LOG = LoggerFactory.getLogger( EditState.class );

    protected DigitizerModule<?> digitizerModule;

    protected MapModel mapModel;

    /**
     * 
     * @param appContainer
     */
    @SuppressWarnings("unchecked")
    public EditState( ApplicationContainer<?> appContainer ) {
        super( appContainer );
        mapModel = appContainer.getMapModel( null );
        List<?> list = appContainer.getModules();
        for ( Object iModule : list ) {
            if ( iModule instanceof DigitizerModule ) {
                digitizerModule = (DigitizerModule<?>) iModule;
            }
        }
    }

    protected double[] getLastPoint() {
        int x = drawingPane.getCurrent().x;
        int y = drawingPane.getCurrent().y;
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        double dx = gt.getSourceX( x );
        double dy = gt.getSourceY( y );
        return new double[] { dx, dy };
    }

    /**
     * special createCommand method for Edit states
     * 
     * @param dataAccessAdapter
     * @param feature
     * @return
     */
    public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature )
                            throws Exception {
        if ( substate != null ) {
            return ( (CreateFeatureState) substate ).createCommand( dataAccessAdapter, feature );
        }
        return null;
    }

    /**
     * special createCommand method for Edit states
     * 
     * @param dataAccessAdapter
     * @param feature
     * @param geomProperty
     * @param points
     * @return edit state
     * @throws Exception
     */
    public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature, QualifiedName geomProperty,
                                  List<Geometry> geometries )
                            throws Exception {
        if ( substate != null ) {
            return ( (CreateFeatureState) substate ).createCommand( dataAccessAdapter, feature, geomProperty,
                                                                    geometries );
        }
        return null;
    }

    /**
     * 
     * @param pointList
     * @return array of {@link Position}s
     */
    protected Position[] pointListToPositions( List<Point> pointList ) {
        Position[] pos = new Position[pointList.size()];
        for ( int i = 0; i < pos.length; i++ ) {
            pos[i] = pointList.get( i ).getPosition();
        }
        return pos;
    }

    /**
     * sets substate for EditState. The method will throw a StateException if passed substate is not valid for EditState
     * 
     * @param substate
     */
    @Override
    public void setSubstate( ToolState substate ) {
        if ( substate.getClass().getSuperclass() != EditState.class ) {
            throw new StateException( "substate must be inherited from EditState" );
        }
        this.substate = substate;
    }

    /**
     * 
     * @param platform
     *            Swing, Applet, Portlet ...
     * @param g
     *            target graphic context
     * @return
     */
    public DrawingPane createDrawingPane( String platform, Graphics g ) {
        if ( this.substate != null ) {
            return this.substate.createDrawingPane( platform, g );
        } else {
            return null;
        }
    }

    /**
     * 
     * @param appContainer
     * @param geometries
     * @param layer
     * @param keyModifiers
     * @return result feature collection
     */
    public FeatureCollection handle( ApplicationContainer<?> appContainer, List<Geometry> geometries, Layer layer,
                                     int keyModifiers ) {

        FeatureCollection fc = layer.getSelectedFeatures();
        DataAccessAdapter dataAccessAdapter = layer.getDataAccess().get( 0 );
        try {
            Command command = createCommand( dataAccessAdapter, fc, null, geometries );
            appContainer.getCommandProcessor().executeSychronously( command, true );
            fc = (FeatureCollection) command.getResult();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null,
                                             Messages.getMessage( Locale.getDefault(), "$MD10313", e.getMessage() ),
                                             Messages.getMessage( Locale.getDefault(), "$MD10312" ) );
        }
        layer.fireRepaintEvent();
        return fc;
    }

    // /////////////////////////////////////////////////////////////////
    // convenience methods for setting sub states //
    // /////////////////////////////////////////////////////////////////

    /**
     * sets sub state for cutting polygon holes
     */
    public void setCreateArcFeatureState() {
        this.substate = new CreateArcFeatureState( appContainer );
    }

    /**
     * sets sub state for cutting polygon holes
     */
    public void setCuttingPolygonHoleState() {
        this.substate = new CuttingPolygonHoleState( appContainer );
    }

    /**
     * sets sub state of Edit to delete feature state
     */
    public void setDeleteFeatureState() {
        this.substate = new DeleteFeatureState( appContainer );
    }

    /**
     * sets sub state of Edit to update feature state
     */
    public void setUpdateFeatureState() {
        this.substate = new UpdateFeatureState( appContainer );
    }

    /**
     * sets sub state of Edit to create feature state
     */
    public void setCreateFeatureState() {
        this.substate = new CreateFeatureState( appContainer );
    }

    /**
     * sets sub state of Edit to draw a hole into a polygon
     */
    public void setDrawPolygonHoleState() {
        this.substate = new DrawPolygonHoleState( appContainer );
    }

    /**
     * sets sub state of Edit to move vertices
     */
    public void setMoveVertexState() {
        this.substate = new MoveVertexState( appContainer );
    }

    /**
     * sets sub state of Edit to delete vertices
     */
    public void setDeleteVertexState() {
        this.substate = new DeleteVertexState( appContainer );
    }

    /**
     * sets sub state of Edit to merge vertices
     */
    public void setMergeVerticesState() {
        this.substate = new MergeVerticesState( appContainer );
    }

    /**
     * sets sub state of Edit to insert a new vertex
     */
    public void setInsertVertexState() {
        this.substate = new InsertVertexState( appContainer );
    }

    /**
     * sets sub state of Edit to split a geometry
     */
    public void setSplitFeatureState() {
        this.substate = new SplitFeatureState( appContainer );
    }

    /**
     * sets sub state of Edit to update feature state
     */
    public void setJoinCurveState() {
        this.substate = new JoinCurvesState( appContainer );
    }

    // /////////////////////////////////////////////////////////////////
    // inner classes ... well known edit sub states //
    // /////////////////////////////////////////////////////////////////

    /**
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version. $Revision$, $Date$
     */
    public static class DeleteFeatureState extends EditState {

        /**
         * 
         * @param appContainer
         */
        public DeleteFeatureState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature )
                                throws Exception {
            if ( substate != null ) {
                return ( (CreateFeatureState) substate ).createCommand( dataAccessAdapter, feature );
            }
            return new DeleteFeatureCommand( dataAccessAdapter, feature );
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature, QualifiedName geomProperty,
                                      List<Geometry> geometries )
                                throws Exception {
            if ( substate != null ) {
                return ( (CreateFeatureState) substate ).createCommand( dataAccessAdapter, feature );
            }
            return createCommand( dataAccessAdapter, feature );
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
    public static class CreateFeatureState extends EditState {

        /**
         * 
         * @param appContainer
         */
        public CreateFeatureState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            if ( substate.getClass().getSuperclass() != CreateFeatureState.class ) {
                throw new StateException( "substate must be instance of CreateFeatureState" );
            }
            this.substate = substate;
        }

        /**
         * 
         * @param platform
         *            Swing, Applet, Portlet ...
         * @param g
         *            target graphic context
         * @return
         */
        public DrawingPane createDrawingPane( String platform, Graphics g ) {
            if ( this.substate != null ) {
                return this.substate.createDrawingPane( platform, g );
            } else {
                return null;
            }
        }

        /**
         * 
         */
        public void setCreatePointFeatureState() {
            this.substate = new CreatePointFeatureState( appContainer );
        }

        /**
         * 
         */
        public void setCreateCurveFeatureState() {
            this.substate = new CreateCurveFeatureState( appContainer );
        }

        /**
         * 
         */
        public void setCreatePolygonFeatureState() {
            this.substate = new CreatePolygonFeatureState( appContainer );
        }

        /**
         * 
         */
        public void setCreatePolygonByFillingHoleFeatureState() {
            this.substate = new CreatePolygonByFillingHoleFeatureState( appContainer );
        }

        /**
         * 
         */
        public void setCreateCircleFeatureState() {
            this.substate = new CreateCircleFeatureState( appContainer );
        }

        /**
         * 
         */
        public void setCreateSizedEllipseFeatureState() {
            this.substate = new CreateSizedEllipseFeatureState( appContainer );
        }

        /**
         * 
         */
        public void setCreateRectangleFeatureState() {
            this.substate = new CreateRectangleFeatureState( appContainer );
        }

        /**
         * 
         */
        public void setCreateSizedRectangleFeatureState() {
            this.substate = new CreateSizedRectangleFeatureState( appContainer );
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
    public static class CreatePointFeatureState extends CreateFeatureState {

        /**
         * 
         * @param appContainer
         */
        public CreatePointFeatureState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature ) {
            return new InsertFeatureCommand( dataAccessAdapter, feature );
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature, QualifiedName geomProperty,
                                      List<Geometry> geometries )
                                throws Exception {

            if ( geometries.size() == 1 ) {
                Point point = (Point) geometries.get( 0 );
                FeatureProperty fp = feature.getProperties( geomProperty )[0];
                fp.setValue( point );
                return createCommand( dataAccessAdapter, feature );
            } else if ( geometries.size() > 1 ) {
                // create multi point
                Point[] pts = new Point[geometries.size()];
                for ( int i = 0; i < geometries.size(); i++ ) {
                    pts[i] = (Point) geometries.get( i );
                }
                MultiPoint mp = GeometryFactory.createMultiPoint( pts );
                FeatureProperty fp = feature.getProperties( geomProperty )[0];
                fp.setValue( mp );
                return createCommand( dataAccessAdapter, feature );
            } else {
                throw new StateException( Messages.getMessage( Locale.getDefault(), "$MD10269" ) );
            }
        }

        /**
         * 
         * @param platform
         *            Swing, Applet, Portlet ...
         * @param g
         *            target graphic context
         * @return
         */
        public DrawingPane createDrawingPane( String platform, Graphics g ) {
            DrawingPane drawingPane = null;
            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreatePointDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreatePointDrawingPane( appContainer );
            } else {
                LOG.logWarning( "view platfroms other than Application and Applet are not supported yet" );
            }

            drawingPane.setGraphicContext( g );
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
    public static class CreateCurveFeatureState extends CreateFeatureState {

        protected static final DecimalFormat df = new DecimalFormat( "#.000" );

        /**
         * 
         * @param appContainer
         */
        public CreateCurveFeatureState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature ) {
            return new InsertFeatureCommand( dataAccessAdapter, feature );
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature, QualifiedName geomProperty,
                                      List<Geometry> geometries )
                                throws Exception {

            if ( geometries.size() == 1 ) {
                Curve curve = (Curve) geometries.get( 0 );
                FeatureProperty fp = feature.getProperties( geomProperty )[0];
                fp.setValue( curve );
                return createCommand( dataAccessAdapter, feature );
            } else if ( geometries.size() > 1 ) {
                // create multi curve
                Curve[] curves = new Curve[geometries.size()];
                for ( int i = 0; i < geometries.size(); i++ ) {
                    curves[i] = (Curve) geometries.get( i );
                }
                MultiCurve mc = GeometryFactory.createMultiCurve( curves );
                FeatureProperty fp = feature.getProperties( geomProperty )[0];
                fp.setValue( mc );
                return createCommand( dataAccessAdapter, feature );
            } else {
                throw new StateException( Messages.getMessage( Locale.getDefault(), "$MD10268" ) );
            }
        }

        /**
         * Instantiates a DrawingPane returns it
         * 
         * @return the DrawingPane
         */
        @Override
        public DrawingPane createDrawingPane( String platform, Graphics g ) {
            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreateLinestringDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreateLinestringDrawingPane( appContainer );
            } else {
                LOG.logWarning( "view platfroms other than Application and Applet are not supported yet" );
            }
            drawingPane.setGraphicContext( g );
            return drawingPane;
        }

        @Override
        public void mouseMoved( MouseEvent event ) {
            Footer ff = ( (Footer) digitizerModule.getApplicationContainer().getFooter() );
            if ( ff != null ) {
                List<Point> points = ( (SwingGeoDrawingPane) drawingPane ).getDrawObjectsAsGeoPoints();
                // add current point
                double[] last = getLastPoint();
                double length = calcLength( points, last[0], last[1] );
                if ( length > 0 ) {
                    String units = mapModel.getCoordinateSystem().getAxisUnits()[0].getSymbol();
                    String s1 = Messages.getMessage( ( (Component) event.getSource() ).getLocale(), "$MD11346" );
                    ( (JLabel) ff.getComponent( ff.getComponentCount() - 1 ) ).setText( " " + s1 + " "
                                                                                        + df.format( length ) + ' '
                                                                                        + units );
                }
            }
        }

        /**
         * @param points
         * @param d
         * @param e
         * @return length of a curve given by passed point list and last point
         */
        public double calcLength( List<Point> points, double lastX, double lastY ) {
            double d = 0;
            for ( int i = 0; i < points.size() - 1; i++ ) {
                d += GeometryUtils.distance( points.get( i ).getPosition(), points.get( i + 1 ).getPosition() );
            }
            Position pos = GeometryFactory.createPosition( lastX, lastY );
            d += GeometryUtils.distance( points.get( points.size() - 1 ).getPosition(), pos );
            return d;
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
    public static class CreatePolygonFeatureState extends CreateFeatureState {

        protected static final DecimalFormat df = new DecimalFormat( "#.000" );

        /**
         * 
         * @param appContainer
         */
        public CreatePolygonFeatureState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature ) {

            return new InsertFeatureCommand( dataAccessAdapter, feature );
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature, QualifiedName geomProperty,
                                      List<Geometry> geometries )
                                throws Exception {

            if ( geometries.size() == 1 ) {
                Surface surface = (Surface) geometries.get( 0 );
                FeatureProperty fp = feature.getProperties( geomProperty )[0];
                fp.setValue( surface );
                return createCommand( dataAccessAdapter, feature );
            } else if ( geometries.size() > 1 ) {
                Surface[] surfaces = new Surface[geometries.size()];
                for ( int i = 0; i < geometries.size(); i++ ) {
                    surfaces[i] = (Surface) geometries.get( i );
                }
                MultiSurface ms = GeometryFactory.createMultiSurface( surfaces );
                FeatureProperty fp = feature.getProperties( geomProperty )[0];
                fp.setValue( ms );
                return createCommand( dataAccessAdapter, feature );
            } else {
                throw new StateException( Messages.getMessage( Locale.getDefault(), "$MD10267" ) );
            }
        }

        @Override
        public DrawingPane createDrawingPane( String platform, Graphics g ) {
            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreatePolygonDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreatePolygonDrawingPane( appContainer );
            } else {
                LOG.logWarning( "view platfroms other than Application, JWS and Applet are not supported yet" );
            }

            drawingPane.setGraphicContext( g );
            return drawingPane;
        }

        @Override
        public void mouseMoved( MouseEvent event ) {
            Footer ff = ( (Footer) digitizerModule.getApplicationContainer().getFooter() );
            if ( ff != null ) {
                List<Point> points = ( (SwingGeoDrawingPane) drawingPane ).getDrawObjectsAsGeoPoints();
                // add current point
                double[] last = getLastPoint();
                double length = calcLength( points, last[0], last[1] );
                double area = calcArea( points, last[0], last[1] );
                if ( area > 0 ) {
                    String units = mapModel.getCoordinateSystem().getAxisUnits()[0].getSymbol();
                    String s1 = Messages.getMessage( ( (Component) event.getSource() ).getLocale(), "$MD11347" );
                    String s2 = Messages.getMessage( ( (Component) event.getSource() ).getLocale(), "$MD11346" );
                    ( (JLabel) ff.getComponent( ff.getComponentCount() - 1 ) ).setText( " " + s1 + " "
                                                                                        + df.format( area ) + ' '
                                                                                        + units + "² - " + s2 + " "
                                                                                        + df.format( length ) + ' '
                                                                                        + units );
                }
            }
        }

        /**
         * @param points
         * @param d
         * @param e
         * @return length of a curve given by passed point list and last point
         */
        public double calcLength( List<Point> points, double lastX, double lastY ) {
            double d = 0;
            for ( int i = 0; i < points.size() - 1; i++ ) {
                d += GeometryUtils.distance( points.get( i ).getPosition(), points.get( i + 1 ).getPosition() );
            }
            Position pos = GeometryFactory.createPosition( lastX, lastY );
            d += GeometryUtils.distance( points.get( points.size() - 1 ).getPosition(), pos );
            return d;
        }

        /**
         * 
         * @param points
         * @param lastX
         * @param lastY
         * @return area of a polygon given by passed point list and last point
         */
        private double calcArea( List<Point> points, double lastX, double lastY ) {
            int i;
            int j;
            double ai;
            double atmp = 0;

            Point p = points.get( 0 );
            for ( i = points.size(), j = 0; j <= points.size(); i = j, j++ ) {
                double xi = lastX - p.getX();
                double yi = lastY - p.getY();
                if ( i < points.size() ) {
                    xi = points.get( i ).getX() - p.getX();
                    yi = points.get( i ).getY() - p.getY();
                }
                double xj = lastX - p.getX();
                double yj = lastY - p.getY();
                if ( j < points.size() ) {
                    xj = points.get( j ).getX() - p.getX();
                    yj = points.get( j ).getY() - p.getY();
                }
                ai = ( xi * yj ) - ( xj * yi );
                atmp += ai;
            }
            return Math.abs( atmp / 2 );
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
    public static class DrawPolygonHoleState extends CreatePolygonFeatureState {

        /**
         * 
         * @param appContainer
         */
        public DrawPolygonHoleState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature, QualifiedName geomProperty,
                                      List<Geometry> geometries )
                                throws Exception {
            if ( geometries.size() == 1 ) {
                Surface surface = (Surface) geometries.get( 0 );
                return new CreatePolygonHoleCommand( (FeatureAdapter) dataAccessAdapter, feature, geomProperty, surface );
            } else {
                throw new StateException( Messages.getMessage( Locale.getDefault(), "$MD10308" ) );
            }
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
    public static class CuttingPolygonHoleState extends CreatePolygonFeatureState {

        /**
         * 
         * @param appContainer
         */
        public CuttingPolygonHoleState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature ) {
            CuttingPolygonHoleCommand cmd = new CuttingPolygonHoleCommand();
            cmd.setApplicationContainer( appContainer );
            FeatureCollection fc = ( (FeatureAdapter) dataAccessAdapter ).getLayer().getSelectedFeatures();
            cmd.setFeatureCollection( fc );
            return cmd;
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature, QualifiedName geomProperty,
                                      List<Geometry> geometries )
                                throws Exception {
            CuttingPolygonHoleCommand cmd = new CuttingPolygonHoleCommand();
            cmd.setApplicationContainer( appContainer );
            FeatureCollection fc = ( (FeatureAdapter) dataAccessAdapter ).getLayer().getSelectedFeatures();
            cmd.setFeatureCollection( fc );
            cmd.setGeometryProperty( geomProperty );
            return cmd;
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
    public static class CreateCircleFeatureState extends CreatePolygonFeatureState {

        /**
         * 
         * @param appContainer
         */
        public CreateCircleFeatureState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DrawingPane createDrawingPane( String platform, Graphics g ) {
            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreateCircleDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreateCircleDrawingPane( appContainer );
            } else {
                LOG.logWarning( "view platfroms other than Application and Applet are not supported yet" );
            }
            drawingPane.setGraphicContext( g );
            return drawingPane;
        }

        @Override
        public void mouseDragged( MouseEvent event ) {
            // calculate area of a circle/polygon while digitizing
            java.awt.Point start = ( (SwingGeoDrawingPane) drawingPane ).getDrawnObject().get( 0 );
            double[] last = getLastPoint();
            GeoTransform gt = mapModel.getToTargetDeviceTransformation();
            double dx = gt.getSourceX( start.x );
            double dy = gt.getSourceY( start.y );
            double r = GeometryUtils.distance( dx, dy, last[0], last[1] );
            double area = 2d * Math.PI * r * r;
            if ( area > 0 ) {
                String s1 = Messages.getMessage( ( (Component) event.getSource() ).getLocale(), "$MD11347" );
                String units = mapModel.getCoordinateSystem().getAxisUnits()[0].getSymbol();
                Footer ff = ( (Footer) digitizerModule.getApplicationContainer().getFooter() );
                ( (JLabel) ff.getComponent( ff.getComponentCount() - 1 ) ).setText( " " + s1 + " " + df.format( area )
                                                                                    + ' ' + units + '²' );
            }
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
    public static class CreateArcFeatureState extends CreateCurveFeatureState {

        /**
         * 
         * @param appContainer
         */
        public CreateArcFeatureState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DrawingPane createDrawingPane( String platform, Graphics g ) {

            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreateArcDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreateArcDrawingPane( appContainer );
            } else {
                LOG.logWarning( "view platfroms other than Application and Applet are not supported yet" );
            }
            drawingPane.setGraphicContext( g );
            DrawArcDialog.create( digitizerModule ).setDrawingPane( (CreateArcDrawingPane) drawingPane );
            return drawingPane;
        }

        @Override
        public void mouseMoved( MouseEvent event ) {
            List<Point> points = ( (CreateArcDrawingPane) drawingPane ).getDrawObjectsAsGeoPoints();
            if ( points != null && points.size() == 1 ) {
                // just before adding the second point the radius is variable
                double[] last = getLastPoint();
                double radius = GeometryUtils.distance( last[0], last[1], points.get( 0 ).getX(),
                                                        points.get( 0 ).getY() );
                DrawArcDialog.create( digitizerModule ).setRadius( radius );
            } else if ( points != null && points.size() > 1 ) {
                double[] last = getLastPoint();
                double p = GeometryUtils.getArc( points.get( 0 ).getX(), points.get( 0 ).getY(),
                                                 points.get( 1 ).getX(), points.get( 1 ).getY(), last[0], last[1] );

                if ( GeometryUtils.isLeft( points.get( 0 ).getX(), points.get( 0 ).getY(), points.get( 1 ).getX(),
                                           points.get( 1 ).getY(), last[0], last[1] ) ) {
                    p *= -1;
                }
                DrawArcDialog.create( digitizerModule ).setArc( p );
            }

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
    public static class CreateSizedEllipseFeatureState extends CreatePolygonFeatureState {

        /**
         * 
         * @param appContainer
         */
        public CreateSizedEllipseFeatureState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DrawingPane createDrawingPane( String platform, Graphics g ) {

            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreatePointDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreatePointDrawingPane( appContainer );
            } else {
                LOG.logWarning( "view platfroms other than Application and Applet are not supported yet" );
            }

            drawingPane.setGraphicContext( g );
            return drawingPane;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void mousePressed( MouseEvent event ) {
            if ( event.getClickCount() == 1 && drawingPane != null ) {
                // created sized ellipse
                List<Point> points = ( (GeoDrawingPane) drawingPane ).getDrawObjectsAsGeoPoints();
                SizedEllipseDialog dlg = new SizedEllipseDialog( (Container) event.getSource(),
                                                                 (DigitizerModule<Container>) digitizerModule,
                                                                 points.get( 0 ) );
                Surface surface = dlg.getSurface();
                if ( surface != null ) {
                    List<Geometry> list = new ArrayList<Geometry>();
                    list.add( surface );
                    digitizerModule.mouseActionFinished( list, -1 );
                }

            }
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
    public static class CreateRectangleFeatureState extends CreatePolygonFeatureState {

        /**
         * 
         * @param appContainer
         */
        public CreateRectangleFeatureState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DrawingPane createDrawingPane( String platform, Graphics g ) {

            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreateRectangleDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreateRectangleDrawingPane( appContainer );
            } else {
                LOG.logWarning( "view platfroms other than Application and Applet are not supported yet" );
            }

            drawingPane.setGraphicContext( g );
            return drawingPane;
        }

        @Override
        public void mouseDragged( MouseEvent event ) {
            // calculate area of a rectangle/polygon while digitizing
            java.awt.Point start = ( (CreatePolygonDrawingPane) drawingPane ).getDrawnObject().get( 0 );
            double[] last = getLastPoint();
            GeoTransform gt = mapModel.getToTargetDeviceTransformation();
            double dx = gt.getSourceX( start.x );
            double dy = gt.getSourceY( start.y );
            double area = Math.abs( ( dx - last[0] ) * ( dy - last[1] ) );
            if ( area > 0 ) {
                String s1 = Messages.getMessage( ( (Component) event.getSource() ).getLocale(), "$MD11347" );
                String units = mapModel.getCoordinateSystem().getAxisUnits()[0].getSymbol();
                Footer ff = ( (Footer) digitizerModule.getApplicationContainer().getFooter() );
                ( (JLabel) ff.getComponent( ff.getComponentCount() - 1 ) ).setText( " " + s1 + " " + df.format( area )
                                                                                    + ' ' + units + '²' );
            }
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
    public static class CreateSizedRectangleFeatureState extends CreatePolygonFeatureState {

        /**
         * 
         * @param appContainer
         */
        public CreateSizedRectangleFeatureState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DrawingPane createDrawingPane( String platform, Graphics g ) {

            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreatePointDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreatePointDrawingPane( appContainer );
            } else {
                LOG.logWarning( "view platfroms other than Application and Applet are not supported yet" );
            }

            drawingPane.setGraphicContext( g );
            return drawingPane;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void mousePressed( MouseEvent event ) {
            if ( event.getClickCount() == 1 && drawingPane != null ) {
                // created sized rectangle
                List<Point> points = ( (GeoDrawingPane) drawingPane ).getDrawObjectsAsGeoPoints();
                SizedRectangleDialog dlg = new SizedRectangleDialog( (Container) event.getSource(),
                                                                     (DigitizerModule<Container>) digitizerModule,
                                                                     points.get( 0 ) );
                Surface surface = dlg.getSurface();
                if ( surface != null ) {
                    List<Geometry> list = new ArrayList<Geometry>();
                    list.add( surface );
                    digitizerModule.mouseActionFinished( list, -1 );
                }
            }
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
    public static class CreatePolygonByFillingHoleFeatureState extends CreateFeatureState {

        /**
         * 
         * @param appContainer
         */
        public CreatePolygonByFillingHoleFeatureState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature ) {
            return null;
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature, QualifiedName geomProperty,
                                      List<Geometry> geometries )
                                throws Exception {

            return new CreatePolygonFromBordersCommand( appContainer, (Point) geometries.get( 0 ) );
        }

        @Override
        public DrawingPane createDrawingPane( String platform, Graphics g ) {

            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreatePointDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreatePointDrawingPane( appContainer );
            } else {
                LOG.logWarning( "view platfroms other than Application and Applet are not supported yet" );
            }

            drawingPane.setGraphicContext( g );
            return drawingPane;
        }

        @Override
        public void mousePressed( MouseEvent event ) {
            if ( event.getClickCount() == 1 && drawingPane != null ) {
                // set inner ring of a polygon
                List<Point> points = ( (GeoDrawingPane) drawingPane ).getDrawObjectsAsGeoPoints();
                List<Geometry> list = new ArrayList<Geometry>();
                list.add( points.get( 0 ) );
                digitizerModule.mouseActionFinished( list, -1 );
            }
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
    public static class UpdateFeatureState extends EditState {

        /**
         * 
         * @param appContainer
         */
        public UpdateFeatureState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            if ( substate.getClass().getSuperclass() != UpdateFeatureState.class ) {
                throw new StateException( "substate must be instance of CreateFeatureState" );
            }
            this.substate = substate;
        }

        /**
         * 
         * 
         */
        public void setMoveFeatureState() {
            this.substate = new MoveFeatureState( appContainer );
        }

        /**
         * 
         * 
         */
        public void setUpdateAlphaNumericPropertiesStateState() {
            this.substate = new UpdateAlphaNumericPropertiesStateState( appContainer );
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
    public static class DeleteVertexState extends UpdateFeatureState {

        /**
         * 
         * @param appContainer
         */
        public DeleteVertexState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature, QualifiedName geomProperty,
                                      List<Geometry> geometries )
                                throws Exception {
            return new DeleteVertexCommand( appContainer, feature, geomProperty, (Point) geometries.get( 0 ) );
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature )
                                throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public DrawingPane createDrawingPane( String platform, Graphics g ) {
            DrawingPane drawingPane = null;
            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new DeleteVertexDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new DeleteVertexDrawingPane( appContainer );
            } else {
                LOG.logWarning( "view platfroms other than Application and Applet are not supported yet" );
            }

            drawingPane.setGraphicContext( g );
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
    public static class MoveVertexState extends UpdateFeatureState {

        /**
         * 
         * @param appContainer
         */
        public MoveVertexState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature, QualifiedName geomProperty,
                                      List<Geometry> geometries )
                                throws Exception {
            Point sourcePoint = (Point) geometries.get( 0 );
            Point targetPoint = (Point) geometries.get( 1 );
            return new MoveVertexCommand( appContainer, feature, geomProperty, sourcePoint, targetPoint );
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature )
                                throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public DrawingPane createDrawingPane( String platform, Graphics g ) {
            DrawingPane drawingPane = null;
            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new MoveVertexDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new MoveVertexDrawingPane( appContainer );
            } else {
                LOG.logWarning( "view platfroms other than Application and Applet are not supported yet" );
            }

            drawingPane.setGraphicContext( g );
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
    public static class MergeVerticesState extends UpdateFeatureState {

        /**
         * 
         * @param appContainer
         */
        public MergeVerticesState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature, QualifiedName geomProperty,
                                      List<Geometry> geometries )
                                throws Exception {
            double minx = ( (Point) geometries.get( 0 ) ).getX();
            double miny = ( (Point) geometries.get( 0 ) ).getY();
            double maxx = ( (Point) geometries.get( 1 ) ).getX();
            double maxy = ( (Point) geometries.get( 1 ) ).getY();
            if ( minx > maxx ) {
                double tmp = minx;
                minx = maxx;
                maxx = tmp;
            }
            if ( miny > maxy ) {
                double tmp = miny;
                miny = maxy;
                maxy = tmp;
            }

            Envelope mergeArea = GeometryFactory.createEnvelope( minx, miny, maxx, maxy,
                                                                 geometries.get( 0 ).getCoordinateSystem() );
            Point targetPoint = (Point) geometries.get( geometries.size() - 1 );
            return new MergeVerticesCommand( appContainer, feature, geomProperty, targetPoint, mergeArea );
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature )
                                throws Exception {
            throw new UnsupportedOperationException();
        }

        /**
         * Instantiates a DrawingPane returns it
         * 
         * @return the DrawingPane
         */
        @Override
        public DrawingPane createDrawingPane( String platform, Graphics g ) {
            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new MergeVerticesDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new MergeVerticesDrawingPane( appContainer );
            } else {
                LOG.logWarning( "view platfroms other than Application and Applet are not supported yet" );
            }

            drawingPane.setGraphicContext( g );
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
    public static class InsertVertexState extends UpdateFeatureState {

        /**
         * 
         * @param appContainer
         */
        public InsertVertexState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature, QualifiedName geomProperty,
                                      List<Geometry> geometries )
                                throws Exception {
            return new InsertVertexCommand( appContainer, feature, geomProperty, (Point) geometries.get( 0 ) );
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature )
                                throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public DrawingPane createDrawingPane( String platform, Graphics g ) {
            DrawingPane drawingPane = null;
            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new InsertVertexDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new InsertVertexDrawingPane( appContainer );
            } else {
                LOG.logWarning( "view platfroms other than Application and Applet are not supported yet" );
            }

            drawingPane.setGraphicContext( g );
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
    public static class MoveFeatureState extends UpdateFeatureState {

        /**
         * 
         * @param appContainer
         */
        public MoveFeatureState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature )
                                throws Exception {
            if ( substate != null ) {
                return ( (CreateFeatureState) substate ).createCommand( dataAccessAdapter, feature );
            }
            List<Geometry> geometries = new ArrayList<Geometry>( 2 );
            geometries.add( GeometryFactory.createPoint( 0, 0, null ) );
            geometries.add( GeometryFactory.createPoint( 0, 0, null ) );
            return createCommand( dataAccessAdapter, feature, null, geometries );
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature, QualifiedName geomProperty,
                                      List<Geometry> geometries )
                                throws Exception {
            if ( substate != null ) {
                return ( (CreateFeatureState) substate ).createCommand( dataAccessAdapter, feature );
            }
            return new MoveFeatureCommand( feature, geomProperty, geometries );
        }

        /**
         * Instantiates a DrawingPane if necessary and returns it. The same DrawingPane instance is used for all user
         * interactions for a given CreatePolygonFeatureState instance. The code assumes that the platform parameter is
         * the same in all calls of createDrawingPane during the lifetime of the {@link MoveFeatureState} instance.
         * 
         * @return the DrawingPane
         */
        public DrawingPane createDrawingPane( String platform, Graphics g ) {
            DrawingPane drawingPane = null;

            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new MoveFeatureDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new MoveFeatureDrawingPane( appContainer );
            } else {
                LOG.logWarning( "view platfroms other than Application and Applet are not supported yet" );
            }

            drawingPane.setGraphicContext( g );
            return drawingPane;
        }

    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version. $Revision$, $Date$
     */
    public static class UpdateAlphaNumericPropertiesStateState extends UpdateFeatureState {

        /**
         * 
         * @param appContainer
         */
        public UpdateAlphaNumericPropertiesStateState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature )
                                throws Exception {
            if ( substate != null ) {
                return ( (CreateFeatureState) substate ).createCommand( dataAccessAdapter, feature );
            }
            return createCommand( dataAccessAdapter, feature, null, null );
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature, QualifiedName geomProperty,
                                      List<Geometry> geometries )
                                throws Exception {
            if ( substate != null ) {
                return ( (CreateFeatureState) substate ).createCommand( dataAccessAdapter, feature );
            }
            return new UpdateFeatureCommand( dataAccessAdapter, feature );
        }

    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version. $Revision$, $Date$
     */
    public static class SplitFeatureState extends UpdateFeatureState {

        /**
         * 
         * @param appContainer
         */
        public SplitFeatureState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
            // TODO Auto-generated constructor stub
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature )
                                throws Exception {
            if ( substate != null ) {
                return ( (UpdateFeatureState) substate ).createCommand( dataAccessAdapter, feature );
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature, QualifiedName geomProperty,
                                      List<Geometry> geometries )
                                throws Exception {
            if ( substate != null ) {
                return ( (UpdateFeatureState) substate ).createCommand( dataAccessAdapter, feature );
            }
            if ( feature instanceof FeatureCollection ) {
                feature = ( (FeatureCollection) feature ).getFeature( 0 );
            }
            return new SplitFeatureCommand( dataAccessAdapter, feature, null, (Curve) geometries.get( 0 ) );
        }

        /**
         * Instantiates a DrawingPane returns it
         * 
         * @return the DrawingPane
         */
        @Override
        public DrawingPane createDrawingPane( String platform, Graphics g ) {
            DrawingPane drawingPane = null;
            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreateLinestringDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreateLinestringDrawingPane( appContainer );
            } else {
                LOG.logWarning( "view platfroms other than Application and Applet are not supported yet" );
            }

            drawingPane.setGraphicContext( g );
            return drawingPane;
        }

    }

    /**
     * 
     * TODO add class documentation here
     * 
     * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    public static class JoinCurvesState extends UpdateFeatureState {

        /**
         * @param appContainer
         */
        public JoinCurvesState( ApplicationContainer<?> appContainer ) {
            super( appContainer );
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature )
                                throws Exception {
            if ( substate != null ) {
                return ( (UpdateFeatureState) substate ).createCommand( dataAccessAdapter, feature );
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public Command createCommand( DataAccessAdapter dataAccessAdapter, Feature feature, QualifiedName geomProperty,
                                      List<Geometry> geometries )
                                throws Exception {
            if ( substate != null ) {
                return ( (UpdateFeatureState) substate ).createCommand( dataAccessAdapter, feature );
            }
            throw new UnsupportedOperationException();
        }

        /**
         * Instantiates a DrawingPane returns it
         * 
         * @return the DrawingPane
         */
        @Override
        public DrawingPane createDrawingPane( String platform, Graphics g ) {
            DrawingPane drawingPane = null;
            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreateLinestringDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new CreateLinestringDrawingPane( appContainer );
            } else {
                LOG.logWarning( "view platfroms other than Application and Applet are not supported yet" );
            }
            drawingPane.setGraphicContext( g );
            return drawingPane;
        }
    }
}
