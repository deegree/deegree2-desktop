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

package org.deegree.igeo.views.swing.style;

import static org.deegree.igeo.i18n.Messages.get;

import java.awt.BorderLayout;
import java.util.List;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.sld.RasterSymbolizer;
import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.igeo.style.model.Preset.PRESETTYPE;
import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.style.perform.RasterVisualPropertyPerformer;
import org.deegree.igeo.views.swing.style.component.MainInformationPanel;
import org.deegree.igeo.views.swing.style.component.PresetsPanel;
import org.deegree.igeo.views.swing.style.component.TransparencyPanel;
import org.deegree.igeo.views.swing.style.component.raster.ContrastEnhancementPanel;
import org.deegree.model.feature.schema.FeatureType;
import org.deegree.model.filterencoding.FilterEvaluationException;

/**
 * <code>RasterVisualPropertyPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class RasterVisualPropertyPanel extends AbstractVisualPropertyPanel {

    private static final long serialVersionUID = -7072224840119551608L;

    private static final ILogger LOG = LoggerFactory.getLogger( RasterVisualPropertyPanel.class );

    private TransparencyPanel transparencyPanel;

    private ContrastEnhancementPanel contrastEnhancementPanelPanel;

    private PresetsPanel presetsPanel;

    public RasterVisualPropertyPanel( StyleDialog owner ) {
        super( owner, new RasterVisualPropertyPerformer() );
        init();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.view.VisualPropertyPanel#setValues(java.util.List)
     */
    public void setRules( List<Rule> rules, FeatureType featureType )
                            throws FilterEvaluationException {
        if ( rules.size() > 0 ) {
            if ( rules.size() > 1 ) {
                LOG.logInfo( "dynamic values are not supported for rasters: read only the first rule!" );
            }
            Symbolizer[] symbolizers = rules.get( 0 ).getSymbolizers();
            if ( symbolizers.length > 0 ) {
                if ( symbolizers.length > 1 ) {
                    LOG.logInfo( "there are more than one symbolizers defined, only the first will be interpreted, if it is an RasterSymbolizer!!" );
                }
                setSymbolizer( symbolizers[0] );
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.view.VisualPropertyPanel#setSymbolizer(org.deegree.graphics
     * .sld.Symbolizer)
     */
    public void setSymbolizer( Symbolizer symbolizer )
                            throws FilterEvaluationException {
        if ( symbolizer instanceof RasterSymbolizer ) {
            RasterSymbolizer rs = ( (RasterSymbolizer) symbolizer );
            transparencyPanel.setValue( rs.getOpacity() );
            contrastEnhancementPanelPanel.setValue( new Double( rs.getGamma() ).intValue() );
            setActive( true );
        } else {
            LOG.logInfo( "symbolizer is not an RasterSymbolizer, so style cannot be set " );
        }
    }

    private void init() {
        mainPanel = new MainInformationPanel( this, get( "$MD10854" ), get( "$MD10855" ) );

        transparencyPanel = new TransparencyPanel( this, ComponentType.OPACITY, get( "$MD10961" ),
                                                   getAsImageIcon( get( "$MD10962" ) ) );
        contrastEnhancementPanelPanel = new ContrastEnhancementPanel( this, ComponentType.CONTRASTENHANCEMENT,
                                                                      get( "$MD10963" ),
                                                                      getAsImageIcon( get( "$MD10964" ) ) );
        presetsPanel = new PresetsPanel( this, PRESETTYPE.RASTER );

        styleAttributeContainer.addTab( get( "$MD10856" ), mainPanel );
        styleAttributeContainer.addTab( get( "$MD10852" ), transparencyPanel );
        styleAttributeContainer.addTab( get( "$MD10853" ), contrastEnhancementPanelPanel );
        styleAttributeContainer.addTab( get( "$MD10938" ), presetsPanel );
        setLayout( new BorderLayout() );
        add( previewPanel, BorderLayout.EAST );
        add( styleAttributeContainer, BorderLayout.CENTER );
    }

}
