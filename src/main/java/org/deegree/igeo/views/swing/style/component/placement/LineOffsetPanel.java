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

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.views.swing.style.VisualPropertyPanel;
import org.deegree.igeo.views.swing.style.component.AbstractStyleAttributePanel;

/**
 * <code>LineOffsetPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class LineOffsetPanel extends AbstractStyleAttributePanel {

    private static final long serialVersionUID = 1439183206160153041L;

    private JTextField fixedLineOffsetTF;

    public LineOffsetPanel( VisualPropertyPanel assignedVisualPropPanel, ComponentType componentType, String helpText,
                            ImageIcon imageIcon ) {
        super( assignedVisualPropPanel, componentType, helpText, imageIcon );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.utils.AbstractFixedDynamicPanel#getFixedPanel()
     */
    @Override
    protected JComponent getStyleAttributeComponent() {
        // init
        this.fixedLineOffsetTF = new JTextField( "0.0" );

        return fixedLineOffsetTF;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.component.AbstractStyleAttributePanel#getTitle()
     */
    @Override
    protected String getTitle() {
        return get( "$MD10830" );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.component.StyleAttributePanel#getValue()
     */
    public Object getValue() {
        return fixedLineOffsetTF.getText();
    }

}
