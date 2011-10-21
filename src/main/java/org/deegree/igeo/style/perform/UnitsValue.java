//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 and
 lat/lon GmbH

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

package org.deegree.igeo.style.perform;

import java.io.IOException;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.sld.CssParameter;
import org.deegree.graphics.sld.ParameterValueType;
import org.deegree.graphics.sld.StyleFactory;
import org.deegree.igeo.style.utils.SldCreatorUtils;
import org.deegree.model.filterencoding.ArithmeticExpression;
import org.deegree.model.filterencoding.FilterConstructionException;
import org.deegree.model.filterencoding.Literal;
import org.deegree.model.filterencoding.PropertyName;
import org.deegree.ogcbase.PropertyPath;
import org.xml.sax.SAXException;

/**
 * <code>UnitsValue</code> represents a double value, which can be in map units or pixel
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class UnitsValue {

    private static final ILogger LOG = LoggerFactory.getLogger( UnitsValue.class );

    private double value;

    private boolean isMapUnit = false;

    /**
     * @param value
     *            the value to set
     * @param isMapUnit
     *            true, if the value is given in map units, false meens units are pixel
     */
    public UnitsValue( double value, boolean isMapUnit ) {
        this.value = value;
        this.isMapUnit = isMapUnit;
    }

    /**
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue( double value ) {
        this.value = value;
    }

    /**
     * @return the isMapUnit
     */
    public boolean isMapUnit() {
        return isMapUnit;
    }

    /**
     * @param isMapUnit
     *            the isMapUnit to set
     */
    public void setMapUnit( boolean isMapUnit ) {
        this.isMapUnit = isMapUnit;
    }

    /**
     * An expression must be set as css parameter, if the value is set in map units!
     * 
     * @param name
     *            the of the css parameter
     * 
     * @return a css parameter
     */
    public CssParameter getAsCssParameter( String name ) {
        if ( isMapUnit() ) {
            try {
                return new CssParameter( name, getAsParameterValueType() );
            } catch ( Exception e ) {
                LOG.logDebug( "Could not create scale dependent css parameter for " + name, e );
            }
        }
        return StyleFactory.createCssParameter( name, value );
    }

    /**
     * @return a parameter value type representing the value, dependent of the scale if the value is given in map units
     * 
     * @throws FilterConstructionException
     * @throws SAXException
     * @throws IOException
     */
    public ParameterValueType getAsParameterValueType()
                            throws FilterConstructionException, SAXException, IOException {
        if ( isMapUnit ) {
            return SldCreatorUtils.getParameterValueType( value );
        }
        return StyleFactory.createParameterValueType( value );
    }

    /**
     * @return a parameter value type representing the value, dependent of the scale if the value is given in map units
     * 
     * @throws FilterConstructionException
     * @throws SAXException
     * @throws IOException
     */
    public ParameterValueType getAsParameterValueType( int add )
                            throws FilterConstructionException, SAXException, IOException {
        if ( isMapUnit ) {
            return SldCreatorUtils.getParameterValueType( value, add );
        }
        return StyleFactory.createParameterValueType( value );
    }

    /**
     * Creates a new instance of a UnitsValue, from the given propertyNameValue
     * 
     * @param pvt
     *            the propertyValueType containing the value and the information, if it is in map units
     * @param defaultValue
     *            the value to set, if the propertyValueType is null or does not contain the right information
     * @return a new instance of a UnitsValue
     */
    public static UnitsValue readFromParameterValueType( ParameterValueType pvt, double defaultValue ) {
        double d = Double.NaN;
        boolean isInMapUnits = false;
        for ( Object component : pvt.getComponents() ) {
            if ( Double.isNaN( d ) && component instanceof ArithmeticExpression ) {
                ArithmeticExpression e = (ArithmeticExpression) component;
                if ( e.getFirstExpression() instanceof ArithmeticExpression ) {
                    e = (ArithmeticExpression) e.getFirstExpression();
                } else if ( e.getSecondExpression() instanceof ArithmeticExpression ) {
                    e = (ArithmeticExpression) e.getSecondExpression();
                }
                String dValue = null;
                if ( e.getFirstExpression() instanceof Literal ) {
                    dValue = ( (Literal) e.getFirstExpression() ).getValue();
                } else if ( e.getSecondExpression() instanceof Literal ) {
                    dValue = ( (Literal) e.getSecondExpression() ).getValue();
                }
                if ( dValue != null ) {
                    try {
                        d = Double.parseDouble( dValue );
                    } catch ( NumberFormatException e1 ) {
                        LOG.logWarning( "ignore", e1 );
                    }
                }
                PropertyPath ppValue = null;
                if ( e.getFirstExpression() instanceof PropertyName ) {
                    ppValue = ( (PropertyName) e.getFirstExpression() ).getValue();
                } else if ( e.getSecondExpression() instanceof PropertyName ) {
                    ppValue = ( (PropertyName) e.getSecondExpression() ).getValue();
                }
                if ( ppValue != null && ppValue.getAsString().contains( "$SCALE" ) ) {
                    isInMapUnits = true;
                }
            }
        }
        if ( Double.isNaN( d ) ) {
            d = defaultValue;
        }
        return new UnitsValue( d, isInMapUnits );
    }
}
