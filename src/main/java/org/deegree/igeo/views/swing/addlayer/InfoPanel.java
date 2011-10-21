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

package org.deegree.igeo.views.swing.addlayer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.deegree.framework.utils.SwingUtils;

/**
 * <code>InfoPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class InfoPanel extends JPanel {

    private static final long serialVersionUID = -4558751929819970476L;

    private JLabel textLabel;

    public InfoPanel() {
        Dimension dim = new Dimension( 300, 75 );
        this.setSize( dim );
        this.setPreferredSize( dim );
        this.setMinimumSize( dim );
        this.setMaximumSize( dim );
        this.setVisible( true );
        GridBagConstraints gbc = SwingUtils.initPanel( this );
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        textLabel = new JLabel();
        textLabel.setVisible( true );
        add( textLabel, gbc );
    }

    public void setInfoText( String infoText ) {
        Font font = new Font( "Dialog", Font.PLAIN, 12 );
        textLabel.setFont( font );
        textLabel.setText( "<html><body>" + infoText + "</body></html>" );
    }

}
