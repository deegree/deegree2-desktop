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

package org.deegree.igeo.views.swing.digitize;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.WKTAdapter;

/**
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
class SingleFeatureTableModel extends DefaultTableModel {

    private static final ILogger LOG = LoggerFactory.getLogger( SingleFeatureTableModel.class );

    private static final long serialVersionUID = 4602900533757748840L;

    private Feature feature;

    private FeatureType ft;

    private List<PropertyType> properties;

    private List<PropertyType> geomProps;

    /**
     * 
     * @param fc
     */
    SingleFeatureTableModel( Feature feature ) {
        this.feature = feature;
        ft = feature.getFeatureType();
        // find all none geometry properties
        PropertyType[] props = ft.getProperties();
        properties = new ArrayList<PropertyType>( props.length );
        geomProps = new ArrayList<PropertyType>();
        for ( int i = 0; i < props.length; i++ ) {
            if ( props[i].getType() == Types.GEOMETRY ) {
                geomProps.add( props[i] );
            }
            properties.add( props[i] );
        }

    }

    /**
     * 
     * @return type description of geometry properties
     */
    public List<PropertyType> getGeometryPropertyNames() {
        return geomProps;
    }

    @Override
    public Class<?> getColumnClass( int columnIndex ) {
        return String.class;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName( int columnIndex ) {
        if ( columnIndex == 0 ) {
            return "property name";
        } else if ( columnIndex == 1 ) {
            return "property value";
        } else {
            return "";
        }
    }

    @Override
    public int getRowCount() {
        if ( properties == null ) {
            return 0;
        }
        return properties.size();

    }

    @Override
    public Object getValueAt( int rowIndex, int columnIndex ) {
        QualifiedName qn = properties.get( rowIndex ).getName();
        if ( columnIndex == 0 ) {
            return qn.getLocalName();
        }
        Object o = null;
        if ( feature.getProperties( qn ) != null && feature.getProperties( qn )[0] != null ) {
            o = feature.getProperties( qn )[0].getValue( "" );
            if ( o instanceof Geometry ) {
                try {
                    o = WKTAdapter.export( (Geometry) o );
                } catch ( GeometryException e ) {
                    LOG.logWarning( "ignore", e );
                }
            }
        }
        return o;
    }

    /**
     * 
     * @param rowIndex
     * @param columnIndex
     * @return property name or property itself
     */
    public Object getFeatureValueAt( int rowIndex, int columnIndex ) {
        QualifiedName qn = properties.get( rowIndex ).getName();
        if ( columnIndex == 0 ) {
            return qn;
        }
        return feature.getProperties( qn )[0];
    }

    @Override
    public boolean isCellEditable( int rowIndex, int columnIndex ) {
        if ( columnIndex == 1 ) {
            return true;
        }
        return false;
    }

    @Override
    public void setValueAt( Object aValue, int rowIndex, int columnIndex ) {
        QualifiedName qn = properties.get( rowIndex ).getName();
        feature.getProperties( qn )[0].setValue( aValue );
    }

    /**
     * 
     * @return {@link FeatureCollection} represented by a FeatureTableModel
     */
    Feature getFeature() {
        return feature;
    }

}