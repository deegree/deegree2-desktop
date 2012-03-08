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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.deegree.igeo.dataadapter.LinkedFileTable;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.swing.util.GenericFileChooser;
import org.deegree.igeo.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;
import org.deegree.igeo.views.swing.util.IGeoFileFilter;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
class FileSelectPanel extends AbstractLinkedDataPanel {

    private static final long serialVersionUID = 3143762864735119326L;

    private JPanel pnFileSelect;

    private JButton btOpenFileDlg;

    private JTextField tfFileName;

    /**
     * 
     */
    FileSelectPanel() {
        initGUI();
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            setPreferredSize( new Dimension( 400, 300 ) );
            thisLayout.rowWeights = new double[] { 0.1, 0.1, 0.1 };
            thisLayout.rowHeights = new int[] { 7, 7, 7 };
            thisLayout.columnWeights = new double[] { 0.1 };
            thisLayout.columnWidths = new int[] { 7 };
            this.setLayout( thisLayout );
            {
                pnFileSelect = new JPanel();
                GridBagLayout pnFileSelectLayout = new GridBagLayout();
                this.add( pnFileSelect,
                          new GridBagConstraints( 0, 0, 1, 3, 0.0, 0.0, GridBagConstraints.CENTER,
                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnFileSelect.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11548" ) ) );
                pnFileSelectLayout.rowWeights = new double[] { 0.0, 0.0, 0.1, 0.1 };
                pnFileSelectLayout.rowHeights = new int[] { 39, 40, 7, 7 };
                pnFileSelectLayout.columnWeights = new double[] { 0.0, 0.1 };
                pnFileSelectLayout.columnWidths = new int[] { 266, 7 };
                pnFileSelect.setLayout( pnFileSelectLayout );
                {
                    tfFileName = new JTextField();
                    pnFileSelect.add( tfFileName, new GridBagConstraints( 0, 0, 2, 1, 0.0, 0.0,
                                                                          GridBagConstraints.SOUTH,
                                                                          GridBagConstraints.HORIZONTAL,
                                                                          new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                }
                {
                    btOpenFileDlg = new JButton( Messages.getMessage( getLocale(), "$MD11549" ) );
                    pnFileSelect.add( btOpenFileDlg, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                                                             GridBagConstraints.WEST,
                                                                             GridBagConstraints.NONE,
                                                                             new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                    btOpenFileDlg.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            File file = GenericFileChooser.showOpenDialog( FILECHOOSERTYPE.geoDataFile,
                                                                           null,
                                                                           FileSelectPanel.this,
                                                                           Preferences.systemNodeForPackage( FileSelectPanel.class ),
                                                                           "LOCATION",
                                                                           IGeoFileFilter.createForExtensions() );
                            if ( file != null ) {
                                tfFileName.setText( file.getAbsolutePath() );
                            }

                        }
                    } );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.linkeddata.AbstractLinkedDataPanel#getNext()
     */
    AbstractLinkedDataPanel getNext() {
        if ( tfFileName.getText() == null || tfFileName.getText().trim().length() == 0 ) {
            DialogFactory.openWarningDialog( appCont.getViewPlatform(), this,
                                             Messages.getMessage( getLocale(), "$MD11550" ),
                                             Messages.getMessage( getLocale(), "$MD11551" ) );
            return null;
        }
        ( (LinkedFileTable) linkedTable ).setEditable( false );
        ( (LinkedFileTable) linkedTable ).setFile( tfFileName.getText() );
        AbstractLinkedDataPanel p = new DefineKeysPanel( appCont, linkedTable );
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
        return Messages.getMessage( getLocale(), "$MD11574" );
    }
}
