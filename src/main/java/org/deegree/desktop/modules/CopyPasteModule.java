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

package org.deegree.desktop.modules;

import static java.awt.datatransfer.DataFlavor.stringFlavor;
import static java.util.Collections.singletonList;
import static java.util.UUID.randomUUID;
import static org.deegree.desktop.i18n.Messages.get;
import static org.deegree.framework.log.LoggerFactory.getLogger;
import static org.deegree.framework.xml.XMLFragment.DEFAULT_URL;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;

import org.deegree.datatypes.Types;
import org.deegree.desktop.commands.UnselectFeaturesCommand;
import org.deegree.desktop.commands.digitize.InsertFeatureCommand;
import org.deegree.desktop.dataadapter.DataAccessAdapter;
import org.deegree.desktop.dataadapter.DataAccessFactory;
import org.deegree.desktop.dataadapter.FeatureAdapter;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.Datasource;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.modules.ActionDescription.ACTIONTYPE;
import org.deegree.desktop.modules.DefaultMapModule.SelectedFeaturesVisitor;
import org.deegree.desktop.settings.ClipboardOptions;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.util.Pair;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.XMLException;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.desktop.config.LayerType.MetadataURL;
import org.deegree.model.Identifier;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureException;
import org.deegree.model.feature.FeatureFactory;
import org.deegree.model.feature.FeatureProperty;
import org.deegree.model.feature.GMLFeatureAdapter;
import org.deegree.model.feature.GMLFeatureCollectionDocument;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.spatialschema.Geometry;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.WKTAdapter;
import org.xml.sax.SAXException;

/**
 * Module for handling copy and paste of features from one layer to another
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * @param <T>
 */
public class CopyPasteModule<T> extends DefaultModule<T> {

    private static final ILogger LOG = getLogger( CopyPasteModule.class );

    static {
        ActionDescription ad1 = new ActionDescription( "clearClipboard", "removes all content from clip board", null,
                                                       "clear clip board", ACTIONTYPE.PushButton, null, null );
        ActionDescription ad2 = new ActionDescription( "copy",
                                                       "copies selected features as GML3 objects into clip board",
                                                       null, "copies selected features", ACTIONTYPE.PushButton, null,
                                                       null );
        ActionDescription ad3 = new ActionDescription( "copyAsWKT", "copies selected features as WKT into clip board",
                                                       null, "copies selected features as WKT", ACTIONTYPE.PushButton,
                                                       null, null );
        ActionDescription ad4 = new ActionDescription( "paste", "pastes features from clip board into selected layer",
                                                       null, "pastes features from clip board", ACTIONTYPE.PushButton,
                                                       null, null );
        moduleCapabilities = new ModuleCapabilities( ad1, ad2, ad3, ad4 );
    }

