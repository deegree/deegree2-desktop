//$HeadURL: svn+ssh://lbuesching@svn.wald.intevation.de/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 and
 lat/lon GmbH

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

package org.deegree.igeo.views.swing.style.component;

import static org.deegree.igeo.i18n.Messages.get;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.deegree.framework.xml.XMLTools;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.style.perform.SldIO;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * <code>SldFrame</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class SldFrame extends JFrame {

    private static final long serialVersionUID = -1141789314492561424L;

    private JTextArea textArea;

    private ApplicationContainer<?> appContainer;

    /**
     * @param ruleName
     *            the name of the rule
     * @param appContainer
     *            the applicationContainer
     */
    public SldFrame( String ruleName, ApplicationContainer<?> appContainer ) {
        this.appContainer = appContainer;
        setTitle( get( "$MD11063", ruleName ) );
        init();
    }

    /**
     * @param sld
     *            the sld to set
     */
    public void setSld( String sld ) {
        textArea.setText( sld );
    }

    private void init() {
        textArea = new JTextArea( 150, 100 );
        textArea.setWrapStyleWord( true );
        textArea.setLineWrap( true );
        JScrollPane scrollPane = new JScrollPane( textArea );
        textArea.setEditable( false );
        JPanel buttons = getButtonBar();
        buttons.setBorder( new EmptyBorder( 20, 10, 10, 10 ) );

        FormLayout fl = new FormLayout( "center:default", "default:grow(1.0), default" );
        DefaultFormBuilder builder = new DefaultFormBuilder( fl );

        builder.append( scrollPane );
        builder.nextLine();
        builder.append( buttons );

        getContentPane().add( builder.getPanel() );
    }

    private JPanel getButtonBar() {
        JButton prettyPrintBt = new JButton( get( "$MD11064" ) );
        prettyPrintBt.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    textArea.setText( XMLTools.getAsPrettyString( textArea.getText() ) );
                } catch ( Exception ex ) {
                    // nothing to do
                }
            }
        } );

        JButton saveSLDBt = new JButton( get( "$MD11065" ) );
        saveSLDBt.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                saveSld();
            }
        } );

        JButton exitBt = new JButton( get( "$MD11066" ) );
        exitBt.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setVisible( false );
            }
        } );

        ButtonBarBuilder bbBuilder = new ButtonBarBuilder();
        bbBuilder.addUnrelatedGap();
        bbBuilder.addGridded( prettyPrintBt );
        bbBuilder.addUnrelatedGap();
        bbBuilder.addGridded( saveSLDBt );
        bbBuilder.addUnrelatedGap();
        bbBuilder.addGridded( exitBt );
        bbBuilder.addUnrelatedGap();
        return bbBuilder.getPanel();
    }

    private void saveSld() {
        SldIO.exportSld( textArea.getText(), appContainer, this );
    }

}
