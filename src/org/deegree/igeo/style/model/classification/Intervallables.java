/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2007 by:
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

package org.deegree.igeo.style.model.classification;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.deegree.framework.util.TimeTools;
import org.deegree.igeo.i18n.Messages;

/**
 * <code>Intervallables</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class Intervallables {

    public static class DateIntervallable implements Intervallable<Date> {

        private String defaultPattern = "dd.mm.yyyy HH:mm";

        private SimpleDateFormat dateFormatter;

        private Date wrappee;

        public DateIntervallable( Date date, String datePattern ) {
            wrappee = date;
            DateFormatSymbols dfs = new DateFormatSymbols( Locale.getDefault() );
            if ( datePattern != null ) {
                defaultPattern = datePattern;
            }
            dateFormatter = new SimpleDateFormat( defaultPattern, dfs );
        }

        public Intervallable<Date> calculateInterval( Date max, int numberOfIntervals ) {
            return new DateIntervallable( new Date( ( max.getTime() - wrappee.getTime() ) / numberOfIntervals ),
                                          defaultPattern );
        }

        public Intervallable<Date> getNextValue( Date interval ) {
            return new DateIntervallable( new Date( wrappee.getTime() + interval.getTime() ), defaultPattern );
        }

        public int compareTo( Date o ) {
            return wrappee.compareTo( o );
        }

        public Date getValue() {
            return wrappee;
        }

        public Intervallable<Date> calculateMean( Date max ) {
            return new DateIntervallable(
                                          new Date( wrappee.getTime() + ( ( max.getTime() - wrappee.getTime() ) / 2 ) ),
                                          defaultPattern );
        }

        public String getFilterExpression() {
            return TimeTools.getISOFormattedTime( wrappee );
        }

        public String getFormattedString() {
            return dateFormatter.format( wrappee );
        }

        @Override
        public boolean equals( Object obj ) {
            if ( obj instanceof DateIntervallable ) {
                return wrappee.equals( ( (DateIntervallable) obj ).getValue() );
            }
            return super.equals( obj );
        }

        public String getInvalidMessage( String text ) {
            return Messages.getMessage( Locale.getDefault(), "$MD10766", text, dateFormatter.toLocalizedPattern() );
        }

        public Intervallable<Date> getAsIntervallable( String string )
                                throws Exception {
            return new DateIntervallable( dateFormatter.parse( string ), defaultPattern );
        }

        @Override
        public String toString() {
            return wrappee.toString();
        }
    }

    public static class StringIntervallable implements Intervallable<String> {

        private String wrappee;

        public StringIntervallable( String wrappee ) {
            this.wrappee = wrappee;
        }

        public Intervallable<String> calculateInterval( String max, int numberOfIntervals ) {
            return new StringIntervallable( max );
        }

        public Intervallable<String> getNextValue( String interval ) {
            return new StringIntervallable( interval );
        }

        public int compareTo( String o ) {
            return wrappee.compareTo( o );
        }

        public String getValue() {
            return wrappee;
        }

        public Intervallable<String> calculateMean( String max ) {
            return new StringIntervallable( max );
        }

        public String getFilterExpression() {
            return wrappee;
        }

        public String getFormattedString() {
            return wrappee;
        }

        @Override
        public boolean equals( Object obj ) {
            if ( obj instanceof StringIntervallable ) {
                return wrappee.equals( ( (StringIntervallable) obj ).getValue() );
            }
            return super.equals( obj );
        }

        public String getInvalidMessage( String text ) {
            return "";
        }

        public Intervallable<String> getAsIntervallable( String string )
                                throws Exception {
            return new StringIntervallable( string );
        }

        @Override
        public String toString() {
            return wrappee;
        }

    }

    public static class DoubleIntervallable implements Intervallable<Double> {

        private String defaultPattern = "#0.###";

        private DecimalFormat decimalFormatter;

        private Double wrappee;

        public DoubleIntervallable( Double wrappee, String decimalPattern ) {
            this.wrappee = wrappee;
            if ( decimalPattern != null ) {
                defaultPattern = decimalPattern;
            }
            decimalFormatter = new DecimalFormat( defaultPattern );
        }

        public Intervallable<Double> calculateInterval( Double max, int numberOfIntervals ) {
            return new DoubleIntervallable( ( max - wrappee ) / numberOfIntervals, defaultPattern );
        }

        public Intervallable<Double> getNextValue( Double interval ) {
            return new DoubleIntervallable( wrappee + interval, defaultPattern );
        }

        public int compareTo( Double o ) {
            return wrappee.compareTo( o );
        }

        public Double getValue() {
            return wrappee;
        }

        public Intervallable<Double> calculateMean( Double max ) {
            return new DoubleIntervallable( wrappee + ( ( max - wrappee ) / 2 ), defaultPattern );
        }

        public String getFilterExpression() {
            return wrappee.toString();
        }

        public String getFormattedString() {
            return decimalFormatter.format( wrappee );
        }

        @Override
        public boolean equals( Object obj ) {
            if ( obj instanceof DoubleIntervallable ) {
                return wrappee.equals( ( (DoubleIntervallable) obj ).getValue() );
            }
            return super.equals( obj );
        }

        public String getInvalidMessage( String text ) {
            return Messages.getMessage( Locale.getDefault(), "$MD10745", text );
        }

        public Intervallable<Double> getAsIntervallable( String string )
                                throws Exception {
            NumberFormat nf = NumberFormat.getInstance( Locale.getDefault() );
            return new DoubleIntervallable( nf.parse( string ).doubleValue(), defaultPattern );
        }

        @Override
        public String toString() {
            return wrappee.toString();
        }

    }

}
