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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Locale;

import javax.swing.ButtonModel;
import javax.swing.JFrame;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.modules.IModule;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.desktop.views.swing.CursorRegistry;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.HttpUtils;
import org.deegree.ogcwebservices.OWSUtils;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilities;
import org.deegree.ogcwebservices.wfs.capabilities.WFSCapabilitiesDocument;
import org.xml.sax.SAXException;

/**
 * <code>JAddWFS</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class AddWFSWizard extends AddServiceWizard<Container> {

    private static final long serialVersionUID = -1395997574648125628L;

    private static final ILogger LOG = LoggerFactory.getLogger( AddWFSWizard.class );

    private volatile Thread blinker;

    /**
     * @param frame
     * @param module
     * @param mapModel
     */
    public AddWFSWizard( JFrame frame, IModule<Container> module, MapModel mapModel ) {
        super( frame, module, mapModel );
        this.setTitle( Messages.getMessage( Locale.getDefault(), "$MD10119" ) );
    }

    @Override
    public void dispose() {
        blinker = null;
        super.dispose();
    }

    @Override
    protected String getEnterServiceLabel() {
        return Messages.getMessage( Locale.getDefault(), "$MD10123" );
    }

    @Override
    protected String getInfoText() {
        return Messages.getMessage( Locale.getDefault(), "$MD10120" );
    }

    @Override
    protected String getKnownServicesLabel() {
        return Messages.getMessage( Locale.getDefault(), "$MD10121" );
    }

    @Override
    protected String getVersionChooserLabel() {
        return Messages.getMessage( Locale.getDefault(), "$MD10124" );
    }

    @Override
    protected String getComboBoxDefaultLabel() {
        return Messages.getMessage( Locale.getDefault(), "$MD10122" );
    }

    @Override
    protected String getHighestVersionLabel() {
        return Messages.getMessage( Locale.getDefault(), "$MD10125" );
    }

    @Override
    protected String getKnownServicesInitParamKey() {
        return "knownWFS";
    }

    @Override
    protected String getVersionInitParamKey() {
        return "WFSversions";
    }

    @Override
    protected void handleNextButtonAction() {
        Runnable r = new Runnable() {

            public void run() {
                setCursor( CursorRegistry.WAIT_CURSOR );
                if ( AddWFSWizard.this.enterServicesField != null
                     && AddWFSWizard.this.enterServicesField.getText() != null
                     && AddWFSWizard.this.enterServicesField.getText().length() > 0 ) {
                    String wfsUrl = AddWFSWizard.this.enterServicesField.getText().trim();
                    String capabilitiesUrl = OWSUtils.validateHTTPGetBaseURL( wfsUrl );
                    capabilitiesUrl = capabilitiesUrl + "SERVICE=WFS&REQUEST=GetCapabilities";
                    ButtonModel bm = versionBG.getSelection();
                    if ( bm.getActionCommand() != null ) {
                        capabilitiesUrl = capabilitiesUrl + "&VERSION=" + bm.getActionCommand().trim();
                    }

                    // add authentication informations if available
                    String tmp = HttpUtils.normalizeURL( capabilitiesUrl );
                    capabilitiesUrl = HttpUtils.addAuthenticationForKVP( capabilitiesUrl, appContainer.getUser(),
                                                                         appContainer.getPassword(),
                                                                         appContainer.getCertificate( tmp ) );

                    LOG.logDebug( "send 'GetCapabilities'-request: " + capabilitiesUrl );

                    WFSCapabilitiesDocument wfsCapsDoc = new WFSCapabilitiesDocument();

                    try {
                        int timeout = appContainer.getSettings().getWFSFeatureAdapter().getTimeout();
                        InputStream is = HttpUtils.performHttpGet( capabilitiesUrl, null, timeout,
                                                                   appContainer.getUser(), appContainer.getPassword(),
                                                                   null ).getResponseBodyAsStream();
                        if ( blinker != Thread.currentThread() ) {
                            // just go on if thread has not been stopped (e.g. by pressing cancel button)
                            LOG.logInfo( "WFS capabilities loading thread has been canceled" );
                            return;
                        }

                        wfsCapsDoc.load( is, capabilitiesUrl );

                        if ( blinker != Thread.currentThread() ) {
                            // just go on if thread has not been stopped (e.g. by pressing cancel button)
                            LOG.logInfo( "WFS capabilities loading thread has been canceled" );
                            return;
                        }                     
                    } catch ( MalformedURLException e ) {
                        if ( AddWFSWizard.this.isVisible() ) {
                            LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10063", capabilitiesUrl ), e );
                            DialogFactory.openErrorDialog( appContainer.getViewPlatform(), AddWFSWizard.this,
                                                           e.getMessage(), getClass().getSimpleName(), e );
                        }
                        return;
                    } catch ( IOException e ) {
                        if ( AddWFSWizard.this.isVisible() ) {
                            LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10059", capabilitiesUrl ), e );
                            String errorTitle = Messages.getMessage( Locale.getDefault(), "$MD10126" );
                            String errorMsg = Messages.getMessage( Locale.getDefault(), "$MD10127", capabilitiesUrl );
                            DialogFactory.openErrorDialog( appContainer.getViewPlatform(), AddWFSWizard.this, errorMsg,
                                                           errorTitle );
                        }
                        return;
                    } catch ( SAXException e ) {
                        if ( AddWFSWizard.this.isVisible() ) {
                            LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10060", capabilitiesUrl ), e );
                            DialogFactory.openErrorDialog( appContainer.getViewPlatform(), AddWFSWizard.this,
                                                           e.getMessage(), getClass().getSimpleName() );
                        }
                        return;
                    } catch ( Exception e ) {
                        if ( AddWFSWizard.this.isVisible() ) {
                            LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10063", capabilitiesUrl ), e );
                            DialogFactory.openErrorDialog( appContainer.getViewPlatform(), AddWFSWizard.this,
                                                           e.getMessage(), getClass().getSimpleName() );
                        }
                        return;
                    } finally {
                        AddWFSWizard.this.dispose();
                    }

                    LOG.logDebug( "received WFS-Capabilities: ", wfsCapsDoc );

                    WFSCapabilities wfsCapabilities = null;
                    try {
                        wfsCapabilities = (WFSCapabilities) wfsCapsDoc.parseCapabilities();
                    } catch ( InvalidCapabilitiesException e ) {
                        LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10061", wfsUrl ), e );
                    }
                    if ( wfsCapabilities != null ) {

                        // insert new entry in the list of known services
                        insertKnownService( wfsCapabilities.getServiceIdentification().getTitle(),
                                            enterServicesField.getText() );

                        ApplicationContainer<Container> appContainer = module.getApplicationContainer();
                        AddWFSChooseFeature nextStep = new AddWFSChooseFeature( AddWFSWizard.this, mapModel,
                                                                                appContainer, wfsCapabilities );
                        nextStep.setLocation( getX(), getY() );
                        nextStep.setVisible( true );
                        setVisible( false );
                    }
                }
                setCursor( CursorRegistry.DEFAULT_CURSOR );
            }
        };
        blinker = new Thread( r );
        blinker.start();
    }
}
