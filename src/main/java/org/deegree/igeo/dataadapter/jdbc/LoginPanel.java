//$HeadURL: svn+ssh://lbuesching@svn.wald.intevation.de/deegree/base/trunk/resources/eclipse/files_template.xml $
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
package org.deegree.igeo.dataadapter.jdbc;

import static org.deegree.igeo.i18n.Messages.get;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.deegree.igeo.views.swing.util.panels.PanelDialog;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
class LoginPanel extends JPanel {

    private static final long serialVersionUID = 2486059603184125006L;

    private JTextField userTF;

    private JPasswordField passwdTF;

    LoginPanel( String driver, String url, String user, String passwd, boolean isFirstAttempt ) {
        FormLayout fl = new FormLayout( "left:min, $ugap, fill:default:grow(1)", "100dlu, 20dlu, 20dlu" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );
        CellConstraints cc = new CellConstraints();

        userTF = new JTextField();
        userTF.setSize( new Dimension( 150, 20 ) );
        userTF.setPreferredSize( new Dimension( 150, 20 ) );
        userTF.setMinimumSize( new Dimension( 150, 20 ) );
        userTF.setMaximumSize( new Dimension( 150, 20 ) );
        userTF.setText( user );

        passwdTF = new JPasswordField();
        passwdTF.setSize( new Dimension( 150, 20 ) );
        passwdTF.setPreferredSize( new Dimension( 150, 20 ) );
        passwdTF.setMinimumSize( new Dimension( 150, 20 ) );
        passwdTF.setMaximumSize( new Dimension( 150, 20 ) );
        passwdTF.setText( passwd );

        JTextArea text = new JTextArea( isFirstAttempt ? get( "$MD11852", url, driver ) : get( "$MD11853", url, driver ) );
        text.setWrapStyleWord( true );
        text.setLineWrap( true );
        text.setEditable( false );

        builder.add( text, cc.xyw( 1, 1, 3 ) );
        builder.addLabel( get( "$MD11854" ), cc.xy( 1, 2 ) );
        builder.add( userTF, cc.xy( 3, 2 ) );
        builder.addLabel( get( "$MD11855" ), cc.xy( 1, 3 ) );
        builder.add( passwdTF, cc.xy( 3, 3 ) );

        this.add( builder.getPanel() );
    }

    @Override
    public String toString() {
        return get( "$MD11848" );
    }

    String getPasswd() {
        return passwdTF.getPassword() != null ? new String( passwdTF.getPassword() ) : null;
    }

    String getUser() {
        return userTF.getText();
    }

    public static void main( String[] args ) {
        LoginPanel panel = new LoginPanel( "eins", "zwei", null, null, true );
        PanelDialog pd = new PanelDialog( panel, true );
        pd.setVisible( true );
    }
}
