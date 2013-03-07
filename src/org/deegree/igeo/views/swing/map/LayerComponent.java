//$HeadURL: svn+ssh://mschneider@svn.wald.intevation.org/deegree/base/trunk/resources/eclipse/svn_classfile_header_template.xml $
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

package org.deegree.igeo.views.swing.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

import javax.swing.JComponent;

import org.deegree.framework.concurrent.Executor;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.ChangeListener;
import org.deegree.igeo.ValueChangedEvent;
import org.deegree.igeo.mapmodel.LayerChangedEvent;
import org.deegree.igeo.views.LayerPane;

/**
 * 
 * <code>JLayerComponent</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class LayerComponent extends JComponent implements ChangeListener {

    private static final long serialVersionUID = -6707309401307638688L;

    private static final ILogger LOG = LoggerFactory.getLogger( LayerComponent.class );

    private LayerPane layerPane;

    // image to paint on the parent component
    private BufferedImage layerImage;

    // indicates if the map model has been changed in a way that requires a complete repainting
    private boolean forceRepaint = false;

    /**
     * 
     * @param layerPane
     */
    public LayerComponent( LayerPane layerPane ) {
        this.layerPane = layerPane;
        this.layerPane.getLayer().addChangeListener( this );
    }

    /**
     * Invoked every time the component should be painted...
     * 
     * Three different options are possible:
     * 
     * 1) there are no changes of the map model or size of the layers -> paint old layer ----------- 2) the map model
     * has changed, but not the size of the layers -> update the image ------------ 3) the map model has changes and
     * also the size of the layers -> create a new image ----------
     * 
     */
    @Override
    protected void paintComponent( Graphics g ) {

        if ( layerPane.isVisible() ) {
            super.paintComponent( g );
            // indicates if a new image must be created

            boolean newImage = this.layerImage == null || this.forceRepaint || !layerPane.isValid();

            if ( newImage ) {
                LOG.logDebug( "paint layer with title " + layerPane.getLayer().getTitle() + ": create a NEW image" );
            }
            double min = layerPane.getLayer().getMinScaleDenominator();
            double max = layerPane.getLayer().getMaxScaleDenominator();
            double sc = layerPane.getMapModel().getScaleDenominator();
            if ( min <= sc && max >= sc ) {
                /*
                 * if ( newImage ) { this.forceRepaint = false;
                 * 
                 * // create a new image if image is null or size of the map model has changed this.layerImage = new
                 * BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB );
                 * 
                 * // create graphics of the image to paint Graphics2D layerGraphics = this.layerImage.createGraphics();
                 * layerGraphics.setClip( 0, 0, getWidth(), getHeight() );
                 * 
                 * // clear the old image layerGraphics.setBackground( new Color( 0, 0, 0, 0 ) );
                 * layerGraphics.clearRect( 0, 0, this.layerImage.getWidth(), this.layerImage.getHeight() );
                 * 
                 * // draw layer this.layerPane.paint( layerGraphics );
                 * 
                 * layerGraphics.dispose();
                 * 
                 * } g.drawImage( layerImage, 0, 0, this );
                 */
                if ( newImage ) {
                    Executor.getInstance().performAsynchronously( new Renderer(), null );
                } else {
                    g.drawImage( layerImage, 0, 0, LayerComponent.this );
                }
            }
        }

    }

    /**
     * 
     * @param forceRepaint
     *            true forces creating of a new image when component will be repainted nex time
     */
    public void setForceDeepRepaint( boolean forceRepaint ) {
        this.forceRepaint = forceRepaint;
    }

    /**
     * 
     * @return the current image of the layer component
     */
    public BufferedImage getImage() {
        return layerImage;
    }

    /**
     * 
     * @param layerImage
     *            the image of the layer
     */
    public void setImage( BufferedImage layerImage ) {
        this.layerImage = layerImage;
    }

    /**
     * 
     * @return the layer pane of the layer component
     */
    public LayerPane getLayerPane() {
        return this.layerPane;
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // ChangeListener
    // /////////////////////////////////////////////////////////////////////////////////

    /**
     * invoked when layer adapter has changed
     */
    public void valueChanged( ValueChangedEvent event ) {
        LayerChangedEvent layerEvent = (LayerChangedEvent) event;

        switch ( layerEvent.getChangeType() ) {
        case datasourceChanged:
        case datasourceAdded:
        case datasourceRemoved:
        case dataChanged:
        case stylesSet:
            this.forceRepaint = true;
            if ( getParent() != null ) {
                getParent().repaint();
            }
            break;

        case visibilityChanged:
            if ( getParent() != null ) {
                getParent().repaint();
            }
            break;
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // Inner classes
    // /////////////////////////////////////////////////////////////////////////////////

    /**
     * 
     * TODO add class documentation here
     * 
     * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
     * @author last edited by: $Author: admin $
     * 
     * @version $Revision: $, $Date: $
     */
    public class Renderer implements Callable<Object> {

        /*
         * (non-Javadoc)
         * 
         * @see java.util.concurrent.Callable#call()
         */
        public Object call()
                                throws Exception {
            // synchronized ( LayerComponent.this ) {
            forceRepaint = false;

            // create a new image if image is null or size of the map model has changed
            layerImage = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB );

            // create graphics of the image to paint
            Graphics2D layerGraphics = layerImage.createGraphics();
            layerGraphics.setClip( 0, 0, getWidth(), getHeight() );

            // clear the old image
            layerGraphics.setBackground( new Color( 0, 0, 0, 0 ) );
            layerGraphics.clearRect( 0, 0, layerImage.getWidth(), layerImage.getHeight() );

            // draw layer
            layerPane.paint( layerGraphics );

            layerGraphics.dispose();

            repaint();
            return null;
            // }
        }

    }
}
