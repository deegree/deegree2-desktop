//$HeadURL: svn+ssh://lbuesching@svn.wald.intevation.de/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2012 by:
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

import java.io.IOException;

import org.deegree.framework.util.Pair;
import org.deegree.igeo.config.LinkedFileTableType;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class LinkedFileTable extends LinkedTable {

    public LinkedFileTable() {
        super( new LinkedFileTableType() );
    }

    public LinkedFileTable( LinkedFileTableType linkedTableType ) {
        super( linkedTableType );
    }

    /**
     * @param file
     */
    public void setFile( String file ) {
        ( (LinkedFileTableType) getLinkedTableType() ).setFile( file );
    }

    @Override
    public int getRowCount() {
        return 0;
    }

    @Override
    public int getColumnCount() {
        return 0;
    }

    @Override
    public String[] getColumnNames() {
        return new String[0];
    }

    @Override
    public int[] getColumnTypes() {
        return new int[0];
    }

    @Override
    public Object[] getRow( int rowNo )
                            throws IOException {
        return null;
    }

    @Override
    public Object[][] getRows( Pair<String, Object>... keys )
                            throws IOException {
        return null;
    }

}
