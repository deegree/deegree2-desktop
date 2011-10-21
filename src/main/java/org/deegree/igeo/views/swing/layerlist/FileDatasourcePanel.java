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

import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.FileDatasource;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class FileDatasourcePanel extends DatasourceBasePanel {
    private static final long serialVersionUID = -2578513143660613092L;

    private JPanel pnFile;

    private LazyLoadingPanel lazyLoadingPanel1;

    private JTextField tfFilename;

    private FileDatasource datasource;

    /**
     * 
     * @param datasource
     */
    public FileDatasourcePanel( FileDatasource datasource ) {
        this.datasource = datasource;
        initGUI();
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            this.setPreferredSize( new java.awt.Dimension( 381, 293 ) );
            thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.1 };
            thisLayout.rowHeights = new int[] { 171, 61, 7 };
            thisLayout.columnWeights = new double[] { 0.1 };
            thisLayout.columnWidths = new int[] { 7 };
            this.setLayout( thisLayout );
            {
                pnFile = new JPanel();
                GridBagLayout pnFileLayout = new GridBagLayout();
                this.add( pnFile, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                          GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnFile.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD10082" ) ) );
                pnFileLayout.rowWeights = new double[] { 0.1 };
                pnFileLayout.rowHeights = new int[] { 7 };
                pnFileLayout.columnWeights = new double[] { 0.1 };
                pnFileLayout.columnWidths = new int[] { 7 };
                pnFile.setLayout( pnFileLayout );
                {
                    tfFilename = new JTextField( datasource.getFile().getAbsolutePath() );
                    tfFilename.setEditable( false );
                    pnFile.add( tfFilename, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                    GridBagConstraints.HORIZONTAL, new Insets( 0, 10,
                                                                                                               0, 10 ),
                                                                    0, 0 ) );
                }
            }
            {
                lazyLoadingPanel1 = new LazyLoadingPanel( datasource );
                this.add( lazyLoadingPanel1, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                     GridBagConstraints.HORIZONTAL, new Insets( 0, 0,
                                                                                                                0, 0 ),
                                                                     0, 0 ) );
            }
            {
                datasourceCorePanel = new DatasourceCorePanel( datasource );
                this.add( datasourceCorePanel, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                       GridBagConstraints.BOTH,
                                                                       new Insets( 0, 0, 0, 0 ), 0, 0 ) );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @return file name
     */
    String getFileName() {
        return tfFilename.getText();
    }

    /**
     * 
     * @return <code>true</code> if data source is lazy loading
     */
    boolean isLazyLoading() {
        return lazyLoadingPanel1.isLazyLoading();
    }

}
