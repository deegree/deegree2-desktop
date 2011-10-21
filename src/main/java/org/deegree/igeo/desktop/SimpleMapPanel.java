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
package org.deegree.igeo.desktop;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.JPanel;

import org.deegree.framework.util.ImageUtils;
import org.deegree.framework.util.MapUtils;
import org.deegree.graphics.transformation.GeoTransform;
import org.deegree.graphics.transformation.WorldToScreenTransform;
import org.deegree.igeo.i18n.Messages;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class SimpleMapPanel extends JPanel {

    private static final long serialVersionUID = -8098724005044613783L;

    private BufferedImage image;

    private Envelope viewBox;

    private Envelope selectBox;

    private Envelope imageBox;

    private Envelope destRect;

    private Envelope zoomBox;

    /**
     * 
     */
    SimpleMapPanel() {
        try {
            String res = Messages.getMessage( getLocale(), "$DI10070" );
            try {
                if ( res.startsWith( "file:" ) || res.startsWith( "http:" ) || res.startsWith( "ftp:" ) ) {
                    image = ImageUtils.loadImage( new URL( res ) );
                } else {
                    URL u = getClass().getResource( res );
                    image = ImageUtils.loadImage( u.openStream() );
                }
            } catch ( Exception e ) {
                e.printStackTrace();
                URL u = getClass().getResource( "worldmap.gif" );
                image = ImageUtils.loadImage( u.openStream() );
            }

            destRect = GeometryFactory.createEnvelope( 0, 0, image.getWidth() - 1, image.getHeight() - 1, null );
            viewBox = GeometryFactory.createEnvelope( -180, -90, 180, 83.64, CRSFactory.create( "EPSG:4326" ) );
            selectBox = GeometryFactory.createEnvelope( -180, -90, 180, 83.64, CRSFactory.create( "EPSG:4326" ) );
            imageBox = GeometryFactory.createEnvelope( -180, -90, 180, 83.64, CRSFactory.create( "EPSG:4326" ) );
        } catch ( Exception e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent( Graphics g ) {
        GeoTransform gt = new WorldToScreenTransform( imageBox, destRect );
        int x1 = (int) Math.round( gt.getDestX( viewBox.getMin().getX() ) );
        int y1 = (int) Math.round( gt.getDestY( viewBox.getMax().getY() ) );
        int x2 = (int) Math.round( gt.getDestX( viewBox.getMax().getX() ) );
        int y2 = (int) Math.round( gt.getDestY( viewBox.getMin().getY() ) );
        if ( x1 >= 0 && y1 >= 0 && x1 + ( x2 - x1 ) < image.getWidth() && y1 + ( y2 - y1 ) < image.getHeight() ) {
            BufferedImage tmp = image.getSubimage( x1, y1, x2 - x1, y2 - y1 );
            g.clearRect( 0, 0, getWidth(), getHeight() );
            g.drawImage( tmp, 0, 0, getWidth(), getHeight(), this );
        }

        Envelope dR = GeometryFactory.createEnvelope( 0, 0, getWidth() - 1, getHeight() - 1, null );
        gt = new WorldToScreenTransform( viewBox, dR );

        // draw select box
        x1 = (int) Math.round( gt.getDestX( selectBox.getMin().getX() ) );
        y1 = (int) Math.round( gt.getDestY( selectBox.getMax().getY() ) );
        x2 = (int) Math.round( gt.getDestX( selectBox.getMax().getX() ) );
        y2 = (int) Math.round( gt.getDestY( selectBox.getMin().getY() ) );
        g.setColor( new Color( 1f, 1, 1, 0.5f ) );
        g.fillRect( x1, y1, x2 - x1, y2 - y1 );
        g.setColor( Color.red );
        g.drawRect( x1, y1, x2 - x1, y2 - y1 );

        // draw zoom box
        if ( zoomBox != null ) {
            x1 = (int) Math.round( gt.getDestX( zoomBox.getMin().getX() ) );
            y1 = (int) Math.round( gt.getDestY( zoomBox.getMax().getY() ) );
            x2 = (int) Math.round( gt.getDestX( zoomBox.getMax().getX() ) );
            y2 = (int) Math.round( gt.getDestY( zoomBox.getMin().getY() ) );
            g.setColor( Color.blue );
            g.drawRect( x1, y1, x2 - x1, y2 - y1 );
        }

    }

    void zoomIn() {
        double dx = viewBox.getWidth() / 8d;
        double dy = viewBox.getHeight() / 8d;
        double x_1 = viewBox.getMin().getX() + dx;
        double y_1 = viewBox.getMin().getY() + dy;
        double x_2 = viewBox.getMax().getX() - dx;
        double y_2 = viewBox.getMax().getY() - dy;
        Envelope tmp = GeometryFactory.createEnvelope( x_1, y_1, x_2, y_2, viewBox.getCoordinateSystem() );
        if ( imageBox.contains( tmp ) ) {
            viewBox = tmp;
        }
    }

    void zoomIn( int x1, int y1, int x2, int y2 ) {
        Envelope dR = GeometryFactory.createEnvelope( 0, 0, getWidth() - 1, getHeight() - 1, null );
        GeoTransform gt = new WorldToScreenTransform( viewBox, dR );
        double x_1 = gt.getSourceX( x1 );
        double y_1 = gt.getSourceY( y2 );
        double x_2 = gt.getSourceX( x2 );
        double y_2 = gt.getSourceY( y1 );
        Envelope tmp = GeometryFactory.createEnvelope( x_1, y_1, x_2, y_2, viewBox.getCoordinateSystem() );
        tmp = MapUtils.ensureAspectRatio( tmp, imageBox.getWidth(), imageBox.getHeight() );
        if ( imageBox.contains( tmp ) ) {
            viewBox = tmp;
        }
        zoomBox = null;
    }

    void setZoomBox( int x1, int y1, int x2, int y2 ) {
        Envelope dR = GeometryFactory.createEnvelope( 0, 0, getWidth() - 1, getHeight() - 1, null );
        GeoTransform gt = new WorldToScreenTransform( viewBox, dR );
        double x_1 = gt.getSourceX( x1 );
        double y_1 = gt.getSourceY( y2 );
        double x_2 = gt.getSourceX( x2 );
        double y_2 = gt.getSourceY( y1 );
        zoomBox = GeometryFactory.createEnvelope( x_1, y_1, x_2, y_2, viewBox.getCoordinateSystem() );
    }

    void zoomOut() {
        double dx = viewBox.getWidth() / 8d;
        double dy = viewBox.getHeight() / 8d;
        double x_1 = viewBox.getMin().getX() - dx;
        double y_1 = viewBox.getMin().getY() - dy;
        double x_2 = viewBox.getMax().getX() + dx;
        double y_2 = viewBox.getMax().getY() + dy;
        if ( x_1 > imageBox.getMin().getX() && y_1 > imageBox.getMin().getY() && x_2 < imageBox.getMax().getX()
             && y_2 < imageBox.getMax().getY() ) {
            viewBox = GeometryFactory.createEnvelope( x_1, y_1, x_2, y_2, viewBox.getCoordinateSystem() );
        }
    }

    void pan( int x1, int y1, int x2, int y2 ) {
        Envelope dR = GeometryFactory.createEnvelope( 0, 0, getWidth() - 1, getHeight() - 1, null );
        GeoTransform gt = new WorldToScreenTransform( viewBox, dR );
        double x_1 = gt.getSourceX( x1 );
        double y_1 = gt.getSourceY( y2 );
        double x_2 = gt.getSourceX( x2 );
        double y_2 = gt.getSourceY( y1 );
        Envelope tmp = viewBox.translate( ( x_1 - x_2 ), ( y_2 - y_1 ) );
        if ( imageBox.contains( tmp ) ) {
            viewBox = tmp;
        }
    }

    void select( int x1, int y1, int x2, int y2 ) {
        Envelope dR = GeometryFactory.createEnvelope( 0, 0, getWidth() - 1, getHeight() - 1, null );
        GeoTransform gt = new WorldToScreenTransform( viewBox, dR );
        double x_1 = gt.getSourceX( x1 );
        double y_1 = gt.getSourceY( y2 );
        double x_2 = gt.getSourceX( x2 );
        double y_2 = gt.getSourceY( y1 );
        selectBox = GeometryFactory.createEnvelope( x_1, y_1, x_2, y_2, viewBox.getCoordinateSystem() );
    }

    void select( Envelope select ) {
        this.selectBox = select;
        select = MapUtils.ensureAspectRatio( select, imageBox.getWidth(), imageBox.getHeight() );
        select = select.getBuffer( select.getHeight() / 5d );
        if ( imageBox.contains( select ) ) {
            viewBox = select;
        } else {
            viewBox = imageBox.createIntersection( select );
        }
    }

    Envelope getSelectBox() {
        return selectBox;
    }

}
