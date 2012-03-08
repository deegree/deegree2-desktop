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
package org.deegree.igeo.views.swing.linkeddata;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.util.Pair;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.config.RelationKeyType;
import org.deegree.igeo.config.Util;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.dataadapter.LinkedTable;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.model.feature.schema.FeatureType;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
class DefineKeysPanel extends AbstractLinkedDataPanel {

    private static final long serialVersionUID = 6138204406236566798L;

    private JPanel pnButtons;

    private JPanel pnKeys;

    private JButton btRemove;

    private JButton btAdd;

    private int index = 0;

    /**
     * 
     * @param appCont
     * @param linkedTable
     */
    DefineKeysPanel( ApplicationContainer<Container> appCont, LinkedTable linkedTable ) {
        this.appCont = appCont;
        this.linkedTable = linkedTable;
        initGUI();
    }

    private void initGUI() {
        try {
            BorderLayout thisLayout = new BorderLayout();
            this.setLayout( thisLayout );
            this.setPreferredSize( new Dimension( 416, 349 ) );
            this.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11545" ) ) );
            {
                pnButtons = new JPanel();
                FlowLayout pnButtonsLayout = new FlowLayout();
                pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                pnButtons.setLayout( pnButtonsLayout );
                this.add( pnButtons, BorderLayout.SOUTH );
                {
                    btAdd = new JButton( Messages.getMessage( getLocale(), "$MD11546" ),
                                         IconRegistry.getIcon( "add.png" ) );
                    pnButtons.add( btAdd );
                    btAdd.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            KeyPanel kp1 = new KeyPanel();
                            try {
                                kp1.setAvaiableKeys( getTableColumns(), getSelectedFeatureType() );
                                pnKeys.add( kp1, new GridBagConstraints( 0, ++index, 1, 1, 0.0, 0.0,
                                                                         GridBagConstraints.CENTER,
                                                                         GridBagConstraints.HORIZONTAL,
                                                                         new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                                btRemove.setEnabled( true );
                                SwingUtilities.updateComponentTreeUI( DefineKeysPanel.this );
                                DefineKeysPanel.this.repaint();
                            } catch ( IOException e1 ) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }

                        }
                    } );
                }
                {
                    btRemove = new JButton( Messages.getMessage( getLocale(), "$MD11547" ),
                                            IconRegistry.getIcon( "remove.png" ) );
                    btRemove.setEnabled( false );
                    pnButtons.add( btRemove );
                    btRemove.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            if ( index > 0 ) {
                                pnKeys.remove( pnKeys.getComponentCount() - 1 );
                                index--;
                                if ( index == 0 ) {
                                    btRemove.setEnabled( false );
                                }
                                SwingUtilities.updateComponentTreeUI( DefineKeysPanel.this );
                                DefineKeysPanel.this.repaint();
                            }
                        }
                    } );
                }
            }
            {
                pnKeys = new JPanel();
                GridBagLayout pnKeysLayout = new GridBagLayout();
                pnKeysLayout.rowWeights = new double[] { 0.1, 0.1, 0.0, 0.1, 0.1 };
                pnKeysLayout.rowHeights = new int[] { 7, 7, 58, 7, 7 };
                pnKeysLayout.columnWeights = new double[] { 0.1 };
                pnKeysLayout.columnWidths = new int[] { 7 };
                pnKeys.setLayout( pnKeysLayout );
                this.add( pnKeys, BorderLayout.CENTER );
                {
                    KeyPanel kp1 = new KeyPanel();
                    kp1.setAvaiableKeys( getTableColumns(), getSelectedFeatureType() );
                    pnKeys.add( kp1, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                             GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ),
                                                             0, 0 ) );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    void setKeys() {
        Component[] comps = pnKeys.getComponents();
        linkedTable.getRelationKey().clear();
        for ( Component component : comps ) {
            if ( component instanceof KeyPanel ) {
                Pair<QualifiedName, String> key = ( (KeyPanel) component ).getSelectedKey();
                RelationKeyType rkt = new RelationKeyType();
                rkt.setFeatureProperty( Util.convertQualifiedName( key.first ) );
                rkt.setTableColumn( key.second );
                linkedTable.getRelationKey().add( rkt );
            }
        }
    }

    /**
     * @return
     * @throws IOException
     */
    private String[] getTableColumns()
                            throws IOException {
        return linkedTable.getColumnNames();
    }

    private FeatureType getSelectedFeatureType() {
        List<Layer> layers = appCont.getMapModel( null ).getLayersSelectedForAction( MapModel.SELECTION_ACTION );
        FeatureAdapter adapter = (FeatureAdapter) layers.get( 0 ).getDataAccess().get( 0 );
        FeatureType ft = adapter.getSchema();
        return ft;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.linkeddata.AbstractLinkedDataPanel#getNext()
     */
    AbstractLinkedDataPanel getNext() {
        setKeys();
        AbstractLinkedDataPanel p = new NamesPanel( appCont, linkedTable );
        p.setPrevious( this );
        p.setView( isView() );
        return p;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.linkeddata.AbstractLinkedDataPanel#getDescription()
     */
    String getDescription() {
        return Messages.getMessage( getLocale(), "$MD11576" );
    }
}
