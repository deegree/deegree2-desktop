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

package org.deegree.igeo.mapmodel;

import java.io.IOException;
import java.net.URL;

import org.deegree.framework.util.FileUtils;
import org.deegree.igeo.config.CertificateAIType;
import org.deegree.igeo.config.OnlineResourceType;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public class CertificateAI implements AuthenticationInformation {

    private String certificate;
    private URL url;
    private CertificateAIType certificateAI;

    /**
     * 
     * @param url
     * @throws IOException
     */
    public CertificateAI( CertificateAIType certificateAI ) throws IOException {
        OnlineResourceType ot = certificateAI.getCertificate();
        URL url = new URL( ot.getHref() );
        this.certificate = FileUtils.readTextFile( url ).toString();        
    }

    /**
     * 
     * @return certificate
     */
    public String getCertificate() {
        return this.certificate;
    }
    
    /**
     * 
     * @return URL of certificare
     */
    public URL getCertificateURL() {
        return url;
    }
    
    /**
     * 
     * @param url
     */
    public void setCertificateURL(URL url) {
        this.url = url;
        OnlineResourceType ort = new OnlineResourceType();
        ort.setHref( url.toExternalForm() );
        certificateAI.setCertificate( ort );
    }

}