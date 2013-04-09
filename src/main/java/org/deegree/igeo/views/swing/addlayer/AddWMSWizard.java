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
import java.util.List;
import java.util.ListIterator;

import javax.swing.ButtonModel;
import javax.swing.JFrame;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.HttpUtils;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.IModule;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.swing.CursorRegistry;
import org.deegree.ogcwebservices.OWSUtils;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilities;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocument;
import org.deegree.ogcwebservices.wms.capabilities.WMSCapabilitiesDocumentFactory;
import org.deegree.owscommon_new.DCP;
import org.deegree.owscommon_new.HTTP;
import org.deegree.owscommon_new.Operation;
import org.deegree.owscommon_new.OperationsMetadata;
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
public class AddWMSWizard extends AddServiceWizard<Container> {

    private static final long serialVersionUID = 8997560564158501902L;

    private static final ILogger LOG = LoggerFactory.getLogger( AddWMSWizard.class );

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
    public AddWMSWizard( JFrame frame, IModule<Container> module, MapModel mapModel ) {
        super( frame, module, mapModel );
        this.setTitle( get( "$MD10022" ) );
    }

    @Override
    public void dispose() {
        blinker = null;
        super.dispose();
    }

    @Override
    protected String getEnterServiceLabel() {
        return get( "$MD10026" );
    }

    @Override
    protected String getInfoText() {
        return get( "$MD10023" );
    }

    @Override
    protected String getKnownServicesLabel() {
        return get( "$MD10024" );
    }

    @Override
    protected String getVersionChooserLabel() {
        return get( "$MD10027" );
    }

    @Override
    protected String getHighestVersionLabel() {
        return get( "$MD10028" );
    }

    @Override
    protected String getComboBoxDefaultLabel() {
        return get( "$MD10025" );
    }

    @Override
    protected String getVersionInitParamKey() {
        return "WMSversions";
    }

    @Override
    protected String getKnownServicesInitParamKey() {
        return "knownWMS";
    }

