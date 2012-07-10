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

package org.deegree.igeo.views.swing.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Iterator;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.displayelements.DisplayElement;
import org.deegree.graphics.displayelements.DisplayElementFactory;
import org.deegree.graphics.sld.LineSymbolizer;
import org.deegree.graphics.sld.PolygonSymbolizer;
import org.deegree.graphics.sld.StyleFactory;
import org.deegree.graphics.sld.UserStyle;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiPoint;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Position;
import org.deegree.model.spatialschema.Ring;
import org.deegree.model.spatialschema.Surface;

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
public class HighlightRenderer {

    private static final ILogger LOG = LoggerFactory.getLogger( HighlightRenderer.class );

    private List<FeatureCollection> selectedFeatures;

    private MapModel mapModel;

    private DefaultMapComponent owner;

    private static Color pointFillColor = Color.RED;

    private static Color pointStrokeColor = Color.BLACK;

    private static int pointSize = 7;

    private static LineSymbolizer lineSymbolizer;

    private static PolygonSymbolizer polygonSymbolizer;
    static {
        UserStyle style = (UserStyle) StyleFactory.createLineStyle( Color.decode( "0xFF8800" ), 3, 1, 0,
                                                                    Double.MAX_VALUE );
        lineSymbolizer = (LineSymbolizer) style.getFeatureTypeStyles()[0].getRules()[0].getSymbolizers()[0];
        style = (UserStyle) StyleFactory.createPolygonStyle( Color.WHITE, 0.5f, Color.decode( "0xFF8800" ), 3, 1, 0,
                                                             Double.MAX_VALUE );
        polygonSymbolizer = (PolygonSymbolizer) style.getFeatureTypeStyles()[0].getRules()[0].getSymbolizers()[0];
    }

    /**
     * 
     * @param mapModel
     * @param owner
     */
    HighlightRenderer( MapModel mapModel, DefaultMapComponent owner ) {
        this.owner = owner;
        this.mapModel = mapModel;
    }

    void highlightFeatures( Graphics g ) {
        if ( selectedFeatures != null ) {
            GeoTransform gt = mapModel.getToTargetDeviceTransformation();
            for ( FeatureCollection fc : selectedFeatures ) {
                Iterator<Feature> iter = fc.iterator();
                while ( iter.hasNext() ) {
                    Feature feature = iter.next();
                    renderFeature( g, gt, feature );
                }
            }

        }
    }

    private void renderFeature( Graphics g, GeoTransform gt, Feature feature ) {
        FeatureProperty[] fp = feature.getProperties();
        for ( FeatureProperty property : fp ) {
            Object value = property.getValue();
            if ( value instanceof Feature ) {
                // recursive invokation for complex features/properties
                renderFeature( g, gt, (Feature) value );
            } else if ( value instanceof FeatureCollection ) {
                // recursive invokation for complex features/properties
                Iterator<Feature> iter = ( (FeatureCollection) value ).iterator();
                while ( iter.hasNext() ) {
                    renderFeature( g, gt, iter.next() );
                }
            } else if ( value instanceof org.deegree.model.spatialschema.Point ) {
                org.deegree.model.spatialschema.Point point = (org.deegree.model.spatialschema.Point) value;
                drawPoint( g, pointFillColor, gt, point.getX(), point.getY() );
            } else if ( value instanceof MultiPoint ) {
                MultiPoint mp = (MultiPoint) value;
                org.deegree.model.spatialschema.Point[] points = mp.getAllPoints();
                for ( org.deegree.model.spatialschema.Point point : points ) {
                    drawPoint( g, pointFillColor, gt, point.getX(), point.getY() );
                }
            } else if ( value instanceof Curve ) {
                Curve curve = (Curve) value;
                drawLine( g, gt, feature, curve );
            } else if ( value instanceof MultiCurve ) {
                MultiCurve multiCurve = (MultiCurve) value;
                Curve[] curves = multiCurve.getAllCurves();
                for ( Curve curve : curves ) {
                    drawLine( g, gt, feature, curve );
                }
            } else if ( value instanceof Surface ) {
                Surface surface = (Surface) value;
                drawPolygon( g, gt, feature, surface );
            } else if ( value instanceof MultiSurface ) {
                MultiSurface multiSurface = (MultiSurface) value;
                Surface[] surfaces = multiSurface.getAllSurfaces();
                for ( Surface surface : surfaces ) {
                    drawPolygon( g, gt, feature, surface );
                }
            }
        }
    }

