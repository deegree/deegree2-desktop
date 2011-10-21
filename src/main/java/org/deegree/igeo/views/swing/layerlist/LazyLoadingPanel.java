//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2009 by:
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
package org.deegree.igeo.views.swing.layerlist;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Datasource;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class LazyLoadingPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = -8851050714064945169L;

    private JPanel pnLazyLoad;

    private JCheckBox cbLazyLoad;

    private Datasource datasource;
    
    public LazyLoadingPanel() {
        initGUI();
    }

    /**
     * 
     * @param datasource
     */
    LazyLoadingPanel( Datasource datasource ) {
        this.datasource = datasource;
        initGUI();
    }

    private void initGUI() {
        try {
            BorderLayout thisLayout = new BorderLayout();
            this.setPreferredSize( new java.awt.Dimension( 400, 64 ) );
            this.setLayout( thisLayout );
            {
                pnLazyLoad = new JPanel();
                GridBagLayout pnLazyLoadLayout = new GridBagLayout();
                this.add( pnLazyLoad, BorderLayout.CENTER );
                pnLazyLoad.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11210" ) ) );
                pnLazyLoad.setPreferredSize( new java.awt.Dimension( 400, 62 ) );
                pnLazyLoadLayout.rowWeights = new double[] { 0.1 };
                pnLazyLoadLayout.rowHeights = new int[] { 7 };
                pnLazyLoadLayout.columnWeights = new double[] { 0.1 };
                pnLazyLoadLayout.columnWidths = new int[] { 7 };
                pnLazyLoad.setLayout( pnLazyLoadLayout );
                {
                    cbLazyLoad = new JCheckBox( Messages.getMessage( getLocale(), "$MD11210a" ),
                                                datasource.isLazyLoading() );
                    pnLazyLoad.add( cbLazyLoad, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                        GridBagConstraints.CENTER,
                                                                        GridBagConstraints.HORIZONTAL,
                                                                        new Insets( 0, 10, 0, 10 ), 0, 0 ) );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @return
     */
    boolean isLazyLoading() {
        return cbLazyLoad.isSelected();
    }

}
