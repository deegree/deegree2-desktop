package org.deegree.desktop.views.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.io.FileSystemAccess;
import org.deegree.desktop.io.FileSystemAccessFactory;
import org.deegree.desktop.main.DeegreeDesktop;
import org.deegree.desktop.views.swing.util.IconRegistry;
import org.deegree.desktop.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;

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
public class IGeoFileChooser extends JDialog {

    private static final long serialVersionUID = -9152988359802404287L;

    private JPanel pnFileList;

    private JScrollPane scFiles;

    private JList lstFiles;

    private JPanel pnFileFilter;

    private JButton btCancel;

    private JButton btOK;

    private JPanel pnButtons;

    private JComboBox cbFileFilter;

    private DeegreeDesktop appCont;

    private File directory;

    private String extension;

    private int result = JFileChooser.CANCEL_OPTION;

    private JPanel pnFileName;

    private JTextField tfFileName;

    private FILECHOOSERTYPE fileChooserType;

    private boolean open;

    /**
     * 
     * @param appCont
     * @param directory
     * @param extension
     * @param parentClass
     */
    public IGeoFileChooser( DeegreeDesktop appCont, File directory, String extension, FILECHOOSERTYPE fileChooserType,
                            boolean open ) {        
        this.appCont = appCont;
        this.extension = extension;
        this.directory = directory;
        this.fileChooserType = fileChooserType;
        this.open = open;
        initGUI();
    }

    private void initGUI() {
        try {
            {
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.1 };
                thisLayout.rowHeights = new int[] { 58, 37, 221, 7 };
                thisLayout.columnWeights = new double[] { 0.0, 0.0, 0.1, 0.1 };
                thisLayout.columnWidths = new int[] { 113, 139, 7, 7 };
                getContentPane().setLayout( thisLayout );
                {
                    pnFileList = new JPanel();
                    BorderLayout pnFileListLayout = new BorderLayout();
                    pnFileList.setLayout( pnFileListLayout );
                    getContentPane().add(
                                          pnFileList,
                                          new GridBagConstraints( 0, 0, 2, 4, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnFileList.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                 "$DI10057" ) ) );
                    {
                        scFiles = new JScrollPane();
                        pnFileList.add( scFiles, BorderLayout.CENTER );
                        {
                            FileSystemAccessFactory fsaf = FileSystemAccessFactory.getInstance( appCont );
                            FileSystemAccess fsa = fsaf.getFileSystemAccess( fileChooserType );
                            File[] files = fsa.listDirectory( directory, extension );
                            String[] fileNames = new String[files.length];
                            for ( int i = 0; i < files.length; i++ ) {
                                fileNames[i] = files[i].getPath();
                            }
                            lstFiles = new JList( fileNames );
                            scFiles.setViewportView( lstFiles );
                            lstFiles.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
                            lstFiles.addListSelectionListener( new ListSelectionListener() {

                                public void valueChanged( ListSelectionEvent e ) {
                                    tfFileName.setText( lstFiles.getSelectedValue().toString() );
                                }

                            } );
                        }
                    }
                }
                {
                    pnFileFilter = new JPanel();
                    FlowLayout pnFileFilterLayout = new FlowLayout();
                    getContentPane().add(
                                          pnFileFilter,
                                          new GridBagConstraints( 2, 0, 2, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnFileFilter.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),"$DI10058" ) ) );
                    pnFileFilter.setLayout( pnFileFilterLayout );
                    {
                        //String[] m = new String[] { "*.prj", "*.mdx", "*.shp", "*.xml", "*.gml" };
                        String[] m = new String[] { "*.*" };
                        cbFileFilter = new JComboBox( m );
                        pnFileFilter.add( cbFileFilter );
                        cbFileFilter.setPreferredSize( new java.awt.Dimension( 163, 22 ) );
                    }
                }
                {
                    pnButtons = new JPanel();
                    GridBagLayout pnButtonsLayout = new GridBagLayout();
                    getContentPane().add(
                                          pnButtons,
                                          new GridBagConstraints( 2, 3, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnButtons.setLayout( pnButtonsLayout );
                    {
                        btOK = new JButton();
                        pnButtons.add( btOK, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                     GridBagConstraints.HORIZONTAL,
                                                                     new Insets( 0, 20, 0, 20 ), 0, 0 ) );
                        if ( open ) {
                            btOK.setText( Messages.getMessage( getLocale(),"$DI10059" ) );
                            btOK.setIcon( IconRegistry.getIcon( "projectOpen.png" ) );
                        } else {
                            btOK.setText( Messages.getMessage( getLocale(),"$DI10060" ) );
                            btOK.setIcon( IconRegistry.getIcon( "save.gif" ) );
                        }
                        btOK.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                result = JFileChooser.APPROVE_OPTION;
                                IGeoFileChooser.this.dispose();
                            }

                        } );
                    }
                    {
                        btCancel = new JButton( Messages.getMessage( getLocale(),"$DI10061" ), IconRegistry.getIcon( "cancel.png" ) );
                        pnButtons.add( btCancel, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                                                         GridBagConstraints.CENTER,
                                                                         GridBagConstraints.HORIZONTAL,
                                                                         new Insets( 0, 20, 0, 20 ), 0, 0 ) );
                        btCancel.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                result = JFileChooser.CANCEL_OPTION;
                                IGeoFileChooser.this.dispose();
                            }

                        } );
                    }
                    pnButtonsLayout.rowWeights = new double[] { 0.1, 0.1 };
                    pnButtonsLayout.rowHeights = new int[] { 7, 7 };
                    pnButtonsLayout.columnWeights = new double[] { 0.1 };
                    pnButtonsLayout.columnWidths = new int[] { 7 };
                }
                {
                    pnFileName = new JPanel();
                    FlowLayout pnFileNameLayout = new FlowLayout();
                    getContentPane().add(
                                          pnFileName,
                                          new GridBagConstraints( 2, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnFileName.setLayout( pnFileNameLayout );
                    pnFileName.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),"$DI10062" ) ) );
                    {
                        tfFileName = new JTextField();
                        pnFileName.add( tfFileName );
                        tfFileName.setPreferredSize( new java.awt.Dimension( 163, 22 ) );
                    }
                }
            }
            this.setSize( 451, 425 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        int x = appCont.getMainWndow().getLocation().x;
        int y = appCont.getMainWndow().getLocation().y;
        int w = appCont.getMainWndow().getSize().width;
        int h = appCont.getMainWndow().getSize().height;
        x = x + w / 2 - getSize().width / 2;
        y = y + h / 2 - getSize().height / 2;
        setLocation( x, y );
    }

    public int showOpenDialog() {
        setModal( true );
        setVisible( true );
        return result;
    }

    /**
     * @return selected list entry as {@link File}
     */
    public File getSelectedFile() {
        if ( result == JFileChooser.APPROVE_OPTION ) {
            String s = tfFileName.getText().trim();
            if ( !s.toLowerCase().endsWith( "." + extension.toLowerCase() ) && !extension.equalsIgnoreCase( "#any" ) ) {
                s += ( "." + extension );
            }
            return new File( s );
        } else {
            return null;
        }
    }
}
