//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2010 by:
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
package org.deegree.desktop.views.swing.digitize.construction;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.commands.digitize.JoinCurvesCommand;
import org.deegree.desktop.commands.digitize.JoinCurvesCommand.CURVE_CONNECTION_TYPE;
import org.deegree.desktop.dataadapter.FeatureAdapter;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.modules.DigitizerModule;
import org.deegree.desktop.state.mapstate.ToolState;
import org.deegree.desktop.state.mapstate.EditState.CreateArcFeatureState;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.desktop.views.swing.digitize.construction.JoinCurveParameter.CONNECTION_PARAMETER;
import org.deegree.framework.util.Pair;
import org.deegree.framework.util.StringTools;
import org.deegree.kernel.Command;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.Point;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class JoinCurveDialog extends JDialog {

    private static final long serialVersionUID = 3375935110460635572L;

    private JPanel pnButtons;

    private JButton btCancle;

    private JPanel pnHelp;

    private JButton btHelp;

    private JPanel pnDescription;

    private JScrollPane scDescription;

    private JPanel pnParameter;

    private JComboBox cbConnectionType;

    private JEditorPane epDescription;

    private JPanel pnConnectionType;

    private JoinCurveParameter panel = null;

    private ApplicationContainer<Container> appCont;

    private DigitizerModule<Container> digitizerModule;

    private static JoinCurveDialog joinCurveDialog;

    /**
     * 
     * @param digitizerModule
     */
    private JoinCurveDialog( DigitizerModule<Container> digitizerModule ) {
        this.appCont = digitizerModule.getApplicationContainer();
        this.digitizerModule = digitizerModule;
        addWindowListener( new WindowAdapter() {

            public void windowOpened( WindowEvent e ) {
                ToolState ts = JoinCurveDialog.this.digitizerModule.getMapModule().getMapTool().getState();
                if ( !ts.getClass().equals( CreateArcFeatureState.class ) && cbConnectionType.getSelectedIndex() > 0 ) {
                    // if the dialog is activated and the current state of the active MapModule is not
                    // equal to CreateArcFeatureState the DigitizerModule must be informed and the state
                    // must be changed to CreateArcFeatureState
                    changeDescription( (CurveConnectionItem) cbConnectionType.getSelectedItem() );
                    changeParameterPanel( (CurveConnectionItem) cbConnectionType.getSelectedItem() );
                    setDigitizerMode( (CurveConnectionItem) cbConnectionType.getSelectedItem() );
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
    public static JoinCurveDialog create( DigitizerModule<?> digitizerModule ) {
        if ( joinCurveDialog == null ) {
            joinCurveDialog = new JoinCurveDialog( (DigitizerModule<Container>) digitizerModule );
        }
        joinCurveDialog.setAlwaysOnTop( true );
        joinCurveDialog.setVisible( true );
        return joinCurveDialog;
    }

    @Override
    public void dispose() {
        digitizerModule.setDigitizingAction( null );
        digitizerModule.resetDigitizerPane();
        digitizerModule.resetFunctionSelect();
        digitizerModule.getMapModule().update();
        super.dispose();
    }

    private void initGUI() {
        try {
            {
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.1 };
                thisLayout.rowHeights = new int[] { 68, 242, 7 };
                thisLayout.columnWeights = new double[] { 0.0, 0.1, 0.1 };
                thisLayout.columnWidths = new int[] { 223, 7, 20 };
                getContentPane().setLayout( thisLayout );
                {
                    pnButtons = new JPanel();
                    FlowLayout pnButtonsLayout = new FlowLayout();
                    pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                    pnButtons.setLayout( pnButtonsLayout );
                    getContentPane().add(
                                          pnButtons,
                                          new GridBagConstraints( 0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    {
                        btCancle = new JButton( Messages.getMessage( getLocale(), "$MD11467" ) );
                        pnButtons.add( btCancle );
                        btCancle.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                digitizerModule.clear();
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
                                          new GridBagConstraints( 2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    {
                        btHelp = new JButton( Messages.getMessage( getLocale(), "$MD11468" ) );
                        pnHelp.add( btHelp );
                    }
                }
                {
                    pnDescription = new JPanel();
                    BorderLayout pnDescriptionLayout = new BorderLayout();
                    pnDescription.setLayout( pnDescriptionLayout );
                    getContentPane().add(
                                          pnDescription,
                                          new GridBagConstraints( 0, 0, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnDescription.setBorder( BorderFactory.createTitledBorder( null, Messages.getMessage( getLocale(),
                                                                                                          "$MD11469" ),
                                                                               TitledBorder.LEADING,
                                                                               TitledBorder.DEFAULT_POSITION ) );
                    {
                        scDescription = new JScrollPane();
                        pnDescription.add( scDescription, BorderLayout.CENTER );
                        {
                            epDescription = new JEditorPane();
                            epDescription.setBackground( pnDescription.getBackground() );
                            scDescription.setViewportView( epDescription );
                            epDescription.setContentType( "text/html" );
                            epDescription.setEditable( false );
                        }
                    }
                }
                {
                    pnConnectionType = new JPanel();
                    GridBagLayout pnConnectionTypeLayout = new GridBagLayout();
                    getContentPane().add(
                                          pnConnectionType,
                                          new GridBagConstraints( 1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnConnectionType.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                       "$MD11470" ) ) );
                    pnConnectionTypeLayout.rowWeights = new double[] { 0.1 };
                    pnConnectionTypeLayout.rowHeights = new int[] { 7 };
                    pnConnectionTypeLayout.columnWeights = new double[] { 0.1 };
                    pnConnectionTypeLayout.columnWidths = new int[] { 7 };
                    pnConnectionType.setLayout( pnConnectionTypeLayout );
                    {
                        CurveConnectionItem[] types = getLineConnectionTypes();
                        cbConnectionType = new JComboBox( new DefaultComboBoxModel( types ) );
                        pnConnectionType.add( cbConnectionType, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                                        GridBagConstraints.CENTER,
                                                                                        GridBagConstraints.HORIZONTAL,
                                                                                        new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                        cbConnectionType.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                changeDescription( (CurveConnectionItem) cbConnectionType.getSelectedItem() );
                                changeParameterPanel( (CurveConnectionItem) cbConnectionType.getSelectedItem() );
                                setDigitizerMode( (CurveConnectionItem) cbConnectionType.getSelectedItem() );
                            }
                        } );
                    }
                }
                {
                    pnParameter = new JPanel();
                    BorderLayout pnParameterLayout = new BorderLayout();
                    pnParameter.setLayout( pnParameterLayout );
                    getContentPane().add(
                                          pnParameter,
                                          new GridBagConstraints( 1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnParameter.setBorder( BorderFactory.createTitledBorder( null, Messages.getMessage( getLocale(),
                                                                                                        "$MD11471" ),
                                                                             TitledBorder.LEADING,
                                                                             TitledBorder.DEFAULT_POSITION ) );
                }
            }
            this.setSize( 589, 373 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * @param selectedItem
     */
    protected void setDigitizerMode( CurveConnectionItem selectedItem ) {
        digitizerModule.setDigitizingAction( "joinCurves" );
        // register call back method
        digitizerModule.setOnActionFinished( new Pair<Object, String>( this, "joinCurves" ) );
    }

    /**
     * 
     */
    public void joinCurves()
                            throws Exception {
        if ( panel == null ) {
            DialogFactory.openWarningDialog( appCont.getViewPlatform(), this, Messages.getMessage( getLocale(),
                                                                                                   "$MD11505" ),
                                             Messages.getMessage( getLocale(), "$MD11506" ) );
            return;
        } else {
            Map<CONNECTION_PARAMETER, Object> parameter = panel.getParameter();
            CURVE_CONNECTION_TYPE conType = ( (CurveConnectionItem) cbConnectionType.getSelectedItem() ).type;
            boolean connectionAsNewCurve = (Boolean) parameter.get( CONNECTION_PARAMETER.connctionAsNewCurve );
            Object tmp = parameter.get( CONNECTION_PARAMETER.noOfSegments );
            int noOfSegments = 0;
            if ( tmp != null ) {
                noOfSegments = (Integer) tmp;
            }

            MapModel mapModel = appCont.getMapModel( null );
            Layer layer = mapModel.getLayersSelectedForAction( MapModel.SELECTION_EDITING ).get( 0 );
            FeatureAdapter featureAdapter = (FeatureAdapter) layer.getDataAccess().get( 0 );
            List<Geometry> geometries = digitizerModule.getGeometries();
            QualifiedName geomProperty = null;
            Point point1 = (Point) geometries.get( 0 );
            Point point2 = (Point) geometries.get( geometries.size() - 1 );
            FeatureCollection fc = featureAdapter.getFeatureCollection( point1 );
            if ( fc.size() == 0 ) {
                throw new Exception( Messages.getMessage( getLocale(), "$MD11507" ) );
            }
            Feature feature1 = fc.getFeature( 0 );
            fc = featureAdapter.getFeatureCollection( point2 );
            if ( fc.size() == 0 ) {
                throw new Exception( Messages.getMessage( getLocale(), "$MD11508" ) );
            }
            Feature feature2 = fc.getFeature( 0 );
            if ( feature1 == null ) {
                throw new Exception( Messages.getMessage( getLocale(), "$MD11507" ) );
            }
            if ( feature2 == null ) {
                throw new Exception( Messages.getMessage( getLocale(), "$MD11508" ) );
            }
            Command cmd = new JoinCurvesCommand( featureAdapter, geomProperty, conType, connectionAsNewCurve, feature1,
                                                 point1, feature2, point2, noOfSegments );
            appCont.getCommandProcessor().executeSychronously( cmd, true );
        }

    }

    /**
     * @param selectedItem
     */
    protected void changeParameterPanel( CurveConnectionItem selectedItem ) {

        Component co = pnParameter.getComponentAt( pnParameter.getWidth() / 2, pnParameter.getHeight() / 2 );
        switch ( selectedItem.type ) {
        case direct:
            if ( !( co instanceof JoinAsNewCurvePanel ) ) {
                panel = new JoinAsNewCurvePanel();
            }
            break;
        case tangent:
            if ( !( co instanceof TangentJoinPanel ) ) {
                panel = new TangentJoinPanel();
            }
            break;
        case stretchLines:
            if ( !( co instanceof JoinAsNewCurvePanel ) ) {
                panel = new JoinAsNewCurvePanel();
            }
            break;

        }
        ( (JPanel) panel ).setVisible( true );
        pnParameter.setVisible( false );
        pnParameter.removeAll();
        pnParameter.add( ( (JPanel) panel ), BorderLayout.CENTER );
        pnParameter.setVisible( true );
    }

    /**
     * @param selectedItem
     */
    protected void changeDescription( CurveConnectionItem selectedItem ) {
        String s = null;
        try {
            s = JoinCurveDialog.class.getResource( selectedItem.type.name() + ".jpg" ).toURI().toASCIIString();
        } catch ( URISyntaxException e ) {
            e.printStackTrace();
        }
        String text = "unknown";
        switch ( selectedItem.type ) {
        case direct:
            text = Messages.getMessage( getLocale(), "$MD11472", s );
            break;
        case tangent:
            text = Messages.getMessage( getLocale(), "$MD11473", s );
            break;
        case stretchLines:
            text = Messages.getMessage( getLocale(), "$MD11474", s );
            break;

        }
        epDescription.setText( text );
    }

    /**
     * @return
     */
    private CurveConnectionItem[] getLineConnectionTypes() {
        String tmp = Messages.getMessage( getLocale(), "$MD11482" );
        String[] s = StringTools.toArray( tmp, ",;", true );
        CurveConnectionItem[] types = new CurveConnectionItem[4];
        types[0] = new CurveConnectionItem( null, s[0] );
        types[1] = new CurveConnectionItem( CURVE_CONNECTION_TYPE.direct, s[1] );
        types[2] = new CurveConnectionItem( CURVE_CONNECTION_TYPE.tangent, s[2] );
        types[3] = new CurveConnectionItem( CURVE_CONNECTION_TYPE.stretchLines, s[3] );
        return types;
    }

    // /////////////////////////////////////////////////////////////////////////////////////
    // inner classes
    // /////////////////////////////////////////////////////////////////////////////////////

    /**
     *  
     */
    private class CurveConnectionItem {
        public CURVE_CONNECTION_TYPE type;

        public String title;

        /**
         * @param type
         * @param title
         */
        public CurveConnectionItem( CURVE_CONNECTION_TYPE type, String title ) {
            super();
            this.type = type;
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }

    }

}
