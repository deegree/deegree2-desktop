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

package org.deegree.igeo.views.swing.style;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.deegree.igeo.style.classification.ClassificationCalculator;
import org.deegree.igeo.style.model.classification.Intervallable;
import org.deegree.igeo.style.model.classification.ValueRange;
import org.deegree.igeo.style.model.classification.Intervallables.DoubleIntervallable;

/**
 * <code>ClassificationCalculatorTest</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class ClassificationCalculatorTest extends TestCase {

    private ClassificationCalculator<Double> calculator = new ClassificationCalculator<Double>();

    private static List<Intervallable<Double>> listSimple = new ArrayList<Intervallable<Double>>();

    private static List<Intervallable<Double>> list1 = new ArrayList<Intervallable<Double>>();

    private static List<Intervallable<Double>> list2 = new ArrayList<Intervallable<Double>>();

    private static List<Intervallable<Double>> list3 = new ArrayList<Intervallable<Double>>();

    private static List<Intervallable<Double>> list4 = new ArrayList<Intervallable<Double>>();

    private static List<ValueRange<Double>> resultEqualIntervalSimple = new ArrayList<ValueRange<Double>>();

    private static List<ValueRange<Double>> resultQuantileSimple = new ArrayList<ValueRange<Double>>();

    private static List<ValueRange<Double>> resultQuantile1 = new ArrayList<ValueRange<Double>>();

    private static List<ValueRange<Double>> resultQuantile2 = new ArrayList<ValueRange<Double>>();

    private static List<ValueRange<Double>> resultQuantile3 = new ArrayList<ValueRange<Double>>();

    private static List<ValueRange<Double>> resultQuantile4 = new ArrayList<ValueRange<Double>>();

    static {
        listSimple.add( new DoubleIntervallable( 1.0, "#0.###" ) );
        listSimple.add( new DoubleIntervallable( 2.0, "#0.###" ) );
        listSimple.add( new DoubleIntervallable( 3.0, "#0.###" ) );
        listSimple.add( new DoubleIntervallable( 4.0, "#0.###" ) );// <-3
        listSimple.add( new DoubleIntervallable( 5.0, "#0.###" ) );
        listSimple.add( new DoubleIntervallable( 6.0, "#0.###" ) );
        listSimple.add( new DoubleIntervallable( 7.0, "#0.###" ) );// <-6
        listSimple.add( new DoubleIntervallable( 8.0, "#0.###" ) );
        listSimple.add( new DoubleIntervallable( 9.0, "#0.###" ) );
        listSimple.add( new DoubleIntervallable( 10.0, "#0.###" ) );

        resultEqualIntervalSimple.add( new ValueRange<Double>( null, new DoubleIntervallable( 4.0, "#0.###" ), 3 ) );
        resultEqualIntervalSimple.add( new ValueRange<Double>( new DoubleIntervallable( 4.0, "#0.###" ),
                                                               new DoubleIntervallable( 7.0, "#0.###" ), 3 ) );
        resultEqualIntervalSimple.add( new ValueRange<Double>( new DoubleIntervallable( 7.0, "#0.###" ), null, 4 ) );

        resultQuantileSimple.add( new ValueRange<Double>( null, new DoubleIntervallable( 3.5, "#0.###" ), 3 ) );
        resultQuantileSimple.add( new ValueRange<Double>( new DoubleIntervallable( 3.5, "#0.###" ),
                                                          new DoubleIntervallable( 6.5, "#0.###" ), 3 ) );
        resultQuantileSimple.add( new ValueRange<Double>( new DoubleIntervallable( 6.5, "#0.###" ), null, 4 ) );

        list1.add( new DoubleIntervallable( 1.0, "#0.###" ) );
        list1.add( new DoubleIntervallable( 1.0, "#0.###" ) );
        list1.add( new DoubleIntervallable( 3.0, "#0.###" ) );
        list1.add( new DoubleIntervallable( 4.0, "#0.###" ) );// <-3
        list1.add( new DoubleIntervallable( 5.0, "#0.###" ) );
        list1.add( new DoubleIntervallable( 5.0, "#0.###" ) );
        list1.add( new DoubleIntervallable( 5.0, "#0.###" ) );// <-6
        list1.add( new DoubleIntervallable( 5.0, "#0.###" ) );
        list1.add( new DoubleIntervallable( 5.0, "#0.###" ) );
        list1.add( new DoubleIntervallable( 9.0, "#0.###" ) );// <-9
        list1.add( new DoubleIntervallable( 9.0, "#0.###" ) );

        resultQuantile1.add( new ValueRange<Double>( null, new DoubleIntervallable( 3.5, "#0.###" ), 3 ) );
        resultQuantile1.add( new ValueRange<Double>( new DoubleIntervallable( 3.5, "#0.###" ),
                                                     new DoubleIntervallable( 7.0, "#0.###" ), 6 ) );
        resultQuantile1.add( new ValueRange<Double>( new DoubleIntervallable( 7.0, "#0.###" ), null, 2 ) );

        list2.add( new DoubleIntervallable( 1.0, "#0.###" ) );
        list2.add( new DoubleIntervallable( 1.0, "#0.###" ) );
        list2.add( new DoubleIntervallable( 3.0, "#0.###" ) );
        list2.add( new DoubleIntervallable( 4.0, "#0.###" ) );
        list2.add( new DoubleIntervallable( 5.0, "#0.###" ) );
        list2.add( new DoubleIntervallable( 5.0, "#0.###" ) );
        list2.add( new DoubleIntervallable( 5.0, "#0.###" ) );
        list2.add( new DoubleIntervallable( 5.0, "#0.###" ) );
        list2.add( new DoubleIntervallable( 5.0, "#0.###" ) );

        resultQuantile2.add( new ValueRange<Double>( null, new DoubleIntervallable( 3.5, "#0.###" ), 3 ) );
        resultQuantile2.add( new ValueRange<Double>( new DoubleIntervallable( 3.5, "#0.###" ), null, 6 ) );

        list3.add( new DoubleIntervallable( 5.0, "#0.###" ) );
        list3.add( new DoubleIntervallable( 5.0, "#0.###" ) );
        list3.add( new DoubleIntervallable( 5.0, "#0.###" ) );
        list3.add( new DoubleIntervallable( 5.0, "#0.###" ) );
        list3.add( new DoubleIntervallable( 5.0, "#0.###" ) );

        resultQuantile3.add( new ValueRange<Double>( null, new DoubleIntervallable( 5.0, "#0.###" ), 0 ) );
        resultQuantile3.add( new ValueRange<Double>( new DoubleIntervallable( 5.0, "#0.###" ), null, 5 ) );

        list4.add( new DoubleIntervallable( 1.0, "#0.###" ) );
        list4.add( new DoubleIntervallable( 1.0, "#0.###" ) );
        list4.add( new DoubleIntervallable( 3.0, "#0.###" ) );
        list4.add( new DoubleIntervallable( 3.0, "#0.###" ) );// <-3
        list4.add( new DoubleIntervallable( 3.0, "#0.###" ) );
        list4.add( new DoubleIntervallable( 5.0, "#0.###" ) );
        list4.add( new DoubleIntervallable( 5.0, "#0.###" ) );// <-6
        list4.add( new DoubleIntervallable( 5.0, "#0.###" ) );
        list4.add( new DoubleIntervallable( 5.0, "#0.###" ) );
        list4.add( new DoubleIntervallable( 6.0, "#0.###" ) );// <-9
        list4.add( new DoubleIntervallable( 7.0, "#0.###" ) );
        list4.add( new DoubleIntervallable( 8.0, "#0.###" ) );

        resultQuantile4.add( new ValueRange<Double>( null, new DoubleIntervallable( 4.0, "#0.###" ), 5 ) );
        resultQuantile4.add( new ValueRange<Double>( new DoubleIntervallable( 4.0, "#0.###" ),
                                                     new DoubleIntervallable( 5.5, "#0.###" ), 4 ) );
        resultQuantile4.add( new ValueRange<Double>( new DoubleIntervallable( 5.5, "#0.###" ), null, 3 ) );
    }

    public void testEqualIntervalClassificationSimple() {
        List<ValueRange<Double>> result = calculator.calculateEqualInterval( listSimple, 3 );
        for ( ValueRange<Double> valueRange : result ) {
            assertTrue( isInResult( resultEqualIntervalSimple, valueRange ) );
        }
    }

    public void testQuantileClassificationSimple() {
        List<ValueRange<Double>> result = calculator.calculateQuantileClassification( listSimple, 3 );
        for ( ValueRange<Double> valueRange : result ) {
            assertTrue( isInResult( resultQuantileSimple, valueRange ) );
        }
    }

    public void testQuantileClassification1() {
        List<ValueRange<Double>> result = calculator.calculateQuantileClassification( list1, 3 );
        for ( ValueRange<Double> valueRange : result ) {
            assertTrue( isInResult( resultQuantile1, valueRange ) );
        }
    }

    public void testQuantileClassification2() {
        List<ValueRange<Double>> result = calculator.calculateQuantileClassification( list2, 3 );
        for ( ValueRange<Double> valueRange : result ) {
            assertTrue( isInResult( resultQuantile2, valueRange ) );
        }
    }

    public void testQuantileClassification3() {
        List<ValueRange<Double>> result = calculator.calculateQuantileClassification( list3, 3 );
        for ( ValueRange<Double> valueRange : result ) {
            assertTrue( isInResult( resultQuantile3, valueRange ) );
        }
    }

    public void testQuantileClassification4() {
        List<ValueRange<Double>> result = calculator.calculateQuantileClassification( list4, 3 );
        for ( ValueRange<Double> valueRange : result ) {
            assertTrue( isInResult( resultQuantile4, valueRange ) );
        }
    }

    private boolean isInResult( List<ValueRange<Double>> results, ValueRange<?> range ) {
        for ( ValueRange<Double> result : results ) {
            if ( result.equals( range ) && result.getCount() == range.getCount() ) {
                return true;
            }
        }
        return false;
    }
}
