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

import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deegree.datatypes.Types;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.views.swing.style.StyleDialogUtils;
import org.deegree.igeo.views.swing.style.VisualPropertyPanel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>TransparencyPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class TransparencyPanel extends AbstractFixedPropertyDependentPanel {

    private static final long serialVersionUID = 5911380004095362194L;

    private JSlider fixedTransparencySlider;

    private JTextField fixedTransparencyTextField;

    public TransparencyPanel( VisualPropertyPanel assignedVisualPropPanel, ComponentType componentType,
                              String helpText, ImageIcon imageIcon, boolean enableDynamic ) {
        super( assignedVisualPropPanel, componentType, helpText, imageIcon, enableDynamic );
        if ( !enableDynamic ) {
            propertyCB.setEnabled( false );
            property.setEnabled( false );
            fixed.setEnabled( false );
        }
    }

    public TransparencyPanel( VisualPropertyPanel assignedVisualPropPanel, ComponentType componentType,
                              String helpText, ImageIcon imageIcon ) {
        this( assignedVisualPropPanel, componentType, helpText, imageIcon, true );
    }

    /**
     * updates text and spinner
     * 
     * @param transparency
     *            the transparency to set
     */
    public void setValue( double transparency ) {
        int i = SldValues.getOpacityInPercent( transparency );
        this.fixedTransparencyTextField.setText( Integer.toString( i ) );
        this.fixedTransparencySlider.setValue( i );
    }

    @Override
    protected JComponent getStyleAttributeComponent() {
        // init
        initFixedPropertyDependentComponents();

        this.fixedTransparencyTextField = new JFormattedTextField();
        this.fixedTransparencyTextField.setAlignmentX( JFormattedTextField.RIGHT_ALIGNMENT );
        this.fixedTransparencyTextField.setEnabled( false );
        int i = (int) Math.abs( ( ( SldValues.getDefaultOpacity() * 100 ) - 100 ) );
        this.fixedTransparencyTextField.setText( Integer.toString( i ) );
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put( new Integer( 0 ), new JLabel( get( "$MD10715" ) ) );
        labelTable.put( new Integer( 100 ), new JLabel( get( "$MD10716" ) ) );

        this.fixedTransparencySlider = new JSlider( JSlider.HORIZONTAL, 0, 100, 0 );
        this.fixedTransparencySlider.setLabelTable( labelTable );
        this.fixedTransparencySlider.setMajorTickSpacing( 100 );
        this.fixedTransparencySlider.setPaintTicks( true );
        this.fixedTransparencySlider.setPaintLabels( true );
        this.fixedTransparencySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int v = fixedTransparencySlider.getValue();
                fixedTransparencyTextField.setText( Integer.toString( v ) );
                fireValueChangeEvent();
            }
        } );

        fillPropertiesCB( Types.INTEGER, Types.DOUBLE, Types.FLOAT, Types.BIGINT, Types.SMALLINT );
        this.fixed.setSelected( true );
        fireValueChangeEvent();

        // layout
        FormLayout fl = new FormLayout( "fill:default:grow(1.0), $glue, fill:17dlu, 2dlu, pref", "pref" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        CellConstraints cc = new CellConstraints();

        builder.add( this.fixedTransparencySlider, cc.xy( 1, 1 ) );
        builder.add( this.fixedTransparencyTextField, cc.xy( 3, 1 ) );
        builder.addLabel( "%", cc.xy( 5, 1 ) );

        return StyleDialogUtils.getFixedAttributeDependentPanel( get( "$MD11661" ), fixed, builder.getPanel(),
                                                                 get( "$MD11662" ), property, propertyCB );
    }

    @Override
    protected String getTitle() {
        return get( "$MD10717" );
    }

    @Override
    protected Object getFixedValue() {
        return ( 100 - new Double( fixedTransparencySlider.getValue() ) ) / 100;
    }

}
