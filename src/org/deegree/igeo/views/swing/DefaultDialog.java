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

package org.deegree.igeo.views.swing;

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.deegree.igeo.config.FrameViewFormType;
import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.mapmodel.ComponentPosition;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.views.IView;

/**
 * The <code>DefaultDialog</code> class is the base class for ViewForms that requires a {@link JDialog} like behavior
 * like being modal;
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author: Andreas Poth $
 * 
 * @version $Revision: $, $Date: $
 * 
 */
public abstract class DefaultDialog extends JDialog implements IView<Container> {

    /**
     * 
     */
    private static final long serialVersionUID = -6913064288231466820L;

    protected IModule<Container> owner;

    /**
     * 
     * 
     */
    protected DefaultDialog() {        
        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                super.windowClosing( e );
                owner.getComponentStateAdapter().setClosed( true );
                owner.clear();
            }
            
            public void windowClosed(WindowEvent e) {
                super.windowClosed( e );
                owner.getComponentStateAdapter().setClosed( true );
                owner.clear();
            };
        } );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#init(org.deegree.client.presenter.state.ComponentStateAdapter,
     * org.deegree.client.configuration.ViewForm)
     */
    public void init( ViewFormType viewForm )
                            throws Exception {
        setResizable( ( (FrameViewFormType) viewForm.get_AbstractViewForm().getValue() ).isResizeable() );
        if ( !( (FrameViewFormType) viewForm.get_AbstractViewForm().getValue() ).isClosable() ) {
            setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
        }
        ComponentPosition compPos = this.owner.getComponentPositionAdapter();
        if ( compPos.hasWindow() ) {      
            setLocation( compPos.getWindowLeft(), compPos.getWindowTop() );
            setSize( compPos.getWindowWidth(), compPos.getWindowHeight() );
        }
        setModal( true );
        setAlwaysOnTop( true );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#update()
     */
    public void update() {
        // TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#registerModule(org.deegree.client.application.modules.IModule)
     */
    public void registerModule( IModule<Container> module ) {
        this.owner = module;
    }

}
