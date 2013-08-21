//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.desktop.views.swing.util.table;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.table.JTableHeader;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author <a href="http://www.esus.com/docs/GetQuestionPage.jsp?uid=1270&type=pf">Nobuo Tamemasa</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class HeaderListener extends MouseAdapter {
    protected JTableHeader header;

    protected SortButtonRenderer renderer;

    public HeaderListener( JTableHeader header, SortButtonRenderer renderer ) {
        this.header = header;
        this.renderer = renderer;
    }

    public void mousePressed( MouseEvent e ) {
        int col = header.columnAtPoint( e.getPoint() );
        int sortCol = header.getTable().convertColumnIndexToModel( col );
        renderer.setPressedColumn( col );
        renderer.setSelectedColumn( col );
        header.repaint();

        if ( header.getTable().isEditing() ) {
            header.getTable().getCellEditor().stopCellEditing();
        }

        boolean isAscent;
        if ( SortButtonRenderer.DOWN == renderer.getState( col ) ) {
            isAscent = true;
        } else {
            isAscent = false;
        }
        System.out.println( isAscent + " " + sortCol );
    }

    public void mouseReleased( MouseEvent e ) {
        renderer.setPressedColumn( -1 ); // clear
        header.repaint();
    }
}
