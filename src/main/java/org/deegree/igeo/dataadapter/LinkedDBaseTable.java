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
package org.deegree.igeo.dataadapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.deegree.datatypes.Types;
import org.deegree.framework.util.Pair;
import org.deegree.igeo.config.AbstractLinkedTableType;
import org.xBaseJ.DBF;
import org.xBaseJ.xBaseJException;
import org.xBaseJ.fields.DateField;
import org.xBaseJ.fields.Field;
import org.xBaseJ.fields.LogicalField;
import org.xBaseJ.fields.NumField;

/**
 * concrete {@link LinkedTable} for accessing dBase files
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class LinkedDBaseTable extends LinkedTable {

    private String[] columnNames;

    private DBF classDB;

    private int[] types;

    /**
     * @param linkedTableType
     * @throws IOException
     * @throws xBaseJException
     */
    public LinkedDBaseTable( AbstractLinkedTableType linkedTableType, File file ) throws IOException {
        super( linkedTableType );
        try {
            classDB = new DBF( file.getAbsolutePath() );
            int c = classDB.getFieldCount();
            columnNames = new String[c];
            types = new int[c];
            for ( int i = 0; i < c; i++ ) {
                columnNames[i] = classDB.getField( i + 1 ).getName();
                types[i] = getType( classDB.getField( i + 1 ) );
            }
        } catch ( xBaseJException e ) {
            throw new IOException( e.getMessage() );
        }

    }

    /**
     * @param field
     * @return
     */
    private int getType( Field field ) {
        int tp = Types.VARCHAR;
        char type = field.getType();
        switch ( type ) {
        case 'C': {
            tp = Types.VARCHAR;
            break;
        }
        case 'L': {
            tp = Types.BOOLEAN;
            break;
        }
        case 'N': {
            tp = Types.FLOAT;
            break;
        }
        case 'D': {
            tp = Types.DATE;
            break;
        }
        case 'F': {
            tp = Types.FLOAT;
            break;
        }
        case 'I': {
            tp = Types.INTEGER;
            break;
        }
        case 'O': {
            tp = Types.DOUBLE;
            break;
        }
        case 'T':
            tp = Types.DATE;
            break;

        default: {
            tp = Types.VARCHAR;
            break;
        }
        }
        return tp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.mapmodel.LinkedTable#getRow(int)
     */
    public Object[] getRow( int rowNo )
                            throws IOException {
        Object[] row = null;
        try {
            classDB.startTop();
            if ( rowNo > 0 ) {
                classDB.gotoRecord( rowNo );
            }
            classDB.read();
            row = readDBaseRow( new Object[columnNames.length] );
        } catch ( xBaseJException e ) {
            throw new IOException( e.getMessage() );
        }

        return row;
    }

    private Object[] readDBaseRow( Object[] row )
                            throws xBaseJException {

        for ( int i = 0; i < row.length; i++ ) {
            Field field = classDB.getField( i + 1 );
            String v = field.get().trim();
            char type = field.getType();
            switch ( type ) {
            case 'C': {
                row[i] = v;
                break;
            }
            case 'L': {
                row[i] = ( (LogicalField) field ).getBoolean();
                break;
            }
            case 'N': {
                if ( ( (NumField) field ).getDecimalPositionCount() > 0 ) {
                    row[i] = Float.parseFloat( v );
                } else {
                    row[i] = Integer.parseInt( v );
                }
                break;
            }
            case 'D': {
                row[i] = ( (DateField) field ).getCalendar().getTime();
                break;
            }
            case 'F': {
                row[i] = Float.parseFloat( v );
                break;
            }
            case 'I': {
                row[i] = Integer.parseInt( v );
                break;
            }
            case 'O': {
                row[i] = Double.parseDouble( v );
                break;
            }
            case 'T':
                row[i] = ( (DateField) field ).getCalendar().getTime();
                break;

            default: {
                row[i] = v;
                break;
            }
            }
        }
        return row;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.deegree.igeo.mapmodel.LinkedTable#getRow(org.deegree.framework.util.Pair<java.lang.String,java.lang.String
     * >[])
     */
    public Object[][] getRows( Pair<String, Object>... keys )
                            throws IOException {

        List<Object[]> rows = new ArrayList<Object[]>( 10 );
        try {
            classDB.startTop();
            Object[] row = new Object[columnNames.length];
            for ( int i = 0; i < classDB.getRecordCount(); i++ ) {
                classDB.read();
                row = readDBaseRow( row );
                boolean match = true;
                for ( Pair<String, Object> pair : keys ) {
                    int idx = getIndexForColumnName( pair.first );
                    if ( !pair.second.equals( row[idx] ) ) {
                        match = false;
                        break;
                    }
                }
                if ( match ) {
                    rows.add( row );
                    row = new Object[columnNames.length];
                }
            }
        } catch ( xBaseJException e ) {
            throw new IOException( e.getMessage() );
        }

        return rows.toArray( new Object[rows.size()][] );
    }

    private int getIndexForColumnName( String name ) {
        for ( int i = 0; i < columnNames.length; i++ ) {
            if ( columnNames[i].equalsIgnoreCase( name ) ) {
                return i;
            }
        }
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.mapmodel.LinkedTable#getColumnCount()
     */
    public int getColumnCount() {
        return columnNames.length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.mapmodel.LinkedTable#getRowCount()
     */
    public int getRowCount() {
        return classDB.getRecordCount();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.dataadapter.LinkedTable#getColumnTypes()
     */
    public int[] getColumnTypes() {
        return types;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.mapmodel.LinkedTable#getColumnNames()
     */
    public String[] getColumnNames() {
        return columnNames;
    }

}
