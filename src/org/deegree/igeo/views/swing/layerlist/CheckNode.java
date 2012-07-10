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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public abstract class CheckNode extends DefaultMutableTreeNode implements Transferable {

    enum NODE_TYPE {
        root, unknown, mixed, layerGroup, wmsLayer, wfsLayer, wcsLayer, fileVectorLayer, fileRsterLayer
    };

    private static final long serialVersionUID = 6473030136589287080L;

    protected Map<String, Boolean> selectedFor = new HashMap<String, Boolean>();

    protected int selectionMode;

    protected boolean selected;

    protected NODE_TYPE nodeType;

    /**
     * @param arg0
     * @param arg1
     */
    public CheckNode( Object userObject, boolean allowsChildren ) {
        super( userObject, allowsChildren );
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

    /**
     * 
     * @return nodeType
     */
    public NODE_TYPE getNodeType() {
        return nodeType;
    }

    /**
     * 
     * @return true if node is selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * 
     * @param mode
     * @return
     */
    public abstract boolean isSelectedFor( String mode );

    /**
     * sets a node to be selected for passed mode
     * 
     * @param mode
     * @param value
     * @param exclusive
     */
    public abstract void setSelectedFor( String mode, boolean value, boolean exclusive );

    /**
     * 
     * @param isSelected
     */
    public abstract void setSelected( boolean isSelected );

}
