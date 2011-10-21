//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2008 by:
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

package org.deegree.igeo.views.swing.map;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentEvent;

import org.deegree.igeo.ChangeListener;
import org.deegree.igeo.ValueChangedEvent;
import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.views.ComponentPosition;
import org.deegree.igeo.views.swing.DefaultFrame;
import org.deegree.igeo.views.swing.Footer;

/**
 * 
 * <code>DefaultMapFrame</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class DefaultMapFrame extends DefaultFrame implements ChangeListener {

    private static final long serialVersionUID = -1276976914247672237L;

    private DefaultMapComponent dmc;

    // /////////////////////////////////////////////////////////////////////////////////
    // DefaultFrame
    // /////////////////////////////////////////////////////////////////////////////////

    @Override
    public void init( ViewFormType viewForm )
                            throws Exception {
        super.init( viewForm );

        // use the DefaultMapComponent to show the map!
        dmc = new DefaultMapComponent();
        dmc.registerModule( this.owner );
        Footer footer = new Footer();
        dmc.setPrivateFooter( footer );
        dmc.init( viewForm );

        Container contentPane = getContentPane();
        contentPane.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets( 0, 0, 0, 0 );
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPane.add( dmc, gbc );

        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPane.add( footer, gbc );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.DefaultFrame#componentResized(java.awt.event.ComponentEvent)
     */
    @Override
    public void componentResized( ComponentEvent e ) {
        int width = e.getComponent().getWidth();
        int height = e.getComponent().getHeight();

        // size of the module is the same as the size of the map component!
        for ( int i = 0; i < getComponentCount(); i++ ) {
            if ( getComponent( i ) instanceof DefaultMapComponent ) {
                width = getComponent( i ).getWidth();
                height = getComponent( i ).getHeight();
                break;
            }
        }
        ComponentPosition compPos = this.owner.getComponentPositionAdapter();
        if ( compPos.hasWindow() ) {
            compPos.setWindowSize( width, height );
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // ChangeListener
    // /////////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.ChangeListener#valueChanged(org.deegree.client.presenter.ValueChangedEvent)
     */
    public void valueChanged( ValueChangedEvent event ) {
        dmc.valueChanged( event );
    }

}
