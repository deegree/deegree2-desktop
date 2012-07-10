/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2007 by:
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

package org.deegree.igeo.style;

import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.displayelements.DisplayElementFactory;
import org.deegree.graphics.displayelements.Label;
import org.deegree.graphics.displayelements.LabelFactory;
import org.deegree.graphics.displayelements.RasterDisplayElement;
import org.deegree.graphics.sld.Fill;
import org.deegree.graphics.sld.Font;
import org.deegree.graphics.sld.Halo;
import org.deegree.graphics.sld.RasterSymbolizer;
import org.deegree.graphics.sld.TextSymbolizer;
import org.deegree.graphics.transformation.WorldToScreenTransform;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.perform.RasterVisualPropertyPerformer;
import org.deegree.model.coverage.grid.ImageGridCoverage;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;

/**
 * <code>ImageFactory</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class ImageFactory {

    private static final ILogger LOG = LoggerFactory.getLogger( ImageFactory.class );

    private static final String LABEL = Messages.getMessage( Locale.getDefault(), "$MD10675" );

    /**
     * Creates an image out of the given RasterSymbolizer.
     * 
     * @param symbolizer
     * @return an image out of the given symbolizer
     */
    public static BufferedImage createImage( RasterSymbolizer symbolizer ) {
        BufferedImage orig = null;
        try {
            orig = ImageIO.read( RasterVisualPropertyPerformer.class.getResourceAsStream( "rasterpreview.jpg" ) );
        } catch ( IOException e ) {
            LOG.logDebug( "could not create preview for RasterSymbolizer" );
        }

        WorldToScreenTransform t = new WorldToScreenTransform();
        Envelope env = GeometryFactory.createEnvelope( -120, -120, 120, 120, null );
        ImageGridCoverage coverage = new ImageGridCoverage( null, env, orig );
        RasterDisplayElement rde = DisplayElementFactory.buildRasterDisplayElement( coverage, symbolizer );

        BufferedImage img = new BufferedImage( orig.getWidth(), orig.getHeight(), BufferedImage.TYPE_INT_ARGB );
        rde.paint( img.getGraphics(), t, 1 );
        return img;
    }

    /**
     * Creates an image out of the given TextSymbolizer.
     * 
     * @param symbolizer
     * @return an image out of the given symbolizer
     */
    public static BufferedImage createImage( TextSymbolizer symbolizer ) {
        BufferedImage img = null;
        Font symbolizerFont = symbolizer.getFont();
        int style = java.awt.Font.PLAIN;
        try {
            if ( Font.STYLE_ITALIC == symbolizerFont.getStyle( null ) ) {
                style = java.awt.Font.ITALIC;
            }

            if ( Font.WEIGHT_BOLD == symbolizerFont.getWeight( null ) ) {
                style = style + java.awt.Font.BOLD;
            }

            BufferedImage biToGetSize = new BufferedImage( 7, 7, BufferedImage.TYPE_INT_ARGB );
            Graphics2D gToGetSize = (Graphics2D) biToGetSize.getGraphics();

            int fontSize = (int) SldValues.getDefaultFontSize();
            try {
                fontSize = (int) symbolizerFont.getSize( null );
            } catch ( FilterEvaluationException e ) {
            }
            java.awt.Font font = new java.awt.Font( symbolizerFont.getFamily( null ), style, fontSize );
            GlyphVector vec = font.createGlyphVector( gToGetSize.getFontRenderContext(), LABEL );
            int width = (int) vec.getPixelBounds( null, 0, 0 ).getWidth();
            int height = (int) vec.getPixelBounds( null, 0, 0 ).getHeight();

            img = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
            Graphics2D g = (Graphics2D) img.getGraphics();
            FontRenderContext frc = g.getFontRenderContext();

            Halo halo = symbolizer.getHalo();
            Fill fill = symbolizer.getFill();

            LineMetrics metrics = font.getLineMetrics( LABEL, frc );

            Label l = LabelFactory.createLabel( LABEL, font, fill.getFill( null ), metrics, createFeature(), halo, 0,
                                                height, width, height, 0, 0, 0, 0, 0, fill.getOpacity( null ) );
            l.paint( g );
        } catch ( FilterEvaluationException e ) {
            LOG.logDebug( "could not create image for TextSymbolizer", e );
        }
        return img;
    }

    private static Feature createFeature() {
        PropertyType[] ftpsGeom = new PropertyType[1];
        ftpsGeom[0] = FeatureFactory.createSimplePropertyType( new QualifiedName( "GEOM" ), Types.GEOMETRY, false );

        FeatureProperty[] featPropGeom = new FeatureProperty[1];
        Geometry geom = null;
        try {
            geom = GeometryFactory.createPoint( 0, 0, CRSFactory.create( "EPSG:4326" ) );
        } catch ( UnknownCRSException e1 ) {
            e1.printStackTrace();
        }

        featPropGeom[0] = FeatureFactory.createFeatureProperty( new QualifiedName( "GEOM" ), geom );

        FeatureType featureType = FeatureFactory.createFeatureType( "featureTypeGom", false, ftpsGeom );
        List<FeatureProperty> properties = new ArrayList<FeatureProperty>();
        return FeatureFactory.createFeature( "peview", featureType, properties );
    }
}
