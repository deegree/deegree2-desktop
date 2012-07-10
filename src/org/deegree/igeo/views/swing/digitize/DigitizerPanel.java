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

package org.deegree.igeo.views.swing.digitize;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.GeometryUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.ChangeListener;
import org.deegree.igeo.ValueChangedEvent;
import org.deegree.igeo.commands.model.ZoomCommand;
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.DefaultMapModule;
import org.deegree.igeo.modules.DigitizerModule;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.settings.DigitizingLinesOpt;
import org.deegree.igeo.state.mapstate.EditState;
import org.deegree.igeo.state.mapstate.MapTool;
import org.deegree.igeo.state.mapstate.SelectState;
import org.deegree.igeo.state.mapstate.ToolState;
import org.deegree.igeo.state.mapstate.EditState.CreatePointFeatureState;
import org.deegree.igeo.state.mapstate.SelectState.RectangleSelectState;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.DrawingPane;
import org.deegree.igeo.views.GeoDrawingPane;
import org.deegree.igeo.views.Snapper;
import org.deegree.igeo.views.swing.Footer;
import org.deegree.igeo.views.swing.drawingpanes.CreateLinestringDrawingPane;
import org.deegree.igeo.views.swing.drawingpanes.CreatePointDrawingPane;
import org.deegree.igeo.views.swing.drawingpanes.CreatePolygonDrawingPane;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.EnvelopeImpl;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiPoint;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Ring;
import org.deegree.model.spatialschema.Surface;
import org.deegree.model.spatialschema.SurfacePatch;

