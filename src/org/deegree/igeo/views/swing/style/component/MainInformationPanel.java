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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;

import org.deegree.igeo.views.swing.style.VisualPropertyPanel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>MainInformationPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class MainInformationPanel extends MainPanel {

    private static final long serialVersionUID = -5905405161398967405L;

    public MainInformationPanel( VisualPropertyPanel assignedVisualPropPanel, String checkBoxTitle, String helpText ) {
        super( assignedVisualPropPanel );
        init( checkBoxTitle, helpText );
    }

    private void init( String checkBoxTitle, String helpText ) {
        this.isActiveCB = new JCheckBox( checkBoxTitle );
        this.isActiveCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                assignedVisualPropPanel.getOwner().setTypePanelIcon( assignedVisualPropPanel, isActiveCB.isSelected() );
            }
        } );

        JTextArea someText = new JTextArea( helpText );
        someText.setEditable( false );
        someText.setLineWrap( true );
        someText.setWrapStyleWord( true );
        someText.setPreferredSize( new Dimension( 350, 300 ) );

        FormLayout fl = new FormLayout( "left:pref:grow(1.0)", "top:20dlu, 20dlu, center:50dlu:grow(1.0)" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        builder.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );

        CellConstraints cc = new CellConstraints();
        builder.add( this.isActiveCB, cc.xy( 1, 1 ) );
        builder.add( someText, cc.xy( 1, 3, CellConstraints.CENTER, CellConstraints.CENTER ) );
        this.setLayout( new BorderLayout() );
        this.setPreferredSize( new Dimension( 350, 222 ) );
        this.add( builder.getPanel(), BorderLayout.CENTER );

    }

}
