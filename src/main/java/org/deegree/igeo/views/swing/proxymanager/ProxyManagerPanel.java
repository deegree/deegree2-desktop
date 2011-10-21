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
package org.deegree.igeo.views.swing.proxymanager;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.deegree.framework.util.StringTools;
import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.HelpManager;
import org.deegree.igeo.views.swing.DefaultPanel;
import org.deegree.igeo.views.swing.HelpFrame;
import org.deegree.igeo.views.swing.util.IconRegistry;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ProxyManagerPanel extends DefaultPanel {

    private static final long serialVersionUID = 7325097434993391287L;

    private JPanel pnButton;

    private JButton btHelp;

    private JTabbedPane tabbedPane;

    private JLabel lbHTTP_PW;

    private JPasswordField pwHTTP_PW;

    private JTextField tfHTTP_User;

    private JSpinner spHTTP_Port;

    private JTextField tfHTTP_Host;

    private JLabel lbHTTP_User;

    private JLabel lbHTTP_Port;

    private JLabel lbHTTP_Host;

    private JPanel pnFTP;

    private JPanel pnHTTP;

    private JList lstHTTP_NoneProxy;

    private JScrollPane scHTTP_NoneProxy;

    private JButton btClear;

    private JButton btTake;

    private JPanel pnHTTP_NoneProxy;

    private JButton btHTTP_Remove;

    private JButton btHTTP_Add;

    private ProxyBean httpBean;

    /**
     * 
     */
    public ProxyManagerPanel() {
        Preferences prefs = Preferences.userNodeForPackage( ProxyManagerPanel.class );
        String host = prefs.get( "PROXYDEF_HTTP_HOST", null );
        int port = prefs.getInt( "PROXYDEF_HTTP_PORT", -1 );
        String user = prefs.get( "PROXYDEF_HTTP_USER", null );
        String pw = prefs.get( "PROXYDEF_HTTP_PASSWORD", null );
        String nonProxyHosts = prefs.get( "PROXYDEF_HTTP_NONPROXYHOSTS", null );
        List<String> nonProxyhostList = StringTools.toList( nonProxyHosts, "|", false );
        httpBean = new ProxyBean();
        if ( host != null ) {
            httpBean.setHost( host );
            httpBean.setPort( port );
            httpBean.setUser( user );
            httpBean.setPassword( pw );
            httpBean.setNonProxyHosts( nonProxyhostList );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#init(org.deegree.igeo.config.ViewFormType)
     */
    public void init( ViewFormType viewForm )
                            throws Exception {
        initGUI();
    }

    private void initGUI() {
        try {
            // Settings settings = appCont.getSettings();
            this.setLayout( new BorderLayout() );
            this.setPreferredSize( new java.awt.Dimension( 400, 345 ) );
            {
                pnButton = new JPanel();
                GridBagLayout pnButtonLayout = new GridBagLayout();
                this.add( pnButton, BorderLayout.SOUTH );
                pnButton.setPreferredSize( new java.awt.Dimension( 126, 37 ) );
                pnButton.setLayout( pnButtonLayout );
                {
                    btHelp = new JButton( Messages.getMessage( getLocale(), "$MD11350" ),
                                          IconRegistry.getIcon( "help.png" ) );
                    pnButton.add( btHelp, new GridBagConstraints( 2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHEAST,
                                                                  GridBagConstraints.NONE, new Insets( 9, 0, 0, 9 ), 0,
                                                                  0 ) );
                    btHelp.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            HelpFrame hf = HelpFrame.getInstance( new HelpManager( owner.getApplicationContainer() ) );
                            hf.setVisible( true );
                            hf.gotoKeyword( "ProxyManager:ProxyManager" );
                        }
                    } );

                    btTake = new JButton( Messages.getMessage( getLocale(), "$MD11358" ),
                                          IconRegistry.getIcon( "accept.png" ) );
                    pnButton.add( btTake, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                                                  GridBagConstraints.NONE, new Insets( 9, 9, 0, 0 ), 0,
                                                                  0 ) );
                    btTake.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            if ( tfHTTP_Host.getText() == null || tfHTTP_Host.getText().length() < 7 ) {
                                DialogFactory.openWarningDialog( owner.getApplicationContainer().getViewPlatform(),
                                                                 ProxyManagerPanel.this, "proxyHost must be defined",
                                                                 "WARING" );
                                return;
                            }

                            Preferences prefs = Preferences.userNodeForPackage( ProxyManagerDialog.class );

                            System.setProperty( "http.proxyHost", tfHTTP_Host.getText() );
                            prefs.put( "PROXYDEF_HTTP_HOST", tfHTTP_Host.getText() );
                            System.setProperty( "http.proxyPort", spHTTP_Port.getValue().toString() );
                            prefs.putInt( "PROXYDEF_HTTP_PORT", ( (Number) spHTTP_Port.getValue() ).intValue() );
                            if ( tfHTTP_User.getText() != null && tfHTTP_User.getText().length() > 0 ) {
                                System.setProperty( "http.proxyUser", tfHTTP_User.getText() );
                                prefs.put( "PROXYDEF_HTTP_USER", tfHTTP_User.getText() );
                            }
                            if ( pwHTTP_PW.getPassword() != null && pwHTTP_PW.getPassword().length > 0 ) {
                                System.setProperty( "http.proxyPassword", new String( pwHTTP_PW.getPassword() ) );
                                prefs.put( "PROXYDEF_HTTP_PASSWORD", new String( pwHTTP_PW.getPassword() ) );
                            }
                            if ( lstHTTP_NoneProxy.getModel().getSize() > 0 ) {
                                int k = lstHTTP_NoneProxy.getModel().getSize();
                                List<String> list = new ArrayList<String>( k );
                                for ( int i = 0; i < k; i++ ) {
                                    list.add( lstHTTP_NoneProxy.getModel().getElementAt( i ).toString() );
                                }
                                String s = StringTools.listToString( list, '|' );
                                System.setProperty( "http.nonProxyHosts", s );
                                prefs.put( "PROXYDEF_HTTP_NONPROXYHOSTS", s );
                            }
                        }
                    } );
                }
                {
                    btClear = new JButton( "clear" );
                    pnButton.add( btClear, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                                                   GridBagConstraints.NONE, new Insets( 9, 9, 0, 0 ),
                                                                   0, 0 ) );
                    btClear.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            tfHTTP_Host.setText( "" );
                            Preferences prefs = Preferences.userNodeForPackage( ProxyManagerDialog.class );
                            System.getProperties().remove( "http.proxyHost" );
                            prefs.remove( "PROXYDEF_HTTP_HOST" );
                            spHTTP_Port.setValue( 9999 );
                            System.getProperties().remove( "http.proxyPort" );
                            prefs.remove( "PROXYDEF_HTTP_PORT" );
                            tfHTTP_User.setText( "" );
                            System.getProperties().remove( "http.proxyUser" );
                            prefs.remove( "PROXYDEF_HTTP_USER" );
                            pwHTTP_PW.setText( "" );
                            System.getProperties().remove( "http.proxyPassword" );
                            prefs.remove( "PROXYDEF_HTTP_PASSWORD" );
                            lstHTTP_NoneProxy.setModel( new DefaultListModel() );
                            System.getProperties().remove( "http.nonProxyHosts" );
                            prefs.remove( "PROXYDEF_HTTP_NONPROXYHOSTS" );
                        }
                    } );
                }
                pnButtonLayout.rowWeights = new double[] { 0.1 };
                pnButtonLayout.rowHeights = new int[] { 7 };
                pnButtonLayout.columnWeights = new double[] { 0.0, 0.1, 0.1 };
                pnButtonLayout.columnWidths = new int[] { 141, 20, 7 };
            }
            {
                tabbedPane = new JTabbedPane();
                this.add( tabbedPane, BorderLayout.CENTER );
                {
                    pnHTTP = new JPanel();
                    GridBagLayout pnHTTPLayout = new GridBagLayout();
                    tabbedPane.addTab( "HTTP", null, pnHTTP, null );
                    pnHTTPLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1 };
                    pnHTTPLayout.rowHeights = new int[] { 33, 33, 33, 33, 20, 41, 37, 20 };
                    pnHTTPLayout.columnWeights = new double[] { 0.0, 0.0, 0.1 };
                    pnHTTPLayout.columnWidths = new int[] { 117, 28, 7 };
                    pnHTTP.setLayout( pnHTTPLayout );
                    {
                        lbHTTP_Host = new JLabel( Messages.getMessage( getLocale(), "$MD11351" ) );
                        pnHTTP.add( lbHTTP_Host, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                         GridBagConstraints.CENTER,
                                                                         GridBagConstraints.HORIZONTAL,
                                                                         new Insets( 0, 10, 0, 0 ), 0, 0 ) );
                    }
                    {
                        lbHTTP_Port = new JLabel( Messages.getMessage( getLocale(), "$MD11352" ) );
                        pnHTTP.add( lbHTTP_Port, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                                                         GridBagConstraints.CENTER,
                                                                         GridBagConstraints.HORIZONTAL,
                                                                         new Insets( 0, 10, 0, 0 ), 0, 0 ) );
                    }
                    {
                        lbHTTP_User = new JLabel( Messages.getMessage( getLocale(), "$MD11353" ) );
                        pnHTTP.add( lbHTTP_User, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0,
                                                                         GridBagConstraints.CENTER,
                                                                         GridBagConstraints.HORIZONTAL,
                                                                         new Insets( 0, 10, 0, 0 ), 0, 0 ) );
                    }
                    {
                        tfHTTP_Host = new JTextField( httpBean.getHost() );
                        pnHTTP.add( tfHTTP_Host, new GridBagConstraints( 1, 0, 2, 1, 0.0, 0.0,
                                                                         GridBagConstraints.CENTER,
                                                                         GridBagConstraints.HORIZONTAL,
                                                                         new Insets( 0, 0, 0, 10 ), 0, 0 ) );
                    }
                    {

                        spHTTP_Port = new JSpinner( new SpinnerNumberModel( 3210, 0, 100000, 1 ) );
                        spHTTP_Port.setValue( httpBean.getPort() );
                        pnHTTP.add( spHTTP_Port, new GridBagConstraints( 1, 1, 2, 1, 0.0, 0.0,
                                                                         GridBagConstraints.CENTER,
                                                                         GridBagConstraints.HORIZONTAL,
                                                                         new Insets( 0, 0, 0, 10 ), 0, 0 ) );
                    }
                    {
                        tfHTTP_User = new JTextField( httpBean.getUser() );
                        pnHTTP.add( tfHTTP_User, new GridBagConstraints( 1, 2, 2, 1, 0.0, 0.0,
                                                                         GridBagConstraints.CENTER,
                                                                         GridBagConstraints.HORIZONTAL,
                                                                         new Insets( 0, 0, 0, 10 ), 0, 0 ) );
                    }
                    {
                        pwHTTP_PW = new JPasswordField( httpBean.getPassword() );
                        pnHTTP.add( pwHTTP_PW, new GridBagConstraints( 1, 3, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                       GridBagConstraints.HORIZONTAL,
                                                                       new Insets( 0, 0, 0, 10 ), 0, 0 ) );
                    }
                    {
                        lbHTTP_PW = new JLabel( Messages.getMessage( getLocale(), "$MD11354" ) );
                        pnHTTP.add( lbHTTP_PW, new GridBagConstraints( 0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                       GridBagConstraints.HORIZONTAL,
                                                                       new Insets( 0, 10, 0, 0 ), 0, 0 ) );
                    }
                    {
                        btHTTP_Add = new JButton( Messages.getMessage( getLocale(), "$MD11355" ) );
                        pnHTTP.add( btHTTP_Add, new GridBagConstraints( 0, 5, 2, 1, 0.0, 0.0,
                                                                        GridBagConstraints.CENTER,
                                                                        GridBagConstraints.HORIZONTAL,
                                                                        new Insets( 0, 10, 0, 10 ), 0, 0 ) );
                        btHTTP_Add.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                String s = JOptionPane.showInputDialog( ProxyManagerPanel.this,
                                                                        Messages.getMessage( getLocale(), "$MD11356" ) );
                                if ( s != null ) {
                                    DefaultComboBoxModel model = (DefaultComboBoxModel) lstHTTP_NoneProxy.getModel();
                                    model.addElement( s );
                                }
                            }
                        } );
                    }
                    {
                        btHTTP_Remove = new JButton( Messages.getMessage( getLocale(), "$MD11357" ) );
                        pnHTTP.add( btHTTP_Remove, new GridBagConstraints( 0, 6, 2, 1, 0.0, 0.0,
                                                                           GridBagConstraints.CENTER,
                                                                           GridBagConstraints.HORIZONTAL,
                                                                           new Insets( 0, 10, 0, 10 ), 0, 0 ) );
                        btHTTP_Remove.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                Object[] values = lstHTTP_NoneProxy.getSelectedValues();
                                DefaultComboBoxModel model = (DefaultComboBoxModel) lstHTTP_NoneProxy.getModel();
                                for ( Object object : values ) {
                                    model.removeElement( object );
                                }

                            }
                        } );
                    }
                    {
                        pnHTTP_NoneProxy = new JPanel();
                        BorderLayout pnNoneProxyLayout = new BorderLayout();
                        pnHTTP_NoneProxy.setLayout( pnNoneProxyLayout );
                        pnHTTP.add( pnHTTP_NoneProxy, new GridBagConstraints( 2, 5, 1, 3, 0.0, 0.0,
                                                                              GridBagConstraints.CENTER,
                                                                              GridBagConstraints.BOTH,
                                                                              new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                        pnHTTP_NoneProxy.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
                        {
                            scHTTP_NoneProxy = new JScrollPane();
                            pnHTTP_NoneProxy.add( scHTTP_NoneProxy, BorderLayout.CENTER );
                            {
                                String[] lst = httpBean.getNonProxyHosts().toArray(
                                                                                    new String[httpBean.getNonProxyHosts().size()] );
                                lstHTTP_NoneProxy = new JList( new DefaultComboBoxModel( lst ) );
                                scHTTP_NoneProxy.setViewportView( lstHTTP_NoneProxy );
                            }
                        }
                    }
                }
                {
                    pnFTP = new JPanel();
                    // tabbedPane.addTab( "FTP", null, pnFTP, null );
                    pnFTP.setEnabled( false );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
