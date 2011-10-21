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
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.model.UpdateDatasource;
import org.deegree.igeo.config.JDBCConnectionType;
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.dataadapter.WFSFeatureAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.DatabaseDatasource;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.FileDatasource;
import org.deegree.igeo.mapmodel.MemoryDatasource;
import org.deegree.igeo.mapmodel.WCSDatasource;
import org.deegree.igeo.mapmodel.WFSDatasource;
import org.deegree.igeo.mapmodel.WMSDatasource;
import org.deegree.igeo.mapmodel.Datasource.DS_PARAMETER;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.swing.CursorRegistry;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.io.JDBCConnection;
import org.deegree.kernel.Command;

/**
 * panel for displaying and manipulating the datasource(s) of a layer
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class DatasourceDescPanel extends JPanel {

    private static final long serialVersionUID = -671269529617885194L;

    private ILogger LOG = LoggerFactory.getLogger( DatasourceDescPanel.class );

    private DatasourceBasePanel datasourcePanel;

    @SuppressWarnings("unused")
    private MemDatasourcePanel memDatasourcePanel;

    private DataAccessAdapter dataAccessAdapter;

    private ApplicationContainer<Container> appContainer;

    /**
     * 
     * @param appContainer
     * @param dataAccessAdapter
     */
    DatasourceDescPanel( ApplicationContainer<Container> appContainer, DataAccessAdapter dataAccessAdapter ) {
        this.dataAccessAdapter = dataAccessAdapter;
        this.appContainer = appContainer;
        Datasource datasource = dataAccessAdapter.getDatasource();
        setLayout( new BorderLayout() );

        JPanel panel = new JPanel();
        panel.setLayout( new BoxLayout( panel, BoxLayout.PAGE_AXIS ) );      

        // data source specific informations
        if ( datasource instanceof FileDatasource ) {
            FileDatasource fd = (FileDatasource) datasource;
            datasourcePanel = new FileDatasourcePanel( fd );
        } else if ( datasource instanceof WMSDatasource ) {
            WMSDatasource wd = (WMSDatasource) datasource;
            datasourcePanel = new WMSDatasourcePanel( wd )  ;          
        } else if ( datasource instanceof WFSDatasource ) {
            datasourcePanel = new WFSDatasourcePanel( (WFSFeatureAdapter)dataAccessAdapter );
        } else if ( datasource instanceof WCSDatasource ) {
            WCSDatasource wd = (WCSDatasource) datasource;
            datasourcePanel = new WCSDatasourcePanel( wd.getCapabilitiesURL(), wd.getCoverage() );
        } else if ( datasource instanceof DatabaseDatasource ) {
            DatabaseDatasource dbd = (DatabaseDatasource) datasource;
            datasourcePanel = new DBDatasourcePanel( dbd.getJdbc() );
        } else if ( datasource instanceof MemoryDatasource ) {
            datasourcePanel = new MemDatasourcePanel();
        }
        JScrollPane sc = new JScrollPane( datasourcePanel );
        panel.add( sc );

        add( panel, BorderLayout.CENTER );

        add( createCommitPanel(), BorderLayout.SOUTH );
        doLayout();
    }

    /**
     * 
     * @return panel containing OK and cancel button
     */
    private JPanel createCommitPanel() {
        JPanel panel = new JPanel();
        panel.setLayout( new FlowLayout( FlowLayout.LEFT, 5, 5 ) );
        JButton savebt = new JButton( Messages.getMessage( Locale.getDefault(), "$MD10068" ) );
        savebt.setIcon( IconRegistry.getIcon( "save.gif" ) );
        savebt.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent event ) {
                Map<DS_PARAMETER, Object> parameters;
                try {
                    parameters = readParamsFromForms();
                } catch ( Exception e ) {
                    // leave method without performing command
                    return;
                }
                Command command = new UpdateDatasource( dataAccessAdapter, parameters );
                try {
                    DatasourceDescPanel.this.setCursor( CursorRegistry.WAIT_CURSOR );
                    appContainer.getCommandProcessor().executeSychronously( command, true );
                    DatasourceDescPanel.this.setCursor( CursorRegistry.DEFAULT_CURSOR );
                } catch ( Exception e ) {
                    LOG.logWarning( "ignore because should never happen", e );
                }
            }

        } );
        panel.add( savebt );
        return panel;

    }

    private Map<DS_PARAMETER, Object> readParamsFromForms()
                            throws Exception {
        Map<DS_PARAMETER, Object> parameters = new HashMap<DS_PARAMETER, Object>();
        DatasourceCorePanel dscp = datasourcePanel.getDatasourceCorePanel();
        parameters.put( DS_PARAMETER.extent, dscp.getEnvelope() );
        parameters.put( DS_PARAMETER.minScaleDenom, dscp.getMin() );
        parameters.put( DS_PARAMETER.maxScaleDenom, dscp.getMax() );
        // TODO
        parameters.put( DS_PARAMETER.authenticationInfo, null );
        parameters.put( DS_PARAMETER.name, dscp.getDSName() );
        Datasource datasource = dataAccessAdapter.getDatasource();
        if ( datasource instanceof FileDatasource ) {
            parameters.put( DS_PARAMETER.file, new File( ((FileDatasourcePanel)datasourcePanel).getFileName() ) );
            parameters.put( DS_PARAMETER.lazyLoading, ((FileDatasourcePanel)datasourcePanel).isLazyLoading() );
        } else if ( datasource instanceof WMSDatasource ) {
            URL url = null;
            try {
                url = new URL( ((WMSDatasourcePanel)datasourcePanel).getCapabilitiesURL() );
            } catch ( MalformedURLException e ) {
                DialogFactory.openWarningDialog( "Application", this,
                                                 Messages.getMessage( getLocale(), "$MD10293", url ),
                                                 Messages.getMessage( getLocale(), "$MD10294" ) );
                throw e;
            }
            String baseReq = ((WMSDatasourcePanel)datasourcePanel).getBaseRequest();
            parameters.put( DS_PARAMETER.capabilitiesURL, url );
            parameters.put( DS_PARAMETER.baseRequest, baseReq );
        } else if ( datasource instanceof WFSDatasource ) {
            URL url = null;
            try {
                url = new URL( ((WFSDatasourcePanel)datasourcePanel).getCapabilitiesURL() );
            } catch ( MalformedURLException e ) {
                DialogFactory.openWarningDialog( "Application", this,
                                                 Messages.getMessage( getLocale(), "$MD10295", url ),
                                                 Messages.getMessage( getLocale(), "$MD10296" ) );
                throw e;
            }
            parameters.put( DS_PARAMETER.capabilitiesURL, url );
            parameters.put( DS_PARAMETER.geomProperty, ((WFSDatasourcePanel)datasourcePanel).getGeometryProperty() );
            parameters.put( DS_PARAMETER.getFeature, ((WFSDatasourcePanel)datasourcePanel).getGetFeature() );
            parameters.put( DS_PARAMETER.lazyLoading, ((WFSDatasourcePanel)datasourcePanel).isLazyLoading() );
        } else if ( datasource instanceof WCSDatasource ) {
            URL url = null;
            try {
                url = new URL( ((WCSDatasourcePanel)datasourcePanel).getCapabilitiesURL() );
            } catch ( MalformedURLException e ) {
                DialogFactory.openWarningDialog( "Application", this,
                                                 Messages.getMessage( getLocale(), "$MD10297", url ),
                                                 Messages.getMessage( getLocale(), "$MD10298" ) );
                throw e;
            }
            parameters.put( DS_PARAMETER.capabilitiesURL, url );
            parameters.put( DS_PARAMETER.coverage, ((WCSDatasourcePanel)datasourcePanel).getCoverageName() );
        } else if ( datasource instanceof DatabaseDatasource ) {
            String user = ((DBDatasourcePanel)datasourcePanel).getUser();
            String password = ((DBDatasourcePanel)datasourcePanel).getPassword();
            String driver = ((DBDatasourcePanel)datasourcePanel).getDriver();
            String url = ((DBDatasourcePanel)datasourcePanel).getConnectionURL();
            parameters.put( DS_PARAMETER.jdbc, new JDBCConnection( driver, url, user, password, null, null, null ) );
            // TODO
            // SQL Template
            parameters.put( DS_PARAMETER.sqlTemplate, null );
        }

        return parameters;
    }

    // /////////////////////////////////////////////////////////////////////////
    // inner classes
    // /////////////////////////////////////////////////////////////////////////
   
    /**
     * panel for displaying/manipulating parameters of a WCS datasource
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version. $Revision$, $Date$
     */
    private class WCSDatasourcePanel extends DatasourceBasePanel {

        private static final long serialVersionUID = -7534622813855556523L;

        private JTextField namespacetf;

        private JTextField nametf;

        private JTextField capsURL;

        /**
         * 
         * @param getCapabilitiesURL
         * @param coverage
         */
        WCSDatasourcePanel( URL getCapabilitiesURL, QualifiedName coverage ) {
            initGUI( getCapabilitiesURL, coverage );
        }

        private void initGUI( URL getCapabilitiesURL, QualifiedName coverage ) {
            try {
                setPreferredSize( new Dimension( 400, 300 ) );
                this.setLayout( null );

                JLabel jLabel1 = new JLabel();
                this.add( jLabel1 );
                jLabel1.setText( Messages.getMessage( Locale.getDefault(), "$MD10093" ) );
                jLabel1.setBounds( 0, 12, 126, 14 );
                jLabel1.setForeground( Color.RED );

                JLabel jLabel2 = new JLabel();
                this.add( jLabel2 );
                jLabel2.setText( Messages.getMessage( Locale.getDefault(), "$MD10085" ) );
                jLabel2.setBounds( 5, 32, 96, 14 );

                capsURL = new JTextField();
                this.add( capsURL );
                capsURL.setText( getCapabilitiesURL.toURI().toASCIIString() );
                capsURL.setBounds( 113, 29, 184, 21 );

                JLabel jLabel3 = new JLabel();
                this.add( jLabel3 );
                jLabel3.setText( Messages.getMessage( Locale.getDefault(), "$MD10094" ) );
                jLabel3.setBounds( 10, 58, 79, 14 );

                JLabel jLabel4 = new JLabel();
                this.add( jLabel4 );
                jLabel4.setText( Messages.getMessage( Locale.getDefault(), "$MD10091" ) );
                jLabel4.setBounds( 25, 80, 65, 14 );

                nametf = new JTextField();
                this.add( nametf );
                nametf.setText( coverage.getLocalName() );
                nametf.setBounds( 113, 77, 184, 21 );

                JLabel jLabel5 = new JLabel();
                this.add( jLabel5 );
                jLabel5.setText( Messages.getMessage( Locale.getDefault(), "$MD10090" ) );
                jLabel5.setBounds( 25, 112, 76, 14 );

                namespacetf = new JTextField();
                this.add( namespacetf );
                if ( coverage.getNamespace() != null ) {
                    namespacetf.setText( coverage.getNamespace().toASCIIString() );
                }
                namespacetf.setBounds( 113, 109, 184, 21 );

            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
            }
        }

        /**
         * 
         * @return WFS GetCapabilties URL
         */
        public String getCapabilitiesURL() {
            return capsURL.getText();
        }

        /**
         * 
         * @return coverage name
         * @throws URISyntaxException
         */
        public QualifiedName getCoverageName()
                                throws URISyntaxException {
            return new QualifiedName( nametf.getText(), new URI( namespacetf.getText() ) );
        }

    }

    /**
     * panel for displaying/manipulating parameters of a database datasource
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version. $Revision$, $Date$
     */
    private class DBDatasourcePanel extends DatasourceBasePanel {

        private static final long serialVersionUID = -7755536883165950555L;

        private JTextField usertf;

        private JPasswordField passwordtf;

        private JTextField connectionURL;

        private JComboBox database;

        private Map<String, String> nameDriver;

        /**
         * 
         * @param jdbc
         */
        DBDatasourcePanel( JDBCConnectionType jdbc ) {
            initGUI( jdbc );
        }

        private void initGUI( JDBCConnectionType jdbc ) {
            try {
                setPreferredSize( new Dimension( 400, 300 ) );
                this.setLayout( null );

                JLabel jLabel1 = new JLabel();
                this.add( jLabel1 );
                jLabel1.setText( Messages.getMessage( Locale.getDefault(), "$MD10095" ) );
                jLabel1.setBounds( 0, 12, 143, 14 );
                jLabel1.setForeground( Color.RED );

                JLabel jLabel2 = new JLabel();
                this.add( jLabel2 );
                jLabel2.setText( "database: " );
                jLabel2.setBounds( 12, 32, 100, 14 );

                nameDriver = appContainer.getSettings().getDatabaseDrivers();
                String[] dbs = nameDriver.keySet().toArray( new String[nameDriver.size()] );
                ComboBoxModel databaseModel = new DefaultComboBoxModel( dbs );

                Map<String, String> driverName = new HashMap<String, String>( dbs.length );
                for ( int i = 0; i < dbs.length; i++ ) {
                    driverName.put( nameDriver.get( dbs[i] ), dbs[i] );
                }
                databaseModel.setSelectedItem( driverName.get( jdbc.getDriver() ) );
                database = new JComboBox();
                this.add( database );
                database.setModel( databaseModel );
                database.setBounds( 118, 29, 149, 21 );

                JLabel jLabel3 = new JLabel();
                this.add( jLabel3 );
                jLabel3.setText( Messages.getMessage( Locale.getDefault(), "$MD10096" ) );
                jLabel3.setBounds( 12, 59, 100, 14 );

                connectionURL = new JTextField();
                this.add( connectionURL );
                connectionURL.setText( jdbc.getUrl() );
                connectionURL.setBounds( 118, 56, 149, 21 );

                JLabel jLabel4 = new JLabel();
                this.add( jLabel4 );
                jLabel4.setText( Messages.getMessage( Locale.getDefault(), "$MD10097" ) );
                jLabel4.setBounds( 12, 86, 100, 14 );

                usertf = new JTextField();
                this.add( usertf );
                usertf.setText( jdbc.getUser() );
                usertf.setBounds( 118, 83, 149, 21 );

                JLabel jLabel5 = new JLabel();
                this.add( jLabel5 );
                jLabel5.setText( Messages.getMessage( Locale.getDefault(), "$MD10098" ) );
                jLabel5.setBounds( 12, 112, 100, 14 );

                passwordtf = new JPasswordField();
                this.add( passwordtf );
                passwordtf.setText( jdbc.getPassword() );
                passwordtf.setBounds( 118, 109, 149, 21 );

            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
            }
        }

        /**
         * 
         * @return database driver
         */
        public String getDriver() {
            return nameDriver.get( database.getSelectedItem() );
        }

        /**
         * 
         * @return database connection URL
         */
        public String getConnectionURL() {
            return connectionURL.getText();
        }

        /**
         * 
         * @return database user name
         */
        public String getUser() {
            return usertf.getText();
        }

        /**
         * 
         * @return database user's password
         */
        public String getPassword() {
            return new String( passwordtf.getPassword() );
        }

    }

    /**
     * panel for displaying that a layer uses memory datasource
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version. $Revision$, $Date$
     */
    private class MemDatasourcePanel extends DatasourceBasePanel {

        private static final long serialVersionUID = 4957892491682741900L;

        /**
         * 
         */
        MemDatasourcePanel() {
            setPreferredSize( new Dimension( 400, 300 ) );
            this.setLayout( null );

            JLabel jLabel1 = new JLabel();
            this.add( jLabel1 );
            jLabel1.setText( Messages.getMessage( Locale.getDefault(), "$MD10099" ) );
            jLabel1.setBounds( 0, 12, 143, 14 );
            jLabel1.setForeground( Color.RED );

        }
    }

}
