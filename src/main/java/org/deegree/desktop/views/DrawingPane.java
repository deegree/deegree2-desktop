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
package org.deegree.desktop.views;

import java.awt.Graphics;
import java.awt.Point;
import java.util.List;


/**
 * Defines a pane that can be assigned to a graphical representation of a layer. This pane can be
 * used to perform drawings on a layer. It is independ of the output devices and the type of layer.
 * For example it will be possible to define a layer that just draws rectangle (e.g. for zoomin) but
 * does not holds any data.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public interface DrawingPane {

    /**
     * sets the graphic context to draw to
     * 
     * @param g
     */
    void setGraphicContext( Graphics g );

    /**
     * starts drawing at the passed coordinates
     * 
     * @param x
     * @param y
     */
    void startDrawing( int x, int y );

    /**
     * stops drawing at the passed coordinates
     * 
     * @param x
     * @param y
     */
    void stopDrawing( int x, int y );

    /**
     * 
     * @return true if pane is within a drawing session but the has been stopped; e.g. by invoking
     *         the {@link #startDrawing(int, int)} method
     */
    boolean isDrawingStopped();

    /**
     * draws at the passed coordinates onto the default graphic context
     * 
     * @param x
     * @param y
     */
    void draw( int x, int y );

    /**
     * draws at the passed coordinates onto a passed graphic context
     * 
     * @param x
     * @param y
     */
    void draw( int x, int y, Graphics g );

    /**
     * revokes the last drawing action
     * 
     */
    void undrawLastPoint();

    /**
     * 
     * @return true if pane is within a drawing session
     */
    boolean isDrawing();

    /**
     * finished a drawing session. This method must be invoked before getDrawnObject() will returned
     * drawn geometry
     * 
     */
    void finishDrawing();

    /**
     * 
     * @return least drawn object
     */
    List<Point> getDrawnObject();

    /**
     * 
     * @return current point of drawing
     */
    Point getCurrent();
    
    /**
     * sets an instance of {@link Snapper} class to use snapping while drawing
     * @param snapper
     */
    void setSnapper(Snapper snapper);

}
