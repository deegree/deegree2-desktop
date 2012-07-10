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

package org.deegree.igeo.modules;

import java.awt.Container;
import java.util.UUID;

import javax.swing.JInternalFrame;

import org.deegree.datatypes.QualifiedName;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.views.swing.digitize.EditFeatureFrame;
import org.deegree.igeo.views.swing.digitize.EditFeatureIFrame;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class EditFeatureFormFactory {

    /**
     * 
     * @param layer
     * @param dataAccessAdapter
     * @param featureType
     * @param viewPlatform
     * @param mapModule
     * @return EditFeature
     */
    public static EditFeature create( Layer layer, FeatureAdapter dataAccessAdapter, QualifiedName featureType,
                                      String viewPlatform, IModule<?> mapModule ) {

        // TODO
        // read feature type and viewPlatform specific EditFeatureForm class from
        // project configuration
        // following code should be default if no specific class is defined

        FeatureCollection fc = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(), 1 );
        fc.add( dataAccessAdapter.getDefaultFeature( featureType ) );

        return create( layer, fc, viewPlatform, mapModule );

    }

    /**
     * 
     * @param layer
     * @param featureCollection
     * @param viewPlatform
     * @param mapModule
     * @return EditFeature
     */
    @SuppressWarnings("unchecked")
    public static EditFeature create( Layer layer, FeatureCollection featureCollection, String viewPlatform,
                                      IModule<?> mapModule ) {

        // TODO
        // read feature type and viewPlatform specific EditFeatureForm class from
        // project configuration
        // following code should be default if no specific class is defined
        ApplicationContainer<?> appCont = mapModule.getApplicationContainer();
        if ( "application".equalsIgnoreCase( viewPlatform ) ) {
            if ( mapModule != null ) {
                Object viewForm = mapModule.getViewForm();
                if ( viewForm instanceof JInternalFrame ) {
                    EditFeatureIFrame fr = new EditFeatureIFrame( mapModule.getApplicationContainer(), layer,
                                                                  featureCollection );
                    Container cont = (Container) mapModule.getGUIContainer();
                    cont.add( fr );
                    fr.toFront();
                    return fr;
                } else {
                    return new EditFeatureFrame( (ApplicationContainer<Container>) appCont, layer, featureCollection );
                }
            } else {
                return new EditFeatureFrame( (ApplicationContainer<Container>) appCont, layer, featureCollection );
            }
        } else {
            return new EditFeatureFrame( (ApplicationContainer<Container>) appCont, layer, featureCollection );
        }

    }
}
