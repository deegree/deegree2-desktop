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
package org.deegree.desktop.views.swing;

import java.awt.BorderLayout;

import org.deegree.desktop.config.ViewFormType;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class MapOverviewInnerFrame extends DefaultInnerFrame {

    private static final long serialVersionUID = 375795520277885190L;

    private MapOverviewPanel mapOverviewPanel;

    @Override
    public void init( ViewFormType viewForm )
                            throws Exception {
        super.init( viewForm );
        mapOverviewPanel = new MapOverviewPanel();
        mapOverviewPanel.registerModule( this.owner );
        mapOverviewPanel.init( viewForm );
        initGUI();
        setVisible( true );
        toFront();
    }

    private void initGUI() {
        BorderLayout thisLayout = new BorderLayout();        
        getContentPane().setLayout( thisLayout );
        getContentPane().add( mapOverviewPanel, BorderLayout.CENTER );
    }

}
