//$HeadURL: svn+ssh://aschmitz@wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2011 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.igeo.views.swing.georef;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.NONE;
import static java.util.Collections.singletonList;
import static javax.swing.BorderFactory.createTitledBorder;
import static org.deegree.igeo.i18n.Messages.get;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;

import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.model.AddFileLayerCommand;
import org.deegree.igeo.config.EnvelopeType;
import org.deegree.igeo.config.LayerType.MetadataURL;
import org.deegree.igeo.config.MemoryDatasourceType;
import org.deegree.igeo.desktop.IGeoDesktop;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MemoryDatasource;
import org.deegree.igeo.mapmodel.SystemLayer;
import org.deegree.igeo.modules.DefaultMapModule;
import org.deegree.igeo.modules.georef.AffineTransformation;
import org.deegree.igeo.modules.georef.ControlPointModel;
import org.deegree.igeo.views.swing.map.DefaultMapComponent;
import org.deegree.igeo.views.swing.util.GenericFileChooser;
import org.deegree.igeo.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;
import org.deegree.igeo.views.swing.util.IGeoFileFilter;
import org.deegree.kernel.CommandProcessedEvent;
import org.deegree.kernel.CommandProcessedListener;
import org.deegree.kernel.ProcessMonitor;
import org.deegree.kernel.ProcessMonitorFactory;
import org.deegree.model.Identifier;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.spatialschema.Envelope;

/**
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: stranger $
 * 
 * @version $Revision: $, $Date: $
 */
