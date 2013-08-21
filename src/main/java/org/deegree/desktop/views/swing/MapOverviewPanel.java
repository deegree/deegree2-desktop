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

package org.deegree.desktop.views.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.ChangeListener;
import org.deegree.desktop.ValueChangedEvent;
import org.deegree.desktop.commands.model.ZoomCommand;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.io.FileSystemAccess;
import org.deegree.desktop.io.FileSystemAccessFactory;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.mapmodel.MapModelChangedEvent;
import org.deegree.desktop.mapmodel.MapModelChangedEvent.CHANGE_TYPE;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.desktop.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;
import org.deegree.framework.util.HttpUtils;
import org.deegree.framework.util.ImageUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.graphics.transformation.WorldToScreenTransform;
import org.deegree.desktop.config.ViewFormType;
import org.deegree.model.Identifier;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;

/**
 * Realizes an overview over the map of an assigned map module.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class MapOverviewPanel extends DefaultPanel implements ChangeListener {

    private static final long serialVersionUID = -8874103946271565149L;

    private MapModel mapModel;

    private BufferedImage mapImage;

    private JLabel map;

    private GeoTransform gt;

    private ApplicationContainer<Container> appContainer;

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#init(org.deegree.client.configuration.ViewForm)
     */
    public void init( ViewFormType viewForm )
                            throws Exception {
        this.appContainer = this.owner.getApplicationContainer();
        this.mapModel = appContainer.getMapModel( null );

        // register change listener to be informed about changes of the map model (concrete: extent
        // of the map model)
        this.mapModel.addChangeListener( this );
        setLayout( null );
        String tmp = this.owner.getInitParameter( "mapImage" );
        FileSystemAccessFactory fsaf = FileSystemAccessFactory.getInstance( appContainer );
        FileSystemAccess fsa = fsaf.getFileSystemAccess( FILECHOOSERTYPE.image );
        URL url = fsa.getFileURL( tmp );
        try {
            String s = StringTools.replace( url.toExternalForm(), " ", "%20", true );
            HttpUtils.validateURL( s );
            mapImage = ImageUtils.loadImage( new URL( s ) );
        } catch ( Exception e ) {
            e.printStackTrace();
            String s = DialogFactory.openNewReferenceDialog( appContainer, Messages.get( "$MD11201", url ),
                                                             url.toString(), true );
            if ( s != null ) {
                url = new URL( s );
                mapImage = ImageUtils.loadImage( url );
            } else {
                mapImage = new BufferedImage( 200, 200, BufferedImage.TYPE_INT_ARGB );
            }
        }

        tmp = this.owner.getInitParameter( "mapEnvelope" );
        Envelope mapEnvelope = GeometryFactory.createEnvelope( tmp, null );
        Envelope imageEnvelope = GeometryFactory.createEnvelope( 0, 0, mapImage.getWidth() - 1,
                                                                 mapImage.getHeight() - 1, null );
        gt = new WorldToScreenTransform( mapEnvelope, imageEnvelope );

        map = new JLabel( new ImageIcon( mapImage ) );
        map.setBorder( BorderFactory.createEmptyBorder() );
        map.setBounds( 10, 10, mapImage.getWidth(), mapImage.getHeight() );
        add( map );
        setMinimumSize( new Dimension( mapImage.getWidth() + 10, mapImage.getHeight() + 10 ) );
        setPreferredSize( new Dimension( mapImage.getWidth() + 10, mapImage.getHeight() + 10 ) );
        addMouseListener( new MouseHandler() );
    }

    @Override
    public void update() {
        super.update();
        String mmId = owner.getInitParameter( "assignedMapModel" );
        MapModel tmp = appContainer.getMapModel( new Identifier( mmId ) );
        if ( this.mapModel == null || !tmp.equals( this.mapModel ) ) {
            this.mapModel = tmp;
            // register change listener to be informed about changes of the map model
            // (concrete: extent of the map model)
            this.mapModel.removeChangeListener( this );
            this.mapModel.addChangeListener( this );
            repaint();
        }
    }

    @Override
    public void paint( Graphics g ) {
        paintComponent( g );
    }

    @Override
    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        map.paint( g );
        Envelope env = this.mapModel.getEnvelope();
        int x1 = (int) gt.getDestX( env.getMin().getX() );
        int y1 = (int) gt.getDestY( env.getMax().getY() );
        int x2 = (int) gt.getDestX( env.getMax().getX() );
        int y2 = (int) gt.getDestY( env.getMin().getY() );
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        if ( x2 - x1 < 15 ) {
            g.setColor( Color.WHITE );
            g2.setStroke( new BasicStroke( 3 ) );
            int cx = ( x2 - x1 ) / 2 + x1;
            int cy = ( y2 - y1 ) / 2 + y1;
            g2.drawOval( cx - 10, cy - 10, 21, 21 );
            g2.drawLine( cx, cy - 15, cx, cy + 15 );
            g2.drawLine( cx - 15, cy, cx + 15, cy );
            g.setColor( Color.BLACK );
            g2.setStroke( new BasicStroke( 1 ) );
            g2.drawOval( cx - 10, cy - 10, 21, 21 );
            g2.drawLine( cx, cy - 15, cx, cy + 15 );
            g2.drawLine( cx - 15, cy, cx + 15, cy );
        } else {
            g.setColor( Color.BLUE );
            g2.setStroke( new BasicStroke( 3 ) );
            g2.drawRect( x1, y1, x2 - x1, y2 - y1 );

        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.ChangeListener#valueChanged(org.deegree.client.presenter.ValueChangedEvent)
     */
    public void valueChanged( ValueChangedEvent event ) {
        MapModelChangedEvent mmce = (MapModelChangedEvent) event;
        if ( mmce.getChangeType() == CHANGE_TYPE.extentChanged ) {
            repaint();
        }
    }

    // ////////////////////////////////////////////////////////////////////////
    // inner classes //
    // ////////////////////////////////////////////////////////////////////////

    class MouseHandler extends MouseAdapter {

        @Override
        public void mouseClicked( MouseEvent event ) {

            double x = gt.getSourceX( event.getX() );
            double y = gt.getSourceY( event.getY() );
            Envelope env = mapModel.getEnvelope();
            double minx = x - env.getWidth() / 2d;
            double maxx = x + env.getWidth() / 2d;
            double miny = y - env.getHeight() / 2d;
            double maxy = y + env.getHeight() / 2d;
            env = GeometryFactory.createEnvelope( minx, miny, maxx, maxy, env.getCoordinateSystem() );

            ZoomCommand zoom = new ZoomCommand( mapModel );
            zoom.setZoomBox( env, mapImage.getWidth(), mapImage.getHeight() );
            try {
                appContainer.getCommandProcessor().executeSychronously( zoom, true );
            } catch ( Exception e ) {
                DialogFactory.openErrorDialog( appContainer.getViewPlatform(), MapOverviewPanel.this,
                                               Messages.get( "$MD11242" ), Messages.get( "$MD11243", env ), e );
            }
        }

    }

}
