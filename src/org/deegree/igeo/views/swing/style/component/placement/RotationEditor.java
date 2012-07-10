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
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deegree.igeo.ValueChangedEvent;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.views.swing.style.RotationDefinitionPanel;
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
public class RotationEditor extends JPanel {

    private static final long serialVersionUID = -5907897487570853280L;

    private JSpinner fixedRotSpinner;

    private RotationDefinitionPanel fixedRotationDefinitionPanel;

    private AbstractStyleAttributePanel toInform;

    public RotationEditor( AbstractStyleAttributePanel toInform ) {
        this();
        this.toInform = toInform;
    }

    public RotationEditor() {
        double def = SldValues.getDefaultRotation();
        SpinnerModel spinnerModel = new SpinnerNumberModel( def, 0.0d, 360.0d, 5.0d );
        fixedRotSpinner = new JSpinner( spinnerModel );
        fixedRotSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double d = (Double) fixedRotSpinner.getValue();
                fixedRotationDefinitionPanel.setValue( d );
                if ( toInform != null ) {
                    toInform.fireValueChangeEvent();
                }
            }
        } );

        fixedRotationDefinitionPanel = new RotationDefinitionPanel();
        fixedRotationDefinitionPanel.addChangeListener( new org.deegree.igeo.ChangeListener() {
            public void valueChanged( ValueChangedEvent event ) {
                double d = (Double) event.getValue();
                fixedRotSpinner.setValue( d );
            }
        } );
        fixedRotationDefinitionPanel.setValue( def );

        FormLayout fl = new FormLayout( "left:default, 10dlu, right:min", "bottom:10dlu, bottom:45dlu" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        CellConstraints cc = new CellConstraints();

        builder.add( fixedRotSpinner, cc.xy( 1, 2 ) );

        builder.addLabel( get( "$MD10844" ), cc.xy( 3, 1, CellConstraints.LEFT, CellConstraints.BOTTOM ) );
        builder.add( fixedRotationDefinitionPanel, cc.xy( 3, 2 ) );

        add( builder.getPanel() );
    }

    public Object getValue() {
        return fixedRotSpinner.getValue();
    }

    /**
     * sets the spinner to the goven rotation
     * 
     * @param rotation
     *            the rotation to set
     */
    public void setValue( double rotation ) {
        fixedRotSpinner.setValue( rotation );
    }
}
