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

package org.deegree.framework.utils;

import static org.deegree.desktop.views.LayerPane.createThemes;
import static org.deegree.framework.util.MapUtils.DEFAULT_PIXEL_SIZE;
import static org.deegree.graphics.MapFactory.createMapView;
import static org.deegree.model.spatialschema.GeometryFactory.createEnvelope;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.LayerGroup;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.mapmodel.MapModelVisitor;
import org.deegree.graphics.MapView;
import org.deegree.graphics.Theme;
import org.deegree.desktop.config.TargetDeviceType;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;

/**
 * 
 * <code>NewMapHandler</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class MapTools {

    /**
     * adjusts the size and extent of the mapModel to the size of the container
     * 
     * @param width
     *            the new with of the map model
     * @param height
     *            the new height of the map model
     * @param mapModel
     *            the map model to adjust
     */
    public static void adjustMapModelExtent( int width, int height, MapModel mapModel ) {
        mapModel.getTargetDevice().setPixelHeight( height );
        mapModel.getTargetDevice().setPixelWidth( width );
        
        Envelope env = mapModel.getEnvelope();
        double mapRatio = (double) width / (double) height;
        double envRatio = env.getWidth() / env.getHeight();
        double minx, miny, maxx, maxy;

        if ( mapRatio > envRatio ) {
            miny = env.getMin().getY();
            maxy = env.getMax().getY();
            minx = env.getCentroid().getX() - env.getHeight() * mapRatio / 2d;
            maxx = env.getCentroid().getX() + env.getHeight() * mapRatio / 2d;
        } else {
            minx = env.getMin().getX();
            maxx = env.getMax().getX();
            miny = env.getCentroid().getY() - env.getWidth() / mapRatio / 2d;
            maxy = env.getCentroid().getY() + env.getWidth() / mapRatio / 2d;
        }

        mapModel.setEnvelope( createEnvelope( minx, miny, maxx, maxy, env.getCoordinateSystem() ) );

    }

  

    /**
     * calculates the position of the mousecursor in the coordinatesystem of the mapmodel
     * 
     * @param mapModel
     *            the mapModel
     * @param mouseX
     *            the x-position of the mousecursor
     * @param mouseY
     *            the y-position of the mousecursor
     * @param componentWidth
     *            the width of the component
     * @param componentHeight
     *            the height of the component
     * @return
     */
    public static Point calculateMouseCoord( MapModel mapModel, double mouseX, double mouseY, double componentWidth,
                                             double componentHeight ) {
        Envelope extent = mapModel.getEnvelope();
        double deltaX = mouseX / componentWidth * extent.getWidth();
        double deltaY = mouseY / componentHeight * extent.getHeight();
        return GeometryFactory.createPoint( extent.getMin().getX() + deltaX, extent.getMax().getY() - deltaY,
                                            mapModel.getCoordinateSystem() );
    }

    /**
     * adjust the point to the size of the given area (width/height), so that the resulting point lays inside of the
     * area
     * 
     * @param point
     *            the point to adjust
     * @param width
     *            the width of the component
     * @param height
     *            the height of the component
     * @return the adjusted point inside the area
     */
    public static java.awt.Point adjustPointToPanelSize( java.awt.Point point, int width, int height ) {
        int x = Double.valueOf( point.getX() ).intValue();
        int y = Double.valueOf( point.getY() ).intValue();

        if ( x < 0 ) {
            x = 0;
        } else if ( x > width - 1 ) {
            x = width - 1;
        }

        if ( y < 0 ) {
            y = 0;
        } else if ( y > height - 1 ) {
            y = height - 1;
        }

        return new java.awt.Point( x, y );
    }

    /**
     * renders legend for a map onto a buffered image
     * 
     * @param mm
     *            {@link MapModel} to be used
     * @param fourByte
     *            <code>true</code> if a four byte image (e.g. for png) should be created
     * @return rendered legend
     * @throws Exception
     */
    public static BufferedImage getLegendAsImage( final MapModel mm, boolean fourByte )
                            throws Exception {
        final List<Layer> layers = new ArrayList<Layer>();

        mm.walkLayerTree( new MapModelVisitor() {
            public void visit( Layer layer )
                                    throws Exception {
                if ( layer.isVisible() ) {
                    layers.add( layer );
                }
            }

            public void visit( LayerGroup layerGroup )
                                    throws Exception {
                // not using grouping nodes
            }
        } );
        int width = 0;
        int height = 0;
        for ( Layer layer : layers ) {
            BufferedImage bi = layer.getLegend();
            Graphics g = bi.getGraphics();
            int w = 0;
            // TODO remove this heuristic
            if ( bi.getHeight() < 40 ) {
                w = SwingUtilities.computeStringWidth( g.getFontMetrics(), layer.getTitle() );
            }
            g.dispose();
            if ( ( bi.getWidth() + w + 10 ) > width ) {
                width = ( bi.getWidth() + w + 10 );
            }
            height += ( bi.getHeight() + 10 );
        }

        BufferedImage img = null;
        if ( fourByte ) {
            img = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        } else {
            img = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        }
        Graphics2D g = img.createGraphics();
        g.setBackground( Color.WHITE );
        g.setColor( Color.WHITE );
        g.fillRect( 0, 0, width, height );
        g.setColor( Color.BLACK );
        int y = 0;
        for ( Layer layer : layers ) {
            BufferedImage bi = layer.getLegend();
            g.drawImage( bi, 0, y, null );
            g.drawString( layer.getTitle(), bi.getWidth() + 10, y + 10 );
            y += ( bi.getHeight() + 10 );
        }
        g.dispose();
        return img;
    }

    /**
     * renders a map onto a buffered image
     * 
     * @param mm
     *            {@link MapModel} to be used
     * @param fourByte
     *            <code>true</code> if a four byte image (e.g. for png) should be created
     * @param scale
     *            scale factor for map size; scale = 1 means rendered image will have same size as defined in a map
     *            models {@link TargetDeviceType}
     * @return rendered legend
     * @throws Exception
     */
    public static BufferedImage getMapAsImage( final MapModel mm, boolean fourByte, float scale )
                            throws Exception {
        final List<Theme> themes = new ArrayList<Theme>();

        mm.walkLayerTree( new MapModelVisitor() {
            public void visit( Layer layer )
                                    throws Exception {
                double mis = layer.getMinScaleDenominator();
                double mxs = layer.getMaxScaleDenominator();
                if ( layer.isVisible() && mis <= mm.getScaleDenominator() && mxs >= mm.getScaleDenominator() ) {
                    List<Theme> layerThemes = createThemes( layer.getCurrentStyle(), layer.getDataAccess(),
                                                            mm.getCoordinateSystem() );
                    themes.addAll( layerThemes );
                }
            }

            public void visit( LayerGroup layerGroup )
                                    throws Exception {
                // not using grouping nodes
            }
        } );
        List<Theme> tmp = new ArrayList<Theme>();
        for ( int i = themes.size() - 1; i >= 0; i-- ) {
            tmp.add( themes.get( i ) );
        }

        MapView mv = createMapView( "iGeoDesktop", mm.getEnvelope(), mm.getCoordinateSystem(),
                                    tmp.toArray( new Theme[tmp.size()] ), DEFAULT_PIXEL_SIZE );

        int width = Math.round( mm.getTargetDevice().getPixelWidth() * scale );
        int height = Math.round( mm.getTargetDevice().getPixelHeight() * scale );
        if ( width < 50 || height < 50 ) {
            throw new Exception( "value of parameter scale is to small; ensure that "
                                 + "result map at least has a size of 50x50 pixel" );
        }
        BufferedImage img = null;
        if ( fourByte ) {
            img = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
        } else {
            img = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        }
        Graphics2D g = img.createGraphics();
        g.setBackground( new Color( 0 ) );
        g.fillRect( 0, 0, width, height );
        g.setClip( 0, 0, width, height );
        mv.paint( g );
        g.dispose();
        return img;
    }

}
