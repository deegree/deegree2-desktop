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

package org.deegree.igeo.views.swing.measure;

import java.awt.Dimension;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.MeasureModule;
import org.deegree.igeo.views.swing.DefaultPanel;

/**
 * <code>MeasurePanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class MeasureFooterPanel extends DefaultPanel {

    private static final long serialVersionUID = -5601492969365491682L;

    private static final ILogger LOG = LoggerFactory.getLogger( MeasureFooterPanel.class );

    private MeasureResultLabel measureResult;

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#init(org.deegree.igeo.configuration.ViewForm)
     */
    public void init( ViewFormType viewForm )
                            throws Exception {
        Dimension d = new Dimension( 200, 20 );
        this.setPreferredSize( d );
        this.setVisible( true );
    }

    public void setMeasureLabel( MapModel mapModel, MeasureModule.MeasureType measureType ) {
        if(measureResult != null) {
            this.remove( measureResult );
        }
        switch ( measureType ) {
        case AREA:
            measureResult = new AreaMeasureResultLabel( mapModel );
            this.add( measureResult );
            break;
        case DISTANCE:
            measureResult = new DistanceMeasureResultLabel( mapModel );
            this.add( measureResult );
            break;
        default:
            LOG.logDebug( "unknown measure type: " + measureType );
            break;
        }
    }

}
