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
package org.deegree.igeo.mapmodel;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import net.sf.ehcache.Cache;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.utils.HashCodeUtil;
import org.deegree.igeo.config.AbstractDatasourceType;
import org.deegree.igeo.config.AbstractLinkedTableType;
import org.deegree.igeo.config.LinkedDatabaseTableType;
import org.deegree.igeo.config.LinkedFileTableType;
import org.deegree.igeo.config.ObjectFactory;
import org.deegree.igeo.config.Util;
import org.deegree.igeo.dataadapter.DataAccessException;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.spatialschema.Envelope;

/**
 * Abstract base class for describing a datasource
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public abstract class Datasource {

    private static ILogger LOG = LoggerFactory.getLogger( Datasource.class );

    public static enum DS_PARAMETER {
        extent, name, minScaleDenom, maxScaleDenom, authenticationInfo, file, baseRequest, capabilitiesURL, geomProperty, getFeature, coverage, jdbc, sqlTemplate, lazyLoading
    };

    private AuthenticationInformation authenticationInformation;

    private Cache cache;

    private int fHashCode;

    protected AbstractDatasourceType dsType;

    /**
     * 
     * @param dsType
     * @param authenticationInformation
     * @param cache
     */
    public Datasource( AbstractDatasourceType dsType, AuthenticationInformation authenticationInformation, Cache cache ) {
        this.dsType = dsType;
        this.authenticationInformation = authenticationInformation;
        this.cache = cache;
    }

    /**
     * 
     * @return wrapped {@link AbstractDatasourceType}
     */
    public AbstractDatasourceType getDatasourceType() {
        return dsType;
    }

    /**
     * 
     * @return description of linked data tables
     */
    public List<AbstractLinkedTableType> getLinkedTables() {
        List<AbstractLinkedTableType> list = new ArrayList<AbstractLinkedTableType>();
        int c = dsType.getAbstractLinkedTable().size();
        for ( int i = 0; i < c; i++ ) {
            list.add( dsType.getAbstractLinkedTable().get( i ).getValue() );
        }
        return list;
    }

    /**
     * 
     * @param linkedTable
     *            description of linked data table to be added
     */
    public void addLinkedTable( AbstractLinkedTableType linkedTable ) {
        JAXBElement<? extends AbstractLinkedTableType> lk = null;
        if ( linkedTable instanceof LinkedFileTableType ) {
            lk = new ObjectFactory().createLinkedFileTable( (LinkedFileTableType) linkedTable );
        } else if ( linkedTable instanceof LinkedDatabaseTableType ) {
            lk = new ObjectFactory().createLinkedDatabaseTable( (LinkedDatabaseTableType) linkedTable );
        }
        dsType.getAbstractLinkedTable().add( lk );
    }

    /**
     * 
     * @param linkedTable
     *            description of linked data table to be removed
     */
    public void removeLinkedTable( AbstractLinkedTableType linkedTable ) {
        // TODO
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if ( obj == null || !( obj instanceof Datasource ) ) {
            return false;
        }
        return dsType.getName().equals( ( (Datasource) obj ).getName() );
    }

    @Override
    public int hashCode() {
        if ( fHashCode == 0 ) {
            int result = HashCodeUtil.SEED;
            result = HashCodeUtil.hash( result, dsType );
            result = HashCodeUtil.hash( result, authenticationInformation );
            result = HashCodeUtil.hash( result, cache );
        }
        return fHashCode;
    }

    /**
     * 
     * @return authentication information
     */
    public AuthenticationInformation getAuthenticationInformation() {
        return this.authenticationInformation;
    }

    /**
     * @param authenticationInformation
     *            the authenticationInformation to set
     */
    public void setAuthenticationInformation( AuthenticationInformation authenticationInformation ) {
        this.authenticationInformation = authenticationInformation;
    }

    /**
     * 
     * @return true if datasource is queryable
     */
    public boolean isQueryable() {
        return dsType.isQueryable();
    }

    /**
     * 
     * @param isQueryable
     */
    public void setQueryable( boolean isQueryable ) {
        dsType.setQueryable( isQueryable );
    }

    /**
     * 
     * @return maximum scale denominator
     */
    public double getMaxScaleDenominator() {
        return dsType.getMaxScaleDenominator();
    }

    /**
     * 
     * @param maxScaleDenominator
     */
    public void setMaxScaleDenominator( double maxScaleDenominator ) {
        dsType.setMaxScaleDenominator( maxScaleDenominator );
    }

    /**
     * 
     * @return minimum scale denominator
     */
    public double getMinScaleDenominator() {
        return dsType.getMinScaleDenominator();
    }

    /**
     * 
     * @param minScaleDenominator
     */
    public void setMinScaleDenominator( double minScaleDenominator ) {
        dsType.setMinScaleDenominator( minScaleDenominator );
    }

    /**
     * 
     * @return covered extent
     */
    public Envelope getExtent() {
        if ( dsType != null && dsType.getExtent() != null ) {
            return Util.convertEnvelope( dsType.getExtent() );
        } else {
            return null;
        }
    }

    /**
     * 
     * @return true if layz loading
     */
    public boolean isLazyLoading() {
        return dsType.isLazyLoading();
    }

    /**
     * @return the cache
     */
    public Cache getCache() {
        return cache;
    }

    /**
     * @param cache
     *            the cache to set
     */
    public void setCache( Cache cache ) {
        this.cache = cache;
    }

    /**
     * @return the isEditable
     */
    public boolean isEditable() {
        return dsType.isEditable();
    }

    /**
     * @param isEditable
     *            the isEditable to set
     */
    public void setEditable( boolean isEditable ) {
        dsType.setEditable( isEditable );
    }

    /**
     * @return the supportsTooltips
     */
    public boolean supportsTooltips() {
        return dsType.isSupportToolTips();
    }

    /**
     * @param supportsTooltips
     *            the supportsTooltips to set
     */
    public void setSupportsTooltips( boolean supportsTooltips ) {
        dsType.setSupportToolTips( supportsTooltips );
    }

    /**
     * @param envelope
     *            the extent to set
     */
    public void setExtent( Envelope envelope ) {
        dsType.setExtent( Util.convertEnvelope( envelope ) );
    }

    /**
     * 
     * @return datasource name
     */
    public String getName() {
        return dsType.getName();
    }

    /**
     * 
     * @param name
     *            datasource name
     */
    public void setName( String name ) {
        dsType.setName( name );
    }

    /**
     * 
     * @return native CRS of a datasource
     */
    public CoordinateSystem getNativeCoordinateSystem() {
        try {
            return CRSFactory.create( dsType.getNativeCRS() );
        } catch ( UnknownCRSException e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( e );
        }
    }

    /**
     * sets the native crs of a datasource
     * 
     * @param nativeCRS
     */
    public void setNativeCoordinateSystem( CoordinateSystem nativeCRS ) {
        dsType.setNativeCRS( nativeCRS.getCRS().getIdentifier() );
    }

    /**
     * sets a data source to use lazy loading or not
     * 
     * @param lazyLoading
     */
    public void setLazyLoading( boolean lazyLoading ) {
        dsType.setLazyLoading( lazyLoading );
    }

    /**
     * resets cache
     * 
     */
    public void reset() {

    }

}