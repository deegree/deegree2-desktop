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

package org.deegree.igeo.views.swing.layerlist;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.deegree.framework.util.GeometryUtils;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.MultiCurve;
import org.deegree.model.spatialschema.MultiPoint;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Ring;
import org.deegree.model.spatialschema.Surface;
import org.deegree.model.spatialschema.SurfaceBoundary;

/**
 * Panel for calculating and displaying layer statistics. At the moment following statistical values - depending on
 * available geometry types - will be calculated/displayed:
 * <ul>
 * <li>number of features
 * <li>mean distance between features
 * <li>number of vertices
 * <li>mean number of vertices per feature
 * <li>number of polygon inner rings
 * <li>mean number of inner rings per polygon
 * <li>total area (polygons)
 * <li>mean area (polygons)
 * <li>max area (polygons)
 * <li>min area (polygons)
 * <li>total linestring length
 * <li>mean linestring length
 * <li>min linestring length
 * <li>max linestring length
 * <li>total boundary length (polygon)
 * <li>total inner boundary length (polygon)
 * <li>mean boundary length (polygon)
 * <li>mean inner boundary length (polygon)
 * <li>minimum outter boundary length
 * <li>maximum outter boundary length
 * </ul>
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class LayerStatisticPanel extends JPanel {

    private static final long serialVersionUID = 7309786709997995099L;

    @SuppressWarnings("unused")
    private ApplicationContainer<Container> appCont;

    /**
     * 
     * @param appCont
     */
    LayerStatisticPanel( ApplicationContainer<Container> appCont ) {
        this.appCont = appCont;
    }

    /**
     * 
     * @param layer
     */
    void init( Layer layer ) {
        String[] tabHeader = new String[] { Messages.getMessage( Locale.getDefault(), "$MD10111" ),
                                           Messages.getMessage( Locale.getDefault(), "$MD10112" ) };
        Object[][] data = new Object[1][2];
        List<DataAccessAdapter> list = layer.getDataAccess();
        int cnt = 0;
        for ( DataAccessAdapter adapter : list ) {
            if ( adapter instanceof FeatureAdapter ) {
                if ( ( (FeatureAdapter) adapter ).getFeatureCollection() != null ) {
                    cnt += ( (FeatureAdapter) adapter ).getFeatureCollection().size();
                }
            }
        }
        data[0][0] = Messages.getMessage( Locale.getDefault(), "$MD10113" );
        data[0][1] = cnt;

        // TODO
        // add futher parameters

        JTable table = new JTable( new DefaultTableModel( data, tabHeader ) );
        table.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
        JPanel center = new JPanel( new BorderLayout() );
        center.add( table.getTableHeader(), BorderLayout.PAGE_START );
        center.add( table, BorderLayout.CENTER );

        JButton calcbt = new JButton( Messages.getMessage( Locale.getDefault(), "$MD10116" ) );
        calcbt.addActionListener( new LSPActionListener( layer, table ) );
        JPanel panel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        panel.add( calcbt );
        center.add( panel, BorderLayout.SOUTH );
        add( center, BorderLayout.CENTER );
    }

    // ///////////////////////////////////////////////////////////////////////////
    // inner classes
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    private class LSPActionListener implements ActionListener {

        private JTable table;

        private Layer layer;

        /**
         * 
         * @param layer
         * @param table
         * @param noOfFeat
         */
        LSPActionListener( Layer layer, JTable table ) {
            this.layer = layer;
            this.table = table;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed( ActionEvent evt ) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            addDistance( model );
            addArea( model );
            addLength( model );
            try {
                addBoundary( model );
                addVertices( model );
            } catch ( Exception e ) {
                // do nothing
            }
        }

        private void addVertices( DefaultTableModel model )
                                throws GeometryException {
            float cnt = 0;
            float vcnt = 0;
            List<DataAccessAdapter> list = layer.getDataAccess();
            for ( DataAccessAdapter adapter : list ) {
                if ( adapter instanceof FeatureAdapter ) {
                    FeatureCollection fc = ( (FeatureAdapter) adapter ).getFeatureCollection();
                    cnt = fc.size();
                    Iterator<Feature> iterator = fc.iterator();
                    while ( iterator.hasNext() ) {
                        Feature feature = iterator.next();
                        Geometry geom = feature.getDefaultGeometryPropertyValue();
                        if ( geom instanceof Point ) {
                            vcnt++;
                        } else if ( geom instanceof MultiPoint ) {
                            vcnt += ( (MultiPoint) geom ).getSize();
                        } else if ( geom instanceof Curve ) {
                            vcnt += ( (Curve) geom ).getAsLineString().getNumberOfPoints();
                        } else if ( geom instanceof MultiCurve ) {
                            Curve[] curves = ( (MultiCurve) geom ).getAllCurves();
                            for ( Curve curve : curves ) {
                                vcnt += curve.getAsLineString().getNumberOfPoints();
                            }
                        } else if ( geom instanceof Surface ) {
                            SurfaceBoundary sb = ( (Surface) geom ).getSurfaceBoundary();
                            vcnt += sb.getExteriorRing().getPositions().length;
                            Ring[] rings = sb.getInteriorRings();
                            for ( Ring ring : rings ) {
                                vcnt += ring.getPositions().length;
                            }
                        } else if ( geom instanceof MultiSurface ) {
                            Surface[] surfaces = ( (MultiSurface) geom ).getAllSurfaces();
                            for ( Surface surface : surfaces ) {
                                SurfaceBoundary sb = surface.getSurfaceBoundary();
                                vcnt += sb.getExteriorRing().getPositions().length;
                                Ring[] rings = sb.getInteriorRings();
                                for ( Ring ring : rings ) {
                                    vcnt += ring.getPositions().length;
                                }
                            }
                        }
                    }
                }
            }

            Object[] rowData = new Object[2];
            rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10494" );
            rowData[1] = (int) vcnt;
            model.addRow( rowData );

            rowData = new Object[2];
            rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10495" );
            rowData[1] = vcnt / cnt;
            model.addRow( rowData );

        }

        private void addBoundary( DefaultTableModel model )
                                throws Exception {
            List<DataAccessAdapter> list = layer.getDataAccess();
            // number of surfaces/multisurfaces
            float cnt = 0;
            // number of iner rings
            float icnt = 0;
            // length of outter boundaries
            double meanLength = 0;
            // outter boundaries min length
            double minLength = Double.MAX_VALUE;
            // outter boundaries max length
            double maxLength = Double.MIN_VALUE;
            // length of inner boundaries
            double innerLength = Double.MIN_VALUE;
            float holes = 0;
            for ( DataAccessAdapter adapter : list ) {
                if ( adapter instanceof FeatureAdapter ) {
                    FeatureCollection fc = ( (FeatureAdapter) adapter ).getFeatureCollection();
                    Iterator<Feature> iterator = fc.iterator();
                    while ( iterator.hasNext() ) {
                        Feature feature = iterator.next();
                        Geometry geom = feature.getDefaultGeometryPropertyValue();
                        double a = 0;
                        double b = 0;
                        if ( geom instanceof Surface ) {
                            SurfaceBoundary sb = ( (Surface) geom ).getSurfaceBoundary();
                            a = sb.getExteriorRing().getAsCurveSegment().getLength();
                            Ring[] rings = sb.getInteriorRings();
                            holes += rings.length;
                            for ( Ring ring : rings ) {
                                b += ring.getAsCurveSegment().getLength();
                                icnt++;
                            }
                        }
                        if ( geom instanceof MultiSurface ) {
                            Surface[] surfaces = ( (MultiSurface) geom ).getAllSurfaces();
                            for ( Surface surface : surfaces ) {
                                SurfaceBoundary sb = surface.getSurfaceBoundary();
                                a = sb.getExteriorRing().getAsCurveSegment().getLength();
                                Ring[] rings = sb.getInteriorRings();
                                holes += rings.length;
                                for ( Ring ring : rings ) {
                                    b += ring.getAsCurveSegment().getLength();
                                    icnt++;
                                }
                            }
                        }
                        if ( a > 0 ) {
                            meanLength += a;
                            innerLength += b;
                            cnt++;
                            if ( a < minLength ) {
                                minLength = a;
                            }
                            if ( a > maxLength ) {
                                maxLength = a;
                            }
                        }
                    }
                }
            }

            if ( meanLength > 0 ) {
                Object[] rowData = new Object[2];
                rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10486" );
                rowData[1] = ( meanLength + innerLength ) / cnt;
                model.addRow( rowData );

                rowData = new Object[2];
                rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10487" );
                rowData[1] = minLength;
                model.addRow( rowData );

                rowData = new Object[2];
                rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10488" );
                rowData[1] = maxLength;
                model.addRow( rowData );

                rowData = new Object[2];
                rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10489" );
                rowData[1] = meanLength + innerLength;
                model.addRow( rowData );

                rowData = new Object[2];
                rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10490" );
                rowData[1] = (int) icnt;
                model.addRow( rowData );

                rowData = new Object[2];
                rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10493" );
                rowData[1] = icnt / cnt;
                model.addRow( rowData );

                rowData = new Object[2];
                rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10491" );
                rowData[1] = innerLength;
                model.addRow( rowData );

                rowData = new Object[2];
                rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10492" );
                rowData[1] = innerLength / icnt;
                model.addRow( rowData );

            }

        }

        private void addLength( DefaultTableModel model ) {
            List<DataAccessAdapter> list = layer.getDataAccess();
            // number of curves/multicurves
            float cnt = 0;
            // curves length
            double meanLength = 0;
            // curves minimum length
            double minLength = Double.MAX_VALUE;
            // curves maximum length
            double maxLength = Double.MIN_VALUE;
            for ( DataAccessAdapter adapter : list ) {
                if ( adapter instanceof FeatureAdapter ) {
                    FeatureCollection fc = ( (FeatureAdapter) adapter ).getFeatureCollection();
                    Iterator<Feature> iterator = fc.iterator();
                    while ( iterator.hasNext() ) {
                        Feature feature = iterator.next();
                        Geometry geom = feature.getDefaultGeometryPropertyValue();
                        double a = 0;
                        if ( geom instanceof Curve ) {
                            a = ( (Curve) geom ).getLength();
                        }
                        if ( geom instanceof MultiCurve ) {
                            Curve[] curves = ( (MultiCurve) geom ).getAllCurves();
                            for ( Curve curve : curves ) {
                                a += curve.getLength();
                            }
                        }
                        if ( a > 0 ) {
                            meanLength += a;
                            cnt++;
                            if ( a < minLength ) {
                                minLength = a;
                            }
                            if ( a > maxLength ) {
                                maxLength = a;
                            }
                        }
                    }
                }
            }

            if ( meanLength > 0 ) {
                Object[] rowData = new Object[2];
                rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10482" );
                rowData[1] = meanLength / cnt;
                model.addRow( rowData );

                rowData = new Object[2];
                rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10483" );
                rowData[1] = minLength;
                model.addRow( rowData );

                rowData = new Object[2];
                rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10484" );
                rowData[1] = maxLength;
                model.addRow( rowData );

                rowData = new Object[2];
                rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10485" );
                rowData[1] = meanLength;
                model.addRow( rowData );
            }

        }

        private void addArea( DefaultTableModel model ) {
            List<DataAccessAdapter> list = layer.getDataAccess();
            // number of surfaces/multisurfaces
            float cnt = 0;
            // surfaces area
            double meanArea = 0;
            // surfaces minimum area
            double minArea = Double.MAX_VALUE;
            // surfaces minimum area
            double maxArea = Double.MIN_VALUE;
            for ( DataAccessAdapter adapter : list ) {
                if ( adapter instanceof FeatureAdapter ) {
                    FeatureCollection fc = ( (FeatureAdapter) adapter ).getFeatureCollection();
                    Iterator<Feature> iterator = fc.iterator();
                    while ( iterator.hasNext() ) {
                        Feature feature = iterator.next();
                        Geometry geom = feature.getDefaultGeometryPropertyValue();
                        double a = 0;
                        if ( geom instanceof Surface ) {
                            a = ( (Surface) geom ).getArea();
                        }
                        if ( geom instanceof MultiSurface ) {
                            a = ( (MultiSurface) geom ).getArea();
                        }
                        if ( a > 0 ) {
                            meanArea += a;
                            cnt++;
                            if ( a < minArea ) {
                                minArea = a;
                            }
                            if ( a > maxArea ) {
                                maxArea = a;
                            }
                        }
                    }
                }
            }

            if ( meanArea > 0 ) {
                Object[] rowData = new Object[2];
                rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10478" );
                rowData[1] = meanArea / cnt;
                model.addRow( rowData );

                rowData = new Object[2];
                rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10479" );
                rowData[1] = minArea;
                model.addRow( rowData );

                rowData = new Object[2];
                rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10480" );
                rowData[1] = maxArea;
                model.addRow( rowData );

                rowData = new Object[2];
                rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10481" );
                rowData[1] = meanArea;
                model.addRow( rowData );
            }

        }

        private void addDistance( DefaultTableModel model ) {
            List<DataAccessAdapter> list = layer.getDataAccess();
            float cnt = 0;
            double meanDist = 0;
            double minDist = Double.MAX_VALUE;
            double maxDist = Double.MIN_VALUE;
            for ( DataAccessAdapter adapter : list ) {
                if ( adapter instanceof FeatureAdapter ) {
                    FeatureCollection fc = ( (FeatureAdapter) adapter ).getFeatureCollection();
                    Feature[] f1 = fc.toArray();
                    for ( int i = 0; i < f1.length; i++ ) {
                        Point p1 = f1[i].getDefaultGeometryPropertyValue().getCentroid();
                        for ( int j = i + 1; j < f1.length; j++ ) {
                            Point p2 = f1[j].getDefaultGeometryPropertyValue().getCentroid();
                            double d = GeometryUtils.distance( p1.getPosition(), p2.getPosition() );
                            meanDist += d;
                            if ( d < minDist ) {
                                minDist = d;
                            }
                            if ( d > maxDist ) {
                                maxDist = d;
                            }
                            cnt++;
                        }
                    }
                }
            }
            Object[] rowData = new Object[2];
            rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10475" );
            rowData[1] = meanDist / cnt;
            model.addRow( rowData );

            rowData = new Object[2];
            rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10476" );
            rowData[1] = minDist;
            model.addRow( rowData );

            rowData = new Object[2];
            rowData[0] = Messages.getMessage( Locale.getDefault(), "$MD10477" );
            rowData[1] = maxDist;
            model.addRow( rowData );
        }

    }

}
