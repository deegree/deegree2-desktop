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

package org.deegree.igeo.views.swing.analysis;

import static java.awt.GridBagConstraints.WEST;
import static org.deegree.datatypes.Types.DOUBLE;
import static org.deegree.datatypes.Types.VARCHAR;
import static org.deegree.framework.util.CollectionUtils.map;
import static org.deegree.framework.util.StringTools.listToString;
import static org.deegree.igeo.i18n.Messages.get;
import static org.deegree.igeo.mapmodel.Layer.ToTitles;
import static org.deegree.igeo.views.swing.util.GuiUtils.initPanel;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.modules.analysis.AnalysisFunction;
import org.deegree.model.feature.schema.PropertyType;

/**
 * <code>AnalysisPanel</code>
 * 
 * Notes: Support for multiple layers is UNTESTED, as selecting multiple layers is currently not possible in
 * iGeoDesktop.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class AnalysisPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = -4878104504387401023L;

    /**
     * The function to calculate. Contains AnalysisFunction objects.
     */
    public JComboBox functionToCalculate;

    /**
     * Whether to use an existing property to store the results.
     */
    public JRadioButton existingProperty;

    /**
     * Whether to use a new property to store the results.
     */
    public JRadioButton newProperty;

    /**
     * The selected attribute.
     */
    public JComboBox propertyBox;

    /**
     * The mapping from property name to property types (obviously one per layer).
     */
    public TreeMap<String, LinkedList<PropertyType>> propertyMap = new TreeMap<String, LinkedList<PropertyType>>();

    /**
     * The name of the new property.
     */
    public JTextField newPropName;

    private TreeSet<String> getProps( FeatureAdapter fa ) {
        TreeSet<String> set = new TreeSet<String>();
        for ( PropertyType pt : fa.getSchema().getProperties() ) {
            if ( pt.getType() == DOUBLE || pt.getType() == VARCHAR ) {
                String name = pt.getName().getLocalName();
                set.add( name );
                LinkedList<PropertyType> list = propertyMap.get( name );
                if ( list == null ) {
                    list = new LinkedList<PropertyType>();
                }
                list.add( pt );
                propertyMap.put( name, list );
            }
        }

        return set;
    }

    /**
     * @param layers
     * @param funs
     * @throws AnalysisPanelException
     */
    public AnalysisPanel( List<Layer> layers, List<AnalysisFunction> funs ) throws AnalysisPanelException {

        if ( layers.isEmpty() ) {
            throw new AnalysisPanelException( get( "$MD10546" ) );
        }

        GridBagConstraints gb = initPanel( this );

        LinkedList<String> names = map( layers, ToTitles );

        TreeSet<String> properties = null;

        for ( Layer l : layers ) {
            for ( DataAccessAdapter adapter : l.getDataAccess() ) {
                if ( adapter instanceof FeatureAdapter ) {
                    FeatureAdapter fa = (FeatureAdapter) adapter;
                    TreeSet<String> props = getProps( fa );
                    if ( properties == null ) {
                        properties = props;
                    } else {
                        properties.retainAll( props );
                        propertyMap.keySet().retainAll( props );
                    }
                }
            }
        }

        if ( propertyMap.keySet().size() == 0 ) {
            if ( layers.size() == 1 ) {
                throw new AnalysisPanelException( get( "$MD10547" ) );
            }
            throw new AnalysisPanelException( get( "$MD10548" ) );
        }

        String text;
        if ( names.size() > 1 ) {
            text = get( "$MD10549" ) + " " + listToString( names, ',' );
        } else {
            text = get( "$MD10550" ) + " " + names.getFirst();
        }
        JLabel label = new JLabel( text );
        gb.gridwidth = 2;
        gb.anchor = WEST;
        add( label, gb );

        ++gb.gridy;
        label = new JLabel( get( "$MD10551" ) );
        add( label, gb );

        functionToCalculate = new JComboBox( new Vector<AnalysisFunction>( funs ) );
        ++gb.gridy;
        add( functionToCalculate, gb );

        label = new JLabel( get( "$MD10552" ) );
        ++gb.gridy;
        add( label, gb );

        existingProperty = new JRadioButton( get( "$MD10553" ) );
        ++gb.gridy;
        add( existingProperty, gb );

        propertyBox = new JComboBox( new Vector<String>( properties ) );
        ++gb.gridy;
        add( propertyBox, gb );

        newProperty = new JRadioButton( get( "$MD10554" ) );
        ++gb.gridy;
        add( newProperty, gb );

        newPropName = new JTextField( 20 );
        ++gb.gridy;
        add( newPropName, gb );

        ButtonGroup g = new ButtonGroup();
        g.add( existingProperty );
        g.add( newProperty );
        if ( propertyMap.keySet().size() == 0 ) {
            existingProperty.setEnabled( false );
            propertyBox.setEnabled( false );
            newProperty.setSelected( true );
        } else {
            existingProperty.setSelected( true );
        }

        existingProperty.addActionListener( this );
        newProperty.addActionListener( this );
        actionPerformed( null );
    }

    public void actionPerformed( ActionEvent e ) {
        propertyBox.setEnabled( existingProperty.isSelected() );
        newPropName.setEnabled( newProperty.isSelected() );
    }

    /**
     * <code>AnalysisPanelException</code>
     * 
     * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    public static class AnalysisPanelException extends Exception {
        private static final long serialVersionUID = -4947985945129449580L;

        /**
         * @param msg
         */
        public AnalysisPanelException( String msg ) {
            super( msg );
        }
    }

}
