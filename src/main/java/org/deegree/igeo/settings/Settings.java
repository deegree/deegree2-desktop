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

package org.deegree.igeo.settings;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.utils.Dictionary;
import org.deegree.framework.utils.DictionaryCollection;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.graphics.sld.AbstractStyle;
import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.StyleFactory;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.config.AuthenticationType;
import org.deegree.igeo.config.ClipboardType;
import org.deegree.igeo.config.ColorSchemesType;
import org.deegree.igeo.config.DashArrayDefinitionsType;
import org.deegree.igeo.config.DataAdapterType;
import org.deegree.igeo.config.DictionariesType;
import org.deegree.igeo.config.DigitizingOptionsType;
import org.deegree.igeo.config.ExternalReferencesType;
import org.deegree.igeo.config.FileAccessType;
import org.deegree.igeo.config.FileFilterType;
import org.deegree.igeo.config.FormatsType;
import org.deegree.igeo.config.GeometryMetricsType;
import org.deegree.igeo.config.GeometryTopologyType;
import org.deegree.igeo.config.GeometryTypeType;
import org.deegree.igeo.config.GraphicDefinitionsType;
import org.deegree.igeo.config.GraphicLineSizeType;
import org.deegree.igeo.config.GraphicsType;
import org.deegree.igeo.config.HelpContextType;
import org.deegree.igeo.config.MapHandlingType;
import org.deegree.igeo.config.OnlineResourceType;
import org.deegree.igeo.config.PresetType;
import org.deegree.igeo.config.ProjectTemplatesType;
import org.deegree.igeo.config.SecurityType;
import org.deegree.igeo.config.ServiceAdapterType;
import org.deegree.igeo.config.SettingsType;
import org.deegree.igeo.config.SnapLayerType;
import org.deegree.igeo.config.SnappingType;
import org.deegree.igeo.config.ValidationType;
import org.deegree.igeo.config.VerticesType;
import org.deegree.igeo.config.WFSDefaultStyleType;
import org.deegree.igeo.config.WFSFeatureAdapterType;
import org.deegree.igeo.config.WMSGridCoverageAdapterType;
import org.deegree.igeo.config.DashArrayDefinitionsType.DashArray;
import org.deegree.igeo.config.DataAdapterType.KnownRasterFormats;
import org.deegree.igeo.config.DatabaseDriversType.Database;
import org.deegree.igeo.config.ExternalReferencesType.Reference;
import org.deegree.igeo.config.FileAccessType.Access;
import org.deegree.igeo.config.FileFilterType.Format;
import org.deegree.igeo.config.GraphicDefinitionsType.Graphic;
import org.deegree.igeo.config.HelpContextType.Page;
import org.deegree.igeo.config.MapHandlingType.PanLevel;
import org.deegree.igeo.config.MapHandlingType.ZoomLevel;
import org.deegree.igeo.config.ProjectTemplatesType.Template;
import org.deegree.igeo.config.ServiceAdapterType.CapabilitiesEvaluator;
import org.deegree.igeo.config.VerticesType.SearchRadius;
import org.deegree.igeo.config.WFSFeatureAdapterType.DataLoader;
import org.deegree.igeo.dataadapter.wfs.WFS110CapabilitiesEvaluator;
import org.deegree.igeo.dataadapter.wms.WMS111CapabilitiesEvaluator;
import org.deegree.igeo.dataadapter.wms.WMS130CapabilitiesEvaluator;

