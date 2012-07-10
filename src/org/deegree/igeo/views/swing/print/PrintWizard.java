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

import static java.lang.Integer.parseInt;
import static org.deegree.framework.log.LoggerFactory.getLogger;
import static org.deegree.igeo.i18n.Messages.get;
import static org.deegree.igeo.views.DialogFactory.openErrorDialog;
import static org.deegree.igeo.views.swing.print.PrintActions.optionsAction;
import static org.deegree.igeo.views.swing.print.PrintActions.printAction;
import static org.deegree.igeo.views.swing.print.PrintActions.selectTemplateAction;
import static org.deegree.igeo.views.swing.util.GuiUtils.unknownError;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import javax.swing.JPanel;

import org.deegree.framework.log.ILogger;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.PrintModule;
import org.deegree.igeo.views.swing.print.SelectTemplatePanel.Template;
import org.deegree.igeo.views.swing.util.wizard.Wizard;

/**
 * <code>PrintWizard</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * @param <T>
 */
public class PrintWizard<T> extends Wizard {

    private static final long serialVersionUID = -8677502061819948282L;

    private static final ILogger LOG = getLogger( PrintWizard.class );

    /**
     * This is the actual constructor.
     * 
     * @param module
     */
    public void setInitParameters( PrintModule<T> module ) {
        try {
            Map<String, String> map = module.getInitParameters();
            String dpis = map.get( "dpi" );
            int dpi;
            try {
                dpi = dpis == null ? 300 : parseInt( dpis );
            } catch ( NumberFormatException nfe ) {
                LOG.logWarning( "DPI init parameter had invalid value, using default of 300 DPI." );
                dpi = 300;
            }
            String tmp = map.get( "wmsSizeScale" );
            double wmsSizeScale = 1;
            if ( tmp != null ) {
                wmsSizeScale = Double.parseDouble( tmp );                
            }

            final MapModel mm = module.getApplicationContainer().getMapModel( null );

            LinkedList<JPanel> panels = new LinkedList<JPanel>();
            LinkedList<Action> actions = new LinkedList<Action>();

            Vector<Template> templates = module.extractTemplates();

            if ( templates.size() == 0 ) {
                openErrorDialog( "Application", this, get( "$MD10350" ), get( "$DI10017" ) );
                return;
            }

            final PrintOptionsPanel optionsPanel = new PrintOptionsPanel();
            final JasperReportPanel reportPanel = new JasperReportPanel();

            SelectTemplatePanel fromList = new SelectTemplatePanel();
            fromList.list.setListData( templates );

            if ( templates.size() == 1 ) {
                fromList.list.setSelectedIndex( 0 );
                if ( !selectTemplateAction( fromList, reportPanel, optionsPanel, dpi ).forward() ) {
                    openErrorDialog( "Application", this, get( "$MD10351" ), get( "$DI10017" ) );
                    return;
                }
            } else {
                panels.add( fromList );
                actions.add( selectTemplateAction( fromList, reportPanel, optionsPanel, dpi ) );
            }

            panels.add( optionsPanel );
            actions.add( optionsAction( optionsPanel ) );

            panels.add( reportPanel );
            actions.add( printAction( reportPanel, mm, optionsPanel, dpi, wmsSizeScale ) );

            init( panels, actions, false );

        } catch ( IOException e ) {
            unknownError( LOG, e, this );
        }
    }

}
