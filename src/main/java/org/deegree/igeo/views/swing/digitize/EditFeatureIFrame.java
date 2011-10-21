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

package org.deegree.igeo.views.swing.digitize;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.modules.DigitizerModule;
import org.deegree.igeo.modules.EditFeature;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class EditFeatureIFrame extends JInternalFrame implements EditFeature {

    private static final long serialVersionUID = -4534404389340932489L;

    private EditFeaturePanel panel;

    private Preferences prefs;

    /**
     * @param appContainer
     * @param layer
     * @param featureCollection
     */
    @SuppressWarnings("unchecked")
    public EditFeatureIFrame( ApplicationContainer<?> appContainer, final Layer layer,
                              FeatureCollection featureCollection ) {
        prefs = Preferences.userNodeForPackage( EditFeatureIFrame.class );
        panel = new EditFeaturePanel( (ApplicationContainer<Container>) appContainer, layer, featureCollection );
        getContentPane().setLayout( new BorderLayout() );
        getContentPane().add( panel, BorderLayout.CENTER );
        setVisible( true );
        setResizable( true );
        toFront();
        ImageIcon icon = new ImageIcon( EditFeatureIFrame.class.getResource( "resources/edit_attribut.png" ) );
        setFrameIcon( icon );
        setPreferredSize( new Dimension( 530, 600 ) );
        Border outsideBorder = BorderFactory.createEtchedBorder( EtchedBorder.RAISED );
        Border insideBorder = BorderFactory.createLineBorder( new Color( 197, 197, 220 ), 3 );
        setBorder( BorderFactory.createCompoundBorder( outsideBorder, insideBorder ) );
        
        int x = prefs.getInt( "posX", 50 );
        int Y = prefs.getInt( "posY", 50 );
        int w = prefs.getInt( "width", 530 );
        int h = prefs.getInt( "widthY", 600 );
        setBounds( x, Y, w, h );

        SwingUtilities.updateComponentTreeUI( this );

        addInternalFrameListener( new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed( InternalFrameEvent ev ) {
                panel.getDigitizerModule().resetDigitizerPane();
            }

        } );

        addComponentListener( new ComponentAdapter() {

            @Override
            public void componentMoved( ComponentEvent e ) {
                prefs.putInt( "posX", getX() );
                prefs.putInt( "posY", getY() );
                prefs.putInt( "width", getWidth() );
                prefs.putInt( "height", getHeight() );
            }
        } );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.modules.EditFeature#getFeature()
     */
    public FeatureCollection getFeatureCollection() {
        return panel.getFeatureCollection();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.modules.EditFeature#getCurrentFeature()
     */
    public Feature getCurrentFeature() {
        return panel.getCurrentFeature();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.modules.EditFeature#setFeature(org.deegree.igeo.mapmodel.Layer,
     * org.deegree.model.feature.Feature)
     */
    public void setFeature( Layer layer, FeatureCollection featureCollection ) {
        panel.setFeature( layer, featureCollection );
        SwingUtilities.updateComponentTreeUI( this );
        setVisible( true );
        toFront();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.modules.EditFeature#setDigitizerModule(org.deegree.igeo.modules.DigitizerModule)
     */
    public void setDigitizerModule( DigitizerModule<?> digitizerModule ) {
        panel.setDigitizerModule( digitizerModule );
    }

}
