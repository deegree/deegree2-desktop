//$HeadURL: svn+ssh://lbuesching@svn.wald.intevation.de/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2010 by:
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
package org.deegree.igeo.style.utils;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.xml.XMLTools;
import org.deegree.graphics.sld.CssParameter;
import org.deegree.graphics.sld.ParameterValueType;
import org.deegree.graphics.sld.StyleFactory;
import org.deegree.model.filterencoding.ArithmeticExpression;
import org.deegree.model.filterencoding.Expression;
import org.deegree.model.filterencoding.PropertyName;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class SldCreatorUtils {

    public static CssParameter getAsCssParameter( String name, PropertyName dynamic ) {
        ParameterValueType pvt = StyleFactory.createParameterValueType( new Expression[] { dynamic } );
        return new CssParameter( name, pvt );
    }

    public static CssParameter getAsCssParameter( String name, boolean isInPixel, QualifiedName dynamic, int add ) {
        if ( !isInPixel ) {
            ParameterValueType pvt = getParameterValueType( new PropertyName( dynamic ), add );
            return new CssParameter( name, pvt );
        } else {
            PropertyName pn = new PropertyName( dynamic );
            ParameterValueType pvt = StyleFactory.createParameterValueType( new Expression[] { pn } );
            return new CssParameter( name, pvt );
        }
    }

    public static CssParameter getAsCssParameter( String name, boolean isInPixel, QualifiedName dynamic ) {
        if ( !isInPixel ) {
            ParameterValueType pvt = getParameterValueType( new PropertyName( dynamic ) );
            return new CssParameter( name, pvt );
        } else {
            PropertyName pn = new PropertyName( dynamic );
            ParameterValueType pvt = StyleFactory.createParameterValueType( new Expression[] { pn } );
            return new CssParameter( name, pvt );
        }
    }

    public static CssParameter getAsCssParameter( String name, boolean isInPixel, double value ) {
        if ( !isInPixel ) {
            ParameterValueType paramValueType = getParameterValueType( value );
            if ( paramValueType != null ) {
                return new CssParameter( name, paramValueType );
            }
        }
        return StyleFactory.createCssParameter( name, value );
    }

    public static ParameterValueType getParameterValueType( double value ) {
        return getParameterValueType( ( (Double) value ).toString() );
    }

    public static ParameterValueType getParameterValueType( double value, int add ) {
        return getParameterValueType( ( (Double) value ).toString(), add );
    }

    public static ParameterValueType getParameterValueType( String value ) {
        String s = "<ogc:Div><ogc:Literal>" + value + "</ogc:Literal>" + "<ogc:PropertyName>$SCALE</ogc:PropertyName>"
                   + "</ogc:Div>";
        try {
            Expression e = Expression.buildFromDOM( XMLTools.getStringFragmentAsElement( s ) );
            return StyleFactory.createParameterValueType( new Expression[] { e } );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public static ParameterValueType getParameterValueType( String value, int add ) {
        String s = "<ogc:Add><ogc:Div><ogc:Literal>" + value + "</ogc:Literal>"
                   + "<ogc:PropertyName>$SCALE</ogc:PropertyName>" + "</ogc:Div>" + "<ogc:Literal>" + add
                   + "</ogc:Literal></ogc:Add>";
        try {
            Expression e = Expression.buildFromDOM( XMLTools.getStringFragmentAsElement( s ) );
            return StyleFactory.createParameterValueType( new Expression[] { e } );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public static ParameterValueType getParameterValueType( PropertyName pn ) {
        String s = "<ogc:Div>" + pn.toXML() + "<ogc:PropertyName>$SCALE</ogc:PropertyName>" + "</ogc:Div>";
        try {
            Expression e = Expression.buildFromDOM( XMLTools.getStringFragmentAsElement( s ) );
            return StyleFactory.createParameterValueType( new Expression[] { e } );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public static ParameterValueType getParameterValueType( PropertyName pn, int add ) {
        String s = "<ogc:Add><ogc:Div>" + pn.toXML() + "<ogc:PropertyName>$SCALE</ogc:PropertyName>" + "</ogc:Div>"
                   + "<ogc:Literal>" + add + "</ogc:Literal></ogc:Add>";
        try {
            Expression e = Expression.buildFromDOM( XMLTools.getStringFragmentAsElement( s ) );
            return StyleFactory.createParameterValueType( new Expression[] { e } );

        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public static PropertyName getPropertyNameFromPvt( ParameterValueType pvt ) {
        if ( pvt.getComponents() != null && pvt.getComponents().length > 0
             && pvt.getComponents()[0] instanceof Expression ) {
            return getAsPropertyName( (Expression) pvt.getComponents()[0] );
        }
        return null;
    }

    private static PropertyName pn = new PropertyName( new QualifiedName( "$SCALE" ) );

    private static PropertyName getAsPropertyName( Expression e ) {
        if ( e instanceof ArithmeticExpression ) {
            Expression firstExpression = ( (ArithmeticExpression) e ).getFirstExpression();
            Expression secondExpression = ( (ArithmeticExpression) e ).getSecondExpression();
            if ( firstExpression instanceof PropertyName && !pn.equals( (PropertyName) firstExpression ) ) {
                return (PropertyName) firstExpression;
            } else if ( secondExpression instanceof PropertyName && !pn.equals( (PropertyName) secondExpression ) ) {
                return (PropertyName) secondExpression;
            } else {
                PropertyName asPropertyName = getAsPropertyName( firstExpression );
                if ( asPropertyName != null && !pn.equals( asPropertyName ) ) {
                    return asPropertyName;
                }
                return getAsPropertyName( secondExpression );
            }
        } else if ( e instanceof PropertyName && !pn.equals( (PropertyName) e ) ) {
            return (PropertyName) e;
        }
        return null;

    }
}
