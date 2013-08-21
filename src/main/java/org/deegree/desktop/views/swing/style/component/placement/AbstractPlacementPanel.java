//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.desktop.views.swing.style.component.placement;

import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.style.perform.ComponentType;
import org.deegree.desktop.views.swing.style.VisualPropertyPanel;
import org.deegree.desktop.views.swing.style.component.AbstractStyleAttributePanel;
import org.deegree.model.filterencoding.PropertyName;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
abstract class AbstractPlacementPanel extends AbstractStyleAttributePanel implements ActionListener {

    private static final long serialVersionUID = -7917555078133408491L;
    
    protected JComboBox propertyCBx;

    protected JComboBox propertyCBy;

    protected JRadioButton property;

    /**
     * @param assignedVisualPropPanel
     * @param componentType
     * @param helpText
     * @param imageIcon
     */
    public AbstractPlacementPanel( VisualPropertyPanel assignedVisualPropPanel, ComponentType componentType,
                                   String helpText, ImageIcon imageIcon ) {
        super( assignedVisualPropPanel, componentType, helpText, imageIcon );
    }

    /**
     * sets the x and y value as property names
     * 
     * @param x
     * @param y
     */
    public void setValue( PropertyName x, PropertyName y ) {
        if ( x != null && y != null ) {
            property.setSelected( true );
            for ( int i = 0; i < propertyCBx.getItemCount(); i++ ) {
                String itemName = ( (QualifiedName) propertyCBx.getItemAt( i ) ).getLocalName();
                String propName = x.getValue().getAllSteps().get( x.getValue().getSteps() - 1 ).getPropertyName().getLocalName();
                if ( itemName.equals( propName ) ) {
                    propertyCBx.setSelectedIndex( i );
                    break;
                }
            }
            for ( int i = 0; i < propertyCBy.getItemCount(); i++ ) {
                String itemName = ( (QualifiedName) propertyCBy.getItemAt( i ) ).getLocalName();
                String propName = y.getValue().getAllSteps().get( y.getValue().getSteps() - 1 ).getPropertyName().getLocalName();
                if ( itemName.equals( propName ) ) {
                    propertyCBy.setSelectedIndex( i );
                    break;
                }
            }
        }
    }
    
   
}
