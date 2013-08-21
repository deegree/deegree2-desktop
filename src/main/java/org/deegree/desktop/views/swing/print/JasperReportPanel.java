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

package org.deegree.desktop.views.swing.print;

import static java.util.prefs.Preferences.userNodeForPackage;
import static javax.imageio.ImageIO.read;
import static org.deegree.desktop.i18n.Messages.get;
import static org.deegree.desktop.views.swing.util.GenericFileChooser.showOpenDialog;
import static org.deegree.desktop.views.swing.util.GuiUtils.initPanel;
import static org.deegree.framework.log.LoggerFactory.getLogger;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.deegree.desktop.views.swing.util.DesktopFileFilter;
import org.deegree.desktop.views.swing.util.IconRegistry;
import org.deegree.desktop.views.swing.util.JIntField;
import org.deegree.desktop.views.swing.util.GenericFileChooser.FILECHOOSERTYPE;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.xml.NamespaceContext;
import org.deegree.framework.xml.XMLFragment;
import org.deegree.framework.xml.XMLParsingException;
import org.deegree.framework.xml.XMLTools;
import org.deegree.ogcbase.CommonNamespaces;
import org.w3c.dom.Element;

/**
 * <code>JasperReportPanel</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class JasperReportPanel extends JPanel {

    private static final long serialVersionUID = 5560002519672051940L;

    static final ILogger LOG = getLogger( JasperReportPanel.class );

    private TreeMap<String, JTextField> strings = new TreeMap<String, JTextField>();

    private TreeMap<String, JCheckBox> booleans = new TreeMap<String, JCheckBox>();

    private TreeMap<String, JIntField> ints = new TreeMap<String, JIntField>();

    TreeMap<String, BufferedImage> images = new TreeMap<String, BufferedImage>();

    /**
     * 
     */
    public XMLFragment report;

    /**
     * @param doc
     * @throws XMLParsingException
     */
    public void init( XMLFragment doc )
                            throws XMLParsingException {
        report = doc;
        // first destroy old components
        strings.clear();
        booleans.clear();
        removeAll();

        NamespaceContext nsc = CommonNamespaces.getNamespaceContext();
        nsc.addNamespace( "jasper", URI.create( "http://jasperreports.sourceforge.net/jasperreports" ) );
        // old jasper format
        List<Element> tmp = XMLTools.getElements( doc.getRootElement(), "//parameter", null );
        // current jasper format
        List<Element> params = XMLTools.getElements( doc.getRootElement(), "//jasper:parameter", nsc );
        for ( Element element : tmp ) {
            params.add( element );
        }

        GridBagConstraints gb = initPanel( this );
        gb.anchor = GridBagConstraints.WEST;
        for ( Element param : params ) {
            final String name = param.getAttribute( "name" );
            String type = param.getAttribute( "class" );
            if ( name.equals( "MAP" ) || name.equals( "LEGEND" ) || name.equals( "SCALE" ) ) {
                continue;
            }
            if ( type.equals( "java.lang.String" ) ) {
                JTextField field = new JTextField( 20 );
                strings.put( name, field );
                add( new JLabel( name + ":" ), gb );
                ++gb.gridx;
                gb.fill = GridBagConstraints.BOTH;
                add( field, gb );
                gb.fill = GridBagConstraints.NONE;
                ++gb.gridy;
                gb.gridx = 0;
                continue;
            }
            if ( type.equals( "java.lang.Boolean" ) ) {
                JCheckBox checkbox = new JCheckBox( name );
                booleans.put( name, checkbox );
                gb.gridwidth = 2;
                add( checkbox, gb );
                gb.gridwidth = 1;
                ++gb.gridy;
                continue;
            }
            if ( type.equals( "java.lang.Integer" ) ) {
                JIntField field = new JIntField();
                field.setColumns( 8 );
                ints.put( name, field );
                add( new JLabel( name + ":" ), gb );
                ++gb.gridx;
                gb.fill = GridBagConstraints.BOTH;
                add( field, gb );
                gb.fill = GridBagConstraints.NONE;
                ++gb.gridy;
                gb.gridx = 0;
                continue;
            }
            if ( type.equals( "java.awt.Image" ) ) {
                JPanel panel = new JPanel();
                GridBagConstraints gb2 = initPanel( panel );
                gb2.anchor = GridBagConstraints.WEST;
                final JLabel fileLabel = new JLabel( get( "$DI10023" ) );
                panel.add( fileLabel, gb2 );
                ++gb2.gridx;
                Icon icon = IconRegistry.getIcon( "/org/deegree/desktop/views/images/open.gif" );
                final JButton browse = new JButton( get( "$DI10021" ), icon );
                panel.add( browse, gb2 );

                browse.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        File f = showOpenDialog( FILECHOOSERTYPE.image, null, JasperReportPanel.this,
                                                 userNodeForPackage( JasperReportPanel.class ), "lastImageReportPanel",
                                                 DesktopFileFilter.IMAGES );
                        if ( f != null ) {
                            try {
                                images.put( name, read( f ) );
                                fileLabel.setText( f.getName() );
                            } catch ( IOException e1 ) {
                                fileLabel.setText( get( "$MD10387" ) );
                                LOG.logDebug( "Stack trace:", e1 );
                            }
                        }
                    }
                } );

                add( new JLabel( name + ":" ), gb );
                ++gb.gridx;
                add( panel, gb );
                ++gb.gridy;
                gb.gridx = 0;
                continue;
            }
            add( new JLabel( "Unsupported parameter type " + type ), gb );
            ++gb.gridy;
        }
    }

    @Override
    public String toString() {
        return get( "$MD10349" );
    }

    /**
     * @param map
     */
    public void fillParameterMap( Map<String, Object> map ) {
        for ( String key : strings.keySet() ) {
            map.put( key, strings.get( key ).getText() );
        }
        for ( String key : booleans.keySet() ) {
            map.put( key, booleans.get( key ).isSelected() );
        }
        for ( String key : ints.keySet() ) {
            map.put( key, ints.get( key ).getInt() );
        }
        for ( String key : images.keySet() ) {
            map.put( key, images.get( key ) );
        }
    }

}
