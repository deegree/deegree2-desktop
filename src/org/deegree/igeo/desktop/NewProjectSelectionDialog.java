//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2008 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/
package org.deegree.igeo.desktop;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;

import org.deegree.crs.configuration.CRSConfiguration;
import org.deegree.crs.configuration.CRSProvider;
import org.deegree.framework.file.FileMemory;
import org.deegree.framework.util.GeometryUtils;
import org.deegree.framework.util.ImageUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.io.FileSystemAccess;
import org.deegree.igeo.io.FileSystemAccessFactory;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.swing.AutoCompleteComboBox;
import org.deegree.igeo.views.swing.util.GenericFileChooser;
import org.deegree.igeo.views.swing.util.IGeoFileFilter;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.igeo.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
class NewProjectSelectionDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = -5680059587928210444L;

    private JTextField loadingProject;

    private JPanel jPanel1;

    private JComboBox cbCRS;

    private JLabel jLabel1;

    private JLabel preview;

    private JComboBox prjTemplateList;

    private JButton btCancel;

    private JButton btOK;

    private JPanel jPanel2;

    private JButton openProject;

    private URL selection;

    private String projectCRS;

    private ProjectTemplate[] templates;

    private JToggleButton btZoomIn;

    private JToggleButton btBox;

    private SimpleMapPanel mapPanel;

    private JComboBox cbCountries;

    private JToggleButton btPan;

    private JToggleButton btZoomOut;

    private JPanel pnMap;

    private ButtonGroup bg = new ButtonGroup();

    private static String[] crsList;

    private ApplicationContainer<Container> appCont;

    static {
        if ( crsList == null ) {
            CRSProvider pr = CRSConfiguration.getCRSConfiguration().getProvider();
            List<String> tmp = pr.getAvailableCRSIds();
            List<String> tmp2 = new ArrayList<String>( tmp.size() / 2 );
            for ( String string : tmp ) {
                if ( string.toLowerCase().startsWith( "epsg:" ) ) {
                    tmp2.add( string );
                }
            }
            Collections.sort( tmp2 );
            // tmp2.add( 0, "no CRS" );
            crsList = tmp2.toArray( new String[tmp2.size()] );
        }
    }

    /**
     * @param appCont
     * @param frame
     *            parent frame
     * @param templates
     */
    NewProjectSelectionDialog( ApplicationContainer<Container> appCont, JFrame frame, ProjectTemplate... templates ) {
        super( frame );
        if ( frame != null ) {
            setTitle( Messages.getMessage( frame.getLocale(), "$DI10006" ) );
        } else {
            setTitle( Messages.getMessage( Locale.getDefault(), "$DI10006" ) );
        }
        this.appCont = appCont;

        this.templates = templates;
        if ( this.templates == null ) {
            this.templates = new ProjectTemplate[0];
        }
        setModal( true );
        getContentPane().setLayout( null );
        initGUI();
        setVisible( true );
    }

    private void initGUI() {
        {
            jPanel1 = new JPanel();
            getContentPane().add( jPanel1 );
            jPanel1.setBounds( 8, 5, 342, 216 );
            jPanel1.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$DI10005" ) ) );
            jPanel1.setLayout( null );
            {
                ComboBoxModel prjTemplateListModel = new DefaultComboBoxModel( templates );
                prjTemplateList = new JComboBox();
                jPanel1.add( prjTemplateList );
                prjTemplateList.setModel( prjTemplateListModel );
                prjTemplateList.setBounds( 10, 17, 147, 21 );
                prjTemplateList.addItemListener( new ItemListener() {

                    public void itemStateChanged( ItemEvent e ) {
                        ProjectTemplate prjt = (ProjectTemplate) e.getItem();
                        preview.setIcon( new ImageIcon( prjt.getPreView() ) );
                        loadingProject.setText( "" );
                    }

                } );
            }
            {
                preview = new JLabel();
                if ( templates.length > 0 ) {
                    preview.setIcon( new ImageIcon( templates[0].getPreView() ) );
                }
                jPanel1.add( preview );
                preview.setBounds( 169, 12, 153, 122 );
                preview.setBorder( BorderFactory.createLineBorder( Color.DARK_GRAY ) );
            }
            {
                jLabel1 = new JLabel();
                jPanel1.add( jLabel1 );
                jLabel1.setText( Messages.getMessage( getLocale(), "$DI10010" ) );
                jLabel1.setBounds( 10, 154, 153, 14 );
            }
            {
                cbCRS = new AutoCompleteComboBox( crsList );
                jPanel1.add( cbCRS );
                cbCRS.setSelectedItem( Messages.getMessage( getLocale(), "$DI10071" ) );
                cbCRS.setBounds( 10, 176, 315, 21 );
            }
        }
        {
            jPanel2 = new JPanel();
            jPanel2.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$DI10004" ) ) );
            getContentPane().add( jPanel2 );
            jPanel2.setBounds( 8, 233, 342, 62 );
            jPanel2.setLayout( null );
            {
                loadingProject = new JTextField();
                jPanel2.add( loadingProject );
                loadingProject.setBounds( 7, 28, 204, 21 );
            }
            {
                openProject = new JButton( Messages.getMessage( getLocale(), "$DI10003" ),
                                           IconRegistry.getIcon( "open.gif" ) );
                jPanel2.add( openProject );
                openProject.setBounds( 223, 28, 102, 21 );
                openProject.addActionListener( new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {
                        Preferences prefs = Preferences.userNodeForPackage( ApplicationContainer.class );
                        File file = GenericFileChooser.showOpenDialog( FILECHOOSERTYPE.project, appCont,
                                                                       NewProjectSelectionDialog.this, prefs,
                                                                       "projectFile", IGeoFileFilter.PRJ );

                        if ( file != null ) {
                            FileSystemAccessFactory fsaf = FileSystemAccessFactory.getInstance( appCont );
                            try {
                                FileSystemAccess fsa = fsaf.getFileSystemAccess( FILECHOOSERTYPE.project );
                                String s = fsa.getFileURL( file.getAbsolutePath() ).toURI().toASCIIString();
                                loadingProject.setText( s );
                                FileMemory.setLastDirectory( "newProjectFile", file );
                            } catch ( Exception e1 ) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }

                        }
                    }

                } );
            }
        }
        {
            btOK = new JButton( Messages.getMessage( getLocale(), "$DI10001" ), IconRegistry.getIcon( "accept.png" ) );
            btOK.setDefaultCapable( true );
            btOK.setSelected( true );
            getContentPane().add( btOK );
            btOK.setBounds( 12, 307, 100, 21 );
            btOK.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    if ( loadingProject.getText() != null && loadingProject.getText().length() > 0 ) {
                        try {
                            // projectCRS must be set to null because an already existing
                            // project will be used to create a new one
                            projectCRS = cbCRS.getSelectedItem().toString();
                            selection = new URL( loadingProject.getText() );
                        } catch ( MalformedURLException e1 ) {
                            e1.printStackTrace();
                        }
                        dispose();
                    } else if ( prjTemplateList.getSelectedItem() != null ) {
                        ProjectTemplate prjt = (ProjectTemplate) prjTemplateList.getSelectedItem();
                        selection = prjt.getProjectURL();
                        projectCRS = cbCRS.getSelectedItem().toString();
                        dispose();
                    } else {
                        DialogFactory.openWarningDialog( "application", NewProjectSelectionDialog.this,
                                                         Messages.getMessage( getLocale(), "$DI10007" ),
                                                         Messages.getMessage( getLocale(), "$DI10008" ) );
                    }
                }

            } );
        }
        {
            btCancel = new JButton( Messages.getMessage( getLocale(), "$DI10002" ), IconRegistry.getIcon( "cancel.png" ) );
            getContentPane().add( btCancel );
            btCancel.setBounds( 124, 307, 100, 21 );
            btCancel.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    projectCRS = null;
                    selection = null;
                    dispose();
                }

            } );
        }
        {
            pnMap = new JPanel();
            getContentPane().add( pnMap );
            pnMap.setBounds( 356, 5, 345, 290 );
            pnMap.setBorder( BorderFactory.createTitledBorder( null, Messages.getMessage( getLocale(), "$DI10066" ),
                                                               TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION ) );
            pnMap.setLayout( null );
            {
                btZoomIn = new JToggleButton( IconRegistry.getIcon( "zoomin.png" ) );
                pnMap.add( btZoomIn );
                btZoomIn.setBounds( 15, 20, 25, 22 );
                btZoomIn.doClick();
                bg.add( btZoomIn );
            }
            {
                btZoomOut = new JToggleButton( IconRegistry.getIcon( "zoomout.png" ) );
                pnMap.add( btZoomOut );
                btZoomOut.setBounds( 50, 20, 25, 22 );
                bg.add( btZoomOut );
            }
            {
                btPan = new JToggleButton( IconRegistry.getIcon( "pan.png" ) );
                pnMap.add( btPan );
                btPan.setBounds( 85, 20, 25, 22 );
                bg.add( btPan );
            }
            {
                btBox = new JToggleButton( Messages.getMessage( getLocale(), "$DI10065" ) );
                pnMap.add( btBox );
                btBox.setBounds( 120, 20, 150, 22 );
                bg.add( btBox );
            }
            {
                String res = Messages.getMessage( getLocale(), "$DI10069" );
                InputStreamReader isr = null;
                if ( res.startsWith( "file:" ) || res.startsWith( "http:" ) || res.startsWith( "ftp:" ) ) {
                    try {
                        isr = new InputStreamReader( new URL( res ).openStream() );
                    } catch ( IOException e1 ) {
                        e1.printStackTrace();
                        isr = new InputStreamReader( getClass().getResourceAsStream( "countries.csv" ) );
                    }
                } else {
                    isr = new InputStreamReader( getClass().getResourceAsStream( res ) );
                }
                BufferedReader br = new BufferedReader( isr );
                List<Country> list = new ArrayList<Country>();
                list.add( new Country( Messages.getMessage( getLocale(), "$DI10067" ),
                                       Messages.getMessage( getLocale(), "$DI10068" ) ) );
                String line = null;
                try {
                    while ( ( line = br.readLine() ) != null ) {
                        String[] tmp = StringTools.toArray( line, ";", false );
                        list.add( new Country( tmp[0], tmp[1] ) );
                    }
                } catch ( Exception e ) {
                    e.printStackTrace();
                }

                cbCountries = new JComboBox( list.toArray( new Country[list.size()] ) );
                cbCountries.addActionListener( new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {
                        Country country = (Country) cbCountries.getSelectedItem();
                        Envelope env = GeometryFactory.createEnvelope( country.env,
                                                                       mapPanel.getSelectBox().getCoordinateSystem() );
                        mapPanel.select( env );
                        mapPanel.invalidate();
                        mapPanel.repaint();
                    }
                } );
                cbCountries.addKeyListener( new KeyListener() {

                    public void keyTyped( KeyEvent e ) {
                    }

                    public void keyReleased( KeyEvent e ) {
                    }

                    public void keyPressed( KeyEvent e ) {
                        cbCountries.selectWithKeyChar( e.getKeyChar() );
                    }
                } );
                pnMap.add( cbCountries );
                cbCountries.setBounds( 17, 259, 311, 25 );
            }
            {
                mapPanel = new SimpleMapPanel();
                MouseHandler mh = new MouseHandler();
                mapPanel.addMouseListener( mh );
                mapPanel.addMouseMotionListener( mh );
                pnMap.add( mapPanel );
                mapPanel.setBounds( 17, 53, 311, 194 );
            }
        }
        if ( getParent() == null ) {
            this.setBounds( 0, 0, 740, 367 );
        } else {
            setBounds( getParent().getX() + 100, getParent().getY() + 100, 740, 367 );
        }
    }

    /**
     * 
     * @return URL of the project file that has been selected to create a new project from it
     */
    URL getSelection() {
        return selection;
    }

    /**
     * 
     * @return bounding box for new Project
     */
    Envelope getTargetBoundingBox() {
        return mapPanel.getSelectBox();
    }

    /**
     * 
     * @return desired CRS of the new project; will be <code>null</code> if a new project shall be created from an
     *         already exiting project
     */
    String getProjectCRS() {
        return projectCRS;
    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author: poth $
     * 
     * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
     */
    public static class ProjectTemplate {

        private String title;

        private URL preViewSrc;

        private BufferedImage preView;

        private URL prjURL;

        /**
         * @param title
         * @param preViewSrc
         */
        public ProjectTemplate( String title, URL preViewSrc, URL prjURL ) {
            this.title = title;
            this.preViewSrc = preViewSrc;
            this.prjURL = prjURL;
        }

        /**
         * @return the preView
         */
        public BufferedImage getPreView() {
            if ( preView == null ) {
                try {
                    preView = ImageUtils.loadImage( preViewSrc );
                } catch ( IOException e ) {
                    preView = new BufferedImage( 10, 10, BufferedImage.TYPE_INT_ARGB );
                }
            }
            return preView;
        }

        /**
         * @return the prjFile
         */
        public URL getProjectURL() {
            return prjURL;
        }

        @Override
        public String toString() {
            return title;
        }

    }

    /**
     * 
     * TODO add class documentation here
     * 
     * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
     * @author last edited by: $Author: admin $
     * 
     * @version $Revision: $, $Date: $
     */
    private class MouseHandler extends MouseAdapter implements MouseMotionListener {

        private int startx;

        private int starty;

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
         */
        public void mouseDragged( MouseEvent e ) {
            if ( bg.getSelection().equals( btPan.getModel() ) ) {
                mapPanel.pan( startx, starty, e.getX(), e.getY() );
                startx = e.getX();
                starty = e.getY();
                mapPanel.invalidate();
                mapPanel.repaint();
            } else if ( bg.getSelection().equals( btBox.getModel() ) ) {
                mapPanel.select( startx, starty, e.getX(), e.getY() );
                mapPanel.invalidate();
                mapPanel.repaint();
            } else if ( bg.getSelection().equals( btZoomIn.getModel() ) ) {
                if ( GeometryUtils.distance( startx, starty, e.getX(), e.getY() ) >= 5 ) {
                    mapPanel.setZoomBox( startx, starty, e.getX(), e.getY() );
                    mapPanel.invalidate();
                    mapPanel.repaint();
                }
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
         */
        public void mouseMoved( MouseEvent e ) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mousePressed( MouseEvent e ) {
            startx = e.getX();
            starty = e.getY();

        }

        @Override
        public void mouseReleased( MouseEvent e ) {
            if ( bg.getSelection().equals( btZoomIn.getModel() ) ) {
                if ( GeometryUtils.distance( startx, starty, e.getX(), e.getY() ) < 5 ) {
                    mapPanel.zoomIn();
                } else {
                    mapPanel.zoomIn( startx, starty, e.getX(), e.getY() );
                }
            } else if ( bg.getSelection().equals( btZoomOut.getModel() ) ) {
                mapPanel.zoomOut();
            } else if ( bg.getSelection().equals( btPan.getModel() ) ) {
                mapPanel.pan( startx, starty, e.getX(), e.getY() );
            }
            mapPanel.invalidate();
            mapPanel.repaint();
        }
    }

    /**
     * 
     * TODO add class documentation here
     * 
     * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
     * @author last edited by: $Author: admin $
     * 
     * @version $Revision: $, $Date: $
     */
    private class Country {
        String name;

        String env;

        /**
         * @param name
         * @param env
         */
        public Country( String name, String env ) {

            this.name = name;
            this.env = env;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
