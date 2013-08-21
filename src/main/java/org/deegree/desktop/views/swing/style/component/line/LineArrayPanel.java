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

package org.deegree.desktop.views.swing.style.component.line;

import static org.deegree.desktop.i18n.Messages.get;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.deegree.desktop.settings.Settings;
import org.deegree.desktop.style.model.DashArray;
import org.deegree.desktop.style.model.SldValues;
import org.deegree.desktop.style.perform.ComponentType;
import org.deegree.desktop.views.swing.style.VisualPropertyPanel;
import org.deegree.desktop.views.swing.style.component.AbstractStyleAttributePanel;
import org.deegree.desktop.views.swing.style.renderer.DashArrayRenderer;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>LineArrayPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class LineArrayPanel extends AbstractStyleAttributePanel {

    private static final long serialVersionUID = -3136813147454971509L;

    private JComboBox fixedLineStyleCB;

    public LineArrayPanel( VisualPropertyPanel assignedVisualPropPanel, ComponentType componentType, String helpText,
                           ImageIcon imageIcon ) {
        super( assignedVisualPropPanel, componentType, helpText, imageIcon );
    }

    /**
     * selects the entry with the same dash array if available, otherwise the dash array will be
     * added to the settings
     * 
     * @param lineStyle
     *            the style to set
     */
    public void setValue( float[] lineStyle ) {
        if ( lineStyle != null ) {
            boolean isInList = false;
            for ( int i = 0; i < this.fixedLineStyleCB.getItemCount(); i++ ) {
                DashArray da = (DashArray) this.fixedLineStyleCB.getItemAt( i );
                if ( Arrays.equals( da.getDashArray(), lineStyle ) ) {
                    this.fixedLineStyleCB.setSelectedIndex( i );
                    isInList = true;
                    break;
                }
            }
            if ( !isInList ) {
                // create a new DashArray, add it in the settings and in the gui and select the
                // entry
                Settings s = this.assignedVisualPropPanel.getOwner().getSettings();
                StringBuffer sb = new StringBuffer( lineStyle.length * 3 );
                for ( int i = 0; i < lineStyle.length; i++ ) {
                    sb.append( lineStyle[i] );
                    if ( i != lineStyle.length - 1 ) {
                        sb.append( ", " );
                    }
                }
                DashArray dashArray = new DashArray( sb.toString(), lineStyle );
                s.getGraphicOptions().addDashArray( sb.toString(), dashArray );
                this.fixedLineStyleCB.addItem( dashArray );
                this.fixedLineStyleCB.setSelectedItem( dashArray );
            }
        } else if ( this.fixedLineStyleCB.getItemCount() > 0 ) {
            this.fixedLineStyleCB.setSelectedIndex( 0 );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.utils.AbstractFixedDynamicPanel#getFixedPanel()
     */
    @Override
    protected JComponent getStyleAttributeComponent() {
        // init
        this.fixedLineStyleCB = new JComboBox();
        this.fixedLineStyleCB.setRenderer( new DashArrayRenderer() );
        // default styles
        for ( DashArray da : SldValues.getDashArrays() ) {
            this.fixedLineStyleCB.addItem( da );
        }
        // setting style
        Settings s = this.assignedVisualPropPanel.getOwner().getSettings();
        if ( s.getGraphicOptions().getDashArrays() != null ) {
            for ( String daName : s.getGraphicOptions().getDashArrays().keySet() ) {
                this.fixedLineStyleCB.addItem( s.getGraphicOptions().getDashArrays().get( daName ) );
            }
        }
        this.fixedLineStyleCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fireValueChangeEvent();
            }
        } );

        // layout
        FormLayout fl = new FormLayout( "left:$rgap, default:grow(1.0)", "$sepheight, $cpheight" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        CellConstraints cc = new CellConstraints();

        builder.addSeparator( get( "$MD10815" ), cc.xyw( 1, 1, 2 ) );
        builder.add( this.fixedLineStyleCB, cc.xy( 2, 2 ) );

        return builder.getPanel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.component.AbstractStyleAttributePanel#getTitle()
     */
    @Override
    protected String getTitle() {
        return get( "$MD10814" );
    }

    /* (non-Javadoc)
     * @see org.deegree.igeo.views.swing.style.component.StyleAttributePanel#getValue()
     */
    public Object getValue() {
        return fixedLineStyleCB.getSelectedItem();
    }

}
