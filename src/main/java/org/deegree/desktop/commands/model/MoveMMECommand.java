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
package org.deegree.desktop.commands.model;

import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.mapmodel.MapModel;
import org.deegree.desktop.mapmodel.MapModelEntry;
import org.deegree.desktop.mapmodel.MapModelException;
import org.deegree.kernel.AbstractCommand;
import org.deegree.kernel.Command;

/**
 * {@link Command} implementation for moving a {@link MapModelEntry} in the {@link MapModel}s layer tree. At the moment
 * just moving to the top is implemented.
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class MoveMMECommand extends AbstractCommand {

    private static final QualifiedName name = new QualifiedName( "Move Map Model Entry" );

    public enum MOVE_TYPE {
        UP, DOWN, TOP, BOTTOM
    };

    // private ApplicationContainer<?> appCont;

    private MOVE_TYPE moveType;

    private MapModel mapModel;

    private MapModelEntry mme;

    /**
     * 
     * @param appCont
     * @param moveType
     */
    public MoveMMECommand( ApplicationContainer<?> appCont, MOVE_TYPE moveType ) {
        // this.appCont = appCont;
        this.moveType = moveType;
        mapModel = appCont.getMapModel( null );
        List<MapModelEntry> list = mapModel.getMapModelEntriesSelectedForAction( MapModel.SELECTION_ACTION );
        if ( list.size() == 0 ) {
            throw new MapModelException( "no layer selected" );
        } else if ( list.size() > 1 ) {
            throw new MapModelException( "just one layer can be moved" );
        } else {
            mme = list.get( 0 );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#execute()
     */
    public void execute()
                            throws Exception {
        switch ( moveType ) {
        case TOP: {
            mapModel.move( mme, mapModel.getLayerGroups().get( 0 ), null, false );
            break;
        }
        case BOTTOM: {
            break;
        }
        case UP: {
            break;
        }
        case DOWN: {
            break;
        }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getName()
     */
    public QualifiedName getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.kernel.Command#getResult()
     */
    public Object getResult() {
        // TODO Auto-generated method stub
        return null;
    }

}
