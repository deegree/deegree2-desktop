/*----------------    FILE HEADER  ------------------------------------------

 This file is part of deegree.
 Copyright (C) 2001-2010 by:
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
package org.deegree.igeo.views.swing.linkeddata;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.util.Pair;
import org.deegree.igeo.i18n.Messages;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.GeometryPropertyType;
import org.deegree.model.feature.schema.PropertyType;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
class KeyPanel extends JPanel {

    private static final long serialVersionUID = 6164666855550518811L;

    private JPanel pnLayer;

    private JPanel pnTable;

    private JComboBox cbTableColumns;

    private JComboBox cbFeatureProperties;

    KeyPanel() {
        initGUI();
    }

    private void initGUI() {
        try {
            this.setPreferredSize( new Dimension( 460, 82 ) );
            GridBagLayout thisLayout = new GridBagLayout();
            thisLayout.rowWeights = new double[] { 0.1 };
            thisLayout.rowHeights = new int[] { 7 };
            thisLayout.columnWeights = new double[] { 0.1, 0.1 };
            thisLayout.columnWidths = new int[] { 7, 7 };
            this.setLayout( thisLayout );
            {
                pnLayer = new JPanel();
                this.add( pnLayer, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                           GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                GridBagLayout pnLayerLayout = new GridBagLayout();
                pnLayer.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11558" ) ) );
                pnLayerLayout.rowWeights = new double[] { 0.1 };
                pnLayerLayout.rowHeights = new int[] { 7 };
                pnLayerLayout.columnWeights = new double[] { 0.1 };
                pnLayerLayout.columnWidths = new int[] { 7 };
                pnLayer.setLayout( pnLayerLayout );
                {
                    ComboBoxModel cbFeaturePropertiesModel = new DefaultComboBoxModel( new String[] {} );
                    cbFeatureProperties = new JComboBox();
                    pnLayer.add( cbFeatureProperties, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                              GridBagConstraints.CENTER,
                                                                              GridBagConstraints.HORIZONTAL,
                                                                              new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                    cbFeatureProperties.setModel( cbFeaturePropertiesModel );
                }
            }
            {
                pnTable = new JPanel();
                this.add( pnTable, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                           GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                GridBagLayout pnTableLayout = new GridBagLayout();
                pnTable.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11559" ) ) );
                pnTableLayout.rowWeights = new double[] { 0.1 };
                pnTableLayout.rowHeights = new int[] { 7 };
                pnTableLayout.columnWeights = new double[] { 0.1 };
                pnTableLayout.columnWidths = new int[] { 7 };
                pnTable.setLayout( pnTableLayout );
                {
                    cbTableColumns = new JComboBox();
                    pnTable.add( cbTableColumns, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                         GridBagConstraints.CENTER,
                                                                         GridBagConstraints.HORIZONTAL,
                                                                         new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param columnNames
     *            table column names
     * @param featureType
     */
    void setAvaiableKeys( String[] columnNames, FeatureType featureType ) {
        ComboBoxModel model = new DefaultComboBoxModel( columnNames );
        cbTableColumns.setModel( model );
        cbTableColumns.setSelectedIndex( 0 );
        PropertyType[] pt = featureType.getProperties();
        List<FeatureTypePropertyContainer> list = new ArrayList<FeatureTypePropertyContainer>();
        for ( PropertyType propertyType : pt ) {
            if ( !( propertyType instanceof GeometryPropertyType ) ) {
                list.add( new FeatureTypePropertyContainer( propertyType.getName() ) );
            }
        }
        model = new DefaultComboBoxModel( list.toArray( new FeatureTypePropertyContainer[list.size()] ) );
        cbFeatureProperties.setModel( model );
        cbFeatureProperties.setSelectedIndex( 0 );
    }

    /**
     * 
     * @return select target-source-keys to link a data table with a feature type
     */
    Pair<QualifiedName, String> getSelectedKey() {
        return new Pair<QualifiedName, String>(
                                                ( (FeatureTypePropertyContainer) cbFeatureProperties.getSelectedItem() ).qn,
                                                (String) cbTableColumns.getSelectedItem() );
    }

    private class FeatureTypePropertyContainer {

        QualifiedName qn;

        /**
         * 
         * @param qn
         */
        FeatureTypePropertyContainer( QualifiedName qn ) {
            this.qn = qn;
        }

        @Override
        public String toString() {
            return qn.getPrefixedName();
        }
    }
}
