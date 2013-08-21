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

package org.deegree.desktop.modules.remotecontrol;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.i18n.Messages;
import org.deegree.desktop.modules.ActionDescription;
import org.deegree.desktop.modules.DefaultModule;
import org.deegree.desktop.modules.IModule;
import org.deegree.desktop.modules.ModuleCapabilities;
import org.deegree.desktop.modules.ModuleException;
import org.deegree.desktop.modules.ActionDescription.ACTIONTYPE;
import org.deegree.desktop.views.DialogFactory;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.KVP2Map;
import org.deegree.desktop.config.ModuleType;
import org.deegree.desktop.config._ComponentPositionType;

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
public class WebServerModule<T> extends DefaultModule<T> {

    static final ILogger LOG = LoggerFactory.getLogger( WebServerModule.class );

    boolean run = false;

    int port;

    Map<String, Class<RequestHandler>> handler = new HashMap<String, Class<RequestHandler>>();

    Map<String, Map<String, String>> handlerParameters = new HashMap<String, Map<String, String>>();

    static {
        ActionDescription ad1 = new ActionDescription( "start", "starts internal server to enable remote control",
                                                       null, "starts internal server", ACTIONTYPE.PushButton, null,
                                                       null );
        ActionDescription ad2 = new ActionDescription( "stop", "stops internal server to avoid remote control", null,
                                                       "stops internal server", ACTIONTYPE.PushButton, null, null );
        moduleCapabilities = new ModuleCapabilities( ad1, ad2 );
    }

    @Override
    public void init( ModuleType moduleType, _ComponentPositionType componentPosition, ApplicationContainer<T> appCont,
                      IModule<T> parent, Map<String, String> initParams ) {

        super.init( moduleType, componentPosition, appCont, parent, initParams );
        String tmp = getInitParameter( "port" );
        if ( tmp == null ) {
            tmp = "80";
        }
        port = Integer.parseInt( tmp );
        Map<String, String> params = getInitParameters();
        for ( String function : params.keySet() ) {
            if ( !function.equals( "port" ) && !function.equals( "autostart" ) && !function.contains( "." ) ) {
                try {
                    handler.put( function, (Class<RequestHandler>) Class.forName( params.get( function ) ) );
                } catch ( ClassNotFoundException e ) {
                    LOG.logError( e.getMessage(), e );
                    throw new ModuleException( e.getMessage() );
                }
            }
        }
        for ( String paramName : params.keySet() ) {
            if ( paramName.contains( "." ) ) {
                String functionName = paramName.split( "[.]" )[0];
                String functionParam = paramName.split( "[.]" )[1];
                Map<String, String> paramMap = handlerParameters.get( functionName );
                if ( paramMap == null ) {
                    paramMap = new HashMap<String, String>();
                    handlerParameters.put( functionName, paramMap );
                }
                paramMap.put( functionParam.toUpperCase(), params.get( paramName ) );
            }
        }
        if ( "true".equalsIgnoreCase( params.get( "autostart" ) ) ) {
            try {
                start();
            } catch ( Throwable e ) {
                LOG.logError( e.getMessage(), e );
                throw new ModuleException( e.getMessage() );
            }
        }
    }

    /**
     * 
     * @return <code>true</code> if internal server is running
     */
    public boolean isServerRunning() {
        return run;
    }

    /**
     * starts the server
     */
    public void start() {

        run = true;

        new Thread() {

            @Override
            public void run() {
                ServerSocket socket;
                try {
                    socket = new ServerSocket( port );
                } catch ( IOException e ) {
                    e.printStackTrace();
                    return;
                }
                LOG.logInfo( "Waiting for client" );
                while ( run ) {
                    Socket clientSocket;
                    try {
                        clientSocket = socket.accept();
                    } catch ( IOException e ) {
                        e.printStackTrace();
                        return;
                    }
                    final HTTPWorker httpWorker = new HTTPWorker( clientSocket );
                    ( new Thread( httpWorker ) ).run();
                }
            }
        }.start();
        DialogFactory.openInformationDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11781" ),
                                             Messages.get( "$MD11782" ) );
    }

    /**
     * stops the server
     */
    public void stop() {
        run = false;
        DialogFactory.openInformationDialog( appContainer.getViewPlatform(), null, Messages.get( "$MD11783" ),
                                             Messages.get( "$MD11784" ) );
    }

    class HTTPWorker implements Runnable {

        private Socket clientSocket;

        public HTTPWorker( final Socket clientSocket ) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                InputStream is = this.clientSocket.getInputStream();

                int k = 0;
                int bytesToRead = 0;
                while ( ( bytesToRead = is.available() ) == 0 && k++ < 10 ) {
                    Thread.sleep( 10 );
                }

                if ( bytesToRead == 0 ) {
                    // throw new IllegalArgumentException( "No request data found" );
                    return;
                }

                final byte[] barray = new byte[bytesToRead];
                is.read( barray, 0, bytesToRead );
                final String client_data = new String( barray );

                final String regex = ".*GET (.*) HTTP.*";
                final Pattern pattern = Pattern.compile( regex, Pattern.MULTILINE | Pattern.DOTALL );
                final Matcher matcher = pattern.matcher( client_data );

                if ( matcher.matches() ) {
                    final String requestedPage = matcher.group( 1 );

                    final Map<String, String> request = KVP2Map.toMap( requestedPage );
                    if ( request.get( "ACTION" ) == null ) {
                        final String msg = "parameter 'action' must be set";
                        final String content = "<html><head><title>400</title></head><body><h1>400 - Bad Request</h1>"
                                               + msg + "</body></html>";
                        final String data = buildHeader( content.length(), "400 - Bad Request" );

                        clientSocket.getOutputStream().write( data.getBytes() );
                        clientSocket.getOutputStream().write( content.getBytes() );
                        LOG.logError( msg );
                    } else if ( handler.containsKey( request.get( "ACTION" ) ) ) {
                        Class<RequestHandler> clzz = handler.get( request.get( "ACTION" ) );
                        RequestHandler obj = clzz.newInstance();
                        obj.init( handlerParameters.get( request.get( "ACTION" ) ) );
                        final String contentString = obj.perform( request, appContainer );

                        final String httpHeader = buildHeader( contentString.length(), "200 OK" );
                        clientSocket.getOutputStream().write( httpHeader.getBytes() );
                        clientSocket.getOutputStream().write( contentString.getBytes() );
                    } else {
                        final String msg = "action: " + request.get( "ACTION" ) + " not known by iGeoDesktop";
                        final String content = "<html><head><title>404</title></head><body><h1>404 - Not found</h1>"
                                               + msg + "</body></html>";
                        final String data = buildHeader( content.length(), "404 Not Found" );

                        clientSocket.getOutputStream().write( data.getBytes() );
                        clientSocket.getOutputStream().write( content.getBytes() );
                        LOG.logError( msg );
                    }
                }
            } catch ( Exception e ) {
                LOG.logError( e.getMessage(), e );
                if ( "Application".equalsIgnoreCase( appContainer.getViewPlatform() ) ) {
                    DialogFactory.openWarningDialog( appContainer.getViewPlatform(), null, e.getMessage(), "error" );
                }
            }
        }

        private String buildHeader( final int stringLength, final String httpState ) {
            return "HTTP/1.1 " + httpState + "\n" + "Content-Length: " + stringLength + "\n"
                   + "Content-Type: text/html\n" + "Connection: close\n\n";
        }

    }

}
