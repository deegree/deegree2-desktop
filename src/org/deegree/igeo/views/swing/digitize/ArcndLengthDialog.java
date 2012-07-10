package org.deegree.igeo.views.swing.digitize;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.modules.DigitizerModule;
import org.deegree.igeo.views.HelpManager;
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
public class ArcndLengthDialog extends JDialog {

    private static final long serialVersionUID = -1003486323527846964L;

    private JPanel pnDescription;

    private JPanel pnParam;

    private JSpinner spLength;

    private JLabel lbLength;

    private JCheckBox cbLength;

    private JSpinner spAngle;

    private JLabel lbAngle;

    private JButton btHelp;

    private JButton btClose;

    private JButton btTake;

    private JTextArea taDescription;

    private JPanel pnHelp;

    private JPanel pnButtons;

    private ApplicationContainer<Container> appCont;

    /**
     * 
     * @param parent
     * @param owner
     */
    public ArcndLengthDialog( Container parent, DigitizerModule<Container> owner ) {        
        this.appCont = owner.getApplicationContainer();
        setTitle( Messages.getMessage( getLocale(), "$MD11274" ) );
        initGUI();
        setLocation( parent.getX() + 150, parent.getY() + 150 );
        setVisible( true );
        toFront();
        setAlwaysOnTop( true );
        appCont.setInstanceSetting( DigitizerModule.LENGTH, -1 );
        appCont.setInstanceSetting( DigitizerModule.ANGLE, -1 );
        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosed( WindowEvent e ) {
                appCont.setInstanceSetting( DigitizerModule.LENGTH, -1 );
                appCont.setInstanceSetting( DigitizerModule.ANGLE, -1 );
            }

            @Override
            public void windowClosing( WindowEvent e ) {
                appCont.setInstanceSetting( DigitizerModule.LENGTH, -1 );
                appCont.setInstanceSetting( DigitizerModule.ANGLE, -1 );
            }
        } );
    }

    private void initGUI() {
        try {
            {
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] { 0.0, 0.1 };
                thisLayout.rowHeights = new int[] { 190, 7 };
                thisLayout.columnWeights = new double[] { 0.0, 0.0, 0.1 };
                thisLayout.columnWidths = new int[] { 163, 97, 20 };
                getContentPane().setLayout( thisLayout );
                {
                    pnDescription = new JPanel();
                    BorderLayout pnDescriptionLayout = new BorderLayout();
                    pnDescription.setLayout( pnDescriptionLayout );
                    getContentPane().add(
                                          pnDescription,
                                          new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnDescription.setBorder( BorderFactory.createTitledBorder( null, Messages.getMessage( getLocale(),
                                                                                                          "$MD11283" ),
                                                                               TitledBorder.LEADING,
                                                                               TitledBorder.DEFAULT_POSITION ) );
                    {
                        taDescription = new JTextArea();
                        taDescription.setText( Messages.getMessage( getLocale(), "$MD11284" ) );
                        taDescription.setWrapStyleWord( true );
                        taDescription.setLineWrap( true );
                        taDescription.setBackground( pnDescription.getBackground() );
                        taDescription.setEditable( false );
                        pnDescription.add( taDescription, BorderLayout.CENTER );
                    }
                }
                {
                    pnButtons = new JPanel();
                    FlowLayout pnButtonsLayout = new FlowLayout();
                    pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                    pnButtons.setLayout( pnButtonsLayout );
                    getContentPane().add(
                                          pnButtons,
                                          new GridBagConstraints( 0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    {
                        btTake = new JButton( Messages.getMessage( getLocale(), "$MD11275" ) );
                        btTake.setToolTipText( Messages.getMessage( getLocale(), "$MD11276" ) );
                        btTake.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                double value = ( (Number) spAngle.getValue() ).doubleValue() + 180;
                                appCont.setInstanceSetting( DigitizerModule.ANGLE, value );
                                if ( cbLength.isSelected() ) {
                                    value = ( (Number) spLength.getValue() ).doubleValue();
                                } else {
                                    value = -1;
                                }
                                appCont.setInstanceSetting( DigitizerModule.LENGTH, value );
                            }
                        } );
                        pnButtons.add( btTake );
                    }
                    {
                        btClose = new JButton( Messages.getMessage( getLocale(), "$MD11277" ) );
                        btClose.setToolTipText( Messages.getMessage( getLocale(), "$MD11278" ) );
                        btClose.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                ArcndLengthDialog.this.dispose();
                            }
                        } );
                        pnButtons.add( btClose );
                    }
                }
                {
                    pnParam = new JPanel();
                    GridBagLayout pnParamLayout = new GridBagLayout();
                    pnParamLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.1 };
                    pnParamLayout.rowHeights = new int[] { 44, 41, 43, 7 };
                    pnParamLayout.columnWeights = new double[] { 0.0, 0.1 };
                    pnParamLayout.columnWidths = new int[] { 106, 7 };
                    pnParam.setLayout( pnParamLayout );
                    getContentPane().add(
                                          pnParam,
                                          new GridBagConstraints( 1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnParam.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11285" ) ) );
                    {
                        lbAngle = new JLabel( Messages.getMessage( getLocale(), "$MD11279" ) );
                        pnParam.add( lbAngle, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                      GridBagConstraints.HORIZONTAL,
                                                                      new Insets( 0, 10, 0, 0 ), 0, 0 ) );
                    }
                    {
                        spAngle = new JSpinner( new SpinnerNumberModel( 0, -180, 180, 0.1 ) );
                        pnParam.add( spAngle, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                      GridBagConstraints.HORIZONTAL,
                                                                      new Insets( 0, 0, 0, 10 ), 0, 0 ) );
                    }
                    {
                        cbLength = new JCheckBox( Messages.getMessage( getLocale(), "$MD11280" ) );
                        pnParam.add( cbLength, new GridBagConstraints( 0, 1, 2, 1, 0.0, 0.0,
                                                                       GridBagConstraints.SOUTHEAST,
                                                                       GridBagConstraints.HORIZONTAL,
                                                                       new Insets( 0, 10, 0, 0 ), 0, 0 ) );
                    }
                    {
                        lbLength = new JLabel();
                        pnParam.add( lbLength, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                       GridBagConstraints.HORIZONTAL,
                                                                       new Insets( 0, 10, 0, 0 ), 0, 0 ) );
                        lbLength.setText( Messages.getMessage( getLocale(), "$MD11281" ) );
                    }
                    {
                        spLength = new JSpinner( new SpinnerNumberModel( 10, 0, 9E99, 0.1 ) );
                        pnParam.add( spLength, new GridBagConstraints( 1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                       GridBagConstraints.HORIZONTAL,
                                                                       new Insets( 0, 0, 0, 10 ), 0, 0 ) );
                    }
                }
                {
                    pnHelp = new JPanel();
                    FlowLayout pnHelpLayout = new FlowLayout();
                    pnHelpLayout.setAlignment( FlowLayout.RIGHT );
                    pnHelp.setLayout( pnHelpLayout );
                    getContentPane().add(
                                          pnHelp,
                                          new GridBagConstraints( 2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    {
                        btHelp = new JButton( Messages.getMessage( getLocale(), "$MD11282" ),
                                              IconRegistry.getIcon( "help.png" ) );
                        btHelp.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent event ) {
                                HelpFrame hf = HelpFrame.getInstance( new HelpManager( appCont ) );
                                hf.setVisible( true );
                                hf.toFront();
                                hf.gotoModule( "Digitizer" );
                            }
                        } );
                        pnHelp.add( btHelp );
                    }
                }
            }
            this.setSize( 400, 257 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
