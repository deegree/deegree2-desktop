//$HeadURL$

/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2008 by:
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
package org.deegree.igeo.views.swing.bookmark;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.swing.util.IconRegistry;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class NewBookmarkDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = -3139472192685748548L;

    private JTextArea ta_description;

    private JLabel lb_description;

    private JTextField tf_name;

    private JCheckBox cb_all;

    private JPanel pn_help;

    private JButton bt_help;

    private JButton bt_cancel;

    private JButton bt_ok;

    private JPanel pn_buttons;

    private JTextArea ta_desc;

    private JLabel lb_name;

    private boolean ok;

    /**
     * 
     * @param parent
     */
    public NewBookmarkDialog( Container parent ) {
        setTitle( Messages.getMessage( getLocale(), "$MD11131" ) );
        initGUI();
        int x = parent.getX() + parent.getWidth() / 2 - getWidth() / 2;
        int y = parent.getY() + parent.getHeight() / 2 - getHeight() / 2;
        setLocation( x, y );
        setModal( true );
        setVisible( true );
    }

    private void initGUI() {
        try {
            {
                GridBagLayout thisLayout = new GridBagLayout();
                thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.1 };
                thisLayout.rowHeights = new int[] { 39, 154, 40, -1, 7 };
                thisLayout.columnWeights = new double[] { 0.0, 0.0, 0.1 };
                thisLayout.columnWidths = new int[] { 165, 103, 20 };
                getContentPane().setLayout( thisLayout );                
                {
                    lb_name = new JLabel();
                    getContentPane().add(
                                          lb_name,
                                          new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0,
                                                                                                             0 ), 0, 0 ) );
                    lb_name.setText( Messages.getMessage( getLocale(), "$MD11134" ) );
                }
                {
                    lb_description = new JLabel();
                    getContentPane().add(
                                          lb_description,
                                          new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                                                                  GridBagConstraints.HORIZONTAL, new Insets( 10, 10, 0,
                                                                                                             0 ), 0, 0 ) );
                    lb_description.setText( Messages.getMessage( getLocale(), "$MD11135" ) );
                }
                {
                    tf_name = new JTextField();
                    getContentPane().add(
                                          tf_name,
                                          new GridBagConstraints( 2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL, new Insets( 0, 5, 0,
                                                                                                             10 ), 0, 0 ) );
                }
                {
                    ta_desc = new JTextArea();
                    ta_desc.setBorder( BorderFactory.createLineBorder( Color.BLACK ) );
                    ta_desc.setLineWrap( true );
                    ta_desc.setWrapStyleWord( true );
                    getContentPane().add(
                                          ta_desc,
                                          new GridBagConstraints( 2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 10, 5, 0, 10 ),
                                                                  0, 0 ) );
                }
                {
                    cb_all = new JCheckBox();
                    getContentPane().add(
                                          cb_all,
                                          new GridBagConstraints( 1, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0,
                                                                                                             0 ), 0, 0 ) );
                    cb_all.setText( Messages.getMessage( getLocale(), "$MD11136" ) );
                }
                {
                    pn_buttons = new JPanel();
                    FlowLayout pn_buttonsLayout = new FlowLayout();
                    pn_buttonsLayout.setAlignment( FlowLayout.LEFT );
                    getContentPane().add(
                                          pn_buttons,
                                          new GridBagConstraints( 0, 4, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    pn_buttons.setLayout( pn_buttonsLayout );
                    {
                        bt_ok = new JButton( Messages.getMessage( getLocale(), "$MD11140" ),
                                             IconRegistry.getIcon( "accept.png" ) );
                        bt_ok.setToolTipText( Messages.getMessage( getLocale(), "$MD11137" ) );
                        pn_buttons.add( bt_ok );
                        bt_ok.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent event ) {
                                if ( tf_name.getText().trim().length() == 0 ) {
                                    DialogFactory.openWarningDialog( "application", NewBookmarkDialog.this,
                                                                     Messages.getMessage( getLocale(), "$MD11138" ),
                                                                     Messages.getMessage( getLocale(), "$MD11139" ) );
                                    return;
                                }
                                ok = true;
                                dispose();
                            }
                        } );
                    }
                    {
                        bt_cancel = new JButton( Messages.getMessage( getLocale(), "$MD11141" ),
                                                 IconRegistry.getIcon( "cancel.png" ) );
                        bt_cancel.setToolTipText( Messages.getMessage( getLocale(), "$MD11142" ) );
                        pn_buttons.add( bt_cancel );
                        bt_cancel.addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent event ) {
                                ok = false;
                                dispose();
                            }
                        } );
                    }
                }
                {
                    pn_help = new JPanel();
                    FlowLayout pn_helpLayout = new FlowLayout();
                    pn_helpLayout.setAlignment( FlowLayout.RIGHT );
                    pn_help.setLayout( pn_helpLayout );
                    getContentPane().add(
                                          pn_help,
                                          new GridBagConstraints( 2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0,
                                                                  0 ) );
                    {
                        bt_help = new JButton( Messages.getMessage( getLocale(), "$MD11143" ), IconRegistry.getIcon( "help.png" ) );
                        bt_help.setToolTipText( Messages.getMessage( getLocale(), "$MD11144" ) );
                        pn_help.add( bt_help );
                    }
                }
                {
                    ta_description = new JTextArea( Messages.getMessage( getLocale(), "$MD11132" ) );
                    ta_description.setLineWrap( true );
                    ta_description.setWrapStyleWord( true );
                    getContentPane().add(
                                          ta_description,
                                          new GridBagConstraints( 0, 0, 1, 4, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                  GridBagConstraints.BOTH, new Insets( 0, 9, 9, 10 ),
                                                                  0, 0 ) );
                    ta_description.setBackground( pn_help.getBackground() );
                    ta_description.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                     "$MD11133" ) ) );
                    ta_description.setEditable( false );
                }
            }
            this.setSize( 456, 300 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * @return book mark name
     */
    public String getBookmarkName() {
        return tf_name.getText();
    }

    /**
     * 
     * @return description of book mark
     */
    public String getDescription() {
        return ta_desc.getText();
    }

    /**
     * 
     * @return true if book mark shall be used for all opened map models
     */
    public boolean isAllMapModels() {
        return cb_all.isSelected();
    }

    /**
     * 
     * @return true if user has clicked OK
     */
    public boolean isOK() {
        return ok;
    }

}
