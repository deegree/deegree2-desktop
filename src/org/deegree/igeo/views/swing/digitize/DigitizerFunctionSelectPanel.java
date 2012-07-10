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
package org.deegree.igeo.views.swing.digitize;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.Pair;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.DigitizerModule;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.DigitizerFunctionSelect;
import org.deegree.igeo.views.swing.ButtonGroup;
import org.deegree.igeo.views.swing.GeometryValidationPanel;
import org.deegree.igeo.views.swing.SnappingOptionsPanel;
import org.deegree.igeo.views.swing.actionlisteners.KeyListenerRegister;
import org.deegree.igeo.views.swing.digitize.construction.DrawArcDialog;
import org.deegree.igeo.views.swing.digitize.construction.JoinCurveDialog;
import org.deegree.igeo.views.swing.digitize.construction.ParallelDialog;
import org.deegree.igeo.views.swing.digitize.construction.PointByCoordinateDialog;
import org.deegree.igeo.views.swing.digitize.options.ArcAndLengthOptionPanel;
import org.deegree.igeo.views.swing.digitize.options.LineOptionsPanel;
import org.deegree.igeo.views.swing.digitize.options.OptionsPanel;
import org.deegree.igeo.views.swing.digitize.options.VerticesOptionsPanel;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.model.spatialschema.Geometry;

