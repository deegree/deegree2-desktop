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
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static org.deegree.igeo.i18n.Messages.get;

import java.awt.GridBagConstraints;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.deegree.igeo.views.swing.util.GuiUtils;
import org.deegree.igeo.views.swing.util.panels.ImagePanel;

/**
 * <code>SelectTemplatePanel</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class SelectTemplatePanel extends JPanel implements ListSelectionListener {

    private static final long serialVersionUID = -3285363102241229085L;

    /**
     * 
     */
    public JList list;

    private ImagePanel imagePanel;

    /**
     * 
     */
    public SelectTemplatePanel() {
        GridBagConstraints gb = GuiUtils.initPanel( this );

        imagePanel = new ImagePanel( 200, 200 );

        list = new JList();
        list.setSelectionMode( SINGLE_SELECTION );
        list.addListSelectionListener( this );
        JScrollPane sp = new JScrollPane( list );
        gb.fill = BOTH;
        add( GuiUtils.addWithSize( sp, 300, 300 ), gb );
        ++gb.gridx;
        add( GuiUtils.addWithSize( imagePanel, 200, 200 ), gb );
    }

    @Override
    public String toString() {
        return get( "$MD10352" );
    }

    public void valueChanged( ListSelectionEvent e ) {
        Template t = (Template) list.getSelectedValue();
        if ( t != null ) {
            imagePanel.setImage( t.image );
            imagePanel.updateUI();
        }
    }

    /**
     * <code>Template</code>
     * 
     * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    public static class Template {
        /**
         * 
         */
        public String title;

        /**
         * 
         */
        public URL location;

        /**
         * 
         */
        public BufferedImage image;

        @Override
        public String toString() {
            return title;
        }

    }

}
