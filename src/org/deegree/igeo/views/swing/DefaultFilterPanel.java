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

package org.deegree.igeo.views.swing;

import java.awt.Dimension;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.deegree.datatypes.QualifiedName;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.views.Filter;
import org.deegree.igeo.views.swing.addlayer.FeatureTypeWrapper;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.LogicalOperation;
import org.deegree.model.filterencoding.Operation;
import org.deegree.model.filterencoding.OperationDefines;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;

/**
 * The <code>DefaultFilter</code> is the default implementation for the 'application' viewPlattform.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class DefaultFilterPanel extends JPanel implements Filter {

    private static final long serialVersionUID = -3917287735374802469L;

    private FeatureTypeWrapper ftWrapper;

    private AttributeCriteriaPanel attributeCriteriaPanel;

    private SpatialCriteriumPanel spatialCriteriaPanel;

    private FilterPanel filterPanel;

    private MapModel mapModel;

    /**
     * @param wfsUrl
     *            the URL of the requested WFS
     * @param wfsCapabilities
     *            the capabilities of the WFS
     * @param featureType
     *            the selected featureType
     * @param mapModelAdapter
     *            the mapModelAdapter
     * @param appContainer
     *            the applicationContainer
     * @throws Exception 
     */
    public DefaultFilterPanel( URL wfsUrl, WFSCapabilities wfsCapabilities, QualifiedName featureType,
                               MapModel mapModel, ApplicationContainer<?> appContainer ) throws Exception {
        this.ftWrapper = new FeatureTypeWrapper( wfsUrl, wfsCapabilities, featureType, appContainer );
        this.mapModel = mapModel;

        JTabbedPane tabbedPane = new JTabbedPane();

        this.attributeCriteriaPanel = new AttributeCriteriaPanel( this.ftWrapper.getNonGeometryProperties(), featureType, appContainer.getSettings().getDictionaries() );
        this.attributeCriteriaPanel.setPreferredSize( new Dimension( 410, 470 ) );
        tabbedPane.addTab( Messages.getMessage( Locale.getDefault(), "$MD10143" ), this.attributeCriteriaPanel );

        this.spatialCriteriaPanel = new SpatialCriteriumPanel( this.ftWrapper.getGeometryProperties(), this.mapModel );
        this.spatialCriteriaPanel.setPreferredSize( new Dimension( 410, 470 ) );
        tabbedPane.addTab( Messages.getMessage( Locale.getDefault(), "$MD10144" ), this.spatialCriteriaPanel );

        this.filterPanel = new FilterPanel( this );
        tabbedPane.addTab( Messages.getMessage( Locale.getDefault(), "$MD10145" ), this.filterPanel );

        this.add( tabbedPane );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.Filter#getFilter()
     */
    public Object getFilter() {
        // collect all operations defined in attribute panel and spatial panel and create one list
        List<Operation> allOperations = new ArrayList<Operation>();
        List<Operation> attOperations = this.attributeCriteriaPanel.getOperations();
        Operation spatOperation = this.spatialCriteriaPanel.getOperation();
        for ( Operation operation : attOperations ) {
            allOperations.add( operation );
        }
        if ( spatOperation != null ) {
            allOperations.add( spatOperation );
        }

        if ( allOperations.size() > 0 ) {
            Operation finalOperation = null;
            if ( allOperations.size() == 1 ) {
                // if only one operation is created, it is not needed to create a logical operation
                finalOperation = allOperations.get( 0 );
            } else {
                // create a logical operation out of all operations combined with the logical
                // operation
                int operation = OperationDefines.getIdByName( this.attributeCriteriaPanel.getOperation() );
                finalOperation = new LogicalOperation( operation, allOperations );
            }
            // create the filter
            ComplexFilter f = new ComplexFilter( finalOperation );
            return f;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.views.Filter#setFilter(java.lang.Object)
     */
    public void setFilter( Object filter ) {
    }
}
