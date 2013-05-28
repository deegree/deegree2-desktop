//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2013 by:
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
 http://www.lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/
package org.deegree.igeo.views.swing.map;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.modules.CopyCoordinatesModule;
import org.deegree.igeo.state.mapstate.MapTool;

/**
 * Handles MouseEvents for the {@link CopyCoordinatesModule}
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class CopyCoordinatesPanel extends JPanel implements MouseListener {

    private static final long serialVersionUID = -6526681726375085230L;

    private static final ILogger LOG = LoggerFactory.getLogger( CopyCoordinatesPanel.class );

    private final MapTool<?> mapTool;

    private Container mapParent;

    public CopyCoordinatesPanel( MapTool<?> mapTool, Container parent ) {
        this.mapTool = mapTool;
        mapParent = getMapParent( parent );
        if ( mapParent == null ) {
            LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10072" ) );
            return;
        }
        connect();
    }

    @Override
    public void mouseClicked( MouseEvent event ) {
        if ( !event.isPopupTrigger() ) {
            mapTool.getState().mousePressed( event );
        }
    }

    @Override
    public void mousePressed( MouseEvent e ) {
    }

    @Override
    public void mouseReleased( MouseEvent e ) {
    }

    @Override
    public void mouseEntered( MouseEvent e ) {
    }

    @Override
    public void mouseExited( MouseEvent e ) {
    }

    public void connect() {
        if ( mapParent != null ) {
            mapParent.addMouseListener( this );
            mapParent.add( this, 0 );
        }
    }

    public void disconnect() {
        if ( mapParent != null ) {
            mapParent.removeMouseListener( this );
            mapParent.remove( this );
        }
    }

    private Container getMapParent( Container parent ) {
        if ( parent instanceof JFrame ) {
            JFrame viewForm = (JFrame) parent;
            Component[] components = viewForm.getContentPane().getComponents();
            return getMapParent( components );
        } else if ( parent instanceof JInternalFrame ) {
            JInternalFrame viewForm = (JInternalFrame) parent;
            Component[] components = viewForm.getContentPane().getComponents();
            return getMapParent( components );
        }
        return null;
    }

    private Container getMapParent( Component[] components ) {
        for ( int i = 0; i < components.length; i++ ) {
            if ( components[i] instanceof DefaultMapComponent ) {
                return (Container) components[i];
            }
        }
        return null;
    }

}