//$HeadURL$
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

package org.deegree.igeo.views.swing.print;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.lang.Math.min;
import static java.lang.Math.round;
import static javax.imageio.ImageIO.write;
import static net.sf.jasperreports.engine.JasperExportManager.exportReportToHtmlFile;
import static net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfFile;
import static net.sf.jasperreports.engine.JasperExportManager.exportReportToXmlFile;
import static net.sf.jasperreports.engine.JasperFillManager.fillReport;
import static net.sf.jasperreports.engine.JasperPrintManager.printPageToImage;
import static org.deegree.framework.log.LoggerFactory.getLogger;
import static org.deegree.framework.util.MapUtils.DEFAULT_PIXEL_SIZE;
import static org.deegree.framework.util.MapUtils.calcScale;
import static org.deegree.framework.util.MapUtils.scaleEnvelope;
import static org.deegree.graphics.MapFactory.createMapView;
import static org.deegree.igeo.i18n.Messages.get;
import static org.deegree.igeo.views.DialogFactory.openConfirmDialogYESNO;
import static org.deegree.igeo.views.DialogFactory.openInformationDialog;
import static org.deegree.igeo.views.LayerPane.createThemes;
import static org.deegree.igeo.views.swing.util.GuiUtils.showErrorMessage;
import static org.deegree.igeo.views.swing.util.GuiUtils.unknownError;
import static org.deegree.igeo.views.swing.util.wizard.MiscActions.chainAction;
import static org.deegree.igeo.views.swing.util.wizard.MiscActions.monitorJListAction;
import static org.deegree.model.spatialschema.GeometryFactory.createEnvelope;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.util.FileUtils;
import org.deegree.framework.util.Pair;
import org.deegree.framework.utils.MapTools;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.graphics.MapView;
import org.deegree.graphics.RenderException;
import org.deegree.graphics.Theme;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.LayerGroup;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MapModelVisitor;
import org.deegree.igeo.views.swing.print.PrintOptionsPanel.Scale;
import org.deegree.igeo.views.swing.print.SelectTemplatePanel.Template;
import org.deegree.igeo.views.swing.util.IGeoFileFilter;
import org.deegree.igeo.views.swing.util.wizard.MiscActions.EmptyAction;
import org.deegree.igeo.views.swing.util.wizard.Wizard.Action;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * <code>PrintActions</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class PrintActions {

    static final ILogger LOG = getLogger( PrintActions.class );

    /**
     * @param options
     * @return a new action
     */
    public static Action optionsAction( final PrintOptionsPanel options ) {
        return new EmptyAction() {
            @Override
            public void addListener( ActionListener listener ) {
                options.scale.addActionListener( listener );
                options.atLeastVisible.addActionListener( listener );
                options.selectScale.addActionListener( listener );
                options.complex.addActionListener( listener );
            }

            @Override
            public boolean canForward() {
                return options.selectScale.isSelected() ? options.scale.getSelectedItem() != null
                                                          && options.scale.getSelectedItem() instanceof Scale : true;
            }

            @Override
            public boolean forward() {
                if ( options.file == null && options.fileChanged ) {
                    options.file = new File( options.fileField.getText() );
                    if ( options.file.exists() ) {
                        if ( !openConfirmDialogYESNO( "Application", options,
                                                      get( "$DI10022", options.file.toString() ), get( "$DI10019" ) ) ) {
                            options.file = null;
                            return false;
                        }
                    }
                }
                if ( options.file == null ) {
                    openInformationDialog( "Application", options, get( "$MD10357" ), get( "$DI10018" ) );
                    return false;
                }

                return true;
            }
        };
    }

    /**
     * @param reportPanel
     * @param mm
     * @param options
     * @param dpi
     * @param wmsSizeScale
     * @return the print action
     */
    public static Action printAction( final JasperReportPanel reportPanel, final MapModel mm,
                                      final PrintOptionsPanel options, final int dpi, final double wmsSizeScale ) {
        return new EmptyAction() {
            @Override
            public boolean forward() {
                Map<String, Object> parameters = new HashMap<String, Object>();

                try {
                    int width = options.width.getInt();
                    int height = options.height.getInt();

                    if ( options.atLeastVisible.isSelected() || options.selectScale.isSelected() ) {
                        Pair<Integer, Integer> dim = extractMapDimension( reportPanel );
                        calculatePrinterFriendlySize( dim, dpi );
                        width = dim.first;
                        height = dim.second;
                    }

                    final List<Theme> themes = new ArrayList<Theme>();
                    final Envelope originalExtent = mm.getEnvelope();

                    final Envelope extent;

                    if ( options.selectScale.isSelected() ) {
                        Envelope e = scaleEnvelope( originalExtent, calcScale( mm.getTargetDevice().getPixelWidth(),
                                                                               mm.getTargetDevice().getPixelHeight(),
                                                                               originalExtent,
                                                                               originalExtent.getCoordinateSystem(),
                                                                               DEFAULT_PIXEL_SIZE ),
                                                    ( (Scale) options.scale.getSelectedItem() ).scale );
                        extent = fitEnvelope( e, width, height );
                    } else {
                        extent = fitEnvelope( originalExtent, width, height );
                    }

                    mm.setEnvelope( extent );

                    mm.walkLayerTree( new MapModelVisitor() {
                        public void visit( Layer layer )
                                                throws Exception {
                            double mis = layer.getMinScaleDenominator();
                            double mxs = layer.getMaxScaleDenominator();
                            if ( layer.isVisible() && mis <= mm.getScaleDenominator()
                                 && mxs >= mm.getScaleDenominator() ) {
                                List<Theme> layerThemes = createThemes( layer.getCurrentStyle(), layer.getDataAccess(),
                                                                        extent.getCoordinateSystem() );
                                themes.addAll( layerThemes );
                            }
                        }

                        public void visit( LayerGroup layerGroup )
                                                throws Exception {
                            // not using grouping nodes
                        }
                    } );
                    BufferedImage img = renderMap( mm, width, height, themes, extent, wmsSizeScale );
                    parameters.put( "MAP", img );
                    mm.setEnvelope( originalExtent );

                    reportPanel.fillParameterMap( parameters );

                    img = renderLegend( mm, reportPanel, dpi );
                    parameters.put( "LEGEND", img );

                    parameters.put( "SCALE", "1 : " + Math.round( mm.getScaleDenominator() ) );

                    if ( reportPanel.report.getRootElement().getAttribute( "language" ) == null
                         || reportPanel.report.getRootElement().getAttribute( "language" ).trim().length() == 0 ) {
                        // dirty hack to fix a bug in iReport 3.1.x
                        // Newer Jasper templates are defined in name space
                        // http://jasperreports.sourceforge.net/jasperreports
                        // In this case the root element must contain the attribute 'language="java"' to enable
                        // compilation of it. iReport 3.1.x does not create this attribute
                        reportPanel.report.getRootElement().setAttribute( "language", "java" );
                    }

                    JasperReport jasperReport = null;
                    try {
                        String loc = reportPanel.report.getSystemId().toExternalForm();
                        int idx = loc.lastIndexOf( '.' );
                        URL url = new URL( loc.substring( 0, idx ) + ".jasper" );
                        LOG.logInfo( "try using tempale: ", url );
                        jasperReport = (JasperReport) JRLoader.loadObject( url );
                    } catch ( Throwable e ) {                        
                        String s = FileUtils.readTextFile( reportPanel.report.getSystemId() ).toString();
                        LOG.logInfo( "compiled tempale not available, use xml file: ", s );
                        JasperDesign jasperDesign = net.sf.jasperreports.engine.xml.JRXmlLoader.load( new ByteArrayInputStream(
                                                                                                                                s.getBytes() ) );
                        jasperReport = net.sf.jasperreports.engine.JasperCompileManager.compileReport( jasperDesign );
                    }
                    JasperPrint print = fillReport( jasperReport, parameters, new JREmptyDataSource() );

                    if ( IGeoFileFilter.PDF.accept( options.file ) ) {
                        exportReportToPdfFile( print, options.file.toString() );
                    }
                    if ( IGeoFileFilter.HTML.accept( options.file ) ) {
                        exportReportToHtmlFile( print, options.file.toString() );
                    }
                    if ( IGeoFileFilter.XML.accept( options.file ) ) {
                        exportReportToXmlFile( print, options.file.toString(), true );
                    }
                    if ( IGeoFileFilter.JPEG.accept( options.file ) || IGeoFileFilter.PNG.accept( options.file ) ) {
                        Image result = printPageToImage( print, 0, 1 );
                        if ( IGeoFileFilter.JPEG.accept( options.file ) ) {
                            write( (RenderedImage) result, "jpg", options.file );
                        }
                        if ( IGeoFileFilter.PNG.accept( options.file ) ) {
                            write( (RenderedImage) result, "png", options.file );
                        }
                    }
                } catch ( IOException e ) {
                    unknownError( LOG, e, reportPanel ); // the template was already loaded
                    // before...
                } catch ( JRException e ) {
                    showErrorMessage( reportPanel, get( "$MD10358", e.getMessage() ), e );
                    e.printStackTrace();
                } catch ( RenderException e ) {
                    showErrorMessage( reportPanel, get( "$MD10359", e.getMessage() ), e );
                    e.printStackTrace();
                } catch ( Exception e ) {
                    unknownError( LOG, e, reportPanel );
                    e.printStackTrace();
                }
                return true;
            }

            private BufferedImage renderLegend( MapModel mm, JasperReportPanel jasper, int dpi )
                                    throws Exception {
                BufferedImage legend = MapTools.getLegendAsImage( mm, true );
                Pair<Integer, Integer> dim = extractLegendDimension( jasper );
                calculatePrinterFriendlySize( dim, dpi );
                float qx = ( (float) dim.first ) / (float) legend.getWidth();
                float qy = ( (float) dim.second ) / (float) legend.getHeight();
                float q = qy;
                if ( qx < qy ) {
                    q = qx;
                }
                // scale a bit down for better looking
                q *= 0.98f;
                BufferedImage out = new BufferedImage( dim.first, dim.second, BufferedImage.TYPE_INT_RGB );
                Graphics g = out.getGraphics();
                g.setColor( Color.WHITE );
                g.fillRect( 0, 0, dim.first, dim.second );
                if ( q < 1 ) {
                    // scale down if necessary
                    g.drawImage( legend, 0, 0, Math.round( legend.getWidth() * q ),
                                 Math.round( legend.getHeight() * q ), null );
                    g.dispose();
                } else {
                    // scale up a little so the legend becomes readable
                    g.drawImage( legend, 0, 0, round( min( dim.first, legend.getWidth() * 3 ) ),
                                 round( min( dim.second, legend.getHeight() * 3 ) ), null );
                }
                g.dispose();
                return out;
            }

            /**
             * 
             * @param mm
             *            MapModel
             * @param width
             *            map width
             * @param height
             *            map height
             * @param themes
             *            list of tThemes to be rendered
             * @param extent
             *            map extent for rendering (may differ from current map extent)
             * @param wmsSizeScale
             * @return map rendered onto a BufferedImage
             * @throws Exception
             */
            private BufferedImage renderMap( final MapModel mm, int width, int height, final List<Theme> themes,
                                             final Envelope extent, final double wmsSizeScale )
                                    throws Exception {
                List<Theme> tmp = new ArrayList<Theme>();
                for ( int i = themes.size() - 1; i >= 0; i-- ) {
                    tmp.add( themes.get( i ) );
                }

                int h = mm.getTargetDevice().getPixelHeight();
                int w = mm.getTargetDevice().getPixelWidth();
                if ( wmsSizeScale >= 1 ) {
                    int hh = (int) Math.round( height / wmsSizeScale );
                    mm.getTargetDevice().setPixelHeight( hh );
                    int ww = (int) Math.round( width / wmsSizeScale );
                    mm.getTargetDevice().setPixelWidth( ww );
                }
                BufferedImage img = null;
                try {
                    MapView mv = createMapView( "iGeoDesktop", extent, extent.getCoordinateSystem(),
                                                tmp.toArray( new Theme[tmp.size()] ), DEFAULT_PIXEL_SIZE );

                    img = new BufferedImage( width, height, TYPE_INT_RGB );
                    Graphics2D g = img.createGraphics();
                    g.setBackground( new Color( 0 ) );
                    g.fillRect( 0, 0, width, height );
                    g.setClip( 0, 0, width, height );
                    mv.paint( g );
                    g.dispose();
                   
                } catch ( Exception e ) {
                    throw e;
                } finally {
                    mm.getTargetDevice().setPixelHeight( h );
                    mm.getTargetDevice().setPixelWidth( w );
                }
                return img;
            }
        };
    }

    static Pair<Integer, Integer> extractMapDimension( JasperReportPanel jasper )
                            throws XMLParsingException {
        NamespaceContext nsc = CommonNamespaces.getNamespaceContext();
        nsc.addNamespace( "jasper", URI.create( "http://jasperreports.sourceforge.net/jasperreports" ) );

        Element root = jasper.report.getRootElement();
        // old jasper version
        int width = XMLTools.getNodeAsInt( root, "//image/reportElement/@width" + "[../../imageExpression='$P{MAP}']",
                                           null, -1 );
        int height = XMLTools.getNodeAsInt( root,
                                            "//image/reportElement/@height" + "[../../imageExpression='$P{MAP}']",
                                            null, -1 );
        if ( width == -1 ) {
            // new jasper version
            width = XMLTools.getRequiredNodeAsInt( root, "//jasper:image/jasper:reportElement/@width"
                                                         + "[../../jasper:imageExpression='$P{MAP}']", nsc );
            height = XMLTools.getRequiredNodeAsInt( root, "//jasper:image/jasper:reportElement/@height"
                                                          + "[../../jasper:imageExpression='$P{MAP}']", nsc );
        }

        return new Pair<Integer, Integer>( width, height );
    }

    static Pair<Integer, Integer> extractLegendDimension( JasperReportPanel jasper )
                            throws XMLParsingException {
        NamespaceContext nsc = CommonNamespaces.getNamespaceContext();
        nsc.addNamespace( "jasper", URI.create( "http://jasperreports.sourceforge.net/jasperreports" ) );

        Element root = jasper.report.getRootElement();
        // old jasper version
        int width = XMLTools.getNodeAsInt( root, "//image/reportElement/@width"
                                                 + "[../../imageExpression='$P{LEGEND}']", null, -1 );
        int height = XMLTools.getNodeAsInt( root, "//image/reportElement/@height"
                                                  + "[../../imageExpression='$P{LEGEND}']", null, -1 );
        if ( width == -1 ) {
            // new jasper version
            width = XMLTools.getRequiredNodeAsInt( root, "//jasper:image/jasper:reportElement/@width"
                                                         + "[../../jasper:imageExpression='$P{LEGEND}']", nsc );
            height = XMLTools.getRequiredNodeAsInt( root, "//jasper:image/jasper:reportElement/@height"
                                                          + "[../../jasper:imageExpression='$P{LEGEND}']", nsc );
        }

        return new Pair<Integer, Integer>( width, height );
    }

    static void calculatePrinterFriendlySize( Pair<Integer, Integer> pixels, int dpi ) {
        pixels.first = (int) round( pixels.first / 72d * dpi );
        pixels.second = (int) round( pixels.second / 72d * dpi );
    }

    static Envelope fitEnvelope( Envelope env, int width, int height ) {
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

        env = createEnvelope( minx, miny, maxx, maxy, env.getCoordinateSystem() );

        return createEnvelope( minx, miny, maxx, maxy, env.getCoordinateSystem() );
    }

    /**
     * @param fromList
     * @param jasper
     * @param optionsPanel
     * @param dpi
     * @return a new action
     */
    public static Action selectTemplateAction( final SelectTemplatePanel fromList, final JasperReportPanel jasper,
                                               final PrintOptionsPanel optionsPanel, final int dpi ) {
        return chainAction( monitorJListAction( fromList.list ), new EmptyAction() {
            @Override
            public boolean forward() {
                Template template = (Template) fromList.list.getSelectedValue();
                try {
                    XMLFragment doc = new XMLFragment( template.location );
                    jasper.init( doc );

                    Pair<Integer, Integer> dim = extractMapDimension( jasper );
                    calculatePrinterFriendlySize( dim, dpi );
                    optionsPanel.width.setInt( dim.first );
                    optionsPanel.height.setInt( dim.second );
                    optionsPanel.calculateAspectRatio();

                    return true;
                } catch ( IOException e ) {
                    showErrorMessage( fromList, get( "$MD10360", e.getMessage() ), e );
                } catch ( SAXException e ) {
                    showErrorMessage( fromList, get( "$MD10361", e.getMessage() ), e );
                } catch ( XMLParsingException e ) {
                    showErrorMessage( fromList, get( "$MD10362", e.getMessage() ), e );
                }
                return false;
            }
        } );
    }

}
