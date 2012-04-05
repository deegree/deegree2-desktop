//$HeadURL: svn+ssh://aschmitz@wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2011 by:
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
package org.deegree.igeo.views.swing.georef;

import java.awt.event.ComponentAdapter;

import javax.swing.event.InternalFrameAdapter;

import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.modules.georef.GeoreferencingModule;
import org.deegree.igeo.views.swing.DefaultInnerFrame;

/**
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: stranger $
 * 
 * @version $Revision: $, $Date: $
 */
public class GeoreferencingControlWindow extends DefaultInnerFrame {

    private static final long serialVersionUID = 7946421962675754576L;

    private final GeoreferencingModule module;

    public GeoreferencingControlWindow( GeoreferencingModule module ) {
        this.module = module;
    }

    @Override
    public void init( ViewFormType viewForm )
                            throws Exception {
        super.init( viewForm );
        getContentPane().add( new GeoreferencingControlPanel() );
        pack();
        registerModule( (IModule) module );
        addComponentListener( new ComponentAdapter() {
        } );
        addInternalFrameListener( new InternalFrameAdapter() {
        } );
    }

}
