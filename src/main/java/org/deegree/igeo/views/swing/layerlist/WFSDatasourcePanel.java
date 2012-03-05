//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2009 by:
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.deegree.datatypes.QualifiedName;
import org.deegree.igeo.dataadapter.wfs.WFSFeatureAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.WFSDatasource;
import org.deegree.model.feature.schema.GeometryPropertyType;
import org.deegree.model.filterencoding.Filter;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class WFSDatasourcePanel extends DatasourceBasePanel {

    private static final long serialVersionUID = -4462789982344987860L;

    private JPanel pnFeatureType;

    private JComboBox cbFeatureTypes;

    private JScrollPane scFilter;

    private JTextField tfCapabilities;

    private JPanel pnCapa;

    private LazyLoadingPanel lazyLoadingPanel;

    private JTextArea taFilter;

    private JPanel pnFilter;

    private JComboBox cbGeom;

    private JPanel pnGeometry;

    private WFSFeatureAdapter adapter;

    private WFSDatasource datasource;

    /**
     * 
     * @param adapter
     */
    WFSDatasourcePanel( WFSFeatureAdapter adapter ) {
        this.adapter = adapter;
        this.datasource = (WFSDatasource) adapter.getDatasource();
        initGUI();
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            this.setPreferredSize( new java.awt.Dimension( 367, 636 ) );
            thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.1 };
            thisLayout.rowHeights = new int[] { 170, 63, 63, 66, 60, 7 };
            thisLayout.columnWeights = new double[] { 0.1 };
            thisLayout.columnWidths = new int[] { 7 };
            this.setLayout( thisLayout );
            {
                pnFeatureType = new JPanel();
                GridBagLayout pnFeatureTypeLayout = new GridBagLayout();
                this.add( pnFeatureType, new GridBagConstraints( 0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                 GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                 0 ) );
                pnFeatureType.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11246" ) ) );
                pnFeatureTypeLayout.rowWeights = new double[] { 0.1 };
                pnFeatureTypeLayout.rowHeights = new int[] { 7 };
                pnFeatureTypeLayout.columnWeights = new double[] { 0.1 };
                pnFeatureTypeLayout.columnWidths = new int[] { 7 };
                pnFeatureType.setLayout( pnFeatureTypeLayout );
                {
                    QualifiedName ft = datasource.getGetFeature().getQuery()[0].getTypeNames()[0];
                    cbFeatureTypes = new JComboBox( new Object[] { ft } );
                    pnFeatureType.add( cbFeatureTypes, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                               GridBagConstraints.CENTER,
                                                                               GridBagConstraints.HORIZONTAL,
                                                                               new Insets( 0, 10, 0, 10 ), 0, 0 ) );
                }
            }
            {
                pnGeometry = new JPanel();
                GridBagLayout pnGeometryLayout = new GridBagLayout();
                this.add( pnGeometry, new GridBagConstraints( 0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                              GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnGeometry.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11247" ) ) );
                pnGeometryLayout.rowWeights = new double[] { 0.1 };
                pnGeometryLayout.rowHeights = new int[] { 7 };
                pnGeometryLayout.columnWeights = new double[] { 0.1 };
                pnGeometryLayout.columnWidths = new int[] { 7 };
                pnGeometry.setLayout( pnGeometryLayout );
                {
                    GeometryPropertyType[] gpt = adapter.getSchema().getGeometryProperties();
                    Object[] o = new Object[gpt.length];
                    for ( int i = 0; i < gpt.length; i++ ) {
                        o[i] = gpt[i].getName();
                    }
                    cbGeom = new JComboBox( o );
                    pnGeometry.add( cbGeom, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                    GridBagConstraints.HORIZONTAL, new Insets( 0, 10,
                                                                                                               0, 10 ),
                                                                    0, 0 ) );
                }
            }
            {
                pnFilter = new JPanel();
                BorderLayout pnFilterLayout = new BorderLayout();
                pnFilter.setLayout( pnFilterLayout );
                this.add( pnFilter, new GridBagConstraints( 0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                            GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnFilter.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11248" ) ) );
                {
                    scFilter = new JScrollPane();
                    pnFilter.add( scFilter, BorderLayout.CENTER );
                    scFilter.setPreferredSize( new java.awt.Dimension( 76, 76 ) );
                    {
                        taFilter = new JTextArea();
                        scFilter.setViewportView( taFilter );
                        Filter filter = datasource.getGetFeature().getQuery()[0].getFilter();
                        if ( filter != null ) {
                            taFilter.setText( filter.to110XML().toString() );
                        }
                    }
                }
            }
            {
                datasourceCorePanel = new DatasourceCorePanel( adapter.getDatasource() );
                this.add( datasourceCorePanel, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                       GridBagConstraints.BOTH,
                                                                       new Insets( 0, 0, 0, 0 ), 0, 0 ) );
            }
            {
                lazyLoadingPanel = new LazyLoadingPanel( adapter.getDatasource() );
                this.add( lazyLoadingPanel, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                    GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ),
                                                                    0, 0 ) );
            }
            {
                pnCapa = new JPanel();
                GridBagLayout pnCapaLayout = new GridBagLayout();
                this.add( pnCapa, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                          GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnCapa.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11249" ) ) );
                pnCapaLayout.rowWeights = new double[] { 0.1 };
                pnCapaLayout.rowHeights = new int[] { 7 };
                pnCapaLayout.columnWeights = new double[] { 0.1 };
                pnCapaLayout.columnWidths = new int[] { 7 };
                pnCapa.setLayout( pnCapaLayout );
                {
                    tfCapabilities = new JTextField( datasource.getCapabilitiesURL().toURI().toASCIIString() );
                    pnCapa.add( tfCapabilities, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                        GridBagConstraints.CENTER,
                                                                        GridBagConstraints.HORIZONTAL,
                                                                        new Insets( 0, 10, 0, 10 ), 0, 0 ) );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @return URL to WFS capabilities
     */
    String getCapabilitiesURL() {
        return tfCapabilities.getText();
    }

    /**
     * 
     * @return <code>true</code> if data source is lazy loading
     */
    boolean isLazyLoading() {
        return lazyLoadingPanel.isLazyLoading();
    }

    /**
     * 
     * @return name of the geometry property to be used
     */
    QualifiedName getGeometryProperty() {
        return (QualifiedName) cbGeom.getSelectedItem();
    }

    /**
     * 
     * @return base GetFeature request assigned to a data source
     */
    GetFeature getGetFeature() {
        return datasource.getGetFeature();
    }

}
