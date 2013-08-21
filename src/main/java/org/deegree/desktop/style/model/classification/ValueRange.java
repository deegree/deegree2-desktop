/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2012 by:
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

 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: info@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/

package org.deegree.desktop.style.model.classification;

import static org.deegree.desktop.i18n.Messages.get;

import org.deegree.desktop.style.model.classification.ThematicGroupingInformation.GROUPINGTYPE;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.Literal;
import org.deegree.model.filterencoding.OperationDefines;
import org.deegree.model.filterencoding.PropertyIsCOMPOperation;
import org.deegree.model.filterencoding.PropertyName;

/**
 * <code>ValueRange</code>
 * 
 * @author <a href="mailto:wanhoff@lat-lon.de">Jeronimo Wanhoff</a>
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class ValueRange<U extends Comparable<U>> implements Comparable<ValueRange<U>> {

    private Intervallable<U> min;

    private Intervallable<U> max;

    private int count;

    // Be careful: this variable is currently used only to check if the groupingType is UNIQUE to set the values after
    // inserting a new in classification, so it can be null!
    private GROUPINGTYPE groupingType;

    public ValueRange() {
    }

    /**
     * @param min
     * @param max
     */
    public ValueRange( Intervallable<U> min, Intervallable<U> max, int count ) {
        super();
        this.min = min;
        this.max = max;
        this.count = count;
    }

    /**
     * @param groupingType
     */
    public ValueRange( GROUPINGTYPE groupingType ) {
        this.groupingType = groupingType;
    }

    /**
     * @return the min
     */
    public Intervallable<U> getMin() {
        return min;
    }

    /**
     * @param min
     *            the min to set
     */
    public void setMin( Intervallable<U> min ) {
        this.min = min;
    }

    /**
     * @return the max
     */
    public Intervallable<U> getMax() {
        return max;
    }

    /**
     * @param max
     *            the max to set
     */
    public void setMax( Intervallable<U> max ) {
        this.max = max;
    }

    public int getCount() {
        return count;
    }

    public void setCount( int count ) {
        this.count = count;
    }
    
    public GROUPINGTYPE getGroupingType(){
        return groupingType;
    }

    /**
     * @param propertyName
     *            the propertyName, to set in the filter
     * @return a filter out of this value range
     */
    public Filter getFilter( PropertyName propertyName ) {
        Filter filter = null;
        if ( min != null && max != null ) {
            if ( min.equals( max ) ) {
                Literal literal = new Literal( min.getFilterExpression() );
                PropertyIsCOMPOperation op = new PropertyIsCOMPOperation( OperationDefines.PROPERTYISEQUALTO,
                                                                          propertyName, literal );
                filter = new ComplexFilter( op );
            } else {
                Literal literal1 = new Literal( min.getFilterExpression() );
                Literal literal2 = new Literal( max.getFilterExpression() );
                PropertyIsCOMPOperation op1 = new PropertyIsCOMPOperation(
                                                                           OperationDefines.PROPERTYISGREATERTHANOREQUALTO,
                                                                           propertyName, literal1 );
                PropertyIsCOMPOperation op2 = new PropertyIsCOMPOperation( OperationDefines.PROPERTYISLESSTHAN,
                                                                           propertyName, literal2 );
                ComplexFilter f1 = new ComplexFilter( op1 );
                ComplexFilter f2 = new ComplexFilter( op2 );
                filter = new ComplexFilter( f1, f2, OperationDefines.AND );

            }
        } else if ( min != null && max == null ) {
            Literal literal = new Literal( min.getFilterExpression() );
            PropertyIsCOMPOperation op = new PropertyIsCOMPOperation( OperationDefines.PROPERTYISGREATERTHANOREQUALTO,
                                                                      propertyName, literal );
            filter = new ComplexFilter( op );
        } else if ( min == null && max != null ) {
            Literal literal = new Literal( max.getFilterExpression() );
            PropertyIsCOMPOperation op = new PropertyIsCOMPOperation( OperationDefines.PROPERTYISLESSTHAN,
                                                                      propertyName, literal );
            filter = new ComplexFilter( op );
        }

        return filter;
    }

    /**
     * @return a short label of this value range
     */
    public String getLabel() {
        String s = "";
        if ( min != null && max != null ) {
            s = get( "$MD10796", min.getFormattedString(), max.getFormattedString() );
        } else if ( min != null && max == null ) {
            s = get( "$MD10797", min.getFormattedString() );
        } else if ( min == null && max != null ) {
            s = get( "$MD10795", max.getFormattedString() );
        }
        return s;
    }

    /**
     * @return a more detailed text displayed as toolttip
     */
    public String getToolTip() {
        String s = "";
        if ( min != null && max != null ) {
            if ( min.equals( max ) ) {
                s = get( "$MD10798", min.getFormattedString() );
            } else {
                s = get( "$MD10791", min.getFormattedString(), max.getFormattedString() );
            }
        } else if ( min != null && max == null ) {
            s = get( "$MD10792", min.getFormattedString() );
        } else if ( min == null && max != null ) {
            s = get( "$MD10790", max.getFormattedString() );
        }
        return s;
    }

    /**
     * @return the text, displayed, when value range will be edited
     */
    public String getEditorLabel() {
        String s = "";
        if ( min != null ) {
            s = min.getFormattedString();
        }
        return s;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getLabel();
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( max == null ) ? 0 : max.hashCode() );
        result = prime * result + ( ( min == null ) ? 0 : min.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        ValueRange<?> other = (ValueRange<?>) obj;
        if ( max == null ) {
            if ( other.max != null )
                return false;
        } else if ( !max.equals( other.max ) )
            return false;
        if ( min == null ) {
            if ( other.min != null )
                return false;
        } else if ( !min.equals( other.min ) )
            return false;
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo( ValueRange<U> o ) {
        if ( ( min == null || min.getValue() == null )
             && ( o == null || o.getMin() == null || o.getMin().getValue() == null ) ) {
            return 0;
        } else if ( ( min == null || min.getValue() == null )
                    && ( o != null && o.min != null && o.min.getValue() != null ) ) {
            return -1;
        } else if ( ( o == null || o.getMin() == null || o.getMin().getValue() == null )
                    && ( min != null && min.getValue() != null ) ) {
            return 1;
        }
        return min.getValue().compareTo( o.min.getValue() );
    }

    /**
     * @param value
     *            the value to test if it belongst to this class
     * @return true, if this value belongs to this class (this means the value is greater then the min value and less or
     *         equal then the max value), false otherwise
     */
    public boolean isInThisValueRange( Intervallable<U> value ) {
        int minComp = 0;
        int maxComp = 0;
        // compare if nothing is null
        if ( ( min != null && min.getValue() != null ) && ( value != null && value.getValue() != null ) ) {
            minComp = value.getValue().compareTo( min.getValue() );
        }
        if ( ( max != null && max.getValue() != null ) && ( value != null && value.getValue() != null ) ) {
            maxComp = value.getValue().compareTo( max.getValue() );
        }
        // if min value is null, but not the max value, test if value is less then the max value
        if ( ( min == null || min.getValue() == null ) && ( max != null && max.getValue() != null )
             && ( value != null && value.getValue() != null ) ) {
            return value.getValue().compareTo( max.getValue() ) < 0;
        }
        // if max value is null, but not the min value, test if value is greater or equal then the min value
        if ( ( max == null || max.getValue() == null ) && ( min != null && min.getValue() != null )
             && ( value != null && value.getValue() != null ) ) {
            return value.getValue().compareTo( min.getValue() ) >= 0;
        }
        if ( min != null && max != null && min.equals( max ) && min.equals( value ) ) {
            return true;
        }
        if ( minComp >= 0 && maxComp < 0 ) {
            return true;
        }
        return false;
    }

    /**
     * inrcreased count for one
     */
    public void increaseCount() {
        count++;
    }

}
