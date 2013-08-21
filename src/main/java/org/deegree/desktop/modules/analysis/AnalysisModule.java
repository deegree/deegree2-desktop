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

package org.deegree.desktop.modules.analysis;

import static javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT;
import static javax.swing.SwingConstants.TOP;
import static org.deegree.datatypes.Types.VARCHAR;
import static org.deegree.desktop.i18n.Messages.get;
import static org.deegree.desktop.views.swing.GeometryStatisticsPanel.resetGlobals;
import static org.deegree.framework.log.LoggerFactory.getLogger;

import java.awt.Component;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.commands.RefreshFeatureInfoCommand;
import org.deegree.desktop.dataadapter.DataAccessAdapter;
import org.deegree.desktop.dataadapter.DataAccessFactory;
import org.deegree.desktop.dataadapter.FeatureAdapter;
import org.deegree.desktop.main.DeegreeDesktop;
import org.deegree.desktop.mapmodel.Datasource;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.modules.ActionDescription;
import org.deegree.desktop.modules.DefaultModule;
import org.deegree.desktop.modules.ModuleCapabilities;
import org.deegree.desktop.modules.ActionDescription.ACTIONTYPE;
import org.deegree.desktop.modules.DefaultMapModule.SelectedFeaturesVisitor;
import org.deegree.desktop.modules.analysis.AnalysisFunction.AnalysisFunctionException;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.desktop.views.swing.GeometryStatisticsPanel;
import org.deegree.desktop.views.swing.analysis.AnalysisPanel;
import org.deegree.desktop.views.swing.analysis.AnalysisPanel.AnalysisPanelException;
import org.deegree.desktop.views.swing.util.panels.PanelDialog;
import org.deegree.framework.log.ILogger;
import org.deegree.desktop.config.MemoryDatasourceType;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.Point;

/**
 * <code>AnalysisModule</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * @param <T>
 */
public class AnalysisModule<T> extends DefaultModule<T> {

    private static final ILogger LOG = getLogger( AnalysisModule.class );

    static {
        ActionDescription ad1 = new ActionDescription(
                                                       "addOrUpdateAreaAndLength",
                                                       "opens a dialog that enables adding geometry length/area as properties to a feature",
                                                       null, "open dialog for adding geometry length/area",
                                                       ACTIONTYPE.PushButton, null, null );
        ActionDescription ad2 = new ActionDescription( "geometryStatistics",
                                                       "calculates statistics for each selected geometry", null,
                                                       "calculates statistics for each selected geometry",
                                                       ACTIONTYPE.PushButton, null, null );
        moduleCapabilities = new ModuleCapabilities( ad1, ad2 );
    }

    /**
     * 
     */
    public void geometryStatistics() {
        if ( "application".equalsIgnoreCase( appContainer.getViewPlatform() ) ) {
            MapModel mm = appContainer.getMapModel( null );
            SelectedFeaturesVisitor visitor = new SelectedFeaturesVisitor( -1 );
            try {
                mm.walkLayerTree( visitor );
                int size = visitor.col.size();
                if ( size > 0 ) {
                    int count = 0;

                    // preprocess to acquire accurate information on number of tabs
                    for ( int i = 0; i < size; ++i ) {
                        Feature feature = visitor.col.getFeature( i );
                        if ( feature.getDefaultGeometryPropertyValue() instanceof Point ) {
                            continue;
                        }
                        ++count;
                    }

                    if ( count > 10 ) {
                        int res = JOptionPane.showConfirmDialog( (Component) getViewForm(), get( "$MD10533", count ),
                                                                 get( "$DI10019" ), JOptionPane.YES_NO_OPTION );
                        if ( res == JOptionPane.NO_OPTION ) {
                            return;
                        }
                    }
                    if ( count == 0 ) {
                        DialogFactory.openInformationDialog( "application", getViewForm(), get( "$MD10585" ),
                                                             get( "$DI10018" ) );
                        return;
                    }

                    JTabbedPane tabs = new JTabbedPane( TOP, SCROLL_TAB_LAYOUT ) {
                        private static final long serialVersionUID = -1328589403039193378L;

                        @Override
                        public String toString() {
                            return get( "$MD10534" );
                        }
                    };

                    synchronized ( GeometryStatisticsPanel.class ) {
                        resetGlobals();

                        for ( int i = 0; i < size; ++i ) {
                            Feature feature = visitor.col.getFeature( i );
                            Geometry geometry = feature.getDefaultGeometryPropertyValue();
                            if ( geometry instanceof Point ) {
                                continue;
                            }

                            tabs.addTab( feature.getId(), new GeometryStatisticsPanel( geometry ) );
                        }

                        tabs.addTab( get( "$MD11058" ), new GeometryStatisticsPanel( null ) );
                    }

                    PanelDialog dlg = new PanelDialog( tabs, false );
                    dlg.setModal( false );
                    dlg.setVisible( true );
                } else {
                    DialogFactory.openInformationDialog( "application", getViewForm(), get( "$MD10584" ),
                                                         get( "$DI10018" ) );
                }
            } catch ( Exception e ) {
                LOG.logError( "Unknown error", e );
            }
        }
    }

