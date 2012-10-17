//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2012 by:
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

 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: info@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/

package org.deegree.igeo.views.swing.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.ImageUtils;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.config.EntryValueType;
import org.deegree.igeo.config.OnlineResourceType;
import org.deegree.igeo.config.PopUpEntryType;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.views.swing.ButtonGroup;
import org.deegree.igeo.views.swing.ControlElement;
import org.deegree.igeo.views.swing.PopUpMenu;

/**
 * TODO add class description
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class PopUpRegister {

    private static final ILogger LOG = LoggerFactory.getLogger( PopUpRegister.class );

    /**
     * 
     * @param appContainer
     * @param component
     * @param owner
     * @param popUpController
     * @param popupListener
     * @return popupControl element
     */
    public static ControlElement registerPopups( ApplicationContainer<Container> appContainer, Component component,
                                                 IModule<Container> owner, ControlElement popUpController,
                                                 MouseListener popupListener ) {
        popUpController = addPopupEntries( appContainer, owner.getApplicationContainer().getPopupEntries(), component,
                                           (ActionListener) owner.getApplicationContainer(), popUpController,
                                           popupListener );
        IModule<Container> tmp = owner.getParentModule();
        while ( tmp != null ) {
            popUpController = addPopupEntries( appContainer, tmp.getPopUpEntries(), component, owner, popUpController,
                                               popupListener );
            tmp = tmp.getParentModule();
        }
        return addPopupEntries( appContainer, owner.getPopUpEntries(), component, owner, popUpController, popupListener );
    }

    /**
     * 
     * @param popupEntries
     */
    private static ControlElement addPopupEntries( ApplicationContainer<Container> appContainer,
                                                   List<PopUpEntryType> popupEntries, Component component,
                                                   ActionListener owner, ControlElement popUpController,
                                                   MouseListener popupListener ) {

        PopUpMenu popup = null;
        if ( popUpController == null ) {
            popUpController = new ControlElement( "PopUpMenu" );
            popup = (PopUpMenu) popUpController.getView();
        } else {
            popup = (PopUpMenu) popUpController.getView();
            if ( popup.getComponentCount() > 0
                 && !( popup.getComponent( popup.getComponentCount() - 1 ) instanceof JPopupMenu.Separator ) ) {
                popup.addSeparator();
            }
        }
        if ( popupEntries != null && popupEntries.size() > 0 ) {

            Map<String, ButtonGroup> popUpGroups = appContainer.getButtonGroups();

            for ( PopUpEntryType entry : popupEntries ) {
                JMenuItem menuItem = null;
                if ( entry.getEntryType() == null ) {
                	menuItem = new JMenuItem( Messages.getMessage( component.getLocale(), entry.getName() ) );
                } else if ( entry.getEntryType() == EntryValueType.SIMPLE_ITEM ) {
                    menuItem = new JMenuItem( Messages.getMessage( component.getLocale(), entry.getName() ) );
                } else if ( entry.getEntryType() == EntryValueType.RADIO_BUTTON_ITEM ) {
                    menuItem = new JRadioButtonMenuItem( Messages.getMessage( component.getLocale(), entry.getName() ) );
                    menuItem.setIcon( IconRegistry.getIcon( "radiobutton_unselected.gif" ) );
                    menuItem.setSelectedIcon( IconRegistry.getIcon( "radiobutton_selected.gif" ) );
                } else if ( entry.getEntryType() == EntryValueType.CHECK_BOX_ITEM ) {
                    menuItem = new JCheckBoxMenuItem( Messages.getMessage( component.getLocale(), entry.getName() ) );
                    menuItem.setIcon( IconRegistry.getIcon( "checkbox_unselected.gif" ) );
                    menuItem.setSelectedIcon( IconRegistry.getIcon( "checkbox_selected.gif" ) );
                } else {
                	// TODO is a log message necessary? This case should not happen.
                	menuItem = new JMenuItem( Messages.getMessage( component.getLocale(), entry.getName() ) );
                }
                addIcon( appContainer, entry.getIcon(), menuItem );
                menuItem.setName( entry.getAssignedAction() );
                String groupName = entry.getAssignedGroup();
                if ( groupName != null && groupName.length() > 0 ) {
                    ButtonGroup group = popUpGroups.get( groupName );
                    if ( group == null ) {
                        group = new ButtonGroup();
                        popUpGroups.put( groupName, group );
                    }
                    group.add( menuItem );
                }
                menuItem.addActionListener( owner );

                if ( entry.getTooltip() != null ) {
                    menuItem.setToolTipText( entry.getTooltip() );
                }

                popup.add( menuItem );
            }
            component.addMouseListener( popupListener );
        }
        return popUpController;

    }

    /**
     * 
     * @param icon
     * @param aButton
     */
    private static void addIcon( ApplicationContainer<Container> appContainer, OnlineResourceType icon,
                                 AbstractButton aButton ) {
        if ( icon != null ) {
            String href = icon.getHref();
            addIcon( appContainer, href, aButton );
        }
    }

    private static void addIcon( ApplicationContainer<Container> appContainer, String href, AbstractButton aButton ) {
        LOG.logDebug( "Loading image from URL ", href );
        LOG.logDebug( "Loading image from URL ", href );
        try {
            BufferedImage img = ImageUtils.loadImage( appContainer.resolve( href ) );
            BufferedImage tmp = new BufferedImage( 15, 15, BufferedImage.TYPE_INT_ARGB );
            int x = ( 15 - img.getWidth() ) / 2;
            int y = ( 15 - img.getHeight() ) / 2;
            Graphics g = tmp.getGraphics();
            g.drawImage( img, x, y, null );
            g.dispose();
            aButton.setIcon( new ImageIcon( tmp ) );
        } catch ( MalformedURLException e ) {
            LOG.logError( "Icon href is not a valied URL: " + href, e );
        } catch ( IOException e ) {
            LOG.logError( "Image from URL: " + href + " can not be loaded", e );
        }
    }

}
