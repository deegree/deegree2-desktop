/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2012 by:
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

 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: info@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/

package org.deegree.igeo.style.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.batik.ext.awt.LinearGradientPaint;
import org.deegree.igeo.settings.ColorListEntry;

/**
 * <code>FillGradient</code>
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class LinearGradient implements Fill {

    private String name;

    private LinearGradientPaint gradient;

    private GraphicSymbol graphicSymbol;

    private BufferedImage image;

    public LinearGradient( String name, List<ColorListEntry> colorsListEntries ) throws Exception {
        if ( colorsListEntries.size() > 1 ) {
            float[] fractions = new float[colorsListEntries.size()];
            Color[] colors = new Color[colorsListEntries.size()];
            float min = colorsListEntries.get( 0 ).getPosition();
            float max = colorsListEntries.get( 0 ).getPosition();
            for ( int i = 0; i < colorsListEntries.size(); i++ ) {
                ColorListEntry entry = colorsListEntries.get( i );
                fractions[i] = entry.getPosition();
                colors[i] = entry.getColor();
                min = Math.min( min, entry.getPosition() );
                max = Math.max( max, entry.getPosition() );
            }
            this.name = name;
            this.gradient = new LinearGradientPaint( new Point2D.Float( min, min ), new Point2D.Float( max, max ),
                                                     fractions, colors );

        } else {
            throw new Exception();
        }
    }

    public LinearGradient( String name, LinearGradientPaint gradient ) {
        this.name = name;
        this.gradient = gradient;
    }

    /**
     * @return the name of this gradient
     */
    public String getName() {
        return name;
    }

    /**
     * @return the gradient
     */
    public LinearGradientPaint getGradient() {
        return gradient;
    }

    /**
     * @return the graphicSymbol
     */
    public GraphicSymbol getGraphicSymbol() {
        return graphicSymbol;
    }

    /**
     * @param graphicSymbol
     *            the graphicSymbol to set
     */
    public void setGraphicSymbol( GraphicSymbol graphicSymbol ) {
        this.graphicSymbol = graphicSymbol;
    }

    /**
     * @return
     */
    public List<ColorListEntry> getAsColorListEntry() {
        List<ColorListEntry> colorListEntries = new ArrayList<ColorListEntry>();
        if ( gradient != null ) {
            for ( int i = 0; i < gradient.getFractions().length; i++ ) {
                colorListEntries.add( new ColorListEntry( gradient.getColors()[i], gradient.getFractions()[i] ) );
            }
        }
        return colorListEntries;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.style.model.Fill#getFills(int)
     */
    public List<Object> getFills( int count ) {
        List<Object> result = new ArrayList<Object>();

        // sort by fractions
        List<ColorFraction> cfs = new ArrayList<ColorFraction>();
        Color[] colors = gradient.getColors();
        float[] fractions = gradient.getFractions();
        for ( int i = 0; i < fractions.length; i++ ) {
            cfs.add( new ColorFraction( fractions[i], colors[i] ) );
        }

        if ( cfs.size() > 1 ) {

            Collections.sort( cfs );

            ColorFraction cf = cfs.get( 0 );
            ColorFraction nextcf = cfs.get( 1 );

            double interval = ( cfs.get( cfs.size() - 1 ).fraction - cf.fraction ) / ( count - 1 );
            double position = cf.fraction;

            boolean isFillPattern = graphicSymbol != null && graphicSymbol instanceof FillPattern;

            for ( int i = 0; i < count; i++ ) {
                if ( Math.abs (position - cf.fraction) < 0.0000001 ) {
                    if ( isFillPattern ) {
                        FillPattern newFP = new FillPattern( (FillPattern) graphicSymbol );
                        newFP.setColor( cf.color );
                        result.add( newFP );
                    } else {
                        result.add( cf.color );
                    }
                } else if ( position > cf.fraction ) {
                    // increase index of of considered ColorFractions until position is between
                    // the two fractions
                    while ( position > nextcf.fraction && cfs.indexOf( nextcf ) < cfs.size() - 1 ) {
                        cf = nextcf;
                        nextcf = cfs.get( cfs.indexOf( nextcf ) + 1 );
                    }
                    double pos = ( position - cf.fraction ) / ( nextcf.fraction - cf.fraction );
                    if ( isFillPattern ) {
                        FillPattern newFP = new FillPattern( (FillPattern) graphicSymbol );
                        newFP.setColor( getColorBetween( cf.color, nextcf.color, new Float( pos ) ) );
                        result.add( newFP );
                    } else {
                        result.add( getColorBetween( cf.color, nextcf.color, new Float( pos ) ) );
                    }
                }
                position = position + interval;
            }
        }
        return result;
    }

    private Color getColorBetween( Color c1, Color c2, float pos ) {
        float r, g, b;
        float[] rgbs1 = c1.getRGBColorComponents( null );
        float[] rgbs2 = c2.getRGBColorComponents( null );

        float r1 = rgbs1[0];
        float r2 = rgbs2[0];
        r = ( r1 + ( r2 - r1 ) * pos ) > 1 ? 1 : ( r1 + ( r2 - r1 ) * pos );
        r = r < 0 ? 0 : r;
        
        float g1 = rgbs1[1];
        float g2 = rgbs2[1];
        g = ( g1 + ( g2 - g1 ) * pos ) > 1 ? 1 : ( g1 + ( g2 - g1 ) * pos );
        g = g < 0 ? 0 : g;
        
        float b1 = rgbs1[2];
        float b2 = rgbs2[2];
        b = ( b1 + ( b2 - b1 ) * pos ) > 1 ? 1 : ( b1 + ( b2 - b1 ) * pos );
        b = b < 0 ? 0 : b; 

        return new Color( r, g, b );
    }

    /**
     * @return a small overview (56 x 32 pixel) of the color ramp as image
     */
    public BufferedImage getAsImage() {
        if ( this.image == null ) {
            BufferedImage bi = new BufferedImage( 56, 22, BufferedImage.TYPE_INT_ARGB );
            Graphics2D g = (Graphics2D) ( bi ).getGraphics();
            int y = 2;
            for ( Object c : getFills( 4 ) ) {
                g.setBackground( (Color) c );
                g.clearRect( 2, y, 52, 3 );
                y = y + 5;
            }
            this.image = bi;
        }
        return this.image;
    }

    private class ColorFraction implements Comparable<ColorFraction> {

        private Float fraction;

        private Color color;

        public ColorFraction( Float fraction, Color color ) {
            this.fraction = fraction;
            this.color = color;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo( ColorFraction o ) {
            if ( fraction == null && o.fraction == null ) {
                return 0;
            } else if ( fraction == null ) {
                return -1;
            }
            return fraction.compareTo( o.fraction );
        }
    }

}
