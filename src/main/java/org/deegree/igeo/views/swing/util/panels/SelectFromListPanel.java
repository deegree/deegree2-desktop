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

import static java.awt.GridBagConstraints.BOTH;
import static javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION;
import static org.deegree.igeo.views.swing.util.GuiUtils.addWithSize;
import static org.deegree.igeo.views.swing.util.GuiUtils.initPanel;

import java.awt.GridBagConstraints;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * <code>SelectFromListPanel</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class SelectFromListPanel extends JPanel {

    private static final long serialVersionUID = -7630339015307656617L;

    /**
     * 
     */
    public JList list;

    private String title;

    /**
     * @param title
     * 
     */
    public SelectFromListPanel( String title ) {
        this.title = title;
        GridBagConstraints gb = initPanel( this );

        list = new JList();
        list.setSelectionMode( MULTIPLE_INTERVAL_SELECTION );
        JScrollPane sp = new JScrollPane( list );
        gb.fill = BOTH;
        add( addWithSize( sp, 300, 300 ), gb );
    }

    @Override
    public String toString() {
        return title;
    }

}
