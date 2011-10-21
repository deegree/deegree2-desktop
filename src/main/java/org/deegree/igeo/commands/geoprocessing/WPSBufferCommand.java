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

package org.deegree.igeo.commands.geoprocessing;

import static java.util.Collections.singletonList;

import java.net.URL;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.deegree.datatypes.QualifiedName;
import org.deegree.enterprise.WebUtils;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLTools;
import org.deegree.igeo.config.MemoryDatasourceType;
import org.deegree.igeo.config.LayerType.MetadataURL;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MemoryDatasource;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.CommandException;
import org.deegree.model.Identifier;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.GMLFeatureAdapter;
import org.deegree.model.feature.GMLFeatureCollectionDocument;

/**
 * Executes a WPS process that returns a feature collection that will be added as new layer into the active map model.
 * The new layer will have
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class WPSBufferCommand extends AbstractCommand {

    private static final ILogger LOG = LoggerFactory.getLogger( WPSBufferCommand.class );

    public static final QualifiedName name = new QualifiedName( "WPS Buffer Command" );

    private static String template = "<?xml version='1.0' encoding='UTF-8' standalone='yes'?><wps:Execute service='WPS' version='0.4.0' store='false' status='false' xmlns:wps='http://www.opengeospatial.net/wps' xmlns:ows='http://www.opengis.net/ows' xmlns:xlink='http://www.w3.org/1999/xlink' ><ows:Identifier>Buffer</ows:Identifier><wps:DataInputs><wps:Input><ows:Identifier>InputGeometry</ows:Identifier><ows:Title>InputGeometry</ows:Title><wps:ComplexValue format='text/xml' encoding='UTF-8' schema='http://schemas.opengis.net/gml/3.0.0/base/gml.xsd'>$InputGeometry$</wps:ComplexValue></wps:Input><wps:Input><ows:Identifier>BufferDistance</ows:Identifier><ows:Title>BufferDistance</ows:Title><wps:LiteralValue dataType='urn:ogc:def:dataType:OGC:0.0:Integer' uom='urn:ogc:def:dataType:OGC:1.0:metre'>$BufferDistance$</wps:LiteralValue></wps:Input><wps:Input><ows:Identifier>EndCapStyle</ows:Identifier><ows:Title>EndCapStyle</ows:Title><wps:LiteralValue dataType='urn:ogc:def:dataType:OGC:0.0:Integer' uom='urn:ogc:def:dataType:OGC:1.0:metre'>$EndCapStyle$</wps:LiteralValue></wps:Input><wps:Input><ows:Identifier>ApproximationQuantization</ows:Identifier><ows:Title>ApproximationQuantization</ows:Title><wps:LiteralValue dataType='urn:ogc:def:dataType:OGC:0.0:Integer' uom='urn:ogc:def:dataType:OGC:1.0:metre'>$ApproximationQuantization$</wps:LiteralValue></wps:Input></wps:DataInputs><wps:OutputDefinitions><wps:Output format='text/xml' encoding='UTF-8' schema='http://schemas.opengis.net/gml/3.0.0/base/gml.xsd' uom='urn:ogc:def:dataType:OGC:1.0:metre'><ows:Identifier>The buffered geometries</ows:Identifier><ows:Title>The buffered geometries</ows:Title></wps:Output></wps:OutputDefinitions></wps:Execute>";

    private FeatureCollection result;

    private String newLayerName;

    private String url;

    private Map<String, Object> parameter;

    private FeatureCollection featureCollection;

    private Layer newLayer;

    private Layer layer;

    private boolean performed = false;

    /**
     * 
     * @param layer
     * @param parameter
     */
    public WPSBufferCommand( Layer layer, Map<String, Object> parameter ) {
        this.newLayerName = (String) parameter.get( "layerName" );
        this.featureCollection = (FeatureCollection) parameter.get( "$InputGeometry$" );
        // this.geomProperty = (QualifiedName) parameter.get( "geometryProperty" );
        this.url = (String) parameter.get( "$WPS" );
        this.parameter = parameter;
        this.layer = layer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {
        performed = false;
        GMLFeatureAdapter adapter = new GMLFeatureAdapter();
        String fcStr = adapter.export( featureCollection ).getAsString();
        int idx = fcStr.lastIndexOf( "?>" );
        fcStr = fcStr.substring( idx + 2 );
        String s = StringTools.replace( template, "$InputGeometry$", fcStr, false );
        Number n = (Number) parameter.get( "$BufferDistance$" );
        s = StringTools.replace( s, "$BufferDistance$", Integer.toString( n.intValue() ), false );
        s = StringTools.replace( s, "$EndCapStyle$", parameter.get( "$EndCapStyle$" ).toString(), false );
        s = StringTools.replace( s, "$ApproximationQuantization$",
                                 parameter.get( "$ApproximationQuantization$" ).toString(), false );
        LOG.logDebug( "WPS execute request: ", s );
        HttpClient client = new HttpClient();
        WebUtils.enableProxyUsage( client, new URL( url ) );
        PostMethod pm = new PostMethod( url );
        pm.setRequestEntity( new StringRequestEntity( s ) );
        client.executeMethod( pm );
        GMLFeatureCollectionDocument doc = new GMLFeatureCollectionDocument();
        doc.load( pm.getResponseBodyAsStream(), url );
        if ( "ServiceExceptionReport".equals( doc.getRootElement().getLocalName() ) ) {
            s = XMLTools.getStringValue( doc.getRootElement().getElementsByTagName( "ServiceException" ).item( 0 ) );
            throw new CommandException( "cann not perform buffer calculation because of: " + s );
        }
        LOG.logDebug( "WPS execute response: ", doc );

        Datasource ds = new MemoryDatasource( new MemoryDatasourceType(), null, null, doc.parse() );

        MapModel mm = layer.getOwner();
        newLayer = new Layer( mm, new Identifier( newLayerName ), newLayerName, newLayerName, singletonList( ds ),
                              Collections.<MetadataURL> emptyList() );
        newLayer.setEditable( true );
        mm.insert( newLayer, layer.getParent(), layer, false );
        performed = true;
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
        return result;
    }

    @Override
    public boolean isUndoSupported() {
        return true;
    }

    @Override
    public void undo()
                            throws Exception {
        if ( performed ) {
            newLayer.getOwner().remove( newLayer );
            performed = false;
        }
    }

}
