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

package org.deegree.igeo.views.swing.objectinfo;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.datatypes.UnknownTypeException;
import org.deegree.framework.util.DateUtil;
import org.deegree.framework.util.Pair;
import org.deegree.framework.utils.DictionaryCollection;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.ogcbase.CommonNamespaces;

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
class FeatureTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 4602900533757748840L;

    private FeatureCollection fc = FeatureFactory.createFeatureCollection( "w1", 1 );

    private FeatureType ft;

    private List<PropertyType> properties;

    private DictionaryCollection dictionaries;

    /**
     * 
     * @param fc
     */
    FeatureTableModel( FeatureCollection fc, DictionaryCollection dictionaries ) {
        this.fc = fc;
        this.dictionaries = dictionaries;
        if ( fc != null && fc.size() > 0 ) {
            ft = fc.getFeature( 0 ).getFeatureType();
            // find all none geometry properties
            PropertyType[] props = ft.getProperties();
            properties = new ArrayList<PropertyType>( props.length );
            URI nsp = CommonNamespaces.GML3_2_NS;
            try {
                properties.add( FeatureFactory.createPropertyType( new QualifiedName( "gmlID", nsp ),
                                                                   new QualifiedName( "xsd", "string",
                                                                                      CommonNamespaces.XSNS ), false ) );
            } catch ( UnknownTypeException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            for ( int i = 0; i < props.length; i++ ) {
                if ( props[i].getType() != Types.GEOMETRY ) {
                    properties.add( props[i] );
                }
            }
        } else {
            properties = new ArrayList<PropertyType>( 1 );
        }
    }

    FeatureTableModel( FeatureCollection fc ) {
        this( fc, null );

    }

    @Override
    public Class<?> getColumnClass( int columnIndex ) {
        return String.class;
    }

    @Override
    public int getColumnCount() {
        return properties.size();
    }

    @Override
    public String getColumnName( int columnIndex ) {
        if ( properties.size() > 0 ) {
            return properties.get( columnIndex ).getName().getLocalName();
        } else {
            return "";
        }
    }

    @Override
    public int getRowCount() {
        if ( fc == null ) {
            return 0;
        } else {
            return fc.size();
        }
    }

    @Override
    public Object getValueAt( int rowIndex, int columnIndex ) {
        if ( columnIndex == 0 ) {
            return fc.getFeature( rowIndex ).getId();
        } else {
            QualifiedName qn = properties.get( columnIndex ).getName();
            QualifiedName qName = new QualifiedName( ft.getName().getLocalName() + '/' + qn.getLocalName(),
                                                     qn.getNamespace() );
            FeatureProperty[] fps = fc.getFeature( rowIndex ).getProperties( qn );
            Object fpValue = null;
            if ( fps != null && fps.length > 0 && fps[0] != null ) {
                fpValue = fps[0].getValue( "" );
            }
            if ( dictionaries != null ) {
                List<Pair<String, String>> codelist = dictionaries.getCodelist( qName, null );
                if ( codelist != null && codelist.size() > 0 ) {
                    String valueAsString;
                    if ( fpValue instanceof Double ) {
                        valueAsString = Double.toString( ( (Double) fpValue ) );
                    } else if ( fpValue instanceof Integer ) {
                        valueAsString = Integer.toString( (Integer) fpValue );
                    } else if ( fpValue instanceof Date ) {
                        valueAsString = DateUtil.formatISO8601Date( (Date) fpValue );
                    } else {
                        valueAsString = fpValue.toString();
                    }
                    for ( Pair<String, String> cl : codelist ) {
                        if ( cl.first != null && cl.first.equals( valueAsString ) ) {
                            return cl.second + " [" + cl.first + "]";
                        }
                    }
                }
            }
            return fpValue;
        }
    }

    @Override
    public boolean isCellEditable( int rowIndex, int columnIndex ) {
        return false;
    }

    @Override
    public void setValueAt( Object aValue, int rowIndex, int columnIndex ) {
        QualifiedName qn = properties.get( columnIndex ).getName();
        FeatureProperty fp = FeatureFactory.createFeatureProperty( qn, aValue );
        fc.getFeature( rowIndex ).setProperty( fp, 0 );
    }

    /**
     * 
     * @return {@link FeatureCollection} represented by a FeatureTableModel
     */
    FeatureCollection getFeatureCollection() {
        return fc;
    }

    void sortByColumn( int column, boolean ascending ) {
        Feature[] feat = fc.toArray();
        if ( column > 0 ) {
            QualifiedName qn = properties.get( column ).getName();
            for ( int i = 0; i < feat.length; i++ ) {
                for ( int j = 0; j < feat.length - 1; j++ ) {
                    Object o1 = null;
                    if ( feat[j + 1].getProperties( qn ) != null && feat[j + 1].getProperties( qn ).length > 0 ) {
                        o1 = feat[j + 1].getProperties( qn )[0].getValue();
                    } else {
                        o1 = "";
                    }
                    Object o2 = null;
                    if ( feat[j].getProperties( qn ) != null && feat[j].getProperties( qn ).length > 0 ) {
                        o2 = feat[j].getProperties( qn )[0].getValue();
                    } else {
                        o2 = "";
                    }
                    boolean swtch = false;
                    if ( o1 instanceof Number && o2 instanceof Number ) {
                        double v1 = ( (Number) o1 ).doubleValue();
                        double v2 = ( (Number) o2 ).doubleValue();
                        if ( ascending ) {
                            swtch = v1 < v2;
                        } else {
                            swtch = v1 > v2;
                        }
                    } else {
                        String s1 = o1.toString();
                        String s2 = o2.toString();
                        if ( ascending ) {
                            swtch = s1.compareTo( s2 ) < 0;
                        } else {
                            swtch = s1.compareTo( s2 ) > 0;
                        }
                    }
                    if ( swtch ) {
                        switchValues( feat, j );
                    }
                }
            }
        }
        fc = FeatureFactory.createFeatureCollection( "ww", feat );
    }

    private void switchValues( Feature[] feat, int j ) {
        Feature temp;
        temp = feat[j];
        feat[j] = feat[j + 1];
        feat[j + 1] = temp;
    }

}