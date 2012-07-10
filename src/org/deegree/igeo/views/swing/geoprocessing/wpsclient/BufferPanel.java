//$HeadURL: svn+ssh://developername@svn.wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
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
package org.deegree.igeo.views.swing.geoprocessing.wpsclient;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.deegree.framework.util.StringTools;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.model.feature.schema.GeometryPropertyType;
import org.deegree.model.spatialschema.Geometry;

/**
 * 
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author: Andreas Poth $
 * 
 * @version $Revision: $, $Date: $
 * 
 */
public class BufferPanel extends JPanel implements ProcessParameter {

    private static final long serialVersionUID = -708999532070884967L;

    private JTextField tfNewLayer;

    private JComboBox cbGeomProperty;

    private JSpinner spSegments;

    private JComboBox cbCapStyle;

    private JSpinner spDistance;

    private JLabel jLabel5;

    private JLabel jLabel4;

    private JLabel jLabel3;

    private JLabel jLabel2;

    private JLabel jLabel1;

    private GeometryPropertyType[] gpt;

    private String layerTitle;

    /**
     * 
     * @param appCont
     */
    public BufferPanel( ApplicationContainer<?> appCont ) {
        List<Layer> layers = appCont.getMapModel( null ).getLayersSelectedForAction( MapModel.SELECTION_ACTION );
        Layer layer = layers.get( 0 );
        FeatureAdapter fa = (FeatureAdapter) layer.getDataAccess().get( 0 );
        this.layerTitle = layer.getTitle() + "_buffer";
        gpt = fa.getSchema().getGeometryProperties();
        initGUI();
        setVisible( true );
    }

    private void initGUI() {
        try {
            {
                this.setSize( 500, 400 );
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
                thisLayout.rowHeights = new int[] { 35, 35, 35, 35, 40 };
                thisLayout.columnWeights = new double[] { 0.0, 0.1, 0.1 };
                thisLayout.columnWidths = new int[] { 146, 7, 7 };
                this.setLayout( thisLayout );
                {
                    jLabel1 = new JLabel();
                    this.add( jLabel1, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                               GridBagConstraints.HORIZONTAL,
                                                               new Insets( 0, 10, 0, 5 ), 0, 0 ) );
                    jLabel1.setText( Messages.getMessage( getLocale(), "$MD10562" ) );
                }
                {
                    jLabel2 = new JLabel();
                    this.add( jLabel2, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                               GridBagConstraints.HORIZONTAL,
                                                               new Insets( 0, 10, 0, 5 ), 0, 0 ) );
                    jLabel2.setText( Messages.getMessage( getLocale(), "$MD10563" ) );
                }
                {
                    jLabel3 = new JLabel();
                    this.add( jLabel3, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                               GridBagConstraints.HORIZONTAL,
                                                               new Insets( 0, 10, 0, 5 ), 0, 0 ) );
                    jLabel3.setText( Messages.getMessage( getLocale(), "$MD10564" ) );
                }
                {
                    jLabel4 = new JLabel();
                    this.add( jLabel4, new GridBagConstraints( 0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                               GridBagConstraints.HORIZONTAL,
                                                               new Insets( 0, 10, 0, 5 ), 0, 0 ) );
                    jLabel4.setText( Messages.getMessage( getLocale(), "$MD10565" ) );
                }
                {
                    jLabel5 = new JLabel();
                    this.add( jLabel5, new GridBagConstraints( 0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                               GridBagConstraints.HORIZONTAL,
                                                               new Insets( 0, 10, 0, 5 ), 0, 0 ) );
                    jLabel5.setText( Messages.getMessage( getLocale(), "$MD10566" ) );
                }
                {
                    spDistance = new JSpinner( new SpinnerNumberModel( 1d, 0, Integer.MAX_VALUE, 1d ) );
                    this.add( spDistance, new GridBagConstraints( 1, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL,
                                                                  new Insets( 0, 5, 0, 5 ), 0, 0 ) );
                }
                {
                    String[] tmp = StringTools.toArray( Messages.getMessage( getLocale(), "$MD10570" ), ",;", true );
                    cbCapStyle = new JComboBox( tmp );
                    this.add( cbCapStyle, new GridBagConstraints( 1, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL,
                                                                  new Insets( 0, 5, 0, 5 ), 0, 0 ) );
                }
                {
                    spSegments = new JSpinner( new SpinnerNumberModel( 12, 1, Integer.MAX_VALUE, 1 ) );
                    this.add( spSegments, new GridBagConstraints( 1, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL,
                                                                  new Insets( 0, 5, 0, 5 ), 0, 0 ) );
                }
                {
                    String[] s = new String[gpt.length];
                    for ( int i = 0; i < s.length; i++ ) {
                        s[i] = gpt[i].getName().getPrefixedName();
                    }
                    cbGeomProperty = new JComboBox( s );
                    this.add( cbGeomProperty, new GridBagConstraints( 1, 3, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                      GridBagConstraints.HORIZONTAL,
                                                                      new Insets( 0, 5, 0, 5 ), 0, 0 ) );
                }
                {
                    tfNewLayer = new JTextField( layerTitle );
                    this.add( tfNewLayer, new GridBagConstraints( 1, 4, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL,
                                                                  new Insets( 0, 5, 0, 5 ), 0, 0 ) );
                }

            }

        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.BufferModel#getCapStyle()
     */
    public int getCapStyle() {
        int idx = cbCapStyle.getSelectedIndex();
        if ( idx == 0 ) {
            return Geometry.BUFFER_CAP_ROUND;
        }
        if ( idx == 1 ) {
            return Geometry.BUFFER_CAP_BUTT;
        }
        if ( idx == 2 ) {
            return Geometry.BUFFER_CAP_SQUARE;
        }
        return Geometry.BUFFER_CAP_ROUND;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.geoprocessing.wpsclient.ProcessParameter#getParameter()
     */
    public Map<String, Object> getParameter() {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put( "$ApproximationQuantization$", ( (Number) spSegments.getValue() ).intValue() );
        param.put( "layerName", tfNewLayer.getText() );
        param.put( "geometryProperty", gpt[cbGeomProperty.getSelectedIndex()].getName() );
        param.put( "$BufferDistance$", ( (Number) spDistance.getValue() ).doubleValue() );
        int idx = cbCapStyle.getSelectedIndex();
        int style = Geometry.BUFFER_CAP_ROUND;
        if ( idx == 0 ) {
            style = Geometry.BUFFER_CAP_ROUND;
        }
        if ( idx == 1 ) {
            style = Geometry.BUFFER_CAP_BUTT;
        }
        if ( idx == 2 ) {
            style = Geometry.BUFFER_CAP_SQUARE;
        }
        param.put( "$EndCapStyle$",  style );
        return param;
    }

}
