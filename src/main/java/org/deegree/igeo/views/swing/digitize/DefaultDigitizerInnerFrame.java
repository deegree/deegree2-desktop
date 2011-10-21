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
import java.awt.Container;
import java.util.Map;

import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.deegree.igeo.config.InnerFrameViewFormType;
import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.config.WindowStateType;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.modules.DigitizerModule;
import org.deegree.igeo.views.ComponentPosition;
import org.deegree.igeo.views.DigitizerFunctionSelect;
import org.deegree.igeo.views.swing.DefaultInnerFrame;

/**
 * 
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class DefaultDigitizerInnerFrame extends DefaultInnerFrame implements DigitizerFunctionSelect {

    private static final long serialVersionUID = -6881917324936394513L;

    private DigitizerFunctionSelectPanel panel;

    /**
     * 
     *
     */
    public DefaultDigitizerInnerFrame() {
        setResizable( false );
        setClosable( true );
        addInternalFrameListener( new InternalFrameAdapter() {
            @Override
            public void internalFrameDeactivated( InternalFrameEvent e ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        moveToFront();
                    }
                } );

            }
        } );
        toFront();
    }

    @Override
    public void init( ViewFormType viewForm )
                            throws Exception {
        super.init( viewForm );
        ComponentPosition cp = this.owner.getComponentPositionAdapter();
        panel = new DigitizerFunctionSelectPanel( owner.getApplicationContainer() );
        setBounds( cp.getWindowLeft(), cp.getWindowTop(), panel.getPreferredSize().width + 5,
                   panel.getPreferredSize().height + 35 );
        panel.registerDigitizerModule( (DigitizerModule<?>) owner );

        InnerFrameViewFormType fvt = (InnerFrameViewFormType) viewForm.get_AbstractViewForm().getValue();
        setTitle( Messages.getMessage( getLocale(), fvt.getFrameTitle() ) );
        WindowStateType wst = fvt.getComponentState().getWindowState();
        if ( wst == WindowStateType.MAXIMIZED ) {
            this.setMaximum( true );
        }

        Container contentPane = getContentPane();
        contentPane.setLayout( new BorderLayout() );
        contentPane.add( panel, BorderLayout.CENTER );

        setVisible( true );
        toFront();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.editfeature.DigitizerFunctionSelect#getOptions()
     */
    public Map<String, Object> getOptions() {
        return panel.getOptions();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.editfeature.DigitizerFunctionSelect#getSelectionParameters()
     */
    public Map<String, Object> getSelectionParameters() {
        return panel.getSelectionParameters();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.digitize.DigitizerFunctionSelect#registerDigitizerModule(org.deegree.client.
     * application.modules.DigitizerModule)
     */
    public void registerDigitizerModule( DigitizerModule<?> digitizerModule ) {
        panel.registerDigitizerModule( digitizerModule );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.digitize.DigitizerFunctionSelect#unselectAll()
     */
    public void unselectAll() {
        panel.unselectAll();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.digitize.DigitizerFunctionSelect#selectFunction(java.lang.String)
     */
    public void selectFunction( String name ) {
        panel.selectFunction( name );

    }

}
