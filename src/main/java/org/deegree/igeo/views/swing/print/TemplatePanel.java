//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2007 by:
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

package org.deegree.igeo.views.swing.print;

import static java.util.prefs.Preferences.userNodeForPackage;
import static org.deegree.igeo.i18n.Messages.get;
import static org.deegree.igeo.views.DialogFactory.openInformationDialog;
import static org.deegree.igeo.views.swing.util.GenericFileChooser.showOpenDialog;
import static org.deegree.igeo.views.swing.util.GuiUtils.initPanel;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.views.swing.util.IGeoFileFilter;
import org.deegree.igeo.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;
import org.deegree.igeo.views.swing.util.panels.PanelDialog.OkCheck;

/**
 * <code>TemplatePanel</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class TemplatePanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = -753313513375534635L;
    
    private static final ILogger LOG = LoggerFactory.getLogger( TemplatePanel.class );

    JTextField title;

    JTextField templateLocation;

    JTextField imageLocation;

    private JButton browseTempl, browseImg;

    final OkCheck okCheck = new OkCheck() {
        public boolean isOk() {
            if ( title.getText().indexOf( ';' ) != -1 ) {
                openInformationDialog( "Application", TemplatePanel.this, get( "$MD10385" ), get( "$DI10018" ) );
                return false;
            }

            String tmpl = templateLocation.getText();
            String imgl = imageLocation.getText();
            try {
                new URL( tmpl );
            } catch ( MalformedURLException e ) {
                openInformationDialog( "Application", TemplatePanel.this, get( "$MD10383" ), get( "$DI10018" ) );
                return false;
            }
            try {
                if ( !imgl.equals( "" ) ) {
                    new URL( imgl );
                }
                return true;
            } catch ( MalformedURLException e ) {
                openInformationDialog( "Application", TemplatePanel.this, get( "$MD10384" ), get( "$DI10018" ) );
                return false;
            }
        }
    };

    /**
     * 
     */
    public TemplatePanel() {
        GridBagConstraints gb = initPanel( this );

        browseTempl = new JButton( get( "$DI10021" ) );
        browseImg = new JButton( get( "$DI10021" ) );
        browseTempl.addActionListener( this );
        browseImg.addActionListener( this );

        title = new JTextField( 20 );
        templateLocation = new JTextField( 20 );
        imageLocation = new JTextField( 20 );

        gb.anchor = GridBagConstraints.WEST;

        add( new JLabel( get( "$DI10032" ) + ":" ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10376" ) + ":" ), gb );
        ++gb.gridy;
        add( new JLabel( get( "$MD10377" ) + ":" ), gb );

        gb.gridy = 0;
        ++gb.gridx;

        gb.gridwidth = 2;
        gb.fill = GridBagConstraints.HORIZONTAL;
        add( title, gb );
        gb.gridwidth = 1;
        ++gb.gridy;
        add( templateLocation, gb );
        ++gb.gridx;
        add( browseTempl, gb );
        --gb.gridx;
        ++gb.gridy;
        add( imageLocation, gb );
        ++gb.gridx;
        add( browseImg, gb );
    }

    public void actionPerformed( ActionEvent e ) {
        JTextField field = null;
        String key = null;
        IGeoFileFilter[] filters = null;

        if ( e.getSource() == browseTempl ) {
            field = templateLocation;
            key = "lastTemplate";
            filters = new IGeoFileFilter[] { IGeoFileFilter.JASPER };
        }

        if ( e.getSource() == browseImg ) {
            field = imageLocation;
            key = "lastImage";
            filters = new IGeoFileFilter[] { IGeoFileFilter.TIFF, IGeoFileFilter.GIF, IGeoFileFilter.BMP,
                                            IGeoFileFilter.PNG, IGeoFileFilter.JPEG, IGeoFileFilter.IMAGES };
        }

        if ( field != null ) {
            File f = showOpenDialog( FILECHOOSERTYPE.image, null, this, userNodeForPackage( TemplatePanel.class ), key,
                                     filters );
            if ( f == null ) {
                return;
            }
            try {
                field.setText( f.toURI().toURL().toExternalForm() );
            } catch ( MalformedURLException ex ) {
                // cannot happen for file urls
                LOG.logWarning( "ignore", ex );
            }
        }
    }

    @Override
    public String toString() {
        return get( "$MD10382" );
    }

}