/**
 * Panl that will be used for drawing all digitizing operations like adding new geometries, moving vertices or selecting
 * a geometry for removing. Because in deegree2 it is not so easy to draw specific features/geometries of a layers with
 * a different style than the others this class implements several drawing methods for selected and newly digitized
 * geometries. In future implementation this shall be removed to a central rendering module/engine.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class DigitizerPanel extends JPanel {

    private static final long serialVersionUID = 6043866783826397896L;

    private static final ILogger LOG = LoggerFactory.getLogger( DigitizerPanel.class );

    private static BasicStroke stroke = new BasicStroke( 2 );

    private static BasicStroke simpleStroke = new BasicStroke( 1 );

    protected DrawingPane drawingPane;

    private IModule<Container> mapModule;

    private DigitizerModule<?> ownerModule;

    private MapModel mapModel;

    private FeatureCollection selectedFC;

    protected List<Geometry> geomList = null;

    private MapTool<?> mapTool;

    private boolean shallDrawSnapTargetVertices = false;

    private BufferedImage buffer;

    private DPKeyListener keyListener = new DPKeyListener();

    private DPMouseMotionListener mouseMotionListener = new DPMouseMotionListener();

    private DPMouseListener mouseListener = new DPMouseListener();

    private JLabel footerLabel;

    private Footer footer;

    /**
     * 
     * @param ownerModule
     * @param mapModule
     * @param assignedMapModel
     */
    public DigitizerPanel( DigitizerModule<Container> ownerModule, IModule<Container> mapModule,
                           MapModel assignedMapModel ) {
        this.ownerModule = ownerModule;
        this.mapModule = mapModule;
        this.mapModel = assignedMapModel;
        this.mapModel.addChangeListener( new DPChangeListener() );
        setDoubleBuffered( false );
        geomList = new ArrayList<Geometry>();
        this.mapTool = ( (DefaultMapModule<Container>) this.mapModule ).getMapTool();
        setBackground( new Color( 255, 255, 255, 0 ) );
        setBounds( 0, 0, assignedMapModel.getTargetDevice().getPixelWidth(),
                   assignedMapModel.getTargetDevice().getPixelHeight() );
        setVisible( true );
        footer = (Footer) ownerModule.getApplicationContainer().getFooter();
        if ( footer != null ) {
            footerLabel = new JLabel();
            footerLabel.setPreferredSize( new Dimension( 300, 25 ) );
            footerLabel.setBorder( BorderFactory.createMatteBorder( 0, 2, 0, 0, Color.BLACK ) );
        }
    }

    /**
     * resets a panel into its initial state and performs a repainting
     * 
     */
    public void resetAll() {
        resetGeometries();
        resetDrawingPane();
        resetSelectedFeatures();
        invalidate();
        repaint();
        if ( getParent() != null ) {
            getParent().repaint();
        }
    }

    /**
     * resets list of geometries
     * 
     */
    public void resetGeometries() {
        geomList.clear();
    }

    /**
     * resets list of selected features
     * 
     */
    public void resetSelectedFeatures() {
        selectedFC = null;
    }

    /**
     * resets the current drawing pane by setting it th <code>null</code>
     * 
     */
    public void resetDrawingPane() {
        drawingPane = null;
    }

    /**
     * 
     * @return list of digitized objects
     */
    public List<Geometry> getDigitizedPoints() {
        return geomList;
    }

    @Override
    public void repaint() {
        // do not invoke super.repaint()!!!!!
        if ( this.mapModel != null ) {
            int w = this.mapModel.getTargetDevice().getPixelWidth();
            int h = this.mapModel.getTargetDevice().getPixelHeight();
            if ( w > 0 && h > 0 ) {
                buffer = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB );
                Graphics g = buffer.getGraphics();
                if ( shallDrawSnapTargetVertices ) {
                    drawSnapTargetVertices( g );
                }
                g.dispose();
            }
        }
    }

    @Override
    public void paint( Graphics g ) {
        super.paint( g );
        if ( drawingPane != null ) {
            if ( drawingPane.isDrawing() ) {
                drawingPane.draw( drawingPane.getCurrent().x, drawingPane.getCurrent().y, g );
            }
            if ( mapTool.getState() instanceof EditState ) {
                drawDigitizedGeoms( g );
            }
        }
        g.drawImage( buffer, 0, 0, null );
    }

    /**
     * sets the features that are currently selected
     * 
     * @param fc
     */
    public void setSelectedFeatures( FeatureCollection fc ) {
        this.selectedFC = fc;
        invalidate();
        if ( getParent() != null ) {
            getParent().repaint();
        }
    }

    /**
     * 
     * @return currently selected features
     */
    public FeatureCollection getSelectedFeatures() {
        return this.selectedFC;
    }

    /**
     * 
     * @param width
     *            in map units
     */
    @SuppressWarnings("unchecked")
    private void setLineWidth() {

        ApplicationContainer<Container> appCont = (ApplicationContainer<Container>) ownerModule.getApplicationContainer();
        DigitizingLinesOpt dlo = appCont.getSettings().getDigitizingLinesOptions();
        String s = dlo.getUOM();
        float width = (float) dlo.getValue();
        if ( !"pixel".equalsIgnoreCase( s ) ) {
            // calculate pixel line width from geogr. line width
            float w = mapModel.getTargetDevice().getPixelWidth();
            float d = w / (float) mapModel.getEnvelope().getWidth();
            width = d * width;
        }
        stroke = new BasicStroke( width );
        if ( drawingPane != null && drawingPane instanceof CreateLinestringDrawingPane ) {
            // just if current drawing pane is an instance of CreateLinestringDrawingPane
            // the width of digitizing line can be set
            // Notice that this method receives width measured in units of the CRS of the
            // current map while the setLineWidth method of CreateLinestringDrawingPane
            // expects width in pixel units
            ( (CreateLinestringDrawingPane) drawingPane ).setLineWidth( width );
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////
    // the following drawing methods are a kind of workaround because in deegree2
    // it is not so easy to draw specific features/geometries of a layers with
    // a different style than the others
    // ///////////////////////////////////////////////////////////////////////////////

    private void drawDigitizedGeoms( Graphics g ) {

        if ( drawingPane instanceof CreatePointDrawingPane ) {
            GeoTransform gt = mapModel.getToTargetDeviceTransformation();
            for ( int i = 0; i < geomList.size(); i++ ) {
                Point p = (Point) geomList.get( i );
                drawPoint( g, gt, p );
            }
        } else if ( drawingPane instanceof CreateLinestringDrawingPane ) {
            for ( int i = 0; i < geomList.size(); i++ ) {
                GeneralPath path = createPath( (Curve) geomList.get( i ) );
                ( (Graphics2D) g ).setStroke( stroke );
                g.setColor( new Color( 1f, 0f, 0f ) );
                ( (Graphics2D) g ).draw( path );
            }
        } else if ( drawingPane instanceof CreatePolygonDrawingPane ) {
            for ( int i = 0; i < geomList.size(); i++ ) {
                GeneralPath path = createPath( (Surface) geomList.get( i ) );
                g.setColor( new Color( 1f, 1f, 1f, 0.6f ) );
                ( (Graphics2D) g ).fill( path );
                g.setColor( new Color( 1f, 0f, 0f ) );
                ( (Graphics2D) g ).draw( path );
            }
        }
    }

    private void drawSnapTargetVertices( Graphics g ) {
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        List<Layer> layers = mapModel.getLayersSelectedForAction( MapModel.SELECTION_EDITING );
        for ( Layer layer : layers ) {
            List<DataAccessAdapter> tmp = layer.getDataAccess();
            for ( DataAccessAdapter adapter : tmp ) {
                if ( adapter instanceof FeatureAdapter ) {
                    FeatureCollection fc = null;
                    try {
                        fc = ( (FeatureAdapter) adapter ).getFeatureCollection( mapModel.getEnvelope() );
                    } catch ( FilterEvaluationException e ) {
                        e.printStackTrace();
                    }
                    Iterator<Feature> iterator = fc.iterator();
                    while ( iterator.hasNext() ) {
                        drawFeature( g, gt, iterator.next() );
                    }
                }
            }
        }
    }

    private void drawFeature( Graphics g, GeoTransform gt, Feature feature ) {
        Geometry[] geometries = feature.getGeometryPropertyValues();
        for ( Geometry geometry : geometries ) {
            // save current stroke
            Stroke temp = ( (Graphics2D) g ).getStroke();
            if ( geometry instanceof Point ) {
                Point p = (Point) geometry;
                drawPoint( g, gt, p );
            } else if ( geometry instanceof MultiPoint ) {
                Point[] points = ( (MultiPoint) geometry ).getAllPoints();
                for ( Point point : points ) {
                    drawPoint( g, gt, point );
                }
            } else if ( geometry instanceof Curve ) {
                drawCurve( g, (Curve) geometry, simpleStroke );
            } else if ( geometry instanceof MultiCurve ) {
                Curve[] curves = ( (MultiCurve) geometry ).getAllCurves();
                for ( Curve curve : curves ) {
                    drawCurve( g, curve, simpleStroke );
                }
            } else if ( geometry instanceof Surface ) {
                drawSurface( g, (Surface) geometry );
            } else if ( geometry instanceof MultiSurface ) {
                Surface[] surfaces = ( (MultiSurface) geometry ).getAllSurfaces();
                for ( Surface surface : surfaces ) {
                    drawSurface( g, surface );
                }
            }
            // restore stroke
            ( (Graphics2D) g ).setStroke( temp );
        }
    }

    private void drawPoint( Graphics g, GeoTransform gt, Point p ) {
        int xx = (int) Math.round( gt.getDestX( p.getX() ) );
        int yy = (int) Math.round( gt.getDestY( p.getY() ) );
        int radius = 3;
        g.setColor( new Color( 1f, 1f, 1f, 0.6f ) );
        g.fillOval( xx - radius, yy - radius, 2 * radius, 2 * radius );
        g.setColor( new Color( 1f, 0f, 0f ) );
        g.drawOval( xx - radius, yy - radius, 2 * radius, 2 * radius );
    }

    private void drawSurface( Graphics g, Surface surface ) {
        GeneralPath path = createPath( surface );
        g.setColor( new Color( 1f, 1f, 1f, 0.6f ) );
        ( (Graphics2D) g ).fill( path );
        g.setColor( new Color( 0f, 0f, 1f ) );
        ( (Graphics2D) g ).draw( path );
        drawVertices( g, path );
    }

    private void drawCurve( Graphics g, Curve curve, Stroke stroke ) {
        GeneralPath path = createPath( curve );
        ( (Graphics2D) g ).setStroke( stroke );
        g.setColor( new Color( 1f, 0f, 0f ) );
        ( (Graphics2D) g ).draw( path );
        drawVertices( g, path );
    }

    private void drawVertices( Graphics g, GeneralPath path ) {
        PathIterator pi = path.getPathIterator( null );
        float[] coords = new float[2];

        while ( !pi.isDone() ) {
            pi.currentSegment( coords );
            pi.next();
            g.setColor( new Color( 1f, 1f, 0f ) );
            g.fillOval( (int) coords[0] - 3, (int) coords[1] - 3, 7, 7 );
            g.setColor( new Color( 0f, 0f, 0f ) );
            g.drawOval( (int) coords[0] - 3, (int) coords[1] - 3, 7, 7 );
        }
    }

    private GeneralPath createPath( Curve geom ) {
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        GeneralPath path = new GeneralPath();
        Position[] pos = null;
        try {
            pos = geom.getAsLineString().getPositions();
        } catch ( GeometryException e ) {
            // TODO
            LOG.logError( e.getMessage(), e );
            return path;
        }
        float xx = (float) gt.getDestX( pos[0].getX() );
        float yy = (float) gt.getDestY( pos[0].getY() );
        path.moveTo( xx, yy );
        for ( int i = 1; i < pos.length; i++ ) {
            float xx_ = (float) gt.getDestX( pos[i].getX() );
            float yy_ = (float) gt.getDestY( pos[i].getY() );
            if ( GeometryUtils.distance( xx, yy, xx_, yy_ ) > 2 || i == pos.length - 1 ) {
                path.lineTo( xx_, yy_ );
                xx = xx_;
                yy = yy_;
            }
        }

        return path;
    }

    private GeneralPath createPath( Surface surface ) {

        GeneralPath path = new GeneralPath();

        SurfacePatch patch = null;
        try {
            patch = surface.getSurfacePatchAt( 0 );
        } catch ( GeometryException e ) {
            LOG.logError( e.getMessage(), e );
        }
        if ( patch == null ) {
            return null;
        }
        appendRingToPath( path, patch.getExteriorRing() );
        Position[][] inner = patch.getInteriorRings();
        if ( inner != null ) {
            for ( int i = 0; i < inner.length; i++ ) {
                appendRingToPath( path, inner[i] );
            }
        }

        return path;
    }

    private void appendRingToPath( GeneralPath path, Position[] ring ) {
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        if ( ring.length == 0 ) {
            return;
        }

        Position p = gt.getDestPoint( ring[0] );
        float xx = (float) p.getX();
        float yy = (float) p.getY();
        path.moveTo( xx, yy );
        for ( int i = 1; i < ring.length; i++ ) {
            p = gt.getDestPoint( ring[i] );
            float xx_ = (float) p.getX();
            float yy_ = (float) p.getY();
            if ( GeometryUtils.distance( xx, yy, xx_, yy_ ) > 2 || i == ring.length - 1 ) {
                path.lineTo( xx_, yy_ );
                xx = xx_;
                yy = yy_;
            }
        }

    }

    /**
     * 
     * @return KeyListener assigned to a DigitizerPanel
     */
    public KeyListener getKeyListener() {
        return keyListener;
    }

    /**
     * 
     * @return MouseMotionListener assigned to a DigitizerPanel
     */
    public MouseMotionListener getMouseMotionListener() {
        return mouseMotionListener;
    }

    /**
     * 
     * @return MouseMotionListener assigned to a DigitizerPanel
     */
    public MouseListener getMouseListener() {
        return mouseListener;
    }

    // ////////////////////////////////////////////////////////////////////////////////
    // inner classes
    // ////////////////////////////////////////////////////////////////////////////////

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    private class DPMouseListener extends MouseAdapter {

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
         */
        public void mousePressed( MouseEvent event ) {
            if ( event.isPopupTrigger() ) {
                return;
            }
            ToolState state = mapTool.getState();
            if ( state instanceof EditState || state instanceof SelectState ) {                
                if ( event.getClickCount() == 2 && drawingPane != null && drawingPane.isDrawing() ) {
                    drawingPane.undrawLastPoint();
                    finishDigitizingAction( event );
                } else {
                    getParent();
                    Graphics g = getGraphics();
                    if ( drawingPane == null ) {
                        // first click on digitizer pane (map) after selecting a digitizer function
                        initDrawingPane( state, g );
                    }
                    if ( drawingPane != null ) {
                        // depending on drawing state: start or stop drawing. Stop drawing
                        // means setting a new vertex for a line of polygon
                        if ( !drawingPane.isDrawing() ) {
                            ( (GeoDrawingPane) drawingPane ).setMapModel( mapModel );
                            ( (GeoDrawingPane) drawingPane ).setFeatureCollection( selectedFC );
                            // initial mouse click to start digitizing a new Object
                            drawingPane.startDrawing( event.getX(), event.getY() );
                        } else {
                            // draw line or polygon boundary
                            drawingPane.stopDrawing( event.getX(), event.getY() );                            
                        }
                        mapModule.update();
                    }
                    state.mousePressed( event );
                    if ( event.getClickCount() == 1 && drawingPane != null ) {
                        // handle digitizer functions that requires just one mouse click or
                        // creates an inner ring of a polygon
                        if ( state instanceof CreatePointFeatureState ) {
                            // create point
                            finishDigitizingAction( event );
                        } 
                    }
                }
            }

        }

        private void initDrawingPane( ToolState state, Graphics g ) {
            drawingPane = state.createDrawingPane( mapModule.getApplicationContainer().getViewPlatform(), g );
            if ( drawingPane != null ) {
                drawingPane.setSnapper( new Snapper( ownerModule.getApplicationContainer() ) );
            }
            setLineWidth();
            if ( footer != null ) {
                footer.add( footerLabel );
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void mouseExited( MouseEvent e ) {
            if ( mapTool.getState() instanceof EditState && e.getModifiers() == MouseEvent.SHIFT_MASK ) {
                int x = e.getX();
                int y = e.getY();
                int limit = 2;
                int h = mapModel.getTargetDevice().getPixelHeight() - limit;
                int w = mapModel.getTargetDevice().getPixelWidth() - limit;
                ApplicationContainer<Container> appContainer = (ApplicationContainer<Container>) mapModel.getApplicationContainer();
                float pan = 0.1f;
                EnvelopeImpl tmp = (EnvelopeImpl) mapModel.getEnvelope();
                Envelope env = (Envelope) tmp.clone();
                if ( x < limit ) {
                    env = env.translate( env.getWidth() * -1 * pan, 0 );
                } else if ( y < limit ) {
                    env = env.translate( 0, env.getHeight() * pan );
                } else if ( x > w ) {
                    env = env.translate( env.getWidth() * pan, 0 );
                } else if ( y > h ) {
                    env = env.translate( 0, env.getHeight() * -1 * pan );
                }
                ZoomCommand zc = new ZoomCommand( mapModel );
                zc.setZoomBox( env, mapModel.getTargetDevice().getPixelWidth(),
                               mapModel.getTargetDevice().getPixelHeight() );
                try {
                    appContainer.getCommandProcessor().executeSychronously( zc, true );
                } catch ( Exception ex ) {
                    LOG.logError( ex.getMessage(), ex );
                    DialogFactory.openErrorDialog( appContainer.getViewPlatform(), DigitizerPanel.this,
                                                   Messages.getMessage( getLocale(), "$MD11253" ),
                                                   Messages.getMessage( getLocale(), "$MD11252", env ), ex );
                }
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
         */
        public void mouseReleased( MouseEvent event ) {

            if ( !event.isPopupTrigger() ) {
                if ( mapTool.getState() instanceof RectangleSelectState
                     || mapTool.getState() instanceof EditState.MoveFeatureState
                     || mapTool.getState() instanceof EditState.MoveVertexState
                     || mapTool.getState() instanceof EditState.InsertVertexState
                     || mapTool.getState() instanceof EditState.DeleteVertexState
                     || mapTool.getState() instanceof EditState.CreateRectangleFeatureState
                     || mapTool.getState() instanceof EditState.CreateCircleFeatureState ) {
                    if ( drawingPane != null && drawingPane.isDrawing() ) {
                        finishDigitizingAction( event );
                    }
                } else if ( mapTool.getState() instanceof EditState.CreateFeatureState ) {
                    if ( drawingPane != null && drawingPane.isDrawing() ) {
                        // pressing shift key while digitizing removes least drawn
                        // point from the panel
                        if ( event.getModifiers() == KeyEvent.SHIFT_MASK ) {
                            drawingPane.undrawLastPoint();
                        }
                        mapModule.update();
                    }
                } else if ( mapTool.getState() instanceof EditState.MergeVerticesState ) {
                    if ( drawingPane.isDrawingStopped() ) {
                        finishDigitizingAction( event );
                    } else {
                        drawingPane.stopDrawing( event.getX(), event.getY() );
                    }
                }
            }
        }

        private void finishDigitizingAction( MouseEvent event ) {
            if ( footer != null ) {
                footer.remove( footerLabel );
                footer.repaint();
            }
            if ( drawingPane != null && drawingPane.isDrawing() ) {

                // final mouse click to stop digitizing a new Object
                drawingPane.stopDrawing( event.getX(), event.getY() );
                List<Point> points = ( (GeoDrawingPane) drawingPane ).getDrawObjectsAsGeoPoints();
                try {
                    geomList = createGeometry( points );
                } catch ( Exception e ) {
                    DialogFactory.openErrorDialog( "Application", DigitizerPanel.this,
                                                   Messages.getMessage( getLocale(), "$MD11123",
                                                                        StringTools.stackTraceToString( e ) ),
                                                   Messages.getMessage( getLocale(), "$MD11124" ), e );
                    LOG.logError( e.getMessage(), e );
                    mapTool.resetState();
                    ownerModule.resetFunctionSelect();
                    return;
                }

                drawingPane.finishDrawing();

                // repaint map with updated geometry state
                mapModule.update();
                // inform owner module about finished action
                ownerModule.mouseActionFinished( geomList, event.getModifiers() );
            }
        }

        private List<Geometry> createGeometry( List<Point> points )
                                throws GeometryException {
            if ( mapTool.getState() instanceof EditState.CreatePointFeatureState ) {
                geomList.add( points.get( 0 ) );
            } else if ( mapTool.getState() instanceof EditState.CreateCurveFeatureState ) {
                Position[] pos = pointListToPositions( points );
                CoordinateSystem crs = points.get( 0 ).getCoordinateSystem();
                geomList.add( GeometryFactory.createCurve( pos, crs ) );
            } else if ( mapTool.getState() instanceof EditState.CreatePolygonFeatureState ) {
                createSurface( points );
            } else if ( mapTool.getState() instanceof EditState.SplitFeatureState ) {
                Position[] pos = pointListToPositions( points );
                CoordinateSystem crs = points.get( 0 ).getCoordinateSystem();
                geomList.clear();
                geomList.add( GeometryFactory.createCurve( pos, crs ) );
            } else {
                geomList.addAll( points );
            }
            return geomList;
        }

        private void createSurface( List<Point> points )
                                throws GeometryException {
            // close polygon
            Point p = GeometryFactory.createPoint( points.get( 0 ).getX(), points.get( 0 ).getY(),
                                                   points.get( 0 ).getCoordinateSystem() );
            points.add( p );
            Position[] pos = pointListToPositions( points );
            CoordinateSystem crs = points.get( 0 ).getCoordinateSystem();
            Surface surface = GeometryFactory.createSurface( pos, null, null, crs );

            for ( int i = 0; i < geomList.size(); i++ ) {
                Ring ring = ( (Surface) geomList.get( i ) ).getSurfaceBoundary().getExteriorRing();
                Surface t = GeometryFactory.createSurface( ring.getPositions(), null, null,
                                                           surface.getCoordinateSystem() );
                if ( t.contains( surface ) ) {
                    // current surface is an inner ring of the i'th surface that has
                    // already been digitized
                    if ( GeometryUtils.isClockwise( surface ) ) {
                        // an inner ring must have a counter clock wise position order
                        surface = GeometryUtils.invertOrder( surface );
                    }
                    Position[] inner = surface.getSurfaceBoundary().getExteriorRing().getPositions();
                    Surface tmp = (Surface) geomList.get( i );
                    Position[] ext = tmp.getSurfaceBoundary().getExteriorRing().getPositions();
                    Ring[] in = tmp.getSurfaceBoundary().getInteriorRings();
                    Position[][] interior = null;
                    if ( in == null || in.length == 0 ) {
                        interior = new Position[1][];
                        interior[0] = inner;
                    } else {
                        interior = new Position[in.length + 1][];
                        for ( int j = 0; j < in.length; j++ ) {
                            interior[j] = in[j].getPositions();
                        }
                        interior[interior.length - 1] = inner;
                    }
                    geomList.set( i, GeometryFactory.createSurface( ext, interior, null, tmp.getCoordinateSystem() ) );
                    return;
                }
            }
            // current surface is not an inner ring!
            geomList.add( GeometryUtils.ensureClockwise( surface ) );
        }

        /**
         * 
         * @param pointList
         * @return array of {@link Position}s
         */
        private Position[] pointListToPositions( List<Point> pointList ) {
            List<Position> pos = new ArrayList<Position>( pointList.size() );
            Position p = null;
            for ( int i = 0; i < pointList.size(); i++ ) {
                p = pointList.get( i ).getPosition();
                if ( i == 0 ) {
                    pos.add( p );
                } else {
                    if ( !pos.get( pos.size() - 1 ).equals( p ) ) {
                        pos.add( p );
                    }
                }
            }
            return pos.toArray( new Position[pos.size()] );
        }
    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    private class DPMouseMotionListener implements MouseMotionListener {

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
         */
        public void mouseDragged( MouseEvent event ) {
            mapTool.getState().mouseDragged( event );
            if ( mapTool.getState() instanceof EditState.CreateRectangleFeatureState
                 || mapTool.getState() instanceof EditState.CreateCircleFeatureState
                 || mapTool.getState() instanceof SelectState.RectangleSelectState
                 || mapTool.getState() instanceof EditState.MoveFeatureState
                 || mapTool.getState() instanceof EditState.MoveVertexState
                 || mapTool.getState() instanceof EditState.InsertVertexState
                 || mapTool.getState() instanceof EditState.MergeVerticesState ) {
                if ( drawingPane != null && drawingPane.isDrawing() ) {
                    // just RectangleSelectState uses mouse dragging
                    drawingPane.draw( event.getX(), event.getY() );
                    mapModule.update();
                }
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
         */
        public void mouseMoved( MouseEvent event ) {
            if ( drawingPane != null && drawingPane.isDrawing() ) {
                mapTool.getState().mouseMoved( event );
                if ( mapTool.getState() instanceof EditState.CreateFeatureState
                     || ( mapTool.getState() instanceof EditState.MergeVerticesState && drawingPane.isDrawingStopped() )
                     || mapTool.getState() instanceof EditState.SplitFeatureState
                     || mapTool.getState() instanceof EditState.JoinCurvesState ) {
                    drawingPane.draw( event.getX(), event.getY() );
                    mapModule.update();
                }
            }
        }

    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    public class DPKeyListener extends KeyAdapter {

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        public void keyPressed( KeyEvent event ) {
            if ( event.getKeyCode() == KeyEvent.VK_M ) {
                shallDrawSnapTargetVertices = !shallDrawSnapTargetVertices;
                repaint();
                if ( getParent() != null ) {
                    getParent().repaint();
                }
            }
        }

    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    private class DPChangeListener implements ChangeListener {

        /*
         * (non-Javadoc)
         * 
         * @see org.deegree.igeo.ChangeListener#valueChanged(org.deegree.igeo.ValueChangedEvent)
         */
        public void valueChanged( ValueChangedEvent event ) {
            invalidate();
            repaint();
        }

    }

}
