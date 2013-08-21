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
package org.deegree.desktop.dataadapter;

import org.deegree.desktop.mapmodel.Datasource;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.model.coverage.grid.GridCoverage;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageDescription;

/**
 * 
 * Abstract basis class for all adapter class accessing raster data
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public abstract class GridCoverageAdapter extends DataAccessAdapter {

    protected CoverageDescription coverageDescription;

    protected GridCoverage coverage;

    /**
     * 
     * @param module
     * @param cmmDatasource
     * @param cmmLayer
     * @param cmmMapModel
     */
    public GridCoverageAdapter( Datasource cmmDatasource, Layer cmmLayer, MapModel cmmMapModel ) {
        super( cmmDatasource, cmmLayer, cmmMapModel );
    }

    /**
     * 
     * @return description of the adapted coverage (if available)
     */
    public CoverageDescription getCoverageDescription() {
        return this.coverageDescription;
    }

    /**
     * 
     * @return adapted coverage
     */
    public GridCoverage getCoverage() {
        return this.coverage;
    }

    /**
     * 
     * @param coverageDescription
     */
    public void setCoverageDescription( CoverageDescription coverageDescription ) {
        this.coverageDescription = coverageDescription;
    }

    /**
     * 
     * @param gridCoverage
     */
    public void setCoverage( GridCoverage gridCoverage ) {
        this.coverage = gridCoverage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.DataAccessAdapter#invalidate()
     */
    @Override
    public void invalidate() {
        coverage = null;
    }

}