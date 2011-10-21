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
package org.deegree.igeo.views.swing.legend;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.deegree.igeo.mapmodel.Layer;

/**
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * @version $Revision$, $Date$
 */
class LegendTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = -2899335131003692328L;

    private Object obj;
    
    private JLabel label;
    
    public LegendTreeCellRenderer() {
        label = new JLabel();
        add( label );
    }

    // must be synchronized because other components may access a layer tree within other threads
    @Override
    public Component getTreeCellRendererComponent( JTree tree, Object value, boolean isSelected, boolean expanded,
                                                   boolean leaf, int row, boolean hasFocus ) {
        synchronized ( tree ) {
            obj = ( (DefaultMutableTreeNode) value ).getUserObject();
            if ( obj instanceof Layer ) {
                BufferedImage bi = ( (Layer) obj ).getLegend();                
                if ( bi.getHeight() > 50 ) {                    
                    label.setText( null );
                } else {
                    label.setText( ( (Layer) obj ).getTitle() );
                }
                label.setIcon( new ImageIcon( bi ) );
                label.setBorder( BorderFactory.createEmptyBorder( 3, 3, 3, 3 ) );
                return label;
            } else {
                return super.getTreeCellRendererComponent( tree, value, isSelected, expanded, leaf, row, hasFocus );
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if ( obj instanceof Layer ) {
            BufferedImage bi = ( (Layer) obj ).getLegend();
            return new Dimension( bi.getWidth(), bi.getHeight() );
        } else {
            return super.getPreferredSize();
        }
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

}