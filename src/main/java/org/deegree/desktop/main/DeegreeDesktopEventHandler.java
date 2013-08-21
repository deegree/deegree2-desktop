//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2012 by:
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
package org.deegree.desktop.main;

import static org.deegree.desktop.Version.getVersionNumber;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.commands.model.AddFileLayerCommand;
import org.deegree.desktop.config.Util;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.io.FileSystemAccess;
import org.deegree.desktop.io.FileSystemAccessFactory;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.modules.IModule;
import org.deegree.desktop.modules.ModuleCreator;
import org.deegree.desktop.settings.ProjectTemplates;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.desktop.views.HelpManager;
import org.deegree.desktop.views.swing.CursorRegistry;
import org.deegree.desktop.views.swing.HelpFrame;
import org.deegree.desktop.views.swing.SplashWindow;
import org.deegree.desktop.views.swing.monitor.CommandMonitorFrame;
import org.deegree.desktop.views.swing.util.GenericFileChooser;
import org.deegree.desktop.views.swing.util.DesktopFileFilter;
import org.deegree.desktop.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;
import org.deegree.framework.log.DeegreeDesktopAppender;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.HttpUtils;
import org.deegree.framework.util.KVP2Map;
import org.deegree.framework.util.StringTools;
import org.deegree.desktop.config.MapModelType;
import org.deegree.desktop.config.ModuleRegisterType;
import org.deegree.io.shpapi.ShapeFile;
import org.deegree.model.coverage.grid.WorldFile;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.feature.GMLFeatureCollectionDocument;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.PlasticTheme;

