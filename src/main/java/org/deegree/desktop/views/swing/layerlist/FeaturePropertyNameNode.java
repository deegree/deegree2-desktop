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
package org.deegree.desktop.views.swing.layerlist;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.deegree.datatypes.QualifiedName;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
class FeaturePropertyNameNode extends DefaultMutableTreeNode implements Transferable {

    private static final long serialVersionUID = 3764105116446569389L;

    private Map<String, Boolean> selectedFor = new HashMap<String, Boolean>();

    public final static int SINGLE_SELECTION = 0;

    public final static int DIG_IN_SELECTION = 4;

    protected int selectionMode;

    protected boolean isSelected;

    private List<NodeSelectionListener> listener;

    /**
     * 
     * @param userObject
     * @param nodeType
     */
    public FeaturePropertyNameNode( Object userObject ) {
        this( userObject, false );
    }

    /**
     * 
     * @param userObject
     * @param nodeType
     * @param allowsChildren
     * @param isSelected
     */
    public FeaturePropertyNameNode( Object userObject, boolean isSelected ) {
        super( userObject, true );
        this.isSelected = isSelected;
        this.listener = new ArrayList<NodeSelectionListener>();
        setSelectionMode( DIG_IN_SELECTION );
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.tree.DefaultMutableTreeNode#toString()
     */
    @Override
    public String toString() {
        if ( userObject instanceof QualifiedName ) {
            return ( (QualifiedName) userObject ).getPrefixedName();
        } else {
            return userObject.toString();
        }
    }

    /**
     * 
     * @param listener
     */
    public void addNodeSelectionListener( NodeSelectionListener listener ) {
        this.listener.add( listener );
    }

    /**
     * 
     * @param listener
     */
    public void removeNodeSelectionListener( NodeSelectionListener listener ) {
        this.listener.remove( listener );
    }

    /**
     * 
     * @param mode
     */
    public void setSelectionMode( int mode ) {
        selectionMode = mode;
    }

    /**
     * 
     * @return selectionMode
     */
    public int getSelectionMode() {
        return selectionMode;
    }

    /**
     * 
     * @param isSelected
     */
    public void setSelected( boolean isSelected ) {
        this.isSelected = isSelected;
        fireNodeSelectionEvent( isSelected );
        if ( ( selectionMode == DIG_IN_SELECTION ) && ( children != null ) ) {
            Enumeration<?> e = children.elements();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
                if ( o instanceof FeaturePropertyNameNode ) {
                    FeaturePropertyNameNode node = (FeaturePropertyNameNode) o;
                    node.setSelected( isSelected );
                }
            }
        }
    }

    /**
     * sets a node to be selected for passed mode
     * 
     * @param mode
     * @param value
     */
    public void setSelectedFor( String mode, boolean value, boolean exclusive ) {
        if ( exclusive ) {
            TreeNode node = getRoot();
            traverseSelectedFor( mode, node );
        }
        selectedFor.put( mode, value );
    }

    private void traverseSelectedFor( String mode, TreeNode node ) {
        if ( node instanceof FeaturePropertyNameNode && ( (FeaturePropertyNameNode) node ).isSelectedFor( mode ) ) {
            ( (FeaturePropertyNameNode) node ).setSelectedFor( mode, false, false );
            return;
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

    private void fireNodeSelectionEvent( boolean isSelected ) {
        for ( int i = 0; i < listener.size(); i++ ) {
            NodeSelectionEvent event = new NodeSelectionEvent( this, i, "selected", isSelected );
            listener.get( i ).nodeSelected( event );
        }
    }

    /**
     * 
     * @return true if node is selected
     */
    public boolean isSelected() {
        return isSelected;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
     */
    public Object getTransferData( DataFlavor flavor )
                            throws UnsupportedFlavorException, IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
     */
    public DataFlavor[] getTransferDataFlavors() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
     */
    public boolean isDataFlavorSupported( DataFlavor flavor ) {
        // TODO Auto-generated method stub
        return false;
    }

}