package org.deegree.igeo.views.swing.addlayer;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.swing.util.IconRegistry;
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
public class DatabaseExpertDialog extends JDialog {

    private static final long serialVersionUID = 77393124633293710L;

    private JPanel pnSQL;

    private JPanel pnPK;

    private JLabel lnSRID;

    private JSpinner spSRID;

    private JComboBox cbPK;

    private JLabel lbPK;

    private JButton btCancel;

    private JButton btCheckSQL;

    private JLabel lbGeometryColumn;

    private JComboBox cbGeometryColumn;

    private JPanel pnGeometryColumn;

    private JTextArea taSQL;

    private JButton btOK;

    private JPanel pnButtons;

    private String driver;

    private String url;

    private String user;

    private String password;

    private String sql;

    private ApplicationContainer<Container> appCont;

    /**
     * 
     * @param appCont
     * @param driver
     * @param url
     * @param user
     * @param password
     * @param sql
     */
    public DatabaseExpertDialog( ApplicationContainer<Container> appCont, String driver, String url, String user,
                                 String password, String sql ) {
        this.appCont = appCont;
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
        this.sql = sql;
        initGUI();
        setModal( true );
        setVisible( true );
    }

    private void initGUI() {
        try {
            {
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.1 };
                thisLayout.rowHeights = new int[] { 197, 131, 65, 7 };
                thisLayout.columnWeights = new double[] { 0.0, 0.1 };
                thisLayout.columnWidths = new int[] { 256, 7 };
                getContentPane().setLayout( thisLayout );
                {
                    pnSQL = new JPanel();
                    GridBagLayout pnSQLLayout = new GridBagLayout();
                    getContentPane().add(
                                          pnSQL,
                                          new GridBagConstraints( 0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnSQL.setBorder( BorderFactory.createTitledBorder( null, Messages.getMessage( getLocale(),
                                                                                                  "$MD11493" ),
                                                                       TitledBorder.LEADING,
                                                                       TitledBorder.DEFAULT_POSITION ) );
                    pnSQLLayout.rowWeights = new double[] { 0.0, 0.1 };
                    pnSQLLayout.rowHeights = new int[] { 124, 7 };
                    pnSQLLayout.columnWeights = new double[] { 0.1 };
                    pnSQLLayout.columnWidths = new int[] { 7 };
                    pnSQL.setLayout( pnSQLLayout );
                    {
                        taSQL = new JTextArea();
                        if ( sql != null ) {
                            taSQL.setText( sql );
                        }
                        pnSQL.add( taSQL, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 9, 9, 0, 9 ), 0,
                                                                  0 ) );
                    }
                    {
                        btCheckSQL = new JButton( Messages.getMessage( getLocale(), "$MD11494" ) );
                        pnSQL.add( btCheckSQL, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                                       GridBagConstraints.NONE,
                                                                       new Insets( 0, 9, 0, 0 ), 0, 0 ) );
                        btCheckSQL.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                try {
                                    checkSQL();
                                } catch ( Exception e1 ) {
                                    DialogFactory.openErrorDialog( appCont.getViewPlatform(),
                                                                   DatabaseExpertDialog.this,
                                                                   Messages.getMessage( getLocale(), "$MD11495" ),
                                                                   Messages.getMessage( getLocale(), "$MD11496" ), e1 );
                                }
                            }
                        } );
                    }
                }
                {
                    pnButtons = new JPanel();
                    FlowLayout pnButtonsLayout = new FlowLayout();
                    pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                    pnButtons.setLayout( pnButtonsLayout );
                    getContentPane().add(
                                          pnButtons,
                                          new GridBagConstraints( 0, 3, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    {
                        btOK = new JButton( Messages.getMessage( getLocale(), "$MD11497" ),
                                            IconRegistry.getIcon( "accept.png" ) );
                        pnButtons.add( btOK );
                        btOK.setEnabled( false );
                        btOK.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                dispose();
                            }
                        } );
                    }
                    {
                        btCancel = new JButton( Messages.getMessage( getLocale(), "$MD11498" ),
                                                IconRegistry.getIcon( "cancel.png" ) );
                        pnButtons.add( btCancel );
                        btCancel.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                if ( cbGeometryColumn.getModel().getSize() > 0 ) {
                                    cbGeometryColumn.setSelectedIndex( 0 );
                                }
                                taSQL.setText( null );
                                dispose();
                            }
                        } );
                    }
                }
                {
                    pnGeometryColumn = new JPanel();
                    GridBagLayout pnGeometryColumnLayout = new GridBagLayout();
                    getContentPane().add(
                                          pnGeometryColumn,
                                          new GridBagConstraints( 0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnGeometryColumnLayout.rowWeights = new double[] { 0.1, 0.1 };
                    pnGeometryColumnLayout.rowHeights = new int[] { 7, 20 };
                    pnGeometryColumnLayout.columnWeights = new double[] { 0.0, 0.1 };
                    pnGeometryColumnLayout.columnWidths = new int[] { 260, 7 };
                    pnGeometryColumn.setLayout( pnGeometryColumnLayout );
                    pnGeometryColumn.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                       "$MD11499" ) ) );
                    {
                        lbGeometryColumn = new JLabel( Messages.getMessage( getLocale(), "$MD11500" ) );
                        pnGeometryColumn.add( lbGeometryColumn, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                                        GridBagConstraints.CENTER,
                                                                                        GridBagConstraints.HORIZONTAL,
                                                                                        new Insets( 0, 9, 0, 0 ), 0, 0 ) );
                    }
                    {
                        cbGeometryColumn = new JComboBox();
                        pnGeometryColumn.add( cbGeometryColumn, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0,
                                                                                        GridBagConstraints.CENTER,
                                                                                        GridBagConstraints.HORIZONTAL,
                                                                                        new Insets( 0, 0, 0, 9 ), 0, 0 ) );
                        cbGeometryColumn.setEnabled( false );
                    }
                    {
                        lnSRID = new JLabel( Messages.getMessage( getLocale(), "$MD11501" ) );
                        pnGeometryColumn.add( lnSRID, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                                                              GridBagConstraints.CENTER,
                                                                              GridBagConstraints.HORIZONTAL,
                                                                              new Insets( 0, 9, 0, 0 ), 0, 0 ) );
                    }
                    {
                        spSRID = new JSpinner( new SpinnerNumberModel( -1, -1, 10000000, 1 ) );
                        pnGeometryColumn.add( spSRID, new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0,
                                                                              GridBagConstraints.CENTER,
                                                                              GridBagConstraints.HORIZONTAL,
                                                                              new Insets( 0, 0, 0, 9 ), 0, 0 ) );
                    }
                }
                {
                    pnPK = new JPanel();
                    GridBagLayout pnPKLayout = new GridBagLayout();
                    getContentPane().add(
                                          pnPK,
                                          new GridBagConstraints( 0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnPK.setBorder( BorderFactory.createTitledBorder( null, Messages.getMessage( getLocale(),
                                                                                                 "$MD11502" ),
                                                                      TitledBorder.LEADING,
                                                                      TitledBorder.DEFAULT_POSITION ) );
                    pnPKLayout.rowWeights = new double[] { 0.1 };
                    pnPKLayout.rowHeights = new int[] { 7 };
                    pnPKLayout.columnWeights = new double[] { 0.0, 0.1 };
                    pnPKLayout.columnWidths = new int[] { 261, 7 };
                    pnPK.setLayout( pnPKLayout );
                    {
                        lbPK = new JLabel( Messages.getMessage( getLocale(), "$MD11503" ) );
                        pnPK.add( lbPK, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                GridBagConstraints.HORIZONTAL,
                                                                new Insets( 0, 9, 0, 0 ), 0, 0 ) );
                    }
                    {
                        cbPK = new JComboBox();
                        cbPK.setEditable( true );
                        pnPK.add( cbPK, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                GridBagConstraints.HORIZONTAL,
                                                                new Insets( 0, 0, 0, 9 ), 0, 0 ) );
                    }
                }
            }
            this.setSize( 539, 460 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void checkSQL()
                            throws Exception {
        DBConnectionPool pool = DBConnectionPool.getInstance();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = pool.acquireConnection( driver, url, user, password );
            stmt = conn.createStatement();
            stmt.setMaxRows( 1 );
            String s = getSQL().trim().toUpperCase();
            rs = stmt.executeQuery( s );
            ResultSetMetaData rsmd = rs.getMetaData();
            int cnt = rsmd.getColumnCount();
            List<String> geomList = new ArrayList<String>();
            List<String> pkList = new ArrayList<String>();
            for ( int i = 0; i < cnt; i++ ) {
                int type = rsmd.getColumnType( i + 1 );
                String name = rsmd.getColumnName( i + 1 );
                if ( type == 1111 ) {
                    // postgis -> 1111
                    geomList.add( name );
                } else {
                    // if not a geometry it may will be used as PK
                    pkList.add( name );
                }
            }
            if ( geomList.size() > 0 ) {
                cbGeometryColumn.setModel( new DefaultComboBoxModel( geomList.toArray() ) );
                cbPK.setModel( new DefaultComboBoxModel( pkList.toArray() ) );
                cbGeometryColumn.setEnabled( true );
                btOK.setEnabled( true );
            }
        } catch ( Exception e ) {
            throw e;
        } finally {
            try {
                rs.close();
                stmt.close();
            } catch ( Exception e2 ) {
                // TODO: handle exception
            }
            try {
                pool.releaseConnection( conn, driver, url, user, password );
            } catch ( DBPoolException e ) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 
     * @return sql statement
     */
    String getSQL() {
        return taSQL.getText();
    }

    /**
     * 
     * @return name of geometry column to be used
     */
    String getGeometryColumn() {
        if ( cbGeometryColumn.getModel().getSize() > 0 ) {
            return (String) cbGeometryColumn.getSelectedItem() + " " + spSRID.getValue();
        }
        return null;
    }

    /**
     * 
     * @return name of primary key column to be used
     */
    String getPKColumn() {
        if ( cbPK.getModel().getSize() > 0 ) {
            return (String) cbPK.getSelectedItem();
        }
        return null;
    }

}
