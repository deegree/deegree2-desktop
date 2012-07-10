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

package org.deegree.igeo.views.swing.style;

import java.util.List;

import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.style.perform.StyleChangedEvent;
import org.deegree.igeo.style.perform.StyleChangedListener;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.filterencoding.FilterEvaluationException;
import org.jfree.data.general.DefaultValueDataset;

/**
 * The <code>VisualPopertyPanel</code> interface summarises the components of all attributes of a visual property (line,
 * polygon, symbol, label, raster).
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public interface VisualPropertyPanel {

    /**
     * @return a list of sld rules representing a visual property
     */
    public List<Rule> getRules();

    /**
     * @return the StyleDialog of the VisualPopertyPanel
     */
    public StyleDialog getOwner();

    /**
     * Updates the style after changes of the user and repaint the preview.
     * 
     * @param changeEvent
     */
    public void update( StyleChangedEvent changeEvent );

    /**
     * Sets whether or not the tabs of the given component types are enabled.
     * 
     * @param enabled
     *            true, if the components should be enabled, false otherwise
     * @param componentType
     *            the type of the components - indicates which components should be set
     */

    public void setTabsEnabled( boolean enabled, ComponentType... componentType );

    /**
     * @param styleChangedListener
     *            the styleChangedListener to add to the list of listeners which are informed, when the value of a
     *            StyleAttributePanel has changed
     */
    public void addStyleChangedListener( StyleChangedListener styleChangedListener );

    /**
     * @param styleChangedListener
     *            the styleChangedListener to remove
     */
    public void removeStyleChangedListener( StyleChangedListener styleChangedListener );

    /**
     * @return true, if this VisualPropertyPanel is activated
     */
    public boolean isActive();

    /**
     * initialise the gui with the values of the rules
     * 
     * @param rules
     *            the rules to set
     */
    public void setRules( List<Rule> rules, FeatureType featureType )
                            throws FilterEvaluationException;

    /**
     * initialises the gui with the symbolizer
     * 
     * @param symbolizer
     *            the symbolizer to set
     */
    public void setSymbolizer( Symbolizer symbolizer )
                            throws FilterEvaluationException;

    /**
     * @return the setting symbolizer
     */
    public Symbolizer getPresetSymbolizer();

    /**
     * sets the status (create style or not for this symbolizer type), this means the icon and the check box will be
     * marked as disabled or enabled
     * 
     * @param active
     */
    public void setActive( boolean active );

    /**
     * @param defaultValue
     *            the value returned, when no StyleAttributePanel with the given componentType exists
     * @param componentType
     *            the component type indicating the component
     * @return the value of the StyleAttributePanel indicated by the given componentType, if no StyleAttributePanel with
     *         the component type exist, the {@link DefaultValueDataset} will be returned
     */
    public Object getValue( ComponentType componentType, Object defaultValue );

    /**
     * Register a listener to be informed when the global setting for uom changed.
     * 
     * @param listener
     *            listener to add
     */
    public void addUomChangedListener( org.deegree.igeo.ChangeListener listener );

}
