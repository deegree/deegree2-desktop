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
package org.deegree.igeo.views.swing.map;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.deegree.datatypes.QualifiedName;
import org.deegree.datatypes.Types;
import org.deegree.framework.utils.DictionaryCollection;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.SelectFeatureCommand;
import org.deegree.igeo.dataadapter.DataAccessAdapter;
import org.deegree.igeo.dataadapter.FeatureAdapter;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.swing.AttributeCriteriaPanel;
import org.deegree.igeo.views.swing.util.GuiUtils;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.kernel.Command;
import org.deegree.model.feature.schema.PropertyType;
import org.deegree.model.filterencoding.ComplexFilter;
import org.deegree.model.filterencoding.Filter;
import org.deegree.model.filterencoding.LogicalOperation;
import org.deegree.model.filterencoding.Operation;
import org.deegree.model.filterencoding.OperationDefines;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class SelectByAttributeDialog extends JDialog {

    private static final long serialVersionUID = 8985710922491934454L;

    private AttributeCriteriaPanel attrPanel;

    private Layer layer;

    private ApplicationContainer<?> appCont;

    /**
     * 
     * @param mapModel
     */
    public SelectByAttributeDialog( MapModel mapModel ) {
        setSize( new Dimension( 450, 550 ) );
        Point p = GuiUtils.getCenterOfMainFrame();
        setLocation( p.x - getWidth() / 2, p.y - getHeight() / 2 );
        appCont = mapModel.getApplicationContainer();
        // at least one layer must be selected
        List<Layer> layers = mapModel.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
        if ( layers.size() == 0 ) {
            DialogFactory.openWarningDialog( appCont.getViewPlatform(), this, Messages.getMessage( getLocale(),
                                                                                                   "$MD11594" ),
                                             Messages.getMessage( getLocale(), "$MD11595" ) );
            return;
        }
        // just first selected layer will be considered for select command
        layer = layers.get( 0 );
        DataAccessAdapter ada = layer.getDataAccess().get( 0 );
        // layer must contain feature - selection on raster data is not possible
        if ( !( ada instanceof FeatureAdapter ) ) {
            DialogFactory.openWarningDialog( appCont.getViewPlatform(), this, Messages.getMessage( getLocale(),
                                                                                                   "$MD11596" ),
                                             Messages.getMessage( getLocale(), "$MD11597" ) );
            return;
        }
        // find all alpha numeric properties of a layers featuretype
        QualifiedName featureType = ( (FeatureAdapter) ada ).getSchema().getName();
        PropertyType[] pt = ( (FeatureAdapter) ada ).getSchema().getProperties();
        List<QualifiedName> propertyNames = new ArrayList<QualifiedName>( pt.length );
        for ( PropertyType propertyType : pt ) {
            if ( propertyType.getType() != Types.GEOMETRY ) {
                propertyNames.add( propertyType.getName() );
            }
        }
        DictionaryCollection dictCollection = appCont.getSettings().getDictionaries();
        initGUI( propertyNames, featureType, dictCollection );

        // show this dialog
        setModal( true );
        setVisible( true );
    }

    private void initGUI( List<QualifiedName> propertyNames, QualifiedName featureType,
                          DictionaryCollection dictCollection ) {
        setLayout( new BorderLayout() );
        add( attrPanel = new AttributeCriteriaPanel( propertyNames, featureType, dictCollection ), BorderLayout.CENTER );
        JPanel pnButtons = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        JButton btOK = new JButton( Messages.getMessage( getLocale(), "$MD11598" ), IconRegistry.getIcon( "accept.png" ) );
        btOK.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                List<Operation> allOperations = new ArrayList<Operation>();
                List<Operation> attOperations = attrPanel.getOperations();

                for ( Operation operation : attOperations ) {
                    allOperations.add( operation );
                }
                if ( allOperations.size() > 0 ) {
                    Operation finalOperation = null;
                    if ( allOperations.size() == 1 ) {
                        // if only one operation is created, it is not needed to create a logical operation
                        finalOperation = allOperations.get( 0 );
                    } else {
                        // create a logical operation out of all operations combined with the logical
                        // operation
                        int operation = OperationDefines.getIdByName( attrPanel.getOperation() );
                        finalOperation = new LogicalOperation( operation, allOperations );
                    }
                    // create the filter
                    Filter filter = new ComplexFilter( finalOperation );
                    Command cmd = new SelectFeatureCommand( layer, filter, false );
                    try {
                        appCont.getCommandProcessor().executeSychronously( cmd, true );
                    } catch ( Exception ex ) {
                        DialogFactory.openErrorDialog( appCont.getViewPlatform(), SelectByAttributeDialog.this,
                                                       Messages.getMessage( getLocale(), "$MD11600" ),
                                                       Messages.getMessage( getLocale(), "$MD11601" ), ex );
                    }
                } else {
                    DialogFactory.openWarningDialog( appCont.getViewPlatform(), this,
                                                     Messages.getMessage( getLocale(), "$MD11602" ),
                                                     Messages.getMessage( getLocale(), "$MD11603" ) );
                    return;
                }
            }
        } );
        pnButtons.add( btOK );
        JButton btClose = new JButton( Messages.getMessage( getLocale(), "$MD11599" ),
                                       IconRegistry.getIcon( "cancel.png" ) );
        btClose.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        } );
        pnButtons.add( btClose );
        add( pnButtons, BorderLayout.SOUTH );
    }

}
