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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.Pair;
import org.deegree.igeo.config.AbstractLinkedTableType;
import org.deegree.igeo.config.RelationKeyType;
import org.deegree.igeo.config.Util;
import org.deegree.igeo.dataadapter.FeatureAdapter.Changes;
import org.deegree.model.feature.FeatureCollection;

/**
 * abstract class for reading alpha numeric data to be linked to layers
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public abstract class LinkedTable {

    private static final ILogger LOG = LoggerFactory.getLogger( LinkedTable.class );

    private List<Pair<QualifiedName, String>> relationKeys;

    private AbstractLinkedTableType linkedTableType;

    /**
     * @param linkedTableType
     */
    protected LinkedTable( AbstractLinkedTableType linkedTableType ) {
        this.linkedTableType = linkedTableType;
        List<RelationKeyType> list = this.linkedTableType.getRelationKey();
        relationKeys = new ArrayList<Pair<QualifiedName, String>>( list.size() );
        for ( RelationKeyType relationKeyType : list ) {
            QualifiedName qn = Util.convertQualifiedName( relationKeyType.getFeatureProperty() );
            String colName = relationKeyType.getTableColumn();
            relationKeys.add( new Pair<QualifiedName, String>( qn, colName ) );
        }
    }

    /**
     * @return the name
     */
    public String getTitle() {
        return linkedTableType.getTitle();
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle( String title ) {
        linkedTableType.setTitle( title );
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return linkedTableType.getDescription();
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription( String description ) {
        linkedTableType.setDescription( description );
    }

    /**
     * @return the relationKeys
     */
    public List<Pair<QualifiedName, String>> getRelationKeys() {
        return relationKeys;
    }

    /**
     * @return the linkedTableType
     */
    public AbstractLinkedTableType getLinkedTableType() {
        return linkedTableType;
    }

    /**
     * @return
     */
    public List<RelationKeyType> getRelationKey() {
        return linkedTableType.getRelationKey();
    }

    /**
     * @param editiable
     */
    public void setEditable( boolean editiable ) {
        linkedTableType.setEditable( editiable );
    }

    /**
     * 
     * @param changeList
     * @throws IOException
     */
    public void commitChanges( List<FeatureAdapter.Changes> changeList )
                            throws IOException {
        // TODO
        List<FeatureAdapter.Changes> tmp = null;
        try {
            if ( changeList != null && changeList.size() > 0 ) {
                // a copy of the change list is made to enable roll back in case of an error
                tmp = new ArrayList<Changes>( changeList );
                Collections.copy( tmp, changeList );
                FeatureCollection fc = getInsertCollection( changeList );
                if ( fc.size() > 0 ) {
                    insert( fc );
                }
                fc = getUpdateCollection( changeList );
                if ( fc.size() > 0 ) {
                    update( fc );
                }
                fc = getDeleteCollection( changeList );
                if ( fc.size() > 0 ) {
                    delete( fc );
                }
            }
            changeList.clear();
        } catch ( Exception e ) {
            changeList = new ArrayList<Changes>( tmp );
            Collections.copy( changeList, tmp );
            changeList.clear();
            changeList.addAll( tmp );
            LOG.logError( e.getMessage(), e );
            throw new IOException( e.getMessage() );
        }
    }

    /**
     * @param fc
     */
    private void insert( FeatureCollection fc ) {
        // TODO Auto-generated method stub
        // shall be abstract
        LOG.logWarning( "insert not supported: " + fc );
    }

    /**
     * @param fc
     */
    private void update( FeatureCollection fc ) {
        // TODO Auto-generated method stub
        // shall be abstract
        LOG.logWarning( "update not supported: " + fc );
    }

    /**
     * @param fc
     */
    private void delete( FeatureCollection fc ) {
        // TODO Auto-generated method stub
        // shall be abstract
        LOG.logWarning( "delete not supported: " + fc );
    }

    /**
     * @param changeList
     * @return
     */
    private FeatureCollection getDeleteCollection( List<Changes> changeList ) {
        // TODO Auto-generated method stub
        LOG.logWarning( "delete not supported: " + changeList );
        return null;
    }

    /**
     * @param changeList
     * @return
     */
    private FeatureCollection getUpdateCollection( List<Changes> changeList ) {
        // TODO Auto-generated method stub
        LOG.logWarning( "update not supported: " + changeList );
        return null;
    }

    /**
     * @param changeList
     * @return
     */
    private FeatureCollection getInsertCollection( List<Changes> changeList ) {
        // TODO Auto-generated method stub
        LOG.logWarning( "insert not supported: " + changeList );
        return null;
    }

    /**
     * 
     * @return number of rows of a table
     */
    public abstract int getRowCount();

    /**
     * 
     * @return number of columns of a table
     */
    public abstract int getColumnCount();

    /**
     * 
     * @return names of the table columns
     */
    public abstract String[] getColumnNames();

    /**
     * 
     * @return data types for columns
     */
    public abstract int[] getColumnTypes();

    /**
     * 
     * @param rowNo
     * @return row by number
     * @throws IOException
     */
    public abstract Object[] getRow( int rowNo )
                            throws IOException;

    /**
     * 
     * @param keys
     * @return rows matching passed keys
     * @throws IOException
     */
    public abstract Object[][] getRows( Pair<String, Object>... keys )
                            throws IOException;

}
