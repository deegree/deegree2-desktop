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

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.DefaultMapModule;
import org.deegree.igeo.views.swing.DefaultFrame;

/**
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: stranger $
 * 
 * @version $Revision: $, $Date: $
 */
public class GeoreferencingControlWindow extends DefaultFrame {

    private static final long serialVersionUID = 7946421962675754576L;

    GeoreferencingControlPanel panel;

    MapModel right;

    DefaultMapModule<?> rightModule;

    @Override
    public void init( ViewFormType viewForm )
                            throws Exception {
        super.init( viewForm );
        panel = new GeoreferencingControlPanel();
        getContentPane().add( panel );
        pack();
        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosed( WindowEvent evt ) {
                // this seems to remove the map module!
                Container c = (Container) rightModule.getViewForm();
                c.setVisible( false );

                // so remove the map model (which is not automatically removed)...
                owner.getApplicationContainer().getMapModelCollection().removeMapModel( right );

                // also, delete points in left (remaining) map window
                panel.points.removeAll();
                panel.points.updateMaps();
            }
        } );
    }

    public void setMapModel( DefaultMapModule<?> leftModule, MapModel left, DefaultMapModule<?> rightModule,
                             MapModel right ) {
        this.right = right;
        this.rightModule = rightModule;
        panel.setMapModel( leftModule, left, rightModule, right );
    }

}
