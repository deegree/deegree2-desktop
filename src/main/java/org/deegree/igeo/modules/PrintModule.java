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

package org.deegree.igeo.modules;

import static javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
import static org.deegree.framework.log.LoggerFactory.getLogger;
import static org.deegree.igeo.i18n.Messages.get;
import static org.deegree.igeo.views.DialogFactory.openInformationDialog;
import static org.deegree.igeo.views.swing.util.GuiUtils.showErrorMessage;

import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.deegree.framework.log.ILogger;
import org.deegree.igeo.desktop.IGeoDesktop;
import org.deegree.igeo.modules.ActionDescription.ACTIONTYPE;
import org.deegree.igeo.views.swing.print.DirectPrinter;
import org.deegree.igeo.views.swing.print.EditAvailableTemplatesPanel;
import org.deegree.igeo.views.swing.print.PrintWizard;
import org.deegree.igeo.views.swing.print.SelectTemplatePanel;
import org.deegree.igeo.views.swing.print.VectorPrintDialog;
import org.deegree.igeo.views.swing.print.SelectTemplatePanel.Template;
import org.deegree.igeo.views.swing.util.panels.PanelDialog;

/**
 * Module for printing a map. Three options are supported:
 * <ul>
 * <li>1. using JasperReport templates for creating PDF documents
 * <li>2. direct print on a printer
 * <li>3. creating PDF document with vector graphics using iText
 * </ul>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * @param <T>
 */
public class PrintModule<T> extends DefaultModule<T> {

    private static final ILogger LOG = getLogger( PrintModule.class );

    static {
        ActionDescription ad1 = new ActionDescription( "print", "starts wizzard for printing current map", null,
                                                       "starts wizzard for printing current map",
                                                       ACTIONTYPE.PushButton, null, null );
        ActionDescription ad2 = new ActionDescription( "editSettings",
                                                       "opens dialog for editing settings of print module", null,
                                                       "editing settings of print module", ACTIONTYPE.PushButton, null,
                                                       null );
        ActionDescription ad3 = new ActionDescription(
                                                       "editTemplates",
                                                       "starts iReport for adding new or editing existing print templates",
                                                       null, "start iReport", ACTIONTYPE.PushButton, null, null );
        ActionDescription ad4 = new ActionDescription( "directPrint",
                                                       "starts printing current map onto an available print divice",
                                                       null,
                                                       "starts printing current map onto an available print divice",
                                                       ACTIONTYPE.PushButton, null, null );
        moduleCapabilities = new ModuleCapabilities( ad1, ad2, ad3, ad4 );
    }

    /**
     * @return a list of templates defined in the configuration
     * @throws MalformedURLException
     * @throws IOException
     */
    public Vector<Template> extractTemplates()
                            throws MalformedURLException, IOException {
        Map<String, String> map = getInitParameters();

        Vector<Template> templates = new Vector<Template>();

        BufferedImage noimage = ImageIO.read( getApplicationContainer().resolve( map.get( "noTemplateImage" ) ) );

        for ( String s : map.get( "templates" ).split( "," ) ) {
            Template t = new Template();
            templates.add( t );

            String[] tmp = s.split( ";" );
            t.location = getApplicationContainer().resolve( tmp[1].trim() );
            t.title = tmp[0].trim();
            if ( tmp.length == 3 ) {
                t.image = ImageIO.read( getApplicationContainer().resolve( tmp[2].trim() ) );
            } else {
                t.image = noimage;
            }
        }

        return templates;
    }

    /**
     * The printing entry method.
     * 
     */
    public void print() {
        PrintWizard<T> w = new PrintWizard<T>();
        w.setInitParameters( this );
        w.setVisible( true );
    }

    /**
     * The printing entry method.
     * 
     */
    public void directPrint() {
        DirectPrinter p = new DirectPrinter( appContainer.getMapModel( null ) );
        p.print();
    }

    /**
     * 
     */
    public void openVectorPrint() {
        VectorPrintDialog dlg = new VectorPrintDialog( null, appContainer );
        dlg.setVisible( true );
    }

    /**
     * 
     */
    public void editSettings() {
        Container parent = ( (IGeoDesktop) appContainer ).getMainWndow();

        EditAvailableTemplatesPanel<T> panel = new EditAvailableTemplatesPanel<T>( this );

        PanelDialog dlg;
        if ( parent == null ) {
            dlg = new PanelDialog( panel, true );
        } else if ( parent instanceof JFrame ) {
            dlg = new PanelDialog( (JFrame) parent, panel, true );
        } else {
            dlg = new PanelDialog( (JDialog) parent, panel, true );
        }

        dlg.setVisible( true );

        if ( dlg.clickedOk ) {
            setInitParameter( "templates", panel.getTemplatesString() );
            openInformationDialog( "Application", dlg, get( "$MD10386" ), get( "$DI10018" ) );
        }
    }

    /**
     * 
     */
    public void editTemplates() {
        Container parent = ( (IGeoDesktop) appContainer ).getMainWndow();

        try {
            String os = System.getProperty( "os.name" );
            String script;
            if ( os.equalsIgnoreCase( "linux" ) ) {
                script = "startup.sh";
            } else {
                script = "startup.bat";
            }
            String cmd = getApplicationContainer().resolve( getInitParameter( "ireportLocation" ) ).getFile();
            File f = new File( cmd, "bin/" + script );
            List<String> cmds = new LinkedList<String>();
            cmds.add( f.toString() );

            Vector<Template> templates = extractTemplates();
            if ( !templates.isEmpty() ) {
                SelectTemplatePanel selectPanel = new SelectTemplatePanel();
                selectPanel.list.setListData( templates );
                selectPanel.list.setSelectionMode( MULTIPLE_INTERVAL_SELECTION );
                PanelDialog dlg;
                if ( parent == null ) {
                    dlg = new PanelDialog( selectPanel, true );
                } else if ( parent instanceof JFrame ) {
                    dlg = new PanelDialog( (JFrame) parent, selectPanel, true );
                } else {
                    dlg = new PanelDialog( (JDialog) parent, selectPanel, true );
                }
                dlg.setVisible( true );
                if ( dlg.clickedOk ) {
                    for ( Object val : selectPanel.list.getSelectedValues() ) {
                        cmds.add( ( (Template) val ).location.getFile() );
                    }
                } else {
                    return;
                }
            }

            ProcessBuilder pb = new ProcessBuilder( cmds );
            Process p = pb.start();
            if ( LOG.isDebug() ) {
                BufferedReader in = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
                String s;
                while ( ( s = in.readLine() ) != null ) {
                    LOG.logDebug( s );
                }
                in.close();
            }
        } catch ( IOException e ) {
            LOG.logError( e.getMessage(), e );
            showErrorMessage( parent, get( "$MD10374" ), e );
        }
    }

}
