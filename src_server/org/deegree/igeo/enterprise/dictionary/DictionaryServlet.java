//$HeadURL: svn+ssh://developername@svn.wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 and
 lat/lon GmbH

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
package org.deegree.igeo.enterprise.dictionary;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.KVP2Map;
import org.deegree.framework.util.TimeTools;
import org.deegree.io.DBConnectionPool;
import org.deegree.io.DBPoolException;

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
public class DictionaryServlet extends HttpServlet {

    private static final long serialVersionUID = -6878316731046973766L;
    private static final ILogger LOG = LoggerFactory.getLogger( DictionaryServlet.class );

    private DictionaryResourceType dictRes;

    @Override
    public void init()
                            throws ServletException {
        super.init();

        String s = getInitParameter( "ConfigFile" );
        File file = new File( s );
        if ( !file.isAbsolute() ) {
            s = getServletContext().getRealPath( s );
            file = new File( s );
        }

        try {
            JAXBContext jc = JAXBContext.newInstance( "org.deegree.igeo.enterprise.dictionary" );
            Unmarshaller u = jc.createUnmarshaller();
            JAXBElement<?> o = (JAXBElement<?>) u.unmarshal( file.toURI().toURL() );
            this.dictRes = (DictionaryResourceType) o.getValue();
        } catch ( Exception e ) {
            e.printStackTrace();
            throw new ServletException( e );
        }
    }

    @Override
    public void doGet( HttpServletRequest req, HttpServletResponse resp )
                            throws ServletException, IOException {
        Map<String, String> param = KVP2Map.toMap( req );
        if ( param.get( "NAME" ) == null ) {
            readAll( resp );
        } else {
            read( param.get( "NAME" ), param.get( "CODESPACE" ), resp );
        }
    }

    /**
     * @param name
     * @param codeSpace
     * @param resp
     */
    private void read( String name, String codeSpace, HttpServletResponse resp ) {
        String table = null;
        List<DefinitionType> defs = dictRes.getDefinition();
        for ( DefinitionType definitionType : defs ) {
            if ( definitionType.getName().equals( name ) && definitionType.getCodeSpace().equals( codeSpace ) ) {
                table = definitionType.getTable();
                break;
            }
        }

        DBConnectionPool pool = DBConnectionPool.getInstance();
        JDBCConnectionType jdbc = dictRes.getConnection();
        Connection conn = null;
        PrintWriter pw = null;
        try {
            pw = resp.getWriter();            
            String cs = Charset.defaultCharset().displayName();
            resp.setCharacterEncoding( cs );
            resp.setContentType( "text/xml" );
            LOG.logDebug( "using charset: ", cs );
            pw.write( "<?xml version=\"1.0\" encoding=\"" + cs + "\"?><gml:Dictionary gml:id=\"ExternalCodeLists\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/gml http://schemas.opengis.net/gml/3.1.1/base/gml.xsd\">" );
            pw.write( "<gml:name codeSpace='" );
            pw.write( codeSpace );
            pw.write( "'>" );
            pw.write( name );
            pw.write( "</gml:name>" );

            conn = pool.acquireConnection( jdbc.driver, jdbc.getUrl(), jdbc.getUser(), jdbc.getPassword() );
            if ( table != null ) {
                handleTable( name, codeSpace, table, conn, pw );
            }

            pw.write( "</gml:Dictionary>" );
            pw.flush();
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            try {
                pool.releaseConnection( conn, jdbc.driver, jdbc.getUrl(), jdbc.getUser(), jdbc.getPassword() );
            } catch ( DBPoolException e ) {
                e.printStackTrace();
            }      
            pw.close();
        }
    }

    private void handleTable( String name, String codeSpace, String table, Connection conn, PrintWriter pw )
                            throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery( "select * from " + table );
        ResultSetMetaData rsmd = rs.getMetaData();
        int cnt = rsmd.getColumnCount();

        pw.write( "<gml:dictionaryEntry>" );
        pw.write( "<gml:DefinitionCollection gml:id='id_" + table + "'>" );
        pw.write( "<gml:name codeSpace='" );
        pw.write( codeSpace );
        pw.write( "'>" );
        pw.write( name );
        pw.write( "</gml:name>" );
        int k = 0;
        while ( rs.next() ) {
            pw.write( "<gml:dictionaryEntry>" );
            pw.write( "<gml:Definition gml:id='id_" + table + "_" + k++ + "'>" );
            for ( int i = 0; i < cnt; i++ ) {
                pw.write( "<gml:name codeSpace='urn:org:deegree:igeodesktop:" );
                pw.write( rsmd.getColumnName( i + 1 ).toLowerCase() );
                pw.write( "'>" );
                Object val = rs.getObject( i + 1 );
                if ( val instanceof Date ) {
                    pw.write( TimeTools.getISOFormattedTime( (Date) val ) );
                } else {
                    pw.write( val.toString() );
                }
                pw.write( "</gml:name>" );
            }
            pw.write( "</gml:Definition>" );
            pw.write( "</gml:dictionaryEntry>" );
        }
        rs.close();
        stmt.close();
        pw.write( "</gml:DefinitionCollection>" );
        pw.write( "</gml:dictionaryEntry>" );
    }

    /**
     * @param resp
     */
    private void readAll( HttpServletResponse resp ) {

        DBConnectionPool pool = DBConnectionPool.getInstance();
        JDBCConnectionType jdbc = dictRes.getConnection();
        Connection conn = null;
        PrintWriter pw = null;
        try {
            pw = resp.getWriter();
            pw.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?><gml:Dictionary gml:id=\"ExternalCodeLists\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/gml http://schemas.opengis.net/gml/3.1.1/base/gml.xsd\">" );
            pw.write( "<gml:name>deegree dictionary</gml:name>" );
            List<DefinitionType> defs = dictRes.getDefinition();
            conn = pool.acquireConnection( jdbc.getDriver().trim(), jdbc.getUrl().trim(), jdbc.getUser().trim(), jdbc.getPassword().trim() );
            for ( DefinitionType definitionType : defs ) {
                String table = definitionType.getTable();
                String name = definitionType.getName();
                String codeSpace = definitionType.getCodeSpace();                
                handleTable( name, codeSpace, table, conn, pw );
            }
            pw.write( "</gml:Dictionary>" );
            pw.flush();
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            try {
                pool.releaseConnection( conn, jdbc.getDriver(), jdbc.getUrl(), jdbc.getUser(), jdbc.getPassword() );
            } catch ( DBPoolException e ) {
                e.printStackTrace();
            }            
        }
        pw.close();
    }

    @Override
    public void doPost( HttpServletRequest req, HttpServletResponse resp )
                            throws ServletException, IOException {
        super.doGet( req, resp );
    }

}
