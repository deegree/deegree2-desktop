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

package org.deegree.igeo.views.swing.geoprocessing;

import java.awt.BorderLayout;

import org.deegree.crs.components.Unit;
import org.deegree.datatypes.QualifiedName;
import org.deegree.igeo.commands.geoprocessing.BufferCommand.BUFFERTYPE;
import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.views.swing.DefaultDialog;

/**
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class BufferDialog extends DefaultDialog implements BufferModel {

    private static final long serialVersionUID = 8502075486142868203L;

    private BufferPanel bp;

    /**
     * 
     */
    public BufferDialog() {
        setLayout( new BorderLayout() );
        bp = new BufferPanel( this );
    }

    @Override
    public void init( ViewFormType viewForm )
                            throws Exception {
        super.init( viewForm );
        bp.registerModule( this.owner );
        bp.init( viewForm );
        add( bp, BorderLayout.CENTER );
        setVisible( true );
        toFront();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#getCapStyle()
     */
    public int getCapStyle() {
        return bp.getCapStyle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#getDistance()
     */
    public double[] getDistances() {
        return bp.getDistances();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#getGeometryProperty()
     */
    public QualifiedName getGeometryProperty() {
        return bp.getGeometryProperty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#getNewLayerName()
     */
    public String getNewLayerName() {
        return bp.getNewLayerName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#getSegments()
     */
    public int getSegments() {
        return bp.getSegments();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#getBufferType()
     */
    public BUFFERTYPE getBufferType() {
        return bp.getBufferType();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#shallMerge()
     */
    public boolean shallMerge() {
        return bp.shallMerge();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#getPropertyForBufferDistance()
     */
    public QualifiedName getPropertyForBufferDistance() {
        return bp.getPropertyForBufferDistance();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#isOverlayedBuffers()
     */
    public boolean isOverlayedBuffers() {
        return bp.isOverlayedBuffers();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#getBufferUnit()
     */
    public Unit getBufferUnit() {
        return bp.getBufferUnit();
    }

}
