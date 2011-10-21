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

package org.deegree.igeo.views.swing.style.component.line;

import static org.deegree.igeo.i18n.Messages.get;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deegree.datatypes.Types;
import org.deegree.igeo.ValueChangedEvent;
import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.style.perform.UnitsValue;
import org.deegree.igeo.views.swing.style.StyleDialogUtils;
import org.deegree.igeo.views.swing.style.UomChangedEvent;
import org.deegree.igeo.views.swing.style.VisualPropertyPanel;
import org.deegree.igeo.views.swing.style.component.AbstractFixedPropertyDependentPanel;
import org.deegree.igeo.views.swing.style.component.UnitsPanel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>LineWidthPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class LineWidthPanel extends AbstractFixedPropertyDependentPanel implements org.deegree.igeo.ChangeListener {

    private static final long serialVersionUID = 8927041761806985956L;

    private UnitsPanel unitsPanel;

    private JSpinner fixedWidthSpinner;

    public LineWidthPanel( VisualPropertyPanel assignedVisualPropPanel, ComponentType componentType, String helpText,
                           ImageIcon imageIcon ) {
        super( assignedVisualPropPanel, componentType, helpText, imageIcon );
        assignedVisualPropPanel.addUomChangedListener( this );
    }

    /**
     * sets the value of the spinner to the width and selectes the correct unit
     * 
     * @param unitsValue
     */
    public void setValue( UnitsValue unitsValue ) {
        fixedWidthSpinner.setValue( unitsValue.getValue() );
        unitsPanel.setIsInMapUnits( unitsValue.isMapUnit() );
    }

    @Override
    protected JComponent getStyleAttributeComponent() {
        // init
        initFixedPropertyDependentComponents();

        unitsPanel = new UnitsPanel( new org.deegree.igeo.ChangeListener() {
            public void valueChanged( ValueChangedEvent event ) {
                fireValueChangeEvent();
            }
        }, this.assignedVisualPropPanel.getOwner().isDefaultUnitPixel() );

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel( 1.0, 0.0, Integer.MAX_VALUE, 0.5 );
        fixedWidthSpinner = new JSpinner( spinnerModel );
        fixedWidthSpinner.setMaximumSize( new Dimension( StyleDialogUtils.PREF_COMPONENT_WIDTH,
                                                         StyleDialogUtils.PREF_ONELINE_COMPONENT_HEIGHT ) );
        fixedWidthSpinner.setMinimumSize( new Dimension( StyleDialogUtils.PREF_COMPONENT_WIDTH,
                                                         StyleDialogUtils.PREF_ONELINE_COMPONENT_HEIGHT ) );
        fixedWidthSpinner.setPreferredSize( new Dimension( StyleDialogUtils.PREF_COMPONENT_WIDTH,
                                                           StyleDialogUtils.PREF_ONELINE_COMPONENT_HEIGHT ) );
        fixedWidthSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fireValueChangeEvent();
            }
        } );

        fillPropertiesCB( Types.INTEGER, Types.DOUBLE,Types.FLOAT, Types.BIGINT, Types.SMALLINT );
        fixed.setSelected( true );
        fireValueChangeEvent();

        // layout
        JPanel fixedPanel;
        FormLayout fl = new FormLayout( "fill:max(100dlu;pref):grow(0.5), 15dlu, fill:max(100dlu;pref):grow(0.5)", "default:grow(1.0)" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );

        builder.add( fixedWidthSpinner );
        builder.nextColumn( 2 );
        builder.add( unitsPanel );

        fixedPanel = builder.getPanel();

        return StyleDialogUtils.getFixedAttributeDependentPanel( get( "$MD11588" ), fixed, fixedPanel,
                                                                 get( "$MD11589" ), property, propertyCB );
    }

    @Override
    protected String getTitle() {
        return get( "$MD10819" );
    }

    @Override
    protected Object getFixedValue() {
        return new UnitsValue( (Double) fixedWidthSpinner.getValue(), unitsPanel.isInMapUnits() );
    }

    public void valueChanged( ValueChangedEvent event ) {
        if(event instanceof UomChangedEvent){
            unitsPanel.setIsInMapUnits( ( (UomChangedEvent) event ).isInMapUnits() );
        }
    }

}
