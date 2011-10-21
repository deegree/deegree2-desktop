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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.deegree.datatypes.Types;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.Pair;
import org.deegree.framework.util.StringTools;
import org.deegree.igeo.config.AbstractLinkedTableType;

/**
 * concrete {@link LinkedTable} for accessing CSV files
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class LinkedCSVTable extends LinkedTable {

    private static final ILogger LOG = LoggerFactory.getLogger( LinkedCSVTable.class );

    private String[] columnNames;

    private CSV csv;

    private int[] types;

    /**
     * @param linkedTableType
     * @throws IOException
     */
    public LinkedCSVTable( AbstractLinkedTableType linkedTableType, File file ) throws IOException {
        super( linkedTableType );
        csv = new CSV( file.getAbsolutePath() );
        columnNames = csv.getHeader();
        types = new int[columnNames.length];
        for ( int i = 0; i < types.length; i++ ) {
            types[i] = Types.VARCHAR;
        }
    }

    /**
     * 
     * @param seperator
     */
    public void setSeparator( String seperator ) {
        csv.setSeparator( seperator );
    }

    /**
     * @param encoding
     *            the encoding to set
     */
    public void setEncoding( String encoding ) {
        csv.setEncoding( encoding );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.mapmodel.LinkedTable#getRow(int)
     */
    public Object[] getRow( int rowNo )
                            throws IOException {
        csv.gotoRecord( rowNo );
        return csv.read();
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
        csv.startReading();
        for ( int i = 0; i < csv.getRecordCount(); i++ ) {
            Object[] row = csv.read();
            boolean match = true;
            for ( Pair<String, Object> pair : keys ) {
                int idx = getIndexForColumnName( pair.first );
                if ( !pair.second.toString().equals( row[idx] ) ) {
                    match = false;
                    break;
                }
            }
            if ( match ) {
                rows.add( row );
            }
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
        try {
            return csv.getRecordCount();
        } catch ( IOException e ) {
            LOG.logError( e );
            return -1;
        }
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

    /**
     * 
     * TODO add class documentation here
     * 
     * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    private class CSV {

        private File file;

        private int rowCount = -1;

        private String[] header;

        private BufferedReader reader;

        private int cursor;

        private String separator = ";\t";

        private String encoding = Charset.defaultCharset().displayName();

        /**
         * @param name
         * @throws IOException
         */
        public CSV( String name ) throws IOException {
            this.file = new File( name );
        }

        @Override
        protected void finalize()
                                throws Throwable {
            stopReading();
            super.finalize();
        }

        /**
         * @param encoding
         *            the encoding to set
         */
        public void setEncoding( String encoding ) {
            this.encoding = encoding;
        }

        /**
         * set token separator; default is ";\t"
         * 
         * @param separator
         */
        public void setSeparator( String separator ) {
            this.separator = separator;
        }

        /**
         * 
         * @return number of records
         * @throws IOException
         */
        public int getRecordCount()
                                throws IOException {
            if ( rowCount == -1 ) {
                try {
                    String s = startReading();
                    createHeader( s );
                    rowCount = 0;
                    while ( ( s = reader.readLine() ) != null ) {
                        rowCount++;
                    }
                    stopReading();
                } catch ( IOException e ) {
                    throw e;
                } finally {
                    stopReading();
                }

            }
            return rowCount;
        }

        /**
         * @param s
         */
        private void createHeader( String s ) {
            if ( header == null ) {
                header = StringTools.toArray( s, separator, false );
                for ( int i = 0; i < header.length; i++ ) {
                    header[i] = header[i].toUpperCase();
                }
            }
        }

        /**
         * 
         * @return names of the column headers
         * @throws IOException
         */
        public String[] getHeader()
                                throws IOException {
            getRecordCount();
            return header;
        }

        /**
         * 
         * @return first line (header)
         * @throws IOException
         */
        public String startReading()
                                throws IOException {
            stopReading();
            reader = new BufferedReader( new InputStreamReader( new FileInputStream( file ), encoding ) );
            return reader.readLine();
        }

        /**
         * 
         * @throws IOException
         */
        public void stopReading()
                                throws IOException {
            if ( reader != null ) {
                reader.close();
                reader = null;
            }
        }

        /**
         * 
         * @return next line of a CSV file
         * @throws IOException
         */
        public String[] read()
                                throws IOException {
            if ( reader == null ) {
                startReading();
            }
            String s = reader.readLine();
            if ( s == null ) {
                return null;
            } else {
                return StringTools.toArray( s, separator, false );
            }
        }

        /**
         * 
         * @param rowNo
         * @throws IOException
         */
        public void gotoRecord( int rowNo )
                                throws IOException {
            if ( reader == null || rowNo < cursor ) {
                startReading();
                cursor = 0;
            }
            while ( cursor < rowNo ) {
                reader.readLine();
                cursor++;
            }
        }
    }

}
