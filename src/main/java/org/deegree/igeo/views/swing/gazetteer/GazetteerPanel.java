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
package org.deegree.igeo.views.swing.gazetteer;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.deegree.crs.components.Unit;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.model.ZoomCommand;
import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.modules.gazetteer.GazetteerItem;
import org.deegree.igeo.modules.gazetteer.GazetteerModule;
import org.deegree.igeo.modules.gazetteer.Hierarchy;
import org.deegree.igeo.modules.gazetteer.HierarchyNode;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.HelpManager;
import org.deegree.igeo.views.swing.DefaultPanel;
import org.deegree.igeo.views.swing.HelpFrame;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryFactory;
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
public class GazetteerPanel extends DefaultPanel {

    private static final long serialVersionUID = 25672757754571918L;

    private static final ILogger LOG = LoggerFactory.getLogger( GazetteerPanel.class );

    private JPanel pnZoomto;

    private JButton btZoomto;

    private JPanel pnSelect;

    private JComboBox cbType;

    private JLabel lbType;

    private JPanel pnType;

    private JPanel pnGazetteer;

    private JButton btHelp;

    private JPanel pnHelp;

    private GazetteerModule<?> owner;

    private ApplicationContainer<Container> appCont;

    private Hierarchy currentHierarchy;

    private Map<String, SelectPanel> selectPanels = new HashMap<String, SelectPanel>();

    private GazetteerItem selectedItem;

