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

package org.deegree.igeo.modules;

import static org.deegree.igeo.i18n.Messages.get;
import static org.deegree.igeo.views.DialogFactory.openErrorDialog;

import java.awt.Component;
import java.awt.Container;
import java.util.List;

import javax.swing.JFrame;

import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.ActionDescription.ACTIONTYPE;
import org.deegree.igeo.views.swing.style.EditFeatureStyleDialog;
import org.deegree.igeo.views.swing.style.RuleDialog;

/**
 * Module for editing styles assigned to a layer
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class EditStyleModule<T> extends DefaultModule<T> {

    private Layer currentLayer;

    static {
        ActionDescription ad1 = new ActionDescription( "editStyle",
                                                       "opens a dialog for editing style of selected layer", null,
                                                       "edit style", ACTIONTYPE.PushButton, null, null );
        ActionDescription ad2 = new ActionDescription(
                                                       "editFeatureStyle",
                                                       "opens a dialog for editing properties assigned to a CSS parameter",
                                                       null, "edit feature style", ACTIONTYPE.PushButton, null, null );
        moduleCapabilities = new ModuleCapabilities( ad1, ad2 );
    }

    /**
     * opens the rule dialog to edit the style of the selected layer
     */
    public void editStyle() {
        MapModel mm = appContainer.getMapModel( null );
        List<Layer> layers = mm.getLayersSelectedForAction( MapModel.SELECTION_ACTION );
        if ( layers.size() == 1 ) {
            this.componentStateAdapter.setClosed( false );
            this.createIView();
            Object view = this.getViewForm();
            if ( view instanceof RuleDialog ) {
                ( (RuleDialog) view ).setApplicationContainer( appContainer );
            }
            if ( view instanceof JFrame ) {
                ( (JFrame) view ).setVisible( true );
            }
            if ( currentLayer != layers.get( 0 ) ) {
                currentLayer = layers.get( 0 );
            }
            editLayer();
        } else {
            openErrorDialog( appContainer.getViewPlatform(), (Component) getViewForm(), get( "$MD10714" ),
                             get( "$DI10017" ) );
        }
    }

    private void editLayer() {
        Object view = this.getViewForm();
        if ( view instanceof RuleDialog && currentLayer != null ) {
            ( (RuleDialog) view ).reset();
            ( (RuleDialog) view ).setLayer( currentLayer );
        }
    }

    /**
     * will be invoked to open a dialog for editing properties of a feature assigned to a CSS parameter
     */
    @SuppressWarnings("unchecked")
    public void editFeatureStyle() {
        // TODO
        if ( appContainer.getViewPlatform().equalsIgnoreCase( "application" ) ) {
            EditFeatureStyleDialog.create( (EditStyleModule<Container>) this ).resetToolbar();
        }
    }

}
