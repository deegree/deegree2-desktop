package org.deegree.desktop.views.swing.print;

import static org.deegree.desktop.views.LayerPane.createThemes;
import static org.deegree.framework.util.MapUtils.DEFAULT_PIXEL_SIZE;
import static org.deegree.graphics.MapFactory.createMapView;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterGraphics;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.LayerGroup;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.mapmodel.MapModelVisitor;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.utils.MapTools;
import org.deegree.graphics.MapView;
import org.deegree.graphics.Theme;
import org.deegree.model.spatialschema.Envelope;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class PrintableMap implements Printable {

    private static final ILogger LOG = LoggerFactory.getLogger( PrintableMap.class );

    private MapModel mapModel;

    private int iResMul = 4; // 1 = 72 dpi; 4 = 288 dpi

    /**
     * 
     * @param mapModel
     */
    PrintableMap( MapModel mapModel ) {
        this.mapModel = mapModel;
    }

    public int print( Graphics g, PageFormat pf, int iPage )
                            throws PrinterException {
        if ( 0 != iPage ) {
            return NO_SUCH_PAGE;
        }
        final List<Theme> themes = new ArrayList<Theme>();
        // calculate size of printable area
        int iWdth = (int) Math.round( pf.getImageableWidth() * iResMul ) - 3;
        int iHght = (int) Math.round( pf.getImageableHeight() * iResMul ) - 3;

        // store current map size
        int tWdth = mapModel.getTargetDevice().getPixelWidth();
        int tHght = mapModel.getTargetDevice().getPixelHeight();
        Envelope tEnv = mapModel.getEnvelope();

        // set map size to size of printable area
        mapModel.getTargetDevice().setPixelWidth( iWdth );
        mapModel.getTargetDevice().setPixelHeight( iHght );
        MapTools.adjustMapModelExtent( iWdth, iHght, mapModel );

        try {
            mapModel.walkLayerTree( new MapModelVisitor() {
                public void visit( Layer layer )
                                        throws Exception {
                    double mis = layer.getMinScaleDenominator();
                    double mxs = layer.getMaxScaleDenominator();
                    if ( layer.isVisible() && mis <= mapModel.getScaleDenominator()
                         && mxs >= mapModel.getScaleDenominator() ) {
                        List<Theme> layerThemes = createThemes( layer.getCurrentStyle(), layer.getDataAccess(),
                                                                mapModel.getCoordinateSystem() );
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

            MapView mv = createMapView( "iGeoDesktop", mapModel.getEnvelope(), mapModel.getCoordinateSystem(),
                                        tmp.toArray( new Theme[tmp.size()] ), DEFAULT_PIXEL_SIZE );

            Graphics2D g2 = (Graphics2D) g;
            g2.translate( pf.getImageableX(), pf.getImageableY() );
            g2.scale( 1.0 / iResMul, 1.0 / iResMul );
            g2.setClip( 0, 0, iWdth, iHght );
            mv.paint( g2 );
        } catch ( Exception e ) {
            LOG.logError( e );
            throw new PrinterException( e.getMessage() );
        } finally {
            // reset mapsize
            mapModel.getTargetDevice().setPixelWidth( tWdth );
            mapModel.getTargetDevice().setPixelHeight( tHght );
            mapModel.setEnvelope( tEnv );
        }
        return PAGE_EXISTS;
    }

    /**
     * @param g
     * @param pf
     * @param iPage
     * @throws PrinterException
     */
    public int print_( Graphics g, PageFormat pf, int iPage )
                            throws PrinterException {
        final int FONTSIZE = 12;
        final double PNT_MM = 25.4 / 72.;
        if ( 0 != iPage )
            return NO_SUCH_PAGE;
        try {
            int iPosX = 1;
            int iPosY = 1;
            int iAddY = FONTSIZE * 3 / 2 * iResMul;
            int iWdth = (int) Math.round( pf.getImageableWidth() * iResMul ) - 3;
            int iHght = (int) Math.round( pf.getImageableHeight() * iResMul ) - 3;
            int iCrcl = Math.min( iWdth, iHght ) - 4 * iResMul;
            Graphics2D g2 = (Graphics2D) g;
            PrinterJob prjob = ( (PrinterGraphics) g2 ).getPrinterJob();
            g2.translate( pf.getImageableX(), pf.getImageableY() );
            g2.scale( 1.0 / iResMul, 1.0 / iResMul );
            g2.setFont( new Font( "SansSerif", Font.PLAIN, FONTSIZE * iResMul ) );
            g2.setColor( Color.black );
            g2.drawRect( iPosX, iPosY, iWdth, iHght );
            g2.drawLine( iPosX, iHght / 2 + iWdth / 50, iPosX + iWdth, iHght / 2 - iWdth / 50 );
            g2.drawLine( iPosX, iHght / 2 - iWdth / 50, iPosX + iWdth, iHght / 2 + iWdth / 50 );
            g2.drawOval( iPosX + 2 * iResMul, iHght - iCrcl - 2 * iResMul, iCrcl, iCrcl );
            iPosX += iAddY;
            iPosY += iAddY / 2;
            g2.drawString( "PrinterJob-UserName: " + prjob.getUserName(), iPosX, iPosY += iAddY );
            g2.drawString( "Betriebssystem: " + System.getProperty( "os.name" ) + " "
                           + System.getProperty( "os.version" ), iPosX, iPosY += iAddY );
            g2.drawString( "Java-Version: JDK " + System.getProperty( "java.version" ), iPosX, iPosY += iAddY );
            g2.drawString( "Width/Height: " + dbldgt( pf.getWidth() ) + " / " + dbldgt( pf.getHeight() ) + " points = "
                           + dbldgt( pf.getWidth() * PNT_MM ) + " / " + dbldgt( pf.getHeight() * PNT_MM ) + " mm",
                           iPosX, iPosY += iAddY );
            g2.drawString( "Imageable Width/Height: " + dbldgt( pf.getImageableWidth() ) + " / "
                           + dbldgt( pf.getImageableHeight() ) + " points = "
                           + dbldgt( pf.getImageableWidth() * PNT_MM ) + " / "
                           + dbldgt( pf.getImageableHeight() * PNT_MM ) + " mm", iPosX, iPosY += iAddY );
            g2.drawString( "Imageable X/Y: " + dbldgt( pf.getImageableX() ) + " / " + dbldgt( pf.getImageableY() )
                           + " points = " + dbldgt( pf.getImageableX() * PNT_MM ) + " / "
                           + dbldgt( pf.getImageableY() * PNT_MM ) + " mm", iPosX, iPosY += iAddY );
            g2.drawString( "versuchte Druckauflösung: " + 72 * iResMul + " dpi", iPosX, iPosY += iAddY );
        } catch ( Exception ex ) {
            throw new PrinterException( ex.getMessage() );
        }
        return PAGE_EXISTS;
    }

    private static double dbldgt( double d ) {
        return Math.round( d * 10. ) / 10.; // show one digit after point
    }
}
