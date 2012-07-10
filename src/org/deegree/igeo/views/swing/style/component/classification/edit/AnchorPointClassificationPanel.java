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
package org.deegree.igeo.views.swing.style.component.classification.edit;

import static org.deegree.igeo.i18n.Messages.get;

import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.vecmath.Point2d;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.util.Pair;
import org.deegree.igeo.views.swing.style.component.placement.AnchorEditor;
import org.deegree.igeo.views.swing.style.renderer.PropertyNameRenderer;
import org.deegree.model.filterencoding.PropertyName;

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
public class AnchorPointClassificationPanel extends JPanel {

    private static final long serialVersionUID = 2404586116828974335L;

    private JComboBox propertyCBx;

    private JComboBox propertyCBy;

    private JRadioButton fixed;

    private JRadioButton property;

    private String title;

    private AnchorEditor anchorEditor;

    @SuppressWarnings("unchecked")
    public AnchorPointClassificationPanel( Object displacement, String title, List<QualifiedName> properties ) {
        this.title = title;

        fixed = new JRadioButton();
        property = new JRadioButton();

        ButtonGroup bg = new ButtonGroup();
        bg.add( fixed );
        bg.add( property );

        propertyCBx = new JComboBox();
        propertyCBx.setRenderer( new PropertyNameRenderer() );
        for ( QualifiedName qn : properties ) {
            propertyCBx.addItem( new PropertyName( qn ) );
        }
        propertyCBy = new JComboBox();
        propertyCBy.setRenderer( new PropertyNameRenderer() );
        for ( QualifiedName qn : properties ) {
            propertyCBy.addItem( new PropertyName( qn ) );
        }

        anchorEditor = new AnchorEditor();

        if ( displacement instanceof Pair<?, ?> ) {
            property.setSelected( true );
            propertyCBx.setSelectedItem( ( (Pair) displacement ).first );
            propertyCBy.setSelectedItem( ( (Pair) displacement ).second );
        } else if ( displacement instanceof Point2d ) {
            fixed.setSelected( true );
            anchorEditor.setValue( ( (Point2d) displacement ).x, ( (Point2d) displacement ).y );
        } else {
            fixed.setSelected( true );
        }
        if ( properties == null || properties.isEmpty() ) {
            propertyCBx.setEnabled( false );
            propertyCBy.setEnabled( false );
            property.setEnabled( false );
        }

        FormLayout fl = new FormLayout( "$rgap, 15dlu, left:default:grow(1.0)",
                                        "$sepheight, center:[20dlu,default], $ug, $sepheight, bottom:10dlu, center:$cpheight, bottom:10dlu,center:$cpheight" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        CellConstraints cc = new CellConstraints();
        builder.addSeparator( get( "$MD11713" ), cc.xyw( 1, 1, 3 ) );
        builder.add( fixed, cc.xy( 2, 2 ) );
        builder.add( anchorEditor, cc.xy( 3, 2 ) );

        builder.addSeparator( get( "$MD11714" ), cc.xyw( 1, 4, 3 ) );
        builder.add( property, cc.xywh( 2, 5, 1, 4 ) );
        builder.addLabel( get( "$MD10835" ), cc.xy( 3, 5 ) );
        builder.add( propertyCBx, cc.xy( 3, 6 ) );
        builder.addLabel( get( "$MD10836" ), cc.xy( 3, 7 ) );
        builder.add( propertyCBy, cc.xy( 3, 8 ) );
        add( builder.getPanel() );

    }

    public Object getValue() {
        if ( property.isSelected() ) {
            return new Pair<PropertyName, PropertyName>( (PropertyName) propertyCBx.getSelectedItem(),
                                                         (PropertyName) propertyCBy.getSelectedItem() );
        } else {
            return anchorEditor.getValue();
        }
    }

    @Override
    public String toString() {
        return title;
    }

}
