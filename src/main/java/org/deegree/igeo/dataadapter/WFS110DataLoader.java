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

package org.deegree.igeo.dataadapter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.FileUtils;
import org.deegree.framework.util.GeometryUtils;
import org.deegree.framework.util.HttpUtils;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.GMLFeatureCollectionDocument;
import org.deegree.model.feature.schema.GMLSchema;
import org.deegree.model.feature.schema.GMLSchemaDocument;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.LogicalOperation;
import org.deegree.model.filterencoding.Operation;
import org.deegree.model.filterencoding.OperationDefines;
import org.deegree.model.filterencoding.PropertyName;
import org.deegree.model.filterencoding.SpatialOperation;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;
import org.deegree.model.spatialschema.MultiSurface;
import org.deegree.model.spatialschema.Surface;
import org.deegree.ogcwebservices.wfs.XMLFactory;
import org.deegree.ogcwebservices.wfs.operation.GetFeature;
import org.deegree.ogcwebservices.wfs.operation.Query;
import org.deegree.ogcwebservices.wfs.operation.GetFeature.RESULT_TYPE;

/**
 * Implementation of {@link WFSDataLoader} for WFS 1.1.0
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class WFS110DataLoader implements WFSDataLoader {

    private static final ILogger LOG = LoggerFactory.getLogger( WFS110DataLoader.class );

    private int timeout = 20000;

    private int maxFeatures = 5000;

    /**
     * package protected to avoid uncontrolled access
     */
    WFS110DataLoader() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.WFSDataLoader#setTimeout(int)
     */
    public void setTimeout( int timeout ) {
        this.timeout = timeout;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.WFSDataLoader#setMaxFeatures(int)
     */
    public void setMaxFeatures( int maxFeatures ) {
        this.maxFeatures = maxFeatures;
    }

    /**
     * reads data from a WFS and returns the result as feature collection. returned data may be limited by a restricting
     * bounding box and/or a filter expression
     * 
     * @param wfs
     * @param property
     * @param bbox
     * @param query
     * @param layer
     * @return feature collection
     */
    public FeatureCollection readFeatureCollection( URL wfs, QualifiedName property, Envelope bbox, Query query,
                                                    Layer layer ) {

        Filter filter = null;
        if ( query != null ) {
            filter = query.getFilter();
        }
        // if all features shall be loaded wether a filter nor a bbox will be set. On the other
        // a user may has defined a general filter but no spatial restrictions should be used
        // loading data (e.g. a WFSDataSource is not LazyLoading).
        Operation op = null;
        if ( bbox != null ) {
            PropertyName pn = new PropertyName( property );
            try {
                op = new SpatialOperation( OperationDefines.BBOX, pn,
                                           GeometryFactory.createSurface( bbox, bbox.getCoordinateSystem() ) );
            } catch ( GeometryException e ) {
                LOG.logError( e.getMessage(), e );
                throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10020" ) );
            }
        }
        if ( op != null && filter != null ) {
            List<Operation> list = new ArrayList<Operation>();
            list.add( op );
            list.add( ( (ComplexFilter) filter ).getOperation() );
            op = new LogicalOperation( OperationDefines.AND, list );
            filter = new ComplexFilter( op );
        } else if ( op != null && filter == null ) {
            filter = new ComplexFilter( op );
        }

        query = Query.create( query.getPropertyNames(), query.getFunctions(), query.getSortProperties(),
                              query.getHandle(), query.getFeatureVersion(), query.getTypeNames(), query.getAliases(),
                              query.getSrsName(), filter, query.getMaxFeatures(), query.getStartPosition(),
                              query.getResultType() );
        query.setSrsName( layer.getOwner().getCoordinateSystem().getPrefixedName() );

        GetFeature getFeature = GetFeature.create( "1.1.0", "99", RESULT_TYPE.RESULTS, GetFeature.FORMAT_GML3, null,
                                                   maxFeatures, 0, -1, -1, new Query[] { query } );

        ApplicationContainer<?> appCont = layer.getOwner().getApplicationContainer();
        XMLFragment xml = null;
        try {
            xml = XMLFactory.export( getFeature );
            xml = HttpUtils.addAuthenticationForXML( xml, appCont.getUser(), appCont.getPassword(),
                                                     appCont.getCertificate( wfs.toURI().toASCIIString() ) );
            if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
                LOG.logDebug( "GetFeature request: ", xml.getAsString() );
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10021" ) );
        }

        Reader reader = null;
        InputStream is = null;
        try {
            HttpMethod m = HttpUtils.performHttpPost( wfs.toURI().toASCIIString(), xml, timeout, appCont.getUser(),
                                                      appCont.getPassword(), null );
            String cs = ( (PostMethod) m ).getResponseCharSet();
            if ( cs != null && cs.length() > 0 ) {
                reader = new InputStreamReader( m.getResponseBodyAsStream(), cs );
            } else {
                is = m.getResponseBodyAsStream();
            }
            if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
                if ( reader != null ) {
                    String s = FileUtils.readTextFile( reader ).toString();
                    LOG.logDebug( "GetFeature Response", s );
                    reader = new StringReader( s );
                } else {
                    String s = FileUtils.readTextFile( is ).toString();
                    LOG.logDebug( "GetFeature Response", s );
                    is = new ByteArrayInputStream( s.getBytes() );
                }
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException(
                                           Messages.getMessage( Locale.getDefault(), "$DG10022", wfs, xml.getAsString() ) );
        }
        GMLFeatureCollectionDocument gml = new GMLFeatureCollectionDocument();
        FeatureCollection fc = null;
        try {
            if ( reader != null ) {
                gml.load( reader, wfs.toExternalForm() );
            } else {
                gml.load( is, wfs.toExternalForm() );
            }
            fc = gml.parse();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException(
                                           Messages.getMessage( Locale.getDefault(), "$DG10023", wfs, xml.getAsString() ) );
        }

        fc = ensureClockwiseSurfaceOrientation( fc );

        return fc;
    }

    /**
     * @param fc
     * @return
     */
    private FeatureCollection ensureClockwiseSurfaceOrientation( FeatureCollection fc ) {
        int c = fc.size();
        for ( int i = 0; i < c; i++ ) {
            Feature feature = fc.getFeature( i );
            FeatureProperty[] fp = feature.getProperties();
            for ( int j = 0; j < fp.length; j++ ) {
                Object value = fp[j].getValue();
                if ( value != null && ( value instanceof Surface || value instanceof MultiSurface ) ) {
                    try {
                        fp[j].setValue( GeometryUtils.ensureClockwise( (Geometry) fp[j].getValue() ) );
                    } catch ( GeometryException e ) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return fc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.deegree.client.presenter.connector.WFSDataLoader#readGMLApplicationSchema(org.deegree.datatypes.QualifiedName
     * )
     */
    public GMLSchema readGMLApplicationSchema( URL wfs, Layer layer, QualifiedName[] featureTypes ) {

        StringBuffer sb = new StringBuffer( 1000 );
        sb.append( "request=DescribeFeatureType&version=1.1.0&service=WFS&TYPENAME=" );
        for ( int i = 0; i < featureTypes.length; i++ ) {
            sb.append( featureTypes[i].getPrefix() ).append( ':' );
            sb.append( featureTypes[i].getLocalName() );
            if ( i < featureTypes.length - 1 ) {
                sb.append( ',' );
            }
        }
        sb.append( "&NAMESPACE=xmlns(" );
        for ( int i = 0; i < featureTypes.length; i++ ) {
            sb.append( featureTypes[i].getPrefix() ).append( '=' );
            sb.append( featureTypes[i].getNamespace().toASCIIString() );
            if ( i < featureTypes.length - 1 ) {
                sb.append( ',' );
            }
        }
        sb.append( ')' );

        InputStream is;
        try {
            ApplicationContainer<?> appCont = layer.getOwner().getApplicationContainer();
            String tmp = HttpUtils.normalizeURL( wfs );
            String request = HttpUtils.addAuthenticationForKVP( sb.toString(), appCont.getUser(),
                                                                appCont.getPassword(), appCont.getCertificate( tmp ) );
            is = HttpUtils.performHttpGet( wfs.toURI().toASCIIString(), request, timeout, appCont.getUser(),
                                           appCont.getPassword(), null ).getResponseBodyAsStream();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10024", wfs, sb.toString() ) );
        }
        GMLSchemaDocument xsd = new GMLSchemaDocument();
        GMLSchema schema = null;
        try {
            xsd.load( is, wfs.toURI().toASCIIString() );
            schema = xsd.parseGMLSchema();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10025", wfs, sb.toString() ) );
        }

        return schema;
    }

}
