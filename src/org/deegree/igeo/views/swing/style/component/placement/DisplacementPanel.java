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

package org.deegree.igeo.views.swing.style.component.placement;

import static org.deegree.igeo.i18n.Messages.get;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Types;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.vecmath.Point2d;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.util.Pair;
import org.deegree.igeo.ChangeListener;
import org.deegree.igeo.ValueChangedEvent;
import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.style.perform.UnitsValue;
import org.deegree.igeo.views.swing.addlayer.QualifiedNameRenderer;
import org.deegree.igeo.views.swing.style.UomChangedEvent;
import org.deegree.igeo.views.swing.style.VisualPropertyPanel;
import org.deegree.igeo.views.swing.style.component.AbstractStyleAttributePanel;
import org.deegree.igeo.views.swing.style.component.UnitsPanel;
import org.deegree.model.filterencoding.PropertyName;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>DisplacementPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class DisplacementPanel extends AbstractStyleAttributePanel implements ActionListener, ChangeListener {

    private static final long serialVersionUID = 2928887134864155027L;

    private JComboBox propertyCBx;

    private JComboBox propertyCBy;

    private JRadioButton fixed;

    private JRadioButton property;

    private DisplacementEditor displacementEditor;

    private UnitsPanel unitsPanel;

    public DisplacementPanel( VisualPropertyPanel assignedVisualPropPanel, ComponentType componentType,
                              String helpText, ImageIcon imageIcon ) {
        super( assignedVisualPropPanel, componentType, helpText, imageIcon );
        assignedVisualPropPanel.addUomChangedListener( this );
    }

    /**
     * sets the x and y value
     * 
     * @param x
     *            the x value to set
     * @param y
     *            the y value to set
     */
    public void setValue( UnitsValue x, UnitsValue y ) {
        displacementEditor.setValue( x.getValue(), y.getValue() );
        unitsPanel.setIsInMapUnits( x.isMapUnit() );
        fixed.setSelected( true );
    }

    /**
     * sets the x and y value as property names
     * 
     * @param x
     * @param y
     */
    public void setValue( PropertyName x, PropertyName y ) {
        if ( x != null && y != null ) {
            property.setSelected( true );
            for ( int i = 0; i < propertyCBx.getItemCount(); i++ ) {
                String itemName = ( (QualifiedName) propertyCBx.getItemAt( i ) ).getLocalName();
                String propName = x.getValue().getAllSteps().get( x.getValue().getSteps() - 1 ).getPropertyName().getLocalName();
                if ( itemName.equals( propName ) ) {
                    propertyCBx.setSelectedIndex( i );
                    break;
                }
            }
            for ( int i = 0; i < propertyCBy.getItemCount(); i++ ) {
                String itemName = ( (QualifiedName) propertyCBy.getItemAt( i ) ).getLocalName();
                String propName = y.getValue().getAllSteps().get( y.getValue().getSteps() - 1 ).getPropertyName().getLocalName();
                if ( itemName.equals( propName ) ) {
                    propertyCBy.setSelectedIndex( i );
                    break;
                }
            }
        }
    }

    @Override
    protected JComponent getStyleAttributeComponent() {
        // init

        fixed = new JRadioButton();
        fixed.addActionListener( this );
        property = new JRadioButton();
        property.addActionListener( this );

        ButtonGroup bg = new ButtonGroup();
        bg.add( fixed );
        bg.add( property );
        fixed.setSelected( true );

        List<QualifiedName> properties = assignedVisualPropPanel.getOwner().getPropertyNames( Types.INTEGER,
                                                                                              Types.DOUBLE,
                                                                                              Types.FLOAT,
                                                                                              Types.BIGINT,
                                                                                              Types.SMALLINT );
        propertyCBx = new JComboBox();
        propertyCBx.setRenderer( new QualifiedNameRenderer() );
        for ( QualifiedName qn : properties ) {
            propertyCBx.addItem( qn );
        }
        propertyCBx.addActionListener( this );

        propertyCBy = new JComboBox();
        propertyCBy.setRenderer( new QualifiedNameRenderer() );
        for ( QualifiedName qn : properties ) {
            propertyCBy.addItem( qn );
        }
        propertyCBy.addActionListener( this );

        if ( properties == null || properties.isEmpty() ) {
            propertyCBx.setEnabled( false );
            propertyCBy.setEnabled( false );
            property.setEnabled( false );
        }
        displacementEditor = new DisplacementEditor( this );

        unitsPanel = new UnitsPanel( new org.deegree.igeo.ChangeListener() {
            public void valueChanged( ValueChangedEvent event ) {
                fireValueChangeEvent();
            }
        }, this.assignedVisualPropPanel.getOwner().isDefaultUnitPixel() );

        FormLayout fl = new FormLayout( "$rgap, 15dlu, fill:max(100dlu;pref):grow(0.5), 1dlu, fill:max(100dlu;pref):grow(0.5)",
                                        "$sepheight, center:[20dlu,default], $ug, $sepheight, bottom:10dlu, center:$cpheight, bottom:10dlu,center:$cpheight" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        CellConstraints cc = new CellConstraints();
        builder.addSeparator( get( "$MD11667" ), cc.xyw( 1, 1, 5 ) );
        builder.add( fixed, cc.xy( 2, 2 ) );
        builder.add( displacementEditor, cc.xy( 3, 2 ) );
        builder.add( unitsPanel, cc.xy( 5, 2 ) );

        builder.addSeparator( get( "$MD11668" ), cc.xyw( 1, 4, 5 ) );
        builder.add( property, cc.xywh( 2, 5, 1, 4 ) );
        builder.addLabel( get( "$MD10835" ), cc.xy( 3, 5 ) );
        builder.add( propertyCBx, cc.xy( 3, 6 ) );
        builder.addLabel( get( "$MD10836" ), cc.xy( 3, 7 ) );
        builder.add( propertyCBy, cc.xy( 3, 8 ) );

        return builder.getPanel();
    }

    @Override
    protected String getTitle() {
        return get( "$MD10823" );
    }

    public Object getValue() {
        if ( property.isSelected() ) {
            return new Pair<QualifiedName, QualifiedName>( (QualifiedName) propertyCBx.getSelectedItem(),
                                                           (QualifiedName) propertyCBy.getSelectedItem() );
        } else {
            Point2d value = displacementEditor.getValue();
            boolean inMapUnits = unitsPanel.isInMapUnits();
            return new Pair<UnitsValue, UnitsValue>( new UnitsValue( value.x, inMapUnits ), new UnitsValue( value.y,
                                                                                                            inMapUnits ) );
        }
    }

    public void actionPerformed( ActionEvent e ) {
        fireValueChangeEvent();
    }

    public void valueChanged( ValueChangedEvent event ) {
        if ( event instanceof UomChangedEvent ) {
            unitsPanel.setIsInMapUnits( ( (UomChangedEvent) event ).isInMapUnits() );
        }
    }
}
