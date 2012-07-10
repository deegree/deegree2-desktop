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
package org.deegree.igeo.views.swing.layerlist;

import java.awt.Dimension;

import javax.swing.tree.TreeNode;

import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.views.swing.layerlist.DnDJTree.TreeLabel;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
class LayerNode extends CheckNode {

    private static final long serialVersionUID = 8694370724196179854L;

    private TreeLabel treeLabel;

    /**
     * 
     * @param userObject
     * @param nodeType
     */
    public LayerNode( Layer userObject, NODE_TYPE nodeType ) {
        this( userObject, nodeType, true, false );
    }

    /**
     * 
     * @param userObject
     * @param nodeType
     * @param allowsChildren
     * @param isSelected
     */
    public LayerNode( Layer userObject, NODE_TYPE nodeType, boolean allowsChildren, boolean isSelected ) {
        super( userObject, allowsChildren );
        this.selected = isSelected;
        this.nodeType = nodeType;
        treeLabel = new TreeLabel( userObject );
        treeLabel.setFocusable( true );
        treeLabel.setOpaque( false );
        treeLabel.setMinimumSize( new Dimension( 100, 15 ) );
    }

    /**
     * @return the treeLabel
     */
    public TreeLabel getTreeLabel() {
        return treeLabel;
    }

    @Override
    public String toString() {
        return ( (Layer) userObject ).getTitle();
    }

    /**
     * 
     * @param selected
     */
    public void setSelected( boolean selected ) {
        // this.selected = selected;
        ( (Layer) this.userObject ).setVisible( selected );
    }

    @Override
    public boolean isSelected() {
        return ( (Layer) this.userObject ).isVisible();
    }

    /**
     * sets a node to be selected for passed mode
     * 
     * @param mode
     * @param value
     * @param exclusive
     */
    public void setSelectedFor( String mode, boolean value, boolean exclusive ) {
        if ( exclusive ) {
            TreeNode node = getRoot();
            traverseSelectedFor( mode, node );
        }
        selectedFor.put( mode, value );
    }

    private void traverseSelectedFor( String mode, TreeNode node ) {
        if ( node instanceof LayerNode ) {
            if ( ( (LayerNode) node ).isSelectedFor( mode ) ) {
                ( (LayerNode) node ).setSelectedFor( mode, false, false );
                return;
            }
        } else if ( node instanceof LayerGroupNode ) {
            if ( ( (LayerGroupNode) node ).isSelectedFor( mode ) ) {
                ( (LayerGroupNode) node ).setSelectedFor( mode, false, false );
            }

        }
        int count = node.getChildCount();
        for ( int i = 0; i < count; i++ ) {
            traverseSelectedFor( mode, node.getChildAt( i ) );
        }
    }

    /**
     * 
     * @param mode
     * @return true if a node is selected for passed mode
     */
    public boolean isSelectedFor( String mode ) {
        return selectedFor.get( mode ) != null && selectedFor.get( mode );
    }

}