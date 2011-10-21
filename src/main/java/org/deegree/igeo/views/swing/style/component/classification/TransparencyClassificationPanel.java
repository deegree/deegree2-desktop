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

package org.deegree.igeo.views.swing.style.component.classification;

import static org.deegree.igeo.i18n.Messages.get;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;

import org.deegree.datatypes.QualifiedName;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.style.model.classification.IntegerRamp;
import org.deegree.igeo.style.model.classification.SingleInteger;
import org.deegree.igeo.views.swing.style.StyleDialogUtils;
import org.deegree.igeo.views.swing.style.renderer.PropertyNameRenderer;
import org.deegree.model.filterencoding.PropertyName;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>TransparencyClassificationPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class TransparencyClassificationPanel extends JPanel {

    private static final long serialVersionUID = 8054343765702530076L;

    protected JComboBox propertyCB;

    protected JRadioButton fixed;

    protected JRadioButton property;

    private JRadioButton fixValueRB;

    private JRadioButton dynamicValueRB;

    private JSpinner fixValueSpinner;

    private JSpinner dynamicMinSpinner;

    private JSpinner dynamicMaxSpinner;

    private String title;

    private boolean supportDynamic;

    public TransparencyClassificationPanel( Object doubleRange, String title, int fix, int min, int max, int step,
                                            boolean supportDynamic, List<QualifiedName> properties ) {
        this.title = title;
        this.supportDynamic = supportDynamic;
        init( fix, min, max, step, properties );
        setDefault( doubleRange, fix );
    }

    private void init( int fix, int min, int max, int step, List<QualifiedName> properties ) {
        // init
        if ( supportDynamic ) {
            propertyCB = new JComboBox();
            propertyCB.setRenderer( new PropertyNameRenderer() );
            if ( properties != null ) {
                for ( QualifiedName pn : properties ) {
                    propertyCB.addItem( new PropertyName( pn ) );
                }
            }
            fixed = new JRadioButton();
            property = new JRadioButton();

            ButtonGroup bg = new ButtonGroup();
            bg.add( fixed );
            bg.add( property );

            if ( properties == null || properties.isEmpty() ) {
                propertyCB.setEnabled( false );
                property.setEnabled( false );
            }
        }
        fixValueRB = new JRadioButton( Messages.get( "$MD11004" ) );
        dynamicValueRB = new JRadioButton( Messages.get( "$MD11005" ) );

        ButtonGroup bg = new ButtonGroup();
        bg.add( fixValueRB );
        bg.add( dynamicValueRB );
        fixValueRB.setSelected( true );

        Dimension dimMinMax = new Dimension( 85, StyleDialogUtils.PREF_ONELINE_COMPONENT_HEIGHT );

        fixValueSpinner = new JSpinner( new SpinnerNumberModel( fix, min, max, step ) );
        fixValueSpinner.setPreferredSize( dimMinMax );
        fixValueSpinner.addChangeListener( new javax.swing.event.ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fixValueRB.setSelected( true );
            }
        } );

        dynamicMinSpinner = new JSpinner( new SpinnerNumberModel( min, min, max, step ) );
        dynamicMinSpinner.setPreferredSize( dimMinMax );
        dynamicMinSpinner.addChangeListener( new javax.swing.event.ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dynamicValueRB.setSelected( true );
                if ( (Integer) dynamicMinSpinner.getValue() > (Integer) dynamicMaxSpinner.getValue() ) {
                    dynamicMinSpinner.setValue( dynamicMaxSpinner.getValue() );
                }
            }
        } );

        dynamicMaxSpinner = new JSpinner( new SpinnerNumberModel( max, min, max, step ) );
        dynamicMaxSpinner.setPreferredSize( dimMinMax );
        dynamicMaxSpinner.addChangeListener( new javax.swing.event.ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dynamicValueRB.setSelected( true );
                if ( (Integer) dynamicMaxSpinner.getValue() < (Integer) dynamicMinSpinner.getValue() ) {
                    dynamicMaxSpinner.setValue( dynamicMinSpinner.getValue() );
                }
            }
        } );

        // layout
        if ( supportDynamic ) {
            FormLayout fl = new FormLayout(
                                            "$rgap, 15dlu, 10dlu, fill:15dlu:grow(0.25), left:default:grow(0.75), 2dlu, pref",
                                            "$sepheight, $cpheight, $cpheight, $cpheight, $cpheight, $cpheight, $sepheight, center:$cpheight" );
            DefaultFormBuilder builder = new DefaultFormBuilder( fl );
            builder.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
            CellConstraints cc = new CellConstraints();

            builder.addSeparator( get( "$MD11718" ), cc.xyw( 1, 1, 6 ) );
            builder.add( fixed, cc.xywh( 2, 2, 1, 5 ) );

            builder.add( fixValueRB, cc.xyw( 3, 2, 4 ) );
            builder.add( fixValueSpinner, cc.xy( 5, 3 ) );
            builder.addLabel( "%", cc.xy( 6, 3 ) );

            builder.add( dynamicValueRB, cc.xyw( 3, 4, 4 ) );
            builder.addLabel( Messages.get( "$MD10784" ), cc.xy( 4, 5 ) );
            builder.add( dynamicMinSpinner, cc.xy( 5, 5 ) );
            builder.addLabel( "%", cc.xy( 6, 5 ) );

            builder.addLabel( Messages.get( "$MD10785" ), cc.xy( 4, 6 ) );
            builder.add( dynamicMaxSpinner, cc.xy( 5, 6 ) );
            builder.addLabel( "%", cc.xy( 6, 6 ) );

            builder.addSeparator( get( "$MD11719" ), cc.xyw( 1, 7, 6 ) );
            builder.add( property, cc.xy( 2, 8 ) );
            builder.add( propertyCB, cc.xyw( 3, 8, 4 ) );
            add( builder.getPanel(), BorderLayout.CENTER );
        } else {
            FormLayout fl = new FormLayout( "10dlu, fill:15dlu:grow(0.25), left:default:grow(0.75), 2dlu, pref",
                                            "$cpheight, $cpheight, $cpheight, $cpheight, $cpheight" );
            DefaultFormBuilder builder = new DefaultFormBuilder( fl );
            builder.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
            CellConstraints cc = new CellConstraints();

            builder.add( fixValueRB, cc.xyw( 1, 1, 3 ) );
            builder.add( fixValueSpinner, cc.xyw( 2, 2, 2 ) );
            builder.addLabel( "%", cc.xy( 5, 2 ) );

            builder.add( dynamicValueRB, cc.xyw( 1, 3, 3 ) );
            builder.addLabel( Messages.get( "$MD10784" ), cc.xy( 2, 4 ) );
            builder.add( dynamicMinSpinner, cc.xy( 3, 4 ) );
            builder.addLabel( "%", cc.xy( 5, 4 ) );

            builder.addLabel( Messages.get( "$MD10785" ), cc.xy( 2, 5 ) );
            builder.add( dynamicMaxSpinner, cc.xy( 3, 5 ) );
            builder.addLabel( "%", cc.xy( 5, 5 ) );

            add( builder.getPanel(), BorderLayout.CENTER );
        }
    }

    private void setDefault( Object doubleRange, double value ) {
        if ( doubleRange != null && doubleRange instanceof PropertyName ) {
            if ( property != null && propertyCB != null ) {
                propertyCB.setSelectedItem( doubleRange );
                property.setSelected( true );
            }
        } else if ( doubleRange != null && doubleRange instanceof IntegerRamp ) {
            dynamicValueRB.setSelected( true );
            dynamicMinSpinner.setValue( ( (IntegerRamp) doubleRange ).getMin() );
            dynamicMaxSpinner.setValue( ( (IntegerRamp) doubleRange ).getMax() );
            if ( fixed != null )
                fixed.setSelected( true );
        } else if ( doubleRange != null && doubleRange instanceof SingleInteger ) {
            fixValueRB.setSelected( true );
            fixValueSpinner.setValue( ( (SingleInteger) doubleRange ).getMin() );
            if ( fixed != null )
                fixed.setSelected( true );
        } else {
            fixValueRB.setSelected( true );
            fixValueSpinner.setValue( value );
            if ( fixed != null )
                fixed.setSelected( true );
        }
    }

    /**
     * @return the value
     */
    public Object getValue() {
        if ( property != null && property.isSelected() && propertyCB != null ) {
            return (PropertyName) propertyCB.getSelectedItem();
        } else {
            if ( dynamicValueRB.isSelected() ) {
                return new IntegerRamp( (Integer) dynamicMinSpinner.getValue(), (Integer) dynamicMaxSpinner.getValue() );
            } else {
                return new SingleInteger( (Integer) fixValueSpinner.getValue() );
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Component#toString()
     */
    @Override
    public String toString() {
        return title;
    }

}
