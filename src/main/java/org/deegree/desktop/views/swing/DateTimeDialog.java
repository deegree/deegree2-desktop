package org.deegree.desktop.views.swing;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class DateTimeDialog extends javax.swing.JDialog {

    {
        // Set Look & Feel
        try {
            javax.swing.UIManager.setLookAndFeel( "com.jgoodies.looks.plastic.Plastic3DLookAndFeel" );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private static final long serialVersionUID = 6922334824758441722L;

    private DatePanel datePanel;

    private JPanel pnTime;

    private JLabel lbTime;

    private JSpinner spMinutes;

    private JLabel lb1;

    private JSpinner spHour;

    private JButton btCancel;

    private JButton btOK;

    private JPanel pnButtons;

    private String timestamp;

    /**
     * Auto-generated main method to display this JDialog
     */
    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                JFrame frame = new JFrame();
                DateTimeDialog inst = new DateTimeDialog( frame );
                inst.setVisible( true );
            }
        } );
    }

    public DateTimeDialog( JFrame frame ) {
        super( frame );
        initGUI();
    }

    public DateTimeDialog() {
        initGUI();
        setModal( true );
        setVisible( true );
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.1 };
            thisLayout.rowHeights = new int[] { 215, 50, 7 };
            thisLayout.columnWeights = new double[] { 0.1 };
            thisLayout.columnWidths = new int[] { 7 };
            getContentPane().setLayout( thisLayout );
            {
                datePanel = new DatePanel();
                getContentPane().add(
                                      datePanel,
                                      new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                              GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
            }
            {
                pnButtons = new JPanel();
                FlowLayout pnButtonsLayout = new FlowLayout();
                pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                pnButtons.setLayout( pnButtonsLayout );
                getContentPane().add(
                                      pnButtons,
                                      new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                              GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                {
                    btOK = new JButton();
                    pnButtons.add( btOK );
                    btOK.setText( "OK" );
                    btOK.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            int h = ( (Number) spHour.getValue() ).intValue();
                            int mn = ( (Number) spMinutes.getValue() ).intValue();
                            String sh = Integer.toString( h );
                            if ( h < 10 ) {
                                sh = '0' + sh;
                            }
                            String sm = Integer.toString( mn );
                            if ( mn < 10 ) {
                                sm = '0' + sm;
                            }
                            timestamp = datePanel.getYear() + "-" + datePanel.getMonth() + '-' + datePanel.getDay()
                                       + 'T' + sh + ':' + sm;
                            dispose();
                        }
                    } );
                }
                {
                    btCancel = new JButton();
                    pnButtons.add( btCancel );
                    btCancel.setText( "cancel" );
                    btCancel.addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            dispose();
                        }
                    } );
                }
            }
            {
                pnTime = new JPanel();
                FlowLayout pnTimeLayout = new FlowLayout();
                pnTimeLayout.setAlignment( FlowLayout.LEFT );
                pnTime.setLayout( pnTimeLayout );
                getContentPane().add(
                                      pnTime,
                                      new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                              GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnTime.setBorder( BorderFactory.createEmptyBorder( 10, 0, 0, 0 ) );
                {
                    lbTime = new JLabel();
                    pnTime.add( lbTime );
                    lbTime.setText( "time:" );
                    lbTime.setBorder( BorderFactory.createEmptyBorder( 0, 10, 0, 10 ) );
                }
                Calendar cal = new GregorianCalendar();
                {
                    spHour = new JSpinner( new SpinnerNumberModel( cal.get( Calendar.HOUR_OF_DAY ) + 1, 0, 23, 1 ) );
                    pnTime.add( spHour );
                    spHour.setPreferredSize( new java.awt.Dimension( 48, 22 ) );
                }
                {
                    lb1 = new JLabel();
                    pnTime.add( lb1 );
                    lb1.setText( " : " );
                }
                {
                    spMinutes = new JSpinner( new SpinnerNumberModel( cal.get( Calendar.MINUTE ), 0, 59, 1 ) );
                    pnTime.add( spMinutes );
                    spMinutes.setPreferredSize( new java.awt.Dimension( 47, 22 ) );
                }
            }
            this.setSize( 297, 339 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @return ISO 8601 formatted current selected time
     */
    public String getIsoFormattedTimestamp() {
        return timestamp;
    }

}
