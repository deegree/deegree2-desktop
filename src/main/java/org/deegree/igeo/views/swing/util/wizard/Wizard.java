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

package org.deegree.igeo.views.swing.util.wizard;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.SOUTH;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static javax.swing.BorderFactory.createTitledBorder;
import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;
import static javax.swing.KeyStroke.getKeyStroke;
import static org.deegree.igeo.i18n.Messages.get;
import static org.deegree.igeo.views.swing.util.GuiUtils.initPanel;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.deegree.igeo.views.swing.util.ProgressDialog;
import org.deegree.igeo.views.swing.util.panels.OkCancelPanel;

/**
 * <code>Wizard</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class Wizard extends JFrame implements ActionListener {

    private static final long serialVersionUID = -2550849294780165975L;

    private List<JPanel> panels;

    protected List<Action> actions;

    protected int position;

    private JButton next, previous, cancel;

    /**
     * 
     */
    public boolean clickedOK;

    /**
     * This is the actual constructor.
     * 
     * @param panels
     * @param actions
     * @param disableCancel
     */
    public void init( List<JPanel> panels, List<Action> actions, boolean disableCancel ) {
        this.panels = panels;
        this.actions = actions;

        // text for next button will set in method checkButtons()
        next = new JButton( );
        previous = new JButton( "< " + get( "$DI10025" ) );
        URL cancelIcon = OkCancelPanel.class.getResource( "/org/deegree/igeo/views/images/cancel.png" );
        cancel = new JButton( get( "$DI10002" ), new ImageIcon( cancelIcon ) );
        next.addActionListener( this );
        previous.addActionListener( this );
        cancel.addActionListener( this );

        init();
        pack();
        setLocationRelativeTo( getOwner() );
        setResizable( false );

        for ( JPanel p : panels ) {
            p.setVisible( false );
        }

        if ( panels.size() > 0 ) {
            panels.get( 0 ).setVisible( true );
            setTitle( panels.get( 0 ).toString() );
        }
        checkButtons();

        getRootPane().setDefaultButton( next );
        KeyStroke stroke = getKeyStroke( VK_ESCAPE, 0 );
        getRootPane().registerKeyboardAction( this, "cancel", stroke, WHEN_IN_FOCUSED_WINDOW );
        stroke = getKeyStroke( '\b', 0 );
        getRootPane().registerKeyboardAction( this, "back", stroke, WHEN_IN_FOCUSED_WINDOW );

        if ( disableCancel ) {
            cancel.getParent().remove( cancel );
        }
    }

    private void initPanels() {
        GridBagConstraints gb = new GridBagConstraints();
        gb.gridx = 0;
        gb.gridy = 0;
        gb.insets = new Insets( 2, 2, 2, 2 );

        JPanel panel = (JPanel) getContentPane();

        for ( JPanel p : panels ) {
            panel.remove( p );
        }

        gb.fill = BOTH;
        gb.weighty = 1;

        for ( JPanel p : panels ) {
            panel.add( p, gb );
            panel.setVisible( true );
            p.setBorder( createTitledBorder( p.toString() ) );
        }

        for ( Action a : actions ) {
            // some will be added several times, if panels are added/removed
            // does no harm, though
            a.addListener( this );
        }

    }

    private void init() {
        JPanel panel = (JPanel) getContentPane();

        GridBagConstraints gb = initPanel( (JPanel) getContentPane() );
        initPanels();

        JPanel buttons = new JPanel();
        GridBagConstraints gb2 = initPanel( buttons );
        gb2.anchor = SOUTH;
        buttons.add( previous, gb2 );
        ++gb2.gridx;
        buttons.add( next, gb2 );
        ++gb2.gridx;
        buttons.add( cancel, gb2 );

        gb.weighty = 0;
        ++gb.gridy;
        gb.anchor = SOUTH;
        panel.add( buttons, gb );
    }

    private void checkButtons() {
        if ( panels.size() > 0 ) {
            previous.setEnabled( position != 0 );
            if ( position == panels.size() - 1 )
                next.setText( get( "$DI10026" ) );
            else
                next.setText( get( "$DI10024" ) + " >" );

            next.setEnabled( actions.get( position ).canForward() );
        }
    }

    private void updatePanel( int oldPos, int newPos ) {
        if ( oldPos == newPos ) {
            return;
        }

        Action action = actions.get( oldPos );
        if ( oldPos > newPos ) {
            if ( !action.backward() ) {
                ++position;
                return;
            }
        } else {
            if ( !action.forward() ) {
                --position;
                return;
            }
        }

        panels.get( oldPos ).setVisible( false );
        panels.get( newPos ).setVisible( true );
        JPanel p = panels.get( newPos );
        setTitle( p.toString() );
    }

    /**
     * @param panel
     */
    public void removePanel( JPanel panel ) {
        int idx = panels.indexOf( panel );
        if ( idx == -1 || position == idx ) {
            return;
        }
        panels.remove( panel );
        actions.remove( idx );
        initPanels();
        checkButtons();
    }

    /**
     * @param panel
     * @param action
     */
    public void addPanel( JPanel panel, Action action ) {
        panels.add( panel );
        actions.add( action );
        checkButtons();
        panel.setVisible( false );
    }

    /**
     * @param idx
     * @param panel
     * @param action
     */
    public void addPanel( int idx, JPanel panel, Action action ) {
        if ( position == idx ) {
            return;
        }
        panels.add( idx, panel );
        actions.add( idx, action );
        initPanels();
        checkButtons();
        panel.setVisible( false );
    }

    public void actionPerformed( ActionEvent e ) {
        if ( e.getSource() == next ) {
            if ( position == panels.size() - 1 ) {
                final ProgressDialog dlg = new ProgressDialog( this );
                new Thread() {
                    @Override
                    public void run() {
                        if ( actions.get( position ).forward() ) {
                            clickedOK = true;
                            setVisible( false );
                        }

                        dlg.dispose();
                        dispose();
                    }
                }.start();
                dlg.setVisible( true );
            } else {
                updatePanel( position, ++position );
            }
        }

        if ( e.getSource() == previous || e.getActionCommand().equals( "back" ) ) {
            if ( position == 0 )
                return;
            updatePanel( position, --position );
        }

        if ( e.getSource() == cancel || e.getActionCommand().equals( "cancel" ) ) {
            clickedOK = false;
            setVisible( false );
        }

        checkButtons();
    }

    /**
     * <code>Action</code>
     * 
     * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    public static interface Action {

        /**
         * @return true, if the forward action could be executed
         */
        public boolean forward();

        /**
         * @return true, if the backward action could be executed
         */
        public boolean backward();

        /**
         * @return true, if the forward action can be executed
         */
        public boolean canForward();

        /**
         * Adds the listener to components. Necessary so canForward is invoked.
         * 
         * @param listener
         */
        public void addListener( ActionListener listener );

    }

    /**
     * 
     */
    public void disableDefaultButton() {
        getRootPane().setDefaultButton( null );
    }

    /**
     * 
     */
    public void enableDefaultButton() {
        getRootPane().setDefaultButton( next );
    }

}
