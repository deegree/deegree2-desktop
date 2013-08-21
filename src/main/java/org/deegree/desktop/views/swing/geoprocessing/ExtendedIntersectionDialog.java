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
package org.deegree.desktop.views.swing.geoprocessing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import org.deegree.desktop.commands.geoprocessing.LayerIntersectionCommand.INTERSECTION_TYPE;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.Layer;
import org.deegree.desktop.modules.geoprocessing.IntersectionModule;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.desktop.views.swing.DefaultDialog;
import org.deegree.desktop.views.swing.util.IconRegistry;
import org.deegree.desktop.config.ViewFormType;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class ExtendedIntersectionDialog extends DefaultDialog implements IntersectionModel {

    private static final long serialVersionUID = 4150442055008217584L;

    private JPanel pnDescription;

    private JTextArea taDescription;

    private ExtendedIntersectionPanel intersectionPanel;

    private JButton btCancel;

    private JButton btOK;

    private JPanel pnButton;

    @Override
    public void init( ViewFormType viewForm )
                            throws Exception {
        super.init( viewForm );
        initGUI();
        intersectionPanel.registerModule( this.owner );
        intersectionPanel.init( viewForm );
        setVisible( true );
        toFront();
    }

    private void initGUI() {
        try {
            {
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] {0.0, 0.1};
                thisLayout.rowHeights = new int[] {255, 7};
                thisLayout.columnWeights = new double[] { 0.0, 0.1 };
                thisLayout.columnWidths = new int[] { 172, 7 };
                getContentPane().setLayout(thisLayout);
                {
                    pnDescription = new JPanel();
                    BorderLayout pnDescriptionLayout = new BorderLayout();
                    pnDescription.setLayout( pnDescriptionLayout );
                    getContentPane().add(
                                          pnDescription,
                                          new GridBagConstraints( 0, 0, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pnDescription.setBorder( BorderFactory.createTitledBorder( null, Messages.getMessage( getLocale(),
                                                                                                          "$MD11417" ),
                                                                               TitledBorder.LEADING,
                                                                               TitledBorder.DEFAULT_POSITION ) );
                    {
                        taDescription = new JTextArea();
                        taDescription.setLineWrap( true );
                        taDescription.setWrapStyleWord( true );
                        taDescription.setEditable( false );
                        pnDescription.add( taDescription, BorderLayout.CENTER );
                        taDescription.setText( Messages.getMessage( getLocale(), "$MD10576" ) );
                        taDescription.setBackground( pnDescription.getBackground() );
                    }
                }
                {
                    pnButton = new JPanel();
                    FlowLayout pnButtonLayout = new FlowLayout();
                    pnButtonLayout.setAlignment( FlowLayout.LEFT );
                    pnButton.setLayout( pnButtonLayout );
                    getContentPane().add(
                                          pnButton,
                                          new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    {
                        btOK = new JButton( Messages.getMessage( getLocale(), "$MD10571" ),
                                            IconRegistry.getIcon( "accept.png" ) );
                        btOK.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                if ( getNewLayerName().trim().length() == 0 ) {
                                    DialogFactory.openWarningDialog( "application", this, "layer name must be entered",
                                                                     "Warning" );
                                    return;
                                }
                                ( (IntersectionModule<Container>) owner ).intersect();
                            }
                        } );
                        pnButton.add( btOK );
                    }
                    {
                        btCancel = new JButton( Messages.getMessage( getLocale(), "$MD10573" ),
                                                IconRegistry.getIcon( "cancel.png" ) );
                        btCancel.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {
                                owner.clear();
                                dispose();
                            }
                        } );
                        pnButton.add( btCancel );
                    }
                }
                {
                    intersectionPanel = new ExtendedIntersectionPanel();
                    getContentPane().add(intersectionPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
                }
            }
            this.setSize(749, 323);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.IntersectionModel#getCompareLayer()
     */
    public Layer getCompareLayer() {
        return intersectionPanel.getCompareLayer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.IntersectionModel#getMainLayer()
     */
    public Layer getMainLayer() {
        return intersectionPanel.getMainLayer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.IntersectionModel#getNewLayerName()
     */
    public String getNewLayerName() {
        return intersectionPanel.getNewLayerName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.IntersectionModel#getIntersectionType()
     */
    public INTERSECTION_TYPE getIntersectionType() {
        return intersectionPanel.getIntersectionType();
    }

}
