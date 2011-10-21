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
package org.deegree.igeo.modules;

import static java.awt.Toolkit.getDefaultToolkit;
import static java.util.prefs.Preferences.userNodeForPackage;

import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.TransferHandler;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.ImageUtils;
import org.deegree.framework.utils.MapTools;
import org.deegree.igeo.modules.ActionDescription.ACTIONTYPE;
import org.deegree.igeo.views.swing.util.GenericFileChooser;
import org.deegree.igeo.views.swing.util.IGeoFileFilter;
import org.deegree.igeo.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;

/**
 * Module for displaying and managing a legend assigned to layers contained in a mapmodel
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class LegendModule<T> extends DefaultModule<T> {

    private static final ILogger LOG = LoggerFactory.getLogger( LegendModule.class );

    static {
        ActionDescription ad1 = new ActionDescription( "exportAsImage",
                                                       "enables exporting current legend as an image to a file", null,
                                                       "export legend as image", ACTIONTYPE.PushButton, null, null );
        ActionDescription ad2 = new ActionDescription( "exportToClipBoard",
                                                       "copies current legend as a png image into clip board", null,
                                                       "copy legend as image into clip board", ACTIONTYPE.PushButton,
                                                       null, null );
        moduleCapabilities = new ModuleCapabilities( ad1, ad2 );
    }

    /**
     * action handler function for exporting a legend as image
     */
    public void exportAsImage() {
        Preferences prefs = userNodeForPackage( LegendModule.class );
        File f = GenericFileChooser.showSaveDialog( FILECHOOSERTYPE.printResult, appContainer, null, prefs,
                                                    "outputdir", IGeoFileFilter.JPEG, IGeoFileFilter.PNG,
                                                    IGeoFileFilter.BMP );

        if ( f != null ) {
            BufferedImage img;
            try {
                img = MapTools.getLegendAsImage( appContainer.getMapModel( null ),
                                                 f.getAbsolutePath().toLowerCase().endsWith( ".png" ) );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                return;
            }
            try {
                ImageUtils.saveImage( img, f, 0.99f );
            } catch ( IOException e ) {
                LOG.logError( e.getMessage(), e );
            }
        }

    }

    /**
     * action handler function for copying a legend into clip board
     */
    public void exportToClipBoard() {
        BufferedImage img;
        try {
            img = MapTools.getLegendAsImage( appContainer.getMapModel( null ), true );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            return;
        }

        final JLabel label = new JLabel( new ImageIcon( img ) );
        label.setTransferHandler( new ImageSelection() );

        // use both clip boards for text?
        Clipboard clip = getDefaultToolkit().getSystemSelection();
        if ( clip != null ) {
            TransferHandler handler = label.getTransferHandler();
            handler.exportToClipboard( label, clip, TransferHandler.COPY );
        }
        clip = getDefaultToolkit().getSystemClipboard();
        TransferHandler handler = label.getTransferHandler();
        handler.exportToClipboard( label, clip, TransferHandler.COPY );
    }

    // //////////////////////////////////////////////////////////////////////////////////
    // inner classes
    // /////////////////////////////////////////////////////////////////////////////////

    /**
     * 
     * TODO add class documentation here
     * 
     * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    private static class ImageSelection extends TransferHandler implements Transferable {

        private static final long serialVersionUID = 6897713712887196351L;

        private static final DataFlavor flavors[] = { DataFlavor.imageFlavor };

        private Image image;

        @Override
        public int getSourceActions( JComponent c ) {
            return TransferHandler.COPY;
        }

        @Override
        public boolean canImport( JComponent comp, DataFlavor flavor[] ) {
            if ( !( comp instanceof JLabel ) ) {
                return false;
            }
            for ( int i = 0, n = flavor.length; i < n; i++ ) {
                for ( int j = 0, m = flavors.length; j < m; j++ ) {
                    if ( flavor[i].equals( flavors[j] ) ) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public Transferable createTransferable( JComponent comp ) {
            // Clear
            image = null;

            if ( comp instanceof JLabel ) {
                JLabel label = (JLabel) comp;
                Icon icon = label.getIcon();
                if ( icon instanceof ImageIcon ) {
                    image = ( (ImageIcon) icon ).getImage();
                    return this;
                }
            }
            return null;
        }

        @Override
        public boolean importData( JComponent comp, Transferable t ) {
            if ( comp instanceof JLabel ) {
                JLabel label = (JLabel) comp;
                if ( t.isDataFlavorSupported( flavors[0] ) ) {
                    try {
                        image = (Image) t.getTransferData( flavors[0] );
                        ImageIcon icon = new ImageIcon( image );
                        label.setIcon( icon );
                        return true;
                    } catch ( UnsupportedFlavorException ignored ) {
                        LOG.logWarning( "ignore", ignored );
                    } catch ( IOException ignored ) {
                        LOG.logWarning( "ignore", ignored );
                    }
                }
            }
            return false;
        }

        // Transferable
        public Object getTransferData( DataFlavor flavor ) {
            if ( isDataFlavorSupported( flavor ) ) {
                return image;
            }
            return null;
        }

        // Transferable
        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        // Transferable
        public boolean isDataFlavorSupported( DataFlavor flavor ) {
            return flavor.equals( DataFlavor.imageFlavor );
        }
    }

}
