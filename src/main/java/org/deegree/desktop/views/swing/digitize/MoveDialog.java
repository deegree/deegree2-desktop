package org.deegree.desktop.views.swing.digitize;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.views.HelpManager;
import org.deegree.desktop.views.swing.HelpFrame;
import org.deegree.desktop.views.swing.util.IconRegistry;
import org.deegree.framework.util.Pair;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class MoveDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 4034735849126833425L;

    private JPanel pnButtons;

    private JButton btCancel;

    private JPanel pnHelp;

    private JButton btHelp;

    private JPanel pnDescription;

    private JLabel lbX;

    private JTextArea taDescription;

    private JSpinner spY;

    private JSpinner spX;

    private JLabel lbY;

    private JPanel pnParams;

    private JButton btOK;

    private ApplicationContainer<Container> appCont;

    private Pair<Double, Double> distance;

    /**
     * 
     * @param parent
     * @param appCont
     */
    public MoveDialog( Container parent, ApplicationContainer<Container> appCont ) {
        setTitle( Messages.getMessage( getLocale(), "$MD11254" ) );
        this.appCont = appCont;
        initGUI();
        setLocation( parent.getX() + 150, parent.getY() + 150 );
        setAlwaysOnTop( true );
        setModal( true );
        setVisible( true );        
    }

    private void initGUI() {
        try {
            {
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] { 0.0, 0.1 };
                thisLayout.rowHeights = new int[] { 232, 7 };
                thisLayout.columnWeights = new double[] { 0.0, 0.1 };
                thisLayout.columnWidths = new int[] { 158, 7 };
                getContentPane().setLayout( thisLayout );
                {
                    pnButtons = new JPanel();
                    FlowLayout pnButtonsLayout = new FlowLayout();
                    pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                    pnButtons.setLayout( pnButtonsLayout );
                    getContentPane().add(
                                          pnButtons,
                                          new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    {
                        btOK = new JButton( Messages.getMessage( getLocale(), "$MD11255" ),
                                            IconRegistry.getIcon( "accept.png" ) );
                        pnButtons.add( btOK );
                        btOK.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent event ) {
                                Double x = ( (Number) ( (SpinnerNumberModel) spX.getModel() ).getValue() ).doubleValue();
                                Double y = ( (Number) ( (SpinnerNumberModel) spY.getModel() ).getValue() ).doubleValue();
                                distance = new Pair<Double, Double>( x, y );
                                MoveDialog.this.dispose();
                            }
                        } );
                    }
                    {
                        btCancel = new JButton( Messages.getMessage( getLocale(), "$MD11256" ),
                                                IconRegistry.getIcon( "cancel.png" ) );
                        pnButtons.add( btCancel );
                        btCancel.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent event ) {
                                distance = null;
                                MoveDialog.this.dispose();
                            }
                        } );
                    }
                }
                {
                    pnHelp = new JPanel();
                    FlowLayout pnHelpLayout = new FlowLayout();
                    pnHelpLayout.setAlignment( FlowLayout.RIGHT );
                    pnHelp.setLayout( pnHelpLayout );
                    getContentPane().add(
                                          pnHelp,
                                          new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    {
                        btHelp = new JButton( Messages.getMessage( getLocale(), "$MD11257" ),
                                              IconRegistry.getIcon( "help.png" ) );
                        pnHelp.add( btHelp );
                        btHelp.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent event ) {
                                HelpFrame hf = HelpFrame.getInstance( new HelpManager( appCont ) );
                                hf.setVisible( true );
                                hf.toFront();
                                hf.gotoModule( "Digitizer" );
                            }
                        } );
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
                    pnDescription.setBorder( BorderFactory.createTitledBorder( null, Messages.getMessage( getLocale(),
                                                                                                          "$MD11258" ),
                                                                               TitledBorder.LEADING,
                                                                               TitledBorder.DEFAULT_POSITION ) );
                    {
                        taDescription = new JTextArea();
                        taDescription.setBackground( pnDescription.getBackground() );
                        taDescription.setWrapStyleWord( true );
                        taDescription.setLineWrap( true );
                        taDescription.setEditable( false );
                        pnDescription.add( taDescription, BorderLayout.CENTER );
                        taDescription.setPreferredSize( new Dimension( 130, 222 ) );
                        taDescription.setText( Messages.getMessage( getLocale(), "$MD11259" ) );
                    }
                }
                {
                    pnParams = new JPanel();
                    GridBagLayout pnParamsLayout = new GridBagLayout();
                    getContentPane().add(
                                          pnParams,
                                          new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnParamsLayout.rowWeights = new double[] { 0.0, 0.0, 0.1, 0.1 };
                    pnParamsLayout.rowHeights = new int[] { 44, 44, 7, 7 };
                    pnParamsLayout.columnWeights = new double[] { 0.0, 0.1 };
                    pnParamsLayout.columnWidths = new int[] { 94, 7 };
                    pnParams.setLayout( pnParamsLayout );
                    {
                        lbX = new JLabel( Messages.getMessage( getLocale(), "$MD11334" ) );
                        pnParams.add( lbX,
                                      new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                              GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0, 0 ),
                                                              0, 0 ) );
                    }
                    {
                        lbY = new JLabel( Messages.getMessage( getLocale(), "$MD11335" )  );
                        pnParams.add( lbY,
                                      new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                              GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0, 0 ),
                                                              0, 0 ) );
                    }
                    {
                        spX = new JSpinner( new SpinnerNumberModel( 0, -9E99, 9E99, 0.5 ) );
                        pnParams.add( spX, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                   GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0,
                                                                                                              10 ), 0,
                                                                   0 ) );
                    }
                    {
                        spY = new JSpinner( new SpinnerNumberModel( 0, -9E99, 9E99, 0.5 ) );
                        pnParams.add( spY, new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                   GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0,
                                                                                                              10 ), 0,
                                                                   0 ) );
                    }
                }
            }
            setSize( 400, 300 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @return distance in x/y direction
     */
    Pair<Double, Double> getDistance() {
        return distance;
    }

}
