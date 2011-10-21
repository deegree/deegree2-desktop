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

package org.deegree.igeo.views.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.utils.SwingUtils;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLTools;
import org.deegree.igeo.i18n.Messages;
import org.deegree.model.filterencoding.Filter;
import org.w3c.dom.Element;

/**
 * <code>QueryPanel</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
class FilterPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 8814363516742516048L;

    private static final ILogger LOG = LoggerFactory.getLogger( FilterPanel.class );

    private JTextArea query;

    private DefaultFilterPanel filter;

    FilterPanel( DefaultFilterPanel filter ) {
        this.filter = filter;
        this.query = new JTextArea();
        GridBagConstraints gbc = SwingUtils.initPanel( this );

        this.query.setEditable( false );

        JScrollPane queryScroll = new JScrollPane( this.query );
        queryScroll.setPreferredSize( new Dimension( 300, 350 ) );
        queryScroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
        queryScroll.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );

        JPanel btPanel = new JPanel();
        JButton queryButton = new JButton( Messages.getMessage( Locale.getDefault(), "$MD10190" ) );
        queryButton.addActionListener( this );
        btPanel.add( queryButton );

        // add components to query panel
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        add( queryScroll, gbc );
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        ++gbc.gridy;
        add( btPanel, gbc );

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed( ActionEvent event ) {
        Filter f = (Filter) this.filter.getFilter();
        String text = Messages.getMessage( Locale.getDefault(), "$MD10191" );
        if ( f != null ) {
            try {
                Element e = XMLTools.getStringFragmentAsElement( f.to110XML().toString() );
                XMLFragment doc = new XMLFragment( e );
                text = doc.getAsPrettyString();
            } catch ( Exception e ) {
                text = Messages.getMessage( Locale.getDefault(), "$MD10192" );
                LOG.logError( Messages.getMessage( Locale.getDefault(), "$DG10067" ) );
            }
        }
        this.query.setText( text );
    }
}
