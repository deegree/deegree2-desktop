package org.deegree.igeo.desktop;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;

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
public class LookAndFeelDialog extends JDialog {
    
    private static final ILogger LOG = LoggerFactory.getLogger( LookAndFeelDialog.class );

    private static final long serialVersionUID = -3601413563920060175L;

    private JPanel pnUIManager;

    private JPanel pnColor;

    private JButton btGBColor;

    private JButton btCancel;

    private JButton btOK;

    private JList lstThemes;

    private JPanel pnButtons;

    private JPanel pnBGColor;

    private JPanel pnTheme;

    private JList lstUIManager;

    private Map<String, String> uiManager = new HashMap<String, String>();

    private Map<String, Class<?>> themes = new HashMap<String, Class<?>>();

    private boolean confirm = false;

    /**
     * 
     * @param frame
     */
    public LookAndFeelDialog( Container container ) {
        initGUI();
        setModal( true );        
        Rectangle rect = container.getBounds();
        setLocation( rect.x + rect.width / 2 - getWidth() / 2, rect.y + rect.height / 2 - getHeight() / 2 );
        setVisible( true );
    }

    private void initGUI() {
        try {
            {
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.1 };
                thisLayout.rowHeights = new int[] { 229, 223, 55, 7 };
                thisLayout.columnWeights = new double[] { 0.0, 0.1 };
                thisLayout.columnWidths = new int[] { 261, 7 };
                getContentPane().setLayout( thisLayout );
                {
                    pnUIManager = new JPanel();
                    GridBagLayout pnUIManagerLayout = new GridBagLayout();
                    pnUIManagerLayout.rowWeights = new double[] { 0.1 };
                    pnUIManagerLayout.rowHeights = new int[] { 7 };
                    pnUIManagerLayout.columnWeights = new double[] { 0.1 };
                    pnUIManagerLayout.columnWidths = new int[] { 7 };
                    pnUIManager.setLayout( pnUIManagerLayout );
                    getContentPane().add(
                                          pnUIManager,
                                          new GridBagConstraints( 0, 0, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 10 ),
                                                                  0, 0 ) );
                    pnUIManager.setBorder( BorderFactory.createTitledBorder( "UI-Manager" ) );
                    {
                        UIManager.LookAndFeelInfo looks[] = UIManager.getInstalledLookAndFeels();
                        for ( int i = 0; i < looks.length; i++ ) {
                            uiManager.put( looks[i].getName(), looks[i].getClassName() );
                        }
                        uiManager.put( "JGoodies Plastic", "com.jgoodies.looks.plastic.PlasticLookAndFeel" );
                        uiManager.put( "JGoodies Plastic 3D", "com.jgoodies.looks.plastic.Plastic3DLookAndFeel" );
                        uiManager.put( "JGoodies Plastic XP", "com.jgoodies.looks.plastic.PlasticXPLookAndFeel" );
                        uiManager.put( "JGoodies Windows", "com.jgoodies.looks.windows.WindowsLookAndFeel" );
                        uiManager.put( "System Look and Feel", UIManager.getSystemLookAndFeelClassName() );
                        ListModel lstUIManagerModel = new DefaultComboBoxModel( uiManager.keySet().toArray() );
                        lstUIManager = new JList();
                        pnUIManager.add( lstUIManager, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                               GridBagConstraints.CENTER,
                                                                               GridBagConstraints.BOTH,
                                                                               new Insets( 10, 10, 10, 10 ), 0, 0 ) );
                        lstUIManager.setModel( lstUIManagerModel );
                        lstUIManager.setPreferredSize( new java.awt.Dimension( 190, 282 ) );
                        lstUIManager.setBorder( BorderFactory.createEmptyBorder( 0, 10, 0, 10 ) );
                        lstUIManager.addListSelectionListener( new ListSelectionListener() {

                            public void valueChanged( ListSelectionEvent e ) {
                                String s = (String) lstUIManager.getSelectedValue();
                                if ( s.startsWith( "JGoodies Plastic" ) ) {
                                    lstThemes.setEnabled( true );
                                    lstThemes.setSelectedIndex( 0 );
                                } else {
                                    lstThemes.setEnabled( false );
                                    lstThemes.removeSelectionInterval( 0, themes.size() - 1 );
                                }

                                try {
                                    UIManager.setLookAndFeel( uiManager.get( s ) );
                                } catch ( Exception e1 ) {
                                    // should never happen
                                    LOG.logWarning( "", e1 );
                                }
                            }

                        } );

                    }
                }
                {
                    pnTheme = new JPanel();
                    GridBagLayout pnThemeLayout = new GridBagLayout();
                    pnThemeLayout.rowWeights = new double[] { 0.1 };
                    pnThemeLayout.rowHeights = new int[] { 7 };
                    pnThemeLayout.columnWeights = new double[] { 0.1 };
                    pnThemeLayout.columnWidths = new int[] { 7 };
                    pnTheme.setLayout( pnThemeLayout );
                    getContentPane().add(
                                          pnTheme,
                                          new GridBagConstraints( 1, 0, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 10, 0, 0 ),
                                                                  0, 0 ) );
                    pnTheme.setBorder( BorderFactory.createTitledBorder( "Themes" ) );
                    {

                        themes.put( "ExperienceBlue", com.jgoodies.looks.plastic.theme.ExperienceBlue.class );
                        themes.put( "ExperienceRoyale", com.jgoodies.looks.plastic.theme.ExperienceRoyale.class );
                        themes.put( "BrownSugar", com.jgoodies.looks.plastic.theme.BrownSugar.class );
                        themes.put( "DarkStar", com.jgoodies.looks.plastic.theme.DarkStar.class );
                        themes.put( "DesertBlue", com.jgoodies.looks.plastic.theme.DesertBlue.class );
                        themes.put( "DesertBluer", com.jgoodies.looks.plastic.theme.DesertBluer.class );
                        themes.put( "DesertGreen", com.jgoodies.looks.plastic.theme.DesertGreen.class );
                        themes.put( "DesertRed", com.jgoodies.looks.plastic.theme.DesertRed.class );
                        themes.put( "DesertYellow", com.jgoodies.looks.plastic.theme.DesertYellow.class );
                        themes.put( "ExperienceGreen", com.jgoodies.looks.plastic.theme.ExperienceGreen.class );
                        themes.put( "InvertedColorTheme", com.jgoodies.looks.plastic.theme.InvertedColorTheme.class );
                        themes.put( "LightGray", com.jgoodies.looks.plastic.theme.LightGray.class );
                        themes.put( "Silver", com.jgoodies.looks.plastic.theme.Silver.class );
                        themes.put( "SkyBlue", com.jgoodies.looks.plastic.theme.SkyBlue.class );
                        themes.put( "SkyBluer", com.jgoodies.looks.plastic.theme.SkyBluer.class );
                        themes.put( "SkyGreen", com.jgoodies.looks.plastic.theme.SkyGreen.class );
                        themes.put( "SkyKrupp", com.jgoodies.looks.plastic.theme.SkyKrupp.class );
                        themes.put( "SkyPink", com.jgoodies.looks.plastic.theme.SkyPink.class );
                        themes.put( "SkyRed", com.jgoodies.looks.plastic.theme.SkyRed.class );
                        themes.put( "SkyYellow", com.jgoodies.looks.plastic.theme.SkyYellow.class );

                        ListModel lstThemesModel = new DefaultComboBoxModel( themes.keySet().toArray() );
                        lstThemes = new JList();
                        lstThemes.setEnabled( false );
                        pnTheme.add( lstThemes, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                        GridBagConstraints.CENTER,
                                                                        GridBagConstraints.BOTH, new Insets( 10, 10,
                                                                                                             10, 10 ),
                                                                        0, 0 ) );
                        lstThemes.setModel( lstThemesModel );
                        lstThemes.setBorder( BorderFactory.createEmptyBorder( 0, 10, 0, 10 ) );
                    }
                }
                {
/*                    
                    pnBGColor = new JPanel();
                    FlowLayout pnBGColorLayout = new FlowLayout();
                    pnBGColorLayout.setAlignment( FlowLayout.LEFT );
                    pnBGColor.setLayout( pnBGColorLayout );
                    getContentPane().add(
                                          pnBGColor,
                                          new GridBagConstraints( 0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    {
                        pnColor = new JPanel();
                        pnBGColor.add( pnColor );
                        pnColor.setPreferredSize( new java.awt.Dimension( 46, 48 ) );
                        pnColor.setBackground( new java.awt.Color( 138, 127, 106 ) );
                    }
                    {
                        btGBColor = new JButton();
                        pnBGColor.add( btGBColor );
                        btGBColor.setText( "select background color" );
                    }
*/                    
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
                        btOK = new JButton( "OK" );
                        pnButtons.add( btOK );
                        btOK.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                confirm = true;
                                dispose();
                            }

                        } );
                    }
                    {
                        btCancel = new JButton( "cancel" );
                        pnButtons.add( btCancel );
                        btCancel.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                confirm = false;
                                dispose();
                            }

                        } );
                    }
                }
            }
            this.setSize( 533, 576 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public String getLookAndfeel() {
        return uiManager.get( lstUIManager.getSelectedValue() );
    }

    public Class<?> getTheme() {
        if ( lstThemes.getSelectedValue() != null ) {
            return themes.get( lstThemes.getSelectedValue() );
        } else {
            return null;
        }
    }

    public boolean confirmed() {
        return confirm;
    }
}
