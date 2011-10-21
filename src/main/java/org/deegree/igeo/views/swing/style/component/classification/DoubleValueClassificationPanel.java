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

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;

import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.style.model.classification.DoubleRamp;
import org.deegree.igeo.style.model.classification.DoubleRange;
import org.deegree.igeo.style.model.classification.SingleDouble;
import org.deegree.igeo.views.swing.style.StyleDialogUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>DoubleValueClassificationFrame</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class DoubleValueClassificationPanel extends JPanel {

    private static final long serialVersionUID = -4245048306045147857L;

    private JRadioButton fixValueRB;

    private JRadioButton dynamicValueRB;

    private JSpinner fixValueSpinner;

    private JSpinner dynamicMinSpinner;

    private JSpinner dynamicMaxSpinner;

    private String title;

    /**
     * 
     * @param changeListener
     *            the change listener to inform, when 'ok' button is cklicked
     * @param doubleRange
     *            the double range to
     * @param title
     * @param fix
     * @param min
     * @param max
     * @param step
     */
    public DoubleValueClassificationPanel( DoubleRange doubleRange, String title, double fix, double min, double max,
                                           double step ) {
        this.title = title;
        init( fix, min, max, step );
        setDefault( doubleRange, fix );
    }
    
    private void init( double fix, double min, double max, double step ) {
        // init
        fixValueRB = new JRadioButton( Messages.get( "$MD10786" ) );
        dynamicValueRB = new JRadioButton( Messages.get( "$MD10787" ) );

        ButtonGroup bg = new ButtonGroup();
        bg.add( fixValueRB );
        bg.add( dynamicValueRB );

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
                if ( (Double) dynamicMinSpinner.getValue() > (Double) dynamicMaxSpinner.getValue() ) {
                    dynamicMinSpinner.setValue( dynamicMaxSpinner.getValue() );
                }
            }
        } );

        dynamicMaxSpinner = new JSpinner( new SpinnerNumberModel( max, min, max, step ) );
        dynamicMaxSpinner.setPreferredSize( dimMinMax );
        dynamicMaxSpinner.addChangeListener( new javax.swing.event.ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                dynamicValueRB.setSelected( true );
                if ( (Double) dynamicMaxSpinner.getValue() < (Double) dynamicMinSpinner.getValue() ) {
                    dynamicMaxSpinner.setValue( dynamicMinSpinner.getValue() );
                }
            }
        } );

        // layout
        FormLayout fl = new FormLayout( "10dlu, 15dlu:grow(0.25), left:pref:grow(0.75),",
                                        "$cpheight, $cpheight, $cpheight, $cpheight, $cpheight" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        builder.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
        CellConstraints cc = new CellConstraints();

        builder.add( fixValueRB, cc.xyw( 1, 1, 3 ) );
        builder.add( fixValueSpinner, cc.xyw( 2, 2, 2, CellConstraints.LEFT, CellConstraints.CENTER ) );

        builder.add( dynamicValueRB, cc.xyw( 1, 3, 3 ) );
        builder.addLabel( Messages.get( "$MD10784" ), cc.xy( 2, 4 ) );
        builder.add( dynamicMinSpinner, cc.xy( 3, 4 ) );

        builder.addLabel( Messages.get( "$MD10785" ), cc.xy( 2, 5 ) );
        builder.add( dynamicMaxSpinner, cc.xy( 3, 5 ) );
        add( builder.getPanel(), BorderLayout.CENTER );
    }

    private void setDefault( DoubleRange doubleRange, double value ) {
        if ( doubleRange != null && doubleRange instanceof DoubleRamp ) {
            dynamicValueRB.setSelected( true );
            dynamicMinSpinner.setValue( doubleRange.getMin() );
            dynamicMaxSpinner.setValue( doubleRange.getMax() );
        } else if ( doubleRange != null ) {
            fixValueRB.setSelected( true );
            fixValueSpinner.setValue( doubleRange.getMin() );
        } else {
            fixValueRB.setSelected( true );
            fixValueSpinner.setValue( value );
        }
    }

    /**
     * @return the value
     */
    public DoubleRange getValue() {
        if ( dynamicValueRB.isSelected() ) {
            return new DoubleRamp( (Double) dynamicMinSpinner.getValue(), (Double) dynamicMaxSpinner.getValue() );
        } else {
            return new SingleDouble( (Double) fixValueSpinner.getValue() );
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
