package org.deegree.desktop.views.swing.util;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.main.DeegreeDesktop;
import org.deegree.desktop.views.HelpManager;
import org.deegree.desktop.views.swing.HelpFrame;
import org.deegree.desktop.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class NewReferenceDialog extends JDialog {

    private static final long serialVersionUID = 8447634264252092707L;

    private JPanel pnMessage;

    private JButton btOpen;

    private JButton btHelp;

    private JPanel pnHelp;

    private JButton btCancel;

    private JButton btOK;

    private JPanel pnButtons;

    private JTextField tfURL;

    private JTextArea taMessage;

    private String message;

    private String url;

    private String value;

    private ApplicationContainer<?> appCont;

    /**
     * 
     * @param appCont
     * @param message
     * @param url
     */
    public NewReferenceDialog( ApplicationContainer<?> appCont, String message, String url ) {
        this.appCont = appCont;
        this.message = message;
        this.url = url;
        initGUI();
        setTitle( Messages.getMessage( getLocale(), "$DI10044" ) );
        setModal( true );
        setLocation( 200, 200 );
        setVisible( true );
        toFront();
    }

    private void initGUI() {
        try {
            {
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] { 0.1, 0.0, 0.0, 0.1 };
                thisLayout.rowHeights = new int[] { 7, 111, 47, 7 };
                thisLayout.columnWeights = new double[] { 0.1, 0.0, 0.1 };
                thisLayout.columnWidths = new int[] { 7, 241, 7 };
                getContentPane().setLayout( thisLayout );
                {
                    pnMessage = new JPanel();
                    BorderLayout pnMessageLayout = new BorderLayout();
                    pnMessage.setLayout( pnMessageLayout );
                    getContentPane().add(
                                          pnMessage,
                                          new GridBagConstraints( 0, 0, 3, 2, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnMessage.setBorder( BorderFactory.createTitledBorder( "Message" ) );
                    {
                        taMessage = new JTextArea( message );
                        taMessage.setEditable( false );
                        taMessage.setLineWrap( true );
                        taMessage.setWrapStyleWord( true );
                        taMessage.setBackground( pnMessage.getBackground() );
                        pnMessage.add( taMessage, BorderLayout.CENTER );
                    }
                }
                {
                    tfURL = new JTextField( url );
                    getContentPane().add(
                                          tfURL,
                                          new GridBagConstraints( 0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL, new Insets( 0, 5, 0,
                                                                                                             10 ), 0, 0 ) );
                }
                {
                    btOpen = new JButton( Messages.getMessage( getLocale(), "$DI10045" ),
                                          IconRegistry.getIcon( "open.gif" ) );
                    btOpen.setToolTipText( Messages.getMessage( getLocale(), "$DI10046" ) );
                    getContentPane().add(
                                          btOpen,
                                          new GridBagConstraints( 2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                                                  GridBagConstraints.NONE, new Insets( 0, 0, 0, 5 ), 0,
                                                                  0 ) );
                    btOpen.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent event ) {
                            final Preferences prefs = Preferences.userNodeForPackage( DeegreeDesktop.class );
                            final File file = GenericFileChooser.showOpenDialog(
                                                                                 FILECHOOSERTYPE.externalResource,
                                                                                 appCont,
                                                                                 NewReferenceDialog.this,
                                                                                 prefs,
                                                                                 "*",
                                                                                 DesktopFileFilter.createForExtensions( "*" ) );
                            if ( file != null ) {
                                tfURL.setText( file.getAbsolutePath() );
                            }

                        }
                    } );

                }
                {
                    pnButtons = new JPanel();
                    FlowLayout pnButtonsLayout = new FlowLayout();
                    pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                    getContentPane().add(
                                          pnButtons,
                                          new GridBagConstraints( 0, 3, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnButtons.setLayout( pnButtonsLayout );
                    {
                        btOK = new JButton( Messages.getMessage( getLocale(), "$DI10047" ),
                                            IconRegistry.getIcon( "accept.png" ) );
                        btOK.setToolTipText( Messages.getMessage( getLocale(), "$DI10048" ) );
                        btOK.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent event ) {
                                value = tfURL.getText();
                                dispose();
                            }
                        } );
                        pnButtons.add( btOK );
                    }
                    {
                        btCancel = new JButton( Messages.getMessage( getLocale(), "$DI10049" ),
                                                IconRegistry.getIcon( "cancel.png" ) );
                        btCancel.setToolTipText( Messages.getMessage( getLocale(), "$DI10050" ) );
                        btCancel.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent event ) {
                                dispose();
                                value = null;
                            }
                        } );
                        pnButtons.add( btCancel );
                    }
                }
                {
                    pnHelp = new JPanel();
                    FlowLayout pnHelpLayout = new FlowLayout();
                    pnHelpLayout.setAlignment( FlowLayout.RIGHT );
                    getContentPane().add(
                                          pnHelp,
                                          new GridBagConstraints( 2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnHelp.setLayout( pnHelpLayout );
                    {
                        btHelp = new JButton( Messages.getMessage( getLocale(), "$DI10051" ),
                                              IconRegistry.getIcon( "help.png" ) );
                        btHelp.setToolTipText( Messages.getMessage( getLocale(), "$DI10052" ) );
                        pnHelp.add( btHelp );
                        btHelp.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                HelpFrame hf = HelpFrame.getInstance( new HelpManager( appCont ) );
                                hf.setVisible( true );
                            }
                        } );
                    }
                }
            }
            this.setSize( 404, 239 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @return new URL or <code>null</code> if user has clicked cancel
     */
    public String getAsURL() {
        if ( value != null ) {
            if ( value.toLowerCase().startsWith( "file:" ) || value.toLowerCase().startsWith( "http:" )
                 || value.toLowerCase().startsWith( "ftp:" ) ) {
                return value;
            }

            try {
                return new File( value ).toURI().toURL().toExternalForm();
            } catch ( MalformedURLException e ) {
                return null;
            }
        }
        return null;
    }

    /**
     * 
     * @return new File or <code>null</code> if user has clicked cancel
     */
    public String getAsFile() {
        if ( value != null ) {
            if ( value.toLowerCase().startsWith( "http:" ) || value.toLowerCase().startsWith( "ftp:" ) ) {
                return null;
            }
            if ( value.toLowerCase().startsWith( "file:" ) ) {
                return value.substring( 5, value.length() );
            }
            return value;
        }
        return null;
    }

}
