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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.deegree.igeo.mapmodel.WMSDatasource;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
class WMSDatasourcePanel extends DatasourceBasePanel {

    private static final long serialVersionUID = -1068427385504021071L;

    private JPanel pnCapa;

    private JPanel pnBaseReq;    

    private JTextField tfBaseReq;

    private JTextField tfCapa;

    private WMSDatasource datasource;

    /**
     * 
     * @param datasource
     */
    WMSDatasourcePanel( WMSDatasource datasource ) {
        this.datasource = datasource;
        initGUI();
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            this.setPreferredSize( new java.awt.Dimension( 400, 318 ) );
            thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.1 };
            thisLayout.rowHeights = new int[] { 170, 73, 7 };
            thisLayout.columnWeights = new double[] { 0.1 };
            thisLayout.columnWidths = new int[] { 7 };
            this.setLayout( thisLayout );
            {
                pnCapa = new JPanel();
                GridBagLayout pnCapaLayout = new GridBagLayout();
                this.add( pnCapa, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                          GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnCapa.setBorder( BorderFactory.createTitledBorder( "WMS Capabilities" ) );
                pnCapaLayout.rowWeights = new double[] { 0.1 };
                pnCapaLayout.rowHeights = new int[] { 7 };
                pnCapaLayout.columnWeights = new double[] { 0.1 };
                pnCapaLayout.columnWidths = new int[] { 7 };
                pnCapa.setLayout( pnCapaLayout );
                {
                    tfCapa = new JTextField();
                    pnCapa.add( tfCapa, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                GridBagConstraints.HORIZONTAL,
                                                                new Insets( 0, 10, 0, 10 ), 0, 0 ) );
                    tfCapa.setText( datasource.getCapabilitiesURL().toURI().toASCIIString() );
                }
            }
            {
                pnBaseReq = new JPanel();
                GridBagLayout pnBaseReqLayout = new GridBagLayout();
                this.add( pnBaseReq, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                             GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnBaseReq.setBorder( BorderFactory.createTitledBorder( "base request" ) );
                pnBaseReqLayout.rowWeights = new double[] { 0.1 };
                pnBaseReqLayout.rowHeights = new int[] { 7 };
                pnBaseReqLayout.columnWeights = new double[] { 0.1 };
                pnBaseReqLayout.columnWidths = new int[] { 7 };
                pnBaseReq.setLayout( pnBaseReqLayout );
                {
                    tfBaseReq = new JTextField();
                    pnBaseReq.add( tfBaseReq, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                      GridBagConstraints.HORIZONTAL,
                                                                      new Insets( 0, 10, 0, 10 ), 0, 0 ) );
                    tfBaseReq.setText( datasource.getBaseRequest() );
                }
            }
            {
                datasourceCorePanel = new DatasourceCorePanel( datasource );
                this.add( datasourceCorePanel, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                        GridBagConstraints.CENTER,
                                                                        GridBagConstraints.BOTH,
                                                                        new Insets( 0, 0, 0, 0 ), 0, 0 ) );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @return capabilities URL
     */
    String getCapabilitiesURL() {
        return tfCapa.getText();
    }

    /**
     * 
     * @return GetMap base request
     */
    String getBaseRequest() {
        return tfBaseReq.getText();
    }

}
