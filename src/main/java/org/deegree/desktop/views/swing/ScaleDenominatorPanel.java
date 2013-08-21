//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 and
 lat/lon GmbH

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/

package org.deegree.desktop.views.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;

import org.deegree.desktop.i18n.Messages;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Panel to edit the minScaleDenominator and maxScaleDenominator
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ScaleDenominatorPanel extends JPanel {

    private static final long serialVersionUID = 7142000637914915491L;

    private JSpinner maxScaleSpinner;

    private JSpinner minScaleSpinner;

    /**
     * default constructor
     */
    public ScaleDenominatorPanel() {
        init();
    }

    /**
     * @return the minScaleSpinnerDenominator
     */
    public double getMinScaleDenominator() {
        return (Double) minScaleSpinner.getValue();
    }

    /**
     * @return the maxScaleDenominator
     */
    public double getMaxScaleDenominator() {
        return (Double) maxScaleSpinner.getValue();
    }

    /**
     * Sets min and max scale denomintor. If min is higher than max, the values will be exchanged.
     * If both value are equal, it is tried first to scale down the min value, if min = 0 and max =
     * 0 the max is set to 1;
     * 
     * @param min
     *            the minScaleSpinnerDenominator to set
     * @param max
     *            the maxScaleDenominator to set
     */
    public void setScaleDenominator( double min, double max ) {
        if ( min > max ) {
            double tmpMin = min;
            min = max;
            max = tmpMin;
        } else if ( min == max ) {
            if ( min > 0 ) {
                min = min - 1;
            } else {
                max = max + 1;
            }
        }
        minScaleSpinner.setValue( min );
        maxScaleSpinner.setValue( max );
    }

    private void init() {
        SpinnerNumberModel maxSpinnerModel = new SpinnerNumberModel( 1000000000, 0.0, 9E99, 100.0 );
        maxScaleSpinner = new JSpinner( maxSpinnerModel );
        maxScaleSpinner.setMaximumSize( new Dimension( 100, 20 ) );
        maxScaleSpinner.setEditor( new ScaleEditor( maxScaleSpinner ) );

        SpinnerNumberModel minSpinnerModel = new SpinnerNumberModel( 0.0, 0.0, 9E99, 100.0 );
        minScaleSpinner = new JSpinner( minSpinnerModel );
        minScaleSpinner.setMaximumSize( new Dimension( 100, 20 ) );
        minScaleSpinner.setEditor( new ScaleEditor( minScaleSpinner ) );

        FormLayout fl = new FormLayout( "left:max(25dlu;pref), 80dlu", "20dlu, 20dlu" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );

        CellConstraints cc = new CellConstraints();
        cc.insets = new Insets( 2, 10, 2, 2 );

        builder.addLabel( Messages.get( "$MD10609" ), cc.xy( 1, 1 ) );
        cc.insets = new Insets( 2, 2, 2, 2 );
        builder.add( minScaleSpinner, cc.xy( 2, 1 ) );
        builder.nextLine();

        cc.insets = new Insets( 2, 10, 2, 2 );
        builder.addLabel( Messages.get( "$MD10610" ), cc.xy( 1, 2 ) );
        cc.insets = new Insets( 2, 2, 2, 2 );
        builder.add( maxScaleSpinner, cc.xy( 2, 2 ) );
        builder.nextLine();

        setLayout( new BorderLayout() );
        add( builder.getPanel(), BorderLayout.CENTER );
    }

    // //////////////////////////////////////////////////////////////////////////////
    // INNER CLASSES
    // //////////////////////////////////////////////////////////////////////////////

    /**
     * Handles the synchronising of the scale denominator spinners
     */
    private class ScaleEditor extends JSpinner.NumberEditor {

        private static final long serialVersionUID = 13163729242308203L;

        private double oldValue = 0;

        public ScaleEditor( JSpinner spinner ) {
            super( spinner );
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.JSpinner.DefaultEditor#stateChanged(javax.swing.event.ChangeEvent)
         */
        @Override
        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() instanceof JSpinner ) {
                JSpinner src = (JSpinner) event.getSource();
                double newValue;
                try {
                    if ( src == maxScaleSpinner ) {
                        double minValue = (Double) minScaleSpinner.getValue();
                        newValue = (Double) src.getValue();
                        if ( newValue <= minValue ) {
                            newValue = minValue + 1;
                        }
                    } else {
                        double maxValue = (Double) maxScaleSpinner.getValue();
                        newValue = (Double) src.getValue();
                        if ( newValue >= maxValue ) {
                            newValue = maxValue - 1;
                        }
                    }
                } catch ( Exception e ) {
                    e.printStackTrace();
                    newValue = oldValue;
                }
                JFormattedTextField textField = getTextField();
                textField.setValue( newValue );
                oldValue = newValue;

            }
        }
    }
}
