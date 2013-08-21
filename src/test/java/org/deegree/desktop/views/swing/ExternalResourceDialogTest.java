//$$HeadURL: svn+ssh://lbuesching@svn.wald.intevation.de/deegree/base/trunk/resources/eclipse/files_template.xml $$
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

package org.deegree.desktop.views.swing;

import java.net.MalformedURLException;

import org.deegree.desktop.views.swing.ExternalResourceDialog;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test of {@link ExternalResourceDialog#isURLValid()}
 * 
 * @author <a href="mailto:goltz@lat-lon.de">Lyn Goltz</a>
 * @author last edited by: $Author: lyn $
 * 
 * @version $Revision: $, $Date: $
 */
public class ExternalResourceDialogTest {

    @Test
    public void testURLValiditionOfValidURL()
                            throws MalformedURLException {
        // arrange / buld
        String validUrl = "http://www.deegree.org";
        ExternalResourceDialog dia = arrangeExternalResource( validUrl );
        // act / operate
        boolean isValid = acrUrlValidation( validUrl, dia );
        // assert / check
        Assert.assertTrue( isValid );
    }

    // TODO: Fix test
    // @Test
    // public void testURLValiditionOfInvalidURLWithSeperator()
    // throws MalformedURLException {
    // String invalidUrl = "http: deegree";
    // ExternalResourceDialog dia = arrangeExternalResource( invalidUrl );
    //
    // boolean isValid = acrUrlValidation( invalidUrl, dia );
    //
    // Assert.assertFalse( isValid );
    // }

    @Test
    public void testURLValiditionOfInvalidURLWithoutSeperator()
                            throws MalformedURLException {
        String invalidUrl = "http deegree";
        ExternalResourceDialog dia = arrangeExternalResource( invalidUrl );

        boolean isValid = acrUrlValidation( invalidUrl, dia );

        Assert.assertFalse( isValid );
    }

    @Test
    public void testURLValiditionOfNullUrl()
                            throws MalformedURLException {
        String invalidUrl = null;
        ExternalResourceDialog dia = arrangeExternalResource( invalidUrl );

        boolean isValid = acrUrlValidation( invalidUrl, dia );

        Assert.assertFalse( isValid );
    }

    private ExternalResourceDialog arrangeExternalResource( String urlTOValidate ) {
        ExternalResourceDialog dia = Mockito.mock( ExternalResourceDialog.class );
        Mockito.when( dia.isURLValid( urlTOValidate ) ).thenCallRealMethod();
        return dia;
    }

    private boolean acrUrlValidation( String urlToValidate, ExternalResourceDialog externalResourceDialog ) {
        return externalResourceDialog.isURLValid( urlToValidate );
    }
}
