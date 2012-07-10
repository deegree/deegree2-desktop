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
package org.deegree.igeo.views.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Stack;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.deegree.framework.util.MapUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.ChangeListener;
import org.deegree.igeo.ValueChangedEvent;
import org.deegree.igeo.commands.model.ZoomCommand;
import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MapModelChangedEvent;
import org.deegree.igeo.mapmodel.MapModelChangedEvent.CHANGE_TYPE;
import org.deegree.igeo.views.DialogFactory;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class ScaleSelectionPanel extends DefaultPanel implements ChangeListener, ActionListener {

    private static final long serialVersionUID = -1742712350112994597L;

    private MapModel mapModel;

    private Stack<ScaleLabel> predefinedScales = new Stack<ScaleLabel>();

    private ScaleLabel extraScale;

    private DefaultComboBoxModel comboBoxModel;

    private ApplicationContainer<Container> appContainer;

    private boolean ignore = false;

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#init(org.deegree.client.configuration.ViewForm)
     */
    public void init( ViewFormType viewForm )
                            throws Exception {

        this.appContainer = this.owner.getApplicationContainer();
        this.mapModel = appContainer.getMapModel( null );

        // register change listener to be informed about changes of the map model
        // (concrete: extent of the map model)
        this.mapModel.addChangeListener( this );

        setLayout( new FlowLayout( FlowLayout.LEFT, 2, 0 ) );

        initGUI();
    }

    private void initGUI() {
        // the label of the module
        JLabel scaleLabel = new JLabel( Messages.getMessage( Locale.getDefault(), "$MD10000" ) );
        scaleLabel.setVisible( true );
        add( scaleLabel );
        add( Box.createRigidArea( new Dimension( 10, 0 ) ) );

        // the drop down list
        this.comboBoxModel = new DefaultComboBoxModel();
        JComboBox scaleListBox = new JComboBox( this.comboBoxModel );
        scaleListBox.setEditable( true );
        add( scaleListBox );

        String[] defScales = this.owner.getInitParameter( "scales" ).split( "," );
        for ( int i = 0; i < defScales.length; i++ ) {
            this.predefinedScales.push( new ScaleLabel( Double.parseDouble( defScales[i] ) ) );
        }

        // add current and predefined scales to the combo box
        double scale = MapUtils.calcScale( this.mapModel.getTargetDevice().getPixelWidth(),
                                           this.mapModel.getTargetDevice().getPixelHeight(),
                                           this.mapModel.getEnvelope(), this.mapModel.getCoordinateSystem(),
                                           MapUtils.DEFAULT_PIXEL_SIZE );

        extraScale = new ScaleLabel( scale );

        this.comboBoxModel.setSelectedItem( extraScale.getLabel() );

        for ( ScaleLabel preScale : predefinedScales ) {
            this.comboBoxModel.addElement( preScale.getLabel() );
        }

        // register action listener to be informed about changes of the combo box
        scaleListBox.addActionListener( this );
    }

    @Override
    public void update() {
        super.update();
        MapModel tmp = appContainer.getMapModel( null );
        if ( this.mapModel == null || !tmp.equals( this.mapModel ) ) {
            this.mapModel = tmp;
            this.mapModel.removeChangeListener( this );
            this.mapModel.addChangeListener( this );
            updateScaleList();
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // ChangeListener
    // /////////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.presenter.ChangeListener#valueChanged(org.deegree.client.presenter.ValueChangedEvent)
     */
    public void valueChanged( ValueChangedEvent event ) {
        if ( event instanceof MapModelChangedEvent ) {
            MapModelChangedEvent mapModelEvent = (MapModelChangedEvent) event;

            if ( mapModelEvent.getChangeType() == CHANGE_TYPE.extentChanged
                 || mapModelEvent.getChangeType() == CHANGE_TYPE.targetDeviceChanged ) {

                // in diesem fall nicht action performed ausführen!
                ignore = true;
                updateScaleList();
            }
        }
    }

    /**
     * updates the list of scales
     */
    private void updateScaleList() {

        double scale = MapUtils.calcScale( this.mapModel.getTargetDevice().getPixelWidth(),
                                           this.mapModel.getTargetDevice().getPixelHeight(),
                                           this.mapModel.getEnvelope(), this.mapModel.getCoordinateSystem(),
                                           MapUtils.DEFAULT_PIXEL_SIZE );

        // remove not predefined item (must be on the first position!), if existent
        boolean predefined = false;
        for ( ScaleLabel predefScaleLabel : predefinedScales ) {
            if ( predefScaleLabel.getLabel().equals( this.comboBoxModel.getElementAt( 0 ) ) ) {
                predefined = true;
            }
        }

        ScaleLabel sc = new ScaleLabel( scale );

        if ( !predefined && !( sc.getLabel().equals( this.comboBoxModel.getElementAt( 0 ) ) ) ) {
            this.comboBoxModel.removeElementAt( 0 );
        }

        boolean exist = false;
        // select entry with the current scale, if already in the list
        for ( int i = 0; i < this.comboBoxModel.getSize(); i++ ) {
            if ( sc.getLabel().equals( this.comboBoxModel.getElementAt( i ) ) ) {
                this.comboBoxModel.setSelectedItem( this.comboBoxModel.getElementAt( i ) );
                exist = true;
            }
        }

        // current scale is not yet in the list of scales (can not be one of the predefined) ->
        // add the new scale and select them
        if ( !exist ) {
            this.extraScale = sc;
            this.comboBoxModel.setSelectedItem( sc.getLabel() );
        }

    }

    // /////////////////////////////////////////////////////////////////////////////////
    // ActionListener
    // /////////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed( ActionEvent event ) {

        if ( !ignore ) {

            // switch scale only if the current scale of the map is not the same like the current scale
            // in the list
            double currentMapScale = MapUtils.calcScale( this.mapModel.getTargetDevice().getPixelWidth(),
                                                         this.mapModel.getTargetDevice().getPixelHeight(),
                                                         this.mapModel.getEnvelope(),
                                                         this.mapModel.getCoordinateSystem(),
                                                         MapUtils.DEFAULT_PIXEL_SIZE );

            double currentListScale = getScaleOutOfLabel( (String) this.comboBoxModel.getSelectedItem() );
            if ( currentListScale > 0 ) {

                if ( currentMapScale != currentListScale ) {
                    ZoomCommand zoom = new ZoomCommand( this.mapModel );

                    zoom.setNewScale( currentListScale, this.mapModel.getTargetDevice().getPixelWidth(),
                                      this.mapModel.getTargetDevice().getPixelHeight() );
                    ApplicationContainer<Container> appContainer = owner.getApplicationContainer();
                    try {
                        appContainer.getCommandProcessor().executeSychronously( zoom, true );
                    } catch ( Exception ex ) {
                        DialogFactory.openErrorDialog( appContainer.getViewPlatform(), ScaleSelectionPanel.this,
                                                       Messages.get( "$MD11244" ), Messages.get( "$MD11245",
                                                                                                 currentListScale ), ex );
                    }
                }
            } else {
                DialogFactory.openErrorDialog( appContainer.getViewPlatform(), this, Messages.getMessage( getLocale(),
                                                                                                          "$MD11289" ),
                                               Messages.getMessage( getLocale(), "$MD11290" ) );
            }
        }
        ignore = false;
    }

    /**
     * 
     * @param s
     * @return scale value
     */
    private double getScaleOutOfLabel( String s ) {
        try {
            String[] tmp = StringTools.toArray( s, ":", false );
            s = StringTools.replace( tmp[1], ".", "", true );
            s = StringTools.replace( s, ",", ".", true );
            return Double.parseDouble( s );
        } catch ( Exception e ) {
            return -1;
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // inner classe //
    // /////////////////////////////////////////////////////////////////////////////////

    /**
     * 
     * <code>KeyValue</code>
     * 
     * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
     * @author last edited by: $Author:$
     * 
     * @version $Revision:$, $Date:$
     * 
     */
    private class ScaleLabel {

        String label;

        public ScaleLabel( double scale ) {
             DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance( Locale.GERMAN );
            df.applyPattern( "#,###,##0" );
            String s = df.format( scale );

            this.label = "1 : " + s;
        }

        public String getLabel() {
            return label;
        }
     

    }

}