public class GeoreferencingControlPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 7031021591515735164L;

    private DefaultMapModule<?> rightModule;

    Layer leftLayer, rightLayer;

    MapModel left, right;

    Buttons buttons = new Buttons();

    ControlPointModel points;

    public GeoreferencingControlPanel() {
        setLayout( new GridBagLayout() );
        GridBagConstraints gb = new GridBagConstraints();

        gb.gridx = 0;
        gb.gridy = 0;
        gb.gridwidth = 2;
        gb.anchor = CENTER;
        gb.insets = new Insets( 2, 2, 2, 2 );
        add( buttons.load = new JButton( get( "$DI10074" ) ), gb );
        buttons.load.addActionListener( this );

        gb = (GridBagConstraints) gb.clone();
        ++gb.gridy;
        buttons.transformList = new JComboBox( new String[] { get( "$DI10075" ) } );
        add( buttons.transformList, gb );

        gb = (GridBagConstraints) gb.clone();
        ++gb.gridy;
        add( buttons.activate = new JToggleButton( get( "$DI10076" ) ), gb );

        gb = (GridBagConstraints) gb.clone();
        gb.gridx = 2;
        gb.gridy = 0;
        gb.gridwidth = 4;
        gb.gridheight = 3;
        gb.fill = BOTH;
        JPanel panel = new JPanel();
        panel.setBorder( createTitledBorder( get( "$DI10085" ) ) );
        panel.add( new JLabel( get( "$DI10086" ) ) );
        add( panel, gb );

        gb = (GridBagConstraints) gb.clone();
        gb.gridx = 0;
        gb.gridy = 3;
        gb.gridwidth = 3;
        gb.gridheight = 1;
        gb.fill = NONE;
        add( buttons.loadTable = new JButton( get( "$DI10077" ) ), gb );

        gb = (GridBagConstraints) gb.clone();
        gb.gridx = 3;
        add( buttons.saveTable = new JButton( get( "$DI10078" ) ), gb );

        gb = (GridBagConstraints) gb.clone();
        gb.gridx = 0;
        ++gb.gridy;
        gb.gridwidth = 6;
        gb.fill = BOTH;
        buttons.table = new JTable( points = new ControlPointModel() );
        add( new JScrollPane( buttons.table ), gb );

        gb = (GridBagConstraints) gb.clone();
        ++gb.gridy;
        gb.gridwidth = 2;
        gb.fill = NONE;
        add( buttons.delete = new JButton( get( "$DI10081" ) ), gb );

        gb = (GridBagConstraints) gb.clone();
        gb.gridx += 2;
        add( buttons.reset = new JButton( get( "$DI10082" ) ), gb );

        gb = (GridBagConstraints) gb.clone();
        gb.gridx += 2;
        add( buttons.start = new JButton( get( "$DI10083" ) ), gb );
        buttons.start.addActionListener( this );

        buttons.enable( false );
    }

    public void setMapModel( DefaultMapModule<?> leftModule, MapModel left, DefaultMapModule<?> rightModule,
                             MapModel right ) {
        this.left = left;
        this.rightModule = rightModule;
        this.right = right;

        leftLayer = addPointsLayer( left );
        rightLayer = addPointsLayer( right );

        DefaultMapComponent mc = (DefaultMapComponent) rightModule.getMapContainer();
        mc.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked( MouseEvent e ) {
                points.clickedRight( e.getX(), e.getY() );
            }
        } );
        mc = (DefaultMapComponent) leftModule.getMapContainer();
        mc.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked( MouseEvent e ) {
                points.clickedLeft( e.getX(), e.getY() );
            }
        } );

        points.updateMaps( left, leftLayer, right, rightLayer );
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        if ( e.getSource() == buttons.load ) {
            loadRaster();
        }
        if ( e.getSource() == buttons.start ) {
            AffineTransformation.approximate( points.getPoints() );
            points.fireTableDataChanged();
        }
    }

    private void loadRaster() {
        ApplicationContainer<?> appContainer = rightModule.getApplicationContainer();
        File file = null;
        if ( "Application".equalsIgnoreCase( appContainer.getViewPlatform() ) ) {
            Preferences prefs = Preferences.userNodeForPackage( GeoreferencingControlPanel.class );
            List<IGeoFileFilter> ff = new ArrayList<IGeoFileFilter>();

            ff.add( IGeoFileFilter.TIFF );
            ff.add( IGeoFileFilter.PNG );

            file = GenericFileChooser.showOpenDialog( FILECHOOSERTYPE.geoDataFile, appContainer,
                                                      ( (IGeoDesktop) appContainer ).getMainWndow(), prefs,
                                                      "geoDataFile", ff );

        }
        if ( file != null ) {

            String crsName = right.getCoordinateSystem().getPrefixedName();
            AddFileLayerCommand command = new AddFileLayerCommand( right, file, null, null, null, crsName );

            final ProcessMonitor pm = ProcessMonitorFactory.createDialogProcessMonitor( appContainer.getViewPlatform(),
                                                                                        Messages.get( "$MD11264" ),
                                                                                        Messages.get( "$MD11265", file ),
                                                                                        0, -1, command );
            command.setProcessMonitor( pm );
            command.addListener( new CommandProcessedListener() {
                @Override
                public void commandProcessed( CommandProcessedEvent event ) {
                    buttons.enable( true );
                    try {
                        pm.cancel();
                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }

            } );
            appContainer.getCommandProcessor().executeASychronously( command );
        }
    }

    private static Layer addPointsLayer( MapModel mm ) {
        Envelope env = mm.getEnvelope();
        MemoryDatasourceType mdst = new MemoryDatasourceType();
        EnvelopeType et = new EnvelopeType();
        et.setMinx( env.getMin().getX() );
        et.setMiny( env.getMin().getY() );
        et.setMaxx( env.getMax().getX() );
        et.setMaxy( env.getMax().getY() );
        et.setCrs( mm.getEnvelope().getCoordinateSystem().getPrefixedName() );
        mdst.setExtent( et );
        mdst.setMinScaleDenominator( 0d );
        mdst.setMaxScaleDenominator( 100000000d );
        Datasource ds = new MemoryDatasource( mdst, null, null, FeatureFactory.createFeatureCollection( null, 0 ) );

        Identifier id = new Identifier( "georef" );

        SystemLayer newLayer = new SystemLayer( mm, id, id.getValue(), "", singletonList( ds ),
                                                Collections.<MetadataURL> emptyList() );
        newLayer.setEditable( false );
        newLayer.setVisibleInLayerTree( false );
        newLayer.setVisible( true );

        mm.insert( newLayer, mm.getLayerGroups().get( 0 ), null, true );
        return newLayer;
    }

    static class Buttons {
        JButton load, loadTable, saveTable, delete, reset, start;

        JToggleButton activate;

        JComboBox transformList;

        JTable table;

        void enable( boolean b ) {
            loadTable.setEnabled( b );
            saveTable.setEnabled( b );
            delete.setEnabled( b );
            reset.setEnabled( b );
            start.setEnabled( b );
            activate.setEnabled( b );
            transformList.setEnabled( b );
            table.setEnabled( b );
        }
    }

}
