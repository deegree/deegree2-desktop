//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2010 by:
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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.deegree.crs.configuration.CRSConfiguration;
import org.deegree.crs.configuration.CRSProvider;
import org.deegree.crs.coordinatesystems.CoordinateSystem;
import org.deegree.framework.util.StringTools;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.views.swing.AutoCompleteComboBox;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.UnknownCRSException;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author: admin $
 * 
 * @version $Revision: $, $Date: $
 */
public class CRSChooserDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = -2041207030483568761L;

    private JPanel pnDescription;

    private JPanel pnCRSDescription;

    private JEditorPane epName;

    private JScrollPane scName;

    private JButton btCancel;

    private JButton btOK;

    private JPanel pnButtons;

    private JScrollPane scAlternateIDs;

    private JEditorPane epAlternateIDs;

    private JEditorPane epCRSDescription;

    private JScrollPane scCRSDescription;

    private JComboBox cbCRSID;

    private JPanel pnName;

    private JPanel pnAlternateIDs;

    private JPanel pnCRS_ID;

    private JEditorPane epDescription;

    private JPanel pnCRS;

    private org.deegree.model.crs.CoordinateSystem coordRefSys;

    private static String[] crsList;

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

    CRSChooserDialog( JFrame frame ) {
        super( frame );
        initGUI();
        setLocation( 100, 100 );
        setModal( true );
        setVisible( true );
    }

    private void initGUI() {
        try {
            {
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] { 0.0, 0.1 };
                thisLayout.rowHeights = new int[] { 374, 7 };
                thisLayout.columnWeights = new double[] { 0.0, 0.1 };
                thisLayout.columnWidths = new int[] { 188, 7 };
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
                    pnDescription.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11758" ) ) );
                    {
                        epDescription = new JEditorPane();
                        pnDescription.add( epDescription, BorderLayout.CENTER );
                        epDescription.setText( Messages.getMessage( getLocale(), "$MD11759" ) );
                    }
                }
                {
                    pnCRS = new JPanel();
                    GridBagLayout pnCRSLayout = new GridBagLayout();
                    getContentPane().add(
                                          pnCRS,
                                          new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnCRS.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11760" ) ) );
                    pnCRSLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.1 };
                    pnCRSLayout.rowHeights = new int[] { 54, 114, 58, 7 };
                    pnCRSLayout.columnWeights = new double[] { 0.1 };
                    pnCRSLayout.columnWidths = new int[] { 7 };
                    pnCRS.setLayout( pnCRSLayout );
                    {
                        pnCRS_ID = new JPanel();
                        GridBagLayout pnCRS_IDLayout = new GridBagLayout();
                        pnCRS.add( pnCRS_ID, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                     GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ),
                                                                     0, 0 ) );
                        pnCRS_ID.setBorder( BorderFactory.createTitledBorder(  Messages.getMessage( getLocale(), "$MD11761" ) ) );
                        pnCRS_IDLayout.rowWeights = new double[] { 0.1 };
                        pnCRS_IDLayout.rowHeights = new int[] { 7 };
                        pnCRS_IDLayout.columnWeights = new double[] { 0.1 };
                        pnCRS_IDLayout.columnWidths = new int[] { 7 };
                        pnCRS_ID.setLayout( pnCRS_IDLayout );
                        {
                            cbCRSID = new AutoCompleteComboBox( crsList );
                            cbCRSID.addActionListener( new ActionListener() {

                                public void actionPerformed( ActionEvent e ) {
                                    String crsID = (String) cbCRSID.getSelectedItem();
                                    try {
                                        coordRefSys = CRSFactory.create( crsID );
                                        CoordinateSystem crs = coordRefSys.getCRS();
                                        epCRSDescription.setText( crs.getDescription() );
                                        epName.setText( crs.getName() );
                                        String[] ids = crs.getIdentifiers();
                                        epAlternateIDs.setText( StringTools.arrayToString( ids, ',' ) );
                                    } catch ( UnknownCRSException e1 ) {
                                        // will never happen here
                                        e1.printStackTrace();
                                    }

                                }
                            } );
                            pnCRS_ID.add( cbCRSID, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                           GridBagConstraints.CENTER,
                                                                           GridBagConstraints.HORIZONTAL,
                                                                           new Insets( 0, 3, 0, 3 ), 0, 0 ) );
                        }
                    }
                    {
                        pnAlternateIDs = new JPanel();
                        BorderLayout pnAlternateIDsLayout = new BorderLayout();
                        pnAlternateIDs.setLayout( pnAlternateIDsLayout );
                        pnCRS.add( pnAlternateIDs, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                                                           GridBagConstraints.CENTER,
                                                                           GridBagConstraints.BOTH, new Insets( 0, 0,
                                                                                                                0, 0 ),
                                                                           0, 0 ) );
                        pnAlternateIDs.setBorder( BorderFactory.createTitledBorder(  Messages.getMessage( getLocale(), "$MD11762" ) ) );
                        {
                            scAlternateIDs = new JScrollPane();
                            pnAlternateIDs.add( scAlternateIDs, BorderLayout.CENTER );
                            {
                                epAlternateIDs = new JEditorPane();
                                scAlternateIDs.setViewportView( epAlternateIDs );
                            }
                        }
                    }
                    {
                        pnName = new JPanel();
                        BorderLayout pnNameLayout = new BorderLayout();
                        pnName.setLayout( pnNameLayout );
                        pnCRS.add( pnName, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                   GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ),
                                                                   0, 0 ) );
                        pnName.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11763" )) );
                        {
                            scName = new JScrollPane();
                            pnName.add( scName, BorderLayout.CENTER );
                            {
                                epName = new JEditorPane();
                                scName.setViewportView( epName );
                            }
                        }
                    }
                    {
                        pnCRSDescription = new JPanel();
                        BorderLayout pnCRSDescriptionLayout = new BorderLayout();
                        pnCRSDescription.setLayout( pnCRSDescriptionLayout );
                        pnCRS.add( pnCRSDescription, new GridBagConstraints( 0, 3, 1, 1, 0.0, 0.0,
                                                                             GridBagConstraints.CENTER,
                                                                             GridBagConstraints.BOTH,
                                                                             new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                        pnCRSDescription.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11764" ) ) );
                        {
                            scCRSDescription = new JScrollPane();
                            pnCRSDescription.add( scCRSDescription, BorderLayout.CENTER );
                            {
                                epCRSDescription = new JEditorPane();
                                scCRSDescription.setViewportView( epCRSDescription );
                            }
                        }
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
                        btOK = new JButton( Messages.getMessage( getLocale(), "$MD11765" ), IconRegistry.getIcon( "accept.png" ) );
                        pnButtons.add( btOK );
                        btOK.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {
                                dispose();
                            }
                        } );

                    }
                    {
                        btCancel = new JButton( Messages.getMessage( getLocale(), "$MD11766" ), IconRegistry.getIcon( "cancel.png" ) );
                        pnButtons.add( btCancel );
                        btCancel.addActionListener( new ActionListener() {

                            public void actionPerformed( ActionEvent e ) {                                
                                coordRefSys = null;
                                dispose();
                            }
                        } );
                    }
                }
            }
            this.setSize( 524, 448 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @return
     */
    org.deegree.model.crs.CoordinateSystem getSelectedCRS() {
        return coordRefSys;
    }

}
