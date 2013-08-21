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
package org.deegree.desktop.views.swing.style.renderer;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.table.DefaultTableCellRenderer;
import javax.vecmath.Point2d;

import org.deegree.desktop.style.model.SldProperty;
import org.deegree.framework.util.Pair;
import org.deegree.model.filterencoding.PropertyName;

/**
 * {@link DefaultTableCellRenderer} considering the locale for decimal formatting
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class LocaleTableCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = -1858131643489367595L;

    private final DecimalFormat df;

    public LocaleTableCellRenderer() {
        NumberFormat nf = NumberFormat.getNumberInstance( getLocale() );
        df = (DecimalFormat) nf;
        df.applyPattern( "0.0" );
    }

    @Override
    protected void setValue( Object value ) {
        if ( value instanceof PropertyName ) {
            PropertyName pn = (PropertyName) value;
            String s = pn.toString();
            if ( s.contains( ":" ) ) {
                s = s.substring( s.indexOf( ':' ) + 1 );
            }
            value = s;
        } else if ( value instanceof Integer ) {
            value = "" + (Integer) value;
        } else if ( value instanceof Double ) {
            value = df.format( (Double) value );
        } else if ( value instanceof SldProperty ) {
            SldProperty sldProperty = (SldProperty) value;
            value = sldProperty.getName();
        } else if ( value instanceof String ) {
            value = (String) value;
        } else if ( value instanceof Pair<?, ?> && ( (Pair<?, ?>) value ).first instanceof PropertyName
                    && ( (Pair<?, ?>) value ).second instanceof PropertyName ) {
            @SuppressWarnings("unchecked")
            Pair<PropertyName, PropertyName> p = (Pair<PropertyName, PropertyName>) value;
            String x = p.first.toString();
            if ( x.contains( ":" ) ) {
                x = x.substring( x.indexOf( ':' ) + 1 );
            }
            String y = p.second.toString();
            if ( y.contains( ":" ) ) {
                y = y.substring( y.indexOf( ':' ) + 1 );
            }
            value = x + " " + y;
        } else if ( value instanceof Point2d ) {
            Point2d point2d = (Point2d) value;
            value = df.format( point2d.x ) + " " + df.format( point2d.y );
        }
        super.setValue( value );
    }

}
