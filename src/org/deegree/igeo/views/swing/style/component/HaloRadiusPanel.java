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

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deegree.datatypes.Types;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.views.swing.style.StyleDialogUtils;
import org.deegree.igeo.views.swing.style.VisualPropertyPanel;

/**
 * <code>HaloRadiusPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class HaloRadiusPanel extends AbstractFixedPropertyDependentPanel {

    private static final long serialVersionUID = -784536068205183987L;

    private JSpinner fixedRadiusSpinner;

    public HaloRadiusPanel( VisualPropertyPanel assignedVisualPropPanel, ComponentType componentType, String helpText,
                            ImageIcon imageIcon ) {
        super( assignedVisualPropPanel, componentType, helpText, imageIcon );
    }

    /**
     * sets the spinner to the given value
     * 
     * @param radius
     *            the radius to set
     */
    public void setValue( double radius ) {
        this.fixedRadiusSpinner.setValue( radius );
    }

    @Override
    protected JComponent getStyleAttributeComponent() {
        // init
        initFixedPropertyDependentComponents();

        SpinnerNumberModel model = new SpinnerNumberModel( SldValues.getDefaultHaloRadius(), 0, 50, 1 );
        this.fixedRadiusSpinner = new JSpinner( model );
        this.fixedRadiusSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fireValueChangeEvent();
            }
        } );

        fillPropertiesCB( Types.INTEGER, Types.DOUBLE, Types.FLOAT, Types.BIGINT, Types.SMALLINT );
        this.fixed.setSelected( true );
        fireValueChangeEvent();

        return StyleDialogUtils.getFixedAttributeDependentPanel( get( "$MD11659" ), fixed, fixedRadiusSpinner,
                                                                 get( "$MD11660" ), property, propertyCB );
    }

    @Override
    protected String getTitle() {
        return get( "$MD10749" );
    }

    @Override
    protected Object getFixedValue() {
        return fixedRadiusSpinner.getValue();
    }

}
