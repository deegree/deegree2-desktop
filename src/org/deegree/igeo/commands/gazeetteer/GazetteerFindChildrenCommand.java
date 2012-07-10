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
package org.deegree.igeo.commands.gazeetteer;

import java.util.Map;
import java.util.UUID;

import org.deegree.datatypes.QualifiedName;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Literal;
import org.deegree.model.filterencoding.Operation;
import org.deegree.model.filterencoding.PropertyIsLikeOperation;
import org.deegree.model.filterencoding.PropertyName;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcbase.SortProperty;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.deegree.ogcwebservices.wfs.operation.Query;
import org.deegree.ogcwebservices.wfs.operation.GetFeature.RESULT_TYPE;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class GazetteerFindChildrenCommand extends AbstractGazetteerCommand {

    private static final QualifiedName name = new QualifiedName( "Find Children" );

    private String geographicIdentifier;

    /**
     * 
     * @param appCont
     * @param gazetteerAddress
     * @param featureType
     * @param properties
     * @param geographicIdentifier
     */
    public GazetteerFindChildrenCommand( ApplicationContainer<?> appCont, String gazetteerAddress,
                                         QualifiedName featureType, Map<String, String> properties,
                                         String geographicIdentifier ) {
        this.appCont = appCont;
        this.gazetteerAddress = gazetteerAddress;
        this.featureType = featureType;
        this.geographicIdentifier = geographicIdentifier;
        this.properties = properties;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {
        if ( !capabilitiesMap.containsKey( gazetteerAddress ) ) {
            loadCapabilities();
        }
        WFSCapabilities capabilities = capabilitiesMap.get( gazetteerAddress );

        PropertyName propertyName;
        String tmp = properties.get( "ParentIdentifier" );
        if ( tmp.startsWith( "{" ) ) {
            propertyName = new PropertyName( new QualifiedName( tmp ) );
        } else {
            propertyName = new PropertyName( new QualifiedName( tmp, featureType.getNamespace() ) );
        }
        Literal literal = new Literal( geographicIdentifier );
        Operation operation = new PropertyIsLikeOperation( propertyName, literal, '*', '?', '/' );
        ComplexFilter filter = new ComplexFilter( operation );
        
        PropertyPath sortProperty = createPropertyPath( properties.get( "DisplayName" ) );
        SortProperty[] sp = new SortProperty[] { SortProperty.create( sortProperty, "ASC" ) };

        // create Query and GetFeature request
        Query query = Query.create( null, null, sp, null, null, new QualifiedName[] { featureType }, null, null,
                                    filter, 500, 0, RESULT_TYPE.RESULTS );
        GetFeature getFeature = GetFeature.create( capabilities.getVersion(), UUID.randomUUID().toString(),
                                                   RESULT_TYPE.RESULTS, GetFeature.FORMAT_GML3, null, 500, 0, -1, -1,
                                                   new Query[] { query } );

        // perform GetFeature request and create resulting GazetteerItems list
        FeatureCollection fc = performGetFeature( capabilities, getFeature );
        createItemsList( fc );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getName()
     */
    public QualifiedName getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getResult()
     */
    public Object getResult() {
        return items;
    }

}
