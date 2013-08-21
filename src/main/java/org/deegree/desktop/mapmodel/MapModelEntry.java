//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2008 by:
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

package org.deegree.desktop.mapmodel;

import java.util.List;

import org.deegree.desktop.ChangeListener;
import org.deegree.model.Identifier;

/**
 * Basic interface for items managed by a {@link MapModel}
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public interface MapModelEntry extends ChangeListener {

    /**
     * 
     * @return layers identifier
     */
    public Identifier getIdentifier();

    /**
     * @return the owner
     */
    public MapModel getOwner();

    /**
     * @return the parent
     */
    public LayerGroup getParent();

    /**
     * 
     * @return antecessor layer or <code>null</code> if a layer do not have an antecessor
     */
    public Layer getAntecessor();

    /**
     * 
     * @return title of a MapModelEntry
     */
    public String getTitle();

    /**
     * 
     * @return abstract
     */
    public String getAbstract();
    
    /**
     * 
     * @param abstract_
     */
    void setAbstract( String abstract_ );

    /**
     * 
     * @return true if visible
     */
    boolean isVisible();

    /**
     * 
     * @param visible
     */
    void setVisible( boolean visible );

    /**
     * 
     * @return true if queryable (e.g. GetFeatureInfo or GetFeature)
     */
    boolean isQueryable();

    /**
     * 
     * @param queryable
     */
    void setQueryable( boolean queryable );
    
    /**
     * 
     * @return list of action a layer is selected for
     */
    public List<String> getSelectedFor();

    /**
     * 
     * @param selectedFor
     */
    public void setSelectedFor( List<String> selectedFor );

    /**
     * 
     * @param selectedFor
     */
    public void addSelectedFor( String selectedFor );

    /**
     * 
     * @param selectedFor
     */
    public void removeSelectedFor( String selectedFor );

    /**
     * adds a listener to a MapModelEntry
     * 
     * @param listener
     */
    void addChangeListener( ChangeListener listener );

}
