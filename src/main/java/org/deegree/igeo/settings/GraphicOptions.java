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

import java.awt.Color;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deegree.framework.util.ColorUtils;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.xml.Marshallable;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.graphics.sld.SLDFactory;
import org.deegree.graphics.sld.Symbolizer;
import org.deegree.igeo.config.ColorListType;
import org.deegree.igeo.config.ColorSchemesType;
import org.deegree.igeo.config.DashArrayDefinitionsType;
import org.deegree.igeo.config.GraphicDefinitionsType;
import org.deegree.igeo.config.GraphicsType;
import org.deegree.igeo.config.PresetType;
import org.deegree.igeo.config.DashArrayDefinitionsType.DashArray;
import org.deegree.igeo.config.GraphicDefinitionsType.Graphic;
import org.deegree.igeo.style.model.GraphicSymbol;
import org.deegree.igeo.style.model.Preset;
import org.xml.sax.SAXException;

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
public class GraphicOptions extends ElementSettings {

    private GraphicsType graphicsType;

    private Map<String, GraphicSymbol> symbolDefinitions;

    private Map<String, GraphicSymbol> fillGraphicsDefinitions;

    private Map<String, org.deegree.igeo.style.model.DashArray> dashArrays;

    private Map<String, Preset> symbolizerPresets;

    private Map<String, List<ColorListEntry>> colorSchemes;

    /**
     * @param graphicsType
     * @param changeable
     */
    public GraphicOptions( GraphicsType graphicsType, boolean changeable ) {
        super( changeable );
        this.graphicsType = graphicsType;
    }

    private void createSymbolDefinitionsMap( List<Graphic> graphics )
                            throws MalformedURLException {
        if ( symbolDefinitions == null ) {
            symbolDefinitions = new HashMap<String, GraphicSymbol>( graphics.size() );
            for ( Graphic graphic : graphics ) {
                URL url = new URL( graphic.getFile() );
                GraphicSymbol symbol = new GraphicSymbol( graphic.getName(), url );
                symbolDefinitions.put( graphic.getName(), symbol );
            }
        }
    }

    private void createFillGraphicsDefinitionsMap( List<Graphic> graphics )
                            throws MalformedURLException {
        if ( fillGraphicsDefinitions == null ) {
            fillGraphicsDefinitions = new HashMap<String, GraphicSymbol>( graphics.size() );
            for ( Graphic graphic : graphics ) {
                URL url = new URL( graphic.getFile() );
                GraphicSymbol symbol = new GraphicSymbol( graphic.getName(), url );
                fillGraphicsDefinitions.put( graphic.getName(), symbol );
            }
        }
    }

    private void createDashArraysMapMap( List<DashArray> das ) {
        if ( dashArrays == null ) {
            dashArrays = new HashMap<String, org.deegree.igeo.style.model.DashArray>( das.size() );
            for ( DashArray dashArray : das ) {
                float[] array = StringTools.toArrayFloat( dashArray.getArray(), ", " );
                org.deegree.igeo.style.model.DashArray da = new org.deegree.igeo.style.model.DashArray(
                                                                                                                                dashArray.getName(),
                                                                                                                                array );
                dashArrays.put( dashArray.getName(), da );
            }
        }
    }

    private void createSymbolizerMap( List<PresetType> presets )
                            throws SAXException, IOException, XMLParsingException {
        if ( symbolizerPresets == null ) {
            symbolizerPresets = new HashMap<String, Preset>( presets.size() );
            for ( PresetType presetType : presets ) {
                String s = presetType.getPreset();
                Reader reader = new StringReader( s );
                XMLFragment xml = new XMLFragment( reader, XMLFragment.DEFAULT_URL );
                Symbolizer symbolizer = SLDFactory.createSymbolizer( xml.getRootElement() );
                symbolizerPresets.put( presetType.getName(), new Preset( presetType.getName(), symbolizer ) );
            }
        }
    }

