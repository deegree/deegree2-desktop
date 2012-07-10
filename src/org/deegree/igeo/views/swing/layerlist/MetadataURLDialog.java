package org.deegree.igeo.views.swing.layerlist;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.deegree.framework.xml.XMLFragment;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.views.DialogFactory;
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
public class MetadataURLDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 8273519842169999761L;

    private JPanel pnButtons;

    private JPanel pnDescription;

    private JPanel jPanel1;

    private JButton btValidate;

    private JTextField tfMetadataURL;

    private JPanel pnCheck;

    private JButton btCancel;

    private JButton btOK;

    private JPanel pnTextfield;

    private JTextArea tpDescription;

    /**
     * 
     * @param parent
     */
    public MetadataURLDialog( JFrame parent ) {
        super( parent );
        initGUI();
        setModal( true );
        int x = parent.getX() + parent.getWidth() / 2 - getWidth() / 2;
        int y = parent.getY() + parent.getHeight() / 2 - getHeight() / 2;
        setLocation( x, y );
        setVisible( true );
    }

    private void initGUI() {
        try {
            {
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] { 0.0, 0.1 };
                thisLayout.rowHeights = new int[] { 155, 7 };
                thisLayout.columnWeights = new double[] { 0.0, 0.1, 0.1 };
                thisLayout.columnWidths = new int[] { 135, 7, 7 };
                getContentPane().setLayout( thisLayout );
                {
                    pnButtons = new JPanel();
                    FlowLayout pnButtonsLayout = new FlowLayout();
                    pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                    pnButtons.setLayout( pnButtonsLayout );
                    getContentPane().add(
                                          pnButtons,
                                          new GridBagConstraints( 0, 2, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    {
                        btOK = new JButton( IconRegistry.getIcon( "accept.png" ) );
                        btOK.setText( Messages.get( "$MD11008" ) );
                        btOK.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                dispose();
                            }
                        } );
                        pnButtons.add( btOK );
                    }
                    {
                        btCancel = new JButton( IconRegistry.getIcon( "cancel.png" ) );
                        btCancel.setText( Messages.get( "$MD11009" ) );
                        btCancel.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                tfMetadataURL.setText( "" );
                                dispose();
                            }

                        } );
                        pnButtons.add( btCancel );
                    }
                }
                {
                    pnDescription = new JPanel();
                    BorderLayout pnDescriptionLayout = new BorderLayout();
                    getContentPane().add(
                                          pnDescription,
                                          new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnDescription.setLayout( pnDescriptionLayout );
                    pnDescription.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                    "$MD11332" ) ) );
                    {
                        tpDescription = new JTextArea();
                        tpDescription.setText( Messages.getMessage( getLocale(), "$MD11011" ) );
                        tpDescription.setEditable( false );
                        tpDescription.setLineWrap( true );
                        tpDescription.setWrapStyleWord( true );
                        pnDescription.add( tpDescription, BorderLayout.CENTER );
                        tpDescription.setBackground( pnDescription.getBackground() );
                    }
                }
                {
                    pnTextfield = new JPanel();
                    GridBagLayout pnTextfieldLayout = new GridBagLayout();
                    getContentPane().add(
                                          pnTextfield,
                                          new GridBagConstraints( 1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnTextfieldLayout.rowWeights = new double[] { 0.1, 0.1 };
                    pnTextfieldLayout.rowHeights = new int[] { 7, 20 };
                    pnTextfieldLayout.columnWeights = new double[] { 0.1 };
                    pnTextfieldLayout.columnWidths = new int[] { 7 };
                    pnTextfield.setLayout( pnTextfieldLayout );
                    {
                        pnCheck = new JPanel();
                        GridBagLayout pnCheckLayout = new GridBagLayout();
                        pnTextfield.add( pnCheck, new GridBagConstraints( 0, 0, 1, 2, 0.0, 0.0,
                                                                          GridBagConstraints.CENTER,
                                                                          GridBagConstraints.BOTH, new Insets( 0, 0, 0,
                                                                                                               0 ), 0,
                                                                          0 ) );
                        pnCheck.setEnabled( false );
                        pnCheck.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                  "$MD11333" ) ) );
                        pnCheckLayout.rowWeights = new double[] { 0.1, 0.1 };
                        pnCheckLayout.rowHeights = new int[] { 7, 7 };
                        pnCheckLayout.columnWeights = new double[] { 0.1 };
                        pnCheckLayout.columnWidths = new int[] { 7 };
                        pnCheck.setLayout( pnCheckLayout );
                        {
                            tfMetadataURL = new JTextField();
                            pnCheck.add( tfMetadataURL, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                                GridBagConstraints.CENTER,
                                                                                GridBagConstraints.HORIZONTAL,
                                                                                new Insets( 0, 10, 0, 10 ), 0, 0 ) );
                        }
                        {
                            jPanel1 = new JPanel();
                            FlowLayout jPanel1Layout = new FlowLayout();
                            jPanel1Layout.setAlignment( FlowLayout.RIGHT );
                            jPanel1Layout.setVgap( 10 );
                            jPanel1Layout.setHgap( 10 );
                            pnCheck.add( jPanel1, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                                                          GridBagConstraints.CENTER,
                                                                          GridBagConstraints.BOTH, new Insets( 0, 0, 0,
                                                                                                               0 ), 0,
                                                                          0 ) );
                            jPanel1.setLayout( jPanel1Layout );
                            {
                                btValidate = new JButton();
                                btValidate.addActionListener( new ActionListener() {

                                    public void actionPerformed( ActionEvent e ) {
                                        try {
                                            URL url = new URL( tfMetadataURL.getText() );
                                            new XMLFragment( url );
                                            DialogFactory.openInformationDialog( "application", getParent(),
                                                                                 Messages.get( "$MD11012" ),
                                                                                 Messages.get( "$MD11013" ) );
                                        } catch ( Exception ex ) {
                                            ex.printStackTrace();
                                            DialogFactory.openErrorDialog( "application", getParent(),
                                                                           Messages.get( "$MD11014" ),
                                                                           Messages.get( "$MD11015" ), ex );
                                        }
                                    }

                                } );
                                jPanel1.add( btValidate );
                                btValidate.setPreferredSize( new java.awt.Dimension( 143, 18 ) );
                                btValidate.setText( Messages.get( "$MD11010" ) );
                            }
                        }
                    }
                }
            }
            this.setSize( 400, 220 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @return metadata URL
     */
    String getMetadataURL() {
        return tfMetadataURL.getText();
    }

}
