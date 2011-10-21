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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import org.deegree.igeo.i18n.Messages;

/**
 * The <code>PreviewPanel</code> gives an overview of the style of a visual property as image.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class PreviewPanel extends JPanel {

    private static final long serialVersionUID = 6587180166661907188L;

    private BufferedImage previewImg = null;

    private JCheckBox showPreviewCB;

    private JPanel imgPanel;

    /**
     * @param actionListener
     *            the action listener to be informed when the user decided to show or hide the
     *            preview
     */
    public PreviewPanel( ActionListener actionListener ) {
        this.setPreferredSize( new Dimension( 150, 70 ) );
        TitledBorder tb = BorderFactory.createTitledBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ),
                                                            Messages.getMessage( Locale.getDefault(), "$MD10846" ) );
        tb.setTitleJustification( TitledBorder.CENTER );
        this.setBorder( tb );
        this.setLayout( new BorderLayout() );

        imgPanel = new JPanel() {

            private static final long serialVersionUID = -5271619777477867125L;

            /*
             * (non-Javadoc)
             * 
             * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
             */
            @Override
            protected void paintComponent( Graphics g ) {
                super.paintComponent( g );
                if ( previewImg != null ) {
                    g.drawImage( previewImg, 10, 10, this.getBackground(), this );
                } else {
                    g.setColor( this.getBackground() );
                    g.drawRect( 0, 0, this.getWidth(), this.getHeight() );
                }
            }
        };
        imgPanel.setVisible( true );
        JScrollPane scrollPane = new JScrollPane( imgPanel );
        scrollPane.setVisible( true );
        this.add( scrollPane, BorderLayout.CENTER );

        showPreviewCB = new JCheckBox( Messages.getMessage( Locale.getDefault(), "$MD10847" ) );
        showPreviewCB.addActionListener( actionListener );
        this.add( showPreviewCB, BorderLayout.NORTH );
    }

    /**
     * @param img
     *            the image to show as preview
     */
    public void update( BufferedImage img ) {
        this.previewImg = img;
        if ( this.previewImg != null ) {
            this.imgPanel.setPreferredSize( new Dimension( previewImg.getWidth() + 20, previewImg.getHeight() + 20 ) );
            this.imgPanel.setSize( new Dimension( previewImg.getWidth() + 20, previewImg.getHeight() + 20 ) );
        }
        this.imgPanel.repaint();
    }

    /**
     * @return true, if the preview is activated; false, if the preview should be hidden
     */
    public boolean isShowPreviewSelected() {
        return this.showPreviewCB.isSelected();
    }

}
