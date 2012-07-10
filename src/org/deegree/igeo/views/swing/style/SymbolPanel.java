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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.deegree.igeo.ChangeListener;
import org.deegree.igeo.ValueChangedEvent;
import org.deegree.igeo.settings.GraphicOptions;
import org.deegree.igeo.style.model.GraphicSymbol;
import org.deegree.igeo.style.model.SldValues;
import org.deegree.igeo.style.model.Symbol;
import org.deegree.igeo.style.model.WellKnownMark;
import org.deegree.igeo.views.swing.style.component.MarkPanel;
import org.deegree.igeo.views.swing.style.renderer.SymbolRenderer;
import org.deegree.igeo.views.swing.util.GenericFileChooser;
import org.deegree.igeo.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;
import org.deegree.igeo.views.swing.util.IGeoFileFilter;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>SymbolPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class SymbolPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 5235268293678456809L;

    private ChangeListener changeListener;

    private GraphicOptions graphicOptions;

    private JComboBox markCB;

    private JTextField newMarkNameTF;

    private JButton selectFileBt;

    private JTextField newMarkTF;

    private JButton addMarkBt;

    private JComboBox markToRemoveCB;

    private JButton removeMarkBt;

    /**
     * @param graphicOptions
     *            the graphic options
     */
    public SymbolPanel( GraphicOptions graphicOptions ) {
        this( null, graphicOptions, null );
    }

    /**
     * @param changeListener
     *            the change listener to inform when the selected symbol has changed
     * @param graphicOptions
     *            the graphic options
     * @param border
     *            the border to paint around the panel
     */
    public SymbolPanel( ChangeListener changeListener, GraphicOptions graphicOptions, Border border ) {
        this.changeListener = changeListener;
        this.graphicOptions = graphicOptions;
        setLayout( new BorderLayout() );
        init( border );
        updateSymbolMarkCB();
    }

    /**
     * selectes the entry with this sld name
     * 
     * @param wellKnownName
     *            the name of the mark
     */
    public void setValue( String wellKnownName ) {
        for ( int i = 0; i < markCB.getItemCount(); i++ ) {
            Object item = markCB.getItemAt( i );
            if ( item instanceof WellKnownMark && ( (WellKnownMark) item ).getSldName().equals( wellKnownName ) ) {
                markCB.setSelectedIndex( i );
                break;
            }
        }
    }

    public Symbol getValue() {
        return (Symbol) markCB.getSelectedItem();
    }

    /**
     * selects the entry with the same url if available, otherwise the URL will be added as Symbol to the settings
     * 
     * @param onlineResource
     *            the url of the external graphic
     */
    public void setValue( URL onlineResource ) {
        boolean isInList = false;
        for ( int i = 0; i < markCB.getItemCount(); i++ ) {
            Object item = markCB.getItemAt( i );
            if ( item instanceof GraphicSymbol
                 && ( (GraphicSymbol) item ).getUrl().toExternalForm().equals( onlineResource.toExternalForm() ) ) {
                markCB.setSelectedIndex( i );
                isInList = true;
                break;
            }
        }
        if ( !isInList ) {
            try {
                graphicOptions.addSymbolDefinition( onlineResource.getFile(), onlineResource.toExternalForm() );
                updateSymbolMarkCB();
                markCB.setSelectedItem( graphicOptions.getSymbolDefinitions().get( onlineResource.getFile() ) );
            } catch ( MalformedURLException e ) {
                JOptionPane.showMessageDialog( this, get( "$MD10789" ), get( "$DI10017" ), JOptionPane.ERROR_MESSAGE );
            }
        }
    }

    private void init( Border border ) {
        // init
        // well known
        markCB = new JComboBox();
        markCB.setRenderer( new SymbolRenderer() );
        markCB.addActionListener( this );

        // add own
        newMarkNameTF = new JTextField();
        selectFileBt = new JButton( get( "$MD10771" ) );
        selectFileBt.addActionListener( this );
        newMarkTF = new JTextField();
        addMarkBt = new JButton( get( "$MD10772" ) );
        addMarkBt.addActionListener( this );

        // remove own
        markToRemoveCB = new JComboBox();
        markToRemoveCB.setRenderer( new SymbolRenderer() );
        removeMarkBt = new JButton( get( "$MD11165" ) );
        removeMarkBt.addActionListener( this );

        // layout
        FormLayout fl = new FormLayout( "left:$rgap, left:min, $ugap, fill:default:grow(1)",
                                        "$sepheight, $cpheight, $sepheight, $cpheight, $btheight, $cpheight, $btheight, $sepheight, $cpheight, $btheight" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        CellConstraints cc = new CellConstraints();

        builder.addSeparator( get( "$MD10774" ), cc.xyw( 1, 1, 4 ) );
        builder.add( markCB, cc.xyw( 2, 2, 3 ) );

        builder.addSeparator( get( "$MD10775" ), cc.xyw( 1, 3, 4 ) );
        builder.addLabel( get( "$MD10776" ), cc.xy( 2, 4 ) );
        builder.add( newMarkNameTF, cc.xy( 4, 4 ) );

        builder.addLabel( get( "$MD10777" ), cc.xy( 2, 5 ) );
        builder.add( selectFileBt, cc.xy( 4, 5, CellConstraints.RIGHT, CellConstraints.CENTER ) );
        builder.add( newMarkTF, cc.xyw( 2, 6, 3 ) );
        builder.add( addMarkBt, cc.xyw( 2, 7, 3, CellConstraints.CENTER, CellConstraints.CENTER ) );

        builder.addSeparator( get( "$MD11166" ), cc.xyw( 1, 8, 4 ) );
        builder.add( markToRemoveCB, cc.xyw( 2, 9, 3 ) );
        builder.add( removeMarkBt, cc.xyw( 2, 10, 3, CellConstraints.CENTER, CellConstraints.CENTER ) );

        add( builder.getPanel() );

    }

    private void selectFile() {
        File file = GenericFileChooser.showOpenDialog( FILECHOOSERTYPE.image, null, this,
                                                       Preferences.systemNodeForPackage( MarkPanel.class ),
                                                       "lastSelectMark", IGeoFileFilter.IMAGES );
        if ( file != null ) {
            try {
                newMarkTF.setText( file.toURI().toURL().toExternalForm() );
            } catch ( MalformedURLException e ) {
                // should never happen
                e.printStackTrace();
            }
        }
    }

    private void addMark() {
        String url = newMarkTF.getText();
        String name = newMarkNameTF.getText();
        boolean invalidURL = false;
        if ( url != null && url.length() > 0 ) {
            try {
                URL u = new URL( url );
                GraphicSymbol newSymbol = new GraphicSymbol( name, u );
                if ( newSymbol.getFormat() != null ) {
                    graphicOptions.addSymbolDefinition( name, u.toExternalForm() );
                    updateSymbolMarkCB();
                    markCB.setSelectedItem( graphicOptions.getSymbolDefinitions().get( name ) );

                    newMarkTF.setText( "" );
                    newMarkNameTF.setText( "" );
                } else {
                    JOptionPane.showMessageDialog( this, get( "$MD10780" ), get( "$MD10781" ),
                                                   JOptionPane.INFORMATION_MESSAGE );
                }
            } catch ( MalformedURLException e ) {
                invalidURL = true;
            }
        } else {
            invalidURL = true;
        }
        if ( invalidURL ) {
            JOptionPane.showMessageDialog( this, get( "$MD10782" ), get( "$MD10783" ), JOptionPane.INFORMATION_MESSAGE );
        }
    }

    private void removeMark() {
        if ( markToRemoveCB.getSelectedItem() != null && markToRemoveCB.getSelectedItem() instanceof GraphicSymbol ) {
            GraphicSymbol gs = (GraphicSymbol) markToRemoveCB.getSelectedItem();
            int result = JOptionPane.showOptionDialog( this, get( "$MD11168", gs.getName() ), get( "$MD11167" ),
                                                       JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                                                       null, null, null );
            if ( result == JOptionPane.OK_OPTION ) {
                try {
                    graphicOptions.removeSymbolDefinition( gs.getName() );
                    markToRemoveCB.removeItem( gs );
                    if ( markCB.getSelectedItem() == gs ) {
                        markCB.setSelectedIndex( 0 );
                    }
                    markCB.removeItem( gs );
                } catch ( MalformedURLException e ) {
                    JOptionPane.showMessageDialog( this, get( "$MD11169" ), get( "$DI10017" ),
                                                   JOptionPane.INFORMATION_MESSAGE );
                }
                if ( markToRemoveCB.getItemCount() > 0 ) {
                    removeMarkBt.setEnabled( true );
                } else {
                    removeMarkBt.setEnabled( false );
                }
            }
        }
    }

    private void updateSymbolMarkCB() {
        markCB.removeAllItems();
        markToRemoveCB.removeAllItems();
        for ( WellKnownMark mark : SldValues.getWellKnownMarks() ) {
            markCB.addItem( mark );
        }
        try {
            Map<String, GraphicSymbol> symbols = graphicOptions.getSymbolDefinitions();
            List<GraphicSymbol> values = new ArrayList<GraphicSymbol>();
            values.addAll( symbols.values() );
            Collections.sort( (List<GraphicSymbol>) values );
            for ( GraphicSymbol gs : values ) {
                markCB.addItem( gs );
                markToRemoveCB.addItem( gs );
            }
        } catch ( MalformedURLException e ) {
            JOptionPane.showMessageDialog( this, get( "$MD10788" ), get( "$DI10017" ), JOptionPane.ERROR_MESSAGE );
        }

        if ( markToRemoveCB.getItemCount() > 0 ) {
            removeMarkBt.setEnabled( true );
        } else {
            removeMarkBt.setEnabled( false );
        }
    }

    // //////////////////////////////////////////////////////////////////////////////
    // ACTIONLISTENER
    // //////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed( ActionEvent e ) {
        if ( e.getSource() == markCB && changeListener != null ) {
            changeListener.valueChanged( new SymbolChangedEvent( (Symbol) markCB.getSelectedItem() ) );
        } else if ( e.getSource() == selectFileBt ) {
            selectFile();
        } else if ( e.getSource() == addMarkBt ) {
            addMark();
        } else if ( e.getSource() == removeMarkBt ) {
            removeMark();
        }
    }

    public class SymbolChangedEvent extends ValueChangedEvent {

        private Symbol symbol;

        public SymbolChangedEvent( Symbol symbol ) {
            this.symbol = symbol;
        }

        @Override
        public Object getValue() {
            return symbol;
        }
    }
}
