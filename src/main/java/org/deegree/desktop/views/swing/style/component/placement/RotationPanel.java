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

package org.deegree.desktop.views.swing.style.component.placement;

import static org.deegree.desktop.i18n.Messages.get;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.deegree.datatypes.Types;
import org.deegree.desktop.style.perform.ComponentType;
import org.deegree.desktop.views.swing.style.StyleDialogUtils;
import org.deegree.desktop.views.swing.style.VisualPropertyPanel;
import org.deegree.desktop.views.swing.style.component.AbstractFixedPropertyDependentPanel;

/**
 * <code>RotationPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class RotationPanel extends AbstractFixedPropertyDependentPanel {

    private static final long serialVersionUID = -5299956752124153475L;

    private RotationEditor rotationEditor;

    public RotationPanel( VisualPropertyPanel assignedVisualPropPanel, ComponentType componentType, String helpText,
                          ImageIcon imageIcon ) {
        super( assignedVisualPropPanel, componentType, helpText, imageIcon );
    }

    /**
     * sets the spinner to the goven rotation
     * 
     * @param rotation
     *            the rotation to set
     */
    public void setValue( double rotation ) {
        this.rotationEditor.setValue( rotation );
    }

    @Override
    protected JComponent getStyleAttributeComponent() {
        // init
        initFixedPropertyDependentComponents();
        rotationEditor = new RotationEditor( this );
        fillPropertiesCB( Types.INTEGER, Types.DOUBLE, Types.FLOAT, Types.BIGINT, Types.SMALLINT );
        this.fixed.setSelected( true );
        fireValueChangeEvent();

        return StyleDialogUtils.getFixedAttributeDependentPanel( get( "$MD11590" ), fixed, rotationEditor,
                                                                 get( "$MD11591" ), property, propertyCB );
    }

    @Override
    protected Object getFixedValue() {
        return rotationEditor.getValue();
    }

    @Override
    protected String getTitle() {
        return get( "$MD10843" );
    }

}