    private void createColorSchemes( List<ColorSchemesType> cst ) {
        if ( colorSchemes == null ) {
            colorSchemes = new HashMap<String, List<ColorListEntry>>( cst.size() );
            for ( ColorSchemesType colorSchemesType : cst ) {
                List<org.deegree.igeo.config.ColorListType.ColorListEntry> list = colorSchemesType.getColorlist().getColorListEntry();
                List<ColorListEntry> entries = new ArrayList<ColorListEntry>( list.size() );
                for ( org.deegree.igeo.config.ColorListType.ColorListEntry colorListEntry : list ) {
                    Color color = Color.decode( colorListEntry.getColor() );
                    ColorListEntry entry = new ColorListEntry( color, colorListEntry.getPosition() );
                    entries.add( entry );
                }
                colorSchemes.put( colorSchemesType.getName(), entries );
            }
        }
    }

    /**
     * 
     * @return mapping of symbol names and file containing a symbol
     * @throws MalformedURLException
     */
    public Map<String, GraphicSymbol> getSymbolDefinitions()
                            throws MalformedURLException {
        createSymbolDefinitionsMap( graphicsType.getSymbolDefinitions().getGraphic() );
        return symbolDefinitions;
    }

    /**
     * 
     * @param name
     * @param file
     * @throws MalformedURLException
     */
    public void addSymbolDefinition( String name, String file )
                            throws MalformedURLException {
        if ( changeable ) {
            List<Graphic> graphics = graphicsType.getSymbolDefinitions().getGraphic();
            createSymbolDefinitionsMap( graphics );
            removeSymbolDefinition( name );
            Graphic graphic = new GraphicDefinitionsType.Graphic();
            graphic.setName( name );
            graphic.setFile( file );
            graphics.add( graphic );
            symbolDefinitions.put( name, new GraphicSymbol( name, new URL( file ) ) );
        }

    }
    
    
    public GraphicSymbol getSymboldefinition(String name) throws MalformedURLException{
        List<Graphic> graphics = graphicsType.getSymbolDefinitions().getGraphic();
        createSymbolDefinitionsMap( graphics );
        return symbolDefinitions.get( name );
    }

    /**
     * 
     * @param name
     * @throws MalformedURLException
     */
    public void removeSymbolDefinition( String name )
                            throws MalformedURLException {
        if ( changeable ) {
            List<Graphic> graphics = graphicsType.getSymbolDefinitions().getGraphic();
            createSymbolDefinitionsMap( graphics );
            if ( symbolDefinitions.containsKey( name ) ) {
                for ( Graphic graphic : graphics ) {
                    if ( graphic.getName().equals( name ) ) {
                        graphics.remove( graphic );
                        break;
                    }
                }
                symbolDefinitions.remove( name );
            }
        }
    }

    /**
     * 
     * @return mapping of fill pattern names and file containing a fill pattern
     * @throws MalformedURLException
     */
    public Map<String, GraphicSymbol> getFillGraphicDefinitions()
                            throws MalformedURLException {
        createFillGraphicsDefinitionsMap( graphicsType.getFillGraphicDefinitions().getGraphic() );
        return fillGraphicsDefinitions;
    }

    /**
     * 
     * @param name
     * @param file
     * @throws MalformedURLException
     * @throws MalformedURLException
     */
    public void addFillGraphicDefinition( String name, String file )
                            throws MalformedURLException {
        if ( changeable ) {
            List<Graphic> graphics = graphicsType.getFillGraphicDefinitions().getGraphic();
            createFillGraphicsDefinitionsMap( graphics );
            removeFillGraphicDefinition( name );
            Graphic graphic = new GraphicDefinitionsType.Graphic();
            graphic.setName( name );
            graphic.setFile( file );
            graphics.add( graphic );
            fillGraphicsDefinitions.put( name, new GraphicSymbol( name, new URL( file ) ) );

        }
    }

    /**
     * 
     * @param name
     * @throws MalformedURLException
     */
    public void removeFillGraphicDefinition( String name )
                            throws MalformedURLException {
        if ( changeable ) {
            List<Graphic> graphics = graphicsType.getSymbolDefinitions().getGraphic();
            createFillGraphicsDefinitionsMap( graphics );
            if ( fillGraphicsDefinitions.containsKey( name ) ) {
                for ( Graphic graphic : graphics ) {
                    if ( graphic.getName().equals( name ) ) {
                        graphics.remove( graphic );
                        break;
                    }
                }
                fillGraphicsDefinitions.remove( name );
            }
        }
    }

    /**
     * 
     * @return mapping of fill pattern names and file containing a fill pattern
     */
    public Map<String, org.deegree.igeo.style.model.DashArray> getDashArrays() {
        createDashArraysMapMap( graphicsType.getDashArrayDefinitions().getDashArray() );
        return dashArrays;
    }

