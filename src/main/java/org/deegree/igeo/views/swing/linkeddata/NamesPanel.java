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
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.config.AbstractLinkedTableType;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.views.DialogFactory;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class NamesPanel extends AbstractLinkedDataPanel {

    private static final long serialVersionUID = 2713200138624198740L;

    private JPanel pnColumnPostfix;

    private JPanel pnLayerTitle;

    private JPanel pnDescription;

    private JScrollPane scDescription;

    private JTextArea taDescription;

    private JTextField tfLayerTitle;

    private JTextField tfColumnPostfix;

    public NamesPanel() {
        initGUI();
    }

    /**
     * 
     * @param appCont
     * @param linkedTable
     */
    NamesPanel( ApplicationContainer<Container> appCont, AbstractLinkedTableType linkedTable ) {
        this.appCont = appCont;
        this.linkedTable = linkedTable;
        initGUI();
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            this.setPreferredSize( new java.awt.Dimension( 492, 369 ) );
            thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.1 };
            thisLayout.rowHeights = new int[] { 70, 70, 7 };
            thisLayout.columnWeights = new double[] { 0.1 };
            thisLayout.columnWidths = new int[] { 7 };
            this.setLayout( thisLayout );
            {
                pnColumnPostfix = new JPanel();
                GridBagLayout pnColumnPostfixLayout = new GridBagLayout();
                this.add( pnColumnPostfix, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                   GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ),
                                                                   0, 0 ) );
                pnColumnPostfix.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                  "$MD11565" ) ) );
                pnColumnPostfixLayout.rowWeights = new double[] { 0.1 };
                pnColumnPostfixLayout.rowHeights = new int[] { 7 };
                pnColumnPostfixLayout.columnWeights = new double[] { 0.1 };
                pnColumnPostfixLayout.columnWidths = new int[] { 7 };
                pnColumnPostfix.setLayout( pnColumnPostfixLayout );
                {
                    tfColumnPostfix = new JTextField( Messages.getMessage( getLocale(), "$MD11566" ) );
                    pnColumnPostfix.add( tfColumnPostfix, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                                  GridBagConstraints.CENTER,
                                                                                  GridBagConstraints.HORIZONTAL,
                                                                                  new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                }
            }
            {
                pnLayerTitle = new JPanel();
                GridBagLayout pnLayerTitleLayout = new GridBagLayout();
                this.add( pnLayerTitle,
                          new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnLayerTitle.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11567" ) ) );
                pnLayerTitleLayout.rowWeights = new double[] { 0.1 };
                pnLayerTitleLayout.rowHeights = new int[] { 7 };
                pnLayerTitleLayout.columnWeights = new double[] { 0.1 };
                pnLayerTitleLayout.columnWidths = new int[] { 7 };
                pnLayerTitle.setLayout( pnLayerTitleLayout );
                {
                    tfLayerTitle = new JTextField( Messages.getMessage( getLocale(), "$MD11568" ) );
                    pnLayerTitle.add( tfLayerTitle, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                            GridBagConstraints.CENTER,
                                                                            GridBagConstraints.HORIZONTAL,
                                                                            new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                }
            }
            {
                pnDescription = new JPanel();
                BorderLayout pnDescriptionLayout = new BorderLayout();
                pnDescription.setLayout( pnDescriptionLayout );
                this.add( pnDescription, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                 GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                 0 ) );
                pnDescription.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11557" ) ) );
                {
                    scDescription = new JScrollPane();
                    pnDescription.add( scDescription, BorderLayout.CENTER );
                    scDescription.setBorder( BorderFactory.createEmptyBorder( 9, 9, 9, 9 ) );
                    {
                        taDescription = new JTextArea();
                        scDescription.setViewportView( taDescription );
                    }
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    String getLayerTitle() {
        if ( tfLayerTitle.getText() == null || tfLayerTitle.getText().trim().length() == 0 ) {
            DialogFactory.openWarningDialog( appCont.getViewPlatform(), this, Messages.getMessage( getLocale(),
                                                                                                   "$MD11569" ),
                                             Messages.getMessage( getLocale(), "$MD11570" ) );
            return null;
        }
        return tfLayerTitle.getText();
    }

    @Override
    public AbstractLinkedTableType getLinkedTable() {

        if ( tfColumnPostfix.getText() == null || tfColumnPostfix.getText().trim().length() == 0 ) {
            DialogFactory.openWarningDialog( appCont.getViewPlatform(), this, Messages.getMessage( getLocale(),
                                                                                                   "$MD11571" ),
                                             Messages.getMessage( getLocale(), "$MD11572" ) );
            return null;
        }
        linkedTable.setDescription( taDescription.getText() );
        linkedTable.setTitle( tfColumnPostfix.getText() );
        return super.getLinkedTable();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.linkeddata.AbstractLinkedDataPanel#getNext()
     */
    AbstractLinkedDataPanel getNext() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.linkeddata.AbstractLinkedDataPanel#getDescription()
     */
    String getDescription() {
        return Messages.getMessage( getLocale(), "$MD11577" );
    }
}
