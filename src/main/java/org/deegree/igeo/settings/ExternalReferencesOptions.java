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

package org.deegree.igeo.settings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.igeo.config.ExternalReferencesType;
import org.deegree.igeo.config.ExternalReferencesType.Reference;

/**
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class ExternalReferencesOptions extends ElementSettings {

    private ExternalReferencesType externalReferences;

    private Map<String, String> references;

    /**
     * @param changeable
     */
    public ExternalReferencesOptions( ExternalReferencesType externalReferences, boolean changeable ) {
        super( changeable );
        this.externalReferences = externalReferences;
        this.references = new HashMap<String, String>();
        List<ExternalReferencesType.Reference> list = this.externalReferences.getReference();
        for ( Reference reference : list ) {
            references.put( reference.getExtension().toUpperCase(), reference.getProgram() );
        }

    }

    /**
     * 
     * @param extension
     * @return program assigned to passed extension
     */
    public String getProgram( String extension ) {
        return references.get( extension.toUpperCase() );
    }

    /**
     * adds a program to list of known references
     * 
     * @param extension
     * @param program
     */
    public void addProgram( String extension, String program ) {
        if ( changeable ) {
            references.put( extension, program );
            removeProgram( extension );
            Reference ref = new ExternalReferencesType.Reference();
            ref.setExtension( extension );
            ref.setProgram( program );
        }
    }

    /**
     * removes a program from list of known references
     * 
     * @param extension
     */
    public void removeProgram( String extension ) {
        if ( changeable ) {
            List<ExternalReferencesType.Reference> list = this.externalReferences.getReference();
            for ( Reference reference : list ) {
                if ( reference.getExtension().equalsIgnoreCase( extension ) ) {
                    list.remove( reference );
                    break;
                }
            }
        }
    }

}
