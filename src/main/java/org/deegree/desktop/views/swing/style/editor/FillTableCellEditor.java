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

package org.deegree.desktop.views.swing.style.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.settings.GraphicOptions;
import org.deegree.desktop.style.model.FillGraphic;
import org.deegree.desktop.style.model.FillPattern;
import org.deegree.desktop.style.model.GraphicSymbol;
import org.deegree.desktop.views.swing.style.FillGraphicPanel;
import org.deegree.desktop.views.swing.util.panels.PanelDialog;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>FillTableCellEditor</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class FillTableCellEditor extends AbstractCellEditor implements TableCellEditor {

    private static final long serialVersionUID = 8520672145892102530L;

    private JButton fillBt;

    private Object selectedFill;

    private GraphicOptions graphicOptions;

    private JButton fixColorBt;

    private FillGraphicPanel fillGraphicPanel;

    private JPanel dialogPanel;

    private Color defaultColor;

    public FillTableCellEditor( GraphicOptions graphicOptions, Color defaultColor ) {
        this.graphicOptions = graphicOptions;
        this.defaultColor = defaultColor;
        fillBt = new JButton();
        fillBt.setBorderPainted( false );
        fillBt.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( dialogPanel == null ) {
                    createDialogPanel();
                }
                if ( selectedFill != null && selectedFill instanceof Color ) {
                    fixColorBt.setBackground( (Color) selectedFill );
                } else if ( selectedFill != null
                            && ( selectedFill instanceof FillGraphic || selectedFill instanceof GraphicSymbol ) ) {
                    GraphicSymbol gs;
                    if ( selectedFill instanceof FillGraphic ) {
                        gs = ( (FillGraphic) selectedFill ).getGraphicSymbol();
                    } else {
                        gs = (GraphicSymbol) selectedFill;
                    }
                    fillGraphicPanel.setValue( gs.getUrl(), gs.getSize() );
                    if ( gs instanceof FillPattern ) {
                        Color color = ( (FillPattern) gs ).getColor();
                        fillGraphicPanel.createAndUpdateColor( color, Color.BLACK );
                        fixColorBt.setBackground( color );
                    }
                }
                PanelDialog dlg = new PanelDialog( dialogPanel, true );
                dlg.setLocationRelativeTo( fillBt );
                dlg.setVisible( true );
                if ( dlg.clickedOk ) {
                    GraphicSymbol gs = fillGraphicPanel.getValue();
                    if ( gs != null ) {
                        if ( gs instanceof FillPattern ) {
                            ( (FillPattern) gs ).setColor( fixColorBt.getBackground() );
                        }
                        selectedFill = gs;
                    } else {
                        selectedFill = fixColorBt.getBackground();
                    }
                }
                fireEditingStopped();
            }

        } );
    }

    private void createDialogPanel() {
        dialogPanel = new JPanel() {
            private static final long serialVersionUID = -8531703257413252791L;

            @Override
            public String toString() {
                return Messages.get( "$MD10743" );
            }
        };
        dialogPanel.setLayout( new BorderLayout() );

        fixColorBt = new JButton( Messages.get( "$MD10742" ) );
        fixColorBt.setBackground( defaultColor );
        fixColorBt.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Color color = JColorChooser.showDialog( fixColorBt, Messages.get( "$MD10736" ), fixColorBt.getBackground() );
                if ( color != null ) {
                    fixColorBt.setBackground( color );
                    fillGraphicPanel.createAndUpdateColor( color, Color.BLACK );
                }
            }
        } );
        fillGraphicPanel = new FillGraphicPanel( graphicOptions );

        FormLayout fl = new FormLayout( "left:pref:grow(1.0)", "$cpheight, pref" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );

        builder.add( fixColorBt );

        builder.nextLine();
        builder.add( fillGraphicPanel );

        dialogPanel.add( builder.getPanel(), BorderLayout.CENTER );
        dialogPanel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing.JTable,
     * java.lang.Object, boolean, int, int)
     */
    public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected, int row, int column ) {
        selectedFill = value;
        return fillBt;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.CellEditor#getCellEditorValue()
     */
    public Object getCellEditorValue() {
        return selectedFill;
    }

    @Override
    public boolean isCellEditable( EventObject e ) {
        if ( e instanceof MouseEvent )
            return ( (MouseEvent) e ).getClickCount() > 1;
        return true;
    }
}