    /**
     * 
     * @param name
     * @param dashArray
     */
    public void addDashArray( String name, org.deegree.igeo.style.model.DashArray dashArray ) {
        if ( changeable ) {
            List<DashArray> das = graphicsType.getDashArrayDefinitions().getDashArray();
            createDashArraysMapMap( das );
            removeDashArray( name );
            DashArray dash = new DashArrayDefinitionsType.DashArray();
            dash.setName( name );
            String s = StringTools.arrayToString( dashArray.getDashArray(), ' ' );
            dash.setArray( s );
            das.add( dash );
            dashArrays.put( name, dashArray );

        }
    }

    /**
     * 
     * @param name
     */
    public void removeDashArray( String name ) {
        if ( changeable ) {
            List<DashArray> das = graphicsType.getDashArrayDefinitions().getDashArray();
            createDashArraysMapMap( das );
            if ( dashArrays.containsKey( name ) ) {
                for ( DashArray dashArray : das ) {
                    if ( dashArray.getName().equals( name ) ) {
                        das.remove( dashArray );
                        break;
                    }
                }
                dashArrays.remove( name );
            }
        }
    }

    /**
     * 
     * @return mapping of symbolizer names and their definition
     */
    public Map<String, Preset> getSymbolizerPresets()
                            throws Exception {
        createSymbolizerMap( graphicsType.getSymbolizerPreset() );
        return symbolizerPresets;
    }
    
    /**
     * 
     * @param name
     * @param symbolizer
     */
    public void addSymbolizerPreset( String name, Symbolizer symbolizer )
                            throws Exception {
        if ( changeable ) {
            List<PresetType> presets = graphicsType.getSymbolizerPreset();
            createSymbolizerMap( presets );
            String s = ( (Marshallable) symbolizer ).exportAsXML();
            removeSymbolizerPreset( name );
            PresetType newType = new PresetType();
            newType.setName( name );
            newType.setPreset( s );
            presets.add( newType );
            symbolizerPresets.put( name, new Preset( name, symbolizer ) );
        }
    }

    /**
     * 
     * @param name
     */
    public void removeSymbolizerPreset( String name )
                            throws Exception {
        if ( changeable ) {
            List<PresetType> presets = graphicsType.getSymbolizerPreset();
            createSymbolizerMap( presets );
            for ( PresetType presetType : presets ) {
                if ( presetType.getName().equals( name ) ) {
                    presets.remove( presetType );
                    break;
                }
            }
            symbolizerPresets.remove( name );
        }
    }

    /**
     * 
     * @return map of available color schemes
     */
    public Map<String, List<ColorListEntry>> getColorSchemes() {
        createColorSchemes( graphicsType.getColorSchemes() );
        return colorSchemes;
    }

    /**
     * 
     * @param name
     * @param entries
     */
    public void addColorScheme( String name, List<ColorListEntry> entries ) {
        if ( changeable ) {
            List<ColorSchemesType> schemes = graphicsType.getColorSchemes();
            createColorSchemes( schemes );
            removeColorScheme( name );
            ColorListType cl = new ColorListType();
            for ( ColorListEntry colorListEntry : entries ) {
                org.deegree.igeo.config.ColorListType.ColorListEntry cle = new org.deegree.igeo.config.ColorListType.ColorListEntry();
                String clr = ColorUtils.toHexCode( "0x", colorListEntry.getColor() );
                cle.setColor( clr );
                cle.setPosition( colorListEntry.getPosition() );
                cl.getColorListEntry().add( cle );
            }
            ColorSchemesType clst = new ColorSchemesType();
            clst.setColorlist( cl );
            clst.setName( name );
            schemes.add( clst );            
            colorSchemes.put( name, entries );    
        }
    }

    /**
     * 
     * @param name
     */
    public void removeColorScheme( String name ) {
        if ( changeable ) {
            List<ColorSchemesType> schemes = graphicsType.getColorSchemes();
            createColorSchemes( schemes );
            for ( ColorSchemesType colorSchemesType : schemes ) {
                if ( colorSchemesType.getName().equals( name ) ) {
                    schemes.remove( colorSchemesType );
                    break;
                }
            }
            colorSchemes.remove( name );
        }
    }

    // TODO
    // add methods for preset styles and preset classifications

}
