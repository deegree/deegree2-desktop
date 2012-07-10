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

package org.deegree.igeo.style.perform;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.deegree.graphics.sld.ParameterValueType;
import org.deegree.graphics.sld.RasterSymbolizer;
import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.StyleFactory;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.igeo.style.ImageFactory;
import org.deegree.igeo.style.model.SldValues;

/**
 * <code>RasterVisualPropertyPerformer</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class RasterVisualPropertyPerformer implements VisualPropertyPerformer {

    private double transparency = SldValues.getDefaultOpacity();

    private double contrastEnhancement = SldValues.getDefaultContrastEnhancement();

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.perform.VisualPropertyPerformer#getRules()
     */
    public List<Rule> getRules( boolean isInPixel ) {
        List<Rule> rules = new ArrayList<Rule>();
        Rule r = StyleFactory.createRule( getSymbolizer( isInPixel ) );
        rules.add( r );
        return rules;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.perform.VisualPropertyPerformer#update(org.deegree.igeo
     * .views.swing.style.perform.StyleChangedEvent)
     */
    public void update( StyleChangedEvent changeEvent ) {
        Object value = changeEvent.getValue();
        switch ( changeEvent.getType() ) {
        case OPACITY:
            if ( value instanceof Double ) {
                this.transparency = (Double) value;
            }
            break;
        case CONTRASTENHANCEMENT:
            if ( value instanceof Integer ) {
                this.contrastEnhancement = (Integer) value;
            }
            break;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.perform.VisualPropertyPerformer#getAsImage()
     */
    public BufferedImage getAsImage() {
        return ImageFactory.createImage( (RasterSymbolizer) getSymbolizer( true ) );
        // BufferedImage orig = null;
        // try {
        // orig = ImageIO.read( RasterVisualPropertyPerformer.class.getResourceAsStream(
        // "rasterpreview.jpg" ) );
        // } catch ( IOException e ) {
        // LOG.logDebug( "could not create preview for raster" );
        // }
        //
        // WorldToScreenTransform t = new WorldToScreenTransform();
        // Envelope env = GeometryFactory.createEnvelope( -120, -120, 120, 120, null );
        // ImageGridCoverage coverage = new ImageGridCoverage( null, env, orig );
        // RasterDisplayElement rde = DisplayElementFactory.buildRasterDisplayElement( coverage,
        // (RasterSymbolizer) getSymbolizer() );
        //
        // BufferedImage img = new BufferedImage( orig.getWidth(), orig.getHeight(),
        // BufferedImage.TYPE_INT_ARGB );
        // rde.paint( img.getGraphics(), t, 1 );
        // return img;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.perform.VisualPropertyPerformer#getSymbolizer()
     */
    public Symbolizer getSymbolizer( boolean isInPixel ) {
        RasterSymbolizer symbolizer = new RasterSymbolizer();
        ParameterValueType transparencyPVT = StyleFactory.createParameterValueType( this.transparency );
        symbolizer.setOpacity( transparencyPVT );
        symbolizer.setGamma( contrastEnhancement );
        return symbolizer;
    }

}
