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

package org.deegree.desktop.views.swing.style.component.font;

import static org.deegree.desktop.i18n.Messages.get;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.deegree.datatypes.Types;
import org.deegree.desktop.style.model.SldProperty;
import org.deegree.desktop.style.perform.ComponentType;
import org.deegree.desktop.views.swing.style.StyleDialogUtils;
import org.deegree.desktop.views.swing.style.VisualPropertyPanel;
import org.deegree.desktop.views.swing.style.component.AbstractFixedPropertyDependentPanel;

/**
 * <code>FontWeightPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class FontWeightPanel extends AbstractFixedPropertyDependentPanel {

    private static final long serialVersionUID = -5369973093025229367L;

    private JComboBox fixedFontWeightCB;

    /**
     * 
     * @param showDynamicPanel
     * @param assignedVisualPropPanel
     * @param componentType
     */
    public FontWeightPanel( VisualPropertyPanel assignedVisualPropPanel, ComponentType componentType, String helpText,
                            ImageIcon imageIcon ) {
        super( assignedVisualPropPanel, componentType, helpText, imageIcon );
    }

    /**
     * selects entry with the given font weight in the list
     * 
     * @param fontWeightCode
     *            the font weight to set
     */
    public void setValue( int fontWeightCode ) {
        for ( int i = 0; i < this.fixedFontWeightCB.getItemCount(); i++ ) {
            SldProperty item = (SldProperty) this.fixedFontWeightCB.getItemAt( i );
            if ( item.getTypeCode() == fontWeightCode ) {
                this.fixedFontWeightCB.setSelectedIndex( i );
                break;
            }
        }

    }

    @Override
    protected JComponent getStyleAttributeComponent() {
        // init
        initFixedPropertyDependentComponents();
        FontHelper fh = new FontHelper();
        this.fixedFontWeightCB = fh.createFontWeightChooser();
        this.fixedFontWeightCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fireValueChangeEvent();
            }
        } );

        fillPropertiesCB( Types.VARCHAR );
        this.fixed.setSelected( true );
        fireValueChangeEvent();

        return StyleDialogUtils.getFixedAttributeDependentPanel( get( "$MD11634" ), fixed, fixedFontWeightCB,
                                                                 get( "$MD11635" ), property, propertyCB );
    }

    @Override
    protected String getTitle() {
        return get( "$MD10842" );
    }

    @Override
    protected Object getFixedValue() {
        return fixedFontWeightCB.getSelectedItem();
    }

}
