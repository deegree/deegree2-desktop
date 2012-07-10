/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2010 by:
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
package org.deegree.igeo.views.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.StringTools;
import org.deegree.framework.utils.ExternalPrograms;
import org.deegree.igeo.config.ExternalResourceType;
import org.deegree.igeo.config.OnlineResourceType;
import org.deegree.igeo.config.ViewFormType;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.MapModelCollection;
import org.deegree.igeo.views.DialogFactory;
import org.deegree.igeo.views.swing.util.IconRegistry;
import org.deegree.model.spatialschema.Envelope;
import org.deegree.model.spatialschema.GeometryFactory;

/**
 * 
 * TODO add class documentation here
 * 
 * @author <a href="mailto:name@deegree.org">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class MapModelCollectionInfoDialog extends DefaultDialog {

    private static final long serialVersionUID = -4390542851480766682L;

    private static final ILogger LOG = LoggerFactory.getLogger( MapModelCollectionInfoDialog.class );

    private JTabbedPane tpTabPane;

    private JPanel pnFileName;

    private JTextField tfFile;

    private JTextField tfMaxy;

    private JTextField tfMaxx;

    private JTextField tfMiny;

    private JTextField tfMinx;

    private JLabel lbMaxx;

    private JLabel lbMaxy;

    private JLabel lbMiny;

    private JLabel lbMinx;

    private JPanel pnExtent;

    private JTextField tfCRS;

    private JPanel pnCRS;

    private JButton btHelp;

    private JPanel pnHelp;

    private JButton btCancel;

    private JButton btTake;

    private JPanel pnInfoButtons;

    private JTextPane tpDescription;

    private JScrollPane scDescription;

    private JPanel pnDescription;

    private JTextField tfName;

    private JPanel pnName;

    private JTable tabExternalResources;

    private JButton btRemoveExtRes;

    private JButton btAddExtRes;

    private JPanel pnExtResButtons;

    private JPanel pnExternalResources;

    private JPanel pnInfo;

    private MapModelCollection mmc;

    @Override
    public void init( ViewFormType viewForm )
                            throws Exception {
        super.init( viewForm );
        mmc = owner.getApplicationContainer().getMapModelCollection();
        initGUI();
        setModal( false );
        setAlwaysOnTop( false );
        setVisible( true );
        toFront();
    }

    private void initGUI() {
        try {
            {
                tpTabPane = new JTabbedPane();
                getContentPane().add( tpTabPane, BorderLayout.CENTER );
                tpTabPane.setPreferredSize( new java.awt.Dimension( 660, 458 ) );
                {
                    pnInfo = new JPanel();
                    GridBagLayout pnInfoLayout = new GridBagLayout();
                    tpTabPane.addTab( Messages.getMessage( getLocale(), "$MD11511" ), null, pnInfo, null );
                    pnInfoLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.1 };
                    pnInfoLayout.rowHeights = new int[] { 80, 80, 238, 20 };
                    pnInfoLayout.columnWeights = new double[] { 0.0, 0.1 };
                    pnInfoLayout.columnWidths = new int[] { 307, 7 };
                    pnInfo.setLayout( pnInfoLayout );
                    pnInfo.setPreferredSize( new java.awt.Dimension( 655, 422 ) );
                    {
                        pnName = new JPanel();
                        GridBagLayout pnNameLayout = new GridBagLayout();
                        pnNameLayout.rowWeights = new double[] { 0.1 };
                        pnNameLayout.rowHeights = new int[] { 7 };
                        pnNameLayout.columnWeights = new double[] { 0.1 };
                        pnNameLayout.columnWidths = new int[] { 7 };
                        pnName.setLayout( pnNameLayout );
                        pnInfo.add( pnName, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                    GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ),
                                                                    0, 0 ) );
                        pnName.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                 "$MD11512" ) ) );
                        {
                            tfName = new JTextField( mmc.getName() );
                            pnName.add( tfName, new GridBagConstraints( -1, 0, 1, 1, 0.0, 0.0,
                                                                        GridBagConstraints.CENTER,
                                                                        GridBagConstraints.HORIZONTAL,
                                                                        new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                            tfName.setPreferredSize( new java.awt.Dimension( 286, 21 ) );
                        }
                    }
                    {
                        pnDescription = new JPanel();
                        BorderLayout pnDescriptionLayout = new BorderLayout();
                        pnDescription.setLayout( pnDescriptionLayout );
                        pnInfo.add( pnDescription, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0,
                                                                           GridBagConstraints.CENTER,
                                                                           GridBagConstraints.BOTH, new Insets( 0, 0,
                                                                                                                0, 0 ),
                                                                           0, 0 ) );
                        pnDescription.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                        "$MD11513" ) ) );
                        {
                            scDescription = new JScrollPane();
                            pnDescription.add( scDescription, BorderLayout.CENTER );
                            scDescription.setPreferredSize( new java.awt.Dimension( 43, 59 ) );
                            {
                                tpDescription = new JTextPane();
                                scDescription.setViewportView( tpDescription );
                                tpDescription.setText( mmc.getDescription() );
                            }
                        }
                    }
                    {
                        pnInfoButtons = new JPanel();
                        FlowLayout pnInfoButtonsLayout = new FlowLayout();
                        pnInfoButtonsLayout.setAlignment( FlowLayout.LEFT );
                        pnInfoButtons.setLayout( pnInfoButtonsLayout );
                        pnInfo.add( pnInfoButtons, new GridBagConstraints( 0, 3, 1, 1, 0.0, 0.0,
                                                                           GridBagConstraints.CENTER,
                                                                           GridBagConstraints.BOTH, new Insets( 0, 0,
                                                                                                                0, 0 ),
                                                                           0, 0 ) );
                        {
                            btTake = new JButton( Messages.getMessage( getLocale(), "$MD11514" ),
                                                  IconRegistry.getIcon( "accept.png" ) );
                            btTake.addActionListener( new ActionListener() {

                                public void actionPerformed( ActionEvent e ) {
                                    mmc.setName( tfName.getText() );
                                    double minx = Double.parseDouble( tfMinx.getText() );
                                    double miny = Double.parseDouble( tfMiny.getText() );
                                    double maxx = Double.parseDouble( tfMaxx.getText() );
                                    double maxy = Double.parseDouble( tfMaxy.getText() );
                                    Envelope maxExtent = GeometryFactory.createEnvelope(
                                                                                         minx,
                                                                                         miny,
                                                                                         maxx,
                                                                                         maxy,
                                                                                         mmc.getMaxExtent().getCoordinateSystem() );
                                    mmc.setMaxExtent( maxExtent );
                                    mmc.setDescription( tpDescription.getText() );
                                    dispose();
                                }
                            } );
                            pnInfoButtons.add( btTake );
                        }
                        {
                            btCancel = new JButton( Messages.getMessage( getLocale(), "$MD11515" ),
                                                    IconRegistry.getIcon( "cancel.png" ) );
                            btCancel.addActionListener( new ActionListener() {

                                public void actionPerformed( ActionEvent e ) {
                                    dispose();
                                }
                            } );
                            pnInfoButtons.add( btCancel );
                        }
                    }
                    {
                        pnHelp = new JPanel();
                        FlowLayout pnHelpLayout = new FlowLayout();
                        pnHelpLayout.setAlignment( FlowLayout.RIGHT );
                        pnInfo.add( pnHelp, new GridBagConstraints( 1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                    GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ),
                                                                    0, 0 ) );
                        pnHelp.setLayout( pnHelpLayout );
                        {
                            btHelp = new JButton( Messages.getMessage( getLocale(), "$MD11516" ),
                                                  IconRegistry.getIcon( "help.png" ) );
                            pnHelp.add( btHelp );
                        }
                    }
                    {
                        pnCRS = new JPanel();
                        GridBagLayout pnCRSLayout = new GridBagLayout();
                        pnInfo.add( pnCRS, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                   GridBagConstraints.BOTH, new Insets( 0, 0, 0, 0 ),
                                                                   0, 0 ) );
                        pnCRS.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11518" ) ) );
                        pnCRSLayout.rowWeights = new double[] { 0.1 };
                        pnCRSLayout.rowHeights = new int[] { 7 };
                        pnCRSLayout.columnWeights = new double[] { 0.1 };
                        pnCRSLayout.columnWidths = new int[] { 7 };
                        pnCRS.setLayout( pnCRSLayout );
                        {
                            tfCRS = new JTextField( mmc.getMaxExtent().getCoordinateSystem().getFormattedString() );
                            pnCRS.add( tfCRS, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                      GridBagConstraints.HORIZONTAL,
                                                                      new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                            tfCRS.setEditable( false );
                        }
                    }
                    {
                        pnExtent = new JPanel();
                        GridBagLayout pnExtentLayout = new GridBagLayout();
                        pnInfo.add( pnExtent, new GridBagConstraints( 1, 1, 1, 2, 0.0, 0.0, GridBagConstraints.CENTER,
                                                                      GridBagConstraints.BOTH,
                                                                      new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                        pnExtent.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(),
                                                                                                   "$MD11519" ) ) );
                        pnExtentLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.1 };
                        pnExtentLayout.rowHeights = new int[] { 37, 37, 36, 39, 7 };
                        pnExtentLayout.columnWeights = new double[] { 0.0, 0.1 };
                        pnExtentLayout.columnWidths = new int[] { 112, 7 };
                        pnExtent.setLayout( pnExtentLayout );
                        {
                            lbMinx = new JLabel( Messages.getMessage( getLocale(), "$MD11520" ) );
                            pnExtent.add( lbMinx, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                          GridBagConstraints.CENTER,
                                                                          GridBagConstraints.HORIZONTAL,
                                                                          new Insets( 0, 9, 0, 0 ), 0, 0 ) );
                        }
                        {
                            lbMiny = new JLabel( Messages.getMessage( getLocale(), "$MD11521" ) );
                            pnExtent.add( lbMiny, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                                                          GridBagConstraints.CENTER,
                                                                          GridBagConstraints.HORIZONTAL,
                                                                          new Insets( 0, 9, 0, 0 ), 0, 0 ) );
                        }
                        {
                            lbMaxx = new JLabel( Messages.getMessage( getLocale(), "$MD11522" ) );
                            pnExtent.add( lbMaxx, new GridBagConstraints( 0, 2, 1, 1, 0.0, 0.0,
                                                                          GridBagConstraints.CENTER,
                                                                          GridBagConstraints.HORIZONTAL,
                                                                          new Insets( 0, 9, 0, 0 ), 0, 0 ) );
                        }
                        {
                            lbMaxy = new JLabel( Messages.getMessage( getLocale(), "$MD11523" ) );
                            pnExtent.add( lbMaxy, new GridBagConstraints( 0, 3, 1, 1, 0.0, 0.0,
                                                                          GridBagConstraints.CENTER,
                                                                          GridBagConstraints.HORIZONTAL,
                                                                          new Insets( 0, 9, 0, 0 ), 0, 0 ) );
                        }
                        {
                            tfMinx = new JTextField( "" + mmc.getMaxExtent().getMin().getX() );
                            tfMinx.setEditable( false );
                            pnExtent.add( tfMinx, new GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0,
                                                                          GridBagConstraints.CENTER,
                                                                          GridBagConstraints.HORIZONTAL,
                                                                          new Insets( 0, 0, 0, 9 ), 0, 0 ) );
                        }
                        {
                            tfMiny = new JTextField( "" + mmc.getMaxExtent().getMin().getY() );
                            tfMiny.setEditable( false );
                            pnExtent.add( tfMiny, new GridBagConstraints( 1, 1, 1, 1, 0.0, 0.0,
                                                                          GridBagConstraints.CENTER,
                                                                          GridBagConstraints.HORIZONTAL,
                                                                          new Insets( 0, 0, 0, 9 ), 0, 0 ) );
                        }
                        {
                            tfMaxx = new JTextField( "" + mmc.getMaxExtent().getMax().getX() );
                            tfMaxx.setEditable( false );
                            pnExtent.add( tfMaxx, new GridBagConstraints( 1, 2, 1, 1, 0.0, 0.0,
                                                                          GridBagConstraints.CENTER,
                                                                          GridBagConstraints.HORIZONTAL,
                                                                          new Insets( 0, 0, 0, 9 ), 0, 0 ) );
                        }
                        {
                            tfMaxy = new JTextField( "" + mmc.getMaxExtent().getMax().getY() );
                            tfMaxy.setEditable( false );
                            pnExtent.add( tfMaxy, new GridBagConstraints( 1, 3, 1, 1, 0.0, 0.0,
                                                                          GridBagConstraints.CENTER,
                                                                          GridBagConstraints.HORIZONTAL,
                                                                          new Insets( 0, 0, 0, 9 ), 0, 0 ) );
                        }
                    }
                    {
                        pnFileName = new JPanel();
                        GridBagLayout pnFileNameLayout = new GridBagLayout();
                        pnInfo.add( pnFileName, new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                                                        GridBagConstraints.CENTER,
                                                                        GridBagConstraints.BOTH,
                                                                        new Insets( 0, 0, 0, 0 ), 0, 0 ) );
                        pnFileNameLayout.rowWeights = new double[] { 0.1 };
                        pnFileNameLayout.rowHeights = new int[] { 7 };
                        pnFileNameLayout.columnWeights = new double[] { 0.1 };
                        pnFileNameLayout.columnWidths = new int[] { 7 };
                        pnFileName.setLayout( pnFileNameLayout );
                        pnFileName.setBorder( BorderFactory.createTitledBorder( Messages.getMessage( getLocale(), "$MD11604") ) );
                        {
                            tfFile = new JTextField(owner.getApplicationContainer().getProjectURL() );
                            tfFile.setEditable( false );
                            pnFileName.add( tfFile, new GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0,
                                                                            GridBagConstraints.CENTER,
                                                                            GridBagConstraints.HORIZONTAL,
                                                                            new Insets( 0, 9, 0, 9 ), 0, 0 ) );
                        }
                    }
                }
                {
                    pnExternalResources = new JPanel();
                    tpTabPane.addTab( Messages.getMessage( getLocale(), "$MD11517" ), null, pnExternalResources, null );
                    BorderLayout pnExternalResourcesLayout = new BorderLayout();
                    pnExternalResources.setLayout( pnExternalResourcesLayout );
                    {
                        pnExtResButtons = new JPanel();
                        FlowLayout pnExtResButtonsLayout = new FlowLayout();
                        pnExtResButtonsLayout.setAlignment( FlowLayout.LEFT );
                        pnExternalResources.add( pnExtResButtons, BorderLayout.SOUTH );
                        pnExtResButtons.setLayout( pnExtResButtonsLayout );
                        pnExtResButtons.setPreferredSize( new java.awt.Dimension( 663, 36 ) );
                        {
                            btAddExtRes = new JButton( Messages.getMessage( getLocale(), "$MD11524" ),
                                                       IconRegistry.getIcon( "add.png" ) );
                            btAddExtRes.addActionListener( new ActionListener() {
                                public void actionPerformed( ActionEvent evt ) {
                                    DefaultTableModel model = (DefaultTableModel) tabExternalResources.getModel();
                                    ExternalResourceDialog dg = new ExternalResourceDialog(
                                                                                            MapModelCollectionInfoDialog.this );
                                    if ( !dg.isCanceled() ) {
                                        String[] values = dg.getValues();
                                        model.addRow( values );
                                        ExternalResourceType extResType = new ExternalResourceType();
                                        extResType.setExternalResourceTitle( values[0] );
                                        extResType.setAbstract( values[1] );
                                        extResType.setExternalResourceType( values[2] );
                                        OnlineResourceType olr = new OnlineResourceType();
                                        olr.setHref( values[3] );
                                        extResType.setOnlineResource( olr );
                                        mmc.addExternalResources( extResType );
                                    }
                                }
                            } );
                            pnExtResButtons.add( btAddExtRes );
                        }
                        {
                            btRemoveExtRes = new JButton( Messages.getMessage( getLocale(), "$MD11525" ),
                                                          IconRegistry.getIcon( "remove.png" ) );
                            btRemoveExtRes.addActionListener( new ActionListener() {
                                public void actionPerformed( ActionEvent evt ) {
                                    DefaultTableModel model = (DefaultTableModel) tabExternalResources.getModel();
                                    int idx = tabExternalResources.getSelectedRow();
                                    if ( idx >= 0 ) {
                                        model.removeRow( idx );
                                        List<ExternalResourceType> extResList = mmc.getExternalResources();
                                        extResList.remove( idx );
                                    }
                                }
                            } );
                            pnExtResButtons.add( btRemoveExtRes );
                        }
                    }
                    {
                        String[] tabHeader = StringTools.toArray( Messages.getMessage( getLocale(), "$MD11526" ), ",;",
                                                                  false );
                        final List<ExternalResourceType> extResList = mmc.getExternalResources();
                        Object[][] data = new Object[extResList.size()][5];
                        for ( int i = 0; i < extResList.size(); i++ ) {
                            data[i][0] = extResList.get( i ).getExternalResourceTitle();
                            data[i][1] = extResList.get( i ).getAbstract();
                            data[i][2] = extResList.get( i ).getExternalResourceType();
                            data[i][3] = extResList.get( i ).getOnlineResource().getHref();
                            data[i][4] = "go to";
                        }
                        tabExternalResources = new JTable( new DefaultTableModel( data, tabHeader ) );
                        pnExternalResources.add( tabExternalResources, BorderLayout.CENTER );
                        tabExternalResources.getColumn( "go to" ).setCellRenderer( new ButtonRenderer() );
                        tabExternalResources.getColumn( "go to" ).setCellEditor(
                                                                                 new ButtonEditor(
                                                                                                   new JCheckBox(
                                                                                                                  "go to" ) ) );
                        tabExternalResources.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
                        pnExternalResources.add( tabExternalResources.getTableHeader(), BorderLayout.PAGE_START );
                    }
                }
            }
            this.setSize( 676, 489 );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version. $Revision$, $Date$
     */
    class ButtonRenderer extends JButton implements TableCellRenderer {

        private static final long serialVersionUID = 2403325936293687523L;

        public ButtonRenderer() {
            setOpaque( true );
        }

        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected,
                                                        boolean hasFocus, int row, int column ) {
            if ( isSelected ) {
                setForeground( table.getSelectionForeground() );
                setBackground( table.getSelectionBackground() );
            } else {
                setForeground( table.getForeground() );
                setBackground( UIManager.getColor( "Button.background" ) );
            }
            setText( ( value == null ) ? "" : value.toString() );
            return this;
        }
    }

    /**
     * 
     * 
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * @author last edited by: $Author$
     * 
     * @version. $Revision$, $Date$
     */
    class ButtonEditor extends DefaultCellEditor {

        private static final long serialVersionUID = 5171885926340378592L;

        protected JButton button;

        private String label = "go to";

        private boolean isPushed;

        private int row;

        private JTable table;

        /**
         * 
         * @param checkBox
         */
        public ButtonEditor( JCheckBox checkBox ) {
            super( checkBox );
            button = new JButton();
            button.setOpaque( true );
            button.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    fireEditingStopped();
                }
            } );
        }

        @Override
        public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected, int row,
                                                      int column ) {
            if ( isSelected ) {
                button.setForeground( table.getSelectionForeground() );
                button.setBackground( table.getSelectionBackground() );
            } else {
                button.setForeground( table.getForeground() );
                button.setBackground( table.getBackground() );
            }
            label = ( value == null ) ? "" : value.toString();
            button.setText( label );
            isPushed = true;
            this.row = row;
            this.table = table;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if ( isPushed ) {
                String value = table.getModel().getValueAt( row, 3 ).toString();
                try {
                    String name = owner.getInitParameter( "programName" );
                    if ( name == null ) {
                        DialogFactory.openErrorDialog( owner.getApplicationContainer().getViewPlatform(),
                                                       MapModelCollectionInfoDialog.this,
                                                       Messages.getMessage( getLocale(), "$MD11527" ),
                                                       Messages.getMessage( getLocale(), "$MD11528" ) );
                        return new String( label );
                    }
                    String parameter = owner.getInitParameter( "programParameter" );
                    ExternalPrograms.startProgram( name, parameter, value );
                } catch ( IOException e ) {
                    LOG.logError( "Unknown error", e );
                }
            }
            isPushed = false;
            return new String( label );
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }

    }

}
