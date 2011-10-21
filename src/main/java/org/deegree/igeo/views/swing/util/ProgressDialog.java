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

import static org.deegree.igeo.i18n.Messages.get;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * <code>ProgressDialog</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ProgressDialog extends JDialog {

    private static final long serialVersionUID = -7237129235588637730L;

    // TODO make some nice icon and stuff
    /**
     * @param owner
     */
    public ProgressDialog( JDialog owner ) {
        super( owner, get( "$DI10027" ), true );
        init();
    }

    /**
     * @param owner
     */
    public ProgressDialog( JFrame owner ) {
        super( owner, get( "$DI10027" ), true );
        init();
    }

    private void init() {
        JPanel panel = new JPanel();
        panel.setLayout( new GridBagLayout() );
        GridBagConstraints gb = new GridBagConstraints();
        gb.insets = new Insets( 5, 5, 5, 5 );
        panel.add( new JLabel( get( "$DI10028" ) ), gb );
        getContentPane().add( panel );
        pack();
        setResizable( false );
        setLocationRelativeTo( getOwner() );
        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
    }

}
