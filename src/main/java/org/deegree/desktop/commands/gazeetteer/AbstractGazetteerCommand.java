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
package org.deegree.desktop.commands.gazeetteer;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.dataadapter.DataAccessException;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.modules.gazetteer.GazetteerItem;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.HttpUtils;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.Command;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.GMLFeatureCollectionDocument;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.Point;
import org.deegree.ogcbase.ElementStep;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcbase.PropertyPathStep;
import org.deegree.ogcwebservices.getcapabilities.DCPType;
import org.deegree.ogcwebservices.getcapabilities.HTTP;
import org.deegree.ogcwebservices.wfs.XMLFactory;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilitiesDocument;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;

/**
 * abstract {@link Command} implementation. Providing several methods that are common to all concrete gazetteer
 * commands.
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
abstract class AbstractGazetteerCommand extends AbstractCommand {

    private static final ILogger LOG = LoggerFactory.getLogger( AbstractGazetteerCommand.class );

    protected List<GazetteerItem> items;

    protected ApplicationContainer<?> appCont;

    protected String gazetteerAddress;

    protected QualifiedName featureType;

    protected static Map<String, WFSCapabilities> capabilitiesMap;

    protected Map<String, String> properties;

    static {
        if ( capabilitiesMap == null ) {
            capabilitiesMap = new HashMap<String, WFSCapabilities>();
        }
    }

    /**
     * @throws IOException
     * @throws HttpException
     * @throws SAXException
     * @throws XMLException
     * 
     */
    protected void loadCapabilities()
                            throws Exception {
        InputStream is = HttpUtils.performHttpGet( gazetteerAddress, "request=GetCapabilities&service=WFS", 60000,
                                                   appCont.getUser(), appCont.getPassword(), null ).getResponseBodyAsStream();
        WFSCapabilitiesDocument doc = new WFSCapabilitiesDocument();
        doc.load( is, gazetteerAddress );
        WFSCapabilities capa = (WFSCapabilities) doc.parseCapabilities();
        capabilitiesMap.put( gazetteerAddress, capa );
    }

    protected FeatureCollection performGetFeature( WFSCapabilities capabilities, GetFeature getFeature ) {

        // find a valid URL for performing GetFeature requests
        URL wfs = null;
        org.deegree.ogcwebservices.getcapabilities.Operation[] op = capabilities.getOperationsMetadata().getOperations();
        for ( org.deegree.ogcwebservices.getcapabilities.Operation operation : op ) {
            if ( "GetFeature".equalsIgnoreCase( operation.getName() ) ) {
                DCPType[] dcp = operation.getDCPs();
                for ( DCPType dcpType : dcp ) {
                    if ( dcpType.getProtocol() instanceof HTTP ) {
                        wfs = ( (HTTP) dcpType.getProtocol() ).getPostOnlineResources()[0];
                    }
                }
            }
        }

        XMLFragment gf;
        try {
            gf = XMLFactory.export( getFeature );
            LOG.logDebug( "GetFeature request: ", gf.getAsPrettyString() );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "*****" ) );
        }

        InputStream is;
        try {
            is = HttpUtils.performHttpPost( wfs.toURI().toASCIIString(), gf, 60000, null, null, null ).getResponseBodyAsStream();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "*****", wfs, gf ) );
        }

        GMLFeatureCollectionDocument gml = new GMLFeatureCollectionDocument();
        FeatureCollection fc = null;
        try {
            gml.load( is, wfs.toExternalForm() );
            fc = gml.parse();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "*****", wfs, gf ) );
        }

        return fc;
    }

    protected void createItemsList( FeatureCollection fc ) {
        items = new ArrayList<GazetteerItem>( fc.size() );
        Iterator<Feature> iterator = fc.iterator();
        QualifiedName gi;
        String tmp = properties.get( "GeographicIdentifier" );
        if ( tmp.startsWith( "{" ) ) {
            gi = new QualifiedName( tmp );
        } else {
            gi = new QualifiedName( tmp, featureType.getNamespace() );
        }
        QualifiedName gai = null;
        if ( properties.get( "AlternativeGeographicIdentifier" ) != null ) {
            tmp = properties.get( "AlternativeGeographicIdentifier" );
            if ( tmp.startsWith( "{" ) ) {
                gai = new QualifiedName( tmp );
            } else {
                gai = new QualifiedName( tmp, featureType.getNamespace() );
            }
        }
        QualifiedName ge;
        tmp = properties.get( "GeographicExtent" );
        if ( tmp.startsWith( "{" ) ) {
            ge = new QualifiedName( tmp );
        } else {
            ge = new QualifiedName( tmp, featureType.getNamespace() );
        }
        QualifiedName pos = null;
        if ( properties.get( "Position" ) != null ) {
            tmp = properties.get( "Position" );
            if ( tmp.startsWith( "{" ) ) {
                pos = new QualifiedName( tmp );
            } else {
                pos = new QualifiedName( tmp, featureType.getNamespace() );
            }
        }
        QualifiedName par = null;
        if ( properties.get( "ParentIdentifier" ) != null ) {
            tmp = properties.get( "ParentIdentifier" );
            if ( tmp.startsWith( "{" ) ) {
                par = new QualifiedName( tmp );
            } else {
                par = new QualifiedName( tmp, featureType.getNamespace() );
            }
        }
        QualifiedName disp = null;
        tmp = properties.get( "DisplayName" );
        if ( tmp.startsWith( "{" ) ) {
            disp = new QualifiedName( tmp );
        } else {
            disp = new QualifiedName( tmp, featureType.getNamespace() );
        }

        QualifiedName high = null;
        tmp = properties.get( "HighlightGeometry" );
        if ( tmp.startsWith( "{" ) ) {
            high = new QualifiedName( tmp );
        } else {
            high = new QualifiedName( tmp, featureType.getNamespace() );
        }

        while ( iterator.hasNext() ) {
            Feature feature = (Feature) iterator.next();
            String gmlID = feature.getId();
            String geoId = (String) feature.getDefaultProperty( gi ).getValue();
            String dispName = (String) feature.getDefaultProperty( disp ).getValue();
            String altGeoId = null;
            if ( gai != null ) {
                FeatureProperty fp = feature.getDefaultProperty( gai );
                if ( fp != null ) {
                    altGeoId = (String) fp.getValue();
                }
            }
            Geometry geoExt = (Geometry) feature.getDefaultProperty( ge ).getValue();
            Geometry highlight = (Geometry) feature.getDefaultProperty( high ).getValue();
            Point position = null;
            if ( pos != null ) {
                FeatureProperty fp = feature.getDefaultProperty( pos );
                if ( fp != null ) {
                    position = (Point) fp.getValue();
                }
            }
            String parent = null;
            if ( par != null ) {
                FeatureProperty fp = feature.getDefaultProperty( par );
                if ( fp != null ) {
                    parent = (String) fp.getValue();
                }
            }
            items.add( new GazetteerItem( gmlID, geoId, parent, altGeoId, geoExt, position, dispName, highlight ) );
        }
    }

    protected PropertyPath createPropertyPath( String name ) {
        List<String> l1 = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        boolean opened = false;
        for ( int i = 0; i < name.length(); i++ ) {
            char c = name.charAt( i );
            if ( c == '{' ) {
                opened = true;
            }
            if ( c == '/' && !opened ) {
                l1.add( sb.toString() );
                sb.delete( 0, sb.length() );
            } else {
                sb.append( c );
            }
            if ( c == '}' ) {
                opened = false;
            }
        }
        l1.add( sb.toString() );

        String[] tmp = l1.toArray( new String[l1.size()] );
        List<PropertyPathStep> steps = new ArrayList<PropertyPathStep>();
        for ( String string : tmp ) {
            QualifiedName qn = null;
            if ( name.startsWith( "{" ) ) {
                qn = new QualifiedName( string );
            } else {
                qn = new QualifiedName( string, featureType.getNamespace() );
            }
            steps.add( new ElementStep( qn ) );
        }

        return new PropertyPath( steps );
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
