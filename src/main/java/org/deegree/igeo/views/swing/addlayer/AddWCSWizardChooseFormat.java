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

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.deegree.datatypes.CodeList;
import org.deegree.datatypes.time.TimePosition;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.HttpUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.utils.SwingUtils;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.model.AddWCSLayerCommand;
import org.deegree.igeo.dataadapter.DataAccessException;
import org.deegree.igeo.dataadapter.WCSCapabilitiesEvaluator;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.settings.WCSGridCoverageAdapterSettings;
import org.deegree.igeo.views.swing.DateTimeDialog;
import org.deegree.igeo.views.swing.util.GuiUtils;
import org.deegree.ogcwebservices.wcs.CoverageOfferingBrief;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageDescriptionDocument;
import org.deegree.ogcwebservices.wcs.describecoverage.CoverageOffering;
import org.deegree.ogcwebservices.wcs.getcapabilities.WCSCapabilities;
import org.xml.sax.SAXException;

/**
 * <code>JAddWMSWizardChooseCRS</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class AddWCSWizardChooseFormat extends WizardDialog implements ActionListener {

    private static final long serialVersionUID = 7746816571048832101L;

    private static final ILogger LOG = LoggerFactory.getLogger( AddWCSWizardChooseFormat.class );

    private MapModel mapModel;

    private WCSCapabilities wcsCaps;

    private URL capabilitiesURL;

    private List<CoverageOfferingBrief> selectedCoverages;

    private List<CoverageOffering> coveragesOfferings;

    private JComboBox formatChooser;

    private String timestamp;

    /**
     * 
     * @param frame
     *            the previous dialog
     * @param mapModel
     *            the mapModel to add the new layer
     * @param appContainer
     *            the application container
     * @param wcsCapabilities
     *            the capabailities of the requested wcs
     * @param selectedCoverages
     *            the selected coverages
     * @param capabilitiesURL
     *            the capabailities url of the wcs
     */
    public AddWCSWizardChooseFormat( JFrame frame, MapModel mapModel, ApplicationContainer<Container> appContainer,
                                     WCSCapabilities wcsCapabilities, List<CoverageOfferingBrief> selectedCoverages,
                                     URL capabilitiesURL ) {
        super( frame );
        this.mapModel = mapModel;
        this.appContainer = appContainer;
        this.wcsCaps = wcsCapabilities;
        this.capabilitiesURL = capabilitiesURL;
        this.selectedCoverages = selectedCoverages;
        this.coveragesOfferings = new ArrayList<CoverageOffering>( this.selectedCoverages.size() );

        this.setSize( 500, 600 );
        this.setResizable( false );
        this.setTitle( Messages.getMessage( Locale.getDefault(), "$MD11381" ) );
        infoPanel.setInfoText( Messages.getMessage( Locale.getDefault(), "$MD11382" ) );

        buttonPanel.registerActionListener( this );
        super.init();

    }

    // /////////////////////////////////////////////////////////////////////////////////
    // WizardDialog
    // /////////////////////////////////////////////////////////////////////////////////

    @Override
    public JPanel getMainPanel() {
        JPanel chooseFormatPanel = new JPanel();
        GridBagConstraints gbc = SwingUtils.initPanel( chooseFormatPanel );

        JLabel formatChooserLabel = new JLabel( Messages.getMessage( Locale.getDefault(), "$MD11383" ) );

        formatChooser = new JComboBox();
        formatChooser.setVisible( true );
        // fill combo box with available formats
        readCoverageOfferings();

        // at the moment handling of just one coverage is supported
        CoverageOffering co = coveragesOfferings.get( 0 );
        CodeList[] codeLists = co.getSupportedFormats().getFormats();
        for ( CodeList codeList : codeLists ) {
            formatChooser.addItem( codeList.getCodes()[0] );
        }

        gbc.anchor = GridBagConstraints.LINE_START;
        chooseFormatPanel.add( formatChooserLabel, gbc );
        ++gbc.gridy;
        chooseFormatPanel.add( formatChooser, gbc );
        gbc.insets = new Insets( 10, 2, 2, 2 );

        TimePosition[] tp = selectedCoverages.get( 0 ).getLonLatEnvelope().getTimePositions();

        ++gbc.gridy;
        JLabel lb = new JLabel( "Datum auswählen" );
        chooseFormatPanel.add( lb, gbc );
        gbc.insets = new Insets( 2, 2, 2, 2 );
        ++gbc.gridy;
        JButton b = new JButton( "Kalender öffnen" );
        b.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                DateTimeDialog dtd = new DateTimeDialog();
                timestamp = dtd.getIsoFormattedTimestamp();
            }
        } );
        chooseFormatPanel.add( b, gbc );
        gbc.insets = new Insets( 10, 2, 2, 2 );
        if ( tp == null || tp.length != 2 ) {
            lb.setEnabled( false );
            b.setEnabled( false );
        }

        return chooseFormatPanel;
    }

    /**
     * coverage offerings are required to get lists of supported formats
     */
    private void readCoverageOfferings() {
        for ( CoverageOfferingBrief cob : selectedCoverages ) {
            CoverageDescriptionDocument doc = new CoverageDescriptionDocument();
            String descURL = null;
            try {
                ApplicationContainer<?> appCont = mapModel.getApplicationContainer();
                WCSGridCoverageAdapterSettings wcsSet = appCont.getSettings().getWCSGridCoveragesAdapter();
                descURL = readDescribeCoverageURL().toURI().toASCIIString();
                String tmp = HttpUtils.normalizeURL( descURL );
                descURL = HttpUtils.addAuthenticationForKVP( descURL, appCont.getUser(), appCont.getPassword(),
                                                             appCont.getCertificate( tmp ) );
                String req = "VERSION=" + wcsCaps.getVersion() + "&SERVICE=WCS&COVERAGE=" + cob.getName()
                             + "&request=DescribeCoverage";
                InputStream is = HttpUtils.performHttpGet( descURL, req, wcsSet.getTimeout(), appCont.getUser(),
                                                           appCont.getPassword(), null ).getResponseBodyAsStream();
                doc.load( is, descURL );
                CoverageOffering co = doc.getCoverageOfferings()[0];
                coveragesOfferings.add( co );
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                String s = StringTools.stackTraceToString( e );
                throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10113", descURL ) + s );
            }
        }

    }

    private URL readDescribeCoverageURL() {

        XMLFragment xml;
        String capabilitiesUrl = null;
        ApplicationContainer<?> appCont = mapModel.getApplicationContainer();
        WCSGridCoverageAdapterSettings wcsSet = appCont.getSettings().getWCSGridCoveragesAdapter();
        try {
            capabilitiesUrl = this.capabilitiesURL.toURI().toASCIIString();
            String tmp = HttpUtils.normalizeURL( capabilitiesUrl );
            capabilitiesUrl = HttpUtils.addAuthenticationForKVP( capabilitiesUrl, appCont.getUser(),
                                                                 appCont.getPassword(), appCont.getCertificate( tmp ) );
            InputStream is = HttpUtils.performHttpGet( capabilitiesUrl, null, wcsSet.getTimeout(), appCont.getUser(),
                                                       appCont.getPassword(), null ).getResponseBodyAsStream();
            xml = new XMLFragment();
            xml.load( is, capabilitiesUrl );

        } catch ( SAXException e ) {
            LOG.logError( e.getMessage(), e );
            String s = StringTools.stackTraceToString( e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10106", capabilitiesUrl ) + s );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            String s = StringTools.stackTraceToString( e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$$DG10107", capabilitiesUrl ) + s );
        }
        String version = null;
        try {
            version = XMLTools.getRequiredAttrValue( "version", null, xml.getRootElement() );
        } catch ( XMLParsingException e ) {
            LOG.logError( e.getMessage(), e );
            LOG.logError( xml.getAsPrettyString() );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10108", capabilitiesUrl,
                                                                xml.getAsPrettyString() ) );
        }

        String className = wcsSet.getCapabilitiesEvaluator( version );
        Class<?> clzz = null;
        try {
            clzz = Class.forName( className );
        } catch ( ClassNotFoundException e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10109", className ) );
        }

        WCSCapabilitiesEvaluator evaluator = null;
        try {
            evaluator = (WCSCapabilitiesEvaluator) clzz.newInstance();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( Messages.getMessage( Locale.getDefault(), "$DG10110", className ) );
        }
        evaluator.setCapabilities( xml );
        try {
            return evaluator.getDescribeCoverageHTTPGetURL();
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            throw new DataAccessException( e.getMessage(), e );
        }

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
                AddWCSWizardSummary nextStep = new AddWCSWizardSummary( this, this.mapModel, this.appContainer,
                                                                        wcsCaps, capabilitiesURL, coveragesOfferings,
                                                                        (String) formatChooser.getSelectedItem(),
                                                                        timestamp );

                GuiUtils.addToFrontListener( nextStep );
                nextStep.setLocation( this.getX(), this.getY() );
                nextStep.setVisible( true );
                this.setVisible( false );
            } else if ( srcButton.getName().equals( ButtonPanel.FINISH_BT ) ) {
                for ( CoverageOffering coverage : coveragesOfferings ) {
                    AddWCSLayerCommand addWCSCmd = new AddWCSLayerCommand( this.mapModel, this.capabilitiesURL,
                                                                           this.wcsCaps, coverage,
                                                                           (String) formatChooser.getSelectedItem(),
                                                                           timestamp );
                    appContainer.getCommandProcessor().executeASychronously( addWCSCmd );
                }
                this.dispose();
            }
        }
    }

}
