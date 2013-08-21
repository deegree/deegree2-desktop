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
package org.deegree.desktop.views.swing.legend;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.ChangeListener;
import org.deegree.desktop.ValueChangedEvent;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.mapmodel.LayerGroup;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.mapmodel.MapModelChangedEvent;
import org.deegree.desktop.mapmodel.MapModelVisitor;
import org.deegree.desktop.views.swing.DefaultPanel;
import org.deegree.desktop.views.swing.util.GenericFileChooser;
import org.deegree.desktop.views.swing.util.DesktopFileFilter;
import org.deegree.desktop.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.ImageUtils;
import org.deegree.desktop.config.ViewFormType;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class LegendPanel extends DefaultPanel implements ChangeListener, MapModelVisitor {

    private static final long serialVersionUID = 6785041996873381154L;

    private static final ILogger LOG = LoggerFactory.getLogger( LegendPanel.class );

    private JScrollPane sc;

    private MapModel mapModel;

    private ApplicationContainer<Container> appContainer;

    private JTree tree;

    private DefaultMutableTreeNode root;

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#init(org.deegree.igeo.config.ViewFormType)
     */
    public void init( ViewFormType viewForm )
                            throws Exception {
        this.appContainer = owner.getApplicationContainer();
        update();
    }

    @Override
    public void update() {
        super.update();
        MapModel tmp = appContainer.getMapModel( null );
        if ( this.mapModel == null || !tmp.equals( this.mapModel ) ) {
            this.mapModel = tmp;
            this.mapModel.removeChangeListener( this );
            this.mapModel.addChangeListener( this );
            initGUI();
        }
    }

    private void initGUI() {
        try {
            if ( sc != null ) {
                // remove pane
                this.remove( sc );
            }
            setLayout( new BorderLayout() );
            root = new DefaultMutableTreeNode( appContainer.getMapModel( null ).getName() );
            tree = new JTree( root );
            tree.setRowHeight( 0 );
            tree.setCellRenderer( new LegendTreeCellRenderer() );
            sc = new JScrollPane( tree );
            this.add( sc, BorderLayout.CENTER );
            mapModel.walkLayerTree( this );
            tree.expandRow( 0 );
            tree.expandRow( 1 );
            tree.addMouseListener( new MouseAdapter() {

                @Override
                public void mouseReleased( MouseEvent e ) {
                    if ( e.isPopupTrigger() ) {
                        popup.show( e.getComponent(), e.getX(), e.getY() );
                    } else {
                        File file = GenericFileChooser.showOpenDialog(
                                                                       FILECHOOSERTYPE.image,
                                                                       appContainer,
                                                                       LegendPanel.this,
                                                                       Preferences.systemNodeForPackage( LegendPanel.class ),
                                                                       "LOCATION", DesktopFileFilter.IMAGES );
                        if ( file != null ) {
                            try {
                                BufferedImage img = ImageUtils.loadImage( file );
                                TreePath path = tree.getPathForLocation( e.getX(), e.getY() );
                                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                                Layer layer = (Layer) node.getUserObject(); 
                                layer.getCurrentStyle().setLegentImage( img );
                                SwingUtilities.updateComponentTreeUI( LegendPanel.this );
                            } catch ( IOException e1 ) {
                                LOG.logError( e1 );
                            }                            
                        }
                    }
                }
            } );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.mapmodel.MapModelVisitor#visit(org.deegree.igeo.mapmodel.Layer)
     */
    public void visit( Layer layer )
                            throws Exception {
        if ( layer.isVisible() ) {
            MutableTreeNode node = new DefaultMutableTreeNode();
            node.setUserObject( layer );
            root.add( node );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.mapmodel.MapModelVisitor#visit(org.deegree.igeo.mapmodel.LayerGroup)
     */
    public void visit( LayerGroup layerGroup )
                            throws Exception {
        // nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.ChangeListener#valueChanged(org.deegree.igeo.ValueChangedEvent)
     */
    public void valueChanged( ValueChangedEvent event ) {

        // If the row height is 0 and the height of a row has dynamically changed, it is necessary
        // to flush the internal cache of row heights. The following calls flush the internal cache.
        if ( tree.getRowHeight() <= 0 ) {
            // Temporary change to non-zero height
            tree.setRowHeight( 1 );
        }
        tree.setRowHeight( 0 );
        root = new DefaultMutableTreeNode( appContainer.getMapModel( null ).getName() );
        DefaultTreeModel model = ( (DefaultTreeModel) tree.getModel() );
        model.setRoot( root );
        tree.setModel( model );

        if ( event instanceof MapModelChangedEvent ) {
            root.removeAllChildren();
            try {
                mapModel.walkLayerTree( this );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
            }
        }
        tree.expandRow( 0 );
        tree.expandRow( 1 );

    }

}
