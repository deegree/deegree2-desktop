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
import javax.vecmath.Point2d;

import org.deegree.desktop.ChangeListener;
import org.deegree.desktop.ValueChangedEvent;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>AnchorPointDefinitionPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class AnchorPointDefinitionPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = -3572232686137649409L;

    private List<ChangeListener> changeListener = new ArrayList<ChangeListener>();

    private List<PointRadioButton> rbList = new ArrayList<PointRadioButton>();

    private PointRadioButton tr = new PointRadioButton( new Point2d( 1,1 ) );

    private PointRadioButton mr = new PointRadioButton( new Point2d( 1, 0.5 ) );

    private PointRadioButton dr = new PointRadioButton( new Point2d( 1, 0 ) );

    private PointRadioButton tc = new PointRadioButton( new Point2d( 0.5, 1 ) );

    private PointRadioButton mc = new PointRadioButton( new Point2d( 0.5, 0.5 ) );

    private PointRadioButton dc = new PointRadioButton( new Point2d( 0.5, 0 ) );

    private PointRadioButton tl = new PointRadioButton( new Point2d( 0, 1 ) );

    private PointRadioButton ml = new PointRadioButton( new Point2d( 0, 0.5 ) );

    private PointRadioButton dl = new PointRadioButton( new Point2d( 0,0 ) );

    private JRadioButton rbToDeselectOther = new JRadioButton();

    public AnchorPointDefinitionPanel() {
        Dimension dim = new Dimension( 140, 70 );
        this.setMinimumSize( dim );
        this.setMaximumSize( dim );
        this.setPreferredSize( dim );
        CompoundBorder cb = BorderFactory.createCompoundBorder( BorderFactory.createRaisedBevelBorder(),
                                                                BorderFactory.createLoweredBevelBorder() );
        this.setBorder( cb );

        JPanel rbPanel = getAnchorPointDefinitionPanel();
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
        g.drawLine( 16, 16, 124, 16 );
        g.drawLine( 124, 16, 124, 54 );
        g.drawLine( 124, 54, 16, 54 );
        g.drawLine( 16, 54, 16, 16 );
    }

    private JPanel getAnchorPointDefinitionPanel() {

        this.rbList.add( this.tl );
        this.rbList.add( this.tc );
        this.rbList.add( this.tr );
        this.rbList.add( this.ml );
        this.rbList.add( this.mc );
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
        builder.append( this.tl );
        builder.append( this.tc );
        builder.append( this.tr );
        builder.nextLine();

        builder.nextColumn();
        builder.append( this.ml );
        builder.append( this.mc );
        builder.append( this.mr );
        builder.nextLine();

        builder.nextColumn();
        builder.append( this.dl );
        builder.append( this.dc );
        builder.append( this.dr );

        return builder.getPanel();
    }

    /**
     * Selects the corresponding radioButton, if there is one. Does not trigger an ChangeEvent!
     * 
     * @param point
     * 
     */
    public void setValue( Point2d point ) {
        boolean found = false;
        for ( PointRadioButton rb : rbList ) {
            if ( rb.getPoint().equals( point ) ) {
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
        Point2d value = new Point2d( 0, 0 );
        for ( PointRadioButton rb : rbList ) {
            if ( e.getSource() == rb ) {
                value = rb.getPoint();
            }
        }
        for ( ChangeListener cl : changeListener ) {
            cl.valueChanged( new PointChangedEvent( value ) );
        }
    }

    // //////////////////////////////////////////////////////////////////////////////
    // INNERCLASSES
    // //////////////////////////////////////////////////////////////////////////////

    /**
     * A PointChangedEvent will be fired, when the selection of a radio button has changed.
     */
    private class PointChangedEvent extends ValueChangedEvent {

        private Point2d value;

        public PointChangedEvent( Point2d value ) {
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

    private class PointRadioButton extends JRadioButton {

        private static final long serialVersionUID = -8673609868450582534L;

        private Point2d point;

        public PointRadioButton( Point2d point ) {
            this.point = point;
        }

        /**
         * @return the point
         */
        public Point2d getPoint() {
            return point;
        }

    }
}
