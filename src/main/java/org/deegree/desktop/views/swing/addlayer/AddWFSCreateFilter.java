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

package org.deegree.desktop.views.swing.addlayer;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.commands.model.AddWFSLayerCommand;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.views.Filter;
import org.deegree.desktop.views.FilterFactory;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.kernel.CommandProcessedEvent;
import org.deegree.kernel.CommandProcessedListener;
import org.deegree.kernel.ProcessMonitor;
import org.deegree.kernel.ProcessMonitorFactory;
import org.deegree.ogcwebservices.OWSUtils;
import org.deegree.ogcwebservices.getcapabilities.GetCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSFeatureType;

/**
 * <code>JAddWFSCreateFilter</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class AddWFSCreateFilter extends WizardDialog implements ActionListener {

    private static final long serialVersionUID = -6036845705166520023L;

    private static final ILogger LOG = LoggerFactory.getLogger( AddWFSCreateFilter.class );

    private MapModel mapModel;

    private URL capabilitiesURL;

    private WFSCapabilities wfsCapabilities;

    private WFSFeatureType featureType;

    private QualifiedName geometryProp;

    private Filter filter;

    /**
     * @param frame
     * @param geometryProp
     * @throws Exception
     */
    public AddWFSCreateFilter( JFrame frame, MapModel mapModel, ApplicationContainer<Container> appContainer,
                               WFSCapabilities wfsCapabilities, WFSFeatureType featureType, QualifiedName geometryProp )
                            throws Exception {
        super( frame );

        this.mapModel = mapModel;
        this.appContainer = appContainer;
        this.capabilitiesURL = OWSUtils.getHTTPGetOperationURL( wfsCapabilities, GetCapabilities.class );
        this.wfsCapabilities = wfsCapabilities;
        this.featureType = featureType;
        this.geometryProp = geometryProp;

        this.setSize( 500, 740 );
        this.setResizable( false );
        this.setTitle( Messages.getMessage( Locale.getDefault(), "$MD10133" ) );

        String capUrl = this.capabilitiesURL.toExternalForm();
        int index = capUrl.indexOf( '?' );
        String wfsUrl = capUrl;
        if ( index > -1 ) {
            wfsUrl = capUrl.substring( 0, index );
        }
        try {
            // create the filter for the selected feature type
            filter = FilterFactory.createFilter( new URL( wfsUrl ), this.wfsCapabilities, this.featureType.getName(),
                                                 this.mapModel, this.appContainer );
        } catch ( MalformedURLException e ) {
            // should never happen
            LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10068", wfsUrl, e.getMessage() ) );
            throw e;
        } catch ( Exception e ) {
            LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10068", wfsUrl, e.getMessage() ) );
            throw e;
        }

        infoPanel.setInfoText( Messages.getMessage( Locale.getDefault(), "$MD10134" ) );

        buttonPanel.registerActionListener( this );
        super.init();
    }

    // /////////////////////////////////////////////////////////////////////////////////
    // WizardDialog
    // /////////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.addlayer.WizardDialog#getMainPanel()
     */
    @Override
    public JPanel getMainPanel() {
        return (JPanel) filter;
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
        if ( event.getSource() instanceof JButton ) {
            JButton srcButton = (JButton) event.getSource();
            if ( srcButton.getName().equals( ButtonPanel.CANCEL_BT ) ) {
                this.dispose();
            } else if ( srcButton.getName().equals( ButtonPanel.PREVIOUS_BT ) ) {
                if ( this.previousFrame != null ) {
                    this.previousFrame.setVisible( true );
                }
                this.close();
            } else if ( srcButton.getName().equals( ButtonPanel.NEXT_BT ) ) {
                org.deegree.model.filterencoding.Filter f = (org.deegree.model.filterencoding.Filter) this.filter.getFilter();
                AddWFSSummary nextStep = new AddWFSSummary( this, this.mapModel, this.appContainer,
                                                            this.wfsCapabilities, this.featureType, this.geometryProp,
                                                            f );
                nextStep.setLocation( this.getX(), this.getY() );
                nextStep.setVisible( true );
                this.setVisible( false );
            } else if ( srcButton.getName().equals( ButtonPanel.FINISH_BT ) ) {
                // adds the new layer and closes the current window
                org.deegree.model.filterencoding.Filter f = (org.deegree.model.filterencoding.Filter) this.filter.getFilter();
                AddWFSLayerCommand command = new AddWFSLayerCommand( this.mapModel, this.wfsCapabilities,
                                                                     this.featureType, this.geometryProp, f );
                final ProcessMonitor pm = ProcessMonitorFactory.createDialogProcessMonitor(
                                                                                            appContainer.getViewPlatform(),
                                                                                            Messages.get( "$MD11272" ),
                                                                                            Messages.get(
                                                                                                          "$MD11273",
                                                                                                          featureType.getTitle() ),
                                                                                            0, -1, command );
                command.setProcessMonitor( pm );
                command.addListener( new CommandProcessedListener() {

                    public void commandProcessed( CommandProcessedEvent event ) {
                        try {
                            pm.cancel();
                        } catch ( Exception e ) {
                            e.printStackTrace();
                        }
                    }

                } );
                appContainer.getCommandProcessor().executeASychronously( command );
                this.dispose();
            }
        }
    }

}
