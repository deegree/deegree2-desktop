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

package org.deegree.igeo.views.swing.style.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;

import org.deegree.datatypes.QualifiedName;
import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.views.swing.addlayer.QualifiedNameRenderer;
import org.deegree.igeo.views.swing.style.VisualPropertyPanel;
import org.deegree.model.filterencoding.PropertyName;

/**
 * <code>AbstractFixedPropertyDependentPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public abstract class AbstractFixedPropertyDependentPanel extends AbstractStyleAttributePanel {

    private static final long serialVersionUID = -2897938982059868622L;

    protected JComboBox propertyCB;

    protected JRadioButton fixed;

    protected JRadioButton property;

    protected boolean enableDynamic;

    public AbstractFixedPropertyDependentPanel( VisualPropertyPanel assignedVisualPropPanel,
                                                ComponentType componentType, String helpText, ImageIcon imageIcon ) {
        this( assignedVisualPropPanel, componentType, helpText, imageIcon, true );
    }

    public AbstractFixedPropertyDependentPanel( VisualPropertyPanel assignedVisualPropPanel,
                                                ComponentType componentType, String helpText, ImageIcon imageIcon,
                                                boolean enableDynamic ) {
        super( assignedVisualPropPanel, componentType, helpText, imageIcon );
        this.enableDynamic = enableDynamic;
    }

    protected void initFixedPropertyDependentComponents() {
        propertyCB = new JComboBox();
        propertyCB.setRenderer( new QualifiedNameRenderer() );
        propertyCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                property.setSelected( true );
                fireValueChangeEvent();
            }
        } );

        fixed = new JRadioButton();
        property = new JRadioButton();

        ButtonGroup bg = new ButtonGroup();
        bg.add( fixed );
        bg.add( property );
        fixed.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fireValueChangeEvent();
            }
        } );
        property.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fireValueChangeEvent();
            }
        } );
    }

    /**
     * 
     * Fills the combo box with the property values.
     * 
     * @param types
     *            the supported types
     */
    public void fillPropertiesCB( int... types ) {
        List<QualifiedName> properties = assignedVisualPropPanel.getOwner().getPropertyNames( types );
        if ( properties.size() > 0 ) {
            for ( QualifiedName qn : properties ) {
                propertyCB.addItem( qn );
            }
            propertyCB.setEnabled( true );
        } else {
            propertyCB.setEnabled( false );
        }
    }

    /**
     * selects the propertyName in the list of propertoies and selects the radio button
     * 
     * @param propertyName
     *            the property name to select
     */
    public void setValue( PropertyName propertyName ) {
        for ( int i = 0; i < propertyCB.getItemCount(); i++ ) {
            String itemName = ( (QualifiedName) propertyCB.getItemAt( i ) ).getLocalName();
            String propName = propertyName.getValue().getAllSteps().get( propertyName.getValue().getSteps() - 1 ).getPropertyName().getLocalName();
            if ( itemName.equals( propName ) ) {
                propertyCB.setSelectedIndex( i );
                break;
            }
        }
    }

    public Object getValue() {
        Object value;
        if ( enableDynamic && property.isSelected() && propertyCB.getSelectedItem() != null ) {
            value = propertyCB.getSelectedItem();
        } else {
            value = getFixedValue();
        }
        return value;
    }

    protected abstract Object getFixedValue();
}
