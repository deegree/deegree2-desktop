//$HeadURL: svn+ssh://aschmitz@wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2011 by:
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
package org.deegree.igeo.views.swing.georef;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.NONE;
import static javax.swing.BorderFactory.createTitledBorder;
import static org.deegree.igeo.i18n.Messages.get;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;

import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.modules.georef.ControlPointModel;

/**
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author: stranger $
 * 
 * @version $Revision: $, $Date: $
 */
public class GeoreferencingControlPanel extends JPanel {

    private static final long serialVersionUID = 7031021591515735164L;

    public GeoreferencingControlPanel() {
        setLayout( new GridBagLayout() );
        GridBagConstraints gb = new GridBagConstraints();

        gb.gridx = 0;
        gb.gridy = 0;
        gb.gridwidth = 2;
        gb.anchor = CENTER;
        gb.insets = new Insets( 2, 2, 2, 2 );
        add( new JButton( get( "$DI10074" ) ), gb );

        gb = (GridBagConstraints) gb.clone();
        ++gb.gridy;
        JComboBox box = new JComboBox( new String[] { get( "$DI10075" ) } );
        add( box, gb );

        gb = (GridBagConstraints) gb.clone();
        ++gb.gridy;
        add( new JToggleButton( get( "$DI10076" ) ), gb );

        gb = (GridBagConstraints) gb.clone();
        gb.gridx = 2;
        gb.gridy = 0;
        gb.gridwidth = 4;
        gb.gridheight = 3;
        gb.fill = BOTH;
        JPanel panel = new JPanel();
        panel.setBorder( createTitledBorder( get( "$DI10085" ) ) );
        add( panel, gb );

        gb = (GridBagConstraints) gb.clone();
        gb.gridx = 0;
        gb.gridy = 3;
        gb.gridwidth = 3;
        gb.gridheight = 1;
        gb.fill = NONE;
        add( new JButton( get( "$DI10077" ) ), gb );

        gb = (GridBagConstraints) gb.clone();
        gb.gridx = 3;
        add( new JButton( get( "$DI10078" ) ), gb );

        gb = (GridBagConstraints) gb.clone();
        gb.gridx = 0;
        ++gb.gridy;
        gb.gridwidth = 6;
        gb.fill = BOTH;
        JTable table = new JTable( new ControlPointModel() );
        add( new JScrollPane( table ), gb );

        gb = (GridBagConstraints) gb.clone();
        ++gb.gridy;
        gb.gridwidth = 2;
        gb.fill = NONE;
        add( new JButton( get( "$DI10081" ) ), gb );

        gb = (GridBagConstraints) gb.clone();
        gb.gridx += 2;
        add( new JButton( get( "$DI10082" ) ), gb );

        gb = (GridBagConstraints) gb.clone();
        gb.gridx += 2;
        add( new JButton( get( "$DI10083" ) ), gb );
    }

    public void setMapModel( MapModel mm ) {
        // TODO Auto-generated method stub
        
    }

}
