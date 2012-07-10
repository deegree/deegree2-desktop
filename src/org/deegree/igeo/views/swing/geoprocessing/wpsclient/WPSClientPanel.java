//$HeadURL: svn+ssh://developername@svn.wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
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
package org.deegree.igeo.views.swing.geoprocessing.wpsclient;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.modules.geoprocessing.WPSClientModule;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.swing.DefaultPanel;
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
public class WPSClientPanel extends DefaultPanel {

    private static final long serialVersionUID = 2048542107296901260L;

    private static final ILogger LOG = LoggerFactory.getLogger( WPSClientPanel.class );

    private JPanel pnWPSControl;

    private JPanel pnProcessSelection;

    private JComboBox cbWPSURL;

    private JPanel pnProcessParameter;

    private JButton btCancel;

    private JButton btOK;

    private JPanel pnButtons;

    private JTextArea taProcessDescription;

    private JPanel pnProcessDesc;

    private JComboBox cbWPSProcess;

    private JPanel pnWPS;

    private Container parent;

    private List<String> wpsURLs;

    private Map<String, List<String>> wpsProcesses;

    private Map<String, String> processesGUI;

    private ProcessParameter current;

    /**
     * default constructor
     */
    public WPSClientPanel() {
        super();
    }

    /**
     * 
     * @param parent
     */
    WPSClientPanel( Container parent ) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#init(org.deegree.igeo.config.ViewFormType)
     */
    public void init( ViewFormType viewForm )
                            throws Exception {
        wpsURLs = new ArrayList<String>();
        wpsProcesses = new LinkedHashMap<String, List<String>>();
        processesGUI = new HashMap<String, String>();
        Map<String, String> initParam = owner.getInitParameters();
        Iterator<String> iterator = initParam.keySet().iterator();

        while ( iterator.hasNext() ) {
            // init parameter name is base URL for a WPS
            String name = (String) iterator.next();
            if ( name.toLowerCase().startsWith( "http://" ) || name.toLowerCase().startsWith( "https://" ) ) {
                wpsURLs.add( name );
                // init parameter value is a comma separated list of processes supported by the WPS and
                // its assigned GUI-Panel. Processes of a WPS that are not listed here will be assigned
                // to a default Panel for entering parameters
                String s = initParam.get( name );
                String[] tmp = StringTools.toArray( s, ":", false );
                processesGUI.put( name + ':' + tmp[0], tmp[1] );
                if ( wpsProcesses.get( name ) == null ) {
                    wpsProcesses.put( name, new ArrayList<String>() );
                }
                wpsProcesses.get( name ).add( tmp[0] );
            }
        }
        // TODO
        // read other processes from WPS using describe process operation
        // ....

        initGUI();
        setVisible( true );
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            this.setPreferredSize( new java.awt.Dimension( 849, 538 ) );
            thisLayout.rowWeights = new double[] { 0.0, 0.1 };
            thisLayout.rowHeights = new int[] { 500, 7 };
            thisLayout.columnWeights = new double[] { 0.0, 0.1, 0.1 };
            thisLayout.columnWidths = new int[] { 270, 7, 20 };
            this.setLayout( thisLayout );
            {
                pnWPSControl = new JPanel();
                GridBagLayout pnWPSControlLayout = new GridBagLayout();
                this.add( pnWPSControl,
                          new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnWPSControl.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11320" ) ) );
                pnWPSControlLayout.rowWeights = new double[] { 0.0, 0.0, 0.1 };
                pnWPSControlLayout.rowHeights = new int[] { 98, 98, 7 };
                pnWPSControlLayout.columnWeights = new double[] { 0.1 };
                pnWPSControlLayout.columnWidths = new int[] { 7 };
                pnWPSControl.setLayout( pnWPSControlLayout );
                {
                    pnWPS = new JPanel();
                    GridBagLayout pnWPSLayout = new GridBagLayout();
                    pnWPSControl.add( pnWPS, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                     GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ),
                                                                     0, 0 ) );
                    pnWPS.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11321" ) ) );
                    pnWPSLayout.rowWeights = new double[] { 0.1 };
                    pnWPSLayout.rowHeights = new int[] { 7 };
                    pnWPSLayout.columnWeights = new double[] { 0.1 };
                    pnWPSLayout.columnWidths = new int[] { 7 };
                    pnWPS.setLayout( pnWPSLayout );
                    {
                        String[] s = wpsURLs.toArray( new String[wpsURLs.size()] );
                        DefaultComboBoxModel cbWPSURLModel = new DefaultComboBoxModel( s );
                        cbWPSURL = new JComboBox( cbWPSURLModel );
                        cbWPSURL.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                // fill combobox with list of processes available from
                                // selected WPS
                                String wps = (String) cbWPSURL.getSelectedItem();
                                List<String> list = wpsProcesses.get( wps );
                                String[] s = list.toArray( new String[list.size()] );
                                cbWPSProcess.setModel( new DefaultComboBoxModel( s ) );
                            }

                        } );
                        pnWPS.add( cbWPSURL, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                     GridBagConstraints.HORIZONTAL,
                                                                     new Insets( 0, 10, 0, 10 ), 0, 0 ) );
                    }
                }
                {
                    pnProcessSelection = new JPanel();
                    GridBagLayout pnProcessSelectionLayout = new GridBagLayout();
                    pnWPSControl.add( pnProcessSelection, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                                                                  GridBagConstraints.NORTH,
                                                                                  GridBagConstraints.BOTH,
                                                                                  new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    pnProcessSelection.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                         "$MD11322" ) ) );
                    pnProcessSelectionLayout.rowWeights = new double[] { 0.1 };
                    pnProcessSelectionLayout.rowHeights = new int[] { 7 };
                    pnProcessSelectionLayout.columnWeights = new double[] { 0.1 };
                    pnProcessSelectionLayout.columnWidths = new int[] { 7 };
                    pnProcessSelection.setLayout( pnProcessSelectionLayout );
                    {
                        String wps = (String) cbWPSURL.getSelectedItem();
                        List<String> list = wpsProcesses.get( wps );
                        String[] s = list.toArray( new String[list.size()] );
                        cbWPSProcess = new JComboBox( new DefaultComboBoxModel( s ) );
                        cbWPSProcess.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                addProcessPanel();
                            }

                        } );
                        pnProcessSelection.add( cbWPSProcess, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                                      GridBagConstraints.CENTER,
                                                                                      GridBagConstraints.HORIZONTAL,
                                                                                      new Insets( 0, 10, 0, 10 ), 0, 0 ) );
                        SwingUtilities.invokeLater( new Runnable() {
                            public void run() {
                                // set form for entering process parameters
                                addProcessPanel();
                            }
                        } );
                    }
                }
                {
                    pnProcessDesc = new JPanel();
                    BorderLayout pnProcessDescLayout = new BorderLayout();
                    pnProcessDesc.setLayout( pnProcessDescLayout );
                    pnWPSControl.add( pnProcessDesc, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0,
                                                                             GridBagConstraints.CENTER,
                                                                             GridBagConstraints.BOTH,
                                                                             new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    pnProcessDesc.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                    "$MD11323" ) ) );
                    {
                        taProcessDescription = new JTextArea();
                        pnProcessDesc.add( taProcessDescription, BorderLayout.CENTER );
                        taProcessDescription.setBackground( pnProcessDesc.getBackground() );
                        taProcessDescription.setEditable( false );
                        taProcessDescription.setLineWrap( true );
                        taProcessDescription.setWrapStyleWord( true );
                    }
                }
            }
            {
                pnProcessParameter = new JPanel();
                pnProcessParameter.setLayout( new BorderLayout() );
                this.add( pnProcessParameter, new GridBagConstraints( 1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                      GridBagConstraints.BOTH,
                                                                      new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnProcessParameter.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                     "$MD11324" ) ) );
            }
            {
                pnButtons = new JPanel();
                FlowLayout pnButtonsLayout = new FlowLayout();
                pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                this.add( pnButtons, new GridBagConstraints( 0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                             GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnButtons.setLayout( pnButtonsLayout );
                {
                    btOK = new JButton( Messages.getMessage( getLocale(), "$MD11325" ),
                                        IconRegistry.getIcon( "accept.png" ) );
                    btOK.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent event ) {
                            Map<String, Object> param = current.getParameter();
                            // add URL of selected WPS
                            param.put( "$WPS", cbWPSURL.getSelectedItem() );
                            // add name of selected process
                            param.put( "$PROCESS", cbWPSProcess.getSelectedItem() );
                            ( (WPSClientModule<?>) owner ).process( param );
                            owner.clear();
                            try {
                                Method m = parent.getClass().getMethod( "dispose", new Class<?>[0] );
                                if ( m != null ) {
                                    m.invoke( parent, new Object[0] );
                                }
                            } catch ( Exception e ) {
                                e.printStackTrace();
                            }
                        }

                    } );
                    pnButtons.add( btOK );
                }
                {
                    btCancel = new JButton( Messages.getMessage( getLocale(), "$MD11326" ),
                                            IconRegistry.getIcon( "cancel.png" ) );
                    pnButtons.add( btCancel );
                    btCancel.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent event ) {
                            owner.clear();
                            try {
                                Method m = parent.getClass().getMethod( "dispose", new Class<?>[0] );
                                if ( m != null ) {
                                    m.invoke( parent, new Object[0] );
                                }
                            } catch ( Exception e ) {
                                e.printStackTrace();
                            }
                        }

                    } );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * sets the GUI/form to to WPS client panel that enables entering parameters required for selected process
     * 
     */
    @SuppressWarnings("unchecked")
    private void addProcessPanel() {
        String wps = (String) cbWPSURL.getSelectedItem();
        String process = (String) cbWPSProcess.getSelectedItem();
        String classname = processesGUI.get( wps + ':' + process );
        try {
            Class<JPanel> clzz = (Class<JPanel>) Class.forName( classname );
            Constructor<?> constructor = clzz.getConstructor( ApplicationContainer.class );
            JPanel panel = (JPanel) constructor.newInstance( owner.getApplicationContainer() );
            current = (ProcessParameter) panel;
            panel.setVisible( true );
            pnProcessParameter.removeAll();
            pnProcessParameter.add( panel, BorderLayout.CENTER );
            Component root = SwingUtilities.getRoot( pnProcessParameter );
            Method m = parent.getClass().getMethod( "pack", new Class<?>[0] );
            if ( m != null ) {
                m.invoke( parent, new Object[0] );
            }
            root.repaint();
        } catch ( Exception e1 ) {
            LOG.logError( e1.getMessage(), e1 );
            DialogFactory.openErrorDialog( "application", WPSClientPanel.this, Messages.getMessage( getLocale(),
                                                                                                    "$MD11327" ),
                                           Messages.getMessage( getLocale(), "$MD11328", classname ), e1 );
        }
    }

}
