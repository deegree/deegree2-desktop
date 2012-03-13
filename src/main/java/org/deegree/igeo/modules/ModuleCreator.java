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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.sf.ehcache.Cache;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.HttpUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.graphics.sld.SLDFactory;
import org.deegree.graphics.sld.StyledLayerDescriptor;
import org.deegree.graphics.sld.UserStyle;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.config.AbstractDatasourceType;
import org.deegree.igeo.config.AuthenticationInformationType;
import org.deegree.igeo.config.DatabaseDatasourceType;
import org.deegree.igeo.config.DatasourceType;
import org.deegree.igeo.config.DirectStyleType;
import org.deegree.igeo.config.FileDatasourceType;
import org.deegree.igeo.config.JDBCConnection;
import org.deegree.igeo.config.LayerGroupType;
import org.deegree.igeo.config.LayerType;
import org.deegree.igeo.config.MapModelCollectionType;
import org.deegree.igeo.config.MapModelType;
import org.deegree.igeo.config.MemoryDatasourceType;
import org.deegree.igeo.config.ModuleGroupType;
import org.deegree.igeo.config.ModuleRegisterType;
import org.deegree.igeo.config.ModuleType;
import org.deegree.igeo.config.NamedStyleType;
import org.deegree.igeo.config.ParameterType;
import org.deegree.igeo.config.ReferencedStyleType;
import org.deegree.igeo.config.StyleType;
import org.deegree.igeo.config.Util;
import org.deegree.igeo.config.WCSDatasourceType;
import org.deegree.igeo.config.WFSDatasourceType;
import org.deegree.igeo.config.WMSDatasourceType;
import org.deegree.igeo.config._ComponentPositionType;
import org.deegree.igeo.dataadapter.jdbc.JdbcConnectionParameterCache;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.io.FileSystemAccess;
import org.deegree.igeo.io.FileSystemAccessFactory;
import org.deegree.igeo.io.LocalFSAccess;
import org.deegree.igeo.mapmodel.AuthenticationInformation;
import org.deegree.igeo.mapmodel.CertificateAI;
import org.deegree.igeo.mapmodel.DatabaseDatasource;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.DirectStyle;
import org.deegree.igeo.mapmodel.FileDatasource;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.LayerGroup;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MapModelCollection;
import org.deegree.igeo.mapmodel.MapModelException;
import org.deegree.igeo.mapmodel.MemoryDatasource;
import org.deegree.igeo.mapmodel.NamedStyle;
import org.deegree.igeo.mapmodel.RasterFileDatasource;
import org.deegree.igeo.mapmodel.ReferencedStyle;
import org.deegree.igeo.mapmodel.SessionIDAI;
import org.deegree.igeo.mapmodel.UserPasswordAI;
import org.deegree.igeo.mapmodel.VectorFileDatasource;
import org.deegree.igeo.mapmodel.WCSDatasource;
import org.deegree.igeo.mapmodel.WFSDatasource;
import org.deegree.igeo.mapmodel.WMSDatasource;
import org.deegree.igeo.utils.Encryption;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;
import org.deegree.kernel.ProcessMonitor;
import org.xml.sax.SAXException;

