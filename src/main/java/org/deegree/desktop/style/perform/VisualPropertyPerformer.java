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

package org.deegree.desktop.style.perform;

import java.awt.image.BufferedImage;
import java.util.List;

import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.Symbolizer;

/**
 * The <code>VisualPropertyPerformer</code> interface handles all sld stuff for a visual property.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public interface VisualPropertyPerformer {

    /**
     * @return the image of the specific visual property
     */
    public BufferedImage getAsImage();

    /**
     * @param isInPixel
     * @return all rules defined by the visual property
     */
    public List<Rule> getRules( boolean isInPixel );

    /**
     * update the the rules representing the visual property
     * 
     * @param changeEvent
     *            the changeEvent
     */
    public void update( StyleChangedEvent changeEvent );

    /**
     * @return the fixed symbolizer
     */
    public Symbolizer getSymbolizer( boolean isInPixel );

}
