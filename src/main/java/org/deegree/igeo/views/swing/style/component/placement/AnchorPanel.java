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

import java.awt.event.ActionEvent;
import java.sql.Types;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.util.Pair;
import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.views.swing.addlayer.QualifiedNameRenderer;
import org.deegree.igeo.views.swing.style.VisualPropertyPanel;

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
public class AnchorPanel extends AbstractPlacementPanel {

    private static final long serialVersionUID = -8411432490731960816L;
    
    protected JRadioButton fixed;

    protected AnchorEditor anchorEditor;


    /**
     * 
     * @param assignedVisualPropPanel
     * @param componentType
     * @param helpText
     * @param imageIcon
     */
    public AnchorPanel( VisualPropertyPanel assignedVisualPropPanel, ComponentType componentType, String helpText,
                        ImageIcon imageIcon ) {
        super( assignedVisualPropPanel, componentType, helpText, imageIcon );
    }
    
    @Override
    protected JComponent getStyleAttributeComponent() {

        fixed = new JRadioButton();
        fixed.addActionListener( this );
        property = new JRadioButton();
        property.addActionListener( this );

        ButtonGroup bg = new ButtonGroup();
        bg.add( fixed );
        bg.add( property );
        fixed.setSelected( true );

        List<QualifiedName> properties = assignedVisualPropPanel.getOwner().getPropertyNames( Types.INTEGER,
                                                                                              Types.DOUBLE,
                                                                                              Types.FLOAT,
                                                                                              Types.BIGINT,
                                                                                              Types.SMALLINT );

        propertyCBx = new JComboBox();
        propertyCBx.setRenderer( new QualifiedNameRenderer() );
        for ( QualifiedName qn : properties ) {
            propertyCBx.addItem( qn );
        }
        propertyCBx.addActionListener( this );

        propertyCBy = new JComboBox();
        propertyCBy.setRenderer( new QualifiedNameRenderer() );
        for ( QualifiedName qn : properties ) {
            propertyCBy.addItem( qn );
        }
        propertyCBy.addActionListener( this );

        if ( properties == null || properties.isEmpty() ) {
            propertyCBx.setEnabled( false );
            propertyCBy.setEnabled( false );
            property.setEnabled( false );
        }

        anchorEditor = new AnchorEditor( this );
        FormLayout fl = new FormLayout( "$rgap, 15dlu, left:default:grow(1.0)",
                                        "$sepheight, center:[20dlu,default], $ug, $sepheight, bottom:10dlu, center:$cpheight, bottom:10dlu,center:$cpheight" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        CellConstraints cc = new CellConstraints();
        builder.addSeparator( get( "$MD11665" ), cc.xyw( 1, 1, 3 ) );
        builder.add( fixed, cc.xy( 2, 2 ) );
        builder.add( anchorEditor, cc.xy( 3, 2 ) );

        builder.addSeparator( get( "$MD11666" ), cc.xyw( 1, 4, 3 ) );
        builder.add( property, cc.xywh( 2, 5, 1, 4 ) );
        builder.addLabel( get( "$MD10835" ), cc.xy( 3, 5 ) );
        builder.add( propertyCBx, cc.xy( 3, 6 ) );
        builder.addLabel( get( "$MD10836" ), cc.xy( 3, 7 ) );
        builder.add( propertyCBy, cc.xy( 3, 8 ) );

        return builder.getPanel();
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
        this.anchorEditor.setValue( x, y );
        fixed.setSelected( true );
    }

    @Override
    protected String getTitle() {
        return get( "$MD10833" );
    }

    public Object getValue() {
        if ( property.isSelected() ) {
            return new Pair<QualifiedName, QualifiedName>( (QualifiedName) propertyCBx.getSelectedItem(),
                                                           (QualifiedName) propertyCBy.getSelectedItem() );
        } else {
            return anchorEditor.getValue();
        }
    }

    public void actionPerformed( ActionEvent e ) {
        fireValueChangeEvent();
    }
}
