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
package org.deegree.igeo.views.swing.digitize;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.deegree.datatypes.QualifiedName;
import org.deegree.framework.log.ILogger;
import org.deegree.framework.log.LoggerFactory;
import org.deegree.framework.util.Pair;
import org.deegree.framework.util.TimeTools;
import org.deegree.framework.utils.DictionaryCollection;
import org.deegree.igeo.ApplicationContainer;
import org.deegree.igeo.i18n.Messages;
import org.deegree.igeo.mapmodel.Layer;
import org.deegree.igeo.modules.DigitizerModule;
import org.deegree.igeo.modules.EditFeature;
import org.deegree.model.feature.Feature;
import org.deegree.model.feature.FeatureCollection;
import org.deegree.model.feature.FeatureFactory;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author$
 * 
 * @version. $Revision$, $Date$
 */
public class EditFeaturePanel extends JPanel implements EditFeature {

    private static final long serialVersionUID = -1491068289424777009L;

    private static final ILogger LOG = LoggerFactory.getLogger( EditFeaturePanel.class );

    private BaseInfoPanel baseInfoPanel;

    private JTabbedPane tabbedPane;

    private FeatureCollection featureCollection;

    private DigitizerModule<Container> digitizerModule;

    private ApplicationContainer<Container> appContainer;

    private Map<String, SingleFeatureTableModel> properties = new HashMap<String, SingleFeatureTableModel>();

