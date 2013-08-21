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

package org.deegree.desktop.views.swing.layerlist;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.dataadapter.DataAccessAdapter;
import org.deegree.desktop.dataadapter.FileFeatureAdapter;
import org.deegree.desktop.dataadapter.FileGridCoverageAdapter;
import org.deegree.desktop.dataadapter.MemoryFeatureAdapter;
import org.deegree.desktop.dataadapter.DataAccessAdapter.DATASOURCETYPE;
import org.deegree.desktop.dataadapter.wcs.WCSGridCoverageAdapter;
import org.deegree.desktop.dataadapter.wfs.WFSFeatureAdapter;
import org.deegree.desktop.dataadapter.wms.WMSGridCoverageAdapter;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.MapModelEntry;
import org.deegree.desktop.views.swing.util.IconRegistry;
import org.deegree.framework.util.FileUtils;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XSLTDocument;
import org.deegree.desktop.config.LayerType;
import org.deegree.desktop.config.OnlineResourceType;
import org.deegree.desktop.config.LayerType.MetadataURL;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class LayerInfoFrame extends JFrame {

    private static final long serialVersionUID = 7556921616653183981L;

    private InfoPanel infoPanel;

    private JPanel metadataPanel;

    private JPanel metadataPanelLeft;

    private ExtResourcesPanel extResPanel;

    private JPanel datasourcesPanel;

    private JPanel metadataViewPanel;

    private LayerStatisticPanel layerstatisticsPanel;

    private ApplicationContainer<Container> appContainer;

    private static LayerInfoFrame layerInfoFrame;

    /**
     * 
     * @param appContainer
     */
    private LayerInfoFrame( ApplicationContainer<Container> appContainer ) {
        this.appContainer = appContainer;
        setBounds( 100, 100, 650, 430 );
        setResizable( true );
        getContentPane().setLayout( new BorderLayout() );
        createGUIElements();
        addWindowListener( new WindowAdapter() {

            public void windowDeactivated( WindowEvent e ) {
                //LayerInfoFrame.this.toFront();
            }

            public void windowGainedFocus( WindowEvent e ) {
              //  LayerInfoFrame.this.toFront();
            }

        } );
    }

    /**
     * @param appContainer
     * @return singleton {@link LayerInfoFrame}
     */
    public static LayerInfoFrame getInstance( ApplicationContainer<Container> appContainer ) {
        if ( layerInfoFrame == null ) {
            layerInfoFrame = new LayerInfoFrame( appContainer );
        }
        return layerInfoFrame;
    }

    /**
     * 
     * 
     */
    private void createGUIElements() {
        JTabbedPane tabbedPane = new JTabbedPane();

        infoPanel = new InfoPanel();
        tabbedPane.addTab( Messages.getMessage( Locale.getDefault(), "$MD10058" ), infoPanel );

        metadataPanel = new JPanel();
        tabbedPane.addTab( Messages.getMessage( Locale.getDefault(), "$MD10059" ), metadataPanel );

        extResPanel = new ExtResourcesPanel( appContainer, this );
        tabbedPane.addTab( Messages.getMessage( Locale.getDefault(), "$MD10060" ), extResPanel );

        datasourcesPanel = new JPanel();
        tabbedPane.addTab( Messages.getMessage( Locale.getDefault(), "$MD10061" ), datasourcesPanel );

        layerstatisticsPanel = new LayerStatisticPanel( appContainer );
        tabbedPane.addTab( "statistics", layerstatisticsPanel );

        getContentPane().add( tabbedPane, BorderLayout.CENTER );
    }

    /**
     * 
     * @param mme
     */
    private void initGUIElements( MapModelEntry mme ) {
        infoPanel.setMapModelEntry( mme );
        if ( mme instanceof Layer ) {
            initMetadataPanel( (Layer) mme );
            initExternalResourcesPanel( (Layer) mme );
            initDatasourcesPanel( (Layer) mme );
            initLayerStatisticsPanel( (Layer) mme );
        }
        doLayout();
    }

    private void initLayerStatisticsPanel( Layer layer ) {
        layerstatisticsPanel.removeAll();
        layerstatisticsPanel.setLayout( new BorderLayout( 10, 0 ) );
        layerstatisticsPanel.init( layer );
    }

    /**
     * 
     * @param layer
     */
    private void initMetadataPanel( final Layer layer ) {
        metadataPanel.removeAll();
        metadataPanel.setLayout( new BorderLayout( 10, 0 ) );

        if ( layer != null ) {
            metadataPanelLeft = new JPanel();
            metadataPanelLeft.setBorder( BorderFactory.createEmptyBorder( 10, 10, 5, 10 ) );
            metadataPanelLeft.setLayout( new BoxLayout( metadataPanelLeft, BoxLayout.PAGE_AXIS ) );

            JLabel title = new JLabel( layer.getTitle() );
            title.setBorder( BorderFactory.createEmptyBorder( 10, 10, 5, 10 ) );
            metadataPanel.add( title, BorderLayout.NORTH );
            final List<MetadataURL> urlList = layer.getMetadataURLs();
            for ( MetadataURL url : urlList ) {
                JButton open = new JButton( Messages.getMessage( Locale.getDefault(), "$MD10065" ) );
                open.setIcon( IconRegistry.getIcon( "open.gif" ) );
                open.setActionCommand( url.getOnlineResource().getHref() );
                open.addActionListener( new MetadataOpenListener() );
                metadataPanelLeft.add( open );
            }
            metadataPanel.add( metadataPanelLeft, BorderLayout.WEST );

            JScrollPane sc = new JScrollPane( metadataViewPanel = new JPanel(),
                                              JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                              JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
            sc.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
            metadataViewPanel.setLayout( new BorderLayout() );
            metadataPanel.add( sc, BorderLayout.CENTER );
            JButton addMDBT = new JButton( Messages.getMessage( Locale.getDefault(), "$MD10109" ) );
            addMDBT.setIcon( IconRegistry.getIcon( "add.png" ) );
            addMDBT.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent evt ) {
                    MetadataURLDialog dg = new MetadataURLDialog( LayerInfoFrame.this );
                    String s = dg.getMetadataURL();
                    if ( s != null && s.length() > 5 ) {
                        JButton open = new JButton( Messages.getMessage( Locale.getDefault(), "$MD10065" ) );
                        open.setIcon( IconRegistry.getIcon( "open.gif" ) );
                        open.setActionCommand( s );
                        open.addActionListener( new MetadataOpenListener() );
                        metadataPanelLeft.add( open );
                        MetadataURL mdu = new LayerType.MetadataURL();
                        OnlineResourceType olr = new OnlineResourceType();
                        olr.setHref( s );
                        mdu.setOnlineResource( olr );
                        layer.addMetadataURL( mdu );
                        invalidate();
                        repaint();
                    }
                }
            } );
            JButton removeMDBT = new JButton( Messages.getMessage( Locale.getDefault(), "$MD10110" ) );
            removeMDBT.setIcon( IconRegistry.getIcon( "remove.png" ) );
            removeMDBT.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent evt ) {
                    // removes last button/meta data link from panel
                    Component[] comps = metadataPanelLeft.getComponents();
                    for ( int i = comps.length; i > 0; i-- ) {
                        if ( comps[i - 1] instanceof JButton ) {
                            JButton bt = (JButton) comps[i - 1];
                            metadataPanelLeft.remove( bt );
                            for ( int j = 0; j < urlList.size(); j++ ) {
                                if ( urlList.get( j ).getOnlineResource().getHref().equals( bt.getActionCommand() ) ) {
                                    layer.removeMetadataURL( urlList.get( j ) );
                                }
                            }
                            invalidate();
                            repaint();
                            break;
                        }
                    }
                }
            } );
            JPanel panel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
            panel.add( addMDBT );
            panel.add( removeMDBT );
            metadataPanel.add( panel, BorderLayout.SOUTH );
        }

    }

    private void initExternalResourcesPanel( Layer layer ) {
        extResPanel.removeAll();
        extResPanel.setLayout( new BorderLayout( 10, 0 ) );
        extResPanel.init( layer );
    }

    /**
     * creates panel for datasource informations
     * 
     * @param layer
     */
    private void initDatasourcesPanel( Layer layer ) {
        datasourcesPanel.removeAll();
        datasourcesPanel.setLayout( new BorderLayout( 10, 0 ) );

        if ( layer != null ) {
            JPanel left = new JPanel();
            left.setBorder( BorderFactory.createEmptyBorder( 10, 10, 5, 10 ) );
            left.setLayout( new BoxLayout( left, BoxLayout.PAGE_AXIS ) );

            JLabel title = new JLabel( layer.getTitle() );
            title.setBorder( BorderFactory.createEmptyBorder( 10, 10, 5, 10 ) );
            datasourcesPanel.add( title, BorderLayout.NORTH );
            List<DataAccessAdapter> list = layer.getDataAccess();
            for ( int i = 0; i < list.size(); i++ ) {
                DataAccessAdapter adapter = list.get( i );
                if ( i == 0 ) {
                    setDatasourcePanel( adapter );
                }
                DATASOURCETYPE dst = getDataSourceTypes( adapter );
                JButton open = new JButton( adapter.getDatasource().getName(), getDSTypeIcon( dst ) );
                open.putClientProperty( "DATASOURCE", adapter );
                open.setActionCommand( dst.name() );
                open.addActionListener( new DatasourceOpenListener() );
                left.add( open );
            }
            datasourcesPanel.add( left, BorderLayout.WEST );
        }
    }

    private void setDatasourcePanel( DataAccessAdapter ada ) {
        JPanel datasourceViewPanel = new JPanel();
        datasourceViewPanel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
        datasourceViewPanel.setLayout( new BorderLayout() );

        datasourceViewPanel.add( new DatasourceDescPanel( appContainer, ada ), BorderLayout.CENTER );
        datasourceViewPanel.doLayout();

        datasourcesPanel.add( datasourceViewPanel, BorderLayout.CENTER );

        datasourcesPanel.doLayout();
        LayerInfoFrame.this.repaint();
    }

    private Icon getDSTypeIcon( DATASOURCETYPE dst ) {
        if ( dst.equals( DATASOURCETYPE.file ) ) {
            return IconRegistry.getIcon( "raster_filelayer_icon.png" );
        }
        if ( dst.equals( DATASOURCETYPE.database ) ) {
            return IconRegistry.getIcon( "databaselayer_icon.gif" );
        }
        if ( dst.equals( DATASOURCETYPE.memory ) ) {
            return IconRegistry.getIcon( "memorylayer_icon.gif" );
        }
        if ( dst.equals( DATASOURCETYPE.mixed ) ) {
            return IconRegistry.getIcon( "mixedlayer_icon.gif" );
        }
        if ( dst.equals( DATASOURCETYPE.wcs ) ) {
            return IconRegistry.getIcon( "wcslayer_icon.png" );
        }
        if ( dst.equals( DATASOURCETYPE.wfs ) ) {
            return IconRegistry.getIcon( "wfslayer_icon.png" );
        }
        if ( dst.equals( DATASOURCETYPE.wms ) ) {
            return IconRegistry.getIcon( "wmslayer_icon.png" );
        }
        return null;
    }

    /**
     * 
     * @param dataaccess
     * @return
     */
    private DATASOURCETYPE getDataSourceTypes( DataAccessAdapter dataaccess ) {

        DATASOURCETYPE dst = null;
        if ( dataaccess instanceof FileFeatureAdapter || dataaccess instanceof FileGridCoverageAdapter ) {
            dst = DATASOURCETYPE.file;
        } else if ( dataaccess instanceof MemoryFeatureAdapter ) {
            dst = DATASOURCETYPE.memory;
        } else if ( dataaccess instanceof WFSFeatureAdapter ) {
            dst = DATASOURCETYPE.wfs;
        } else if ( dataaccess instanceof WCSGridCoverageAdapter ) {
            dst = DATASOURCETYPE.wcs;
        } else if ( dataaccess instanceof WMSGridCoverageAdapter ) {
            dst = DATASOURCETYPE.wms;
        } else {
            dst = DATASOURCETYPE.database;
        }

        return dst;
    }

    /**
     * 
     * @param mme
     */
    public void setLayer( MapModelEntry mme ) {
        setTitle( mme.getTitle() );
        initGUIElements( mme );
        repaint();
    }

    // /////////////////////////////////////////////////////////////////////////////
    // inner classes
    // /////////////////////////////////////////////////////////////////////////////

    private class MetadataOpenListener implements ActionListener {

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed( ActionEvent event ) {
            String url = event.getActionCommand();
            // JTextArea ta = new JTextArea( url );
            JTextPane textpane = new JTextPane();
            try {
                String s = FileUtils.readTextFile( new URL( url ) ).toString();
                StringReader sr = new StringReader( s );
                try {
                    XMLFragment xml = new XMLFragment();
                    xml.load( sr, url );
                    XSLTDocument xslt = new XSLTDocument( LayerInfoFrame.class.getResource( "metaContent2html.xsl" ) );
                    ByteArrayOutputStream bos = new ByteArrayOutputStream( 2000 );
                    xslt.transform( xml, bos );
                    textpane.setContentType( "text/html" );
                    textpane.setText( new String( bos.toByteArray() ) );
                } catch ( Exception e1 ) {
                    e1.printStackTrace();
                    // cannot parse as XML assume HTML
                    try {
                        textpane.setPage( url );
                    } catch ( Exception e2 ) {
                        e2.printStackTrace();
                        // not HTML -> set as text
                        textpane.setText( s );
                    }
                }

            } catch ( Exception e ) {
                e.printStackTrace();
            }

            metadataViewPanel.add( textpane, BorderLayout.CENTER );
            metadataViewPanel.doLayout();
        }

    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version. $Revision$, $Date$
     */
    private class DatasourceOpenListener implements ActionListener {

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed( ActionEvent e ) {
            DataAccessAdapter ada = (DataAccessAdapter) ( (JButton) e.getSource() ).getClientProperty( "DATASOURCE" );

            setDatasourcePanel( ada );
        }

    }

}
