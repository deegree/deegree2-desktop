//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 and
 lat/lon GmbH

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

package org.deegree.desktop.style.perform;

import static org.deegree.desktop.Version.getVersionNumber;
import static org.deegree.desktop.i18n.Messages.get;

import java.awt.Component;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.prefs.Preferences;

import javax.xml.parsers.DocumentBuilder;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.views.swing.util.GenericFileChooser;
import org.deegree.desktop.views.swing.util.DesktopFileFilter;
import org.deegree.desktop.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLTools;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class SldIO {

    private static final ILogger LOG = LoggerFactory.getLogger( SldIO.class );

    private static Preferences prefs = Preferences.userNodeForPackage( SldIO.class );

    /**
     * 
     * @param sld
     *            the sld to export
     * @param appContainer
     *            the applicationContainer
     * @param parent
     *            the parent of the dialog to save the sld as file
     */
    public static void exportSld( String sld, ApplicationContainer<?> appContainer, Component parent ) {

        File file = GenericFileChooser.showSaveDialog( FILECHOOSERTYPE.local, appContainer, parent, prefs, "sld",
                                                       DesktopFileFilter.XML );
        if ( file != null ) {
            if ( file.getParent() != null ) {
                prefs.put( "sld" + getVersionNumber(), file.getParent() );
            }
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( file ), "UTF-8" ) );
                // try to pretty print
                try {
                    DocumentBuilder builder = XMLTools.getDocumentBuilder();
                    Document doc = builder.parse( new ByteArrayInputStream( sld.getBytes() ) );
                    sld = new XMLFragment( (Element) doc.getFirstChild() ).getAsPrettyString();
                } catch ( Exception e ) {
                    LOG.logDebug( "Could not pretty print the sld: {}", e.getMessage() );
                }
                writer.write( sld );
            } catch ( IOException e ) {
                LOG.logError( get( "$DG10097", file ) );
            } finally {
                if ( writer != null ) {
                    try {
                        writer.close();
                    } catch ( IOException e ) {
                        LOG.logError( get( "$DG10098", file ) );
                    }
                }
            }
        }

    }

    /**
     * @param appContainer
     *            the applicationContainer
     * @param parent
     *            the parent of the open dialog
     * @return the xml file, containig the sld to import
     */
    public static File importSld( ApplicationContainer<?> appContainer, Component parent ) {
        return GenericFileChooser.showOpenDialog( FILECHOOSERTYPE.local, appContainer, parent, prefs, "sld",
                                                  DesktopFileFilter.XML );

    }
}
