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

package org.deegree.desktop.views.swing.style;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.CompoundBorder;

import org.deegree.desktop.ChangeListener;
import org.deegree.desktop.ValueChangedEvent;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>RotationDefinitionPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class RotationDefinitionPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 5484174868431109812L;

    private List<ChangeListener> changeListener = new ArrayList<ChangeListener>();

    private List<DoubleRadioButton> rbList = new ArrayList<DoubleRadioButton>();

    private DoubleRadioButton tr = new DoubleRadioButton( 315 );

    private DoubleRadioButton mr = new DoubleRadioButton( 0 );

    private DoubleRadioButton dr = new DoubleRadioButton( 45 );

    private DoubleRadioButton tc = new DoubleRadioButton( 270 );

    private DoubleRadioButton dc = new DoubleRadioButton( 90 );

    private DoubleRadioButton tl = new DoubleRadioButton( 225 );

    private DoubleRadioButton ml = new DoubleRadioButton( 180 );

    private DoubleRadioButton dl = new DoubleRadioButton( 135 );

    private JRadioButton rbToDeselectOther = new JRadioButton();

    public RotationDefinitionPanel() {
        Dimension dim = new Dimension( 140, 70 );
        this.setMinimumSize( dim );
        this.setMaximumSize( dim );
        this.setPreferredSize( dim );
        CompoundBorder cb = BorderFactory.createCompoundBorder( BorderFactory.createRaisedBevelBorder(),
                                                                BorderFactory.createLoweredBevelBorder() );
        this.setBorder( cb );

        JPanel rbPanel = getRotationDefinitionPanel();
        rbPanel.setBackground( new Color( 1f, 1f, 1f, 0.0f ) );
        this.setLayout( new BorderLayout() );

        this.add( rbPanel, BorderLayout.CENTER );
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        g.drawLine( 10, 35, 130, 35 );
        g.drawLine( 69, 9, 69, 60 );
        g.drawLine( 10, 13, 130, 57 );
        g.drawLine( 10, 57, 130, 13 );
    }

    private JPanel getRotationDefinitionPanel() {

        this.rbList.add( this.tl );
        this.rbList.add( this.tc );
        this.rbList.add( this.tr );
        this.rbList.add( this.ml );
        this.rbList.add( this.mr );
        this.rbList.add( this.dl );
        this.rbList.add( this.dc );
        this.rbList.add( this.dr );

        // init
        ButtonGroup bg = new ButtonGroup();
        for ( JRadioButton rb : this.rbList ) {
            rb.setPreferredSize( new Dimension( 12,12 ) );
            bg.add( rb );
            rb.addActionListener( this );
        }

        // invisible RadioButton to 'deselect' visible RadioButtons
        this.rbToDeselectOther.setVisible( false );
        bg.add( rbToDeselectOther );

        // layout
        FormLayout fl = new FormLayout( "5px, left:30px, 10px, center:40px:grow(1.0), 10px, right:30px, 5px",
                                        "5px, top:17px,  center:16px:grow(1.0), bottom:17px, 5px" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        builder.setBorder( Borders.createEmptyBorder( "0dlu, 0dlu, 0dlu, 0dlu" ) );

        builder.nextLine();
        builder.nextColumn();
        builder.append( tl );
        builder.append( tc );
        builder.append( tr );
        builder.nextLine();

        builder.nextColumn();
        builder.append( ml );
        builder.nextColumn( 2 );
        builder.append( mr );
        builder.nextLine();

        builder.nextColumn();
        builder.append( dl );
        builder.append( dc );
        builder.append( dr );

        return builder.getPanel();
    }

    /**
     * Selects the corresponding radioButton, if there is one. Does not trigger an ChangeEvent!
     * 
     * @param d
     */
    public void setValue( double d ) {
        boolean found = false;
        for ( DoubleRadioButton rb : rbList ) {
            if ( rb.getValue() == d ) {
                rb.setSelected( true );
                found = true;
            }
        }
        if ( !found ) {
            this.rbToDeselectOther.setSelected( true );
        }
    }

    /**
     * @param changeListener
     *            the change Listener, to be informed, when anchor point has changed
     */
    public void addChangeListener( ChangeListener changeListener ) {
        this.changeListener.add( changeListener );
    }

    /**
     * @param changeListener
     *            the changeListener to remove from the list of change listeners
     */
    public void removeChangeListener( ChangeListener changeListener ) {
        this.changeListener.remove( changeListener );
    }

    // //////////////////////////////////////////////////////////////////////////////
    // ACTIONLISTENER
    // //////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed( ActionEvent e ) {
        double value = 0;
        for ( DoubleRadioButton rb : rbList ) {
            if ( e.getSource() == rb ) {
                value = rb.getValue();
            }
        }
        for ( ChangeListener cl : this.changeListener ) {
            cl.valueChanged( new RotationChangedEvent( value ) );
        }

    }

    // //////////////////////////////////////////////////////////////////////////////
    // INNERCLASSE
    // //////////////////////////////////////////////////////////////////////////////

    private class RotationChangedEvent extends ValueChangedEvent {

        private double value;

        public RotationChangedEvent( double value ) {
            this.value = value;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deegree.igeo.ValueChangedEvent#getValue()
         */
        @Override
        public Object getValue() {
            return value;
        }

    }

    private class DoubleRadioButton extends JRadioButton {
        private static final long serialVersionUID = 2224863791769314411L;

        private double value;

        public DoubleRadioButton( double value ) {
            this.value = value;
        }

        /**
         * @return the value
         */
        public double getValue() {
            return value;
        }

    }

}
