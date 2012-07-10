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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.Pair;
import org.deegree.igeo.config.AbstractLinkedTableType;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class LinkedExcelTable extends LinkedTable {

    private static final ILogger LOG = LoggerFactory.getLogger( LinkedExcelTable.class );

    private String[] columnNames;

    private int[] types;

    private Workbook workbook;

    private Sheet sheet;

    /**
     * 
     * @param linkedTableType
     * @param file
     * @param sheetName
     * @throws IOException
     */
    public LinkedExcelTable( AbstractLinkedTableType linkedTableType, File file ) throws IOException {
        super( linkedTableType );
        if ( file.getAbsolutePath().toLowerCase().endsWith( ".xls" ) ) {
            workbook = new HSSFWorkbook( new FileInputStream( file ) );
        } else {
            workbook = new XSSFWorkbook( new FileInputStream( file ) );
        }
        sheet = workbook.getSheetAt( 0 );
        LOG.logDebug( "load first excel sheet" );

        Iterator<Row> rowIter = sheet.rowIterator();
        Row firstRow = rowIter.next();

        List<String> headerNames = new ArrayList<String>();
        List<Integer> headerTypes = new ArrayList<Integer>();

        for ( Iterator<Cell> cit = firstRow.cellIterator(); cit.hasNext(); ) {
            Cell cell = cit.next();
            String cellValue = cell.getRichStringCellValue().getString();
            headerNames.add( cellValue );
            headerTypes.add( getCellType( cell.getCellType() ) );
        }
        columnNames = headerNames.toArray( new String[headerNames.size()] );
        types = new int[headerTypes.size()];
        for ( int i = 0; i < types.length; i++ ) {
            types[i] = headerTypes.get( i );
        }
    }

    private int getCellType( int type ) {
        int tp = Types.VARCHAR;
        switch ( type ) {
        case Cell.CELL_TYPE_BLANK:
            tp = Types.VARCHAR;
            break;
        case Cell.CELL_TYPE_BOOLEAN:
            tp = Types.BOOLEAN;
            break;
        case Cell.CELL_TYPE_NUMERIC:
            tp = Types.DOUBLE;
            break;
        case Cell.CELL_TYPE_STRING:
            tp = Types.VARCHAR;
            break;
        default:
            tp = Types.VARCHAR;
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
        Object[] row = new Object[columnNames.length];
        Row hssfRow = sheet.getRow( rowNo );
        for ( int i = 0; i < row.length; i++ ) {
            row[i] = hssfRow.getCell( i ).getRichStringCellValue().getString();
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
        Iterator<Row> rowIter = sheet.rowIterator();

        // dummy call to read first (header) row
        rowIter.next();

        Object[] row = new Object[columnNames.length];
        while ( rowIter.hasNext() ) {
            Row hssfRow = rowIter.next();
            row = readRow( row, hssfRow );
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
        return rows.toArray( new Object[rows.size()][] );
    }

    private Object[] readRow( Object[] row, Row hssfRow ) {
        for ( int i = 0; i < row.length; i++ ) {
            int type = Cell.CELL_TYPE_STRING;
            Cell cell = hssfRow.getCell( i );
            if ( cell != null ) {
                type = cell.getCellType();
                switch ( type ) {
                case Cell.CELL_TYPE_BLANK:
                    row[i] = "";
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    row[i] = new Boolean( hssfRow.getCell( i ).getBooleanCellValue() );
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    row[i] = hssfRow.getCell( i ).getNumericCellValue();
                    break;
                case Cell.CELL_TYPE_STRING:
                    row[i] = hssfRow.getCell( i ).getRichStringCellValue().toString();
                    break;
                default:
                    break;
                }
            }
        }
        return row;
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
        return sheet.getLastRowNum();
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
