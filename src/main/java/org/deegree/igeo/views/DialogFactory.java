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

package org.deegree.igeo.views;

import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;
import static javax.swing.JOptionPane.showConfirmDialog;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.views.swing.util.ErrorDialog;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.igeo.views.swing.util.NewReferenceDialog;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class DialogFactory {

    private static final ILogger LOG = LoggerFactory.getLogger( DialogFactory.class );

    /**
     * 
     * @param viewplatform
     * @param parent
     * @param message
     * @param title
     * @return true if confirmed
     */
    public static boolean openConfirmDialog( String viewplatform, Object parent, String message, String title ) {
        if ( "Application".equalsIgnoreCase( viewplatform ) ) {
            int c = JOptionPane.showConfirmDialog( (Component) parent, message, title,
                                                   JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE );
            return c == JOptionPane.YES_OPTION;
        }
        LOG.logWarning( "viewplatform not supported: " + viewplatform );
        return false;
    }

    /**
     * @param viewplatform
     * @param parent
     * @param message
     * @param title
     * @return true, if YES clicked
     */
    public static boolean openConfirmDialogYESNO( String viewplatform, Object parent, String message, String title ) {
        if ( "Application".equalsIgnoreCase( viewplatform ) ) {
            int c = showConfirmDialog( (Component) parent, message, title, YES_NO_OPTION );
            return c == YES_OPTION;
        }
        LOG.logWarning( "viewplatform not supported: " + viewplatform );
        return false;
    }

    /**
     * 
     * @param viewplatform
     * @param parent
     * @param message
     * @param title
     */
    public static void openInformationDialog( String viewplatform, Object parent, String message, String title ) {
        if ( "Application".equalsIgnoreCase( viewplatform ) ) {
            JOptionPane.showMessageDialog( (Component) parent, message, title, JOptionPane.INFORMATION_MESSAGE,
                                           IconRegistry.getIcon( "information_large.gif" ) );
        } else {
            LOG.logWarning( "viewplatform not supported: " + viewplatform );
        }
    }

    /**
     * @param viewplatform
     * @param parent
     * @param message
     * @param title
     */
    public static void openErrorDialog( String viewplatform, Component parent, final String message, final String title ) {
        if ( "Application".equalsIgnoreCase( viewplatform ) ) {
            // showMessageDialog( parent, message, title, ERROR_MESSAGE );
            SwingUtilities.invokeLater( new Thread() {
                public void run() {
                    new ErrorDialog( title, message, "-" );
                };
            } );
        } else {
            LOG.logWarning( "viewplatform not supported: " + viewplatform );
        }
    }

    /**
     * @param viewplatform
     * @param parent
     * @param message
     * @param title
     */
    public static void openErrorDialog( String viewplatform, Component parent, final String title,
                                        final String message, final Throwable exception ) {
        if ( "Application".equalsIgnoreCase( viewplatform ) ) {
            // showMessageDialog( parent, message, title, ERROR_MESSAGE );
            SwingUtilities.invokeLater( new Thread() {
                public void run() {
                    new ErrorDialog( title, message, StringTools.stackTraceToString( exception ) );
                };
            } );
        } else {
            LOG.logWarning( "viewplatform not supported: " + viewplatform );
        }
    }

    /**
     * 
     * @param viewplatform
     * @param parent
     * @param message
     * @param title
     */
    public static void openWarningDialog( String viewplatform, Object parent, String message, String title ) {
        if ( "Application".equalsIgnoreCase( viewplatform ) ) {
            JOptionPane.showMessageDialog( (Component) parent, message, title, JOptionPane.WARNING_MESSAGE );
        } else {
            LOG.logWarning( "viewplatform not supported: " + viewplatform );
        }
    }

    /**
     * opens a dialog that expects a user to enter a string
     * 
     * @param viewplatform
     * @param parent
     * @param message
     * @param title
     * @return entered string or <code>null</code> if user has pressed cancle
     */
    public static String openInputDialog( String viewplatform, Object parent, String message, String title ) {
        String s = null;
        if ( "Application".equalsIgnoreCase( viewplatform ) ) {
            s = JOptionPane.showInputDialog( (Component) parent, message, title, JOptionPane.PLAIN_MESSAGE );
        } else {
            LOG.logWarning( "viewplatform not supported: " + viewplatform );

        }
        return s;
    }
    
    /**
     * opens a dialog with a predefined value that expects a user to enter a string
     * 
     * @param viewplatform
     * @param parent
     * @param message
     * @param value
     * @return entered string or <code>null</code> if user has pressed cancel
     */
    public static String openInputDialogWithValue( String viewplatform, Object parent, String message, String value ) {
        String s = null;
        if ( "Application".equalsIgnoreCase( viewplatform ) ) {
            s = JOptionPane.showInputDialog( (Component) parent, message, value );
        } else {
            LOG.logWarning( "viewplatform not supported: " + viewplatform );

        }
        return s;
    }

    /**
     * 
     * @param viewplatform
     * @param message
     * @param reference
     * @param asURL
     * @return URL/file or <code>null</code>
     */
    public static String openNewReferenceDialog( ApplicationContainer<?> appCont, String message,
                                                 String reference, boolean asURL ) {
        String s = null;
        if ( "Application".equalsIgnoreCase( appCont.getViewPlatform() ) ) {
            NewReferenceDialog nud = new NewReferenceDialog( appCont, message, reference );
            if ( asURL ) {
                s = nud.getAsURL();
            } else {
                s = nud.getAsFile();
            }
        } else {
            LOG.logWarning( "viewplatform not supported: " + appCont.getViewPlatform() );

        }
        return s;
    }
}
