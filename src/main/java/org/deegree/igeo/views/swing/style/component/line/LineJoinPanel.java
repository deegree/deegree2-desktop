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

package org.deegree.igeo.views.swing.style.component.line;

import static org.deegree.igeo.i18n.Messages.get;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.deegree.igeo.style.model.SldProperty;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.views.swing.style.VisualPropertyPanel;
import org.deegree.igeo.views.swing.style.component.AbstractStyleAttributePanel;
import org.deegree.igeo.views.swing.style.renderer.SldPropertyRenderer;

/**
 * <code>LineJoinPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class LineJoinPanel extends AbstractStyleAttributePanel {

    private static final long serialVersionUID = 4126326288633616093L;

    private JComboBox fixedLineJoinCB;

    public LineJoinPanel( VisualPropertyPanel assignedVisualPropPanel, ComponentType componentType, String helpText,
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
        this.fixedLineJoinCB = new JComboBox();
        this.fixedLineJoinCB.setRenderer( new SldPropertyRenderer() );
        for ( SldProperty lc : SldValues.getLineJoins() ) {
            this.fixedLineJoinCB.addItem( lc );
        }

        this.fixedLineJoinCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fireValueChangeEvent();
            }
        } );

        return fixedLineJoinCB;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.component.AbstractStyleAttributePanel#getTitle()
     */
    @Override
    protected String getTitle() {
        return get( "$MD10810" );
    }

    /* (non-Javadoc)
     * @see org.deegree.igeo.views.swing.style.component.StyleAttributePanel#getValue()
     */
    public Object getValue() {
        return fixedLineJoinCB.getSelectedItem();
    }

}
