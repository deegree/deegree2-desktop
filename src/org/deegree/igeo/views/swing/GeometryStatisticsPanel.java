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

package org.deegree.igeo.views.swing;

import static java.awt.GridBagConstraints.WEST;
import static org.deegree.framework.log.LoggerFactory.getLogger;
import static org.deegree.igeo.i18n.Messages.get;
import static org.deegree.igeo.views.swing.util.GuiUtils.initPanel;

import java.awt.GridBagConstraints;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.deegree.framework.log.ILogger;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.CurveSegment;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Ring;
import org.deegree.model.spatialschema.Surface;
import org.deegree.model.spatialschema.SurfacePatch;

/**
 * <code>GeometryStatisticsPanel</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class GeometryStatisticsPanel extends JPanel {

    private static final long serialVersionUID = -3064427234044168687L;

    private static final ILogger LOG = getLogger( GeometryStatisticsPanel.class );

    private DecimalFormat df = new DecimalFormat();

    private DecimalFormat big = new DecimalFormat( "0.######E0" );

    private GridBagConstraints gb;

    /** * */
    public static double totalLength;

    /** * */
    public static double totalArea;

    /** * */
    public static double totalCurveLength;

    /** * */
    public static double totalInnerLength;

    /** * */
    public static double totalOuterLength;

    /**
     * Please note that the total* globals makes instance of this class inherently thread unsafe.
     */
    public static void resetGlobals() {
        totalLength = 0;
        totalArea = 0;
        totalCurveLength = 0;
        totalInnerLength = 0;
        totalOuterLength = 0;
    }

    /**
     * @param geometry
     */
    public GeometryStatisticsPanel( Geometry geometry ) {
        gb = initPanel( this );
        gb.anchor = WEST;

        df.setGroupingUsed( true );
        df.setGroupingSize( 3 );
        df.setMaximumFractionDigits( 2 );

        if ( geometry instanceof Curve ) {
            init( (Curve) geometry );
        } else if ( geometry instanceof MultiCurve ) {
            init( (MultiCurve) geometry );
        } else if ( geometry instanceof Surface ) {
            init( (Surface) geometry );
        } else if ( geometry instanceof MultiSurface ) {
            init( (MultiSurface) geometry );
        } else {
            // from global statistics
            add( new JLabel( get( "$MD10513" ) ), gb );
            ++gb.gridy;
            add( new JLabel( get( "$MD10514" ) ), gb );
            ++gb.gridy;
            add( new JLabel( get( "$MD11059" ) ), gb );
            ++gb.gridy;
            add( new JLabel( get( "$MD10501" ) ), gb );
            ++gb.gridy;
            add( new JLabel( get( "$MD10517" ) ), gb );

            gb.gridy = 0;
            ++gb.gridx;
            add( new JLabel( format( totalOuterLength ) ), gb );
            ++gb.gridy;
            add( new JLabel( format( totalInnerLength ) ), gb );
            ++gb.gridy;
            add( new JLabel( format( totalCurveLength ) ), gb );
            ++gb.gridy;
            add( new JLabel( format( totalLength ) ), gb );
            ++gb.gridy;
            add( new JLabel( format( totalArea ) ), gb );
        }
    }

    private String format( double d ) {
        return d > 1E10 ? big.format( d ) : df.format( d );
    }

    private String format( int i ) {
        return i > 1E10 ? big.format( i ) : df.format( i );
    }

    /**
     * @param c
     */
    public void init( Curve c ) {
        int numSegs = c.getNumberOfCurveSegments();
        add( new JLabel( get( "$MD10496" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10497" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10498" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10499" ) ), gb );

        gb.gridy = 0;
        ++gb.gridx;
        double length = c.getLength();
        totalLength += length;
        totalCurveLength += length;
        add( new JLabel( format( length ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( numSegs ) ), gb );
        ++gb.gridy;
        int cnt = 0;
        try {
            for ( CurveSegment seg : c.getCurveSegments() ) {
                cnt += seg.getNumberOfPoints();
            }
        } catch ( GeometryException e ) {
            LOG.logError( "Unknown error", e );
        }
        add( new JLabel( format( cnt ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( (double) cnt / (double) numSegs ) ), gb );
    }

    /**
     * @param mc
     */
    public void init( MultiCurve mc ) {
        add( new JLabel( get( "$MD10500" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10501" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10502" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10503" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10504" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10505" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10506" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10507" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10508" ) ), gb );

        double length = 0;
        int ps = 0, ss = 0, numCurves = mc.getAllCurves().length;
        for ( Curve c : mc.getAllCurves() ) {
            length += c.getLength();
            ss += c.getNumberOfCurveSegments();
            try {
                for ( CurveSegment cs : c.getCurveSegments() ) {
                    ps += cs.getNumberOfPoints();
                }
            } catch ( GeometryException e ) {
                LOG.logError( "Unknown error", e );
            }
        }
        totalLength += length;
        totalCurveLength += length;

        double lengthpc = length / numCurves;
        double segspc = (double) ss / (double) numCurves;
        double ppc = (double) ps / (double) numCurves;
        double pps = (double) ps / (double) ss;
        double lps = length / ss;

        gb.gridy = 0;
        ++gb.gridx;
        add( new JLabel( format( numCurves ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( length ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( ss ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( ps ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( lengthpc ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( lps ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( segspc ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( ppc ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( pps ) ), gb );
    }

    /**
     * @param s
     */
    public void init( Surface s ) {
        add( new JLabel( get( "$MD10509" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10510" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10511" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10512" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10513" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10514" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10515" ) ), gb );

        double area = s.getArea();
        totalArea += area;
        int numPatches = s.getNumberOfSurfacePatches();

        int rings = 0;
        int ps = 0;
        double outerLength = 0;
        double innerLength = 0;

        try {
            for ( int i = 0; i < numPatches; ++i ) {
                SurfacePatch p = s.getSurfacePatchAt( i );
                CurveSegment cs = p.getExterior().getAsCurveSegment();
                rings += p.getInterior() == null ? 1 : ( p.getInterior().length + 1 );
                ps += cs.getNumberOfPoints();
                outerLength += cs.getLength();
                if ( p.getInterior() != null ) {
                    for ( Ring r : p.getInterior() ) {
                        CurveSegment ics = r.getAsCurveSegment();
                        innerLength += ics.getLength();
                    }
                }
            }
        } catch ( GeometryException e ) {
            LOG.logError( "Unknown error", e );
        }

        totalLength += outerLength + innerLength;
        totalInnerLength += innerLength;
        totalOuterLength += outerLength;

        gb.gridy = 0;
        ++gb.gridx;
        add( new JLabel( format( area ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( numPatches ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( rings ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( ps ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( outerLength ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( innerLength ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( outerLength + innerLength ) ), gb );
    }

    /**
     * @param ms
     */
    public void init( MultiSurface ms ) {
        add( new JLabel( get( "$MD10516" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10517" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10518" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10519" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10520" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10521" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10522" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10523" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10524" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10525" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10526" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10527" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10528" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10529" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10530" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10531" ) ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10532" ) ), gb );

        int numSurfaces = ms.getSize();
        int numPatches = 0;
        double area = 0;
        long rings = 0;
        long ps = 0;
        double outerLength = 0;
        double innerLength = 0;

        try {
            for ( Surface s : ms.getAllSurfaces() ) {
                numPatches += s.getNumberOfSurfacePatches();
                area += s.getArea();
                for ( int i = 0; i < s.getNumberOfSurfacePatches(); ++i ) {
                    SurfacePatch p = s.getSurfacePatchAt( i );
                    rings += p.getInterior() == null ? 1 : ( p.getInterior().length + 1 );
                    CurveSegment outer = p.getExterior().getAsCurveSegment();
                    ps += outer.getNumberOfPoints();
                    outerLength += outer.getLength();
                    if ( p.getInterior() != null ) {
                        for ( Ring r : p.getInterior() ) {
                            CurveSegment inner = r.getAsCurveSegment();
                            ps += inner.getNumberOfPoints();
                            innerLength += inner.getLength();
                        }
                    }
                }
            }
        } catch ( GeometryException e ) {
            LOG.logError( "Unknown error", e );
        }

        double length = outerLength + innerLength;
        totalLength += length;
        totalArea += area;
        totalInnerLength += innerLength;
        totalOuterLength += outerLength;
        double patchesps = (double) numPatches / (double) numSurfaces;
        double rps = (double) rings / (double) numSurfaces;
        double pps = (double) ps / (double) numSurfaces;
        double rpp = (double) rings / (double) numPatches;
        double ppp = (double) ps / (double) numPatches;
        double lps = length / numSurfaces;
        double lpp = length / numPatches;
        double aps = area / numSurfaces;
        double app = area / numPatches;

        gb.gridy = 0;
        ++gb.gridx;
        add( new JLabel( format( numSurfaces ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( area ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( numPatches ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( rings ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( ps ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( outerLength ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( innerLength ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( length ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( patchesps ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( rps ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( pps ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( rpp ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( ppp ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( lps ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( lpp ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( aps ) ), gb );
        ++gb.gridy;
        add( new JLabel( format( app ) ), gb );
    }

}