    /**
     * 
     */
    public void addOrUpdateAreaAndLength() {
        if ( appContainer instanceof DeegreeDesktop || getViewForm() instanceof Component ) {
            MapModel mm = appContainer.getMapModel( null );
            try {
                List<Layer> layers = mm.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
                List<AnalysisFunction> funs = new LinkedList<AnalysisFunction>();
                funs.add( new LengthFunction( appContainer.getCommandProcessor() ) );
                funs.add( new AreaFunction( appContainer.getCommandProcessor() ) );
                AnalysisPanel panel = new AnalysisPanel( layers, funs );
                PanelDialog dlg = PanelDialog.create( appContainer.getMainWndow(), panel, true );
                dlg.setTitle( getName() );
                dlg.setVisible( true );
                if ( dlg.clickedOk ) {
                    AnalysisFunction fun = (AnalysisFunction) panel.functionToCalculate.getSelectedItem();
                    if ( panel.existingProperty.isSelected() ) {
                        fun.apply( panel.propertyMap.get( panel.propertyBox.getSelectedItem() ), layers );
                    } else {
                        LinkedList<PropertyType> list = copyLayersAndAddAttribute( layers, panel.newPropName.getText() );
                        fun.apply( list, layers );
                    }
                }
            } catch ( AnalysisPanelException e ) {
                DialogFactory.openErrorDialog( appContainer.getViewPlatform(), (Component) getViewForm(),
                                               e.getLocalizedMessage(), get( "$DI10017" ), e );
            } catch ( AnalysisFunctionException e ) {
                DialogFactory.openErrorDialog( appContainer.getViewPlatform(), (Component) getViewForm(),
                                               e.getLocalizedMessage(), get( "$DI10017" ), e );
            }
        }
    }

    private LinkedList<PropertyType> copyLayersAndAddAttribute( List<Layer> layers, String name ) {
        LinkedList<PropertyType> pts = new LinkedList<PropertyType>();
        for ( Layer l : layers ) {
            FeatureAdapter adapter = null;
            inner: for ( DataAccessAdapter a : l.getDataAccess() ) {
                if ( a instanceof FeatureAdapter ) {
                    adapter = (FeatureAdapter) a;
                    break inner;
                }
            }

            if ( adapter == null ) {
                continue;
            }

            FeatureType fs = adapter.getSchema();
            URI ns = fs.getNameSpace();

            QualifiedName qn = new QualifiedName( fs.getName().getPrefix(), name, ns );
            if ( fs.getProperty( qn ) != null ) {
                LOG.logDebug( "Schema for layer " + l.getTitle() + " already contains an attribute named " + name );
            } else {
                List<PropertyType> props = new LinkedList<PropertyType>( Arrays.asList( fs.getProperties() ) );
                pts.add( FeatureFactory.createSimplePropertyType( qn, VARCHAR, true ) );
                props.add( pts.getLast() ); // TODO create as varchar always? double?
                fs = FeatureFactory.createFeatureType( fs.getName(), false,
                                                       props.toArray( new PropertyType[props.size()] ) );
            }

            FeatureCollection fc = adapter.getFeatureCollection();
            for ( int i = 0; i < fc.size(); ++i ) {
                fc.getFeature( i ).setFeatureType( fs );
            }

            Datasource oldDatasource = adapter.getDatasource();
            MemoryDatasourceType dsType = new MemoryDatasourceType();
            dsType.setName( UUID.randomUUID().toString() );
            Datasource newDatasource = DataAccessFactory.createDatasource( oldDatasource.getName(), fc );
            newDatasource.setCache( oldDatasource.getCache() );
            newDatasource.setAuthenticationInformation( oldDatasource.getAuthenticationInformation() );
            l.removeDatasource( oldDatasource );
            l.addDatasource( newDatasource );

            try {
                appContainer.getCommandProcessor().executeSychronously( new RefreshFeatureInfoCommand(), true );
            } catch ( Exception e ) {
                LOG.logError( "Unknown error", e );
            }

        }

        return pts;
    }

}
