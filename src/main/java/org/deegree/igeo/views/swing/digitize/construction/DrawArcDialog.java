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
package org.deegree.igeo.views.swing.digitize.construction;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.GeometryUtils;
import org.deegree.framework.util.Pair;
import org.deegree.framework.utils.LineUtils;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.digitize.InsertFeatureCommand;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.DigitizerModule;
import org.deegree.igeo.state.mapstate.ToolState;
import org.deegree.igeo.state.mapstate.EditState.CreateArcFeatureState;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.swing.drawingpanes.CreateArcDrawingPane;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.kernel.Command;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.spatialschema.Curve;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.Point;
import org.deegree.model.spatialschema.Position;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class DrawArcDialog extends JDialog {

    private static final long serialVersionUID = 1002972169326008273L;

    private static final ILogger LOG = LoggerFactory.getLogger( DrawArcDialog.class );

    private JPanel pnButtons;

    private JPanel pnHelp;

    private JPanel pnRotation;

    private JSpinner spNoOfVertices;

    private JComboBox cbUOM;

    private JLabel lbUOM;

    private JPanel pnUOM;

    private JSpinner spArcRadius;

    private JButton btHelp;

    private JPanel pnDescription;

    private JPanel pnRadius;

    private JSpinner spArc;

    private JPanel pnArc;

    private JTextPane tpDescription;

    private JPanel pnControl;

    private JButton btCancel;

    private JButton btOK;

    private DigitizerModule<Container> digitizerModule;

    private CreateArcDrawingPane drawingPane;

    private static DrawArcDialog drawArcDialog;

    /**
     * 
     * @param owner
     */
    private DrawArcDialog( DigitizerModule<Container> digitizerModule ) {
        this.digitizerModule = digitizerModule;

        addWindowListener( new WindowAdapter() {
            public void windowActivated( WindowEvent e ) {
                ToolState ts = DrawArcDialog.this.digitizerModule.getMapModule().getMapTool().getState();
                if ( !ts.getClass().equals( CreateArcFeatureState.class ) ) {
                    // if the dialog is activated and the current state of the active MapModule is not
                    // equal to CreateArcFeatureState the DigitizerModule must be informed and the state
                    // must be changed to CreateArcFeatureState
                    DrawArcDialog.this.digitizerModule.setDigitizingAction( "drawArc" );
                    // register call back method
                    Pair<Object, String> pair = new Pair<Object, String>( DrawArcDialog.this, "createArc" );
                    DrawArcDialog.this.digitizerModule.setOnActionFinished( pair );
                }
            };

            public void windowClosing( WindowEvent e ) {
                dispose();
            };
        } );
        initGUI();
        setLocation( 200, 200 );
    }

    /**
     * 
     * @param digitizerModule
     * @return singleton instance of @see {@link DrawArcDialog}
     */
    @SuppressWarnings("unchecked")
    public static DrawArcDialog create( DigitizerModule<?> digitizerModule ) {
        if ( drawArcDialog == null ) {
            drawArcDialog = new DrawArcDialog( (DigitizerModule<Container>) digitizerModule );
        }
        drawArcDialog.setAlwaysOnTop( true );
        drawArcDialog.setVisible( true );
        return drawArcDialog;
    }

    @Override
    public void dispose() {
        digitizerModule.setDigitizingAction( null );
        if ( drawingPane != null && drawingPane.getDrawObjectsAsGeoPoints() != null ) {
            drawingPane.getDrawObjectsAsGeoPoints().clear();
        }
        digitizerModule.resetDigitizerPane();
        digitizerModule.resetFunctionSelect();
        digitizerModule.getMapModule().update();
        super.dispose();
    }

    private void initGUI() {
        try {
            {
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] { 0.0, 0.1 };
                thisLayout.rowHeights = new int[] { 267, 7 };
                thisLayout.columnWeights = new double[] { 0.0, 0.1, 0.1 };
                thisLayout.columnWidths = new int[] { 154, 7, 7 };
                getContentPane().setLayout( thisLayout );
                {
                    pnButtons = new JPanel();
                    FlowLayout pnButtonsLayout = new FlowLayout();
                    pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                    pnButtons.setLayout( pnButtonsLayout );
                    getContentPane().add(
                                          pnButtons,
                                          new GridBagConstraints( 0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    {
                        btOK = new JButton( Messages.getMessage( getLocale(), "$MD11617" ),
                                            IconRegistry.getIcon( "accept.png" ) );
                        pnButtons.add( btOK );
                        btOK.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                createArc();
                                drawingPane.getDrawObjectsAsGeoPoints().clear();
                                digitizerModule.getMapModule().update();
                            }
                        } );
                    }
                    {
                        btCancel = new JButton( Messages.getMessage( getLocale(), "$MD11618" ),
                                                IconRegistry.getIcon( "cancel.png" ) );
                        pnButtons.add( btCancel );
                        btCancel.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                dispose();
                            }
                        } );
                    }
                }
                {
                    pnHelp = new JPanel();
                    FlowLayout pnHelpLayout = new FlowLayout();
                    pnHelpLayout.setAlignment( FlowLayout.RIGHT );
                    pnHelp.setLayout( pnHelpLayout );
                    getContentPane().add(
                                          pnHelp,
                                          new GridBagConstraints( 2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    {
                        btHelp = new JButton( Messages.getMessage( getLocale(), "$MD11619" ),
                                              IconRegistry.getIcon( "help.png" ) );
                        pnHelp.add( btHelp );
                    }
                }
                {
                    pnDescription = new JPanel();
                    BorderLayout pnDescriptionLayout = new BorderLayout();
                    pnDescription.setLayout( pnDescriptionLayout );
                    getContentPane().add(
                                          pnDescription,
                                          new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnDescription.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                    "$MD11620" ) ) );
                    {
                        tpDescription = new JTextPane();
                        pnDescription.add( tpDescription, BorderLayout.CENTER );
                        tpDescription.setBackground( pnDescription.getBackground() );
                        tpDescription.setEditable( false );
                        tpDescription.setContentType( "text/html" );
                        tpDescription.setText( Messages.getMessage( getLocale(), "$MD11621" ) );
                    }
                }
                {
                    pnControl = new JPanel();
                    GridBagLayout pnControlLayout = new GridBagLayout();
                    getContentPane().add(
                                          pnControl,
                                          new GridBagConstraints( 1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnControl.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11622" ) ) );
                    pnControlLayout.rowWeights = new double[] { 0.0, 0.0, 0.1 };
                    pnControlLayout.rowHeights = new int[] { 63, 109, 20 };
                    pnControlLayout.columnWeights = new double[] { 0.1 };
                    pnControlLayout.columnWidths = new int[] { 7 };
                    pnControl.setLayout( pnControlLayout );
                    {
                        pnArc = new JPanel();
                        GridBagLayout pnArcLayout = new GridBagLayout();
                        pnControl.add( pnArc, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                      GridBagConstraints.BOTH,
                                                                      new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                        pnArc.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11623" ) ) );
                        pnArcLayout.rowWeights = new double[] { 0.1 };
                        pnArcLayout.rowHeights = new int[] { 7 };
                        pnArcLayout.columnWeights = new double[] { 0.1 };
                        pnArcLayout.columnWidths = new int[] { 7 };
                        pnArc.setLayout( pnArcLayout );
                        {
                            spArc = new JSpinner( new SpinnerNumberModel( 0, -181, 181, 0.5 ) );
                            pnArc.add( spArc, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                      GridBagConstraints.HORIZONTAL,
                                                                      new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                            spArc.addChangeListener( new ChangeListener() {

                                public void stateChanged( ChangeEvent e ) {
                                    applyArc();
                                }

                            } );
                        }
                    }
                    {
                        pnRadius = new JPanel();
                        GridBagLayout pnRadiusLayout = new GridBagLayout();
                        pnControl.add( pnRadius,
                                       new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                               GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                        pnRadius.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                   "$MD11624" ) ) );
                        pnRadiusLayout.rowWeights = new double[] { 0.0, 0.1 };
                        pnRadiusLayout.rowHeights = new int[] { 43, 20 };
                        pnRadiusLayout.columnWeights = new double[] { 0.1 };
                        pnRadiusLayout.columnWidths = new int[] { 7 };
                        pnRadius.setLayout( pnRadiusLayout );
                        {
                            spArcRadius = new JSpinner( new SpinnerNumberModel( 1d, 0.0001, 999999999, 0.5 ) );
                            pnRadius.add( spArcRadius, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                               GridBagConstraints.CENTER,
                                                                               GridBagConstraints.HORIZONTAL,
                                                                               new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                            spArcRadius.addChangeListener( new ChangeListener() {

                                public void stateChanged( ChangeEvent e ) {
                                    applyRadius();
                                }
                            } );
                        }
                        {
                            pnUOM = new JPanel();
                            FlowLayout pnUOMLayout = new FlowLayout();
                            pnUOMLayout.setAlignment( FlowLayout.LEFT );
                            pnUOMLayout.setHgap( 9 );
                            pnUOMLayout.setVgap( 10 );
                            pnUOM.setLayout( pnUOMLayout );
                            pnRadius.add( pnUOM,
                                          new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                            {
                                lbUOM = new JLabel( Messages.getMessage( getLocale(), "$MD11625" ) );
                                pnUOM.add( lbUOM );
                                lbUOM.setPreferredSize( new Dimension( 102, 16 ) );
                            }
                            {
                                String tmp = Messages.getMessage( getLocale(), "$MD11626" );
                                cbUOM = new JComboBox( new DefaultComboBoxModel( tmp.split( "," ) ) );
                                pnUOM.add( cbUOM );
                                cbUOM.setPreferredSize( new Dimension( 136, 22 ) );
                            }
                        }
                    }
                    {
                        pnRotation = new JPanel();
                        GridBagLayout pnRotationLayout = new GridBagLayout();
                        pnControl.add( pnRotation, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0,
                                                                           GridBagConstraints.CENTER,
                                                                           GridBagConstraints.BOTH, new Insets( 0, 0,
                                                                                                                0, 0 ),
                                                                           0, 0 ) );
                        pnRotation.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                     "$MD11627" ) ) );
                        pnRotationLayout.rowWeights = new double[] { 0.1 };
                        pnRotationLayout.rowHeights = new int[] { 7 };
                        pnRotationLayout.columnWeights = new double[] { 0.1 };
                        pnRotationLayout.columnWidths = new int[] { 7 };
                        pnRotation.setLayout( pnRotationLayout );
                        {
                            spNoOfVertices = new JSpinner( new SpinnerNumberModel( 10, 5, 1000, 1 ) );
                            pnRotation.add( spNoOfVertices, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                                    GridBagConstraints.CENTER,
                                                                                    GridBagConstraints.HORIZONTAL,
                                                                                    new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                        }
                    }
                }
            }
            this.setSize( 457, 339 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * updates geometry with current ({@link #spArc}) arc
     */
    private void applyArc() {
        List<Point> points = drawingPane.getDrawObjectsAsGeoPoints();
        if ( points.size() > 2 ) {
            double d = GeometryUtils.distance( points.get( 1 ).getPosition(), points.get( 0 ).getPosition() );
            Point p = GeometryUtils.vectorByAngle( points.get( 1 ), points.get( 0 ), d, Math.toRadians( getArc() ),
                                                   false );
            points.set( 2, p );
            digitizerModule.getMapModule().update();
        }
    }

    /**
     * updates geometry with current ({@link #spArcRadius}) radius
     */
    private void applyRadius() {
        List<Point> points = drawingPane.getDrawObjectsAsGeoPoints();
        if ( points.size() > 2 ) {
            // calculate new end point for first vector
            double[] lineParam = LineUtils.getLineFromPoints( points.get( 0 ).getX(), points.get( 0 ).getY(),
                                                              points.get( 1 ).getX(), points.get( 1 ).getY() );
            Pair<Position, Position> pair1 = LineUtils.getSymmetricPoints( points.get( 0 ).getX(),
                                                                           points.get( 0 ).getY(), lineParam[0],
                                                                           getRadius() );

            // calculate new end point for second vector
            lineParam = LineUtils.getLineFromPoints( points.get( 0 ).getX(), points.get( 0 ).getY(),
                                                     points.get( 2 ).getX(), points.get( 2 ).getY() );
            Pair<Position, Position> pair2 = LineUtils.getSymmetricPoints( points.get( 0 ).getX(),
                                                                           points.get( 0 ).getY(), lineParam[0],
                                                                           getRadius() );
            // must check to ensure correct direction
            if ( points.get( 1 ).getX() >= points.get( 0 ).getX() ) {
                points.set( 1, GeometryFactory.createPoint( pair1.first, points.get( 0 ).getCoordinateSystem() ) );
            } else {
                points.set( 1, GeometryFactory.createPoint( pair1.second, points.get( 0 ).getCoordinateSystem() ) );
            }
            if ( points.get( 2 ).getX() >= points.get( 0 ).getX() ) {
                points.set( 2, GeometryFactory.createPoint( pair2.first, points.get( 0 ).getCoordinateSystem() ) );
            } else {
                points.set( 2, GeometryFactory.createPoint( pair2.second, points.get( 0 ).getCoordinateSystem() ) );
            }
            digitizerModule.getMapModule().update();
        }
    }

    /**
     * sets arc value
     * 
     * @param arc
     */
    public void setArc( double arc ) {
        ChangeListener cl = spArc.getChangeListeners()[0];
        spArc.removeChangeListener( cl );
        spArc.setValue( arc );
        spArc.addChangeListener( cl );
    }

    /**
     * 
     * @return current arc value
     */
    public double getArc() {
        return ( (Number) spArc.getValue() ).doubleValue();
    }

    /**
     * 
     * @return current arc value
     */
    public void setRadius( double radius ) {
        ChangeListener cl = spArcRadius.getChangeListeners()[0];
        spArcRadius.removeChangeListener( cl );
        spArcRadius.setValue( radius );
        spArcRadius.addChangeListener( cl );
    }

    /**
     * 
     * @return current arc value
     */
    public double getRadius() {
        return ( (Number) spArcRadius.getValue() ).doubleValue();
    }

    /**
     * 
     * @return current arc value
     */
    public int getNoOfVertices() {
        return ( (Number) spNoOfVertices.getValue() ).intValue();
    }

    /**
     * @param drawingPane
     */
    public void setDrawingPane( CreateArcDrawingPane drawingPane ) {
        this.drawingPane = drawingPane;
    }

    public void createArc() {
        List<Point> points = drawingPane.getDrawObjectsAsGeoPoints();
        ApplicationContainer<?> appCont = digitizerModule.getApplicationContainer();
        if ( points.size() >= 3 ) {
            try {
                Curve curve = GeometryUtils.calcCircleCoordinates( points.get( 0 ).getPosition(), getRadius(),
                                                                   getNoOfVertices(), points.get( 1 ).getPosition(),
                                                                   points.get( 2 ).getPosition(),
                                                                   points.get( 1 ).getCoordinateSystem() );
                MapModel mapModel = appCont.getMapModel( null );
                Layer layer = mapModel.getLayersSelectedForAction( MapModel.SELECTION_EDITING ).get( 0 );
                FeatureAdapter featureAdapter = (FeatureAdapter) layer.getDataAccess().get( 0 );
                FeatureType ft = featureAdapter.getSchema();
                Feature feat = featureAdapter.getDefaultFeature( ft.getName() );
                feat = feat.cloneDeep();
                QualifiedName geomProperty = ft.getGeometryProperties()[0].getName();
                feat.getProperties( geomProperty )[0].setValue( curve );
                Command cmd = new InsertFeatureCommand( featureAdapter, feat );
                appCont.getCommandProcessor().executeSychronously( cmd, true );
            } catch ( Exception ex ) {
                LOG.logError( ex );
                DialogFactory.openErrorDialog( appCont.getViewPlatform(), DrawArcDialog.this,
                                               Messages.getMessage( getLocale(), "$MD11628" ),
                                               Messages.getMessage( getLocale(), "$MD11629" ), ex );
            }
        }
    }

}
