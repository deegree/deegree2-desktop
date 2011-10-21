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
package org.deegree.igeo.views;

import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.modules.IModule;

/**
 * 
 * The interface <code>IView</code> is the upperclass of all containerClasses of the application
 * IGeoDesktop defined in the configurationfile of the project. It represents the graphical
 * representation of the project and his modules.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public interface IView<T> {

    /**
     * updates the containerClass
     */
    public void update();

    /**
     * its essential that the containerClass if a module have a link to his module, so it is
     * possible to register the module
     * 
     * @param module
     *            the module to register
     */
    public void registerModule( IModule<T> module );

    /**
     * the init method will be invoked when the application will be started
     * @param viewForm
     *            the viewForm defined in the configurationfile
     * 
     * @throws Exception
     */
    public void init( ViewFormType viewForm )
                            throws Exception;    

}
