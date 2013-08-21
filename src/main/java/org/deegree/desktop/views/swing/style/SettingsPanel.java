//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2010 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.desktop.views.swing.style;

import static org.deegree.desktop.i18n.Messages.get;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.deegree.desktop.mapmodel.DefinedStyle;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * GUI containing the settings.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class SettingsPanel extends JPanel {

    private static final long serialVersionUID = 774691088061902237L;

    private JRadioButton unitsRbPixel;

    private JRadioButton unitsRbMap;

    public SettingsPanel() {
        FormLayout fl = new FormLayout( "left:pref", "$rbheight, $rbheight" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        builder.setBorder( BorderFactory.createTitledBorder( get( "$MD11587" ) ) );

        CellConstraints cc = new CellConstraints();
        cc.insets = new Insets( 2, 10, 2, 2 );

        ButtonGroup bg = new ButtonGroup();

        unitsRbPixel = new JRadioButton( get( "$MD11061" ) );
        bg.add( unitsRbPixel );
        builder.add( unitsRbPixel, cc.xy( 1, 1 ) );

        unitsRbMap = new JRadioButton( get( "$MD11062" ) );
        bg.add( unitsRbMap );
        builder.add( unitsRbMap, cc.xy( 1, 2 ) );

        JPanel panel = builder.getPanel();

        Dimension dim = new Dimension( 125, 75 );
        panel.setPreferredSize( dim );
        add( panel );
    }

    /**
     * @param uom
     *            the uom
     */
    public void setUom( String uom ) {
        if ( DefinedStyle.UOM_MAP.equalsIgnoreCase( uom ) ) {
            unitsRbMap.setSelected( true );
        } else {
            unitsRbPixel.setSelected( true );
        }
    }

    /**
     * @return the uom
     */
    public String getUom() {
        if ( unitsRbMap.isSelected() ) {
            return DefinedStyle.UOM_MAP;
        }
        return DefinedStyle.UOM_PIXEL;
    }

}
