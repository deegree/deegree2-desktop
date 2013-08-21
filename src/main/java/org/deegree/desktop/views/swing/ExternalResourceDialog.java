package org.deegree.desktop.views.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.desktop.views.swing.util.IconRegistry;

/**
 * 
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class ExternalResourceDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 8357351448549507959L;

    private JPanel pnDescription;

    private JLabel jLabel2;

    private JLabel jLabel3;

    private JLabel jLabel4;

    private JTextPane tpDescription;

    private JButton btValidate;

    private JPanel pnURL;

    private JTextField layerResourceURL;

    private JComboBox cbType;

    private JTextPane tpAbstract;

    private JTextField tfTitle;

    private JLabel jLabel1;

    private JButton btCancel;

    private JButton btOK;

    private JPanel pnButtons;

    private boolean state;

    /**
     * 
     * @param parent
     */
    public ExternalResourceDialog( Window parent ) {
        initGUI();
        setModal( true );
        int x = parent.getX() + parent.getWidth() / 2 - getWidth() / 2;
        int y = parent.getY() + parent.getHeight() / 2 - getHeight() / 2;
        setLocation( x, y );
        setVisible( true );
        setAlwaysOnTop( true );
    }

    private void initGUI() {
        try {
            {
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.1 };
                thisLayout.rowHeights = new int[] { 33, 130, 36, 72, 7 };
                thisLayout.columnWeights = new double[] { 0.0, 0.0, 0.1, 0.1 };
                thisLayout.columnWidths = new int[] { 167, 113, 7, 7 };
                getContentPane().setLayout( thisLayout );
                {
                    pnDescription = new JPanel();
                    BorderLayout pnDescriptionLayout = new BorderLayout();
                    getContentPane().add( pnDescription,
                                          new GridBagConstraints( 0, 0, 1, 4, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnDescription.setLayout( pnDescriptionLayout );
                    pnDescription.setBorder( BorderFactory.createTitledBorder( Messages.get( "$MD11017" ) ) );
                    {
                        tpDescription = new JTextPane();
                        tpDescription.setText( Messages.get( "$MD11016" ) );
                        tpDescription.setEditable( false );
                        pnDescription.add( tpDescription, BorderLayout.CENTER );
                        tpDescription.setBackground( getBackground() );
                    }
                }
                {
                    pnButtons = new JPanel();
                    FlowLayout pnButtonsLayout = new FlowLayout();
                    pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                    pnButtons.setLayout( pnButtonsLayout );
                    getContentPane().add( pnButtons,
                                          new GridBagConstraints( 0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    {
                        btOK = new JButton( Messages.get( "$MD11018" ), IconRegistry.getIcon( "accept.png" ) );
                        btOK.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                state = true;
                                dispose();
                            }

                        } );
                        pnButtons.add( btOK );
                    }
                    {
                        btCancel = new JButton( Messages.get( "$MD11019" ), IconRegistry.getIcon( "cancel.png" ) );
                        btCancel.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                state = false;
                                dispose();
                            }

                        } );
                        pnButtons.add( btCancel );
                    }
                }
                {
                    jLabel1 = new JLabel( Messages.get( "$MD11020" ) );
                    getContentPane().add( jLabel1,
                                          new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0,
                                                                                                             0 ), 0, 0 ) );
                }
                {
                    tfTitle = new JTextField();
                    getContentPane().add( tfTitle,
                                          new GridBagConstraints( 2, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0,
                                                                                                             10 ), 0, 0 ) );
                }
                {
                    jLabel2 = new JLabel( Messages.get( "$MD11021" ) );
                    getContentPane().add( jLabel2,
                                          new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                                                  GridBagConstraints.HORIZONTAL, new Insets( 10, 10, 0,
                                                                                                             0 ), 0, 0 ) );
                }
                {
                    tpAbstract = new JTextPane();
                    getContentPane().add( tpAbstract,
                                          new GridBagConstraints( 2, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 10, 0, 10, 10 ),
                                                                  0, 0 ) );
                }
                {
                    jLabel3 = new JLabel( Messages.get( "$MD11022" ) );
                    getContentPane().add( jLabel3,
                                          new GridBagConstraints( 1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0,
                                                                                                             0 ), 0, 0 ) );
                }
                {
                    ComboBoxModel cbTypeModel = new DefaultComboBoxModel( new String[] { "text/html", "image/gif",
                                                                                        "image/png", "image/tiff",
                                                                                        "image/bmp", "image/jpeg" } );
                    cbType = new JComboBox( cbTypeModel );
                    cbType.setEditable( true );
                    getContentPane().add( cbType,
                                          new GridBagConstraints( 2, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0,
                                                                                                             10 ), 0, 0 ) );
                    cbType.setEditable( true );
                }
                {
                    jLabel4 = new JLabel( Messages.get( "$MD11023" ) );
                    getContentPane().add( jLabel4,
                                          new GridBagConstraints( 1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                                                  GridBagConstraints.HORIZONTAL, new Insets( 10, 10, 0,
                                                                                                             0 ), 0, 0 ) );
                }
                {
                    pnURL = new JPanel();
                    GridBagLayout pnURLLayout = new GridBagLayout();
                    getContentPane().add( pnURL,
                                          new GridBagConstraints( 2, 3, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnURLLayout.rowWeights = new double[] { 0.1, 0.1 };
                    pnURLLayout.rowHeights = new int[] { 7, 7 };
                    pnURLLayout.columnWeights = new double[] { 0.1 };
                    pnURLLayout.columnWidths = new int[] { 7 };
                    pnURL.setLayout( pnURLLayout );
                    {
                        layerResourceURL = new JTextField();
                        pnURL.add( layerResourceURL,
                                   new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                           GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 10 ), 0,
                                                           0 ) );
                    }
                    {
                        btValidate = new JButton();
                        btValidate.setText( Messages.get( "$MD11024" ) );
                        btValidate.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                validateURL( layerResourceURL.getText() );
                            }

                        } );
                        pnURL.add( btValidate, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                                                       GridBagConstraints.NONE,
                                                                       new Insets( 0, 0, 0, 10 ), 0, 0 ) );
                    }
                }
            }
            this.setSize( 585, 336 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void validateURL( String url ) {
        if ( isURLValid( url ) ) {
            DialogFactory.openInformationDialog( "application", getParent(), Messages.get( "$MD11025" ),
                                                 Messages.get( "$MD11026" ) );
        } else {
            DialogFactory.openErrorDialog( "application", getParent(), Messages.get( "$MD11026" ),
                                           Messages.get( "$MD11028" ), null );
        }
    }

    /**
     * checks if the passed URL is valid.
     * 
     * @param urlToValidate
     *            the url to validate
     * @return true if the url is valid, false otherwise
     * @throws MalformedURLException
     */
    boolean isURLValid( String urlToValidate ) {
        try {
            new URL( urlToValidate );
            return true;
        } catch ( MalformedURLException e ) {
            return false;
        }
    }

    /**
     * 
     * @return
     */
    public boolean isCanceled() {
        return !state;
    }

    public String[] getValues() {
        String[] values = new String[5];
        values[0] = tfTitle.getText();
        values[1] = tpAbstract.getText();
        values[2] = cbType.getSelectedItem().toString();
        values[3] = layerResourceURL.getText();
        values[4] = "go to";
        return values;
    }

}
