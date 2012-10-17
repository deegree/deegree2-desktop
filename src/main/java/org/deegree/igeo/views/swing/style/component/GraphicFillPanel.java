/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2012 by:
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

 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: info@lat-lon.de

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
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.deegree.igeo.ChangeListener;
import org.deegree.igeo.ValueChangedEvent;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.style.perform.StyleChangedEvent;
import org.deegree.igeo.style.perform.StyleChangedListener;
import org.deegree.igeo.views.swing.style.FillGraphicPanel;
import org.deegree.igeo.views.swing.style.VisualPropertyPanel;

/**
 * <code>PolygonStyleGraphicFillPanel</code>
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class GraphicFillPanel extends AbstractStyleAttributePanel implements StyleChangedListener {

    private static final long serialVersionUID = -3680637665249383572L;

    private FillGraphicPanel fillGraphicPanel;

    private Color currentPatternColor = SldValues.getDefaultColor();

    private Object value;

    public GraphicFillPanel( VisualPropertyPanel assignedVisualPropPanel, ComponentType componentType, String helpText,
                             ImageIcon imageIcon ) {
        super( assignedVisualPropPanel, componentType, helpText, imageIcon );
    }

    /**
     * selects the entry with the same url if available, otherwise the URL will be added as
     * fillGraphic to the settings
     * 
     * @param onlineResource
     *            the url of the graphic
     * @param size
     *            the size of the graphic
     */
    public void setValue( URL onlineResource, double size ) {
        fillGraphicPanel.setValue( onlineResource, size );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.utils.AbstractFixedDynamicPanel#getFixedPanel()
     */
    @Override
    protected JComponent getStyleAttributeComponent() {
        fillGraphicPanel = new FillGraphicPanel( new ChangeListener() {

            public void valueChanged( ValueChangedEvent event ) {
                value = event.getValue();
                fireValueChangeEvent();
            }
        }, assignedVisualPropPanel.getOwner().getSettings().getGraphicOptions() );
        return fillGraphicPanel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.deegree.igeo.views.swing.style.perform.StyleChangedListener#stylePanelChanged(org.deegree
     * .igeo.views.swing.style.perform.StyleChangedEvent)
     */
    public void stylePanelChanged( StyleChangedEvent changeEvent ) {
        switch ( changeEvent.getType() ) {
        case FILLCOLOR:
            if ( changeEvent.getValue() instanceof Color ) {
                fillGraphicPanel.createAndUpdateColor( (Color) changeEvent.getValue(), this.currentPatternColor );
            }
            break;
        case FILLOPACITY:
            if ( changeEvent.getValue() instanceof Double ) {
                double d = (Double) changeEvent.getValue();
                Color transColor = new Color( 0f, 0f, 0f, (float) d );
                fillGraphicPanel.createAndUpdateColor( this.currentPatternColor, transColor );
            }
            break;
		default:
			break;
        }

    }

    /**
     * sets the color and opacity of the fillGraphicPanel
     * 
     * @param color
     *            the color
     * @param opacity
     *            the opacity
     */
    public void setColorAndOpacitiy( Color color, double opacity ) {
        Color transColor = new Color( 0f, 0f, 0f, (float) opacity );
        fillGraphicPanel.createAndUpdateColor( color, transColor );
    }

    @Override
    protected String getTitle() {
        return get( "$MD10755" );
    }

    @Override
    protected boolean showHelp() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.component.StyleAttributePanel#getValue()
     */
    public Object getValue() {
        return value;
    }

}
