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

package org.deegree.desktop.config;

import java.awt.Component;
import java.awt.Container;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

import javax.swing.JInternalFrame;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.modules.DefaultMapModule;
import org.deegree.desktop.modules.IModule;
import org.deegree.desktop.config.AbsolutePositionType;
import org.deegree.desktop.config.ComponentStateType;
import org.deegree.desktop.config.EnvelopeType;
import org.deegree.desktop.config.FrameViewFormType;
import org.deegree.desktop.config.IdentifierType;
import org.deegree.desktop.config.InnerFrameViewFormType;
import org.deegree.desktop.config.LayerGroupType;
import org.deegree.desktop.config.MapModelType;
import org.deegree.desktop.config.MenuType;
import org.deegree.desktop.config.ModuleRegisterType;
import org.deegree.desktop.config.ModuleType;
import org.deegree.desktop.config.ObjectFactory;
import org.deegree.desktop.config.ParameterType;
import org.deegree.desktop.config.PopUpEntryType;
import org.deegree.desktop.config.QualifiedNameType;
import org.deegree.desktop.config.SupportedCRSType;
import org.deegree.desktop.config.TargetDeviceType;
import org.deegree.desktop.config.ToolbarEntryType;
import org.deegree.desktop.config.ViewFormType;
import org.deegree.desktop.config.WindowStateType;
import org.deegree.desktop.config.WindowType;
import org.deegree.desktop.config._AbstractViewFormType;
import org.deegree.desktop.config._AbstractViewFormType.ContainerClass;
import org.deegree.model.Identifier;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class Util {

    /**
     * 
     * @param envelope
     * @return configuration envelope
     */
    public static EnvelopeType convertEnvelope( Envelope envelope ) {
        EnvelopeType value = new EnvelopeType();
        if ( envelope.getCoordinateSystem() != null ) {
            value.setCrs( envelope.getCoordinateSystem().getPrefixedName() );
        }
        value.setMinx( envelope.getMin().getX() );
        value.setMiny( envelope.getMin().getY() );
        value.setMaxx( envelope.getMax().getX() );
        value.setMaxy( envelope.getMax().getY() );
        return value;
    }

    /**
     * 
     * @param envelope
     * @return
     */
    public static Envelope convertEnvelope( EnvelopeType envelope ) {
        CoordinateSystem cs = null;
        try {
            cs = CRSFactory.create( envelope.getCrs() );
        } catch ( UnknownCRSException e ) {
            // fatal exception should never happen
            e.printStackTrace();
            throw new RuntimeException( e );
        }
        return GeometryFactory.createEnvelope( envelope.getMinx(), envelope.getMiny(), envelope.getMaxx(),
                                               envelope.getMaxy(), cs );

    }

    /**
     * 
     * @param qname
     * @return configuration {@link QualifiedNameType}
     */
    public static QualifiedNameType convertQualifiedName( QualifiedName qname ) {
        QualifiedNameType qnt = new QualifiedNameType();
        qnt.setLocalName( qname.getLocalName() );
        if ( qname.getNamespace() != null ) {
            qnt.setNamespace( qname.getNamespace().toASCIIString() );
        }
        return qnt;
    }

    /**
     * 
     * @param qname
     *            configuration {@link QualifiedNameType}
     * @return deegree {@link QualifiedName}
     */
    public static QualifiedName convertQualifiedName( QualifiedNameType qname ) {
        URI nspace = null;
        if ( qname.getNamespace() != null ) {
            try {
                nspace = new URI( qname.getNamespace() );
            } catch ( URISyntaxException e ) {
                throw new RuntimeException( e );
            }
        }

        return new QualifiedName( qname.getLocalName(), nspace );

    }

    /**
     * 
     * @param id
     *            configuration {@link IdentifierType}
     * @return model {@link Identifier}
     */
    public static Identifier convertIdentifier( IdentifierType id ) {
        URI nspace = null;
        if ( id.namespace != null ) {
            try {
                nspace = new URI( id.namespace );
            } catch ( URISyntaxException e ) {
                // fatal exception that never should happen
                e.printStackTrace();
            }
        }
        return new Identifier( id.value, nspace );
    }

    /**
     * 
     * @param name
     *            map model name
     * @param env
     *            map model envelope
     * @return a new {@link MapModelType}
     */
    public static MapModelType createMapModel( String name, Envelope env ) {
        MapModelType mmt = new MapModelType();
        mmt.setCurrent( true );
        mmt.setSupportedCRS( new SupportedCRSType() );
        IdentifierType id = new IdentifierType();
        id.setValue( UUID.randomUUID().toString() );
        mmt.setIdentifier( id );
        mmt.setName( name );
        TargetDeviceType tdt = new TargetDeviceType();
        tdt.setDpi( 96 );
        tdt.setPixelHeight( 400 );
        tdt.setPixelWidth( 500 );
        mmt.setTargetDevice( tdt );
        EnvelopeType et = new EnvelopeType();
        et.setMinx( env.getMin().getX() );
        et.setMiny( env.getMin().getY() );
        et.setMaxx( env.getMax().getX() );
        et.setMaxy( env.getMax().getY() );
        et.setCrs( env.getCoordinateSystem().getPrefixedName() );
        mmt.setExtent( et );
        et = new EnvelopeType();
        et.setMinx( env.getMin().getX() );
        et.setMiny( env.getMin().getY() );
        et.setMaxx( env.getMax().getX() );
        et.setMaxy( env.getMax().getY() );
        et.setCrs( env.getCoordinateSystem().getPrefixedName() );
        mmt.setMaxExtent( et );
        LayerGroupType lgt = new LayerGroupType();
        lgt.setAbstract( "new layer group" );
        lgt.setQueryable( true );
        lgt.setSupportToolTips( false );
        lgt.setTitle( "root" );
        lgt.setVisible( true );
        id = new IdentifierType();
        id.setValue( UUID.randomUUID().toString() );
        lgt.setIdentifier( id );
        mmt.getLayerGroup().add( lgt );
        return mmt;
    }

    /**
     * 
     * @param assignedMapModel
     * @return {@link ModuleRegisterType} containing a new {@link MapModelType}
     * @throws Exception
     */
    public static synchronized ModuleRegisterType createMapModule( ApplicationContainer<Container> igeo, String assignedMapModel )
                            throws Exception {

        List<IModule<Container>> tmp = igeo.findModuleByName( "MapModule" );
        DefaultMapModule<Container> template = null;
        if ( tmp.size() > 0 ) {
            template = (DefaultMapModule<Container>) tmp.get( 0 );
        }
        // base module settings
        ModuleType mt = new ModuleType();
        mt.setClassName( DefaultMapModule.class.getName() );
        IdentifierType id = new IdentifierType();
        id.setValue( UUID.randomUUID().toString() );
        mt.setIdentifier( id );
        mt.setName( "MapModule" );
        mt.setVisible( true );
        List<ParameterType> pmtList = mt.getInitParam();
        ParameterType pmt = new ParameterType();
        // assign new map model to map module
        pmt.setName( "assignedMapModel" );
        pmt.setValue( assignedMapModel );
        pmtList.add( pmt );

        // modules view form (use inner frame as default)
        ViewFormType viewForm = new ViewFormType();
        ComponentStateType cmpSt = new ComponentStateType();
        cmpSt.setActive( true );
        cmpSt.setModal( false );
        if ( template != null ) {
            cmpSt.setOrder( (int) ( template.getComponentStateAdapter().getOrder() + 10 ) );
        } else {
            cmpSt.setOrder( 0 );
        }
        cmpSt.setWindowState( WindowStateType.NORMAL );

        ContainerClass cc = new _AbstractViewFormType.ContainerClass();
        cc.setViewPlatform( "Application" );
        cc.setValue( "org.deegree.igeo.views.swing.map.DefaultMapInnerFrame" );

        ObjectFactory of = new ObjectFactory();
        _AbstractViewFormType avft = null;

        if ( template == null || template.getViewForm() instanceof JInternalFrame ) {
            avft = new InnerFrameViewFormType();
            ( (InnerFrameViewFormType) avft ).setFrameTitle( "New Map Module" );
            ( (InnerFrameViewFormType) avft ).setResizeable( true );
            viewForm.set_AbstractViewForm( of.createInnerFrameViewForm( (InnerFrameViewFormType) avft ) );
        } else {
            avft = new FrameViewFormType();
            ( (FrameViewFormType) avft ).setFrameTitle( "New Map Module" );
            ( (FrameViewFormType) avft ).setResizeable( true );
            viewForm.set_AbstractViewForm( of.createFrameViewForm( (FrameViewFormType) avft ) );
        }
        avft.setComponentState( cmpSt );
        avft.setUseHorizontalScrollBar( false );
        avft.setUseVerticalScrollBar( false );
        avft.getContainerClass().add( cc );

        mt.setViewForm( viewForm );

        // toolbar entries
        if ( template != null ) {
            List<ToolbarEntryType> list = mt.getToolBarEntry();
            List<ToolbarEntryType> tList = template.getToolBarEntries();
            for ( ToolbarEntryType tbEntry : tList ) {
                list.add( tbEntry );
            }
        }

        // menu entries
        if ( template != null ) {
            List<MenuType> list = mt.getMenu();
            List<MenuType> tList = template.getMenus();
            for ( MenuType menuType : tList ) {
                list.add( menuType );
            }
        }

        // popup entries
        if ( template != null ) {
            List<PopUpEntryType> list = mt.getPopUpEntry();
            List<PopUpEntryType> tList = template.getPopUpEntries();
            for ( PopUpEntryType popUpEntryType : tList ) {
                list.add( popUpEntryType );
            }
        }

        ModuleRegisterType mrt = new ModuleRegisterType();
        AbsolutePositionType apt = of.createAbsolutePositionType();
        WindowType wt = new WindowType();
        if ( template != null && template.getViewForm() instanceof Component ) {
            Component ifr = (Component) template.getViewForm();
            wt.setHeight( ifr.getHeight() );
            wt.setWidth( ifr.getWidth() );
            wt.setLeft( ifr.getLocation().x + 50 );
            wt.setTop( ifr.getLocation().y + 50 );
        } else {
            wt.setHeight( igeo.getMainWndow().getHeight() );
            wt.setWidth( igeo.getMainWndow().getWidth() );
            wt.setLeft( igeo.getMainWndow().getLocation().x + 50 );
            wt.setTop( igeo.getMainWndow().getLocation().y + 50 );
        }
        apt.setWindow( wt );
        mrt.set_ComponentPosition( of.create_ComponentPosition( apt ) );
        mrt.setModule( of.createModule( mt ) );
        return mrt;
    }

}
