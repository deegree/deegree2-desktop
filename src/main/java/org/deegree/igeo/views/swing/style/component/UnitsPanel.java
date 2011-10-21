//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 and
 lat/lon GmbH

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

package org.deegree.igeo.views.swing.style.component;

import static org.deegree.igeo.i18n.Messages.get;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.deegree.igeo.ChangeListener;
import org.deegree.igeo.ValueChangedEvent;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>UnitsPanel</code> is the GUI to change between map units and pixel
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class UnitsPanel extends JPanel {

    private static final long serialVersionUID = 1093920670176528567L;

    private ChangeListener changeListener;

    private JRadioButton pixelRB;

    private JRadioButton mapUnitsRB;

    private boolean isPixel;

    /**
     * @param changeListener
     *            the changeListener to be informe, when seleection changed
     */
    public UnitsPanel( ChangeListener changeListener, boolean isPixel ) {
        this.changeListener = changeListener;
        this.isPixel = isPixel;
        setLayout( new BorderLayout() );
        init();
    }

    private void init() {
        // init
        pixelRB = new JRadioButton( get( "$MD11061" ) );
        mapUnitsRB = new JRadioButton( get( "$MD11062" ) );
        ButtonGroup bg = new ButtonGroup();
        bg.add( pixelRB );
        bg.add( mapUnitsRB );

        pixelRB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent arg0 ) {
                fireSelectionChangedEvent();
            }
        } );
        mapUnitsRB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent arg0 ) {
                fireSelectionChangedEvent();
            }
        } );

        if ( isPixel ) {
            pixelRB.setSelected( true );
        } else {
            mapUnitsRB.setSelected( true );
        }

        // layout
        FormLayout fl = new FormLayout( "$rgap, left:default:grow(1.0)", "$sepheight, default, default" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );

        CellConstraints cc = new CellConstraints();

        builder.addSeparator( get( "$MD11060" ), cc.xyw( 1, 1, 2 ) );
        builder.add( pixelRB, cc.xy( 2, 2 ) );
        builder.add( mapUnitsRB, cc.xy( 2, 3 ) );

        add( builder.getPanel(), BorderLayout.CENTER );

    }

    /**
     * @return true, if mapUnits are selected; false, if units are pixel
     */
    public boolean isInMapUnits() {
        return mapUnitsRB.isSelected();
    }

    /**
     * @param isInMapUnits
     *            if true, the radio button for map units is selected
     */
    public void setIsInMapUnits( boolean isInMapUnits ) {
        if ( isInMapUnits ) {
            mapUnitsRB.setSelected( true );
        } else {
            pixelRB.setSelected( true );
        }
        fireSelectionChangedEvent();
    }

    private void fireSelectionChangedEvent() {
        if ( changeListener != null ) {
            changeListener.valueChanged( new ValueChangedEvent() {
                @Override
                public Object getValue() {
                    return isInMapUnits();
                }
            } );
        }
    }

}
