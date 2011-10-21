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

package org.deegree.igeo.views.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import org.deegree.igeo.views.IFooter;
import org.deegree.igeo.views.IFooterEntry;

/**
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class Footer extends JPanel implements IFooter {

    private static final long serialVersionUID = 1618885978118817467L;

    /**
     * 
     * 
     */
    public Footer() {
        FlowLayout fl = new FlowLayout();
        fl.setAlignment( FlowLayout.LEFT );
        fl.setVgap( 2 );
        this.setLayout( fl );
        this.setBorder( new LineBorder( Color.BLACK, 1 ) );
        this.setPreferredSize( new Dimension( 100, 25 ) );
        this.setMinimumSize( new Dimension( 100, 33 ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.views.Footer#removeEntry(java.lang.String)
     */
    public void removeEntry( String name ) {
        Component[] comps = getComponents();
        for ( int i = 0; i < comps.length; i++ ) {
            if ( name.equals( comps[i].getName() ) ) {
                this.remove( comps[i] );
                return;
            }
        }
    }

    private void adjustHeight( Component comp ) {
        if ( comp.getPreferredSize().height > this.getHeight() ) {
            this.setPreferredSize( new Dimension( 100, comp.getPreferredSize().height + 6 ) );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.views.Footer#addEntry(org.deegree.client.application.views.FooterEntry)
     */
    public void addEntry( IFooterEntry entry ) {
        add( (JComponent) entry );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.views.Footer#addEntry(org.deegree.client.application.views.FooterEntry,
     *      int)
     */
    public void addEntry( IFooterEntry entry, int index ) {
        add( (JComponent) entry, index );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.views.Footer#hasEntry(java.lang.String)
     */
    public boolean hasEntry( String name ) {
        Component[] comps = getComponents();
        for ( int i = 0; i < comps.length; i++ ) {
            if ( name.equals( comps[i].getName() ) ) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.views.Footer#repaintFooter()
     */
    public void repaintFooter() {
        this.repaint();
    }

    @Override
    public Component add( Component comp, int index ) {
        adjustHeight( comp );
        Dimension dim = comp.getMinimumSize();
        comp.setMinimumSize( new Dimension( dim.width, dim.height ) );
        dim = comp.getPreferredSize();
        comp.setPreferredSize( new Dimension( dim.width, dim.height ) );
        while ( index > getComponentCount() ) {
            index--;
        }
        return super.add( comp, index );
    }

    @Override
    public void add( Component comp, Object constraints, int index ) {
        adjustHeight( comp );
        Dimension dim = comp.getMinimumSize();
        comp.setMinimumSize( new Dimension( dim.width, dim.height ) );
        dim = comp.getPreferredSize();
        comp.setPreferredSize( new Dimension( dim.width, dim.height ) );
        while ( index > getComponentCount() ) {
            index--;
        }
        super.add( comp, constraints, index );
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Container#add(java.awt.Component, java.lang.Object)
     */
    @Override
    public void add( Component comp, Object constraints ) {
        adjustHeight( comp );
        Dimension dim = comp.getMinimumSize();
        comp.setMinimumSize( new Dimension( dim.width, dim.height ) );
        dim = comp.getPreferredSize();
        comp.setPreferredSize( new Dimension( dim.width, dim.height ) );
        super.add( comp, constraints );
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Container#add(java.awt.Component)
     */
    @Override
    public Component add( Component comp ) {
        adjustHeight( comp );
        Dimension dim = comp.getMinimumSize();
        comp.setMinimumSize( new Dimension( dim.width, dim.height ) );
        dim = comp.getPreferredSize();
        comp.setPreferredSize( new Dimension( dim.width, dim.height ) );
        return add( comp.getName(), comp );
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.Container#add(java.lang.String, java.awt.Component)
     */
    @Override
    public Component add( String name, Component comp ) {
        adjustHeight( comp );
        Dimension dim = comp.getMinimumSize();
        comp.setMinimumSize( new Dimension( dim.width, dim.height ) );
        dim = comp.getPreferredSize();
        comp.setPreferredSize( new Dimension( dim.width, dim.height ) );
        return super.add( name, comp );
    }

}
