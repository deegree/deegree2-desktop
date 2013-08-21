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
package org.deegree.desktop.modules;

import javax.swing.ImageIcon;

/**
 * Class encapsulating description for an action that can be performed by a module
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ActionDescription {

    public static enum ACTIONTYPE {
        radiobutton, checkbox, PushButton, ToggleButton
    };

    private String name;
    
    private String description; 

    private ImageIcon icon;

    private String tooltip;

    private ACTIONTYPE actionType;

    private String mnemonic;

    private String accelerator;

    /**
     * 
     * @param name
     * @param description
     * @param icon
     * @param tooltip
     * @param actionType
     * @param mnemonic
     * @param accelerator
     */
    public ActionDescription( String name, String description, ImageIcon icon, String tooltip, ACTIONTYPE actionType, String mnemonic,
                              String accelerator ) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.tooltip = tooltip;
        this.actionType = actionType;
        this.mnemonic = mnemonic;
        this.accelerator = accelerator;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return the name
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the icon
     */
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * @return the tool tip
     */
    public String getTooltip() {
        return tooltip;
    }

    /**
     * @return the actionType
     */
    public ACTIONTYPE getActionType() {
        return actionType;
    }

    /**
     * @return the mnemonic
     */
    public String getMnemonic() {
        return mnemonic;
    }

    /**
     * @return the accelerator
     */
    public String getAccelerator() {
        return accelerator;
    }

}
