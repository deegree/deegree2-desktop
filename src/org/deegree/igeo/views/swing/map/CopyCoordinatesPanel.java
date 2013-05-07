package org.deegree.igeo.views.swing.map;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.state.mapstate.MapTool;

public class CopyCoordinatesPanel extends JPanel implements MouseListener {

    private static final long serialVersionUID = -6526681726375085230L;

    private static final ILogger LOG = LoggerFactory.getLogger( CopyCoordinatesPanel.class );

    private final MapTool<?> mapTool;

    private Container mapParent;

    public CopyCoordinatesPanel( MapTool<?> mapTool, Container parent ) {
        this.mapTool = mapTool;
        mapParent = getMapParent( parent );
        if ( mapParent == null ) {
            LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10072" ) );
            return;
        }
        connect();
    }

    @Override
    public void mouseClicked( MouseEvent event ) {
        if ( !event.isPopupTrigger() ) {
            mapTool.getState().mousePressed( event );
        }
    }

    @Override
    public void mousePressed( MouseEvent e ) {
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

    public void connect() {
        if ( mapParent != null ) {
            mapParent.addMouseListener( this );
            mapParent.add( this, 0 );
        }
    }

    public void disconnect() {
        if ( mapParent != null ) {
            mapParent.removeMouseListener( this );
            mapParent.remove( this );
        }
    }

    private Container getMapParent( Container parent ) {
        if ( parent instanceof JFrame ) {
            JFrame viewForm = (JFrame) parent;
            Component[] components = viewForm.getContentPane().getComponents();
            return getMapParent( components );
        } else if ( parent instanceof JInternalFrame ) {
            JInternalFrame viewForm = (JInternalFrame) parent;
            Component[] components = viewForm.getContentPane().getComponents();
            return getMapParent( components );
        }
        return null;
    }

    private Container getMapParent( Component[] components ) {
        for ( int i = 0; i < components.length; i++ ) {
            if ( components[i] instanceof DefaultMapComponent ) {
                return (Container) components[i];
            }
        }
        return null;
    }

}