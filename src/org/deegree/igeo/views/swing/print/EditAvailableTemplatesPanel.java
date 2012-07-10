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

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NORTH;
import static org.deegree.framework.log.LoggerFactory.getLogger;
import static org.deegree.framework.util.CollectionUtils.collectionToString;
import static org.deegree.framework.util.CollectionUtils.map;
import static org.deegree.igeo.i18n.Messages.get;
import static org.deegree.igeo.views.DialogFactory.openConfirmDialogYESNO;
import static org.deegree.igeo.views.swing.util.GuiUtils.getOwnerFrame;
import static org.deegree.igeo.views.swing.util.GuiUtils.initPanel;
import static org.deegree.igeo.views.swing.util.GuiUtils.unknownError;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.util.CollectionUtils.Mapper;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.views.swing.util.panels.PanelDialog;
import org.deegree.igeo.views.swing.util.panels.SelectFromListPanel;

/**
 * <code>EditAvailableTemplatesPanel</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * @param <T>
 */
public class EditAvailableTemplatesPanel<T> extends JPanel implements ActionListener {

    private static final ILogger LOG = getLogger( EditAvailableTemplatesPanel.class );

    private static final long serialVersionUID = -976650390820219492L;

    private JButton add, edit, remove, up, down;

    private Vector<Template> templatesList;

    private JList templates;

    /**
     * @param module
     * 
     */
    public EditAvailableTemplatesPanel( IModule<T> module ) {

        GridBagConstraints gb = initPanel( this );
        SelectFromListPanel fromList = new SelectFromListPanel( get( "$MD10366" ) );

        templatesList = new Vector<Template>();

        // parse the templates string from init parameters
        for ( String s : module.getInitParameter( "templates" ).split( "," ) ) {
            Template t = new Template();
            templatesList.add( t );

            String[] tmp = s.split( ";" );
            try {
                t.location = module.getApplicationContainer().resolve( tmp[1].trim() );
            } catch ( MalformedURLException e ) {
                unknownError( LOG, e, this );
            }
            t.title = tmp[0].trim();
            if ( tmp.length == 3 ) {
                try {
                    t.imageLocation = module.getApplicationContainer().resolve( tmp[2].trim() );
                } catch ( MalformedURLException e ) {
                    unknownError( LOG, e, this );
                }
            }
        }

        add( fromList, gb );
        fromList.list.setListData( templatesList );
        templates = fromList.list;

        ++gb.gridx;

        JPanel buttons = new JPanel();
        GridBagConstraints gb2 = initPanel( buttons );

        add = new JButton( get( "$DI10029" ) );
        edit = new JButton( get( "$DI10030" ) );
        remove = new JButton( get( "$DI10031" ) );
        up = new JButton( get( "$DI10033" ) );
        down = new JButton( get( "$DI10034" ) );
        add.addActionListener( this );
        edit.addActionListener( this );
        remove.addActionListener( this );
        up.addActionListener( this );
        down.addActionListener( this );

        gb2.fill = HORIZONTAL;
        buttons.add( add, gb2 );
        ++gb2.gridy;
        buttons.add( edit, gb2 );
        ++gb2.gridy;
        buttons.add( remove, gb2 );
        ++gb2.gridy;
        buttons.add( up, gb2 );
        ++gb2.gridy;
        buttons.add( down, gb2 );

        gb.anchor = NORTH;
        add( buttons, gb );

    }

    private static Template getTemplate( TemplatePanel panel, Template t ) {
        if ( t == null ) {
            t = new Template();
        }
        t.title = panel.title.getText();
        try {
            t.location = new URL( panel.templateLocation.getText() );
            if ( !panel.imageLocation.getText().equals( "" ) ) {
                t.imageLocation = new URL( panel.imageLocation.getText() );
            }
        } catch ( MalformedURLException mfe ) {
            // cannot happen, dialog has an ok check ensuring correct URLs
        }
        return t;
    }

    /**
     * @return the templates list as String to be used as init parameter
     */
    public String getTemplatesString() {
        return collectionToString( map( templatesList, TemplateSerializer ), ",\n" );
    }

    private static final Mapper<String, Template> TemplateSerializer = new Mapper<String, Template>() {
        public String apply( Template u ) {
            if ( u.imageLocation == null ) {
                return u.title + "; " + u.location;
            }
            return u.title + "; " + u.location + "; " + u.imageLocation;
        }
    };

    public void actionPerformed( ActionEvent e ) {
        if ( e.getSource() == add ) {
            TemplatePanel panel = new TemplatePanel();
            PanelDialog dlg = new PanelDialog( (JFrame) getOwnerFrame( this ), panel, panel.okCheck, true );
            dlg.setVisible( true );
            if ( dlg.clickedOk ) {
                Template t = getTemplate( panel, null );
                templatesList.add( t );
                templates.updateUI();
            }
        }

        if ( e.getSource() == edit ) {
            if ( templates.getSelectedValue() == null ) {
                return;
            }
            Template t = (Template) templates.getSelectedValue();
            TemplatePanel panel = new TemplatePanel();
            panel.title.setText( t.title );
            panel.templateLocation.setText( t.location.toExternalForm() );
            if ( t.imageLocation != null ) {
                panel.imageLocation.setText( t.imageLocation.toExternalForm() );
            }
            PanelDialog dlg = new PanelDialog( (JFrame) getOwnerFrame( this ), panel, panel.okCheck, true );
            dlg.setVisible( true );
            if ( dlg.clickedOk ) {
                getTemplate( panel, t );
                templates.updateUI();
            }
        }

        if ( e.getSource() == remove ) {
            if ( templates.getSelectedValue() == null ) {
                return;
            }

            if ( openConfirmDialogYESNO( "Application", this, get( "$MD10375" ), get( "$DI10019" ) ) ) {
                for ( Object obj : templates.getSelectedValues() ) {
                    templatesList.remove( obj );
                    templates.updateUI();
                }
            }
        }

        if ( e.getSource() == up ) {
            if ( templates.getSelectedValue() == null ) {
                return;
            }

            int i = templates.getSelectedIndex();
            if ( i != 0 ) {
                Template t = templatesList.remove( i );
                templatesList.add( i - 1, t );
                templates.setSelectedIndex( i - 1 );
                updateUI();
            }
        }

        if ( e.getSource() == down ) {
            if ( templates.getSelectedValue() == null ) {
                return;
            }

            int i = templates.getSelectedIndex();
            if ( i != templatesList.size() - 1 ) {
                Template t = templatesList.remove( i );
                templatesList.add( i + 1, t );
                templates.setSelectedIndex( i + 1 );
                updateUI();
            }
        }

    }

    @Override
    public String toString() {
        return get( "$MD10366" );
    }

    /**
     * <code>Template</code>
     * 
     * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    public static class Template {

        /**
         * 
         */
        public URL location;

        /**
         * 
         */
        public URL imageLocation;

        /**
         * 
         */
        public String title;

        @Override
        public String toString() {
            return title;
        }

    }

}
