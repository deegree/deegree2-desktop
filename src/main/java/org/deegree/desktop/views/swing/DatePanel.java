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
package org.deegree.desktop.views.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class DatePanel extends JPanel {

    private static final long serialVersionUID = -7571308312333804228L;

    public final static int dom[] = { 31, 28, 31, 30, /* jan feb mar apr */
    31, 30, 31, 31, /* may jun jul aug */
    30, 31, 30, 31 /* sep oct nov dec */
    };

    /** Currently-interesting year, month and day */
    private int year, month, day;

    /** The buttons to be displayed */
    private JButton labs[][];

    /** The number of day squares to leave blank at the start of this month */
    private int leadGap = 0;

    /** A Calendar object used throughout */
    Calendar calendar = new GregorianCalendar();

    /** Today's year */
    private final int thisYear = calendar.get( Calendar.YEAR );

    /** Today's month */
    private final int thisMonth = calendar.get( Calendar.MONTH );

    /** We keep its reference for getBackground(). */
    private Color backGround;

    /** The month choice */
    private JComboBox monthChoice;

    /** The year choice */
    private JComboBox yearChoice;

    private String[] months = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
                               "October", "November", "December" };

    private String[] days = { "So", "Mo", "Tu", "We", "Th", "Fr", "Sa" };

    private int minYear;

    private int maxYear;

    private int activeDay = -1;

    /**
   * 
   */
    public DatePanel() {
        setYYYYMMDD( thisYear, thisMonth + 1, calendar.get( Calendar.DAY_OF_MONTH ) );
        this.minYear = year - 10;
        this.maxYear = year + 10;
        initGUI();
        recompute();
    }

    /**
     * 
     * @param year
     * @param month
     * @param today
     * @param minYear
     * @param maxYear
     */
    public DatePanel( int year, int month, int today, int minYear, int maxYear ) {
        setYYYYMMDD( year, month, today );
        this.minYear = minYear;
        this.maxYear = maxYear;
        initGUI();
        recompute();
    }

    private void setYYYYMMDD( int year, int month, int today ) {
        this.year = year;
        this.month = month - 1;
        this.day = today;
    }

    private void initGUI() {
        setLayout( new BorderLayout() );

        JPanel tp = new JPanel();
        tp.add( monthChoice = new JComboBox() );
        for ( int i = 0; i < months.length; i++ ) {
            monthChoice.addItem( months[i] );
        }
        monthChoice.setSelectedItem( months[month] );
        monthChoice.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                int i = monthChoice.getSelectedIndex();
                if ( i >= 0 ) {
                    month = i;
                    recompute();
                }
            }
        } );
     
        tp.add( yearChoice = new JComboBox() );
        yearChoice.setEditable( true );
        for ( int i = minYear; i <= maxYear; i++ ) {
            yearChoice.addItem( Integer.toString( i ) );
        }
        yearChoice.setSelectedItem( Integer.toString( year ) );
        yearChoice.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent ae ) {
                int i = yearChoice.getSelectedIndex();
                if ( i >= 0 ) {
                    year = Integer.parseInt( yearChoice.getSelectedItem().toString() );
                    recompute();
                }
            }
        } );
        add( BorderLayout.CENTER, tp );

        JPanel bp = new JPanel( new GridLayout( 7, 7 ) );
        labs = new JButton[6][7]; // first row is days

        for ( int i = 0; i < days.length; i++ ) {
            bp.add( new JButton( days[i] ) );
        }
        backGround = bp.getComponent( 0 ).getBackground();

        ActionListener dateSetter = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                String num = e.getActionCommand();
                if ( !num.equals( "" ) ) {
                    // set the current day highlighted
                    setDayActive( Integer.parseInt( num ) );
                }
            }
        };

        // Construct all the buttons, and add them.
        for ( int i = 0; i < 6; i++ ) {
            for ( int j = 0; j < 7; j++ ) {
                bp.add( labs[i][j] = new JButton( "" ) );
                labs[i][j].addActionListener( dateSetter );
            }
        }

        add( BorderLayout.SOUTH, bp );
    }

    /**
     * Compute which days to put where, in the DatePanel panel
     */
    private void recompute() {
        if ( month < 0 || month > 11 ) {
            throw new IllegalArgumentException( "Month " + month + " bad, must be 0-11" );
        }
        clearDayActive();
        for ( int i = 0; i < 6; i++ ) {
            for ( int j = 0; j < 7; j++ ) {
                labs[i][j].setText( "" );
            }
        }
        calendar = new GregorianCalendar( year, month, day );

        // Compute how much to leave before the first.
        // getDay() returns 0 for Sunday, which is just right.
        leadGap = new GregorianCalendar( year, month, 1 ).get( Calendar.DAY_OF_WEEK ) - 1;

        int daysInMonth = dom[month];
        if ( isLeap( calendar.get( Calendar.YEAR ) ) && month > 1 ) {
            ++daysInMonth;
        }

        // Blank out the labels before 1st day of month
        for ( int i = 0; i < leadGap; i++ ) {
            labs[0][i].setText( "" );
        }

        // Fill in numbers for the day of month.
        for ( int i = 1; i <= daysInMonth; i++ ) {
            JButton b = labs[( leadGap + i - 1 ) / 7][( leadGap + i - 1 ) % 7];
            b.setText( Integer.toString( i ) );
        }

        // 7 days/week * up to 6 rows
        for ( int i = leadGap + 1 + daysInMonth; i < 6 * 7; i++ ) {
            labs[( i ) / 7][( i ) % 7].setText( "" );
        }

        // Shade current day, only if current month
        if ( thisYear == year && month == thisMonth ) {
            setDayActive( day ); // shade the box for today
        }

        // Say we need to be drawn on the screen
        repaint();
    }

    /**
     * isLeap() returns true if the given year is a Leap Year.
     */
    private boolean isLeap( int year ) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    /**
     * Set the year, month, and day
     * 
     * @param year
     * @param month
     * @param day
     */
    public void setDate( int year, int month, int day ) {
        this.year = year;
        this.month = month - 1;
        this.day = day;
        recompute();
    }

    private void clearDayActive() {

        // First un-shade the previously-selected square, if any
        if ( activeDay > 0 ) {
            JButton b = labs[( leadGap + activeDay - 1 ) / 7][( leadGap + activeDay - 1 ) % 7];
            b.setBackground( backGround );
            b.repaint();
            activeDay = -1;
        }
    }

    /** Set just the day, on the current month */
    public void setDayActive( int newDay ) {
        clearDayActive();
        // Set the new one
        if ( newDay <= 0 ) {
            day = new GregorianCalendar().get( Calendar.DAY_OF_MONTH );
        } else {
            day = newDay;
        }
        // Now shade the correct square
        Component square = labs[( leadGap + newDay - 1 ) / 7][( leadGap + newDay - 1 ) % 7];
        square.setBackground( Color.red );
        square.repaint();
        activeDay = newDay;
    }

    /**
     * 
     * @return selected year
     */
    public int getYear() {
        return year;
    }

    /**
     * 
     * @return selected month
     */
    public int getMonth() {
        return month + 1;
    }

    /**
     * 
     * @return selected day
     */
    public int getDay() {
        return day;
    }

    /** For testing, a main program */
    public static void main( String[] av ) {
        JFrame f = new JFrame( "Cal" );
        Container c = f.getContentPane();
        c.setLayout( new FlowLayout() );
        DatePanel p = new DatePanel();
        // p = new DatePanel( 2010, 5, 14, 1980, 2010 );
        c.add( p );
        f.pack();
        f.setVisible( true );
    }
}