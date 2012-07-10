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

package org.deegree.igeo.views.swing.style.component;

import static org.deegree.igeo.i18n.Messages.get;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.deegree.datatypes.Types;
import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.views.swing.style.StyleDialogUtils;
import org.deegree.igeo.views.swing.style.VisualPropertyPanel;
import org.jdesktop.swingx.JXColorSelectionButton;

/**
 * <code>ColorPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class ColorPanel extends AbstractFixedPropertyDependentPanel {

    private static final long serialVersionUID = 3953747401382101078L;

    private JXColorSelectionButton fixedOpenColorDialogBt;

    /**
     * 
     * @param assignedVisualPropPanel
     * @param componentType
     */
    public ColorPanel( VisualPropertyPanel assignedVisualPropPanel, ComponentType componentType, String helpText,
                       ImageIcon imageIcon ) {
        super( assignedVisualPropPanel, componentType, helpText, imageIcon );
    }

    /**
     * set the background of the color panel and fires a value changed event
     * 
     * @param color
     *            the color to set
     * 
     */
    public void setValue( Color color ) {
        fixedOpenColorDialogBt.setBackground( color );
        fireValueChangeEvent();
    }

    @Override
    protected JComponent getStyleAttributeComponent() {
        // init
        initFixedPropertyDependentComponents();
        fixedOpenColorDialogBt = new JXColorSelectionButton();
        fixedOpenColorDialogBt.setMinimumSize( new Dimension( 50, 50 ) );
        fixedOpenColorDialogBt.setMaximumSize( new Dimension( 50, 50 ) );
        fixedOpenColorDialogBt.setText( get( "$MD10746" ) );
        fixedOpenColorDialogBt.addPropertyChangeListener( "background", new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                fireValueChangeEvent();
            }
        } );

        fillPropertiesCB( Types.VARCHAR );
        this.fixed.setSelected( true );
        fireValueChangeEvent();

        return StyleDialogUtils.getFixedAttributeDependentPanel( get( "$MD11663" ), fixed, fixedOpenColorDialogBt,
                                                                 get( "$MD11664" ), property, propertyCB );

    }

    @Override
    protected String getTitle() {
        return get( "$MD10748" );
    }

    @Override
    protected Object getFixedValue() {
        return fixedOpenColorDialogBt.getBackground();
    }

}