/**
 * Manages settings for a project. If no settings are defined with a project they will be read from the general users
 * setting; if there are not settings defined either they will be read from general setting made by an administrator
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:poth@lat-lon.de">Your Name</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class Settings {

    private static final ILogger LOG = LoggerFactory.getLogger( Settings.class );

    private ApplicationContainer<?> appCont;

    private SettingsType adminSettings;

    private SettingsType userSettings;

    private SettingsType projectSettings;

    private GraphicOptions graphicOptions;

    private DigitizingLinesOpt digitizingLinesOpt;

    /**
     * constructs a new Settings Instance
     * 
     * @param appCont
     * @param adminSettings
     * @param userSettings
     * @param projectSettings
     */
    public Settings( ApplicationContainer<?> appCont, SettingsType adminSettings, SettingsType userSettings,
                     SettingsType projectSettings ) {
        this.adminSettings = adminSettings;
        this.projectSettings = projectSettings;
        this.userSettings = userSettings;
        this.appCont = appCont;
    }

    /**
     * 
     * @return language of a project
     */
    public String getLanguage() {
        if ( projectSettings != null && projectSettings.getLanguage() != null ) {
            return projectSettings.getLanguage().getValue();
        }
        if ( userSettings != null && userSettings.getLanguage() != null ) {
            return userSettings.getLanguage().getValue();
        }
        if ( adminSettings != null && adminSettings.getLanguage() != null ) {
            return adminSettings.getLanguage().getValue();
        }
        return Locale.getDefault().getLanguage();
    }

    /**
     * 
     * @return <code>true</code> if language can be changed
     */
    public boolean languageIsChangeable() {
        return ( adminSettings == null || adminSettings.getLanguage().isChangeable() )
               && ( userSettings == null || userSettings.getLanguage().isChangeable() );
    }

    /**
     * sets a new language for a project if it is allowed. Otherwise nothing happens
     * 
     * @param language
     */
    public void setLanguage( String language ) {
        if ( ( adminSettings == null || adminSettings.getLanguage().isChangeable() )
             && ( userSettings == null || userSettings.getLanguage().isChangeable() ) ) {
            if ( projectSettings.getLanguage() == null ) {
                projectSettings.setLanguage( new SettingsType.Language() );
            }
            projectSettings.getLanguage().setValue( language );
        }
    }

    /**
     * 
     * @return zoom level for a project
     */
    public float getZoomLevel() {
        if ( adminSettings != null && adminSettings.getMapHandling().getZoomLevel() != null ) {
            return adminSettings.getMapHandling().getZoomLevel().getVal();
        }
        boolean changeable = adminSettings == null || adminSettings.getMapHandling().isChangeable();
        if ( changeable && userSettings != null && userSettings.getMapHandling().getZoomLevel() != null ) {
            return userSettings.getMapHandling().getZoomLevel().getVal();
        }
        changeable = changeable && ( userSettings == null || userSettings.getMapHandling().isChangeable() );
        if ( projectSettings.getMapHandling().getZoomLevel() != null ) {
            return projectSettings.getMapHandling().getZoomLevel().getVal();
        }
        MapHandlingType mht = new MapHandlingType();
        mht.setZoomLevel( new MapHandlingType.ZoomLevel() );
        projectSettings.setMapHandling( mht );
        return mht.getZoomLevel().getVal();
    }

    /**
     * sets zoom level for a project if user is allowed to do
     * 
     * @param zoomLevel
     */
    public void setZoomLevel( float zoomLevel ) {
        if ( ( adminSettings == null || adminSettings.getMapHandling().isChangeable() )
             && ( userSettings == null || userSettings.getMapHandling().isChangeable() ) ) {
            if ( projectSettings.getMapHandling() == null ) {
                MapHandlingType mht = new MapHandlingType();
                projectSettings.setMapHandling( mht );
            }
            ZoomLevel zl = new MapHandlingType.ZoomLevel();
            zl.setVal( zoomLevel );
            projectSettings.getMapHandling().setZoomLevel( zl );
        }
    }

    /**
     * 
     * @return zoom level for a project
     */
    public float getPanLevel() {
        if ( projectSettings.getMapHandling().getPanLevel() != null ) {
            return projectSettings.getMapHandling().getPanLevel().getVal();
        }
        if ( userSettings != null && userSettings.getMapHandling().getPanLevel() != null ) {
            return userSettings.getMapHandling().getPanLevel().getVal();
        }
        if ( adminSettings != null && adminSettings.getMapHandling().getPanLevel() != null ) {
            return adminSettings.getMapHandling().getPanLevel().getVal();
        }
        MapHandlingType mht = new MapHandlingType();
        mht.setPanLevel( new MapHandlingType.PanLevel() );
        projectSettings.setMapHandling( mht );
        return mht.getPanLevel().getVal();
    }

    /**
     * sets pan level for a project if user is allowed to do
     * 
     * @param panLevel
     */
    public void setPanLevel( float panLevel ) {
        if ( ( adminSettings == null || adminSettings.getMapHandling().isChangeable() )
             && ( userSettings == null || userSettings.getMapHandling().isChangeable() ) ) {
            if ( projectSettings.getMapHandling() == null ) {
                MapHandlingType mht = new MapHandlingType();
                projectSettings.setMapHandling( mht );
            }
            PanLevel pl = new MapHandlingType.PanLevel();
            pl.setVal( panLevel );
            projectSettings.getMapHandling().setPanLevel( pl );
        }
    }

    /**
     * 
     * @return available database driver classes
     */
    public Map<String, String> getDatabaseDrivers() {
        Map<String, String> dbs = new HashMap<String, String>();
        if ( adminSettings != null && adminSettings.getDatabaseDrivers() != null ) {
            List<Database> dbList = adminSettings.getDatabaseDrivers().getDatabase();
            for ( Database database : dbList ) {
                dbs.put( database.getName(), database.getValue() );
            }
        }
        if ( userSettings != null && userSettings.getDatabaseDrivers() != null ) {
            List<Database> dbList = userSettings.getDatabaseDrivers().getDatabase();
            for ( Database database : dbList ) {
                dbs.put( database.getName(), database.getValue() );
            }
        }
        if ( projectSettings.getDatabaseDrivers() != null ) {
            List<Database> dbList = projectSettings.getDatabaseDrivers().getDatabase();
            for ( Database database : dbList ) {
                dbs.put( database.getName(), database.getValue() );
            }
        }
        return dbs;
    }

    /**
     * 
     * @return list of help pages directly assigned to iGeoDesktop
     */
    public List<HelpPage> getHelp() {
        Map<String, HelpPage> tmp = new HashMap<String, HelpPage>();
        if ( adminSettings != null && adminSettings.getHelpPage() != null ) {
            List<HelpContextType> list = adminSettings.getHelpPage().getHelpContext();
            for ( HelpContextType hct : list ) {
                Page page = hct.getPage();
                HelpPage hp = new HelpPage( page.getOnlineResource().getHref(), page.getKeyword(), page.isMainPage(),
                                            page.getLanguage() );
                tmp.put( hp.getOnlineResource(), hp );
            }
        }
        if ( userSettings != null && userSettings.getHelpPage() != null ) {
            List<HelpContextType> list = userSettings.getHelpPage().getHelpContext();
            for ( HelpContextType hct : list ) {
                Page page = hct.getPage();
                HelpPage hp = new HelpPage( page.getOnlineResource().getHref(), page.getKeyword(), page.isMainPage(),
                                            page.getLanguage() );
                tmp.put( hp.getOnlineResource(), hp );
            }
        }
        if ( projectSettings.getHelpPage() != null ) {
            List<HelpContextType> list = projectSettings.getHelpPage().getHelpContext();
            for ( HelpContextType hct : list ) {
                Page page = hct.getPage();
                HelpPage hp = new HelpPage( page.getOnlineResource().getHref(), page.getKeyword(), page.isMainPage(),
                                            page.getLanguage() );
                tmp.put( hp.getOnlineResource(), hp );
            }
        }
        return new ArrayList<HelpPage>( tmp.values() );
    }

    /**
     * 
     * @return digitizing option for a project
     */
    public DigitizingVerticesOpt getDigitizingVerticesOptions() {
        VerticesType vt = new VerticesType();

        if ( adminSettings != null && adminSettings.getDigitizingOptions() != null
             && adminSettings.getDigitizingOptions().getVertices() != null ) {
            VerticesType tmp = adminSettings.getDigitizingOptions().getVertices();
            vt.setHandleNearest( tmp.isHandleNearest() );
            vt.setUseNearest( tmp.isUseNearest() );
            SearchRadius sr = new VerticesType.SearchRadius();
            sr.setUom( tmp.getSearchRadius().getUom() );
            sr.setVal( tmp.getSearchRadius().getVal() );
            vt.setSearchRadius( sr );
        }
        // admin settings can be changed if a) available and b) declared as changeable
        boolean changeable = adminSettings == null || adminSettings.getDigitizingOptions() == null
                             || adminSettings.getDigitizingOptions().isChangeable();
        if ( changeable && userSettings != null && userSettings.getDigitizingOptions() != null
             && userSettings.getDigitizingOptions().getVertices() != null ) {
            VerticesType tmp = userSettings.getDigitizingOptions().getVertices();
            vt.setHandleNearest( tmp.isHandleNearest() );
            vt.setUseNearest( tmp.isUseNearest() );
            SearchRadius sr = new VerticesType.SearchRadius();
            sr.setUom( tmp.getSearchRadius().getUom() );
            sr.setVal( tmp.getSearchRadius().getVal() );
            vt.setSearchRadius( sr );
        }

        // user settings can be changed if a) admin settings are changeable b) available and
        // c) declared as changeable
        changeable = changeable
                     && ( userSettings == null || userSettings.getDigitizingOptions() == null || userSettings.getDigitizingOptions().isChangeable() );
        if ( changeable && projectSettings.getDigitizingOptions() != null
             && projectSettings.getDigitizingOptions().getVertices() != null ) {
            VerticesType tmp = projectSettings.getDigitizingOptions().getVertices();
            vt.setHandleNearest( tmp.isHandleNearest() );
            vt.setUseNearest( tmp.isUseNearest() );
            SearchRadius sr = new VerticesType.SearchRadius();
            sr.setUom( tmp.getSearchRadius().getUom() );
            sr.setVal( tmp.getSearchRadius().getVal() );
            vt.setSearchRadius( sr );
        } else if ( projectSettings.getDigitizingOptions() == null ) {
            projectSettings.setDigitizingOptions( new DigitizingOptionsType() );
            projectSettings.getDigitizingOptions().setChangeable( changeable );
        }
        projectSettings.getDigitizingOptions().setVertices( vt );
        return new DigitizingVerticesOpt( vt, changeable );
    }

    /**
     * 
     * @return digitizing option for a project
     */
    public DigitizingLinesOpt getDigitizingLinesOptions() {
        if ( digitizingLinesOpt == null ) {
            GraphicLineSizeType gt = new GraphicLineSizeType();

            if ( adminSettings != null && adminSettings.getDigitizingOptions() != null
                 && adminSettings.getDigitizingOptions().getGraphicLineSize() != null ) {
                GraphicLineSizeType tmp = adminSettings.getDigitizingOptions().getGraphicLineSize();
                gt.setUom( tmp.getUom() );
                gt.setVal( tmp.getVal() );
            }

            // admin settings can be changed if a) available and b) declared as changeable
            boolean changeable = adminSettings == null || adminSettings.getDigitizingOptions() == null
                                 || adminSettings.getDigitizingOptions().isChangeable();
            if ( changeable && userSettings != null && userSettings.getDigitizingOptions() != null
                 && userSettings.getDigitizingOptions().getGraphicLineSize() != null ) {
                GraphicLineSizeType tmp = userSettings.getDigitizingOptions().getGraphicLineSize();
                gt.setUom( tmp.getUom() );
                gt.setVal( tmp.getVal() );
            }

            // user settings can be changed if a) admin settings are changeable b) available and
            // c) declared as changeable
            changeable = changeable
                         && ( userSettings == null || userSettings.getDigitizingOptions() == null || userSettings.getDigitizingOptions().isChangeable() );
            if ( changeable && projectSettings.getDigitizingOptions() != null
                 && projectSettings.getDigitizingOptions().getGraphicLineSize() != null ) {
                GraphicLineSizeType tmp = projectSettings.getDigitizingOptions().getGraphicLineSize();
                gt.setUom( tmp.getUom() );
                gt.setVal( tmp.getVal() );
            } else if ( projectSettings.getDigitizingOptions() == null ) {
                projectSettings.setDigitizingOptions( new DigitizingOptionsType() );
                projectSettings.getDigitizingOptions().setChangeable( changeable );
            }
            projectSettings.getDigitizingOptions().setGraphicLineSize( gt );
            digitizingLinesOpt = new DigitizingLinesOpt( gt, changeable );
        }
        return digitizingLinesOpt;
    }

    /**
     * 
     * @return description of classes to be used for accessing WFS
     */
    public WFSFeatureAdapterSettings getWFSFeatureAdapter() {
        WFSFeatureAdapterType wfsAda = new WFSFeatureAdapterType();
        if ( adminSettings != null && adminSettings.getDataAdapter() != null
             && adminSettings.getDataAdapter().getWFSFeatureAdapter() != null ) {
            WFSFeatureAdapterType tmp = adminSettings.getDataAdapter().getWFSFeatureAdapter();
            wfsAda.getDataLoader().addAll( tmp.getDataLoader() );
            wfsAda.getCapabilitiesEvaluator().addAll( tmp.getCapabilitiesEvaluator() );
            wfsAda.setTimeout( tmp.getTimeout() );
            wfsAda.setMaxFeature( tmp.getMaxFeature() );
        } else {
            wfsAda.getDataLoader().add( new WFSFeatureAdapterType.DataLoader() );
            wfsAda.getCapabilitiesEvaluator().add( new WFSFeatureAdapterType.CapabilitiesEvaluator() );
            wfsAda.setTimeout( new ServiceAdapterType.Timeout() );
            wfsAda.setMaxFeature( new WFSFeatureAdapterType.MaxFeature() );
        }

        // admin settings can be changed if a) available and b) declared as changeable
        boolean changeable = adminSettings == null || adminSettings.getDataAdapter() == null
                             || adminSettings.getDataAdapter().getWFSFeatureAdapter() == null
                             || adminSettings.getDataAdapter().isChangeable();
        if ( changeable && userSettings != null && userSettings.getDataAdapter() != null
             && userSettings.getDataAdapter().getWFSFeatureAdapter() != null ) {
            WFSFeatureAdapterType tmp = userSettings.getDataAdapter().getWFSFeatureAdapter();
            if ( tmp != null ) {
                wfsAda.setTimeout( tmp.getTimeout() );
                wfsAda.setMaxFeature( tmp.getMaxFeature() );
                List<CapabilitiesEvaluator> list = tmp.getCapabilitiesEvaluator();
                for ( CapabilitiesEvaluator capabilitiesEvaluator : list ) {
                    if ( !isCapabilitiesEvaluatorAvailable( wfsAda, capabilitiesEvaluator.getVersion() ) ) {
                        wfsAda.getCapabilitiesEvaluator().add( capabilitiesEvaluator );
                    }
                }
                List<DataLoader> dlList = tmp.getDataLoader();
                for ( DataLoader dataLoader : dlList ) {
                    if ( !isDataLoaderAvailable( wfsAda, dataLoader.getVersion() ) ) {
                        wfsAda.getDataLoader().add( dataLoader );
                    }
                }
            }

        }

        // user settings can be changed if a) admin settings are changeable b) available and
        // c) declared as changeable
        changeable = changeable
                     && ( userSettings == null || userSettings.getDataAdapter() == null || userSettings.getDataAdapter().isChangeable() );
        if ( changeable && projectSettings.getDataAdapter() != null
             && projectSettings.getDataAdapter().getWFSFeatureAdapter() != null ) {
            WFSFeatureAdapterType tmp = projectSettings.getDataAdapter().getWFSFeatureAdapter();
            if ( tmp != null ) {
                wfsAda.setTimeout( tmp.getTimeout() );
                wfsAda.setMaxFeature( tmp.getMaxFeature() );
                List<CapabilitiesEvaluator> list = tmp.getCapabilitiesEvaluator();
                for ( CapabilitiesEvaluator capabilitiesEvaluator : list ) {
                    if ( !isCapabilitiesEvaluatorAvailable( wfsAda, capabilitiesEvaluator.getVersion() ) ) {
                        wfsAda.getCapabilitiesEvaluator().add( capabilitiesEvaluator );
                    }
                }
                List<DataLoader> dlList = tmp.getDataLoader();
                for ( DataLoader dataLoader : dlList ) {
                    if ( !isDataLoaderAvailable( wfsAda, dataLoader.getVersion() ) ) {
                        wfsAda.getDataLoader().add( dataLoader );
                    }
                }
            }

        } else if ( projectSettings.getDataAdapter() == null ) {
            DataAdapterType ada = new DataAdapterType();
            ada.setChangeable( changeable );
            projectSettings.setDataAdapter( ada );
        }

        CapabilitiesEvaluator ce = new CapabilitiesEvaluator();
        ce.setChangeable( false );
        ce.setVersion( "1.1.0" );
        ce.setVal( WFS110CapabilitiesEvaluator.class.getName() );
        wfsAda.getCapabilitiesEvaluator().add( ce );

        projectSettings.getDataAdapter().setWFSFeatureAdapter( wfsAda );
        return new WFSFeatureAdapterSettings( wfsAda, false );
    }

    /**
     * 
     * @param wfsAda
     * @param version
     * @return <code>true</code> if a {@link DataLoader} for passed version is already available
     */
    private boolean isDataLoaderAvailable( WFSFeatureAdapterType wfsAda, String version ) {
        List<DataLoader> list = wfsAda.getDataLoader();
        for ( DataLoader dataloader : list ) {
            if ( dataloader.getVersion() == null && version == null ) {
                return true;
            }
            if ( dataloader.getVersion() != null && version == null ) {
                return false;
            }
            if ( dataloader.getVersion() == null && version != null ) {
                return false;
            }
            return dataloader.getVersion().equals( version );
        }
        return false;
    }

    /**
     * 
     * @param serviceAda
     * @param version
     * @return <code>true</code> if a {@link CapabilitiesEvaluator} for passed version is already available
     */
    private boolean isCapabilitiesEvaluatorAvailable( ServiceAdapterType serviceAda, String version ) {
        List<CapabilitiesEvaluator> list = serviceAda.getCapabilitiesEvaluator();
        for ( CapabilitiesEvaluator capabilitiesEvaluator : list ) {
            if ( capabilitiesEvaluator.getVersion() == null && version == null ) {
                return true;
            }
            if ( capabilitiesEvaluator.getVersion() != null && version == null ) {
                return false;
            }
            if ( capabilitiesEvaluator.getVersion() == null && version != null ) {
                return false;
            }
            return capabilitiesEvaluator.getVersion().equals( version );
        }
        return false;
    }

    /**
     * 
     * @return description of classes to be used for accessing WMS
     */
    public WMSGridCoverageAdapterSettings getWMSGridCoveragesAdapter() {
        WMSGridCoverageAdapterType wmsAda = new WMSGridCoverageAdapterType();
        if ( adminSettings != null && adminSettings.getDataAdapter() != null
             && adminSettings.getDataAdapter().getWMSGridCoverageAdapter() != null ) {
            WMSGridCoverageAdapterType tmp = adminSettings.getDataAdapter().getWMSGridCoverageAdapter();
            wmsAda.getCapabilitiesEvaluator().addAll( tmp.getCapabilitiesEvaluator() );
            wmsAda.setTimeout( tmp.getTimeout() );
            wmsAda.setFeatureCount( tmp.getFeatureCount() );
        } else {
            wmsAda.getCapabilitiesEvaluator().add( new ServiceAdapterType.CapabilitiesEvaluator() );
            wmsAda.setTimeout( new ServiceAdapterType.Timeout() );
            wmsAda.setFeatureCount( new WMSGridCoverageAdapterType.FeatureCount() );
        }

        // admin settings can be changed if a) available and b) declared as changeable
        boolean changeable = adminSettings == null || adminSettings.getDataAdapter() == null
                             || adminSettings.getDataAdapter().getWMSGridCoverageAdapter() == null
                             || adminSettings.getDataAdapter().isChangeable();
        if ( changeable && userSettings != null && userSettings.getDataAdapter() != null
             && userSettings.getDataAdapter().getWMSGridCoverageAdapter() != null ) {
            WMSGridCoverageAdapterType tmp = userSettings.getDataAdapter().getWMSGridCoverageAdapter();
            if ( tmp != null ) {
                wmsAda.setTimeout( tmp.getTimeout() );
                wmsAda.setFeatureCount( tmp.getFeatureCount() );
                List<CapabilitiesEvaluator> list = tmp.getCapabilitiesEvaluator();
                for ( CapabilitiesEvaluator capabilitiesEvaluator : list ) {
                    if ( !isCapabilitiesEvaluatorAvailable( wmsAda, capabilitiesEvaluator.getVersion() ) ) {
                        wmsAda.getCapabilitiesEvaluator().add( capabilitiesEvaluator );
                    }
                }
            }
        }

        // user settings can be changed if a) admin settings are changeable b) available and
        // c) declared as changeable
        changeable = changeable
                     && ( userSettings == null || userSettings.getDataAdapter() == null || userSettings.getDataAdapter().isChangeable() );
        if ( changeable && projectSettings.getDataAdapter() != null
             && projectSettings.getDataAdapter().getWMSGridCoverageAdapter() != null ) {
            WMSGridCoverageAdapterType tmp = projectSettings.getDataAdapter().getWMSGridCoverageAdapter();
            if ( tmp != null ) {
                wmsAda.setTimeout( tmp.getTimeout() );
                wmsAda.setFeatureCount( tmp.getFeatureCount() );
                List<CapabilitiesEvaluator> list = tmp.getCapabilitiesEvaluator();
                for ( CapabilitiesEvaluator capabilitiesEvaluator : list ) {
                    if ( !isCapabilitiesEvaluatorAvailable( wmsAda, capabilitiesEvaluator.getVersion() ) ) {
                        wmsAda.getCapabilitiesEvaluator().add( capabilitiesEvaluator );
                    }
                }
            }
        } else if ( projectSettings.getDataAdapter() == null ) {
            DataAdapterType ada = new DataAdapterType();
            ada.setChangeable( changeable );
            projectSettings.setDataAdapter( ada );
        }
        if ( wmsAda.getCapabilitiesEvaluator().size() == 0 ) {
            CapabilitiesEvaluator ce = new CapabilitiesEvaluator();
            ce.setChangeable( false );
            ce.setVersion( "1.1.1" );
            ce.setVal( WMS111CapabilitiesEvaluator.class.getName() );
            wmsAda.getCapabilitiesEvaluator().add( ce );
            ce = new CapabilitiesEvaluator();
            ce.setChangeable( false );
            ce.setVersion( "1.3.0" );
            ce.setVal( WMS130CapabilitiesEvaluator.class.getName() );
            wmsAda.getCapabilitiesEvaluator().add( ce );
        }

        projectSettings.getDataAdapter().setWMSGridCoverageAdapter( wmsAda );
        return new WMSGridCoverageAdapterSettings( wmsAda, false );
    }

    /**
     * 
     * @return description of classes to be used for accessing WCS
     */
    public WCSGridCoverageAdapterSettings getWCSGridCoveragesAdapter() {
        ServiceAdapterType wcsAda = new ServiceAdapterType();
        if ( adminSettings != null && adminSettings.getDataAdapter() != null
             && adminSettings.getDataAdapter().getWCSGridCoverageAdapter() != null ) {
            ServiceAdapterType tmp = adminSettings.getDataAdapter().getWCSGridCoverageAdapter();
            wcsAda.getCapabilitiesEvaluator().addAll( tmp.getCapabilitiesEvaluator() );
            wcsAda.setTimeout( tmp.getTimeout() );
        } else {
            wcsAda.getCapabilitiesEvaluator().add( new ServiceAdapterType.CapabilitiesEvaluator() );
            wcsAda.setTimeout( new ServiceAdapterType.Timeout() );
        }

        // admin settings can be changed if a) available and b) declared as changeable
        boolean changeable = adminSettings == null || adminSettings.getDataAdapter() == null
                             || adminSettings.getDataAdapter().getWCSGridCoverageAdapter() == null
                             || adminSettings.getDataAdapter().isChangeable();
        if ( changeable && userSettings != null && userSettings.getDataAdapter() != null
             && userSettings.getDataAdapter().getWCSGridCoverageAdapter() != null ) {
            ServiceAdapterType tmp = userSettings.getDataAdapter().getWCSGridCoverageAdapter();
            if ( tmp != null ) {
                wcsAda.setTimeout( tmp.getTimeout() );
                List<CapabilitiesEvaluator> list = tmp.getCapabilitiesEvaluator();
                for ( CapabilitiesEvaluator capabilitiesEvaluator : list ) {
                    if ( !isCapabilitiesEvaluatorAvailable( wcsAda, capabilitiesEvaluator.getVersion() ) ) {
                        wcsAda.getCapabilitiesEvaluator().add( capabilitiesEvaluator );
                    }
                }
            }
        }

        // user settings can be changed if a) admin settings are changeable b) available and
        // c) declared as changeable
        changeable = changeable
                     && ( userSettings == null || userSettings.getDataAdapter() == null || userSettings.getDataAdapter().isChangeable() );
        if ( changeable && projectSettings.getDataAdapter() != null
             && projectSettings.getDataAdapter().getWCSGridCoverageAdapter() != null ) {
            ServiceAdapterType tmp = projectSettings.getDataAdapter().getWCSGridCoverageAdapter();
            if ( tmp != null ) {
                wcsAda.setTimeout( tmp.getTimeout() );
                List<CapabilitiesEvaluator> list = tmp.getCapabilitiesEvaluator();
                for ( CapabilitiesEvaluator capabilitiesEvaluator : list ) {
                    if ( !isCapabilitiesEvaluatorAvailable( wcsAda, capabilitiesEvaluator.getVersion() ) ) {
                        wcsAda.getCapabilitiesEvaluator().add( capabilitiesEvaluator );
                    }
                }
            }
        } else if ( projectSettings.getDataAdapter() == null ) {
            DataAdapterType ada = new DataAdapterType();
            ada.setChangeable( changeable );
            projectSettings.setDataAdapter( ada );
        }

        projectSettings.getDataAdapter().setWCSGridCoverageAdapter( wcsAda );
        return new WCSGridCoverageAdapterSettings( wcsAda, false );
    }

    /**
     * 
     * @return list of known raster format (e.g. |gif|jpeg|tif| )
     */
    public String getKnownRasterFormats() {
        KnownRasterFormats raster = null;
        if ( adminSettings != null && adminSettings.getDataAdapter() != null
             && adminSettings.getDataAdapter().getKnownRasterFormats() != null ) {
            raster = adminSettings.getDataAdapter().getKnownRasterFormats();
        }
        // admin settings can be changed if a) available and b) declared as changeable
        boolean changeable = adminSettings == null || adminSettings.getDataAdapter() == null
                             || adminSettings.getDataAdapter().getKnownRasterFormats() == null
                             || adminSettings.getDataAdapter().isChangeable();
        if ( changeable && userSettings != null && userSettings.getDataAdapter() != null
             && userSettings.getDataAdapter().getKnownRasterFormats() != null ) {
            raster = userSettings.getDataAdapter().getKnownRasterFormats();
        }
        // user settings can be changed if a) admin settings are changeable b) available and
        // c) declared as changeable
        changeable = changeable
                     && ( userSettings == null || userSettings.getDataAdapter() == null || userSettings.getDataAdapter().isChangeable() );
        if ( changeable && projectSettings.getDataAdapter() != null
             && projectSettings.getDataAdapter().getKnownRasterFormats() != null ) {
            raster = userSettings.getDataAdapter().getKnownRasterFormats();
        } else if ( projectSettings.getDataAdapter() == null ) {
            DataAdapterType ada = new DataAdapterType();
            ada.setChangeable( changeable );
            projectSettings.setDataAdapter( ada );
        }

        projectSettings.getDataAdapter().setKnownRasterFormats( raster );
        return raster.getVal();
    }

    /**
     * 
     * @return list of available project templates
     */
    public ProjectTemplates getProjectTemplates() {
        ProjectTemplatesType templates = null;
        if ( adminSettings != null && adminSettings.getProjectTemplates() != null ) {
            templates = adminSettings.getProjectTemplates();
        } else {
            templates = new ProjectTemplatesType();
            templates.setChangeable( true );
        }
        // admin settings can be changed if a) available and b) declared as changeable
        boolean changeable = templates.isChangeable();
        if ( changeable && userSettings != null && userSettings.getProjectTemplates() != null ) {
            ProjectTemplatesType tmp = userSettings.getProjectTemplates();
            List<Template> list = tmp.getTemplate();
            for ( Template template : list ) {
                templates.getTemplate().add( template );
            }
        }
        // user settings can be changed if a) admin settings are changeable b) available and
        // c) declared as changeable
        changeable = changeable && templates.isChangeable();
        if ( changeable && projectSettings.getProjectTemplates() != null ) {
            ProjectTemplatesType tmp = projectSettings.getProjectTemplates();
            List<Template> list = tmp.getTemplate();
            for ( Template template : list ) {
                templates.getTemplate().add( template );
            }
        } else if ( projectSettings.getProjectTemplates() == null ) {
            projectSettings.setProjectTemplates( templates );
        }

        return new ProjectTemplates( templates, templates.isChangeable() );
    }

    /**
     * 
     * @return defined file filters/extensions to be considered when opening a file dialog
     */
    public FileFilters getFileFilters() {
        FileFilterType fft = new FileFilterType();
        if ( adminSettings != null && adminSettings.getFileFilter() != null ) {
            FileFilterType tmp = adminSettings.getFileFilter();
            List<Format> list = tmp.getFormat();
            for ( Format format : list ) {
                fft.getFormat().add( format );
            }
        }

        // admin settings can be changed if a) available and b) declared as changeable
        boolean changeable = adminSettings == null || adminSettings.getFileFilter() == null
                             || adminSettings.getFileFilter().isChangeable();
        if ( changeable && userSettings != null && userSettings.getFileFilter() != null ) {
            FileFilterType tmp = userSettings.getFileFilter();
            List<Format> list = tmp.getFormat();
            for ( Format format : list ) {
                fft.getFormat().add( format );
            }
        }
        // user settings can be changed if a) admin settings are changeable b) available and
        // c) declared as changeable
        changeable = changeable
                     && ( userSettings == null || userSettings.getFileFilter() == null || userSettings.getFileFilter().isChangeable() );
        if ( changeable && projectSettings.getFileFilter() != null ) {
            FileFilterType tmp = projectSettings.getFileFilter();
            List<Format> list = tmp.getFormat();
            for ( Format format : list ) {
                fft.getFormat().add( format );
            }
        } else if ( projectSettings.getFileFilter() == null ) {
            projectSettings.setFileFilter( fft );
        }
        return new FileFilters( fft, false );
    }

    /**
     * 
     * @return metrics definitions for geometry validation
     */
    public ValidationGeomMetrics getValidationGeomMetrics() {
        GeometryMetricsType gmt = null;
        if ( adminSettings != null && adminSettings.getValidation() != null ) {
            gmt = adminSettings.getValidation().getGeometryMetrics();
        } else {
            gmt = new GeometryMetricsType();
            gmt.setChangeable( true );
        }

        // admin settings can be changed if a) available and b) declared as changeable
        boolean changeable = adminSettings == null || adminSettings.getValidation() == null
                             || adminSettings.getValidation().getGeometryMetrics().isChangeable();
        if ( changeable && userSettings != null && userSettings.getValidation() != null ) {
            gmt = userSettings.getValidation().getGeometryMetrics();
        }

        // user settings can be changed if a) admin settings are changeable b) available and
        // c) declared as changeable
        changeable = changeable && gmt.isChangeable();
        if ( changeable && projectSettings.getValidation() != null ) {
            gmt = projectSettings.getValidation().getGeometryMetrics();
        } else if ( projectSettings.getValidation() == null ) {
            projectSettings.setValidation( new ValidationType() );
        }
        projectSettings.getValidation().setGeometryMetrics( gmt );

        return new ValidationGeomMetrics( gmt, gmt.isChangeable() );
    }

    /**
     * 
     * @return types definitions for geometry validation
     */
    public ValidationGeomTypes getValidationGeomTypes() {
        GeometryTypeType gtt = null;
        if ( adminSettings != null && adminSettings.getValidation() != null ) {
            gtt = adminSettings.getValidation().getGeometryTypes();
        } else {
            gtt = new GeometryTypeType();
            gtt.setChangeable( true );
        }

        // admin settings can be changed if a) available and b) declared as changeable
        boolean changeable = adminSettings == null || adminSettings.getValidation() == null
                             || adminSettings.getValidation().getGeometryTypes().isChangeable();
        if ( changeable && userSettings != null && userSettings.getValidation() != null ) {
            gtt = userSettings.getValidation().getGeometryTypes();
        }
        // user settings can be changed if a) admin settings are changeable b) available and
        // c) declared as changeable
        changeable = changeable && gtt.isChangeable();
        if ( changeable && projectSettings.getValidation() != null
             && projectSettings.getValidation().getGeometryTypes() != null ) {
            gtt = projectSettings.getValidation().getGeometryTypes();
        } else if ( projectSettings.getValidation() == null ) {
            projectSettings.setValidation( new ValidationType() );
        }
        projectSettings.getValidation().setGeometryTypes( gtt );
        return new ValidationGeomTypes( gtt, gtt.isChangeable() );
    }

    /**
     * 
     * @return topology definitions for geometry validation
     */
    public ValidationGeomTopology getValidationGeomTopology() {
        GeometryTopologyType gtt = null;
        if ( adminSettings != null && adminSettings.getValidation() != null ) {
            gtt = adminSettings.getValidation().getGeometryTopology();
        } else {
            gtt = new GeometryTopologyType();
            gtt.setChangeable( true );
        }

        // admin settings can be changed if a) available and b) declared as changeable
        boolean changeable = adminSettings == null || adminSettings.getValidation() == null
                             || adminSettings.getValidation().getGeometryTopology().isChangeable();
        if ( changeable && userSettings != null && userSettings.getValidation() != null ) {
            gtt = userSettings.getValidation().getGeometryTopology();
        }

        // user settings can be changed if a) admin settings are changeable b) available and
        // c) declared as changeable
        changeable = changeable && gtt.isChangeable();
        if ( changeable && projectSettings.getValidation() != null
             && projectSettings.getValidation().getGeometryTopology() != null ) {
            gtt = projectSettings.getValidation().getGeometryTopology();
        } else if ( projectSettings.getValidation() == null ) {
            projectSettings.setValidation( new ValidationType() );
        }
        projectSettings.getValidation().setGeometryTopology( gtt );
        return new ValidationGeomTopology( gtt, gtt.isChangeable() );
    }

    /**
     * 
     * @return <code>true</code> if warnings for validation errors shall be printed automaticlly while
     *         editing/digitizing
     */
    public boolean printValidationWaring() {
        boolean warning = false;
        if ( adminSettings != null && adminSettings.getValidation() != null ) {
            warning = adminSettings.getValidation().isPrintWarning();
        }

        // admin settings can be changed if a) available and b) declared as changeable
        if ( userSettings != null && userSettings.getValidation() != null ) {
            warning = userSettings.getValidation().isPrintWarning();
        }

        // user settings can be changed if a) admin settings are changeable b) available and
        // c) declared as changeable
        if ( projectSettings.getValidation() != null ) {
            warning = projectSettings.getValidation().isPrintWarning();
        } else if ( projectSettings.getValidation() == null ) {
            projectSettings.setValidation( new ValidationType() );
        }
        return warning;
    }

    /**
     * @see #printValidationWaring()
     * @param value
     */
    public void setPrintValidationWaring( boolean value ) {
        projectSettings.getValidation().setPrintWarning( value );
    }

    /**
     * 
     * @return options for snapping metrics (tolerance, UOM)
     */
    public SnappingToleranceOpt getSnappingToleranceOptions() {
        SnappingType gt = new SnappingType();
        gt.setTolerance( new SnappingType.Tolerance() );

        if ( adminSettings != null && adminSettings.getSnapping() != null
             && adminSettings.getSnapping().getTolerance() != null ) {
            SnappingType.Tolerance st = new SnappingType.Tolerance();
            st.setUom( adminSettings.getSnapping().getTolerance().getUom() );
            st.setVal( adminSettings.getSnapping().getTolerance().getVal() );
            gt.setTolerance( st );
        }

        if ( userSettings != null && userSettings.getSnapping() != null
             && userSettings.getSnapping().getTolerance() != null ) {
            SnappingType.Tolerance st = new SnappingType.Tolerance();
            st.setUom( userSettings.getSnapping().getTolerance().getUom() );
            st.setVal( userSettings.getSnapping().getTolerance().getVal() );
            gt.setTolerance( st );
        }

        if ( projectSettings.getSnapping() != null ) {
            SnappingType.Tolerance st = new SnappingType.Tolerance();
            if ( projectSettings.getSnapping() != null && projectSettings.getSnapping().getTolerance() != null ) {
                st.setUom( projectSettings.getSnapping().getTolerance().getUom() );
                st.setVal( projectSettings.getSnapping().getTolerance().getVal() );
            }
            gt.setTolerance( st );
            List<SnapLayerType> list = projectSettings.getSnapping().getSnapLayer();
            for ( SnapLayerType snapLayerType : list ) {
                SnapLayerType slt = new SnapLayerType();
                slt.setEdge( snapLayerType.isEdge() );
                slt.setEdgeCenter( snapLayerType.isEdgeCenter() );
                slt.setEndNode( snapLayerType.isEndNode() );
                slt.setStartNode( snapLayerType.isStartNode() );
                slt.setVertex( snapLayerType.isVertex() );
                slt.setLayer( snapLayerType.getLayer() );
                gt.getSnapLayer().add( slt );
            }
        }
        projectSettings.setSnapping( gt );

        return new SnappingToleranceOpt( gt.getTolerance(), true );
    }

    /**
     * 
     * @return list of layers and definition if and how they are selected for snapping
     */
    public SnappingLayersOpts getSnappingLayersOptions() {

        SnappingType gt = new SnappingType();

        if ( adminSettings != null && adminSettings.getSnapping() != null ) {
            List<SnapLayerType> list = adminSettings.getSnapping().getSnapLayer();
            for ( SnapLayerType snapLayerType : list ) {
                SnapLayerType slt = new SnapLayerType();
                slt.setEdge( snapLayerType.isEdge() );
                slt.setEdgeCenter( snapLayerType.isEdgeCenter() );
                slt.setEndNode( snapLayerType.isEndNode() );
                slt.setStartNode( snapLayerType.isStartNode() );
                slt.setVertex( snapLayerType.isVertex() );
                slt.setLayer( snapLayerType.getLayer() );
                gt.getSnapLayer().add( slt );
            }
        }

        if ( userSettings != null && userSettings.getSnapping() != null ) {
            List<SnapLayerType> list = userSettings.getSnapping().getSnapLayer();
            for ( SnapLayerType snapLayerType : list ) {
                SnapLayerType slt = new SnapLayerType();
                slt.setEdge( snapLayerType.isEdge() );
                slt.setEdgeCenter( snapLayerType.isEdgeCenter() );
                slt.setEndNode( snapLayerType.isEndNode() );
                slt.setStartNode( snapLayerType.isStartNode() );
                slt.setVertex( snapLayerType.isVertex() );
                slt.setLayer( snapLayerType.getLayer() );
                gt.getSnapLayer().add( slt );
            }
        }

        // user settings can be changed if a) admin settings are changeable b) available and
        if ( projectSettings.getSnapping() != null ) {
            List<SnapLayerType> list = projectSettings.getSnapping().getSnapLayer();
            for ( SnapLayerType snapLayerType : list ) {
                SnapLayerType slt = new SnapLayerType();
                slt.setEdge( snapLayerType.isEdge() );
                slt.setEdgeCenter( snapLayerType.isEdgeCenter() );
                slt.setEndNode( snapLayerType.isEndNode() );
                slt.setStartNode( snapLayerType.isStartNode() );
                slt.setVertex( snapLayerType.isVertex() );
                slt.setLayer( snapLayerType.getLayer() );
                gt.getSnapLayer().add( slt );
            }
            SnappingType.Tolerance st = new SnappingType.Tolerance();
            if ( projectSettings.getSnapping() != null && projectSettings.getSnapping().getTolerance() != null ) {
                st.setUom( projectSettings.getSnapping().getTolerance().getUom() );
                st.setVal( projectSettings.getSnapping().getTolerance().getVal() );
            }
            gt.setTolerance( st );
        }
        projectSettings.setSnapping( gt );

        return new SnappingLayersOpts( gt.getSnapLayer(), true );
    }

    /**
     * 
     * @return settings for handling clipboard
     */
    public ClipboardOptions getClipboardOptions() {

        ClipboardType clipboard = new ClipboardType();
        clipboard.setChangeable( true );
        if ( adminSettings != null && adminSettings.getClipboard() != null ) {
            clipboard.setFormat( adminSettings.getClipboard().getFormat() );
            clipboard.setMaxObjects( adminSettings.getClipboard().getMaxObjects() );
            clipboard.setChangeable( adminSettings.getClipboard().isChangeable() );
        }

        // admin settings can be changed if a) available and b) declared as changeable
        boolean changeable = clipboard.isChangeable();
        if ( changeable && userSettings != null && userSettings.getClipboard() != null ) {
            clipboard.setFormat( userSettings.getClipboard().getFormat() );
            clipboard.setMaxObjects( userSettings.getClipboard().getMaxObjects() );
            clipboard.setChangeable( userSettings.getClipboard().isChangeable() );
        }

        // user settings can be changed if a) admin settings are changeable b) available and
        // c) declared as changeable
        changeable = clipboard.isChangeable();
        if ( changeable && projectSettings.getClipboard() != null ) {
            clipboard.setFormat( projectSettings.getClipboard().getFormat() );
            clipboard.setMaxObjects( projectSettings.getClipboard().getMaxObjects() );
            clipboard.setChangeable( projectSettings.getClipboard().isChangeable() );
        }
        projectSettings.setClipboard( clipboard );

        return new ClipboardOptions( clipboard, clipboard.isChangeable() );
    }

    /**
     * 
     * @return encapsulated security settings
     */
    public SecurityOptions getSecurityOptions() {
        SecurityType securityType = new SecurityType();
        securityType.setChangeable( true );
        if ( adminSettings != null && adminSettings.getSecurity() != null ) {
            List<AuthenticationType> list = adminSettings.getSecurity().getAuthentication();
            for ( AuthenticationType authenticationType : list ) {
                securityType.getAuthentication().add( authenticationType );
            }
            securityType.setChangeable( adminSettings.getSecurity().isChangeable() );
        }

        // admin settings can be changed if a) available and b) declared as changeable
        boolean changeable = securityType.isChangeable();
        if ( changeable && userSettings != null && userSettings.getSecurity() != null ) {
            List<AuthenticationType> list = userSettings.getSecurity().getAuthentication();
            for ( AuthenticationType authenticationType : list ) {
                securityType.getAuthentication().add( authenticationType );
            }
            securityType.setChangeable( userSettings.getSecurity().isChangeable() );
        }

        // user settings can be changed if a) admin settings are changeable b) available and
        // c) declared as changeable
        changeable = securityType.isChangeable();
        if ( changeable && projectSettings.getSecurity() != null ) {
            List<AuthenticationType> list = projectSettings.getSecurity().getAuthentication();
            for ( AuthenticationType authenticationType : list ) {
                securityType.getAuthentication().add( authenticationType );
            }
            securityType.setChangeable( projectSettings.getSecurity().isChangeable() );
        }
        projectSettings.setSecurity( securityType );
        return new SecurityOptions( securityType, securityType.isChangeable() );
    }

    public GraphicOptions getGraphicOptions() {
        if ( graphicOptions != null ) {
            return graphicOptions;
        }
        GraphicsType graphicsType = new GraphicsType();
        graphicsType.setDashArrayDefinitions( new DashArrayDefinitionsType() );
        graphicsType.setFillGraphicDefinitions( new GraphicDefinitionsType() );
        graphicsType.setSymbolDefinitions( new GraphicDefinitionsType() );

        if ( adminSettings != null && adminSettings.getGraphics() != null ) {
            GraphicsType tmp = adminSettings.getGraphics();
            List<PresetType> presets = tmp.getClassificationPreset();
            for ( PresetType presetType : presets ) {
                graphicsType.getClassificationPreset().add( presetType );
            }
            presets = tmp.getStylePreset();
            for ( PresetType presetType : presets ) {
                graphicsType.getStylePreset().add( presetType );
            }
            presets = tmp.getSymbolizerPreset();
            for ( PresetType presetType : presets ) {
                graphicsType.getSymbolizerPreset().add( presetType );
            }
            List<ColorSchemesType> colorSchemes = tmp.getColorSchemes();
            for ( ColorSchemesType colorSchemesType : colorSchemes ) {
                graphicsType.getColorSchemes().add( colorSchemesType );
            }
            DashArrayDefinitionsType udadt = tmp.getDashArrayDefinitions();
            if ( udadt == null ) {
                udadt = new DashArrayDefinitionsType();
            }
            DashArrayDefinitionsType adadt = new DashArrayDefinitionsType();
            List<DashArray> da = udadt.getDashArray();
            for ( DashArray dashArray : da ) {
                adadt.getDashArray().add( dashArray );
            }
            graphicsType.setDashArrayDefinitions( adadt );

            GraphicDefinitionsType ufgdt = tmp.getFillGraphicDefinitions();
            if ( ufgdt == null ) {
                ufgdt = new GraphicDefinitionsType();
            }
            GraphicDefinitionsType afgdt = new GraphicDefinitionsType();
            List<Graphic> list = ufgdt.getGraphic();
            for ( Graphic graphic : list ) {
                afgdt.getGraphic().add( graphic );
            }
            graphicsType.setFillGraphicDefinitions( afgdt );

            GraphicDefinitionsType usdt = tmp.getSymbolDefinitions();
            if ( usdt == null ) {
                usdt = new GraphicDefinitionsType();
            }
            GraphicDefinitionsType asdt = new GraphicDefinitionsType();
            list = usdt.getGraphic();
            for ( Graphic graphic : list ) {
                asdt.getGraphic().add( graphic );
            }
            graphicsType.setSymbolDefinitions( asdt );

        }

        // admin settings can be changed if a) available and b) declared as changeable
        boolean changeable = adminSettings == null || adminSettings.getGraphics() == null
                             || adminSettings.getGraphics().isChangeable();
        if ( changeable && userSettings != null && userSettings.getGraphics() != null ) {
            GraphicsType tmp = userSettings.getGraphics();
            List<PresetType> presets = tmp.getClassificationPreset();
            for ( PresetType presetType : presets ) {
                graphicsType.getClassificationPreset().add( presetType );
            }
            presets = tmp.getStylePreset();
            for ( PresetType presetType : presets ) {
                graphicsType.getStylePreset().add( presetType );
            }
            presets = tmp.getSymbolizerPreset();
            for ( PresetType presetType : presets ) {
                graphicsType.getSymbolizerPreset().add( presetType );
            }
            List<ColorSchemesType> colorSchemes = tmp.getColorSchemes();
            for ( ColorSchemesType colorSchemesType : colorSchemes ) {
                graphicsType.getColorSchemes().add( colorSchemesType );
            }
            DashArrayDefinitionsType udadt = tmp.getDashArrayDefinitions();
            if ( udadt != null ) {
                DashArrayDefinitionsType adadt = graphicsType.getDashArrayDefinitions();
                if ( adadt == null ) {
                    graphicsType.setDashArrayDefinitions( udadt );
                } else {
                    List<DashArray> da = udadt.getDashArray();
                    for ( DashArray dashArray : da ) {
                        adadt.getDashArray().add( dashArray );
                    }
                }
            }
            GraphicDefinitionsType ufgdt = tmp.getFillGraphicDefinitions();
            if ( ufgdt != null ) {
                GraphicDefinitionsType afgdt = graphicsType.getFillGraphicDefinitions();
                if ( afgdt == null ) {
                    graphicsType.setFillGraphicDefinitions( ufgdt );
                } else {
                    List<Graphic> list = ufgdt.getGraphic();
                    for ( Graphic graphic : list ) {
                        afgdt.getGraphic().add( graphic );
                    }
                }
            }
            GraphicDefinitionsType usdt = tmp.getSymbolDefinitions();
            if ( usdt != null ) {
                GraphicDefinitionsType asdt = graphicsType.getSymbolDefinitions();
                if ( asdt == null ) {
                    graphicsType.setSymbolDefinitions( usdt );
                } else {
                    List<Graphic> list = usdt.getGraphic();
                    for ( Graphic graphic : list ) {
                        asdt.getGraphic().add( graphic );
                    }
                }
            }
        }

        // user settings can be changed if a) admin settings are changeable b) available and
        // c) declared as changeable
        changeable = changeable
                     && ( userSettings == null || userSettings.getGraphics() == null || userSettings.getGraphics().isChangeable() );
        if ( changeable && projectSettings.getGraphics() != null ) {
            GraphicsType tmp = projectSettings.getGraphics();
            List<PresetType> presets = tmp.getClassificationPreset();
            for ( PresetType presetType : presets ) {
                graphicsType.getClassificationPreset().add( presetType );
            }
            presets = tmp.getStylePreset();
            for ( PresetType presetType : presets ) {
                graphicsType.getStylePreset().add( presetType );
            }
            presets = tmp.getSymbolizerPreset();
            for ( PresetType presetType : presets ) {
                graphicsType.getSymbolizerPreset().add( presetType );
            }
            List<ColorSchemesType> colorSchemes = tmp.getColorSchemes();
            for ( ColorSchemesType colorSchemesType : colorSchemes ) {
                graphicsType.getColorSchemes().add( colorSchemesType );
            }
            DashArrayDefinitionsType udadt = tmp.getDashArrayDefinitions();
            if ( udadt != null ) {
                DashArrayDefinitionsType adadt = graphicsType.getDashArrayDefinitions();
                if ( adadt == null ) {
                    graphicsType.setDashArrayDefinitions( udadt );
                } else {
                    List<DashArray> da = udadt.getDashArray();
                    for ( DashArray dashArray : da ) {
                        adadt.getDashArray().add( dashArray );
                    }
                }
            }

            GraphicDefinitionsType ufgdt = tmp.getFillGraphicDefinitions();
            if ( ufgdt != null ) {
                GraphicDefinitionsType afgdt = graphicsType.getFillGraphicDefinitions();
                if ( afgdt == null ) {
                    graphicsType.setFillGraphicDefinitions( ufgdt );
                } else {
                    List<Graphic> list = ufgdt.getGraphic();
                    for ( Graphic graphic : list ) {
                        afgdt.getGraphic().add( graphic );
                    }
                }
            }
            GraphicDefinitionsType usdt = tmp.getSymbolDefinitions();
            if ( usdt != null ) {
                GraphicDefinitionsType asdt = graphicsType.getSymbolDefinitions();
                if ( asdt == null ) {
                    graphicsType.setSymbolDefinitions( usdt );
                } else {
                    List<Graphic> list = usdt.getGraphic();
                    for ( Graphic graphic : list ) {
                        asdt.getGraphic().add( graphic );
                    }
                }
            }
        }
        projectSettings.setGraphics( graphicsType );

        graphicOptions = new GraphicOptions( graphicsType, changeable );
        return graphicOptions;
    }

    /**
     * 
     * @return map of file extensions assigned to external programs
     */
    public ExternalReferencesOptions getExternalReferencesOptions() {
        ExternalReferencesType extRefType = new ExternalReferencesType();
        extRefType.setChangeable( true );
        if ( adminSettings != null && adminSettings.getExternalReferences() != null ) {
            List<ExternalReferencesType.Reference> list = adminSettings.getExternalReferences().getReference();
            for ( Reference reference : list ) {
                extRefType.getReference().add( reference );
            }
            extRefType.setChangeable( adminSettings.getExternalReferences().isChangeable() );
        }

        // admin settings can be changed if a) available and b) declared as changeable
        boolean changeable = extRefType.isChangeable();
        if ( changeable && userSettings != null && userSettings.getExternalReferences() != null ) {
            List<ExternalReferencesType.Reference> list = userSettings.getExternalReferences().getReference();
            for ( Reference reference : list ) {
                extRefType.getReference().add( reference );
            }
            extRefType.setChangeable( userSettings.getExternalReferences().isChangeable() );
        }

        // user settings can be changed if a) admin settings are changeable b) available and
        // c) declared as changeable
        changeable = extRefType.isChangeable();
        if ( changeable && projectSettings.getExternalReferences() != null ) {
            List<ExternalReferencesType.Reference> list = projectSettings.getExternalReferences().getReference();
            for ( Reference reference : list ) {
                extRefType.getReference().add( reference );
            }
            extRefType.setChangeable( projectSettings.getExternalReferences().isChangeable() );
        }
        projectSettings.setExternalReferences( extRefType );
        return new ExternalReferencesOptions( extRefType, extRefType.isChangeable() );
    }

    /**
     * 
     * @return instance of {@link FileAccessOptions}
     */
    public FileAccessOptions getFileAccessOptions() {
        FileAccessType fileAccessType = new FileAccessType();
        fileAccessType.setChangeable( true );
        if ( adminSettings != null && adminSettings.getFileAccess() != null ) {
            List<FileAccessType.Access> list = adminSettings.getFileAccess().getAccess();
            for ( Access access : list ) {
                fileAccessType.getAccess().add( access );
            }
            fileAccessType.setChangeable( adminSettings.getFileAccess().isChangeable() );
        }

        // admin settings can be changed if a) available and b) declared as changeable
        boolean changeable = fileAccessType.isChangeable();
        if ( changeable && userSettings != null && userSettings.getFileAccess() != null ) {
            List<FileAccessType.Access> list = userSettings.getFileAccess().getAccess();
            for ( Access access : list ) {
                fileAccessType.getAccess().add( access );
            }
            fileAccessType.setChangeable( userSettings.getFileAccess().isChangeable() );
        }

        // user settings can be changed if a) admin settings are changeable b) available and
        // c) declared as changeable
        changeable = fileAccessType.isChangeable();
        if ( changeable && projectSettings.getFileAccess() != null ) {
            List<FileAccessType.Access> list = projectSettings.getFileAccess().getAccess();
            for ( Access access : list ) {
                fileAccessType.getAccess().add( access );
            }
            fileAccessType.setChangeable( projectSettings.getFileAccess().isChangeable() );
        }
        projectSettings.setFileAccess( fileAccessType );

        return new FileAccessOptions( fileAccessType, fileAccessType.isChangeable() );
    }

    /**
     * 
     * @return
     */
    public FormatsOptions getFormatsOptions() {
        FormatsType formatsType = new FormatsType();
        formatsType.setChangeable( true );
        if ( adminSettings != null && adminSettings.getFormats() != null ) {
            List<org.deegree.igeo.config.FormatsType.Format> list = adminSettings.getFormats().getFormat();
            for ( org.deegree.igeo.config.FormatsType.Format format : list ) {

                formatsType.getFormat().add( format );

            }
            formatsType.setChangeable( adminSettings.getFormats().isChangeable() );
        }

        // admin settings can be changed if a) available and b) declared as changeable
        boolean changeable = formatsType.isChangeable();
        if ( changeable && userSettings != null && userSettings.getFormats() != null ) {
            List<org.deegree.igeo.config.FormatsType.Format> list = userSettings.getFormats().getFormat();
            List<org.deegree.igeo.config.FormatsType.Format> list2 = formatsType.getFormat();
            for ( org.deegree.igeo.config.FormatsType.Format format : list ) {
                boolean tmp = false;
                for ( org.deegree.igeo.config.FormatsType.Format format2 : list2 ) {
                    if ( format.getName().equals( format2.getName() ) ) {
                        tmp = true;
                        break;
                    }
                }
                if ( !tmp ) {
                    formatsType.getFormat().add( format );
                }
            }
            formatsType.setChangeable( userSettings.getFormats().isChangeable() );
        }

        // user settings can be changed if a) admin settings are changeable b) available and
        // c) declared as changeable
        changeable = formatsType.isChangeable();
        if ( changeable && projectSettings.getFormats() != null ) {
            List<org.deegree.igeo.config.FormatsType.Format> list = projectSettings.getFormats().getFormat();
            List<org.deegree.igeo.config.FormatsType.Format> list2 = formatsType.getFormat();
            for ( org.deegree.igeo.config.FormatsType.Format format : list ) {
                boolean tmp = false;
                for ( org.deegree.igeo.config.FormatsType.Format format2 : list2 ) {
                    if ( format.getName().equals( format2.getName() ) ) {
                        tmp = true;
                        break;
                    }
                }
                if ( !tmp ) {
                    formatsType.getFormat().add( format );
                }
            }
            formatsType.setChangeable( projectSettings.getFormats().isChangeable() );
        }
        projectSettings.setFormats( formatsType );
        return new FormatsOptions( formatsType, formatsType.isChangeable() );
    }

    /**
     * 
     * @return
     */
    public DictionaryCollection getDictionaries() {
        DictionaryCollection dictCol = new DictionaryCollection();
        DictionariesType dt = null;
        if ( adminSettings != null ) {
            dt = adminSettings.getDictionaries();
            if ( dt != null ) {
                List<OnlineResourceType> orList = dt.getOnlineResource();
                for ( OnlineResourceType onlineResourceType : orList ) {
                    try {
                        String s = onlineResourceType.getHref();
                        URL url = appCont.resolve( s );
                        XMLFragment xml = new XMLFragment( url );
                        dictCol.addDictionary( new Dictionary( xml ) );
                    } catch ( Exception e ) {
                        LOG.logError( e );
                    }
                }
            }
        }
        if ( ( ( dt != null && dt.isChangeable() ) || dt == null ) && ( userSettings != null ) ) {
            dt = userSettings.getDictionaries();
            if ( dt != null ) {
                List<OnlineResourceType> orList = dt.getOnlineResource();
                for ( OnlineResourceType onlineResourceType : orList ) {
                    try {
                        String s = onlineResourceType.getHref();
                        URL url = appCont.resolve( s );
                        XMLFragment xml = new XMLFragment( url );
                        dictCol.addDictionary( new Dictionary( xml ) );
                    } catch ( Exception e ) {
                        LOG.logError( e );
                    }
                }
            }
        }
        if ( ( dt != null && dt.isChangeable() ) || dt == null ) {
            dt = projectSettings.getDictionaries();
            if ( dt != null ) {
                List<OnlineResourceType> orList = dt.getOnlineResource();
                for ( OnlineResourceType onlineResourceType : orList ) {
                    try {
                        String s = onlineResourceType.getHref();
                        URL url = appCont.resolve( s );
                        XMLFragment xml = new XMLFragment( url );
                        dictCol.addDictionary( new Dictionary( xml ) );
                    } catch ( Exception e ) {
                        LOG.logError( e );
                    }
                }
            }
        }
        return dictCol;
    }

    public WFSDefaultStyleSettings getWFSDefaultStyle() {
        WFSDefaultStyleType defType = null;
        boolean changeable = true;
        if ( adminSettings != null && adminSettings.getWFSDefaultStyle() != null ) {
            defType = adminSettings.getWFSDefaultStyle();
            changeable = defType.isChangeable();
        }
        if ( ( defType == null || defType.isChangeable() )
             && ( userSettings != null && userSettings.getWFSDefaultStyle() != null ) ) {
            defType = userSettings.getWFSDefaultStyle();
            changeable = defType.isChangeable();
        }
        if ( defType == null || defType.isChangeable() ) {
            if ( projectSettings.getWFSDefaultStyle() != null ) {
                defType = projectSettings.getWFSDefaultStyle();
                changeable = defType.isChangeable();
            } else {
                defType = new WFSDefaultStyleType();
                defType.setChangeable( true );
                Symbolizer[] sym = new Symbolizer[3];
                sym[0] = StyleFactory.createPointSymbolizer();
                sym[1] = StyleFactory.createLineSymbolizer();
                sym[2] = StyleFactory.createPolygonSymbolizer();
                Rule rule = StyleFactory.createRule( sym );
                AbstractStyle style = StyleFactory.createStyle( "default", "default", null, null, new Rule[] { rule } );
                defType.setStyle( style.exportAsXML() );
            }
        }
        return new WFSDefaultStyleSettings( defType, changeable );
    }

}
