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

package org.deegree.desktop.views.swing.style.component;

import static org.deegree.desktop.i18n.Messages.get;

import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.deegree.datatypes.Types;
import org.deegree.desktop.ChangeListener;
import org.deegree.desktop.ValueChangedEvent;
import org.deegree.desktop.style.model.GraphicSymbol;
import org.deegree.desktop.style.model.SldValues;
import org.deegree.desktop.style.model.Symbol;
import org.deegree.desktop.style.perform.ComponentType;
import org.deegree.desktop.views.swing.style.StyleDialogUtils;
import org.deegree.desktop.views.swing.style.SymbolPanel;
import org.deegree.desktop.views.swing.style.VisualPropertyPanel;

/**
 * <code>MarkPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class MarkPanel extends AbstractFixedPropertyDependentPanel {

    private static final long serialVersionUID = -570566909525362586L;

    private SymbolPanel symbolPanel;

    private Symbol value = SldValues.getDefaultWKM();

    /**
     * @param panel
     * @param mark
     */
    public MarkPanel( VisualPropertyPanel assignedVisualPropPanel, ComponentType componentType, String helpText,
                      ImageIcon imageIcon ) {
        super( assignedVisualPropPanel, componentType, helpText, imageIcon );
    }

    /**
     * selectes the entry with this sld name
     * 
     * @param wellKnownName
     *            the name of the mark
     */
    public void setValue( String wellKnownName ) {
        symbolPanel.setValue( wellKnownName );
        value = symbolPanel.getValue();
        fixed.setSelected( true );
    }

    /**
     * selects the entry with the same url if available, otherwise the URL will be added as Symbol to the settings
     * 
     * @param onlineResource
     *            the url of the external graphic
     * @param title
     *            the title of the symbol, may be <code>null</code>, than the onlineresource is the title
     */
    public void setValue( URL onlineResource, String title ) {
        symbolPanel.setValue( onlineResource, title );
        value = symbolPanel.getValue();
        fixed.setSelected( true );
    }

    protected JComponent getStyleAttributeComponent() {
        initFixedPropertyDependentComponents();

        symbolPanel = new SymbolPanel( new ChangeListener() {
            public void valueChanged( ValueChangedEvent event ) {
                value = (Symbol) event.getValue();
                if ( value instanceof GraphicSymbol ) {
                    assignedVisualPropPanel.setTabsEnabled( false, ComponentType.FILLCOLOR, ComponentType.COLOR,
                                                            ComponentType.OPACITY );
                } else {
                    assignedVisualPropPanel.setTabsEnabled( true, ComponentType.FILLCOLOR, ComponentType.COLOR,
                                                            ComponentType.OPACITY );
                }
                fireValueChangeEvent();
            }

        }, assignedVisualPropPanel.getOwner().getSettings().getGraphicOptions() );

        symbolPanel.setBorder( StyleDialogUtils.createGroupBorder() );
        fillPropertiesCB( Types.VARCHAR );
        this.fixed.setSelected( true );
        fireValueChangeEvent();

        return StyleDialogUtils.getFixedAttributeDependentPanel( get( "$MD11672" ), fixed, symbolPanel,
                                                                 get( "$MD11673" ), property, propertyCB );
    }

    @Override
    protected String getTitle() {
        return get( "$MD10773" );
    }

    @Override
    protected boolean showHelp() {
        return false;
    }

    @Override
    protected Object getFixedValue() {
        return value;
    }
}