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

package org.deegree.igeo.style.model.classification;

import java.util.List;

/**
 * <code>ThematicGroupingInformation</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class ThematicGroupingInformation<U extends Comparable<U>> {

    public static enum GROUPINGTYPE {

        UNIQUE, EQUAL( true ), QUANTILE( true ), MANUAL( true ), QUALITY;

        private boolean changesAffectsOtherRows = false;

        private GROUPINGTYPE() {
        }

        private GROUPINGTYPE( boolean changesAffectsOtherRows ) {
            this.changesAffectsOtherRows = changesAffectsOtherRows;
        }

        public boolean changesAffectsOtherRows() {
            return changesAffectsOtherRows;
        }

    };

    private GROUPINGTYPE type;

    private List<ClassificationTableRow<U>> rows;

    public ThematicGroupingInformation( GROUPINGTYPE type, List<ClassificationTableRow<U>> rows ) {
        super();
        this.type = type;
        this.rows = rows;
    }

    /**
     * @return the type
     */
    public GROUPINGTYPE getType() {
        return type;
    }

    /**
     * @return the rows
     */
    public List<ClassificationTableRow<U>> getRows() {
        return rows;
    }

}
