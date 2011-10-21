//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2007 by:
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

package org.deegree.igeo.views.swing.util;

import static javax.swing.BorderFactory.createEmptyBorder;
import static org.deegree.framework.log.LoggerFactory.getLogger;
import static org.deegree.igeo.i18n.Messages.get;
import static org.deegree.igeo.views.DialogFactory.openErrorDialog;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import org.deegree.framework.log.ILogger;

/**
 * <code>GuiUtils</code> is a collection of useful functions.
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class GuiUtils {

    private static final ILogger LOG = getLogger( GuiUtils.class );

    /**
     * @param log
     * @param e
     * @param parent
     */
    public static void unknownError( ILogger log, Exception e, Component parent ) {
        openErrorDialog( "Application", null, get( "$DI10020" ), get( "$DI10017" ), e );
        log.logError( "Stack trace:", e );
    }

    /**
     * @param log
     * @param msg
     */
    public static void unknownError( ILogger log, String msg ) {
        openErrorDialog( "Application", null, msg, get( "$DI10017" ) );
        log.logError( "An error occurred: " + msg );
    }

    /**
     * @param parent
     * @param msg
     * @param e
     */
    public static void showErrorMessage( Component parent, String msg, Throwable e ) {
        if ( e != null ) {
            LOG.logDebug( "Stack trace", e );
        }
        openErrorDialog( "Application", parent, msg, get( "$DI10017" ), e );
    }

    /**
     * @param panel
     * @return pre-configured constraints
     */
    public static GridBagConstraints initPanel( JPanel panel ) {
        panel.setLayout( new GridBagLayout() );
        panel.setBorder( createEmptyBorder( 2, 2, 2, 2 ) );
        GridBagConstraints gb = new GridBagConstraints();
        gb.gridx = 0;
        gb.gridy = 0;
        gb.insets = new Insets( 2, 2, 2, 2 );
        return gb;
    }

    /**
     * @param list
     * @param box
     */
    public static void installComboBoxListener( final Vector<String> list, final JComboBox box ) {
        box.setEditable( true );
        box.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( e.getSource() == box ) {
                    String s = (String) box.getSelectedItem();
                    if ( s != null ) {
                        list.remove( s );
                        list.add( 0, s );
                        box.setSelectedIndex( 0 );
                    }
                    try {
                        box.updateUI();
                    } catch ( Exception _ ) {
                        // eat unwanted exceptions, it's the only way
                        LOG.logWarning( "eat unwanted exceptions, it's the only way" );
                    }
                }
            }
        } );
    }

    /**
     * Adds a component with the specified size. If width or height are 0 or less, they will be added with the preferred
     * width or height.
     * 
     * @param c
     * @param width
     * @param height
     * @return a panel with the component that can be added.
     * 
     */
    public static JPanel addWithSize( Component c, int width, int height ) {
        Dimension d = c.getPreferredSize();
        if ( width <= 0 ) {
            width = d.width;
        }
        if ( height <= 0 ) {
            height = d.height;
        }

        JPanel p = new JPanel();
        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
        p.add( c );
        c.setPreferredSize( new Dimension( width, height ) );
        return p;
    }

    /**
     * @param gb2
     * @param components
     * @return a panel containing the row
     */
    public static JPanel makeRow( GridBagConstraints gb2, Component... components ) {
        GridBagConstraints gb = (GridBagConstraints) gb2.clone();
        JPanel p = new JPanel();
        gb.gridx = 0;
        gb.gridy = 0;
        initPanel( p );
        for ( Component c : components ) {
            ++gb.gridx;
            p.add( c, gb );
        }
        return p;
    }

    /**
     * @param gb2
     * @param components
     * @return a panel containing the column
     */
    public static JPanel makeColumn( GridBagConstraints gb2, Component... components ) {
        GridBagConstraints gb = (GridBagConstraints) gb2.clone();
        JPanel p = new JPanel();
        gb.gridx = 0;
        gb.gridy = 0;
        initPanel( p );
        for ( Component c : components ) {
            ++gb.gridy;
            p.add( c, gb );
        }
        return p;
    }

    /**
     * Wrapper for list selection listeners.
     * 
     * @param listener
     * @return a list selection listener that notifies the specified action listener.
     */
    public static ListSelectionListener listen( final ActionListener listener ) {
        return new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent e ) {
                listener.actionPerformed( new ActionEvent( e.getSource(), 0, "bogus commandus" ) );
            }
        };
    }

    /**
     * Wrapper for tree selection listeners.
     * 
     * @param listener
     * @return a tree selection listener that notifies the specified action listener.
     */
    public static TreeSelectionListener treeListen( final ActionListener listener ) {
        return new TreeSelectionListener() {
            public void valueChanged( TreeSelectionEvent e ) {
                listener.actionPerformed( new ActionEvent( e.getSource(), 0, "bogus commandus" ) );
            }
        };
    }

    /**
     * Automatically selects the field's text when it gains focus.
     * 
     * @param field
     */
    public static void addSelectAllListener( final JTextField field ) {
        field.addFocusListener( new FocusAdapter() {
            @Override
            public void focusGained( FocusEvent e ) {
                field.selectAll();
            }
        } );
    }

    /**
     * @param c
     * @return null, if not found
     */
    public static Frame getOwnerFrame( Container c ) {
        while ( true ) {
            if ( c == null ) {
                return null;
            }
            if ( ( c = c.getParent() ) instanceof Frame ) {
                return (Frame) c;
            }
        }
    }

    /**
     * @param component
     */
    public static void addToFrontListener( final Window component ) {
        // component.setAlwaysOnTop( true );
        component.addComponentListener( new ComponentAdapter() {
            @Override
            public void componentShown( ComponentEvent e ) {
                component.toFront();
            }
        } );
    }

    /**
     * 
     * @return center of the largest open window/frame
     */
    public static Point getCenterOfMainFrame() {
        Frame[] frames = Frame.getFrames();
        int w = 0;
        int h = 0;
        int x = 0;
        int y = 0;
        for ( Frame frame : frames ) {
            if ( frame.getWidth() > w && frame.getHeight() > h ) {
                x = frame.getX();
                y = frame.getY();
                w = frame.getWidth();
                h = frame.getHeight();

            }
        }
        return new Point( x + w / 2, y + h / 2 );
    }

}
