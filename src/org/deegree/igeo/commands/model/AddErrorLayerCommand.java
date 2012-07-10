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
import org.deegree.framework.util.BootLogger;
import org.deegree.framework.util.Pair;
import org.deegree.graphics.sld.Mark;
import org.deegree.graphics.sld.PointSymbolizer;
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
import org.deegree.igeo.mapmodel.SystemLayer;
import org.deegree.kernel.AbstractCommand;
import org.deegree.model.Identifier;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.Point;
import org.deegree.ogcbase.CommonNamespaces;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class AddErrorLayerCommand extends AbstractCommand {

    private static final QualifiedName name = new QualifiedName( "Add Error Layer" );

    private ApplicationContainer<?> appCont;

    private Layer sourceLayer;

    private List<Pair<String, Point>> errorLocations;

    private Layer newLayer;

    private static FeatureType ft;
    static {
        try {
            if ( ft == null ) {
                PropertyType[] pt = new PropertyType[3];
                URI nsp = URI.create( "http://www.deegree.org/igeo" );
                pt[0] = FeatureFactory.createPropertyType( new QualifiedName( "ID", nsp ),
                                                           new QualifiedName( "xsd", "int", CommonNamespaces.XSNS ),
                                                           true );
                pt[1] = FeatureFactory.createPropertyType( new QualifiedName( "error", nsp ),
                                                           new QualifiedName( "xsd", "string", CommonNamespaces.XSNS ),
                                                           true );
                pt[2] = FeatureFactory.createPropertyType( new QualifiedName( "geom", nsp ),
                                                           Types.GEOMETRY_PROPERTY_NAME, true );
                ft = FeatureFactory.createFeatureType( new QualifiedName( "error" ), false, pt );
            }
        } catch ( Exception e ) {
            BootLogger.logError( e.getMessage(), e );
        }
    }

    /**
     * 
     * @param appCont
     * @param sourceLayer
     * @param errorLocations
     */
    public AddErrorLayerCommand( ApplicationContainer<?> appCont, Layer sourceLayer,
                                 List<Pair<String, Point>> errorLocations ) {
        this.appCont = appCont;
        this.sourceLayer = sourceLayer;
        this.errorLocations = errorLocations;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {
        FeatureCollection resultFC = FeatureFactory.createFeatureCollection( "UUID_" + UUID.randomUUID().toString(),
                                                                             errorLocations.size() );
        PropertyType[] pt = ft.getProperties();
        for ( int i = 0; i < errorLocations.size(); i++ ) {
            FeatureProperty[] fp = new FeatureProperty[3];
            fp[0] = FeatureFactory.createFeatureProperty( pt[0].getName(), i );
            fp[1] = FeatureFactory.createFeatureProperty( pt[1].getName(), errorLocations.get( i ).first );
            fp[2] = FeatureFactory.createFeatureProperty( pt[2].getName(), errorLocations.get( i ).second );
            Feature feature = FeatureFactory.createFeature( "UUID_" + UUID.randomUUID().toString(), ft, fp );
            resultFC.add( feature );
        }

        Envelope env = resultFC.getBoundedBy();
        MemoryDatasourceType mdst = new MemoryDatasourceType();
        EnvelopeType et = new EnvelopeType();
        et.setMinx( env.getMin().getX() );
        et.setMiny( env.getMin().getY() );
        et.setMaxx( env.getMax().getX() );
        et.setMaxy( env.getMax().getY() );
        MapModel mm = appCont.getMapModel( null );
        et.setCrs( mm.getCoordinateSystem().getPrefixedName() );
        mdst.setExtent( et );
        mdst.setMinScaleDenominator( 0d );
        mdst.setMaxScaleDenominator( 100000000d );
        Datasource ds = new MemoryDatasource( mdst, null, null, resultFC );

        Identifier id = new Identifier( "error_" + sourceLayer.getTitle() );
        int i = 0;
        while ( mm.exists( id ) ) {
            id = new Identifier( "error_" + sourceLayer.getTitle() + "_" + i++ );
        }
        newLayer = new SystemLayer( mm, id, id.getValue(), id.getValue(), singletonList( ds ),
                                    Collections.<MetadataURL> emptyList() );

        UserStyle us = (UserStyle) StyleFactory.createPointStyle( "circle", Color.WHITE, Color.GREEN, 4, 0, 30, 0, 0,
                                                                  9E99 );
        // set mark fill to be 30% opaque
        PointSymbolizer ps = (PointSymbolizer) us.getFeatureTypeStyles()[0].getRules()[0].getSymbolizers()[0];
        ( (Mark) ps.getGraphic().getMarksAndExtGraphics()[0] ).getFill().setOpacity( 0.3 );

        DirectStyleType dst = new DirectStyleType();
        dst.setName( us.getName() );
        dst.setTitle( us.getTitle() );
        dst.setAbstract( us.getAbstract() );
        dst.setCurrent( true );

        List<NamedStyle> styles = new ArrayList<NamedStyle>();
        styles.add( new DirectStyle( dst, us, newLayer ) );
        newLayer.setStyles( styles );
        newLayer.setEditable( false );
        mm.insert( newLayer, sourceLayer.getParent(), null, true );
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
        return newLayer;
    }

}