/**
 * Panel for selecting digitizing functionalities. It is the swing implementation of {@link DigitizerFunctionSelect}
 * interface and provides some additional GUI elements for validating layers and geometries as well as for set up some
 * digitizing options.
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class DigitizerFunctionSelectPanel extends javax.swing.JPanel implements DigitizerFunctionSelect {

    private static final long serialVersionUID = -3532489115230890601L;

    private static final ILogger LOG = LoggerFactory.getLogger( DigitizerFunctionSelectPanel.class );

    private DigitizerModule<Container> digitizerModule;

    private Map<String, Object> selectionParams;

    private Map<String, Object> options;

    private SnappingOptionsPanel snapPanel;

    private JTabbedPane functionSelectTab;

    private JToggleButton select2PerFilter;

    private JToggleButton btDrawPolygonHole;

    private JButton btCutPolygonHole;

    private JButton uniteLineStrings;

    private JButton unitePolygons;

    private JButton btDrawArc;

    private JToggleButton selectPerFilter;

    private JButton deleteFeature;

    private JToggleButton select2PerLine;

    private JToggleButton select2PerPolygon;

    private JToggleButton select2ForEdit;

    private JTree optionTree;

    private JScrollPane selectOptionSC;

    private JSplitPane optionsplit;

    private JToggleButton fillArc;

    private JToggleButton rotateFeature;

    private JToggleButton moveFeature;

    private JPanel createPanel;

    private JButton btJoinCurves;

    private JToggleButton btDrawPolygonByFillingHole;

    private JButton btMoveByDistance;

    private JButton btDrawParallel;

    private JButton btSetDigitizeAngle;

    private JButton btDrawPointAt;

    private JToggleButton btDrawSizedEllipse;

    private JToggleButton btDrawSizedRect;

    private JTextArea editDescriptionTA;

    private JToggleButton btDrawCircle;

    private JToggleButton btDrawPoint;

    private JToggleButton btDrawLineString;

    private JToggleButton btDrawPolygon;

    private JToggleButton btDrawRectangle;

    private JPanel optionsPanel;

    private JToggleButton btSelectForInsert;

    private JTextArea taCreateDescriptionTA;

    private JButton ungroupMultiPolygon;

    private JButton ungroupMultiCurve;

    private JButton ungroupMultiPoint;

    private JButton groupPolygons;

    private JButton groupLines;

    private JButton groupPoints;

    private JToggleButton splitPolygon;

    private JToggleButton splitLine;

    private JToggleButton mergeVertices;

    private JToggleButton moveVertex;

    private JToggleButton deleteVertex;

    private JToggleButton insertVertex;

    private JToggleButton dummy;

    private JPanel editPanel;

    private ButtonGroup mainButtonGroup;

    private ActionListener buttonselectListener;

    private FocusListener focusListener;

    private ApplicationContainer<Container> appContainer;

    private Properties prop = new Properties();

    /**
     * default constructor
     * 
     * @param appContainer
     */
    public DigitizerFunctionSelectPanel( ApplicationContainer<Container> appContainer ) {
        this.appContainer = appContainer;
        try {
            InputStream is = DigitizerFunctionSelectPanel.class.getResourceAsStream( "resources/digitizer.properties" );
            prop.load( is );
            is.close();
        } catch ( Exception e ) {
            LOG.logError( e );
        }
        mainButtonGroup = new ButtonGroup();
        buttonselectListener = new ButtonSelectListener();
        focusListener = (FocusListener) buttonselectListener;
        initGUI();
        selectionParams = new HashMap<String, Object>();
        options = new HashMap<String, Object>();
    }

    private void initGUI() {

        try {
            BorderLayout thisLayout = new BorderLayout();
            this.setLayout( thisLayout );
            this.setPreferredSize(new java.awt.Dimension(346, 399));
            {
                functionSelectTab = new JTabbedPane();
                this.add( functionSelectTab, BorderLayout.CENTER );
                functionSelectTab.setPreferredSize(new java.awt.Dimension(438, 402));
                createCreatePanel();
                // createConstructionPanel();
                createEditPanel();
                createSnapPanel();
                createValidationPanel();
                createOptionPanel();
                createArcAndLengthPanel();
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    private void createArcAndLengthPanel() {
        JPanel panel = new ArcAndLengthOptionPanel( appContainer );
        panel.setVisible( true );
        functionSelectTab.addTab( Messages.getMessage( getLocale(), "$MD11779" ), null, panel, null );
    }

    private void createOptionPanel() {
        optionsPanel = new JPanel();
        BorderLayout optionsPanelLayout = new BorderLayout();
        optionsPanel.setLayout( optionsPanelLayout );
        functionSelectTab.addTab( Messages.getMessage( getLocale(), "$MD11191" ), null, optionsPanel, null );
        {
            optionsplit = new JSplitPane();
            optionsPanel.add( optionsplit, BorderLayout.CENTER );
            {
                selectOptionSC = new JScrollPane();
                optionsplit.add( selectOptionSC, JSplitPane.LEFT );
                selectOptionSC.setPreferredSize( new Dimension( 150, 355 ) );
                selectOptionSC.setMinimumSize( new Dimension( 150, 355 ) );
                {
                    optionTree = new JTree();
                    optionTree.setCellRenderer( new CellRenderer() );
                    optionTree.setMinimumSize( new Dimension( 150, 60 ) );
                    optionTree.addMouseListener( new OptionsMouseListener() );
                    fillTreeModel();
                    optionTree.expandRow( 0 );
                    selectOptionSC.setViewportView( optionTree );
                    optionTree.setPreferredSize( new Dimension( 161, 379 ) );
                }
            }
        }
    }

    private void createValidationPanel() {
        JPanel validationPanel = new GeometryValidationPanel( appContainer );
        validationPanel.setVisible( true );
        functionSelectTab.addTab( Messages.getMessage( getLocale(), "$MD11192" ), null, validationPanel, null );
    }

    private void createEditPanel() {
        editPanel = new JPanel();
        editPanel.addComponentListener( new EditShownListener() );
        functionSelectTab.addTab( Messages.getMessage( getLocale(), "$MD11193" ), null, editPanel, null );
        editPanel.setLayout( null );
        {
            editDescriptionTA = new JTextArea();
            editDescriptionTA.setLineWrap( true );
            editDescriptionTA.setWrapStyleWord( true );
            editDescriptionTA.setEditable( false );
            String s = Messages.getMessage( getLocale(), "$MD10203" );
            editDescriptionTA.setBorder( BorderFactory.createTitledBorder( s ) );
            editPanel.add( editDescriptionTA );
            editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10202" ) );
            editDescriptionTA.setBounds(5, 189, 330, 126);
        }
        {
            select2ForEdit = new JToggleButton( getImageIcon( "select.gif" ) );
            editPanel.add( select2ForEdit );
            select2ForEdit.setActionCommand( "select2ForEdit" );
            select2ForEdit.setToolTipText( Messages.getMessage( getLocale(), "$MD10212" ) );
            select2ForEdit.setBounds( 6, 5, 30, 30 );
            select2ForEdit.addActionListener( buttonselectListener );
            select2ForEdit.addFocusListener( focusListener );
            select2ForEdit.setVisible( "true".equals( prop.get( "select2ForEdit" ) ) );
            mainButtonGroup.add( select2ForEdit );
        }
        /*
         * { select2PerLine = new JToggleButton(); editPanel.add( select2PerLine ); select2PerLine.setActionCommand(
         * "select2PerLine" ); select2PerLine.setToolTipText( Messages.getMessage( getLocale(), "$MD10213" ) );
         * select2PerLine.setText( "select2PerLine" ); select2PerLine.setBounds( 77, 5, 30, 30 );
         * select2PerLine.addActionListener( buttonselectListener ); select2PerLine.addFocusListener( focusListener );
         * mainButtonGroup.add( select2PerLine ); } { select2PerPolygon = new JToggleButton(); editPanel.add(
         * select2PerPolygon ); select2PerPolygon.setActionCommand( "select2PerPolygon" );
         * select2PerPolygon.setToolTipText( Messages.getMessage( getLocale(), "$MD10214" ) );
         * select2PerPolygon.setText( "selectPerPolygon" ); select2PerPolygon.setBounds( 41, 5, 30, 30 );
         * select2PerPolygon.addActionListener( buttonselectListener ); select2PerPolygon.addFocusListener(
         * focusListener ); mainButtonGroup.add( select2PerPolygon ); } { select2PerFilter = new JToggleButton();
         * editPanel.add( select2PerFilter ); select2PerFilter.setActionCommand( "select2PerFilter" );
         * select2PerFilter.setToolTipText( Messages.getMessage( getLocale(), "$MD10215" ) ); select2PerFilter.setText(
         * "select2PerFilter" ); select2PerFilter.setBounds( 113, 5, 30, 30 ); select2PerFilter.addActionListener(
         * buttonselectListener ); select2PerFilter.addFocusListener( focusListener ); mainButtonGroup.add(
         * select2PerFilter ); }
         */
        {
            insertVertex = new JToggleButton( getImageIcon( "insert_vertex.png" ) );
            editPanel.add( insertVertex );
            insertVertex.setActionCommand( "update:insertVertex" );
            insertVertex.setToolTipText( Messages.getMessage( getLocale(), "$MD10216" ) );
            insertVertex.setBounds( 5, 41, 30, 30 );
            insertVertex.addActionListener( buttonselectListener );
            insertVertex.addFocusListener( focusListener );
            insertVertex.setVisible( "true".equals( prop.get( "update_insertVertex" ) ) );
            mainButtonGroup.add( insertVertex );
        }
        {
            deleteVertex = new JToggleButton( getImageIcon( "delete_vertex.png" ) );
            editPanel.add( deleteVertex );
            deleteVertex.setActionCommand( "update:deleteVertex" );
            deleteVertex.setToolTipText( Messages.getMessage( getLocale(), "$MD10217" ) );
            deleteVertex.setBounds( 41, 41, 30, 30 );
            deleteVertex.addActionListener( buttonselectListener );
            deleteVertex.addFocusListener( focusListener );
            deleteVertex.setVisible( "true".equals( prop.get( "update_deleteVertex" ) ) );
            mainButtonGroup.add( deleteVertex );
        }
        {
            moveVertex = new JToggleButton( getImageIcon( "move_vertex.png" ) );
            editPanel.add( moveVertex );
            moveVertex.setActionCommand( "update:moveVertex" );
            moveVertex.setToolTipText( Messages.getMessage( getLocale(), "$MD10218" ) );
            moveVertex.setBounds( 77, 41, 30, 30 );
            moveVertex.addActionListener( buttonselectListener );
            moveVertex.addFocusListener( focusListener );
            moveVertex.setVisible( "true".equals( prop.get( "update_moveVertex" ) ) );
            mainButtonGroup.add( moveVertex );
        }
        {
            mergeVertices = new JToggleButton( getImageIcon( "merge_vertices.png" ) );
            editPanel.add( mergeVertices );
            mergeVertices.setToolTipText( Messages.getMessage( getLocale(), "$MD10219" ) );
            mergeVertices.setActionCommand( "update:mergeVertices" );
            mergeVertices.setBounds( 113, 41, 30, 30 );
            mergeVertices.addActionListener( buttonselectListener );
            mergeVertices.addFocusListener( focusListener );
            mergeVertices.setVisible( "true".equals( prop.get( "update_mergeVertices" ) ) );
            mainButtonGroup.add( mergeVertices );
        }
        {
            splitLine = new JToggleButton( getImageIcon( "split_line.png" ) );
            editPanel.add( splitLine );
            splitLine.setActionCommand( "splitLine" );
            splitLine.setToolTipText( Messages.getMessage( getLocale(), "$MD10220" ) );
            splitLine.setBounds( 5, 77, 30, 30 );
            splitLine.addActionListener( buttonselectListener );
            splitLine.addFocusListener( focusListener );
            splitLine.setVisible( "true".equals( prop.get( "splitLine" ) ) );
            mainButtonGroup.add( splitLine );
        }
        {
            splitPolygon = new JToggleButton( getImageIcon( "split_polygon.png" ) );
            editPanel.add( splitPolygon );
            splitPolygon.setActionCommand( "splitPolygon" );
            splitPolygon.setToolTipText( Messages.getMessage( getLocale(), "$MD10221" ) );
            splitPolygon.setBounds( 41, 77, 30, 30 );
            splitPolygon.addActionListener( buttonselectListener );
            splitPolygon.addFocusListener( focusListener );
            splitPolygon.setVisible( "true".equals( prop.get( "splitPolygon" ) ) );
            mainButtonGroup.add( splitPolygon );
        }
        {
            unitePolygons = new JButton( getImageIcon( "union_polygon.png" ) );
            editPanel.add( unitePolygons );
            unitePolygons.setActionCommand( "uniteGeometries" );
            unitePolygons.setToolTipText( Messages.getMessage( getLocale(), "$MD10270" ) );
            unitePolygons.addActionListener( buttonselectListener );
            unitePolygons.addFocusListener( focusListener );
            unitePolygons.setVisible( "true".equals( prop.get( "uniteGeometries" ) ) );
            unitePolygons.setBounds( 124, 77, 30, 30 );
        }
        {
            uniteLineStrings = new JButton( getImageIcon( "union_line.png" ) );
            editPanel.add( uniteLineStrings );
            uniteLineStrings.setActionCommand( "uniteGeometries" );
            uniteLineStrings.setToolTipText( Messages.getMessage( getLocale(), "$MD10272" ) );
            uniteLineStrings.addActionListener( buttonselectListener );
            uniteLineStrings.addFocusListener( focusListener );
            uniteLineStrings.setVisible( "true".equals( prop.get( "uniteGeometries" ) ) );
            uniteLineStrings.setBounds( 160, 77, 30, 30 );
        }
        {
            groupPoints = new JButton( getImageIcon( "point_group.gif" ) );
            editPanel.add( groupPoints );
            groupPoints.setActionCommand( "groupPoints" );
            groupPoints.setToolTipText( Messages.getMessage( getLocale(), "$MD10222" ) );
            groupPoints.setBounds( 5, 113, 30, 30 );
            groupPoints.addActionListener( buttonselectListener );
            groupPoints.setVisible( "true".equals( prop.get( "groupPoints" ) ) );
            groupPoints.addFocusListener( focusListener );
        }
        {
            groupLines = new JButton( getImageIcon( "line_group.gif" ) );
            editPanel.add( groupLines );
            groupLines.setActionCommand( "groupLines" );
            groupLines.setToolTipText( Messages.getMessage( getLocale(), "$MD10223" ) );
            groupLines.setBounds( 41, 113, 30, 30 );
            groupLines.addActionListener( buttonselectListener );
            groupLines.setVisible( "true".equals( prop.get( "groupLines" ) ) );
            groupLines.addFocusListener( focusListener );
        }
        {
            groupPolygons = new JButton( getImageIcon( "shape_group.gif" ) );
            editPanel.add( groupPolygons );
            groupPolygons.setActionCommand( "groupPolygons" );
            groupPolygons.setToolTipText( Messages.getMessage( getLocale(), "$MD10224" ) );
            groupPolygons.setBounds( 77, 113, 30, 30 );
            groupPolygons.addActionListener( buttonselectListener );
            groupPolygons.setVisible( "true".equals( prop.get( "groupPolygons" ) ) );
            groupPolygons.addFocusListener( focusListener );
        }
        {
            ungroupMultiPoint = new JButton( getImageIcon( "point_ungroup.gif" ) );
            editPanel.add( ungroupMultiPoint );
            ungroupMultiPoint.setActionCommand( "ungroupMultiPoint" );
            ungroupMultiPoint.setToolTipText( Messages.getMessage( getLocale(), "$MD10225" ) );
            ungroupMultiPoint.addActionListener( buttonselectListener );
            ungroupMultiPoint.addFocusListener( focusListener );
            ungroupMultiPoint.setVisible( "true".equals( prop.get( "ungroupMultiPoint" ) ) );
            ungroupMultiPoint.setBounds( 124, 113, 30, 31 );
        }
        {
            ungroupMultiCurve = new JButton( getImageIcon( "line_ungroup.gif" ) );
            editPanel.add( ungroupMultiCurve );
            ungroupMultiCurve.setActionCommand( "ungroupMultiCurve" );
            ungroupMultiCurve.setToolTipText( Messages.getMessage( getLocale(), "$MD10226" ) );
            ungroupMultiCurve.addActionListener( buttonselectListener );
            ungroupMultiCurve.addFocusListener( focusListener );
            ungroupMultiCurve.setVisible( "true".equals( prop.get( "ungroupMultiCurve" ) ) );
            ungroupMultiCurve.setBounds( 160, 113, 30, 31 );
        }
        {
            ungroupMultiPolygon = new JButton( getImageIcon( "shape_ungroup.gif" ) );
            editPanel.add( ungroupMultiPolygon );
            ungroupMultiPolygon.setActionCommand( "ungroupMultiPolygon" );
            ungroupMultiPolygon.setToolTipText( Messages.getMessage( getLocale(), "$MD10227" ) );
            ungroupMultiPolygon.addActionListener( buttonselectListener );
            ungroupMultiPolygon.addFocusListener( focusListener );
            ungroupMultiPolygon.setVisible( "true".equals( prop.get( "ungroupMultiPolygon" ) ) );
            ungroupMultiPolygon.setBounds( 196, 113, 30, 31 );
        }
        {
            moveFeature = new JToggleButton( getImageIcon( "shape_square_move.gif" ) );
            editPanel.add( moveFeature );
            moveFeature.setActionCommand( "update:moveFeature" );
            moveFeature.setToolTipText( Messages.getMessage( getLocale(), "$MD10228" ) );
            moveFeature.setBounds( 40, 148, 30, 30 );
            moveFeature.addActionListener( buttonselectListener );
            moveFeature.addFocusListener( focusListener );
            moveFeature.setVisible( "true".equals( prop.get( "update_moveFeature" ) ) );
            mainButtonGroup.add( moveFeature );
        }
        {
            rotateFeature = new JToggleButton( getImageIcon( "shape_rotate.gif" ) );
            editPanel.add( rotateFeature );
            rotateFeature.setActionCommand( "rotateFeature" );
            rotateFeature.setToolTipText( Messages.getMessage( getLocale(), "$MD10229" ) );
            rotateFeature.setBounds( 113, 148, 30, 30 );
            rotateFeature.addActionListener( buttonselectListener );
            rotateFeature.addFocusListener( focusListener );
            rotateFeature.setVisible( "true".equals( prop.get( "rotateFeature" ) ) );
            mainButtonGroup.add( rotateFeature );
        }
        {
            deleteFeature = new JButton( getImageIcon( "shape_square_delete.gif" ) );
            editPanel.add( deleteFeature );
            deleteFeature.setActionCommand( "deleteFeature" );
            deleteFeature.setToolTipText( Messages.getMessage( getLocale(), "$MD10230" ) );
            deleteFeature.addActionListener( buttonselectListener );
            deleteFeature.addFocusListener( focusListener );
            deleteFeature.setVisible( "true".equals( prop.get( "deleteFeature" ) ) );
            deleteFeature.setBounds( 5, 148, 30, 30 );
        }
        {
            btMoveByDistance = new JButton( getImageIcon( "move_by_distance.png" ) );
            editPanel.add( btMoveByDistance );
            btMoveByDistance.setToolTipText( Messages.getMessage( getLocale(), "$MD11288" ) );
            btMoveByDistance.setBounds( 76, 148, 30, 30 );
            btMoveByDistance.setVisible( "true".equals( prop.get( "moveByDistance" ) ) );
            btMoveByDistance.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent event ) {
                    MoveDialog md = new MoveDialog( DigitizerFunctionSelectPanel.this, appContainer );
                    Pair<Double, Double> distance = md.getDistance();
                    if ( distance != null ) {
                        Map<String, Object> parameter = new HashMap<String, Object>();
                        parameter.put( "distance", distance );
                        digitizerModule.performDigitizingAction( "moveByDistance", parameter );
                    }
                }
            } );
        }

        {
            dummy = new JToggleButton();
            mainButtonGroup.add( dummy );
        }
    }
  
    private void createSnapPanel() {
        snapPanel = new SnappingOptionsPanel( appContainer );
        functionSelectTab.addTab( Messages.getMessage( getLocale(), "$MD11194" ), null, snapPanel, null );
    }

    private void createCreatePanel() {
        createPanel = new JPanel();
        createPanel.addComponentListener( new CreateShownListener() );
        functionSelectTab.addTab( Messages.getMessage( getLocale(), "$MD11195" ), null, createPanel, null );
        createPanel.setLayout( null );
        createPanel.setPreferredSize(new java.awt.Dimension(433, 373));
        {
            btSelectForInsert = new JToggleButton( getImageIcon( "select.gif" ) );
            createPanel.add( btSelectForInsert );
            btSelectForInsert.setBounds( 6, 5, 30, 30 );
            btSelectForInsert.setActionCommand( "selectForInsert" );
            btSelectForInsert.setToolTipText( Messages.getMessage( getLocale(), "$MD10204" ) );
            btSelectForInsert.addActionListener( buttonselectListener );
            btSelectForInsert.addFocusListener( focusListener );
            btSelectForInsert.setVisible( "true".equals( prop.get( "selectForInsert" ) ) );
            mainButtonGroup.add( btSelectForInsert );
        }
        /*
         * { selectPerFilter = new JToggleButton(); createPanel.add( selectPerFilter );
         * selectPerFilter.setActionCommand( "selectPerFilter" ); selectPerFilter.setText( "selectPerFilter" );
         * selectPerFilter.setToolTipText( Messages.getMessage( getLocale(), "$MD10205" ) ); selectPerFilter.setBounds(
         * 42, 5, 30, 30 ); selectPerFilter.addActionListener( buttonselectListener ); selectPerFilter.addFocusListener(
         * focusListener ); mainButtonGroup.add( selectPerFilter ); }
         */
        {
            btDrawRectangle = new JToggleButton( getImageIcon( "shape_square_create.gif" ) );
            createPanel.add( btDrawRectangle );
            btDrawRectangle.setActionCommand( "drawRectangle" );
            btDrawRectangle.setToolTipText( Messages.getMessage( getLocale(), "$MD10206" ) );
            btDrawRectangle.setBounds(6, 82, 30, 30);
            btDrawRectangle.addActionListener( buttonselectListener );
            btDrawRectangle.addFocusListener( focusListener );
            btDrawRectangle.setVisible( "true".equals( prop.get( "drawRectangle" ) ) );
            mainButtonGroup.add( btDrawRectangle );
        }
        {
            btDrawPolygon = new JToggleButton( getImageIcon( "polygon_create.png" ) );
            createPanel.add( btDrawPolygon );
            btDrawPolygon.setActionCommand( "drawPolygon" );
            btDrawPolygon.setToolTipText( Messages.getMessage( getLocale(), "$MD10207" ) );
            btDrawPolygon.setBounds( 6, 41, 30, 30 );
            btDrawPolygon.addActionListener( buttonselectListener );
            btDrawPolygon.addFocusListener( focusListener );
            btDrawPolygon.setVisible( "true".equals( prop.get( "drawPolygon" ) ) );
            mainButtonGroup.add( btDrawPolygon );
        }
        {
            btDrawLineString = new JToggleButton( getImageIcon( "line_create.gif" ) );
            createPanel.add( btDrawLineString );
            btDrawLineString.setActionCommand( "drawLineString" );
            btDrawLineString.setToolTipText( Messages.getMessage( getLocale(), "$MD10208" ) );
            btDrawLineString.setBounds(6, 123, 30, 30);
            btDrawLineString.addActionListener( buttonselectListener );
            btDrawLineString.addFocusListener( focusListener );
            btDrawLineString.setVisible( "true".equals( prop.get( "drawLineString" ) ) );
            mainButtonGroup.add( btDrawLineString );
        }
        {
            btDrawPoint = new JToggleButton( getImageIcon( "point_create.gif" ) );
            createPanel.add( btDrawPoint );
            btDrawPoint.setActionCommand( "drawPoint" );
            btDrawPoint.setToolTipText( Messages.getMessage( getLocale(), "$MD10209" ) );
            btDrawPoint.setBounds(6, 164, 30, 30);
            btDrawPoint.addActionListener( buttonselectListener );
            btDrawPoint.addFocusListener( focusListener );
            btDrawPoint.setVisible( "true".equals( prop.get( "drawPoint" ) ) );
            mainButtonGroup.add( btDrawPoint );
        }
        {
            btDrawCircle = new JToggleButton( getImageIcon( "circle_create.gif" ) );
            createPanel.add( btDrawCircle );
            btDrawCircle.setActionCommand( "drawCircle" );
            btDrawCircle.setToolTipText( Messages.getMessage( getLocale(), "$MD10210" ) );
            btDrawCircle.setBounds(76, 82, 30, 30);
            btDrawCircle.addActionListener( buttonselectListener );
            btDrawCircle.addFocusListener( focusListener );
            btDrawCircle.setVisible( "true".equals( prop.get( "drawCircle" ) ) );
            mainButtonGroup.add( btDrawCircle );
        }
        {
            taCreateDescriptionTA = new JTextArea();
            taCreateDescriptionTA.setLineWrap( true );
            taCreateDescriptionTA.setWrapStyleWord( true );
            taCreateDescriptionTA.setEditable( false );
            String s = Messages.getMessage( getLocale(), "$MD10201" );
            taCreateDescriptionTA.setBorder( BorderFactory.createTitledBorder( s ) );
            createPanel.add( taCreateDescriptionTA );
            taCreateDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10200" ) );
            taCreateDescriptionTA.setBounds(6, 205, 327, 141);
        }
        {
            btDrawPolygonHole = new JToggleButton( getImageIcon( "polygonhole_create.png" ) );
            createPanel.add( btDrawPolygonHole );
            btDrawPolygonHole.setActionCommand( "drawPolygonHole" );
            btDrawPolygonHole.setToolTipText( Messages.getMessage( getLocale(), "$MD10299" ) );
            btDrawPolygonHole.setBounds( 41, 41, 30, 30 );
            btDrawPolygonHole.addActionListener( buttonselectListener );
            btDrawPolygonHole.addFocusListener( focusListener );
            btDrawPolygonHole.setVisible( "true".equals( prop.get( "drawPolygonHole" ) ) );
            mainButtonGroup.add( btDrawPolygonHole );
        }
        {
            // cutting holes into a surface by selecting overlapping surfaces
            btCutPolygonHole = new JButton( getImageIcon( "cut.png" ) );
            createPanel.add( btCutPolygonHole );
            btCutPolygonHole.setActionCommand( "cutPolygonHole" );
            btCutPolygonHole.setToolTipText( Messages.getMessage( getLocale(), "$MD11610" ) );
            btCutPolygonHole.setBounds( 76, 41, 30, 30 );
            btCutPolygonHole.addActionListener( buttonselectListener );
            btCutPolygonHole.addFocusListener( focusListener );
            btCutPolygonHole.setVisible( "true".equals( prop.get( "cutPolygonHole" ) ) );
        }
        {
            btDrawSizedRect = new JToggleButton( getImageIcon( "rect_create_by_size.gif" ) );
            createPanel.add( btDrawSizedRect );
            btDrawSizedRect.setToolTipText( Messages.getMessage( getLocale(), "$MD11086" ) );
            btDrawSizedRect.setBounds(41, 82, 30, 30);
            btDrawSizedRect.addFocusListener( focusListener );
            btDrawSizedRect.setActionCommand( "drawSizedRectangle" );
            btDrawSizedRect.addActionListener( buttonselectListener );
            btDrawSizedRect.setVisible( "true".equals( prop.get( "drawSizedRectangle" ) ) );
            mainButtonGroup.add( btDrawSizedRect );
        }

        {
            btDrawArc = new JButton( getImageIcon( "arcdraw_create.gif" ) );
            createPanel.add( btDrawArc );
            btDrawArc.setToolTipText( Messages.getMessage( getLocale(), "$MD11084" ) );
            btDrawArc.setBounds(75, 123, 30, 30);
            btDrawArc.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    DrawArcDialog.create( digitizerModule );
                    unselectAll();
                }
            } );
            btDrawArc.addFocusListener( focusListener );
            btDrawArc.setVisible( "true".equals( prop.get( "drawArc" ) ) );
        }

        {
            btDrawSizedEllipse = new JToggleButton( getImageIcon( "ellipse_create_by_size.png" ) );
            createPanel.add( btDrawSizedEllipse );
            btDrawSizedEllipse.setToolTipText( Messages.getMessage( getLocale(), "$MD11082" ) );
            btDrawSizedEllipse.setBounds(111, 82, 30, 30);
            btDrawSizedEllipse.addFocusListener( focusListener );
            btDrawSizedEllipse.setActionCommand( "drawSizedEllipse" );
            btDrawSizedEllipse.addActionListener( buttonselectListener );
            btDrawSizedEllipse.setVisible( "true".equals( prop.get( "drawSizedEllipse" ) ) );
            mainButtonGroup.add( btDrawSizedEllipse );
        }
        {
            btDrawPointAt = new JButton( getImageIcon( "point_create_by_coords.gif" ) );
            btDrawPointAt.setVisible( "true".equals( prop.get( "drawPointAt" ) ) );
            mainButtonGroup.add( btDrawPointAt );
            createPanel.add( btDrawPointAt );
            btDrawPointAt.setToolTipText( Messages.getMessage( getLocale(), "$MD11081" ) );
            btDrawPointAt.setBounds(41, 164, 30, 30);
            btDrawPointAt.addFocusListener( focusListener );
            btDrawPointAt.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    unselectAll();
                    PointByCoordinateDialog dlg = new PointByCoordinateDialog( DigitizerFunctionSelectPanel.this,
                                                                               digitizerModule );
                    org.deegree.model.spatialschema.Point pnt = dlg.getPoint();
                    if ( pnt != null ) {
                        digitizerModule.setDigitizingAction( "drawPoint" );
                        List<Geometry> list = new ArrayList<Geometry>();
                        list.add( pnt );
                        digitizerModule.mouseActionFinished( list, -1 );
                    }

                }

            } );
        }
        {
            btDrawParallel = new JButton( getImageIcon( "parallel.png" ) );
            btDrawParallel.setVisible( "true".equals( prop.get( "drawParallel" ) ) );
            createPanel.add( btDrawParallel );
            btDrawParallel.setToolTipText( Messages.getMessage( getLocale(), "$MD11088" ) );
            btDrawParallel.setBounds(41, 123, 30, 30);
            btDrawParallel.addFocusListener( focusListener );
            btDrawParallel.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    MapModel mapModel = appContainer.getMapModel( null );
                    List<Layer> list = mapModel.getLayersSelectedForAction( MapModel.SELECTION_EDITING );
                    Layer layer = list.get( 0 );
                    if ( layer.getSelectedFeatures().size() == 0 ) {
                        DialogFactory.openErrorDialog( "application", DigitizerFunctionSelectPanel.this,
                                                       Messages.getMessage( getLocale(), "$MD11286" ),
                                                       Messages.getMessage( getLocale(), "$MD11287" ) );
                        return;
                    }
                    new ParallelDialog( DigitizerFunctionSelectPanel.this, digitizerModule );
                }
            } );

        }

        {
            btSetDigitizeAngle = new JButton( getImageIcon( "angle.png" ) );
            createPanel.add( btSetDigitizeAngle );
            btSetDigitizeAngle.setToolTipText( Messages.getMessage( getLocale(), "$MD11090" ) );
            btSetDigitizeAngle.setBounds(112, 5, 30, 30);
            btSetDigitizeAngle.addFocusListener( focusListener );
            btSetDigitizeAngle.setVisible( "true".equals( prop.get( "setDigitizeAngle" ) ) );
            btSetDigitizeAngle.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    new ArcndLengthDialog( DigitizerFunctionSelectPanel.this, digitizerModule );
                }
            } );
        }
        {
            btDrawPolygonByFillingHole = new JToggleButton( IconRegistry.getIcon( "paintcan.png" ) );
            createPanel.add( btDrawPolygonByFillingHole );
            btDrawPolygonByFillingHole.setToolTipText( Messages.getMessage( getLocale(), "$MD11291" ) );
            btDrawPolygonByFillingHole.setBounds( 112, 41, 30, 30 );
            btDrawPolygonByFillingHole.addFocusListener( focusListener );
            btDrawPolygonByFillingHole.setActionCommand( "drawPolygonByFillingHole" );
            btDrawPolygonByFillingHole.addActionListener( buttonselectListener );
            btDrawPolygonByFillingHole.setVisible( "true".equals( prop.get( "drawPolygonByFillingHole" ) ) );
            mainButtonGroup.add( btDrawPolygonByFillingHole );
        }
        {
            btJoinCurves = new JButton( "CL" );
            createPanel.add( btJoinCurves );
            btJoinCurves.setBounds(112, 123, 30, 30);
            btJoinCurves.setToolTipText( Messages.getMessage( getLocale(), "$MD11614" ) );
            btJoinCurves.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    JoinCurveDialog.create( digitizerModule );
                    unselectAll();
                }
            } );
            btJoinCurves.addFocusListener( focusListener );
            btJoinCurves.setVisible( "true".equals( prop.get( "joinCurves" ) ) );
            mainButtonGroup.add( btJoinCurves );
        }

    }

    private Icon getImageIcon( String image ) {
        URL url = DigitizerFunctionSelectPanel.class.getResource( "resources/" + image );
        return IconRegistry.getIcon( url );
    }

    private void fillTreeModel() {
        DefaultTreeModel treeModel = (DefaultTreeModel) optionTree.getModel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode( Messages.getMessage( getLocale(), "$MD10231" ) );
        JPanel panel = new OptionsPanel();
        root.setUserObject( panel );
        optionsplit.add( panel, JSplitPane.RIGHT );
        optionsplit.invalidate();
        optionsplit.repaint();
        root.removeAllChildren();
        treeModel.setRoot( root );
        optionTree.setModel( treeModel );

        DefaultMutableTreeNode vertices = new DefaultMutableTreeNode( "-" );
        vertices.setUserObject( new VerticesOptionsPanel( appContainer ) );
        DefaultMutableTreeNode lines = new DefaultMutableTreeNode( "-" );
        lines.setUserObject( new LineOptionsPanel( appContainer ) );
        root.add( vertices );
        root.add( lines );
    }

    /**
     * 
     * @return additional parameters that may be assigned to a selected action
     */
    public Map<String, Object> getSelectionParameters() {
        return selectionParams;
    }

    /**
     * 
     * @return digitizing options
     */
    public Map<String, Object> getOptions() {
        return options;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.deegree.igeo.views.swing.editfeature.DigitizerFunctionSelect#registerDigitizerModule(org.deegree.client.
     * application.modules.DigitizerModule)
     */
    @SuppressWarnings("unchecked")
    public void registerDigitizerModule( DigitizerModule<?> digitizerModule ) {
        this.digitizerModule = (DigitizerModule<Container>) digitizerModule;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.digitize.DigitizerFunctionSelect#unselectAll()
     */
    public void unselectAll() {
        mainButtonGroup.removeSelection();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.digitize.DigitizerFunctionSelect#selectFunction(java.lang.String)
     */
    public void selectFunction( String name ) {
        if ( !selectFunction( name, this ) ) {
            LOG.logWarning( "no function with name: " + name + " available" );
        }
    }

    private boolean selectFunction( String name, JComponent parent ) {
        Component[] components = parent.getComponents();
        for ( Component component : components ) {
            if ( component instanceof AbstractButton ) {
                if ( name.equals( ( (AbstractButton) component ).getActionCommand() ) ) {
                    ( (AbstractButton) component ).doClick();
                    this.invalidate();
                    this.getParent().repaint();
                    return true;
                }
            } else {
                if ( selectFunction( name, (JComponent) component ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    // ///////////////////////////////////////////////////////////////////////////////
    // inner classes
    // ///////////////////////////////////////////////////////////////////////////////

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    private class ButtonSelectListener implements ActionListener, FocusListener {

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed( ActionEvent e ) {
            appContainer.resetToolbar();
            AbstractButton button = (AbstractButton) e.getSource();
            if ( digitizerModule != null && button instanceof JToggleButton ) {
                // inform registered DigitizerModule about current selected action
                digitizerModule.setDigitizingAction( button.getActionCommand() );
                setExplainationText( button );
            } else {
                // must be a JButton
                digitizerModule.performDigitizingAction( button.getActionCommand() );
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
         */
        public void focusGained( FocusEvent e ) {
            AbstractButton button = (AbstractButton) e.getSource();
            setExplainationText( button );
            if ( button instanceof JToggleButton ) {
                // button.setSelected( true );
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
         */
        public void focusLost( FocusEvent e ) {
            // taCreateDescriptionTA.setText( "" );
            // editDescriptionTA.setText( "" );
        }

        private void setExplainationText( AbstractButton button ) {

            if ( btSelectForInsert == button ) {
                taCreateDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10232" ) );
            } else if ( selectPerFilter == button ) {
                taCreateDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10233" ) );
            } else if ( btDrawRectangle == button ) {
                taCreateDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10234" ) );
            } else if ( btDrawCircle == button ) {
                taCreateDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10235" ) );
            } else if ( fillArc == button ) {
                taCreateDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10236" ) );
            } else if ( btDrawArc == button ) {
                taCreateDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10300" ) );
            } else if ( btDrawPolygon == button ) {
                taCreateDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10237" ) );
            } else if ( btDrawPolygonHole == button ) {
                taCreateDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10307" ) );
            } else if ( btDrawLineString == button ) {
                taCreateDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10238" ) );
            } else if ( btDrawPoint == button ) {
                taCreateDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10239" ) );
            } else if ( select2ForEdit == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10240" ) );
            } else if ( select2PerLine == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10241" ) );
            } else if ( select2PerPolygon == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10242" ) );
            } else if ( select2PerFilter == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10243" ) );
            } else if ( insertVertex == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10244" ) );
            } else if ( moveVertex == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10245" ) );
            } else if ( deleteVertex == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10246" ) );
            } else if ( mergeVertices == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10247" ) );
            } else if ( splitLine == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10248" ) );
            } else if ( splitPolygon == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10249" ) );
            } else if ( groupPoints == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10250" ) );
            } else if ( groupLines == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10251" ) );
            } else if ( groupPolygons == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10252" ) );
            } else if ( ungroupMultiPoint == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10253" ) );
            } else if ( ungroupMultiCurve == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10254" ) );
            } else if ( ungroupMultiPolygon == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10255" ) );
            } else if ( deleteFeature == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10256" ) );
            } else if ( moveFeature == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10257" ) );
            } else if ( rotateFeature == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10258" ) );
            } else if ( unitePolygons == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10274" ) );
            } else if ( uniteLineStrings == button ) {
                editDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD10276" ) );
            } else if ( btDrawPointAt == button ) {
                taCreateDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD11080" ) );
            } else if ( btDrawSizedEllipse == button ) {
                taCreateDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD11083" ) );
            } else if ( btDrawArc == button ) {
                taCreateDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD11085" ) );
            } else if ( btDrawSizedRect == button ) {
                taCreateDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD11087" ) );
            } else if ( btDrawParallel == button ) {
                taCreateDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD11089" ) );
            } else if ( btSetDigitizeAngle == button ) {
                taCreateDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD11091" ) );
            } else if ( btDrawPolygonByFillingHole == button ) {
                taCreateDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD11292" ) );
            } else if ( btJoinCurves == button ) {
                taCreateDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD11584" ) );
            } else if ( btCutPolygonHole == button ) {
                taCreateDescriptionTA.setText( Messages.getMessage( getLocale(), "$MD11611" ) );
            }
        }

    }

    @Override
    public boolean isFocusable() {
        return true;
    }

    // ///////////////////////////////////////////////////////////////////////////////////
    // inner classes
    // ///////////////////////////////////////////////////////////////////////////////////

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    private class OptionsMouseListener extends MouseAdapter {

        @Override
        public void mouseClicked( MouseEvent event ) {
            Point dropPoint = event.getPoint();
            TreePath path = optionTree.getPathForLocation( dropPoint.x, dropPoint.y );
            if ( path != null ) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();

                Component[] comps = optionsplit.getComponents();
                for ( Component component : comps ) {
                    if ( component instanceof JPanel ) {
                        optionsplit.remove( component );
                    }
                }
                JPanel panel = (JPanel) node.getUserObject();
                optionsplit.add( panel, JSplitPane.RIGHT );
                optionsplit.invalidate();
                optionsplit.repaint();
            }
        }

    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    private class CellRenderer extends DefaultTreeCellRenderer {

        private static final long serialVersionUID = -2295842834575094286L;

        @Override
        public Component getTreeCellRendererComponent( JTree tree, Object value, boolean selected, boolean expanded,
                                                       boolean leaf, int row, boolean hasFocus ) {
            super.getTreeCellRendererComponent( tree, value, selected, expanded, leaf, row, hasFocus );

            setIcon( null );

            return this;
        }

    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    private class DFSPActionListener implements ActionListener {

        private AbstractButton command;

        /**
         * 
         * @param command
         */
        public DFSPActionListener( AbstractButton command ) {
            this.command = command;
        }

        public void actionPerformed( ActionEvent e ) {
            selectFunction( command.getActionCommand() );
        }
    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    private class CreateShownListener extends ComponentAdapter {

        @Override
        public void componentShown( ComponentEvent event ) {

            // KeyStroke keyStroke = KeyStroke.getKeyStroke( KeyEvent.getKeyText(KeyEvent.VK_H ) );
            KeyListenerRegister.registerDefaultKeyListener( DigitizerFunctionSelectPanel.this );
            // create actions
            KeyStroke keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_S, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( btSelectForInsert ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_F, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( selectPerFilter ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_D, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( deleteFeature ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_R, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( btDrawRectangle ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_I, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( btDrawCircle ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_A, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( fillArc ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_P, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( btDrawPolygon ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_H, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( btDrawPolygonHole ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_B, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( btDrawArc ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_L, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( btDrawLineString ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_T, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( btDrawPoint ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

        }
    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    private class EditShownListener extends ComponentAdapter {

        @Override
        public void componentShown( ComponentEvent event ) {
            // edit actions

            KeyStroke keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_S, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( select2ForEdit ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_F, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( select2PerFilter ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_I, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( insertVertex ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_M, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( moveVertex ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_D, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( deleteVertex ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_E, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( mergeVertices ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_T, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( groupPoints ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_L, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( groupLines ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_P, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( groupPolygons ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_T, InputEvent.ALT_MASK | InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( ungroupMultiPoint ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_L, InputEvent.ALT_MASK | InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( ungroupMultiCurve ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_P, InputEvent.ALT_MASK | InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( ungroupMultiPolygon ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_Z, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( deleteFeature ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0 );
            registerKeyboardAction( new DFSPActionListener( deleteFeature ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_O, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( moveFeature ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
            keyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_R, InputEvent.CTRL_MASK );
            registerKeyboardAction( new DFSPActionListener( rotateFeature ), keyStroke,
                                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );

        }

    }

}
