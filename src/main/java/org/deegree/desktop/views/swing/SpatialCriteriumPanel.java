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

package org.deegree.desktop.views.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.LayerGroup;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.views.swing.addlayer.QualifiedNameRenderer;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.utils.SwingUtils;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.feature.Feature;
import org.deegree.model.filterencoding.Operation;
import org.deegree.model.filterencoding.OperationDefines;
import org.deegree.model.filterencoding.PropertyName;
import org.deegree.model.filterencoding.SpatialOperation;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Surface;

/**
 * <code>SpatialCriteriaPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
class SpatialCriteriumPanel extends JPanel {

    private static final long serialVersionUID = -9121476995238855181L;

    private static final ILogger LOG = LoggerFactory.getLogger( SpatialCriteriumPanel.class );

    private enum GEOMTYPE {
        CURVE, POINT, SURFACE, MIXED
    };

    private List<SpatialOperator> spatialOperators;

    // spatial operator
    private int selectedSpaOp = OperationDefines.UNKNOWN;

    // geometry to compare
    private String selectedRefGeom = BBOX_GEOM;

    public static final String BBOX_GEOM = "BBOX_GEOM";

    public static final String SELECTED_GEOM = "SELECTED_GEOM";

    private List<QualifiedName> geomPropNames;

    private MapModel mapModel;

    private JComboBox geomPropsCombo;

    private JFormattedTextField dwithinValue;

    private JFormattedTextField beyondValue;

    private List<Geometry> selectedGeometries;

    private GEOMTYPE selectedGeomType;

    /*
     * @param geomPropNames list of all available property names of the type GEOMETRY
     * 
     * @param mapModelAdapter the mapModelAdaper
     */
    SpatialCriteriumPanel( List<QualifiedName> geomPropNames, MapModel mapModel ) {
        this.geomPropNames = geomPropNames;
        this.mapModel = mapModel;

        // init the list containing all spatial operations
        spatialOperators = new ArrayList<SpatialOperator>();

        String no = Messages.getMessage( Locale.getDefault(), "$MD10166" );
        String noTT = Messages.getMessage( Locale.getDefault(), "$MD10167" );
        spatialOperators.add( new SpatialOperator( OperationDefines.UNKNOWN, no, noTT ) );

        String bbox = Messages.getMessage( Locale.getDefault(), "$MD10168" );
        String bboxTT = Messages.getMessage( Locale.getDefault(), "$MD10169" );
        spatialOperators.add( new SpatialOperator( OperationDefines.BBOX, bbox, bboxTT ) );

        String contains = Messages.getMessage( Locale.getDefault(), "$MD10170" );
        String containsTT = Messages.getMessage( Locale.getDefault(), "$MD10171" );
        spatialOperators.add( new SpatialOperator( OperationDefines.CONTAINS, contains, containsTT ) );

        String crosses = Messages.getMessage( Locale.getDefault(), "$MD10172" );
        String crossesTT = Messages.getMessage( Locale.getDefault(), "$MD10173" );
        spatialOperators.add( new SpatialOperator( OperationDefines.CROSSES, crosses, crossesTT ) );

        String disjoint = Messages.getMessage( Locale.getDefault(), "$MD10174" );
        String disjointTT = Messages.getMessage( Locale.getDefault(), "$MD10175" );
        spatialOperators.add( new SpatialOperator( OperationDefines.DISJOINT, disjoint, disjointTT ) );

        String equals = Messages.getMessage( Locale.getDefault(), "$MD10176" );
        String equalsTT = Messages.getMessage( Locale.getDefault(), "$MD10177" );
        spatialOperators.add( new SpatialOperator( OperationDefines.EQUALS, equals, equalsTT ) );

        String intersects = Messages.getMessage( Locale.getDefault(), "$MD10178" );
        String intersectsTT = Messages.getMessage( Locale.getDefault(), "$MD10179" );
        spatialOperators.add( new SpatialOperator( OperationDefines.INTERSECTS, intersects, intersectsTT ) );

        String overlaps = Messages.getMessage( Locale.getDefault(), "$MD10180" );
        String overlapsTT = Messages.getMessage( Locale.getDefault(), "$MD10181" );
        spatialOperators.add( new SpatialOperator( OperationDefines.OVERLAPS, overlaps, overlapsTT ) );

        String touches = Messages.getMessage( Locale.getDefault(), "$MD10182" );
        String touchesTT = Messages.getMessage( Locale.getDefault(), "$MD10183" );
        spatialOperators.add( new SpatialOperator( OperationDefines.TOUCHES, touches, touchesTT ) );

        String within = Messages.getMessage( Locale.getDefault(), "$MD10184" );
        String withinTT = Messages.getMessage( Locale.getDefault(), "$MD10185" );
        spatialOperators.add( new SpatialOperator( OperationDefines.WITHIN, within, withinTT ) );

        String beyond = Messages.getMessage( Locale.getDefault(), "$MD10186" );
        String beyondTT = Messages.getMessage( Locale.getDefault(), "$MD10187" );
        spatialOperators.add( new SpatialOperator( OperationDefines.BEYOND, beyond, beyondTT ) );

        String dwithin = Messages.getMessage( Locale.getDefault(), "$MD10188" );
        String dwithinTT = Messages.getMessage( Locale.getDefault(), "$MD10189" );
        spatialOperators.add( new SpatialOperator( OperationDefines.DWITHIN, dwithin, dwithinTT ) );

        selectedGeometries = getSelectedGeometries( mapModel.getLayerGroups() );

        // init the gui
        init();
    }

    private void init() {
        GridBagConstraints gbc = SwingUtils.initPanel( this );

        // combo box to choose geometry property
        JPanel geomPropPanel = new JPanel();
        JLabel geomPropLabel = new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10161" ) );
        geomPropsCombo = new JComboBox();
        geomPropsCombo.setRenderer( new QualifiedNameRenderer() );
        geomPropsCombo.setPreferredSize( new Dimension( 200, 22 ) );
        geomPropPanel.add( geomPropLabel );
        geomPropPanel.add( geomPropsCombo );
        for ( QualifiedName name : geomPropNames ) {
            geomPropsCombo.addItem( name );
        }

        // action listener for spatial operators to get the selected
        ActionListener spatialOpAl = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JRadioButton rb = (JRadioButton) e.getSource();
                selectedSpaOp = OperationDefines.getIdByName( rb.getActionCommand() );
                if ( selectedSpaOp == OperationDefines.BEYOND ) {
                    beyondValue.setEnabled( true );
                    dwithinValue.setEnabled( false );
                } else if ( selectedSpaOp == OperationDefines.DWITHIN ) {
                    dwithinValue.setEnabled( true );
                    beyondValue.setEnabled( false );
                } else {
                    beyondValue.setEnabled( false );
                    dwithinValue.setEnabled( false );
                }
            }
        };
        // create a radioButton for every spatial operator and add them to the panel
        JPanel spatialOpPanel = new JPanel();
        GridBagConstraints spatialOpGbc = SwingUtils.initPanel( spatialOpPanel );
        spatialOpGbc.anchor = GridBagConstraints.LINE_START;
        ButtonGroup spatOpGroup = new ButtonGroup();
        for ( SpatialOperator spatOp : this.spatialOperators ) {
            JRadioButton rb = new JRadioButton( spatOp.text );
            rb.setToolTipText( spatOp.toolTipText );
            rb.setActionCommand( spatOp.getOperationName() );
            rb.addActionListener( spatialOpAl );
            spatOpGroup.add( rb );
            spatialOpPanel.add( rb, spatialOpGbc );
            ++spatialOpGbc.gridy;
            if ( spatOp.operationId == OperationDefines.UNKNOWN ) {
                rb.setSelected( true );
            }
        }

        // text fields for BEYOND/DWITHIN operation
        Dimension textFieldDim = new Dimension( 150, 20 );
        beyondValue = new JFormattedTextField();
        beyondValue.setValue( new Double( 0.0 ) );
        beyondValue.setPreferredSize( textFieldDim );
        dwithinValue = new JFormattedTextField();
        dwithinValue.setPreferredSize( textFieldDim );
        dwithinValue.setValue( new Double( 0.0 ) );
        beyondValue.setEnabled( false );
        dwithinValue.setEnabled( false );

        gbc.gridx = 1;
        gbc.gridy = spatialOpPanel.getComponentCount() - 2;
        spatialOpPanel.add( beyondValue, gbc );
        ++gbc.gridy;
        spatialOpPanel.add( dwithinValue, gbc );
        spatialOpPanel.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( Locale.getDefault(),
                                                                                         "$MD10162" ) ) );

        // actionListener to get the selected reference geometry
        ActionListener geomAl = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                JRadioButton rb = (JRadioButton) e.getSource();
                selectedRefGeom = rb.getActionCommand();
            }
        };
        // create panel with available reference geometries
        JPanel compareGeomPanel = new JPanel();
        compareGeomPanel.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( Locale.getDefault(),
                                                                                           "$MD10163" ) ) );
        JRadioButton bbox = new JRadioButton( Messages.getMessage( Locale.getDefault(), "$MD10164" ) );
        JRadioButton selectedGeom = new JRadioButton( Messages.getMessage( Locale.getDefault(), "$MD10165" ) );

        if ( selectedGeometries == null || selectedGeometries.size() == 0 ) {
            selectedGeom.setEnabled( false );
        }
        ButtonGroup compGeomGroup = new ButtonGroup();
        compGeomGroup.add( bbox );
        compareGeomPanel.add( bbox );
        bbox.setActionCommand( BBOX_GEOM );
        bbox.addActionListener( geomAl );
        compGeomGroup.add( selectedGeom );
        compareGeomPanel.add( selectedGeom );
        selectedGeom.setActionCommand( SELECTED_GEOM );
        selectedGeom.addActionListener( geomAl );

        bbox.setSelected( true );

        // add all components to spatial panel
        gbc.anchor = GridBagConstraints.CENTER;

        this.add( geomPropPanel, gbc );

        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ++gbc.gridy;
        this.add( spatialOpPanel, gbc );

        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        ++gbc.gridy;
        this.add( compareGeomPanel, gbc );
    }

    /**
     * @return the selected property name
     */
    private PropertyName getPropertyName() {
        QualifiedName qName = (QualifiedName) this.geomPropsCombo.getSelectedItem();
        return new PropertyName( qName );
    }

    /**
     * @return the selected operator
     */
    private int getOperator() {
        return selectedSpaOp;
    }

    /**
     * @return the reference geometry
     */
    private Geometry getReferenceGeometry() {
        Geometry geom = null;
        if ( this.selectedRefGeom.equals( SELECTED_GEOM ) ) {
            Envelope extent = this.mapModel.getEnvelope();
            CoordinateSystem crs = extent.getCoordinateSystem();
            // create MultiXXX dependent on the type of the geometry to avoid problems with
            // MultiGeometries id not needed
            switch ( selectedGeomType ) {
            case CURVE:
                Curve[] curveList = (Curve[]) selectedGeometries.toArray( new Curve[selectedGeometries.size()] );
                geom = GeometryFactory.createMultiCurve( curveList, crs );
                break;
            case POINT:
                Point[] pointList = (Point[]) selectedGeometries.toArray( new Point[selectedGeometries.size()] );
                geom = GeometryFactory.createMultiPoint( pointList, crs );
                break;
            case SURFACE:
                Surface[] surfaceList = (Surface[]) selectedGeometries.toArray( new Surface[selectedGeometries.size()] );
                geom = GeometryFactory.createMultiSurface( surfaceList, crs );
                break;
            default:
                Geometry[] geomList = (Geometry[]) selectedGeometries.toArray( new Geometry[selectedGeometries.size()] );
                geom = GeometryFactory.createMultiGeometry( geomList, crs );
                break;
            }
        } else {
            Envelope extent = this.mapModel.getEnvelope();
            CoordinateSystem crs = extent.getCoordinateSystem();
            try {
                geom = GeometryFactory.createSurface( extent, crs );
            } catch ( GeometryException e ) {
                LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10020" ), e );
            }
        }
        return geom;
    }

    private List<Geometry> getSelectedGeometries( List<LayerGroup> layerGroups ) {
        List<Geometry> selectedGeometries = new ArrayList<Geometry>();
        for ( LayerGroup layerGroup : layerGroups ) {
            selectedGeometries.addAll( getSelectedGeometries( ( layerGroup.getLayerGroups() ) ) );
            for ( Layer layer : layerGroup.getLayers() ) {
                Iterator<Feature> iterator = layer.getSelectedFeatures().iterator();
                while ( iterator.hasNext() ) {
                    Feature feature = iterator.next();
                    Geometry geom = feature.getDefaultGeometryPropertyValue();
                    // detect type of the geometry
                    if ( ( selectedGeomType == null || selectedGeomType.equals( GEOMTYPE.CURVE ) )
                         && geom instanceof Curve ) {
                        selectedGeomType = GEOMTYPE.CURVE;
                    } else if ( ( selectedGeomType == null || selectedGeomType.equals( GEOMTYPE.POINT ) )
                                && geom instanceof Point ) {
                        selectedGeomType = GEOMTYPE.POINT;
                    } else if ( ( selectedGeomType == null || selectedGeomType.equals( GEOMTYPE.SURFACE ) )
                                && geom instanceof Surface ) {
                        selectedGeomType = GEOMTYPE.SURFACE;
                    } else {
                        selectedGeomType = GEOMTYPE.MIXED;
                    }
                    selectedGeometries.add( geom );
                }
            }
        }
        return selectedGeometries;
    }

    /**
     * @return the spatial operation created in this dialog
     */
    public Operation getOperation() {
        SpatialOperation spatialOp = null;

        switch ( this.selectedSpaOp ) {
        case OperationDefines.BBOX:
        case OperationDefines.WITHIN:
        case OperationDefines.CONTAINS:
        case OperationDefines.CROSSES:
        case OperationDefines.DISJOINT:
        case OperationDefines.EQUALS:
        case OperationDefines.INTERSECTS:
        case OperationDefines.OVERLAPS:
        case OperationDefines.TOUCHES: {
            spatialOp = new SpatialOperation( getOperator(), getPropertyName(), getReferenceGeometry() );
            break;
        }
        case OperationDefines.DWITHIN: {
            double distDWitihn = new Double( dwithinValue.getText() );
            spatialOp = new SpatialOperation( getOperator(), getPropertyName(), getReferenceGeometry(), distDWitihn );
            break;
        }
        case OperationDefines.BEYOND: {
            double distBeyond = new Double( beyondValue.getText() );
            spatialOp = new SpatialOperation( getOperator(), getPropertyName(), getReferenceGeometry(), distBeyond );
            break;
        }
        }

        return spatialOp;
    }

    /**
     * The <code>SpatialOperator</code> combines the type of the operation with the text shown in
     * the gui and a toolTip.
     * 
     * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     * 
     */
    private class SpatialOperator {

        int operationId;

        String text;

        String toolTipText;

        /**
         * @param operationId
         *            the {@link OperationDefines} id
         * @param text
         *            the text
         * @param toolTipText
         *            the toolTip text
         */
        SpatialOperator( int operationId, String text, String toolTipText ) {
            this.operationId = operationId;
            this.text = text;
            this.toolTipText = toolTipText;

        }

        /**
         * @return the name of the operation assigned to the operationId defined in class
         *         {@link OperationDefines}
         */
        String getOperationName() {
            return OperationDefines.getNameById( operationId );
        }

    }
}