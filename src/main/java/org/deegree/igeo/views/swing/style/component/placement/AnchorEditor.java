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

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.vecmath.Point2d;

import org.deegree.igeo.ChangeListener;
import org.deegree.igeo.ValueChangedEvent;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.views.swing.style.AnchorPointDefinitionPanel;
import org.deegree.igeo.views.swing.style.component.AbstractStyleAttributePanel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>AnchorPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class AnchorEditor extends JPanel {

    private static final long serialVersionUID = -1427748517143304464L;

    private JSpinner fixedAnchorX;

    private JSpinner fixedAnchorY;

    private AnchorPointDefinitionPanel fixedAnchorDefinitionPanel;

    private boolean xChanged = true;

    private boolean yChanged = true;

    private AbstractStyleAttributePanel parent;

    public AnchorEditor( AbstractStyleAttributePanel parent ) {
        this();
        this.parent = parent;
    }

    public AnchorEditor() {
        Point2d defaultAnchorPoint = SldValues.getDefaultAnchorPoint();

        SpinnerNumberModel modelX = new SpinnerNumberModel( defaultAnchorPoint.x, 0.0, 1.0, 0.1 );
        fixedAnchorX = new JSpinner( modelX );
        Dimension dim = new Dimension( 75, 20 );
        fixedAnchorX.setPreferredSize( dim );
        fixedAnchorX.addChangeListener( new javax.swing.event.ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateDefAndVisPropPanel();
                xChanged = true;
            }
        } );

        SpinnerNumberModel modelY = new SpinnerNumberModel( defaultAnchorPoint.y, 0.0, 1.0, 0.1 );
        fixedAnchorY = new JSpinner( modelY );
        fixedAnchorY.setPreferredSize( dim );
        fixedAnchorY.addChangeListener( new javax.swing.event.ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateDefAndVisPropPanel();
                yChanged = true;
            }
        } );

        fixedAnchorDefinitionPanel = new AnchorPointDefinitionPanel();
        fixedAnchorDefinitionPanel.setPreferredSize( new Dimension( 135, 70 ) );
        fixedAnchorDefinitionPanel.addChangeListener( new ChangeListener() {
            public void valueChanged( ValueChangedEvent event ) {
                Point2d p = (Point2d) event.getValue();
                // required to avoid multiple calls of the update methode of the assigned visual
                // property panel
                if ( p.x != (Double) fixedAnchorX.getValue() ) {
                    xChanged = false;
                }
                if ( p.y != (Double) fixedAnchorY.getValue() ) {
                    yChanged = false;
                }
                fixedAnchorX.setValue( p.x );
                fixedAnchorY.setValue( p.y );
            }
        } );

        fixedAnchorDefinitionPanel.setValue( defaultAnchorPoint );

        // layout
        FormLayout fl = new FormLayout( "fill:default:grow(1.0), $rgap, right:min",
                                        "bottom:10dlu, bottom:5dlu, bottom:15dlu, bottom:10dlu, bottom:15dlu" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        CellConstraints cc = new CellConstraints();

        builder.addLabel( get( "$MD10835" ), cc.xywh( 1, 1, 1, 2 ) );
        builder.add( fixedAnchorX, cc.xy( 1, 3 ) );

        builder.addLabel( get( "$MD10836" ), cc.xy( 1, 4 ) );
        builder.add( fixedAnchorY, cc.xy( 1, 5 ) );

        builder.addLabel( get( "$MD10834" ), cc.xy( 3, 1, CellConstraints.LEFT, CellConstraints.BOTTOM ) );
        builder.add( fixedAnchorDefinitionPanel, cc.xywh( 3, 2, 1, 4 ) );

        add( builder.getPanel() );
    }

    /**
     * sets the x and y value
     * 
     * @param x
     *            the x value to set
     * @param y
     *            the y value to set
     */
    public void setValue( double x, double y ) {
        fixedAnchorX.setValue( x );
        fixedAnchorY.setValue( y );
    }

    private void updateDefAndVisPropPanel() {
        if ( !( !xChanged && !yChanged ) ) {
            fixedAnchorDefinitionPanel.setValue( (Point2d) getValue() );
            if ( parent != null ) {
                parent.fireValueChangeEvent();
            }
        }
    }

    public Point2d getValue() {
        return new Point2d( (Double) fixedAnchorX.getValue(), (Double) fixedAnchorY.getValue() );
    }
}
