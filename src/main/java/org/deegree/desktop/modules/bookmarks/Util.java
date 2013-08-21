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
package org.deegree.desktop.modules.bookmarks;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.deegree.desktop.modules.bookmarks.BookmarkModule.BookmarkEntry;
import org.deegree.model.Identifier;
import org.deegree.model.crs.CRSFactory;
import org.deegree.model.crs.CoordinateSystem;
import org.deegree.model.crs.UnknownCRSException;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;

/**
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class Util {
    
    /**
     * 
     * @param bookmarks
     * @param file
     * @throws Exception
     */
    public static void saveBookmarks(List<BookmarkEntry> bookmarks, File file) throws Exception {
        BookmarkList bml = new BookmarkList();
        List<BookmarkType> list = bml.getBookmark();
        for ( BookmarkEntry bookmarkEntry : bookmarks ) {
            BookmarkType bmt = new BookmarkType();
            bmt.setAllMapModels( bookmarkEntry.allMapModels );
            bmt.setDescription( bookmarkEntry.description );
            bmt.setName( bookmarkEntry.name );
            bmt.setEnvelope( Util.convertEnvelope( bookmarkEntry.env ) );
            if ( !bookmarkEntry.allMapModels ) {
                IdentifierType idt = new IdentifierType();
                if ( bookmarkEntry.mapModel.getNamespace() != null ) {
                    idt.setNamespace( bookmarkEntry.mapModel.getNamespace().toASCIIString() );
                }
                idt.setValue( bookmarkEntry.mapModel.getValue() );
                bmt.setMapModelId( idt );
            }
            list.add( bmt );
        }
        JAXBContext jc = JAXBContext.newInstance( "org.deegree.igeo.modules.bookmarks" );
        Marshaller m = jc.createMarshaller();
        FileOutputStream fos = new FileOutputStream( file );
        m.marshal( bml, fos );
        fos.flush();
        fos.close();
    }
    
    /**
     * 
     * @param file
     * @return
     * @throws Exception
     */
    public static  List<BookmarkEntry> loadBookmarks(File file) throws Exception {
        List<BookmarkEntry> bookmarks = new ArrayList<BookmarkEntry>();
        if ( file.exists() ) {
            try {
                JAXBContext jc = JAXBContext.newInstance( "org.deegree.igeo.modules.bookmarks" );
                Unmarshaller u = jc.createUnmarshaller();
                BookmarkList bml = (BookmarkList) u.unmarshal( file );
                List<BookmarkType> list = bml.getBookmark();
                for ( BookmarkType bookmarkType : list ) {
                    String name = bookmarkType.getName();
                    String desc = bookmarkType.getDescription();
                    boolean allMapModels = bookmarkType.isAllMapModels();
                    Identifier mapModelId = null;
                    if ( bookmarkType.getMapModelId() != null ) {
                        String nsp = bookmarkType.getMapModelId().getNamespace();
                        String val = bookmarkType.getMapModelId().getValue();
                        URI nspURI = null;
                        if ( nsp != null ) {
                            nspURI = URI.create( nsp );
                        }
                        mapModelId = new Identifier( val, nspURI );
                    }
                    Envelope env = Util.convertEnvelope( bookmarkType.getEnvelope() );
                    bookmarks.add( new BookmarkEntry( mapModelId, name, desc, env, allMapModels ) );
                }
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
        return bookmarks;
    }
    
    /**
     * 
     * @param envelope
     * @return
     */
    static Envelope convertEnvelope( EnvelopeType envelope ) {
        CoordinateSystem cs = null;
        try {
            cs = CRSFactory.create( envelope.getCrs() );
        } catch ( UnknownCRSException e ) {
            // fatal exception should never happen
            e.printStackTrace();
            throw new RuntimeException( e );
        }
        return GeometryFactory.createEnvelope( envelope.getMinx(), envelope.getMiny(), envelope.getMaxx(),
                                               envelope.getMaxy(), cs );

    }

    /**
     * 
     * @param envelope
     * @return
     */
    static EnvelopeType convertEnvelope( Envelope envelope ) {
        EnvelopeType value = new EnvelopeType();
        if ( envelope.getCoordinateSystem() != null ) {
            value.setCrs( envelope.getCoordinateSystem().getPrefixedName() );
        }
        value.setMinx( envelope.getMin().getX() );
        value.setMiny( envelope.getMin().getY() );
        value.setMaxx( envelope.getMax().getX() );
        value.setMaxy( envelope.getMax().getY() );
        return value;
    }

}
