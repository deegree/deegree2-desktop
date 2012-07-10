package org.deegree.igeo.desktop;

import static java.awt.event.KeyEvent.VK_ENTER;
import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;
import static javax.swing.KeyStroke.getKeyStroke;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.views.swing.util.IconRegistry;

/**
 * 
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author: Andreas Poth $
 * 
 * @version $Revision: $, $Date: $
 * 
 */
public class LoginDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = -6573871765947388995L;

    private JPanel pnButtons;

    private JButton btLogin;

    private JPanel pnText;

    private JPasswordField ftfPassword;

    private JTextField tfUser;

    private JLabel jLabel2;

    private JLabel jLabel1;

    private JTextArea tpText;

    private JButton btCancel;

    private IGeoDesktop iGeoDesktop;

    private JCheckBox cbRemeber;

    /**
     * 
     * @param frame
     */
    public LoginDialog( JFrame frame, IGeoDesktop iGeoDesktop ) {
        super( frame );
        this.iGeoDesktop = iGeoDesktop;
        initGUI();
        setVisible( true );
        setModal( true );
        Rectangle rect = frame.getBounds();
        setLocation( rect.x + rect.width / 2 - getWidth() / 2, rect.y + rect.height / 2 - getHeight() / 2 );
    }

    private void initGUI() {
        try {
            {
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.1 };
                thisLayout.rowHeights = new int[] { 43, 45, 43, 7 };
                thisLayout.columnWeights = new double[] { 0.0, 0.0, 0.1, 0.1 };
                thisLayout.columnWidths = new int[] { 138, 91, 7, 7 };
                getContentPane().setLayout( thisLayout );
                {
                    pnButtons = new JPanel();
                    FlowLayout pnButtonsLayout = new FlowLayout();
                    pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                    pnButtons.setLayout( pnButtonsLayout );
                    getContentPane().add(
                                          pnButtons,
                                          new GridBagConstraints( 0, 3, 4, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 5, 0, 0, 0 ), 0,
                                                                  0 ) );
                    {
                        btLogin = new JButton( Messages.getMessage( getLocale(), "$MD10870" ),
                                               IconRegistry.getIcon( "log_in.png" ) );
                        getRootPane().setDefaultButton( btLogin );

                        // login action listener
                        ActionListener al = new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                dispose();
                                Preferences prefs = Preferences.userNodeForPackage( IGeoDesktop.class );
                                prefs.putBoolean( "rememberloging", cbRemeber.isSelected() );
                                if ( cbRemeber.isSelected() ) {
                                    prefs.put( "rememberloginguser", new String( tfUser.getText() ) );
                                    prefs.put( "rememberlogingpassword", new String( ftfPassword.getPassword() ) );
                                } else {
                                    prefs.put( "rememberloginguser", "" );
                                    prefs.put( "rememberlogingpassword", "" );
                                }
                                iGeoDesktop.login( tfUser.getText(), new String( ftfPassword.getPassword() ) );
                            }
                        };

                        KeyStroke stroke = getKeyStroke( VK_ENTER, 0 );
                        getRootPane().registerKeyboardAction( al, "login", stroke, WHEN_IN_FOCUSED_WINDOW );
                        btLogin.addActionListener( al );
                        pnButtons.add( btLogin );
                        btLogin.setPreferredSize( new java.awt.Dimension( 120, 25 ) );
                    }
                    {
                        btCancel = new JButton( Messages.getMessage( getLocale(), "$MD10871" ),
                                                IconRegistry.getIcon( "cancel.png" ) );
                        btCancel.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                dispose();
                            }
                        } );
                        pnButtons.add( btCancel );
                        btCancel.setPreferredSize( new java.awt.Dimension( 120, 25 ) );
                    }
                }
                {
                    pnText = new JPanel();
                    BorderLayout pnTextLayout = new BorderLayout();
                    pnText.setLayout( pnTextLayout );
                    getContentPane().add(
                                          pnText,
                                          new GridBagConstraints( 0, 0, 1, 3, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnText.setBorder( BorderFactory.createTitledBorder( "Log In" ) );
                    pnText.setPreferredSize( new java.awt.Dimension( 130, 10 ) );
                    {
                        tpText = new JTextArea();
                        tpText.setText( Messages.getMessage( getLocale(), "$MD10869" ) );
                        tpText.setBackground( pnText.getBackground() );
                        tpText.setLineWrap( true );
                        tpText.setWrapStyleWord( true );
                        pnText.add( tpText, BorderLayout.CENTER );
                        tpText.setEditable( false );
                    }
                }
                {
                    jLabel1 = new JLabel( Messages.getMessage( getLocale(), "$MD10872" ) );
                    getContentPane().add(
                                          jLabel1,
                                          new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0,
                                                                                                             0 ), 0, 0 ) );
                }
                {
                    jLabel2 = new JLabel( Messages.getMessage( getLocale(), "$MD10873" ) );
                    getContentPane().add(
                                          jLabel2,
                                          new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0,
                                                                                                             0 ), 0, 0 ) );
                }
                {
                    tfUser = new JTextField();
                    getContentPane().add(
                                          tfUser,
                                          new GridBagConstraints( 2, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL,
                                                                  new Insets( 0, 0, 0, 5 ), 0, 0 ) );
                    tfUser.setPreferredSize( new java.awt.Dimension( 150, 25 ) );
                    tfUser.setSize( 157, 25 );
                }
                {
                    ftfPassword = new JPasswordField();
                    getContentPane().add(
                                          ftfPassword,
                                          new GridBagConstraints( 2, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL,
                                                                  new Insets( 0, 0, 0, 5 ), 0, 0 ) );
                    ftfPassword.setPreferredSize( new java.awt.Dimension( 150, 25 ) );
                    ftfPassword.setSize( 157, 25 );
                }
                {
                    cbRemeber = new JCheckBox();
                    getContentPane().add(
                                          cbRemeber,
                                          new GridBagConstraints( 2, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL,
                                                                  new Insets( 0, 0, 0, 5 ), 0, 0 ) );
                    cbRemeber.setText( Messages.get( "$MD11007" ) );
                    Preferences prefs = Preferences.userNodeForPackage( IGeoDesktop.class );
                    cbRemeber.setSelected( prefs.getBoolean( "rememberloging", cbRemeber.isSelected() ) );
                    ftfPassword.setText( prefs.get( "rememberlogingpassword", "" ) );
                    tfUser.setText( prefs.get( "rememberloginguser", "" ) );
                }
            }
            this.setSize( 400, 215 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
