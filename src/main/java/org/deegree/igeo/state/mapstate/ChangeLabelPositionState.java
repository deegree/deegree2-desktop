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
package org.deegree.igeo.state.mapstate;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.GeometryUtils;
import org.deegree.framework.util.Pair;
import org.deegree.framework.utils.MapTools;
import org.deegree.graphics.displayelements.ScaledFeature;
import org.deegree.graphics.sld.FeatureTypeStyle;
import org.deegree.graphics.sld.Font;
import org.deegree.graphics.sld.LabelPlacement;
import org.deegree.graphics.sld.ParameterValueType;
import org.deegree.graphics.sld.PointPlacement;
import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.graphics.sld.TextSymbolizer;
import org.deegree.graphics.sld.UserStyle;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.digitize.UpdateFeatureCommand;
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.DefinedStyle;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.NamedStyle;
import org.deegree.igeo.modules.DefaultMapModule;
import org.deegree.igeo.modules.EditStyleModule;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.DrawingPane;
import org.deegree.igeo.views.GeoDrawingPane;
import org.deegree.igeo.views.swing.style.AssignLabelToCurveDrawingPane;
import org.deegree.igeo.views.swing.style.ChangeLabelPositionDrawingPane;
import org.deegree.igeo.views.swing.style.EditFeatureStyleDialog;
import org.deegree.igeo.views.swing.style.EditFeatureStyleDrawingPane;
import org.deegree.igeo.views.swing.style.LabelSelectDrawingPane;
import org.deegree.io.datastore.PropertyPathResolvingException;
import org.deegree.kernel.Command;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.filterencoding.ArithmeticExpression;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.deegree.model.filterencoding.PropertyName;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ChangeLabelPositionState extends MapState {

    private static final ILogger LOG = LoggerFactory.getLogger( ChangeLabelPositionState.class );

    private double xLabel;

    private double yLabel;

    private int hLabel;

    private int wLabel;

    private double dx;

    private double dy;

    private double rotation;

    private String label;

    private Feature selectedFeature;

    private Feature origFeature;

    private EditStyleModule<Container> editStyleModule;

    /**
     * @param appContainer
     * @param parameter
     * @param invokingAction
     */
    @SuppressWarnings("unchecked")
    public ChangeLabelPositionState( ApplicationContainer<?> appContainer, HashMap<String, Object> parameter,
                                     String invokingAction ) {
        super( appContainer, parameter, invokingAction );
        List list = appContainer.getModules();
        for ( Object iModule : list ) {
            if ( iModule instanceof EditStyleModule ) {
                editStyleModule = (EditStyleModule<Container>) iModule;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.state.mapstate.MapState#createCommand(org.deegree.igeo.modules.IModule,
     * org.deegree.igeo.mapmodel.MapModel, org.deegree.igeo.mapmodel.Layer, org.deegree.model.spatialschema.Point[])
     */
    public Command createCommand( IModule<?> module, MapModel mapModel, Layer layer, Point... points ) {
        Command cmd = null;
        try {
            Feature feature = origFeature;

            NamedStyle nst = layer.getCurrentStyle();
            UserStyle us = (UserStyle) ( (DefinedStyle) nst ).getStyle();
            FeatureTypeStyle[] fts = us.getFeatureTypeStyles();
            // even if SLD allows having several TextSymbolizers within one style that can be
            // applied to one feature just one will be considered here!!!
            for ( FeatureTypeStyle featureTypeStyle : fts ) {
                Rule[] rules = featureTypeStyle.getRules();
                for ( Rule rule : rules ) {
                    Symbolizer[] symbolizer = rule.getSymbolizers();
                    for ( Symbolizer sym : symbolizer ) {
                        if ( sym instanceof TextSymbolizer ) {
                            LabelPlacement lp = ( (TextSymbolizer) sym ).getLabelPlacement();
                            ParameterValueType[] dis = lp.getPointPlacement().getDisplacement();
                            try {
                                if ( dis != null ) {
                                    PropertyName displacementXProperty = dis[0].getValueAsPropertyName();
                                    PropertyName displacementYProperty = dis[1].getValueAsPropertyName();
                                    // if just one of the properties is null style definition can not be handled with
                                    // igeodesktop
                                    if ( displacementXProperty == null && displacementYProperty == null
                                         && dis[0].getComponents() != null
                                         && dis[0].getComponents()[0] instanceof ArithmeticExpression ) {
                                        // displacement has been expressed as function -> uom of the map
                                        ArithmeticExpression ae = (ArithmeticExpression) dis[0].getComponents()[0];
                                        displacementXProperty = (PropertyName) ae.getFirstExpression();
                                        ae = (ArithmeticExpression) dis[1].getComponents()[0];
                                        displacementYProperty = (PropertyName) ae.getFirstExpression();
                                    }
                                    if ( displacementXProperty != null && displacementYProperty != null ) {
                                        feature.getDefaultProperty( displacementXProperty.getValue() ).setValue( dx );
                                        feature.getDefaultProperty( displacementYProperty.getValue() ).setValue( dy );
                                    }
                                }
                            } catch ( Exception e ) {
                                // ignore
                                LOG.logWarning( "ignore", e );
                            }
                            PropertyName rotationProperty = lp.getPointPlacement().getRotationPropertyName();
                            if ( rotationProperty != null ) {
                                feature.getDefaultProperty( rotationProperty.getValue() ).setValue( rotation );
                            }
                            break;
                        }
                    }
                }
            }
            cmd = new UpdateFeatureCommand( layer.getDataAccess().get( 0 ), feature );
        } catch ( Exception e ) {
            LOG.logError( e );
            throw new RuntimeException( e );
        }
        return cmd;
    }

    public void setDisplacement( double dx, double dy ) {
        this.dx = dx;
        this.dy = dy;
    }

    public void setRotation( double rotation ) {
        this.rotation = rotation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.state.mapstate.ToolState#createDrawingPane(java.lang.String, java.awt.Graphics)
     */
    public DrawingPane createDrawingPane( String platform, Graphics g ) {
        if ( this.substate != null ) {
            return this.substate.createDrawingPane( platform, g );
        } else {
            return null;
        }
    }

    @Override
    public void mousePressed( MouseEvent event ) {
        MapModel mapModel = appContainer.getMapModel( null );
        List<Layer> layers = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
        // one layer must be selected otherwise draw won't be started
        if ( layers.size() == 0 ) {
            DialogFactory.openWarningDialog( appContainer.getViewPlatform(), event.getSource(),
                                             Messages.get( "$MD11723" ), Messages.get( "$MD11724" ) );
        } else {
            NamedStyle nst = layers.get( 0 ).getCurrentStyle();
            if ( !( nst instanceof DefinedStyle ) ) {
                DialogFactory.openWarningDialog( appContainer.getViewPlatform(), event.getSource(),
                                                 Messages.get( "$MD11725" ), Messages.get( "$MD11726" ) );
                return;
            }
            PropertyName rotationProperty = null;
            PropertyName displacementXProperty = null;
            PropertyName displacementYProperty = null;
            UserStyle us = (UserStyle) ( (DefinedStyle) nst ).getStyle();
            FeatureTypeStyle[] fts = us.getFeatureTypeStyles();
            // even if SLD allows having several TextSymbolizers within one style that can be
            // applied to one feature just one will be considered here!!!
            for ( FeatureTypeStyle featureTypeStyle : fts ) {
                Rule[] rules = featureTypeStyle.getRules();
                for ( Rule rule : rules ) {
                    Symbolizer[] symbolizer = rule.getSymbolizers();
                    for ( Symbolizer sym : symbolizer ) {
                        if ( sym instanceof TextSymbolizer ) {
                            LabelPlacement lp = ( (TextSymbolizer) sym ).getLabelPlacement();
                            ParameterValueType[] dis = lp.getPointPlacement().getDisplacement();
                            if ( dis != null ) {
                                displacementXProperty = dis[0].getValueAsPropertyName();
                                displacementYProperty = dis[1].getValueAsPropertyName();
                                // if just one of the properties is null style definition can not be handled with
                                // igeodesktop
                                if ( displacementXProperty == null && displacementYProperty == null
                                     && dis[0].getComponents() != null
                                     && dis[0].getComponents()[0] instanceof ArithmeticExpression ) {
                                    // displacement has been expressed as function -> uom of the map
                                    ArithmeticExpression ae = (ArithmeticExpression) dis[0].getComponents()[0];
                                    if ( ae.getFirstExpression() instanceof PropertyName ) {
                                        displacementXProperty = (PropertyName) ae.getFirstExpression();
                                        // inform EditFeatureStyleDialog that map units are used with current symbolizer
                                        EditFeatureStyleDialog.create( editStyleModule ).setUOM( true );
                                    } else {
                                        displacementXProperty = null;
                                    }
                                } else {
                                    // inform EditFeatureStyleDialog that pixels are used with current symbolizer
                                    EditFeatureStyleDialog.create( editStyleModule ).setUOM( false );
                                }
                            }
                            rotationProperty = lp.getPointPlacement().getRotationPropertyName();
                            break;
                        }
                    }
                }
            }
            if ( rotationProperty == null && displacementXProperty == null ) {
                // at least one attribute - rotation or displacement - must defined by a feature property
                DialogFactory.openWarningDialog( appContainer.getViewPlatform(),
                                                 EditFeatureStyleDialog.create( editStyleModule ),
                                                 Messages.get( "$MD11727" ), Messages.get( "$MD11728" ) );
                return;
            }
            EditFeatureStyleDialog.create( editStyleModule ).setRotationEnabled( rotationProperty != null );
            EditFeatureStyleDialog.create( editStyleModule ).setDisplacementEnabled( displacementXProperty != null );

            if ( drawingPane != null ) {
                drawingPane.startDrawing( event.getX(), event.getY() );
            }
        }
    }

    protected double[] getLastPoint() {
        MapModel mapModel = appContainer.getMapModel( null );
        int x = drawingPane.getCurrent().x;
        int y = drawingPane.getCurrent().y;
        GeoTransform gt = mapModel.getToTargetDeviceTransformation();
        double dx = gt.getSourceX( x );
        double dy = gt.getSourceY( y );
        return new double[] { dx, dy };
    }

    /**
     * 
     * @param feature
     */
    protected void setSelectedFeature( Feature feature ) {
        MapModel mapModel = appContainer.getMapModel( null );
        double scale = mapModel.getScaleDenominator();
        this.origFeature = feature;
        this.selectedFeature = new ScaledFeature( feature, scale * 0.00028 );

        Layer layer = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION ).get( 0 );

        // first find String to be rendered
        UserStyle style = (UserStyle) layer.getCurrentStyle().getStyle();
        FeatureTypeStyle[] fts = style.getFeatureTypeStyles();
        TextSymbolizer textSymbolizer = null;
        // even if SLD allows having several TextSymbolizers within one style that can be
        // applied to one feature just one will be considered here!!!
        for ( FeatureTypeStyle featureTypeStyle : fts ) {
            Rule[] rules = featureTypeStyle.getRules();
            for ( Rule rule : rules ) {
                Symbolizer[] symbolizer = rule.getSymbolizers();
                for ( Symbolizer sym : symbolizer ) {
                    if ( sym instanceof TextSymbolizer ) {
                        try {
                            textSymbolizer = (TextSymbolizer) sym;
                            extractLabel( feature, textSymbolizer );
                        } catch ( PropertyPathResolvingException e ) {
                            LOG.logError( e );
                            return;
                        }
                    }
                }
            }
        }

        // than get informations of the font to be used, to get the pixel size of the rendered String
        try {
            calculateStringPixelSize( textSymbolizer );
        } catch ( FilterEvaluationException e ) {
            LOG.logError( e );
            return;
        }

        // than consider anchor point and displacement
        LabelPlacement lp = textSymbolizer.getLabelPlacement();
        if ( lp.getLinePlacement() != null ) {
            DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11729" ),
                                             Messages.get( "$MD11730" ) );
            return;
        }
        PointPlacement pp = lp.getPointPlacement();
        double[] anchorPoint = null;
        double[] displace = null;
        try {
            anchorPoint = pp.getAnchorPoint( selectedFeature );
            displace = pp.getDisplacement( selectedFeature );
            dx = displace[0];
            dy = displace[1];
        } catch ( FilterEvaluationException e ) {
            LOG.logError( e );
            return;
        }
        // get rotation
        try {
            rotation = pp.getRotation( selectedFeature );
        } catch ( FilterEvaluationException e ) {
            LOG.logError( e );
            return;
        }
        // than get geometry where to be rendered
        Geometry geom = selectedFeature.getDefaultGeometryPropertyValue();
        Point point = geom.getCentroid();
        xLabel = mapModel.getToTargetDeviceTransformation().getDestX( point.getX() );
        yLabel = mapModel.getToTargetDeviceTransformation().getDestY( point.getY() );

        xLabel += ( wLabel * anchorPoint[0] + 5 );
        yLabel += ( hLabel * anchorPoint[1] + hLabel / 2f );

        ( (EditFeatureStyleDrawingPane) drawingPane ).setStringEnvelope( xLabel, yLabel, wLabel, hLabel );
        ( (EditFeatureStyleDrawingPane) drawingPane ).setDisplacement( dx, dy );
        ( (EditFeatureStyleDrawingPane) drawingPane ).setRotation( rotation );

        EditFeatureStyleDialog.create( (EditStyleModule<Container>) editStyleModule ).setDisplacement( dx, dy );
        EditFeatureStyleDialog.create( (EditStyleModule<Container>) editStyleModule ).setArc( rotation );
        EditFeatureStyleDialog.create( (EditStyleModule<Container>) editStyleModule ).setDrawingPane(
                                                                                                      (EditFeatureStyleDrawingPane) drawingPane );

    }

    private void extractLabel( Feature feature, TextSymbolizer symbolizer )
                            throws PropertyPathResolvingException {
        ParameterValueType pvt = symbolizer.getLabel();
        PropertyName pn = pvt.getValueAsPropertyName();
        FeatureProperty fp = null;
        fp = feature.getDefaultProperty( pn.getValue() );

        if ( fp != null && fp.getValue() != null ) {
            label = fp.getValue().toString();
        }
    }

    private void calculateStringPixelSize( TextSymbolizer symbolizer )
                            throws FilterEvaluationException {
        Font symbolizerFont = symbolizer.getFont();
        int ftStyle = java.awt.Font.PLAIN;

        if ( Font.STYLE_ITALIC == symbolizerFont.getStyle( selectedFeature ) ) {
            ftStyle = java.awt.Font.ITALIC;
        }

        if ( Font.WEIGHT_BOLD == symbolizerFont.getWeight( selectedFeature ) ) {
            ftStyle = ftStyle + java.awt.Font.BOLD;
        }

        BufferedImage biToGetSize = new BufferedImage( 7, 7, BufferedImage.TYPE_INT_RGB );
        Graphics2D gToGetSize = (Graphics2D) biToGetSize.getGraphics();

        int fontSize = (int) symbolizerFont.getSize( selectedFeature );

        java.awt.Font font = new java.awt.Font( symbolizerFont.getFamily( selectedFeature ), ftStyle, fontSize );

        GlyphVector vec = font.createGlyphVector( gToGetSize.getFontRenderContext(), label );
        wLabel = (int) vec.getPixelBounds( null, 0, 0 ).getWidth() + 8;
        hLabel = (int) vec.getPixelBounds( null, 0, 0 ).getHeight() + 8;
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
     * @throws Exception
     * 
     */
    public void setRotateState()
                            throws Exception {
        this.substate = new RotateState( appContainer );
    }

    /**
     * @throws Exception
     * 
     */
    public void setDisplaceState()
                            throws Exception {
        this.substate = new DisplaceState( appContainer );
    }

    /**
     * @throws Exception
     * 
     */
    public void setAssignToCurveState()
                            throws Exception {
        this.substate = new AssigneToCurveState( appContainer );
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
    public class RectangleSelectState extends ChangeLabelPositionState {

        ChangeLabelPositionState owner = ChangeLabelPositionState.this;

        /**
         * 
         * @param appContainer
         */
        public RectangleSelectState( ApplicationContainer<?> appContainer ) {
            super( appContainer, null, "editFeatureStype" );
        }

        @Override
        public void setSubstate( ToolState substate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DrawingPane createDrawingPane( String platform, Graphics g ) {
            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new LabelSelectDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new LabelSelectDrawingPane( appContainer );
            } else if ( "JME".equalsIgnoreCase( platform ) ) {
                LOG.logWarning( "JME as view platfrom is not supported yet" );
            }
            ( (EditFeatureStyleDrawingPane) drawingPane ).setRotation( owner.rotation );
            ( (EditFeatureStyleDrawingPane) drawingPane ).setDisplacement( owner.dx, owner.dy );

            ChangeLabelPositionState.this.drawingPane = drawingPane;
            return drawingPane;
        }

        @Override
        public void mouseReleased( MouseEvent event ) {
            // stop draw actions and get list of digitized points; will be two
            drawingPane.stopDrawing( event.getX(), event.getY() );
            List<Point> points = ( (GeoDrawingPane) drawingPane ).getDrawObjectsAsGeoPoints();
            ( (LabelSelectDrawingPane) drawingPane ).clear();

            MapModel mapModel = appContainer.getMapModel( null );
            Layer layer = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION ).get( 0 );

            // just feature can be selected (and labeled)
            DataAccessAdapter daa = layer.getDataAccess().get( 0 );
            if ( !( daa instanceof FeatureAdapter ) ) {
                DialogFactory.openWarningDialog( appContainer.getViewPlatform(), event.getSource(),
                                                 Messages.get( "$MD11731" ), Messages.get( "$MD11732" ) );
                return;
            }

            // select feature(s)
            Envelope envelope = GeometryFactory.createEnvelope( points.get( 0 ).getPosition(),
                                                                points.get( 1 ).getPosition(),
                                                                mapModel.getCoordinateSystem() );
            FeatureCollection fc;
            try {
                fc = ( (FeatureAdapter) daa ).getFeatureCollection( envelope );
            } catch ( FilterEvaluationException e ) {
                DialogFactory.openErrorDialog( invokingAction, (Component) event.getSource(),
                                               Messages.get( "$MD11733" ), Messages.get( "$MD11734" ), e );
                return;
            }

            // labeling just can work on one feature
            if ( fc.size() > 1 || fc.size() == 0 ) {
                DialogFactory.openWarningDialog( appContainer.getViewPlatform(), event.getSource(),
                                                 Messages.get( "$MD11735" ), Messages.get( "$MD11736" ) );
                return;
            }

            ChangeLabelPositionState.this.setSelectedFeature( fc.getFeature( 0 ) );
            DefaultMapModule<?> mapModule = appContainer.getActiveMapModule();
            mapModule.update();

        }

        @Override
        public void mouseDragged( MouseEvent event ) {
            if ( drawingPane != null && drawingPane.isDrawing() ) {
                DefaultMapModule<?> mapModule = appContainer.getActiveMapModule();
                mapModule.update();
                Component c = (Component) event.getSource();
                java.awt.Point p = MapTools.adjustPointToPanelSize( event.getPoint(), c.getWidth(), c.getHeight() );
                drawingPane.draw( p.x, p.y );

                // force repainting of the container, otherwise the
                // zoom rectangle is never visible, when view form is a frame
                if ( mapModule.getViewForm() instanceof JFrame ) {
                    Container con = c.getParent();
                    con.repaint();
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
    public class RotateState extends ChangeLabelPositionState {

        ChangeLabelPositionState owner = ChangeLabelPositionState.this;

        /**
         * @param appContainer
         * @param parameter
         * @param invokingAction
         * @throws Exception
         */
        public RotateState( ApplicationContainer<?> appContainer ) throws Exception {
            super( appContainer, null, "editFeatureStype" );
            if ( ChangeLabelPositionState.this.selectedFeature == null ) {
                throw new Exception( Messages.get( "$MD11737" ) );
            }
        }

        @Override
        public DrawingPane createDrawingPane( String platform, Graphics g ) {
            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new ChangeLabelPositionDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new ChangeLabelPositionDrawingPane( appContainer );
            } else if ( "JME".equalsIgnoreCase( platform ) ) {
                LOG.logWarning( "JME as view platfrom is not supported yet" );
            }
            ( (ChangeLabelPositionDrawingPane) drawingPane ).setStringEnvelope( owner.xLabel, owner.yLabel,
                                                                                owner.wLabel, owner.hLabel );
            ( (ChangeLabelPositionDrawingPane) drawingPane ).setRotation( owner.rotation );
            ( (ChangeLabelPositionDrawingPane) drawingPane ).setDisplacement( owner.dx, owner.dy );
            EditFeatureStyleDialog.create( (EditStyleModule<Container>) editStyleModule ).setDisplacement( owner.dx,
                                                                                                           owner.dy );
            owner.drawingPane = drawingPane;

            DefaultMapModule<?> mapModule = appContainer.getActiveMapModule();
            mapModule.update();
            EditFeatureStyleDialog.create( (EditStyleModule<Container>) editStyleModule ).setDrawingPane(
                                                                                                          (EditFeatureStyleDrawingPane) drawingPane );
            return drawingPane;
        }

        @Override
        public void mouseDragged( MouseEvent event ) {
            Component c = (Component) event.getSource();
            java.awt.Point p = MapTools.adjustPointToPanelSize( event.getPoint(), c.getWidth(), c.getHeight() );
            drawingPane.draw( p.x, p.y );
            drawingPane.stopDrawing( p.x, p.y );
            List<Point> points = ( (ChangeLabelPositionDrawingPane) drawingPane ).getDrawObjectsAsGeoPoints();
            double arc = GeometryUtils.getArc( points.get( 0 ).getX(), points.get( 0 ).getY(),
                                               points.get( 0 ).getX() + 10, points.get( 0 ).getY(),
                                               points.get( 1 ).getX(), points.get( 1 ).getY() );

            if ( GeometryUtils.isLeft( points.get( 0 ).getX(), points.get( 0 ).getY(), points.get( 0 ).getX() + 10,
                                       points.get( 0 ).getY(), points.get( 1 ).getX(), points.get( 1 ).getY() ) ) {
                arc *= -1;
            }
            EditFeatureStyleDialog.create( editStyleModule ).setArc( arc );
            ( (ChangeLabelPositionDrawingPane) drawingPane ).setRotation( arc );
            ChangeLabelPositionState.this.rotation = arc;
            DefaultMapModule<?> mapModule = appContainer.getActiveMapModule();
            mapModule.update();
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
    public class DisplaceState extends ChangeLabelPositionState {

        ChangeLabelPositionState owner = ChangeLabelPositionState.this;

        /**
         * @param appContainer
         * @param parameter
         * @param invokingAction
         * @throws Exception
         */
        public DisplaceState( ApplicationContainer<?> appContainer ) throws Exception {
            super( appContainer, null, "editFeatureStype" );
            if ( ChangeLabelPositionState.this.selectedFeature == null ) {
                throw new Exception( Messages.get( "$MD11737" ) );
            }
        }

        @Override
        public DrawingPane createDrawingPane( String platform, Graphics g ) {
            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new ChangeLabelPositionDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new ChangeLabelPositionDrawingPane( appContainer );
            } else if ( "JME".equalsIgnoreCase( platform ) ) {
                LOG.logWarning( "JME as view platfrom is not supported yet" );
            }
            ( (ChangeLabelPositionDrawingPane) drawingPane ).setStringEnvelope( owner.xLabel, owner.yLabel,
                                                                                owner.wLabel, owner.hLabel );
            ( (ChangeLabelPositionDrawingPane) drawingPane ).setRotation( owner.rotation );
            ( (ChangeLabelPositionDrawingPane) drawingPane ).setDisplacement( owner.dx, owner.dy );
            EditFeatureStyleDialog.create( (EditStyleModule<Container>) editStyleModule ).setDisplacement( owner.dx,
                                                                                                           owner.dy );
            owner.drawingPane = drawingPane;
            DefaultMapModule<?> mapModule = appContainer.getActiveMapModule();
            mapModule.update();
            EditFeatureStyleDialog.create( editStyleModule ).setDrawingPane( (EditFeatureStyleDrawingPane) drawingPane );
            return drawingPane;
        }

        @Override
        public void mouseReleased( MouseEvent event ) {
            Component c = (Component) event.getSource();
            java.awt.Point p = MapTools.adjustPointToPanelSize( event.getPoint(), c.getWidth(), c.getHeight() );
            drawingPane.draw( p.x, p.y );
            drawingPane.stopDrawing( p.x, p.y );
            List<Point> points = ( (ChangeLabelPositionDrawingPane) drawingPane ).getDrawObjectsAsGeoPoints();
            GeoTransform gt = appContainer.getMapModel( null ).getToTargetDeviceTransformation();
            owner.dx = gt.getDestX( points.get( 1 ).getX() ) - owner.xLabel;
            owner.dy = owner.yLabel - gt.getDestY( points.get( 1 ).getY() );
        }

        @Override
        public void mouseDragged( MouseEvent event ) {
            Component c = (Component) event.getSource();
            java.awt.Point p = MapTools.adjustPointToPanelSize( event.getPoint(), c.getWidth(), c.getHeight() );
            drawingPane.draw( p.x, p.y );
            drawingPane.stopDrawing( p.x, p.y );
            List<Point> points = ( (ChangeLabelPositionDrawingPane) drawingPane ).getDrawObjectsAsGeoPoints();
            GeoTransform gt = appContainer.getMapModel( null ).getToTargetDeviceTransformation();
            owner.dx = gt.getDestX( points.get( 1 ).getX() ) - owner.xLabel;
            owner.dy = owner.yLabel - gt.getDestY( points.get( 1 ).getY() ) - owner.hLabel;
            EditFeatureStyleDialog.create( editStyleModule ).setDisplacement( owner.dx, owner.dy );
            ( (ChangeLabelPositionDrawingPane) drawingPane ).setDisplacement( owner.dx, owner.dy );
            DefaultMapModule<?> mapModule = appContainer.getActiveMapModule();
            mapModule.update();
        }
    }

    public class AssigneToCurveState extends ChangeLabelPositionState {

        ChangeLabelPositionState owner = ChangeLabelPositionState.this;

        /**
         * @param appContainer
         * @param parameter
         * @param invokingAction
         * @throws Exception
         */
        public AssigneToCurveState( ApplicationContainer<?> appContainer ) throws Exception {
            super( appContainer, null, "editFeatureStyle" );
            if ( ChangeLabelPositionState.this.selectedFeature == null ) {
                throw new Exception( Messages.get( "$MD11737" ) );
            }
        }

        @Override
        public DrawingPane createDrawingPane( String platform, Graphics g ) {
            if ( "Application".equalsIgnoreCase( platform ) ) {
                drawingPane = new AssignLabelToCurveDrawingPane( appContainer );
            } else if ( "Applet".equalsIgnoreCase( platform ) ) {
                drawingPane = new AssignLabelToCurveDrawingPane( appContainer );
            } else if ( "JME".equalsIgnoreCase( platform ) ) {
                LOG.logWarning( "JME as view platfrom is not supported yet" );
            }
            ( (AssignLabelToCurveDrawingPane) drawingPane ).setStringEnvelope( owner.xLabel, owner.yLabel,
                                                                               owner.wLabel, owner.hLabel );
            ( (AssignLabelToCurveDrawingPane) drawingPane ).setRotation( owner.rotation );
            ( (AssignLabelToCurveDrawingPane) drawingPane ).setDisplacement( owner.dx, owner.dy );
            EditFeatureStyleDialog.create( (EditStyleModule<Container>) editStyleModule ).setDisplacement( owner.dx,
                                                                                                           owner.dy );
            owner.drawingPane = drawingPane;
            DefaultMapModule<?> mapModule = appContainer.getActiveMapModule();
            mapModule.update();
            EditFeatureStyleDialog.create( editStyleModule ).setDrawingPane( (EditFeatureStyleDrawingPane) drawingPane );
            Layer layer = EditFeatureStyleDialog.create( editStyleModule ).getSelectedLayer();
            ( (AssignLabelToCurveDrawingPane) drawingPane ).setLayer( layer );
            return drawingPane;
        }

        @Override
        public void mouseDragged( MouseEvent event ) {
            DefaultMapModule<?> mapModule = appContainer.getActiveMapModule();
            mapModule.update();
            Component c = (Component) event.getSource();
            java.awt.Point p = MapTools.adjustPointToPanelSize( event.getPoint(), c.getWidth(), c.getHeight() );
            drawingPane.draw( p.x, p.y );

            // force repainting of the container, otherwise the
            // zoom rectangle is never visible, when view form is a frame
            if ( mapModule.getViewForm() instanceof JFrame ) {
                Container con = c.getParent();
                con.repaint();
            }
        }

        @Override
        public void mouseReleased( MouseEvent event ) {
            Component c = (Component) event.getSource();
            java.awt.Point p = MapTools.adjustPointToPanelSize( event.getPoint(), c.getWidth(), c.getHeight() );
            drawingPane.draw( p.x, p.y );
            drawingPane.stopDrawing( p.x, p.y );
            List<Point> points = ( (AssignLabelToCurveDrawingPane) drawingPane ).getDrawObjectsAsGeoPoints();
            Envelope env = GeometryFactory.createEnvelope( points.get( 0 ).getPosition(),
                                                           points.get( 1 ).getPosition(),
                                                           appContainer.getMapModel( null ).getCoordinateSystem() );
            Pair<Position, Position> segment = null;
            try {
                segment = ( (AssignLabelToCurveDrawingPane) drawingPane ).selectSegment( env );
            } catch ( GeometryException e ) {
                // never happens
                e.printStackTrace();
            }
            if ( segment != null ) {
                // rotation just can be calculated if a segment has been selected
                double arc = 0;
                if ( segment.first.getX() <= segment.second.getX() && segment.first.getY() <= segment.second.getY() ) {
                    arc = GeometryUtils.getArc( segment.first.getX(), segment.first.getY(), segment.first.getX() - 10,
                                                segment.first.getY(), segment.second.getX(), segment.second.getY() );
                    arc += 180;
                } else if ( segment.first.getX() >= segment.second.getX()
                            && segment.first.getY() <= segment.second.getY() ) {
                    arc = GeometryUtils.getArc( segment.first.getX(), segment.first.getY(), segment.first.getX() - 10,
                                                segment.first.getY(), segment.second.getX(), segment.second.getY() );
                    arc += 180;
                } else if ( segment.first.getX() >= segment.second.getX()
                            && segment.first.getY() >= segment.second.getY() ) {
                    arc = GeometryUtils.getArc( segment.first.getX(), segment.first.getY(), segment.first.getX() + 10,
                                                segment.first.getY(), segment.second.getX(), segment.second.getY() );
                } else if ( segment.first.getX() <= segment.second.getX()
                            && segment.first.getY() >= segment.second.getY() ) {
                    arc = GeometryUtils.getArc( segment.first.getX(), segment.first.getY(), segment.first.getX() + 10,
                                                segment.first.getY(), segment.second.getX(), segment.second.getY() );
                }

                EditFeatureStyleDialog.create( editStyleModule ).setArc( arc );
                ( (AssignLabelToCurveDrawingPane) drawingPane ).setRotation( arc );
                ChangeLabelPositionState.this.rotation = arc;
                ( (AssignLabelToCurveDrawingPane) drawingPane ).clear();
                DefaultMapModule<?> mapModule = appContainer.getActiveMapModule();
                mapModule.update();
            }
        }
    }
}
