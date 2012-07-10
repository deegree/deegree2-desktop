//$HeadURL: svn+ssh://developername@svn.wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
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

import java.util.ArrayList;
import java.util.List;

/**
 * The <code></code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author: Andreas Poth $
 * 
 * @version $Revision: $, $Date: $
 * 
 */
public class HelpPage {

    private String onlineResource;

    private List<String> keyword;

    private boolean mainPage;

    private String language;
    
    

    /**
     * @param onlineResource
     * @param keyword
     * @param mainPage
     * @param language
     */
    public HelpPage( String onlineResource, List<String> keyword, boolean mainPage, String language ) {
        this.onlineResource = onlineResource;
        this.keyword = keyword;
        this.mainPage = mainPage;
        this.language = language;
    }

    /**
     * Gets the value of the onlineResource property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getOnlineResource() {
        return onlineResource;
    }

    /**
     * Sets the value of the onlineResource property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setOnlineResource( String value ) {
        this.onlineResource = value;
    }

    /**
     * Gets the value of the keyword property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the keyword property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getKeyword().add( newItem );
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link String }
     * 
     * 
     */
    public List<String> getKeyword() {
        if ( keyword == null ) {
            keyword = new ArrayList<String>();
        }
        return this.keyword;
    }

    /**
     * Gets the value of the mainPage property.
     * 
     * @return possible object is {@link Boolean }
     * 
     */
    public boolean isMainPage() {
        return mainPage;
    }

    /**
     * Sets the value of the mainPage property.
     * 
     * @param value
     *            allowed object is {@link Boolean }
     * 
     */
    public void setMainPage( boolean value ) {
        this.mainPage = value;
    }

    /**
     * Gets the value of the language property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the value of the language property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setLanguage( String value ) {
        this.language = value;
    }

}
