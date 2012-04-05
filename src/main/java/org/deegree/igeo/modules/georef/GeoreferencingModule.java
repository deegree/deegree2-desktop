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
package org.deegree.igeo.modules.georef;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;

import org.deegree.igeo.modules.DefaultModule;
import org.deegree.igeo.views.swing.georef.GeoreferencingControlWindow;

/**
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: stranger $
 * 
 * @version $Revision: $, $Date: $
 */
public class GeoreferencingModule extends DefaultModule<Container> {

    private GeoreferencingControlWindow view;

    @Override
    public void actionPerformed( ActionEvent e ) {
        // getViewForm().setVisible(true );
        System.out.println( getViewForm() );
        try {
            getViewForm().setClosed(false);
            getViewForm().setVisible(true);
            
        } catch ( PropertyVetoException e1 ) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // System.out.println( "action" );
    }

    public void open() {
        System.out.println( "opening" );
    }

    @Override
    public GeoreferencingControlWindow getViewForm() {
        synchronized ( this ) {
            if ( view == null ) {
                view = new GeoreferencingControlWindow(this);
                view.registerModule(this );
            }
        }
        view.setVisible( false );
        return view;
    }

}
