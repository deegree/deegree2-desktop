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

package org.deegree.igeo.views.swing.util.panels;

import static org.deegree.igeo.views.swing.util.GuiUtils.initPanel;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * <code>PanelDialog</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class PanelDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = -1711151380930168325L;

    private Component panel;

    private OkCancelPanel okcancel;

    /**
     * 
     */
    public boolean clickedOk;

    private OkCheck okCheck;

    /**
     * @param panel
     * @param cancel
     */
    public PanelDialog( Component panel, boolean cancel ) {
        this.panel = panel;
        init( cancel );
        pack();
        setResizable( false );
        setModal( true );
        setTitle( panel.toString() );
    }

    public PanelDialog( Component panel, boolean cancel, OkCheck okCheck ) {
        this( panel, cancel );
        this.okCheck = okCheck;
    }

    /**
     * @param parent
     * @param panel
     * @param cancel
     */
    public PanelDialog( JFrame parent, Component panel, boolean cancel ) {
        super( parent );
        this.panel = panel;
        init( cancel );
        pack();
        setResizable( false );
        setModal( true );
        setLocationRelativeTo( parent );
        setTitle( panel.toString() );
    }

    /**
     * @param parent
     * @param panel
     * @param cancel
     * @return a new panel dialog with the parent's top level ancestor as owner
     */
    public static PanelDialog create( JComponent parent, Component panel, boolean cancel ) {
        if ( parent != null && parent.getTopLevelAncestor() instanceof JFrame ) {
            return new PanelDialog( (JFrame) parent.getTopLevelAncestor(), panel, cancel );
        }
        if ( parent != null && parent.getTopLevelAncestor() instanceof JDialog ) {
            return new PanelDialog( (JDialog) parent.getTopLevelAncestor(), panel, cancel );
        }
        return new PanelDialog( panel, cancel );
    }

    /**
     * @param parent
     * @param panel
     * @param cancel
     * @return a new panel dialog with the parent's top level ancestor as owner
     */
    public static PanelDialog create( JFrame parent, Component panel, boolean cancel ) {
        if ( parent != null ) {
            return new PanelDialog( parent, panel, cancel );
        }

        return new PanelDialog( panel, cancel );
    }

    /**
     * @param parent
     * @param panel
     * @param cancel
     */
    public PanelDialog( JDialog parent, Component panel, boolean cancel ) {
        super( parent );
        this.panel = panel;
        init( cancel );
        pack();
        setResizable( false );
        setModal( true );
        setLocationRelativeTo( parent );
        setTitle( panel.toString() );
    }

    /**
     * @param parent
     * @param panel
     * @param okCheck
     * @param cancel
     */
    public PanelDialog( JFrame parent, Component panel, OkCheck okCheck, boolean cancel ) {
        this( parent, panel, cancel );
        this.okCheck = okCheck;
    }

    private void init( boolean cancel ) {
        JPanel p = (JPanel) getContentPane();
        GridBagConstraints gb = initPanel( p );

        p.add( panel, gb );
        ++gb.gridy;

        okcancel = new OkCancelPanel( this, getRootPane(), cancel );
        p.add( okcancel, gb );
    }

    public void actionPerformed( ActionEvent e ) {
        if ( e.getSource() == getRootPane() || e.getSource() == okcancel.cancelButton ) {
            clickedOk = false;
        }

        if ( e.getSource() == okcancel.okButton ) {
            if ( okCheck != null ) {
                if ( !okCheck.isOk() ) {
                    return;
                }
            }
            clickedOk = true;
        }

        setVisible( false );
    }

    /**
     * <code>OkCheck</code>
     * 
     * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    public interface OkCheck {
        /**
         * @return true if ok was clicked
         */
        public boolean isOk();
    }

}
