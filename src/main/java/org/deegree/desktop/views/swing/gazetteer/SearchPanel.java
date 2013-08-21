//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.desktop.views.swing.gazetteer;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.commands.gazeetteer.GazetteerFindItemsCommand;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.modules.gazetteer.GazetteerItem;
import org.deegree.desktop.modules.gazetteer.HierarchyNode;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.desktop.views.swing.CursorRegistry;
import org.deegree.desktop.views.swing.util.IconRegistry;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class SearchPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = -792519717422849473L;

    private static final ILogger LOG = LoggerFactory.getLogger( SearchPanel.class );

    private JLabel lbName;

    private JTextField tfSeach;

    private JComboBox cbValues;

    private JButton tbSearch;

    private HierarchyNode node;

    private ApplicationContainer<Container> appCont;

    private String gazetteerAddr;

    private GazetteerPanel view;

    /**
     * 
     * @param appCont
     * @param gazetteerAddr
     * @param node
     * @param view
     */
    public SearchPanel( ApplicationContainer<Container> appCont, String gazetteerAddr, HierarchyNode node,
                        GazetteerPanel view ) {
        this.appCont = appCont;
        this.gazetteerAddr = gazetteerAddr;
        this.node = node;
        this.view = view;
        initGUI();
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            this.setPreferredSize( new java.awt.Dimension( 317, 74 ) );
            thisLayout.rowWeights = new double[] { 0.1, 0.1 };
            thisLayout.rowHeights = new int[] { 7, 20 };
            thisLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.1 };
            thisLayout.columnWidths = new int[] { -1, 125, 146, 7 };
            this.setLayout( thisLayout );
            {
                lbName = new JLabel( node.getName() );
                this.add( lbName, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                          GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0, 0 ), 0,
                                                          0 ) );
            }
            {
                tfSeach = new JTextField();
                this.add( tfSeach, new GridBagConstraints( 2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                           GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0,
                                                           0 ) );
            }
            {
                tbSearch = new JButton( IconRegistry.getIcon( "forward_green.png" ) );
                this.add( tbSearch, new GridBagConstraints( 3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                            GridBagConstraints.NONE, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                tbSearch.setToolTipText( Messages.getMessage( getLocale(), "$MD11301" ) );
                tbSearch.addActionListener( new ActionListener() {

                    public void actionPerformed( ActionEvent event ) {
                        search();
                        cbValues.setEnabled( true );
                    }
                } );
            }
            {
                cbValues = new JComboBox();
                this.add( cbValues, new GridBagConstraints( 1, 1, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                            GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0, 10 ),
                                                            0, 0 ) );
                cbValues.setEnabled( false );
                cbValues.addActionListener( new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {
                        Object o = cbValues.getSelectedItem();
                        if ( o instanceof GazetteerItem ) {
                            view.findChildren( (GazetteerItem) o, node );
                        }
                    }
                } );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private void search() {
        String s = tfSeach.getText();
        if ( s.length() < 3 ) {
            DialogFactory.openWarningDialog( appCont.getViewPlatform(), getParent(), Messages.getMessage( getLocale(),
                                                                                                          "$MD11302" ),
                                             Messages.getMessage( getLocale(), "$MD11303" ) );
            return;
        }
        GazetteerFindItemsCommand cmd = new GazetteerFindItemsCommand( appCont, gazetteerAddr, node.getFeatureType(),
                                                                       node.getProperties(), s, true, true, false );
        getParent().setCursor( CursorRegistry.WAIT_CURSOR );
        try {
            // perform synchronously; listeners must not be informed because result
            // will just be used by this class
            appCont.getCommandProcessor().executeSychronously( cmd, false );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openErrorDialog( appCont.getViewPlatform(), getParent(), Messages.getMessage( getLocale(),
                                                                                                        "$MD11304" ),
                                           Messages.getMessage( getLocale(), "$MD11305", node.getName(), s ), e );
            return;
        } finally {
            getParent().setCursor( CursorRegistry.DEFAULT_CURSOR );
        }

        List<?> items = (List<?>) cmd.getResult();
        Object[] gi = items.toArray();
        Arrays.sort( gi );        
        DefaultComboBoxModel model = new DefaultComboBoxModel( );
        model.addElement( Messages.getMessage( getLocale(), "$MD11306" ) );
        for ( Object object : gi ) {
            model.addElement( object );
        }
        
        cbValues.setModel( model );
    }

}
