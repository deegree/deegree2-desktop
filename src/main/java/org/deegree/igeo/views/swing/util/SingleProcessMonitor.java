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

package org.deegree.igeo.views.swing.util;

import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.i18n.Messages;
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
public class SingleProcessMonitor extends JDialog implements ProcessMonitor {

    private static final long serialVersionUID = 2657747499480703667L;

    private static final ILogger LOG = LoggerFactory.getLogger( SingleProcessMonitor.class );

    private JProgressBar jProgressBar;

    private JLabel information;

    private JButton btCancel;

    private JLabel messageLabel;

    private volatile boolean canceled = false;

    private int itemsDone;

    private String description;

    /**
     * 
     */
    public SingleProcessMonitor() {
        Frame[] frames = Frame.getFrames();
        Frame frame = frames[0];
        for ( int i = 0; i < frames.length; i++ ) {
            if ( "iGeoDesktop".equals( frames[i].getName() ) ) {
                frame = frames[i];
                break;
            }
        }
        // set dialog position to center of the root frame
        Rectangle rectangle = frame.getBounds();
        this.setBounds( rectangle.x + rectangle.width / 2 - 175, rectangle.y + rectangle.height / 2 - 55, 353, 113 );
        setAlwaysOnTop( true );
    }

    /**
     * if max < min than an infinite progress bar will be displayed
     * 
     * @param title
     * @param message
     * @param min
     * @param max
     */
    public void init( String title, String message, int min, int max, Command command ) {
        this.setTitle( title );
        initGUI( message );
        if ( max > min ) {
            setMinimumValue( min );
            setMaximumValue( max );
        } else {
            jProgressBar.setIndeterminate( true );
        }
        setVisible( true );
        repaint();

        addWindowListener( new WindowAdapter() {
            public void windowOpened( WindowEvent e ) {
                new Thread( SingleProcessMonitor.this ).start();
            }
        } );

    }

    private void initGUI( String message ) {
        try {
            {
                getContentPane().setLayout( null );
                {
                    messageLabel = new JLabel();
                    getContentPane().add( messageLabel );
                    messageLabel.setText( message );
                    messageLabel.setBounds( 44, 6, 289, 14 );
                }
                {
                    jProgressBar = new JProgressBar();
                    getContentPane().add( jProgressBar );
                    jProgressBar.setBounds( 12, 27, 321, 14 );
                }
                {
                    btCancel = new JButton( Messages.getMessage( getLocale(), "$MD10888" ),
                                            IconRegistry.getIcon( "cancel.png" ) );
                    getContentPane().add( btCancel );
                    btCancel.setBounds( 12, 53, 133, 21 );
                    btCancel.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            canceled = true;
                        }

                    } );
                }
                {
                    information = new JLabel();
                    getContentPane().add( information );
                    information.setIcon( IconRegistry.getIcon( "information.png" ) );
                    information.setBounds( 12, 5, 16, 16 );
                }
            }
            this.setSize( 353, 113 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.ProcessMonitor#setMaximumValue(int)
     */
    public void setMaximumValue( int maximum ) {
        jProgressBar.setMaximum( maximum );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.ProcessMonitor#setMinimunValue(int)
     */
    public void setMinimumValue( int minimum ) {
        jProgressBar.setMinimum( minimum );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.ProcessMonitor#cancel()
     */
    public void cancel()
                            throws Exception {
        canceled = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.ProcessMonitor#updateStatus(int, java.lang.String)
     */
    public void updateStatus( int itemsDone, String itemDescription ) {
        this.itemsDone = itemsDone;
        this.description = itemDescription;
        jProgressBar.setValue( itemsDone );
        jProgressBar.setString( description );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.ProcessMonitor#updateStatus(java.lang.String)
     */
    public void updateStatus( String description ) {
        this.description = description;
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
     * @see java.lang.Runnable#run()
     */
    public void run() {

        while ( !canceled ) {
            try {
                Thread.sleep( 50 );
                jProgressBar.setValue( itemsDone );
                jProgressBar.setString( description );
            } catch ( Exception e ) {
                LOG.logWarning( e.getMessage(), e );
            }
        }
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                dispose();
            }
        } );

    }

}
