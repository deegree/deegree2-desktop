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
package org.deegree.igeo.commands.model;

import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.MapUtils;
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.mapmodel.Datasource;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.kernel.AbstractCommand;
import org.deegree.model.Identifier;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryException;
import org.deegree.model.spatialschema.GeometryFactory;

/**
 * 
 * <code>ZoomCommand</code> handles all changes of the extent of the mapModel.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class ZoomCommand extends AbstractCommand {

    public static final QualifiedName name = new QualifiedName( "Zoom" );

    public static final ILogger LOG = LoggerFactory.getLogger( ZoomCommand.class );

    private Identifier identifer;

    private MapModel mapModel;

    private Envelope newExtent;

    private Envelope lastExtent;

    /**
     * As default zoom will be performed an common bounding box of selected layers
     * 
     * @param mapModel
     *            the mapModel to change
     */
    public ZoomCommand( MapModel mapModel ) {
        this( new Identifier(), mapModel );
    }

    /**
     * As default zoom will be performed an common bounding box of selected layers
     * 
     * @param identifer
     *            the id of this command
     * @param mapModel
     *            the mapModel to change
     */
    public ZoomCommand( Identifier identifier, MapModel mapModel ) {
        this.identifer = identifier;
        this.mapModel = mapModel;
        // stores the old extent to allow undo
        this.lastExtent = this.mapModel.getEnvelope();
        setDefaultZoom();
    }

    /**
     * 
     */
    private void setDefaultZoom() {
        List<Layer> layers = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
        Envelope envelope = null;
        try {
            for ( Layer layer : layers ) {
                if ( layer.isVisible() ) {
                    envelope = mergeEnvelopes( layer, envelope );
                }
            }
        } catch ( GeometryException e ) {
            LOG.logError( e.getMessage(), e );
        }
        if ( envelope == null ) {
            envelope = mapModel.getEnvelope();
        }
        if ( envelope.getWidth() == 0 || envelope.getHeight() == 0 ) {
            envelope = envelope.getBuffer( mapModel.getEnvelope().getWidth() / 50d );
        }
        setZoomBox( envelope, mapModel.getTargetDevice().getPixelWidth(), mapModel.getTargetDevice().getPixelHeight() );

    }

    private Envelope mergeEnvelopes( Layer layer, Envelope envelope )
                            throws GeometryException {
        List<DataAccessAdapter> adapters = layer.getDataAccess();
        for ( DataAccessAdapter dataAccessAdapter : adapters ) {
            if ( dataAccessAdapter instanceof FeatureAdapter && isValid( (FeatureAdapter) dataAccessAdapter ) ) {
                Envelope env = ( (FeatureAdapter) dataAccessAdapter ).getFeatureCollection().getBoundedBy();
                if ( envelope == null ) {
                    envelope = env;
                } else {
                    envelope = envelope.merge( env );
                }
            }
        }
        return envelope;
    }

    public boolean isValid( FeatureAdapter featureAdapter ) {
        Datasource ds = featureAdapter.getDatasource();
        double sc = mapModel.getScaleDenominator();
        return ds.getMinScaleDenominator() <= sc && ds.getMaxScaleDenominator() >= sc;
    }

    /**
     * Calculates the new extent of the Map from a zoom box (pixel coordinates).
     * 
     * @param x1
     *            x value of the first point (pressed mouse)
     * @param y1
     *            y value of the first point (pressed mouse)
     * @param x2
     *            x value of the first point (released mouse; identical with x1 in case of click)
     * @param y2
     *            y value of the first point (released mouse; identical with x1 in case of click)
     * @param zoomFactor
     *            the factor to zoom in/out or pan the pan (negative means zoom out; positiv for zoom in; 1 means pan)
     * @param width
     *            the width of the map in pixel
     * @param height
     *            the height of the map in pixel
     */
    public void setZoom( double x1, double y1, double x2, double y2, double zoomFactor, int width, int height ) {

        // detect min / max values
        double minx;
        double miny;
        double maxx;
        double maxy;
        if ( x1 > x2 ) {
            maxx = x1;
            minx = x2;
        } else {
            maxx = x2;
            minx = x1;
        }
        if ( y1 > y2 ) {
            maxy = y1;
            miny = y2;
        } else {
            maxy = y2;
            miny = y1;
        }

        if ( maxx - minx < 10 && maxy - miny < 10 ) {
            zoom( x1, y1, zoomFactor, width, height );
        } else {
            if ( zoomFactor == 1 ) {
                pan( x1, y1, x2, y2, width, height );
            } else {
                setZoomBox( minx, miny, maxx, maxy, width, height );
            }
        }
    }

    public void setNewScale( double newScale, int width, int height ) {
        double currentScale = MapUtils.calcScale( width, height, this.mapModel.getEnvelope(),
                                                  this.mapModel.getCoordinateSystem(), MapUtils.DEFAULT_PIXEL_SIZE );
        this.newExtent = MapUtils.scaleEnvelope( this.mapModel.getEnvelope(), currentScale, newScale );
    }

    // pan
    private void pan( double x1, double y1, double x2, double y2, int width, int height ) {
        // information about the map
        double mapWidth = this.mapModel.getEnvelope().getWidth();
        double mapHeight = this.mapModel.getEnvelope().getHeight();

        double deltaX = x2 - x1;
        double deltaY = y2 - y1;

        double newMinX = this.mapModel.getEnvelope().getMin().getX() - ( deltaX * ( mapWidth / width ) );
        double newMinY = this.mapModel.getEnvelope().getMin().getY() + ( ( deltaY ) * ( mapHeight / height ) );

        this.newExtent = GeometryFactory.createEnvelope( newMinX, newMinY, newMinX + mapWidth, newMinY + mapHeight,
                                                         mapModel.getCoordinateSystem() );
    }

    // zoom in - rectangle
    private void setZoomBox( double minx, double miny, double maxx, double maxy, int width, int height ) {
        // information about the map
        double mapWidth = this.mapModel.getEnvelope().getWidth();
        double mapHeight = this.mapModel.getEnvelope().getHeight();

        double newMinX = this.mapModel.getEnvelope().getMin().getX() + ( minx * ( mapWidth / width ) );
        double newMinY = this.mapModel.getEnvelope().getMin().getY() + ( ( height - maxy ) * ( mapHeight / height ) );

        double newMaxX = this.mapModel.getEnvelope().getMin().getX() + ( maxx * ( mapWidth / width ) );
        double newMaxY = this.mapModel.getEnvelope().getMin().getY() + ( ( height - miny ) * ( mapHeight / height ) );

        Envelope ext = GeometryFactory.createEnvelope( newMinX, newMinY, newMaxX, newMaxY,
                                                       mapModel.getCoordinateSystem() );

        double deltaX = maxx - minx;
        double deltaY = maxy - miny;

        double scale;
        if ( ( width / deltaX ) < ( height / deltaY ) ) {
            scale = width / deltaX;
        } else {
            scale = height / deltaY;
        }

        this.newExtent = MapUtils.ensureAspectRatio( ext, mapWidth * scale, mapHeight * scale );
    }

    /**
     * sets a zoom command to a defined envelope (geographic coordinates)
     * 
     * @param envelope
     *            new envelope
     * @param width
     *            map width in pixel
     * @param height
     *            map height in pixel
     */
    public void setZoomBox( Envelope envelope, int width, int height ) {
        if ( width <= 0 ) {
            width = mapModel.getTargetDevice().getPixelWidth();
        }
        if ( height <= 0 ) {
            height = mapModel.getTargetDevice().getPixelHeight();
        }
        if ( envelope == null ) {
            envelope = mapModel.getEnvelope();
        }
        double deltaX = envelope.getWidth();
        double deltaY = envelope.getHeight();

        double scale;
        if ( ( width / deltaX ) < ( height / deltaY ) ) {
            scale = width / deltaX;
        } else {
            scale = height / deltaY;
        }
        double mapWidth = this.mapModel.getEnvelope().getWidth();
        double mapHeight = this.mapModel.getEnvelope().getHeight();

        this.newExtent = MapUtils.ensureAspectRatio( envelope, mapWidth * scale, mapHeight * scale );
    }

    /**
     * zoom in / out - clickpoint
     * 
     * @param x
     * @param y
     * @param zoomFactor
     * @param width
     * @param height
     */
    private void zoom( double x, double y, double zoomFactor, int width, int height ) {
        // information about the map
        double mapWidth = this.mapModel.getEnvelope().getWidth();
        double mapHeight = this.mapModel.getEnvelope().getHeight();

        // calculate new coordinates of the centroid
        double centerX = this.mapModel.getEnvelope().getMin().getX() + ( x * ( mapWidth / width ) );
        double centerY = this.mapModel.getEnvelope().getMin().getY() + ( ( height - y ) * ( mapHeight / height ) );

        // calculate new map width and height
        zoomFactor = zoomFactor < -1 ? zoomFactor * -1 : zoomFactor + 1;

        double newMapWidth = mapWidth * zoomFactor;
        double newMapHeight = mapHeight * zoomFactor;

        // new coordinates
        double minx = centerX - ( newMapWidth / 2 );
        double maxx = centerX + ( newMapWidth / 2 );
        double miny = centerY - ( newMapHeight / 2 );
        double maxy = centerY + ( newMapHeight / 2 );

        this.newExtent = GeometryFactory.createEnvelope( minx, miny, maxx, maxy, mapModel.getCoordinateSystem() );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.Command#execute()
     */
    public void execute()
                            throws Exception {
        if ( this.newExtent != null ) {
            this.mapModel.setEnvelope( this.newExtent );
        }
        fireCommandProcessedEvent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.Command#getIdentifier()
     */
    public Identifier getIdentifier() {
        return this.identifer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.Command#getName()
     */
    public QualifiedName getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.Command#isUndoSupported()
     */
    public boolean isUndoSupported() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.Command#undo()
     */
    public void undo()
                            throws Exception {
        if ( this.lastExtent != null ) {
            this.mapModel.setEnvelope( this.lastExtent );
            // module.update();
        }
    }

    /**
     * 
     * @return extent of a map before zoom action has been performed
     */
    public Envelope getUndoExtent() {
        return lastExtent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.connector.Command#getResult()
     */
    public Object getResult() {
        // TODO Auto-generated method stub
        return null;
    }

}
