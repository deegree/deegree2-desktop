//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
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
package org.deegree.desktop.views.swing.bookmark;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.deegree.desktop.modules.bookmarks.BookmarkModule;
import org.deegree.desktop.views.swing.DefaultFrame;
import org.deegree.desktop.config.ViewFormType;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class BookmarkFrame extends DefaultFrame {

    private static final long serialVersionUID = -55663600898682987L;

    private BookmarkPanel panel;
    

    @Override
    public void init( ViewFormType viewForm )
                            throws Exception {
        super.init( viewForm );
        setLayout( new BorderLayout() );
        panel = new BookmarkPanel( owner.getApplicationContainer(), (BookmarkModule<Container>)owner );
        add( panel, BorderLayout.CENTER );
        setAlwaysOnTop( true );
        setVisible( true );
        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {                
                super.windowClosed( e );
                ((BookmarkModule<Container>)owner).closed();
            } 
        });
    }

}
