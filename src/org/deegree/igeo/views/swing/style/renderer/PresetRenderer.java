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

package org.deegree.igeo.views.swing.style.renderer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import org.deegree.igeo.style.model.Preset;

/**
 * <code>SettingsRenderer</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class PresetRenderer extends JPanel implements ListCellRenderer {

    private static final long serialVersionUID = 231392571539599708L;

    private Border unselectedBorder = null;

    private Border selectedBorder = null;

    private JLabel settingsName = new JLabel();

    private JLabel settingsSymbol = new JLabel();

    public PresetRenderer() {
        setPreferredSize( new Dimension( 55, 30 ) );
        setLayout( new BorderLayout() );
        setBackground( Color.WHITE );
        setBorder( BorderFactory.createEmptyBorder( 2, 2, 2, 2 ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList,
     * java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected,
                                                   boolean cellHasFocus ) {
        Preset ps = (Preset) value;
        if ( isSelected ) {
            if ( selectedBorder == null ) {
                selectedBorder = BorderFactory.createMatteBorder( 2, 5, 2, 5, list.getSelectionBackground() );
            }
            setBorder( selectedBorder );
        } else {
            if ( unselectedBorder == null ) {
                unselectedBorder = BorderFactory.createMatteBorder( 2, 5, 2, 5, list.getBackground() );
            }
            setBorder( unselectedBorder );
        }

        settingsName.setText( ps.getName() );
        settingsSymbol.setIcon( new ImageIcon( ps.getAsImage() ) );
        this.add( settingsName, BorderLayout.NORTH );
        this.add( settingsSymbol, BorderLayout.CENTER );
        return this;
    }
}
