//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
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
package org.deegree.igeo.views.swing.linkeddata;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.config.DatabaseDriverUtils;
import org.deegree.igeo.config.JDBCConnectionType;
import org.deegree.igeo.config.LinkedDatabaseTableType;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.swing.addlayer.AddDatabaseLayerDialog;
import org.deegree.io.DBConnectionPool;
import org.deegree.io.DBPoolException;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class DatabaseSelectPanel extends AbstractLinkedDataPanel {

    private static final long serialVersionUID = -6844190629872583902L;

    private static final ILogger LOG = LoggerFactory.getLogger( DatabaseSelectPanel.class );

    private JLabel lbDBDriver;

    private JLabel lbDBPassword;

    private JSpinner spPort;

    private JTextField tfDBURL;

    private JComboBox cbDBDriver;

    private JLabel lbDBTables;

    private JButton btConnect;

    private JButton btTestConnection;

    private JTextField tfDBUserName;

    private JComboBox cbTables;

    private JCheckBox cbSave;

    private JPasswordField pwDBPassword;

    private JTextField tfDBName;

    private JPanel pnButtons;

    private JLabel lbDBUserName;

    private JLabel lbDBName;

    private JLabel lbPort;

    private JLabel lbDBURL;

    /**
     * 
     */
    public DatabaseSelectPanel() {
        initGUI();
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            this.setPreferredSize( new java.awt.Dimension( 506, 365 ) );
            thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1 };
            thisLayout.rowHeights = new int[] { 37, 40, 39, 39, 44, 40, 41, 20 };
            thisLayout.columnWeights = new double[] { 0.0, 0.0, 0.1 };
            thisLayout.columnWidths = new int[] { 200, 158, 7 };
            this.setLayout( thisLayout );
            this.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11532" ) ) );
            {
                lbDBDriver = new JLabel( Messages.getMessage( getLocale(), "$MD11533" ) );
                this.add( lbDBDriver, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                              GridBagConstraints.HORIZONTAL, new Insets( 0, 9, 0, 0 ),
                                                              0, 0 ) );
            }
            {
                lbDBURL = new JLabel( Messages.getMessage( getLocale(), "$MD11534" ) );
                this.add( lbDBURL, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                           GridBagConstraints.HORIZONTAL, new Insets( 0, 9, 0, 0 ), 0,
                                                           0 ) );
            }
            {
                lbPort = new JLabel( Messages.getMessage( getLocale(), "$MD11535" ) );
                this.add( lbPort,
                          new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                  GridBagConstraints.HORIZONTAL, new Insets( 0, 9, 0, 0 ), 0, 0 ) );
            }
            {
                lbDBName = new JLabel( Messages.getMessage( getLocale(), "$MD11536" ) );
                this.add( lbDBName, new GridBagConstraints( 0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                            GridBagConstraints.HORIZONTAL, new Insets( 0, 9, 0, 0 ), 0,
                                                            0 ) );
            }
            {
                lbDBUserName = new JLabel( Messages.getMessage( getLocale(), "$MD11537" ) );
                this.add( lbDBUserName, new GridBagConstraints( 0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                GridBagConstraints.HORIZONTAL,
                                                                new Insets( 0, 9, 0, 0 ), 0, 0 ) );
            }
            {
                lbDBPassword = new JLabel( Messages.getMessage( getLocale(), "$MD11538" ) );
                this.add( lbDBPassword, new GridBagConstraints( 0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                GridBagConstraints.HORIZONTAL,
                                                                new Insets( 0, 9, 0, 0 ), 0, 0 ) );
            }
            {
                pnButtons = new JPanel();
                FlowLayout pnButtonsLayout = new FlowLayout();
                pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                pnButtons.setLayout( pnButtonsLayout );
                this.add( pnButtons, new GridBagConstraints( 0, 6, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                             GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                {
                    btTestConnection = new JButton( Messages.getMessage( getLocale(), "$MD11539" ) );
                    pnButtons.add( btTestConnection );
                    btTestConnection.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            testConnection();
                        }
                    } );
                }
                {
                    btConnect = new JButton( Messages.getMessage( getLocale(), "$MD11540" ) );
                    pnButtons.add( btConnect );
                    btConnect.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            connectToDatabase();
                        }
                    } );
                }
            }
            {
                lbDBTables = new JLabel( Messages.getMessage( getLocale(), "$MD11541" ) );
                this.add( lbDBTables, new GridBagConstraints( 0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                                              GridBagConstraints.HORIZONTAL, new Insets( 5, 9, 0, 0 ),
                                                              0, 0 ) );
            }
            {
                cbDBDriver = new JComboBox( new DefaultComboBoxModel( DatabaseDriverUtils.getDriverLabels() ) );
                cbDBDriver.addActionListener( new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {
                        changeDatabaseVendor();
                    }
                } );

                this.add( cbDBDriver, new GridBagConstraints( 1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                              GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 9 ),
                                                              0, 0 ) );
            }
            {
                tfDBURL = new JTextField();
                this.add( tfDBURL, new GridBagConstraints( 1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                           GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 9 ), 0,
                                                           0 ) );
            }
            {
                spPort = new JSpinner( new SpinnerNumberModel( 5432, 0, 60000, 1 ) );
                spPort.setEditor( new JSpinner.NumberEditor( spPort, "####" ) );
                this.add( spPort,
                          new GridBagConstraints( 1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                  GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
            }
            {
                tfDBName = new JTextField();
                this.add( tfDBName, new GridBagConstraints( 1, 3, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                            GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 9 ), 0,
                                                            0 ) );
            }
            {
                pwDBPassword = new JPasswordField();
                this.add( pwDBPassword, new GridBagConstraints( 1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                GridBagConstraints.HORIZONTAL,
                                                                new Insets( 0, 0, 0, 0 ), 0, 0 ) );
            }
            {
                cbSave = new JCheckBox( Messages.getMessage( getLocale(), "$MD11543" ) );
                this.add( cbSave,
                          new GridBagConstraints( 1, 6, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                  GridBagConstraints.HORIZONTAL, new Insets( 0, 9, 0, 0 ), 0, 0 ) );
            }
            {
                tfDBUserName = new JTextField();
                this.add( tfDBUserName, new GridBagConstraints( 1, 4, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                GridBagConstraints.HORIZONTAL,
                                                                new Insets( 0, 0, 0, 9 ), 0, 0 ) );
            }
            {
                String s = Messages.getMessage( getLocale(), "$MD11544" );
                ComboBoxModel cbTablesModel = new DefaultComboBoxModel( new String[] { s } );
                cbTables = new JComboBox();
                this.add( cbTables, new GridBagConstraints( 1, 8, 2, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                                            GridBagConstraints.HORIZONTAL, new Insets( 5, 0, 0, 9 ), 0,
                                                            0 ) );
                cbTables.setModel( cbTablesModel );
            }
            readConnectionInfoFromCache();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private String getConnectionString() {
        String database = null;
        String s = cbDBDriver.getSelectedItem().toString().toLowerCase();
        if ( s.indexOf( "postgis" ) > -1 ) {
            database = "jdbc:postgresql://" + tfDBURL.getText() + ':' + ( (Number) spPort.getValue() ).intValue() + '/'
                       + tfDBName.getText();
        } else if ( s.indexOf( "oracle" ) > -1 ) {
            database = "jdbc:oracle:thin:@" + tfDBURL.getText() + ':' + ( (Number) spPort.getValue() ).intValue() + ':'
                       + tfDBName.getText();
        } else if ( s.indexOf( "mysql" ) > -1 ) {
            database = "jdbc:mysql://" + tfDBURL.getText() + ':' + ( (Number) spPort.getValue() ).intValue() + '/'
                       + tfDBName.getText();
        } else if ( s.indexOf( "sqlserver" ) > -1 ) {
            database = "jdbc:sqlserver:// " + tfDBURL.getText() + ':' + ( (Number) spPort.getValue() ).intValue()
                       + ";databaseName=" + tfDBName.getText() + ";";
        }
        return database;
    }

    /**
     * 
     */
    private void testConnection() {
        String driver = DatabaseDriverUtils.getDriver( cbDBDriver.getSelectedItem().toString() );
        String database = getConnectionString();
        try {
            DriverManager.registerDriver( (Driver) Class.forName( driver ).newInstance() );

            Connection conn = DriverManager.getConnection( database, tfDBUserName.getText(),
                                                           new String( pwDBPassword.getPassword() ) );
            conn.close();
            DialogFactory.openInformationDialog( appCont.getViewPlatform(), this,
                                                 Messages.getMessage( getLocale(), "$MD11456" ),
                                                 Messages.getMessage( getLocale(), "$MD11452" ) );
        } catch ( Exception e ) {
            DialogFactory.openErrorDialog( appCont.getViewPlatform(), this,
                                           Messages.getMessage( getLocale(), "$MD11452" ),
                                           Messages.getMessage( getLocale(), "$MD11457", database ), e );
        }
    }

    /**
     * 
     */
    private void connectToDatabase() {
        DBConnectionPool pool = DBConnectionPool.getInstance();
        String driver = DatabaseDriverUtils.getDriver( cbDBDriver.getSelectedItem().toString() );
        String database = getConnectionString();
        Connection conn = null;
        try {
            conn = pool.acquireConnection( driver, database, tfDBUserName.getText(),
                                           new String( pwDBPassword.getPassword() ) );
            readAvailableTables( conn );
        } catch ( Exception e ) {
            DialogFactory.openErrorDialog( appCont.getViewPlatform(), this,
                                           Messages.getMessage( getLocale(), "$MD11452" ),
                                           Messages.getMessage( getLocale(), "$MD11458", e.getMessage() ), e );
        } finally {
            try {
                pool.releaseConnection( conn, driver, database, tfDBUserName.getText(),
                                        new String( pwDBPassword.getPassword() ) );
            } catch ( DBPoolException e ) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param conn
     * @throws Exception
     */
    private void readAvailableTables( Connection conn )
                            throws Exception {
        String s = cbDBDriver.getSelectedItem().toString().toLowerCase();
        String sql = null;
        List<String> tables = new ArrayList<String>( 500 );
        tables.add( "--- select a table ---" );
        ResultSet rs = null;
        Statement stmt = conn.createStatement();
        try {
            if ( s.indexOf( "postgis" ) > -1 ) {
                sql = "SELECT distinct pg_class.relname AS relname, pg_namespace.nspname AS nspname FROM pg_attribute"
                      + " JOIN pg_class ON pg_class.oid = pg_attribute.attrelid"
                      + " JOIN pg_namespace ON pg_namespace.oid = pg_class.relnamespace"
                      + " JOIN pg_type ON pg_attribute.atttypid = pg_type.oid"
                      + " WHERE pg_attribute.attstattarget <> 0 AND lower(pg_namespace.nspname) <> 'information_schema' "
                      + " AND lower(pg_namespace.nspname) <> 'pg_catalog' "
                      + " AND lower(pg_namespace.nspname) <> 'pg_toast' order by 1,2";
                rs = stmt.executeQuery( sql );
                while ( rs.next() ) {
                    String table = rs.getString( 1 );
                    String schema = rs.getString( 2 );
                    tables.add( schema + '.' + table );
                }
            } else if ( s.indexOf( "oracle" ) > -1 ) {
                sql = "select table_name from USER_TABLES ";
                rs = stmt.executeQuery( sql );
                while ( rs.next() ) {
                    tables.add( rs.getString( 1 ) );
                }
            } else if ( s.indexOf( "mysql" ) > -1 ) {
                // TODO
                LOG.logWarning( "MYSQL is not supported yet" );
            } else if ( s.indexOf( "sqlserver" ) > -1 ) {
                // TODO
                LOG.logWarning( "SQLServer is not supported yet" );
            }
        } catch ( Exception e ) {
            throw e;
        } finally {
            rs.close();
            stmt.close();
        }
        Collections.sort( tables );
        cbTables.setModel( new DefaultComboBoxModel( tables.toArray() ) );

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.linkeddata.AbstractLinkedDataPanel#getNext()
     */
    AbstractLinkedDataPanel getNext() {
        if ( cbTables.getSelectedIndex() == 0 ) {
            // TODO
            // add warning dialog
            return null;
        }
        String table = cbTables.getSelectedItem().toString();
        linkedTable.setEditable( false );
        JDBCConnectionType conType = new JDBCConnectionType();
        conType.setDriver( DatabaseDriverUtils.getDriver( cbDBDriver.getSelectedItem().toString() ) );
        conType.setUrl( getConnectionString() );
        conType.setUser( tfDBUserName.getText() );
        conType.setPassword( new String( pwDBPassword.getPassword() ) );
        ( (LinkedDatabaseTableType) linkedTable ).setConnection( conType );
        ( (LinkedDatabaseTableType) linkedTable ).setSqlTemplate( "select * from " + table );
        AbstractLinkedDataPanel p = new DefineKeysPanel( appCont, linkedTable );
        p.setPrevious( this );
        p.setView( isView() );
        writeConnectionInfoToCache();
        return p;
    }

    private void writeConnectionInfoToCache() {
        if ( cbSave.isSelected() ) {
            Preferences prefs = Preferences.userNodeForPackage( AddDatabaseLayerDialog.class );
            String s = cbDBDriver.getSelectedItem().toString().toLowerCase();
            prefs.put( s + "URL", tfDBURL.getText() );
            prefs.put( s + "USER", tfDBUserName.getText() );
            prefs.put( s + "DATABASE", tfDBName.getText() );
            prefs.put( s + "PASSWORD", new String( pwDBPassword.getPassword() ) );
            prefs.putInt( s + "PORT", ( (Number) spPort.getValue() ).intValue() );
        }
    }

    private void readConnectionInfoFromCache() {
        Preferences prefs = Preferences.userNodeForPackage( AddDatabaseLayerDialog.class );
        String s = cbDBDriver.getSelectedItem().toString().toLowerCase();
        tfDBURL.setText( prefs.get( s + "URL", "localhost" ) );
        tfDBUserName.setText( prefs.get( s + "USER", "" ) );
        tfDBName.setText( prefs.get( s + "DATABASE", "" ) );
        pwDBPassword.setText( prefs.get( s + "PASSWORD", "" ) );

        if ( s.indexOf( "postgis" ) > -1 ) {
            spPort.setValue( prefs.getInt( s + "PORT", 5432 ) );
        } else if ( s.indexOf( "oracle" ) > -1 ) {
            spPort.setValue( prefs.getInt( s + "PORT", 1521 ) );
        } else if ( s.indexOf( "mysql" ) > -1 ) {
            spPort.setValue( prefs.getInt( s + "PORT", 3306 ) );
        } else if ( s.indexOf( "sqlserver" ) > -1 ) {
            spPort.setValue( prefs.getInt( s + "PORT", 1433 ) );
        }
    }

    /**
     * 
     */
    private void changeDatabaseVendor() {
        String s = cbDBDriver.getSelectedItem().toString().toLowerCase();
        if ( s.indexOf( "postgis" ) > -1 ) {
            lbDBName.setText( "database name" );
            spPort.setValue( 5432 );
        } else if ( s.indexOf( "oracle" ) > -1 ) {
            lbDBName.setText( "SID" );
            spPort.setValue( 1521 );
        } else if ( s.indexOf( "mysql" ) > -1 ) {
            lbDBName.setText( "database name" );
            spPort.setValue( 3306 );
        } else if ( s.indexOf( "sqlserver" ) > -1 ) {
            lbDBName.setText( "database name" );
            spPort.setValue( 1433 );
        }
        SwingUtilities.updateComponentTreeUI( lbDBName );
        readConnectionInfoFromCache();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.linkeddata.AbstractLinkedDataPanel#getDescription()
     */
    String getDescription() {
        return Messages.getMessage( getLocale(), "$MD11575" );
    }
}
