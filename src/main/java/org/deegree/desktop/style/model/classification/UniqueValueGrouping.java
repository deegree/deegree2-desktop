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

package org.deegree.desktop.style.model.classification;

import java.util.List;
import java.util.Locale;

import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.style.classification.ClassificationCalculator;

/**
 * <code>UniqueValueGrouping</code> Each single value means one class.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class UniqueValueGrouping<U extends Comparable<U>> extends AbstractThematicGrouping<U> {
    private ClassificationCalculator<U> classificationCalculator = new ClassificationCalculator<U>();

    private static final String ATTRIBUTHEADER = Messages.getMessage( Locale.getDefault(), "$MD10728" );

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.deegree.igeo.views.swing.style.view.components.polygon.Classification#getAttributeHeader
     * ()
     */
    public String getAttributeHeader() {
        return ATTRIBUTHEADER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.view.components.polygon.Classification#getValues()
     */
    public List<ValueRange<U>> getValues() {
        return classificationCalculator.calculateUniqueValues( data );
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.deegree.igeo.views.swing.style.view.components.polygon.Classification#getNoOfClasses()
     */
    public int getNumberOfClasses() {
        return getValues() != null ? getValues().size() : 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.deegree.igeo.views.swing.style.view.components.polygon.Classification#setNoOfClasses()
     */
    public void setNoOfClasses( int noOfClasses ) {
        // nothing to do: number of classes equals size of values
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.deegree.igeo.views.swing.style.model.classification.ThematicGrouping#allowsSameClassBorder
     * ()
     */
    public boolean hasSameClassBorders() {
        return true;
    }

}
