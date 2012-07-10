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

package org.deegree.igeo.views.swing.addlayer;

import static org.deegree.igeo.i18n.Messages.get;
import static org.deegree.igeo.views.swing.util.GuiUtils.addToFrontListener;

import java.awt.Container;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ButtonModel;
import javax.swing.JFrame;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.HttpUtils;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.swing.CursorRegistry;
import org.deegree.ogcwebservices.OWSUtils;
import org.deegree.ogcwebservices.getcapabilities.InvalidCapabilitiesException;
import org.deegree.ogcwebservices.wcs.getcapabilities.WCSCapabilities;
import org.deegree.ogcwebservices.wcs.getcapabilities.WCSCapabilitiesDocument;
import org.xml.sax.SAXException;

/**
 * <code>JAddWMSWizard</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class AddWCSWizard extends AddServiceWizard<Container> {

    private static final long serialVersionUID = 8997560564158501902L;

    private static final ILogger LOG = LoggerFactory.getLogger( AddWCSWizard.class );

    private volatile Thread blinker;

    /**
     * 
     * @param frame
     *            the previous dialog
     * @param module
     *            the module
     * @param mapModel
     *            the mapModelAdapter to add the new layer
     */
    public AddWCSWizard( JFrame frame, IModule<Container> module, MapModel mapModel ) {
        super( frame, module, mapModel );
        this.setTitle( get( "$MD11368" ) );
    }

    @Override
    public void dispose() {
        blinker = null;
        super.dispose();
    }

    @Override
    protected String getEnterServiceLabel() {
        return get( "$MD11369" );
    }

    @Override
    protected String getInfoText() {
        return get( "$MD11370" );
    }

    @Override
    protected String getKnownServicesLabel() {
        return get( "$MD11371" );
    }

    @Override
    protected String getVersionChooserLabel() {
        return get( "$MD11372" );
    }

    @Override
    protected String getHighestVersionLabel() {
        return get( "$MD11373" );
    }

    @Override
    protected String getComboBoxDefaultLabel() {
        return get( "$MD11374" );
    }

    @Override
    protected String getVersionInitParamKey() {
        return "WCSversions";
    }

    @Override
    protected String getKnownServicesInitParamKey() {
        return "knownWCS";
    }

    @Override
    protected void handleNextButtonAction() {
        Runnable r = new Runnable() {

            public void run() {
                setCursor( CursorRegistry.WAIT_CURSOR );

                if ( enterServicesField != null && enterServicesField.getText() != null
                     && enterServicesField.getText().length() > 0 ) {

                    String wcsUrl = enterServicesField.getText();
                    String capabilitiesUrl = OWSUtils.validateHTTPGetBaseURL( wcsUrl );
                    String baseWCSUrl = OWSUtils.validateHTTPGetBaseURL( wcsUrl );
                    if ( baseWCSUrl.indexOf( '?' ) > -1 ) {
                        baseWCSUrl = baseWCSUrl.substring( 0, baseWCSUrl.indexOf( '?' ) );
                    }
                    capabilitiesUrl = capabilitiesUrl + "SERVICE=WCS&REQUEST=GetCapabilities";
                    // add authentication informations if available
                    String tmp = HttpUtils.normalizeURL( capabilitiesUrl );
                    capabilitiesUrl = HttpUtils.addAuthenticationForKVP( capabilitiesUrl, appContainer.getUser(),
                                                                         appContainer.getPassword(),
                                                                         appContainer.getCertificate( tmp ) );
                    ButtonModel bm = versionBG.getSelection();
                    if ( bm.getActionCommand() != null ) {
                        capabilitiesUrl = capabilitiesUrl + "&VERSION=" + bm.getActionCommand().trim();
                    }
                    LOG.logDebug( "send 'GetCapabilities'-request: " + capabilitiesUrl );

                    WCSCapabilitiesDocument wcsCapsDoc = new WCSCapabilitiesDocument();
                    URL capsUrl = null;
                    try {
                        int timeout = appContainer.getSettings().getWCSGridCoveragesAdapter().getTimeout();
                        InputStream is = HttpUtils.performHttpGet( capabilitiesUrl, null, timeout,
                                                                   appContainer.getUser(), appContainer.getPassword(),
                                                                   null ).getResponseBodyAsStream();
                        if ( blinker != Thread.currentThread() ) {
                            // just go on if thread has not been stopped (e.g. by pressing cancel button)
                            LOG.logInfo( "WCS capabilities loading thread has been canceled" );
                            return;
                        }
                        wcsCapsDoc.load( is, capabilitiesUrl );
                        if ( blinker != Thread.currentThread() ) {
                            // just go on if thread has not been stopped (e.g. by pressing cancel button)
                            LOG.logInfo( "WCS capabilities loading thread has been canceled" );
                            return;
                        }
                        // must be created again without authentication information to be passed to add
                        // layer command
                        wcsUrl = OWSUtils.validateHTTPGetBaseURL( wcsUrl );
                        wcsUrl = wcsUrl + "SERVICE=WCS&REQUEST=GetCapabilities";
                        if ( bm.getActionCommand() != null ) {
                            wcsUrl = wcsUrl + "&VERSION=" + bm.getActionCommand().trim();
                        }
                        capsUrl = new URL( wcsUrl );
                    } catch ( MalformedURLException e ) {
                        LOG.logError( get( "$DG10114", capabilitiesUrl ), e );
                        DialogFactory.openErrorDialog( appContainer.getViewPlatform(), AddWCSWizard.this,
                                                       e.getMessage(), getClass().getSimpleName(), e );
                        return;
                    } catch ( IOException e ) {
                        LOG.logError( get( "$DG10115", capabilitiesUrl ), e );
                        String errorTitle = get( "$MD11375" );
                        String errorMsg = get( "$MD11376", capabilitiesUrl );
                        DialogFactory.openErrorDialog( appContainer.getViewPlatform(), AddWCSWizard.this, errorMsg,
                                                       errorTitle );
                        return;
                    } catch ( SAXException e ) {
                        LOG.logError( get( "$DG10116", capabilitiesUrl ), e );
                        DialogFactory.openErrorDialog( appContainer.getViewPlatform(), AddWCSWizard.this,
                                                       e.getMessage(), getClass().getSimpleName() );
                        return;
                    } catch ( Exception e ) {
                        LOG.logError( get( "$DG10117", capabilitiesUrl ), e );
                        DialogFactory.openErrorDialog( appContainer.getViewPlatform(), AddWCSWizard.this,
                                                       e.getMessage(), getClass().getSimpleName() );
                        return;
                    } finally {
                        AddWCSWizard.this.dispose();
                    }
                    LOG.logDebug( "received WCS-Capabilities: " + wcsCapsDoc.getAsPrettyString() );
                    WCSCapabilities wcsCapabilities = null;
                    try {
                        wcsCapabilities = (WCSCapabilities) wcsCapsDoc.parseCapabilities();
                    } catch ( InvalidCapabilitiesException e ) {
                        LOG.logError( get( "$DG10118", wcsUrl ), e );
                        DialogFactory.openErrorDialog( appContainer.getViewPlatform(), AddWCSWizard.this,
                                                       e.getMessage(), getClass().getSimpleName() );
                    }
                    if ( wcsCapabilities != null ) {
                        // insert new entry in the list of known services
                        insertKnownService( wcsCapabilities.getService().getLabel(), enterServicesField.getText() );

                        // open next dialog
                        ApplicationContainer<Container> appContainer = AddWCSWizard.this.module.getApplicationContainer();
                        final AddWCSWizardChooseCoverage nextStep = new AddWCSWizardChooseCoverage(
                                                                                                    AddWCSWizard.this,
                                                                                                    AddWCSWizard.this.mapModel,
                                                                                                    appContainer,
                                                                                                    wcsCapabilities,
                                                                                                    capsUrl );
                        addToFrontListener( nextStep );
                        nextStep.setLocation( AddWCSWizard.this.getX(), AddWCSWizard.this.getY() );
                        nextStep.setVisible( true );
                        AddWCSWizard.this.setVisible( false );
                    }
                }
                setCursor( CursorRegistry.DEFAULT_CURSOR );
            }
        };
        blinker = new Thread( r );
        blinker.start();

    }
}
