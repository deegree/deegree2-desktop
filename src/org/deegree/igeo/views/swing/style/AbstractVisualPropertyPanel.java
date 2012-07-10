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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.ImageUtils;
import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.style.perform.StyleChangedEvent;
import org.deegree.igeo.style.perform.StyleChangedListener;
import org.deegree.igeo.style.perform.VisualPropertyPerformer;
import org.deegree.igeo.views.swing.style.component.MainPanel;
import org.deegree.igeo.views.swing.style.component.StyleAttributePanel;
import org.deegree.igeo.views.swing.style.component.classification.AbstractClassificationPanel;

/**
 * <code>AbstractVisualPropertyPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public abstract class AbstractVisualPropertyPanel extends JPanel implements VisualPropertyPanel, ActionListener {

    private static final long serialVersionUID = 913164314294448079L;

    private static final ILogger LOG = LoggerFactory.getLogger( AbstractVisualPropertyPanel.class );

    protected StyleDialog owner;

    protected AbstractClassificationPanel classificationPanel;

    protected PreviewPanel previewPanel;

    protected VisualPropertyPerformer performer;

    protected JTabbedPane styleAttributeContainer;

    protected MainPanel mainPanel;

    private List<StyleChangedListener> styleChangedListener = new ArrayList<StyleChangedListener>();

    /**
     * sets the variables and forces an update of the preview panel
     * 
     * @param owner
     * @param performer
     */
    public AbstractVisualPropertyPanel( StyleDialog owner, VisualPropertyPerformer performer ) {
        this.owner = owner;
        this.performer = performer;
        initComponents();
        updatePreview();
    }

    private void initComponents() {
        previewPanel = new PreviewPanel( this );
        styleAttributeContainer = new JTabbedPane();
        styleAttributeContainer.setTabPlacement( JTabbedPane.LEFT );
        styleAttributeContainer.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( styleAttributeContainer.getSelectedComponent() == classificationPanel ) {
                    previewPanel.setVisible( false );
                } else {
                    previewPanel.setVisible( true );
                }
            }
        } );
    }

    public List<Rule> getRules() {
        if ( isActive() ) {
            return performer.getRules( owner.isDefaultUnitPixel() );
        }
        return new ArrayList<Rule>();

    }

    public Symbolizer getPresetSymbolizer() {
        return performer.getSymbolizer( true );
    }

    public void update( StyleChangedEvent changeEvent ) {
        performer.update( changeEvent );
        informListener( changeEvent );
        updatePreview();
    }

    public StyleDialog getOwner() {
        return owner;
    }

    public void addStyleChangedListener( StyleChangedListener styleAttributeChangedListener ) {
        styleChangedListener.add( styleAttributeChangedListener );
    }

    public void removeStyleChangedListener( StyleChangedListener styleAttributeChangedListener ) {
        styleChangedListener.remove( styleAttributeChangedListener );
    }

    public void setActive( boolean active ) {
        mainPanel.setActive( active );
        getOwner().setTypePanelIcon( this, active );
    }

    public boolean isActive() {
        return mainPanel.isActive();
    }

    public void actionPerformed( ActionEvent e ) {
        updatePreview();
    }

    private void updatePreview() {
        BufferedImage img = null;
        if ( previewPanel.isShowPreviewSelected() ) {
            img = performer.getAsImage();
        }
        previewPanel.update( img );
    }

    private void informListener( StyleChangedEvent changeEvent ) {
        for ( StyleChangedListener listener : styleChangedListener ) {
            listener.stylePanelChanged( changeEvent );
        }
    }

    public void setTabsEnabled( boolean enabled, ComponentType... componentType ) {
        for ( int i = 0; i < styleAttributeContainer.getComponentCount(); i++ ) {
            Component comp = styleAttributeContainer.getComponent( i );
            if ( comp instanceof StyleAttributePanel ) {
                for ( int j = 0; j < componentType.length; j++ ) {
                    if ( componentType[j].equals( ( (StyleAttributePanel) comp ).getComponentType() ) ) {
                        styleAttributeContainer.setEnabledAt( i, enabled );
                    }
                }
            }
        }
    }

    protected ImageIcon getAsImageIcon( String file ) {
        ImageIcon icon = null;
        if ( file != null && file.length() > 0 ) {
            try {
                URL url = getClass().getResource( file );
                if ( url != null ) {
                    icon = new ImageIcon( ImageUtils.loadImage( url.openStream() ) );
                }
            } catch ( Exception e ) {
                LOG.logDebug( "could not create image icon from file " + file, e.getMessage() );
            }
        }
        return icon;
    }

    public Object getValue( ComponentType componentType, Object defaultValue ) {
        for ( int i = 0; i < styleAttributeContainer.getComponentCount(); i++ ) {
            Component comp = styleAttributeContainer.getComponent( i );
            if ( comp instanceof StyleAttributePanel ) {
                StyleAttributePanel sta = ( (StyleAttributePanel) comp );
                if ( sta.getComponentType() != null && componentType.equals( sta.getComponentType() ) ) {
                    return ( (StyleAttributePanel) comp ).getValue();
                }
            }
        }
        return defaultValue;
    }

    /**
     * Register a listener to be informed when the global setting for uom changed.
     * 
     * @param listener listener to add
     */
    public void addUomChangedListener( org.deegree.igeo.ChangeListener listener ) {
        owner.addUomChangedListener( listener );
    }

}
