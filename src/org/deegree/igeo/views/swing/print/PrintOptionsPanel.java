//$HeadURL$
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

package org.deegree.igeo.views.swing.print;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import static java.lang.Integer.parseInt;
import static java.util.prefs.Preferences.userNodeForPackage;
import static org.deegree.framework.util.CollectionUtils.map;
import static org.deegree.igeo.i18n.Messages.get;
import static org.deegree.igeo.views.swing.util.GenericFileChooser.showSaveDialog;
import static org.deegree.igeo.views.swing.util.GuiUtils.getOwnerFrame;
import static org.deegree.igeo.views.swing.util.GuiUtils.initPanel;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.deegree.framework.util.CollectionUtils.Mapper;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.views.swing.util.IGeoFileFilter;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.igeo.views.swing.util.JIntField;
import org.deegree.igeo.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;
import org.deegree.igeo.views.swing.util.wizard.Wizard;

/**
 * <code>PrintOptionsPanel</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class PrintOptionsPanel extends JPanel implements DocumentListener, ActionListener {

    private static final long serialVersionUID = -7307405601676884335L;

    JTextField fileField;

    boolean fileChanged = false;

    private double aspect;

    private JButton browse;

    File file;

    JIntField width, height;

    JComboBox scale;

    JRadioButton atLeastVisible, selectScale, complex;

    AtLeastVisiblePanel atLeastVisiblePanel;

    ComplexOptionsPanel complexPanel;

    SelectScalePanel selectScalePanel;

    private ApplicationContainer<?> appCont;

    private ActionListener buttonListener = new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
            atLeastVisiblePanel.setEnabled( atLeastVisible.isSelected() );
            selectScalePanel.setEnabled( selectScale.isSelected() );
            complexPanel.setEnabled( complex.isSelected() );
        }

    };

    /**
     * 
     */
    public PrintOptionsPanel() {
        GridBagConstraints gb = initPanel( this );
        gb.anchor = WEST;

        ButtonGroup group = new ButtonGroup();
        atLeastVisible = new JRadioButton( get( "$MD10368" ) );
        atLeastVisible.addActionListener( buttonListener );
        selectScale = new JRadioButton( get( "$MD10369" ) );
        selectScale.addActionListener( buttonListener );
        complex = new JRadioButton( get( "$MD10367" ) );
        complex.addActionListener( buttonListener );
        group.add( atLeastVisible );
        group.add( selectScale );
        group.add( complex );

        atLeastVisiblePanel = new AtLeastVisiblePanel();
        selectScalePanel = new SelectScalePanel();
        complexPanel = new ComplexOptionsPanel();

        ++gb.gridy;
        add( atLeastVisiblePanel, gb );

        ++gb.gridy;
        add( selectScalePanel, gb );

        ++gb.gridy;
        add( complexPanel, gb );

        ++gb.gridy;
        gb.fill = BOTH;
        add( fileField = new JTextField( get( "$DI10023" ), 20 ), gb );
        fileField.getDocument().addDocumentListener( this );

        ++gb.gridx;
        gb.fill = NONE;
        Icon icon = IconRegistry.getIcon( "save.gif" );
        add( browse = new JButton( get( "$DI10042" ), icon ), gb );

        browse.addActionListener( this );
        width.setColumns( 5 );
        height.setColumns( 5 );

        atLeastVisible.setSelected( true );
        buttonListener.actionPerformed( null );
    }

    /**
     * Also installs the document listeners for the width/height fields
     */
    public void calculateAspectRatio() {
        aspect = (double) width.getInt() / (double) height.getInt();
        height.getDocument().addDocumentListener( this );
        width.getDocument().addDocumentListener( this );
    }

    public void changedUpdate( DocumentEvent e ) {
        if ( e.getDocument() == fileField.getDocument() ) {
            fileChanged = true;
        }
        if ( e.getDocument() == width.getDocument() ) {
            height.getDocument().removeDocumentListener( this );
            height.setInt( (int) ( width.getInt() / aspect ) );
            height.getDocument().addDocumentListener( this );
        }

        if ( e.getDocument() == height.getDocument() ) {
            width.getDocument().removeDocumentListener( this );
            width.setInt( (int) ( height.getInt() * aspect ) );
            width.getDocument().addDocumentListener( this );
        }
    }

    public void insertUpdate( DocumentEvent e ) {
        changedUpdate( e );
    }

    public void removeUpdate( DocumentEvent e ) {
        changedUpdate( e );
    }

    public void actionPerformed( ActionEvent e ) {
        Preferences prefs = userNodeForPackage( PrintOptionsPanel.class );
        File f = showSaveDialog( new File( fileField.getText() ), FILECHOOSERTYPE.printResult, appCont, this, prefs,
                                 "outputdir", IGeoFileFilter.XML, IGeoFileFilter.JPEG, IGeoFileFilter.PNG,
                                 IGeoFileFilter.HTML, IGeoFileFilter.PDF );
        if ( f != null ) {
            fileField.setText( f.toString() );
            file = f;
        }
    }

    private class ComplexOptionsPanel extends JPanel {

        private static final long serialVersionUID = -2194789235167324831L;

        ComplexOptionsPanel() {
            GridBagConstraints gb = initPanel( this );
            gb.anchor = WEST;
            gb.gridwidth = 2;
            add( complex, gb );
            gb.gridwidth = 1;
            ++gb.gridy;
            add( new JLabel( get( "$MD10353" ) ), gb );
            ++gb.gridx;
            add( width = new JIntField( 0 ), gb );
            ++gb.gridy;
            gb.gridx = 0;
            add( new JLabel( get( "$MD10354" ) ), gb );
            ++gb.gridx;
            add( height = new JIntField( 0 ), gb );

            width.setColumns( 5 );
            height.setColumns( 5 );
        }

        @Override
        public void setEnabled( boolean enabled ) {
            super.setEnabled( enabled );
            height.setEnabled( enabled );
            width.setEnabled( enabled );
        }

    }

    private class AtLeastVisiblePanel extends JPanel {

        private static final long serialVersionUID = 7916928762036539769L;

        AtLeastVisiblePanel() {
            GridBagConstraints gb = initPanel( this );
            gb.anchor = WEST;
            add( atLeastVisible, gb );
        }

    }

    private class SelectScalePanel extends JPanel {

        private static final long serialVersionUID = 4623153715240768987L;

        Vector<Scale> scales = new Vector<Scale>();

        SelectScalePanel() {
            GridBagConstraints gb = initPanel( this );
            gb.anchor = WEST;
            add( selectScale, gb );
            ++gb.gridy;
            add( scale = new JComboBox( scales ), gb );

            Preferences prefs = userNodeForPackage( SelectScalePanel.class );
            String pref = prefs.get( "scales", "100000,250000,500000,1000000,5000000,10000000" );
            scales.addAll( map( pref.split( "," ), new Mapper<Scale, String>() {
                public Scale apply( String u ) {
                    Scale s = new Scale( u );
                    return s;
                }
            } ) );

            scale.setEditable( true );
            scale.getEditor().getEditorComponent().addFocusListener( new FocusListener() {
                public void focusGained( FocusEvent e ) {
                    ( (Wizard) getOwnerFrame( getParent() ) ).disableDefaultButton();
                }

                public void focusLost( FocusEvent e ) {
                    ( (Wizard) getOwnerFrame( getParent() ) ).enableDefaultButton();
                }
            } );
            scale.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( scale.getSelectedItem() instanceof String ) {
                        String s = ( (String) scale.getEditor().getItem() ).trim();
                        try {
                            Scale sc = new Scale( s );
                            if ( !scales.contains( sc ) ) {
                                int idx = 0;
                                while ( idx < scales.size() && scales.get( idx ).scale < sc.scale ) {
                                    ++idx;
                                }
                                scales.add( idx, sc );
                                scale.setSelectedIndex( idx );
                            } else {
                                scale.setSelectedItem( sc );
                            }
                        } catch ( NumberFormatException ex ) {
                            // ignore
                        }
                    }
                }
            } );
        }

        @Override
        public void setEnabled( boolean enabled ) {
            super.setEnabled( enabled );
            scale.setEnabled( enabled );
        }

    }

    class Scale {
        int scale;

        Scale( String s ) {
            int first = 1;
            if ( s.indexOf( ":" ) != -1 ) {
                first = parseInt( s.split( ":" )[0].trim() );
                s = s.substring( s.indexOf( ":" ) + 1 ).trim();
            }
            scale = parseInt( s ) / first;
        }

        @Override
        public boolean equals( Object o ) {
            if ( o instanceof Scale ) {
                return ( (Scale) o ).scale == scale;
            }

            return false;
        }

        @Override
        public String toString() {
            return "1 : " + scale;
        }
    }

    @Override
    public String toString() {
        return get( "$MD10355" );
    }

}