    /**
     * 
     */
    public GazetteerPanel() {
        initGUI();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#init(org.deegree.igeo.config.ViewFormType)
     */
    public void init( ViewFormType viewForm )
                            throws Exception {

    }

    @SuppressWarnings("unchecked")
    @Override
    public void registerModule( IModule<Container> module ) {
        super.registerModule( module );
        this.owner = (GazetteerModule<?>) module;
        // just after registering owner module list of available gazetteers/hierarchies
        // can be filled
        appCont = (ApplicationContainer<Container>) owner.getApplicationContainer();
        List<Hierarchy> hierarchies = owner.getHierarchyList();
        String[] hNames = new String[hierarchies.size()];
        for ( int i = 0; i < hNames.length; i++ ) {
            hNames[i] = hierarchies.get( i ).getName();
        }
        ComboBoxModel model = new DefaultComboBoxModel( hNames );
        cbType.setModel( model );
        selectHierarchy();
        // will be disabled because no gazetteer item has been selected at this point
        // so a user can not perform a zoom
        btZoomto.setEnabled( false );
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            this.setPreferredSize( new Dimension( 300, 400 ) );
            thisLayout.rowWeights = new double[] { 0.0, 0.1 };
            thisLayout.rowHeights = new int[] { 300, 7 };
            thisLayout.columnWeights = new double[] { 0.0, 0.1 };
            thisLayout.columnWidths = new int[] { 125, 7 };
            this.setLayout( thisLayout );
            {
                pnZoomto = new JPanel();
                FlowLayout pnZoomtoLayout = new FlowLayout();
                pnZoomtoLayout.setAlignment( FlowLayout.LEFT );
                pnZoomto.setLayout( pnZoomtoLayout );
                this.add( pnZoomto, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                            GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                {
                    btZoomto = new JButton( Messages.getMessage( getLocale(), "$MD11293" ) );
                    pnZoomto.add( btZoomto );
                    btZoomto.setToolTipText( Messages.getMessage( getLocale(), "$MD11294" ) );
                    btZoomto.setEnabled( false );
                    btZoomto.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            zoomToSelectedItem();
                        }
                    } );

                }
            }
            {
                pnHelp = new JPanel();
                FlowLayout pnHelpLayout = new FlowLayout();
                pnHelpLayout.setAlignment( FlowLayout.RIGHT );
                pnHelp.setLayout( pnHelpLayout );
                this.add( pnHelp, new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                          GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                {
                    btHelp = new JButton( Messages.getMessage( getLocale(), "$MD11295" ),
                                          IconRegistry.getIcon( "help.png" ) );
                    btHelp.setToolTipText( Messages.getMessage( getLocale(), "$MD11296" ) );
                    pnHelp.add( btHelp );
                    btHelp.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            HelpFrame hf = HelpFrame.getInstance( new HelpManager( owner.getApplicationContainer() ) );
                            hf.setVisible( true );
                            hf.gotoModule( "Gazetteer" );
                        }
                    } );
                }
            }
            {
                pnGazetteer = new JPanel();
                BorderLayout pnGazetteerLayout = new BorderLayout();
                pnGazetteer.setLayout( pnGazetteerLayout );
                this.add( pnGazetteer, new GridBagConstraints( 0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                               GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnGazetteer.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11297" ) ) );
                {
                    pnType = new JPanel();
                    GridBagLayout pnTypeLayout = new GridBagLayout();
                    pnGazetteer.add( pnType, BorderLayout.NORTH );
                    pnTypeLayout.rowWeights = new double[] { 0.1 };
                    pnTypeLayout.rowHeights = new int[] { 7 };
                    pnTypeLayout.columnWeights = new double[] { 0.0, 0.1 };
                    pnTypeLayout.columnWidths = new int[] { 120, 20 };
                    pnType.setLayout( pnTypeLayout );
                    pnType.setPreferredSize( new Dimension( 312, 37 ) );
                    {
                        lbType = new JLabel();
                        pnType.add( lbType, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                    GridBagConstraints.HORIZONTAL, new Insets( 0, 10,
                                                                                                               0, 0 ),
                                                                    0, 0 ) );
                        lbType.setText( Messages.getMessage( getLocale(), "$MD11298" ) );
                    }
                    {
                        cbType = new JComboBox();
                        pnType.add( cbType, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                    GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0,
                                                                                                               10 ), 0,
                                                                    0 ) );

                        cbType.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent event ) {
                                selectHierarchy();
                            }

                        } );
                    }
                }
                {
                    pnSelect = new JPanel();
                    pnGazetteer.add( pnSelect, BorderLayout.CENTER );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     */
    protected void zoomToSelectedItem() {
        if ( selectedItem != null ) {
            MapModel mapModel = appCont.getMapModel( null );
            Geometry geom = selectedItem.getGeographicExtent();
            // ensure that gazetteer geogr. extent uses same CRS than current map model
            GeoTransformer gt = new GeoTransformer( mapModel.getCoordinateSystem() );
            try {
                geom = gt.transform( geom );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
            }
            Envelope env = geom.getEnvelope();
            if ( geom instanceof Point ) {
                Point p = (Point)geom;
                env = GeometryFactory.createEnvelope( p.getPosition(), p.getPosition(), p.getCoordinateSystem() );
                CoordinateSystem crs = geom.getCoordinateSystem();
                if ( crs.getAxisUnits()[0].equals( Unit.METRE ) ) {
                    // for point items create a buffer of 20m if UoM is metre
                    // so the map will have a bounding box of ~40x40m (aspect ratio of map
                    // pixel size may change this)
                    env = env.getBuffer( 20 );
                } else {
                    // for point items with other UoM than metre map will be zoomed to
                    // 10% of its current size
                    env = mapModel.getEnvelope().getBuffer( 0.1 );
                }
            }
            ZoomCommand cmd = new ZoomCommand( mapModel );
            cmd.setZoomBox( env, -1, -1 );
            try {
                appCont.getCommandProcessor().executeSychronously( cmd, true );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                DialogFactory.openErrorDialog( appCont.getViewPlatform(), getParent(),
                                               Messages.getMessage( getLocale(), "$MD11299" ),
                                               Messages.getMessage( getLocale(), "$MD11300",
                                                                    selectedItem.getGeographicIdentifier() ), e );
            }
        }
    }

    /**
     * fills the gazetteer panel with select elements depending on selected feature type tree/hierarchy
     */
    protected void selectHierarchy() {
        pnSelect.removeAll();
        selectPanels.clear();
        List<Hierarchy> hierarchies = owner.getHierarchyList();
        String hName = (String) cbType.getSelectedItem();
        currentHierarchy = null;
        for ( Hierarchy tmp : hierarchies ) {
            if ( tmp.getName().equals( hName ) ) {
                currentHierarchy = tmp;
                break;
            }
        }
        HierarchyNode node = currentHierarchy.getRoot();
        pnSelect.setVisible( false );
        int k = 0;
        while ( node != null ) {
            if ( node.supportFreeSearch() ) {
                pnSelect.add( new SearchPanel( appCont, currentHierarchy.getGazetteerAddress(), node, this ) );
            } else {
                SelectPanel sp = new SelectPanel( appCont, currentHierarchy.getGazetteerAddress(), node, this );
                if ( k == 0 ) {
                    sp.load();
                }
                selectPanels.put( node.getName(), sp );
                pnSelect.add( sp );
            }
            node = node.getChildNode();
            k++;
        }
        pnSelect.setVisible( true );
    }

    /**
     * @param gazetteerItem
     * @param node
     */
    public void findChildren( GazetteerItem gazetteerItem, HierarchyNode node ) {
        this.selectedItem = gazetteerItem;
        // now can be enabled because an item is selected
        btZoomto.setEnabled( true );
        HierarchyNode child = node.getChildNode();
        if ( child != null ) {
            SelectPanel sp = selectPanels.get( child.getName() );
            sp.load( gazetteerItem );
        }
    }

}
