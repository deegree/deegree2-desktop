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

import static org.deegree.igeo.i18n.Messages.get;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
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

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>SizePanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class SizePanel extends AbstractFixedPropertyDependentPanel implements org.deegree.igeo.ChangeListener {

    private static final long serialVersionUID = -2912996021486241016L;

    private UnitsPanel unitsPanel;

    private JSpinner fixedSizeSpinner;

    /**
     * @param performer
     * @param size
     */
    public SizePanel( VisualPropertyPanel assignedVisualPropPanel, ComponentType componentType, String helpText,
                      ImageIcon imageIcon ) {
        super( assignedVisualPropPanel, componentType, helpText, imageIcon );
        assignedVisualPropPanel.addUomChangedListener( this );
    }

    /**
     * @param size
     *            the size to set
     */
    public void setValue( UnitsValue unitsValue ) {
        fixedSizeSpinner.setValue( unitsValue.getValue() );
        unitsPanel.setIsInMapUnits( unitsValue.isMapUnit() );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.symbol.AbstractSymbolStyleComponentPanel#getFixedPanel()
     */
    @Override
    protected JComponent getStyleAttributeComponent() {
        // init
        initFixedPropertyDependentComponents();

        unitsPanel = new UnitsPanel( new org.deegree.igeo.ChangeListener() {
            public void valueChanged( ValueChangedEvent event ) {
                fireValueChangeEvent();
            }
        }, this.assignedVisualPropPanel.getOwner().isDefaultUnitPixel() );

        SpinnerModel model = new SpinnerNumberModel( 6.0, 0.00001, Integer.MAX_VALUE, 1.0 );
        fixedSizeSpinner = new JSpinner( model );
        fixedSizeSpinner.setMaximumSize( new Dimension( StyleDialogUtils.PREF_COMPONENT_WIDTH,
                                                        StyleDialogUtils.PREF_ONELINE_COMPONENT_HEIGHT ) );
        fixedSizeSpinner.setMinimumSize( new Dimension( StyleDialogUtils.PREF_COMPONENT_WIDTH,
                                                        StyleDialogUtils.PREF_ONELINE_COMPONENT_HEIGHT ) );
        fixedSizeSpinner.setPreferredSize( new Dimension( StyleDialogUtils.PREF_COMPONENT_WIDTH,
                                                          StyleDialogUtils.PREF_ONELINE_COMPONENT_HEIGHT ) );
        fixedSizeSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fireValueChangeEvent();
            }
        } );

        fillPropertiesCB( Types.INTEGER, Types.DOUBLE, Types.FLOAT, Types.BIGINT, Types.SMALLINT );
        fixed.setSelected( true );
        fireValueChangeEvent();

        // layout
        JPanel fixedPanel;
        FormLayout fl = new FormLayout( "fill:max(100dlu;pref):grow(0.5), 15dlu, fill:max(100dlu;pref):grow(0.5)", "default:grow(1.0)" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );

        builder.add( fixedSizeSpinner );
        builder.nextColumn( 2 );
        builder.add( unitsPanel );

        fixedPanel = builder.getPanel();

        return StyleDialogUtils.getFixedAttributeDependentPanel( get( "$MD11592" ), fixed, fixedPanel,
                                                                 get( "$MD11593" ), property, propertyCB );

    }

    @Override
    protected String getTitle() {
        return get( "$MD10731" );
    }

    @Override
    protected Object getFixedValue() {
        return new UnitsValue( (Double) fixedSizeSpinner.getValue(), unitsPanel.isInMapUnits() );
    }

    public void valueChanged( ValueChangedEvent event ) {
        if ( event instanceof UomChangedEvent ) {
            unitsPanel.setIsInMapUnits( ( (UomChangedEvent) event ).isInMapUnits() );
        }
    }

}
