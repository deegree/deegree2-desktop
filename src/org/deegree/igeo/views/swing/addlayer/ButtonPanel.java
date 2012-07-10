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

package org.deegree.igeo.views.swing.addlayer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.deegree.framework.utils.SwingUtils;
import org.deegree.igeo.i18n.Messages;

/**
 * The <code>ButtonPanel</code> groups four buttons (previous, next, finish, cancel) in  one row. 
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 * 
 */
public class ButtonPanel extends JPanel {

    private static final long serialVersionUID = -767642922155371140L;

    public static final String PREVIOUS_BT = "previous";

    public static final String NEXT_BT = "next";

    public static final String FINISH_BT = "finish";

    public static final String CANCEL_BT = "cancel";

    private JButton cancelButton;

    private JButton nextButton;

    private JButton previousButton;

    private JButton finishButton;

    /**
     * initialise a new button panel
     */
    public ButtonPanel() {
        super();
        Dimension dim = new Dimension( 300, 75 );
        this.setSize( dim );
        this.setPreferredSize( dim );
        this.setMinimumSize( dim );

        GridBagConstraints gbc = SwingUtils.initPanel( this );

        previousButton = new JButton( "< " + Messages.getMessage( Locale.getDefault(), "$MD10011" ) );
        previousButton.setName( PREVIOUS_BT );
        previousButton.setVisible( true );
        this.add( previousButton, gbc );
        gbc.weightx = 0;
        gbc.weighty = 0;

        ++gbc.gridx;
        nextButton = new JButton( Messages.getMessage( Locale.getDefault(), "$MD10010" ) + " >" );
        nextButton.setName( NEXT_BT );
        nextButton.setVisible( true );
        this.add( nextButton );

        gbc.insets = new Insets( 2, 10, 2, 10 );
        ++gbc.gridx;
        finishButton = new JButton( Messages.getMessage( Locale.getDefault(), "$MD10012" ) );
        finishButton.setName( FINISH_BT );
        finishButton.setVisible( true );
        this.add( finishButton, gbc );

        gbc.insets = new Insets( 2, 2, 2, 2 );
        ++gbc.gridx;
        cancelButton = new JButton( Messages.getMessage( Locale.getDefault(), "$MD10009" ) );
        cancelButton.setName( CANCEL_BT );
        cancelButton.setVisible( true );
        this.add( cancelButton, gbc );
    }

    /**
     * @param name the name of the button to enable/disable
     * @param enabled 
     */
    public void setButtonEnabled( String name, boolean enabled ) {
        if ( name.equals( NEXT_BT ) ) {
            nextButton.setEnabled( enabled );
        } else if ( name.equals( PREVIOUS_BT ) ) {
            previousButton.setEnabled( enabled );
        } else if ( name.equals( FINISH_BT ) ) {
            finishButton.setEnabled( enabled );
        } else if ( name.equals( CANCEL_BT ) ) {
            cancelButton.setEnabled( enabled );
        }

    }

    //    
    // public JButton getNextButton(){
    // return nextButton;
    // }
    //
    // public JButton getPreviousButton(){
    // return previousButton;
    // }
    //    
    // public JButton getFinishButton(){
    // return finishButton;
    // }
    //    
    // public JButton getCancelButton(){
    // return cancelButton;
    // }
    
    /**
     * @param actionListener the actionListener to register to all buttons
     */
    public void registerActionListener( ActionListener actionListener ) {
        previousButton.addActionListener( actionListener );
        nextButton.addActionListener( actionListener );
        finishButton.addActionListener( actionListener );
        cancelButton.addActionListener( actionListener );
    }
}
