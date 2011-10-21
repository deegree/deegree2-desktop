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
package org.deegree.igeo.views.swing.layerlist;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;

import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.mapmodel.MapModelEntry;
import org.deegree.igeo.modules.LayerListTreeViewModule;
import org.deegree.igeo.views.HelpManager;
import org.deegree.igeo.views.swing.HelpFrame;
import org.deegree.igeo.views.swing.util.IconRegistry;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class InfoPanel extends JPanel {

    private static final long serialVersionUID = -1590677362265513481L;

    private JPanel pnAbstract;

    private JPanel pnButtons;

    private JButton btTake;

    private JToggleButton tbSelectedForEdit;

    private JSpinner spMax;

    private JSpinner spMin;

    private JLabel lbMax;

    private JLabel lnMin;

    private JPanel pnScale;

    private JLabel lbLegend;

    private JPanel pnLegend;

    private JButton btHelp;

    private JToggleButton tbEditable;

    private JToggleButton tbQueryable;

    private JToggleButton tbVisible;

    private JPanel pnHelp;

    private JPanel pnState;

    private JScrollPane scLegend;

    private JEditorPane epDescription;

    private MapModelEntry mme;

    /**
     * 
     */
    public InfoPanel() {
        initGUI();
    }

    /**
     * 
     * @param mme
     */
    void setMapModelEntry( MapModelEntry mme ) {
        this.mme = mme;
        this.epDescription.setText( mme.getAbstract() );
        this.tbVisible.getModel().setSelected( mme.isVisible() );
        this.tbQueryable.getModel().setSelected( mme.isQueryable() );
        if ( mme instanceof Layer ) {
            Layer layer = (Layer) mme;
            this.lbLegend.setIcon( new ImageIcon( layer.getLegend() ) );
            this.tbEditable.getModel().setSelected( layer.isEditable() );
            this.tbSelectedForEdit.setSelected( layer.getSelectedFor().contains( MapModel.SELECTION_EDITING ) );
            double d = Math.round( layer.getMaxScaleDenominator() / 100 );
            ( (SpinnerNumberModel) this.spMin.getModel() ).setValue( layer.getMinScaleDenominator() );
            ( (SpinnerNumberModel) this.spMin.getModel() ).setStepSize( d );
            ( (SpinnerNumberModel) this.spMax.getModel() ).setValue( layer.getMaxScaleDenominator() );
            ( (SpinnerNumberModel) this.spMax.getModel() ).setStepSize( d );

        }
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            this.setPreferredSize( new java.awt.Dimension( 588, 351 ) );
            thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.1 };
            thisLayout.rowHeights = new int[] { 166, 65, 83, -1, 7 };
            thisLayout.columnWeights = new double[] { 0.0, 0.0, 0.0 };
            thisLayout.columnWidths = new int[] { 166, 254, 147 };
            this.setLayout( thisLayout );
            {
                pnLegend = new JPanel();
                BorderLayout pn_legendLayout = new BorderLayout();
                pnLegend.setLayout( pn_legendLayout );
                this.add( pnLegend, new GridBagConstraints( 0, 1, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER,
                                                             GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnLegend.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11128" ) ) );
                {
                    scLegend = new JScrollPane();
                    pnLegend.add( scLegend, BorderLayout.CENTER );
                    {
                        lbLegend = new JLabel();
                        scLegend.setViewportView( lbLegend );
                        lbLegend.setPreferredSize( new java.awt.Dimension( 143, 15 ) );
                    }
                }
            }

            {
                pnState = new JPanel();
                GridBagLayout pn_stateLayout = new GridBagLayout();
                this.add( pnState, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                            GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnState.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11129" ) ) );
                pn_stateLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.1 };
                pn_stateLayout.rowHeights = new int[] { 28, 28, 28, 28, 7 };
                pn_stateLayout.columnWeights = new double[] { 0.1 };
                pn_stateLayout.columnWidths = new int[] { 7 };
                pnState.setLayout( pn_stateLayout );
                {
                    Icon icon = new ImageIcon( InfoPanel.class.getResource( "visible.png" ) );
                    tbVisible = new JToggleButton( icon );
                    pnState.add( tbVisible, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                      GridBagConstraints.NONE,
                                                                      new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    tbVisible.setToolTipText( Messages.getMessage( getLocale(), "$MD10001" ) );
                }
                {
                    Icon icon = new ImageIcon( InfoPanel.class.getResource( "queryable.png" ) );
                    tbQueryable = new JToggleButton( icon );
                    pnState.add( tbQueryable, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                                                        GridBagConstraints.CENTER,
                                                                        GridBagConstraints.NONE,
                                                                        new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    tbQueryable.setToolTipText( Messages.getMessage( getLocale(), "$MD10002" ) );
                }
                {
                    Icon icon = new ImageIcon( InfoPanel.class.getResource( "editable.png" ) );
                    tbEditable = new JToggleButton( icon );
                    pnState.add( tbEditable, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                       GridBagConstraints.NONE,
                                                                       new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    tbEditable.setToolTipText( Messages.getMessage( getLocale(), "$MD10003" ) );
                }
                {
                    Icon icon = new ImageIcon( InfoPanel.class.getResource( "selected4edit.png" ) );
                    tbSelectedForEdit = new JToggleButton( icon );
                    pnState.add( tbSelectedForEdit, new GridBagConstraints( 0, 3, 1, 1, 0.0, 0.0,
                                                                                GridBagConstraints.CENTER,
                                                                                GridBagConstraints.NONE,
                                                                                new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                    tbSelectedForEdit.setToolTipText( Messages.getMessage( getLocale(), "$MD10005" ) );
                }
            }

            {
                pnAbstract = new JPanel();
                BorderLayout pn_abstractLayout = new BorderLayout();
                pnAbstract.setLayout( pn_abstractLayout );
                this.add( pnAbstract, new GridBagConstraints( 1, 0, 2, 2, 0.0, 0.0, GridBagConstraints.CENTER,
                                                               GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnAbstract.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11130" ) ) );
                {
                    epDescription = new JEditorPane();
                    JScrollPane sc = new JScrollPane( epDescription );
                    pnAbstract.add( sc, BorderLayout.CENTER );
                    epDescription.setPreferredSize( new java.awt.Dimension( 259, 218 ) );
                }
            }
            {
                pnButtons = new JPanel();
                FlowLayout pn_buttonsLayout = new FlowLayout();
                pn_buttonsLayout.setAlignment( FlowLayout.LEFT );
                pnButtons.setLayout( pn_buttonsLayout );
                this.add( pnButtons, new GridBagConstraints( 0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                              GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                {
                    btTake = new JButton( Messages.getMessage( getLocale(), "$MD10006" ),
                                           IconRegistry.getIcon( "save.gif" ) );
                    btTake.setToolTipText( Messages.getMessage( getLocale(), "$MD11125" ) );
                    pnButtons.add( btTake );
                    btTake.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            mme.setAbstract( epDescription.getText() );
                            if ( mme instanceof Layer ) {
                                double min = ( (Number) spMin.getValue() ).doubleValue();
                                double max = ( (Number) spMax.getValue() ).doubleValue();
                                double d = min;
                                if ( min > max ) {
                                    min = max;
                                    max = d;
                                }
                                ( (Layer) mme ).setMinScaleDenominator( min );
                                ( (Layer) mme ).setMaxScaleDenominator( max );
                            }
                            ( (Layer) mme ).fireRepaintEvent();
                        }

                    } );
                }
            }
            {
                pnHelp = new JPanel();
                FlowLayout jPanel1Layout = new FlowLayout();
                jPanel1Layout.setAlignment( FlowLayout.RIGHT );
                pnHelp.setLayout( jPanel1Layout );
                this.add( pnHelp, new GridBagConstraints( 2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                           GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                {
                    btHelp = new JButton( Messages.getMessage( getLocale(), "$MD11126" ),
                                           IconRegistry.getIcon( "help.png" ) );
                    pnHelp.add( btHelp );
                    btHelp.setToolTipText( Messages.getMessage( getLocale(), "$MD11127" ) );
                    btHelp.addActionListener( new ActionListener() {

                        @SuppressWarnings("unchecked")
                        public void actionPerformed( ActionEvent e ) {
                            ApplicationContainer<Container> appCont = (ApplicationContainer<Container>) mme.getOwner().getApplicationContainer();
                            HelpFrame hf = HelpFrame.getInstance( new HelpManager( appCont ) );
                            hf.setVisible( true );
                            hf.gotoModule( LayerListTreeViewModule.class.getName() );
                        }
                    } );
                }
            }
            {
                pnScale = new JPanel();
                FormLayout pnScaleLayout = new FormLayout( "38dlu, 101dlu", "18dlu, 18dlu" );
                pnScale.setLayout( pnScaleLayout );
                this.add( pnScale, new GridBagConstraints( 1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                                                           GridBagConstraints.VERTICAL, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnScale.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11207" ) ) );
                {
                    lnMin = new JLabel();
                    pnScale.add( lnMin, new CellConstraints( "1, 1, 1, 1, default, default" ) );
                    lnMin.setText( Messages.getMessage( getLocale(), "$MD11208" ) );
                }
                {
                    lbMax = new JLabel();
                    pnScale.add( lbMax, new CellConstraints( "1, 2, 1, 1, default, default" ) );
                    lbMax.setText( Messages.getMessage( getLocale(), "$MD11209" ) );
                }
                {
                    spMin = new JSpinner( new SpinnerNumberModel( 0, 0, 9E99, 100 ) );
                    pnScale.add( spMin, new CellConstraints( 2, 1, 1, 1, CellConstraints.FILL, CellConstraints.DEFAULT,
                                                             new Insets( 0, 10, 0, 10 ) ) );
                }
                {
                    spMax = new JSpinner( new SpinnerNumberModel( 0, 0, 9E99, 100 ) );
                    pnScale.add( spMax, new CellConstraints( 2, 2, 1, 1, CellConstraints.FILL, CellConstraints.CENTER,
                                                             new Insets( 0, 10, 0, 10 ) ) );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

}
