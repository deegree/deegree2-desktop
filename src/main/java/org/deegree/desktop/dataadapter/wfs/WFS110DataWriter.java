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

package org.deegree.desktop.dataadapter.wfs;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.dataadapter.DataAccessException;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.FileUtils;
import org.deegree.framework.util.HttpUtils;
import org.deegree.framework.utils.OWSExceptionParser;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.filterencoding.FeatureFilter;
import org.deegree.model.filterencoding.FeatureId;
import org.deegree.ogcbase.CommonNamespaces;
import org.deegree.ogcbase.PropertyPath;
import org.deegree.ogcbase.PropertyPathFactory;
import org.deegree.ogcwebservices.wfs.XMLFactory;
import org.deegree.ogcwebservices.wfs.operation.transaction.Delete;
import org.deegree.ogcwebservices.wfs.operation.transaction.Insert;
import org.deegree.ogcwebservices.wfs.operation.transaction.Transaction;
import org.deegree.ogcwebservices.wfs.operation.transaction.TransactionOperation;
import org.deegree.ogcwebservices.wfs.operation.transaction.Update;
import org.deegree.ogcwebservices.wfs.operation.transaction.Insert.ID_GEN;

/**
 * Implementation of {@link WFSDataWriter} for WFS 1.1.0
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class WFS110DataWriter implements WFSDataWriter {

    private static final ILogger LOG = LoggerFactory.getLogger( WFS110DataWriter.class );

    private int timeout = 25000;

    /**
     * package protected to avoid uncontrolled access
     */
    WFS110DataWriter() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.WFSDataWriter#setTimeout(int)
     */
    public void setTimeout( int timeout ) {
        this.timeout = timeout;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.WFSDataWriter#deleteFeatures(java.io.URL,
     * org.deegree.model.feature.FeatureCollection)
     */
    public int deleteFeatures( URL wfsURL, FeatureCollection featureCollection, Layer layer ) {
        int count = 0;
        if ( featureCollection.size() > 0 ) {
            List<TransactionOperation> list = new ArrayList<TransactionOperation>( featureCollection.size() );
            Iterator<Feature> iter = featureCollection.iterator();
            while ( iter.hasNext() ) {
                Feature feature = (Feature) iter.next();
                FeatureFilter filter = new FeatureFilter( new ArrayList<FeatureId>( 1 ) );
                filter.addFeatureId( new FeatureId( feature.getId() ) );
                QualifiedName qn = feature.getFeatureType().getName();
                Delete delete = new Delete( UUID.randomUUID().toString(), qn, filter );
                list.add( delete );
            }
            XMLFragment xml = performTransaction( wfsURL, list, layer.getOwner().getApplicationContainer() );
            String xpath = "wfs:TransactionSummary/wfs:totalUpdated";
            try {
                count = XMLTools.getNodeAsInt( xml.getRootElement(), xpath, CommonNamespaces.getNamespaceContext(),
                                               featureCollection.size() );
            } catch ( XMLParsingException e ) {
                LOG.logError( e.getMessage(), e );
                String[] exc = OWSExceptionParser.parseException( xml );
                throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10084", wfsURL, exc[0],
                                                                    exc[1] ) );
            }
        }
        return count;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.WFSDataWriter#insertFeatures(java.io.URL,
     * org.deegree.model.feature.FeatureCollection)
     */
    public List<String> insertFeatures( URL wfsURL, FeatureCollection featureCollection, Layer layer ) {

        if ( featureCollection.size() > 0 ) {
            // force that a WFS generates new IDs to ensure ID - data type matches data type of the backend
            // ad that a features ID is really unique
            Insert insert = new Insert( UUID.randomUUID().toString(), ID_GEN.GENERATE_NEW, null, featureCollection );
            List<TransactionOperation> list = new ArrayList<TransactionOperation>();
            list.add( insert );
            XMLFragment xml = performTransaction( wfsURL, list, layer.getOwner().getApplicationContainer() );
            // read and return IDs of inserted features. This is required to update features within the layer
            // on which a transaction has been performed with IDs generated by the WFS
            String xpath = "wfs:InsertResults/wfs:Feature/ogc:FeatureId/@fid";
            List<String> ids = null;
            try {
                String[] tmp = XMLTools.getRequiredNodesAsStrings( xml.getRootElement(), xpath,
                                                                   CommonNamespaces.getNamespaceContext() );
                ids = new ArrayList<String>( tmp.length );
                for ( String string : tmp ) {
                    ids.add( string );
                }
            } catch ( XMLParsingException e ) {
                LOG.logError( e.getMessage(), e );
                String[] exc = OWSExceptionParser.parseException( xml );
                throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10084", wfsURL, exc[0],
                                                                    exc[1] ) );
            }
            return ids;
        } else {
            return new ArrayList<String>( 1 );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.WFSDataWriter#updateFeatures(java.io.URL,
     * org.deegree.model.feature.FeatureCollection)
     */
    public int updateFeatures( URL wfsURL, FeatureCollection featureCollection, Layer layer ) {
        int count = 0;
        if ( featureCollection.size() > 0 ) {
            List<TransactionOperation> list = new ArrayList<TransactionOperation>( featureCollection.size() );
            Iterator<Feature> iter = featureCollection.iterator();
            while ( iter.hasNext() ) {
                Feature feature = (Feature) iter.next();
                FeatureFilter filter = new FeatureFilter( new ArrayList<FeatureId>( 1 ) );
                filter.addFeatureId( new FeatureId( feature.getId() ) );
                QualifiedName qn = feature.getFeatureType().getName();
                FeatureProperty[] fp = feature.getProperties();
                Map<PropertyPath, FeatureProperty> propertyMap = new HashMap<PropertyPath, FeatureProperty>();
                for ( FeatureProperty featureProperty : fp ) {
                    PropertyPath pp = PropertyPathFactory.createPropertyPath( featureProperty.getName() );
                    propertyMap.put( pp, featureProperty );
                }
                Update update = new Update( UUID.randomUUID().toString(), qn, propertyMap, filter );
                list.add( update );
            }
            XMLFragment xml = performTransaction( wfsURL, list, layer.getOwner().getApplicationContainer() );
            String xpath = "wfs:TransactionSummary/wfs:totalUpdated";
            try {
                count = XMLTools.getNodeAsInt( xml.getRootElement(), xpath, CommonNamespaces.getNamespaceContext(),
                                               featureCollection.size() );
            } catch ( XMLParsingException e ) {
                LOG.logError( e.getMessage(), e );
                String[] exc = OWSExceptionParser.parseException( xml );
                throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10084", wfsURL, exc[0],
                                                                    exc[1] ) );
            }
        }
        return count;
    }

    private XMLFragment performTransaction( URL wfsURL, List<TransactionOperation> list, ApplicationContainer<?> appCont ) {

        Transaction transaction = new Transaction( null, null, null, null, list, true, null );
        InputStream is;
        try {
            XMLFragment xml = XMLFactory.export( transaction );
            HttpUtils.addAuthenticationForXML( xml, appCont.getUser(), appCont.getPassword(),
                                               appCont.getCertificate( wfsURL.toURI().toASCIIString() ) );
            if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
                LOG.logDebug( "WFS Transaction: ", xml.getAsString() );
            }
            is = HttpUtils.performHttpPost( wfsURL.toURI().toASCIIString(), xml, timeout, appCont.getUser(),
                                            appCont.getPassword(), null ).getResponseBodyAsStream();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10082", wfsURL ) );
        }
        XMLFragment xml = null;
        try {
            if ( LOG.getLevel() == ILogger.LOG_DEBUG ) {
                String st = FileUtils.readTextFile( is ).toString();
                is = new ByteArrayInputStream( st.getBytes() );
                LOG.logDebug( "WFS transaction result: ", st );
            }
            xml = new XMLFragment();
            xml.load( is, wfsURL.toExternalForm() );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage() + ": " + xml.getAsString(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10080", wfsURL, "" ) );
        }
        if ( "ExceptionReport".equalsIgnoreCase( xml.getRootElement().getLocalName() ) ) {
            LOG.logError( "Transaction on: " + xml.getAsString() + " failed" );
            // TODO
            // extract exception message
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10083", wfsURL, "" ) );
        }
        return xml;
    }
}
