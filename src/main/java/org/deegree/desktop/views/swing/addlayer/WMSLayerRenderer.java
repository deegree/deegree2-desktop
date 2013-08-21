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

package org.deegree.desktop.views.swing.addlayer;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.deegree.desktop.views.swing.util.IconRegistry;
import org.deegree.ogcwebservices.wms.capabilities.Layer;

/**
 * The <code>WMSLayerRenderer</code> renders a WMS-Layer as tree node
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class WMSLayerRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 8919285589498809986L;

    @Override
    public Component getTreeCellRendererComponent( JTree tree, Object value, boolean sel, boolean expanded,
                                                   boolean leaf, int row, boolean hasFocus ) {
        super.getTreeCellRendererComponent( tree, value, sel, expanded, leaf, row, hasFocus );

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        if ( node.getUserObject() instanceof Layer ) {

            Layer layerEntry = (Layer) node.getUserObject();

            String icon = "/org/deegree/desktop/views/images/status1.gif";
            if ( layerEntry.getName() == null || !( layerEntry.getName().length() > 0 ) ) {
                icon = "/org/deegree/desktop/views/images/status3.gif";
            } else if ( !layerEntry.isQueryable() ) {
                icon = "/org/deegree/desktop/views/images/status2.gif";
            }
            
            setIcon( IconRegistry.getIcon( icon ) );

            setText( layerEntry.getTitle() );
            setToolTipText( layerEntry.getAbstract() );
        }

        return this;
    }
}
