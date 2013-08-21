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
package org.deegree.desktop.views.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.commands.geoprocessing.ValidateGeometriesCommand;
import org.deegree.desktop.commands.geoprocessing.ValidateLayer4GeometriesCommand;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.settings.ValidationGeomMetrics;
import org.deegree.desktop.settings.ValidationGeomTopology;
import org.deegree.desktop.settings.ValidationGeomTypes;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.desktop.views.HelpManager;
import org.deegree.desktop.views.swing.util.IconRegistry;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.kernel.Command;
import org.deegree.kernel.CommandProcessedEvent;
import org.deegree.kernel.CommandProcessedListener;
import org.deegree.kernel.ProcessMonitor;
import org.deegree.kernel.ProcessMonitorFactory;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.spatialschema.Geometry;

/**
 * Panel for selecting and invoking validations on selected geometries or a layer. Settings made in the GUI will be
 * read/stored from/in deegreeConfiguration.xml.
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class GeometryValidationPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 3545341231564908617L;

    private static final ILogger LOG = LoggerFactory.getLogger( GeometryValidationPanel.class );

    private JPanel geommetrics;

    private JPanel geomtypes;

    private JCheckBox cbAllowHoles;

    private JCheckBox cbAllowMultiPolys;

    private JCheckBox cbAllowMultiLines;

    private JCheckBox cbAllowMultiPoints;

    private JCheckBox cbAllowPolygons;

    private JCheckBox cbAllowLines;

    private JCheckBox cbAllowPoints;

    private JCheckBox cbRepeatedPoints;

    private JTextField tfMinPolyArea;

    private JCheckBox cbMinPolyArea;

    private JTextField tfMinSegLength;

    private JCheckBox cbMinSegmentLength;

    private JCheckBox cbAllowEqualGeometries;

    private JCheckBox cbAllowTouching;

    private JCheckBox cbAllowIntersection;

    private JPanel topologyPanel;

    private JCheckBox cbSimpleLines;

    private JPanel jPanel1;

    private JPanel checkButtonPanel;

    private JCheckBox cbPrintWarnings;

    private JButton btHelp;

    private JCheckBox cbAllowGeometryCollections;

    private JButton btCheckGeometries;

    private JButton btCheckLayer;

    private JPanel buttonPanel;

    private JCheckBox cbAllowNoneLinearInterpolations;

    private JCheckBox cbDoubleGeometries;

    private JCheckBox cbPolygonOrientation;

    private JCheckBox cbGeometry;

    private JTabbedPane jTabbedPane1;

    private CheckActionListener checkListener = new CheckActionListener();

    private ApplicationContainer<Container> appContainer;

    /**
     * 
     * @param appContainer
     */
    public GeometryValidationPanel( ApplicationContainer<Container> appContainer ) {
        this.appContainer = appContainer;
        initGUI();
    }

    private void initGUI() {
        final ValidationGeomMetrics vm = appContainer.getSettings().getValidationGeomMetrics();
        final ValidationGeomTypes vgt = appContainer.getSettings().getValidationGeomTypes();
        final ValidationGeomTopology vgto = appContainer.getSettings().getValidationGeomTopology();
        try {
            this.setPreferredSize( new java.awt.Dimension( 360, 311 ) );
            BorderLayout thisLayout = new BorderLayout();
            this.setLayout( thisLayout );
            this.setSize( 360, 311 );
            {
                jTabbedPane1 = new JTabbedPane();
                this.add( jTabbedPane1, BorderLayout.NORTH );
                {
                    geommetrics = new JPanel();
                    geommetrics.addComponentListener( new ComponentAdapter() {

                        public void componentShown( ComponentEvent e ) {
                            if ( btCheckGeometries != null )
                                btCheckGeometries.setEnabled( true );
                        }

                    } );
                    GridBagLayout geommetricsLayout = new GridBagLayout();
                    jTabbedPane1.addTab( Messages.getMessage( getLocale(), "$MD11196" ), null, geommetrics, null );
                    geommetricsLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
                    geommetricsLayout.rowHeights = new int[] { 28, 28, 28, 28, 28, 28, 35 };
                    geommetricsLayout.columnWeights = new double[] { 0.0, 0.1 };
                    geommetricsLayout.columnWidths = new int[] { 247, 7 };
                    geommetrics.setLayout( geommetricsLayout );
                    {
                        cbGeometry = new JCheckBox();
                        cbGeometry.setEnabled( vm.isChangeable() );
                        geommetrics.add( cbGeometry, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                             GridBagConstraints.WEST,
                                                                             GridBagConstraints.HORIZONTAL,
                                                                             new Insets( 0, 5, 0, 0 ), 0, 0 ) );
                        cbGeometry.setText( Messages.getMessage( getLocale(), "$MD10388" ) );
                        cbGeometry.setToolTipText( Messages.getMessage( getLocale(), "$MD10389" ) );
                        cbGeometry.setSelected( vm.checkForValidGeometries() );
                        cbGeometry.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                vm.setCheckForValidGeometries( !vm.checkForValidGeometries() );
                            }
                        } );
                    }
                    {
                        cbRepeatedPoints = new JCheckBox();
                        cbRepeatedPoints.setEnabled( vm.isChangeable() );
                        geommetrics.add( cbRepeatedPoints, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                                                                   GridBagConstraints.WEST,
                                                                                   GridBagConstraints.HORIZONTAL,
                                                                                   new Insets( 0, 5, 0, 0 ), 0, 0 ) );
                        cbRepeatedPoints.setText( Messages.getMessage( getLocale(), "$MD10390" ) );
                        cbRepeatedPoints.setToolTipText( Messages.getMessage( getLocale(), "$MD10391" ) );
                        cbRepeatedPoints.setSelected( vm.disallowRepeatedPoints() );
                        cbRepeatedPoints.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                vm.setDisallowRepeatedPoints( !vm.disallowRepeatedPoints() );
                            }
                        } );
                    }
                    {
                        cbPolygonOrientation = new JCheckBox();
                        cbPolygonOrientation.setEnabled( vm.isChangeable() );
                        geommetrics.add( cbPolygonOrientation, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0,
                                                                                       GridBagConstraints.WEST,
                                                                                       GridBagConstraints.HORIZONTAL,
                                                                                       new Insets( 0, 5, 0, 0 ), 0, 0 ) );
                        cbPolygonOrientation.setText( Messages.getMessage( getLocale(), "$MD10392" ) );
                        cbPolygonOrientation.setToolTipText( Messages.getMessage( getLocale(), "$MD10393" ) );
                        cbPolygonOrientation.setSelected( vm.checkForPolygonOrientation() );
                        cbPolygonOrientation.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                vm.setCheckForPolygonOrientation( !vm.checkForPolygonOrientation() );
                            }
                        } );
                    }
                    {
                        cbDoubleGeometries = new JCheckBox();
                        cbDoubleGeometries.setEnabled( vm.isChangeable() );
                        geommetrics.add( cbDoubleGeometries, new GridBagConstraints( 0, 3, 1, 1, 0.0, 0.0,
                                                                                     GridBagConstraints.WEST,
                                                                                     GridBagConstraints.HORIZONTAL,
                                                                                     new Insets( 0, 5, 0, 0 ), 0, 0 ) );
                        cbDoubleGeometries.setText( Messages.getMessage( getLocale(), "$MD10394" ) );
                        cbDoubleGeometries.setToolTipText( Messages.getMessage( getLocale(), "$MD10395" ) );
                        cbDoubleGeometries.setSelected( vm.disallowDoubleGeomerties() );
                        cbDoubleGeometries.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                vm.setDisallowDoubleGeomerties( !vm.disallowDoubleGeomerties() );
                            }
                        } );
                    }
                    {
                        cbSimpleLines = new JCheckBox();
                        cbSimpleLines.setEnabled( vm.isChangeable() );
                        geommetrics.add( cbSimpleLines, new GridBagConstraints( 0, 4, 1, 1, 0.0, 0.0,
                                                                                GridBagConstraints.WEST,
                                                                                GridBagConstraints.HORIZONTAL,
                                                                                new Insets( 0, 5, 0, 0 ), 0, 0 ) );
                        cbSimpleLines.setText( Messages.getMessage( getLocale(), "$MD10434" ) );
                        cbSimpleLines.setToolTipText( Messages.getMessage( getLocale(), "$MD10435" ) );
                        cbSimpleLines.setSelected( vm.ensureSimpleLines() );
                        cbSimpleLines.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                vm.setEnsureSimpleLines( !vm.ensureSimpleLines() );
                            }
                        } );
                    }
                    {
                        cbMinSegmentLength = new JCheckBox();
                        cbMinSegmentLength.setEnabled( vm.isChangeable() );
                        geommetrics.add( cbMinSegmentLength, new GridBagConstraints( 0, 5, 1, 1, 0.0, 0.0,
                                                                                     GridBagConstraints.WEST,
                                                                                     GridBagConstraints.HORIZONTAL,
                                                                                     new Insets( 0, 5, 0, 0 ), 0, 0 ) );
                        cbMinSegmentLength.setText( Messages.getMessage( getLocale(), "$MD10398" ) );
                        cbMinSegmentLength.setToolTipText( Messages.getMessage( getLocale(), "$MD10399" ) );
                        cbMinSegmentLength.setSelected( vm.limitMinSegmentLength() );
                        cbMinSegmentLength.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                vm.setLimitMinSegmentLength( !vm.limitMinSegmentLength() );
                            }
                        } );
                    }
                    {
                        tfMinSegLength = new JTextField();
                        tfMinSegLength.setEnabled( vm.isChangeable() );
                        geommetrics.add( tfMinSegLength, new GridBagConstraints( 1, 5, 1, 1, 0.0, 0.0,
                                                                                 GridBagConstraints.WEST,
                                                                                 GridBagConstraints.HORIZONTAL,
                                                                                 new Insets( 0, 5, 0, 5 ), 0, 0 ) );
                        tfMinSegLength.setText( Float.toString( vm.getMinSegmentLength() ) );
                        tfMinSegLength.addFocusListener( new FocusListener() {

                            public void focusGained( FocusEvent e ) {
                            }

                            public void focusLost( FocusEvent e ) {
                                vm.setMinSegmentLength( Float.parseFloat( tfMinSegLength.getText() ) );
                            }

                        } );
                    }
                    {
                        cbMinPolyArea = new JCheckBox();
                        cbMinPolyArea.setEnabled( vm.isChangeable() );
                        geommetrics.add( cbMinPolyArea, new GridBagConstraints( 0, 6, 1, 1, 0.0, 0.0,
                                                                                GridBagConstraints.WEST,
                                                                                GridBagConstraints.HORIZONTAL,
                                                                                new Insets( 0, 5, 0, 0 ), 0, 0 ) );
                        cbMinPolyArea.setText( Messages.getMessage( getLocale(), "$MD10400" ) );
                        cbMinPolyArea.setToolTipText( Messages.getMessage( getLocale(), "$MD10401" ) );
                        cbMinPolyArea.setSelected( vm.limitMinPolygonArea() );
                        cbMinPolyArea.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                vm.setLimitMinPolygonArea( !vm.limitMinPolygonArea() );
                            }
                        } );
                    }
                    {
                        tfMinPolyArea = new JTextField();
                        tfMinPolyArea.setEnabled( vm.isChangeable() );
                        geommetrics.add( tfMinPolyArea, new GridBagConstraints( 1, 6, 1, 1, 0.0, 0.0,
                                                                                GridBagConstraints.WEST,
                                                                                GridBagConstraints.HORIZONTAL,
                                                                                new Insets( 0, 5, 0, 5 ), 0, 0 ) );
                        tfMinPolyArea.setText( Float.toString( vm.getMinPolygonArea() ) );
                        tfMinPolyArea.addFocusListener( new FocusListener() {

                            public void focusGained( FocusEvent e ) {
                            }

                            public void focusLost( FocusEvent e ) {
                                vm.setMinPolygonArea( Float.parseFloat( tfMinPolyArea.getText() ) );
                            }

                        } );
                    }
                }
                {
                    geomtypes = new JPanel();
                    geomtypes.addComponentListener( new ComponentAdapter() {

                        public void componentShown( ComponentEvent e ) {
                            btCheckGeometries.setEnabled( false );
                        }

                    } );
                    GridBagLayout geomtypesLayout = new GridBagLayout();
                    jTabbedPane1.addTab( Messages.getMessage( getLocale(), "$MD11197" ), null, geomtypes, null );
                    geomtypesLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1 };
                    geomtypesLayout.rowHeights = new int[] { 31, 31, 30, 30, 31, 31, 7 };
                    geomtypesLayout.columnWeights = new double[] { 0.0, 0.1 };
                    geomtypesLayout.columnWidths = new int[] { 178, 20 };
                    geomtypes.setLayout( geomtypesLayout );
                    {
                        cbAllowPoints = new JCheckBox();
                        cbAllowPoints.setEnabled( vgt.isChangeable() );
                        geomtypes.add( cbAllowPoints, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                              GridBagConstraints.WEST,
                                                                              GridBagConstraints.HORIZONTAL,
                                                                              new Insets( 0, 5, 0, 0 ), 0, 0 ) );
                        cbAllowPoints.setText( Messages.getMessage( getLocale(), "$MD10402" ) );
                        cbAllowPoints.setToolTipText( Messages.getMessage( getLocale(), "$MD10403" ) );
                        cbAllowPoints.setSelected( vgt.pointsAllowed() );
                        cbAllowPoints.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                vgt.setPointsAllowed( !vgt.pointsAllowed() );
                            }
                        } );
                    }
                    {
                        cbAllowLines = new JCheckBox();
                        cbAllowLines.setEnabled( vgt.isChangeable() );
                        geomtypes.add( cbAllowLines, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                                                             GridBagConstraints.WEST,
                                                                             GridBagConstraints.HORIZONTAL,
                                                                             new Insets( 0, 5, 0, 0 ), 0, 0 ) );
                        cbAllowLines.setText( Messages.getMessage( getLocale(), "$MD10404" ) );
                        cbAllowLines.setToolTipText( Messages.getMessage( getLocale(), "$MD10405" ) );
                        cbAllowLines.setSelected( vgt.linestringsAllowed() );
                        cbAllowLines.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                vgt.setLinestringsAllowed( !vgt.linestringsAllowed() );
                            }
                        } );
                    }
                    {
                        cbAllowPolygons = new JCheckBox();
                        cbAllowPolygons.setEnabled( vgt.isChangeable() );
                        geomtypes.add( cbAllowPolygons, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0,
                                                                                GridBagConstraints.WEST,
                                                                                GridBagConstraints.HORIZONTAL,
                                                                                new Insets( 0, 5, 0, 0 ), 0, 0 ) );
                        cbAllowPolygons.setText( Messages.getMessage( getLocale(), "$MD10406" ) );
                        cbAllowPolygons.setToolTipText( Messages.getMessage( getLocale(), "$MD10407" ) );
                        cbAllowPolygons.setSelected( vgt.polygonsAllowed() );
                        cbAllowPolygons.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                vgt.setPolygonsAllowed( !vgt.polygonsAllowed() );
                            }
                        } );
                    }
                    {
                        cbAllowMultiPoints = new JCheckBox();
                        cbAllowMultiPoints.setEnabled( vgt.isChangeable() );
                        geomtypes.add( cbAllowMultiPoints, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0,
                                                                                   GridBagConstraints.WEST,
                                                                                   GridBagConstraints.HORIZONTAL,
                                                                                   new Insets( 0, 5, 0, 0 ), 0, 0 ) );
                        cbAllowMultiPoints.setText( Messages.getMessage( getLocale(), "$MD10408" ) );
                        cbAllowMultiPoints.setToolTipText( Messages.getMessage( getLocale(), "$MD10409" ) );
                        cbAllowMultiPoints.setSelected( vgt.multiPointsAllowed() );
                        cbAllowMultiPoints.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                vgt.setMultiPointsAllowed( !vgt.multiPointsAllowed() );
                            }
                        } );
                    }
                    {
                        cbAllowMultiLines = new JCheckBox();
                        cbAllowMultiLines.setEnabled( vgt.isChangeable() );
                        geomtypes.add( cbAllowMultiLines, new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0,
                                                                                  GridBagConstraints.WEST,
                                                                                  GridBagConstraints.HORIZONTAL,
                                                                                  new Insets( 0, 5, 0, 0 ), 0, 0 ) );
                        cbAllowMultiLines.setText( Messages.getMessage( getLocale(), "$MD10410" ) );
                        cbAllowMultiLines.setSelected( vgt.multiLinestringsAllowed() );
                        cbAllowMultiLines.setToolTipText( Messages.getMessage( getLocale(), "$MD10411" ) );
                        cbAllowMultiLines.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                vgt.setMultiLinestringsAllowed( !vgt.multiLinestringsAllowed() );
                            }
                        } );
                    }
                    {
                        cbAllowMultiPolys = new JCheckBox();
                        cbAllowMultiPolys.setEnabled( vgt.isChangeable() );
                        geomtypes.add( cbAllowMultiPolys, new GridBagConstraints( 1, 2, 1, 1, 0.0, 0.0,
                                                                                  GridBagConstraints.WEST,
                                                                                  GridBagConstraints.HORIZONTAL,
                                                                                  new Insets( 0, 5, 0, 0 ), 0, 0 ) );
                        cbAllowMultiPolys.setText( Messages.getMessage( getLocale(), "$MD10412" ) );
                        cbAllowMultiPolys.setToolTipText( Messages.getMessage( getLocale(), "$MD10413" ) );
                        cbAllowMultiPolys.setSelected( vgt.multiPolygonsAllowed() );
                        cbAllowMultiPolys.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                vgt.setMultiPolygonsAllowed( !vgt.multiPolygonsAllowed() );
                            }
                        } );
                    }
                    {
                        cbAllowHoles = new JCheckBox();
                        cbAllowHoles.setEnabled( vgt.isChangeable() );
                        geomtypes.add( cbAllowHoles, new GridBagConstraints( 0, 4, 2, 1, 0.0, 0.0,
                                                                             GridBagConstraints.WEST,
                                                                             GridBagConstraints.HORIZONTAL,
                                                                             new Insets( 0, 5, 0, 0 ), 0, 0 ) );
                        cbAllowHoles.setText( Messages.getMessage( getLocale(), "$MD10414" ) );
                        cbAllowHoles.setToolTipText( Messages.getMessage( getLocale(), "$MD10415" ) );
                        cbAllowHoles.setSelected( vgt.polygonsWithHolesAllowed() );
                        cbAllowHoles.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                vgt.setPolygonsWithHolesAllowed( !vgt.polygonsWithHolesAllowed() );
                            }
                        } );
                    }
                    {
                        cbAllowNoneLinearInterpolations = new JCheckBox();
                        cbAllowNoneLinearInterpolations.setEnabled( vgt.isChangeable() );
                        geomtypes.add( cbAllowNoneLinearInterpolations,
                                       new GridBagConstraints( 0, 5, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                               GridBagConstraints.HORIZONTAL, new Insets( 0, 5, 0, 0 ),
                                                               0, 0 ) );
                        cbAllowNoneLinearInterpolations.setText( Messages.getMessage( getLocale(), "$MD10416" ) );
                        cbAllowNoneLinearInterpolations.setToolTipText( Messages.getMessage( getLocale(), "$MD10417" ) );
                        cbAllowNoneLinearInterpolations.setSelected( vgt.noneLinearInterpolationAllowed() );
                        cbAllowNoneLinearInterpolations.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                vgt.setNoneLinearInterpolationAllowed( !vgt.noneLinearInterpolationAllowed() );
                            }
                        } );
                    }
                    {
                        cbAllowGeometryCollections = new JCheckBox();
                        cbAllowGeometryCollections.setEnabled( vgt.isChangeable() );
                        geomtypes.add( cbAllowGeometryCollections,
                                       new GridBagConstraints( 0, 3, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                               GridBagConstraints.HORIZONTAL, new Insets( 0, 5, 0, 0 ),
                                                               0, 0 ) );
                        cbAllowGeometryCollections.setText( Messages.getMessage( getLocale(), "$MD10396" ) );
                        cbAllowGeometryCollections.setToolTipText( Messages.getMessage( getLocale(), "$MD10397" ) );
                        cbAllowGeometryCollections.setSelected( vgt.geometryCollectionsAllowed() );
                        cbAllowGeometryCollections.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                vgt.setGeometryCollectionsAllowed( !vgt.geometryCollectionsAllowed() );
                            }
                        } );
                    }
                }
                {
                    topologyPanel = new JPanel();
                    topologyPanel.addComponentListener( new ComponentAdapter() {

                        public void componentShown( ComponentEvent e ) {
                            btCheckGeometries.setEnabled( true );
                        }

                    } );
                    GridBagLayout topologyPanelLayout = new GridBagLayout();
                    jTabbedPane1.addTab( Messages.getMessage( getLocale(), "$MD11198" ), null, topologyPanel, null );
                    topologyPanelLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.1 };
                    topologyPanelLayout.rowHeights = new int[] { 27, 27, 27, 7 };
                    topologyPanelLayout.columnWeights = new double[] { 0.0, 0.1 };
                    topologyPanelLayout.columnWidths = new int[] { 272, 7 };
                    topologyPanel.setLayout( topologyPanelLayout );
                    {
                        cbAllowIntersection = new JCheckBox();
                        cbAllowIntersection.setEnabled( vgto.isChangeable() );
                        topologyPanel.add( cbAllowIntersection, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                                        GridBagConstraints.WEST,
                                                                                        GridBagConstraints.HORIZONTAL,
                                                                                        new Insets( 0, 5, 0, 0 ), 0, 0 ) );
                        cbAllowIntersection.setText( Messages.getMessage( getLocale(), "$MD10437" ) );
                        cbAllowIntersection.setToolTipText( Messages.getMessage( getLocale(), "$MD10438" ) );
                        cbAllowIntersection.setSelected( vgto.intersectionAllowed() );
                        cbAllowIntersection.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                vgto.setIntersectionAllowed( !vgto.intersectionAllowed() );
                            }
                        } );
                    }
                    {
                        cbAllowTouching = new JCheckBox();
                        cbAllowTouching.setEnabled( vgto.isChangeable() );
                        topologyPanel.add( cbAllowTouching, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                                                                    GridBagConstraints.WEST,
                                                                                    GridBagConstraints.HORIZONTAL,
                                                                                    new Insets( 0, 5, 0, 0 ), 0, 0 ) );
                        cbAllowTouching.setText( Messages.getMessage( getLocale(), "$MD10439" ) );
                        cbAllowTouching.setToolTipText( Messages.getMessage( getLocale(), "$MD10440" ) );
                        cbAllowTouching.setSelected( vgto.touchingAllowed() );
                        cbAllowTouching.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                vgto.setTouchingAllowed( !vgto.touchingAllowed() );
                            }
                        } );
                    }
                    {
                        cbAllowEqualGeometries = new JCheckBox();
                        cbAllowEqualGeometries.setEnabled( vgto.isChangeable() );
                        topologyPanel.add( cbAllowEqualGeometries,
                                           new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                                   GridBagConstraints.HORIZONTAL, new Insets( 0, 5, 0,
                                                                                                              0 ), 0, 0 ) );
                        cbAllowEqualGeometries.setText( Messages.getMessage( getLocale(), "$MD10441" ) );
                        cbAllowEqualGeometries.setToolTipText( Messages.getMessage( getLocale(), "$MD10442" ) );
                        cbAllowEqualGeometries.setSelected( vgto.equalGeometriesAllowed() );
                        cbAllowEqualGeometries.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                vgto.setEqualGeometriesAllowed( !vgto.equalGeometriesAllowed() );
                            }
                        } );
                    }
                }
            }
            {
                buttonPanel = new JPanel();
                this.add( buttonPanel, BorderLayout.CENTER );
                GridBagLayout buttonPanelLayout = new GridBagLayout();
                buttonPanelLayout.rowWeights = new double[] { 0.0, 0.1 };
                buttonPanelLayout.rowHeights = new int[] { 36, 7 };
                buttonPanelLayout.columnWeights = new double[] { 0.1, 0.0, 0.1 };
                buttonPanelLayout.columnWidths = new int[] { 7, 158, 7 };
                buttonPanel.setLayout( buttonPanelLayout );
                {
                    cbPrintWarnings = new JCheckBox();
                    buttonPanel.add( cbPrintWarnings, new GridBagConstraints( 0, 0, 2, 1, 0.0, 0.0,
                                                                              GridBagConstraints.WEST,
                                                                              GridBagConstraints.HORIZONTAL,
                                                                              new Insets( 0, 5, 0, 0 ), 0, 0 ) );
                    cbPrintWarnings.setText( Messages.getMessage( getLocale(), "$MD10418" ) );
                    cbPrintWarnings.setToolTipText( Messages.getMessage( getLocale(), "$MD10419" ) );
                    cbPrintWarnings.setSelected( appContainer.getSettings().printValidationWaring() );
                    cbPrintWarnings.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            boolean value = !appContainer.getSettings().printValidationWaring();
                            appContainer.getSettings().setPrintValidationWaring( value );
                        }
                    } );
                    cbPrintWarnings.setVisible( false );
                }
                {
                    checkButtonPanel = new JPanel();
                    FlowLayout checkButtonPanelLayout = new FlowLayout();
                    checkButtonPanelLayout.setAlignment( FlowLayout.LEFT );
                    checkButtonPanel.setLayout( checkButtonPanelLayout );
                    buttonPanel.add( checkButtonPanel, new GridBagConstraints( 0, 1, 2, 1, 0.0, 0.0,
                                                                               GridBagConstraints.CENTER,
                                                                               GridBagConstraints.BOTH,
                                                                               new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    {
                        btCheckLayer = new JButton();
                        checkButtonPanel.add( btCheckLayer );
                        btCheckLayer.setText( Messages.getMessage( getLocale(), "$MD10420" ) );
                        btCheckLayer.setToolTipText( Messages.getMessage( getLocale(), "$MD10421" ) );
                        btCheckLayer.addActionListener( checkListener );
                        btCheckLayer.setActionCommand( "checklayer" );
                    }
                    {
                        btCheckGeometries = new JButton();
                        checkButtonPanel.add( btCheckGeometries );
                        btCheckGeometries.setText( Messages.getMessage( getLocale(), "$MD10422" ) );
                        btCheckGeometries.setToolTipText( Messages.getMessage( getLocale(), "$MD10423" ) );
                        btCheckGeometries.addActionListener( checkListener );
                        btCheckGeometries.setActionCommand( "checkgeometries" );
                    }
                }
                {
                    jPanel1 = new JPanel();
                    FlowLayout jPanel1Layout = new FlowLayout();
                    jPanel1Layout.setAlignment( FlowLayout.RIGHT );
                    jPanel1.setLayout( jPanel1Layout );
                    buttonPanel.add( jPanel1, new GridBagConstraints( 2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                      GridBagConstraints.BOTH,
                                                                      new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    {
                        btHelp = new JButton( Messages.getMessage( getLocale(), "$MD10424" ),
                                              IconRegistry.getIcon( "help.png" ) );
                        jPanel1.add( btHelp );
                        btHelp.setToolTipText( Messages.getMessage( getLocale(), "$MD10425" ) );
                        btHelp.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                HelpFrame hf = HelpFrame.getInstance( new HelpManager( appContainer ) );
                                hf.setVisible( true );
                                hf.gotoModule( "Digitizer" );
                            }

                        } );
                    }
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    // ///////////////////////////////////////////////////////////////////////////
    // inner classes
    // ///////////////////////////////////////////////////////////////////////////

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version. $Revision$, $Date$
     */
    private class CheckActionListener implements ActionListener {

        Command command = null;

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed( ActionEvent event ) {
            AbstractButton button = (AbstractButton) event.getSource();
            String action = button.getActionCommand();
            List<Layer> layers = appContainer.getMapModel( null ).getLayersSelectedForAction( MapModel.SELECTION_ACTION );
            for ( Layer layer : layers ) {
                if ( "checkgeometries".equals( action ) ) {
                    checkGeometries( layer );
                } else {
                    checkLayer( layer );
                }
            }
        }

        private void checkLayer( Layer layer ) {
            layer.addSelectedFor( MapModel.SELECTION_ACTION );
            command = new ValidateLayer4GeometriesCommand( layer, null );
            String title = Messages.getMessage( getLocale(), "$DI10035" );
            String message = Messages.getMessage( getLocale(), "$MD10462" );
            ProcessMonitor pm = ProcessMonitorFactory.createDialogProcessMonitor( appContainer.getViewPlatform(),
                                                                                  title, message, 0, 1, command );
            command.setProcessMonitor( pm );
            command.addListener( new CommandProcessedListener() {

                @SuppressWarnings("unchecked")
                public void commandProcessed( CommandProcessedEvent event ) {
                    Map<String, String> result = (Map<String, String>) command.getResult();
                    if ( result.size() == 0 ) {
                        DialogFactory.openInformationDialog( appContainer.getViewPlatform(),
                                                             GeometryValidationPanel.this,
                                                             Messages.getMessage( getLocale(), "$MD10458" ),
                                                             Messages.getMessage( getLocale(), "$MD10459" ) );
                    } else {
                        // TODO
                        // print results to iGeoDesktop logging console
                        DialogFactory.openInformationDialog( appContainer.getViewPlatform(),
                                                             GeometryValidationPanel.this,
                                                             Messages.getMessage( getLocale(), "$MD10460" ),
                                                             Messages.getMessage( getLocale(), "$MD10461" ) );
                        LOG.logWarning( "numbers of error: " + result.get( "errorCount" ) );
                        LOG.logWarning( "invalid layer: " + result );

                    }
                }

            } );
            try {
                appContainer.getCommandProcessor().executeASychronously( command );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
            }
        }

        @SuppressWarnings("unchecked")
        private void checkGeometries( Layer layer ) {
            layer.addSelectedFor( MapModel.SELECTION_ACTION );
            FeatureCollection fc = layer.getSelectedFeatures();
            List<Geometry> geometries = new ArrayList<Geometry>( fc.size() );
            Iterator<Feature> iterator = fc.iterator();
            while ( iterator.hasNext() ) {
                geometries.add( iterator.next().getDefaultGeometryPropertyValue() );
            }
            Command command = new ValidateGeometriesCommand( layer, geometries );
            try {
                appContainer.getCommandProcessor().executeSychronously( command, false );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
            }
            Map<String, String> result = (Map<String, String>) command.getResult();
            if ( result.size() == 0 ) {
                DialogFactory.openInformationDialog( appContainer.getViewPlatform(), GeometryValidationPanel.this,
                                                     Messages.getMessage( getLocale(), "$MD10454" ),
                                                     Messages.getMessage( getLocale(), "$MD10455" ) );
            } else {
                // TODO
                // print results to iGeoDesktopn logging console
                DialogFactory.openInformationDialog( appContainer.getViewPlatform(), GeometryValidationPanel.this,
                                                     Messages.getMessage( getLocale(), "$MD10456" ),
                                                     Messages.getMessage( getLocale(), "$MD10457" ) );
                LOG.logWarning( "numbers of error: " + result.get( "errorCount" ) );
                LOG.logWarning( "invalid geometries: " + result );
            }
        }

    }

}
