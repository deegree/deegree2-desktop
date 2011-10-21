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
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.deegree.datatypes.QualifiedName;
import org.deegree.igeo.ChangeListener;
import org.deegree.igeo.ValueChangedEvent;
import org.deegree.igeo.settings.GraphicOptions;
import org.deegree.igeo.style.model.FillColor;
import org.deegree.igeo.style.model.LinearGradient;
import org.deegree.igeo.views.swing.style.StyleDialogUtils;
import org.deegree.igeo.views.swing.style.renderer.PropertyNameRenderer;
import org.deegree.model.filterencoding.PropertyName;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>ColorClassificationPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class ColorClassificationPanel extends JPanel {

    private static final long serialVersionUID = -4479201621378835090L;

    private String title;

    protected JComboBox propertyCB;

    protected JRadioButton fixed;

    protected JRadioButton property;

    private JRadioButton fixValueRB;

    private JRadioButton dynamicValueRB;

    private JButton fixColorBt;

    private ColorGradientPanel gradientPanel;

    private Color defaultColor;

    private boolean supportDynamic;

    /**
     * 
     * @param graphicOptions
     * @param changeListener
     *            the change listener to inform when the user finished the choice
     * @param color
     *            the initial color to select
     * @param title
     *            the frame title
     * @param defaultColor
     */
    public ColorClassificationPanel( GraphicOptions graphicOptions, Object color, String title, Color defaultColor,
                                     boolean supportDynamic, List<QualifiedName> properties ) {
        this.title = title;
        this.defaultColor = defaultColor;
        this.supportDynamic = supportDynamic;
        setLayout( new BorderLayout() );
        init( graphicOptions, properties );
        setDefaultValues( color );
    }

    private void setDefaultValues( Object color ) {
        if ( color instanceof FillColor ) {
            Color c = (Color) ( (FillColor) color ).getFills( 1 ).get( 0 );
            fixValueRB.setSelected( true );
            fixColorBt.setBackground( c );
            if ( fixed != null ) {
                fixed.setSelected( true );
            }
        } else if ( color instanceof LinearGradient ) {
            gradientPanel.setLinearGradient( (LinearGradient) color );
            dynamicValueRB.setSelected( true );
            if ( fixed != null ) {
                fixed.setSelected( true );
            }
        } else if ( color instanceof PropertyName && property != null && propertyCB != null ) {
            propertyCB.setSelectedItem( color );
            property.setSelected( true );
        }
    }

    private void init( GraphicOptions graphicOptions, List<QualifiedName> properties ) {
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
        fixValueRB = new JRadioButton( get( "$MD10737" ) );
        dynamicValueRB = new JRadioButton( get( "$MD10738" ) );

        ButtonGroup bgFixed = new ButtonGroup();
        bgFixed.add( fixValueRB );
        bgFixed.add( dynamicValueRB );

        fixColorBt = new JButton( get( "$MD10742" ) );
        fixColorBt.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Color color = JColorChooser.showDialog( ColorClassificationPanel.this, get( "$MD10736" ), defaultColor );
                if ( color != null ) {
                    fixColorBt.setBackground( color );
                    fixValueRB.setSelected( true );
                }
            }
        } );

        gradientPanel = new ColorGradientPanel( graphicOptions, new ChangeListener() {
            public void valueChanged( ValueChangedEvent event ) {
                dynamicValueRB.setSelected( true );
            }
        } );
        fixValueRB.setSelected( true );

        FormLayout fl = new FormLayout( "10dlu, center:pref:grow(1.0)", "$cpheight, $cpheight, $cpheight, pref" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        builder.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );

        builder.setColumnSpan( 2 );
        builder.add( fixValueRB );

        builder.nextLine();
        builder.nextColumn();
        builder.setColumnSpan( 1 );
        builder.add( fixColorBt );

        builder.nextLine();
        builder.setColumnSpan( 2 );
        builder.add( dynamicValueRB );

        builder.nextLine();
        builder.setColumnSpan( 2 );
        builder.add( gradientPanel );

        JPanel colorPanel = builder.getPanel();
        if ( supportDynamic ) {
            FormLayout flD = new FormLayout( "$rgap, 15dlu, center:pref:grow(1.0)",
                                             "$sepheight, pref, $sepheight, center:$cpheight" );
            DefaultFormBuilder builderD = new DefaultFormBuilder( flD );
            builderD.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
            CellConstraints cc = new CellConstraints();

            builderD.addSeparator( get( "$MD11718" ), cc.xyw( 1, 1, 3 ) );
            builderD.add( fixed, cc.xy( 2, 2 ) );

            colorPanel.setBorder( StyleDialogUtils.createGroupBorder() );
            builderD.add( colorPanel, cc.xy( 3, 2 ) );

            builderD.addSeparator( get( "$MD11719" ), cc.xyw( 1, 3, 3 ) );
            builderD.add( property, cc.xy( 2, 4 ) );
            builderD.add( propertyCB, cc.xy( 3, 4 ) );
            add( builderD.getPanel(), BorderLayout.CENTER );
        } else {
            add( colorPanel, BorderLayout.CENTER );
        }
    }

    public Object getValue() {
        if ( property != null && property.isSelected() && propertyCB != null ) {
            return (PropertyName) propertyCB.getSelectedItem();
        } else {
            if ( dynamicValueRB.isSelected() ) {
                return gradientPanel.getLinearGradient();
            } else {
                return new FillColor( fixColorBt.getBackground() );
            }
        }

    }

    @Override
    public String toString() {
        return title;
    }
}
