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

package org.deegree.desktop.modules.analysis;

import static org.deegree.datatypes.Types.VARCHAR;
import static org.deegree.desktop.i18n.Messages.get;
import static org.deegree.model.feature.FeatureFactory.createFeatureProperty;

import org.deegree.kernel.CommandProcessor;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Surface;

/**
 * <code>AreaFunction</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class AreaFunction extends AnalysisFunction {

    /**
     * @param cmds
     */
    public AreaFunction( CommandProcessor cmds ) {
        super( cmds );
    }

    @Override
    public void apply( Feature f, PropertyType pt ) {
        f.removeProperty( pt.getName() );
        Geometry geom = f.getDefaultGeometryPropertyValue();
        double val = 0;
        if ( geom instanceof Surface ) {
            val = ( (Surface) geom ).getArea();
        }
        if ( geom instanceof MultiSurface ) {
            MultiSurface m = (MultiSurface) geom;
            for ( Surface s : m.getAllSurfaces() ) {
                val += s.getArea();
            }
        }
        if ( pt.getType() == VARCHAR ) {
            f.addProperty( createFeatureProperty( pt.getName(), "" + val ) );
        } else {
            f.addProperty( createFeatureProperty( pt.getName(), val ) );
        }
    }

    @Override
    public String toString() {
        return get( "$MD10557" );
    }

}
