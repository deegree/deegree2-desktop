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

package org.deegree.igeo.style.model;

import java.awt.Color;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.vecmath.Point2d;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.sld.Font;
import org.deegree.graphics.sld.Graphic;
import org.deegree.graphics.sld.Mark;
import org.deegree.graphics.sld.PointSymbolizer;
import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.Stroke;
import org.deegree.graphics.sld.StyleFactory;
import org.deegree.igeo.i18n.Messages;

/**
 * <code>SldDefaultValues</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class SldValues {

    private static final ILogger LOG = LoggerFactory.getLogger( SldValues.class );

    private static Color defaultColor = new Color( 0.5f, 0.5f, 0.5f );

    private static double defaultOpacity = 1.0;

    private static double defaultRotation = 0.0;

    private static double defaultSize = 6;

    private static double defaultLineWidth = 1.0;

    private static int defaultLineCapCode = Stroke.LC_DEFAULT;

    private static int defaultLineJoinCode = Stroke.LJ_DEFAULT;

    private static Color defaultLineColor = Color.BLACK;

    private static double defaultFontSize = Font.SIZE_DEFAULT;

    private static Color defaultFontColor = Color.BLACK;

    private static String defaultFontWeight = "normal";

    private static String defaultFontStyle = "normal";

    private static Color defaultHaloColor = Color.WHITE;

    private static double defaultHaloRadius = 1.0;

    private static double defaultContrastEnhancement = 1.0;

    private static WellKnownMark defaultWKM = new WellKnownMark( "square", Messages.getMessage( Locale.getDefault(),
                                                                                                "$MD10611" ) );

    private static DashArray defaultLineStyle = new DashArray( Messages.getMessage( Locale.getDefault(), "$MD10612" ),
                                                               new float[] { 1, 0 } );

    private static List<WellKnownMark> wkm = new ArrayList<WellKnownMark>();

    private static List<SldProperty> lc = new ArrayList<SldProperty>();

    private static List<SldProperty> lj = new ArrayList<SldProperty>();

    private static List<SldProperty> fontStyles = new ArrayList<SldProperty>();

    private static List<SldProperty> fontWeights = new ArrayList<SldProperty>();

    private static List<DashArray> dashArrays = new ArrayList<DashArray>();

    private static List<FillPattern> fillPatterns = new ArrayList<FillPattern>();

    private static Point2d defaultAnchorPoint = new Point2d( 0, 0.5 );

    private static Point2d defaultDisplacement = new Point2d( 0, 0 );

    private static String defaultFontFamily = "SansSerif";

    static {

        wkm.add( defaultWKM );
        wkm.add( new WellKnownMark( "circle", Messages.getMessage( Locale.getDefault(), "$MD10613" ) ) );
        wkm.add( new WellKnownMark( "triangle", Messages.getMessage( Locale.getDefault(), "$MD10614" ) ) );
        wkm.add( new WellKnownMark( "star", Messages.getMessage( Locale.getDefault(), "$MD10615" ) ) );
        wkm.add( new WellKnownMark( "cross", Messages.getMessage( Locale.getDefault(), "$MD10616" ) ) );
        wkm.add( new WellKnownMark( "x", Messages.getMessage( Locale.getDefault(), "$MD10617" ) ) );

        lc.add( new SldProperty( Stroke.LC_BUTT, "butt", Messages.getMessage( Locale.getDefault(), "$MD10618" ) ) );
        lc.add( new SldProperty( Stroke.LC_SQUARE, "square", Messages.getMessage( Locale.getDefault(), "$MD10619" ) ) );
        lc.add( new SldProperty( Stroke.LC_ROUND, "round", Messages.getMessage( Locale.getDefault(), "$MD10620" ) ) );

        lj.add( new SldProperty( Stroke.LJ_MITRE, "mitre", Messages.getMessage( Locale.getDefault(), "$MD10621" ) ) );
        lj.add( new SldProperty( Stroke.LJ_ROUND, "round", Messages.getMessage( Locale.getDefault(), "$MD10622" ) ) );
        lj.add( new SldProperty( Stroke.LJ_BEVEL, "bevel", Messages.getMessage( Locale.getDefault(), "$MD10623" ) ) );

        dashArrays.add( defaultLineStyle );
        dashArrays.add( new DashArray( Messages.getMessage( Locale.getDefault(), "$MD10624" ), new float[] { 1, 1 } ) );
        dashArrays.add( new DashArray( Messages.getMessage( Locale.getDefault(), "$MD10625" ), new float[] { 3, 3 } ) );
        dashArrays.add( new DashArray( Messages.getMessage( Locale.getDefault(), "$MD10626" ), new float[] { 5, 5 } ) );
        dashArrays.add( new DashArray( Messages.getMessage( Locale.getDefault(), "$MD10627" ), new float[] { 5, 1 } ) );
        dashArrays.add( new DashArray( Messages.getMessage( Locale.getDefault(), "$MD10628" ), new float[] { 7, 7 } ) );
        dashArrays.add( new DashArray( Messages.getMessage( Locale.getDefault(), "$MD10629" ), new float[] { 7, 12 } ) );
        dashArrays.add( new DashArray( Messages.getMessage( Locale.getDefault(), "$MD10630" ), new float[] { 9, 9 } ) );
        dashArrays.add( new DashArray( Messages.getMessage( Locale.getDefault(), "$MD10631" ), new float[] { 9, 2 } ) );
        dashArrays.add( new DashArray( Messages.getMessage( Locale.getDefault(), "$MD10632" ), new float[] { 15, 6 } ) );
        dashArrays.add( new DashArray( Messages.getMessage( Locale.getDefault(), "$MD10633" ), new float[] { 20, 3 } ) );

        fontStyles.add( new SldProperty( Font.STYLE_NORMAL, "normal", Messages.getMessage( Locale.getDefault(),
                                                                                           "$MD10634" ) ) );
        fontStyles.add( new SldProperty( Font.STYLE_ITALIC, "italic", Messages.getMessage( Locale.getDefault(),
                                                                                           "$MD10635" ) ) );
        // fontStyles.add( new SldProperty( Font.STYLE_OBLIQUE, "oblique", Messages.getMessage( Locale.getDefault(),
        // "$MD10636" ) ) );

        fontWeights.add( new SldProperty( Font.WEIGHT_NORMAL, "normal", Messages.getMessage( Locale.getDefault(),
                                                                                             "$MD10637" ) ) );
        fontWeights.add( new SldProperty( Font.WEIGHT_BOLD, "bold", Messages.getMessage( Locale.getDefault(),
                                                                                         "$MD10638" ) ) );
        // set defined fill patterns
        try {
            DecimalFormat df = new DecimalFormat( "000" );
            for ( int i = 0; i < 500; i++ ) {
                String s = df.format( i );
                URL fpURL = SldValues.class.getResource( "/org/deegree/igeo/style/model/fillpattern/" + s + ".png" );
                InputStream is = SldValues.class.getResourceAsStream( "/org/deegree/igeo/style/model/fillpattern/" + s
                                                                      + ".png" );
                if ( is != null ) {
                    is.close();
                    fillPatterns.add( new FillPattern( s, fpURL, defaultColor ) );
                }
            }
        } catch ( Exception e ) {
            LOG.logError( "Could not read fill patterns", e );
        }

    }

    private SldValues() {
    }

    /**
     * @return the defaultColor
     */
    public static Color getDefaultColor() {
        return defaultColor;
    }

    /**
     * @return the defaultOpacity
     */
    public static double getDefaultOpacity() {
        return defaultOpacity;
    }

    /**
     * @return the defaultRotation
     */
    public static double getDefaultRotation() {
        return defaultRotation;
    }

    /**
     * @return the defaultSize
     */
    public static double getDefaultSize() {
        return defaultSize;
    }

    /**
     * @return the defaultWKM
     */
    public static WellKnownMark getDefaultWKM() {
        return defaultWKM;
    }

    /**
     * @return the defaultLineWidth
     */
    public static double getDefaultLineWidth() {
        return defaultLineWidth;
    }

    /**
     * @return the defaultLineStyle
     */
    public static DashArray getDefaultLineStyle() {
        return defaultLineStyle;
    }

    /**
     * @return the defaultLineCapCode
     */
    public static int getDefaultLineCapCode() {
        return defaultLineCapCode;
    }

    /**
     * @return the defaultLineJoinCode
     */
    public static int getDefaultLineJoinCode() {
        return defaultLineJoinCode;
    }

    /**
     * @return the defaultLineColor
     */
    public static Color getDefaultLineColor() {
        return defaultLineColor;
    }

    /**
     * @return the defaultFontSize
     */
    public static double getDefaultFontSize() {
        return defaultFontSize;
    }

    /**
     * @return
     */
    public static String getDefaultFontStyle() {
        return defaultFontStyle;
    }

    /**
     * @return
     */
    public static String getDefaultFontWeight() {
        return defaultFontWeight;
    }

    /**
     * @return the defaultFontColor
     */
    public static Color getDefaultFontColor() {
        return defaultFontColor;
    }

    /**
     * @return
     */
    public static String getDefaultFontFamily() {
        return defaultFontFamily;
    }

    /**
     * @return the defaultHaloColor
     */
    public static Color getDefaultHaloColor() {
        return defaultHaloColor;
    }

    /**
     * @return the defaultHaloRadius
     */
    public static double getDefaultHaloRadius() {
        return defaultHaloRadius;
    }

    /**
     * @return the defaultAnchorPointX
     */
    public static Point2d getDefaultAnchorPoint() {
        return defaultAnchorPoint;
    }

    /**
     * @return the defaultDisplacementX
     */
    public static Point2d getDefaultDisplacement() {
        return defaultDisplacement;
    }

    /**
     * @return the defaultContrastEnhancement (gammaValue)
     */
    public static double getDefaultContrastEnhancement() {
        return defaultContrastEnhancement;
    }

    /**
     * @return the wkm
     */
    public static List<WellKnownMark> getWellKnownMarks() {
        return wkm;
    }

    /**
     * @return all linecaps
     */
    public static List<SldProperty> getLineCaps() {
        return lc;
    }

    /**
     * @return all linejoins
     */
    public static List<SldProperty> getLineJoins() {
        return lj;
    }

    /**
     * @return the list of dashArrays
     */
    public static List<DashArray> getDashArrays() {
        return dashArrays;
    }

    /**
     * @return a copy of the fillPatterns
     */
    public static List<FillPattern> getFillPatterns() {
        List<FillPattern> copiedList = new ArrayList<FillPattern>( fillPatterns.size() );
        for ( FillPattern fillPattern : fillPatterns ) {
            copiedList.add( new FillPattern( fillPattern ) );
        }
        return copiedList;
    }

    /**
     * @param onlineResource
     * @return
     */
    public static boolean isFillPattern( URL onlineResource ) {
        for ( FillPattern fillPattern : fillPatterns ) {
            if ( fillPattern.getUrl().equals( onlineResource ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the fontStyles
     */
    public static List<SldProperty> getFontStyles() {
        return fontStyles;
    }

    /**
     * @param fontStyleCode
     *            the code of the sldPropetry
     * @return the fontStyle with the given code as sldProperty
     */
    public static SldProperty getFontStyle( int fontStyleCode ) {
        for ( SldProperty sldP : getFontStyles() ) {
            if ( fontStyleCode == sldP.getTypeCode() ) {
                return sldP;
            }
        }
        return null;
    }

    /**
     * @param fontWeightCode
     *            the code of the sldPropetry
     * @return the fontWeight with the given code as sldProperty
     */
    public static SldProperty getFontWeight( int fontWeightCode ) {
        for ( SldProperty sldP : getFontWeights() ) {
            if ( fontWeightCode == sldP.getTypeCode() ) {
                return sldP;
            }
        }
        return null;
    }

    /**
     * @return the fontWeights
     */
    public static List<SldProperty> getFontWeights() {
        return fontWeights;
    }

    public static Rule createDefaultPointSymbolizerRule() {
        Mark mark = StyleFactory.createMark( getDefaultWKM().getSldName(), getDefaultColor() );
        Graphic graphic = StyleFactory.createGraphic( null, mark, getDefaultOpacity(), getDefaultSize(),
                                                      getDefaultRotation() );
        PointSymbolizer ps = StyleFactory.createPointSymbolizer();
        ps.setGraphic( graphic );
        return StyleFactory.createRule( ps );
    }

    /**
     * @return the default line cap as string
     */
    public static String getDefaultLineCap() {
        String s = "";
        switch ( defaultLineCapCode ) {
        case Stroke.LC_BUTT:
            s = "butt";
            break;
        case Stroke.LC_ROUND:
            s = "round";
            break;
        default:
            s = "square";
            break;
        }
        return s;
    }

    /**
     * @return the daualit line cap as property
     */
    public static SldProperty getDefaultLineCapAsProperty() {
        String defaultLc = getDefaultLineCap();
        for ( SldProperty lineCap : lc ) {
            if ( defaultLc.equals( lineCap.getTypeCode() ) ) {
                return lineCap;
            }
        }
        return lc.get( 0 );
    }

    /**
     * @param opacity
     *            the opacity to return as percent value, returns 100 if opacity < 0 or 0 if opacity > 1
     * @return a value between 0 and 100, 100 means full transparent, 0 full opaque
     */
    public static int getOpacityInPercent( double opacity ) {
        if ( opacity < 0 ) {
            return 100;
        }
        if ( opacity > 1 ) {
            return 0;
        }
        return (int) Math.abs( ( ( opacity * 100 ) - 100 ) );
    }

    /**
     * @param opacityInPercent
     *            the opacity in percent to return as sld value, returns 0 if opacityInPercent > 100 or 1 if
     *            opacityInPercent < 0
     * @return a value between 0 and 1, 0 means full transparent, 1 full opaque
     */
    public static double getOpacity( double opacityInPercent ) {
        if ( opacityInPercent < 0 ) {
            return 1;
        }
        if ( opacityInPercent > 100 ) {
            return 0;
        }
        return ( 100d - opacityInPercent ) / 100d;
    }

}
