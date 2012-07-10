//$HeadURL: svn+ssh://developername@svn.wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
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
package org.deegree.igeo.commands.model;

import static java.util.Collections.singletonList;

import java.awt.Color;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.graphics.sld.PolygonSymbolizer;
import org.deegree.graphics.sld.StyleFactory;
import org.deegree.graphics.sld.UserStyle;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.config.DirectStyleType;
import org.deegree.igeo.config.EnvelopeType;
import org.deegree.igeo.config.MemoryDatasourceType;
import org.deegree.igeo.config.LayerType.MetadataURL;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.DirectStyle;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MemoryDatasource;
import org.deegree.igeo.mapmodel.NamedStyle;
import org.deegree.kernel.AbstractCommand;
import org.deegree.model.Identifier;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Geometry;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author: admin $
 * 
 * @version $Revision: $, $Date: $
 */
public class AddMemoryLayerCommand extends AbstractCommand {

    public static final QualifiedName commandName = new QualifiedName( "add memory based layer command" );

    private ApplicationContainer<?> appContainer;

    private String title;

    private Layer layerBefore;

    private FeatureCollection fc;

    private Layer newLayer;
    
    private UserStyle style;
    
    public  AddMemoryLayerCommand() {
        PolygonSymbolizer sym = StyleFactory.createPolygonSymbolizer( Color.WHITE, Color.RED, 2 );
        sym.getFill().setOpacity( 0.3 );
        style = (UserStyle) StyleFactory.createStyle( "default", sym );
    }

    /**
     * 
     * @param appContainer
     */
    public void setApplicationContainer( ApplicationContainer<?> appContainer ) {
        this.appContainer = appContainer;
    }

    /**
     * 
     * @param title
     *            layer title
     */
    public void setTitle( String title ) {
        this.title = title;
    }

    /**
     * 
     * @param layerBefore
     */
    public void setLayerBefore( Layer layerBefore ) {
        this.layerBefore = layerBefore;
    }
    
    public void setStyle(UserStyle style) {
        
    }

    /**
     * 
     * @param geometries
     *            geometries to add as new layer
     */
    public void setGeometries( List<Geometry> geometries )
                            throws Exception {

        PropertyType[] pt = new PropertyType[2];
        pt[0] = FeatureFactory.createSimplePropertyType(
                                                         new QualifiedName( "id",
                                                                            URI.create( "http://www.deegree.org/app" ) ),
                                                         Types.INTEGER, false );

        pt[1] = FeatureFactory.createSimplePropertyType(
                                                         new QualifiedName( "geometry",
                                                                            URI.create( "http://www.deegree.org/app" ) ),
                                                         Types.GEOMETRY, false );

        FeatureType ft = FeatureFactory.createFeatureType( UUID.randomUUID().toString(), false, pt );
        fc = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(), geometries.size() );

        int c = 0;
        for ( Geometry geometry : geometries ) {
            FeatureProperty[] fp = new FeatureProperty[2];
            fp[0] = FeatureFactory.createFeatureProperty( pt[0].getName(), c++ );
            fp[1] = FeatureFactory.createFeatureProperty( pt[1].getName(), geometry );
            fc.add( FeatureFactory.createFeature( UUID.randomUUID().toString(), ft, fp ) );
        }
    }

    /**
     * 
     * @param fc
     *            feature collection to add as new layer
     */
    public void setFeatureCollection( FeatureCollection fc ) {
        this.fc = fc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {
        MapModel mm = appContainer.getMapModel( null );
        Envelope env = mm.getEnvelope();
        MemoryDatasourceType mdst = new MemoryDatasourceType();
        EnvelopeType et = new EnvelopeType();
        et.setMinx( env.getMin().getX() );
        et.setMiny( env.getMin().getY() );
        et.setMaxx( env.getMax().getX() );
        et.setMaxy( env.getMax().getY() );
        et.setCrs( mm.getEnvelope().getCoordinateSystem().getPrefixedName() );
        mdst.setExtent( et );
        mdst.setMinScaleDenominator( 0d );
        mdst.setMaxScaleDenominator( 100000000d );
        Datasource ds = new MemoryDatasource( mdst, null, null, fc );

        Identifier id = new Identifier( title );
        int i = 0;
        while ( mm.exists( id ) ) {
            id = new Identifier( title + "_" + i++ );
        }
        newLayer = new Layer( mm, id, id.getValue(), title, singletonList( ds ), Collections.<MetadataURL> emptyList() );
        newLayer.setEditable( false );
        
        List<NamedStyle> styles = new ArrayList<NamedStyle>();
        DirectStyleType dst = new DirectStyleType();
        dst.setCurrent( true );       
        dst.setName( style.getName() );
        dst.setTitle( style.getTitle() );
        dst.setAbstract( style.getAbstract() );
        dst.setCurrent( true );
        //styles.add( new NamedStyle( dst, newLayer ) );
        styles.add( new DirectStyle( dst, style, newLayer ) );
        newLayer.setStyles( styles );
        newLayer.setVisible( true );
        if ( layerBefore == null ) {
            mm.insert( newLayer, mm.getLayerGroups().get( 0 ), null, true );
        } else {
            mm.insert( newLayer, layerBefore.getParent(), layerBefore, true );
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getName()
     */
    public QualifiedName getName() {
        return commandName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getResult()
     */
    public Object getResult() {
        return newLayer;
    }

}
