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

package org.deegree.desktop.style.perform;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.graphics.legend.LegendElement;
import org.deegree.graphics.legend.LegendException;
import org.deegree.graphics.legend.LegendFactory;
import org.deegree.graphics.sld.CssParameter;
import org.deegree.graphics.sld.ParameterValueType;
import org.deegree.graphics.sld.Rule;
import org.deegree.graphics.sld.StyleFactory;
import org.deegree.model.filterencoding.Expression;
import org.deegree.model.filterencoding.PropertyName;

/**
 * <code>AbstractVisualPropertyPerformer</code>
 * 
 * @author <a href="mailto:buesching@lat-lon.de">Lyn Buesching</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public abstract class AbstractVisualPropertyPerformer implements VisualPropertyPerformer {

    private static final ILogger LOG = LoggerFactory.getLogger( SymbolVisualPropertyPerformer.class );

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.perform.VisualPropertyPerformer#getRules()
     */
    public List<Rule> getRules( boolean isInPixel ) {
        List<Rule> rules = new ArrayList<Rule>();
        rules.add( StyleFactory.createRule( getSymbolizer( isInPixel ) ) );
        return rules;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.swing.style.Style#getAsImage()
     */
    public BufferedImage getAsImage() {
        BufferedImage img = null;

        List<Rule> rules = getRules( true );
        Rule[] rs = rules.toArray( new Rule[rules.size()] );
        org.deegree.graphics.sld.AbstractStyle as = StyleFactory.createStyle( "", "", "", "", rs );

        LegendFactory lf = new LegendFactory();
        LegendElement le = null;
        try {
            int symbolSize = getSize();
            le = lf.createLegendElement( as, symbolSize, symbolSize, null );
            if ( le != null ) {
                img = le.exportAsImage( "image/gif" );
            }
        } catch ( LegendException e ) {
            LOG.logError( "Could not create SymbolVisualProperty as image", e );
        }
        return img;
    }

    /**
     * @param name
     *            name in sld
     * @param dynamic
     *            must not be null
     * @return
     */
    protected CssParameter getAsCssParameter( String name, QualifiedName dynamic ) {
        PropertyName pn = new PropertyName( dynamic );
        ParameterValueType pvt = StyleFactory.createParameterValueType( new Expression[] { pn } );
        return new CssParameter( name, pvt );
    }

    abstract int getSize();
}
