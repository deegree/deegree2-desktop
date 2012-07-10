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

package org.deegree.igeo.views.swing.map;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.utils.MapTools;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.mapmodel.CRSEntry;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.views.MapMouseCoords;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.GeoTransformer;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.spatialschema.Point;

/**
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
class MapMouseCoordsLabel extends JPanel implements MapMouseCoords {

    private static final long serialVersionUID = 8755774234458317339L;

    private static final ILogger LOG = LoggerFactory.getLogger( MapMouseCoordsLabel.class );

    private ApplicationContainer<Container> appCont;

    private Point point;

    private MapModel mapModel;

    private CoordinateSystem crs;

    private GeoTransformer gt;

    private DecimalFormat df;

    private JLabel label;

    private JComboBox cbCRS;

    /**
     * 
     * @param appCont
     * @param mapModule
     */
    MapMouseCoordsLabel( ApplicationContainer<Container> appCont ) {
        this.appCont = appCont;
        initGUI();
    }

    /**
     * 
     */
    private void initGUI() {
        setLayout( new FlowLayout( FlowLayout.LEFT, 5, 0 ) );
        label = new JLabel();
        label.setPreferredSize( new Dimension( 500, 22 ) );
        add( label = new JLabel() );
        updateCRSList();        
        this.setVisible( true );
    }

    /**
     * 
     */
    void updateCRSList() {
        if ( cbCRS != null ) {
            remove( cbCRS );
        }
        this.mapModel = appCont.getMapModel( null );
        CRSEntry[] entries = mapModel.getSupportedCRSs();
        if ( entries.length > 0 ) {
            setFormat( mapModel.getCoordinateSystem() );
            add( cbCRS = new JComboBox() );
            int cbWidth = calcComboboxWidth( entries );
            Graphics g = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB ).getGraphics();            
            FontMetrics fm = g.getFontMetrics( getFont() ); 
            g.dispose();
            int t = fm.stringWidth( df.format( -90000000 ) + " / " + df.format( -90000000 ) );
            setPreferredSize( new Dimension( t + cbWidth, 22 ) );
            cbCRS.setModel( new DefaultComboBoxModel( entries ) );
            // find index of selected CRS
            CoordinateSystem crs = this.appCont.getMapModel( null ).getCoordinateSystem();
            int index = 0;
            for ( int i = 0; i < entries.length; i++ ) {
                if ( entries[i].getCode().equals( crs.getPrefixedName() ) ) {
                    index = i;
                    break;
                }
            }
            cbCRS.setSelectedIndex( index );
            cbCRS.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent event ) {
                    CRSEntry entry = (CRSEntry) cbCRS.getSelectedItem();
                    try {
                        setDisplayCRS( entry.getCode() );
                    } catch ( UnknownCRSException e ) {
                        LOG.logError( e );
                    }
                }
            } );
            try {
                setDisplayCRS( appCont.getMapModel(null).getCoordinateSystem().getPrefixedName() );
            } catch ( UnknownCRSException e ) {
                // should never happen
                LOG.logError( e );
            }
        } else {
            setPreferredSize( new Dimension( 250, 22 ) );
        }
    }

    /**
     * @param entries
     * @return width required for rendering list of available CRS as combobox
     */
    private int calcComboboxWidth( CRSEntry[] entries ) {
        int w = 0;
        BufferedImage b = new BufferedImage( 10, 10, BufferedImage.TYPE_INT_RGB );
        Graphics g = b.getGraphics();
        FontMetrics metrics = g.getFontMetrics();
        for ( CRSEntry crsEntry : entries ) {
            int t = metrics.stringWidth( crsEntry.getName() );
            if ( t > w ) {
                w = t;
            }
        }
        g.dispose();
        return w + 10;
    }

    private void setFormat( CoordinateSystem cs ) {
        String units = cs.getAxisUnits()[0].getSymbol();
        if ( units.equals( "m" ) ) {
            df = new DecimalFormat( "#.00" );
        } else if ( units.equals( "°" ) ) {
            df = new DecimalFormat( "#.0000" );
        } else {
            df = new DecimalFormat( "#.00" );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.MapMouseCoords#updateCoords()
     */
    public void setMouseCoords( double mouseX, double mouseY, double componentWidth, double componentHeight ) {
        MapModel mm = appCont.getMapModel( null );
        point = MapTools.calculateMouseCoord( mm, mouseX, mouseY, componentWidth, componentHeight );
        if ( crs != null && !mm.getCoordinateSystem().equals( crs ) ) {
            try {
                point = (Point) gt.transform( point );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
            }
        }
        updateText();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.MapMouseCoords#deleteMouseCoords()
     */
    public void deleteMouseCoords() {
        point = null;
        updateText();
    }

    private void updateText() {

        if ( point == null ) {
            label.setText( "" );
        } else if ( point != null ) {
            label.setText( df.format( point.getX() ) + " / " + df.format( point.getY() ) );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.MapMouseCoords#setDisplayCRS(java.lang.String)
     */
    public void setDisplayCRS( String name )
                            throws UnknownCRSException {
        this.crs = CRSFactory.create( name );
        gt = new GeoTransformer( this.crs );
        setFormat( this.crs );
    }

}
