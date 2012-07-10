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

package org.deegree.igeo.views.swing.style.component;

import static org.deegree.igeo.i18n.Messages.get;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.deegree.datatypes.QualifiedName;
import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.style.perform.StyleChangedEvent;
import org.deegree.igeo.views.swing.addlayer.QualifiedNameRenderer;
import org.deegree.igeo.views.swing.style.VisualPropertyPanel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>LabelChoosePanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class LabelChoosePanel extends MainPanel {

    private static final long serialVersionUID = 5396378082913014491L;

    private ComponentType componentType;

    private JComboBox labelCB;

    private JCheckBox halosActiveCheckB;

    private JCheckBox autoPlacementCheckB;

    public LabelChoosePanel( VisualPropertyPanel assignedVisualPropPanel, ComponentType componentType ) {
        super( assignedVisualPropPanel );
        this.componentType = componentType;
        init();
    }

    private void init() {
        this.isActiveCB = new JCheckBox( get( "$MD10732" ) );
        this.isActiveCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                assignedVisualPropPanel.getOwner().setTypePanelIcon( assignedVisualPropPanel, isActiveCB.isSelected() );
            }
        } );

        this.halosActiveCheckB = new JCheckBox( get( "$MD10733" ) );
        this.halosActiveCheckB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                assignedVisualPropPanel.update( new StyleChangedEvent( halosActiveCheckB.isSelected(),
                                                                       ComponentType.LABEL_HALO ) );
            }
        } );

        this.autoPlacementCheckB = new JCheckBox( get( "$MD10828" ) );
        this.autoPlacementCheckB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( autoPlacementCheckB.isSelected() ) {
                    assignedVisualPropPanel.setTabsEnabled( false, ComponentType.ANCHOR, ComponentType.DISPLACEMENT,
                                                            ComponentType.ROTATION );
                } else {
                    assignedVisualPropPanel.setTabsEnabled( true, ComponentType.ANCHOR, ComponentType.DISPLACEMENT,
                                                            ComponentType.ROTATION );
                }
                assignedVisualPropPanel.update( new StyleChangedEvent( halosActiveCheckB.isSelected(),
                                                                       ComponentType.LABEL_PLACEMENT ) );
            }
        } );

        this.labelCB = new JComboBox();
        this.labelCB.setRenderer( new QualifiedNameRenderer() );
        this.labelCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                assignedVisualPropPanel.update( new StyleChangedEvent( labelCB.getSelectedItem(), componentType ) );
            }
        } );
        List<QualifiedName> properties = this.assignedVisualPropPanel.getOwner().getPropertyNames();
        for ( QualifiedName value : properties ) {
            this.labelCB.addItem( value );
        }

        FormLayout fl = new FormLayout( "$rgap, pref:grow(1.0)",
                                        "$btheight, $btheight, $btheight, $sepheight, $btheight" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        builder.setBorder( BorderFactory.createEmptyBorder( 20, 5, 5, 5 ) );
        CellConstraints cc = new CellConstraints();

        builder.addSeparator( get( "$MD10734" ), cc.xyw( 1, 4, 2 ) );
        builder.add( this.isActiveCB, cc.xy( 2, 1 ) );
        builder.add( this.halosActiveCheckB, cc.xy( 2, 2 ) );
        builder.add( this.autoPlacementCheckB, cc.xy( 2, 3 ) );
        builder.add( labelCB, cc.xy( 2, 5 ) );

        this.setLayout( new BorderLayout() );
        this.add( builder.getPanel(), BorderLayout.CENTER );
    }

    /**
     * @return true, if the labels should be placed automatically
     */
    public boolean isAutoPlacementActive() {
        return autoPlacementCheckB.isSelected();
    }

    /**
     * selects the entry with the given label
     * 
     * @param label
     *            the label to set
     */
    public void setLabel( String label ) {
        for ( int i = 0; i < this.labelCB.getItemCount(); i++ ) {
            QualifiedName pv = (QualifiedName) this.labelCB.getItemAt( i );
            if ( pv.getPrefixedName().equals( label ) ) {
                this.labelCB.setSelectedIndex( i );
                break;
            }
        }
    }

    /**
     * Sets the status of the halo and triggers updating of the assigned visual property panel
     * 
     * @param haloActive
     *            status to set the halo active check box
     */
    public void setHalo( boolean haloActive ) {
        this.halosActiveCheckB.setSelected( haloActive );
        assignedVisualPropPanel.update( new StyleChangedEvent( halosActiveCheckB.isSelected(), ComponentType.LABEL_HALO ) );
    }

    /**
     * Sets the status of the auto placement
     * 
     * @param autoPlacement
     *            status to set the auto placement check box
     */
    public void setAutoPlacement( boolean autoPlacement ) {
        this.autoPlacementCheckB.setSelected( autoPlacement );
        assignedVisualPropPanel.update( new StyleChangedEvent( autoPlacementCheckB.isSelected(),
                                                               ComponentType.LABEL_PLACEMENT ) );
    }

    /**
     * @return the selected label as QualifiedName
     */
    public QualifiedName getLabel() {
        return (QualifiedName) labelCB.getSelectedItem();
    }

    /**
     * @return true if the halo is activated
     */
    public boolean isHaloActive() {
        return halosActiveCheckB.isSelected();
    }

    /**
     * @return true if auto placement is enabled
     */
    public boolean isAutoPlacement() {
        return autoPlacementCheckB.isSelected();
    }

}
