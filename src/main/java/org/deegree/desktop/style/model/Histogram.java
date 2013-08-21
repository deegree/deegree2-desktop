//$HeadURL$
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
package org.deegree.desktop.style.model;

import static org.deegree.desktop.i18n.Messages.get;

import java.util.List;

import javax.swing.JFrame;

import org.deegree.desktop.style.model.classification.ValueRange;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleInsets;

/**
 * handles the histogram of a classification
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class Histogram extends JFrame {

    private static final long serialVersionUID = -5180054325661700908L;

    public ChartPanel cf;

    public Histogram() {
        this.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
    }

    public void update( String title, List<ValueRange<?>> values ) {
        if ( values != null ) {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            for ( ValueRange<?> value : values ) {
                value.getCount();
                value.getLabel();
                dataset.addValue( value.getCount(), "1", value.getLabel() );
            }

            JFreeChart chart = ChartFactory.createBarChart( null, get( "$MD11052" ), get( "$MD11053" ), dataset,
                                                            PlotOrientation.VERTICAL, false, true, false );

            chart.setPadding( new RectangleInsets( 10, 10, 10, 10 ) );

            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            CategoryAxis cAxis = plot.getDomainAxis();
            cAxis.setCategoryMargin( 0 );
            cAxis.setCategoryLabelPositions( CategoryLabelPositions.createUpRotationLabelPositions( 1 ) );

            if ( cf == null ) {
                cf = new ChartPanel( chart );
                this.add( cf );
                cf.setVisible( true );
            } else {
                cf.setChart( chart );
            }
            this.setTitle( title );
            this.setVisible( true );
            this.pack();
        }
    }

}
