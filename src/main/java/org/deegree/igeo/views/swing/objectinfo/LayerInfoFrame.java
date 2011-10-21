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

package org.deegree.igeo.views.swing.objectinfo;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.views.FeatureTable;
import org.deegree.igeo.views.swing.DefaultFrame;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.filterencoding.Filter;

/**
 * {@link JFrame} container for {@link FeatureTablePanel}
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class LayerInfoFrame extends DefaultFrame implements FeatureTable {

    private static final long serialVersionUID = -6550973152665699887L;

    private FeatureTablePanel ftp;

    /**
     * default constructor
     * 
     */
    public LayerInfoFrame() {
        setResizable( true );
        setLayout( new BorderLayout() );
        ftp = new FeatureTablePanel();
        setAlwaysOnTop( true );
        ActionListener al = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ftp.copyToClipboard( false );
            }
        };
        KeyStroke aKeyStroke = KeyStroke.getKeyStroke( KeyEvent.VK_C, KeyEvent.VK_ALT );
        getRootPane().registerKeyboardAction( al, aKeyStroke, JComponent.WHEN_IN_FOCUSED_WINDOW );
    }

    @Override
    public void init( ViewFormType viewForm )
                            throws Exception {
        super.init( viewForm );
        ftp.registerModule( this.owner );
        add( ftp, BorderLayout.CENTER );
        setVisible( true );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.FeatureTable#getSelected()
     */
    public List<Feature> getSelected() {
        return ftp.getSelected();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.FeatureTable#select(org.deegree.model.filterencoding.Filter)
     */
    public void select( Filter filter ) {
        ftp.select( filter );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.FeatureTable#select(org.deegree.model.feature.FeatureCollection)
     */
    public void select( FeatureCollection fc ) {
        ftp.select( fc );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.FeatureTable#setFeatureCollection(org.deegree.igeo.mapmodel.Layer,
     * org.deegree.model.feature.FeatureCollection)
     */
    public void setFeatureCollection( Layer layer, FeatureCollection featureCollection ) {
        ftp.setFeatureCollection( layer, featureCollection );
    }

    /**
     * 
     */
    public void refresh() {
        ftp.refresh();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.FeatureTable#clear()
     */
    public void clear() {
        ftp.clear();
    }

}
