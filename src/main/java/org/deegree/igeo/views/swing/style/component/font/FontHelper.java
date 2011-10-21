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
package org.deegree.igeo.views.swing.style.component.font;

import static org.deegree.igeo.i18n.Messages.get;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.deegree.igeo.style.model.SldProperty;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.views.swing.style.renderer.SldPropertyRenderer;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class FontHelper {

    private static final String TITLEINTER = get( "$MD11054" );

    private static final String TTINTER = get( "$MD11055" );

    private static final String TITLEIND = get( "$MD11056" );

    private static final String TTIND = get( "$MD11057" );

    private static final List<String> commonFonts = new ArrayList<String>();

    static {
        commonFonts.add( "Dialog" );
        commonFonts.add( "DialogInput" );
        commonFonts.add( "Monospaced" );
        commonFonts.add( "SansSerif" );
        commonFonts.add( "Serif" );
    }

    public JComboBox createFontFamilyChooser() {
        // get all available system fonts
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();
        List<String> items = new ArrayList<String>();

        items.add( TITLEINTER );
        for ( String commonFont : commonFonts ) {
            items.add( commonFont );
        }
        items.add( TITLEIND );
        for ( int i = 0; i < fontNames.length; i++ ) {
            if ( !commonFonts.contains( fontNames[i] ) ) {
                items.add( fontNames[i] );
            }
        }
        JComboBox fixedFontFamilyCB = new JComboBox( new FontComboBoxModel( items ) );
        fixedFontFamilyCB.setRenderer( new FontRenderer() );
        fixedFontFamilyCB.setSelectedItem( SldValues.getDefaultFontFamily() );
        return fixedFontFamilyCB;
    }

    public JComboBox createFontStyleChooser() {
        JComboBox fixedCB = new JComboBox();
        fixedCB = new JComboBox();
        fixedCB.setRenderer( new SldPropertyRenderer() );
        List<SldProperty> fontStyles = SldValues.getFontStyles();
        for ( SldProperty fontStyle : fontStyles ) {
            fixedCB.addItem( fontStyle );
            if ( fontStyle.getSldName().equals( SldValues.getDefaultFontStyle() ) ) {
                fixedCB.setSelectedItem( fontStyle );
            }
        }
        return fixedCB;
    }

    public JComboBox createFontWeightChooser() {
        JComboBox fixedCB = new JComboBox();
        fixedCB.setRenderer( new SldPropertyRenderer() );
        List<SldProperty> fontWeights = SldValues.getFontWeights();
        for ( SldProperty fontWeight : fontWeights ) {
            fixedCB.addItem( fontWeight );
            if ( fontWeight.getSldName().equals( SldValues.getDefaultFontWeight() ) ) {
                fixedCB.setSelectedItem( fontWeight );
            }
        }
        return fixedCB;
    }

    // //////////////////////////////////////////////////////////////////////////////
    // INNER CLASSES
    // //////////////////////////////////////////////////////////////////////////////
    private class FontRenderer extends JLabel implements ListCellRenderer {

        private static final long serialVersionUID = -4777623881537342087L;

        public FontRenderer() {
            setPreferredSize( new Dimension( 150, 20 ) );
            setBorder( BorderFactory.createEmptyBorder( 0, 2, 0, 0 ) );
        }

        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected,
                                                       boolean cellHasFocus ) {
            String v = (String) value;
            Font f = getFont();
            if ( TITLEINTER.equals( value ) || TITLEIND.equals( value ) ) {
                if ( TITLEINTER.equals( value ) ) {
                    setToolTipText( TTINTER );
                } else {
                    setToolTipText( TTIND );
                }
                setFont( new Font( "SansSerif", Font.BOLD | Font.ITALIC, f.getSize() ) );
                setText( "<html><font style='text-decoration:underline'>" + (String) value + "</font></html>" );
            } else {

                setFont( new Font( v, Font.PLAIN, f.getSize() ) );
                setToolTipText( v );
                setText( v );
            }
            return this;
        }
    }

    private class FontComboBoxModel extends DefaultComboBoxModel {

        private static final long serialVersionUID = 8637101817815252261L;

        private FontComboBoxModel( List<String> items ) {
            super( (String[]) items.toArray( new String[items.size()] ) );
        }

        @Override
        public void setSelectedItem( Object anObject ) {
            Object itemToSelect = getSelectedItem();
            if ( !( TITLEINTER.equals( anObject ) || TITLEIND.equals( anObject ) ) ) {
                itemToSelect = anObject;
            }
            super.setSelectedItem( itemToSelect );
        }
    }

}
