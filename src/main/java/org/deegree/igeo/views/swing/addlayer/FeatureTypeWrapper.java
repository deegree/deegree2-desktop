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

package org.deegree.igeo.views.swing.addlayer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.HttpUtils;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.schema.XMLSchemaException;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.i18n.Messages;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.GMLSchema;
import org.deegree.model.feature.schema.GMLSchemaDocument;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.xml.sax.SAXException;

/**
 * The <code>FeatureTypeWrapper</code> encapsulates stuff around a WFSFeatureType.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class FeatureTypeWrapper {

    private static final ILogger LOG = LoggerFactory.getLogger( FeatureTypeWrapper.class );

    private URL wfsUrl;

    private WFSCapabilities wfsCapabilities;

    private QualifiedName featureType;

    private GMLSchemaDocument xsd;

    private GMLSchema schema;

    private ApplicationContainer<?> appContainer;

    /**
     * @param wfsUrl
     *            the URL of the WFS
     * @param wfsCapabilities
     *            the capabilities of the WFS
     * @param featureType
     *            the featureType
     * @param appContainer
     *            the applicationContainer
     * @throws Exception
     */
    public FeatureTypeWrapper( URL wfsUrl, WFSCapabilities wfsCapabilities, QualifiedName featureType,
                               ApplicationContainer<?> appContainer ) throws Exception {
        this.wfsUrl = wfsUrl;
        this.wfsCapabilities = wfsCapabilities;
        this.featureType = featureType;
        this.appContainer = appContainer;

        this.xsd = new GMLSchemaDocument();
        String describeFeatureTypeRequest = getDescribeFeatureTypeRequest();
        try {
            this.xsd.load( new URL( describeFeatureTypeRequest ) );
            this.schema = this.xsd.parseGMLSchema();
        } catch ( MalformedURLException e ) {
            LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10064", describeFeatureTypeRequest,
                                               e.getMessage() ) );
            throw e;
        } catch ( IOException e ) {
            LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10024", this.wfsUrl, e.getMessage() ) );
            throw e;
        } catch ( SAXException e ) {
            LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10025", this.wfsUrl, e.getMessage() ) );
            throw e;
        } catch ( XMLSchemaException e ) {
            LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10065", xsd.getAsPrettyString(), e.getMessage() ) );
            throw e;
        } catch ( XMLParsingException e ) {
            LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10065", xsd.getAsPrettyString(), e.getMessage() ) );
            throw e;
        } catch ( UnknownCRSException e ) {
            LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10066", xsd.getAsPrettyString(), e.getMessage() ) );
            throw e;
        }

    }

    /**
     * @return a list of the properties of the type geometry
     */
    public List<QualifiedName> getGeometryProperties() {
        FeatureType ft = this.schema.getFeatureType( this.featureType );
        List<QualifiedName> qNames = new ArrayList<QualifiedName>();
        PropertyType[] props = ft.getProperties();
        for ( int j = 0; j < props.length; j++ ) {
            if ( props[j].getType() == Types.GEOMETRY ) {
                QualifiedName ftQualityName = this.featureType;
                QualifiedName qName = new QualifiedName( ftQualityName.getPrefix(), props[j].getName().getLocalName(),
                                                         ftQualityName.getNamespace() );
                qNames.add( qName );
            }
        }
        return qNames;
    }

    /**
     * @return a list of all properties which are not from type geometry or feature
     */
    public List<QualifiedName> getNonGeometryProperties() {
        FeatureType ft = this.schema.getFeatureType( this.featureType );
        List<QualifiedName> qNames = new ArrayList<QualifiedName>();
        PropertyType[] props = ft.getProperties();
        for ( int j = 0; j < props.length; j++ ) {
            if ( props[j].getType() != Types.GEOMETRY && props[j].getType() != Types.FEATURE ) {
                QualifiedName ftQualityName = this.featureType;
                QualifiedName qName = new QualifiedName( ftQualityName.getPrefix(), props[j].getName().getLocalName(),
                                                         ftQualityName.getNamespace() );
                qNames.add( qName );
            }
        }
        return qNames;
    }

    public GMLSchemaDocument getFeatureTypeDescriptionAsXML() {
        return this.xsd;
    }

    private String getDescribeFeatureTypeRequest() {
        StringBuffer sb = new StringBuffer( 300 );
        sb.append( wfsUrl ).append( '?' );
        sb.append( "SERVICE=WFS&REQUEST=DescribeFeatureType" );
        sb.append( "&VERSION=" ).append( wfsCapabilities.getVersion() );
        sb.append( "&TYPENAME=" ).append( featureType.getPrefixedName() );
        if ( featureType.getNamespace() != null ) {
            sb.append( "&NAMESPACE=xmlns(" ).append( featureType.getPrefix() ).append( '=' );
            sb.append( featureType.getNamespace().toASCIIString() ).append( ')' );
        }
        // add authentication informations if available
        return HttpUtils.addAuthenticationForKVP( sb.toString(), appContainer.getUser(), appContainer.getPassword(),
                                                  appContainer.getCertificate( wfsUrl.toExternalForm() ) );
    }

}
