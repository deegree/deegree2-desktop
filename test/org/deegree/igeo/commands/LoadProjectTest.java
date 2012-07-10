package org.deegree.igeo.commands;

import java.io.File;
import java.net.URL;

import junit.framework.TestCase;

import org.deegree.igeo.desktop.IGeoDesktop;
import org.deegree.kernel.Command;
import org.deegree.kernel.ProcessMonitor;

public class LoadProjectTest extends TestCase {

    public void testLoading()
                            throws Exception {
        
        IGeoDesktop g = new IGeoDesktop( new ProcessMonitor() {

            public void cancel()
                                    throws Exception {
            }

            public void init( String title, String message, int min, int max, Command command ) {
            }

            public boolean isCanceled() {
                return false;
            }

            public void setMaximumValue( int maximum ) {
            }

            public void setMinimumValue( int minimum ) {
            }

            public void updateStatus( String description ) {
                System.out.println(description);
            }

            public void updateStatus( int itemsDone, String itemDescription ) {
            }

            public void run() {
            }
            
        });
        File file = new File( "D:\\java\\projekte\\deegree2_client\\resources\\data\\testconfig6_new.xml" );
        URL projectUrl = file.toURI().toURL();
        g.init();
        g.loadProject( projectUrl, false );
        
    }
    
}
