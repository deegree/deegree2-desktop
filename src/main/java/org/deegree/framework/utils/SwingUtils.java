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

package org.deegree.framework.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;

/**
 * <code>SwingUtils</code> useful to layout swing components.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class SwingUtils {

    /**
     * @param panel
     * @return pre-configured constraints
     */
    public static GridBagConstraints initPanel( JPanel panel ) {
        panel.setLayout( new GridBagLayout() );
        panel.setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
        GridBagConstraints gb = new GridBagConstraints();
        gb.gridx = 0;
        gb.gridy = 0;
        gb.insets = new Insets( 2, 2, 2, 2 );
        return gb;
    }

    /**
     * @param scrollPane
     * @return
     */
    public static GridBagConstraints initScrollPane( JScrollPane scrollPane ) {
        scrollPane.setLayout( new ScrollPaneLayout() );
        scrollPane.setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
        GridBagConstraints gb = new GridBagConstraints();
        gb.gridx = 0;
        gb.gridy = 0;
        gb.insets = new Insets( 2, 2, 2, 2 );
        return gb;
    }

    public static void addComponent( Container container, GridBagLayout gbl, Component c, int x, int y, int width,
                                     int height, double weightx, double weighty, int fill ) {
        addComponent( container, gbl, c, x, y, width, height, weightx, weighty, GridBagConstraints.CENTER, 0, 0, fill );

    }

    public static void addComponent( Container container, GridBagLayout gbl, Component c, int x, int y, int width,
                                     int height, int fill ) {
        addComponent( container, gbl, c, x, y, width, height, 1, 1, GridBagConstraints.CENTER, 0, 0, fill );

    }

    public static void addComponent( Container container, GridBagLayout gbl, Component c, int x, int y, int width,
                                     int height, double weightx, double weighty, int anchor, int ipadx, int ipady,
                                     int fill ) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = fill;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.anchor = anchor;
        gbc.ipadx = ipadx;
        gbc.ipady = ipady;
        gbc.insets = new Insets( 2, 2, 2, 2 );
        gbl.setConstraints( c, gbc );
        container.add( c );
    }

}