/**
 * Factory class for creating modules from module definition stored in a iGeodesktop project files
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class ModuleCreator<T> {

    private static final ILogger LOG = LoggerFactory.getLogger( ModuleCreator.class );

    private XMLFragment xml;

    private ApplicationContainer<T> appContainer;

    /**
     * 
     * @param appContainer
     * @param url
     * @throws IOException
     * @throws SAXException
     */
    public ModuleCreator( ApplicationContainer<T> appContainer, URL url ) throws IOException, SAXException {
        this.xml = new XMLFragment( url );
        this.appContainer = appContainer;
    }

    /**
     * 
     * @param url
     * @return absolute URL
     * @throws MalformedURLException
     */
    public URL resolve( String url )
                            throws MalformedURLException {
        return this.xml.resolve( url );
    }

    /**
     * creates a module from its definition in a iGeoDesktop configuration/project file
     * 
     * @param module
     * @param componentPosition
     * @param parent
     * @param processMonitor
     * @return initialized module
     */
    @SuppressWarnings("unchecked")
    public IModule<T> createModule( ModuleType module, _ComponentPositionType componentPosition, IModule<T> parent,
                                    ProcessMonitor processMonitor ) {

        if ( module instanceof ModuleGroupType ) {
            return createModuleGroup( (ModuleGroupType) module, componentPosition, parent, processMonitor );
        }

        LOG.logDebug( "Creating module " + module.getClassName().trim() + " with identifier "
                      + module.getIdentifier().getValue().trim() );

        IModule<T> mod = null;
        try {
            mod = (IModule<T>) Class.forName( module.getClassName().trim() ).newInstance();
        } catch ( Exception e ) {
            ModuleCreator.LOG.logError( e.getMessage(), e );
            throw new ModuleException( e.getMessage(), e );
        }

        // create init parameters of the module
        Map<String, String> initParams = new HashMap<String, String>();
        List<ParameterType> params = module.getInitParam();
        for ( ParameterType param : params ) {
            initParams.put( param.getName(), param.getValue() );
        }

        mod.init( module, componentPosition, appContainer, parent, initParams );
        return mod;
    }

    /**
     * creates a module group and all contained modules from its definition in a iGeoDesktop configuration/project file
     * 
     * @param moduleGroup
     * @param componentPosition
     * @return initialized module group
     */
    @SuppressWarnings("unchecked")
    public IModuleGroup<T> createModuleGroup( ModuleGroupType moduleGroup, _ComponentPositionType componentPosition,
                                              IModule<T> parent, ProcessMonitor processMonitor ) {

        IModuleGroup<T> modGrp = null;
        try {
            modGrp = (IModuleGroup<T>) Class.forName( moduleGroup.getClassName().trim() ).newInstance();
        } catch ( Exception e ) {
            ModuleCreator.LOG.logError( e.getMessage(), e );
            throw new ModuleException( e.getMessage(), e );
        }

        // create init parameters of the module
        Map<String, String> initParams = new HashMap<String, String>();
        List<ParameterType> params = moduleGroup.getInitParam();
        for ( ParameterType param : params ) {
            initParams.put( param.getName(), param.getValue() );
        }

        // initialize module group
        modGrp.init( moduleGroup, componentPosition, appContainer, parent, initParams );

        // create and set groups children
        List<ModuleRegisterType> moduleRegister = moduleGroup.getModuleRegister();
        IModule<T> module = null;
        for ( ModuleRegisterType mr : moduleRegister ) {
            _ComponentPositionType cmpPosition = mr.get_ComponentPosition().getValue();
            if ( mr.getModuleReference() != null ) {
                URL mLoc = null;
                try {
                    String href = mr.getModuleReference().getOnlineResource().getHref();
                    FileSystemAccessFactory fsaf = FileSystemAccessFactory.getInstance( appContainer );
                    FileSystemAccess fsa = fsaf.getFileSystemAccess( FILECHOOSERTYPE.module );
                    mLoc = fsa.getFileURL( href );
                    try {
                        String s = StringTools.replace( mLoc.toExternalForm(), " ", "%20", true );
                        HttpUtils.validateURL( s );
                    } catch ( Exception e ) {
                        String t = Messages.get( "$MD11199", mLoc );
                        String s = DialogFactory.openNewReferenceDialog( appContainer, t, mLoc.toString(), true );
                        if ( s != null ) {
                            mr.getModuleReference().getOnlineResource().setHref( s );
                        } else {
                            return null;
                        }
                    }

                } catch ( Exception e ) {
                    LOG.logError( e.getMessage(), e );
                    String msg = Messages.getMessage( Locale.getDefault(), "$DG10043",
                                                      mr.getModuleReference().getOnlineResource().getHref() );
                    throw new ModuleException( msg, e );
                }

                try {
                    JAXBContext jc = JAXBContext.newInstance( "org.deegree.igeo.config" );
                    Unmarshaller u = jc.createUnmarshaller();
                    ModuleType mt = ( (JAXBElement<? extends ModuleType>) u.unmarshal( mLoc ) ).getValue();
                    processMonitor.updateStatus( "loading module: " + mt.getName() );
                    module = createModule( mt, cmpPosition, modGrp, processMonitor );
                } catch ( JAXBException e ) {
                    LOG.logError( e.getMessage(), e );
                    throw new ModuleException( Messages.getMessage( Locale.getDefault(), "$DG10045", mLoc.getPath() ),
                                               e );
                }
            } else if ( mr.getModule().getValue() instanceof ModuleGroupType ) {
                processMonitor.updateStatus( "loading module: " + mr.getModule().getValue().getName() );
                module = createModuleGroup( (ModuleGroupType) mr.getModule().getValue(), cmpPosition, modGrp,
                                            processMonitor );
            } else {
                processMonitor.updateStatus( "loading module: " + mr.getModule().getValue().getName() );
                module = createModule( mr.getModule().getValue(), cmpPosition, modGrp, processMonitor );
            }
            modGrp.addChildModule( module );
        }

        return modGrp;

    }

    /**
     * 
     * @param collection
     * @param processMonitor
     * @return created {@link MapModelCollection}
     * @throws IOException
     * @throws URISyntaxException
     */
    public MapModelCollection createMapModelCollection( MapModelCollectionType collection, ProcessMonitor processMonitor )
                            throws IOException, URISyntaxException {

        List<MapModel> mapModels = convertMapModel( collection.getMapModel(), processMonitor );

        return new MapModelCollection( collection, mapModels );

    }

    /**
     * 
     * @param models
     * @param processMonitor
     * @return list of MapModel
     * @throws IOException
     * @throws IOException
     * @throws URISyntaxException
     */
    public List<MapModel> convertMapModel( List<MapModelType> models, ProcessMonitor processMonitor )
                            throws IOException, URISyntaxException {
        List<MapModel> mapModels = Collections.synchronizedList( new ArrayList<MapModel>() );
        for ( MapModelType mmType : models ) {

            MapModel mm = new MapModel( appContainer, mmType );
            List<LayerGroup> layerGroups = new ArrayList<LayerGroup>();
            List<LayerGroupType> lgTypes = mmType.getLayerGroup();
            for ( int i = 0; i < lgTypes.size(); i++ ) {
                if ( processMonitor != null ) {
                    processMonitor.updateStatus( "load layerGrp: " + lgTypes.get( i ).getTitle() );
                }
                layerGroups.add( convertLayers( mm, lgTypes.get( i ), null, processMonitor ) );
            }

            mm.setLayerGroups( layerGroups );

            mapModels.add( mm );

        }
        return mapModels;
    }

    private LayerGroup convertLayers( MapModel owner, LayerGroupType layerGroupType, LayerGroup parent,
                                      ProcessMonitor processMonitor )
                            throws IOException, URISyntaxException {

        LayerGroup layerGroup = new LayerGroup( owner, parent, layerGroupType );
        List<Object> list = layerGroupType.getLayerOrLayerReferenceOrLayerGroup();
        for ( int i = 0; i < list.size(); i++ ) {
            Object object = list.get( i );
            if ( object instanceof LayerType ) {
                LayerType layerType = (LayerType) object;
                processMonitor.updateStatus( "load layer: " + layerType.getTitle() );
                List<Datasource> ds = convertDatasources( layerType.getDatasource() );
                if ( ds.size() > 0 ) {
                    // just add layer if at least one data source is available.
                    // a layer with not data source may results from wrong reference
                    // to file or service
                    List<String> selectedFor = layerType.getSelectedFor();
                    Layer layer = new Layer( layerType, owner, ds, null );
                    List<NamedStyle> styles = convertStyles( layerType.getStyle(), layer );
                    if ( styles.size() == 0 ) {
                        NamedStyleType nst = new NamedStyleType();
                        nst.setCurrent( true );
                        nst.setName( "default" );
                        nst.setTitle( "default" );
                        nst.setAbstract( "default" );
                        styles.add( new NamedStyle( nst, layer ) );
                    }
                    layer.setStyles( styles );
                    for ( String s4 : selectedFor ) {
                        layer.addSelectedFor( s4 );
                    }
                    layer.setParent( layerGroup );
                    layerGroup.addLayer( layer );
                }
            } else if ( object instanceof LayerGroupType ) {
                processMonitor.updateStatus( "load layerGrp: " + ( (LayerGroupType) object ).getTitle() );
                // recursion
                LayerGroup lg = convertLayers( owner, (LayerGroupType) object, layerGroup, processMonitor );
                layerGroup.addLayerGroup( lg );
            } else {
                DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null,
                                               "layer reference is not supported yet", "unsupported reference" );
                throw new RuntimeException( "layer reference is not supported yet" );

            }
        }

        StringBuilder sb = new StringBuilder();
        for ( Layer layer : layerGroup.getLayers() ) {
            for ( String layerName : layer.getDataAccessExceptions().keySet() ) {
                sb.append( Messages.get( "$MD11860", layerName, layer.getDataAccessExceptions().get( layerName ) ) ).append( "\n\n" );
            }
        }
        if ( sb.length() > 0 ) {
            DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11861" ),
                                           sb.toString(), false );
        }
        return layerGroup;
    }

    private List<NamedStyle> convertStyles( List<StyleType> styles, Layer layer ) {

        List<NamedStyle> namedStyles = new ArrayList<NamedStyle>();
        for ( StyleType style : styles ) {
            NamedStyleType nst = style.getNamedStyle().getValue();
            NamedStyle namedStyle = null;
            if ( nst.getLegendURL() != null ) {
                String s = nst.getLegendURL().getOnlineResource().getHref();
                try {
                    nst.getLegendURL().getOnlineResource().setHref( xml.resolve( s ).toExternalForm() );
                } catch ( MalformedURLException e ) {
                    LOG.logError( e.getMessage(), e );
                }
            }
            if ( nst instanceof DirectStyleType ) {
                DirectStyleType dst = (DirectStyleType) nst;
                StyledLayerDescriptor sld = null;
                try {
                    sld = SLDFactory.createSLD( dst.getSld() );
                } catch ( XMLParsingException e ) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                namedStyle = new DirectStyle( dst, (UserStyle) sld.getNamedLayers()[0].getStyles()[0], layer );
            } else if ( nst instanceof ReferencedStyleType ) {
                ReferencedStyleType rst = (ReferencedStyleType) nst;
                try {
                    URL linkage = new URL( rst.getLinkage().getOnlineResource().getHref() );
                    StyledLayerDescriptor sld = SLDFactory.createSLD( linkage );

                    namedStyle = new ReferencedStyle( rst, (UserStyle) sld.getNamedLayers()[0].getStyles()[0], layer );
                } catch ( MalformedURLException e ) {
                    LOG.logError( e.getMessage(), e );
                    String msg = Messages.getMessage( Locale.getDefault(), "$DG10039",
                                                      rst.getLinkage().getOnlineResource().getHref() );
                    throw new ModuleException( msg );
                } catch ( XMLParsingException e ) {
                    LOG.logError( e.getMessage(), e );
                    String msg = Messages.getMessage( Locale.getDefault(), "$DG10040",
                                                      rst.getLegendURL().getOnlineResource().getHref() );
                    throw new ModuleException( msg );
                }
            } else {
                namedStyle = new NamedStyle( nst, layer );
            }
            namedStyles.add( namedStyle );
        }

        return namedStyles;
    }

    private AuthenticationInformation createAuthenticationInformation( AuthenticationInformationType ai )
                            throws IOException {

        if ( ai != null ) {
            if ( ai.getUserPasswordAI() != null ) {
                return new UserPasswordAI( ai.getUserPasswordAI().getUser(), ai.getUserPasswordAI().getPassword() );
            } else if ( ai.getSessionIDAI() != null ) {
                return new SessionIDAI();
            } else if ( ai.getCertificateAI() != null ) {
                return new CertificateAI( ai.getCertificateAI() );
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    /**
     * maps configuration datasources onto according core map model classes
     * 
     * 
     * @param datasourceChoice
     * @return
     * @throws IOException
     */
    private List<Datasource> convertDatasources( List<DatasourceType> ds )
                            throws IOException {

        List<Datasource> datasources = new ArrayList<Datasource>();
        if ( ds != null ) {
            for ( DatasourceType dsType : ds ) {
                AbstractDatasourceType type = dsType.getAbstractDatasource().getValue();
                Datasource dds = null;
                if ( type instanceof DatabaseDatasourceType ) {
                    dds = createDatabaseDatasource( (DatabaseDatasourceType) type );
                } else if ( type instanceof FileDatasourceType ) {
                    dds = createFileDatasource( (FileDatasourceType) type );
                } else if ( type instanceof MemoryDatasourceType ) {
                    dds = createMemoryDatasource( (MemoryDatasourceType) type );
                } else if ( type instanceof WCSDatasourceType ) {
                    dds = createWCSDatasource( (WCSDatasourceType) type );
                } else if ( type instanceof WFSDatasourceType ) {
                    dds = createWFSDatasource( (WFSDatasourceType) type );
                } else if ( type instanceof WMSDatasourceType ) {
                    dds = createWMSDatasource( (WMSDatasourceType) type );
                } else {
                    throw new MapModelException( Messages.getMessage( Locale.getDefault(), "$DG10004",
                                                                      type.getClass().getName() ) );
                }
                if ( dds != null ) {
                    datasources.add( dds );
                }
            }
        }

        return datasources;
    }

    /**
     * creates a deegree object for encapsulating a WFS datasource from map model configuration
     * 
     * @param dsci
     * @return database datasource object
     * @throws IOException
     */
    private WFSDatasource createWFSDatasource( WFSDatasourceType cnfWfsds )
                            throws IOException {
        WFSDatasource wfsds = null;
        Cache cache = createCache( cnfWfsds.getCache() );

        AuthenticationInformation authenticationInformation = createAuthenticationInformation( cnfWfsds.getAuthenticationInformation() );
        String url = cnfWfsds.getCapabilitiesURL().getOnlineResource().getHref();

        try {
            HttpUtils.validateURL( url );
        } catch ( Exception e ) {
            LOG.logError( e );
            String s = DialogFactory.openNewReferenceDialog( appContainer, Messages.get( "$MD11200", url ), url, true );
            if ( s != null ) {
                cnfWfsds.getCapabilitiesURL().getOnlineResource().setHref( s );
            } else {
                return null;
            }
        }

        try {
            wfsds = new WFSDatasource( cnfWfsds, authenticationInformation, cache );
            wfsds.setExtent( Util.convertEnvelope( cnfWfsds.getExtent() ) );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new ModuleException( Messages.getMessage( Locale.getDefault(), "$DG10008", url ) );
        }
        return wfsds;
    }

    /**
     * creates a deegree object for encapsulating a WCS datasource from map model configuration
     * 
     * @param dsci
     * @return database datasource object
     * @throws IOException
     */
    private WMSDatasource createWMSDatasource( WMSDatasourceType cnfWmsds )
                            throws IOException {
        WMSDatasource wmsds = null;
        Cache cache = createCache( cnfWmsds.getCache() );
        AuthenticationInformation authenticationInformation = createAuthenticationInformation( cnfWmsds.getAuthenticationInformation() );
        String url = cnfWmsds.getCapabilitiesURL().getOnlineResource().getHref();

        try {
            HttpUtils.validateURL( url );
        } catch ( Exception e ) {
            LOG.logError( e );
            String s = DialogFactory.openNewReferenceDialog( appContainer, Messages.get( "$MD11201", url ), url, true );
            if ( s != null ) {
                cnfWmsds.getCapabilitiesURL().getOnlineResource().setHref( s );
            } else {
                return null;
            }
        }
        wmsds = new WMSDatasource( cnfWmsds, authenticationInformation, cache );
        wmsds.setExtent( Util.convertEnvelope( cnfWmsds.getExtent() ) );

        return wmsds;
    }

    /**
     * creates a deegree object for encapsulating a WCS datasource from map model configuration
     * 
     * @param dsci
     * @return database datasource object
     * @throws IOException
     */
    private WCSDatasource createWCSDatasource( WCSDatasourceType cnfWcsds )
                            throws IOException {
        WCSDatasource wcsds = null;
        Cache cache = createCache( cnfWcsds.getCache() );
        AuthenticationInformation authenticationInformation = createAuthenticationInformation( cnfWcsds.getAuthenticationInformation() );
        String url = cnfWcsds.getCapabilitiesURL().getOnlineResource().getHref();
        try {
            HttpUtils.validateURL( url );
        } catch ( Exception e ) {
            LOG.logError( e );
            String s = DialogFactory.openNewReferenceDialog( appContainer, Messages.get( "$MD11201", url ), url, true );
            if ( s != null ) {
                cnfWcsds.getCapabilitiesURL().getOnlineResource().setHref( s );
            } else {
                return null;
            }
        }
        try {
            wcsds = new WCSDatasource( cnfWcsds, authenticationInformation, cache );
            wcsds.setExtent( Util.convertEnvelope( cnfWcsds.getExtent() ) );
        } catch ( MalformedURLException e ) {
            LOG.logError( e.getMessage(), e );
            throw new ModuleException( Messages.getMessage( Locale.getDefault(), "$DG10006", url ) );
        }
        return wcsds;
    }

    /**
     * creates a deegree object for encapsulating a memory datasource from map model configuration. Notice that a
     * MemoryDatastore does not contain any data when initialized from map model configuration!
     * 
     * @param dsci
     * @return database datasource object
     * @throws IOException
     */
    private MemoryDatasource createMemoryDatasource( MemoryDatasourceType cnfMds )
                            throws IOException {
        Cache cache = createCache( cnfMds.getCache() );
        AuthenticationInformation authenticationInformation = createAuthenticationInformation( cnfMds.getAuthenticationInformation() );
        return new MemoryDatasource( cnfMds, authenticationInformation, cache, null );
    }

    /**
     * creates a deegree object for encapsulating a file datasource from map model configuration
     * 
     * @param dsci
     * @return database datasource object
     */
    private FileDatasource createFileDatasource( FileDatasourceType cnfFds ) {
        FileDatasource fds = null;
        Cache cache = createCache( cnfFds.getCache() );

        File fl = new File( cnfFds.getFile().trim() );
        fl = getAbsoluteFilePath( fl );

        Class<FileSystemAccess> fsaCL = appContainer.getSettings().getFileAccessOptions().getFileSystemAccess( FILECHOOSERTYPE.geoDataFile );

        if ( !fl.exists() && fsaCL.equals( LocalFSAccess.class ) ) {
            String s = DialogFactory.openNewReferenceDialog( appContainer, Messages.get( "$MD11203", fl ),
                                                             cnfFds.getFile(), false );
            if ( s != null ) {
                cnfFds.setFile( s );
            } else {
                return null;
            }
        }

        if ( cnfFds.getFile().toLowerCase().endsWith( ".shp" ) || cnfFds.getFile().toLowerCase().endsWith( ".mif" )
             || cnfFds.getFile().toLowerCase().endsWith( ".xml" ) || cnfFds.getFile().toLowerCase().endsWith( ".gml" ) ) {
            fds = new VectorFileDatasource( cnfFds, null, cache );
        } else {
            fds = new RasterFileDatasource( cnfFds, null, cache );
        }
        fds.setExtent( Util.convertEnvelope( cnfFds.getExtent() ) );

        return fds;
    }

    private File getAbsoluteFilePath( File file ) {
        if ( !file.isAbsolute() ) {
            try {
                URL url = resolve( file.getPath() );
                file = new File( url.toExternalForm().substring( 5 ) );
            } catch ( MalformedURLException e ) {
                // should never happen
                LOG.logError( e );
            }
        }
        return file;
    }

    /**
     * creates a deegree object for encapsulating a database datasource from map model configuration
     * 
     * @param dsci
     * @return database datasource object
     */
    private DatabaseDatasource createDatabaseDatasource( DatabaseDatasourceType cnfDbds ) {
        DatabaseDatasource dbds = null;
        Cache cache = createCache( cnfDbds.getCache() );
        String password = cnfDbds.getConnection().getPassword();
        JDBCConnection jdbcConnection = JdbcConnectionParameterCache.getInstance().getJdbcConnectionParameter( cnfDbds.getConnection().getDriver(),
                                                                                                               cnfDbds.getConnection().getUrl(),
                                                                                                               cnfDbds.getConnection().getUser(),
                                                                                                               password != null ? Encryption.decrypt( password )
                                                                                                                               : null );
        dbds = new DatabaseDatasource( cnfDbds, null, cache, jdbcConnection );
        dbds.setExtent( Util.convertEnvelope( cnfDbds.getExtent() ) );
        return dbds;
    }

    private Cache createCache( String cache ) {
        // TODO Auto-generated method stub
        return null;
    }
}
