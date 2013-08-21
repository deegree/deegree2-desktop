//$HeadURL: svn+ssh://lbuesching@svn.wald.intevation.de/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2012 by:
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
package org.deegree.desktop.settings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.deegree.desktop.settings.GraphicOptions;
import org.deegree.desktop.style.model.GraphicSymbol;
import org.deegree.desktop.config.GraphicDefinitionsType;
import org.deegree.desktop.config.GraphicDefinitionsType.Graphic;
import org.deegree.desktop.config.GraphicsType;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test cases for {@link GraphicOptions}
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class GraphicOptionsTest {

    private static final String nameTree = "Tree";

    private static final String nameCastle = "Castle";

    private static URL urlTree;

    private static URL urlCastle;

    @BeforeClass
    public static void initUrl()
                            throws MalformedURLException {
        urlTree = new URL( "file://home/deegree/desktop/tree.svg" );
        urlCastle = new URL( "file://home/deegree/desktop/castle.svg" );
    }

    @Test
    public void testGetSymbolWithSameUrl()
                            throws MalformedURLException, JAXBException {
        GraphicOptions go = initGraphicsOptions();
        GraphicSymbol symboldDefinition = go.getSymbolDefinition( nameTree, urlTree );
        assertEquals( nameTree, symboldDefinition.getName() );
        assertEquals( urlTree, symboldDefinition.getUrl() );
    }

    @Test
    public void testGetSymbolWithSameUrlUnkownUrl()
                            throws MalformedURLException, JAXBException {
        URL unknownUrl = new URL( "file://home/deegree/desktop/unkown.png" );
        GraphicOptions go = initGraphicsOptions();
        GraphicSymbol symboldDefinition = go.getSymbolDefinition( nameTree, unknownUrl );
        assertEquals( nameTree, symboldDefinition.getName() );
        assertEquals( urlTree, symboldDefinition.getUrl() );
        assertNotSame( unknownUrl, symboldDefinition.getUrl() );
    }

    @Test
    public void testGetSymbolWithSameUrlUnkownName()
                            throws MalformedURLException, JAXBException {
        GraphicOptions go = initGraphicsOptions();
        String unknownName = "unknown";
        GraphicSymbol symboldDefinition = go.getSymbolDefinition( unknownName, urlTree );
        assertNotSame( unknownName, symboldDefinition.getName() );
        assertEquals( nameTree, symboldDefinition.getName() );
        assertEquals( urlTree, symboldDefinition.getUrl() );
    }

    @Test
    public void testGetSymbolWithSameUrlUnkown()
                            throws MalformedURLException, JAXBException {
        String unknownName = "unknown";
        URL unknownUrl = new URL( "file://home/deegree/desktop/unkown.png" );
        GraphicOptions go = initGraphicsOptions();
        GraphicSymbol symboldDefinition = go.getSymbolDefinition( unknownName, unknownUrl );
        assertNull( symboldDefinition );
    }

    @Test
    public void testGetSymbolWithSameUrlUrlPreferred()
                            throws MalformedURLException, JAXBException {
        GraphicOptions go = initGraphicsOptions();
        GraphicSymbol symboldDefinition = go.getSymbolDefinition( nameCastle, urlTree );
        assertEquals( nameTree, symboldDefinition.getName() );
        assertEquals( urlTree, symboldDefinition.getUrl() );
    }

    private GraphicOptions initGraphicsOptions()
                            throws MalformedURLException {
        GraphicsType graphicsType = new GraphicsType();
        GraphicOptions graphicOptions = new GraphicOptions( graphicsType, true );
        GraphicDefinitionsType value = new GraphicDefinitionsType();
        List<Graphic> graphics = value.getGraphic();
        Graphic g1 = new Graphic();
        g1.setName( nameTree );
        g1.setFile( urlTree.toExternalForm() );
        graphics.add( g1 );
        Graphic g2 = new Graphic();
        g2.setName( nameCastle );
        g2.setFile( urlCastle.toExternalForm() );
        graphics.add( g2 );

        graphicsType.setSymbolDefinitions( value );
        return graphicOptions;
    }

}
