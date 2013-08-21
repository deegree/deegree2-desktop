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

package org.deegree.desktop.views.swing.addlayer;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.framework.utils.SwingUtils;

/**
 * The <code>WizardDialog</code> is a JFrame which describes a header area, described by a
 * InfoPanel where some informmation can be stored a main area and a footer, where some buttons are
 * placed. The main area is variable.
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public abstract class WizardDialog extends JFrame {

    private static final long serialVersionUID = 4898145115401755995L;

    protected InfoPanel infoPanel;

    protected ButtonPanel buttonPanel;

    protected JFrame previousFrame;
    
    protected ApplicationContainer<Container> appContainer;

    /**
     * Initialise needed panels and set default options.
     * 
     * @param appContainer
     * @param frame
     *            the previous dialog
     */
    public WizardDialog( JFrame frame ) {
        this.previousFrame = frame;
        this.buttonPanel = new ButtonPanel();
        this.infoPanel = new InfoPanel();
    }

    /**
     * Initialises the wizard dialog with header, main area and footer.
     */
    public void init() {
        Container pane = this.getContentPane();

        JPanel scrollPanel = new JPanel();
        // scrollBars
        JScrollPane scrollPane = new JScrollPane( scrollPanel );
        scrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        scrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
        scrollPane.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );

        // add components to frame
        GridBagConstraints gbc = SwingUtils.initPanel( scrollPanel );
        gbc.insets = new Insets( 2, 2, 2, 2 );
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1;
        gbc.weighty = 0;

        infoPanel.setVisible( true );

        scrollPanel.add( infoPanel, gbc );
        ++gbc.gridy;
        scrollPanel.add( new JSeparator(), gbc );

        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        ++gbc.gridy;
        scrollPanel.add( getMainPanel(), gbc );

        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        ++gbc.gridy;
        scrollPanel.add( new JSeparator(), gbc );
        ++gbc.gridy;

        buttonPanel.setVisible( true );
        scrollPanel.add( buttonPanel, gbc );

        scrollPanel.setVisible( true );
        pane.add( scrollPane );

    }

    /**
     * close only the current dialog
     */
    protected void close() {
        super.dispose();
    }

    /**
     * Overwride this method to display your own stuff in the main area!
     */
    public abstract JPanel getMainPanel();

    // /////////////////////////////////////////////////////////////////////////////////
    // JFrame
    // /////////////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Window#dispose()
     */
    @Override
    public void dispose() {
        if ( this.previousFrame != null ) {
            this.previousFrame.dispose();
        }
        super.dispose();
    }
}
