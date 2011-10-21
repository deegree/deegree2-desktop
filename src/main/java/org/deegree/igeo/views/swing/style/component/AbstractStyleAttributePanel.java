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

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.style.perform.StyleChangedEvent;
import org.deegree.igeo.views.swing.style.StyleDialogUtils;
import org.deegree.igeo.views.swing.style.VisualPropertyPanel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * The <code>AbstractFixedDynamicPanel</code> collects all properties of a single component of a visual property, which
 * can have fixed or dynamic values.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public abstract class AbstractStyleAttributePanel extends JPanel implements StyleAttributePanel {

    private static final long serialVersionUID = -6143352831218002785L;

    protected VisualPropertyPanel assignedVisualPropPanel;

    protected ComponentType componentType;

    /**
     * @param assignedVisualPropPanel
     *            the visual property panel assigned to the fixed dynamic panel
     * @param componentType
     *            the type of the component
     * @param helpText
     *            help text
     * @param imageIcon
     *            image as icon
     */
    public AbstractStyleAttributePanel( VisualPropertyPanel assignedVisualPropPanel, ComponentType componentType,
                                        String helpText, ImageIcon imageIcon ) {
        this.assignedVisualPropPanel = assignedVisualPropPanel;
        this.componentType = componentType;
        init( helpText, imageIcon );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.view.components.StyleAttributePanel#getComponentType()
     */
    public ComponentType getComponentType() {
        return this.componentType;
    }

    /**
     * create the layout of the SymbolStyleComponent
     */
    protected void init( String text, ImageIcon ii ) {
        JTextArea helpText = new JTextArea( text );
        helpText.setEditable( false );
        helpText.setLineWrap( true );
        helpText.setWrapStyleWord( true );

        JLabel imageLabel = new JLabel();
        imageLabel.setIcon( ii );

        JComponent cmp = getStyleAttributeComponent();

        FormLayout fl = new FormLayout( "fill:max(200dlu;pref):grow", "top:pref, 10dlu, top:pref, 5dlu, top:pref " );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        builder.setBorder( StyleDialogUtils.createStyleAttributeBorder( getTitle() ) );

        CellConstraints cc = new CellConstraints();
        builder.add( cmp, cc.xy( 1, 1 ) );

        if ( showHelp() ) {
            builder.add( helpText, cc.xy( 1, 3 ) );
            builder.add( imageLabel, cc.xy( 1, 5 ) );
        }

        add( builder.getPanel() );

    }

    protected boolean showHelp() {
        return true;
    }

    /**
     * informs the visual property panel about changes of this style attribute panel
     */
    public void fireValueChangeEvent() {
        assignedVisualPropPanel.update( new StyleChangedEvent( getValue(), componentType ) );
    }

    /**
     * @return the component to display as changeable style
     */
    abstract protected JComponent getStyleAttributeComponent();

    /**
     * @return the title displayed in the border
     */
    abstract protected String getTitle();

}
