//$HeadURL: svn+ssh://aschmitz@wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2011 by:
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
package org.deegree.igeo.modules.georef;

import static org.deegree.igeo.modules.georef.ControlPointModel.State.Left;
import static org.deegree.igeo.modules.georef.ControlPointModel.State.Right;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.deegree.igeo.i18n.Messages;

/**
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: stranger $
 * 
 * @version $Revision: $, $Date: $
 */
public class ControlPointModel extends AbstractTableModel {

    private static final long serialVersionUID = -4947856920124504250L;

    private LinkedList<Point> points = new LinkedList<Point>();

    private State state = Left;

    @Override
    public int getRowCount() {
        return points.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public String getColumnName( int columnIndex ) {
        switch ( columnIndex ) {
        case 0:
            return "<html>" + Messages.get( "$DI10079" ) + "<br>x";
        case 1:
            return "<html>" + Messages.get( "$DI10079" ) + "<br>y";
        case 2:
            return "<html>" + Messages.get( "$DI10080" ) + "<br>x";
        case 3:
            return "<html>" + Messages.get( "$DI10080" ) + "<br>y";
        case 4:
            return "<html>" + Messages.get( "$DI10084" ) + "<br>x";
        case 5:
            return "<html>" + Messages.get( "$DI10084" ) + "<br>y";
        }
        return null;
    }

    @Override
    public Class<?> getColumnClass( int columnIndex ) {
        return Double.class;
    }

    @Override
    public boolean isCellEditable( int rowIndex, int columnIndex ) {
        return columnIndex <= 3;
    }

    @Override
    public Object getValueAt( int rowIndex, int columnIndex ) {
        Point p = points.get( rowIndex );
        switch ( columnIndex ) {
        case 0:
            return p.x0;
        case 1:
            return p.y0;
        case 2:
            return p.x1;
        case 3:
            return p.y1;
        case 4:
            return p.resx;
        case 5:
            return p.resy;
        }
        return null;
    }

    @Override
    public void setValueAt( Object aValue, int rowIndex, int columnIndex ) {
        Double val = (Double) aValue;
        Point p = points.get( rowIndex );
        switch ( columnIndex ) {
        case 0:
            p.x0 = val;
            break;
        case 1:
            p.y0 = val;
            break;
        case 2:
            p.x1 = val;
            break;
        case 3:
            p.y1 = val;
            break;
        }
        fireTableDataChanged();
    }

    public void newPoint() {
        points.add( new Point() );
        state = Left;
        fireTableDataChanged();
    }

    public void removeAll() {
        points.clear();
        state = Left;
        fireTableDataChanged();
    }

    public void setLeft( int idx, double x, double y ) {
        Point p = points.get( idx );
        p.x0 = x;
        p.y0 = y;
        if ( idx == ( points.size() - 1 ) && p.x1 == null ) {
            state = Right;
        }
        fireTableDataChanged();
    }

    public void setRight( int idx, double x, double y ) {
        Point p = points.get( idx );
        p.x1 = x;
        p.y1 = y;
        if ( idx == ( points.size() - 1 ) ) {
            state = Left;
        }
        fireTableDataChanged();
    }

    public void remove( int idx ) {
        if ( points.size() == ( idx - 1 ) ) {
            state = Left;
        }
        points.remove( idx );
        fireTableDataChanged();
    }

    public void next( double x, double y ) {
        if ( points.isEmpty() ) {
            newPoint();
        }
        switch ( state ) {
        case Left:
            state = Right;
            points.getLast().x0 = x;
            points.getLast().y0 = y;
            break;
        case Right:
            points.getLast().x1 = x;
            points.getLast().y1 = y;
            newPoint();
            state = Left;
            break;
        }
        fireTableDataChanged();
    }

    static class Point {
        Double x0, y0, x1, y1, resx, resy;
    }

    public State getState() {
        return state;
    }

    public List<Point> getPoints() {
        return points;
    }

    public static enum State {
        Left, Right
    }

}
