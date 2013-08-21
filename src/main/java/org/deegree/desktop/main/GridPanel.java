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

package org.deegree.desktop.main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Extension of {@link JPanel} required for creating grid-based layouts
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
class GridPanel extends JPanel {

    private static final long serialVersionUID = 8901599643361748299L;

    private int noOfColumns;

    private int noOfRows = 0;

    private int[] columnWidth;

    private int horizontalSpace;

    private int verticalSpace;

    private boolean autodistance;

    private boolean useColumnBorders;

    /**
     * 
     * @param columnWidth
     *            desired column widths
     * @param horizontalSpace
     *            space between columns
     * @param autodistance
     *            enforced horizontal space between columns
     * @param useColumnBorders
     *            true if Borders around each column should be drawn
     */
    public GridPanel( int[] columnWidth, int horizontalSpace, int verticalSpace, boolean autodistance,
                      boolean useColumnBorders ) {
        this.columnWidth = columnWidth;
        this.horizontalSpace = horizontalSpace;
        this.verticalSpace = verticalSpace;
        this.autodistance = autodistance;
        this.useColumnBorders = useColumnBorders;
        noOfColumns = columnWidth.length;
        createPanel();
    }

    /**
     * creates the basic panel
     * 
     */
    private void createPanel() {
        setBackground( Color.LIGHT_GRAY );
        setLayout( new BoxLayout( this, BoxLayout.LINE_AXIS ) );

        for ( int i = 0; i < columnWidth.length; i++ ) {
            JPanel tmp = new JPanel();
            tmp.setMinimumSize( new Dimension( columnWidth[i], 20 ) );
            tmp.setSize( new Dimension( columnWidth[i], 00 ) );
            tmp.setLayout( new BoxLayout( tmp, BoxLayout.PAGE_AXIS ) );
            if ( useColumnBorders ) {
                tmp.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
            } else {
                tmp.setBorder( BorderFactory.createEmptyBorder( 3, 3, 3, 3 ) );
            }
            tmp.setAlignmentY( 0 );
            add( tmp );
            if ( autodistance ) {
                add( Box.createHorizontalGlue() );
            }
            add( Box.createRigidArea( new Dimension( horizontalSpace, 0 ) ) );
        }
    }

    /**
     * 
     * @param row
     * @param column
     * @return component at the passed cell coordinate
     */
    public JComponent get( int row, int column ) {
        JPanel panel = getColumn( column );

        return (JComponent) panel.getComponent( row );
    }

    /**
     * 
     * @param column
     * @return panel at the passed column
     */
    private JPanel getColumn( int column ) {
        int dx = 2;
        if ( autodistance ) {
            dx++;
        }
        dx *= column;
        return (JPanel) getComponent( dx );
    }

    /**
     * adds component to a GridPanel
     * 
     * @param value
     * @param row
     * @param column
     */
    public void add( JComponent value, int row, int column ) {
        boolean delete = false;
        JPanel panel = getColumn( column );

        if ( row > panel.getComponentCount() - 1 ) {
            for ( int i = panel.getComponentCount(); i < row; i++ ) {
                JPanel tmp = new JPanel();
                // tmp.setBackground( panel.getBackground() );
                tmp.setBackground( Color.yellow );
                tmp.setMinimumSize( new Dimension( 10, 10 ) );
                panel.add( tmp, i );
            }
        } else {
            delete = true;
        }
        if ( row > noOfRows ) {
            noOfRows = row;
        }
        value.setBorder( BorderFactory.createEmptyBorder( verticalSpace / 2, 0, verticalSpace / 2, 0 ) );
        panel.add( value, row );
        if ( delete ) {
            panel.remove( row + 1 );
        }
    }

    /**
     * removes a component identified by its cell coordiantes from GridPanel
     * 
     * @param row
     * @param column
     */
    public void remove( int row, int column ) {
        JPanel panel = getColumn( column );
        panel.remove( row );
    }

    /**
     * removes a component from GridPanel (if a component exist several times each occurence will be
     * removed)
     * 
     * @param value
     */
    public void remove( JComponent value ) {
        for ( int i = 0; i < noOfColumns; i++ ) {
            JPanel panel = getColumn( i );
            Component[] comps = panel.getComponents();
            for ( int j = 0; j < comps.length; j++ ) {
                // yes, we do use '==' and noe equal
                if ( comps[i] == value ) {
                    panel.remove( comps[i] );
                }
            }
        }
    }

    /**
     * number of columns
     * 
     * @return
     */
    public int getColumnCount() {
        return noOfColumns;
    }

    /**
     * 
     * @param column
     * @return maximum number of rows within a column
     */
    public int getRowCount( int column ) {
        JPanel panel = getColumn( column );
        return panel.getComponentCount();
    }

    /**
     * 
     * @return maximum number of rows within a column
     */
    public int getMaxRowCount() {
        return noOfRows;
    }

}
