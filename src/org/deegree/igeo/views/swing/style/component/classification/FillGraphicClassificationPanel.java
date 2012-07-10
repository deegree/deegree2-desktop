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

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.deegree.igeo.ChangeListener;
import org.deegree.igeo.ValueChangedEvent;
import org.deegree.igeo.settings.GraphicOptions;
import org.deegree.igeo.style.model.Fill;
import org.deegree.igeo.style.model.FillColor;
import org.deegree.igeo.style.model.FillGraphic;
import org.deegree.igeo.style.model.FillPattern;
import org.deegree.igeo.style.model.GraphicSymbol;
import org.deegree.igeo.style.model.LinearGradient;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.views.swing.style.FillGraphicPanel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>FillGraphicClassificationPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class FillGraphicClassificationPanel extends JPanel {

    private static final long serialVersionUID = 5142027543723420340L;

    private String title;

    private JRadioButton fixValueRB;

    private JRadioButton dynamicValueRB;

    private JButton fixColorBt;

    private FillGraphicPanel fillGraphicPanel;

    private ColorGradientPanel gradientPanel;

    /**
     * 
     * @param changeListener
     *            the change listener to inform when the user finished the choice
     * @param color
     *            the initial color to select
     * @param title
     *            the frame title
     */
    public FillGraphicClassificationPanel( GraphicOptions graphicOptions, Fill color, String title ) {
        this.title = title;
        setLayout( new BorderLayout() );
        init( graphicOptions );
        setDefaultValues( color );
    }

    private void setDefaultValues( Fill color ) {
        if ( color instanceof FillColor ) {
            Color c = (Color) ( (FillColor) color ).getFills( 1 ).get( 0 );
            fixValueRB.setSelected( true );
            fixColorBt.setBackground( c );
            fillGraphicPanel.createAndUpdateColor( c, Color.BLACK );
        } else if ( color instanceof FillGraphic ) {
            fixValueRB.setSelected( true );
            GraphicSymbol c = ( (FillGraphic) color ).getGraphicSymbol();
            fillGraphicPanel.setValue( c.getUrl(), c.getSize() );
            if ( c instanceof FillPattern ) {
                Color fpColor = ( (FillPattern) c ).getColor();
                fixColorBt.setBackground( fpColor );
                fillGraphicPanel.createAndUpdateColor( fpColor, Color.BLACK );
            }
        } else if ( color instanceof LinearGradient ) {
            LinearGradient gradient = (LinearGradient) color;
            gradientPanel.setLinearGradient( gradient );
            if ( gradient.getGraphicSymbol() != null ) {
                GraphicSymbol symbol = gradient.getGraphicSymbol();
                fillGraphicPanel.setValue( symbol.getUrl(), symbol.getSize() );
            }
            dynamicValueRB.setSelected( true );
        }
    }

    private void init( GraphicOptions graphicOptions ) {
        fixValueRB = new JRadioButton( get( "$MD10737" ) );
        dynamicValueRB = new JRadioButton( get( "$MD10738" ) );

        ButtonGroup bg = new ButtonGroup();
        bg.add( fixValueRB );
        bg.add( dynamicValueRB );

        fixColorBt = new JButton( get( "$MD10742" ) );
        fixColorBt.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Color color = JColorChooser.showDialog( FillGraphicClassificationPanel.this, get( "$MD10736" ),
                                                        SldValues.getDefaultColor() );
                if ( color != null ) {
                    fixColorBt.setBackground( color );
                    fillGraphicPanel.createAndUpdateColor( color, Color.BLACK );
                    fixValueRB.setSelected( true );
                }
            }
        } );

        fillGraphicPanel = new FillGraphicPanel( graphicOptions );

        gradientPanel = new ColorGradientPanel( graphicOptions, new ChangeListener() {
            public void valueChanged( ValueChangedEvent event ) {
                dynamicValueRB.setSelected( true );
            }
        } );

        FormLayout fl = new FormLayout( "min, $ugap, min", "$cpheight, $cpheight, $cpheight, top:pref:grow(1.0)" );
        fl.setColumnGroups( new int[][] { { 1, 3 } } );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        builder.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );

        CellConstraints cc = new CellConstraints();

        builder.add( fixValueRB, cc.xy( 1, 1 ) );
        builder.add( fixColorBt, cc.xy( 1, 2 ) );
        builder.add( dynamicValueRB, cc.xy( 1, 3 ) );
        builder.add( gradientPanel, cc.xy( 1, 4 ) );
        builder.add( fillGraphicPanel, cc.xywh( 3, 1, 1, 4 ) );

        add( builder.getPanel(), BorderLayout.CENTER );
    }

    public Fill getValue() {
        if ( dynamicValueRB.isSelected() ) {
            LinearGradient gradient = gradientPanel.getLinearGradient();
            if ( fillGraphicPanel.getValue() instanceof FillPattern ) {
                gradient.setGraphicSymbol( fillGraphicPanel.getValue() );
            }
            return gradient;
        } else {
            if ( fillGraphicPanel.getValue() instanceof GraphicSymbol ) {
                return new FillGraphic( fillGraphicPanel.getValue() );
            }
            return new FillColor( fixColorBt.getBackground() );
        }
    }

    @Override
    public String toString() {
        return title;
    }

}
