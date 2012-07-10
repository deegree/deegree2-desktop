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

package org.deegree.igeo.views.swing.clipboard;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.deegree.model.feature.Feature;

/**
 * <code>FeaturesSelection</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class FeaturesSelection implements Transferable {

    private final Collection<Feature> features;

    /**
     * A flavor accepting collections. Cannot be typed with Feature as class literals cannot be type parameterized.
     */
    public static final DataFlavor featuresFlavor = new DataFlavor( Collection.class, null );

    private static final DataFlavor[] flavors = new DataFlavor[] { featuresFlavor };

    /**
     * @param features
     */
    public FeaturesSelection( Collection<Feature> features ) {
        this.features = features;
    }

    public LinkedList<Feature> getTransferData( DataFlavor flavor )
                            throws UnsupportedFlavorException, IOException {
        return new LinkedList<Feature>( features );
    }

    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    public boolean isDataFlavorSupported( DataFlavor flavor ) {
        return flavor.getDefaultRepresentationClass().equals( Collection.class );
    }

}
