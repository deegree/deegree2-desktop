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

package org.deegree.igeo.views.swing.style.component.raster;

import static org.deegree.igeo.i18n.Messages.get;

import java.awt.Dimension;
import java.text.NumberFormat;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.deegree.igeo.style.perform.ComponentType;
import org.deegree.igeo.style.perform.StyleChangedEvent;
import org.deegree.igeo.views.swing.style.VisualPropertyPanel;
import org.deegree.igeo.views.swing.style.component.AbstractStyleAttributePanel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>ContrastEnhancementPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class ContrastEnhancementPanel extends AbstractStyleAttributePanel {

    private static final long serialVersionUID = 1926426508955606108L;

    private JSlider gammaValueSlider;

    private JTextField gammaValueTextField;

    public ContrastEnhancementPanel( VisualPropertyPanel assignedVisualPropPanel, ComponentType componentType,
                                     String helpText, ImageIcon imageIcon ) {
        super( assignedVisualPropPanel, componentType, helpText, imageIcon );
    }

    /**
     * @param gamma
     */
    public void setValue( int gamma ) {
        this.gammaValueTextField.setText( String.valueOf( gamma ) );
        this.gammaValueSlider.setValue( gamma );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.view.components.AbstractStyleAttributePanel#getFixedPanel ()
     */
    @Override
    protected JComponent getStyleAttributeComponent() {
        // init
        NumberFormat nf = NumberFormat.getNumberInstance( getLocale() );
        nf.setParseIntegerOnly( true );
        this.gammaValueTextField = new JFormattedTextField( nf );
        this.gammaValueTextField.setEnabled( false );
        this.gammaValueTextField.setText( nf.format( 0 ) );
        Dimension dim = new Dimension( 40, 20 );
        this.gammaValueTextField.setSize( dim );
        this.gammaValueTextField.setMaximumSize( dim );
        this.gammaValueTextField.setMinimumSize( dim );
        this.gammaValueTextField.setPreferredSize( dim );
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put( new Integer( -255 ), new JLabel( get( "$MD10850" ) ) );
        labelTable.put( new Integer( 0 ), new JLabel() );
        labelTable.put( new Integer( 255 ), new JLabel( get( "$MD10851" ) ) );

        this.gammaValueSlider = new JSlider( JSlider.HORIZONTAL, -255, 255, 0 );
        this.gammaValueSlider.setLabelTable( labelTable );
        this.gammaValueSlider.setMajorTickSpacing( 255 );
        this.gammaValueSlider.setPaintTicks( true );
        this.gammaValueSlider.setPaintLabels( true );
        // 
        this.gammaValueSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int value = ( (JSlider) e.getSource() ).getValue();
                gammaValueTextField.setText( String.valueOf( value ) );
                assignedVisualPropPanel.update( new StyleChangedEvent( value, componentType ) );
            }
        } );

        // layout
        FormLayout fl = new FormLayout( "left:default, $glue, left:default", "bottom:pref" );

        DefaultFormBuilder builder = new DefaultFormBuilder( fl );

        CellConstraints cc = new CellConstraints();

        builder.add( this.gammaValueSlider, cc.xy( 1, 1 ) );
        builder.add( this.gammaValueTextField, cc.xy( 3, 1 ) );

        return builder.getPanel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.component.AbstractStyleAttributePanel#getTitle()
     */
    @Override
    protected String getTitle() {
        return get( "$MD10849" );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.component.StyleAttributePanel#getValue()
     */
    public Object getValue() {
        return null;
    }
}
