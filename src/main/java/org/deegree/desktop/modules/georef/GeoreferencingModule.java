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
package org.deegree.desktop.modules.georef;

import java.awt.Container;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.main.DeegreeDesktopEventHandler;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.mapmodel.MapModelCollection;
import org.deegree.desktop.modules.DefaultMapModule;
import org.deegree.desktop.modules.DefaultModule;
import org.deegree.desktop.modules.IModule;
import org.deegree.desktop.views.swing.georef.GeoreferencingControlWindow;

/**
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: stranger $
 * 
 * @version $Revision: $, $Date: $
 */
public class GeoreferencingModule extends DefaultModule<Container> {

    public void open() {
        this.componentStateAdapter.setClosed( false );
        createIView();

        // whyever one needs to set the window visible oneself (digitizer does not seem to need to do it)
        GeoreferencingControlWindow wnd = (GeoreferencingControlWindow) getViewForm();
        wnd.setVisible( true );

        ApplicationContainer<Container> igeo = getApplicationContainer();
        MapModelCollection mms = igeo.getMapModelCollection();
        MapModel right = null;
        MapModel left = null;
        for ( MapModel mm2 : mms.getMapModels() ) {
            if ( mm2.getName().equals( "georef" ) ) {
                right = mm2;
            } else {
                left = mm2;
            }
        }

        if ( right == null ) {
            DeegreeDesktopEventHandler.addMapModel( igeo, "georef" );
            for ( MapModel mm2 : mms.getMapModels() ) {
                if ( mm2.getName().equals( "georef" ) ) {
                    right = mm2;
                }
            }
        }

        DefaultMapModule<?> rightModule = null, leftModule = null;
        for ( IModule<?> m : igeo.getModules() ) {
            if ( m instanceof DefaultMapModule ) {
                DefaultMapModule<?> dmm2 = (DefaultMapModule<?>) m;
                if ( dmm2.getInitParameter( "assignedMapModel" ).equals( right.getIdentifier().getValue() ) ) {
                    rightModule = dmm2;
                } else {
                    leftModule = dmm2;
                }
            }
        }

        Container c = (Container) rightModule.getViewForm();
        c.setVisible( true );

        wnd.setMapModel( leftModule, left, rightModule, right );
    }

}
