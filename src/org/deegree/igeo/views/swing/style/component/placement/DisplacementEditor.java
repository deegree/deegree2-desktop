//$HeadURL: svn+ssh://lbuesching@svn.wald.intevation.de/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2010 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

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
package org.deegree.igeo.views.swing.style.component.placement;

import static org.deegree.igeo.i18n.Messages.get;

import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.vecmath.Point2d;

import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.views.swing.style.component.AbstractStyleAttributePanel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class DisplacementEditor extends JPanel {

    private static final long serialVersionUID = -7512662591104626263L;

    private JSpinner fixedDisplacementX;

    private JSpinner fixedDisplacementY;

    private boolean xChanged = true;

    private boolean yChanged = true;

    private AbstractStyleAttributePanel toInform;

    public DisplacementEditor( AbstractStyleAttributePanel toInform ) {
        this();
        this.toInform = toInform;
    }

    public DisplacementEditor() {
        Point2d defaultDisplacement = SldValues.getDefaultDisplacement();
        // init
        SpinnerNumberModel modelX = new SpinnerNumberModel( ( new Double( defaultDisplacement.x ) ).intValue(),
                                                            Integer.MIN_VALUE, Integer.MAX_VALUE, 1 );
        fixedDisplacementX = new JSpinner( modelX );
        fixedDisplacementX.addChangeListener( new javax.swing.event.ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateDefAndVisPropPanel();
                xChanged = true;
            }
        } );

        SpinnerNumberModel modelY = new SpinnerNumberModel( ( new Double( defaultDisplacement.y ) ).intValue(),
                                                            Integer.MIN_VALUE, Integer.MAX_VALUE, 1 );
        fixedDisplacementY = new JSpinner( modelY );
        fixedDisplacementY.addChangeListener( new javax.swing.event.ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateDefAndVisPropPanel();
                yChanged = true;
            }
        } );

        // layout
        FormLayout fl = new FormLayout( "fill:default:grow(1.0), $rgap, right:min ",
                                        "bottom:10dlu, bottom:5dlu, bottom:15dlu, bottom:10dlu, bottom:15dlu" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );

        CellConstraints cc = new CellConstraints();

        builder.addLabel( get( "$MD10825" ), cc.xywh( 1, 1, 1, 2 ) );
        builder.add( fixedDisplacementX, cc.xy( 1, 3 ) );

        builder.addLabel( get( "$MD10826" ), cc.xy( 1, 4 ) );
        builder.add( fixedDisplacementY, cc.xy( 1, 5 ) );

        add( builder.getPanel() );
    }

    public void setValue( double x, double y ) {
        fixedDisplacementX.setValue( ( new Double( x ) ).intValue() );
        fixedDisplacementY.setValue( ( new Double( y ) ).intValue() );
    }

    private void updateDefAndVisPropPanel() {
        if ( !( !xChanged && !yChanged ) && toInform != null ) {
            toInform.fireValueChangeEvent();
        }
    }

    public Point2d getValue() {
        return new Point2d( (Integer) fixedDisplacementX.getValue(), (Integer) fixedDisplacementY.getValue() );
    }

}
