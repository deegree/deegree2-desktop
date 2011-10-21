package org.deegree.igeo.views.swing.gazetteer;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.commands.gazeetteer.GazetteerFindChildrenCommand;
import org.deegree.igeo.commands.gazeetteer.GazetteerFindItemsCommand;
import org.deegree.igeo.desktop.IGeoDesktop;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.modules.gazetteer.GazetteerItem;
import org.deegree.igeo.modules.gazetteer.HierarchyNode;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.swing.CursorRegistry;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class SelectPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = -4384308976508947467L;

    private static final ILogger LOG = LoggerFactory.getLogger( SelectPanel.class );

    private JLabel lbType;

    private JComboBox cbValues;

    private ApplicationContainer<Container> appCont;

    private String gazetteerAddr;

    private HierarchyNode node;

    private GazetteerPanel view;

    /**
     * 
     * @param appCont
     * @param gazetteerAddr
     * @param node
     * @param view
     */
    public SelectPanel( ApplicationContainer<Container> appCont, String gazetteerAddr, HierarchyNode node,
                        GazetteerPanel view ) {
        this.appCont = appCont;
        this.gazetteerAddr = gazetteerAddr;
        this.node = node;
        this.view = view;
        initGUI();
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            this.setPreferredSize( new Dimension( 317, 37 ) );
            thisLayout.rowWeights = new double[] { 0.1 };
            thisLayout.rowHeights = new int[] { 7 };
            thisLayout.columnWeights = new double[] { 0.0, 0.1 };
            thisLayout.columnWidths = new int[] { 125, 7 };
            this.setLayout( thisLayout );
            {
                lbType = new JLabel( node.getName() );
                this.add( lbType,
                          new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                  GridBagConstraints.HORIZONTAL, new Insets( 0, 9, 0, 0 ), 0, 0 ) );
            }
            {
                cbValues = new JComboBox();
                this.add( cbValues, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                            GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 10 ),
                                                            0, 0 ) );
                cbValues.addActionListener( new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {
                        Object o = cbValues.getSelectedItem();
                        if ( o instanceof GazetteerItem ) {
                            view.findChildren( (GazetteerItem) o, node );
                        }
                    }
                } );
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    void load() {
        GazetteerFindItemsCommand cmd = new GazetteerFindItemsCommand( appCont, gazetteerAddr, node.getFeatureType(),
                                                                       node.getProperties(), "*", false, true, false );
        ( (IGeoDesktop) appCont ).getMainWndow().setCursor( CursorRegistry.WAIT_CURSOR );
        try {
            // perform synchronously; listeners must not be informed because result
            // will just be used by this class
            appCont.getCommandProcessor().executeSychronously( cmd, false );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openErrorDialog( appCont.getViewPlatform(), getParent(), Messages.getMessage( getLocale(),
                                                                                                        "$MD11314" ),
                                           Messages.getMessage( getLocale(), "$MD11315", node.getName() ), e );
            return;
        } finally {
            ( (IGeoDesktop) appCont ).getMainWndow().setCursor( CursorRegistry.DEFAULT_CURSOR );
        }

        List items = (List) cmd.getResult();
        Object[] gi = items.toArray();
        Arrays.sort( gi );
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement( Messages.getMessage( getLocale(), "$MD11316" ) );
        for ( Object object : gi ) {
            model.addElement( object );
        }

        cbValues.setModel( model );
    }

    /**
     * loads all children of passed {@link GazetteerItem}
     * 
     * @param gazetteerItem
     */
    @SuppressWarnings("unchecked")
    void load( GazetteerItem gazetteerItem ) {
        String search = gazetteerItem.getGeographicIdentifier();

        GazetteerFindChildrenCommand cmd = new GazetteerFindChildrenCommand( appCont, gazetteerAddr,
                                                                             node.getFeatureType(),
                                                                             node.getProperties(), search );
        ( (IGeoDesktop) appCont ).getMainWndow().setCursor( CursorRegistry.WAIT_CURSOR );
        try {
            appCont.getCommandProcessor().executeSychronously( cmd, false );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
            DialogFactory.openErrorDialog( appCont.getViewPlatform(), getParent(), Messages.getMessage( getLocale(),
                                                                                                        "$MD11307" ),
                                           Messages.getMessage( getLocale(), "$MD11308",
                                                                gazetteerItem.getGeographicIdentifier() ), e );
            return;
        } finally {
            ( (IGeoDesktop) appCont ).getMainWndow().setCursor( CursorRegistry.DEFAULT_CURSOR );
        }
        List items = (List) cmd.getResult();
        Object[] gi = items.toArray();
        Arrays.sort( gi );
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement( Messages.getMessage( getLocale(), "$MD11309" ) );
        for ( Object object : gi ) {
            model.addElement( object );
        }

        cbValues.setModel( model );
    }
}