    /**
     * draws a Surface onto the passed graphic context using the {@link PolygonSymbolizer} that has been created in the
     * classes static block
     * 
     * @param g
     * @param gt
     * @param feature
     * @param surface
     */
    private void drawPolygon( Graphics g, GeoTransform gt, Feature feature, Surface surface ) {
        try {
            DisplayElement de = DisplayElementFactory.buildPolygonDisplayElement( feature, surface, polygonSymbolizer );
            de.paint( g, gt, 1 );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
        }
        Position[] pos = surface.getSurfaceBoundary().getExteriorRing().getPositions();
        double dr = ( 255d - pointFillColor.getRed() ) / pos.length;
        double dg = ( 255d - pointFillColor.getGreen() ) / pos.length;
        double db = ( 255d - pointFillColor.getBlue() ) / pos.length;
        double rr = 255;
        double gg = 255;
        double bb = 255;
        for ( Position position : pos ) {
            Color color = new Color( (int) Math.round( rr ), (int) Math.round( gg ), (int) Math.round( bb ) );
            rr -= dr;
            gg -= dg;
            bb -= db;
            drawPoint( g, color, gt, position.getX(), position.getY() );
        }
        Ring[] innerRings = surface.getSurfaceBoundary().getInteriorRings();
        for ( Ring ring : innerRings ) {
            pos = ring.getPositions();
            for ( Position position : pos ) {
                drawPoint( g, pointFillColor, gt, position.getX(), position.getY() );
            }
        }
    }

    /**
     * draws a Curce onto the passed graphic context using the {@link LineSymbolizer} that has been created in the
     * classes static block
     * 
     * @param g
     * @param gt
     * @param feature
     * @param curve
     */
    private void drawLine( Graphics g, GeoTransform gt, Feature feature, Curve curve ) {
        try {
            DisplayElement de = DisplayElementFactory.buildLineStringDisplayElement( feature, curve, lineSymbolizer );
            de.paint( g, gt, 1 );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
        }
        try {
            Position[] pos = curve.getAsLineString().getPositions();

            double dr = ( 255d - pointFillColor.getRed() ) / pos.length;
            double dg = ( 255d - pointFillColor.getGreen() ) / pos.length;
            double db = ( 255d - pointFillColor.getBlue() ) / pos.length;
            double rr = 255;
            double gg = 255;
            double bb = 255;
            for ( Position position : pos ) {
                Color color = new Color( (int) Math.round( rr ), (int) Math.round( gg ), (int) Math.round( bb ) );
                rr -= dr;
                gg -= dg;
                bb -= db;
                drawPoint( g, color, gt, position.getX(), position.getY() );
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
        }
    }

    /**
     * draws a Point onto the passed graphic context using style read from deegree iGeoDesktop configuration
     * 
     * @param g
     * @param gt
     * @param px
     * @param py
     */
    private void drawPoint( Graphics g, Color color, GeoTransform gt, double px, double py ) {
        double x = gt.getDestX( px );
        double y = gt.getDestY( py );
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke( new BasicStroke( 1 ) );
        g2.setColor( color );
        int size2 = pointSize / 2;
        g2.fillRect( (int) x - size2, (int) y - size2, pointSize, pointSize );
        g2.setColor( pointStrokeColor );
        g2.drawRect( (int) x - size2, (int) y - size2, pointSize, pointSize );
    }

    /**
     * adjust the point to the size of the panel, so that the resulting point lays inside of the zoom panel
     * 
     * @param point
     *            the point to adjust
     * @return the adjusted point inside the zoom panel
     */
    protected Point adjustPointToPanelSize( Point point ) {
        int x = Double.valueOf( point.getX() ).intValue();
        int y = Double.valueOf( point.getY() ).intValue();

        if ( x < 0 ) {
            x = 0;
        } else if ( x > owner.getWidth() - 1 ) {
            x = owner.getWidth() - 1;
        }

        if ( y < 0 ) {
            y = 0;
        } else if ( y > owner.getHeight() - 1 ) {
            y = owner.getHeight() - 1;
        }

        return new Point( x, y );
    }

    /**
     * 
     * @param selectedFeatures
     */
    void setSelectedFeatures( List<FeatureCollection> selectedFeatures ) {
        this.selectedFeatures = selectedFeatures;
    }
}