    /**
     * 
     */
    public void selectedFeaturesToLayer() {
        String mmId = getInitParameter( "assignedMapModel" );
        MapModel mm = appContainer.getMapModel( new Identifier( mmId ) );
        SelectedFeaturesVisitor visitor = new SelectedFeaturesVisitor( -1 );
        try {
            mm.walkLayerTree( visitor );
            if ( visitor.col.size() > 0 ) {
                FeatureCollection col = FeatureFactory.createFeatureCollection( "UUID_" + randomUUID().toString(),
                                                                                visitor.col.size() );
                col.setFeatureType( visitor.col.getFeature( 0 ).getFeatureType() );
                for ( int i = 0; i < visitor.col.size(); ++i ) {
                    col.add( visitor.col.getFeature( i ).cloneDeep() );
                }

                String name = DialogFactory.openInputDialog( appContainer.getViewPlatform(), getViewForm(), get( "$MD10558" ),
                                               get( "$MD10559" ) );
                if ( name == null ) {
                    return;
                }
                Datasource ds = DataAccessFactory.createDatasource( name, col );

                Layer layer = new Layer( mm, new Identifier( name ), name, name, singletonList( ds ),
                                         Collections.<MetadataURL> emptyList() );
                appContainer.getCommandProcessor().executeSychronously( new UnselectFeaturesCommand( mm, false ), true );
                Layer selLayer = mm.getLayersSelectedForAction( MapModel.SELECTION_ACTION ).get( 0 );
                mm.insert( layer, selLayer.getParent(), selLayer, false );
                layer.fireRepaintEvent();
            }

        } catch ( Exception e ) {
            LOG.logError( "Unknown error", e );
            DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11213" ),
                                           Messages.get( "$MD11214" ), e );
        }
    }

    /**
     * 
     */
    public void copyAsWKT() {
        if ( "application".equalsIgnoreCase( appContainer.getViewPlatform() ) ) {
            int max = appContainer.getSettings().getClipboardOptions().getMaxObjects();
            MapModel mm = appContainer.getMapModel( null );
            SelectedFeaturesVisitor visitor = new SelectedFeaturesVisitor( max );
            try {
                mm.walkLayerTree( visitor );
            } catch ( Exception e ) {
                LOG.logError( "Unknown error", e );
                return;
            }
            try {
                StringBuffer wkt = new StringBuffer();
                for ( int i = 0; i < visitor.col.size(); ++i ) {
                    Geometry geom = visitor.col.getFeature( i ).getDefaultGeometryPropertyValue();
                    wkt.append( WKTAdapter.export( geom ) );
                    if ( i != visitor.col.size() - 1 ) {
                        wkt.append( ";\n" );
                    }
                }

                // use both clip boards for text?
                Clipboard clip = Toolkit.getDefaultToolkit().getSystemSelection();
                if ( clip != null ) {
                    clip.setContents( new StringSelection( wkt.toString() ), null );
                }
                clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                clip.setContents( new StringSelection( wkt.toString() ), null );
            } catch ( GeometryException e ) {
                LOG.logError( "Unknown error", e );
            }
        }
    }

    /**
     * 
     */
    public void paste() {
        if ( "application".equalsIgnoreCase( appContainer.getViewPlatform() ) ) {
            MapModel mm = appContainer.getMapModel( null );
            Toolkit tk = Toolkit.getDefaultToolkit();
            if ( !addFeaturesFromClipboard( tk.getSystemClipboard(), mm ) ) {
                addFeaturesFromClipboard( tk.getSystemSelection(), mm );
            }
        }
    }

    /**
     * removes content from clipboard
     */
    public void clearClipboard() {
        if ( "application".equalsIgnoreCase( appContainer.getViewPlatform() ) ) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            tk.getSystemClipboard().setContents( new StringSelection( "" ), null );
        }
    }

    private boolean addCompleteFeaturesFromClipboard( String trans, MapModel mm ) {
        GMLFeatureCollectionDocument doc = new GMLFeatureCollectionDocument();
        try {
            doc.load( new StringReader( trans ), DEFAULT_URL );
            FeatureCollection col = doc.parse();
            doc = null; // could be freed now in case of memory problems

            boolean asked = false;
            boolean yes = false;

            for ( Layer l : mm.getLayersSelectedForAction( MapModel.SELECTION_ACTION ) ) {
                for ( int i = 0; i < col.size(); ++i ) {
                    Feature f = col.getFeature( i );
                    Pair<FeatureType, InsertFeatureCommand> pair = insertFeature( l, f );

                    if ( pair.first != null && pair.first.equals( f.getFeatureType() ) ) { // this is implemented on a
                        // name comparison basis
                        // only, but I say it's close enough...
                        try {
                            appContainer.getCommandProcessor().executeSychronously( pair.second, true );
                        } catch ( Exception e ) {
                            LOG.logError( e.getMessage(), e );
                            DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null,
                                                           Messages.get( "$MD11215" ), Messages.get( "$MD11216" ), e );
                        }
                    } else {
                        if ( ( asked && yes )
                             || ( !asked && DialogFactory.openConfirmDialogYESNO( appContainer.getViewPlatform(), getViewForm(),
                                                                    get( "$MD10555" ), get( "$DI10019" ) ) ) ) {
                            asked = true;
                            yes = true;
                            InsertFeatureCommand newFeature = insertFeature( l, f.getDefaultGeometryPropertyValue() );
                            try {
                                appContainer.getCommandProcessor().executeSychronously( newFeature, true );
                            } catch ( Exception e ) {
                                LOG.logError( e.getMessage(), e );
                                DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null,
                                                               Messages.get( "$MD11217" ), Messages.get( "$MD11218" ),
                                                               e );
                            }
                        } else {
                            asked = true;
                        }
                    }
                }
            }
        } catch ( SAXException e ) {
            // no GML either. message?
            LOG.logWarning( "no GML either.", e );
        } catch ( IOException e ) {
            // should not happen, a string is a string
            LOG.logWarning( "should not happen, a string is a string", e );
        } catch ( XMLParsingException e ) {
            // no GML either. message?
            LOG.logWarning( "no GML either.", e );
        }

        return false;

        // I'll leave this here for later use. Maybe someone'd like a better copy/paste mechanism one day.
        // // let's try it with the featuresFlavor
        // boolean inserted = false;
        //
        // try {
        // Collection<?> col = (Collection<?>) trans.getTransferData( FeaturesSelection.featuresFlavor );
        // for ( Object o : col ) {
        // if ( o instanceof Feature ) {
        // for ( Layer l : mm.getLayersSelectedForAction( MapModel.SELECTION_ACTION ) ) {
        // appContainer.getCommandProcessor().addCommand( insertFeature( l, (Feature) o ) );
        // inserted = true;
        // }
        // }
        // }
        //
        // return inserted;
        // } catch ( UnsupportedFlavorException e ) {
        // LOG.logDebug( "Stack trace: ", e );
        // // this time it's final
        // } catch ( IOException e ) {
        // LOG.logDebug( "Stack trace: ", e );
        // // probably ignore, or message for no data
        // // unsure when this will actually happen
        // }
        //
        // return false;
    }

    private boolean addFeaturesFromClipboard( Clipboard clip, MapModel mm ) {
        if ( clip == null ) {
            return false;
        }

        Transferable trans = clip.getContents( null );
        String str = null;
        try {
            str = (String) trans.getTransferData( stringFlavor );
            LOG.logDebug( "String from Clipboard: ", str );
            if ( str == null ) {
                // return addCompleteFeaturesFromClipboard( trans, mm );
                return false;
            }
            String[] s = StringTools.toArray( str, ";", false );
            for ( String string : s ) {
                if ( string != null && string.length() > 5 ) {
                    Geometry geom = WKTAdapter.wrap( string, mm.getCoordinateSystem() );

                    for ( Layer l : mm.getLayersSelectedForAction( MapModel.SELECTION_ACTION ) ) {
                        try {
                            appContainer.getCommandProcessor().executeSychronously( insertFeature( l, geom ), true );
                        } catch ( Exception e ) {
                            LOG.logError( e.getMessage(), e );
                            DialogFactory.openErrorDialog( appContainer.getViewPlatform(), null,
                                                           Messages.get( "$MD11219" ), Messages.get( "$MD11220a", l ),
                                                           e );
                        }
                    }
                }
            }

            return true;
        } catch ( UnsupportedFlavorException e ) {
            LOG.logDebug( "Stack trace: ", e );
            // return addCompleteFeaturesFromClipboard( trans, mm ); // comment this in again if the method can cope
            // with different flavors
        } catch ( IOException e ) {
            LOG.logDebug( "Stack trace: ", e );
            // probably ignore, or message for no data
            // unsure when this will actually happen
        } catch ( GeometryException e ) {
            LOG.logDebug( "Stack trace: ", e );
            return addCompleteFeaturesFromClipboard( str, mm );
        }

        return false;
    }

    private static InsertFeatureCommand insertFeature( Layer layer, Geometry geometry ) {
        for ( DataAccessAdapter adapter : layer.getDataAccess() ) {
            if ( adapter instanceof FeatureAdapter ) {
                FeatureAdapter fa = (FeatureAdapter) adapter;
                FeatureType ft = fa.getSchema();
                LinkedList<FeatureProperty> props = new LinkedList<FeatureProperty>();
                PropertyType[] pts = ft.getProperties();
                for ( PropertyType pt : pts ) {
                    if ( pt.getType() == Types.GEOMETRY ) {
                        props.add( FeatureFactory.createFeatureProperty( pt.getName(), geometry ) );
                    } else {
                        for ( int i = 0; i < pt.getMinOccurs(); ++i ) {
                            switch ( pt.getType() ) {
                            case Types.VARCHAR:
                                props.add( FeatureFactory.createFeatureProperty( pt.getName(), "string" ) );
                            case Types.INTEGER:
                                props.add( FeatureFactory.createFeatureProperty( pt.getName(), 0 ) );
                            case Types.DATE:
                                props.add( FeatureFactory.createFeatureProperty( pt.getName(), new Date() ) );
                            }
                            // TODO add more types with default values, or find a better solution to create a blank new
                            // feature
                        }
                    }
                }

                return new InsertFeatureCommand( adapter, FeatureFactory.createFeature( randomUUID().toString(), ft,
                                                                                        props ) );
            }
        }

        return null;
    }

    private static Pair<FeatureType, InsertFeatureCommand> insertFeature( Layer layer, Feature feature ) {
        for ( DataAccessAdapter adapter : layer.getDataAccess() ) {
            if ( adapter instanceof FeatureAdapter ) {

                return new Pair<FeatureType, InsertFeatureCommand>( ( (FeatureAdapter) adapter ).getSchema(),
                                                                    new InsertFeatureCommand( adapter, feature ) );
            }
        }

        return null;
    }

    /**
     * 
     */
    public void copy() {
        if ( "application".equalsIgnoreCase( appContainer.getViewPlatform() ) ) {

            ClipboardOptions opts = appContainer.getSettings().getClipboardOptions();
            String format = opts.getFormat();

            String mmId = getInitParameter( "assignedMapModel" );
            MapModel mm = appContainer.getMapModel( new Identifier( mmId ) );
            SelectedFeaturesVisitor visitor = new SelectedFeaturesVisitor( opts.getMaxObjects() );
            try {
                mm.walkLayerTree( visitor );
            } catch ( Exception e ) {
                LOG.logError( "Unknown error", e );
                return;
            }

            if ( format.equalsIgnoreCase( "text/xml; subtype=gml/3.1.1" ) ) {

                // LinkedList<Feature> features = new LinkedList<Feature>();
                // for ( int i = 0; i < visitor.col.size(); ++i ) {
                // features.add( visitor.col.getFeature( i ) );
                // }

                // format or no format?
                String doc;
                try {
                    doc = new GMLFeatureAdapter().export( visitor.col ).getAsPrettyString();

                    // use both clip boards for text?
                    Clipboard clip = Toolkit.getDefaultToolkit().getSystemSelection();
                    if ( clip != null ) {
                        clip.setContents( new StringSelection( doc ), null );
                    }
                    clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clip.setContents( new StringSelection( doc ), null );
                } catch ( XMLException e ) {
                    LOG.logError( "Unknown error", e );
                } catch ( IOException e ) {
                    LOG.logError( "Unknown error", e );
                } catch ( FeatureException e ) {
                    LOG.logError( "Unknown error", e );
                } catch ( SAXException e ) {
                    LOG.logError( "Unknown error", e );
                }
            }
        }

    }

}