    /**
     * @param appContainer
     * @param layer
     * @param featureCollection
     */
    public EditFeaturePanel( ApplicationContainer<Container> appContainer, Layer layer,
                             FeatureCollection featureCollection ) {
        this.featureCollection = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(),
                                                                         featureCollection.size() );
        Iterator<Feature> iter = featureCollection.iterator();
        while ( iter.hasNext() ) {
            Feature feature = (Feature) iter.next();
            this.featureCollection.add( feature );
        }
        this.appContainer = appContainer;
        setMaximumSize( new Dimension( 460, 600 ) );
        initGUI( layer, featureCollection );
        setVisible( true );
    }

    private void initGUI( Layer layer, FeatureCollection featureCollection ) {
        try {
            tabbedPane = new JTabbedPane( JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT );
            this.setFocusCycleRoot( false );
            setLayout( new BorderLayout() );
            Iterator<Feature> iterator = featureCollection.iterator();
            tabbedPane.removeAll();
            properties.clear();
            while ( iterator.hasNext() ) {
                addFeatureTabPane( layer, iterator.next() );
            }
            this.setSize( 660, 700 );
            add( createCommitPanel(), BorderLayout.SOUTH );
        } catch ( Exception e ) {
            LOG.logError( e.getMessage(), e );
        }
    }

    /**
     * @return the digitizerModule
     */
    DigitizerModule<Container> getDigitizerModule() {
        return digitizerModule;
    }

    /**
     * sets a new feature to display
     * 
     * @param layerName
     * @param feature
     */
    public void setFeature( Layer layer, FeatureCollection featureCollection ) {
        // update base info
        this.featureCollection = FeatureFactory.createFeatureCollection( UUID.randomUUID().toString(),
                                                                         featureCollection.size() );
        Iterator<Feature> iterator = featureCollection.iterator();
        tabbedPane.removeAll();
        properties.clear();
        while ( iterator.hasNext() ) {
            Feature feat = iterator.next();
            this.featureCollection.add( feat );
            addFeatureTabPane( layer, feat );
        }

        doLayout();
        this.setSize( 660, 700 );
        setVisible( true );
    }

    private void addFeatureTabPane( Layer layer, Feature feature ) {
        JScrollPane jScrollPane1 = new JScrollPane( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        jScrollPane1.setMaximumSize( new Dimension( 680, 600 ) );
        JPanel rootPanel = new JPanel();
        tabbedPane.addTab( feature.getId(), rootPanel );
        add( tabbedPane, BorderLayout.CENTER );

        rootPanel.setLayout( new BorderLayout() );

        baseInfoPanel = new BaseInfoPanel( layer.getTitle(), feature );
        rootPanel.add( baseInfoPanel, BorderLayout.NORTH );

        JTable propertiesPanel = createFeaturePropertyPanel( feature );
        jScrollPane1.setViewportView( propertiesPanel );
        rootPanel.add( jScrollPane1, BorderLayout.CENTER );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.client.application.modules.IEditFeature#getFeature()
     */
    public FeatureCollection getFeatureCollection() {
        int tc = tabbedPane.getTabCount();
        FeatureCollection fc = FeatureFactory.createFeatureCollection( featureCollection.getId(), tc );
        for ( int i = 0; i < tc; i++ ) {
            Feature feature = featureCollection.getFeature( i );
            fc.add( createUpdatedFeature( feature ) );
        }

        return fc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.modules.EditFeature#getCurrentFeature()
     */
    public Feature getCurrentFeature() {
        int index = tabbedPane.getSelectedIndex();
        // get current feature
        Feature feature = featureCollection.getFeature( index );
        return createUpdatedFeature( feature );
    }

    private Feature createUpdatedFeature( Feature feature ) {
        Feature feat = properties.get( feature.getId() ).getFeature();
        try {
            Feature tmp = feat.cloneDeep();
            tmp.setId( feat.getId() );
            return tmp;
        } catch ( CloneNotSupportedException e ) {
            return null;
        }
    }

    /**
     * 
     * @return panel containing OK and cancel button
     */
    private JPanel createCommitPanel() {
        JPanel panel = new JPanel();
        panel.setLayout( new FlowLayout( FlowLayout.LEFT, 5, 5 ) );
        JButton save = new JButton( Messages.getMessage( getLocale(), "$MD10062" ) );
        save.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                digitizerModule.propertyEditingFinished( false, false );
            }
        } );

        JButton saveAndClose = new JButton( Messages.getMessage( getLocale(), "$MD10062a" ) );
        saveAndClose.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                digitizerModule.propertyEditingFinished( false, true );
            }
        } );

        JButton cancel = new JButton( Messages.getMessage( getLocale(), "$MD10063" ) );
        cancel.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                digitizerModule.propertyEditingFinished( true, false );
            }
        } );

        JButton delete = new JButton( Messages.getMessage( getLocale(), "$MD11826" ) );
        delete.setToolTipText( Messages.getMessage( getLocale(), "$MD11827" ) );
        delete.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                Map<String, Object> map = new HashMap<String, Object>();
                int idx = tabbedPane.getSelectedIndex();
                String title = tabbedPane.getTitleAt( idx );
                map.put( "dummy", title );
                digitizerModule.performDigitizingAction( "deleteFeature", map );
                tabbedPane.remove( idx );
            }
        } );
        panel.add( save );
        panel.add( saveAndClose );
        panel.add( cancel );
        JPanel p = new JPanel();
        p.setSize( 50, 10 );
        panel.add( p );
        panel.add( delete );
        return panel;
    }

    /**
     * creates a panel that contains one TextField for each simple property and a tree of panels for each feature
     * property. Geometry properties will be ignored.
     * 
     * @param feature
     * @return table displaying simple and feature properties of passed feature(type)
     */
    private JTable createFeaturePropertyPanel( Feature feature ) {

        JTable propertiesPanel = new JTable();
        // propertiesPanel.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        SingleFeatureTableModel model = new SingleFeatureTableModel( feature );
        propertiesPanel.setModel( model );
        TableColumn col = propertiesPanel.getColumnModel().getColumn( 1 );
        propertiesPanel.setRowHeight( 22 );
        col.setCellEditor( new FeatureTableCellEditor() );
        col.setCellRenderer( new FeatureTableCellRenderer() );

        // col = propertiesPanel.getColumnModel().getColumn( 2 );
        // col.setCellRenderer( new ButtonRenderer() );
        // col.setPreferredWidth( 50 );
        properties.put( feature.getId(), model );
        return propertiesPanel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.modules.EditFeature#setDigitizerModule(org.deegree.igeo.modules.DigitizerModule)
     */
    @SuppressWarnings("unchecked")
    public void setDigitizerModule( DigitizerModule<?> digitizerModule ) {
        this.digitizerModule = (DigitizerModule<Container>) digitizerModule;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.deegree.igeo.modules.EditFeature#dispose()
     */
    public void dispose() {
        super.removeAll();
    }

    // ////////////////////////////////////////////////////////////////////////////////////
    // inner classes
    // ////////////////////////////////////////////////////////////////////////////////////

    /**
     * 
     * The <code>EditFeaturePanel</code> class TODO add class documentation here.
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * 
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     * 
     */
    private class FeatureTableCellEditor extends AbstractCellEditor implements TableCellEditor {

        private static final long serialVersionUID = 3493150733584336950L;

        // This is the component that will handle the editing of the cell value
        private JTextField tfText = new JTextField();

        private JSpinner spFloat = new JSpinner();

        private JSpinner spInt = new JSpinner();

        private Component comp = null;

        private Object val;

        private DictionaryCollection dictCol = appContainer.getSettings().getDictionaries();

        /**
         * 
         */
        public FeatureTableCellEditor() {
            spFloat.setModel( new SpinnerNumberModel( 0, -9E99, 9E99, 0.1 ) );
            spInt.setModel( new SpinnerNumberModel( 0, -9999999999l, 9999999999l, 1 ) );
        }

        // This method is called when a cell value is edited by the user.
        public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected, int rowIndex,
                                                      int vColIndex ) {

            SingleFeatureTableModel model = (SingleFeatureTableModel) table.getModel();
            String ftn = model.getFeature().getFeatureType().getName().getLocalName();
            QualifiedName qn = (QualifiedName) model.getFeatureValueAt( rowIndex, 0 );
            // create new QualifiedName by concatinating feature type name and property name
            qn = new QualifiedName( ftn + '/' + qn.getLocalName(), qn.getNamespace() );

            List<Pair<String, String>> list = null;
            if ( vColIndex == 1 ) {
                list = dictCol.getCodelist( qn, null );
            }

            // Configure the component with the specified value
            // JComboboxes will be used for editing if a code list is available
            JComboBox cb = null;
            if ( list != null && list.size() > 0 ) {
                cb = new JComboBox();
                for ( Pair<String, String> pair : list ) {
                    ComboBoxItem cbItem = new ComboBoxItem( pair );
                    cb.addItem( cbItem );
                    if ( equals( pair, value ) ) {
                        cb.setSelectedItem( cbItem );
                    }
                }
                comp = cb;
            } else {
                if ( value instanceof Double || value instanceof Float ) {
                    spFloat.setValue( value );
                    comp = spFloat;
                } else if ( value instanceof Number ) {
                    spInt.setValue( value );
                    comp = spInt;
                } else if ( value instanceof Date ) {
                    tfText.setText( TimeTools.getISOFormattedTime( (Date) value ) );
                    comp = tfText;
                } else {
                    tfText.setText( value.toString() );
                    comp = tfText;
                }
            }
            val = value;

            // Return the configured component
            return comp;
        }

        private boolean equals( Pair<String, String> pair, Object value ) {
            if ( value != null ) {
                if ( value instanceof Double || value instanceof Float ) {
                    try {
                        return Double.parseDouble( pair.first ) == (Double) value;
                    } catch ( Exception e ) {
                        LOG.logDebug( "not a double value: " + pair.first );
                    }
                } else if ( value instanceof Integer ) {
                    try {
                        return Integer.parseInt( pair.first ) == (Integer) value;
                    } catch ( Exception e ) {
                        LOG.logDebug( "not an integer value: " + pair.first );
                    }
                } else if ( value instanceof Date ) {
                    try {
                        return TimeTools.createDate( pair.first ).equals( value );
                    } catch ( Exception e ) {
                        LOG.logDebug( "not a date: " + pair.first );
                    }
                } else {
                    return pair.first.equals( value.toString() );
                }
            }
            return false;
        }

        // This method is called when editing is completed.
        // It must return the new value to be stored in the cell.
        public Object getCellEditorValue() {
            if ( val instanceof Double || val instanceof Float ) {
                if ( comp instanceof JComboBox ) {
                    ComboBoxItem item = (ComboBoxItem) ( (JComboBox) comp ).getSelectedItem();
                    return Double.parseDouble( item.pair.first );
                } else {
                    try {
                        spFloat.commitEdit();
                    } catch ( ParseException e ) {
                        LOG.logWarning( "ignore", e );
                    }
                    return spFloat.getValue();
                }
            } else if ( val instanceof Number ) {
                if ( comp instanceof JComboBox ) {
                    ComboBoxItem item = (ComboBoxItem) ( (JComboBox) comp ).getSelectedItem();
                    return Integer.parseInt( item.pair.first );
                } else {
                    try {
                        spInt.commitEdit();
                    } catch ( ParseException e ) {
                        LOG.logWarning( "ignore", e );
                    }
                    return ( (Number) spInt.getValue() ).intValue();
                }
            } else if ( val instanceof Date ) {
                return TimeTools.createCalendar( tfText.getText() ).getTime();
            } else {
                if ( comp instanceof JComboBox ) {
                    ComboBoxItem item = (ComboBoxItem) ( (JComboBox) comp ).getSelectedItem();
                    return item.pair.first;
                } else {
                    return tfText.getText();
                }
            }
        }
    }

    /**
     * 
     * The <code>EditFeaturePanel</code> class TODO add class documentation here.
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * 
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     * 
     */
    private class ComboBoxItem {

        Pair<String, String> pair;

        /**
         * @param pair2
         */
        public ComboBoxItem( Pair<String, String> pair ) {
            this.pair = pair;
        }

        @Override
        public String toString() {
            return pair.second + " [" + pair.first + "]";
        }

        @Override
        public boolean equals( Object obj ) {
            if ( obj instanceof ComboBoxItem ) {
                return pair.equals( ( (ComboBoxItem) obj ).pair );
            }
            return super.equals( obj );
        }

    }

    /**
     * 
     * The <code>EditFeaturePanel</code> class TODO add class documentation here.
     * 
     * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
     * 
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     * 
     */
    private class FeatureTableCellRenderer implements TableCellRenderer {

        private DictionaryCollection dictCol = appContainer.getSettings().getDictionaries();

        // This method is called each time a cell in a column
        // using this renderer needs to be rendered.
        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected,
                                                        boolean hasFocus, int rowIndex, int vColIndex ) {

            SingleFeatureTableModel model = (SingleFeatureTableModel) table.getModel();
            QualifiedName qn = (QualifiedName) model.getFeatureValueAt( rowIndex, 0 );
            String ftn = model.getFeature().getFeatureType().getName().getLocalName();
            // create new QualifiedName by concatinating feature type name and property name
            qn = new QualifiedName( ftn + '/' + qn.getLocalName(), qn.getNamespace() );

            List<Pair<String, String>> list = null;
            if ( vColIndex == 1 ) {
                list = dictCol.getCodelist( qn, null );
            }

            if ( list != null && list.size() > 0 ) {

                for ( Pair<String, String> pair : list ) {
                    if ( value instanceof Number ) {
                        double d = Double.parseDouble( pair.first );
                        if ( d == ( (Number) value ).doubleValue() ) {
                            value = pair.second;
                            break;
                        }
                    } else if ( value instanceof Date ) {
                        Date date = TimeTools.createCalendar( pair.first ).getTime();
                        if ( date.equals( value ) ) {
                            value = pair.second;
                            break;
                        }
                    } else if ( pair.first.equals( value.toString() ) ) {
                        value = pair.second;
                        break;
                    }
                }

            }

            // Return the configured component
            if ( value != null ) {
                return new JLabel( value.toString() );
            } else {
                return new JLabel();
            }
        }

        // The following methods override the defaults for performance reasons
        @SuppressWarnings("unused")
        public void validate() {
        }

        @SuppressWarnings("unused")
        public void revalidate() {
        }

        @SuppressWarnings("unused")
        protected void firePropertyChange( String propertyName, Object oldValue, Object newValue ) {
        }

        @SuppressWarnings("unused")
        public void firePropertyChange( String propertyName, boolean oldValue, boolean newValue ) {
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

}