/**
 * handler class for events produced by {@link DeegreeDesktop} mein application
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class DeegreeDesktopEventHandler {

    private static final ILogger LOG = LoggerFactory.getLogger( DeegreeDesktopEventHandler.class );

    static void actionPerformed( ApplicationContainer<Container> desktop, ActionEvent event ) {
        Container container = desktop.getMainWndow();
        String eventName = null;
        if ( event.getSource() instanceof JMenuItem ) {
            eventName = ( (JMenuItem) event.getSource() ).getName();
        } else if ( event.getSource() instanceof JButton ) {
            eventName = ( (JButton) event.getSource() ).getName();
        } else {
        	LOG.logError ( "could not determine name of event " + event );
        	return;
        }
        if ( eventName.startsWith( "$class:" ) ) {
            // if a event name starts with '$class:' the event name contains name of class
            // and a static method that shall be invoked. E.g.:
            // $class:de.lat-lon.igeodesktop.example.EventHandler#test
            DeegreeDesktopEventHandler.invokeStaticEventHandler( desktop, eventName );
        } else if ( eventName.startsWith( "$module:" ) ) {
            // if a event name starts with '$module:' the event name contains the name
            // of a registered module (or ApplicationContainer) and a method that shall be
            // invoked. E.g.: $module:MyModule#test or $module:ApplicationContainer#test
            DeegreeDesktopEventHandler.invokeModuleEventHandler( desktop, eventName );
        } else if ( "newproject".equals( eventName ) ) {
            DeegreeDesktopEventHandler.openNewProject( desktop );
        } else if ( "newprojectbyfile".equals( eventName ) ) {
            DeegreeDesktopEventHandler.createNewProjectByDataFile( desktop );
        } else if ( "open".equals( eventName ) ) {
            DeegreeDesktopEventHandler.loadProject( desktop );
        } else if ( "undo".equals( eventName ) ) {
            try {
                desktop.getCommandProcessor().undo();
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                throw new RuntimeException( Messages.getMessage( container.getLocale(), "$DG10047" ) );
            }
        } else if ( "redo".equals( eventName ) ) {
            try {
                desktop.getCommandProcessor().redo();
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                throw new RuntimeException( Messages.getMessage( container.getLocale(), "$DG10048" ) );
            }
        } else if ( "save".equals( eventName ) ) {
            try {
                if ( desktop.isNew() ) {
                    DeegreeDesktopEventHandler.saveProject( desktop );
                } else {
                    DeegreeDesktopEventHandler.saveProject( desktop, null );
                }
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                throw new RuntimeException( e );
            }
        } else if ( "saveas".equals( eventName ) ) {
            try {
                DeegreeDesktopEventHandler.saveProject( desktop );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                throw new RuntimeException( Messages.getMessage( container.getLocale(), "$DG10069", eventName,
                                                                 e.getMessage() ) );
            }
        } else if ( "orderWindows".equals( eventName ) ) {
            Frame[] frames = Frame.getFrames();
            for ( Frame fr : frames ) {
                if ( !"deegreeDesktop".equals( fr.getName() ) ) {
                    fr.toFront();
                }
            }
        } else if ( "closeWindows".equals( eventName ) ) {
            Frame[] frames = Frame.getFrames();
            for ( Frame fr : frames ) {
                if ( !"deegreeDesktop".equals( fr.getName() ) ) {
                    fr.dispose();
                }
            }
        } else if ( "openLogger".equals( eventName ) ) {
            DeegreeDesktopAppender.show();
        } else if ( "login".equals( eventName ) ) {
            new LoginDialog( desktop );
        } else if ( "logout".equals( eventName ) ) {
            desktop.logout();
        } else if ( "addMapModel".equals( eventName ) ) {
            DeegreeDesktopEventHandler.addMapModel( desktop );
        } else if ( "removeMapModel".equals( eventName ) ) {
            DeegreeDesktopEventHandler.removeMapModel( desktop );
        } else if ( "openMapModel".equals( eventName ) ) {
            DeegreeDesktopEventHandler.openMapModel( desktop );
        } else if ( "setLookAndFeel".equals( eventName ) ) {
            LookAndFeelDialog laf = new LookAndFeelDialog( container );
            try {
                if ( laf.confirmed() ) {
                    String look = laf.getLookAndfeel();
                    Class<?> clzz = laf.getTheme();
                    if ( clzz != null ) {
                        PlasticLookAndFeel cl = (PlasticLookAndFeel) Class.forName( look ).newInstance();
                        PlasticLookAndFeel.setPlasticTheme( (PlasticTheme) clzz.newInstance() );
                        UIManager.setLookAndFeel( cl );
                    } else {
                        UIManager.setLookAndFeel( look );
                    }
                }
                SwingUtilities.updateComponentTreeUI( container );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage() );
            }
        } else if ( "monitorCommands".equals( eventName ) ) {
            new CommandMonitorFrame( desktop );
        } else if ( "deegreeDesktop:open help".equals( eventName ) ) {
            HelpFrame hf = HelpFrame.getInstance( new HelpManager( desktop ) );
            hf.setVisible( true );
        } else if ( "deegreeDesktop:about".equals( eventName ) ) {
            new VersionDialog();
        }
    }

    /**
     * predefined templates for new projects are located in directory org/deegree/igeo/desktop/templates. Each template
     * consists of a project file and a preview jpeg image. Both files must have the same name with different extensions
     * (prj and jpg).
     * 
     */
    static void openNewProject( ApplicationContainer<Container> desktop ) {
        desktop.cleanUp();
        ProjectTemplates templates = desktop.getSettings().getProjectTemplates();
        Map<String, String[]> map = templates.getTemplates();
        NewProjectSelectionDialog.ProjectTemplate[] prjt = new NewProjectSelectionDialog.ProjectTemplate[map.size()];
        Iterator<String> iter = map.keySet().iterator();
        int i = 0;
        while ( iter.hasNext() ) {
            String name = iter.next();
            String[] value = map.get( name );
            try {
                URL preView = new File( value[0] + ".jpg" ).toURI().toURL();
                URL prjURL = new File( value[0] + ".tmpl" ).toURI().toURL();
                prjt[i++] = new NewProjectSelectionDialog.ProjectTemplate( name, preView, prjURL );
            } catch ( MalformedURLException e ) {
                LOG.logError( e.getMessage(), e );
            }
        }
        NewProjectSelectionDialog inst = new NewProjectSelectionDialog( desktop, prjt );
        if ( inst.getSelection() != null ) {
            try {
                desktop.init();
                desktop.getMainWndow().setCursor( CursorRegistry.WAIT_CURSOR );
                desktop.loadProject( inst.getSelection(), true );
                // set crs for new project to EPSG:4326
                desktop.getMapModelCollection().setCoordinateSystem( CRSFactory.create( "EPSG:4326" ) );
                // set target bounding box for new project
                desktop.getMapModelCollection().setMaxExtent( inst.getTargetBoundingBox() );
                List<MapModel> models = desktop.getMapModelCollection().getMapModels();
                for ( MapModel mapModel : models ) {
                    mapModel.setEnvelope( inst.getTargetBoundingBox() );
                    mapModel.setMaxExtent( inst.getTargetBoundingBox() );
                }
                if ( inst.getProjectCRS() != null ) {
                    // set selected CRS for new project
                    String crsName = inst.getProjectCRS();
                    CoordinateSystem crs = null;
                    if ( "no CRS".equals( crsName ) ) {
                        crs = CRSFactory.createDummyCRS( inst.getProjectCRS() );
                    } else {
                        crs = CRSFactory.create( inst.getProjectCRS() );
                    }
                    desktop.getMapModelCollection().setCoordinateSystem( crs );
                }
                desktop.paint();
                HelpFrame.reset();
                if ( desktop.getMainWndow() instanceof Frame ) {
                    ( (Frame) desktop.getMainWndow() ).setTitle( "new project" );
                }
                desktop.getMainWndow().setCursor( CursorRegistry.DEFAULT_CURSOR );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                DialogFactory.openErrorDialog( "application", desktop.getMainWndow(), "can not create new project",
                                               "error", e );
            }
        }
    }

    /**
     * 
     * @param desktop
     */
    static void loadProject( final ApplicationContainer<Container> desktop ) {
        desktop.cleanUp();
        final Container frame = desktop.getMainWndow();
        final Preferences prefs = Preferences.userNodeForPackage( DeegreeDesktop.class );
        final File file = GenericFileChooser.showOpenDialog( FILECHOOSERTYPE.project, desktop, frame, prefs,
                                                             "projectFile", DesktopFileFilter.PRJ );
        if ( file != null ) {
            try {
                JLabel label = new JLabel( new ImageIcon( DeegreeDesktop.class.getResource( "igeodesktop.jpg" ) ) );
                label.setSize( 420, 303 );
                final SplashWindow processMonitor = new SplashWindow( label );
                desktop.setProcessMonitor( processMonitor );
                int x = frame.getX() + frame.getWidth() / 2 - label.getWidth() / 2;
                int y = frame.getY() + frame.getHeight() / 2 - label.getHeight() / 2;
                processMonitor.setBounds( x, y, label.getWidth(), label.getHeight() );
                processMonitor.setVisible( true );
                processMonitor.setCursor( CursorRegistry.WAIT_CURSOR );
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            desktop.init();
                            frame.setCursor( CursorRegistry.WAIT_CURSOR );
                            prefs.put( "projectFile" + getVersionNumber(), file.toString() );
                            FileSystemAccessFactory fsaf = FileSystemAccessFactory.getInstance( desktop );
                            FileSystemAccess fsa = fsaf.getFileSystemAccess( FILECHOOSERTYPE.project );
                            desktop.getCommandProcessor().clear();
                            desktop.loadProject( fsa.getFileURL( file.getAbsolutePath() ), false );
                            desktop.paint();
                            HelpFrame.reset();
                            desktop.resizeToolbar();
                            frame.setCursor( CursorRegistry.DEFAULT_CURSOR );
                        } catch ( Exception e ) {
                            LOG.logError( e.getMessage(), e );
                            DialogFactory.openErrorDialog( "Application", frame,
                                                           "can not read project: " + e.getMessage(),
                                                           "error reading project", e );
                            processMonitor.dispose();
                        }
                    }
                }.start();
            } catch ( Exception e ) {
                String msg = e.getMessage();
                if ( msg == null || msg.trim().length() == 0 ) {
                    msg = e.getClass().getName();
                }
                DialogFactory.openErrorDialog( "application", frame, msg, "error", e );
                LOG.logError( e.getMessage(), e );
            }
        }
    }

    /**
     * 
     * @param desktop
     * @throws IOException
     * @throws JAXBException
     */
    static void saveProject( ApplicationContainer<Container> desktop )
                            throws IOException, JAXBException {
        final Preferences prefs = Preferences.userNodeForPackage( DeegreeDesktop.class );

        File file = GenericFileChooser.showSaveDialog( FILECHOOSERTYPE.project, desktop, desktop.getMainWndow(), prefs,
                                                       "projectFile", DesktopFileFilter.PRJ );

        if ( file != null ) {
            if ( file.getParent() != null ) {
                prefs.put( "projectFile" + getVersionNumber(), file.getParent() );
            }
            LOG.logInfo( "save to: ", file );
            saveProject( desktop, file );
        }
        desktop.getCommandProcessor().clearCommands();
    }

    /**
     * saves (marshalls) a project into the passed <code>OutputStream</code>
     * 
     * @param desktop
     * @param file
     *            if <code>null</code> project will be written to its source file
     * @throws IOException
     * @throws JAXBException
     */
    static void saveProject( ApplicationContainer<Container> desktop, File file )
                            throws IOException, JAXBException {
        if ( file == null ) {
            file = new File( new URL( desktop.getProjectURL() ).getFile() );
        } else {
            desktop.setProjectURL( file.toURI().toURL() );
        }
        // get configuration information from current MapModelCollection and
        // update Configuration object

        ByteArrayOutputStream bos = new ByteArrayOutputStream( 100000 );
        FileSystemAccessFactory fsaf = FileSystemAccessFactory.getInstance( desktop );
        FileSystemAccess fsa = null;
        try {
            fsa = fsaf.getFileSystemAccess( FILECHOOSERTYPE.project );
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        JAXBContext jc = JAXBContext.newInstance( "org.deegree.deegree.config" );
        Marshaller m = jc.createMarshaller();
        m.marshal( desktop.getProject(), bos );
        ByteArrayInputStream bis = new ByteArrayInputStream( bos.toByteArray() );
        Map<String, String> map = KVP2Map.toMap( file.getPath() );
        if ( fsa != null) {
	        if ( map.get( "FILE" ) != null && !file.isAbsolute() ) {
	            LOG.logInfo( "save to: ", map.get( "FILE" ) );
	            fsa.writeFile( new File( map.get( "FILE" ) ), bis );
	        } else {
	            LOG.logInfo( "save to ", file );
	            fsa.writeFile( file, bis );
	        }
        } else {
        	LOG.logWarning("file " + file + " could not be safed.");
        }
        desktop.setNew( false );
    }

    /**
     * 
     * @param desktop
     */
    static void addMapModel( ApplicationContainer<Container> desktop ) {
        // first a new map model must be created and registered to deegreeDesktop and JAXB classes
        String id = JOptionPane.showInputDialog( Messages.getMessage( desktop.getMainWndow().getLocale(), "$DI10072" ),
                                                 "ID " + System.currentTimeMillis() );
        addMapModel( desktop, id );
    }

    public static void addMapModel( ApplicationContainer<Container> desktop, String id ) {
        try {
            // first a new map model must be created and registered to deegreeDesktop and JAXB classes
            if ( id == null || id.trim().length() == 0 ) {
                id = "ID " + System.currentTimeMillis();
            }
            MapModelType mm = Util.createMapModel( id, desktop.getMapModelCollection().getMaxExtent() );
            desktop.getProject().getMapModelCollection().getMapModel().add( mm );
            ModuleCreator<Container> mc = new ModuleCreator<Container>( desktop, new URL( desktop.getProjectURL() ) );
            List<MapModelType> mmList = new ArrayList<MapModelType>();
            mmList.add( mm );
            MapModel newMM = mc.convertMapModel( mmList, null ).get( 0 );
            desktop.getMapModelCollection().addMapModel( newMM );

            // a new (map) module must be wrapped by a module register before it can be
            // added to current project (JAXB class)
            ModuleRegisterType mrt = Util.createMapModule( desktop, mm.getIdentifier().getValue() );
            desktop.getProject().getView().getModuleRegister().add( mrt );

            IModule<Container> dmm = mc.createModule( mrt.getModule().getValue(),
                                                      mrt.get_ComponentPosition().getValue(), null, null );
            desktop.getModules().add( dmm );
            List<IModule<Container>> mList = new ArrayList<IModule<Container>>();
            mList.add( dmm );
            // create GUI representation
            desktop.appendModules( mList, desktop.getRootTargetPane() );
            // register tool bar entries/listeners
            desktop.addToolBarEntries( dmm );
            // register menu bar/item entries/listeners
            Iterator<String> iterator = desktop.getMenuItems().keySet().iterator();
            while ( iterator.hasNext() ) {
                String tmp = iterator.next();
                String s = StringTools.toArray( tmp, "|", false )[0];
                if ( s.equals( dmm.getClass().getName() ) ) {
                    AbstractButton menuItem = desktop.getMenuItems().get( tmp );
                    menuItem.addActionListener( dmm );
                }
            }
            if ( dmm.getViewForm() instanceof JInternalFrame ) {
                ( (JInternalFrame) dmm.getViewForm() ).toFront();
            }
        } catch ( Exception e ) {
            LOG.logError( e );
            DialogFactory.openErrorDialog( desktop.getViewPlatform(), desktop.getMainWndow(), Messages.get( "$MD11774" ),
                                           Messages.get( "$MD11775" ), e );
        }
    }

    /**
     * 
     * @param desktop
     */
    static void removeMapModel( ApplicationContainer<Container> desktop ) {
        if ( desktop.getMapModelCollection().getMapModels().size() == 1 ) {
            DialogFactory.openWarningDialog( "application", desktop.getMainWndow(), Messages.get( "$DI10037" ),
                                             Messages.get( "$DI10038" ) );
        } else {
            desktop.getMapModelCollection().removeMapModel( desktop.getMapModel( null ) );
            IModule<Container> mapModule = desktop.getActiveMapModule();
            desktop.removeModule( mapModule );
            try {
                Object view = mapModule.getViewForm();
                if ( view != null ) {
                    Method m = view.getClass().getMethod( "dispose", new Class<?>[0] );
                    if ( m != null ) {
                        // close view if it's a frame or an internal frame
                        m.invoke( mapModule.getViewForm(), new Object[0] );
                    } else {
                        // otherwise remove it from its parent
                        Container cont = ( (JComponent) view ).getParent();
                        ( (JComponent) view ).getParent().remove( (JComponent) view );
                        cont.repaint();
                    }
                }
            } catch ( Exception e ) {
                LOG.logWarning( e.getMessage() );
            }
            if ( desktop.getMapModelCollection().getMapModels().size() > 0 ) {
                desktop.setActiveMapModel( desktop.getMapModelCollection().getMapModels().get( 0 ) );
            }
        }
    }

    /**
     * 
     * @param desktop
     * @throws Exception
     */
    static void openMapModel( ApplicationContainer<Container> desktop ) {
        try {
            // first find all map models that are not assigned to a map module
            List<MapModel> mapModels = desktop.getMapModelCollection().getMapModels();
            List<IModule<Container>> mapModules = desktop.findModuleByName( "MapModule" );
            List<MapModel> unusedMM = new ArrayList<MapModel>();
            for ( MapModel mapModel : mapModels ) {
                boolean flag = false;
                for ( IModule<Container> module : mapModules ) {
                    if ( module.getInitParameter( "assignedMapModel" ).equals( mapModel.getIdentifier().getValue() ) ) {
                        flag = true;
                        break;
                    }
                }
                if ( !flag ) {
                    unusedMM.add( mapModel );
                }
            }
            if ( unusedMM.size() > 0 ) {
                // create a new map module for selected map model
                MapModelDialog mmd = new MapModelDialog( desktop.getMainWndow(),
                                                         unusedMM.toArray( new MapModel[unusedMM.size()] ) );
                MapModel model = mmd.getMapModel();
                if ( model != null ) {
                    // a new (map) module must be wrapped by a module register before it can be
                    // added to current project (JAXB class)
                    ModuleRegisterType mrt = Util.createMapModule( desktop, model.getIdentifier().getValue() );
                    desktop.getProject().getView().getModuleRegister().add( mrt );

                    ModuleCreator<Container> mc = new ModuleCreator<Container>( desktop, new URL( desktop.getProjectURL() ) );
                    IModule<Container> dmm = mc.createModule( mrt.getModule().getValue(),
                                                              mrt.get_ComponentPosition().getValue(), null, null );
                    desktop.getModules().add( dmm );
                    List<IModule<Container>> mList = new ArrayList<IModule<Container>>();
                    mList.add( dmm );
                    // create GUI representation
                    desktop.appendModules( mList, desktop.getRootTargetPane() );
                    // register tool bar entries/listeners
                    desktop.addToolBarEntries( dmm );
                    // register menu bar/item entries/listeners
                    Iterator<String> iterator = desktop.getMenuItems().keySet().iterator();
                    while ( iterator.hasNext() ) {
                        String tmp = (String) iterator.next();
                        String s = StringTools.toArray( tmp, "|", false )[0];
                        if ( s.equals( dmm.getClass().getName() ) ) {
                            AbstractButton menuItem = desktop.getMenuItems().get( tmp );
                            menuItem.addActionListener( (ActionListener) dmm );
                        }
                    }
                }
            } else {
                // nothing to do
                DialogFactory.openWarningDialog( desktop.getViewPlatform(), desktop.getMainWndow(),
                                                 Messages.get( "$DI10039" ), Messages.get( "$DI10040" ) );
            }

        } catch ( Exception e ) {
            LOG.logError( e );
            DialogFactory.openErrorDialog( desktop.getViewPlatform(), desktop.getMainWndow(), Messages.get( "$MD11773" ),
                                           Messages.get( "$MD11774" ), e );
        }
    }

    /**
     * if a event name starts with '$class:' the event name contains name of class and a static method that shall be
     * invoked. E.g.: $class:de.lat-lon.igeodesktop.example.EventHandler#test
     * 
     * @param desktop
     * @param action
     */
    static void invokeStaticEventHandler( ApplicationContainer<Container> desktop, String action ) {
        try {
            String[] tmp = StringTools.toArray( action, ":#", false );
            Class<?> clzz = Class.forName( tmp[1] );
            Class<?>[] varTypes = new Class[] { ApplicationContainer.class };
            Method method = clzz.getDeclaredMethod( tmp[2], varTypes );
            Object[] var = new Object[] { desktop };
            method.invoke( null, var );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            Container parent = desktop.getMainWndow();
            DialogFactory.openWarningDialog( desktop.getViewPlatform(), parent,
                                             Messages.getMessage( parent.getLocale(), "$DI10011", action ),
                                             Messages.getMessage( parent.getLocale(), "$DI10012" ) );
        }
    }

    /**
     * @param desktop
     * @param actionName
     */
    static void invokeModuleEventHandler( ApplicationContainer<Container> desktop, String actionName ) {
        try {
            String[] tmp = StringTools.toArray( actionName, ":#", false );
            if ( "ApplicationContainer".equals( tmp[1] ) ) {
                Class<?> clzz = desktop.getClass();
                Method method = clzz.getDeclaredMethod( tmp[2], (Class[]) null );
                method.invoke( desktop, (Object[]) null );
            } else {
                List<IModule<Container>> modules = desktop.findModuleByName( tmp[1] );
                for ( IModule<Container> module : modules ) {
                    Class<?> clzz = module.getClass();
                    Method method = clzz.getDeclaredMethod( tmp[2], (Class[]) null );
                    method.invoke( module, (Object[]) null );
                }
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            Container parent = desktop.getMainWndow();
            DialogFactory.openWarningDialog( desktop.getViewPlatform(), parent,
                                             Messages.getMessage( parent.getLocale(), "$DI10013", actionName ),
                                             Messages.getMessage( parent.getLocale(), "$DI10014" ) );
        }

    }

    /**
     * creates a new deegreeDesktop project from loading a data file (shape or GML)
     * 
     * @param desktop
     */
    static void createNewProjectByDataFile( ApplicationContainer<Container> desktop ) {
        final Container parent = desktop.getMainWndow();
        try {
            URL url = desktop.resolve( "./resources/templates/empty_innerframelayout.tmpl" );
            try {
                HttpUtils.validateURL( url.toExternalForm() );
            } catch ( Exception e ) {
                url = desktop.resolve( "../resources/templates/empty_innerframelayout.tmpl" );
            }

            desktop.cleanUp();
            Preferences prefs = Preferences.userNodeForPackage( ApplicationContainer.class );
            String[] s = StringTools.toArray( Messages.getMessage( parent.getLocale(), "$MD11767" ), "|", false );
            File file = GenericFileChooser.showOpenDialog( FILECHOOSERTYPE.project, desktop, parent, prefs, "geoDataFile",
                                                           DesktopFileFilter.createForExtensions( s ) );
            if ( file != null ) {
                Envelope bbox = getBBox( file );
                if ( bbox == null ) {
                    DialogFactory.openInformationDialog( desktop.getViewPlatform(), parent,
                                                         Messages.getMessage( parent.getLocale(), "$MD11768" ),
                                                         Messages.getMessage( parent.getLocale(), "$MD11769" ) );
                    return;
                }

                parent.setCursor( CursorRegistry.WAIT_CURSOR );
                bbox = bbox.getBuffer( Math.pow( bbox.getHeight() * bbox.getHeight() + bbox.getWidth()
                                                                         * bbox.getWidth(), 0.5 ) );

                desktop.init();
                SwingUtilities.updateComponentTreeUI( desktop.getMainWndow() );
                desktop.loadProject( url, true );
                // set target bounding box for new project
                desktop.getMapModelCollection().setMaxExtent( bbox );
                List<MapModel> models = desktop.getMapModelCollection().getMapModels();
                for ( MapModel mapModel : models ) {
                    mapModel.setEnvelope( bbox );
                    mapModel.setMaxExtent( bbox );
                }
                // set selected CRS for new project
                desktop.getMapModelCollection().setCoordinateSystem( bbox.getCoordinateSystem() );
                AddFileLayerCommand cmd = new AddFileLayerCommand( desktop.getMapModel( null ), file, file.getName(),
                                                                   file.getName(), file.getName(),
                                                                   bbox.getCoordinateSystem().getPrefixedName() );
                desktop.getCommandProcessor().executeSychronously( cmd, true );
                desktop.paint();
                HelpFrame.reset();
                if ( parent instanceof Frame ) {
                    ( (Frame) parent ).setTitle( Messages.getMessage( parent.getLocale(), "$MD11770" ) );
                }
                parent.setCursor( CursorRegistry.DEFAULT_CURSOR );
            }
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openErrorDialog( "application", parent,
                                           Messages.getMessage( parent.getLocale(), "$MD11771" ),
                                           Messages.getMessage( parent.getLocale(), "$MD11772" ), e );
        }

    }

    /**
     * @return Envelope
     * @throws IOException
     */
    private static Envelope getBBox( File file )
                            throws Exception {
        Envelope env = null;
        String fileName = file.getAbsolutePath();
        String tmp = fileName.toLowerCase();
        if ( tmp.endsWith( ".shp" ) ) {
            ShapeFile sf = new ShapeFile( fileName.substring( 0, fileName.length() - 4 ) );
            env = sf.getFileMBR();
        } else if ( tmp.endsWith( ".xml" ) || tmp.endsWith( ".gml" ) ) {
            GMLFeatureCollectionDocument doc = new GMLFeatureCollectionDocument();
            doc.load( file.toURI().toURL() );
            env = doc.parse().getBoundedBy();
        } else if ( tmp.endsWith( ".gif" ) || tmp.endsWith( ".bmp" ) || tmp.endsWith( ".png" ) || tmp.endsWith( ".jpg" )
                    || tmp.endsWith( ".jpeg" ) || tmp.endsWith( ".tif" ) || tmp.endsWith( ".tiff" ) ) {
            env = WorldFile.readWorldFile( fileName, WorldFile.TYPE.CENTER ).getEnvelope();
        } else {
        	// env is null
        	return null;
        }
        CoordinateSystem crs = null;
        if ( env.getCoordinateSystem() == null ) {
            CRSChooserDialog crsChosser = new CRSChooserDialog();
            crs = crsChosser.getSelectedCRS();
            if ( crs == null ) {
                return null;
            }
        } else {
            crs = env.getCoordinateSystem();
        }
        return GeometryFactory.createEnvelope( env.getMin(), env.getMax(), crs );
    }

}
