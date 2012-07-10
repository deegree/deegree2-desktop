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

package org.deegree.igeo.views.swing.style;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.LayoutMap;

/**
 * <code>StyleDialogConstants</code> is a collection of constants to standardise the layout of the style editor
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class StyleDialogUtils {

    public static final int PREF_COMPONENT_WIDTH = 75;

    public static final int PREF_ONELINE_COMPONENT_HEIGHT = 20;

    public static void prepareFormConstants() {
        LayoutMap root = LayoutMap.getRoot();
        root.rowPut( "sepheight", "10dlu" );
        root.rowPut( "btheight", "20dlu" );
        root.rowPut( "rbheight", "15dlu" );
        root.rowPut( "cpheight", "20dlu" );
    }

    public static Border createStyleAttributeBorder( String title ) {
        Border outer = BorderFactory.createTitledBorder( title );
        Border inner = BorderFactory.createEmptyBorder( 10, 10, 10, 10 );
        return BorderFactory.createCompoundBorder( outer, inner );
    }

    public static Border createGroupBorder() {
        return BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( Color.LIGHT_GRAY, 1 ),
                                                   BorderFactory.createEmptyBorder( 2, 10, 2, 2 ) );
    }

    /**
     * layouts the given component
     * 
     * @param title
     *            the title of the complete panel shown in the titled border
     * @param fixed
     *            the fixed radio button
     * @param fixedComponent
     *            a component to show/edit the fixed value
     * @param property
     *            the property radio button
     * @param propertyComponent
     *            a component to show/edit the property value
     * @return
     */
    public static JPanel getFixedAttributeDependentPanel( String fixedText, JRadioButton fixed,
                                                          JComponent fixedComponent, String propertyText,
                                                          JRadioButton property, JComponent propertyComponent ) {
        FormLayout fl = new FormLayout( "$rgap, 15dlu, fill:max(100dlu;pref):grow",
                                        "$sepheight, center:[20dlu,default], $ug, $sepheight , center:$cpheight" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );

        CellConstraints cc = new CellConstraints();

        builder.addSeparator( fixedText, cc.xyw( 1, 1, 3 ) );
        builder.add( fixed, cc.xy( 2, 2 ) );
        builder.add( fixedComponent, cc.xy( 3, 2 ) );

        builder.addSeparator( propertyText, cc.xyw( 1, 4, 3 ) );
        builder.add( property, cc.xy( 2, 5 ) );
        builder.add( propertyComponent, cc.xy( 3, 5 ) );

        return builder.getPanel();
    }

}
