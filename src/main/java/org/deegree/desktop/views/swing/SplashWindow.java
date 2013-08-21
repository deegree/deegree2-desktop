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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JWindow;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.kernel.Command;
import org.deegree.kernel.ProcessMonitor;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class SplashWindow extends JWindow implements ProcessMonitor, MouseListener, MouseMotionListener {

    private static final long serialVersionUID = 4526103047947164641L;

    private static final ILogger LOG = LoggerFactory.getLogger( SplashWindow.class );

    private boolean canceled = false;

    private String status = "loading ...";

    private JLabel label = new JLabel( status );

    /**
     * 
     * @param contents
     */
    public SplashWindow( JComponent contents ) {
        setSize( 420, 303 );
        label.setFont( new Font( "ARIAL", Font.PLAIN, 16 ) );
        label.setBounds( 180, 240, 220, 40 );
        label.setVisible( true );
        getContentPane().setLayout( null );
        getContentPane().add( label );
        getContentPane().add( contents );

        addMouseListener( this );
        addMouseMotionListener( this );

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = contents.getPreferredSize();
        setLocation( ( screenSize.width / 2 ) - ( labelSize.width / 2 ), ( screenSize.height / 2 )
                                                                         - ( labelSize.height / 2 ) );
        new Thread( this ).start();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.ProcessMonitor#init(java.lang.String, java.lang.String, int, int,
     * org.deegree.kernel.Command)
     */
    public void init( String title, String message, int min, int max, Command command ) {
        setMinimumValue( min );
        setMaximumValue( max );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.ProcessMonitor#setMaximumValue(int)
     */
    public void setMaximumValue( int maximum ) {
        // not to be used
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.ProcessMonitor#setMinimunValue(int)
     */
    public void setMinimumValue( int minimum ) {
        // not to be used
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.ProcessMonitor#cancel()
     */
    public void cancel()
                            throws Exception {
        canceled = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.ProcessMonitor#isCanceled()
     */
    public boolean isCanceled() {
        return canceled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.ProcessMonitor#updateStatus(int, java.lang.String)
     */
    public void updateStatus( int itemsDone, String itemDescription ) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.ProcessMonitor#updateStatus(java.lang.String)
     */
    public void updateStatus( String description ) {
        status = description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {
        while ( !canceled ) {
            try {
                Thread.sleep( 50 );
                label.setText( status );
            } catch ( Exception e ) {
                LOG.logWarning( "ignore", e );
            }
        }
        dispose();
    }

    private Point location;

    private int pressedX;

    private int pressedY;

    @Override
    public void mouseDragged( MouseEvent e ) {
        location = getLocation( location );
        int x = location.x - pressedX + e.getX();
        int y = location.y - pressedY + e.getY();
        setLocation( x, y );
    }

    @Override
    public void mouseMoved( MouseEvent e ) {
    }

    @Override
    public void mouseClicked( MouseEvent e ) {
    }

    @Override
    public void mousePressed( MouseEvent e ) {
        pressedX = e.getX();
        pressedY = e.getY();
    }

    @Override
    public void mouseReleased( MouseEvent e ) {
    }

    @Override
    public void mouseEntered( MouseEvent e ) {
    }

    @Override
    public void mouseExited( MouseEvent e ) {
    }

}
