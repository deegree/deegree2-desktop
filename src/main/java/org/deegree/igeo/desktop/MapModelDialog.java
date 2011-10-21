package org.deegree.igeo.desktop;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.ListModel;

import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.MapModel;
import org.deegree.igeo.views.swing.util.IconRegistry;

/**
 * 
 * Dialog for selecting/opening an available map model
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * 
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 * 
 */
public class MapModelDialog extends javax.swing.JDialog {

    private static final long serialVersionUID = 4811300611179747978L;

    private JPanel pnDesc;

    private JPanel pnModels;

    private JTextPane tpDesp;

    private JList lstMapModels;

    private JButton btCancel;

    private JButton btOK;

    private JPanel pnButtons;

    private MapModel[] mapModels;

    /**
     * 
     * @param parent
     * @param mapModels
     */
    public MapModelDialog( Container parent, MapModel[] mapModels ) {
        this.mapModels = mapModels;
        initGUI();        
        setModal( true );
        setVisible( true );
        Rectangle rect = parent.getBounds();
        setLocation( rect.x + rect.width / 2 - getWidth() / 2, rect.y + rect.height / 2 - getHeight() / 2 );
    }

    private void initGUI() {
        try {
            GridBagLayout thisLayout = new GridBagLayout();
            getContentPane().setLayout( thisLayout );
            {
                pnDesc = new JPanel();
                BorderLayout pnDescLayout = new BorderLayout();
                pnDesc.setLayout( pnDescLayout );
                getContentPane().add(
                                      pnDesc,
                                      new GridBagConstraints( 0, 0, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER,
                                                              GridBagConstraints.BOTH, new Insets( 0, 0, 0, 5 ), 0, 0 ) );
                pnDesc.setBorder( BorderFactory.createTitledBorder( "map model selection" ) );
                {
                    tpDesp = new JTextPane();
                    pnDesc.add( tpDesp, BorderLayout.CENTER );
                    tpDesp.setText( Messages.get( "$DI10041" ) );
                    tpDesp.setBackground( new Color( 212, 208, 200 ) );
                    tpDesp.setEditable( false );
                }
            }
            {
                pnModels = new JPanel();
                BorderLayout pnModelsLayout = new BorderLayout();
                pnModels.setLayout( pnModelsLayout );
                getContentPane().add(
                                      pnModels,
                                      new GridBagConstraints( 1, 0, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER,
                                                              GridBagConstraints.BOTH, new Insets( 0, 5, 0, 0 ), 0, 0 ) );
                pnModels.setBorder( BorderFactory.createTitledBorder( "available map models" ) );
                {
                    ListModel lstMapModelsModel = new DefaultComboBoxModel( mapModels );
                    lstMapModels = new JList();
                    pnModels.add( lstMapModels, BorderLayout.CENTER );
                    lstMapModels.setModel( lstMapModelsModel );
                }
            }
            {
                pnButtons = new JPanel();
                FlowLayout pnButtonsLayout = new FlowLayout();
                pnButtonsLayout.setAlignment( FlowLayout.LEFT );
                getContentPane().add(
                                      pnButtons,
                                      new GridBagConstraints( 0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                              GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                pnButtons.setLayout( pnButtonsLayout );
                {
                    btOK = new JButton( "add map model", IconRegistry.getIcon( "accept.png" ) );
                    pnButtons.add( btOK );
                    btOK.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            dispose();
                        }

                    } );
                }
                {
                    btCancel = new JButton( "cancel", IconRegistry.getIcon( "cancel.png" ) );
                    pnButtons.add( btCancel );
                    btCancel.addActionListener( new ActionListener() {

                        public void actionPerformed( ActionEvent e ) {
                            lstMapModels.removeSelectionInterval( 0, mapModels.length - 1 );
                            dispose();
                        }

                    } );
                }
            }
            thisLayout.rowWeights = new double[] { 0.0, 0.0, 0.1 };
            thisLayout.rowHeights = new int[] { 127, 124, 7 };
            thisLayout.columnWeights = new double[] { 0.0, 0.1 };
            thisLayout.columnWidths = new int[] { 178, 7 };
            this.setSize( 414, 317 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * @return
     */
    public MapModel getMapModel() {
        return (MapModel) lstMapModels.getSelectedValue();
    }

}