    @Override
    protected void handleNextButtonAction() {
        Runnable r = new Runnable() {

            public void run() {
                setCursor( CursorRegistry.WAIT_CURSOR );

                if ( enterServicesField != null && enterServicesField.getText() != null
                     && enterServicesField.getText().length() > 0 ) {

                    String wmsUrl = enterServicesField.getText();
                    String capabilitiesUrl = OWSUtils.validateHTTPGetBaseURL( wmsUrl );
                    capabilitiesUrl = capabilitiesUrl + "SERVICE=WMS&REQUEST=GetCapabilities";
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

                    WMSCapabilitiesDocument wmsCapsDoc = null;
                    try {
                        int timeout = appContainer.getSettings().getWMSGridCoveragesAdapter().getTimeout();
                        InputStream is = HttpUtils.performHttpGet( capabilitiesUrl, null, timeout,
                                                                   appContainer.getUser(), appContainer.getPassword(),
                                                                   null ).getResponseBodyAsStream();
                        if ( blinker != Thread.currentThread() ) {
                            // just go on if thread has not been stopped (e.g. by pressing cancel button)
                            LOG.logInfo( "WMS capabilities loading thread has been canceled" );
                            return;
                        }
                        XMLFragment xml = new XMLFragment();
                        xml.load( is, capabilitiesUrl );
                        wmsCapsDoc = WMSCapabilitiesDocumentFactory.getWMSCapabilitiesDocument( xml.getRootElement() );
                        if ( blinker != Thread.currentThread() ) {
                            // just go on if thread has not been stopped (e.g. by pressing cancel button)
                            LOG.logInfo( "WMS capabilities loading thread has been canceled" );
                            return;
                        }
                    } catch ( MalformedURLException e ) {
                        LOG.logError( get( "$DG10058", capabilitiesUrl ), e );
                        DialogFactory.openErrorDialog( appContainer.getViewPlatform(), AddWMSWizard.this,
                                                       e.getMessage(), getClass().getSimpleName(), e );
                        return;
                    } catch ( IOException e ) {
                        LOG.logError( get( "$DG10059", capabilitiesUrl ), e );
                        String errorTitle = get( "$MD10053" );
                        String errorMsg = get( "$MD10054", capabilitiesUrl );
                        DialogFactory.openErrorDialog( appContainer.getViewPlatform(), AddWMSWizard.this, errorMsg,
                                                       errorTitle );
                        return;
                    } catch ( SAXException e ) {
                        LOG.logError( get( "$DG10060", capabilitiesUrl ), e );
                        DialogFactory.openErrorDialog( appContainer.getViewPlatform(), AddWMSWizard.this,
                                                       e.getMessage(), getClass().getSimpleName() );
                        return;
                    } catch ( Exception e ) {
                        LOG.logError( get( "$DG10058", capabilitiesUrl ), e );
                        DialogFactory.openErrorDialog( appContainer.getViewPlatform(), AddWMSWizard.this,
                                                       e.getMessage(), getClass().getSimpleName() );
                        return;
                    } finally {
                        AddWMSWizard.this.dispose();
                    }
                    LOG.logDebug( "received WMS-Capabilities: " + wmsCapsDoc.getAsPrettyString() );
                    WMSCapabilities wmsCapabilities = null;
                    try {
                        wmsCapabilities = (WMSCapabilities) wmsCapsDoc.parseCapabilities();

                        String pref = wmsUrl.contains( "?" ) ? wmsUrl.substring( 0, wmsUrl.indexOf( "?" ) - 1 )
                                                            : wmsUrl;
                        boolean alwaysUseBase = false;

                        OperationsMetadata md = wmsCapabilities.getOperationMetadata();
                        for ( Operation oper : md.getOperations() ) {
                            for ( DCP dcp : oper.getDCP() ) {
                                if ( dcp instanceof HTTP ) {
                                    HTTP http = (HTTP) dcp;
                                    List<URL> urls = http.getGetOnlineResources();
                                    ListIterator<URL> iter = urls.listIterator();
                                    while ( iter.hasNext() ) {
                                        URL url = iter.next();
                                        if ( !url.toString().startsWith( pref ) ) {
                                            if ( !alwaysUseBase ) {
                                                alwaysUseBase = !DialogFactory.openConfirmDialogYESNO( appContainer.getViewPlatform(),
                                                                                                       AddWMSWizard.this,
                                                                                                       get( "$DI10097",
                                                                                                            url.toString() ),
                                                                                                       get( "$DI10096" ) );
                                            }
                                            if ( alwaysUseBase ) {
                                                url = new URL( wmsUrl );
                                                iter.set( url );
                                            }
                                        }
                                    }
                                    http.setGetOnlineResources( urls );
                                }
                            }
                        }

                    } catch ( Exception e ) {
                        LOG.logError( get( "$DG10061", wmsUrl ), e );
                        DialogFactory.openErrorDialog( appContainer.getViewPlatform(), AddWMSWizard.this,
                                                       e.getMessage(), getClass().getSimpleName() );
                    }
                    if ( wmsCapabilities != null ) {
                        // insert new entry in the list of known services
                        insertKnownService( wmsCapabilities.getServiceIdentification().getTitle(),
                                            enterServicesField.getText() );

                        // open next dialog
                        ApplicationContainer<Container> appContainer = AddWMSWizard.this.module.getApplicationContainer();
                        final AddWMSWizardChooseLayer nextStep = new AddWMSWizardChooseLayer(
                                                                                              AddWMSWizard.this,
                                                                                              AddWMSWizard.this.mapModel,
                                                                                              appContainer,
                                                                                              wmsCapabilities,
                                                                                              cbSwapAxis.isSelected() );
                        addToFrontListener( nextStep );
                        nextStep.setLocation( AddWMSWizard.this.getX(), AddWMSWizard.this.getY() );
                        nextStep.setVisible( true );
                        AddWMSWizard.this.setVisible( false );
                    }
                }
            }
        };
        blinker = new Thread( r );
        blinker.start();
    }
}
