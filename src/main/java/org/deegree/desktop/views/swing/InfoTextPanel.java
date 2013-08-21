package org.deegree.desktop.views.swing;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import org.deegree.desktop.ApplicationContainer;
import org.deegree.desktop.config.ViewFormType;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class InfoTextPanel extends DefaultPanel {

    private static final long serialVersionUID = 3503412245497165303L;

    private JScrollPane spInfoText;

    private JEditorPane epInfoText;

    private ApplicationContainer<Container> appCont;

    private void initGUI() {
        try {
            BorderLayout thisLayout = new BorderLayout();
            this.setLayout( thisLayout );
            this.setPreferredSize( new java.awt.Dimension( 665, 555 ) );
            {
                spInfoText = new JScrollPane();
                this.add( spInfoText, BorderLayout.CENTER );
                {
                    epInfoText = new JEditorPane();
                    spInfoText.setViewportView( epInfoText );
                    epInfoText.setContentType( "text/html" );
                    String s = owner.getInitParameter( "page" );
                    epInfoText.setPage( appCont.resolve( s ) );
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.views.IView#init(org.deegree.igeo.config.ViewFormType)
     */
    public void init( ViewFormType viewForm )
                            throws Exception {
        this.appCont = owner.getApplicationContainer();
        initGUI();
    }

}
