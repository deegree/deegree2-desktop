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
package org.deegree.igeo.views.swing.layerlist;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.deegree.framework.util.ImageUtils;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class ImageViewer extends JFrame {

    private static final long serialVersionUID = 2901475995941344565L;

    private ImageComponent imageComp = null;

    /**
     * 
     * @param image
     * @param title
     */
    public ImageViewer( Image image, String title ) {
        setTitle( title );

        imageComp = new ImageComponent( image );
        JScrollPane sc = new JScrollPane( imageComp, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                          JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS );
        getContentPane().add( sc );
    }

    /**
     * loads an image from the passed url and displayes it
     * 
     * @param fileURL
     * @throws IOException
     * @throws MalformedURLException
     */
    public static void show( String fileURL )
                            throws MalformedURLException, IOException {
        Image image = ImageUtils.loadImage( new URL( fileURL ) );
        JFrame f = new ImageViewer( image, fileURL );
        f.setBounds( 200, 200, 400, 400 );
        f.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        f.setVisible( true );
        f.toFront();
    }

    // ////////////////////////////////////////////////////////////////////////////////
    // inner classes
    // ////////////////////////////////////////////////////////////////////////////////

    // ImageComponent is used for displaying the image
    private class ImageComponent extends JComponent {

        private static final long serialVersionUID = 8169980021796576246L;

        private Image image = null;

        private Dimension size = null;

        private Insets insets = new Insets( 0, 0, 0, 0 );

        /**
         * 
         * @param image
         */
        ImageComponent( Image image ) {
            this.image = image;
        }

        @Override
        public void paint( Graphics g ) {
            super.paint( g );
            insets = getInsets( insets );
            size = getSize( size );
            if ( image == null )
                return;

            g.drawImage( image, insets.left, insets.top, this );
        }

        @Override
        public Dimension getMinimumSize() {
            int imgw = 32, imgh = 32;
            if ( image != null ) {
                imgw = image.getWidth( this );
                imgh = image.getHeight( this );
            }
            insets = getInsets( insets );
            return new Dimension( insets.left + Math.max( 32, imgw / 10 ) + insets.right, insets.top
                                                                                          + Math.max( 32, imgh / 10 )
                                                                                          + insets.bottom );
        }

        @Override
        public Dimension getPreferredSize() {
            int imgw = 32, imgh = 32;
            if ( image != null ) {
                imgw = image.getWidth( this );
                imgh = image.getHeight( this );
            }
            insets = getInsets( insets );
            return new Dimension( insets.left + imgw + insets.right, insets.top + imgh + insets.bottom );
        }

        @Override
        public Dimension getMaximumSize() {
            return getPreferredSize();
        }

    }

}
