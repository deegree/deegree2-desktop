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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.deegree.igeo.config.LinkedDatabaseTableType;
import org.deegree.igeo.config.LinkedFileTableType;
import org.deegree.igeo.i18n.Messages;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
class InitialPanel extends AbstractLinkedDataPanel {

    private static final long serialVersionUID = 8661188011501645513L;

    private JPanel pnLinageType;

    private JRadioButton rbLinkageView;

    private ButtonGroup bgLinkageType;

    private ButtonGroup bgDatasourceType;

    private JRadioButton rbDsDatabase;

    private JRadioButton rbDsFile;

    private JPanel pnDatasource;

    private JRadioButton rbLinkageLayer;

    /**
     * 
     */
    InitialPanel() {
        initGUI();
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            setPreferredSize( new Dimension( 400, 300 ) );
            thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.1 };
            thisLayout.rowHeights = new int[] { 70, 70, 7 };
            thisLayout.columnWeights = new double[] { 0.1 };
            thisLayout.columnWidths = new int[] { 7 };
            this.setLayout( thisLayout );
            {
                pnLinageType = new JPanel();
                FlowLayout pnLinageTypeLayout = new FlowLayout();
                pnLinageTypeLayout.setAlignment( FlowLayout.LEFT );
                pnLinageType.setLayout( pnLinageTypeLayout );
                this.add( pnLinageType,
                          new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnLinageType.setBorder( BorderFactory.createTitledBorder(  Messages.getMessage( getLocale(), "$MD11579" ) ) );
                {
                    rbLinkageView = new JRadioButton( Messages.getMessage( getLocale(), "$MD11552" ) );
                    pnLinageType.add( rbLinkageView );
                    rbLinkageView.setBorder( BorderFactory.createEmptyBorder( 0, 20, 0, 0 ) );
                    getBgLinkageType().add( rbLinkageView );
                }
                {
                    rbLinkageLayer = new JRadioButton( Messages.getMessage( getLocale(), "$MD11553" ) );
                    pnLinageType.add( rbLinkageLayer );
                    rbLinkageLayer.setBorder( BorderFactory.createEmptyBorder( 0, 10, 0, 0 ) );
                    getBgLinkageType().add( rbLinkageLayer );
                    rbLinkageLayer.setSelected( true );
                }
            }
            {
                pnDatasource = new JPanel();
                FlowLayout pnDatasourceLayout = new FlowLayout();
                pnDatasourceLayout.setAlignment( FlowLayout.LEFT );
                pnDatasource.setLayout( pnDatasourceLayout );
                this.add( pnDatasource,
                          new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnDatasource.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11554" ) ) );
                {
                    rbDsFile = new JRadioButton( Messages.getMessage( getLocale(), "$MD11555" ) );
                    pnDatasource.add( rbDsFile );
                    rbDsFile.setBorder( BorderFactory.createEmptyBorder( 0, 20, 0, 0 ) );
                    getBgDatasourceType().add( rbDsFile );
                    rbDsFile.setSelected( true );
                }
                {
                    rbDsDatabase = new JRadioButton( Messages.getMessage( getLocale(), "$MD11556" ) );
                    pnDatasource.add( rbDsDatabase );
                    rbDsDatabase.setBorder( BorderFactory.createEmptyBorder( 0, 10, 0, 0 ) );
                    getBgDatasourceType().add( rbDsDatabase );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    private ButtonGroup getBgLinkageType() {
        if ( bgLinkageType == null ) {
            bgLinkageType = new ButtonGroup();
        }
        return bgLinkageType;
    }

    private ButtonGroup getBgDatasourceType() {
        if ( bgDatasourceType == null ) {
            bgDatasourceType = new ButtonGroup();
        }
        return bgDatasourceType;
    }

   
    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.linkeddata.AbstractLinkedDataPanel#getNext()
     */
    AbstractLinkedDataPanel getNext() {

        AbstractLinkedDataPanel p = null;
        if ( rbDsFile.isSelected() ) {
            p = new FileSelectPanel();
            if ( linkedTable == null || linkedTable instanceof LinkedDatabaseTableType ) {
                linkedTable = new LinkedFileTableType();
            }
        }
        if ( rbDsDatabase.isSelected() ) {
            p = new DatabaseSelectPanel();
            if ( linkedTable == null || linkedTable instanceof LinkedFileTableType ) {
                linkedTable = new LinkedDatabaseTableType();
            }
        }
        p.setLinkedTable( linkedTable );
        p.setApplicationContainer( appCont );
        p.setPrevious( this );
        p.setView( rbLinkageView.isSelected() );
        return p;
    }
    
    /* (non-Javadoc)
     * @see org.deegree.igeo.views.swing.linkeddata.AbstractLinkedDataPanel#getDescription()
     */
    String getDescription() {        
        return  Messages.getMessage( getLocale(), "$MD11573" );
    }

}
